package edu.avans.hartigehap.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@MappedSuperclass
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter
@Setter
@ToString(includeFieldNames = true, of = { "id", "version" })
public abstract class DomainObject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	@Setter(AccessLevel.NONE)
	Long version;

	/*
	 * This hashCode() implementation violates an important property: 
	 * - The id changes during the lifetime of the object: before being 
	 *   persisted, the id is not set (so null). During invocation of the 
	 *   persist method, the id is provided by the ORM provider. After being 
	 *   persisted, the id is immutable (as a good id is being supposed to 
	 *   behave).
	 * - The result of this violation is that if the entities are used as keys 
	 *   in a HashSet or HashMap, they will become lost when being persisted.
	 * 
	 * This is the equals() implementation taken from
	 * http://www.onjava.com/pub/a
	 * /onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html?page=1 
	 * (see Netbeans project EqualsHashCodeTryOut).
	 */
	@Override
	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		} else {
			return super.hashCode();
		}
	}

	/*
	 * This equals() implementation is meant for entities with *surrogate id*,
	 * where the id is provided by the ORM provider upon invocation of the
	 * persist method. This means that there is a period between object creation
	 * and persisting of the entity, where the id is not set and therefore null.
	 * 
	 * The properties are as follows:
	 * - if two non-persisted entities are compared, they are by definition 
	 *   unequal. This allow storing them in a set, even before being persisted. 
	 * - if two persisted entities are compared, they are compared based on their 
	 *   surrogate id (no other information from the object is used). 
	 * - if a persisted an a non-persisted entity are compared, they are by 
	 *   definition unequal.
	 * 
	 * This is the equals() implementation taken from
	 * http://www.onjava.com/pub/a
	 * /onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html?page=1
	 * (see Netbeans project EqualsHashCodeTryOut).
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		// null is not an instance of anything, so check whether null is not necessary
		if (!(o instanceof DomainObject)) {
			return false;
		}

		DomainObject other = (DomainObject) o;

		// unsaved objects are never equal
		if (id == null || other.getId() == null) {
			return false;
		}

		return id.equals(other.getId());
	}
}
