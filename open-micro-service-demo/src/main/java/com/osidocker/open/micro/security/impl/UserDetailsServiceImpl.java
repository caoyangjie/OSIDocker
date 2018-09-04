package com.osidocker.open.micro.security.impl;

import com.osidocker.open.micro.model.SystemUser;
import com.osidocker.open.micro.security.mapper.UserMapper;
import com.osidocker.open.micro.security.service.UserService;
import com.osidocker.open.micro.security.vos.SecurityUser;
import com.osidocker.open.micro.security.vos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import static java.util.Collections.emptyList;

/**
 * @author zhaoxinguo on 2017/9/13.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService,UserService {

    @Resource
    private UserMapper userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = findUserByTelephone(username);
        if (securityUser == null) {
            throw new UsernameNotFoundException("用户未注册或已删除。");
        }
        if (!securityUser.isAccountNonLocked()) {
            throw new AuthenticationServiceException("您的账号已被管理员禁用，请联系管理员。");
        }
        // 用户密码已在登录拦截时处理、此处不显示
        securityUser.setPassword("***");
        return new org.springframework.security.core.userdetails.User(securityUser.getUsername(), securityUser.getPassword(), emptyList());
    }

    @Override
    public SecurityUser findUserByTelephone(String username) {
        if (!StringUtils.hasText(username)) {
            throw new AuthenticationServiceException("电话号码不能为空。");
        }
        SecurityUser securityUser = userRepository.findByUsername(username);
        return securityUser;
    }

    @Override
    public boolean registerUser(String telephone) {
        return userRepository.addUserByPhone(telephone)==1;
    }

    @Override
    public boolean updateUser(SystemUser user) {
        return userRepository.updateUser(user)==1;
    }


}
