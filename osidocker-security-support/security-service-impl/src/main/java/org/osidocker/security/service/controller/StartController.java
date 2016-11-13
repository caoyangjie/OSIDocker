package org.osidocker.security.service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StartController {

	@RequestMapping("/login")
	public String dologinController(String value){
		return "login";
	}
	
	@RequestMapping("/hello")
	public String doHelloController(String value){
		return "hello";
	}
	
	@RequestMapping("/")
	public String doController(String value){
		return "index";
	}
	
}
