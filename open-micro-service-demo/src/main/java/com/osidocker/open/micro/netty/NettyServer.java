package com.osidocker.open.micro.netty;

import com.osidocker.open.micro.netty.protocol.handlers.HeartBeatRspHandler;
import com.osidocker.open.micro.netty.protocol.handlers.LoginAuthRspHandler;
import com.osidocker.open.micro.netty.protocol.marshalling.decoder.MarshallingNettyDecoder;
import com.osidocker.open.micro.netty.protocol.marshalling.encoder.MarshallingNettyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author Administrator
 * @creato 2019-03-16 23:18
 */
public class NettyServer {
    private final static String HOST = "192.168.31.240";
    private final static int PORT = 12345;

    public void bind() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("nettyMessageDecoder",new MarshallingNettyDecoder())
                                .addLast("nettyMessageEncoder",new MarshallingNettyEncoder())
//                                .addLast("readTimeOutHandler",new ReadTimeoutHandler(50))
                                .addLast("loginAuthHandler",new LoginAuthRspHandler())
                                .addLast("heartbeatHandler",new HeartBeatRspHandler());
                    }
                });
        server.bind(HOST,PORT).sync();
        System.out.println("监听了本地端口"+PORT);
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().bind();
    }

}
