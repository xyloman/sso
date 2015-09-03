package demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

public class OIDCResourceServerTokenServices implements ResourceServerTokenServices {

	private OAuth2RestOperations restTemplate;

	private AccessTokenConverter tokenConverter;

	public void setRestTemplate(OAuth2RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setTokenConverter(AccessTokenConverter tokenConverter) {
		this.tokenConverter = tokenConverter;
	}

	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		// when loading the authentication the access token that is contained on
		// the rest template holds additional
		OAuth2AccessToken token = restTemplate.getAccessToken();
		Map<String, Object> map = new HashMap<>();

		// carry over the additional information obtained on the original access
		// token to gain access to id_token and additional information that was
		// provided during log in.
		map.putAll(token.getAdditionalInformation());
		map.put("client_id", restTemplate.getResource().getClientId());
		map.put("scope", token.getScope());
		return tokenConverter.extractAuthentication(map);

	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new RuntimeException("operation not supported.");
	}

}
