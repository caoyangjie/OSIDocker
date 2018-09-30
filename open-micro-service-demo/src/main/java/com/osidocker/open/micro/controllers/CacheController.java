package com.osidocker.open.micro.controllers;

import javax.annotation.Resource;

import com.netflix.hystrix.HystrixCommand;
import com.osidocker.open.micro.config.HystrixCommandConfig;
import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.hystrix.AbsHystrixCommand;
import com.osidocker.open.micro.hystrix.showuser.ShowUserHystrixCommand;
import com.osidocker.open.micro.model.ProductInfo;
import com.osidocker.open.micro.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private HystrixCommandConfig config;
	
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

	/**
	 * nginx开始，各级缓存都失效了，nginx发送很多的请求直接到缓存服务要求拉取最原始的数据
	 * @param productId
	 * @return
	 */
	@RequestMapping("/getProductInfo")
	@ResponseBody
	public ShowUserEntity getProductInfo(Long productId) {
		// 拿到一个商品id
		// 调用商品服务的接口，获取商品id对应的商品的最新数据
		// 用HttpClient去调用商品服务的http接口
		AbsHystrixCommand<ShowUserEntity,ShowUserEntity> getProductInfoCommand = new ShowUserHystrixCommand(new ShowUserEntity(),config);
		ShowUserEntity user = getProductInfoCommand.execute();
		System.out.println(user);
		return user;
	}
}
