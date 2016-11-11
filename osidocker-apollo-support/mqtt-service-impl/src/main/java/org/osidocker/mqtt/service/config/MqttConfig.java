package org.osidocker.mqtt.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:/mqtt.properties")
public class MqttConfig {
	@Value("${service.mqtt.host}")
	private String host;
	@Value("${service.mqtt.topic}")
	private String topic;
	@Value("${service.mqtt.clientId}")
	private String clientId;
	@Value("${service.mqtt.userName}")
	private String userName;
	@Value("${service.mqtt.password}")
	private String password;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
