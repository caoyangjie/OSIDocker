/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message;

import com.osidocker.open.micro.message.kafka.KafkaProducer;
import com.osidocker.open.micro.message.rabbitmq.RabbitMqProducer;

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
public enum MessageEnums {
    Kafka("kafka", KafkaProducer.KAFKA_PRODUCER),
    RabbitMq("rabbitMq", RabbitMqProducer.RABBIT_MQ_PRODUCER);

    private String name;
    private String serviceName;
    MessageEnums(String name, String serviceName) {
        this.name = name;
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
