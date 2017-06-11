package org.dclou.example.demogpb.order.config;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msnikitin on 28.04.2017.
 */
@Configuration
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    private static class DummyCommand extends HystrixCommand<Void> {
        DummyCommand() {
            super(HystrixCommandGroupKey.Factory.asKey("Dummy"));
        }

        @Override
        protected Void run() throws Exception {
            return null;
        }
    }

    @PostConstruct
    public void workaround() {
        new DummyCommand().execute();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint("/login.html");

        // @formatter:off
        http
            .exceptionHandling()
            .accessDeniedPage("/login.html")
            .authenticationEntryPoint(authenticationEntryPoint)
        .and()
            .csrf().disable()
            .rememberMe().disable()
            .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
            .logout().logoutSuccessUrl("/login.html").invalidateHttpSession(true).permitAll()
        .and()
            .antMatcher("/**")
            .authorizeRequests()
            .antMatchers( "/login**", "/logout**", "/auth-service/**", "/auth/**", "/webjars/**",
                "/v2/api-docs", "/favicon.ico", "/configuration/ui", "/swagger-resources",
			    "/configuration/security", "/swagger-resources/configuration/ui",
                "/swagger-resources/configuration/security", "/swagger-ui.html",
                "/documentation/v2/api-docs", "/documentation/configuration/ui",
                "/documentation/swagger-resources", "/documentation/configuration/security",
                "/documentation/swagger-resources/configuration/ui",
                "/documentation/swagger-resources/configuration/security",
                "/documentation/swagger-ui.html", "/documentation/swagger*", "/documentation/webjars/**", "/mgmt/**")
            .permitAll()
            .anyRequest().authenticated();
        // @formatter:on
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(ssoFilter(oauth2settings(), "/login/order"));

        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(WebSecurityConfiguration.ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);

        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);

        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);

        filter.setTokenServices(tokenServices);
        return filter;
    }

    @Bean
    @ConfigurationProperties("oauth2settings")
    public WebSecurityConfiguration.ClientResources oauth2settings() {
        return new WebSecurityConfiguration.ClientResourcesAuthCodeGrant();
    }

    @Bean
    public OAuth2RestTemplate restTemplate() {
        OAuth2RestTemplate template = new OAuth2RestTemplate(oauth2settings().getClient(), oauth2ClientContext);
        template.setRetryBadAccessTokens(false);
        return template;
    }

    interface ClientResources {
        BaseOAuth2ProtectedResourceDetails getClient();

        ResourceServerProperties getResource();
    }

    class ClientResourcesAuthCodeGrant implements WebSecurityConfiguration.ClientResources {
        @NestedConfigurationProperty
        private ResourceServerProperties resource = new ResourceServerProperties();

        @NestedConfigurationProperty
        private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @Override
        public AuthorizationCodeResourceDetails getClient() {
            return client;
        }

        @Override
        public ResourceServerProperties getResource() {
            return resource;
        }
    }
}
