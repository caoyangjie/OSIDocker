#! /usr/bin/env lua
--[[
参数校验拦截器
--]]
module(..., package.seeall)

local json_util = require("com.lajin.common.util.json")
local logger = luastar_log.getLog()
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
function beforeHandle()
	--[[ 参数校验--]]
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	
	--[[ 参数校验--]]
	if not isHead then
		logger:i(" Head  args is null")
		ngx.ctx.response:writeln(json_util.illegal_argument())
		return false
	end
	
	local param = {}
	param["utoken"] = ngx.ctx.request:get_arg("utoken")
	param["uid"] = ngx.ctx.request:get_arg("uid")
	param["addrlist"]= ngx.ctx.request:get_arg("addrlist")
	--[[参数校验--]]
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		ngx.ctx.response:writeln(json_util.fail("sign error"))
		return false
	end
	
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
		ngx.ctx.response:writeln(json_util.illegal_argument())
		return false 	
	end

    return true
end

function afterHandle(ctrl_call_ok, err_info)

end
