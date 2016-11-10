package org.osidocker.redis.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osidocker.redis.service.api.RedisAPI;
import org.osidocker.redis.service.client.ShardedMSSentinelPool;
import org.osidocker.redis.service.client.masterslave.MasterSlaveJedis;
import org.osidocker.redis.service.client.masterslave.ShardedMasterSlaveJedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;

@Service
public class RedisAPIService implements RedisAPI {

	@Autowired
	protected ShardedMSSentinelPool shardedMSSentinelPool;
	
	@Override
	public void setObject(byte[] key, byte[] objVal) {
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			// TODO Auto-generated method stub
			jedis.set(key, objVal);
		}catch(Exception e){
			throw e;
		}finally{
			if(null!=jedis){
				jedis.close();
			}
		}
	}

	@Override
	public void setObjectWithexpire(byte[] key, byte[] objVal, int time) {
		ShardedMasterSlaveJedis jedis = null;
        try {
            jedis = shardedMSSentinelPool.getResource();
            jedis.set(key, objVal);
            jedis.expire(key, time);
        } catch (Exception ce) {
            throw ce;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
	}

	@Override
	public void setObjectWithexpire(String key, String objVal, int time) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			jedis.set(key, objVal);
			jedis.expire(key, time);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public void delString(String key) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			jedis.del(key);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public String getString(String key) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
		   return jedis.get(key);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] getObject(byte[] key) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			return jedis.get(key);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public void setObjectToHash(byte[] key, byte[] field, byte[] value) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			jedis.hset(key, field, value);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public void setMutiObjectToHash(byte[] key, Map<byte[], byte[]> values) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			jedis.hmset(key, values);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public byte[] getObjetFromHash(byte[] key, byte[] field) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		byte[] result = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			result = jedis.hget(key, field);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public List<byte[]> getObjectsFromHash(byte[] key, List<byte[]> feilds) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		List<byte[]> resultList = null;
		try {
			if (CollectionUtils.isNotEmpty(feilds)) {
				jedis = shardedMSSentinelPool.getResource();
				resultList = new ArrayList<byte[]>();
				for (byte[] b : feilds) {
					resultList.add(jedis.hget(key, b));
				}
			}
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return resultList;
	}

	@Override
	public List<byte[]> getObjectsFromHash(byte[] key, byte[]... fields) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		List<byte[]> resultList = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			resultList = jedis.hmget(key, fields);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return resultList;
	}

	@Override
	public Map<byte[], byte[]> getAllObjectFromHash(byte[] key) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		Map<byte[], byte[]> resultMap = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			resultMap = jedis.hgetAll(key);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return resultMap;
	}

	@Override
	public void delObjectFromHash(byte[] key, byte[] field) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			jedis = shardedMSSentinelPool.getResource();
			jedis.hdel(key, field);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public void delObject(byte[] key) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			 jedis = shardedMSSentinelPool.getResource();
		     jedis.del(key);
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public List<String> getALLmgetStr(String[] keys) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = shardedMSSentinelPool.getResource();
		Iterator<MasterSlaveJedis> iterator = jedis.getAllShards().iterator();

		List<String> result = new ArrayList<String>();
		while (iterator.hasNext()) {
			MasterSlaveJedis js = iterator.next();
			List<String> resultList = js.mget(keys);

			if (CollectionUtils.isNotEmpty(resultList)) {
				result.addAll(resultList);
			}
		}
		jedis.close();
		return result;
	}

	@Override
	public List<String> getStrMuti(String[] fields, String key) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = shardedMSSentinelPool.getResource();
		List<String> values = jedis.hmget(key, fields);
		jedis.close();
		return values;
	}

	@Override
	public boolean exists(String key) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = null;
		try {
			 jedis = shardedMSSentinelPool.getResource();
			 if(jedis.exists(key))
			 {
				 return true;
			 }
			 return false;
		} catch (Exception ce) {
			throw ce;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public void setHashkv(String key, String field, String value) {
		// TODO Auto-generated method stub
		ShardedMasterSlaveJedis jedis = shardedMSSentinelPool.getResource();
		jedis.hset(key, field, value);
		jedis.close();
	}

}
