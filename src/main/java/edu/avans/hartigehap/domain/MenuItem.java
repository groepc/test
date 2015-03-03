package edu.avans.hartigehap.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
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
@Table(name = "MENUITEMS")
// images are stored in a separate database table (optional)
@SecondaryTable(name = "MENUITEM_IMAGES", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true, of= {})
@NoArgsConstructor
public abstract class MenuItem extends DomainObjectNaturalId {
	private static final long serialVersionUID = 1L;

	// image stored in the database
	@Column(name = "IMAGE", table = "MENUITEM_IMAGES")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] image;
	
	// filename of image stored in the database
	@Column(name = "IMAGEFILENAME")
	private String imageFileName; 
	
	// JPA is case sensitive: the corresponding column name will be in small
	// caps "price"
	private int price;

	// no cascade
	@ManyToMany
	private Collection<FoodCategory> foodCategories = new ArrayList<FoodCategory>();
	
	public MenuItem(String id, String imageFileName, int price) {
		super(id);
		this.imageFileName = imageFileName;
		this.price = price;

	}

	public void addFoodCategories(Collection<FoodCategory> foodCategories) {
		setFoodCategories(foodCategories);
		for(FoodCategory foodCategory : foodCategories) {
			foodCategory.getMenuItems().add(this);
		}
	}

	/* business logic */
}
