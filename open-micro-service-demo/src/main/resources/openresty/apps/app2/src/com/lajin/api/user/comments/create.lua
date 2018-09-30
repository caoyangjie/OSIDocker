module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local cjson = require("cjson")
local underscore = require("underscore")


function create(request, response)
	-- local head = {}
	-- local datakey = request:get_header("datakey")
	-- local sign = request:get_header("sign")
	-- local ip = request.remote_addr
	-- check param and header
	local beanFactory = luastar_context.getBeanFactory()
	local check = beanFactory:getBean("check")
	
	local is_ok = check:head()
	logger:info("head  is ok "..tostring(is_ok))
	if not is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
    -- get param
	local utoken = request:get_arg("utoken")
	local uid = tonumber(request:get_arg("uid"))
    -- check sign
	local param = get_param(request)
	-- check param is nil
	is_ok = _.any(_.pick(param,"utoken","uid","fid","ctype","isreply","words"),function(v) if v ==nil or ""==v then return  true end   end)
	logger:info(" praised feed  param is ok2 "..tostring(is_ok))
	if is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	is_ok = check:sign(param)
	logger:info(" praised feed  sign "..tostring(is_ok))
	if not is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	if tonumber(param["ctype"]) == 2 and _.isNil(param["score"]) then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	if tonumber(param["isreply"]) == 1 then
		if _.isNil(param["reply_uid"]) or  _.isNil(param["reply_cid"]) then
			response:writeln(json_util.illegal_argument())
			return
		end
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
	
	local cmid,comment = save_comment_info(param,redis)
	----- todo------
	if tonumber(param["ctype"]) ==1 then  --1 pub comments
		local is_ok,err = redis:zadd("feed:comment:pub:"..param["fid"],cmid,cmid)
	elseif tonumber(param["ctype"]) ==2 then -- 2 music comments
		local is_ok,err = redis:zadd("feed:comment:music:"..param["fid"],cmid,cmid)
	else  
		redis_util:close(redis)
		response:writeln(json_util.illegal_argument())
		return
	end
	local userinfo = send_msg_friend(uid,param["fid"],cmid,param,redis)	
	local res = {
		cid = cmid,
		fid= param["fid"],
		uid = param["uid"],
		username = userinfo.username,
		picurl = userinfo.picurl ,        
		usertype = userinfo.usertype,
		userrole = get_user_role(userinfo.userrole,redis),
		ctype    = param["ctype"],
		isreply   = param["isreply"],
		reply_uid   = param["reply_uid"],
		reply_uname   = get_reply_user_info(param["reply_uid"],redis).username,
		reply_cid  = param["reply_cid"],
		score         = param["score"],
		words         = param["words"],
		created_time     = comment["created_time"]
	}
	redis_util:close(redis)
	response:writeln(json_util.success(res)) 
end

function get_reply_user_info(reply_uid,redis)
	local  replyuserinfo = {}
	if nil ~= reply_uid and ""~= reply_uid then
		replyuserinfo = table_util.array_to_hash(redis:hgetall("user:info:"..reply_uid))
	end
	return replyuserinfo
end

function get_user_role(userroleArr,redis)
	local userrole = ""
	if nil ~= userroleArr and ""~= userroleArr then	
		local rolehash = table_util.array_to_hash(redis:hgetall("musician:role"))
		userrole = _.map(str_util.split(userroleArr,","),function(k,v)  return rolehash[v] end)
		userrole = table.concat(userrole,",")
	end
	return userrole
end


