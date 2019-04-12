package com.osidocker.open.micro.guava;

import com.google.common.base.Strings;
import org.junit.Test;

/**
 * @author Administrator
 * @creato 2019-04-08 21:46
 */
public class TestStrings {

    @Test
    public void testStrings(){
        System.out.println(Strings.commonPrefix("www.baidu.com","www.google.com"));
        System.out.println(Strings.commonSuffix("www.baidu.com","www.google.com"));
        System.out.println(Strings.padStart("111",8,"0".charAt(0)));
        System.out.println(Strings.padEnd("111",8,"0".charAt(0)));
        System.out.println(Strings.repeat("hello",3));
        System.out.println(Strings.lenientFormat("%s 对 %s 说：它就是它的天使!如果%s是坨屎,那么坨屎就是他的天使!","小明","小强","pad"));
    }
}
