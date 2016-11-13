package org.osidocker.apollo.service.api;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public interface MqttAPI {
	
	public void publish(MqttMessage message) throws MqttPersistenceException, MqttException;
	
	public void publish(byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException;
	
	public <T> void publish(T t,int qos, boolean retained) throws MqttException, MqttPersistenceException;
	
	public <T> void publish(T t) throws MqttException, MqttPersistenceException;
}
