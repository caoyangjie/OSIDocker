/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.api;

import com.osidocker.open.micro.config.PropertiesConfig;
import com.osidocker.open.micro.pay.entity.PayOrder;
import com.osidocker.open.micro.pay.enums.OrderStatusEnums;
import com.osidocker.open.micro.pay.enums.PayTypeEnums;
import com.osidocker.open.micro.pay.enums.PayWayEnums;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.impl.BasePayService;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.pay.vos.TransOrderBase;
import com.osidocker.open.micro.utils.JsonTools;
import com.osidocker.open.micro.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @author  caoyangjie
 * @类修改者： 曹杨杰
 * @创建日期： 创建于10:34 2017/7/7
 * @修改说明：
 * @修改日期： 修改于10:34 2017/7/7
 * @版本号： V1.0.0
 */
public abstract class YuancreditPayGateway extends BasePayService implements ApiPayGateway {
    public static final String PAY_STATUS = "pay_status";
    public static final String STATUS = "status";
    public static final String TIMEOUT = "timeout";
    public static final String ORDER_NO = "order_no";
    public static final String TRADE_STATE = "trade_state";
    public static final String TRADE_STATUS = "trade_status";
    public static final String SUCCESS = "SUCCESS";
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
    public static final String PAY_URL = "payUrl";
    public static final String OUT_TRADE_NO = "out_trade_no";
    public static final String RESULT_CODE = "result_code";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String TRADE_NO = "trade_no";
    public static final String FIELD_1 = "field1";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected ApiPayOrderService payOrderService;

    @Autowired
    protected ApiIdempotencyService<Map> idempotencyService;

    @Autowired
    protected ApiOrderService orderService;

    @Autowired
    private PropertiesConfig config;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ApiResponse execute(TransOrderBase orderInfo){
        //检测数据
        if(orderInfo.checkTransData()){
            ApiResponse apiResponse = orderService.getOrderInfo(orderInfo.getOrderId());
            Map<String,Object> map = apiResponse.getRspData();
            Optional.ofNullable(map).orElseThrow(()->new PayException("无法获取订单信息!"));
            Map<String,Object> result = new HashMap<>();
            // 支付状态为待支付
            if(map.get(PAY_STATUS).equals(OrderStatusEnums.NEEDPAY.getStatus())){
                result = pendingPay(orderInfo);
            }
            // 订单为初始化状态
            else if(map.get(PAY_STATUS).equals(OrderStatusEnums.INIT.getStatus())){
                result = createOrder(orderInfo);
                result.put(STATUS,"0");
            }else{
                throw new PayException("订单支付状态不正确!");
            }
            return buildSuccMap(result);
        }
        throw new PayException("请求数据校验失败!");
    }


    /**
     * 支付为待支付状态
     * @param orderInfo
     * @return
     */
    public  Map<String,Object> pendingPay(TransOrderBase orderInfo){
        Map<String,Object> result = new HashMap<>();
        // 支付订单信息
        Map<String,Object> payOrder = payOrderService.getPayOrder(orderInfo.getOrderId(), PayWayEnums.getEnum(orderInfo.getPayWayCode()).getDbValue(),orderInfo.getPayType());
        if(StringUtil.isEmpty(payOrder)){
            result = createOrder(orderInfo);
        }else{
            int timeout = Integer.parseInt(payOrder.get(TIMEOUT)+"");
            //查询第三方支付订单
            Map<String,String>  queryOrder = queryOrder(payOrder.get(ORDER_NO)+"");
            //微信订单
            if(queryOrder.containsKey(TRADE_STATE)){
                if(queryOrder.get(TRADE_STATE).equals(SUCCESS)){
                    noticeOrder(queryOrder);
                    result.put(STATUS,"1");
                }else{
                    result = getPayUrl(orderInfo,timeout,payOrder);
                }
            //支付宝订单
            }else if(queryOrder.containsKey(TRADE_STATUS)){
                if(!StringUtil.isEmpty(queryOrder.get(TRADE_STATUS))){
                    if(queryOrder.get(TRADE_STATUS).equals(TRADE_SUCCESS) || queryOrder.get(TRADE_STATUS).equals(TRADE_FINISHED)){
                        noticeOrder(queryOrder);
                        result.put(STATUS,"1");
                    }else {
                        result =getPayUrl(orderInfo,timeout,payOrder);
                    }
                }else {
                    result =getPayUrl(orderInfo,timeout,payOrder);
                }
            }
        }
        return  result;
    }

