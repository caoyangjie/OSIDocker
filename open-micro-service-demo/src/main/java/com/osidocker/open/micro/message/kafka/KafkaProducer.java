/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message.kafka;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.message.AbsMessageProducer;
import com.osidocker.open.micro.message.IProducer;
import com.osidocker.open.micro.vo.BaseMessage;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于9:53 2018/3/15
 * @修改说明：
 * @修改日期： 修改于9:53 2018/3/15
 * @版本号： V1.0.0
 */
@Service(value = KafkaProducer.KAFKA_PRODUCER)
public class KafkaProducer extends AbsMessageProducer implements IProducer{
    public static final String KAFKA_PRODUCER = "kafkaProducer";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void send(String topic, BaseMessage msg, ListenableFutureCallback callbackHandler) {
        ListenableFuture future = kafkaTemplate.send(topic, JSONObject.toJSONString(msg));
        future.addCallback(callbackHandler);
    }

    @Override
    public void send(String topic, BaseMessage message) {
        ListenableFuture<BaseMessage> future = kafkaTemplate.send(topic,JSONObject.toJSONString(message));
        future.addCallback( msg -> success(msg), ex -> fail(ex));
    }
}
