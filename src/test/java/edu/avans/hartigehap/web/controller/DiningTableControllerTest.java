package edu.avans.hartigehap.web.controller;


import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;

import edu.avans.hartigehap.domain.DiningTable;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.RestaurantPopulatorService;
import edu.avans.hartigehap.service.testutil.AbstractTransactionRollbackTest;

public class DiningTableControllerTest extends AbstractTransactionRollbackTest {

	@Autowired
	private DiningTableController diningTableController;

	@Autowired
	private RestaurantPopulatorService restaurantPopulatorService;

	@Test
	public void dummy() {
		// empty - tests configuration of test context.
	}

	@Test
	public void createRestaurantsWithInventory() {
		// restaurantPopulatorService.createRestaurantsWithInventory() is implicitly called
		// for every test case if the package scan includes the controller map.
		// so no need to call it in this method

		
		String diningTableId = "1";
		ExtendedModelMap uiModel = new ExtendedModelMap();
		diningTableController.showTable(diningTableId, uiModel);
		// http://stackoverflow.com/questions/2592642/type-safety-unchecked-cast-from-object
		@SuppressWarnings("unchecked") 
		Collection<Restaurant> restaurants = (Collection<Restaurant>) uiModel.get("restaurants");
		DiningTable diningTable = (DiningTable) uiModel.get("diningTable");		
		Restaurant restaurant = (Restaurant) uiModel.get("restaurant");
		
		System.out.println("menu meals = " + restaurants.iterator().next().getMenu().getMeals());
		System.out.println("menu drinks = " + restaurants.iterator().next().getMenu().getDrinks());
		
		System.out.println("menu meals = " + diningTable.getRestaurant().getMenu().getMeals());
		System.out.println("menu drinks = " + diningTable.getRestaurant().getMenu().getDrinks());
		
		System.out.println("menu meals = " + restaurant.getMenu().getMeals());
		System.out.println("menu drinks = " + restaurant.getMenu().getDrinks());
	}

}

