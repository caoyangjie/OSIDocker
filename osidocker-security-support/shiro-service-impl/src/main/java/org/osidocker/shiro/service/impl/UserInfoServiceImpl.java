package org.osidocker.shiro.service.impl;

import javax.annotation.Resource;

import org.osidocker.shiro.service.api.UserInfoService;
import org.osidocker.shiro.service.entity.UserInfo;
import org.osidocker.shiro.service.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService{
	
	@Resource
	private UserInfoRepository userInfoRepository;
	
	public UserInfo findByUsername(String username) {
		System.out.println("UserInfoServiceImpl.findByUsername()");
		return userInfoRepository.findByUsername(username);
	}
	
}
