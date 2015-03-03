package edu.avans.hartigehap.web.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;
import org.springframework.validation.*;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import edu.avans.hartigehap.web.form.*;
import javax.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.google.common.collect.Lists;
import edu.avans.hartigehap.web.util.*;

import java.util.*;

import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.service.*;
import javax.servlet.http.*;

@Controller
@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
public class CustomerController {
	final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private RestaurantService restaurantService;
	
	@RequestMapping(value = "/restaurants/{restaurantName}/customers", method = RequestMethod.GET)
	public String listCustomers(@PathVariable("restaurantName") String restaurantName, Model uiModel) {
		
		Restaurant restaurant = warmupRestaurant(restaurantName, uiModel);
		
		logger.info("Listing customers");
		List<Customer> customers = customerService.findCustomersForRestaurant(restaurant);
		uiModel.addAttribute("customers", customers);
		logger.info("No. of customers: " + customers.size());
		
		return "hartigehap/listcustomers";
	}
	
	@RequestMapping(value = "/restaurants/{restaurantName}/customers/{id}", method = RequestMethod.GET)
	public String showCustomer(@PathVariable("restaurantName") String restaurantName, @PathVariable("id") Long id, Model uiModel) {

		warmupRestaurant(restaurantName, uiModel);
		
		logger.info("Show customer: " + id);
		
		Customer customer = customerService.findById(id);
		uiModel.addAttribute("customer", customer);
		return "hartigehap/showcustomer";
	}

	@RequestMapping(value = "/restaurants/{restaurantName}/customers/{id}", params = "form", method = RequestMethod.GET)
	public String updateCustomerForm(@PathVariable("restaurantName") String restaurantName, @PathVariable("id") Long id, Model uiModel) {

		warmupRestaurant(restaurantName, uiModel);
		
		logger.info("Customer update form for customer: " + id);
		
		Customer customer = customerService.findById(id);
		uiModel.addAttribute("customer", customer);
		logger.info("updatingCustomerForm(" + customer.getFirstName() + ", " + customer.getLastName() + ")");
		return "hartigehap/editcustomer";
	}

	@RequestMapping(value = "/restaurants/{restaurantName}/customers/{id}", params = "form", method = RequestMethod.PUT)
	public String updateCustomer(
			// the path variable is not used; data binding retrieves its info from
			// query string parameters and form fields, so customer includes id as well
			@PathVariable("restaurantName") String restaurantName,
			@PathVariable("id") Long id, 
			@Valid Customer customer, 
			BindingResult bindingResult,
			Model uiModel, 
			HttpServletRequest httpServletRequest,
			RedirectAttributes redirectAttributes, 
			Locale locale,
			@RequestParam(required=false) Part file) {
		
		if(bindingResult.hasErrors()) {
			uiModel.addAttribute(
					"message",
					new Message("error", messageSource.getMessage(
							"customer_save_fail", new Object[] {}, locale)));
			uiModel.addAttribute("customer", customer);
			return "hartigehap/editcustomer";
		}
		uiModel.asMap().clear();
		redirectAttributes.addFlashAttribute(
				"message",
				new Message("success", messageSource.getMessage(
						"customer_save_success", new Object[] {}, locale)));

		// Process upload file
        if(file != null) {
			logger.info("File name: " + file.getName());
			logger.info("File size: " + file.getSize());
			logger.info("File content type: " + file.getContentType());
			byte[] fileContent = null;
			try {
				InputStream inputStream = file.getInputStream();
				if(inputStream == null) {
					logger.info("File inputstream is null");
				}
				fileContent = IOUtils.toByteArray(inputStream);
				customer.setPhoto(fileContent);
			} catch (IOException ex) {
				logger.error("Error saving uploaded file", ex);
			}
			customer.setPhoto(fileContent);
		}

        Customer existingCustomer = customerService.findById(customer.getId());
        assert existingCustomer != null : "customer should exist";
        
        // update user-editable fields
        existingCustomer.updateEditableFields(customer);

		customerService.save(existingCustomer);
		return "redirect:/restaurants/" + restaurantName + "/customers/"
				+ UrlUtil.encodeUrlPathSegment(customer.getId().toString(),
						httpServletRequest);
	}

	@RequestMapping(value = "/restaurants/{restaurantName}/customers", params = "form", method = RequestMethod.GET)
	public String createCustomerForm(@PathVariable("restaurantName") String restaurantName, Model uiModel) {

		warmupRestaurant(restaurantName, uiModel);
		
		logger.info("Create customer form");
		
		Customer customer = new Customer();
		uiModel.addAttribute("customer", customer);
		return "hartigehap/editcustomer";
	}
	

