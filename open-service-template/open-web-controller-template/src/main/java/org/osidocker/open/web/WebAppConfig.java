package org.osidocker.open.web;

import java.util.List;
import java.util.Locale;

import org.osidocker.open.web.converter.DemoMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

@SpringBootApplication
@ServletComponentScan
@ComponentScan(basePackages="org.osidocker.open")
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
//      registry.addInterceptor(new UserSecurityInterceptor()).addPathPatterns("/user/**");
//    	registry.addInterceptor(localeChangeInterceptor());
    }
    
    @Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    	converters.add(demoMessageConverter());
		super.extendMessageConverters(converters);
	}
    
    @Bean
    public DemoMessageConverter demoMessageConverter(){
    	return new DemoMessageConverter();
    }

	/**
     * cookie区域解析器;
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
       FixedLocaleResolver slr = new FixedLocaleResolver ();
        //设置默认区域,
       slr.setDefaultLocale(Locale.US);
       return slr;
    }
    
    /**
     * 设置SpringBoot项目的session生命周期
     * @return
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
       return new EmbeddedServletContainerCustomizer() {
           @Override
           public void customize(ConfigurableEmbeddedServletContainer container) {
                container.setSessionTimeout(1800);//单位为S
          }
       };
    }
//    
//    @Bean
//    public LocaleChangeInterceptor localeChangeInterceptor() {
//           LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
//           // 设置请求地址的参数,默认为：locale
////         lci.setParamName(LocaleChangeInterceptor.DEFAULT_PARAM_NAME);
//           return lci;
//    }
    
//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setPrefix("WEB-INF/pages/");
//        resolver.setSuffix(".html");
//        resolver.setCache(false);
//        resolver.setOrder(2);
//        return resolver;
//    }
    
//    /**
//     * 注入sessionfatory
//     * @return
//     */
//    @Bean
//    public HibernateJpaSessionFactoryBean sessionFactory() {
//        return new HibernateJpaSessionFactoryBean();
//    }
}
