package org.dclou.example.demogpb.order.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
public class CustomerClient {

	private final Logger log = LoggerFactory.getLogger(CustomerClient.class);

	private RestTemplate restTemplate;
	private String customerServiceHost;
	private long customerServicePort;
	private boolean useRibbon;
	private LoadBalancerClient loadBalancer;

	static class CustomerPagedResources extends PagedResources<Customer> {

	}

	@Autowired
	public CustomerClient(
			@Value("${customer.service.host:customer}") String customerServiceHost,
			@Value("${customer.service.port:8080}") long customerServicePort,
			@Value("${ribbon.eureka.enabled:false}") boolean useRibbon,
			RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.customerServiceHost = customerServiceHost;
		this.customerServicePort = customerServicePort;
		this.useRibbon = useRibbon;
	}

	@Autowired(required = false)
	public void setLoadBalancer(LoadBalancerClient loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public boolean isValidCustomerId(long customerId) {
		return true;
//		try {
//			ResponseEntity<String> entity = getRestTemplate().getForEntity(
//					customerURL() + customerId, String.class);
//			return entity.getStatusCode().is2xxSuccessful();
//		} catch (final HttpClientErrorException e) {
//			if (e.getStatusCode().value() == 404)
//				return false;
//			else
//				throw e;
//		}
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public Collection<Customer> findAll() {
		PagedResources<Customer> pagedResources = getRestTemplate()
				.getForObject(customerURL(), CustomerPagedResources.class);
		return pagedResources.getContent();
	}

	private String customerURL() {
		String url;
		ServiceInstance instance = loadBalancer.choose("CUSTOMER");
		if (useRibbon && instance != null) {
			url = "http://" + instance.getHost() + ":" + instance.getPort()
					+ "/api/customer/";

		} else {
			url = "http://" + customerServiceHost + ":" + customerServicePort
					+ "/api/customer/";
		}
		log.debug("Customer: URL {} ", url);
		return url;

	}

	public Customer getOne(long customerId) {
		return getRestTemplate().getForObject(customerURL() + customerId,
				Customer.class);
	}
}
