package org.osidocker.zbus.service.impl;

import java.io.IOException;
import java.io.Serializable;

import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.api.ZbusAPI;
import org.osidocker.zbus.service.config.ZbusConfig;
import org.osidocker.zbus.service.handler.ZbusAbsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.Consumer;
import org.zbus.mq.MqConfig;
import org.zbus.mq.Producer;
import org.zbus.net.http.Message;

@Service
public class ZbusAPIDefaultService extends ZbusAPIBaseService {
	@Autowired
	protected ZbusConfig config;
	
	@Autowired
	protected Broker broker;

	@SuppressWarnings("resource")
	@Override
	public void registerHandler(ZbusAbsHandler handler) {
		try {
			MqConfig config = new MqConfig();
			config.setBroker(broker);
			config.setMq(handler.Name().name());
			Consumer c = new Consumer(config);
			c.onMessage(handler);
			c.start();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	@Bean
	public SingleBroker getSingleBroker() throws IOException{
		BrokerConfig brokerConfig = new BrokerConfig();
		brokerConfig.setServerAddress(config.getZbusIp() + ":" + config.getZbusPort() );
		return new SingleBroker(brokerConfig);
	}

	@Override
	public <T extends Serializable> void doSendMQHandler(EnumZbusMqName topic, T t) {
		try {
			Producer producer = new Producer(broker,topic.name());
			producer.createMQ();
			Message msg = new Message();
			//创建消息，消息体可以是任意binary，应用协议交给使用者
			msg.setBody(SerializationUtils.serialize(t));
			producer.sendSync(msg);
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
}
