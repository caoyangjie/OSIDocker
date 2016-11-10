package org.osidocker.redis.service.caller;

import java.io.Serializable;

public class APIEntity implements Serializable {
	private String key;
	private String value;
	private int expire;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getExpire() {
		return expire;
	}
	public void setExpire(int expire) {
		this.expire = expire;
	}
}	
