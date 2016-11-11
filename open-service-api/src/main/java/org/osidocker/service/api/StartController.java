package org.osidocker.service.api;

import org.osidocker.mongo.service.api.MongoAPI;
import org.osidocker.redis.service.api.RedisAPI;
import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.api.ZbusAPI;
import org.osidocker.zbus.service.entity.TransformData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.utils.StringUtils;

@Controller
public class StartController {

	@Autowired
	protected RedisAPI redisApi;
	
	@Autowired
	protected ZbusAPI zbusApi;
	
	@Autowired
	protected MongoAPI mongoApi;
	
	@RequestMapping("/zbusApi")
	public @ResponseBody String dozbusApiController(@RequestParam(name="mqName",required=true)String mqName){
		TransformData tfd = new TransformData();
		tfd.setHead("caoyangjie");
		tfd.setBody("eat food!");
		if(EnumZbusMqName.helloWorld.name().equals(mqName)){
			zbusApi.doSendMQHandler(EnumZbusMqName.helloWorld, tfd);
		}else if(EnumZbusMqName.executeWord.name().equals(mqName)){
			zbusApi.doSendMQHandler(EnumZbusMqName.executeWord, tfd);
		}
		return "1";
	}
	
	@RequestMapping("/redisApi")
	public @ResponseBody String doredisApiController(String key,String value,int expire){
		if(StringUtils.isBlank(value)){
			if(redisApi.exists(key)){
				return redisApi.getString(key);
			}else {
				return "the key ["+key+"] has't redis value!";
			}
		}else if(expire>0){
			redisApi.setObjectWithexpire(key, value, expire);
		}else{
			redisApi.setObject(key.getBytes(), value.getBytes());
		}
		return "1";
	}
			
	@RequestMapping("/mongoApi")
	public @ResponseBody String domongoApiController(String key,String value,int expire){
		mongoApi.insert();
		return "1";
	}
	
}
