#!/usr/bin/env lua
--[[
	大咖认证确认
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local http = require "resty.http"
local str_util = require("luastar.util.str")
function confirm(request, response)
	
	local hc = http:new()	
	--[[ 参数校验--]]
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	
	--[[ 参数校验--]]
	if not isHead then
		logger:i(" Head  args is error")
		response:writeln(json_util.illegal_argument())
		return 
	end
	
	--[[ 私有参数校验 --]]
	local param = {}
	param["utoken"] = request:get_arg("utoken") or '';
	param["uid"] = request:get_arg("uid") or '';
	param["authuid"]= request:get_arg("authuid") or '';
	param["msgid"] = request:get_arg("msgid") or '';

	--[[ 校验必填参数 --]]
	local  is_ok = _.any(_.pick(param,"utoken","uid","authuid",'msgid'),function(v) if v ==nil or ""==v then return  true end   end)
	logger:info("  param is ok2 "..tostring(is_ok))
	if is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	--[[参数校验 sign--]]
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		ngx.ctx.response:writeln(json_util.fail("sign error"))
		return false
	end

	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()

	--[[ 校验token--]]
	local loginProcess = beanFactory:getBean("loginProcess")
	local isok ,emsg = loginProcess:checktoken(param,redis)
	if not isok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(emsg))
		return
	end
		
	--[[ 查看用户是否存在 --]]
	local uid = redis:hlen("user:info:"..param["uid"])
	if str_util.isnull(uid) or uid<=0 then
		redis_util:close(redis)
		response:writeln(json_util.fail('账号不存在!'))
		return
	end
	param['opstatus'] = 2 -- tongguo
	--[[  请求内网接口 --]]
	local httpclient = require("luastar.util.httpclient")
	local ok, code, headers, status, resUpload = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/app/uc/user/auth","POST",param,1500000,{})
	
	if tonumber(code) ~= 200 or str_util.isnull(resUpload) then
		redis_util:close(redis)
		response:writeln(json_util.fail('http request error'))
		return
	end
	redis_util:close(redis)
	local resUpload_table = cjson.decode(resUpload)
	logger:info(resUpload)
	if not str_util.isnull(resUpload_table)  then
		if resUpload_table["code"] =="A000000" then
			response:writeln(json_util.success())
			return
		else
			response:writeln(json_util.fail(resUpload_table['message']))
			return
		end		
	else 
		response:writeln(json_util.not_found())
	end

end
