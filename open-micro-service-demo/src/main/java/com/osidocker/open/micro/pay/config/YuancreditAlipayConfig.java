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

}
