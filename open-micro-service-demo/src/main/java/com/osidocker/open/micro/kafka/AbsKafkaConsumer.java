/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于14:50 2018/3/8
 * @修改说明：
 * @修改日期： 修改于14:50 2018/3/8
 * @版本号： V1.0.0
 */
public abstract class AbsKafkaConsumer implements Runnable{
    private String topic;
    private ConsumerConnector consumerConnector;

    public AbsKafkaConsumer(String topic) {
        this.topic = topic;
        this.consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig());
    }

    /**
     * 返回Kafka连接配置对象
     * @return  连接配置对象
     */
    protected abstract ConsumerConfig createConsumerConfig();

    @Override
    public void run() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 2);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        for (KafkaStream stream : streams) {
            new Thread(new KafkaMessageProcessor(stream)).start();
        }
    }
}
