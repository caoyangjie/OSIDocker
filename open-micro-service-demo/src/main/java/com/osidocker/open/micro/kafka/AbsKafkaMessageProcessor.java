package com.osidocker.open.micro.kafka;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.spring.SpringContext;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * kafka消息处理线程
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class AbsKafkaMessageProcessor implements Runnable {

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
        	
        	// 首先将message转换成json对象
        	JSONObject messageJSONObject = JSONObject.parseObject(message);
        	
        	// 从这里提取出消息对应的服务的标识
        	String serviceId = messageJSONObject.getString("serviceId");  
        	
        	// 如果是商品信息服务
            Optional<AbsMessageHandler> optionalHandler = Optional.ofNullable((AbsMessageHandler)SpringContext.getApplicationContext().getBean(serviceId));
            if( optionalHandler.isPresent() ){
                AbsMessageHandler handler = optionalHandler.get();
                handler.execute(JSONObject.parseObject(message,handler.messageClass()));
            }else{
                logger.error("未能找到服务ID为：【"+serviceId+"】的服务!处理消息内容为：【"+messageJSONObject.toJSONString()+"】");
            }
        }
	}
}
