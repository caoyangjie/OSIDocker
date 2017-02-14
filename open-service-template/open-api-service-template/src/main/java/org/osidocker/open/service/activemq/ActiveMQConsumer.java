package org.osidocker.open.service.activemq;

import org.osidocker.open.entity.QueueName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ActiveMQConsumer {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());  
	  
    @JmsListener(destination = QueueName.LOG_QUEUE)  
    public void receivedQueue(String msg) {  
        LOGGER.info("Has received from " + QueueName.LOG_QUEUE + ", msg: " + msg);  
    }
}
