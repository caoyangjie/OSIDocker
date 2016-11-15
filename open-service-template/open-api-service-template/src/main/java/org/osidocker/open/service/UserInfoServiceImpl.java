package org.osidocker.open.service;

import org.osidocker.open.api.UserInfoService;
import org.osidocker.open.entity.UserInfo;
import org.osidocker.open.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService{
	
	@Autowired
	private UserInfoRepository userInfoRepository;
	
//	@Cacheable(value="testCache",key="'findByUsername'+#username")
	public UserInfo findByUsername(String username) {
		System.out.println("UserInfoServiceImpl.findByUsername()");
		return userInfoRepository.findByUsername(username);
	}
	
}
