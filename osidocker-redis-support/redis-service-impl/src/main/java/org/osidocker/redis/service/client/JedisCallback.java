package org.osidocker.redis.service.client;

public interface JedisCallback<I,O> {

	public O doInJedis(I jedis);
	
}
