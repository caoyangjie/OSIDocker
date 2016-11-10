package org.osidocker.redis.service.client;

import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.JedisPoolConfig;

public class ClusterJedisPoolConfig implements InitializingBean {
	
	private int maxTotal;
	
	private int maxIdle;
	
	private int minIdle;
	
	private int maxWaitMillis;

	private JedisPoolConfig jedisPoolConfig ;
	
	private boolean testOnBorrow = true;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		 jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		if(testOnBorrow == false)
		{
			jedisPoolConfig.setTestOnBorrow(false);
		}
		else
		{
			jedisPoolConfig.setTestOnBorrow(true);
		}
	}





	public JedisPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}


	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}





	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}





	public int getMaxTotal() {
		return maxTotal;
	}





	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}





	public int getMaxIdle() {
		return maxIdle;
	}





	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}





	public int getMinIdle() {
		return minIdle;
	}





	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}





	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}





	public void setMaxWaitMillis(int maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}





	
	
}
