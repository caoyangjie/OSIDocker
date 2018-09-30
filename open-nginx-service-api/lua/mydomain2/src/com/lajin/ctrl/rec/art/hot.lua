#! /usr/bin/env lua
--[[
    随便看看-热门推荐
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local underscore = require("underscore")
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()

function art_hot(request, response)
--[[ 私有参数校验 --]]
    local param = {}
	param["score"] = request:get_arg("score")
    param["limit"] = request:get_arg("limit")
	ngx.log(logger.i("获取作品列表参数：", cjson.encode(param)))
    --[[ 验证sign --]]
	local paramService = beanFactory:getBean("paramService")
	local check_ok = paramService:checkSign(param)
	if not check_ok then
		response:writeln(json_util.fail("数据错误"))
		return
	end
	
    local score = request:get_arg("score")
	local limit = tonumber(request:get_arg("limit",20))
	
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	--[[
	local old_score = '+inf'
	if not str_util.isNil(score) and tonumber(score) >0 then
		old_score = score -1
	end 
	--]]
	local offset = 0 
	if not str_util.isNil(score) and tonumber(score) >0 then
		offset = score
	end
	
	
	local artid_list = redis:zrevrangebyscore('rec:art:hot','+inf','-inf','limit',offset,limit) 
	print(cjson.encode(artid_list))
	local userService = beanFactory:getBean("userService")
	local art_list = underscore.map(artid_list,function(v)
			    local artInfo = table_util.array_to_hash(redis:hgetall('art:info:'..v))
				local user_info = userService:getUserInfo(artInfo.uid)
				if _.isEmpty(user_info) then
					return nil
				end
				if not str_util.isNil(artInfo) then
					local art = {}
					art['aid'] = v	
					art['pubuserinfo'] = {
						uid = artInfo.uid,
						username = user_info.username or '',
						picurl = user_info.picurl or ''
					}
					art['mid'] = artInfo.mid
					art['tplid'] = artInfo.tplid
					art['title'] = artInfo.title
					local imgInfo = table_util.array_to_hash(redis:hgetall('pub:image:'..(artInfo.thumbnail_id or '')))
					local thumbnailpx = luastar_config.getConfig("tplpicpx")["thumbnail"]
					local thumbnail = imgInfo.pic_url or ''
					if not str_util.isNil(thumbnail) and not str_util.isNil(thumbnailpx)  then 
						art['thumbnail'] = thumbnail..thumbnailpx
					else
						art['thumbnail'] = thumbnail
					end
					art['has_audio'] =  0
					if not str_util.isNil(artInfo.audio_id) then
						art['has_audio'] =  1
					end
					art['pubtime'] = artInfo.created_time
					return art
				end
			end
		)
	score = redis:zscore('rec:art:hot',artid_list[#artid_list])  or 0-- ZSCORE key member
	redis_util:close(redis)
	local res_art = {}
	res_art['score'] = offset+ #artid_list   -- score
	res_art['hotlist'] = art_list or {}
    local result_json = json_util.success(res_art)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end
