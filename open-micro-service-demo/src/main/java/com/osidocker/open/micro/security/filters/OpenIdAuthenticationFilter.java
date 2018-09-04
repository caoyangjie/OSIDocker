package com.osidocker.open.micro.security.filters;

import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.security.WebSecurityConfig;
import com.osidocker.open.micro.security.exceptions.TokenException;
import com.osidocker.open.micro.security.impl.GrantedAuthorityImpl;
import com.osidocker.open.micro.utils.StringUtil;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * OpenId的校验
 * 该类继承自BasicAuthenticationFilter，在doFilterInternal方法中，
 * 从http头的Authorization 项读取token数据，然后用Jwts包提供的方法校验token的合法性。
 * 如果校验通过，就认为这是一个取得授权的合法请求
 * @author zhaoxinguo on 2017/9/13.
 */
public class OpenIdAuthenticationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(OpenIdAuthenticationFilter.class);

    public OpenIdAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String openId =(String) request.getSession().getAttribute("openId");
        if (StringUtil.isEmpty(openId)){
            chain.doFilter(request, response);
            return;
        }
        ShowUserEntity user = (ShowUserEntity) request.getSession().getAttribute("userInfo");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("openId->"+openId, user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
