package org.dclou.example.demogpb.order.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
class HomeController {

	@Autowired
	private HomeController() {
	}

	@RequestMapping(value = "/login.html", method = RequestMethod.GET)
	public String loginIndex() {
		return "login";
	}
}
