#!/usr/bin/env lua
--[[


local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local date_util = require("luastar.util.date")
local messageProcess = Class("com.lajin.api.process.messageProcess")
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")
local userinfo_util = require("com.lajin.api.biz.userinfo")
function messageProcess:init()
	
end


--[[ 
	创建自己的消息 
--]]
function messageProcess:mymsg(param,redis)
	local msgid,err= self:createmsgid(redis)
	if msgid == false or str_util.isnull(msgid) then
		return false,'创建消息ID失败!!'
	end
	redis:zadd('user:msg:outbox:'..param['uid'],msgid,msgid)
	local msginfo = {}
	msginfo['created_time'] = os.time()
	msginfo['opt_type'] = 8 --认证
	msginfo['receiver_uid'] = param['higherups']
	msginfo['sender_uid'] = param['uid']
	msginfo['status'] = 1 --消息未处理
	msginfo['type'] = 4 --认证
	msginfo['text'] ='认证角色:'..self:rolemsg(param['roles'])
	redis:hmset('user:msg:info:'..msgid,msginfo)
	return msgid
end

--[[
    接收认证消息
--]]
function messageProcess:sendmsg(param,ruid,smsgid,redis)
	local msgid,err= self:createmsgid(redis)
	if msgid == false or str_util.isnull(msgid) then
		return false,err
	end
	redis:zadd('user:msg:inbox:'..param['uid'],msgid,msgid)
	local msginfo ={}
	msginfo['created_time'] = os.time()
	msginfo['opt_type'] = 8 --认证
	msginfo['receiver_uid'] = ruid
	msginfo['sender_msgid'] = smsgid
	msginfo['sender_uid'] = param['uid']
	msginfo['status'] = 4 --认证中
	msginfo['type'] = 4 --认证
	msginfo['text'] ='认证角色:'..self:rolemsg(param['roles'])
	redis:hmset('user:msg:info:'..msgid,msginfo)
end

--[[ 
	创建消息ID
--]]
function messageProcess:createmsgid(redis)
	local msgid,msgid_err = redis:incr("user:msg:ids")
	if str_util.isnull(msgid) then
		return false,'创建消息ID失败'
	end
	return msgid
end

--[[ 
	认证角色信息
--]]
function messageProcess:rolemsg(roles)
	local rolelist = str_util.split(roles,',')
	local roleval = {}
	for k,v in pairs(rolelist) do
		local rolename 
		if tonumber(v) == 1 then
			rolename  = '原创歌手'
		elseif tonumber(v) == 2 then
			rolename  = '作词家'
		elseif tonumber(v) == 3 then
			rolename  = '作曲家'
		elseif tonumber(v) == 4 then
			rolename  = '乐手'
		elseif tonumber(v) == 5 then
			rolename  = '编曲'
		elseif tonumber(v) == 6 then
			rolename  = '制作人'
		elseif tonumber(v) == 7 then
			rolename  = '录音师'
		elseif tonumber(v) == 8 then
			rolename  = '乐评人'
		elseif tonumber(v) == 9 then
			rolename  = '媒体人'
		elseif tonumber(v) == 10 then
			rolename  = '唱片公司'
		else
			rolename = '非法角色'
		end
		table.insert(roleval,rolename)
	end
	return table.concat(roleval,'/')
end

return messageProcess
--]]