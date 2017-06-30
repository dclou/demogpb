package org.dclou.example.demogpb.catalog.web;

import org.apache.commons.collections.IteratorUtils;
import org.dclou.example.demogpb.catalog.Item;
import org.dclou.example.demogpb.catalog.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CatalogController {

	private final ItemRepository itemRepository;

	@Autowired
	public CatalogController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@RequestMapping(value = "/api/catalog", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<Item> catalog() {
        List<Item> list = IteratorUtils.toList(itemRepository.findAll().iterator());
		return list;
	}
}
