package org.dclou.example.demogpb.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RibbonClient("order")
public class OrderServiceApplication {
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate template = new RestTemplate();

		return template;
	}
	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
}
