package org.osidocker.open.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osidocker.open.StartDubboServicePublish;
import org.osidocker.open.api.NoticeMsgService;
import org.osidocker.open.entity.RpTransactionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StartDubboServicePublish.class)
public class NoticeMsgServiceTest {
	
	@Autowired
	protected NoticeMsgService noticeService;
	
	@Test
	public void test(){
		RpTransactionMessage message = new RpTransactionMessage();
		noticeService.saveMessageWaitingConfirm(message);
	}
}
