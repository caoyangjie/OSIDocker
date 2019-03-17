package com.osidocker.open.micro.netty.protocol.handlers;

import com.osidocker.open.micro.netty.protocol.NettyMessage;
import com.osidocker.open.micro.netty.protocol.MessageHeader;
import com.osidocker.open.micro.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @creato 2019-03-16 22:13
 */
public class LoginAuthRspHandler extends ChannelHandlerAdapter {

    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<>();

    private Stream<String> whiteList =  Stream.of( "127.0.0.1","192.168.31.240" );

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if( message.getHeader()!=null && message.getHeader().getType() == MessageType.HAND_REQ.getType() ){
            System.out.println("读到了客户端的登录认证请求");
            String nodeIndex = ctx.channel().remoteAddress().toString();
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            NettyMessage rsp = buildResponse((byte)-1);
            if( nodeCheck.containsKey(nodeIndex) ){
                rsp = buildResponse((byte)-1);
            }
            if( whiteList.anyMatch(w->w.equals(address.getAddress().getHostAddress()))  ){
                nodeCheck.put(nodeIndex,Boolean.TRUE);
                rsp.setBody((byte)0);
                ctx.writeAndFlush(rsp);
            }else{
                ctx.fireChannelRead(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage<Byte> buildResponse(byte result){
        NettyMessage<Byte> rsp = new NettyMessage<>();
        MessageHeader header = new MessageHeader();
        header.setType(MessageType.HAND_RSP.getType());
        rsp.setHeader(header);
        rsp.setBody(result);
        return rsp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
