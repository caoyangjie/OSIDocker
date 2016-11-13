package org.osidocker.shiro.service.repository;

import org.osidocker.shiro.service.entity.UserInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * UserInfo持久化类;
 * @author Angel(QQ:412887952)
 * @version v.0.1
 */
public interface UserInfoRepository extends CrudRepository<UserInfo,Long>{
	
	/**通过username查找用户信息;*/
	public UserInfo findByUsername(String username);
	
}
