package com.osidocker.open.micro.service.impl;

import javax.annotation.Resource;

import com.osidocker.open.micro.model.ProductInfo;
import com.osidocker.open.micro.model.ShopInfo;
import com.osidocker.open.micro.service.ICacheService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisCluster;

import com.alibaba.fastjson.JSONObject;
/**
 * 缓存Service实现类
 * @author Administrator
 *
 */
@Service("cacheService")
public class CacheServiceImpl implements ICacheService {
	
	public static final String CACHE_NAME = "local";
	
	@Resource
	private JedisCluster jedisCluster;
	
	/** 
	 * 将商品信息保存到本地缓存中
	 * @param productInfo
	 * @return
	 */
	@Override
	@CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
	public ProductInfo saveLocalCache(ProductInfo productInfo) {
		return productInfo;
	}
	
	/**
	 * 从本地缓存中获取商品信息
	 * @param id 
	 * @return
	 */
	@Override
	@Cacheable(value = CACHE_NAME, key = "'key_'+#id")
	public ProductInfo getLocalCache(Long id) {
		return null;
	}
	
	/**
	 * 将商品信息保存到本地的ehcache缓存中
	 * @param productInfo
	 */
	@Override
	@CachePut(value = CACHE_NAME, key = "'product_info_'+#productInfo.getId()")
	public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
		return productInfo;
	}
	
	/**
	 * 从本地ehcache缓存中获取商品信息
	 * @param productId
	 * @return
	 */
	@Override
	@Cacheable(value = CACHE_NAME, key = "'product_info_'+#productId")
	public ProductInfo getProductInfoFromLocalCache(Long productId) {
		return null;
	}
	
	/**
	 * 将店铺信息保存到本地的ehcache缓存中
	 * @param shopInfo
	 */
	@Override
	@CachePut(value = CACHE_NAME, key = "'shop_info_'+#shopInfo.getId()")
	public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
		return shopInfo;
	}
	
	/**
	 * 从本地ehcache缓存中获取店铺信息
	 * @param shopId
	 * @return
	 */
	@Override
	@Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
	public ShopInfo getShopInfoFromLocalCache(Long shopId) {
		return null;
	}
	
	/**
	 * 将商品信息保存到redis中
	 * @param productInfo 
	 */
	@Override
	public void saveProductInfo2ReidsCache(ProductInfo productInfo) {
		String key = "product_info_" + productInfo.getId();
		jedisCluster.set(key, JSONObject.toJSONString(productInfo));  
	}
	
	/**
	 * 将店铺信息保存到redis中
	 * @param shopInfo
	 */
	@Override
	public void saveShopInfo2ReidsCache(ShopInfo shopInfo) {
		String key = "shop_info_" + shopInfo.getId();
		jedisCluster.set(key, JSONObject.toJSONString(shopInfo));  
	}
	
	/**
	 * 从redis中获取商品信息
	 * @param productId
	 */
	@Override
	public ProductInfo getProductInfoFromReidsCache(Long productId) {
		String key = "product_info_" + productId;
		String json = jedisCluster.get(key);
		if(json != null) {
			return JSONObject.parseObject(json, ProductInfo.class);
		}
		return null;
	}
	
	/**
	 * 从redis中获取店铺信息
	 * @param shopId
	 */
	@Override
	public ShopInfo getShopInfoFromRedisCache(Long shopId) {
		String key = "shop_info_" + shopId;
		String json = jedisCluster.get(key);
		if(json != null) {
			return JSONObject.parseObject(json, ShopInfo.class);
		}
		return null;
	}
	
}
