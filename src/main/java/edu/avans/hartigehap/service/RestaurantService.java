package edu.avans.hartigehap.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.avans.hartigehap.domain.Restaurant;

public interface RestaurantService {
	List<Restaurant> findAll();
	Restaurant findById(String name);
	Restaurant save(Restaurant restaurant);
	void delete(String name);
	Page<Restaurant> findAllByPage(Pageable pageable);
		
	Restaurant fetchWarmedUp(String restaurantName);
}