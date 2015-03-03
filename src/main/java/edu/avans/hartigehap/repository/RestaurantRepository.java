package edu.avans.hartigehap.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import edu.avans.hartigehap.domain.Restaurant;

public interface RestaurantRepository extends PagingAndSortingRepository<Restaurant, String> {
}