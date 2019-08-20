package com.osidocker.open.micro.guava;

import com.google.common.base.Optional;
import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * descrption: guava缓存的使用
 * authohr: wangji
 * date: 2018-02-05 14:05
 */
@Slf4j
public class CacheLoaderTest {

    public static final String WANGJI = "wangji";

    @Test
    public void testBaisc() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(30L, TimeUnit.MILLISECONDS)
                .build(CacheLoaderCreator.createCacheLoader());
        Employee employee = cache.get("wangji");
        log.info("获取结果：" + employee.toString());
        log.info("获取结果：" + cache.get("wangji"));

        TimeUnit.MILLISECONDS.sleep(31);
        employee = cache.getUnchecked("wangji");
        log.info("重新创建加载信息：" + employee.toString());
    }

    @Test
    public void testSize() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .build(CacheLoaderCreator.createCacheLoader());
        cache.getUnchecked("wangji");
        cache.getUnchecked("wangwang");
        cache.getUnchecked("old wang");
        assertThat(cache.size(), equalTo(3L));

        cache.getUnchecked("new wang");
        Employee employee = cache.getIfPresent("wangji"); //不会重新加载创建cache
        log.info("最新的把老的替换掉：" + (employee == null ? "是的" : "否"));
        Employee newEmployee = cache.getIfPresent("new wang"); //不会重新加载创建cache
        log.info("获取结果：" + newEmployee);
    }

    @Test
    public void testWeight() throws ExecutionException, InterruptedException {
        //如果不同的高速缓存条目有不同的“权重”，例如，如果你的缓存值有着完全不同的记忆的足迹--你可以用cachebuilder指定一个权重函数。秤（秤）和一个cachebuilder最大缓存量的最大重量（长）
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .maximumWeight(100)
                .weigher(new Weigher<String, Employee>() {
                    public int weigh(String key, Employee employee) {
                        int weight = employee.getName().length() + employee.getEmpID().length() + employee.getDept().length();//权重计算器
                        log.info("weight is :" + weight);
                        return weight;
                    }
                })
                .build(CacheLoaderCreator.createCacheLoader());
        cache.get("wangji");
        log.info("cacheSize：" + cache.size());
        cache.get("wangwang");
        log.info("cacheSize：" + cache.size());
        cache.get("old wang");
        log.info("cacheSize：" + cache.size());
        cache.get("new wang");
        log.info("cacheSize：" + cache.size());
        cache.get("new wang");
        log.info("cacheSize：" + cache.size());
    }

    /**
     * TTL->time to live
     * Access time => Write/Update/Read
     */
    @Test
    public void testEvictionByAccessTime() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterAccess(3, TimeUnit.SECONDS)
                .build(CacheLoaderCreator.createCacheLoader());
        cache.getUnchecked("wangji");
        TimeUnit.SECONDS.sleep(3);
        Employee employee = cache.getIfPresent(WANGJI); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));
        cache.getUnchecked("guava");

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

    }

    /**
     * Write time => write/update
     */
    @Test
    public void testEvictionByWriteTime() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build(CacheLoaderCreator.createCacheLoader());
        cache.getUnchecked("guava");
        TimeUnit.SECONDS.sleep(2);
        Employee employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        cache.put("guava", new Employee("guava", "guava" + "dept", "guava" + "id")); //手动插入
        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

        cache.put("guava", new Employee("guava", "guava" + "dept", "guava" + "id"));
        TimeUnit.SECONDS.sleep(2);
        employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));

    }

    /**
     * Strong/soft/weak/Phantom reference
     * https://www.cnblogs.com/daxin/p/5604923.html
     * http://cd826.iteye.com/blog/2036659
     */
    @Test
    public void testWeakKey() throws ExecutionException, InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .weakValues()
                .weakKeys()
                .build(CacheLoaderCreator.createCacheLoader());
        cache.getUnchecked("guava");
        cache.getUnchecked("wangji");

        System.gc();
        TimeUnit.MILLISECONDS.sleep(100);
        Employee employee = cache.getIfPresent("guava"); //不会重新加载创建cache
        log.info("被销毁：" + (employee == null ? "是的" : "否"));
    }

    @Test
    public void testSoftKey() throws InterruptedException {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .softValues()
                .build(CacheLoaderCreator.createCacheLoader());
        int i = 0;
        for (; ; ) {
            cache.put("Alex" + i, new Employee("Alex" + 1, "Alex" + 1, "Alex" + 1));
            log.info("The Employee [" + (i++) + "] is store into cache.");
            log.info("cache size" + cache.size());
            TimeUnit.MILLISECONDS.sleep(600);
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * 测试为空的情况
     *
     * @throws InterruptedException
     * @throws Exception
     */
    @Test
    public void testLoadNullValue() throws InterruptedException, Exception {
        LoadingCache<String, Employee> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .softValues()
                .build(CacheLoaderCreator.createNUllCacheLoader());
        thrown.expect(CacheLoader.InvalidCacheLoadException.class);
        try {
            //不存在创建了一个null的value，不被允许的！
            cache.getUnchecked("null");
        } catch (Exception e) {
            log.error("error", e);
            throw e;
        }
    }

    @Test
    public void testLoadNullValueUseOptional() {
        LoadingCache<String, Optional<Employee>> cache = CacheBuilder.newBuilder().build(CacheLoaderCreator.createNullValueUseOptionalCacheLoader());
        Optional<Employee> employeeOptional = cache.getUnchecked("guava");
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            log.info("employee：" + employee.toString());
        }

        Optional<Employee> employeeNull = cache.getUnchecked("null");
        if (!employeeNull.isPresent()) {
            log.info("employee is null");
            Employee def = cache.getUnchecked("null").or(new Employee("default", "default", "default"));
            log.info("employee deful：" + def.toString());
        }
    }

    @Test
    public void testCacheRemovedNotification() {
        CacheLoader<String, String> loader = CacheLoader.from(String::toUpperCase);
        RemovalListener<String, String> listener = notification ->
        {
            if (notification.wasEvicted()) {
                RemovalCause cause = notification.getCause();
                log.info("remove cacase is :" + cause.toString());
                log.info("key:" + notification.getKey() + "value:" + notification.getValue());
            }
        };
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .removalListener(listener)// 添加删除监听
                .build(loader);
        cache.getUnchecked("wangji");
        cache.getUnchecked("wangwang");
        cache.getUnchecked("guava");
        cache.getUnchecked("test");
        cache.getUnchecked("test1");
    }

    @Test
    public void testCachePreLoad() {
        CacheLoader<String, String> loader = CacheLoader.from(String::toUpperCase);
        LoadingCache<String, String> cache = CacheBuilder.newBuilder().build(loader);
        Map<String, String> preData = new HashMap<String, String>() {
            {
                put("guava", "guava");
                put("guava1", "guava1");
            }
        };
        cache.putAll(preData); //提前插入
        log.info("cache size :" + cache.size());
        log.info("guava:" + cache.getUnchecked("guava"));
    }

    @Test
    public void testCacheRefresh() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CacheLoader<String, Long> cacheLoader = CacheLoader
                .from(k -> {
                    counter.incrementAndGet();
                    log.info("创建 key :" + k);
                    return System.currentTimeMillis();
                });
        LoadingCache<String, Long> cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(2, TimeUnit.SECONDS) // 2s后重新刷新
                .build(cacheLoader);


        Long result1 = cache.getUnchecked("guava");
        TimeUnit.SECONDS.sleep(3);
        Long result2 = cache.getUnchecked("guava");
        log.info(result1.longValue() != result2.longValue() ? "是的" : "否");

    }
}
