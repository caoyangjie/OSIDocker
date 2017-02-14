package org.osidocker.open.web.config;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.osidocker.open.entity.QueueName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMqConfig {
	@Bean  
    public Queue logQueue() {  
        return new ActiveMQQueue(QueueName.LOG_QUEUE);
    }
}
