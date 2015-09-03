package demo;

import static org.springframework.security.jwt.codec.Codecs.b64UrlDecode;

import java.math.BigInteger;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import demo.pojo.Jwk;
import demo.pojo.JwkSet;
import demo.pojo.OIDCUserInfo;
import demo.pojo.WellKnownOpenIdConfiguration;

public class OIDCOperations {

	private RestOperations restOperations = new RestTemplate();

	private OAuth2RestOperations oauth2RestOperations;

	/**
	 * Converts an {@link Jwk} into a {@link SignatureVerifier} which can be
	 * used by {@link Jwt#verifySignature(SignatureVerifier)}
	 * 
	 * @param key
	 *            to construct a {@link SignatureVerifier} for.
	 * @return constructed {@link SignatureVerifier}
	 */
	public SignatureVerifier jwkToSignatureVerifier(Jwk key) {
		String alg = key.getAlg();
		switch (alg.toUpperCase()) {
		case "RS256":
			return new RsaVerifier(b64ToBigInt(key.getN()), b64ToBigInt(key.getE()));
		default:
			throw new RuntimeException("Unsupported JWK [" + key + "]");
		}
	}

	/**
	 * Attempts to find/construct a {@link SignatureVerifier} for the kty
	 * provided that is specific to the issuer and kid.
	 * 
	 * @param issuer
	 *            - to find / construct the signature verifier for.
	 * @param kty
	 *            - the kty associated with the {@link Jwk} that will need to be
	 *            retrieved, typically RSA.
	 * @param kid
	 *            - the kid associated with with the {@link Jwk} to support
	 *            rotation.
	 * @return an instance of the {@link SignatureVerifier} that can be used to
	 *         validate the signature.
	 */
	public SignatureVerifier findSignatureVerifierByIssuerAndKtyAndKeyId(String issuer, String kty, String kid) {
		Jwk jwk = findJwkByIssuerAndKtyAndKeyId(issuer, kty, kid);
		if (jwk == null) {
			throw new RuntimeException("JWK not found for issuer [" + issuer + "] kty [RSA] and kid [" + kid + "]");
		}
		return jwkToSignatureVerifier(jwk);
	}

	/**
	 * Attempts to find the JSON Web Key (JWK) by issuer and algorithm provided.
	 * 
	 * @param issuer
	 *            - associated to the JWK.
	 * @param algorithm
	 *            - that the JWK is for.
	 * @param kid
	 *            - the kid associated with the JWK (in the case that key
	 *            rotation is supported). If the provided value is null then the
	 *            first {@link Jwk} found for the associated algorithm is
	 *            returned.
	 * @return JSON Web Key {@link Jwk} associated with the issuer and algorithm
	 *         provided.
	 */
	public Jwk findJwkByIssuerAndKtyAndKeyId(String issuer, String algorithm, String kid) {
		WellKnownOpenIdConfiguration config = findWellKnownOpenIdConfigurationByIssuer(issuer);
		JwkSet jwks = restOperations.getForObject(config.getJwksUri(), JwkSet.class);
		for (Jwk key : jwks) {
			if (algorithm.equalsIgnoreCase(key.getKty())) {
				if (StringUtils.hasText(kid)) {
					if (!kid.equalsIgnoreCase(key.getKid())) {
						return null;
					}
					return key;
				}
				return key;
			}
		}
		return null;
	}

	/**
	 * Attempts to find {@link OIDCUserInfo} associated to the
	 * {@link OAuth2RestOperations#getOAuth2ClientContext()}
	 * 
	 * @param issuer
	 *            - to find {@link OIDCUserInfo} for
	 * @return discovered {@link OIDCUserInfo}.
	 */
	public OIDCUserInfo findUserInfo(String issuer) {
		return oauth2RestOperations.getForObject(
				this.findWellKnownOpenIdConfigurationByIssuer(issuer).getUserInfoEndpoint(), OIDCUserInfo.class);
	}

	/**
	 * Attempts to find the .well-known/openid-configuration resource associated
	 * to the provided issuer.
	 * 
	 * @param issuer
	 * @return {@link WellKnownOpenIdConfiguration} associated to the issuer
	 *         provided.
	 */
	public WellKnownOpenIdConfiguration findWellKnownOpenIdConfigurationByIssuer(String issuer) {
		return restOperations.getForObject("{issuer}/.well-known/openid-configuration",
				WellKnownOpenIdConfiguration.class, issuer);
	}

	/**
	 * Converts a Base64 encoded URL to a {@link BigInteger}
	 * 
	 * @param value
	 *            - Base64 encoded URL to be converted
	 */
	private static BigInteger b64ToBigInt(String value) {
		return new BigInteger(1, b64UrlDecode(value));
	}

	public void setRestOperations(RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	public void setOAuth2RestOperations(OAuth2RestOperations oauth2RestOperations) {
		this.oauth2RestOperations = oauth2RestOperations;
	}
}
