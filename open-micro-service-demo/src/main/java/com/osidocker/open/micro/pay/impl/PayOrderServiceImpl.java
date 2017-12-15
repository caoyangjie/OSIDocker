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
import com.osidocker.open.micro.config.PropertiesConfig;
import com.osidocker.open.micro.pay.api.ApiOrderService;
import com.osidocker.open.micro.pay.api.ApiPayOrderService;
import com.osidocker.open.micro.pay.entity.PayOrder;
import com.osidocker.open.micro.pay.enums.OrderStatusEnums;
import com.osidocker.open.micro.pay.enums.PayWayEnums;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.mapper.PayOrderMapper;
import com.osidocker.open.micro.pay.vos.TransOrderBase;
import com.osidocker.open.micro.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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

    public static final String COMPANY_ID = "company_id";
    public static final String DISCOUNT_PRICE = "discount_price";
    public static final String PAY_URL = "payUrl";
    public static final String ORDER_ID = "orderId";
    public static final String PAY_WAY = "payWay";
    @Autowired
    private PropertiesConfig config;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private ApiOrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrder createOrder(TransOrderBase context) {
        Map<String,Object> order =  orderService.getOrderInfo(context.getOrderId(),null);
        Optional.ofNullable(order).orElseThrow(()->new PayException("获取支付订单信息失败!"));
        PayOrder payOrder = new PayOrder();
        String  payWay = PayWayEnums.getEnum(context.getPayWayCode()).getDbValue();
        payOrder.setOrderNo(DataUtils.getOrderNo());
        payOrder.setCompanyId(String.valueOf(order.get(COMPANY_ID)));
        payOrder.setProductName(context.getProductName());
        payOrder.setOrderPrice(String.valueOf(order.get(DISCOUNT_PRICE)));
        payOrder.setPayWayCode(payWay);
        payOrder.setPayType(context.getPayType());
        payOrder.setOrderIp(context.getOrderIp());
        payOrder.setOrderPeriod(30L);
        payOrder.setNotifyUrl(config.getNotifyUrl()+payWay);
        payOrder.setReturnUrl(config.getReturnUrl()+"?t="+DataUtils.getTimeStamp());
        payOrder.setOrderId(context.getOrderId());
        payOrder.setOpenId(context.getOpenId());
        payOrder.setRemark(context.getRemark());
        int row = payOrderMapper.createPayOrder(payOrder);
        if(row > 0){
            orderService.updOrderStatus(context.getOrderId(), OrderStatusEnums.NEEDPAY.getStatus());
            return payOrder;
        }
        return null;
    }

    @Override
    public PayOrder queryOrder(String orderNo) {
        PayOrder yuancreditOrder = payOrderMapper.queryOrder(orderNo);
        return yuancreditOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updPayCodeUrl(String orderId, String payUrl,String payWay) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PAY_URL,payUrl);
        Map<String,Object> map = new HashMap<>();
        map.put(PAY_URL,jsonObject.toJSONString());
        map.put(ORDER_ID,orderId);
        map.put(PAY_WAY,payWay);
        return  payOrderMapper.updPayCodeUrl(map);
    }

    @Override
    public String getOrderId(String orderNo) {
        return payOrderMapper.getOrderId(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updOrderOutTradeNo(String orderNo, String outTradeNo) {
        return payOrderMapper.updOrderOutTradeNo(orderNo,outTradeNo);
    }

    @Override
    public Map<String,Object> getPayOrder(String orderId, String payWay,String payType) {
        Map<String,Object> map = payOrderMapper.getPayOrder(orderId,payWay,payType);
        return map;
    }

    @Override
    public String queryOrderStatus(String orderId) {
        return payOrderMapper.queryOrderStatus(orderId);
    }
}
