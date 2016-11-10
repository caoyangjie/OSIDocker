package org.osidocker.redis.service.caller;

import org.osidocker.redis.service.api.RedisAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.utils.StringUtils;

@Controller
public class StartController {

	@Autowired
	protected RedisAPI api;
	
	@RequestMapping("/api")
	public @ResponseBody String doController(@RequestBody APIEntity entity){
		if(StringUtils.isBlank(entity.getValue())){
			if(api.exists(entity.getKey())){
				return api.getString(entity.getKey());
			}else {
				return "the key ["+entity.getKey()+"] has't redis value!";
			}
		}else if(entity.getExpire()>0){
			api.setObjectWithexpire(entity.getKey(), entity.getValue(), entity.getExpire());
		}else{
			api.setObject(entity.getKey().getBytes(), entity.getValue().getBytes());
		}
		return "1";
	}
	
	@RequestMapping("/redisApi")
	public @ResponseBody String doController(String key,String value,int expire){
		if(StringUtils.isBlank(value)){
			if(api.exists(key)){
				return api.getString(key);
			}else {
				return "the key ["+key+"] has't redis value!";
			}
		}else if(expire>0){
			api.setObjectWithexpire(key, value, expire);
		}else{
			api.setObject(key.getBytes(), value.getBytes());
		}
		return "1";
	}
	
}
