/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.api;

import com.osidocker.open.micro.pay.entity.YuancreditOrder;
import com.osidocker.open.micro.pay.vos.TransOrderBase;

import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author caoyangjie
 * @创建日期： 创建于10:46 2017/7/7
 * @修改说明：
 * @修改日期： 修改于10:46 2017/7/7
 * @版本号： V1.0.0
 */
public interface ApiPayOrderService {

    public YuancreditOrder createOrder(TransOrderBase orderBase);

    public YuancreditOrder queryOrder(String orderNo);

    public int updPayCodeUrl(String orderNo, String codeUrl, String payWay);

    public String getOrderId(String orderNo);

    public int updOrderOutTradeNo(String orderNo, String outTradeNo);

    public Map<String,Object> getPayOrder(String orderId, String payWay, String payType);

    public String queryOrderStatus(String orderId);

}
