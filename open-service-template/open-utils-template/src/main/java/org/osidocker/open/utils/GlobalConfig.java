package org.osidocker.open.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class GlobalConfig {

	private static Environment env;
	
	@Autowired
	private Environment enviroment;
	
	@PostConstruct
	public void init(){
		env = enviroment;
	}
	
	public static boolean getBooleanProperty(String key){
		return "true".equals(env.getProperty(key));
	}
	
	public static int getIntProperty(String key){
		return Integer.decode(env.getProperty(key));
	}
	
	public static String getProperty(String key){
		return env.getProperty(key);
	}
}
