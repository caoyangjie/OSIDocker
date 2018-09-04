/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.message.MessageEnums;
import com.osidocker.open.micro.message.PublishMessageFactory;
import com.osidocker.open.micro.message.kafka.KafkaConsumer;
import com.osidocker.open.micro.model.SystemUser;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.security.service.UserService;
import com.osidocker.open.micro.service.LsbAllService;
import com.osidocker.open.micro.vo.BaseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 10:42 2018/8/29
 * @修改说明：
 * @修改日期： 10:42 2018/8/29
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/lsb")
public class LsbApiController {

    public static final String FLOW_NO = "flowNo";
    public static final String GENERATE_SERVICE = "GenerateService";
    @Autowired
    UserService userService;

    @Autowired
    LsbAllService lsbAllService;

    @Value("${lsb.python.service.url}")
    private String lsbPythonService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取用户个人基本信息
     * @return
     */
    @GetMapping("/userInfo")
    public ApiResponse getUserInfo(){
        return ApiResponse.generator("000000","获取用户信息成功!").initData(getUserDetails());
    }

    /**
     * 获取我的认证报告
     * @return
     */
    @GetMapping("/myReport")
    public ApiResponse getMyReportList(){
        return ApiResponse.generator("000000","获取数据成功!").initData(lsbAllService.searchUserReport(getUserDetails().getUserId()));
    }

    /**
     * 获取我的发送认证报告
     * @return
     */
    @GetMapping("/sendReport")
    public ApiResponse getSendReportList(){
        return ApiResponse.generator("000000","获取发送认证报告列表成功!").initData(lsbAllService.searchUserSendReport(getUserDetails().getUserId()));
    }

    /**
     * 获取我的学信验证记录列表
     * @return
     */
    @GetMapping("/educations")
    public ApiResponse getUserEducationalList(){
        return ApiResponse.generator("000000","获取学信信息列表成功!").initData(lsbAllService.getUserEducationList(getUserDetails().getUserId()));
    }

    @GetMapping("/oper/{type}")
    public ApiResponse getOperTypeList(@PathVariable("type")String type){
        return ApiResponse.generator("000000","获取可支持爬取的列表成功!").initData(lsbAllService.getOperationList(type));
    }

    /**
     * 更新个人用户信息
     * @return
     */
    @PostMapping("/update")
    public ApiResponse updateUserInfo(@RequestBody SystemUser user){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ShowUserEntity userInfo = (ShowUserEntity) auth.getCredentials();
        Optional.ofNullable(user.getEmail()).ifPresent(x->{
            userInfo.setEmail(x);
        });
        Optional.ofNullable(user.getTelephone()).ifPresent(x->{
            userInfo.setTelephone(x);
        });
        lsbAllService.updateUserInfo(userInfo);
        return ApiResponse.generator("000000","更新成功!");
    }

    /**
     * 发送报告到邮箱
     * @return
     */
    @RequestMapping(value = "/sendToMail/{validateId}/{email}", method = RequestMethod.POST)
    public ApiResponse sendReportToMail(@PathVariable("email") String email,@PathVariable("validateId") Long validateId){
        lsbAllService.sendMessageMail(getUserDetails().getUserId(),validateId,"银行流水认证报告","report.ftl",email);
        return ApiResponse.generator("000000","邮件发送成功!");
    }

    @PostMapping("/sendRequest")
    public ApiResponse gatewayToPythonService(@RequestBody JSONObject data){
        data.put(FLOW_NO,UUID.randomUUID().toString()+"#"+getUserDetails().getUserId());
        JSONObject response = restTemplate.postForObject(lsbPythonService,data,JSONObject.class);
        return ApiResponse.generator("000000","获取数据成功!").initData(response);
    }

    /**
     * 发送消息主题
     * @param data  数据
     * @param service 主题
     * @return
     */
    @PostMapping("/send/{service}")
    public ApiResponse sendMqMessage(@RequestBody JSONObject data,@PathVariable("service") String service){
        BaseMessage<JSONObject> message = new BaseMessage<>();
        message.setServiceId(service);
        message.setEventType(data.getString("type")+ GENERATE_SERVICE);
        message.setMessage(data);
        PublishMessageFactory.send(KafkaConsumer.MYKAFKA, message , MessageEnums.Kafka);
        return ApiResponse.generator("000000","MQ消息发送成功!");
    }

    protected ShowUserEntity getUserDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (ShowUserEntity) auth.getCredentials();
    }
}
