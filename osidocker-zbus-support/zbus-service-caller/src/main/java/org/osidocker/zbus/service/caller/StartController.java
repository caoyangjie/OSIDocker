package org.osidocker.zbus.service.caller;

import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.api.ZbusAPI;
import org.osidocker.zbus.service.entity.TransformData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StartController {

	@Autowired
	protected ZbusAPI api;
	
	@RequestMapping("/api")
	public @ResponseBody String doController(){
		TransformData tfd = new TransformData();
		tfd.setHead("caoyangjie");
		tfd.setBody("eat food!");
		api.doSendMQHandler(EnumZbusMqName.helloWorld, tfd);
		return "1";
	}
	
}
