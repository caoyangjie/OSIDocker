#!/usr/bin/env lua
--[[

]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local cjson = require("cjson")
local underscore = require("underscore")




function get_param(request)
	local param = {}
	param["utoken"] =request:get_arg("utoken") or ""
	param["uid"] =request:get_arg("uid") or ""
	param["fid"] = request:get_arg("fid") or ""
	param["ctype"] = request:get_arg("ctype") or ""
	param["count"] = request:get_arg("count")
	param["score"] = request:get_arg("score")
	logger:info(cjson.encode(param))
	return param
end

-- get comments 
function comments(request, response)
	-- check
	local beanFactory = luastar_context.getBeanFactory()
	local check = beanFactory:getBean("check")
	-- check head
	local is_ok = check:head()
	logger:info("head  is ok "..tostring(is_ok))
	if not is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	-- get param
	local utoken = request:get_arg("utoken") or ""
	local uid = tonumber(request:get_arg("uid")) or ""
	local param = get_param(request)
	
	-- check param is nil
	is_ok = _.any(_.pick(param,"utoken","uid","fid","ctype"),function(v) if v ==nil or ""==v then return  true end   end)
	logger:info(" pub feed  param is ok2 "..tostring(is_ok))
	if is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
    -- check sign
	is_ok = check:sign(param)
	logger:info(" get feeds  sign "..tostring(is_ok))
	if not is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	-- check utoken
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local loginProcess = beanFactory:getBean("loginProcess")
	local ok ,emsg = loginProcess:checktoken(param,redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(emsg))
		return
	end
	
    
	
	local comments,mscore,leftdatanum = get_commentlist_list(param["fid"],param["count"],param["score"],param["ctype"],redis)
	local res ={}
	res.commentlist = comments
	res.score = mscore
	logger:info(leftdatanum)
	res.leftdatanum = leftdatanum
	logger:info(res.leftdatanum)
	redis_util:close(redis)
	res = json_util.success(res)
	logger:info(res)
	res = res:gsub("{}", "[]")
	logger:info(res)
	response:writeln(res) 
end

function get_commentlist_list(fid,commcount,score,ctype,redis)
	local commentlist = {}
	local leftdatanum = 0
	local left_score = 0
	if tonumber(ctype) == 2 then  -- music
		commcount = commcount or 1
		commentlist,left_score,leftdatanum = get_comments(commcount,score,redis,"feed:comment:music:"..fid) -- music comment 
	else 
		commcount = commcount or 20
		commentlist,left_score,leftdatanum = get_comments(commcount,score,redis,"feed:comment:pub:"..fid)	-- pub comment
	end
	logger:info(cjson.encode(commentlist))
		
	return commentlist,left_score,leftdatanum
end




function get_comments(commcount,mscore,redis,redis_key)
	local commentlist = {}
	if nil == mscore or ""==mscore or tonumber(mscore) <=0 then
		mscore = "+inf"
	else
		mscore = mscore -1
	end
	logger:info(commcount)
	-- feed:comment:music:
	local comments ,err = redis:zrevrangebyscore(redis_key,mscore,"-inf","LIMIT",0,commcount)
	logger:info("music ----"..cjson.encode(comments))
	if nil == comments or type(comments) =="userdata"   or _.size(comments)==0 then
		return commentlist
	end
	
	local left_score =  redis:zscore(redis_key,comments[#comments])
		
	logger:info(left_score)
		
	local leftdatanum = redis:zcount(redis_key,"-inf",left_score) 
	logger:info(leftdatanum)
	
	
	if  type(leftdatanum) ==nil or type(leftdatanum) == "boolean" or tonumber(leftdatanum)==0 then
		leftdatanum = 0
	else
		leftdatanum = leftdatanum -1
	end
	
	if  type(left_score) ==nil or type(left_score) == "boolean" then
			left_score = 0
	end
	
	
	logger:info("pub ----"..cjson.encode(comments))
	commentlist = _.map(comments,function(k,v)
			local commentinfo = table_util.array_to_hash(redis:hgetall("feed:comment:info:"..v))
			local comment = {}
			if nil == commentinfo or _.size(commentinfo) ==0  or type(commentinfo) =="userdata" then
				return comment
			end 
			logger:info("commentinfo is ---".. cjson.encode(commentinfo))
			local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..commentinfo.uid))
			if nil ~= commentinfo.reply_uid then	
				local replyuserinfo = table_util.array_to_hash(redis:hgetall("user:info:"..commentinfo.reply_uid))
				comment.reply_uid = commentinfo.reply_uid
				comment.reply_uname  = replyuserinfo.username
				comment.reply_cid  = commentinfo.reply_cid
			end
			comment.cid = v
			comment.uid = commentinfo.uid
			comment.username = userinfo.username
			comment.usertype = userinfo.usertype
			comment.picurl = userinfo.picurl
			local userrole = ""
			if nil ~= userinfo.userrole and ""~= userinfo.userrole then	
				local rolehash = table_util.array_to_hash(redis:hgetall("musician:role"))
				userrole = _.map(str_util.split(userinfo.userrole,","),function(k,v)  return rolehash[v] end)
				userrole = table.concat(userrole,",")
			end
			logger:info(userrole)
			comment.userrole = userrole -- todo 
			comment.ctype = commentinfo.ctype
			comment.isreply = commentinfo.isreply
			comment.score  = commentinfo.score
			comment.words   = commentinfo.words
			comment.created_time   = commentinfo.created_time
			return comment
		end
	)

	logger:info(cjson.encode(commentlist))
	return commentlist,left_score,leftdatanum
end