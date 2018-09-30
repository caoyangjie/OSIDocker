#!/usr/bin/env lua
--[[
	消息列表
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")

function import(request, response)
	getList(request, response,1)
end
function ordinary(request, response)
	getList(request, response,2)
end
function recommend(request, response)
	getList(request, response,3)
end
function auth(request, response)
	getList(request, response,4)
end

--主程序入口
function getList(request, response,msgtype)
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
	param["utoken"] = request:get_arg("utoken") or '';
	param["uid"] = request:get_arg("uid") or '';
	param["count"]= request:get_arg("count");
	param["score"]= request:get_arg("score");

	--[[ 校验必填参数 --]]
	local  is_ok = _.any(_.pick(param,"utoken","uid"),function(v) if v ==nil or ""==v then return  true end   end)
	if is_ok then
		logger:info("utoken or uid is nil")
		response:writeln(json_util.illegal_argument())
		return
	end
	
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	--[[ 验证sign --]]
	local isSign = check:sign(param)
	if not isSign then
		logger:info(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end

	--[[ 校验token--]]
	local loginProcess = beanFactory:getBean("loginProcess")
	local ok ,emsg = loginProcess:checktoken(param,redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(emsg))
		return
	end
	
	--[[ 业务逻辑 --]]
	local msgIdList,left_score,leftdatanum = getIdList(param["uid"],msgtype,param["score"],param["count"],redis)
	logger:info("msgIdList size="..tostring(#msgIdList))

	local msgList = {}
	if msgtype == 1 or msgtype == 2 then
		msgList = getMsgList(msgIdList,redis)
	elseif msgtype == 3 then
		msgList = getRecMsgList(msgIdList,redis)
	elseif msgtype == 4 then
		msgList = getAuthMsgList(msgIdList,param["uid"],redis)
	end
	
	local resultObject = {}
	resultObject['score'] = left_score or 0
	resultObject['leftdatanum'] = leftdatanum or 0
	resultObject['msglist'] = msgList
	
	local result_json = json_util.formatesuccess(resultObject)
	result_json = string.gsub(result_json,"{}","[]")
	response:writeln(result_json)
	redis_util:close(redis)
	return 
end



--获取分页消息list
function getIdList(uid,msgtype,score,count,redis)
	local msgkey = getmsgkey(msgtype,uid)
	logger:info("msgkey="..msgkey)
	
	local msgIdList = {}
	if nil ~= score and tonumber(score)>0 then
		msgIdList = redis:zrevrangebyscore(msgkey,score-1,"-inf","LIMIT",0,count or 20)  --  ZREVRANGE salary 0 -1 WITHSCORES 
	else
		msgIdList = redis:zrevrangebyscore(msgkey,"+inf","-inf","LIMIT",0,count or 20)  --  ZREVRANGE salary 0 -1 WITHSCORES 
		setReadScore(uid,msgIdList[1],msgtype,redis)
	end	
	logger:info(cjson.encode(msgIdList))	
	
	local left_score =  redis:zscore(msgkey,msgIdList[#msgIdList])
	logger:info("left_score="..tostring(left_score))
	if  type(left_score) ==nil or type(left_score) == "boolean" then
		left_score = 0
	end
	
	local leftdatanum,err2 = redis:zcount(msgkey,"-inf",left_score) 
	if  type(leftdatanum) ==nil or type(leftdatanum) == "boolean" or tonumber(leftdatanum)==0 then
		leftdatanum = 0
	else
		leftdatanum = leftdatanum -1
	end
	return msgIdList,left_score,leftdatanum
end

--根据ID数组获取重要消息、普通消息详情
function getMsgList(msgIdList,redis)
	local msglist = {}
	if str_util.isnull(msgIdList) or  #msgIdList <= 0 or msgIdList == false then
		return msglist
	end
	for k,v in pairs(msgIdList) do
		local flag = redis:exists("user:msg:info:"..v)
		if flag == 1 then
			local msg = {}
			msg.msgid = v
			local msghash = table_util.array_to_hash(redis:hgetall("user:msg:info:"..v))
			msg.msgtext = msghash.text
			msg.msg_type = msghash.opt_type
			msg.created_time = msghash.created_time
			msg.senderinfor = getSenderInfo(msghash.sender_uid,redis)
			msg.receiverinfor = getReceiverInfo(msghash.receiver_uid,redis)
			msg.contentinfo = getcontentinfo(msghash.fid,msghash.opt_type,redis)
		
			logger:info("msghash.opt_type"..tostring(msghash.opt_type))
			if msghash.opt_type == "4" then
				--乐评评分
				msg.score = getCommontScore(msghash.cid,redis)
			else
				msg.score = ""
			end
			table.insert(msglist,msg)
		else
			logger:info("msg not exists,msgid="..v)
		end
	end
	return msglist
end

--根据ID数组推荐消息详情列表
function getRecMsgList(msgIdList,redis)
	local msglist = {}
	if str_util.isnull(msgIdList) or  #msgIdList <= 0 or msgIdList == false then
		return msglist
	end
	for k,v in pairs(msgIdList) do
		local flag = redis:exists("user:msg:info:"..v)
		if flag == 1 then
			local msg = {}
			msg.msgid = v
			local msghash = table_util.array_to_hash(redis:hgetall("user:msg:info:"..v))
			msg.msgtext = msghash.text
			msg.created_time = msghash.created_time
			msg.senderinfor = getSenderInfo(msghash.sender_uid,redis)
			msg.contentinfo = getRecContentinfo(msghash.fid,redis)
			table.insert(msglist,msg)
		else
			logger:info("msg not exists,msgid="..v)
		end
	end
	return msglist
end

--根据ID数组认证消息详情列表
function getAuthMsgList(msgIdList,uid,redis)
	local msglist = {}
	if str_util.isnull(msgIdList) or  #msgIdList <= 0 or msgIdList == false then
		return msglist
	end
	for k,v in pairs(msgIdList) do
		local flag = redis:exists("user:msg:info:"..v)
		if flag == 1 then
			local msg = {}
			msg.msgid = v
			local msghash = table_util.array_to_hash(redis:hgetall("user:msg:info:"..v))
			msg.created_time = msghash.created_time
			msg.senderinfor = getSenderInfo(msghash.sender_uid,redis)
			msg.receiverinfor = getReceiverInfo(msghash.receiver_uid,redis)
			msg.userrole = getRoles(msghash.text,redis)
			msg.status = msghash.status
			logger:info(uid == msghash.sender_uid)
			if uid == msghash.sender_uid then
				msg.type = 1
			else
				msg.type = 2
			end
			table.insert(msglist,msg)
		else
			logger:info("msg not exists,msgid="..v)
		end
	end
	return msglist
end

--设置已读取消息最大分值
function setReadScore(uid,msgid,msgtype,redis)
	local msgkey = getmsgkey(msgtype,uid)
	
	local right_score =  redis:zscore(msgkey,msgid)
	logger:info("right_score="..tostring(right_score))
	if  type(right_score) ==nil or type(right_score) == "boolean" then
		right_score = "-inf"
	end
	local scorekey = get_msg_score_key(msgtype,uid)
	logger:info("update msg score,key="..scorekey..",right_score="..tostring(right_score))
	redis:set(scorekey,tostring(right_score))
end

--获取重要消息、普通消息内容
function getcontentinfo(fid,opttype,redis)
	--1关注 2点赞，3评论，4乐评、5回复、6转发单曲，7转发活动
	local contentinfo = contentinfo(fid)
	
	local feedExist = redis:exists("feed:info:"..fid)
	if feedExist == 0 then
		logger:info("feed not exists,fid="..fid)
		return contentinfo
	end
	if opttype == 1 then
		return contentinfo
	end
	local feed = table_util.array_to_hash(redis:hgetall("feed:info:"..fid))
	if feed.status ~= "1" then
		return contentinfo
	end
	
	--feed_type 1-单曲，2-图片，3-文字，4-活动，5-H5
	logger:info("feed.feed_type="..feed.feed_type)
	if feed.feed_type == "1" then
		contentinfo.respath = get_picurl_by_id(feed.pid1,redis)
		contentinfo.text = get_audio_name(feed.cid,redis)
	elseif feed.feed_type == "2" then
		contentinfo.respath = get_picurl_by_id(feed.pid1,redis)
	elseif feed.feed_type == "3" then
		contentinfo.text = feed.words
	elseif feed.feed_type == "4" then
		contentinfo.respath = get_picurl_by_id(feed.pid1,redis)
		contentinfo.text = get_act_name(feed.cid,redis)
	end
	return contentinfo
end

--获取推荐消息内容
function getRecContentinfo(fid,redis)
	local contentinfo = contentinfo(fid)
	local feedExist = redis:exists("feed:info:"..fid)
	if feedExist == 0 then
		logger:info("feed not exists,fid="..fid)
		return contentinfo
	end
	local feed = table_util.array_to_hash(redis:hgetall("feed:info:"..fid))
	if feed.status ~= "1" then
		return contentinfo
	end
	logger:info("feed.pid1="..feed.pid1)
	contentinfo.respath = get_picurl_by_id(feed.pid1,redis)
	contentinfo.text = get_audio_name(feed.cid,redis)
	return contentinfo
end

--获取认证消息内容
function getAuthContentinfo(redis)
	local contentinfo = contentinfo(fid)
	local feedExist = redis:exists("feed:info:"..fid)
	if feedExist == 0 then
		logger:info("feed not exists,fid="..fid)
		return contentinfo
	end
	local feed = table_util.array_to_hash(redis:hgetall("feed:info:"..fid))
	if feed.status ~= "1" then
		return contentinfo
	end
	
	contentinfo.respath = get_picurl_by_id(feed.pid1,redis)
	contentinfo.text = get_audio_name(feed.cid)
	return contentinfo
end

--获取乐评评分
function getCommontScore(commontId,redis)
	local score = ""
	if nil ~= commontId and "" ~= commontId then
		local commontInfo = table_util.array_to_hash(redis:hgetall("feed:comment:info:"..commontId))
		logger:info("commontInfo.ctype="..commontInfo.ctype)
		if "2" == commontInfo.ctype and nil ~= commontInfo.score then
			score = commontInfo.score
		end
	end
	return score
end

--获取音乐名称
function get_audio_name(aid,redis)
	local name = ""
	if nil ~= aid and "" ~= aid then
		local audioInfo = table_util.array_to_hash(redis:hgetall("md:audio:"..aid))
		if nil ~= audioInfo.audio_name then
			name = audioInfo.audio_name
		end
	end
	return name
end

--获取活动名称
function get_act_name(actid,redis)
	local name = ""
	if nil ~= actid and "" ~= actid then
		local actinfo = table_util.array_to_hash(redis:hgetall("activity:info:"..actid))
		if nil ~= actinfo.actname then
			name = actinfo.actname
		end
	end
	logger:info("get_act_name actid="..actid..",name="..name)
	return name
end

--获取图片访问地址
function get_picurl_by_id(uuid,redis)
	local picurl = ""
	if nil ~= uuid and ""~=uuid then
		local picinfo = table_util.array_to_hash(redis:hgetall("pub:image:"..uuid))
		if picinfo.pic_audit == "1" and picinfo.status == "1" then
			picurl = picinfo.pic_url
		end
	end
	return picurl
end

--获取发送者信息
function getSenderInfo(uid,redis)
	local senderinfor = {}
	senderinfor.sender_uid = ""
	senderinfor.sender_name = ""
	senderinfor.sender_picurl = ""
	senderinfor.sender_phone = ""
	local flag = redis:exists("user:info:"..uid)
	if(flag == 1) then
		local user = userinfo_util.get_user_info(uid,redis)
		senderinfor.sender_uid = uid
		senderinfor.sender_name = user.username
		senderinfor.sender_picurl = user.picurl
		senderinfor.sender_phone = user.phone
	else
		logger:info("user not exists,uid="..uid)
	end
	return senderinfor
end

--获取接受者信息
function getReceiverInfo(uid,redis)
	local receiverinfor = {}
	receiverinfor.receiver_uid = ""
	receiverinfor.receiver_name = ""
	local flag = redis:exists("user:info:"..uid)
	if(flag == 1) then
		local user = userinfo_util.get_user_info(uid,redis)
		receiverinfor.receiver_uid = uid
		receiverinfor.receiver_name = user.username
	else
		logger:info("user not exists,uid="..uid)
	end
	return receiverinfor
end

--获取音乐人角色名字
function getRoles(roleids,redis)
	logger:info("auth roleids="..roleids)
	local userrole = ""
	if not str_util.isnull(roleids) then
		local rolehash = table_util.array_to_hash(redis:hgetall("musician:role"))
		userrole = _.map(str_util.split(roleids,","),function(k,v)  return rolehash[v] end)
		userrole = table.concat(userrole,",")
		logger:info("userrole========="..userrole)
	end
	
	return userrole
end

--获取消息详情KEY
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

--获取消息SCORE KEY
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

--消息内容
function contentinfo(fid)
	local contentinfo = {}
	contentinfo.fid = fid
	contentinfo.text = ""
	contentinfo.respath = ""
	return contentinfo
end