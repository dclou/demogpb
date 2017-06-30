package org.dclou.example.demogpb.catalog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@EnableWebSecurity
@ActiveProfiles("test")
public class RepositoryTest {

	@Autowired
	private ItemRepository itemRepository;

	@Test
	public void AreAllIPodReturned() {

		assertThat(itemRepository.findByNameContaining("iPod"), hasSize(3));
		assertTrue(itemRepository.findByNameContaining("iPod").stream().anyMatch(s -> s.getName().equals("iPod touch")));

	}
}
