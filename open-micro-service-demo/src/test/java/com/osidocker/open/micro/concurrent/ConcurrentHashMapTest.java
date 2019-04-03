package com.osidocker.open.micro.concurrent;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @creato 2019-03-27 20:59
 */
public class ConcurrentHashMapTest {

    @Test
    public void testConcurrentHashMap(){
        Map map = new HashMap(10);
        Stream.iterate(0,i->++i).limit(10).collect(Collectors.toList()).forEach(i->map.put(i,"i的值为："+i));
        ConcurrentHashMap hashMap = new ConcurrentHashMap();
        hashMap.putAll(map);
    }

}
