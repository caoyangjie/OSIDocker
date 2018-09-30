#!/usr/bin/env lua
--[[
	报名列表
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")

function participants(request, response)
	
	--[[ 私有参数校验 --]]
	local param = {}
	param["utoken"] = request:get_arg("utoken") or '';
	param["uid"] = request:get_arg("uid") or '';
	param["aid"]= request:get_arg("aid") or '';
	param["count"]= request:get_arg("count");
	param["score"]= request:get_arg("score");
	param["callback"]= request:get_arg("callback");

	--[[ 校验必填参数 --]]
	local  is_ok = _.any(_.pick(param,"utoken","uid","aid"),function(v) if v ==nil or ""==v then return  true end   end)
	logger:info("  param is ok2 "..tostring(is_ok))
	if is_ok then
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
	
	local activity = {['applyfriendlist']={},['score']='',['leftdatanum']=''}
	--[[ 业务逻辑 --]]
	local outboxList,left_score,leftdatanum = activityPaging(param["aid"],param["score"],param["count"],redis)
	activity['score'] = left_score or 0
	activity['leftdatanum'] = leftdatanum or 0
	
	if not str_util.isnull(outboxList) and  #outboxList >0 and outboxList ~= false then
		for k,v in pairs(outboxList) do
			local user = userinfo_util.get_user_info(v,redis)
			if not str_util.isnull(user) then
				local info = userresult(v)
				table_util.arryunion(info,user)
				info['uid'] = v
				table.insert(activity['applyfriendlist'],info)
			end
		end
	end
		
	local res = json_util.formatesuccess(activity)
	if nil ~= param["callback"] and ""~= param["callback"] then
		res = param["callback"] .."("..res..")"
	end
	
	--[[ 返回 --]]
	redis_util:close(redis)
	response:writeln(res)
	return 
end

--[[ 分页 --]]
function activityPaging(aid,score,count,redis)
	local outboxList = {}
	if str_util.isnull(count) or tonumber(count) <= 0  then count = 32 end
	if not str_util.isnull(score) and tonumber(score)>0 then
	    outboxList =  redis:zrevrangebyscore("activity:apply:"..aid,score,"-inf","LIMIT",1,count)
	else  
		outboxList =  redis:zrevrangebyscore("activity:apply:"..aid,"+inf","-inf","LIMIT",0,count)
	end
	local left_score = 0
	local leftdatanum = 0
	if outboxList ~= false and #outboxList > 0 then
		left_score =  redis:zscore("activity:apply:"..aid,outboxList[#outboxList])
		if str_util.isnull(left_score) or type(left_score) == "boolean" then
			left_score = 0
			end
		leftdatanum = redis:zcount("activity:apply:"..aid,"-inf",left_score) 
		if str_util.isnull(left_score) or type(leftdatanum) == "boolean" or tonumber(leftdatanum)==0 then
			leftdatanum = 0
		else
			leftdatanum = leftdatanum -1
		end
	end
	logger:info(leftdatanum)
	return outboxList,left_score,leftdatanum
end

--[[ data--]]
function userresult()
	local o = {}
	o['uid']=''
	o['username']=''
	o['picurl']=''
	return o
end

