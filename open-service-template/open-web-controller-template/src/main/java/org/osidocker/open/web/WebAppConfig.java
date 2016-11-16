package org.osidocker.open.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
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
    
//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setPrefix("WEB-INF/pages/");
//        resolver.setSuffix(".html");
//        resolver.setCache(false);
//        resolver.setOrder(2);
//        return resolver;
//    }
    
    /**
     * 注入sessionfatory
     * @return
     */
    @Bean
    public HibernateJpaSessionFactoryBean sessionFactory() {
        return new HibernateJpaSessionFactoryBean();
    }
}
