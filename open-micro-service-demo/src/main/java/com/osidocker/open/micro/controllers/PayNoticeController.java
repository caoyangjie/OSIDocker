/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.pay.api.ApiPayGateway;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.vos.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于9:17 2017/7/7
 * @修改说明：
 * @修改日期： 修改于9:17 2017/7/7
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/payNotice")
public class PayNoticeController {
    /**
     * 微信支付
     */
    public static final String WXPAY = "wxpay";
    /**
     * 支付宝支付
     */
    public static final String ALIPAY = "alipay";

    @Autowired
    @Qualifier("alipayGateway")
    protected ApiPayGateway alipayGateway;

    @Autowired
    @Qualifier("wxPayGateway")
    protected ApiPayGateway wxPayGateway;


    @RequestMapping(value = "/{payWay}",method = RequestMethod.POST)
    public APIResponse NoticePayOrder(@PathVariable String payWay, HttpServletRequest request) {
        Map<String,String> payResult = null;
        if(payWay.equalsIgnoreCase(WXPAY)){
              payResult = Optional.ofNullable(wxPayGateway.payResultNotice(request)).orElseThrow(()->new PayException("微信支付结果回调操作异常!"));
              wxPayGateway.noticeOrder(payResult);
        }else if(payWay.equalsIgnoreCase(ALIPAY)){
              payResult = Optional.ofNullable(alipayGateway.payResultNotice(request)).orElseThrow(()->new PayException("支付宝结果回调操作异常!"));
              alipayGateway.noticeOrder(payResult);
        }else{
            return null;
        }
        return null;
    }
}
