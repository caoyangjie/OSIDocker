package org.osidocker.open.web.controller;

import org.osidocker.open.utils.SmsPushUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/openAPI/sms")
public class SmsSendController {
	
	@RequestMapping("/send")
	@ApiOperation(notes="短信发送-/openAPI/sms/send",nickname="发送短信", value = "/openAPI/sms/send")
	public @ResponseBody String sendMsgHandler(
			@ApiParam(name="mobiles",required=true)@RequestParam(name="mobiles",required=true)String mobiles,
			@ApiParam(name="templateId",required=true)@RequestParam(name="templateId",required=true)String templateId,
			@ApiParam(name="datas",required=true)@RequestParam(name="datas",required=true)String datas
			){
		SmsPushUtils.sendTemplateSMS(mobiles, templateId, datas.split(";"), null);
		return "success";
	}
}
