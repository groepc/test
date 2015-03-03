package edu.avans.hartigehap.service;

import java.util.List;

import edu.avans.hartigehap.domain.*;

public interface BillService {
	Bill findById(Long billId);
	void billHasBeenPaid(Bill bill) throws StateException;
	List<Bill> findSubmittedBillsForRestaurant(Restaurant restaurant);
}