    /**
     * 创建支付订单
     * @param orderInfo
     *
     * @return
     */
    public Map<String,Object> createOrder(TransOrderBase orderInfo){
        PayOrder order = Optional.ofNullable(payOrderService.createOrder(orderInfo)).orElseThrow(()->new PayException("创建支付订单失败!"));
        //创建系统订单
        Map<String,Object> result = Optional.ofNullable(createOrder(order)).orElseThrow(()->new PayException("调用第三方创建订单接口失败!"));
        String payUrl = result.get(PAY_URL)+"";
        if(!StringUtil.isEmpty(payUrl)){
            if(orderInfo.getPayType().equals(PayTypeEnums.JSAPI.getDbValue())){
                payOrderService.updPayCodeUrl(order.getOrderId(), JsonTools.toJson(result),order.getPayWayCode());
            }else {
                // 更新支付二维码
                payOrderService.updPayCodeUrl(order.getOrderId(),result.get(PAY_URL)+"",order.getPayWayCode());
            }
        }else{
            //支付二维码生成失败
            new PayException("创建支付二维码失败!");
        }
        return  result;
    }

    /**
     * 获取支付二维码及路径
     * @param orderInfo
     * @param timeout
     * @param payOrder
     * @return
     */

    public Map<String,Object> getPayUrl(TransOrderBase orderInfo,int timeout,Map<String,Object> payOrder){
        Map<String,Object> result = new HashMap<>();
        // 电脑端支付
        if(orderInfo.getPayType().equals(PayTypeEnums.PC.getDbValue())){
            if(timeout >= config.getPayTimeOut()){
                result = createOrder(orderInfo);
            }else {
                result.put(PAY_URL, JsonTools.jsonStr2Map(payOrder.get(FIELD_1)+"").get(PAY_URL));
            }
        }
        // 手机网站支付
        else if(orderInfo.getPayType().equals(PayTypeEnums.WEB.getDbValue())){
            // 如果为支付宝支付
            if(orderInfo.getPayWayCode() == PayWayEnums.ali_pay.getData()){
                if(timeout >= config.getPayTimeOut()){
                    result = createOrder(orderInfo);
                }else {
                    result.put(PAY_URL, JsonTools.jsonStr2Map(payOrder.get(FIELD_1)+"").get(PAY_URL));
                }
            }else {
                result = createOrder(orderInfo);
            }
        }
        // 公众号支付
        else if(orderInfo.getPayType().equals(PayTypeEnums.JSAPI.getDbValue())){
             result = createOrder(orderInfo);
        }
        result.put(STATUS,"0");
        return result;
    }

    @Override
    public Map<String,Object> payAgain(String orderNo) {
        return null;
    }

    @Override
    public Map<String,String> queryOrder(String orderNo) {
        PayOrder yuancreditOrder = Optional.ofNullable(payOrderService.queryOrder(orderNo)).orElseThrow(()->new PayException("查询支付订单记录失败!"));
        Map<String,String> map = null;
        if(checkOrderStatus(yuancreditOrder)){
            map =queryOrderStatus(yuancreditOrder);
        }
        return map;
    }

    protected boolean checkOrderStatus(PayOrder payOrder){
        if(payOrder.getOrderStatus().equals(OrderStatusEnums.FAIL)||payOrder.getOrderStatus().equals(OrderStatusEnums.NEEDPAY)){
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean noticeOrder(Map<String, String> context) {
        //幂等性判断
        if(idempotencyService.query(context)!=null){
            logger.info("支付回调参数："+context);
            //执行购买成功后回调操作代码
            String orderNo = context.get(OUT_TRADE_NO);
            String orderId = payOrderService.getOrderId(orderNo);
            if(!StringUtil.isEmpty(orderId)){
                //微信支付
                if(context.containsKey(RESULT_CODE)){
                    String result_code = context.get(RESULT_CODE);
                    String transaction_id = context.get(TRANSACTION_ID);
                    if (result_code.equals(SUCCESS)) {
                        // 更新支付订单号
                        payOrderService.updOrderOutTradeNo(orderNo,transaction_id);
                        updProductQuantity(orderId);
                    } else {
                        //更新订单状态
                        orderService.updOrderStatus(orderId,OrderStatusEnums.FAIL.getStatus());
                    }
                //支付宝支付
                }else if(context.containsKey(TRADE_STATUS)){
                    String trade_status = context.get(TRADE_STATUS);
                    String trade_no = context.get(TRADE_NO);
                    if(trade_status.equals(TRADE_SUCCESS) || trade_status.equals(TRADE_FINISHED)){
                        // 更新支付订单号
                        payOrderService.updOrderOutTradeNo(orderNo,trade_no);
                        updProductQuantity(orderId);
                    }else{
                        //更新订单状态
                        orderService.updOrderStatus(orderId,OrderStatusEnums.FAIL.getStatus());
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 更新产品数量及支付状态
     * @param orderId
     */
    public void updProductQuantity(String orderId){
        //更新订单状态
        orderService.updOrderStatus(orderId,OrderStatusEnums.SUCCESS.getStatus());
    }


    protected String buildPayUrl(String qrCode,String orderNo){
        //TODO 实现获取支付二维码地址路径返回
        return "";
    }

    protected abstract Map<String, String> queryOrderStatus(PayOrder order);

    protected abstract Map<String, Object> createOrder(PayOrder order);

    protected abstract YuancreditPayConfig initConfig();
}
