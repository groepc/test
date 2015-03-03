package edu.avans.hartigehap.domain;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@NoArgsConstructor
@ToString(callSuper=true, includeFieldNames=true)
public abstract class DecoratedOrderItem extends BaseOrderItem {
	private static final long serialVersionUID = 1L;

	// unidirectional one-to-one and no cascading
	@OneToOne
	private BaseOrderItem orderItem;

	public DecoratedOrderItem(BaseOrderItem orderItem, MenuItem menuItem, int quantity) {
		super(menuItem, quantity);
		this.orderItem = orderItem;
	}

	@Transient
	public int getPrice() {
		return getMenuItem().getPrice() * getQuantity() + orderItem.getPrice();
	}

	public String description() {
		return orderItem.description() + " + extra " + super.description();
	}
}
