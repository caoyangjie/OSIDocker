package org.osidocker.open;

import java.util.concurrent.CountDownLatch;

import org.osidocker.open.entity.UserInfo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

@SpringBootApplication
//@ComponentScan(basePackages="org.osidocker.open",basePackageClasses=UserInfo.class)
public class StartDubboServicePublish {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new SpringApplicationBuilder(StartDubboServicePublish.class).web(false).run(args);
		CountDownLatch closeLatch = new CountDownLatch(1);
		closeLatch.await();
	}
	
	/**
     * 注入sessionfatory
     * @return
     */
    @Bean
    public HibernateJpaSessionFactoryBean sessionFactory() {
        return new HibernateJpaSessionFactoryBean();
    }
}
