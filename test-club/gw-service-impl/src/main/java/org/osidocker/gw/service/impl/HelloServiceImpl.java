package org.osidocker.gw.service.impl;

import org.osidocker.gw.service.api.HelloService;
import org.osidocker.gw.service.entity.User;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class HelloServiceImpl implements HelloService {

	@Override
	public User getUserByName(String name) {
		User u = new User();
		u.setName("this test hello ServiceImpl"+name);
		return u;
	}

	@Override
	public User getUserByAge(int age) {
		// TODO Auto-generated method stub
		User u = new User();
		u.setName("this test hello ServiceImpl");
		u.setAge(age);
		return u;
	}

}
