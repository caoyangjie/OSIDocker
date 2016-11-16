package org.osidocker.open.web.controller;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.osidocker.open.entity.UserInfo;
import org.osidocker.open.utils.LocaleMessageSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wordnik.swagger.annotations.ApiOperation;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	protected LocaleMessageSourceService message;
	
	@RequestMapping({"/","/index"})
	public String index(){
		return "/index";
	}
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	@ApiOperation(value="登录-login",notes="执行登录")
	public String login(){
		return "/login";
	}
	
	@RequestMapping(value="/userInfo",method=RequestMethod.GET)
	@ApiOperation(value="用户信息-userInfo",notes="执行用户查询")
	@RequiresPermissions("userInfo:view")//权限管理;
	public String userInfo(){
		return "userInfo";
	}
	
	@RequestMapping(value="/userInfoAdd",method=RequestMethod.GET)
	@ApiOperation(value="用户新增-userInfoAdd",notes="执行用户新增")
	@RequiresPermissions("userInfo:add")//权限管理;
	public String userInfoAdd(){
		return "userInfoAdd";
	}
	
	@RequestMapping(value="/userInfoDel",method=RequestMethod.GET)
	@ApiOperation(value="用户删除-userInfoDel",notes="执行删除用户")
	@RequiresPermissions("userInfo:del")//权限管理;
	public String userInfoDel(){
		return "userInfoDel";
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
    public @ResponseBody String login(@Valid UserInfo user,boolean rememberMe,BindingResult bindingResult,RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "login";
        }

        String username = user.getUsername();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword(),rememberMe);
        //获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        try {  
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查  
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应  
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法 
        	logger.info(message.getMessage("welcome"));
            logger.info("对用户[" + username + "]进行登录验证..验证开始");  
            currentUser.login(token);  
            logger.info("对用户[" + username + "]进行登录验证..验证通过");  
        }catch(UnknownAccountException uae){  
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");  
            redirectAttributes.addFlashAttribute("message", "未知账户");  
        }catch(IncorrectCredentialsException ice){  
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");  
            redirectAttributes.addFlashAttribute("message", "密码不正确");  
        }catch(LockedAccountException lae){  
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");  
            redirectAttributes.addFlashAttribute("message", "账户已锁定");  
        }catch(ExcessiveAttemptsException eae){  
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");  
            redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");  
        }catch(AuthenticationException ae){  
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景  
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");  
            ae.printStackTrace();  
            redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");  
        }  
        //验证是否登录成功  
        if(currentUser.isAuthenticated()){  
            logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");  
            return "{\"state\":\"success\",\"message\",\"登陆成功!\"}";
        }else{  
            token.clear();  
            return "redirect:/login";
        }  
    }
	
}
