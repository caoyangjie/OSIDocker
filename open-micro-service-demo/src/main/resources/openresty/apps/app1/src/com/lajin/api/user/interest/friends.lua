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
function friends(request, response)
	
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
	
	local friend = {['friendlist']='',['recommlist']=''}
	--[[ 匹配通讯录中的拉近用户--]]
	if not str_util.isnull(param["addrlist"]) then
		local friendProcess = beanFactory:getBean("friendProcess")
		local friendlist = friendProcess:matchbook(param,redis)
		if  not str_util.isnull(friendlist) then
			--logger:info('friendlist:'..#friendlist)
			for i = 1 ,#friendlist do
				--[[ 获取拉近用户的最新动态--]]
				friendlist[i]['newstatuses']  = friendProcess:getfeed(friendlist[i]['uid'],redis)
				logger:info('friendlist:'..#friendlist)
			end
			friend['friendlist'] = friendlist
		end
	end
	--[[ -获取推荐歌手信息-]]
	local recomProcess = beanFactory:getBean("recomProcess")
	local recomlist = recomProcess:getattention(redis)
	friend['recommlist'] = recomlist
	table_util.tabletoarr(friend,arrkey())
	response:writeln(json_util.formatesuccess(friend))
	redis_util:close(redis)
	return
end

--[[ 转数组的key--]]
function arrkey()
	local o = {}
	o['newstatuses']=true
	o['friendlist']=true
	o['recommlist']=true
	return o
end
