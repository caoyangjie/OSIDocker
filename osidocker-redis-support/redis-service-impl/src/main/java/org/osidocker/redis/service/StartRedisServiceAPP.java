package org.osidocker.redis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.osidocker.redis.service.client.ClusterJedisPoolConfig;
import org.osidocker.redis.service.client.ShardedMSSentinelPool;
import org.osidocker.redis.service.config.RedisConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StartRedisServiceAPP {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new SpringApplicationBuilder(StartRedisServiceAPP.class).web(false).run(args);
		CountDownLatch closeLatch = new CountDownLatch(1);
		closeLatch.await();
	}

	@Bean
	@ConfigurationProperties(prefix="spring.redis")
	public ClusterJedisPoolConfig getClusterJedisPoolConfig(){
		return new ClusterJedisPoolConfig();
	}
	
	@Bean
	public static ShardedMSSentinelPool getShardedMSSentinelPool(ClusterJedisPoolConfig poolConfig,RedisConfig config){
		ShardedMSSentinelPool pool = new ShardedMSSentinelPool();
		pool.setClusterJedisPoolConfig(poolConfig);
		List<String> nameList = new ArrayList<String>();
		nameList.add(config.getMaster1());
		nameList.add(config.getMaster2());
		pool.setMasterNameList(nameList);
		List<String> urlList = new ArrayList<String>();
		urlList.add(config.getUrl1());
		urlList.add(config.getUrl2());
		pool.setSentinelList(urlList);
		return pool;
	}
}
