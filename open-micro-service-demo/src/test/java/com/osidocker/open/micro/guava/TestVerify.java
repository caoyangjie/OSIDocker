package com.osidocker.open.micro.guava;

import com.google.common.base.Ticker;
import com.google.common.base.Verify;
import org.junit.Test;

/**
 * @author Administrator
 * @creato 2019-04-08 22:15
 */
public class TestVerify {

    @Test
    public void testVerify(){
//        Verify.verify(1!=1,"%s 居然不等于 %s",null,null);
        Verify.verifyNotNull(null);
    }

    @Test
    public void testTicker(){
        System.out.println(System.nanoTime());
        System.out.println(Ticker.systemTicker().read());
    }
}
