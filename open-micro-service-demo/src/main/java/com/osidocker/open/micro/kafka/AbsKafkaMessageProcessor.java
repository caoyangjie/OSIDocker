package com.osidocker.open.micro.kafka;

import com.osidocker.open.micro.message.AbsMessageProcessor;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka消息处理线程
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class AbsKafkaMessageProcessor extends AbsMessageProcessor implements Runnable {

    protected Logger logger = LoggerFactory.getLogger(AbsKafkaMessageProcessor.class);

	private KafkaStream kafkaStream;

	public AbsKafkaMessageProcessor(KafkaStream kafkaStream) {
		this.kafkaStream = kafkaStream;
	}
	
    @Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
        while (it.hasNext()) {
        	String message = new String(it.next().message());
        	//处理消息对象
        	process(message);
        }
	}
}
