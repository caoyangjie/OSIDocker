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
import com.osidocker.open.micro.pay.vos.ApiResponse;
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
public class PayNoticeController extends CoreController {

    private static final String GATEWAY = "Gateway";

    @RequestMapping(value = "/{payWay}",method = RequestMethod.POST)
    public ApiResponse NoticePayOrder(@PathVariable String payWay, HttpServletRequest request) {
        try{
            Map<String,String> payResult;
            ApiPayGateway gateway = getServiceBy(payWay+ GATEWAY,ApiPayGateway.class,version());
            payResult = Optional.ofNullable(gateway.payResultNotice(request)).orElseThrow(()->new PayException("支付结果回调操作异常!"));
            gateway.noticeOrder(payResult);
            return getDefaultApiRosponse();
        }catch(Exception e){
            logger.error(e.getMessage());
            return getTryCatchExceptions(e);
        }
    }
}
