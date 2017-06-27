package org.dclou.example.demogpb.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.dclou.example.demogpb.order.clients.CatalogClient;
import org.dclou.example.demogpb.order.clients.Customer;
import org.dclou.example.demogpb.order.clients.CustomerClient;
import org.dclou.example.demogpb.order.clients.Item;
import org.dclou.example.demogpb.order.logic.Order;
import org.dclou.example.demogpb.order.logic.OrderRepository;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class OrderTestApp {

    private OrderRepository orderRepository;

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private CustomerClient customerClient;

	@Autowired
	public OrderTestApp(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Bean
	RestTemplate restTemplate() {
/*
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
		converter.setObjectMapper(mapper);
		//OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource, oauth2Context);
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(Collections.<HttpMessageConverter<?>> singletonList(converter));
		return restTemplate;
*/
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                jsonConverter.setObjectMapper(new ObjectMapper());
                jsonConverter.setSupportedMediaTypes(ImmutableList.of(new MediaType("application", "json", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET), new MediaType("text", "javascript", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET)));
            }
        }
        return restTemplate;

    }

	@PostConstruct
	public void generateTestData() {
        Order o = new Order(2);
        o.addLine(3, 1);

        orderRepository.save(o);
	}

    @PreDestroy
    public void cleanUp() {
    }

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OrderTestApp.class);
		app.setAdditionalProfiles("test");
		app.run(args);
	}

}
