package org.osidocker.mqtt.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.osidocker.mqtt.service.config.MqttConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class MqttBaseCallHandler implements InitializingBean{
	protected Logger logger = Logger.getLogger(this.getClass());
	private ScheduledExecutorService scheduler;
	private MqttClient client;
	private MqttConnectOptions options;
	@Autowired
	protected MqttConfig config;
	
	protected static Map<String,Map<String,Object>> cacheMap = new ConcurrentHashMap<String,Map<String,Object>>();

	//重新链接
	public void startReconnect() {
		start();
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if (!client.isConnected()) {
					try {
						client.connect(options);
					} catch (MqttSecurityException e) {
						e.printStackTrace();
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
	}

	private void start() {
		try {
			logger.info("start...");
			// host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
			client = new MqttClient(config.getHost(), config.getClientId()+"_"+this.getClass(), new MemoryPersistence());
			// MQTT的连接设置
			options = new MqttConnectOptions();
			// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
			options.setCleanSession(false);
			// 设置连接的用户名
			options.setUserName(config.getUserName());
			// 设置连接的密码
			options.setPassword(config.getPassword().toCharArray());
			// 设置超时时间 单位为秒
			options.setConnectionTimeout(10);
			// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
			options.setKeepAliveInterval(20);
			// 设置回调
			client.setCallback(getMqttCallBackHandler());
			//获取监测主题
			String[] topic1 = getTopics();
			for (String t : topic1) {
				options.setWill(t, "close".getBytes(), 0, true);
			}
			client.connect(options);
			//订阅消息
			int Qos[] = new int[topic1.length];
			for (int i = 0; i < topic1.length; i++) {
				Qos[i] = 0;
				logger.info(topic1[i].toString());
			}
			client.subscribe(topic1, Qos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		 try {
			client.disconnect();
			scheduler.shutdown();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public abstract String[] getTopics();
	
	public abstract MqttCallback getMqttCallBackHandler();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		startReconnect();
	}
}
