package org.dclou.example.demogpb.customer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CustomerWebIntegrationTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Value("${server.port}")
	private int serverPort;

	@Autowired
	private TestRestTemplate restTemplate;

	private Customer customerWolf;

	private <T> T getForMediaType(Class<T> value, MediaType mediaType, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(mediaType, MediaType.APPLICATION_XHTML_XML));

		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<T> resultEntity = restTemplate.exchange(url, HttpMethod.GET, entity, value);

		return resultEntity.getBody();
	}

	@Before
	public void setup() {
		customerWolf = customerRepository.findByName("Wolff").get(0);
	}

	@Test
	public void IsCustomerReturnedAsJSON() {

		Customer customerWolff = customerRepository.findByName("Wolff").get(0);

		String url = customerURL() + "/customer/" + customerWolff.getId();
		Customer body = getForMediaType(Customer.class, MediaType.APPLICATION_JSON, url);

		assertThat(body, equalTo(customerWolff));
	}

	private String customerURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void FetchRepositoryWorks() throws IOException {
		String url = customerURL() + "/api/customer";
		String body = restTemplate.getForObject(url, String.class);
		Collection<Customer> items = new ObjectMapper().readValue(body, new TypeReference<Collection<Customer>>() { });

		assertThat(items.size(), equalTo(2));
	}

}
