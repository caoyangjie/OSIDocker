#!/usr/bin/env lua
--[[

]]
local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local date_util = require("luastar.util.date")
local recomProcess = Class("com.lajin.api.process.recomProcess")
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")
local userinfo_util = require("com.lajin.api.biz.userinfo")
function recomProcess:init()
	
end

--[[
     获取推荐歌手
--]]
function recomProcess:getattention(redis)
	local recomlist = {}
	local uidlist = redis:zrevrange('recom:attention',0,-1)
	if not str_util.isnull(uidlist) then
		for i=1,#uidlist do
			local user  = redis:hgetall('user:info:'..uidlist[i])
			local recom = newrecom(uidlist[i])
			user = table_util.array_to_hash(user)
			recom = table_util.arryunion(recom,user)
			local number = redis:zcard('user:songs:'..recom['uid'])
			recom['songnum'] = number or 0
			table.insert(recomlist, recom)
		end
	end
	return recomlist
end


--[[ 生成歌手推荐数据--]]
function newrecom (uid)
	local o = {}
	o['uid']=uid
	o['username']=''
	o['namereal']=''
	o['picurl']=''
	o['songnum']=0
	return o
end

return recomProcess
