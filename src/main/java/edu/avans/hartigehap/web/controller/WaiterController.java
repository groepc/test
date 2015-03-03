package edu.avans.hartigehap.web.controller;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.service.*;
import edu.avans.hartigehap.web.form.Message;


@Controller
@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
public class WaiterController {

	final Logger logger = LoggerFactory.getLogger(WaiterController.class);

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private BillService billService;
	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/restaurants/{restaurantName}/waiter", method = RequestMethod.GET)
	public String showWaiter(@PathVariable("restaurantName") String restaurantName, Model uiModel) {
		
		// warmup stuff
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(restaurantName);
		uiModel.addAttribute("restaurant", restaurant);
		
		List<Order> allPreparedOrders = 
				orderService.findPreparedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allPreparedOrders", allPreparedOrders);

		List<Bill> allSubmittedBills = 
				billService.findSubmittedBillsForRestaurant(restaurant);
		uiModel.addAttribute("allSubmittedBills", allSubmittedBills);
		
		return "hartigehap/waiter";
	}

		
	@RequestMapping(value = "/waiter/orders/{orderId}", method = RequestMethod.GET)
	public String showOrderInWaiter(@PathVariable("orderId") String orderId,
			Model uiModel, Locale locale) {
		
		// warmup stuff
		Order order = orderService.findById(Long.valueOf(orderId));
		Restaurant restaurant = warmupRestaurant(order, uiModel);
		uiModel.addAttribute("restaurant", restaurant);
		
		List<Order> allPreparedOrders = 
				orderService.findPreparedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allPreparedOrders", allPreparedOrders);

		List<Bill> allSubmittedBills = 
				billService.findSubmittedBillsForRestaurant(restaurant);
		uiModel.addAttribute("allSubmittedBills", allSubmittedBills);
		
		String orderContent = "";
		for(BaseOrderItem orderItem : order.getOrderItems()) {
			orderContent += orderItem.getMenuItem().getId() + " (" + orderItem.getQuantity() + "x)" + "; ";
		}
		
		uiModel.addAttribute("message", new Message("info",
				messageSource.getMessage("label_order_content", new Object[]{}, locale) + ": " + orderContent));

		return "hartigehap/waiter";
	}

		
	@RequestMapping(value = "/waiter/bills/{billId}", method = RequestMethod.GET)
	public String showBillInWaiter(@PathVariable("billId") String billId,
			Model uiModel, Locale locale) {
		
		// warmup stuff
		Bill bill = billService.findById(Long.valueOf(billId));
		Restaurant restaurant = warmupRestaurant(bill, uiModel);
		uiModel.addAttribute("restaurant", restaurant);
		
		List<Order> allPreparedOrders = 
				orderService.findPreparedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allPreparedOrders", allPreparedOrders);

		List<Bill> allSubmittedBills = 
				billService.findSubmittedBillsForRestaurant(restaurant);
		uiModel.addAttribute("allSubmittedBills", allSubmittedBills);
		
		uiModel.addAttribute("message", new Message("info",
				messageSource.getMessage("label_bill_amount", new Object[]{}, locale)
				+ ": " + bill.getPriceAllOrders()
				+ " "
				+ messageSource.getMessage("label_currency", new Object[]{}, locale)));

		return "hartigehap/waiter";
	}

	
	@RequestMapping(value = "/waiter/orders/{orderId}", method = RequestMethod.PUT)
	public String receiveOrderEvent(@PathVariable("orderId") String orderId,
			@RequestParam String event, Model uiModel, Locale locale) {

		switch (event) {
		case "orderHasBeenServed":
			return orderHasBeenServed(orderId, uiModel, locale);
			// break unreachable

		default:
			logger.error("Internal error: event " + event + " not recognized");
			Order order = orderService.findById(Long.valueOf(orderId));
			Restaurant restaurant = warmupRestaurant(order, uiModel);
			return "redirect:/restaurants/" + restaurant.getId();
		}
	}

	
	private String orderHasBeenServed(String orderId, Model uiModel,
			Locale locale) {
		Order order = orderService.findById(Long.valueOf(orderId));
		Restaurant restaurant = warmupRestaurant(order, uiModel);
		try {
			orderService.orderServed(order);
		} catch (StateException e) {
			logger.error(
					"Internal error has occurred! Order "
							+ Long.valueOf(orderId)
							+ "has not been changed to served state!", e);

			// StateException triggers a rollback; consequently all Entities are
			// invalidated by Hibernate
			// So new warmup needed
			warmupRestaurant(order, uiModel);
			return "hartigehap/waiter";
		}
		return "redirect:/restaurants/" + restaurant.getId() + "/waiter";
	}

	
	@RequestMapping(value = "/waiter/bills/{billId}", method = RequestMethod.PUT)
	public String receiveBillEvent(
			@PathVariable("billId") String billId, 
			@RequestParam String event, 
			Model uiModel, Locale locale) {

		Bill bill = billService.findById(Long.valueOf(billId));
		Restaurant restaurant = warmupRestaurant(bill, uiModel);
		
		switch(event) {
		case "billHasBeenPaid":
			try {
				billService.billHasBeenPaid(bill);
			} catch (StateException e) {
				logger.error("Internal error has occurred! Order " + Long.valueOf(billId) 
						+ "has not been changed to served state!", e);
				// StateException triggers a rollback; consequently all Entities are invalidated by Hibernate
				// So new warmup needed
				warmupRestaurant(bill, uiModel);
				return "hartigehap/waiter";
			}
			break;
			
		default:
			logger.error("Internal error: event " + event + " not recognized");
			break;
		}
		
		return "redirect:/restaurants/" + restaurant.getId() + "/waiter";
	}

	private Restaurant warmupRestaurant(Order order, Model uiModel) {
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(order.getBill()
				.getDiningTable().getRestaurant().getId());
		uiModel.addAttribute("restaurant", restaurant);
		return restaurant;
	}

	private Restaurant warmupRestaurant(Bill bill, Model uiModel) {
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(bill.getDiningTable().getRestaurant().getId());
		uiModel.addAttribute("restaurant", restaurant);
		return restaurant;
	}
}
