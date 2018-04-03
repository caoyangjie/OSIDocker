/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.entity;

import java.io.Serializable;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/8/29 14:54
 * @修改说明：
 * @修改日期： 修改于 2017/8/29 14:54
 * @版本号： V1.0.0
 */
public class AccessToken implements Serializable {
    /**
     * 网页授权接口调用凭证
     */
    private String accessToken;
    /**
     * 凭证有效时长
     */
    private int expiresIn;
    /**
     * 用于刷新凭证
     */
    private String refreshToken;
    /**
     * 用户标识
     */
    private String openId;
    /**
     * 用户授权作用域
     */
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
