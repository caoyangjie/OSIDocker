package com.osidocker.open.micro.netty.protocol.handlers;

import com.osidocker.open.micro.netty.NettyMessage;
import com.osidocker.open.micro.netty.protocol.MessageHeader;
import com.osidocker.open.micro.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @creato 2019-03-16 22:35
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if( message.getHeader()!=null && message.getHeader().getType()== MessageType.HAND_RSP.getType()){
            System.out.println("client init heartbeat task!");
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx),0,5000, TimeUnit.MILLISECONDS);
        }else if( message.getHeader()!=null && message.getHeader().getType()==MessageType.HEART_RSP.getType() ){
            System.out.println("client receive server heart beat message!");
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private class HeartBeatTask implements Runnable{

        private final ChannelHandlerContext ctx;

        private HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            System.out.println("Client send heartBeat message to server!");
            ctx.writeAndFlush(buildHeartBeat());
        }

        private NettyMessage buildHeartBeat() {
            NettyMessage message = new NettyMessage();
            MessageHeader header = new MessageHeader();
            header.setType(MessageType.HEART_REQ.getType());
            message.setHeader(header);
            return message;
        }
    }
}
