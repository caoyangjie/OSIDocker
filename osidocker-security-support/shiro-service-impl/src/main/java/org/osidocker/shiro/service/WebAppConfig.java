package org.osidocker.shiro.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class WebAppConfig extends WebMvcConfigurerAdapter{    
	
    public static void main(String[] args) {  
        SpringApplication.run(WebAppConfig.class, args);  
    }   
      
    /** 
     * 配置拦截器 
     * @author lance 
     * @param registry 
     */  
    public void addInterceptors(InterceptorRegistry registry) {  
//        registry.addInterceptor(new UserSecurityInterceptor()).addPathPatterns("/user/**");  
    }
}
