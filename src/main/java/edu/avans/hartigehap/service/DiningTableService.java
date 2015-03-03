package edu.avans.hartigehap.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import edu.avans.hartigehap.domain.*;

public interface DiningTableService {

	List<DiningTable> findAll();
	DiningTable findById(Long id);
	DiningTable save(DiningTable diningTable);
	void delete(Long id);
	Page<DiningTable> findAllByPage(Pageable pageable);
		
	DiningTable fetchWarmedUp(Long diningTableId);
	void addOrderItem(DiningTable diningTable, String menuItemName);
	void deleteOrderItem(DiningTable diningTable, String menuItemName);
	void submitOrder(DiningTable diningTable) throws StateException;
	void submitBill(DiningTable diningTable) throws StateException, EmptyBillException;
}