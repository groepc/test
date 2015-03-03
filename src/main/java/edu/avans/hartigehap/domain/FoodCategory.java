package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * @author Erco
 */
@Entity
@Table(name = "FOODCATEGORIES")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {"tag", "menuItems"})
@NoArgsConstructor
public class FoodCategory extends DomainObject {
	private static final long serialVersionUID = 1L;
	
	private String tag;

	// no cascade
	@ManyToMany(mappedBy = "foodCategories")
	private Collection<MenuItem> menuItems = new ArrayList<MenuItem>();
	
	public FoodCategory(String tag) {
		this.tag = tag;
	}


}
