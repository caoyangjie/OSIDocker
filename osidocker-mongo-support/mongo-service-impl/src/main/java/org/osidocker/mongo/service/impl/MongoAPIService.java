package org.osidocker.mongo.service.impl;

import org.osidocker.mongo.service.api.MongoAPI;
import org.osidocker.mongo.service.client.MongoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoAPIService implements MongoAPI {

	@Autowired
	protected MongoManager mm;
	
	@Override
	public void insert() {
		// TODO Auto-generated method stub
		System.out.println("执行了mongoDB的数据插入操作!");
	}

}
