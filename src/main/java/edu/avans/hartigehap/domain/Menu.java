package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "MENUS")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"meals", "mealOptions", "drinks", "foodCategories"})
public class Menu extends DomainObject {
	private static final long serialVersionUID = 1L;

	// unidirectional many-to-many relationship + no cascade
	// there are multiple relations between Menu and MenuItem. In order to
	// distinguish these, they must be mapped to separate join tables
	@ManyToMany
	@JoinTable(name="menus_meals")
	private Collection<MenuItem> meals = new ArrayList<MenuItem>();
	
	@ManyToMany
	@JoinTable(name="menus_mealoptions")
	private Collection<MenuItem> mealOptions = new ArrayList<MenuItem>();

	// unidirectional many-to-many relationship + no cascade
	// there are multiple relations between Menu and MenuItem. In order to
	// distinguish these, they must be mapped to separate join tables
	@ManyToMany
	@JoinTable(name="menus_drinks")
	private Collection<MenuItem> drinks = new ArrayList<MenuItem>();


	// unidirectional many-to-many relationship + no cascade
	@ManyToMany
	private List<FoodCategory> foodCategories = new ArrayList<FoodCategory>();

}
