package org.dclou.example.demogpb.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.dclou.example.demogpb.order.clients.CatalogClient;
import org.dclou.example.demogpb.order.clients.CustomerClient;
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

    @Value("${catalog.service.port}")
    private int catalogServerPort;

    @Value("${customer.service.port}")
    private int customerServerPort;

    private ClientAndServer mockServerCatalog;
    private ClientAndServer mockServerCustomer;
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

        // Items
        mockServerCatalog = startClientAndServer(catalogServerPort);

        // given
        mockServerCatalog
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/catalog/api/catalog")
                )
                .respond(
                        response()
                                .withHeaders(
                                        new Header(CONTENT_TYPE.toString(), "application/json")
                                )
                                .withBody("" +
                                        "["+ System.getProperty("line.separator") +
                                        "   {"+ System.getProperty("line.separator") +
                                        "      \"id\":1,"+ System.getProperty("line.separator") +
                                        "      \"name\":\"iPod\","+ System.getProperty("line.separator") +
                                        "      \"price\":42.0"+ System.getProperty("line.separator") +
                                        "   },"+ System.getProperty("line.separator") +
                                        "   {"+ System.getProperty("line.separator") +
                                        "      \"id\":2,"+ System.getProperty("line.separator") +
                                        "      \"name\":\"iPod touch\","+ System.getProperty("line.separator") +
                                        "      \"price\":21.0"+ System.getProperty("line.separator") +
                                        "   },"+ System.getProperty("line.separator") +
                                        "   {"+ System.getProperty("line.separator") +
                                        "      \"id\":3,"+ System.getProperty("line.separator") +
                                        "      \"name\":\"iPod nano\","+ System.getProperty("line.separator") +
                                        "      \"price\":1.0"+ System.getProperty("line.separator") +
                                        "   },"+ System.getProperty("line.separator") +
                                        "   {"+ System.getProperty("line.separator") +
                                        "      \"id\":4,"+ System.getProperty("line.separator") +
                                        "      \"name\":\"Apple TV\","+ System.getProperty("line.separator") +
                                        "      \"price\":100.0"+ System.getProperty("line.separator") +
                                        "   }"+ System.getProperty("line.separator") +
                                        "]")
                );
        mockServerCatalog
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/catalog/api/catalog/1")
                )
                .respond(
                        response()
                                .withHeaders(
                                        new Header(CONTENT_TYPE.toString(), "application/json")
                                )
                                .withBody("" +
                                        "{"+ System.getProperty("line.separator") +
                                        "   \"id\":1,"+ System.getProperty("line.separator") +
                                        "   \"name\":\"iPod\","+ System.getProperty("line.separator") +
                                        "   \"price\":42.0"+ System.getProperty("line.separator") +
                                        "}")
                );

        // Customers
        mockServerCustomer = startClientAndServer(customerServerPort);

        // given
        mockServerCustomer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/customer/api/customer")
                )
                .respond(
                        response()
                                .withHeaders(
                                        new Header(CONTENT_TYPE.toString(), "application/json")
                                )
                                .withBody("" +
                                        "["+ System.getProperty("line.separator") +
                                        "   {"+ System.getProperty("line.separator") +
                                        "      \"id\":1,"+ System.getProperty("line.separator") +
                                        "      \"name\":\"Wolff\","+ System.getProperty("line.separator") +
                                        "      \"firstname\":\"Eberhard\","+ System.getProperty("line.separator") +
                                        "      \"email\":\"eberhard.wolff@gmail.com\","+ System.getProperty("line.separator") +
                                        "      \"street\":\"Unter den Linden\","+ System.getProperty("line.separator") +
                                        "      \"city\":\"Berlin\""+ System.getProperty("line.separator") +
                                        "   },"+ System.getProperty("line.separator") +
                                        "   {"+ System.getProperty("line.separator") +
                                        "      \"id\":2,"+ System.getProperty("line.separator") +
                                        "      \"name\":\"Johnson\","+ System.getProperty("line.separator") +
                                        "      \"firstname\":\"Rod\","+ System.getProperty("line.separator") +
                                        "      \"email\":\"rod@somewhere.com\","+ System.getProperty("line.separator") +
                                        "      \"street\":\"Market Street\","+ System.getProperty("line.separator") +
                                        "      \"city\":\"San Francisco\""+ System.getProperty("line.separator") +
                                        "   },"+ System.getProperty("line.separator") +
                                        "   {"+ System.getProperty("line.separator") +
                                        "      \"id\":3,"+ System.getProperty("line.separator") +
                                        "      \"name\":\"Hoeller\","+ System.getProperty("line.separator") +
                                        "      \"firstname\":\"Juergen\","+ System.getProperty("line.separator") +
                                        "      \"email\":\"springjuergen@twitter.com\","+ System.getProperty("line.separator") +
                                        "      \"street\":\"Schlossallee\","+ System.getProperty("line.separator") +
                                        "      \"city\":\"Linz\""+ System.getProperty("line.separator") +
                                        "   }"+ System.getProperty("line.separator") +
                                        "]")
                );
        mockServerCustomer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/customer/api/customer/1")
                )
                .respond(
                        response()
                                .withHeaders(
                                        new Header(CONTENT_TYPE.toString(), "application/json")
                                )
                                .withBody("" +
                                        "{"+ System.getProperty("line.separator") +
                                        "   \"id\":1,"+ System.getProperty("line.separator") +
                                        "   \"name\":\"Wolff\","+ System.getProperty("line.separator") +
                                        "   \"firstname\":\"Eberhard\","+ System.getProperty("line.separator") +
                                        "   \"email\":\"eberhard.wolff@gmail.com\","+ System.getProperty("line.separator") +
                                        "   \"street\":\"Unter den Linden\","+ System.getProperty("line.separator") +
                                        "   \"city\":\"Berlin\""+ System.getProperty("line.separator") +
                                        "}")
                );
        mockServerCustomer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/customer/api/customer/2")
                )
                .respond(
                        response()
                                .withHeaders(
                                        new Header(CONTENT_TYPE.toString(), "application/json")
                                )
                                .withBody("" +
                                        "{"+ System.getProperty("line.separator") +
                                        "   \"id\":2,"+ System.getProperty("line.separator") +
                                        "   \"name\":\"Johnson\","+ System.getProperty("line.separator") +
                                        "   \"firstname\":\"Rod\","+ System.getProperty("line.separator") +
                                        "   \"email\":\"rod@somewhere.com\","+ System.getProperty("line.separator") +
                                        "   \"street\":\"Market Street\","+ System.getProperty("line.separator") +
                                        "   \"city\":\"San Francisco\""+ System.getProperty("line.separator") +
                                        "}")
                );

    }

    @PreDestroy
    public void cleanUp() {
        mockServerCatalog.stop();
        mockServerCustomer.stop();
    }

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OrderTestApp.class);
		app.setAdditionalProfiles("test");
		app.run(args);
	}

}
