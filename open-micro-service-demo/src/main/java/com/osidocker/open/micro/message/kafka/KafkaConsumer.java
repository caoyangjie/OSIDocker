/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message.kafka;

import com.osidocker.open.micro.message.AbsMessageProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于16:12 2018/3/15
 * @修改说明：
 * @修改日期： 修改于16:12 2018/3/15
 * @版本号： V1.0.0
 */
@Service
public class KafkaConsumer extends AbsMessageProcessor{

    @KafkaListener(id = "t1", topics = "mykafka")
    public void handlerMessage(ConsumerRecord<?, ?> cr) throws Exception {
        process(cr.value().toString());
    }

}
