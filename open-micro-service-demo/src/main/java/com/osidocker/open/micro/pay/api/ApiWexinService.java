/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.api;

import com.osidocker.open.micro.pay.entity.AccessToken;
import com.osidocker.open.micro.pay.entity.WeXinUserInfo;
import com.osidocker.open.micro.pay.vos.ApiResponse;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @author caoyangjie
 * @创建日期： 创建于 2017/8/29 14:57
 * @修改说明：
 * @修改日期： 修改于 2017/8/29 14:57
 * @版本号： V1.0.0
 */
public interface ApiWexinService {

    /**
     * 获取accessToken
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    AccessToken getAccessToken(String appId, String appSecret, String code);

    /**
     * 通过网页授权获取用户信息
     * @param accessToken
     * @param openId
     * @return
     */
    WeXinUserInfo getUserInfo(String accessToken, String openId);

    /**
     * 根据openId获取数据库中注册微信用户
     * @param openId
     * @return
     */
    WeXinUserInfo getUserInfoByOpenId(String openId);

    /**
     * 公众号授权获取code
     * @param appid 应用Id
     * @param redirectUrl 跳转URL
     * @param scope   微信需要值
     * @param dispatchPageURL 跳转页面
     * @param getOpenIdFlag 是否需要重新进行授权判断 0 需要 1不需要
     * @return
     */
    ApiResponse getOauthPageUrl(String appid, String redirectUrl, int scope, String dispatchPageURL, String getOpenIdFlag);

    // 获取openId

    /**
     * 更加用户Id获取OpenId
     * @param userId
     * @return
     */
    ApiResponse getOpenId(long userId);

    /**
     * 添加微信用户
     * @param openId
     * @param userId 用户Id
     * @return
     */
    int addWeXinUser(String openId, String userId);

    /**
     * 添加微信用户详细信息
     * @param userInfo 添加用户详细信息
     * @return
     */
    int saveWeXinUserInfo(WeXinUserInfo userInfo);
}
