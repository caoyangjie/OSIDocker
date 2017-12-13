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
import com.osidocker.open.micro.pay.entity.UserInfo;
import com.osidocker.open.micro.pay.vos.APIResponse;

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
    UserInfo getUserInfo(String accessToken, String openId);

    /**
     * 公众号授权获取code
     * @param appid 应用Id
     * @param redirectUrl 跳转URL
     * @param scope
     * @param state
     * @param status
     * @return
     */
    APIResponse getOauthPageUrl(String appid, String redirectUrl, int scope, String state, String status);

    // 获取openId

    /**
     * 更加用户Id获取OpenId
     * @param userId
     * @return
     */
    APIResponse getOpenId(long userId);

    /**
     * 添加微信用户
     * @param openId
     * @param userId 用户Id
     * @return
     */
    int addWeXinUser(String openId, String userId);
}
