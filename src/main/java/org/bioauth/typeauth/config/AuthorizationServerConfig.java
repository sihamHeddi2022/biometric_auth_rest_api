package org.bioauth.typeauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//super.configure(endpoints);
		endpoints.tokenStore(tokenStore())
				.accessTokenConverter(accessTokenConverter())
				.authenticationManager(authenticationManager)
		;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
	//	super.configure(clients);
		clients.inMemory()
				.withClient("client1")
				.secret(passwordEncoder.encode("cpass1"))
				.scopes("read")
				.authorizedGrantTypes(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())
				.accessTokenValiditySeconds(300);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter()
	{
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		//accessTokenConverter.setSigningKey("bio");
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore()
	{
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public DefaultTokenServices tokenServices()
	{
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		//defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

}
