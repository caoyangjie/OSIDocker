#!/usr/bin/env lua
--[[
	活动详细信息
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")
local date_util = require("luastar.util.date")
function show(request, response)
	
	--[[ 私有参数校验 --]]
	local param = {}
	param["utoken"] = request:get_arg("utoken");
	param["uid"] = request:get_arg("uid");
	param["aid"]= request:get_arg("aid");
	param["callback"]= request:get_arg("callback");
	
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) or str_util.isnull(param["aid"]) then
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
	
	--[[ 业务逻辑--]]
	local resultinfo = activitydata(param["aid"])
	--[[ 活动详情 --]]
	local info = getactivityinfo(param["aid"],redis)
	if str_util.isnull(info) then
		redis_util:close(redis)
		response:writeln(json_util.fail('未找到活动!'))
		return
	end
	resultinfo=table_util.arryunion(resultinfo,info)
	
	--[[ 找到图片地址--]]
	resultinfo['picurl'] = getpicurl(info['pid'],redis)
	
	--[[ 用报名截止时间 算出 isapplyend 的值--]]
	resultinfo['isapplyend']  = is_isapplyend(info['applyendtime'],redis)
	
	--[[ 从缓存中找到用户报名活动的id 算出 isapply 的值 --]]
	resultinfo['isapply'] = is_isapply(param["uid"],param["aid"],redis)
	
	--[[ 获得好友列表 --]]
	local outboxList = getfriends(param["aid"],param["uid"],redis)
	if not str_util.isnull(outboxList) and outboxList ~= false and  #outboxList >0  then
		for k,v in pairs(outboxList) do
			local user = userinfo_util.get_user_info(v,redis)
			if not str_util.isnull(user) then
				local info = userresult()
				table_util.arryunion(info,user)
				info['uid'] = v
				table.insert(resultinfo['applyfriendlist'],info)
			end
		end
	end

	--[[ 返回 --]]
	redis_util:close(redis)
	local res = json_util.formatesuccess(resultinfo)
	if nil ~= param["callback"] and ""~= param["callback"] then
		res = param["callback"] .."("..res..")"
	end
	response:writeln(res)
	return 
end

--[[ 获得缓存中得活动详情 --]]
function getactivityinfo(uid,redis)
	local info = redis:hgetall('activity:info:'..uid)
	info = table_util.array_to_hash(info)
	return info
end

--[[ 找到图片地址--]]
function getpicurl(uuid,redis)
	local pic_url =''
	if not str_util.isnull(uuid) then
		pic_url =  redis:hget('pub:image:'..uuid,'pic_url')
	end
	return pic_url
end

--[[ 用报名截止时间 算出 isapplyend 的值--]]
function is_isapplyend(applyendtime,redis)
	local isapplyend = 0
	if not str_util.isnull(applyendtime) then
		--local time = date_util.GetTimeByDate(applyendtime)
		if (applyendtime - os.time()) <=0 then
			isapplyend = 1
		end
	end
	return isapplyend
end

--[[ 是否报名活动 --]]
function is_isapply(uid,aid,redis)
	local isapply = redis:zscore('activity:join:'..uid,aid)
	if str_util.isnull(isapply) then
		isapply = 0
	else
		isapply = 1
	end
	return isapply
end

--[[ 获得好友列表 --]]
function getfriends(aid,uid,redis)
	local outboxList = {}
	local friends =  redis:zinterstore('activity:friends:'..uid,2,'activity:apply:'..aid,'user:friends:'..uid,'aggregate','min')
	if friends ~= false and tonumber(friends) > 0 then
		outboxList =  redis:zrevrangebyscore("activity:friends:"..uid,"+inf","-inf","LIMIT",0,-1)
		if outboxList == false or str_util.isnull(outboxList) or #outboxList <= 0 then
			outboxList = {}
		end
	end
	redis:del("activity:friends:"..uid)
	return outboxList
end

function activitydata(aid) 
	local o = {}
	o['aid'] = aid
	o['actname'] = ''
	o['picurl'] = ''
	o['acttime'] = ''
	o['actaddr'] = ''
	o['price'] = ''
	o['applyendtime'] = ''
	o['isapplyend'] = ''
	o['status'] = ''
	o['isapply'] = ''
	o['intro'] = ''
	o['desc'] = ''
	o['applyfriendlist']={}
	return o
end

--[[ data--]]
function userresult()
	local o = {}
	o['uid']=''
	o['username']=''
	o['picurl']=''
	return o
end
