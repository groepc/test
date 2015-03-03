package edu.avans.hartigehap.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.avans.hartigehap.domain.MenuItem;
import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.testutil.AbstractTransactionRollbackTest;

public class RestaurantServiceTest extends AbstractTransactionRollbackTest {

	@Autowired
	private RestaurantService restaurantService;
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
		List<Restaurant> restaurants = restaurantService.findAll();
		
		assertEquals("HartigeHap", restaurants.get(0).getId());
		assertEquals("HmmmBurger", restaurants.get(1).getId());
		assertEquals("PittigePannekoek", restaurants.get(2).getId());
		List<MenuItem> meals = new ArrayList<>(restaurants.get(0).getMenu().getMeals());
		assertEquals("spaghetti", meals.get(0).getId());
		assertEquals("macaroni", meals.get(1).getId());
		List<MenuItem> drinks = new ArrayList<>(restaurants.get(0).getMenu().getDrinks());
		assertEquals("beer", drinks.get(0).getId());

	}

}
