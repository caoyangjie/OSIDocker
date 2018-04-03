/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message;

import com.osidocker.open.micro.spring.SpringContext;
import com.osidocker.open.micro.vo.BaseMessage;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于9:42 2018/3/15
 * @修改说明：
 * @修改日期： 修改于9:42 2018/3/15
 * @版本号： V1.0.0
 */
public class PublishMessageFactory {

    /**
     * 请求发送消息
     * @param topic             消息主题
     * @param msg               信息内容
     * @param messageType       消息类型
     * @param callbackHandler   成功失败的处理器
     */
    public static <Message extends BaseMessage> void send(String topic, Message msg, MessageEnums messageType, ListenableFutureCallback<Message> callbackHandler){
        IProducer producer = getProducerInstance(messageType);
        producer.send(topic,msg,callbackHandler);
    }

    /**
     *  请求发送消息
     * @param topic             消息主题
     * @param msg               信息内容
     * @param messageType       消息类型
     */
    public static <Message extends BaseMessage> void send(String topic, Message msg, MessageEnums messageType){
        IProducer producer = getProducerInstance(messageType);
        producer.send(topic,msg);
    }

    /**
     * 获取消息生产者实体对象
     * @param messageType   消息类型
     * @return
     */
    private static IProducer<BaseMessage> getProducerInstance(MessageEnums messageType){
        Optional<IProducer> iProducerOptional = Optional.ofNullable((IProducer) SpringContext.getApplicationContext().getBean(messageType.getServiceName()));
        if( !iProducerOptional.isPresent() )
        {
            throw new RuntimeException("无法获取到消息对应的处理服务实例![]"+messageType.getServiceName());
        }
        return iProducerOptional.get();
    }
}
