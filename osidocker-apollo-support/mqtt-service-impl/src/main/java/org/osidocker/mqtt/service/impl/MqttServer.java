package org.osidocker.mqtt.service.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.osidocker.mqtt.service.config.MqttConfig;
import org.osidocker.mqtt.service.entity.PushCallBack;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

@Service
public class MqttServer implements InitializingBean{
	private ScheduledExecutorService scheduler;
	private MqttClient client;  
    private MqttTopic topic;  
    private MqttMessage message;
    MqttConnectOptions options;  
    
    @Autowired
    protected MqttConfig config;
    
    private void connect() throws MqttException {  
    	client = new MqttClient(config.getHost(), config.getClientId(), new MemoryPersistence());  
    	options = new MqttConnectOptions();
        options.setCleanSession(false);  
        options.setUserName(config.getUserName());  
        options.setPassword(config.getPassword().toCharArray());  
        // 设置超时时间  
        options.setConnectionTimeout(10);  
        // 设置会话心跳时间  
        options.setKeepAliveInterval(20);  
        try {  
           client.setCallback(new PushCallBack());  
           client.connect(options);  
           topic = client.getTopic("caoyangjie");  
        } catch (Exception e) {  
               e.printStackTrace();  
        }
    }  
      
    public void publish(MqttMessage message) throws MqttPersistenceException, MqttException{  
    	MqttDeliveryToken token = topic.publish(message);  
    	token.waitForCompletion();  
    }  
    
    public void publish(byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException{
    	MqttDeliveryToken token = topic.publish(payload, qos, retained);
    	token.waitForCompletion();  
    }
    
    public <T> void publish(T t,int qos, boolean retained) throws MqttException, MqttPersistenceException{
    	MqttDeliveryToken token = topic.publish(SerializationUtils.serialize(t), qos, retained);
    	token.waitForCompletion();  
    }

	@Override
	public void afterPropertiesSet() throws Exception {
        connect();  
	}
	
	public void disconnect() {
		 try {
			client.disconnect();
			scheduler.shutdown();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void startReconnect() {
		connect();
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

}
