package org.osidocker.open.web.profiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DemoProfileConfig {
	@Bean
	@Profile("dev")
	public DemoBean devDemoBean(){
		return new DemoBean("this is dev demo bean!");
	}
	
	@Bean
	@Profile("sit")
	public DemoBean sitDemoBean(){
		return new DemoBean("this is sit demo bean!");
	}
}
