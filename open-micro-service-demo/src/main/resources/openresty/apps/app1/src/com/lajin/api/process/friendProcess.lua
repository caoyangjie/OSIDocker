#!/usr/bin/env lua
--[[

]]
local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local date_util = require("luastar.util.date")
local friendProcess = Class("com.lajin.api.process.friendProcess")
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")
local userinfo_util = require("com.lajin.api.biz.userinfo")
function friendProcess:init()
	
end

--[[
     根据电话获取匹配用户信息
--]]
function friendProcess:matchbook(param,redis)
--[[ 匹配通讯录中的拉近用户--]]
	param['addrlist'] = str_util.urldecode(param['addrlist'])
	param["addrlist"] = str_util.split(param["addrlist"],",")
	logger:info('电话号码...'..cjson.encode(param["addrlist"]))
	local friendlist = {}
	for key,val in pairs(param["addrlist"]) do
		logger:info('电话号码======='..cjson.encode(val))
		local number = str_util.trans62to10(val)
		--local number = val
		local uid = redis:get("user:phone:"..number)
		if not str_util.isnull(uid) then
			local user = userinfo_util.get_user_info(uid,redis)
			if not str_util.isnull(user) then
				local friend = newarry(uid)
				friend = table_util.arryunion(friend,user)
				--[[ 是否关注 --]]
				local is_follow = redis:zrank('user:following:'..param["uid"],uid)
				friend['is_follow'] = not str_util.isnull(is_follow) and  1 or 0
				table.insert(friendlist, friend)
			end
		end
	end
	return friendlist
end

--[[ 获取动态--]]
function friendProcess:getfeed(uid,redis)
	local feedid = redis:zrevrange('feed:outbox:'..uid,0,1)
	local newstatuses = {}
	for key,val in pairs(feedid) do
		local feed = redis:hgetall('feed:info:'..feedid[key])
		local result = newfeed()
		if not str_util.isnull(feed) then
			feed = table_util.array_to_hash(feed)
			
			--[[ 根据动态类型查找 动态内容--]]
			local text = findtext(feed['feed_type'],feed['cid'],redis)
			if not str_util.isnull(text) and text ~= false and type(text) ~= 'boolean' then
				result['text'] = text
			end
			local uuid = feed['pid1']
			if not str_util.isnull(uuid) then
				local pic_url = redis:hget('pub:image:'..(uuid or -1),'pic_url')
				if not str_util.isnull(pic_url) then
					--imageinfo = table_util.array_to_hash(imageinfo)
					result['respath'] = pic_url
				end
			end
			--newstatuses[key] = result
			table.insert(newstatuses, result)
		end
	end
	return newstatuses
end

--[[ 根据动态类型查找 动态内容 --]]
function findtext(textype,id,redis)
	local text = ''
	if not str_util.isnull(textype) and textype ~= false and type(textype) ~= 'boolean' and not str_util.isnull(id)  then
		--[[ 1-单曲，2-图片，3-文字，4-活动 --]]
		if tonumber(textype) == 1 then
			text = redis:hget('md:audio:'..id,'audio_name')
		elseif tonumber(textype) == 4 then
			text = redis:hget('activity:info:'..id,'actname')
		end
	end
	return text
end

--[[ 生成返回数据--]]
function newarry (uid)
	local o = {}
	o['uid']=uid
	o['phone']=''
	o['username']=''
	o['namereal']=''
	o['picurl']=''
	o['usertype']=''
	o['is_follow']=''
	o['userrole']=''
	o['thridsrc']=''
	o['thirduid']=''
	o['newstatuses']=''
	return o
end

--[[ 生成动态返回数据--]]
function newfeed ()
	local o = {}
	o['text']=''
	o['respath']=''
	return o
end

return friendProcess
