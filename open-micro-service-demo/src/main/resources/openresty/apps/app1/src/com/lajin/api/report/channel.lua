#!/usr/bin/env lua
--[[
	推送设备channel上报接口
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local http = require "resty.http"
local str_util = require("luastar.util.str")
function channel(request, response)
	--[[ 私有参数校验 --]]
	local param = {}
	param["utoken"] = request:get_arg("utoken") or '';
	param["uid"] = request:get_arg("uid") or '';
	param["channel"]= request:get_arg("channel") or '' ;

	--[[ 参数校验--]]
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	
	--[[ 参数校验--]]
	if not isHead then
		logger:i(" Head  args is error")
		response:writeln(json_util.illegal_argument())
		return 
	end
		
	--[[ 校验必填参数 --]]
	local  is_ok = _.any(_.pick(param,"utoken","uid","channel"),function(v) if v ==nil or ""==v then return  true end   end)
	if is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	--[[参数校验--]]
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return 
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
	local info = redis:hgetall("user:info:"..param["uid"])
	info = table_util.array_to_hash(info)
	if str_util.isnull(info) or type(info) == 'boolean' then
		redis_util:close(redis)
		response:writeln(json_util.fail('账号不存在!'))
		return
	end
	
	--[[ 写设备信息缓存--]]
	local ok ,er  = setdev(param,request.headers,info,redis)
	redis_util:close(redis)
	if ok ~='OK' then 
		response:writeln(json_util.fail('上报失败!'))
		return 
	end
	response:writeln(json_util.success())
	return 
end

--[[ 写设备信息缓存--]]
function setdev(param,heander,info,redis)
	local dev = {}
	dev['devid'] =param['channel']
	dev['devmanufacturer'] = heander['devmanufacturer']
	dev['devmodel'] = heander['devmodel'] 
	dev['login_time'] = info['lastlogintime']
	local ok ,err = redis:hmset('app:dev:'..param['uid'],dev)
	return ok,err 
end