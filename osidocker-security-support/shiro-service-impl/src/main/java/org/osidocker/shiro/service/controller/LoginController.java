package org.osidocker.shiro.service.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by cdyoue on 2016/10/21.
 * 登陆控制器
 */
@Controller
public class LoginController
{
    private Logger logger =  LoggerFactory.getLogger(this.getClass());
    
	@RequestMapping({"/","/index"})
	public String index(){
		return "/index";
	}
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(){
		return "login";
	}

    @RequestMapping(value = "/login",method= RequestMethod.POST)
    public String login(
            @RequestParam(value = "username",required = true)String userName,
            @RequestParam(value = "password",required = true)String password,
            @RequestParam(value = "rememberMe",required = true,defaultValue = "false")boolean rememberMe
    ){
        logger.info("=========="+userName+password+rememberMe);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName,password);
        token.setRememberMe(rememberMe);

        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
//            rediect.addFlashAttribute("errorText", "您的账号或密码输入错误!");
            return "{\"Msg\":\"您的账号或密码输入错误\",\"state\":\"failed\"}";
        }
        return "{\"Msg\":\"登陆成功\",\"state\":\"success\"}";
    }
}
