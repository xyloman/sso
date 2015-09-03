package demo.pojo;

public class Jwk {

	private String alg;

	private String e;

	private String n;

	private String kty;

	private String kid;

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}

	public String getE() {
		return e;
	}

	public void setE(String e) {
		this.e = e;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getKty() {
		return kty;
	}

	public void setKty(String kty) {
		this.kty = kty;
	}

	public String getKid() {
		return kid;
	}

	public void setKid(String kid) {
		this.kid = kid;
	}

	@Override
	public String toString() {
		return "Jwk [alg=" + alg + ", e=" + e + ", n=" + n + ", kty=" + kty + ", kid=" + kid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alg == null) ? 0 : alg.hashCode());
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((kid == null) ? 0 : kid.hashCode());
		result = prime * result + ((kty == null) ? 0 : kty.hashCode());
		result = prime * result + ((n == null) ? 0 : n.hashCode());
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
		Jwk other = (Jwk) obj;
		if (alg == null) {
			if (other.alg != null)
				return false;
		} else if (!alg.equals(other.alg))
			return false;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		if (kid == null) {
			if (other.kid != null)
				return false;
		} else if (!kid.equals(other.kid))
			return false;
		if (kty == null) {
			if (other.kty != null)
				return false;
		} else if (!kty.equals(other.kty))
			return false;
		if (n == null) {
			if (other.n != null)
				return false;
		} else if (!n.equals(other.n))
			return false;
		return true;
	}

}
