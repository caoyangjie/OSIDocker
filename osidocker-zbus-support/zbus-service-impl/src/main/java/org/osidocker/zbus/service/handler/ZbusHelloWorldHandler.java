package org.osidocker.zbus.service.handler;

import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.entity.TransformData;
import org.springframework.stereotype.Service;
import org.zbus.net.http.Message;

@Service
public class ZbusHelloWorldHandler extends ZbusAbsHandler {

	@Override
	public void doObjectHandler(TransformData t, Message msg) {
		System.out.println(t.getHead()+":"+t.getBody());
	}

	@Override
	public EnumZbusMqName Name() {
		return EnumZbusMqName.helloWorld;
	}

}
