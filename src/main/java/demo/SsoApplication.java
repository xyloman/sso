package demo;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import demo.pojo.OIDCAuthentication;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@RequestMapping("/dashboard")
@EnableOAuth2Sso
public class SsoApplication {

	@Autowired
	private OAuth2RestOperations restOperations;

	@RequestMapping("/message")
	public Map<String, Object> dashboard(Principal user) {
		OIDCAuthentication authentication = (OIDCAuthentication) ((OAuth2Authentication) user).getUserAuthentication();

		@SuppressWarnings("unchecked")
		Map<String, ?> details = (Map<String, ?>) authentication.getDetails();
		return Collections.<String, Object> singletonMap("message", restOperations
				.getForObject("https://fhir-api.smarthealthit.org/Patient/" + details.get("patient"), String.class));
	}

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	public static void main(String[] args) {
		SpringApplication.run(SsoApplication.class, args);
	}

	@Controller
	public static class LoginErrors {

		@RequestMapping("/dashboard/login")
		public String dashboard() {
			return "redirect:/#/";
		}

	}

	@Component
	public static class LoginConfigurer extends OAuth2SsoConfigurerAdapter {

		@Override
		public void match(RequestMatchers matchers) {
			matchers.antMatchers("/dashboard/**");
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/dashboard/**").authorizeRequests().anyRequest().authenticated().and().csrf()
					.csrfTokenRepository(csrfTokenRepository()).and()
					.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
		}

		private Filter csrfHeaderFilter() {
			return new OncePerRequestFilter() {
				@Override
				protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
						FilterChain filterChain) throws ServletException, IOException {
					CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
					if (csrf != null) {
						Cookie cookie = new Cookie("XSRF-TOKEN", csrf.getToken());
						cookie.setPath("/");
						response.addCookie(cookie);
					}
					filterChain.doFilter(request, response);
				}
			};
		}

		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}
	}

	@Autowired
	private OAuth2RestOperations oauth2RestOperations;

	@Bean(name = { "userInfoTokenServices", "oidResourceServerTokenServices" })
	public ResourceServerTokenServices resourceServerTokenServices() {
		OIDCResourceServerTokenServices tokenServices = new OIDCResourceServerTokenServices();
		tokenServices.setRestTemplate(oauth2RestOperations);
		tokenServices.setTokenConverter(tokenConverter());
		return tokenServices;
	}

	@Bean
	public UserAuthenticationConverter userAuthenticationConverter() {
		OIDUserAuthenticationConverter converter = new OIDUserAuthenticationConverter();
		converter.setOIDCOperations(oidcOperations());
		return converter;
	}

	@Bean
	public OIDCOperations oidcOperations() {
		OIDCOperations operations = new OIDCOperations();
		operations.setOAuth2RestOperations(oauth2RestOperations);
		return operations;
	}

	@Bean
	public AccessTokenConverter tokenConverter() {
		OIDCAccessTokenConverter tokenConverter = new OIDCAccessTokenConverter();
		tokenConverter.setUserAuthenticationConverter(userAuthenticationConverter());
		return tokenConverter;
	}
}
