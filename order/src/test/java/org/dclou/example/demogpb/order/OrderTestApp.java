package org.dclou.example.demogpb.order;

import org.dclou.example.demogpb.order.clients.CatalogStub;
import org.dclou.example.demogpb.order.clients.Customer;
import org.dclou.example.demogpb.order.clients.Item;
import org.dclou.example.demogpb.order.clients.CustomerStub;
import org.dclou.example.demogpb.order.logic.Order;
import org.dclou.example.demogpb.order.logic.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class OrderTestApp {

	private OrderRepository orderRepository;

	@Autowired
	public OrderTestApp(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

/*
	@Bean
	RestTemplate restTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
		converter.setObjectMapper(mapper);
		//OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource, oauth2Context);
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(Collections.<HttpMessageConverter<?>> singletonList(converter));
		return restTemplate;
	}
*/

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

	@PostConstruct
	public void generateTestData() {
		CatalogStub clientStub	= new CatalogStub();
		Item item = clientStub.getAll().iterator().next();

		CustomerStub customerStub	= new CustomerStub();
		Customer customer = customerStub.getAll().iterator().next();

		Order o = new Order(customer.getCustomerId());
		o.addLine(3, item.getItemId());

		orderRepository.save(o);
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OrderTestApp.class);
		app.setAdditionalProfiles("test");
		app.run(args);
	}

}
