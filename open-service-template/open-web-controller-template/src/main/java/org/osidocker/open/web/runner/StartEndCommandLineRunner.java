package org.osidocker.open.web.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(100)
public class StartEndCommandLineRunner implements CommandLineRunner {

	@Override
	public void run(String... arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("这是最后一个执行的StartEndCommandLineRunner");
	}

}
