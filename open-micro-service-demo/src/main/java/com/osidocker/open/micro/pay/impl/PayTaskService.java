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
import com.osidocker.open.micro.pay.enums.PayWayEnums;
import com.osidocker.open.micro.pay.mapper.PayOrderMapper;
import com.osidocker.open.micro.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @author  caoyangjie
 * @创建日期： 创建于 2017/8/9 9:13
 * @修改说明：
 * @修改日期： 修改于 2017/8/9 9:13
 * @版本号： V1.0.0
 */
@Component("task")
public class PayTaskService {
    public static final String ORDER_ID = "order_id";
    public static final String PAY_WAY = "payWay";
    public static final String ORDER_NO = "order_no";
    public static final String TRADE_STATE = "trade_state";
    public static final String SUCCESS = "SUCCESS_CODE";
    public static final String TRADE_STATUS = "trade_status";
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
    private Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    @Qualifier("alipayGateway")
    private ApiPayGateway alipayGateway;

    @Autowired
    @Qualifier("wxPayGateway")
    private ApiPayGateway wxPayGateway;

    @Autowired
    private ApiOrderService orderService;

    @Scheduled(cron="0 0/3 * * * ? ")   //每3分钟执行一次
    @Transactional(rollbackFor = Exception.class)
    public void updPayOrder(){
        List<Map<String,String>> list = payOrderMapper.getOrderByNotPay();
        list.stream().forEach(map -> {
            String orderId = map.get(ORDER_ID);
            List<Map<String,Object>> payOrderList = payOrderMapper.getPayOrderById(orderId);
            int index = 0;
            if(!StringUtil.isEmpty(payOrderList)){
                for (int i=0;i<payOrderList.size();i++){
                    String payWay = payOrderList.get(i).get(PAY_WAY)+"";
                    String orderNo = payOrderList.get(i).get(ORDER_NO)+"";
                    // 微信支付
                    logger.info("查询第三方订单接口："+payWay);
                    if(payWay.equals(PayWayEnums.weixin_pay.getDbValue())){
                        Map<String,String> queryOrder = wxPayGateway.queryOrder(orderNo);
                        if(!StringUtil.isEmpty(queryOrder)){
                             if(queryOrder.containsKey(TRADE_STATE)) {
                                if(queryOrder.get(TRADE_STATE).equals(SUCCESS)){
                                    boolean flag = wxPayGateway.noticeOrder(queryOrder);
                                    if(flag){
                                        index++;
                                    }
                                    log(flag,orderId);
                                }
                            }
                        }
                    }
                    // 支付宝支付
                    else if(payWay.equals(PayWayEnums.ali_pay.getDbValue())){
                        Map<String,String> queryOrder =  alipayGateway.queryOrder(orderNo);
                        if(!StringUtil.isEmpty(queryOrder)){
                            if (!StringUtil.isEmpty(queryOrder.get(TRADE_STATUS))) {
                                if (queryOrder.get(TRADE_STATUS).equals(TRADE_SUCCESS) || queryOrder.get(TRADE_STATUS).equals(TRADE_FINISHED)) {
                                    boolean flag =  alipayGateway.noticeOrder(queryOrder);
                                    if(flag){
                                        index++;
                                    }
                                    log(flag,orderId);
                                }
                            }
                        }
                    }
                }
                if(index == 0){
                    cancelOrder(orderId);
                }
            }
        });
    }

    public void cancelOrder(String orderId){
        int row =  orderService.orderFailed(orderId);
        if(row > 0){
            logger.info("订单号："+orderId+"#更新为交易失败成功!");
        }else {
            logger.info("订单号："+orderId+"#更新为交易失败失败!");
        }
    }

    public void log(boolean flag,String orderId){
        if(flag){
            logger.info("订单号："+orderId+"#订单状态及产品数量更新成功!");
        }else {
            logger.info("订单号："+orderId+"#订单状态及产品数量更新失败!");
        }
    }

}
