package org.osidocker.zbus.service.api;

import java.io.Serializable;

import org.osidocker.zbus.service.EnumZbusMqName;

public interface ZbusAPI {
	public <T extends Serializable> void doSendMQHandler(EnumZbusMqName topic, T t);
}
