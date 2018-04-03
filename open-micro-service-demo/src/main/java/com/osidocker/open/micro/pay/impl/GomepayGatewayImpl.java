/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.osidocker.open.micro.pay.api.YuancreditPayConfig;
import com.osidocker.open.micro.pay.api.YuancreditPayGateway;
import com.osidocker.open.micro.pay.entity.PayOrder;
import com.osidocker.open.micro.pay.enums.PayWayEnums;
import com.osidocker.open.micro.pay.exceptions.PayException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于11:29 2017/12/15
 * @修改说明：
 * @修改日期： 修改于11:29 2017/12/15
 * @版本号： V1.0.0
 */
@Service("gomepayGateway")
public class GomepayGatewayImpl extends YuancreditPayGateway{
    @Override
    public String getPayName() {
        return PayWayEnums.gome_pay.getDbValue();
    }

    @Override
    public Map<String, String> payResultNotice(HttpServletRequest request) throws PayException {
        return null;
    }

    @Override
    protected Map<String, String> queryOrderStatus(PayOrder order) {
        return null;
    }

    @Override
    protected Map<String, Object> createOrder(PayOrder order) {
        return null;
    }

    @Override
    protected YuancreditPayConfig initConfig() {
        return null;
    }
}
