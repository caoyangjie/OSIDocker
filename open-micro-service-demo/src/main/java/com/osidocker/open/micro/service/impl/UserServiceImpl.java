package com.osidocker.open.micro.service.impl;

import com.osidocker.open.micro.mapper.UserInfoMapper;
import com.osidocker.open.micro.service.IUserService;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 用户操作实现类
 *
 * @author Administrator
 * @creato 2017-12-02 13:40
 */
@Service
public class UserServiceImpl implements IUserService {
    private final String USER_CACHE_IN_REDIS = "USER:SERVICE";

    @Resource
    private UserInfoMapper userInfoMapper;

    @CachePut(cacheNames = USER_CACHE_IN_REDIS,key = "'USER:'+#name")
    public Map<String, Object> findUserByName(String name) {
        System.out.println("=============================执行数据库查询");
        return userInfoMapper.findByUsername(name);
    }

    @Cacheable(cacheNames = USER_CACHE_IN_REDIS,key="'USER:'+#name")
    public Map<String,Object> getUserByName(String name) {
        return null;
    }
}
