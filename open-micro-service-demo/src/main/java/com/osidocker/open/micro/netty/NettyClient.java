package com.osidocker.open.micro.netty;

import com.osidocker.open.micro.netty.protocol.handlers.HeartBeatReqHandler;
import com.osidocker.open.micro.netty.protocol.handlers.LoginAuthReqHandler;
import com.osidocker.open.micro.netty.protocol.marshalling.decoder.MarshallingNettyDecoder;
import com.osidocker.open.micro.netty.protocol.marshalling.encoder.MarshallingNettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @creato 2019-03-16 23:02
 */
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final static String HOST = "192.168.31.240";
    private final static int PORT = 12345;
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(String host,int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("nettyMessageDecoder",new MarshallingNettyDecoder())
                                .addLast("nettyMessageEncoder",new MarshallingNettyEncoder())
//                                .addLast("timeoutHandler",new ReadTimeoutHandler(50))
                                .addLast("loginHandler",new LoginAuthReqHandler())
                                .addLast("heartbeatHandler",new HeartBeatReqHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,port), new InetSocketAddress(HOST,19999)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bootstrap.group().shutdownGracefully();
            group.shutdownGracefully();
            executor.execute(()->{
                try {
                    TimeUnit.SECONDS.sleep(5);
                    connect(HOST,PORT);
                } catch (InterruptedException e) {
                }

            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient().connect(HOST,PORT);
    }

}
