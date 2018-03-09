package com.osidocker.open.micro;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.osidocker.open.micro.filters.HystrixRequestContextFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

/**
 * micro service模板服务
 *
 */
@SpringBootApplication
@ServletComponentScan
@MapperScan(value = {"com.osidocker.open.**.mapper"})
@EnableCaching
public class App
{

    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
                new HystrixRequestContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }


    @Bean
    public ServletRegistrationBean indexServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
        registration.addUrlMappings("/hystrix.stream");
        return registration;
    }
}
