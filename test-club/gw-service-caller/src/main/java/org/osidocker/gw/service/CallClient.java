package org.osidocker.gw.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CallClient {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubbo.xml");  
//        context.start();  
//	    HelloService demoService = (HelloService)context.getBean("helloService"); // 获取远程服务代理  
//	    User u = demoService.getUserByName("caoyangjie"); // 执行远程方法  
//	    System.out.println(u.getName());  
		SpringApplication.run(CallClient.class, args);
	}

}
