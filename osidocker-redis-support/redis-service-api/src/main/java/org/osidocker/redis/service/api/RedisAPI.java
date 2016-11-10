package org.osidocker.redis.service.api;

import java.util.List;
import java.util.Map;

public interface RedisAPI {
	
	public void setObject(byte[] key,byte[] objVal);
	
	public void setObjectWithexpire(byte[] key,byte[] objVal,int time);
	
	public void setObjectWithexpire(String key,String objVal,int time);
	
	public void delString(String key);
	
	public String getString(String key);
	
	public byte[] getObject(byte[] key);
	
	public void setObjectToHash(byte[] key, byte[] field, byte[] value);
	
	public void setMutiObjectToHash(byte[] key, Map<byte[], byte[]> values);
	
	public byte[] getObjetFromHash(byte[] key, byte[] field);
	
	public List<byte[]> getObjectsFromHash(byte[] key, List<byte[]> feilds);
	
	public List<byte[]> getObjectsFromHash(byte[] key, byte[]... fields);
	
	public Map<byte[], byte[]> getAllObjectFromHash(byte[] key);
	
	public void delObjectFromHash(byte[] key, byte[] field);
	
	public void delObject(byte[] key);
	
	public List<String> getALLmgetStr(String[] keys);
	
	public List<String> getStrMuti(String[] fields, String key);
	
	public boolean exists(String key);
	
	public void setHashkv(String key, String fields, String value);
}
