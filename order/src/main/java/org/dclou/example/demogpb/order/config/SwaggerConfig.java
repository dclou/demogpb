package org.dclou.example.demogpb.order.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

/**
 * Created by apodoplelov on 04.06.17.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    ServletContext servletContext;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.dclou.example.demogpb.order"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .pathProvider(new RelativePathProvider(servletContext)) ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Order Service API Tool")
                .description("The metadata for API of the Order Application")
                .version("1.0.0")
                .termsOfServiceUrl("http://demo.zatona.com")
                .license("LICENSE")
                .licenseUrl("http://url-to-license.com")
                .build();
    }
}
