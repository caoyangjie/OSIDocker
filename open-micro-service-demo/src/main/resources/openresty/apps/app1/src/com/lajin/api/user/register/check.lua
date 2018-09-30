#!/usr/bin/env lua
--[[

--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local date_util = require("luastar.util.date")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")
function check(request, response)
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	--[[
	-- 参数校验--]]
	if not isHead then
		logger:i(" Head  args is null")
		response:writeln(json_util.illegal_argument())
		return
	end
	
	local weixinid = request:get_arg("thirduid");
	-- 参数校验
	if str_util.isnull(weixinid) then
		logger:i(" args weixinid is ")
		response:writeln(json_util.illegal_argument())
		return
	end
	--[[参数校验--]]
	local param = {}
	param["thirduid"]= weixinid
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end
	
    logger:i(" args thirduid is "..cjson.encode(weixinid))
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local uid = redis:get("user:thirdid:weixin:"..weixinid)
	local user = require("com.lajin.model.user")
	local data = {
			isreg=0,
			userinfo=user:new()
		}
		
	if not str_util.isnull(uid) then
		local userDate = userinfo_util.get_user_info(uid,redis)--read redis user data
		--redis_util:close(redis)
		if  not str_util.isnull(userDate) then
			--[[ 登陆流程--]]
			local loginProcess = beanFactory:getBean("loginProcess")
			local ok = loginProcess:loginlog(uid,userDate,redis)
			redis_util:close(redis)
			if not ok then
				return
			end
			
			data.isreg=1
			--data.userinfo=userDate
			data.userinfo=table_util.arryunion(data.userinfo,userDate)
			table_util.tabletoarr(data,arrkey())
			response:writeln(json_util.formatesuccess(data))
			return
		end
	end
	redis_util:close(redis)
	table_util.tabletoarr(data,arrkey())
	response:writeln(json_util.formatesuccess(data))
end

--[[ 转数组的key--]]
function arrkey()
	local o = {}
	o['userrole']=true
	return o
end