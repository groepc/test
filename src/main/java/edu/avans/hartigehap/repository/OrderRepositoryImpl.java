package edu.avans.hartigehap.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.avans.hartigehap.domain.Order;
import edu.avans.hartigehap.domain.Restaurant;

// this impl class must be in the same package as the interfaces
// you could call this a spring limitation

public class OrderRepositoryImpl implements OrderRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	// this is a custom method for which Spring cannot create an implementation
	// so we need to make our own repository implementation!
	public List<Order> findSubmittedOrdersForRestaurant(Restaurant restaurant) {
		return em.createNamedQuery("Order.findSubmittedOrders", Order.class).
				setParameter("restaurant", restaurant).getResultList();		
	}

}
