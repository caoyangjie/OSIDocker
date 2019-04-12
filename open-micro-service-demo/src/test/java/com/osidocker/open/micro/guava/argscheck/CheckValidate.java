package com.osidocker.open.micro.guava.argscheck;

/**
 * @author Administrator
 * @creato 2019-04-11 23:13
 */
public class CheckValidate {

    public <T> boolean isTrue(T t){
        return t==null?false:true;
    }

}
