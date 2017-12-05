package com.osidocker.open.micro.controllers;

import javax.annotation.Resource;

import com.osidocker.open.micro.model.ProductInfo;
import com.osidocker.open.micro.service.ICacheService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 缓存Controller
 * @author Administrator
 *
 */
@Controller
public class CacheController {

	@Resource
	private ICacheService cacheService;
	
	@RequestMapping("/testPutCache")
	@ResponseBody
	public String testPutCache(ProductInfo productInfo) {
		cacheService.saveLocalCache(productInfo);
		return "success";
	}
	
	@RequestMapping("/testGetCache")
	@ResponseBody
	public ProductInfo testGetCache(Long id) {
		return cacheService.getLocalCache(id);
	}
	
}
