package org.dclou.example.demogpb.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

import javax.annotation.PostConstruct;

@EnableHystrix
@EnableHystrixDashboard
@SpringCloudApplication
@EnableFeignClients
public class Application {

	private final ItemRepository itemRepository;

	@Autowired
	public Application(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@PostConstruct
	public void generateTestData() {
		itemRepository.save(new Item("iPod", 42.0));
		itemRepository.save(new Item("iPod touch", 21.0));
		itemRepository.save(new Item("iPod nano", 1.0));
		itemRepository.save(new Item("Apple TV", 100.0));
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
