package org.osidocker.open.api.service;

import javassist.NotFoundException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osidocker.open.StartDubboServicePublish;
import org.osidocker.open.api.UserInfoService;
import org.osidocker.open.entity.UserInfo;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
////指定我们SpringBoot工程的Application启动类
@SpringApplicationConfiguration(classes = StartDubboServicePublish.class)
///由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
//@WebAppConfiguration
public class UserInfoServiceTest {
	
	@Resource(name="userinfoservice-0.0.1")
	protected UserInfoService uis;
	
	@Test
    public void testCache() throws NotFoundException{
    	//存入两条数据.
    	UserInfo userinfo = uis.findByUsername("admin");
    	userinfo = uis.findByUsername("admin");
    	System.out.println(userinfo.getName());
    	userinfo = uis.findByUsername("admin");
    	System.out.println(userinfo.getName());
    	
    	userinfo = uis.findById(userinfo.getUid());
    	System.out.println(userinfo.getName());
    	userinfo = uis.findById(userinfo.getUid());
    	System.out.println(userinfo.getName());
    	userinfo.setName(userinfo.getName()+"_updated");
    	uis.update(userinfo);
    	userinfo = uis.findById(userinfo.getUid());
    	System.out.println(userinfo.getName());
    }
}
