package org.osidocker.open.service.activemq;

import org.osidocker.open.entity.ActiveMsgBaseEntity;
import org.osidocker.open.entity.QueueName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

@Service
public class ActiveMQByteConsumer {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());  
	  
    @JmsListener(destination = QueueName.BYTE_QUEUE)  
    public void receivedQueue(byte[] msg) {
    	ActiveMsgBaseEntity ambe = (ActiveMsgBaseEntity) SerializationUtils.deserialize(msg);
        LOGGER.info("Has received from " + QueueName.BYTE_QUEUE + ", msg: " + ambe);  
    }
}
