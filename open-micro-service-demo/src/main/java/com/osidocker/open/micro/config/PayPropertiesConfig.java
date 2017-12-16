/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @author caoyangjie
 * @创建日期： 创建于 2017/8/11 10:46
 * @修改说明：
 * @修改日期： 修改于 2017/8/11 10:46
 * @版本号： V1.0.0
 */
@Configuration
public class PayPropertiesConfig {
    @Value("${pay.url.notify}")
    private String notifyUrl;

    @Value("${pay.url.return}")
    private String returnUrl;

    @Value("${pay.url.base}")
    private String baseUrl;

    @Value("${pay.ali.appid}")
    private String aliAppid ;

    @Value("${pay.ali.mchid}")
    private String aliMchid;

    @Value("${pay.ali.key}")
    private String aliKey;

    @Value("${pay.wx.appid}")
    private String wxAppid ;

    @Value("${pay.wx.mchid}")
    private String wxMchid;

    @Value("${pay.wx.key}")
    private String wxKey;

    @Value("${pay.wx.secret}")
    private String wxSecret;

    @Value("${pay.time.connect}")
    private int connectTimeOutMs;

    @Value("${pay.time.read}")
    private int readTimeOutMs;

    @Value("${pay.time.out}")
    private int payTimeOut;

    @Value("${pay.path.icon}")
    private String payPathIcon;

    @Value("${pay.path.qrcode}")
    private String payPathQrCode;

    @Value("${pay.path.base}")
    private String payPathBase;

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public int getPayTimeOut() {
        return payTimeOut;
    }

    public String getAliAppid() {
        return aliAppid;
    }

    public String getAliMchid() {
        return aliMchid;
    }

    public String getAliKey() {
        return aliKey;
    }

    public String getWxAppid() {
        return wxAppid;
    }

    public String getWxMchid() {
        return wxMchid;
    }

    public String getWxKey() {
        return wxKey;
    }

    public String getWxSecret() {
        return wxSecret;
    }

    public int getConnectTimeOutMs() {
        return connectTimeOutMs;
    }

    public int getReadTimeOutMs() {
        return readTimeOutMs;
    }

    public String getPayPathIcon() {
        return payPathIcon;
    }

    public String getPayPathQrCode() {
        return payPathQrCode;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPayPathBase() {
        return payPathBase;
    }
}
