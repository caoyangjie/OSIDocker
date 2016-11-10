package org.osidocker.redis.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:/redis.properties")
public class RedisConfig {
	@Value("${spring.redis.master1}")
	private String master1;
	@Value("${spring.redis.master2}")
	private String master2;
	@Value("${spring.redis.url1}")
	private String url1;
	@Value("${spring.redis.url2}")
	private String url2;
	@Value("${spring.redis.maxTotal}")
	private int maxTotal;
	@Value("${spring.redis.maxIdle}")
	private int maxIdle;
	@Value("${spring.redis.minIdle}")
	private int minIdle;
	@Value("${spring.redis.maxWaitMillis}")
	private int maxWaitMillis;
	public String getMaster1() {
		return master1;
	}
	public void setMaster1(String master1) {
		this.master1 = master1;
	}
	public String getMaster2() {
		return master2;
	}
	public void setMaster2(String master2) {
		this.master2 = master2;
	}
	public String getUrl1() {
		return url1;
	}
	public void setUrl1(String url1) {
		this.url1 = url1;
	}
	public String getUrl2() {
		return url2;
	}
	public void setUrl2(String url2) {
		this.url2 = url2;
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
