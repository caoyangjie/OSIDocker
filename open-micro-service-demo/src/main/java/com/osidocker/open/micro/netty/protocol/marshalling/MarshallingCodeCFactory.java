package com.osidocker.open.micro.netty.protocol.marshalling;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class MarshallingCodeCFactory {
  
    public static UnmarshallerProvider buildMarshallingDecoderProvider() {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);  
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, configuration);
        return provider;
    }  
  
    public static MarshallerProvider buildMarshallingEncoderProvider() {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");  
        final MarshallingConfiguration configuration = new MarshallingConfiguration();  
        configuration.setVersion(5);  
        MarshallerProvider provider = new DefaultMarshallerProvider(factory, configuration);
        return provider;
    }  
}
