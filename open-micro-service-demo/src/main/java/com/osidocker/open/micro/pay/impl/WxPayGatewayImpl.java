/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.osidocker.open.micro.pay.api.YuancreditPayConfig;
import com.osidocker.open.micro.pay.api.YuancreditPayGateway;
import com.osidocker.open.micro.pay.config.YuancreditWxPayConfig;
import com.osidocker.open.micro.pay.entity.PayOrder;
import com.osidocker.open.micro.pay.enums.PayTypeEnums;
import com.osidocker.open.micro.pay.enums.PayWayEnums;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.utils.DataUtils;
import com.osidocker.open.micro.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于11:23 2017/7/7
 * @修改说明：
 * @修改日期： 修改于11:23 2017/7/7
 * @版本号： V1.0.0
 */
@Service("wxPayGateway")
public class WxPayGatewayImpl extends YuancreditPayGateway {

    public static final String BODY = "body";
    public static final String DEVICE_INFO = "device_info";
    public static final String FEE_TYPE = "fee_type";
    public static final String TOTAL_FEE = "total_fee";
    public static final String SPBILL_CREATE_IP = "spbill_create_ip";
    public static final String NOTIFY_URL = "notify_url";
    public static final String TRADE_TYPE = "trade_type";
    public static final String NATIVE = "NATIVE";
    public static final String MWEB = "MWEB";
    public static final String SCENE_INFO = "scene_info";
    public static final String OPENID = "openid";
    public static final String JSAPI = "JSAPI";
    public static final String TIME_EXPIRE = "time_expire";
    public static final String APP_ID = "appId";
    public static final String TIME_STAMP = "timeStamp";
    public static final String NONCE_STR = "nonceStr";
    public static final String PACKAGE = "package";
    public static final String PREPAY_ID = "prepay_id";
    public static final String SIGN_TYPE = "signType";
    public static final String MD_5 = "MD5";
    public static final String PAY_SIGN = "paySign";
    public static final String CODE_URL = "code_url";
    public static final String MWEB_URL = "mweb_url";
    public static final String REDIRECT_URL = "redirect_url";

    private WXPay pay;

    @Override
    public String getPayName() {
        return PayWayEnums.weixin_pay.getDbValue();
    }

    @Override
    public Map<String, Object> createOrder(PayOrder order) {
        Map<String,String> data = new HashMap<>();
        Map<String,Object> resultData = new HashMap<>();
        String orderNo = order.getOrderNo();
        data.put(BODY, order.getRemark());
        data.put(OUT_TRADE_NO,orderNo) ;
        data.put(DEVICE_INFO, "WEB");
        data.put(FEE_TYPE, "CNY");
        BigDecimal orderPrice = new BigDecimal(order.getOrderPrice()).multiply(new BigDecimal(100));
        data.put(TOTAL_FEE, orderPrice.intValue()+"");
        data.put(SPBILL_CREATE_IP, order.getOrderIp());
        data.put(NOTIFY_URL, order.getNotifyUrl());
        //PC端支付
        if(order.getPayType().equals(PayTypeEnums.PC.getDbValue())){
            data.put(TRADE_TYPE, NATIVE);
        }
        //H5支付
        else if(order.getPayType().equals(PayTypeEnums.WEB.getDbValue())){
            data.put(TRADE_TYPE, MWEB);
            data.put(SCENE_INFO,"{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"https://app.moledata.cn\",\"wap_name\": \"鼹鼠大数据产品服务\"}}");
        }
        //公众号
        else if(order.getPayType().equals(PayTypeEnums.JSAPI.getDbValue())){
            data.put(OPENID,order.getOpenId());
            data.put(TRADE_TYPE, JSAPI);
        }
        data.put(TIME_EXPIRE, DataUtils.getTimeExpire(config.getPayTimeOut()));
        Map<String,String> result = new HashMap<>();
        try {
            result = Optional.ofNullable(getPayClient().unifiedOrder(data)).orElseThrow(()->new PayException("创建微信支付订单失败!"));
            if(result.containsKey(RESULT_CODE)){
              if(result.get(RESULT_CODE).equals(SUCCESS)){
                  // PC端支付
                  if(order.getPayType().equals(PayTypeEnums.getEnum(1).getDbValue())){
                      result.put(PAY_URL,buildPayUrl(result.get(CODE_URL),orderNo));
                  }
                  // H5支付
                  else if(order.getPayType().equals(PayTypeEnums.getEnum(2).getDbValue())){
                      // 同步通知页面
                      String returnUrl = URLEncoder.encode(config.getReturnUrl(),"UTF-8");
                      result.put(PAY_URL, result.get(MWEB_URL)+ "&" + REDIRECT_URL + "=" +returnUrl+"?t="+ DataUtils.getTimeStamp());
                  }
                  // 公众号支付
                  else if(order.getPayType().equals(PayTypeEnums.getEnum(3).getDbValue())){
                      result = setBrandWCPayRequest(result);
                  }
              }else {
                   new PayException("创建支付订单状态失败!");
              }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        resultData.putAll(result);
        return resultData;
    }

    /**
     * 设置公众号支付请求参数
     * @param result
     * @return
     * @throws Exception
     */
    public Map<String,String> setBrandWCPayRequest(Map<String,String> result) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put(APP_ID,config.getWxAppid());
        map.put(TIME_STAMP,DataUtils.getTimeStamp().toString());
        map.put(NONCE_STR, WXPayUtil.generateNonceStr());
        map.put(PACKAGE, PREPAY_ID + "=" +result.get(PREPAY_ID));
        map.put(SIGN_TYPE, MD_5);
        String sign = WXPayUtil.generateSignature(map, config.getWxKey(), WXPayConstants.SignType.MD5);
        map.put(PAY_SIGN,sign);
        return map;
    }


    @Override
    protected Map<String,String> queryOrderStatus(PayOrder order) {
        Map<String,String> data = new HashMap<>();
        if(!StringUtil.isEmpty(order.getOutTradeNo())){
            data.put(TRANSACTION_ID, order.getOutTradeNo());
        }
        data.put(OUT_TRADE_NO, order.getOrderNo());
        Map<String,String> result =null;
        try {
            result = Optional.ofNullable(getPayClient().orderQuery(data)).orElseThrow(()->new PayException("订单状态查询失败!"));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    protected YuancreditPayConfig initConfig() {
        return new YuancreditWxPayConfig(config);
    }

    @Override
    public Map<String, String> payResultNotice(HttpServletRequest request) {
        StringBuffer xmlStr = new StringBuffer();
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                xmlStr.append(line);
            }
            //解析字符串
            String notifyResult = xmlStr.toString();
            logger.info("notifyResult:" + notifyResult);
            return getPayClient().processResponseXml(notifyResult);
        } catch (Exception e) {
            logger.info("支付回调处理异常：" + e.getMessage(), e);
        }
        return null;
    }

    protected WXPay getPayClient(){
        if(pay==null){
            pay = new WXPay(initConfig());
        }
        return pay;
    }
}
