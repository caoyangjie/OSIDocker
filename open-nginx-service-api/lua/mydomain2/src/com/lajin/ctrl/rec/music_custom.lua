#!/usr/bin/env lua
--[[
模版标签
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local underscore = require("underscore")


function music_custom(request, response)
	local rec_type =  tonumber(request:get_arg("rec_type"))
	local score = tonumber(request:get_arg("score"))
	local limit = tonumber(request:get_arg("limit",20))
	 if str_util.isNil(pic_type) then
        response:writeln(json_util.illegal_argument())
        return
    end
	
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local old_score = '+inf'
	if not str_util.isNil(score) and tonumber(score) >0 then
		old_score = score -1
	end 
	local rec_tag_list = redis:zrevrangebyscore('rec:tag:tab:' .. rec_type,old_score,'-inf','limit',0,limit) 
	print(cjson.encode(rec_tag_list))
	
	local music_custom_list = underscore.map(rec_tag_list,function(v)
			    local musiciansInfo = getMusiciansInfo(v)
				if not str_util.isNil(musiciansInfo) then
					return musiciansInfo
				end
			end
		)
	
	local res_music_custom = {}
	res_music_custom['reclist'] = music_custom_list or {}
	print(music_custom_list[#music_custom_list])
	score = redis:zscore('rec:tag:tab:' .. rec_type,music_custom_list[#music_custom_list])  or 0-- ZSCORE key member
	redis_util:close(redis)
	res_pic['score'] = score
    local result_json = json_util.success(res_pic)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end


function getMusiciansInfo(uid)
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local userService = beanFactory:getBean("userService")
	local musiciansInfo = userService:getUserInfo(conId)
	local musicians = {}
	local opus_num = redis:zcound('musicians:comment:'..conId,'-inf','+inf')
	local good_num = redis:zcound('musicians:art:'..conId,'-inf','+inf')
	musicians= {
		uid = musiciansInfo.uid,
		uname = musiciansInfo.username,
		head_pic = musiciansInfo.picurl,
		role = redis:hget('user:role',musiciansInfo.roleid),
		opus_num = opus_num,
		good_num = good_num	
	}
	return musicians
end