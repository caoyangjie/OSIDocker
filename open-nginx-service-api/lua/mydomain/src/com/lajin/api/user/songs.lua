#!/usr/bin/env lua
--[[
我的活动列表
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function songs(request, response)
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	--[[ head参数校验--]]
	if not isHead then
		logger:info(" Head  args is null")
		response:writeln(json_util.illegal_argument())
		return
	end
	
	--[[ 私有参数校验 --]]
	local param = {}
	param["utoken"] = request:get_arg("utoken");
	param["uid"] = request:get_arg("uid");

	--[[ 参数检查--]]
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
		logger:info("utoken or uid is null")
		response:writeln(json_util.illegal_argument())
		return
	end

	--[[ 验证sign --]]
	local isSign = check:sign(param)
	if not isSign then
		logger:info(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end

	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()

	--[[ 校验token--]]
	local loginProcess = beanFactory:getBean("loginProcess")
	local ok ,emsg = loginProcess:checktoken(param,redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(emsg))
		return
	end
	
	-- 业务代码start
	local uid = param["uid"]
	-- 获取发布活动列表
	local song_array = redis:zrevrange("user:songs:"..uid,0,-1)
	logger:info("songs num is "..#(song_array))
	
	local song_list = {}
	--循环获取歌曲信息和歌手姓名
	if table.getn(song_array) > 0 then
		for i=1, #(song_array) do
			local song = {}
			local songId = song_array[i]
			local songInfo = table_util.array_to_hash(redis:hgetall("md:audio:"..songId))
			
			if nil ~= songInfo then
				song.mid = songId
				song.songname = songInfo.audio_name
				local userInfo = table_util.array_to_hash(redis:hgetall("user:info:"..songInfo.uid))
				if next(userInfo) == nil then
					song.singer = ""
					logger:info("userInfo is nil,uid="..songInfo.uid)
				else
					song.singer = userInfo.username
				end
				table.insert(song_list,song)
				--song_list[table.getn(song_list)+1] = song
			end
	    end
	end
	redis_util:close(redis)
	local data = {}
	data.songlist = song_list
	local result_json = json_util.formatesuccess(data)
	result_json = string.gsub(result_json,"{}","[]")
	response:writeln(result_json)
end