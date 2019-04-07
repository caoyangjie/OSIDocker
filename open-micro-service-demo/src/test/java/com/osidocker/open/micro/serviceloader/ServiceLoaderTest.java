package com.osidocker.open.micro.serviceloader;

import java.util.ServiceLoader;

/**
 * @author Administrator
 * @creato 2019-04-06 23:52
 */
public class ServiceLoaderTest {

    public static void main(String[] args){
        ServiceLoader<HelloWorld> loader = ServiceLoader.load(HelloWorld.class);
        loader.iterator().forEachRemaining(serv->System.out.println(serv.sayHello()));
    }
}
