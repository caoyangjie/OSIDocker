/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.config.PayPropertiesConfig;
import com.osidocker.open.micro.pay.api.ApiOrderService;
import com.osidocker.open.micro.pay.api.ApiPayOrderService;
import com.osidocker.open.micro.pay.entity.PayOrder;
import com.osidocker.open.micro.pay.enums.OrderStatusEnums;
import com.osidocker.open.micro.pay.enums.PayStatusEnum;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.mapper.PayOrderMapper;
import com.osidocker.open.micro.pay.vos.TransOrderBase;
import com.osidocker.open.micro.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.osidocker.open.micro.pay.api.YuancreditPayGateway.STATUS;


/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于17:22 2017/7/7
 * @修改说明：
 * @修改日期： 修改于17:22 2017/7/7
 * @版本号： V1.0.0
 */
@Service("payOrderService")
public class PayOrderServiceImpl implements ApiPayOrderService {

    public static final String PRICE = "pay_price";
    public static final String PAY_URL = "payUrl";
    public static final String ORDER_No = "orderNo";
    public static final String PAY_WAY = "payWay";
    @Autowired
    private PayPropertiesConfig config;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private ApiOrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrder createOrder(TransOrderBase order) {
        Map<String,Object> orderMap =  orderService.getOrderInfo(order.getOrderId(),null);
        Optional.ofNullable(orderMap).orElseThrow(()->new PayException("获取支付订单信息失败!"));
        PayOrder payOrder = new PayOrder();
        String  payWay = order.getPayWayCode();
        payOrder.setOrderNo(DataUtils.getOrderNo());
        payOrder.setProductName(order.getProductName());
        payOrder.setOrderPrice(String.valueOf(order.getPayPrice()));
        payOrder.setPayWayCode(payWay);
        payOrder.setPayType(order.getPayType());
        payOrder.setOrderIp(order.getOrderIp());
        payOrder.setOrderPeriod(30L);
        payOrder.setNotifyUrl(config.getNotifyUrl()+payWay);
        payOrder.setReturnUrl(config.getReturnUrl()+"?t="+DataUtils.getTimeStamp());
        payOrder.setOrderId(order.getOrderId());
        payOrder.setOpenId(order.getOpenId());
        payOrder.setRemark(order.getRemark());
        payOrder.setStatus(PayStatusEnum.INIT.getDbValue());
        int row = payOrderMapper.createPayOrder(payOrder);
        if(row > 0){
            orderService.updOrderStatus(order.getOrderId(), OrderStatusEnums.NEEDPAY.getStatus(),String.valueOf(order.getPayPrice()));
            return payOrder;
        }
        return null;
    }

    @Override
    public PayOrder queryOrder(String orderNo) {
        return payOrderMapper.queryOrder(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updPayCodeUrl(String orderNo, String payUrl,String payWay,PayStatusEnum payStatus) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PAY_URL,payUrl);
        Map<String,Object> map = new HashMap<>();
        map.put(PAY_URL,jsonObject.toJSONString());
        map.put(ORDER_No,orderNo);
        map.put(PAY_WAY,payWay);
        map.put(STATUS,payStatus.getDbValue());
        return  payOrderMapper.updPayCodeUrl(map);
    }

    @Override
    public String getOrderId(String orderNo) {
        return payOrderMapper.getOrderId(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updOrderOutTradeNo(String orderNo, String outTradeNo, PayStatusEnum paysuccess) {
        return payOrderMapper.updOrderOutTradeNo(orderNo,outTradeNo,paysuccess.getDbValue());
    }

    @Override
    public List<Map<String,Object>> getPayOrder(String orderId, String payWay, String payType) {
        return payOrderMapper.getPayOrder(orderId,payWay,payType);
    }

    @Override
    public String queryOrderStatus(String orderId) {
        return payOrderMapper.queryOrderStatus(orderId);
    }
}
