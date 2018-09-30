#!/usr/bin/env lua
--[[
音频转码回调
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local http = require "resty.http"
local str_util = require("luastar.util.str")
function tccallback(request, response)
	logger:info("tc callback start!")

	logger:info("messageBody="..cjson.decode(request:get_request_body())["messageBody"])
	local msgBody = cjson.decode(cjson.decode(request:get_request_body())["messageBody"])
	local param = {}
	param.jobId = msgBody.jobId
	param.jobStatus = msgBody.jobStatus
	if nil ~= msgBody.error then
		param.errorMsg = msgBody.error.message
	end
	param.presetName = msgBody.target.presetName
	
	--转码表增加JOBID字段，回调方法根据JOBID更新相应记录
	local httpclient = require("luastar.util.httpclient")
	ok, code, headers, status, body = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/md/audio/tccallback","POST",param,1500000,{})
	if tonumber(code) ~= 200 then
		logger:info("http request error")
		return
	end
	local responseBody = cjson.decode(body)
	if responseBody["code"] ~="A000000" then
		logger:info("inner api exception:"..json_util.fail(responseBody['message']))
		return
	end
	logger:info("call inner api success")
	
end
