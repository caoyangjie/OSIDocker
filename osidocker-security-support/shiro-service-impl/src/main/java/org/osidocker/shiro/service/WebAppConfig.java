package org.osidocker.shiro.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
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
    
    @Override 
    public void addViewControllers(ViewControllerRegistry registry) { 
	    registry.addViewController("/error").setViewName("error.html"); 
	    registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override 
    public void configurePathMatch(PathMatchConfigurer configurer) { 
	    super.configurePathMatch(configurer); 
	    configurer.setUseSuffixPatternMatch(false); 
    }
}
