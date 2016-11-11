package org.osidocker.mongo.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.Mongo;

@SpringBootApplication
@PropertySource(value = "classpath:/dubbo.properties")
public class StartMongoServiceAPP {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new SpringApplicationBuilder(StartMongoServiceAPP.class).web(false).run(args);
		CountDownLatch closeLatch = new CountDownLatch(1);
		closeLatch.await();
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public Mongo mongo() throws Exception {
        return new Mongo("127.0.0.1");
    }

	@Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "mydatabase");
    }
	
	@Bean
	public SimpleMongoDbFactory getSimpleMongoDbFactory() throws Exception{
		return new SimpleMongoDbFactory(mongo(), "mydatabase");
	}
}
