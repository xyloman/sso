package demo.pojo;

import java.util.Collection;
import java.util.Iterator;

public class JwkSet implements Iterable<Jwk> {

	private Collection<Jwk> keys;

	public Collection<Jwk> getKeys() {
		return keys;
	}

	public void setKeys(Collection<Jwk> keys) {
		this.keys = keys;
	}
	

	@Override
	public Iterator<Jwk> iterator() {
		return this.keys.iterator();
	}

	@Override
	public String toString() {
		return "Jwks [keys=" + keys + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keys == null) ? 0 : keys.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JwkSet other = (JwkSet) obj;
		if (keys == null) {
			if (other.keys != null)
				return false;
		} else if (!keys.equals(other.keys))
			return false;
		return true;
	}


}
