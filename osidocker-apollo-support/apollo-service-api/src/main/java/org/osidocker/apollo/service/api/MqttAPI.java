package org.osidocker.apollo.service.api;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public interface MqttAPI {
	
	void publish(MqttMessage message) throws MqttPersistenceException, MqttException;
	
	void publish()
}
