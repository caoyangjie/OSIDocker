package org.osidocker.open.api.service;

import org.junit.Test;

import com.github.sd4324530.fastweixin.api.MessageAPI;
import com.github.sd4324530.fastweixin.api.config.ApiConfig;
import com.github.sd4324530.fastweixin.message.TextMsg;

//@RunWith(SpringJUnit4ClassRunner.class)
////指定我们SpringBoot工程的Application启动类
//@SpringApplicationConfiguration(classes = StartDubboServicePublish.class)
///由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
//@WebAppConfiguration
public class WeChatStudyTest {
	
	@Test
	public void testSendMessage(){
		ApiConfig config = new ApiConfig("wxbe8ccd279e797514", "b0e8150e0df9b8d0b3cf93d30290e154");
		System.out.println("=====================this is my token "+config.getAccessToken());
		MessageAPI messAPI = new MessageAPI(config);
		messAPI.sendMessageToUser(new TextMsg("nihao"), true, null);
	}
}
