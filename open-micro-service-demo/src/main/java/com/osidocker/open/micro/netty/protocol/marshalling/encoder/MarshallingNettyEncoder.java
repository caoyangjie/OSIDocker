package com.osidocker.open.micro.netty.protocol.marshalling.encoder;

import com.osidocker.open.micro.netty.NettyMessage;
import com.osidocker.open.micro.netty.protocol.marshalling.ChannelBufferByteOutput;
import com.osidocker.open.micro.netty.protocol.marshalling.MarshallingCodeCFactory;
import com.osidocker.open.micro.vo.CoreException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import org.jboss.marshalling.Marshaller;
import org.springframework.security.crypto.codec.Utf8;

import java.util.List;

/**
 * @author Administrator
 * @creato 2019-03-16 19:51
 */
public class MarshallingNettyEncoder extends MessageToMessageEncoder<NettyMessage> {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private MarshallerProvider provider;

    public MarshallingNettyEncoder() {
        this.provider = MarshallingCodeCFactory.buildMarshallingEncoderProvider();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
        NettyMessage nettyMessage = (NettyMessage) msg;
        if( nettyMessage==null || nettyMessage.getHeader()==null ){
            throw new CoreException("the encode message is null!");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(nettyMessage.getHeader().getMsgCode());
        sendBuf.writeInt(nettyMessage.getHeader().getLength());
        sendBuf.writeLong(nettyMessage.getHeader().getSessionId());
        sendBuf.writeByte(nettyMessage.getHeader().getType());
        sendBuf.writeByte(nettyMessage.getHeader().getPriority());
        sendBuf.writeInt(nettyMessage.getHeader().getAttachment().size());
        nettyMessage.getHeader().getAttachment().forEach((k,v)->{
            try {
                sendBuf.writeInt(Utf8.encode(k).length);
                sendBuf.writeBytes(Utf8.encode(k));
                encode(ctx,v,sendBuf);
            } catch (Exception e) {
            }
        });
        if( nettyMessage.getBody()!=null ){
            encode(ctx,nettyMessage.getBody(),sendBuf);
        }
        out.add(sendBuf);
    }

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Marshaller marshaller = provider.getMarshaller(ctx);
        int lengthPos = out.writerIndex();
        out.writeBytes(LENGTH_PLACEHOLDER);
        ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
        marshaller.start(output);
        marshaller.writeObject(msg);
        marshaller.finish();
        marshaller.close();
        out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
    }
}
