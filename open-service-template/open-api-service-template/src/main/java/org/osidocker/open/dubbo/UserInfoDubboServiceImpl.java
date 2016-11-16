package org.osidocker.open.dubbo;

import javax.annotation.Resource;

import org.osidocker.open.api.UserInfoService;
import org.osidocker.open.entity.UserInfo;

import com.alibaba.dubbo.config.annotation.Service;

import javassist.NotFoundException;

@Service(version="0.0.1")
public class UserInfoDubboServiceImpl implements UserInfoService {
	
	@Resource(name="userinfoservice-0.0.1")
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
