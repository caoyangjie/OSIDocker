package com.osidocker.open.micro.guava.base;

import com.google.common.base.Enums;
import com.osidocker.open.micro.draw.system.enums.DrawEnums;
import org.junit.Test;

/**
 * @author Administrator
 * @creato 2019-04-13 18:12
 */
public class TestEnums {

    @Test
    public void testEnums() throws IllegalAccessException {
        System.out.println(Enums.stringConverter(DrawEnums.class).convert("Squared"));
        System.out.println(Enums.getIfPresent(DrawEnums.class,"Squared").toJavaUtil().orElseThrow(()->new RuntimeException("fdsafas")));
        System.out.println(Enums.getField(DrawEnums.BigWheel).getType());
    }
}
