/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.controllers.pay;

import com.osidocker.open.micro.config.PayPropertiesConfig;
import com.osidocker.open.micro.controllers.CoreController;
import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.pay.api.ApiWexinService;
import com.osidocker.open.micro.pay.entity.AccessToken;
import com.osidocker.open.micro.pay.entity.WeXinUserInfo;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.service.LsbAllService;
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
@RequestMapping("/wexin")
public class WxAuthorizeController extends CoreController {

    private static final String APP_REDIRECT_URI = "您的网站地址跳转地址";
    public static final String HTTPS_WX_USERBASE = "https://app.moledata.cn/creditmole/wexin/userbase";
    public static final String CODE = "code";
    public static final String STATE = "state";
    public static final String OPEN_ID = "openId";

    @Autowired
    PayPropertiesConfig config;

    @Autowired
    @Qualifier("wexinService")
    ApiWexinService wexinService;

    @Autowired
    LsbAllService lsbAllService;
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
        String code = request.getParameter(CODE);
        // 获取网页授权access_token
        AccessToken weixinOauthToken =  getWexinService(version()).getAccessToken(config.getWxAppid(), config.getWxSecret(), code);
        // 获取用户信息
        WeXinUserInfo userInfo =  getWexinService(version()).getUserInfo(weixinOauthToken.getAccessToken(), weixinOauthToken.getOpenId());
        //存储微信用户信息
        wexinService.saveWeXinUserInfo(userInfo);
    }

    @RequestMapping(value = "/userbase",method = {RequestMethod.POST,RequestMethod.GET})
    public void doGetUserBase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter(CODE);
        String dispatchPageURL = request.getParameter(STATE);
        logger.info("获取跳转页面路径："+dispatchPageURL);
        logger.info("获取code："+code);
         //获取网页授权access_token
        AccessToken accessToken =  getWexinService(version()).getAccessToken(config.getWxAppid(), config.getWxSecret(), code);
        logger.info("获取openId："+ accessToken.getOpenId());
        request.getSession().setAttribute(OPEN_ID,accessToken.getOpenId());
        ShowUserEntity user = lsbAllService.getUserInfo(accessToken.getOpenId());
        request.getSession().setAttribute("userInfo",user);
        response.sendRedirect(APP_REDIRECT_URI+dispatchPageURL+"?t="+ DataUtils.getTimeStamp());
    }

    @RequestMapping(value = "/openId",method = {RequestMethod.POST,RequestMethod.GET})
    public ApiResponse doGetOpenId(@RequestParam String dispatchPageURL, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String openId =(String) request.getSession().getAttribute(OPEN_ID);
        //是否需要发起授权请求,0代表需要,1代表不需要
        String getOpenIdFlag = StringUtil.isEmpty(openId)?"0":"1";
        // 后端拼装前端发起授权请求的请求参数
        return getWexinService(version()).getOauthPageUrl(config.getWxAppid(),HTTPS_WX_USERBASE,1, dispatchPageURL,getOpenIdFlag);
    }
}
