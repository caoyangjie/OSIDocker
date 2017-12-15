package com.osidocker.open.micro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * micro service模板服务
 *
 */
@SpringBootApplication
@ServletComponentScan
@MapperScan(value = {"com.osidocker.open.micro.mapper","com.osidocker.open.**.mapper"})
@EnableCaching
public class App
{

    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }
}
