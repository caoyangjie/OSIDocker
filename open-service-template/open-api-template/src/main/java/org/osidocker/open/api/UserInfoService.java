package org.osidocker.open.api;

import org.osidocker.open.entity.UserInfo;

public interface UserInfoService {
	
	/**通过username查找用户信息;*/
	public UserInfo findByUsername(String username);
}
