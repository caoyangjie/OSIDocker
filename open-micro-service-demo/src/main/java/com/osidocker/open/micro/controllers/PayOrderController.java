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
import com.osidocker.open.micro.pay.api.ApiQueryOrderService;
import com.osidocker.open.micro.pay.enums.PayTypeEnums;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.vos.APIResponse;
import com.osidocker.open.micro.pay.vos.QueryOrder;
import com.osidocker.open.micro.pay.vos.TransOrderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于9:20 2017/7/7
 * @修改说明：
 * @修改日期： 修改于9:20 2017/7/7
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/createOrder")
public class PayOrderController extends CoreController {

    public static final String WXPAY = "wxpay";
    public static final String ALIPAY = "alipay";
    private Logger logger = LoggerFactory.getLogger(PayOrderController.class);

    @Autowired
    @Qualifier("alipayGateway")
    protected ApiPayGateway alipayGateway;

    @Autowired
    @Qualifier("wxPayGateway")
    protected ApiPayGateway wxPayGateway;

    @Autowired
    private ApiQueryOrderService queryOrderService;

    @RequestMapping(value = "/{payWay}",method = RequestMethod.POST)
    public APIResponse unifiedPayOrder(@RequestBody TransOrderBase order, @PathVariable String payWay, HttpServletRequest request){
        order.setOrderIp(getIpAddr(request));
        if(payWay.equalsIgnoreCase(WXPAY)){
            // 公众号支付时获取openId
            if(order.getPayType().equals(PayTypeEnums.JSAPI.getDbValue())){
                String openId =(String)request.getSession().getAttribute("openId");
                logger.info("公众号openId:"+openId);
                if(null != openId){
                     order.setOpenId(openId);
                }else {
                    throw new PayException("无法获取微信公众号OpenId值!");
                }
            }
            return wxPayGateway.execute(order);
        }else if(payWay.equalsIgnoreCase(ALIPAY)){
            return alipayGateway.execute(order);
        }else{
            return getTryCatchExceptions(new Exception("系统不支持的第三方支付方式!"));
        }
    }

    @RequestMapping(value = "/query/order",method = RequestMethod.POST)
    public APIResponse getQueryOrder(@RequestBody QueryOrder queryOrder) {
        return queryOrderService.getQueryOrder(queryOrder);
    }

    /**
     * 获取客服端IP
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
