/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.pay.api.ApiWexinService;
import com.osidocker.open.micro.pay.entity.AccessToken;
import com.osidocker.open.micro.pay.entity.WeXinUserInfo;
import com.osidocker.open.micro.pay.enums.ScopeEnum;
import com.osidocker.open.micro.pay.mapper.WeXinMapper;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.utils.CommonUtil;
import com.osidocker.open.micro.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @author  caoyangjie
 * @创建日期： 创建于 2017/8/29 15:45
 * @修改说明：
 * @修改日期： 修改于 2017/8/29 15:45
 * @版本号： V1.0.0
 */
@Service("wexinService")
public class WexinServiceImpl extends BasePayService implements ApiWexinService {
    public static final String GET = "GET";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String OPENID = "openid";
    public static final String SCOPE = "scope";
    public static final String ERRCODE = "errcode";
    public static final String ERRMSG = "errmsg";
    private static Logger log = LoggerFactory.getLogger(WexinServiceImpl.class);

    @Autowired
    WeXinMapper weXinMapper;

    @Override
    public AccessToken getAccessToken(String appId, String appSecret, String code) {
        AccessToken wat = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        requestUrl = requestUrl.replace("APPID", appId);
        requestUrl = requestUrl.replace("SECRET", appSecret);
        requestUrl = requestUrl.replace("CODE", code);
        // 获取网页授权凭证
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, GET, null);
        if (null != jsonObject) {
            try {
                wat = new AccessToken();
                wat.setAccessToken(jsonObject.getString(ACCESS_TOKEN));
                wat.setExpiresIn(jsonObject.getIntValue(EXPIRES_IN));
                wat.setRefreshToken(jsonObject.getString(REFRESH_TOKEN));
                wat.setOpenId(jsonObject.getString(OPENID));
                wat.setScope(jsonObject.getString(SCOPE));
            } catch (Exception e) {
                wat = null;
                int errorCode = jsonObject.getIntValue(ERRCODE);
                String errorMsg = jsonObject.getString(ERRMSG);
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return wat;
    }

    @Override
    public WeXinUserInfo getUserInfoByOpenId(String openId) {
        return weXinMapper.getWexinUserInfoByOpenId(openId);
    }

    @Override
    public WeXinUserInfo getUserInfo(String accessToken, String openId) {
        WeXinUserInfo userInfo = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // 通过网页授权获取用户信息
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                userInfo = new WeXinUserInfo();
                // 用户的标识
                userInfo.setOpenId(jsonObject.getString("openid"));
                // 昵称
                userInfo.setNickname(jsonObject.getString("nickname"));
                // 性别（1是男性，2是女性，0是未知）
                userInfo.setSex(jsonObject.getIntValue("sex"));
                // 用户所在国家
                userInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                userInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                userInfo.setCity(jsonObject.getString("city"));
                // 用户头像
                userInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
                // 用户特权信息
                userInfo.setPrivilege(jsonObject.getString("privilege"));
            } catch (Exception e) {
                userInfo = null;
                int errorCode = jsonObject.getIntValue("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return userInfo;
    }

    @Override
    public ApiResponse getOauthPageUrl(String appid, String redirectUrl, int scope, String dispatchPageURL, String getOpenIdFlag) {
        if(StringUtil.isEmpty(redirectUrl)){
            throw new NullPointerException("redirectUrl is null");
        }
        String url = null;
        try {
            url = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer stringBuffer = new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize?");
            stringBuffer.append("appid=").append(appid).append("&redirect_uri=").append(url).append("&response_type=code&scope=")
                    .append(ScopeEnum.getEnum(scope).getDbValue()).append("&state=").append(dispatchPageURL).append("#wechat_redirect");
        Map<String,Object> map = new HashMap<>();
        map.put("url",stringBuffer.toString());
        map.put("getOpenIdFlag",getOpenIdFlag);
        return buildSuccMap(map);
    }

    @Override
    public ApiResponse getOpenId(long userId) {
        Map<String,Object> map=weXinMapper.getOpenId(userId);
        if(StringUtil.isEmpty(map)){
            return buildFail("100001");
        }
        return buildSuccMap(map);
    }

    @Override
    public int addWeXinUser(String openId,String userId) {
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("openId",openId);
        return weXinMapper.addWeXinUser(map);
    }

    @Override
    public int saveWeXinUserInfo(WeXinUserInfo userInfo) {
        return weXinMapper.saveWeXinUserInfo(userInfo);
    }
}
