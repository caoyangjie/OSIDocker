package com.osidocker.open.micro.netty.protocol;

import com.osidocker.open.micro.netty.protocol.MessageHeader;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @creato 2019-03-16 19:31
 */
@Data
public final class NettyMessage<T> implements Serializable {

    private MessageHeader header;
    private T body;
}
