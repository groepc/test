package edu.avans.hartigehap.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.repository.*;
import edu.avans.hartigehap.service.*;
import org.joda.time.DateTime;

@Service("restaurantPopulatorService")
@Repository
@Transactional
public class RestaurantPopulatorServiceImpl implements RestaurantPopulatorService {
	final Logger logger = LoggerFactory.getLogger(RestaurantPopulatorServiceImpl.class);
	
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private FoodCategoryRepository foodCategoryRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private BaseOrderItemRepository baseOrderItemRepository;
	
	private List<Meal> meals = new ArrayList<Meal>();
	private List<FoodCategory> foodCats = new ArrayList<FoodCategory>();
	private List<Drink> drinks = new ArrayList<Drink>();
	private List<Meal> mealOptions = new ArrayList<Meal>();
	private List<Customer> customers = new ArrayList<Customer>();

		
	/**
	 *  menu items, food categories and customers are common to all restaurants and should be created only once.
	 *  Although we can safely assume that the are related to at least one restaurant and therefore are saved via
	 *  the restaurant, we save them explicitly anyway
	 */
	private void createCommonEntities() {
		
		createFoodCategory("low fat");
		createFoodCategory("high energy");
		createFoodCategory("vegatarian");
		createFoodCategory("italian");
		createFoodCategory("asian");
		createFoodCategory("alcoholic drinks");
		createFoodCategory("energizing drinks");
		
		createMeal("spaghetti", "spaghetti.jpg", 8, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}));
		createMeal("macaroni", "macaroni.jpg", 8, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}));		
		createMeal("canneloni", "canneloni.jpg", 9, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}));
		createMeal("pizza", "pizza.jpg", 9, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1)}));
		createMeal("carpaccio", "carpaccio.jpg", 7, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(0)}));
		createMeal("ravioli", "ravioli.jpg", 8, "easy",
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(1), foodCats.get(2)}));

		createMealOption("bell pepper", "pizza.jpg", 2, "easy",
				Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(2)}));
		createMealOption("mushrooms", "pizza.jpg", 3, "easy",
				Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(2)}));
		createMealOption("mozzarella", "pizza.jpg", 1, "easy",
				Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(2)}));
		createMealOption("shrimps", "pizza.jpg", 5, "easy",
				Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(2)}));
		createMealOption("cream cheese", "pizza.jpg", 5, "easy",
				Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(3), foodCats.get(2)}));
		
		createDrink("beer", "beer.jpg", 1, Drink.Size.LARGE,
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(5)}));
		createDrink("coffee", "coffee.jpg", 1, Drink.Size.MEDIUM,
			Arrays.<FoodCategory>asList(new FoodCategory[]{foodCats.get(6)}));
		
		byte[] photo = new byte[]{127,-128,0};
		createCustomer("piet", "bakker", new DateTime(), 1, "description", photo);
		createCustomer("piet", "bakker", new DateTime(), 1, "description", photo);
		createCustomer("piet", "bakker", new DateTime(), 1, "description", photo);

	}

	private void createFoodCategory(String tag) {
		FoodCategory foodCategory = new FoodCategory(tag);
		foodCategory = foodCategoryRepository.save(foodCategory);
		foodCats.add(foodCategory);
	}
	
	private void createMeal(String name, String image, int price, String recipe, List<FoodCategory> foodCats) {
		Meal meal = new Meal(name, image, price, recipe);
		// as there is no cascading between FoodCategory and MenuItem (both ways), it is important to first 
		// save foodCategory and menuItem before relating them to each other, otherwise you get errors
		// like "object references an unsaved transient instance - save the transient instance before flushing:"
		meal.addFoodCategories(foodCats);
		meal = menuItemRepository.save(meal);
		meals.add(meal);
	}
	
	private void createMealOption(String name, String image, int price, String recipe, List<FoodCategory> foodCats) {
		Meal meal = new Meal(name, image, price, recipe);
		// as there is no cascading between FoodCategory and MenuItem (both ways), it is important to first 
		// save foodCategory and menuItem before relating them to each other, otherwise you get errors
		// like "object references an unsaved transient instance - save the transient instance before flushing:"
		meal.addFoodCategories(foodCats);
		meal = menuItemRepository.save(meal);
		mealOptions.add(meal);
	}
	
	private void createDrink(String name, String image, int price, Drink.Size size, List<FoodCategory> foodCats) {
		Drink drink = new Drink(name, image, price, size);
		drink = menuItemRepository.save(drink);
		drink.addFoodCategories(foodCats);
		drinks.add(drink);
	}
	
	private void createCustomer(String firstName, String lastName, DateTime birthDate,
		int partySize, String description, byte[] photo) {
		Customer customer = new Customer(firstName, lastName, birthDate, partySize, description, photo); 
		customers.add(customer);
		customerRepository.save(customer);
	}
	
	private void createDiningTables(int numberOfTables, Restaurant restaurant) {
		for(int i=0; i<numberOfTables; i++) {
			DiningTable diningTable = new DiningTable(i+1);
			diningTable.setRestaurant(restaurant);
			restaurant.getDiningTables().add(diningTable);
		}
	}
	
	private Restaurant populateRestaurant(Restaurant restaurant) {
				
		// will save everything that is reachable by cascading
		// even if it is linked to the restaurant after the save
		// operation
		restaurant = restaurantRepository.save(restaurant);

		// every restaurant has its own dining tables
		createDiningTables(5, restaurant);

		// for the moment every restaurant has all available food categories 
		for(FoodCategory foodCat : foodCats) {
			restaurant.getMenu().getFoodCategories().add(foodCat);
		}

		// for the moment every restaurant has the same menu 
		for(Meal meal : meals) {
			restaurant.getMenu().getMeals().add(meal);
		}

		// for the moment every restaurant has the same menu 
		for(Drink drink : drinks) {
			restaurant.getMenu().getDrinks().add(drink);
		}
		
		// for the moment every restaurant has the same meal options 
		for(Meal mealOption : mealOptions) {
			restaurant.getMenu().getMealOptions().add(mealOption);
		}
		
		// for the moment, every customer has dined in every restaurant
		// no cascading between customer and restaurant; therefore both restaurant and customer
		// must have been saved before linking them one to another
		for(Customer customer : customers) {
			customer.getRestaurants().add(restaurant);
			restaurant.getCustomers().add(customer);
		}
		
		return restaurant;
		
	}

	
	public void createRestaurantsWithInventory() {
		
		createCommonEntities();

		Restaurant restaurant = new Restaurant(HARTIGEHAP_RESTAURANT_NAME, "deHartigeHap.jpg");
		restaurant = populateRestaurant(restaurant);
		
		restaurant = new Restaurant(PITTIGEPANNEKOEK_RESTAURANT_NAME, "dePittigePannekoek.jpg");
		restaurant = populateRestaurant(restaurant);
		
		restaurant = new Restaurant(HMMMBURGER_RESTAURANT_NAME, "deHmmmBurger.jpg");
		restaurant = populateRestaurant(restaurant);
		
		
		/////////////////////// ORDER DECORATOR FREEHAND TEST ////////////////////////////////////////
		
		
		// how to ensure in the GUI that only pizza options can be added to pizza's?:
		// * use food category to distinguish pizza options from other options (use queries
		//   for showing it in the GUI)
		// * add a new category to distinguish pizza options from other options (use queries
		//   for showing it in the GUI)
		// * add a different collection in Menu for each type of option is a bad idea, because
		//   it hard-codes specific information
		
		// easy option to show the options in the GUI:
		// * Show on one page the menu item with its options as a radio box and a quantity
		//   for each option.
		// * All that information goes in one form to the controller and from there to the service impl.
		// * The service impl creates the decorators

		
		OrderItem orderItem = new OrderItem(meals.get(3), 1); // pizza
		baseOrderItemRepository.save(orderItem);
		OrderOption orderOption = new OrderOption(orderItem, mealOptions.get(0), 1); // bell pepper
		baseOrderItemRepository.save(orderOption);
		OrderOption orderOption2 = new OrderOption(orderOption, mealOptions.get(1), 3); // mushrooms
		baseOrderItemRepository.save(orderOption2);
		OrderOption orderOption3 = new OrderOption(orderOption2, mealOptions.get(2), 2); // mozzarella
		baseOrderItemRepository.save(orderOption3);
		OrderOption orderOption4 = new OrderOption(orderOption3, mealOptions.get(3), 5); // shrimps
		baseOrderItemRepository.save(orderOption4);
		// orderOption5 saved by cascading from Order
		OrderOption orderOption5 = new OrderOption(orderOption4, mealOptions.get(4), 1); // cream cheese
		logger.info("***************************** description: " + orderOption5.description());
		logger.info("***************************** price: " + orderOption5.getPrice());
		
		Collection<DiningTable> diningTables = restaurant.getDiningTables(); // dining tables of the hmmm burger
		DiningTable t = null;
		Iterator<DiningTable> it = diningTables.iterator();
		if(it.hasNext()) {
			t = it.next(); // this is dining table 1
		}
		
		// add the decorated pizza to the current order ot table 1 of the hmmm burger
		t.getCurrentBill().getCurrentOrder().getOrderItems().add(orderOption5);
		// add a less decorated pizza to the order
		t.getCurrentBill().getCurrentOrder().getOrderItems().add(orderOption3);		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////

	}	
}
