package edu.avans.hartigehap.web.controller;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.service.*;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.*;
import javax.annotation.PostConstruct;


@Controller
public class RestaurantController {
		
	final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
	
	@Autowired
	private RestaurantService restaurantService;
	@Autowired
	private RestaurantPopulatorService restaurantPopulatorService;

	// mapping to "/" is not RESTful, but is for bootstrapping!
	@RequestMapping(value = {"/", "/restaurants"}, method = RequestMethod.GET)
	public String listRestaurants(Model uiModel) {
		
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);

		// use HartigeHap as default restaurant
		Restaurant restaurant = restaurantService.fetchWarmedUp(RestaurantPopulatorService.HARTIGEHAP_RESTAURANT_NAME);
		uiModel.addAttribute("restaurant", restaurant);
		
		return "hartigehap/listrestaurants";
	}


	@RequestMapping(value = "/restaurants/{restaurantName}", method = RequestMethod.GET)
	public String showRestaurant(@PathVariable("restaurantName") String restaurantName, Model uiModel) {

		// warmup stuff
		Collection<Restaurant> restaurants = restaurantService.findAll();
		uiModel.addAttribute("restaurants", restaurants);
		
		Restaurant restaurant = restaurantService.fetchWarmedUp(restaurantName);
		uiModel.addAttribute("restaurant", restaurant);
		
		return "hartigehap/restaurant";
	}
	
	// called once immediately after bean creation
	@PostConstruct
	public void createRestaurants() {
		restaurantPopulatorService.createRestaurantsWithInventory();
	}
	
}
