package org.osidocker.zbus.service.caller;

import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.api.ZbusAPI;
import org.osidocker.zbus.service.entity.TransformData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StartController {

	@Autowired
	protected ZbusAPI api;
	
	@RequestMapping("/api")
	public @ResponseBody String doController(@RequestParam(name="mqName",required=true)String mqName){
		TransformData tfd = new TransformData();
		tfd.setHead("caoyangjie");
		tfd.setBody("eat food!");
		if(EnumZbusMqName.helloWorld.name().equals(mqName)){
			api.doSendMQHandler(EnumZbusMqName.helloWorld, tfd);
		}else if(EnumZbusMqName.executeWord.name().equals(mqName)){
			api.doSendMQHandler(EnumZbusMqName.executeWord, tfd);
		}
		return "1";
	}
	
}
