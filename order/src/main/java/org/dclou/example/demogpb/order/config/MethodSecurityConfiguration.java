package org.dclou.example.demogpb.order.config;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Created by msnikitin on 28.04.2017.
 */
@Configuration
@EnableGlobalMethodSecurity(proxyTargetClass = true, mode = AdviceMode.PROXY,
        prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Profile("!test")
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
}
