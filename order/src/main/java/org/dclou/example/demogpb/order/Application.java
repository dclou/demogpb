package org.dclou.example.demogpb.order;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableHystrix
@SpringCloudApplication
@RibbonClient("order")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
