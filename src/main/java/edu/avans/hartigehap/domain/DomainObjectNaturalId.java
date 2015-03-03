package edu.avans.hartigehap.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@MappedSuperclass
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Getter
@Setter
@ToString(includeFieldNames = true, of = { "id", "version" })
@NoArgsConstructor
public abstract class DomainObjectNaturalId implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int PRIME = 31;

	// natural id (so no auto generation needed)
	@Id
	private String id;

	@Version
	@Setter(AccessLevel.NONE)
	Long version;

	DomainObjectNaturalId(String id) {
		this.id = id;
	}

	/*
	 * Domain objects with a natural id get their id upon object creation.
	 * During the complete lifetime of the object (before being persisted and
	 * after being persisted), the id will no change (so it's immutable). This
	 * is how a good id is supposed to behave. Therefore, we can use the
	 * "normal" equals() and hashcode() implementations.
	 * 
	 * This is the hashCode() implementation taken from lombok (see Netbeans
	 * project EqualsHashCodeTryOut). The implementation is slightly modified
	 * to prevent sonar complaining
	 */
	@Override
	public int hashCode() {
		int result = 1;
		Object _id = getId();
		result = result * PRIME + (_id == null ? 0 : _id.hashCode());
		return result;
	}

	/*
	 * Domain objects with a natural id get their id upon object creation.
	 * During the complete lifetime of the object (before being persisted and
	 * after being persisted), the id will no change (so it's immutable). This
	 * is how a good id is supposed to behave. Therefore, we can use the
	 * "normal" equals() and hashcode() implementations.
	 * 
	 * This is the equals() implementation taken from lombok (see Netbeans
	 * project EqualsHashCodeTryOut).
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof DomainObjectNaturalId)) {
			return false;
		}
		DomainObjectNaturalId other = (DomainObjectNaturalId) o;
		if (!other.canEqual(this)) {
			return false;
		}
		Object thisId = getId();
		Object otherId = other.getId();
		return thisId == null ? otherId == null : thisId.equals(otherId);
	}

	public boolean canEqual(Object other) {
		return other instanceof DomainObjectNaturalId;
	}
}
