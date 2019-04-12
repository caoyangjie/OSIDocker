package com.osidocker.open.micro.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.Reflection;
import com.osidocker.open.micro.annotation.Check;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @creato 2019-04-11 21:00
 */
public class CheckArgsHandlerFactory {
    private static Cache<Class, Object> proxyCache = CacheBuilder.newBuilder()
            .maximumSize(256)
            .build();

    private static class CheckArgsHandler<T> implements InvocationHandler {
        private T instance;
        private LoadingCache<Method, List<Predicate>> cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(new CacheLoader<Method, List<Predicate>>() {

                    @Override
                    public List<Predicate> load(Method method) throws Exception {
                        return Invokable.from(method)
                                .getParameters().stream()
                                .flatMap(p -> Stream.of(
                                        p.isAnnotationPresent(Nonnull.class) ? Predicates.notNull() : Predicates.alwaysTrue()))
                                .collect(Collectors.toList());
                    }
                });

        public CheckArgsHandler(T instance) {
            this.instance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            doCheck(args, method);
            return method.invoke(instance, args);
        }

        private void doCheck(Object[] args, Method method) throws Throwable {
            for (int i = 0; i < cache.get(method).size(); i++) {
                Preconditions.checkArgument(cache.get(method).get(i).test(args[i]), "当前方法[%s]的第[%s]个参数不允许为空!", method.getName(), i + 1);
            }
        }
    }

    /**
     * 默认不进行强制更新接口实现类
     * @param interfaceType
     * @param instance
     * @param <T>
     */
    public static <T> void registerInstance(Class<T> interfaceType, T instance){
        registerInstance(interfaceType,instance,false);
    }

    /**
     * 注入需要代理的接口实现对象
     * @param interfaceType
     * @param instance
     * @param override
     * @param <T>
     */
    public static <T> void registerInstance(Class<T> interfaceType, T instance,boolean override) {
        try {
            if( override ){
                System.out.println("强制进行覆盖更新服务对象!");
                proxyCache.put(interfaceType,Reflection.newProxy(interfaceType, new CheckArgsHandler<T>(instance)));
                return;
            }
            proxyCache.get(interfaceType,()->{
                System.out.println("第一次进来哦");
                return Reflection.newProxy(interfaceType, new CheckArgsHandler<T>(instance));
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取成功代理后的代理对象
     * @param interfaceType
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> interfaceType){
        T result = (T) proxyCache.getIfPresent(interfaceType);
        Preconditions.checkState(result!=null,"当前接口[%s],还未进行注册,请在调用getProxy之前先执行registerProxy函数!",interfaceType.getSimpleName());
        return result;
    }
}
