/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.vo;

import java.io.Serializable;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于17:47 2018/3/14
 * @修改说明：
 * @修改日期： 修改于17:47 2018/3/14
 * @版本号： V1.0.0
 */
public class BaseMessage<MessageEntity> implements Serializable{

    /**
     * 消息处理服务对象
     */
    private String serviceId;
    /**
     * 消息处理类型
     */
    private String eventType;
    /**
     * 真实的请求对象
     */
    private MessageEntity message;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public MessageEntity getMessage() {
        return message;
    }

    public void setMessage(MessageEntity message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "serviceId='" + serviceId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", message=" + message +
                '}';
    }
}
