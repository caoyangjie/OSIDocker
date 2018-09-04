/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.service.LsbAllService;
import com.osidocker.open.micro.service.SmsSendService;
import com.osidocker.open.micro.vo.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:11 2018/7/25
 * @修改说明：
 * @修改日期： 18:11 2018/7/25
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/sendSms")
public class SmsSendController {

    @Autowired
    SmsSendService<String,Response> smsSendService;

    @Autowired
    LsbAllService lsbAllService;

    @PostMapping("/{telephone}")
    @ApiOperation("根据手机号码发送短信验证码")
    public Response handSendSms(@PathVariable("telephone") String telephone){
        // 发送短信验证码
        return smsSendService.sendMessage(telephone);
    }

    @PostMapping("/init/{openId}")
    public ApiResponse initOpenId(HttpServletRequest request, HttpServletResponse response, @PathVariable("openId") String openId){
        request.getSession().setAttribute("openId",openId);
        ShowUserEntity user = lsbAllService.getUserInfo(openId);
        request.getSession().setAttribute("userInfo", user);
        return ApiResponse.generator("000000","成功!");
    }
}
