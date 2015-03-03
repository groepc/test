package edu.avans.hartigehap.service;

public interface RestaurantPopulatorService {
	public static final String HARTIGEHAP_RESTAURANT_NAME = "HartigeHap";
	public static final String PITTIGEPANNEKOEK_RESTAURANT_NAME = "PittigePannekoek";
	public static final String HMMMBURGER_RESTAURANT_NAME = "HmmmBurger";

	void createRestaurantsWithInventory();
}
