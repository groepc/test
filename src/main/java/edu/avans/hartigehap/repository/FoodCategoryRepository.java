package edu.avans.hartigehap.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import edu.avans.hartigehap.domain.FoodCategory;

public interface FoodCategoryRepository extends PagingAndSortingRepository<FoodCategory, String> {
}
