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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomerApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
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
	public void IsCustomerReturnedAsHTML() {

		String body = getForMediaType(String.class, MediaType.TEXT_HTML,
				customerURL() + "/" + customerWolf.getId() + ".html");

		assertThat(body, containsString("Wolff"));
		assertThat(body, containsString("<div"));
	}

	@Test
	public void IsCustomerReturnedAsJSON() {

		Customer customerWolff = customerRepository.findByName("Wolff").get(0);

		String url = customerURL() + "/customer/" + customerWolff.getId();
		Customer body = getForMediaType(Customer.class, MediaType.APPLICATION_JSON, url);

		assertThat(body, equalTo(customerWolff));
	}

	@Test
	public void IsCustomerListReturned() {

		Iterable<Customer> customers = customerRepository.findAll();
		assertTrue(
				StreamSupport.stream(customers.spliterator(), false).noneMatch(c -> (c.getName().equals("Hoeller1"))));
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(customerURL() + "/list.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		String customerList = resultEntity.getBody();
		assertFalse(customerList.contains("Hoeller1"));
		customerRepository
				.save(new Customer("Juergen", "Hoeller1", "springjuergen@twitter.com", "Schlossallee", "Linz"));

		customerList = restTemplate.getForObject(customerURL() + "/list.html", String.class);
		assertTrue(customerList.contains("Hoeller1"));

	}

	private String customerURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void IsCustomerFormDisplayed() {
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(customerURL() + "/form.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<form"));
	}

	@Test
	@Transactional
	public void IsSubmittedCustomerSaved() {
		assertEquals(0, customerRepository.findByName("Hoeller").size());
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("firstname", "Juergen");
		map.add("name", "Hoeller");
		map.add("street", "Schlossallee");
		map.add("city", "Linz");
		map.add("email", "springjuergen@twitter.com");

		restTemplate.postForObject(customerURL() + "/form.html", map, String.class);
		assertEquals(1, customerRepository.findByName("Hoeller").size());
	}

	@Test
	public void FetchRepositoryWorks() throws IOException {
		String url = customerURL() + "/api/list";
		String body = restTemplate.getForObject(url, String.class);
		Collection<Customer> items = new ObjectMapper().readValue(body, new TypeReference<Collection<Customer>>() { });

		assertThat(items.size(), equalTo(3));
	}

}