	@RequestMapping(value = "/restaurants/{restaurantName}/customers", params = "form", method = RequestMethod.POST)
	public String createCustomer(@PathVariable("restaurantName") String restaurantName, @Valid Customer customer, BindingResult bindingResult,
		Model uiModel, HttpServletRequest httpServletRequest,
		RedirectAttributes redirectAttributes, Locale locale,
		@RequestParam(value = "file", required = false) Part file) {

		logger.info("Creating customer: " + customer.getFirstName() + " " + customer.getLastName());	
		logger.info("Binding Result target: " + (Customer) bindingResult.getTarget()); 
		logger.info("Binding Result: " + bindingResult);
		
		if(bindingResult.hasErrors()) {
			uiModel.addAttribute(
					"message",
					new Message("error", messageSource.getMessage(
							"customer_save_fail", new Object[] {}, locale)));
			uiModel.addAttribute("customer", customer);
			return "hartigehap/editcustomer";
		}
		uiModel.asMap().clear();
		redirectAttributes.addFlashAttribute(
				"message",
				new Message("success", messageSource.getMessage(
						"customer_save_success", new Object[] {}, locale)));
		
		// Process upload file
		if(file != null) {
			logger.info("File name: " + file.getName());
			logger.info("File size: " + file.getSize());
			logger.info("File content type: " + file.getContentType());
			byte[] fileContent = null;
			try {
				InputStream inputStream = file.getInputStream();
				if(inputStream == null) {
					logger.info("File inputstream is null");
				}
				fileContent = IOUtils.toByteArray(inputStream);
				customer.setPhoto(fileContent);
			} catch (IOException ex) {
				logger.error("Error saving uploaded file", ex);
			}
			customer.setPhoto(fileContent);
		}

		// relate customer to current restaurant
		Restaurant restaurant = warmupRestaurant(restaurantName, uiModel);
		customer.setRestaurants(Arrays.asList(new Restaurant[]{restaurant}));
		
		// to get the auto generated id
		Customer storedCustomer = customerService.save(customer);
		
		return "redirect:/restaurants/" + restaurantName + "/customers/"
				+ UrlUtil.encodeUrlPathSegment(storedCustomer.getId().toString(),
						httpServletRequest);
	}

	@RequestMapping(value = "/restaurants/{restaurantName}/customers/{id}/photo", method = RequestMethod.GET)
	@ResponseBody
	public byte[] downloadPhoto(@PathVariable("id") Long id) {
		Customer customer = customerService.findById(id);
		if(customer.getPhoto() != null) {
			logger.info("Downloading photo for id: {} with size: {}",
					customer.getId(), customer.getPhoto().length);
		}
		return customer.getPhoto();
	}

	// to be truly RESTful use DELETE instead of GET
	@RequestMapping(value = "/restaurants/{restaurantName}/customers/{id}", params = "delete", method = RequestMethod.GET)
	public String delete(@PathVariable("restaurantName") String restaurantName, @PathVariable("id") Long id) {

		logger.info("Deleting customer: " + id);
		customerService.delete(id);
		return "redirect:/restaurants/" + restaurantName + "/customers/";
	}


	@RequestMapping(value = "/restaurants/{restaurantName}/customers/listgrid", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public CustomerGrid listGrid(
			@PathVariable("restaurantName") String restaurantName,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "sidx", required = false) String sortBy,
			@RequestParam(value = "sord", required = false) String order) {
		logger.info("Listing customers for grid with page: {}, rows: {}", page,
				rows);
		logger.info("Listing customers for grid with sort: {}, order: {}",
				sortBy, order);
		// Process order by
		Sort sort = null;
		String orderBy = sortBy;
		if(orderBy != null && "birthDateString".equals(orderBy)) {
			orderBy = "birthDate";
		}
		if(orderBy != null && order != null) {
			if ("desc".equals(order)) {
				sort = new Sort(Sort.Direction.DESC, orderBy);
			} else {
				sort = new Sort(Sort.Direction.ASC, orderBy);
			}
		}

		// Constructs page request for current page
		// Note: page number for Spring Data JPA starts with 0, while jqGrid
		// starts with 1
		PageRequest pageRequest = null;
		if(sort != null) {
			pageRequest = new PageRequest(page - 1, rows, sort);
		} else {
			pageRequest = new PageRequest(page - 1, rows);
		}

		// no need to warmup, as is ajax
		Restaurant restaurant = restaurantService.findById(restaurantName);
		
		Page<Customer> customerPage = customerService.findCustomersForRestaurantByPage(restaurant, pageRequest);
		
		// Construct the grid data that will return as JSON data
		CustomerGrid customerGrid = new CustomerGrid();
		customerGrid.setCurrentPage(customerPage.getNumber() + 1);
		customerGrid.setTotalPages(customerPage.getTotalPages());
		customerGrid.setTotalRecords(customerPage.getTotalElements());
		
		List<Customer> customers = Lists.newArrayList(customerPage.iterator());

		// customer has relations to Restaurants and Bills. The current JQGrid
		// cannot handle a JSON response with all information about restaurants
		// and bills in it. So temporary solution:
		Iterator<Customer> custIt = customers.iterator();
		while(custIt.hasNext()) {
			Customer c = custIt.next();
			c.setRestaurants(null);
			c.setBills(null);
		}
		
		customerGrid.setCustomerData(customers);
		return customerGrid;
	}

	private Restaurant warmupRestaurant(String restaurantName, Model uiModel) {
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		Restaurant restaurant = restaurantService.fetchWarmedUp(restaurantName);
		uiModel.addAttribute("restaurant", restaurant);
		return restaurant;
	}

}