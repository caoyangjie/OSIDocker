package com.osidocker.open.micro.annotation;

import java.util.function.Function;

/**
 * @Description:
 * @author: Administrator
 * @date: 2020年09月28日 13:40
 * @Copyright: © 大科城云运维管理平台
 */
public class StringFunction implements Function<Object,String> {

    @Override
    public String apply(Object o) {
        return o.toString();
    }
}
