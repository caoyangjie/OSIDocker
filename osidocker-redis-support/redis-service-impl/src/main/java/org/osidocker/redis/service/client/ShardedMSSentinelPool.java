package org.osidocker.redis.service.client;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osidocker.redis.service.client.masterslave.ShardedMasterSlaveJedis;
import org.osidocker.redis.service.client.masterslave.ShardedMasterSlaveJedisSentinelPool;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

public class ShardedMSSentinelPool implements InitializingBean {

	private ClusterJedisPoolConfig clusterJedisPoolConfig;

	private List<String> masterNameList;
	
	private List<String> sentinelList;
	
	private Pool<ShardedMasterSlaveJedis> jedisPool;
	
	
	
	private Set<String> covertListToSet(List<String> list)
	{
		Set<String> masterNames = new LinkedHashSet<String>();
        for(String str : list)
        {
        	masterNames.add(str);
        }
		return masterNames;
	}
	
	
	public Pool<ShardedMasterSlaveJedis> createJedisPool(JedisPoolConfig jedisPoolConfig) {
		Set<String> masterNames = covertListToSet(masterNameList);
		Set<String> sentinels = covertListToSet(sentinelList);
		return new ShardedMasterSlaveJedisSentinelPool(masterNames, sentinels, jedisPoolConfig);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		jedisPool = createJedisPool(clusterJedisPoolConfig.getJedisPoolConfig());
	}
	
	

	public ClusterJedisPoolConfig getClusterJedisPoolConfig() {
		return clusterJedisPoolConfig;
	}


	public void setClusterJedisPoolConfig(
			ClusterJedisPoolConfig clusterJedisPoolConfig) {
		this.clusterJedisPoolConfig = clusterJedisPoolConfig;
	}


	public List<String> getMasterNameList() {
		return masterNameList;
	}


	public void setMasterNameList(List<String> masterNameList) {
		this.masterNameList = masterNameList;
	}


	public List<String> getSentinelList() {
		return sentinelList;
	}


	public void setSentinelList(List<String> sentinelList) {
		this.sentinelList = sentinelList;
	}


	public Pool<ShardedMasterSlaveJedis> getJedisPool() {
		return jedisPool;
	}


	public void setJedisPool(Pool<ShardedMasterSlaveJedis> jedisPool) {
		this.jedisPool = jedisPool;
	}


	public ShardedMasterSlaveJedis getResource()
	{
		return jedisPool.getResource();
	}
	
	public void returnResource(ShardedMasterSlaveJedis resource)
	{
		 jedisPool.returnResource(resource);
	}
	
	public void destroy()
	{
		jedisPool.destroy();
	}
	
}
