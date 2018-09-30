#! /usr/bin/env lua
--[[
参数校验拦截器
--]]
module(..., package.seeall)

local json_util = require("com.lajin.common.util.json")
local logger = luastar_log.getLog()

function beforeHandle()
	local appkey = ngx.req.get_headers()["appkey"]
	if not appkey then
		ngx.ctx.response:writeln(json_util.illegal_argument())
		return false 	
	end
    return true
end

function afterHandle(ctrl_call_ok, err_info)

end
