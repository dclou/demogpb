package org.dclou.example.demogpb.order.logic;

import com.codahale.metrics.annotation.Gauge;
import io.swagger.annotations.*;
import org.dclou.example.demogpb.order.clients.CatalogClient;
import org.dclou.example.demogpb.order.clients.Customer;
import org.dclou.example.demogpb.order.clients.CustomerClient;
import org.dclou.example.demogpb.order.clients.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;


@Controller
@Api(value = "Order")
class OrderController {

	private OrderRepository orderRepository;
	private OrderService orderService;
	private CustomerClient customerClient;
	private CatalogClient catalogClient;

	@Autowired
	public OrderController(OrderService orderService,
			OrderRepository orderRepository, CustomerClient customerClient,
			CatalogClient catalogClient) {
		this.orderRepository = orderRepository;
		this.customerClient = customerClient;
		this.catalogClient = catalogClient;
		this.orderService = orderService;
	}

	@ModelAttribute("items")
	public Collection<Item> items() {
		return catalogClient.findAll();
	}

	@ModelAttribute("customers")
	public Collection<Customer> customers() {
		return customerClient.findAll();
	}

	@RequestMapping("/")
	@ApiOperation(value = "/", nickname = "orderList", response = Order.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Failure")})
	public ModelAndView orderList() {
			Iterable<Order> o = orderRepository.findAll();
			return new ModelAndView("orderlist", "orders", o);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	@ApiOperation(value = "/form", nickname = "form", response = Order.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Failure")})
	public ModelAndView form() {
		return new ModelAndView("orderForm", "order", new Order());
	}

	@RequestMapping(value = "/line", method = RequestMethod.POST)
	@ApiOperation(value = "/line", nickname = "line", response = Order.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "order", value = "Order object", required = true, dataType = "Order", paramType = "query")
	})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Failure")})
	public ModelAndView addLine(Order order) {
		order.addLine(0, catalogClient.findAll().iterator().next().getItemId());
		return new ModelAndView("orderForm", "order", order);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "/{id}", nickname = "/", response = Order.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Order's id", required = true, dataType = "long", paramType = "query", defaultValue="0")
	})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Failure")})
	public ModelAndView get(@PathVariable("id") long id) {
		return new ModelAndView("order", "order", orderRepository.findOne(id));
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@Gauge(name = "new.order")
	public ModelAndView post(Order order) {
		order = orderService.order(order);
		return  new ModelAndView("redirect:/");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "/{id}", nickname = "deleteOrder")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Failure")})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Order's id", required = true, dataType = "long", paramType = "query", defaultValue="0")
	})
	public ModelAndView post(@PathVariable("id") long id) {
		orderRepository.delete(id);

		return new ModelAndView("redirect:/");
	}
}
