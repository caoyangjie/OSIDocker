package org.osidocker.open.service;

import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TestEhcacheService {
	
	@Cacheable(value="demo",key="'list_'+#params['abc']+#params['def']+#params['ghi']")
	public String list(Map<String, Object> params){
//		return "fasfa"+Jsonu;
		System.out.println("没有进入缓存获取数据");
		return "fdsafsda"+params.get("abc")+params.get("def");
	}
}
