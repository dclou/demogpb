package org.dclou.example.demogpb.customer.web;

import org.apache.commons.collections.IteratorUtils;
import org.dclou.example.demogpb.customer.Customer;
import org.dclou.example.demogpb.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CustomerController {

	private CustomerRepository customerRepository;

	@Autowired
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@RequestMapping(value = "/api/customer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody
	List<Customer> catalog() {
		List<Customer> list = IteratorUtils.toList(customerRepository.findAll().iterator());
		return list;
	}
}
