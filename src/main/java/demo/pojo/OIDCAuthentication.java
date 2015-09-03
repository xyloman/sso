package demo.pojo;

import static java.util.Collections.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class OIDCAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -1926145863966997556L;

	private Principal principal;

	private Map<String, ?> context;

	public OIDCAuthentication(String issuer, Principal principal, Map<String, ?> context,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		if (context == null) {
			this.context = emptyMap();
		} else {
			this.context = unmodifiableMap(context);
		}
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return "N/A";
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public Object getDetails() {
		return this.context;
	}

	@Override
	public String toString() {
		return "OIDCAuthentication [principal=" + principal + ", context=" + context + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((principal == null) ? 0 : principal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OIDCAuthentication other = (OIDCAuthentication) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (principal == null) {
			if (other.principal != null)
				return false;
		} else if (!principal.equals(other.principal))
			return false;
		return true;
	}

}
