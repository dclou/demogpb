package org.dclou.example.demogpb.order.logic;

import org.dclou.example.demogpb.order.OrderServiceApplication;
import org.dclou.example.demogpb.order.clients.CatalogClient;
import org.dclou.example.demogpb.order.clients.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class CatalogConsumerDrivenContractTest {

	@Autowired
	CatalogClient catalogClient;

	@Test
	public void testFindAll() {
		Collection<Item> result = catalogClient.findAll();
		assertEquals(1, result.stream()
				.filter(i -> (i.getName().equals("iPod") && i.getPrice() == 42.0 && i.getItemId() == 1)).count());
	}

	@Test
	public void testGetOne() {
		Collection<Item> allItems = catalogClient.findAll();
		Long id = allItems.iterator().next().getItemId();
		Item result = catalogClient.getOne(id);
		assertEquals(id.longValue(), result.getItemId());
	}

}
