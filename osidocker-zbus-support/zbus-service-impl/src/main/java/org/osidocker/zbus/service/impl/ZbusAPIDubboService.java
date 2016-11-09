package org.osidocker.zbus.service.impl;

import java.io.Serializable;

import org.osidocker.zbus.service.EnumZbusMqName;
import org.osidocker.zbus.service.api.ZbusAPI;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class ZbusAPIDubboService implements ZbusAPI{
	@Autowired
	protected ZbusAPIDefaultService defalutService;

	@Override
	public <T extends Serializable> void doSendMQHandler(EnumZbusMqName topic, T t) {
		defalutService.doSendMQHandler(topic, t);
	}

}
