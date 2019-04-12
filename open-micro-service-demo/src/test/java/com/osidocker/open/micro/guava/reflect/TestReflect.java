package com.osidocker.open.micro.guava.reflect;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeToken;
import org.junit.Test;
import org.testng.Assert;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @creato 2019-04-11 19:10
 */
public class TestReflect {

    @Test
    public void testClassPath() throws IOException {
        ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("com.osidocker").forEach(System.out::println);
    }

    @Test
    public final void testTypeToken(){
        TypeToken key = new TypeToken<String>() {};
        System.out.println(key.getRawType());
        System.out.println(key.getComponentType());
        System.out.println(key.getType());

        List<Integer> list = new ArrayList<Integer>();
        TypeToken typeInteger = new TypeToken<List<Integer>>() {};
        System.out.println(typeInteger.getRawType());
        System.out.println(typeInteger.getComponentType());
        System.out.println(typeInteger.getType());
        System.out.println(typeInteger.getSubtype(List.class));
    }

    @Test
    public void testInvokable() throws NoSuchMethodException {
        Assert.assertEquals(Invokable.from(TestReflect.class.getMethod("testTypeToken")).isPublic(),true);
        Assert.assertEquals(Invokable.from(TestReflect.class.getMethod("testTypeToken")).isOverridable(),false);
        Assert.assertEquals(Invokable.from(TestReflect.class.getMethod("testTypeToken")).isPackagePrivate(),false);

        Assert.assertEquals(Invokable.from(TestReflect.class.getMethod("testSomeThing")).isStatic(),true);
        Assert.assertEquals(Invokable.from(TestReflect.class.getMethod("testSomeThing")).isFinal(),true);
        Assert.assertEquals(Invokable.from(TestReflect.class.getMethod("testSomeThing")).isAbstract(),false);
        Assert.assertEquals(Invokable.from(TestReflect.class.getMethod("testSomeThing")).isAccessible(),false);

        Assert.assertEquals(Invokable.from(TestReflect.class.getDeclaredMethod("testSomeThing",String.class,List.class,Object.class,int[].class)).isVarArgs(),true);
        Invokable.from(TestReflect.class.getDeclaredMethod("testSomeThing",String.class,List.class,Object.class,int[].class)).getParameters().forEach(System.out::println);
        assert Invokable.from(TestReflect.class.getDeclaredMethod("testSomeThing",String.class,List.class,Object.class,int[].class)).getParameters().get(0).isAnnotationPresent(Nullable.class);
        assert !Invokable.from(TestReflect.class.getDeclaredMethod("testSomeThing",String.class,List.class,Object.class,int[].class)).getParameters().get(1).isAnnotationPresent(Nullable.class);
    }

    @Test
    public void testDynamicProxy(){
        Reflection.newProxy(DrawProxy.class, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if( method.getName().equals("doSomething") ){
                     doSomething();
                     return "";
                }
                return null;
            }

            private void doSomething(){
                System.out.println("我在dosomething...");
            }

            private void doSomething(String key){
                System.out.println(key);
            }
        }).doSomething();
    }

    interface DrawProxy{
        void doSomething();
        void doSomething(String hello);
    }

    public static final String testSomeThing(){
        return "";
    }
    public static final <T> String testSomeThing(@Nullable String key, List key2, Object key3, int... key5){
        return "";
    }
}
