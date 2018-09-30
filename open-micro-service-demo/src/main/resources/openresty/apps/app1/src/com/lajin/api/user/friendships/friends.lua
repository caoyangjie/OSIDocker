#!/usr/bin/env lua
--[[
我的我的好友，包括好友、粉丝、关注
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function friends(request, response)
	getUsers(request,response,1)
end
function followers(request, response)
	getUsers(request,response,2)
end
function followings(request, response)
	getUsers(request,response,3)
end

function getUsers(request, response,friendType)
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
	param["ouid"] = request:get_arg("ouid");
	param["count"] = request:get_arg("count");
	param["score"] = request:get_arg("score");

	--[[ 参数检查--]]
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
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
	local httpclient = require("luastar.util.httpclient")
	local friendInnerApi
	if friendType == 1 then
		--好友
		friendInnerApi = "/inner/app/uc/friendship/friends"
	elseif friendType == 2 then
		--粉丝
		friendInnerApi = "/inner/app/uc/friendship/fans"
	elseif friendType == 3 then
		--关注
		friendInnerApi = "/inner/app/uc/friendship/concerns"
	end
	logger:info("friendInnerApi="..friendInnerApi)
		
	ok, code, headers, status, body = httpclient.request(luastar_config.getConfig('httproot')['address']..friendInnerApi,"POST",param,1500000,{})
	if tonumber(code) ~= 200 then
		logger:info("inner api call error")
		response:writeln(json_util.fail('http request error'))
		redis_util:close(redis)
		return
	end
	local responseBody = cjson.decode(body)
	if responseBody["code"] ~="A000000" then
		response:writeln(json_util.fail(responseBody['message']))
		redis_util:close(redis)
		return
	end
	
	local strUtils = require("luastar.util.str")
	local innerdate = cjson.decode(body)["data"]
	local innerList = innerdate.friendlist;
	local friendlist = {}
	for i=1,#innerList do
		local friend = {}
		friend.uid = innerList[i].id
		friend.username = innerList[i].username
		friend.picurl = innerList[i].picUrl
		friend.usertype = innerList[i].userRole
		friend.userrole = getRoles(redis,friend.uid)
		if nil ~= innerList[i].phone then
			friend.phone = strUtils.md5(innerList[i].phone)
		else
			friend.phone = ""
		end
		friendlist[i] = friend
	end
	redis_util:close(redis)
	
	local result_map = {}
	result_map["score"] = innerdate["score"]
	result_map["leftdatanum"] = innerdate["leftdatanum"]
	result_map["friendlist"] = friendlist
    local result_json = json_util.success(result_map)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end


--获取音乐人角色名字
function getRoles(redis,uid)
	local userrole = ""
	local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
	if nil ~= userinfo and "1" == userinfo.usertype and nil ~= userinfo.userrole and ""~= userinfo.userrole then	
		local rolehash = table_util.array_to_hash(redis:hgetall("musician:role"))
		userrole = _.map(str_util.split(userinfo.userrole,","),function(k,v)  return rolehash[v] end)
		userrole = table.concat(userrole,",")
		logger:info("userrole========="..userrole)
	end
	return userrole
end

