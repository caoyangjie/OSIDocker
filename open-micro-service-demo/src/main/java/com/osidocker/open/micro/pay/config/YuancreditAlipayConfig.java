/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.config;

import com.osidocker.open.micro.config.PayPropertiesConfig;
import com.osidocker.open.micro.pay.api.YuancreditPayConfig;

import java.io.InputStream;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @author  caoyangjie
 * @类修改者： 曹杨杰
 * @创建日期： 创建于14:17 2017/7/7
 * @修改说明：
 * @修改日期： 修改于14:17 2017/7/7
 * @版本号： V1.0.0
 */
public class YuancreditAlipayConfig implements YuancreditPayConfig {

    private PayPropertiesConfig config;

    public YuancreditAlipayConfig(PayPropertiesConfig config){
        this.config = config;
    }

    @Override
    public String getAppID() {
        return config.getAliAppid();
    }

    @Override
    public String getMchID() {
        return config.getAliMchid();
    }

    @Override
    public String getKey() {
        return config.getAliKey();
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return config.getConnectTimeOutMs();
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return config.getReadTimeOutMs();
    }

    @Override
    public String getAliPublicKey() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs0/LjQbXJIpaGWoQ4/0UXbNvxhbTZyoY5eyLOxHw8v/FfCj/PM1d3K5HvLXECoSxCbaFyRLaCMBn4n254D8fJcALNnfYdqxwK11bEY77TFTOAstFtHbeUZlnr6Ei0mtgHjWnK/De4TFvO3uHV4hdfZ6KE7hcM5Zq07RX7YSrjhD6ZAPYe5/7r6ceisZb9H+RWA9SRyKo0EKTEaL/1VWR0Znqp8w/eYznkgx/8KcGLu3QxExNMyxZELIOqQwe4M2KT7PVNTGTgcnQrAKdomkj+nFWaGmYekmADA0Gz7us51uACGO50gAq4ssWiKnzqP2rqpPo23YplY2n7HRbuzoSLwIDAQAB";
    }
}
