#!/usr/bin/env lua
--[[
开机启动图片
]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local beanFactory = luastar_context.getBeanFactory()

function imglist(request, response)
	-- 参数校验
	local check = beanFactory:getBean("check")
	local isHead = check:head()

	--[[ head参数校验--]]
	if not isHead then
		logger:i(" Head  args is null")
		response:writeln(json_util.illegal_argument())
		return
	end
	
	
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	
	local startList  = redis:lrange("app:start:imgs",0,-1)
	redis_util:close(redis)
	if 0 == table.getn(startList) then
		response:writeln(json_util.not_found("data not found"))
	else
		local res = {
			imglist={
				startList[1]
			}
			}
		
		response:writeln(json_util.success(res)) 
	end 	
	
end
