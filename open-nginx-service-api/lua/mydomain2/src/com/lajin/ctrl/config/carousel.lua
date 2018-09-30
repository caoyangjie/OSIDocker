#!/usr/bin/env lua
--[[
	首页轮播图接口
]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local underscore = require("underscore")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

local function getImglist()
    -- 获取redis
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local imglist = redis:lrange("config:carousel:imglist", 0, -1)
    -- 关闭redis
    redis_util:close(redis)
	if _.isEmpty(imglist) then
		local imgpx = luastar_config.getConfig("tplpicpx")["carousel"]
		imglist = underscore.map(imglist, function(v)
			if not _.isEmpty(imgpx) then
				return v .. imgpx
			end
			return v
		end)
	
	end
	
    return imglist
end

function recCarousel(request, response)
	 -- 校验必填参数
	-- head参数
    local paramService = beanFactory:getBean("paramService")
    local headParam = paramService:getHeadParam()
	
	local loc = request:get_arg("loc")
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local rec_carousel_list = redis:zrevrangebyscore('rec:home:carousel:' .. loc,'+inf','-inf') 
	local carousel_list = underscore.map(rec_carousel_list,function(v)
			    local recInfo = table_util.array_to_hash(redis:hgetall('rec:tag:' .. loc .. ':1:'..v))
				if not str_util.isNil(recInfo) then
					local rec = {}
					rec['title'] = recInfo.conTitle or ''
					rec['rec_id'] = recInfo.conId or ''
					rec['rec_type'] = recInfo.conType or ''
					rec['rec_url'] = recInfo.conUrl or ''
					local recpicpx = luastar_config.getConfig("tplpicpx")["carousel"]
					local pic_url = recInfo.picUrl or ''
					if not str_util.isNil(pic_url) and not str_util.isNil(recpicpx)  then 
						rec['pic_url'] = pic_url..recpicpx
					else
						rec['pic_url'] = pic_url
					end
					return rec
				end
			end
		)
	redis_util:close(redis)
	
	if _.isEmpty(carousel_list) then
		response:writeln(json_util.exp("no carousel list data found."))
		return
	end
	local rs = {
		rec_home_carousel= carousel_list or {}
	}
	response:writeln(json_util.success(rs,true))
	
end

function carousel(request, response)
    -- 校验必填参数
	-- head参数
    local paramService = beanFactory:getBean("paramService")
    local headParam = paramService:getHeadParam()
	-- 输出轮播图列表结果
	local imglist = getImglist()
	if _.isEmpty(imglist) then
			response:writeln(json_util.exp("no carousel imglist data found."))
			return
	end
	local rs = {
			imglist = imglist_addpx or {}
	}
	response:writeln(json_util.success(rs,true))
end