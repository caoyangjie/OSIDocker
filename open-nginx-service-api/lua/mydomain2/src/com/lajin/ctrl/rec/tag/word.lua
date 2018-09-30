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


function tplwordtag(request, response)
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local type_list = redis:lrange("dict:tpl:word:type", 0, -1)
	local word_type_list = underscore.map(type_list,function(v)
			    local tagInfo = table_util.array_to_hash(redis:hgetall('dict:tpl:word:type:'..v))
				if not str_util.isNil(tagInfo) and tonumber(tagInfo.status) == 1 then
					local tag = {}
					tag['type'] = v
					tag['name'] = tagInfo.name
					return tag
				end
			end
		)
	redis_util:close(redis)
	local res_word_tags = {}
	res_word_tags['wordtag'] = word_type_list or {}
    local result_json = json_util.success(res_word_tags)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end
