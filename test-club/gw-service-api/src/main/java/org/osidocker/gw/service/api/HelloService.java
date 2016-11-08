package org.osidocker.gw.service.api;

import org.osidocker.gw.service.entity.User;

public interface HelloService {
	
	User getUserByName(String name);
	
	User getUserByAge(int age);
}
