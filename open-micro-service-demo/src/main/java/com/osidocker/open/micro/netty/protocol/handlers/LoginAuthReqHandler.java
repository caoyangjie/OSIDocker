package com.osidocker.open.micro.netty.protocol.handlers;

import com.osidocker.open.micro.netty.NettyMessage;
import com.osidocker.open.micro.netty.protocol.MessageHeader;
import com.osidocker.open.micro.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Administrator
 * @creato 2019-03-16 21:49
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("开始发起握手!");
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if( message.getHeader()!=null && message.getHeader().getType()== MessageType.HAND_RSP.getType()){
            System.out.println("读到了服务端的握手返回");
            byte loginResult = (byte) message.getBody();
            if( loginResult != (byte)0 ){
                System.out.println("登录失败!");
                ctx.close();
            }else {
                System.out.println("登录成功!");
                ctx.fireChannelRead(msg);
            }
        }else{
            System.out.println("收到服务端其他响应消息!");
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage<Byte> buildLoginReq() {
        NettyMessage<Byte> message = new NettyMessage<>();
        MessageHeader header = new MessageHeader();
        header.setType(MessageType.HAND_REQ.getType());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }
}
