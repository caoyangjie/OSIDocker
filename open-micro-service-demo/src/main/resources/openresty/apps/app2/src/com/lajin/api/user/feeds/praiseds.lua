#!/usr/bin/env lua
--[[
	点赞列表和大咖赞台列表
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")

function praiseds(request, response)
	
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
	param["utoken"] = request:get_arg("utoken") or '';
	param["uid"] = request:get_arg("uid") or '';
	param["fid"]= request:get_arg("fid") or '';
	param["flag"]= request:get_arg("flag") or '';
	param["count"]= request:get_arg("count");
	param["score"]= request:get_arg("score");

	--[[ 校验必填参数 --]]
	local  is_ok = _.any(_.pick(param,"utoken","uid","fid","flag"),function(v) if v ==nil or ""==v then return  true end   end)
	logger:info("  param is ok2 "..tostring(is_ok))
	
	if is_ok then
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
	
	--[[ 业务逻辑 --]]

	local goodlist = {['goodlist']={},['score']='',['leftdatanum']=''}
	
	local key
	if tonumber(param["flag"]) == 1 then
		key = 'feed:good:higherups:'
	else
		key = 'feed:good:comm:'
	end
	
	--[[ 分页 --]]
	local outboxList,left_score,leftdatanum = paging(param["fid"],param["score"],param["count"],key,redis)
	
	goodlist['score'] = left_score or 0
	goodlist['leftdatanum'] = leftdatanum or 0
	
	if not str_util.isnull(outboxList) and outboxList ~= false and  #outboxList >0 then
		for k,v in pairs(outboxList) do
			local user = userinfo_util.get_user_info(v,redis)
			if not str_util.isnull(user) then
				local info = userresult()
				table_util.arryunion(info,user)
				info['uid'] = v
				table.insert(goodlist['goodlist'],info)
			end
		end
	end
	
	--[[ 返回 --]]
	redis_util:close(redis)	
	response:writeln(json_util.formatesuccess(goodlist))
	return 
end

--[[ 分页 --]]
function paging(fid,score,count,key,redis)
	local outboxList = {}
	if str_util.isnull(count) or tonumber(count) <= 0  then count = 48 end
	if not str_util.isnull(score) and tonumber(score)>0 then
	    outboxList =  redis:zrevrangebyscore(key..fid,score,"-inf","LIMIT",1,count)
	else  
		outboxList =  redis:zrevrangebyscore(key..fid,"+inf","-inf","LIMIT",0,count)
	end
	local left_score = 0
	local leftdatanum = 0
	if outboxList ~= false and #outboxList > 0 then
		left_score =  redis:zscore(key..fid,outboxList[#outboxList])
		if str_util.isnull(left_score) or type(left_score) == "boolean" then
			left_score = 0
		end
		leftdatanum = redis:zcount(key..fid,"-inf",left_score) 
		if str_util.isnull(left_score) or type(leftdatanum) == "boolean" or tonumber(leftdatanum)==0 then
			leftdatanum = 0
		else
			leftdatanum = leftdatanum -1
		end
	end
	return outboxList,left_score,leftdatanum
end

--[[ data--]]
function userresult()
	local o = {}
	o['uid']=''
	o['username']=''
	o['picurl']=''
	o['usertype'] =''
	return o
end

