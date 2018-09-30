#!/usr/bin/env lua
--[[
大咖用户作品列表
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local http = require "resty.http"
local str_util = require("luastar.util.str")
function opus(request, response)
	local hc = http:new()
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
	param["ouid"] = request:get_arg("ouid");
	
	--[[ 参数检查--]]
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
		logger:info(" utoken or uid is null")
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
	redis_util:close(redis)
	if not ok then
		response:writeln(json_util.fail(emsg))
		return
	end
	
	local httpclient = require("luastar.util.httpclient")
	ok, code, headers, status, body = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/app/uc/user/opus","POST",param,1500000,{})
	
	if tonumber(code) ~= 200 then
		logger:info("inner api call error")
		response:writeln(json_util.fail('http request error'))
		return
	end
	logger:info(body)
	local responseBody = cjson.decode(body)
	if responseBody["code"] ~="A000000" then
		response:writeln(json_util.fail(responseBody['message']))
		return
	end
	
	local opusdate = responseBody["data"]
	local opuslist = {}
	logger:info("1111111")
	for i=1,#opusdate do
		local opus = {}
		opus.fid = opusdate[i].fid
		opus.mid = opusdate[i].aid
		opus.songname = opusdate[i].audioName
		opus.higherupsnum = opusdate[i].goodNum
		opus.thumbnail = opusdate[i].picUrl
		opus.pubtime = opusdate[i].pubTime
		
		opuslist[i] = opus
	end
	logger:info("222222222")
	
	local result_map = {}
	result_map["opuslist"]=opuslist
    local result_json = json_util.success(result_map)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end
