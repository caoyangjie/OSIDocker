/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.osidocker.open.micro.pay.api.ApiOrderService;
import com.osidocker.open.micro.pay.api.ApiPayGateway;
import com.osidocker.open.micro.pay.api.ApiQueryOrderService;
import com.osidocker.open.micro.pay.enums.OrderStatusEnums;
import com.osidocker.open.micro.pay.enums.PayWayEnums;
import com.osidocker.open.micro.pay.mapper.PayOrderMapper;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.pay.vos.QueryOrder;
import com.osidocker.open.micro.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.osidocker.open.micro.pay.api.YuancreditPayGateway.OUT_TRADE_NO;
import static com.osidocker.open.micro.pay.api.YuancreditPayGateway.TRADE_NO;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/8/9 18:25
 * @修改说明：
 * @修改日期： 修改于 2017/8/9 18:25
 * @版本号： V1.0.0
 */
@Service("queryOrderService")
public class QueryOrderServiceImpl extends BasePayService implements ApiQueryOrderService {

    public static final String PAY_WAY = "payWay";
    public static final String ORDER_NO = "order_no";
    public static final String TRADE_STATE = "trade_state";
    public static final String SUCCESS = "SUCCESS";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String TRADE_STATUS = "trade_status";
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    @Qualifier("alipayGateway")
    protected ApiPayGateway alipayGateway;

    @Autowired
    @Qualifier("wxPayGateway")
    protected ApiPayGateway wxPayGateway;

    @Autowired
    private ApiOrderService orderService;

    @Override
    @Transactional
    public ApiResponse getQueryOrder(QueryOrder queryOrder) {
        List<Map<String,Object>> list = payOrderMapper.getPayOrderById(queryOrder.getOrderId());
        int index = 0;
        if(!StringUtil.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                String payWay = list.get(i).get(PAY_WAY) + "";
                String orderNo = list.get(i).get(ORDER_NO) + "";
                // 微信支付
                if (payWay.equals(PayWayEnums.weixin_pay.getDbValue())) {
                    Map<String, String> payOrder = wxPayGateway.queryOrder(orderNo);
                    if (!StringUtil.isEmpty(payOrder)) {
                        if (payOrder.get(TRADE_STATE).equals(SUCCESS)) {
                            String transaction_id = payOrder.get(TRANSACTION_ID);
                            boolean flag = updateOrder(payOrder, transaction_id);
                            if(flag){
                                index++;
                            }
                        }
                    }
                }
                // 支付宝支付
                else if (payWay.equals(PayWayEnums.ali_pay.getDbValue())) {
                    Map<String, String> payOrder = alipayGateway.queryOrder(orderNo);
                    if (!StringUtil.isEmpty(payOrder)) {
                        if (!StringUtil.isEmpty(payOrder.get(TRADE_STATUS))) {
                            if (payOrder.get(TRADE_STATUS).equals(TRADE_SUCCESS) || payOrder.get(TRADE_STATUS).equals(TRADE_FINISHED)) {
                                String trade_no = payOrder.get(TRADE_NO);
                                boolean flag = updateOrder(payOrder, trade_no);
                                if(flag){
                                    index++;
                                }
                            }
                        }
                    }
                }
            }
            if(index > 0){
                return  buildSuccess();
            }else {
                return buildFail("100008");
            }
        }
        return buildFail("100003");
    }


    /**
     * 更新产品支付状态
     * @param map
     */
    protected boolean updateOrder(Map<String,String> map,String outTradeNo){
        String orderNo = map.get(OUT_TRADE_NO);
        String orderId = payOrderMapper.getOrderId(orderNo);
        if(!StringUtil.isEmpty(orderId)){
            String status = payOrderMapper.queryOrderStatus(orderId);
            //当前订单为待支付或订单失败
            if(status.equals(OrderStatusEnums.NEEDPAY.getStatus()) || status.equals(OrderStatusEnums.FAIL.getStatus())){
                payOrderMapper.updOrderOutTradeNo(orderNo,outTradeNo);
                //更新订单状态
               int  row = orderService.updOrderStatus(orderId, OrderStatusEnums.SUCCESS.getStatus(),null);
               if(row > 0){
                   //TODO 更新产品数量
               }
            }
        }
        return false;
    }
}
