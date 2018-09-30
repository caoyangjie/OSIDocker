#!/usr/bin/env lua
--[[
我的消息数
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function num(request, response)
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
	local num1 = getmsgnum(1,uid,redis) 
	local num2 = getmsgnum(2,uid,redis) 
	local num3 = getmsgnum(3,uid,redis) 
	local num4 = getmsgnum(4,uid,redis) 
	
	redis_util:close(redis)
	local data = {}
	data.msgnun = num1 + num2 + num3 + num4
	local result_json = json_util.formatesuccess(data)
	response:writeln(result_json)
end

function getmsgnum(msgtype,uid,redis)
	local scorekey = get_msg_score_key(msgtype,uid)
	
	local score = redis:get(scorekey)
	if str_util.isnull(score) then
		score = "-inf"
	else
		score = score + 1
	end
	local msgkey = getmsgkey(msgtype,uid)
	local num = redis:zcount(msgkey,score,"+inf") 
	logger:info("msgtype="..msgtype..",score="..score..",num="..num)
	if  type(num) ==nil or type(num) == "boolean" then
		num = 0
	end
	return num
end


function getmsgkey(msgtype,uid)
	local msgkey = ""
	if msgtype == 1 then
		msgkey = "user:msg:inbox:import:"..uid
	elseif msgtype == 2 then
		msgkey = "user:msg:inbox:common:"..uid
	elseif msgtype == 3 then
		msgkey = "user:msg:inbox:rec:"..uid
	elseif msgtype == 4 then
		msgkey = "user:msg:inbox:auth:"..uid
	end
	return msgkey
end
function get_msg_score_key(msgtype,uid)
	local msgkey = ""
	if msgtype == 1 then
		msgkey = "user:msg:inbox:import:"..uid..":readscore"
	elseif msgtype == 2 then
		msgkey = "user:msg:inbox:common:"..uid..":readscore"
	elseif msgtype == 3 then
		msgkey = "user:msg:inbox:rec:"..uid..":readscore"
	elseif msgtype == 4 then
		msgkey = "user:msg:inbox:auth:"..uid..":readscore"
	end
	return msgkey
end