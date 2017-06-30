package org.dclou.example.demogpb.catalog.config;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.PostConstruct;

/**
 * Created by msnikitin on 20.04.2017.
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

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
    public void configure(HttpSecurity http) throws Exception {
        http
                //.antMatcher("/api/**")
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/manage/**").permitAll()
                .anyRequest().hasAuthority("FOO_READ");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("catalog").tokenStore(tokenStore);
    }

    @Autowired
    TokenStore tokenStore;
}
