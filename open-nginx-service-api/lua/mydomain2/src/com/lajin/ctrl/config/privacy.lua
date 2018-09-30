#!/usr/bin/env lua
--[[
隐私策略
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local beanFactory = luastar_context.getBeanFactory()

function privacy(request, response)
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local url = redis:hget("config:privacy", "conUrl")
	redis_util:close(redis)
	if _.isEmpty(url) then
		response:writeln(json_util.exp("获取隐私策略失败。"))
		return
	end
	response:writeln(json_util.success({ url = url}))
end