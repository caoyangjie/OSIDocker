package org.osidocker.mongo.service.impl;

import org.osidocker.mongo.service.api.MongoAPI;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class MongoAPIDubboService implements MongoAPI {
	
	@Autowired
	protected MongoAPI api;

	@Override
	public void insert() {
		// TODO Auto-generated method stub
		api.insert();
	}

}
