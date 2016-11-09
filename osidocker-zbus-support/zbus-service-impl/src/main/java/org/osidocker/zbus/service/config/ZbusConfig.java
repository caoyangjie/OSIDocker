package org.osidocker.zbus.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:/zbus.properties")
public class ZbusConfig {
	@Value("${sys.mq.zbus.ip}")
	private String zbusIp;
	@Value("${sys.mq.zbus.port}")
	private int zbusPort;
	
	public String getZbusIp() {
		return zbusIp;
	}
	public void setZbusIp(String zbusIp) {
		this.zbusIp = zbusIp;
	}
	public int getZbusPort() {
		return zbusPort;
	}
	public void setZbusPort(int zbusPort) {
		this.zbusPort = zbusPort;
	}
}
