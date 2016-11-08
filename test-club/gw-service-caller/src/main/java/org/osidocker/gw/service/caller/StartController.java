package org.osidocker.gw.service.caller;

import org.osidocker.gw.service.api.HelloService;
import org.osidocker.gw.service.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;

@Controller
public class StartController {
	
	@Reference
	protected HelloService hsAPI;
	
	@RequestMapping("/api")
	public void doController(){
		User u = hsAPI.getUserByName("曹杨杰");
		System.out.println(u.getName());
	}
	
}
