package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * @author Erco
 */
@Entity
//optional
@Table(name = "BILLS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id") 
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"billStatus", "currentOrder", "orders"})
public class Bill extends DomainObject {
	private static final long serialVersionUID = 1L;
	
	public enum BillStatus {
		CREATED, SUBMITTED, PAID
	}
	
	// represented in database as integer
	@Enumerated(EnumType.ORDINAL)
	private BillStatus billStatus;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date paidTime;

	// unidirectional one-to-one relationship
	@OneToOne(cascade = javax.persistence.CascadeType.ALL)
	private Order currentOrder;
	
	@OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "bill")
	private Collection<Order> orders = new ArrayList<Order>();
	
	// bidirectional one-to-many relationship
	@ManyToOne()
	private DiningTable diningTable;

	// bidirectional one-to-many relationship
	@ManyToOne(cascade = javax.persistence.CascadeType.ALL)
	private Customer customer;
	
	public Bill() {
		billStatus = BillStatus.CREATED;
		currentOrder = new Order();
		currentOrder.setBill(this);
		orders.add(currentOrder);
	}


	/* business logic */

	@Transient
	public Collection<Order> getSubmittedOrders() {
		Collection<Order> submittedOrders = new ArrayList<Order>();
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			Order tmp = orderIterator.next();
			if (tmp.isSubmittedOrSuccessiveState()) {
				submittedOrders.add(tmp);
			}
		}
		return submittedOrders;
	}

	/**
	 * price of *all* orders, so submitted orders and current (not
	 * yet submitted) order
	 * @return
	 */
	@Transient
	public int getPriceAllOrders() {
		int price = 0;
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			price += orderIterator.next().getPrice();
		}
		return price;
	}

	/**
	 * price of the *submitted or successive state* orders only
	 * @return
	 */
	@Transient
	public int getPriceSubmittedOrSuccessiveStateOrders() {
		int price = 0;
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			Order tmp = orderIterator.next();
			if (tmp.isSubmittedOrSuccessiveState()) {
				price += tmp.getPrice();
			}
		}
		return price;
	}

	public void submitOrder() throws StateException {
		currentOrder.submit();
		currentOrder = new Order();
		currentOrder.setBill(this);
		orders.add(currentOrder);
	}

	/*
	 * as the table gets a new bill, there is no risk that a customer keeps
	 * ordering on the submitted or paid bill
	 */
	public void submit() throws StateException, EmptyBillException {
		boolean allEmpty = true;
		Iterator<Order> orderIterator = orders.iterator();
		while (orderIterator.hasNext()) {
			Order order = orderIterator.next();
			if (!order.isEmpty()) {
				allEmpty = false;
				break;
			}
		}
		if (allEmpty) {
			throw new EmptyBillException("not allowed to submit an empty bill");
		}

		if (!currentOrder.isEmpty() && currentOrder.getOrderStatus() == Order.OrderStatus.CREATED) {
			// the currentOrder is not empty, but not yet submitted
			throw new StateException("not allowed to submit an with currentOrder in created state");
		}

		// this can only happen by directly invoking HTTP requests, so not via
		// GUI
		// TODO better to use another exception, because now GUI show wrong
		// error message
		if (billStatus != BillStatus.CREATED) {
			throw new StateException(
			        "not allowed to submit an already submitted bill");
		}

		submittedTime = new Date();
		billStatus = BillStatus.SUBMITTED;
	}

	@Transient
	public boolean isSubmitted() {
		return billStatus == BillStatus.SUBMITTED;
	}

	public void paid() throws StateException {

		// this can only happen by directly invoking HTTP requests, so not via
		// GUI
		if (billStatus != BillStatus.SUBMITTED) {
			throw new StateException(
			        "not allowed to pay an bill that is not in the submitted state");
		}

		paidTime = new Date();
		billStatus = BillStatus.PAID;
	}

}
