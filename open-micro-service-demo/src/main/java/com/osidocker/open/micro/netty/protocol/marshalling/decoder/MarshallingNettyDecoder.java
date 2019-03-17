package com.osidocker.open.micro.netty.protocol.marshalling.decoder;

import com.osidocker.open.micro.netty.NettyMessage;
import com.osidocker.open.micro.netty.protocol.MessageHeader;
import com.osidocker.open.micro.netty.protocol.marshalling.ChannelBufferByteInput;
import com.osidocker.open.micro.netty.protocol.marshalling.MarshallingCodeCFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import io.netty.util.CharsetUtil;
import org.jboss.marshalling.Unmarshaller;

import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @creato 2019-03-16 21:11
 */
public class MarshallingNettyDecoder extends LengthFieldBasedFrameDecoder {

    private UnmarshallerProvider provider;
    /**
     * Creates a new decoder whose maximum object size is {@code 1048576}
     * bytes.  If the size of the received object is greater than
     * {@code 1048576} bytes, a {@link StreamCorruptedException} will be
     * raised.
     *
     */
    public MarshallingNettyDecoder() {
        this(1048576);
    }

    /**
     * Creates a new decoder with the specified maximum object size.
     *
     * @param maxObjectSize  the maximum byte length of the serialized object.
     *                       if the length of the received object is greater
     *                       than this value, {@link TooLongFrameException}
     *                       will be raised.
     */
    public MarshallingNettyDecoder(int maxObjectSize) {
        super(maxObjectSize, 0, 4, 0, 4);
        this.provider = MarshallingCodeCFactory.buildMarshallingDecoderProvider();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if( in==null ){
            return null;
        }
        NettyMessage message = new NettyMessage();
        MessageHeader header = new MessageHeader();
        header.setMsgCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionId(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());
        int size = in.readInt();
        if( size > 0 ){
            Map<String,Object> attachMap = new HashMap<>();
            Stream.iterate(1,i->i+1).limit(size).forEach(i->{
                try {
                    int keySize = in.readInt();
                    byte[] keyStr = new byte[keySize];
                    in.readBytes(keyStr);
                    String key = new String(keyStr, Charset.forName(CharsetUtil.UTF_8.name()));
                    attachMap.put(key,decoder(ctx,in));
                } catch (Exception e) {
                }
            });
            header.setAttachment(attachMap);
        }
        message.setHeader(header);
        if( in.readableBytes() > 4 ){
            message.setBody(decoder(ctx,in));
        }
        return message;
    }

    protected Object decoder(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx,in);
        if (frame == null) {
            return null;
        }

        Unmarshaller unmarshaller = MarshallingCodeCFactory.buildMarshallingDecoderProvider().getUnmarshaller(ctx);
        ChannelBufferByteInput input = new ChannelBufferByteInput(frame);

        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            return obj;
        } finally {
            // Call close in a finally block as the ReplayingDecoder will throw an Error if not enough bytes are
            // readable. This helps to be sure that we do not leak resource
            unmarshaller.close();
        }
    }
}
