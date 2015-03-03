package edu.avans.hartigehap.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import edu.avans.hartigehap.domain.DiningTable;

public interface DiningTableRepository extends PagingAndSortingRepository<DiningTable, Long> {
}