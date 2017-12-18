/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.mapper;

import com.osidocker.open.micro.pay.entity.PayOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/8/1 9:58
 * @修改说明：
 * @修改日期： 修改于 2017/8/1 9:58
 * @版本号： V1.0.0
 */
public interface PayOrderMapper {
    /**
     * 创建内部支付订单
     * @param yuancreditOrder 订单信息
     * @return
     */
    int createPayOrder(PayOrder yuancreditOrder);

    /**
     * 更新支付地址
     * @param map
     * @return
     */
    int updPayCodeUrl(@Param("map") Map<String, Object> map);

    /**
     * 根据订单编号获取订单Id
     * @param orderNo
     * @return
     */
    String getOrderId(@Param("orderNo") String orderNo);

    /**
     * 根据订单编号获取订单信息
     * @param orderNo
     * @return
     */
    PayOrder queryOrder(@Param("orderNo") String orderNo);

    /**
     * 更新订单外部流水号
     * @param orderNo
     * @param outTradeNo
     * @param payStatus
     * @return
     */
    int updOrderOutTradeNo(@Param("orderNo") String orderNo, @Param("outTradeNo") String outTradeNo, @Param("payStatus") String payStatus);

    /**
     * 根据请求参数获取订单信息
     * @param orderId 订单Id
     * @param payWay 下单方式
     * @param payType 下单类型
     * @return
     */
    List<Map<String,Object>> getPayOrder(@Param("orderId") String orderId, @Param("payWay") String payWay, @Param("payType") String payType);

    /**
     * 跟进订单Id获取订单信息
     * @param orderId
     * @return
     */
    List<Map<String,Object>> getPayOrderById(@Param("orderId") String orderId);

    /**
     * 获取未支付的订单
     * @return
     */
    List<Map<String,String>> getOrderByNotPay();

    /**
     * 根据订单Id获取订单状态
     * @param orderId 订单id
     * @return
     */
    String queryOrderStatus(@Param("orderId") String orderId);
}
