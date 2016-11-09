package org.osidocker.zbus.service.handler;

import java.io.IOException;

import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.entity.TransformData;
import org.osidocker.zbus.service.impl.ZbusAPIBaseService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;

@Service
public abstract class ZbusAbsHandler implements MessageHandler,InitializingBean {
	public abstract EnumZbusMqName Name();
	
	@Autowired
	protected ZbusAPIBaseService baseService;

	@Override
	public void afterPropertiesSet() throws Exception {
		baseService.registerHandler(this);
	}
	
	@Override
	public void handle(Message arg0, Session arg1) throws IOException {
		doObjectHandler( (TransformData) SerializationUtils.deserialize(arg0.getBody()), arg0 );
	}
	
	public abstract void doObjectHandler(TransformData t,Message msg);
}
