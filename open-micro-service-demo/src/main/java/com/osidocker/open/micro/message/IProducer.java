/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message;

import com.osidocker.open.micro.vo.BaseMessage;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于9:27 2018/3/15
 * @修改说明：
 * @修改日期： 修改于9:27 2018/3/15
 * @版本号： V1.0.0
 */
public interface IProducer<Message extends BaseMessage> {

    /**
     * 发送消息
     * @param topic 主题
     * @param msg   消息内容对象
     * @param callbackHandler 处理回调函数
     */
    void send(String topic, Message msg, ListenableFutureCallback<Message> callbackHandler);

    /**
     * 发送消息
     * @param topic 主题
     * @param msg   消息内容对象
     */
    void send(String topic, Message msg);
}
