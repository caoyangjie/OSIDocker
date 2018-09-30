#!/usr/bin/env lua
--[[

--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")
function addrbook(request, response)
	local param = {}
	param["utoken"] = request:get_arg("utoken")
	param["uid"] = request:get_arg("uid")
	param["addrlist"]= request:get_arg("addrlist")
	
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	--[[ 检查token--]]
	local loginProcess = beanFactory:getBean("loginProcess")
	local ok ,emsg = loginProcess:checktoken(param,redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(emsg))
		return
	end
	
	local friend = {['friendlist']=''}
	if not str_util.isnull(param["addrlist"]) then
		--[[ 匹配通讯录中的拉近用户--]]
		local friendProcess = beanFactory:getBean("friendProcess")
		local friendlist = friendProcess:matchbook(param,redis)
		if  not str_util.isnull(friendlist) then
			friend['friendlist'] = friendlist
		end
	end
	redis_util:close(redis)
	table_util.tabletoarr(friend,arrkey())
	response:writeln(json_util.formatesuccess(friend))
	return
	--local a  = {'friendlist'=friendlist}
end

--[[ 转数组的key--]]
function arrkey()
	local o = {}
	o['friendlist']=true
	o['userrole']=true
	return o
end

