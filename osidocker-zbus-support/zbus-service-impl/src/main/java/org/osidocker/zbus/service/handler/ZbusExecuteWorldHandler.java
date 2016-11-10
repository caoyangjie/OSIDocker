package org.osidocker.zbus.service.handler;

import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.entity.TransformData;
import org.springframework.stereotype.Service;
import org.zbus.net.http.Message;

@Service
public class ZbusExecuteWorldHandler extends ZbusAbsHandler{

	@Override
	public EnumZbusMqName Name() {
		return EnumZbusMqName.executeWord;
	}

	@Override
	public void doObjectHandler(TransformData t, Message msg) {
		System.out.println(t.getHead()+"对"+t.getBody());
	}

}
