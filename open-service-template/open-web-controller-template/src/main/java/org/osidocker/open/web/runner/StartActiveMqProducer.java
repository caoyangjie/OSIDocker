package org.osidocker.open.web.runner;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Order(101)
public class StartActiveMqProducer implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartActiveMqProducer.class);  
	  
    @Autowired  
    private JmsMessagingTemplate jmsMessagingTemplate;  
  
    @Autowired  
    private Queue logQueue;  
  
    @Override  
    public void run(String... strings) throws Exception {  
        send("This is a log message.");  
        LOGGER.info("Log Message was sent to the Queue named sample.log");  
    }  
  
    public void send(String msg) {
        this.jmsMessagingTemplate.convertAndSend(this.logQueue, msg);  
    }  
}
