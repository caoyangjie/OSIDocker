package org.osidocker.open.service;

import javassist.NotFoundException;

import org.osidocker.open.api.UserInfoService;
import org.osidocker.open.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version="0.0.1")
public class UserInfoDubboServiceImpl implements UserInfoService {
	
	@Autowired
	protected UserInfoService uis;

	@Override
	public UserInfo findByUsername(String username) {
		return uis.findByUsername(username);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		uis.delete(id);
	}

	@Override
	public UserInfo update(UserInfo updated) throws NotFoundException {
		// TODO Auto-generated method stub
		return uis.update(updated);
	}

	@Override
	public UserInfo findById(Long id) {
		// TODO Auto-generated method stub
		return uis.findById(id);
	}

	@Override
	public UserInfo save(UserInfo demoInfo) {
		// TODO Auto-generated method stub
		return uis.save(demoInfo);
	}

}
