#!/usr/bin/env lua
--[[
	首页推荐接口
]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local underscore = require("underscore")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()


function home_rec(request, response)
	 -- 校验必填参数
	-- head参数
    local paramService = beanFactory:getBean("paramService")
    local headParam = paramService:getHeadParam()
	
	local loc = request:get_arg("loc")
	local score = tonumber(request:get_arg("score"))
	local limit = tonumber(request:get_arg("limit",20))
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local old_score = '+inf'
	if not str_util.isNil(score) and tonumber(score) >0 then
		old_score = score -1
	end 
	
	local home_rec_list = redis:zrevrangebyscore('rec:home:' .. loc .. ':3',old_score,'-inf','limit',0,limit) 
	local rec_list = underscore.map(home_rec_list,function(v)
			    local recInfo = table_util.array_to_hash(redis:hgetall('rec:tag:' .. loc .. ':3:'..v))
				if not str_util.isNil(recInfo) then
					local rec = {}
					rec['title'] = recInfo.conTitle or ''
					rec['type'] = recInfo.conType or ''
					rec['desc'] = recInfo.conDesc or ''
					rec['nid'] = recInfo.conId or ''
					local rec_type = tonumber(rec['type'])
					if  1== rec_type or 2 == rec_type then
						rec['musicians_info'] = getMusiciansInfo(recInfo.conId)
					elseif 2 == rec_type then
						rec['music_info'] = getMusicInfo(recInfo.conId)
					elseif 3 == rec_type then
						rec['story_info'] = getStoryInfo(recInfo.conId)
					end
					return rec
				end
			end
		)
	if _.isEmpty(rec_list) then
		response:writeln(json_util.exp("no nav list data found."))
		return
	end
	score = redis:zscore('rec:home:' .. loc .. ':3',home_rec_list[#home_rec_list])  or 0-- ZSCORE key member
	redis_util:close(redis)
	
	local rs = {
		reclist= rec_list or {},
		score = score 
	}
	response:writeln(json_util.success(rs,true))
	
end




function getMusiciansInfo(conId)
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

function getMusicInfo(conId)
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local artInfo = table_util.array_to_hash(redis:hgetall('art:song:info' .. conId))
	if  str_util.isNil(artInfo) then
		redis_util:close(redis)
		return nil
	end
	local music = {}
	music['art_id'] = conId  
	
	local musicInfo = table_util.array_to_hash(redis:hgetall('md:audio:' .. artInfo.music_id))
	
	music['mid'] = artInfo.music_id
	music['song_url'] = musicInfo.play_url
	music['song_name'] = musicInfo.audio_name
	local singer_id = musicInfo.owner_id
	music['singer_id'] = musicInfo.singer_id
	local userService = beanFactory:getBean("userService")
	local singerInfo = userService:getUserInfo(singer_id)
	music['singer'] = singerInfo.username
	music['singer_pic'] = singerInfo.picurl
	music['song_desc'] = musicInfo.introduce
	music['thumbnail'] = musicInfo.thumbnail
	music['audioDuration'] = musicInfo.duration
	music['play_num'] = musicInfo.play_num
	music['pubtime'] = musicInfo.created_time
	return music
end


function getStoryInfo(conId)
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local storyInfo = table_util.array_to_hash(redis:hgetall('story:info:' .. conId))
	if  str_util.isNil(storyInfo) then
		redis_util:close(redis)
		return nil
	end 
	local story = {}	
	story['art_id'] = storyInfo.art_id or ''
	story['title'] = storyInfo.title or ''
	local customer_uid = storyInfo.customer_uid or ''
	local userService = beanFactory:getBean("userService")
	local customerInfo = userService:getUserInfo(customer_uid)
	story['user_info'] = {
		uid = customerInfo.uid,
		uname = customerInfo.username,
		head_pic = customerInfo.picurl
	}
		
	local producer_id = storyInfo.cus_id or '' -- 定制师
	local producerInfo = userService:getUserInfo(producer_id)
	story['producer_info'] = {
		uid = producerInfo.uid,
		uname = producerInfo.username,
		head_pic = producerInfo.picurl,
		role = redis:hget('user:role',producerInfo.roleid),
		phone = producerInfo.phone
	}
	local mus_id = storyInfo.mus_id or ''
	local musiciansInfo = userService:getUserInfo(mus_id)
	story['musicians_info'] = {
		uid = musiciansInfo.uid,
		uname = musiciansInfo.username,
		head_pic = musiciansInfo.picurl,
		role = redis:hget('user:role',musiciansInfo.roleid),
		phone = musiciansInfo.phone
	}
		
	story['thumbnail'] = storyInfo.thumbnail or ''
	story['created_time'] = storyInfo.created_time or ''
	story['story_id'] = storyInfo.story_id or ''
	redis_util:close(redis)
	return story
end