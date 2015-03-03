package edu.avans.hartigehap.repository;

import java.util.List;

import edu.avans.hartigehap.domain.Order;
import edu.avans.hartigehap.domain.Restaurant;

public interface OrderRepositoryCustom {

	// this is a custom method for which Spring cannot create an implementation
	// so we need to make our own repository implementation!
	List<Order> findSubmittedOrdersForRestaurant(Restaurant restaurant);
}
