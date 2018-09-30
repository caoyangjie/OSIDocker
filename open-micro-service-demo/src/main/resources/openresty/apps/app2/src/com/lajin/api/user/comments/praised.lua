module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local cjson = require("cjson")
local underscore = require("underscore")


function praised(request, response)
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
	is_ok = _.any(_.pick(param,"utoken","uid","fid","type"),function(v) if v ==nil or ""==v then return  true end   end)
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
	
	if tonumber(param["type"]) ==1 then
		local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
		local feedInfo,f_err = table_util.array_to_hash(redis:hgetall("feed:info:"..param["fid"]))
		-- praised
		local goodid = redis:incr("feed:good:ids")
		local is_ok,err = redis:zadd("feed:good:"..param["fid"],goodid,uid)
		if tonumber(userinfo.usertype) == 1 and tonumber(feedInfo.feed_type) == 1 and tonumber(feedInfo.is_original) == 1 and tonumber(feedInfo.is_forward) ==0 then -- common
			-- higherups
			local is_ok_up,err2 = redis:zadd("feed:good:higherups:"..param["fid"],goodid,uid)
	    else
			local is_ok_comm,err1 = redis:zadd("feed:good:comm:"..param["fid"],goodid,uid)
		end
		-- send msg
		send_msg_friend(uid,param["fid"],userinfo,redis)
	else
		-- cancel praised
		local is_del_ok,err = redis:zrem("feed:good:"..param["fid"],uid)
		local is_del_ok_comm,err1 = redis:zrem("feed:good:comm:"..param["fid"],uid)
		local is_del_ok_up,err2 = redis:zrem("feed:good:higherups:"..param["fid"],uid)
	end
	redis_util:close(redis)
	local res = {fid= param["fid"]}
	response:writeln(json_util.success(res)) 
end

function send_msg_friend(uid,feedid,userinfo,redis)
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
	msgInfo["opt_type"] = 2 -- 点赞
	msgInfo["fid"] = feedid
	msgInfo["text"] = ""
	msgInfo["created_time"] = ngx.time()
	
	local msg_ok,msg_err = redis:hmset("user:msg:info:"..msgid,msgInfo)
	if msgInfo["type"] == 1 then
		redis:zadd("user:msg:inbox:import:"..receiver_uid,msgid,msgid)
	else 
		redis:zadd("user:msg:inbox:common:"..receiver_uid,msgid,msgid)
	end
end



function get_param(request)
	local param = {}
	param["utoken"] =request:get_arg("utoken")  or ""
	param["uid"] =request:get_arg("uid")  or ""
	param["fid"] = request:get_arg("fid")  or ""
	param["type"] = request:get_arg("type")  or ""
	logger:info(cjson.encode(param))
	return param
end