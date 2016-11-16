package org.osidocker.open;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
//@ComponentScan(basePackages="org.osidocker.open",basePackageClasses=UserInfo.class)
public class StartDubboServicePublish {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new SpringApplicationBuilder(StartDubboServicePublish.class).web(false).run(args);
		CountDownLatch closeLatch = new CountDownLatch(1);
		closeLatch.await();
	}
}
