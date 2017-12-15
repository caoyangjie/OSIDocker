/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/8/29 15:17
 * @修改说明：
 * @修改日期： 修改于 2017/8/29 15:17
 * @版本号： V1.0.0
 */
public class MyX509TrustManager implements X509TrustManager {
    // 检查客户端证书
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    // 检查服务器端证书
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    // 返回受信任的X509证书数组
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
