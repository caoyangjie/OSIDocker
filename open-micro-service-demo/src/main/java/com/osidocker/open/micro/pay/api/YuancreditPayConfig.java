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
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs0/LjQbXJIpaGWoQ4/0UXbNvxhbTZyoY5eyLOxHw8v/FfCj/PM1d3K5HvLXECoSxCbaFyRLaCMBn4n254D8fJcALNnfYdqxwK11bEY77TFTOAstFtHbeUZlnr6Ei0mtgHjWnK/De4TFvO3uHV4hdfZ6KE7hcM5Zq07RX7YSrjhD6ZAPYe5/7r6ceisZb9H+RWA9SRyKo0EKTEaL/1VWR0Znqp8w/eYznkgx/8KcGLu3QxExNMyxZELIOqQwe4M2KT7PVNTGTgcnQrAKdomkj+nFWaGmYekmADA0Gz7us51uACGO50gAq4ssWiKnzqP2rqpPo23YplY2n7HRbuzoSLwIDAQAB";
    }
}
