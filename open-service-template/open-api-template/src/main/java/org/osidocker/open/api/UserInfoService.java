package org.osidocker.open.api;

import org.osidocker.open.entity.UserInfo;

import com.github.pagehelper.PageInfo;

import javassist.NotFoundException;

public interface UserInfoService {
	
	/**通过username查找用户信息;*/
	public UserInfo findByUsername(String username);
	
	void delete(Long id);
	 
	UserInfo update(UserInfo updated) throws NotFoundException;
 
	UserInfo findById(Long id);
 
	UserInfo save(UserInfo demoInfo);
	
	public PageInfo<UserInfo> searchName(String username);
}
