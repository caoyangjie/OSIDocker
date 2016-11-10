package org.osidocker.redis.service.impl;

import java.util.List;
import java.util.Map;

import org.osidocker.redis.service.api.RedisAPI;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class RedisAPIDubboService implements RedisAPI {
	
	@Autowired
	protected RedisAPI api;

	@Override
	public void setObject(byte[] key, byte[] objVal) {
		api.setObject(key, objVal);
	}

	@Override
	public void setObjectWithexpire(byte[] key, byte[] objVal, int time) {
		api.setObjectWithexpire(key, objVal, time);
	}

	@Override
	public void setObjectWithexpire(String key, String objVal, int time) {
		api.setObjectWithexpire(key, objVal, time);
	}

	@Override
	public void delString(String key) {
		api.delString(key);
	}

	@Override
	public String getString(String key) {
		return api.getString(key);
	}

	@Override
	public byte[] getObject(byte[] key) {
		return api.getObject(key);
	}

	@Override
	public void setObjectToHash(byte[] key, byte[] field, byte[] value) {
		api.setObjectToHash(key, field, value);
	}

	@Override
	public void setMutiObjectToHash(byte[] key, Map<byte[], byte[]> values) {
		api.setMutiObjectToHash(key, values);
	}

	@Override
	public byte[] getObjetFromHash(byte[] key, byte[] field) {
		return api.getObjetFromHash(key, field);
	}

	@Override
	public List<byte[]> getObjectsFromHash(byte[] key, List<byte[]> feilds) {
		return api.getObjectsFromHash(key, feilds);
	}

	@Override
	public List<byte[]> getObjectsFromHash(byte[] key, byte[]... fields) {
		return api.getObjectsFromHash(key, fields);
	}

	@Override
	public Map<byte[], byte[]> getAllObjectFromHash(byte[] key) {
		return api.getAllObjectFromHash(key);
	}

	@Override
	public void delObjectFromHash(byte[] key, byte[] field) {
		api.delObjectFromHash(key, field);
	}

	@Override
	public void delObject(byte[] key) {
		api.delObject(key);
	}

	@Override
	public List<String> getALLmgetStr(String[] keys) {
		return api.getALLmgetStr(keys);
	}

	@Override
	public List<String> getStrMuti(String[] fields, String key) {
		return api.getStrMuti(fields, key);
	}

	@Override
	public boolean exists(String key) {
		return api.exists(key);
	}

	@Override
	public void setHashkv(String key, String fields, String value) {
		api.setHashkv(key, fields, value);
	}

}
