package com.osidocker.open.micro.guava;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheLoaderCreator {
    private static Logger log = LoggerFactory.getLogger(CacheLoaderCreator.class);

    public static com.google.common.cache.CacheLoader<String, Employee> createCacheLoader() {
       return new com.google.common.cache.CacheLoader<String, Employee>() {
           @Override
           public Employee load(String key) throws Exception {
               log.info("加载创建key:" + key);
               return new Employee(key, key + "dept", key + "id");
           }
       };
    }

    public static com.google.common.cache.CacheLoader<String, Employee> createNUllCacheLoader() {
       return new com.google.common.cache.CacheLoader<String, Employee>() {
           @Override
           public Employee load(String key) throws Exception {
               log.info("加载创建key:" + key);
               if (key.equals("null")) {
                   return null;
               }
               return new Employee(key, key + "dept", key + "id");
           }
       };
    }

    public static com.google.common.cache.CacheLoader<String, Optional<Employee>> createNullValueUseOptionalCacheLoader() {
       return new com.google.common.cache.CacheLoader<String, Optional<Employee>>() {
           @Override
           public Optional<Employee> load(String key) throws Exception {
               log.info("加载创建key:" + key);
               if (key.equals("null")) {
                   return Optional.fromNullable(null);
               } else {
                   return Optional.fromNullable( new Employee(key, key + "dept", key + "id"));
               }
           }
       };
    }
}
