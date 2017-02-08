package org.osidocker.open.service;

import org.osidocker.open.utils.LocaleMessageSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class OsiDockerBaseService {
	@Autowired
	protected LocaleMessageSourceService messageServ;
	
	protected Logger log = LoggerFactory.getLogger(this.getClass().getName());
}
