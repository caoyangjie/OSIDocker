package org.osidocker.open.service;

import org.osidocker.open.api.UserInfoService;
import org.osidocker.open.entity.UserInfo;
import org.osidocker.open.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.alibaba.dubbo.config.annotation.Service;

import javassist.NotFoundException;

@Service
public class UserInfoServiceImpl implements UserInfoService{
	
	private static final String CACHE_KEY = "userInfo";
	private static final String DEMO_CACHE_NAME = "demo";
	@Autowired
	private UserInfoRepository userInfoRepository;
	
	@Cacheable(value=DEMO_CACHE_NAME,key="'userInfo_'+#username")
	public UserInfo findByUsername(String username) {
		System.out.println("UserInfoServiceImpl.findByUsername()");
		return userInfoRepository.findByUsername(username);
	}

	@Override
	@CacheEvict(value = DEMO_CACHE_NAME,key = "'demoInfo_'+#id")
	public void delete(Long id) {
		userInfoRepository.delete(id);
	}

	@Override
	@CachePut(value = DEMO_CACHE_NAME,key = "'userInfo_'+#updated.getUid()")
	public UserInfo update(UserInfo updated) throws NotFoundException {
		// TODO Auto-generated method stub
		UserInfo ui = userInfoRepository.findOne(updated.getUid());
		if(ui==null){
			throw new NotFoundException("not find "+updated.getUid());
		}
		ui.setName(updated.getName());
		ui.setState(updated.getState());
		userInfoRepository.save(ui);
		return ui;
	}

	@Override
	@Cacheable(value=DEMO_CACHE_NAME,key="'userInfo_'+#id")
	public UserInfo findById(Long id) {
		// TODO Auto-generated method stub
		return userInfoRepository.findOne(id);
	}

	@Override
	@CacheEvict(value=DEMO_CACHE_NAME,key=CACHE_KEY)
	public UserInfo save(UserInfo userInfo) {
		// TODO Auto-generated method stub
		return userInfoRepository.save(userInfo);
	}
	
}