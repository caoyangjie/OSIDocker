#!/usr/bin/env lua
--[[
	主页个人信息+动态数+关注数+粉丝数
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")

function domainshow(request, response)
	
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
	param["ouid"]= request:get_arg("ouid");
	
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	--[[ 验证sign --]]
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
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
	
	--[[ 判断是个人 还是 他人 1：个人 0 他人 --]]
	local is_self = 1
	local uid = param["uid"]
	if not str_util.isnull(param["ouid"]) and param["ouid"] ~= uid then
		uid = param["ouid"]
		is_self = 0
	end
	
	--[[ 查看用户是否存在 --]]
	local user,err = getuser(uid,redis)
	if user == false then
		redis_util:close(redis)
		response:writeln(json_util.fail(err))
		return
	end
	
	--[[ 数据处理 --]]
	local result = {}
	local data =  userdata()
	data=table_util.arryunion(data,user)
	
	--[[ 查看是否关注 --]]
	isfollow(is_self,param,data,redis)
	
	--[[ 关注数 --]]
	local focusnum = redis:zcard('user:following:'..uid)
	
	--[[ 粉丝数 --]]
	local followersnum = redis:zcard('user:followers:'..uid)
	
	--[[ 动态数 --]]
	local feedsnum = getfeedsnum(is_self,param,redis)

	result['userinfor'] = data
	result['focusnum'] = focusnum or 0
	result['followersnum'] = followersnum or 0
	result['feedsnum'] = feedsnum or 0

	--[[ 返回 --]]
	redis_util:close(redis)
	response:writeln(json_util.success(result))
	return 
end

--[[ 查看是否关注 --]]
function isfollow (is_self,param,data,redis)
	local is_follow = is_self==1 and 0 or (not str_util.isnull(redis:zrank('user:following:'..param["uid"],param["ouid"])) and  1 or 0 )
	if tonumber(is_follow) > 0 then
		is_follow = 1
	end
	data['is_follow'] = is_follow or 0
end

--[[ 获取个人信息--]]
function getuser(uid,redis)
	--[[ 查看用户是否存在 --]]
	local user = userinfo_util.get_user_info(uid,redis)
	
	if str_util.isnull(user) then
		return false,'账号不存在'
	end
	
	if not str_util.isnull(user) then
		if tonumber(user['status']) == 4 then
			return false ,'账号已拉黑，无法查看主页!'
		end
	end
	
	return user
end

--[[ 取动态数 --]]
function getfeedsnum(is_self,param,redis)
	local feedsnum = 0
	if is_self == 0 then
		feedsnum = redis:zcard('feed:outbox:'..param['ouid'])
	else
		local ulist = redis:zrange('user:following:'..param['uid'],0,-1,'WITHSCORES')
		if str_util.isnull(ulist) or ulist == false or type(ulist) == 'boolean' or #ulist<=0 then
			feedsnum = 0
		else
			ulist = table_util.array_to_hash(ulist)
			for k,v in pairs(ulist) do 
				local fcount  = redis:zcount('feed:outbox:'..k,v,'+inf')
				feedsnum = feedsnum + (fcount or 0)
			end
		end
		local mycount = redis:zcard('feed:outbox:'..param['uid'])
		feedsnum = feedsnum + (mycount or 0)
	end 
	return (feedsnum or 0)
end 

function userdata() 
	local o = {}
	o['username'] = ''
	o['picurl'] = ''
	o['homepic'] = ''
	o['usertype'] = ''
	o['is_follow'] = ''
	return o
end
