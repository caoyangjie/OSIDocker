package com.osidocker.open.micro.netty.protocol.handlers;

import com.osidocker.open.micro.netty.protocol.NettyMessage;
import com.osidocker.open.micro.netty.protocol.MessageHeader;
import com.osidocker.open.micro.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Administrator
 * @creato 2019-03-16 22:55
 */
public class HeartBeatRspHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if( message.getHeader()!=null && message.getHeader().getType()== MessageType.HEART_REQ.getType()){
            System.out.println("读到客户端的心跳请求");
            ctx.writeAndFlush(buildHeartBeatRsp());
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage<Byte> buildHeartBeatRsp() {
        NettyMessage message = new NettyMessage();
        MessageHeader header = new MessageHeader();
        header.setType(MessageType.HEART_RSP.getType());
        message.setHeader(header);
        return message;
    }
}
