package com.osidocker.open.micro.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osidocker.open.micro.security.WebSecurityConfig;
import com.osidocker.open.micro.security.vos.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 验证用户名密码正确后，生成一个token，并将token返回给客户端
 * 该类继承自UsernamePasswordAuthenticationFilter，重写了其中的2个方法
 * attemptAuthentication ：接收并解析用户凭证。
 * successfulAuthentication ：用户成功登录后，这个方法会被调用，我们在这个方法里生成token。
 * @author zhaoxinguo on 2017/9/12.
 */
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    private String usernameParameter = "username";
    private String passwordParameter = "password";
    private String loginModeParameter = "loginMode";
    private AuthenticationManager authenticationManager;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // 接收并解析用户凭证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if ( !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("请使用POST登录。");
        } else {
            String username = this.obtainUsername(request);
            String password = this.obtainPassword(request);
            String loginMode = this.obtainLoginMode(request);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password,new ArrayList<>());
            token.setDetails(loginMode);
            return authenticationManager.authenticate(token);
        }
    }

    // 用户成功登录后，这个方法会被调用，我们在这个方法里生成token
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        // builder the token
        String token = null;
        try {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            // 定义存放角色集合的对象
            List roleList = new ArrayList<>();
            for (GrantedAuthority grantedAuthority : authorities) {
                roleList.add(grantedAuthority.getAuthority());
            }
            token = Jwts.builder()
                    .setSubject(auth.getName() + "-" + roleList)
                    .setExpiration(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000))
                    // 设置过期时间 30 * 24 * 60 * 60秒(这里为了方便测试，所以设置了1月的过期时间，实际项目需要根据自己的情况修改)
                    .signWith(SignatureAlgorithm.HS512, WebSecurityConfig.SIGNING_KEY)
                    //采用什么算法是可以自己选择的，不一定非要采用HS512
                    .compact();
            // 登录成功后，返回token到header里面
            response.addHeader("Authorization", "Bearer " + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = request.getParameter(this.usernameParameter);
        if (username == null) {
            throw new AuthenticationServiceException("用户名不能为空。");
        }
        return username.trim();
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = request.getParameter(this.passwordParameter);
        if (password == null) {
            throw new AuthenticationServiceException("密码不能为空。");
        }
        return password;
    }

    private String obtainLoginMode(HttpServletRequest request) {
        String loginMode = request.getParameter(this.loginModeParameter);
        if (loginMode == null) {
            throw new AuthenticationServiceException("登录方式不能为空。");
        }
        return loginMode;
    }
}
