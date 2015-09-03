package demo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

public class OIDCAccessTokenConverter implements AccessTokenConverter {

	private UserAuthenticationConverter userAuthenticationConverter;

	private boolean includeGrantType;

	@Override
	public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {

		Map<String, String> parameters = new HashMap<String, String>();
		Set<String> scope = new LinkedHashSet<String>(
				map.containsKey(SCOPE) ? (Collection<String>) map.get(SCOPE) : Collections.<String> emptySet());
		String clientId = (String) map.get(CLIENT_ID);
		parameters.put(CLIENT_ID, clientId);
		if (includeGrantType && map.containsKey(GRANT_TYPE)) {
			parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
		}

		Set<String> resourceIds = new LinkedHashSet<String>(
				map.containsKey(AUD) ? (Collection<String>) map.get(AUD) : Collections.<String> emptySet());

		OAuth2Request request = new OAuth2Request(parameters, clientId, null, true, scope, resourceIds, null, null,
				null);
		return new OAuth2Authentication(request, userAuthenticationConverter.extractAuthentication(map));
	}

	public void setUserAuthenticationConverter(UserAuthenticationConverter userAuthenticationConverter) {
		this.userAuthenticationConverter = userAuthenticationConverter;
	}

	public void setIncludeGrantType(boolean includeGrantType) {
		this.includeGrantType = includeGrantType;
	}
}
