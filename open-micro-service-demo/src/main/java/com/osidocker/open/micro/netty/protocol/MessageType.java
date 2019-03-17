package com.osidocker.open.micro.netty.protocol;

/**
 * @author Administrator
 * @creato 2019-03-16 21:53
 */
public enum  MessageType {
    /** 业务请求消息 **/
    BUS_REQ((byte)0),
    /** 业务响应消息 **/
    BUS_RSP((byte)1),
    /*** 业务OneWay消息 */
    BUS_WAY((byte)2),
    /** 握手请求消息 **/
    HAND_REQ((byte)3),
    /** 握手响应消息 **/
    HAND_RSP((byte)4),
    /** 心跳请求消息 **/
    HEART_REQ((byte)5),
    /** 心跳响应消息 **/
    HEART_RSP((byte)6);

    byte type;

    MessageType(byte type){
        this.type = type;
    }

    public byte getType() {
        return type;
    }}
