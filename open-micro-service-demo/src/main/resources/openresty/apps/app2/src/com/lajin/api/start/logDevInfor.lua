#!/usr/bin/env lua
--[[

]]
local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")

local LogDevInfor = Class("com.lajin.api.start.logDevInfor")

function LogDevInfor:init()
end


function LogDevInfor:logDev(uid)
	if nil == uid or ""==uid then
	    return false,"参数错误"
	end
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local devHash = {}
	local head = ngx.req.get_headers()
	devHash["devid"]   = head["devid"] 
	devHash["devmanufacturer"]   = head["devmanufacturer"]
	devHash["devmodel"]   = head["devmodel"]
	devHash ["login_time"]  = os.date('%Y%m%d%H%M%S', os.time())
	
	local ok,err = redis:hmset("app:dev:"..uid,devHash)
	redis_util:close(redis)
	logger:info("hmset"..cjson.encode(ok).." err " .. cjson.encode(err))
	return ok,err
end
return LogDevInfor
