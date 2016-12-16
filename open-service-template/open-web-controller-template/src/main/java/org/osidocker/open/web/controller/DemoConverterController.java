package org.osidocker.open.web.controller;

import org.osidocker.open.web.converter.DemoConverterVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoConverterController {
	
	@RequestMapping(value="/openAPI/converter",produces="application/x-wisely")
	@ResponseBody
	public DemoConverterVO converter(@RequestBody DemoConverterVO dcvo){
		return dcvo;
	}
	
	@RequestMapping({"/openAPI/demoConv"})
	public String index(){
		return "httpconverter";
	}
}
