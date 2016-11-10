package org.osidocker.redis.service.client;

import org.osidocker.redis.service.client.masterslave.ShardedMasterSlaveJedis;
import org.springframework.beans.factory.FactoryBean;


public class ShardedMasterSlaveJedisFactory  implements FactoryBean<ShardedMasterSlaveJedis> {
	
	private ShardedMSSentinelPool shardedMSSentinelPool;
	
	@Override
	public ShardedMasterSlaveJedis getObject() throws Exception {
		 return shardedMSSentinelPool.getResource();
	}

	@Override
	public Class<?> getObjectType() {
		  return  ShardedMasterSlaveJedis.class;  
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setShardedMSSentinelPool(ShardedMSSentinelPool shardedMSSentinelPool) {
		this.shardedMSSentinelPool = shardedMSSentinelPool;
	}

	public ShardedMSSentinelPool getShardedMSSentinelPool() {
		return shardedMSSentinelPool;
	}

}
