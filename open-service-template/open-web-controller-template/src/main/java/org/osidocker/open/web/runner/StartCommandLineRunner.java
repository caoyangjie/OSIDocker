package org.osidocker.open.web.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
public class StartCommandLineRunner implements CommandLineRunner {

	@Override
	public void run(String... arg0) throws Exception {
		System.out.println("this is the StartCommandLineRunner!");
	}

}
