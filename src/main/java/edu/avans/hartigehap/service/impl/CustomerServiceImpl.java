package edu.avans.hartigehap.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.avans.hartigehap.domain.*;
import edu.avans.hartigehap.repository.*;
import edu.avans.hartigehap.service.*;
import com.google.common.collect.Lists;



@Service("customerService")
@Repository
@Transactional
public class CustomerServiceImpl implements CustomerService {
	final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Transactional(readOnly=true)
	public List<Customer> findAll() {
		return Lists.newArrayList(customerRepository.findAll());
	}

	@Transactional(readOnly=true)
	public Customer findById(Long id) {
		return customerRepository.findOne(id);
	}

	@Transactional(readOnly=true)
	public Customer findByFirstNameAndLastName(String firstName, String lastName) {
		
		Customer customer = null;
		
		List<Customer> customers = customerRepository.findByFirstNameAndLastName(firstName, lastName);
		if (!customers.isEmpty()) {
			customer = customers.get(0);
		}
		return customer;
	}


	@Transactional(readOnly=true)
	public List<Customer> findCustomersForRestaurant(Restaurant restaurant) {
		
		// a query created using a repository method name
		List<Customer> customersForRestaurants = customerRepository.
				findByRestaurants(
						Arrays.asList(new Restaurant[]{restaurant}),
						new Sort(Sort.Direction.ASC, "lastName"));

		logger.info("findCustomersForRestaurant using query created using repository method name");
		ListIterator<Customer>	it = customersForRestaurants.listIterator();
		while(it.hasNext()) {
			Customer customer = it.next();
			logger.info("customer = " + customer);
		}
		
		return customersForRestaurants;
	}
	
	@Transactional(readOnly=true)
	public Page<Customer> findAllByPage(Pageable pageable) {
		return customerRepository.findAll(pageable);
	}	

	@Transactional(readOnly=true)
	public Page<Customer> findCustomersForRestaurantByPage(Restaurant restaurant, Pageable pageable) {
		// a query created using a repository method name
		Page<Customer> customersForRestaurants = customerRepository.
				findByRestaurants((Collection<Restaurant>)Arrays.asList(new Restaurant[]{restaurant}), pageable);

		logger.info("findCustomersForRestaurant using query created using repository method name");
		Iterator<Customer>	it = customersForRestaurants.iterator();
		while(it.hasNext()) {
			Customer customer = it.next();
			logger.info("customer = " + customer);
		}
		
		return customersForRestaurants;
	}	

	
	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	public void delete(Long id) {
		customerRepository.delete(id);
	}

	
}

