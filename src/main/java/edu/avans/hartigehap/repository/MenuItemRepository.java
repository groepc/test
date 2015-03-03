package edu.avans.hartigehap.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import edu.avans.hartigehap.domain.MenuItem;

public interface MenuItemRepository extends PagingAndSortingRepository<MenuItem, String> {
}