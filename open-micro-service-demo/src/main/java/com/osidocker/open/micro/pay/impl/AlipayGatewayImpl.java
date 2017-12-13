/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.osidocker.open.micro.config.PropertiesConfig;
import com.osidocker.open.micro.pay.api.YuancreditPayConfig;
import com.osidocker.open.micro.pay.api.YuancreditPayGateway;
import com.osidocker.open.micro.pay.config.YuancreditAlipayConfig;
import com.osidocker.open.micro.pay.entity.YuancreditOrder;
import com.osidocker.open.micro.pay.enums.PayTypeEnums;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static java.lang.System.out;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @author caoyangjie
 * @类修改者： 曹杨杰
 * @创建日期： 创建于12:39 2017/7/7
 * @修改说明：
 * @修改日期： 修改于12:39 2017/7/7
 * @版本号： V1.0.0
 */
@Service("alipayGateway")
public class AlipayGatewayImpl extends YuancreditPayGateway {
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String SUB_CODE = "sub_code";
    public static final String SUB_MSG = "sub_msg";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";
    public static final String RSA_2 = "RSA2";
    public static final String FAIL = "fail";
    public static final String JSON = "json";
    public static final String QUICK_WAP_WAY = "QUICK_WAP_WAY";
    @Autowired
    private PropertiesConfig config;

    private AlipayClient client;

    @Override
    public String getPayName() {
        return "aliPay";
    }

    @Override
    public Map<String, Object> createOrder(YuancreditOrder order) throws PayException {
        Map<String,Object> result = null;
        if(order.getPayType().equals(PayTypeEnums.getEnum(1).getDbValue())){
            result = alipayTradePrecreate(order);
        }else if(order.getPayType().equals(PayTypeEnums.getEnum(2).getDbValue())){
            result = alipayTradeWapPayRequest(order);
        }
        return result;
    }

    /**
     * 电脑网站支付
     * @param order
     * @return
     */
    public Map<String,Object>  alipayTradePrecreate(YuancreditOrder order){
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        String orderNo = order.getOrderNo();
        model.setOutTradeNo(orderNo);
        model.setSubject(order.getProductName());
        model.setTotalAmount(order.getOrderPrice());
        model.setBody(order.getRemark());
        model.setTimeoutExpress(config.getPayTimeOut()+"m");
        request.setBizModel(model);
        // 设置异步通知地址
        request.setNotifyUrl(order.getNotifyUrl());
        // 设置同步地址
        request.setReturnUrl(order.getReturnUrl());
        Map<String,Object> result = new HashMap<>();
        try {
            AlipayTradePrecreateResponse response = Optional.ofNullable(getPayClient().execute(request)).orElseThrow(()->new PayException("创建网页版支付宝订单失败!"));
            Optional.ofNullable(response.getQrCode()).orElseThrow(()->new PayException("无法获得支付二维码地址!"));
            result.put(PAY_URL,buildPayUrl(response.getQrCode(),orderNo));
        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
            return null;
        }
        return  result;
    }


    /**
     * 手机网站支付
     * @param order
     * @return
     */
    public Map<String,Object>  alipayTradeWapPayRequest(YuancreditOrder order){
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest ();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        String orderNo = order.getOrderNo();
        model.setOutTradeNo(orderNo);
        model.setSubject(order.getProductName());
        model.setTotalAmount(order.getOrderPrice());
        model.setBody(order.getRemark());
        model.setProductCode(QUICK_WAP_WAY);
        model.setTimeoutExpress(config.getPayTimeOut()+"m");
        request.setBizModel(model);
        // 设置异步通知地址
        request.setNotifyUrl(order.getNotifyUrl());
        // 设置同步地址
        request.setReturnUrl(order.getReturnUrl());
        Map<String,Object> result = new HashMap<>();
        String from = null;
        try {
            from = Optional.ofNullable(getPayClient().pageExecute(request).getBody()).orElseThrow(()->new PayException("创建手机版支付宝订单失败!"));
            Optional.ofNullable(from).orElseThrow(()->new PayException("无法获取支付宝的支付地址!"));
            result.put(PAY_URL,from);
        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
            return null;
        }
        return  result;
    }


    @Override
    protected Map<String,String> queryOrderStatus(YuancreditOrder order) {
        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(order.getOrderNo());
        if(!StringUtil.isEmpty(order.getOutTradeNo())){
            model.setTradeNo(order.getOutTradeNo());
        }
        queryRequest.setBizModel(model);
        queryRequest.setNotifyUrl(order.getNotifyUrl());
        queryRequest.setReturnUrl(order.getReturnUrl());
        Map<String,String> result = new HashMap<>();
        try {
            AlipayTradeQueryResponse response = Optional.ofNullable(getPayClient().execute(queryRequest)).orElseThrow(()->new PayException("获取支付订单状态失败!"));
            result.put(TRADE_STATUS,response.getTradeStatus());
            result.put(OUT_TRADE_NO,response.getOutTradeNo());
            result.put(TRADE_NO,response.getTradeNo());
            result.put(CODE,response.getCode());
            result.put(MSG,response.getMsg());
            result.put(SUB_CODE,response.getSubCode());
            result.put(SUB_MSG,response.getSubMsg());
        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    protected YuancreditPayConfig initConfig() {
        return new YuancreditAlipayConfig(config);
    }

    @Override
    public Map<String, String> payResultNotice(HttpServletRequest request) {
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        try {
            //商户订单号
            String out_trade_no = new String(request.getParameter(OUT_TRADE_NO).getBytes(ISO_8859_1), UTF_8);
            //支付宝交易号
            String trade_no = new String(request.getParameter(TRADE_NO).getBytes(ISO_8859_1),UTF_8);
            //交易状态
            String trade_status = new String(request.getParameter(TRADE_STATUS).getBytes(ISO_8859_1),UTF_8);
            YuancreditPayConfig config = initConfig();
            boolean verify_result = AlipaySignature.rsaCheckV1(params, config.getKey(),UTF_8, RSA_2);
            if(verify_result){//验证成功
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码

                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

                if(trade_status.equals(TRADE_FINISHED)){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                } else if (trade_status.equals(TRADE_SUCCESS)){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                }
                out.println(SUCCESS);	//请不要修改或删除
            }else{//验证失败
                out.println(FAIL);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return params;
    }

    protected AlipayClient getPayClient(){
        if(client==null){
            YuancreditPayConfig config = initConfig();
            client = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",config.getAppID(),config.getKey(), JSON,UTF_8,config.getAliPublicKey(),RSA_2);
        }
        return client;
    }
}
