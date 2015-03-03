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
public class KitchenController {

	final Logger logger = LoggerFactory.getLogger(KitchenController.class);

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/restaurants/{restaurantName}/kitchen", method = RequestMethod.GET)
	public String showKitchen(
			@PathVariable("restaurantName") String restaurantName, Model uiModel) {

		// warmup stuff
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(restaurantName);
		uiModel.addAttribute("restaurant", restaurant);

		List<Order> allSubmittedOrders = orderService
				.findSubmittedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allSubmittedOrders", allSubmittedOrders);

		List<Order> allPlannedOrders = orderService
				.findPlannedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allPlannedOrders", allPlannedOrders);

		return "hartigehap/kitchen";
	}


	
	@RequestMapping(value = "/kitchen/orders/{orderId}", method = RequestMethod.GET)
	public String showOrderInKitchen(@PathVariable("orderId") String orderId,
			Model uiModel, Locale locale) {

		// warmup stuff
		Order order = orderService.findById(Long.valueOf(orderId));
		Restaurant restaurant = warmupRestaurant(order, uiModel);
		uiModel.addAttribute("restaurant", restaurant);

		List<Order> allSubmittedOrders = orderService
				.findSubmittedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allSubmittedOrders", allSubmittedOrders);

		List<Order> allPlannedOrders = orderService
				.findPlannedOrdersForRestaurant(restaurant);
		uiModel.addAttribute("allPlannedOrders", allPlannedOrders);

		String orderContent = "";
		for(BaseOrderItem orderItem : order.getOrderItems()) {
			orderContent += orderItem.getMenuItem().getId() + " (" + orderItem.getQuantity() + "x)" + "; ";
		}
		
		uiModel.addAttribute("message", new Message("info",
				messageSource.getMessage("label_order_content", new Object[]{}, locale) + ": " + orderContent));

		return "hartigehap/kitchen";
	}
	
	
	@RequestMapping(value = "/kitchen/orders/{orderId}", method = RequestMethod.PUT)
	public String receiveOrderEvent(@PathVariable("orderId") String orderId,
			@RequestParam String event, Model uiModel, Locale locale) {

		switch (event) {
		case "planOrder":
			return planOrder(orderId, uiModel, locale);
			// break unreachable

		case "orderHasBeenPrepared":
			return orderHasBeenPrepared(orderId, uiModel, locale);
			// break unreachable

		default:
			logger.error("Internal error: event " + event + " not recognized");
			Order order = orderService.findById(Long.valueOf(orderId));
			Restaurant restaurant = warmupRestaurant(order, uiModel);
			return "redirect:/restaurants/" + restaurant.getId();
		}
	}

	private String planOrder(String orderId, Model uiModel, Locale locale) {
		Order order = orderService.findById(Long.valueOf(orderId));
		Restaurant restaurant = warmupRestaurant(order, uiModel);
		try {
			orderService.planOrder(order);
		} catch (StateException e) {
			logger.error(
					"Internal error has occurred! Order "
							+ Long.valueOf(orderId)
							+ "has not been changed to planned state!", e);

			// StateException triggers a rollback; consequently all Entities are
			// invalidated by Hibernate
			// So new warmup needed
			warmupRestaurant(order, uiModel);
			return "hartigehap/kitchen";
		}
		return "redirect:/restaurants/" + restaurant.getId() + "/kitchen";
	}

	private String orderHasBeenPrepared(String orderId, Model uiModel,
			Locale locale) {
		Order order = orderService.findById(Long.valueOf(orderId));
		Restaurant restaurant = warmupRestaurant(order, uiModel);
		try {
			orderService.orderPrepared(order);
		} catch (StateException e) {
			logger.error(
					"Internal error has occurred! Order "
							+ Long.valueOf(orderId)
							+ "has not been changed to prepared state!", e);

			// StateException triggers a rollback; consequently all Entities are
			// invalidated by Hibernate
			// So new warmup needed
			warmupRestaurant(order, uiModel);
			return "hartigehap/kitchen";
		}
		return "redirect:/restaurants/" + restaurant.getId() + "/kitchen";
	}


	private Restaurant warmupRestaurant(Order order, Model uiModel) {
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(order.getBill()
				.getDiningTable().getRestaurant().getId());
		uiModel.addAttribute("restaurant", restaurant);
		return restaurant;
	}
}
