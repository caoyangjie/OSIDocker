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

function activitys(request, response)
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	--[[ head参数校验--]]
	if not isHead then
		logger:i(" Head  args is null")
		response:writeln(json_util.illegal_argument())
		return
	end

	--[[ 私有参数校验 --]]
	local param = {}

	param["utoken"] = request:get_arg("utoken");
	param["uid"] = request:get_arg("uid");

	--[[ 验证sign --]]
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end

	--[[ 参数检查--]]
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
		response:writeln(json_util.illegal_argument())
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
	local pub_array = redis:zrevrange("activity:pub:"..uid,0,-1)
	-- 获取参加活动的列表
	local join_array = redis:zrevrange("activity:join:"..uid,0,-1)
	
	local data = {}
	local pub_list = {}
	local join_list = {}
	
	if table.getn(pub_array) > 0 then
		local index = 1
		for i=1, #(pub_array) do
			local pub_str = {}
			local activityId = pub_array[i]
			local activityInfo = table_util.array_to_hash(redis:hgetall("activity:info:"..activityId))
			if str_util.isnull(activityInfo) then
				logger:info("activityInfo is nil,activityId="..activityId)
			else
				pub_str.aid = tonumber(activityId)
				pub_str.acturl = activityInfo.acturl or ""
				pub_str.actname = activityInfo.actname or ""
				--resultinfo['picurl'] = getpicurl(info['pid'],redis)
				pub_str.picurl = getpicurl(activityInfo.pid,redis)
				pub_str.status = tonumber(activityInfo.status) or -1
				pub_str.acttime = activityInfo.acttime or ""
				pub_list[index] = pub_str
				index = index + 1
			end
	    end
		data.pubactivitylist = pub_list
	else
	end
	
	if table.getn(join_array) > 0 then
		local index = 1
		for i=1, #(join_array) do
			local join_str = {}
			local activityId = join_array[i]
			local activityInfo = table_util.array_to_hash(redis:hgetall("activity:info:"..activityId))
			if str_util.isnull(activityInfo) then
				logger:info("activityInfo is nil,activityId="..activityId)
			else
				join_str.aid = tonumber(activityId)
				join_str.acturl = activityInfo.acturl or ""
				join_str.actname = activityInfo.actname or ""
				--join_str.picurl = activityInfo.picurl or ""
				join_str.picurl = getpicurl(activityInfo.pid,redis)
				join_str.status = tonumber(activityInfo.status) or -1
				join_str.acttime = activityInfo.acttime or ""
				join_list[index] = join_str
				index = index + 1
			end
	    end
		data.joinactivitylist = join_list
	else
	end
	
	local result_json = json_util.formatesuccess(data)
	-- 关闭redis
	redis_util:close(redis)
	response:writeln(result_json)
end

function getpicurl(uuid,redis)
	local pic_url =''
	if not str_util.isnull(uuid) then
		pic_url =  redis:hget('pub:image:'..uuid,'pic_url')
	end
	return pic_url
end