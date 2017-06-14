package org.dclou.example.demogpb.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

/**
 * Created by Александр Подоплелов on 06.06.2017.
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/documentation/v2/api-docs", "/v2/api-docs");
        registry.addRedirectViewController("/documentation/configuration/ui", "/configuration/ui");
        registry.addRedirectViewController("/documentation/configuration/security", "/configuration/security");
        registry.addRedirectViewController("/documentation/swagger-resources", "/swagger-resources");
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/ui", "/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/security", "/swagger-resources/configuration/security");
        registry.addRedirectViewController("/documentation", "/documentation/swagger-ui.html");
        registry.addRedirectViewController("/documentation/", "/documentation/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/documentation/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-resources/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    }

}

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Autowired
    ServletContext servletContext;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathProvider(new RelativePathProvider(servletContext)) ;
    }
}
