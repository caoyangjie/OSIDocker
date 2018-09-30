#!/usr/bin/env lua
--[[
    随便看看-最新推荐
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local underscore = require("underscore")
local beanFactory = luastar_context.getBeanFactory()

function art_new(request, response)
    local param = {}
    param["score"] = request:get_arg("score", 0)
    param["limit"] = request:get_arg("limit", 20)
    local score = tonumber(param["score"])
    local limit = tonumber(param["limit"])
    if score < 0 or limit < 0 then
        response:writeln(json_util.illegal_argument())
        return
    end
    local start = "+inf"
    if score > 0 then
        start = score - 1
    end
    local redis_util = beanFactory:getBean("redis")
    local userService = beanFactory:getBean("userService")
    local picService = beanFactory:getBean("picService")
    local redis = redis_util:getConnect()
    local new_id_list = redis:zrevrangebyscore("rec:art:new", start, "-inf", 'limit', 0, limit)
    local new_info_list = underscore.map(new_id_list, function(aid)
        local art_info = table_util.array_to_hash(redis:hgetall("art:info:" .. aid))
        if _.isEmpty(art_info) or tonumber(art_info["is_public"]) == 0 then
            return nil
        end
        local user_info = userService:getUserInfo(art_info["uid"])
        if _.isEmpty(user_info) then
            return nil
        end
        local thumbnail_info = picService:getPicInfoNotNull(art_info["thumbnail_id"])
		local thumbnailpx = luastar_config.getConfig("tplpicpx")["thumbnail"]
		if not _.isEmpty(thumbnail_info["pic_url"]) and not _.isEmpty(thumbnailpx)  then
			thumbnail_info['pic_url'] = thumbnail_info['pic_url']..thumbnailpx
        end
		local hasAudio =  0
		if not str_util.isNil(art_info.audio_id) then
			hasAudio =  1
		end
        return {
            aid = aid,
            pubuserinfo = {
                uid = art_info["uid"],
                username = user_info["username"] or "",
                picurl = user_info["picurl"] or ""
            },
            mid = art_info["mid"],
            tplid = art_info["tplid"],
            title = art_info["title"] or "",
            thumbnail = thumbnail_info["pic_url"] or "",
			has_audio = hasAudio,
            pubtime = art_info["created_time"]
        }
    end)
    local data = {}
    local rs_score = redis:zscore("rec:art:new", new_id_list[#new_id_list]) or 0
    data['score'] = rs_score
    data['newlist'] = new_info_list or {}
    redis_util:close(redis)
    response:writeln(json_util.success(data, true))
end

