package org.dclou.example.demogpb.customer.config;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by msnikitin on 21.04.2017.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(proxyTargetClass = true, mode = AdviceMode.PROXY,
        prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Profile("!test")
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
}
