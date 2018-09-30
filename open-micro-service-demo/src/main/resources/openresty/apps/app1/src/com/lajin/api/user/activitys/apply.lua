#!/usr/bin/env lua
--[[
	参加活动
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
function apply(request, response)
	
	--[[ 私有参数校验 --]]
	local param = {}
	param["utoken"] = request:get_arg("utoken");
	param["uid"] = request:get_arg("uid");
	param["aid"]= request:get_arg("aid");
	param["callback"]= request:get_arg("callback");
	
    if str_util.isnull(param["uid"]) or str_util.isnull(param["utoken"]) or str_util.isnull(param["aid"]) then
        response:writeln(json_util.illegal_argument())
        return
    end
		
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	
	--[[ 校验token--]]
	local loginProcess = beanFactory:getBean("loginProcess")
	local ok ,err = loginProcess:checktoken(param,redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(err))
		return
	end
	
	--[[  检查是否具有报名资格--]]
	ok ,err = check(param["uid"],param["aid"],redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.fail(err))
		return
	end

	local httpclient = require("luastar.util.httpclient")
	param["bizId"] = luastar_config.getConfig('msg')['biz_aid_apply'];
	ok, code, headers, status, body = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/msg/send","POST",param,1500000,{})
	
	if tonumber(code) ~= 200 or str_util.isnull(body) then
		redis_util:close(redis)
		response:writeln(json_util.fail('http request error'))
		return
	end
	
	local responseBody = cjson.decode(body)
	if responseBody["code"] ~="A000000" then
		redis_util:close(redis)
		response:writeln(json_util.fail(responseBody['message']))
		return
	end
	ok ,err = activityjoin(param["uid"],param["aid"],redis)
	ok ,err = activityapply(param["uid"],param["aid"],redis)
	
	local resultinfo = 	json_util.success()
	if nil ~= param["callback"] and ""~= param["callback"] then
		resultinfo = param["callback"] .."("..resultinfo..")"
	end
	redis_util:close(redis)
	response:writeln(resultinfo)
	return
end

--[[ 写用户报名动态缓存--]]
function activityjoin(uid,aid,redis)
	local ok ,err = redis:zadd('activity:join:'..uid,aid,aid)
	return ok,err
end

--[[ 写活动报名列表缓存--]]
function activityapply(uid,aid,redis)
	local ok ,err = redis:zadd('activity:apply:'..aid,uid,uid)
	return ok,err
end

--[[  检查是否具有报名资格--]]
function check(uid,aid,redis)
	local info = redis:hgetall('activity:info:'..aid)
	local isok = redis:zscore('activity:apply:'..aid,uid)
	if str_util.isnull(info) or type(info) == 'boolean'then
		return false,'活动不存在'
	else
		info = table_util.array_to_hash(info)
		if tonumber(info['uid']) == tonumber(uid) then
			return false,'自己的活动不能报名'
		elseif tonumber(info['status']) == 2 or is_isapplyend(info['applyendtime'],redis) == 1 then
			return false,'活动已经取消或者报名已经截止'
		elseif not str_util.isnull(isok) then
			return false,'已经报名的活动不能报名'
		else
			return true
		end
	end
end

--[[ 用报名截止时间 算出 isapplyend 的值--]]
function is_isapplyend(applyendtime,redis)
	local isapplyend = 0
	if not str_util.isnull(applyendtime) then
		--local time = date_util.GetTimeByDate(applyendtime)
		local applyendtime = tonumber(applyendtime) - os.time()
		if applyendtime <=0 then
			isapplyend = 1
		end
	end
	return isapplyend
end
