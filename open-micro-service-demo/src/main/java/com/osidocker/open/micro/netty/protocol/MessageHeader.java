package com.osidocker.open.micro.netty.protocol;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @creato 2019-03-16 19:33
 */
@Data
public final class MessageHeader implements Serializable {
    /**
     * 消息码
     */
    private int msgCode = 0XC0FFB0BE;
    /**
     * 消息长度
     */
    private int length;
    /**
     * 会话Id
     */
    private long sessionId;
    /**
     * 消息类型
     */
    private byte type;
    /**
     * 消息优先级
     */
    private byte priority;
    /**
     * 协议扩展预留
     */
    private Map<String,Object> attachment = new HashMap<String,Object>();

    @Override
    public String toString() {
        return "MessageHeader{" +
                "msgCode=" + msgCode +
                ", length=" + length +
                ", sessionId=" + sessionId +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
