package org.osidocker.zbus.service.impl;

import org.apache.log4j.Logger;
import org.osidocker.zbus.service.api.ZbusAPI;
import org.osidocker.zbus.service.handler.ZbusAbsHandler;
import org.springframework.stereotype.Service;

public abstract class ZbusAPIBaseService implements ZbusAPI {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	public abstract void registerHandler(ZbusAbsHandler handler);
}
