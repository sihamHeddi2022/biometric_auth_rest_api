package org.bioauth.typeauth.config;

import org.bioauth.typeauth.service.ClientServiceDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	//@Autowired
	//private PasswordEncoder passwordEncoder;
	@Autowired
	private ClientServiceDb clientServiceDb;

	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	@Autowired
	private JwtTokenEnhancer jwtTokenEnhancer;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//super.configure(endpoints);
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtTokenEnhancer, accessTokenConverter));
		endpoints.tokenStore(tokenStore)
				.accessTokenConverter(accessTokenConverter)
				.tokenEnhancer(tokenEnhancerChain)
				.authenticationManager(authenticationManager)
		;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
	//	super.configure(clients);
		/*
		clients.inMemory()
				.withClient("client1")
				.secret(passwordEncoder.encode("cpass1"))
				.scopes("manage")
				.authorizedGrantTypes(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())
				.accessTokenValiditySeconds(300);
		 */
		clients.withClientDetails(clientServiceDb);
	}
}