function send_msg_friend(uid,feedid,cmid,param,redis)
	local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
	local feedInfo,err = table_util.array_to_hash(redis:hgetall("feed:info:"..feedid))
	local receiver_uid  = feedInfo.uid
	local receiver_userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..receiver_uid))
	local msgInfo = {}
	local msgid,msgid_err = redis:incr("user:msg:ids")
	msgInfo["sender_uid"] = uid
	msgInfo["receiver_uid"] = receiver_uid
	if tonumber(userinfo.usertype) == 1 and tonumber(receiver_userinfo.usertype) == 1 then
		-- 当前用户为大咖用户，并且 接受者也是大咖用户
		msgInfo["type"] = 1  -- 重要消息
	else 
		msgInfo["type"] = 2  -- 普通消息
	end
	msgInfo["fid"] = feedid
	msgInfo["text"] = param["words"] or ""
	msgInfo["created_time"] = ngx.time()
	msgInfo["cid"] = cmid
	
	if tonumber(param["isreply"])  ==1 then
		msgInfo["opt_type"] = 5   -- 3评论，4乐评、5回复
		send_msg_to_reply(uid,feedid,userinfo,param,redis)	
	elseif tonumber(param["ctype"]) ==2 then
		msgInfo["opt_type"] = 4
	else 
		msgInfo["opt_type"] = 3
	end
	local msg_ok,msg_err = redis:hmset("user:msg:info:"..msgid,msgInfo)
	if msgInfo["type"] == 1 then
		redis:zadd("user:msg:inbox:import:"..receiver_uid,msgid,msgid)
	else 
		redis:zadd("user:msg:inbox:common:"..receiver_uid,msgid,msgid)
	end
	return userinfo
end

function send_msg_to_reply(uid,feedid,userinfo,param,redis)
	local receiver_uid  = param["reply_uid"]
	local receiver_userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..receiver_uid))
	local msgInfo = {}
	local msgid,msgid_err = redis:incr("user:msg:ids")
	msgInfo["sender_uid"] = uid
	msgInfo["receiver_uid"] = receiver_uid
	if tonumber(userinfo.usertype) == 1 and tonumber(receiver_userinfo.usertype) == 1 then
		-- 当前用户为大咖用户，并且 接受者也是大咖用户
		msgInfo["type"] = 1  -- 重要消息
	else 
		msgInfo["type"] = 2  -- 普通消息
	end
	msgInfo["fid"] = feedid
	msgInfo["text"] = param["words"] or ""
	msgInfo["created_time"] = ngx.time()
	msgInfo["opt_type"] = 5   -- 3评论，4乐评、5回复
	
	local msg_ok,msg_err = redis:hmset("user:msg:info:"..msgid,msgInfo)
	if msgInfo["type"] == 1 then
		redis:zadd("user:msg:inbox:import:"..receiver_uid,msgid,msgid)
	else 
		redis:zadd("user:msg:inbox:common:"..receiver_uid,msgid,msgid)
	end
end

function save_comment_info(param,redis)
	local cmid = redis:incr("feed:comment:ids")
	local comment = {}
	comment["uid"] =param["uid"]
	local fid  = param["fid"]
	comment["ctype"] = param["ctype"]
	comment["isreply"] = param["isreply"]
	comment["reply_uid"] = param["reply_uid"] or -1 
	comment["reply_cid"] = param["reply_cid"] or -1 
	comment["score"] = param["score"] or ""
	comment["words"] = param["words"] or ""
	comment["status"] = 1
	local now = ngx.time()
	comment["created_time"]=now
	comment["updated_time"]=now
	local ok,err = redis:hmset("feed:comment:info:"..cmid,comment)
	return cmid,comment
end

function get_param(request)
	local param = {}
	param["utoken"] =request:get_arg("utoken") or ""
	param["uid"] =request:get_arg("uid") or ""
	param["fid"] = request:get_arg("fid") or ""
	param["ctype"] = request:get_arg("ctype") or ""
	param["isreply"] = request:get_arg("isreply") or ""
	param["reply_uid"] = request:get_arg("reply_uid")
	param["reply_cid"] = request:get_arg("reply_cid")
	param["score"] = request:get_arg("score")
	param["words"] = request:get_arg("words") or ""
	logger:info(cjson.encode(param))
	return param
end