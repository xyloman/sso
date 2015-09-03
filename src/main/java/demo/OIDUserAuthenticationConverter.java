package demo;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import demo.pojo.OIDCAuthentication;
import demo.pojo.OIDCUserInfo;

public class OIDUserAuthenticationConverter implements UserAuthenticationConverter {

	private UserDetailsService userDetailsService;

	// FIXME: should we use the one provided by spring boot?
	private JsonParser jsonParser = JsonParserFactory.create();

	private OIDCOperations oidcOperations;

	@Override
	public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
		throw new RuntimeException("Operation is currently not supported.");
	}

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		if (!map.containsKey("id_token")) {
			return null;
		}
		Map<String, Object> claims = validateIdToken(map);
		String issuer = (String) claims.get("iss");
		OIDCUserInfo principal = oidcOperations.findUserInfo(issuer);

		Map<String, ?> context = new HashMap<>(map);
		context.keySet().removeAll(asList("client_id", "id_token", "scope"));

		Collection<? extends GrantedAuthority> authorities = null;
		if (userDetailsService != null) {
			UserDetails user = userDetailsService.loadUserByUsername((String) map.get(USERNAME));
			authorities = user.getAuthorities();
			// principal = user;
		}
		return new OIDCAuthentication(issuer, principal, context, authorities);
	}

	/**
	 * IDTokenValidation
	 * 
	 * @param map
	 * @return the validated claims from the OIDC id_token
	 * @see http://openid.net/specs/openid-connect-core-1_0.html#
	 *      IDTokenValidation
	 */
	private Map<String, Object> validateIdToken(Map<String, ?> map) {
		Jwt idToken = JwtHelper.decode((String) map.get("id_token"));
		Map<String, Object> claims = jsonParser.parseMap(idToken.getClaims());

		idToken.verifySignature(
				oidcOperations.findSignatureVerifierByIssuerAndKtyAndKeyId((String) claims.get("iss"), "RSA", null));

		// FIXME: The Client MUST validate that the aud (audience) Claim
		// contains its client_id value registered at the Issuer identified
		// by the iss (issuer) Claim as an audience. The aud (audience)
		// Claim MAY contain an array with more than one element. The ID
		// Token MUST be rejected if the ID Token does not list the Client
		// as a valid audience, or if it contains additional audiences not
		// trusted by the Client.

		// FIXME: If the ID Token contains multiple audiences, the Client
		// SHOULD verify that an azp Claim is present.

		// FIXME: If an azp (authorized party) Claim is present, the Client
		// SHOULD verify that its client_id is the Claim Value.

		// FIXME: If the ID Token is received via direct communication
		// between the Client and the Token Endpoint (which it is in this
		// flow), the TLS server validation MAY be used to validate the
		// issuer in place of checking the token signature. The Client MUST
		// validate the signature of all other ID Tokens according to JWS
		// [JWS] using the algorithm specified in the JWT alg Header
		// Parameter. The Client MUST use the keys provided by the Issuer.

		// FIXME: The alg value SHOULD be the default of RS256 or the
		// algorithm sent by the Client in the id_token_signed_response_alg
		// parameter during Registration.

		// FIXME: validate The current time MUST be before the time
		// represented by the exp Claim.

		// FIXME: The iat Claim can be used to reject tokens that were
		// issued too far away from the current time, limiting the amount of
		// time that nonces need to be stored to prevent attacks. The
		// acceptable range is Client specific.

		// FIXME: If a nonce value was sent in the Authentication Request, a
		// nonce Claim MUST be present and its value checked to verify that
		// it is the same value as the one that was sent in the
		// Authentication Request. The Client SHOULD check the nonce value
		// for replay attacks. The precise method for detecting replay
		// attacks is Client specific.

		// FIXME: If the acr Claim was requested, the Client SHOULD check
		// that the asserted Claim Value is appropriate. The meaning and
		// processing of acr Claim Values is out of scope for this
		// specification.

		// FIXME: If the auth_time Claim was requested, either through a
		// specific request for this Claim or by using the max_age
		// parameter, the Client SHOULD check the auth_time Claim value and
		// request re-authentication if it determines too much time has
		// elapsed since the last End-User authentication.

		return claims;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public void setOIDCOperations(OIDCOperations oidcOperations) {
		this.oidcOperations = oidcOperations;
	}
}
