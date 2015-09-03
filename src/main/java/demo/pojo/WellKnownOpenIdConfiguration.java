package demo.pojo;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WellKnownOpenIdConfiguration {

	@JsonProperty("jwks_uri")
	private URI jwksUri;

	@JsonProperty("userinfo_endpoint")
	private URI userInfoEndpoint;

	public URI getJwksUri() {
		return jwksUri;
	}

	public void setJwksUri(URI jwksUri) {
		this.jwksUri = jwksUri;
	}

	public URI getUserInfoEndpoint() {
		return userInfoEndpoint;
	}

	public void setUserInfoEndpoint(URI userInfoEndpoint) {
		this.userInfoEndpoint = userInfoEndpoint;
	}

}
