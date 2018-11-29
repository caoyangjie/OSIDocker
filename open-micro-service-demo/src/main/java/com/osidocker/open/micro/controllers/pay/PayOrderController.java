/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.controllers.pay;

import com.osidocker.open.micro.controllers.CoreController;
import com.osidocker.open.micro.pay.api.ApiOrderService;
import com.osidocker.open.micro.pay.api.ApiPayGateway;
import com.osidocker.open.micro.pay.api.ApiQueryOrderService;
import com.osidocker.open.micro.pay.enums.PayTypeEnums;
import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.impl.OrderServiceExtendsImpl;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.pay.vos.QueryOrder;
import com.osidocker.open.micro.pay.vos.TransOrderBase;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

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
@RequestMapping("/pay")
public class PayOrderController extends CoreController {
    public static final String X_FORWARDED_FOR = "x-forwarded-for";
    public static final String UNKNOWN = "unknown";
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    public static final String X_REAL_IP = "X-Real-IP";
    public static final String GATEWAY = "Gateway";
    private Logger logger = LoggerFactory.getLogger(PayOrderController.class);

    @Autowired
    private ApiQueryOrderService queryOrderService;

    @Autowired
    @Qualifier(OrderServiceExtendsImpl.ORDER_SERVICE_EXTENDS)
    private ApiOrderService orderService;

    @ApiOperation("进行支付第三方订单创建")
    @RequestMapping(value = "/{payWay}",method = RequestMethod.POST)
    public ApiResponse unifiedPayOrder(@RequestBody TransOrderBase order, @PathVariable String payWay, HttpServletRequest request){
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
        order.setOrderIp(getIpAddr(request));
        order.setPayWayCode(payWay);
        try{
            ApiPayGateway gateway = getServiceBy(payWay+ GATEWAY,ApiPayGateway.class,version());
            return gateway.execute(order);
        }catch(Exception e){
            logger.error(e.getMessage());
            return getTryCatchExceptions(e);
        }
    }

    @RequestMapping(value = "/query",method = RequestMethod.POST)
    public ApiResponse getQueryOrder(@RequestBody QueryOrder queryOrder) {
        return queryOrderService.getQueryOrderSuccess(queryOrder);
    }

    @ApiOperation("查询临时合同信息表")
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public ApiResponse createSystemOrder(@RequestParam("applyId") String applyId, @RequestParam("totalPrice") BigDecimal totalPrice){
        try{
            logger.info("开始进行订单创建操作");
            totalPrice = new BigDecimal(0.01);
            return orderService.createSystemOrder(applyId,totalPrice);
        }catch(Exception e){
            return getTryCatchExceptions(e);
        }
    }

    /**
     * 获取客服端IP
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(X_REAL_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
