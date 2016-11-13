package org.osidocker.mqtt.service.impl;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class HelloMqttCallHandler extends MqttBaseCallHandler {

	@Override
	public String[] getTopics() {
		return "caoyangjie".split(",");
	}

	@Override
	public MqttCallback getMqttCallBackHandler() {
		return new MqttCallback() {
			
			public void connectionLost(Throwable cause) {
				// 连接丢失后，一般在这里面进行重连
				logger.info("连接断开，可以做重连");
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				// TODO Auto-generated method stub
				logger.info("deliveryComplete---------"+ token.isComplete());
				
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				// TODO Auto-generated method stub
				String msg = new String(message.getPayload(),"UTF-8");
				logger.info("接收消息主题:"+topic);
				logger.info("接收消息Qos:"+message.getQos());
				logger.info("接收消息内容:"+msg);
			}
		};
	}

}
