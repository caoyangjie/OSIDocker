package org.osidocker.mongo.service.impl;

import java.util.List;

import org.osidocker.mongo.service.api.MongoAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class MongoAPIDubboService implements MongoAPI {
	
	@Autowired
	protected MongoTemplate template;

	@Override
	public <T> List<T> find(Query query, Class<T> t) {
		return template.find(query, t);
	}

	@Override
	public <T> T findOne(Query query, Class<T> t) {
		return template.findOne(query, t);
	}

	@Override
	public <T> List<T> findAll(Class<T> t) {
		return template.findAll(t);
	}

	@Override
	public <T> T findAndModify(Query query, Update update, Class<T> t) {
		return template.findAndModify(query, update, t);
	}

	@Override
	public <T> T findAndRemove(Query query, Class<T> t) {
		return template.findAndRemove(query, t);
	}

	@Override
	public <T> void updateFirst(Query query, Update update,Class<T> t) {
		template.updateFirst(query, update, t);
	}

	@Override
	public <T> T save(T bean) {
		template.save(bean);
		return bean;
	}

	@Override
	public <T> T findById(String id, Class<T> t) {
		return template.findById(id, t);
	}

	@Override
	public <T> T findById(String id, String collectionName, Class<T> t) {
		return template.findById(id, t, collectionName);
	}
	
	
}
