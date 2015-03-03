package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * @author Erco
 */
@Entity
@Table(name = "DININGTABLES")
// optional
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"bills", "currentBill"})
public class DiningTable extends DomainObject {
	private static final long serialVersionUID = 1L;

	private int tableNr;
	
	// example of an *unidirectional* one-to-one relationship, mapped on
	// database by diningTable side
	@OneToOne(cascade = javax.persistence.CascadeType.ALL)
	private Bill currentBill;

	@OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "diningTable")
	private Collection<Bill> bills = new ArrayList<Bill>();

	@ManyToOne()
	private Restaurant restaurant;

	public DiningTable() {
		// when the system resets, the c'tor is executed and a new Bill object
		// is created (which in its turn creates a new order object. However, 
		// when the dining table becomes managed, the currentBill as was 
		// stored in the database is retrieved, and the new Bill and new Order
		// object, which were not managed yet are discarded.
		currentBill = new Bill();
		currentBill.setDiningTable(this);
		bills.add(currentBill);
	}

	public DiningTable(int tableNr) {
		this.tableNr = tableNr;
		// when the system resets, the c'tor is executed and a new Bill object
		// is created (which in its turn creates a new order object. However, 
		// when the dining table becomes managed, the currentBill as was 
		// stored in the database is retrieved, and the new Bill and new Order
		// object, which were not managed yet are discarded.
		currentBill = new Bill();
		currentBill.setDiningTable(this);
		bills.add(currentBill);
	}

	/* business logic */

	public void warmup() {
		Iterator<BaseOrderItem> orderItemIterator = currentBill.getCurrentOrder()
				.getOrderItems().iterator();
		while (orderItemIterator.hasNext()) {
			orderItemIterator.next().getId();
			// note: menu items have been warmed up via the restaurant->menu
			// relation; therefore it
			// is not needed to warm these objects via this relation
		}
	}

	public void submitBill() throws StateException, EmptyBillException {
		currentBill.submit();
		currentBill = new Bill();
		currentBill.setDiningTable(this);
		bills.add(currentBill);
	}

}
