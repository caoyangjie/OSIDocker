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


function recmusic(request, response)
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local rec_music_list = redis:zrevrangebyscore('rec:music','+inf','-inf') 
	print(cjson.encode(rec_music_list))
	
	local music_list = underscore.map(rec_music_list,function(v)
			    local musicInfo = table_util.array_to_hash(redis:hgetall('rec:music:'..v))
				if not str_util.isNil(musicInfo)  then
					local music = {}
					music['mid'] = musicInfo.conId	
					music['songname'] = musicInfo.conTitle	
					return music
				end
			end
		)
	redis_util:close(redis)
	local res_music = {}
	res_music['musiclist'] = music_list or {}
    local result_json = json_util.success(res_music)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end