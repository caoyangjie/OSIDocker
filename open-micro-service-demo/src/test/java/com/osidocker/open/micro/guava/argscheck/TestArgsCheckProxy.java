package com.osidocker.open.micro.guava.argscheck;

import com.osidocker.open.micro.utils.CheckArgsHandlerFactory;
import org.junit.Test;

/**
 * @author Administrator
 * @creato 2019-04-11 20:44
 */
public class TestArgsCheckProxy {

    @Test
    public void testProxy(){
        CheckArgsHandlerFactory.registerInstance(IDoWork.class,new DoWorkService());
        CheckArgsHandlerFactory.getProxy(IDoWork.class).execute("你好!");
        CheckArgsHandlerFactory.getProxy(IDoWork.class).execute("你好!","世界",null,true);
        ValidateData data = new ValidateData();
        CheckArgsHandlerFactory.getProxy(IDoWork.class).process(data);
    }

    private static class ValidateData{

    }
}
