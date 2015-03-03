package edu.avans.hartigehap.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.avans.hartigehap.domain.BaseOrderItem;

public interface BaseOrderItemRepository extends PagingAndSortingRepository<BaseOrderItem, String> {

}
