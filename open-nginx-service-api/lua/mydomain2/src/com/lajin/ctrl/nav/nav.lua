#!/usr/bin/env lua
--[[
	首页导航接口
]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local underscore = require("underscore")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()


function nav(request, response)
	 -- 校验必填参数
	-- head参数
    local paramService = beanFactory:getBean("paramService")
    local headParam = paramService:getHeadParam()
	
	local loc = request:get_arg("loc")
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local rec_nav_list = redis:zrevrangebyscore('rec:home:nav:' .. loc .. ':2','+inf','-inf') 
	local nav_list = underscore.map(rec_nav_list,function(v)
			    local recInfo = table_util.array_to_hash(redis:hgetall('rec:tag:' .. loc .. ':2:'..v))
				if not str_util.isNil(recInfo) then
					local nav = {}
					nav['title'] = recInfo.conTitle or ''
					nav['nid'] = recInfo.conId or ''
					nav['nav_type'] = recInfo.conType or ''
					nav['nav_url'] = recInfo.conUrl or ''
					nav['desc'] = recInfo.conDesc or ''
					local recpicpx = luastar_config.getConfig("tplpicpx")["nav"]
					local pic_url = recInfo.picUrl or ''
					if not str_util.isNil(pic_url) and not str_util.isNil(recpicpx)  then 
						nav['pic_url'] = pic_url..recpicpx
					else
						nav['pic_url'] = pic_url
					end
					return nav
				end
			end
		)
	redis_util:close(redis)
	
	if _.isEmpty(nav_list) then
		response:writeln(json_util.exp("no nav list data found."))
		return
	end
	local rs = {
		navlist= nav_list or {}
	}
	response:writeln(json_util.success(rs,true))
	
end