package org.osidocker.mqtt.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class StartMqttServiceAPP {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new SpringApplicationBuilder(StartMqttServiceAPP.class).web(false).run(args);
		CountDownLatch closeLatch = new CountDownLatch(1);
		closeLatch.await();
	}
}
