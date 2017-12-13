/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.api;

import com.osidocker.open.micro.pay.vos.APIResponse;

import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author caoyangjie
 * @创建日期： 创建于16:57 2017/12/13
 * @修改说明：
 * @修改日期： 修改于16:57 2017/12/13
 * @版本号： V1.0.0
 */
public interface ApiOrderService {
    /**
     * 根据订单ID获取订单信息
     * @param orderId 订单Id
     * @return 返回订单信息
     */
    APIResponse getOrderInfo(String orderId);

    /**
     * 跟进订单id和订单状态获取订单信息
     * @param orderId   订单id
     * @param status    订单状态
     * @return 订单对象
     */
    Map<String,Object> getOrderInfo(String orderId, String status);

    /**
     * 更新订单状态
     * @param orderId 订单id
     * @param status 订单状态
     * @return 返回受影响行数
     */
    int updOrderStatus(String orderId, String status);

    /**
     * 订单失败
     * @param orderId
     * @return
     */
    int orderFailed(String orderId);
}
