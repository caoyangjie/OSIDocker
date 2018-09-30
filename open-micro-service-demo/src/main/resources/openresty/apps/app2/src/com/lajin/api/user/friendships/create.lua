#!/usr/bin/env lua
--[[
批量关注、取消关注
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local http = require "resty.http"
local str_util = require("luastar.util.str")
function create(request, response)
	local hc = http:new()
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
	param["fuids"]= request:get_arg("fuids");
	param["flag"] = request:get_arg("flag");
	
    if str_util.isnull(param["uid"]) or str_util.isnull(param["fuids"]) or str_util.isnull(param["flag"]) then
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
		response:writeln(json_util.illegal_token(emsg))
		redis_util:close(redis)
		return
	end
	if(param["flag"] ~= "0" and param["flag"] ~= "1") then
		logger:info("flag value is invalid,value="..param["flag"])
		response:writeln(json_util.illegal_argument())
        return
	end
	
	logger:info("send msg,flag="..param["flag"]..",uid="..param["uid"]..",fuids="..param["fuids"])
		
	--[[开始业务处理--]]
	local httpclient = require("luastar.util.httpclient")
	param["bizId"] = luastar_config.getConfig('msg')['biz_id_concern'];
	ok, code, headers, status, body = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/msg/send","POST",param,1500000,{})
	if code ~= 200  then
		logger:info('http request error')
		response:writeln(json_util.fail('http request error'))
		return
	end
	
	local responseBody = cjson.decode(body)
	if responseBody["code"] ~="A000000" then
		logger:info(responseBody['message'])
		response:writeln(json_util.fail(responseBody['message']))
		return
	end
	
    local fuids = param["fuids"]
	--根据逗号分割获取
	local fuid_array = str_util.split(fuids,",",0)
	if fuid_array ~= nil and table.getn(fuid_array) > 0 then
		for i=1, #(fuid_array) do
            local fuid = fuid_array[i]
			concern(request,response,redis,param["uid"],fuid,param["flag"])
		end
		redis_util:close(redis)
	else
		response:writeln(json_util.illegal_argument())
		redis_util:close(redis)
        return
	end
	
	response:writeln(json_util.success({}))
end

--关注
function concern(request,response,redis,uid,fuid,flag)
	logger:info("start process uid="..uid..",fuid="..fuid)
	if(uid == fuid) then
		logger:info("uid cant't equals fuid")
		return
	end
	
	if flag == "0" then
		--删除关注记录、粉丝记录、好友记录（假如有）
		redis:zrem("user:following:"..uid,fuid)
		redis:zrem("user:followers:"..fuid,uid)
		redis:zrem("user:friends:"..uid,fuid)
		redis:zrem("user:friends:"..fuid,uid)
		logger:info("delete following,uid="..uid..",fuid="..fuid);
		logger:info("delete following,uid="..fuid..",fuid="..uid);
		logger:info("delete friend:"..uid..","..fuid);
	elseif flag == "1" then
		--验证用户是否存在
		local fuser_exists = redis:exists("user:info:"..fuid)
		local user_exists = redis:exists("user:info:"..uid)
		if user_exists == 0 or fuser_exists == 0 then
			logger:info("user is nil or fuser is nil")
			return
		end
		--插入关注、插入粉丝
		redis:zadd("user:following:"..uid,os.time(),fuid)
		redis:zadd("user:followers:"..fuid,os.time(),uid)
		logger:info("insert following,uid"..uid..",fuid="..fuid)
		logger:info("insert followers,uid"..fuid..",fuid="..uid)
		
		local follower = redis:zscore("user:following:"..fuid,uid)
		if  type(follower) == "string" then
			--假如互相关注，插入好友记录
			redis:zadd("user:friends:"..uid,os.time(),fuid)
			redis:zadd("user:friends:"..fuid,os.time(),uid)
			logger:info("insert friend,"..uid..","..fuid)
		end
		send_msg_concern(uid,fuid,redis)
	end
end

--发送关注消息
function send_msg_concern(uid,fuid,redis)
	local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
	local fuser = table_util.array_to_hash(redis:hgetall("user:info:"..fuid))
	
	local msgInfo = {}
	local msgid,msgid_err = redis:incr("user:msg:ids")
	msgInfo["sender_uid"] = uid
	msgInfo["receiver_uid"] = fuid
	--双方都未大咖才是重要消息
	if "1" == userinfo.usertype and "1" == fuser.usertype then	
		msgInfo["type"] = 1
	else
		msgInfo["type"] = 2
	end
	msgInfo["opt_type"] = 1
	msgInfo["fid"] = ""
	msgInfo["text"] = ""
	msgInfo["created_time"] = os.time()
	
	logger:info("insert user:msg:info:"..msgid)
	local msg_ok,msg_err = redis:hmset("user:msg:info:"..msgid,msgInfo)
	
	if msgInfo["type"] == 1 then
		redis:zadd("user:msg:inbox:import:"..fuid,msgid,msgid)
	else
		redis:zadd("user:msg:inbox:common:"..fuid,msgid,msgid)
	end
	
	
	
	return true
end