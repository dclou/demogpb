package org.dclou.example.demogpb.order.logic;

import org.dclou.example.demogpb.order.OrderTestApp;
import org.dclou.example.demogpb.order.clients.CatalogClient;
import org.dclou.example.demogpb.order.clients.Customer;
import org.dclou.example.demogpb.order.clients.CustomerClient;
import org.dclou.example.demogpb.order.clients.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.stream.StreamSupport;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrderTestApp.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class OrderWebIntegrationTest {


    @Value("${catalog.service.port}")
    private int catalogServerPort;

    @Value("${customer.service.port}")
    private int customerServerPort;

    private ClientAndServer mockServerCatalog;
    private ClientAndServer mockServerCustomer;

    @Autowired
	private TestRestTemplate restTemplate;

	@Value("${server.port}")
	private long serverPort;

	@Autowired
	private CatalogClient catalogClient;

	@Autowired
	private CustomerClient customerClient;

	@Autowired
	private OrderRepository orderRepository;

	private Item item;

	private Customer customer;

    @Before
    public void launchMockServers() {
        // Items
        mockServerCatalog = startClientAndServer(catalogServerPort);

        // given
        mockServerCatalog
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/catalog")
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
                                .withPath("/api/catalog/1")
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
                                .withPath("/api/customer")
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
                                .withPath("/api/customer/1")
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
                                .withPath("/api/customer/2")
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


	@Before
	public void setup() throws Exception {
        item = catalogClient.findAll().iterator().next();
        customer = customerClient.findAll().iterator().next();
		assertEquals("Eberhard", customer.getFirstname());
	}

	@After
    public void StopServers() {
        mockServerCatalog.stop();
        mockServerCustomer.stop();
    }

    @Test
	public void IsOrderListReturned() {
		try {
			Iterable<Order> orders = orderRepository.findAll();
			assertTrue(StreamSupport.stream(orders.spliterator(), false)
                    .noneMatch(o -> (o.getCustomerId() == customer.getCustomerId())));
			//ResponseEntity<String> resultEntity = restTemplate.getForEntity(orderURL(), String.class);
            String orderList = restTemplate.getForObject(orderURL(), String.class);
			//assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
			//String orderList = resultEntity.getBody();
			assertFalse(orderList.contains("Eberhard"));
			Order order = new Order(customer.getCustomerId());
			order.addLine(42, item.getItemId());
			orderRepository.save(order);
			orderList = restTemplate.getForObject(orderURL(), String.class);
			assertTrue(orderList.contains("Eberhard"));
		} finally {
			orderRepository.deleteAll();
		}
	}

	private String orderURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void IsOrderFormDisplayed() {
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(orderURL() + "/form", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<form"));
	}

	@Test
	@Transactional
	public void IsSubmittedOrderSaved() {
		long before = orderRepository.count();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("submit", "");
		map.add("customerId", Long.toString(customer.getCustomerId()));
		map.add("orderLine[0].itemId", Long.toString(item.getItemId()));
		map.add("orderLine[0].count", "42");
		URI uri = restTemplate.postForLocation(orderURL(), map, String.class);
		UriTemplate uriTemplate = new UriTemplate(orderURL() + "/{id}");
		assertEquals(before + 1, orderRepository.count());
	}
}
