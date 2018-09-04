/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.message.AbsMessageProducer;
import com.osidocker.open.micro.message.IProducer;
import com.osidocker.open.micro.vo.BaseMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于11:18 2018/3/15
 * @修改说明：
 * @修改日期： 修改于11:18 2018/3/15
 * @版本号： V1.0.0
 */
//@Service(value = RabbitMqProducer.RABBIT_MQ_PRODUCER)
public class RabbitMqProducer extends AbsMessageProducer implements IProducer{

    public static final String RABBIT_MQ_PRODUCER = "rabbitMqProducer";

    @Autowired
    private AmqpTemplate rabbitTemplate;
    /**
     * 发送RabbitMq消息
     * @param topic 主题
     * @param msg   消息内容对象
     * @param callbackHandler 处理回调函数
     */
    @Override
    public void send(String topic, BaseMessage msg, ListenableFutureCallback callbackHandler) {
        try{
            rabbitTemplate.convertAndSend(topic, JSONObject.toJSONString(msg));
            callbackHandler.onSuccess(msg);
        }catch(Exception e){
            logger.error("发送消息外抛异常:",e);
            callbackHandler.onFailure(e);
        }
    }

    /**
     * 发送RabbitMq消息
     * @param topic 主题
     * @param msg   消息内容对象
     */
    @Override
    public void send(String topic, BaseMessage msg) {
        try{
            rabbitTemplate.convertAndSend(topic, JSONObject.toJSONString(msg));
            success(msg);
        }catch(Exception e){
            logger.error("发送消息外抛异常:",e);
            fail(e);
        }
    }
}
