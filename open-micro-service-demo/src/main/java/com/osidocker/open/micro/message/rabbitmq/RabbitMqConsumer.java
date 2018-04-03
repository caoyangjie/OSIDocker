/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message.rabbitmq;

import com.osidocker.open.micro.message.AbsMessageProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于16:05 2018/3/15
 * @修改说明：
 * @修改日期： 修改于16:05 2018/3/15
 * @版本号： V1.0.0
 */
@Service
@RabbitListener(queues = {"mykafka"})
public class RabbitMqConsumer extends AbsMessageProcessor{

    @RabbitHandler
    public void handlerMessage(String message){
        process(message);
    }
}
