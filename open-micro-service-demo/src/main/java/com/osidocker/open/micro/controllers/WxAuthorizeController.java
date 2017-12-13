/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.config.PropertiesConfig;
import com.osidocker.open.micro.pay.api.ApiWexinService;
import com.osidocker.open.micro.pay.entity.AccessToken;
import com.osidocker.open.micro.pay.entity.UserInfo;
import com.osidocker.open.micro.pay.vos.APIResponse;
import com.osidocker.open.micro.utils.DataUtils;
import com.osidocker.open.micro.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/8/25 15:08
 * @修改说明：
 * @修改日期： 修改于 2017/8/25 15:08
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/wx")
public class WxAuthorizeController extends CoreController {

    private static final String APP_REDIRECT_URI = "您的网站地址";
    public static final String HTTPS_WX_USERBASE = "https://app.moledata.cn/creditmole/wx/userbase";

    @Autowired
    PropertiesConfig config;

    @Autowired
    @Qualifier("wexinService")
    ApiWexinService wexinService;

    /**
     * 根据请求版本获取服务实现
     * @param version           服务版本
     * @return                  返回服务的实现对象
     */
    private ApiWexinService getWexinService(String version)
    {
        return getServiceBy("wexinService",ApiWexinService.class,version);
    }

    @RequestMapping(value = "/userinfo",method = {RequestMethod.POST,RequestMethod.GET})
    public void doGetUserInfo(HttpServletRequest request, HttpServletResponse response){
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        // 获取网页授权access_token
        AccessToken weixinOauthToken =  getWexinService(version()).getAccessToken(config.getWxAppid(), config.getWxSecret(), code);
        getWexinService(version()).addWeXinUser(weixinOauthToken.getOpenId(),state);
        // 获取用户信息
        UserInfo userInfo =  getWexinService(version()).getUserInfo(weixinOauthToken.getAccessToken(), weixinOauthToken.getOpenId());
    }

    @RequestMapping(value = "/userbase",method = {RequestMethod.POST,RequestMethod.GET})
    public void doGetUserBase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        logger.info("获取state："+state);
        logger.info("获取code："+code);
         //获取网页授权access_token
        AccessToken accessToken =  getWexinService(version()).getAccessToken(config.getWxAppid(), config.getWxSecret(), code);
        logger.info("获取openId："+ accessToken.getOpenId());
        request.getSession().setAttribute("openId",accessToken.getOpenId());
        response.sendRedirect(APP_REDIRECT_URI+state+"?t="+ DataUtils.getTimeStamp());
    }

    @RequestMapping(value = "/openId",method = {RequestMethod.POST,RequestMethod.GET})
    public APIResponse doGetOpenId(@RequestParam String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String openId =(String) request.getSession().getAttribute("openId");
        String status = "0";
        if(!StringUtil.isEmpty(openId)){
            status = "1";
            request.getSession().setAttribute("openId",openId);
        }
        // 授权
        return getWexinService(version()).getOauthPageUrl(config.getWxAppid(),HTTPS_WX_USERBASE,1, state,status);
    }
}
