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


function tplpic(request, response)
	local pic_type = tonumber(request:get_arg("pic_type"))
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
	local rec_pic_list = redis:zrevrangebyscore('rec:tpl:pic:' .. pic_type,old_score,'-inf','limit',0,limit) 
	print(cjson.encode(rec_pic_list))
	
	local pic_list = underscore.map(rec_pic_list,function(v)
			    local picInfo = table_util.array_to_hash(redis:hgetall('rec:tpl:pic:'..pic_type..":"..v))
				if not str_util.isNil(picInfo) then
					local img = {}
					img['pic_id'] = v	
					img['pic_type'] = pic_type	
					local recpicpx = luastar_config.getConfig("tplpicpx")["recpic"]
					local pic_url = picInfo.picUrl or ''
					if not str_util.isNil(pic_url) and not str_util.isNil(recpicpx)  then 
						img['pic_url'] = pic_url..recpicpx
					else
						img['pic_url'] = pic_url
					end
					return img
				end
			end
		)
	
	local res_pic = {}
	res_pic['tplpiclist'] = pic_list or {}
	print(rec_pic_list[#rec_pic_list])
	score = redis:zscore('rec:tpl:pic:' .. pic_type,rec_pic_list[#rec_pic_list])  or 0-- ZSCORE key member
	redis_util:close(redis)
	res_pic['score'] = score
    local result_json = json_util.success(res_pic)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end