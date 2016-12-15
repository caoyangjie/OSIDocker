package org.osidocker.open.api.test;

import org.osidocker.open.web.profiles.DemoBean;
import org.osidocker.open.web.profiles.DemoProfileConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunProfileService {
	
	public static void main(String[] args){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.getEnvironment().setActiveProfiles("sit");
		ctx.register(DemoProfileConfig.class);
		ctx.refresh();
		
		DemoBean db = ctx.getBean(DemoBean.class);
		System.out.println(db.getContent());
	}
}
