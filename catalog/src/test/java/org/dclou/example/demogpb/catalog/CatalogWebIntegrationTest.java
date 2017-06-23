package org.dclou.example.demogpb.catalog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CatalogApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CatalogWebIntegrationTest {

	@Autowired
	private ItemRepository itemRepository;

	@Value("${local.server.port}")
	private int serverPort;

	private Item iPodNano;

    @Autowired
    private TestRestTemplate restTemplate;

	@Before
	public void setup() {
		iPodNano = itemRepository.findByName("iPod nano").get(0);
	}

	@Test
	public void IsItemReturnedAsHTML() {
        String url = catalogURL() + "/" + iPodNano.getId() + ".html";
        String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, containsString("<div"));
	}

	private String catalogURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void IsItemReturnedAsJON() {
		String url = catalogURL() + "/catalog/" + iPodNano.getId();
		Item body = getForMediaType(Item.class, MediaType.APPLICATION_JSON, url);

		assertThat(body, equalTo(iPodNano));
	}

	@Test
	public void FormReturned() {
		String url = catalogURL() + "/searchForm.html";
		String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("<form"));
		assertThat(body, containsString("<div>"));
	}

	@Test
	public void SearchWorks() {
		String url = catalogURL() + "/searchByName.html?query=iPod";
		String body = restTemplate.getForObject(url, String.class);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, containsString("<div"));
	}

	private <T> T getForMediaType(Class<T> value, MediaType mediaType, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(mediaType, MediaType.APPLICATION_XHTML_XML));

		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<T> resultEntity = restTemplate.exchange(url, HttpMethod.GET, entity, value);

		return resultEntity.getBody();
	}

}
