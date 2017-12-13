/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.api;

import com.github.wxpay.sdk.WXPayConfig;

import java.io.InputStream;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于14:12 2017/7/7
 * @修改说明：
 * @修改日期： 修改于14:12 2017/7/7
 * @版本号： V1.0.0
 */
public interface YuancreditPayConfig extends WXPayConfig {

    /**
     * 获取 App ID
     *
     * @return App ID
     */
    @Override
    public String getAppID();


    /**
     * 获取 Mch ID
     *
     * @return Mch ID
     */
    @Override
    public String getMchID();


    /**
     * 获取 API 密钥
     *
     * @return API密钥
     */
    @Override
    public String getKey();


    /**
     * 获取商户证书内容
     *
     * @return 商户证书内容
     */
    @Override
    public InputStream getCertStream();

    /**
     * HTTP(S) 连接超时时间，单位毫秒
     *
     * @return
     */
    @Override
    public int getHttpConnectTimeoutMs();

    /**
     * HTTP(S) 读数据超时时间，单位毫秒
     *
     * @return
     */
    @Override
    public int getHttpReadTimeoutMs();



    default String getAliPublicKey(){
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvAgG5knYlPBMPjo2MwKuFDFXsvruDcnA5u9XSuICqSlev280EAqRUhR24ei7/+MwA4JclvXoEKJRn4wPV2Rok4X9J3T/YYjo6vY2r/K4kPY7RD/BotIJTvLDQklK5HfI4A/vX0hY2MBsQI1enU9hJhBeUVwO5YCk/QcplSMiOLZyHzvSND/JzSukcYtVYBXYzjwd6z+NLhSNtRpb1r0sa0CQQPOF6/y0IBq+ekHjHHC/bWxkbYYIOr92w+8clllsIeLPi644gUFXVRKIkG9xdbEeN0s0aFY6vre499ZuoFeVvqddYEoThYhWmZE5nH77itL35V2ZTzLH+GlXXB11TwIDAQAB";
    }
}
