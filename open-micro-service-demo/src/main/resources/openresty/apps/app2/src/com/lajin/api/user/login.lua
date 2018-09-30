#!/usr/bin/env lua
--[[

--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")
function login(request, response)
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	--[[ 参数校验--]]
	if not isHead then
		logger:i(" Head  args is null")
		response:writeln(json_util.illegal_argument())
		return
	end
	
	local param = {}
	param["phone"] = request:get_arg("phone");
	param["passwd"] = request:get_arg("passwd");
	--[[ 验证sign--]]
	if str_util.isnull(param["phone"]) or str_util.isnull(param["passwd"])  then
		response:writeln(json_util.illegal_argument())
		return
	end
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end
	
	--[[ 读取用户缓存数据--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local uid = redis:get("user:phone:"..param["phone"])--read redis user data 
	if not str_util.isnull(uid) then
		local userDate = userinfo_util.get_user_info(uid,redis)--read redis user data
		--redis_util:close(redis)
		if not str_util.isnull(userDate) then
			if tonumber(userDate['status']) ==4 then
				redis_util:close(redis)
				response:writeln(json_util.fail('该账号不存在!'))
				return
			end
			--[[ 密码是否正确--]]
			if param["passwd"] ~= userDate["passwd"] then
				redis_util:close(redis)
				response:writeln(json_util.fail('账号或密码错误!'))
				return
			end
			--[[ 登陆流程--]]
			local loginProcess = beanFactory:getBean("loginProcess")
			local ok = loginProcess:loginlog(uid,userDate,redis)
			redis_util:close(redis)
			if not ok then
				return
			end
			local user = require("com.lajin.model.user")
			local data = user:new()
			data=table_util.arryunion(data,userDate)
			table_util.tabletoarr(data,arrkey())
			response:writeln(json_util.formatesuccess(data))
			return
		end
	else
		redis_util:close(redis)
		response:writeln(json_util.fail('账号不存在!'))
		return
	end
	redis_util:close(redis)
	response:writeln(json_util.fail())
	return
end

--[[ 转数组的key--]]
function arrkey()
	local o = {}
	o['userrole']=true
	return o
end