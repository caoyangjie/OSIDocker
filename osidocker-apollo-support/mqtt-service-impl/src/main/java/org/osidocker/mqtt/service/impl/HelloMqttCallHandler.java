package org.osidocker.mqtt.service.impl;

import org.springframework.stereotype.Service;

@Service
public class HelloMqttCallHandler extends MqttBaseCallHandler {

	@Override
	public String[] getTopics() {
		return "caoyangjie".split(",");
	}

}
