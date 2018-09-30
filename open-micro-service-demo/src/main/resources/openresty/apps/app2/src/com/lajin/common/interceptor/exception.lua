#! /usr/bin/env lua
--[[
异常处理拦截器
--]]
module(..., package.seeall)

local json_util = require("com.lajin.common.util.json")
local logger = luastar_log.getLog()

function beforeHandle()
    return true
end

function afterHandle(ctrl_call_ok, err_info)
    if not ctrl_call_ok then
        ngx.ctx.response:writeln(json_util.fail(err_info))
    end
end
