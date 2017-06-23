package org.dclou.example.demogpb.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerTestApp {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CustomerTestApp.class);
		app.setAdditionalProfiles("test");
		app.run(args);
	}

}
