package com.osidocker.open.micro.security.impl;

import clojure.lang.Obj;
import com.osidocker.open.micro.service.impl.SmsSendServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;

/**
 * 自定义身份认证验证组件
 *
 * @author zhaoxinguo on 2017/9/12.
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    public static final String MODE_PHONE_MESSAGE = "PhoneMessage";
    public static final String MODE_PHONE_PASSWORD = "PhonePassword";
    public static final String MODE_USER_OPENID = "OpenId";

    private UserDetailsService userDetailsService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private RedisTemplate<Object,Object> redisTemplate;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,RedisTemplate<Object,Object> redisTemplate){
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        String loginMode = (String) authentication.getDetails();

        // 认证逻辑
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);
        if (null != userDetails) {
            if( MODE_PHONE_MESSAGE.equals(loginMode) ){
                if( !validMessage( name,password ) ){
                    throw new BadCredentialsException("短信验证码不正确!");
                }
            }else if( MODE_PHONE_PASSWORD.equals(loginMode) ){
                String encodePassword = DigestUtils.md5DigestAsHex((password).getBytes());
                if (!userDetails.getPassword().equals(encodePassword)) {
                    throw new BadCredentialsException("密码错误");
                }
            }
        } else {
            throw new UsernameNotFoundException("用户不存在");
        }
        // 这里设置权限和角色
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add( new GrantedAuthorityImpl("ROLE_ADMIN"));
        authorities.add( new GrantedAuthorityImpl("AUTH_WRITE"));
        // 生成令牌 这里令牌里面存入了:name,password,authorities, 当然你也可以放其他内容
        Authentication auth = new UsernamePasswordAuthenticationToken(name, password, authorities);
        return auth;
    }

    /**
     * 校验短信验证码是否正确
     * @param name      用户名(手机号码)
     * @param password  密码（短信验证码）
     * @return
     */
    private boolean validMessage(String name, String password) {
        String cacheKey = SmsSendServiceImpl.LOGIN_PHONE_CODE + name;
        String code = (String) redisTemplate.opsForValue().get(cacheKey);
        if( password.equals(code) ){
            return true;
        }
        return false;
    }

    /**
     * 是否可以提供输入类型的认证服务
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
