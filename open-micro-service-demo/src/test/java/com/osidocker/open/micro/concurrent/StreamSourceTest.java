package com.osidocker.open.micro.concurrent;

import com.sun.tools.javac.util.List;
import org.junit.Test;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @creato 2019-04-05 9:12
 */
public class StreamSourceTest {


    @Test
    public void testStream(){
        Stream.iterate(1,i->++i).distinct().limit(100).forEach(System.out::println);
    }

    @Test
    public void max(){
        List<String> strings = List.of("Apple", "bug", "ABC", "Dog");
        OptionalInt max
                = strings.stream()
                //无状态中间操作
                .filter(s -> s.startsWith("A"))
                //无状态中间操作
                .mapToInt(String::length)
                //有状态中间操作
                .sorted()
                //非短路终端操作
                .max();
        System.out.println(strings.parallelStream().collect(Collectors.joining("#")));
    }
}
