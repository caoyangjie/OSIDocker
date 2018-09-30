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

function pub(request, response)
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
	logger:info(" pub feed  param is ok  "..cjson.encode(_.pick(param,"utoken","uid","is_forward")))
	is_ok = _.any(_.pick(param,"utoken","uid","is_forward","feed_type"),function(v) if v ==nil or ""==v then return  true end   end)
	logger:info(" pub feed  param is ok2 "..tostring(is_ok))
	if is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	is_ok = check:sign(param)
	logger:info(" pub feed  sign "..tostring(is_ok))
	if not is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	local biz_req_param_is_ok,nmsg = check_biz_req_param(request,param)
	if not biz_req_param_is_ok then
		response:writeln(json_util.illegal_argument(nmsg))
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
	--[[
	local utoken_hash,token_err = table_util.array_to_hash(redis:hgetall("user:token:"..uid))
	logger:info(" utoken_ok is  "..cjson.encode(utoken_hash))
	if utoken ~= utoken_hash.token or os.time() > tonumber(utoken_hash.timeout) then
		response:writeln(json_util.illegal_token())
		return
	end
	]]--
	
	
	local feed = get_param_to_feed(request)
	if _.size(feed) ==0 then
		redis_util:close(redis)
		response:writeln(json_util.not_found("被转发feed已删除"))
		return
	end
	
	
	
	local mid = request:get_arg("mid","")
	if nil ~= mid and ""~=mid and tonumber(mid)>0 then
		local musicinfo = table_util.array_to_hash(redis:hgetall("md:audio:"..mid))
		if nil == musicinfo or  type(musicinfo) == "userdata" or  _.size(musicinfo) == 0 then 
			redis_util:close(redis)
			response:writeln(json_util.not_found("音乐已被删除"))
			return
		end
	end
	
	
	local uids = request:get_arg("uids","")
	
	-- deal pic
	local is_deal_pic_ok = deal_pic(request,feed,mid,tonumber(param["feed_type"]),redis)
	if not is_deal_pic_ok then
		redis_util:close(redis)
		response:writeln(json_util.fail("upload pic fail"))
		return
	end
	local now = ngx.time()
    local feedid = save_feed(uid,feed,redis,now)
	
    -- insert user.outbox and is send to inbox and send inbox  
	send_to_fans_inbox(uid,feedid,redis,now)
	
	-- send to uuids
	send_msg_friend(uid,feedid,uids,redis)
	
	-- todo send push msg
	redis_util:close(redis)
	local res = {fid= feedid}
	response:writeln(json_util.success(res)) 	
	
end


function check_biz_req_param(request,param)
	local feed_type = tonumber(param["feed_type"])
	if feed_type == 1 and tonumber(param["is_forward"]) ~= 1 then
		if nil == param["mid"] or ""==param["mid"] then
			return false,"音乐动态请添加音乐"
		end
	elseif feed_type == 2 then
		local param_pic1 = request:get_arg("pic1")
		local param_pic2 = request:get_arg("pic2")
		local param_pic3 = request:get_arg("pic3")	
		local picArry = {param_pic1,param_pic2,param_pic3}
		local is_hash_file = _.any(picArry,function(v) if nil ~= v then return true  end end)
		if not is_hash_file then
			return false,"图片动态请添加图片"
		end
	elseif 	feed_type == 3 then
		if nil == param["mood"] or ""==param["mood"] then
			return false,"文字动态请输入文字"
		end	
	end
	
	if tonumber(param["is_forward"]) == 1 then
		if nil == param["sourcesid"] or "" == param["sourcesid"] then
			return false,"转发动态请添加转发源动态ID"
		end
	end
	
	return true
end

function deal_pic(request,feed,mid,feed_type,redis)
	local param_pic1 = request:get_arg("pic1")
	local param_pic2 = request:get_arg("pic2")
	local param_pic3 = request:get_arg("pic3")	
	local is_deal_pic_ok = true
	
	logger:info("type param_pic1 is "..type(param_pic1))
	local picArry = {param_pic1,param_pic2,param_pic3}
	local is_hash_file = _.any(picArry,function(v) if nil ~= v then return true  end end)
	logger:info("type is_hash_file is "..tostring(is_hash_file))
	if  is_hash_file then
		--todo file uuids
		local resty_uuid = require "resty.uuid"
		local month = os.date('%Y%m', ngx.time())
		local filedata ={}
		_.each(picArry,
			function(k,v)
				if nil ~= v then
					local picuuid = month..resty_uuid:generate()
					feed["pid"..k] = picuuid
					filedata["pic"..k] = {name=v.filename,data=v.value}
					filedata["uuid"..k] = picuuid
				end
			end)
		
		if _.size(filedata) >0 then
			filedata["uid"] = request:get_arg("uid")
			logger:info("--------begin-----")
			local httpclient = require("luastar.util.httpclient")
			ok, code, headers, status, body = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/app/image/upload","POST",filedata,1500000,{})
			logger:info("--------end-----")
			-- local url = "http://219.234.131.42:8004/inner/app/image/upload"
			-- local lua_http_post = (require "multipart-post").lua_http_post
			-- local resUpload = lua_http_post(url,filedata)
			logger:info(body)
			local resUpload_table = cjson.decode(body)
			local resdata = resUpload_table["data"]
			logger:info("-------code-----"..resUpload_table["code"])
			if "A000000" ~= resUpload_table["code"] then
				is_deal_pic_ok = false
			end
			
			-- pic1url=resdata[picuuid1]  -- todo
		    -- pic2url=resdata[picuuid2]
		    -- pic3url=resdata[picuuid3]
		end
	else
		if nil ~= mid and ""~=mid then
			if 1==feed_type then
				local musicinfo = table_util.array_to_hash(redis:hgetall("md:audio:"..mid))
				feed["pid1"] = musicinfo.poster_h_uuid
			elseif 4 ==feed_type then
				local activityinfo = table_util.array_to_hash(redis:hgetall("activity:info:"..mid))
				feed["pid1"] = activityinfo.pid
			end
		end
	end
	
	logger:info("type big file ------ is "..cjson.encode(feed))
	return is_deal_pic_ok
end

function get_param_to_feed(request)
	local feed = {}
	feed["uid"] =tonumber(request:get_arg("uid"))
	feed["words"] = request:get_arg("mood","")
	feed["cid"] =  request:get_arg("mid","")
	feed["src_fid"] = request:get_arg("sourcesid",-1)
	feed["is_forward"] = request:get_arg("is_forward")
	feed["pid1"] = ""
	feed["pid2"] = ""
	feed["pid3"] = ""
	if 0~=tonumber(feed["is_forward"]) and tonumber(feed["src_fid"]) >0   then
		local srcfeedinfo = get_feed_hash(feed["src_fid"])
		if nil == srcfeedinfo or  type(srcfeedinfo) == "userdata" or  _.size(srcfeedinfo) == 0 then 
			-- src fid is not exist
			return {}
		end
		feed["cid"] = srcfeedinfo.cid
		feed["pid1"] = srcfeedinfo.pid1 -- if forward get src's pid1 to my pid1
		feed["src_fuid"] = srcfeedinfo.uid
	end
	feed["feed_type"] = request:get_arg("feed_type")
	if nil ~= feed["cid"] and ""~= feed["cid"] and tonumber(feed["cid"] )>0  then
		local feed_type = tonumber(feed["feed_type"])
		local beanFactory = luastar_context.getBeanFactory()
		local redis_util = beanFactory:getBean("redis")
		local redis = redis_util:getConnect()	
		if 1==feed_type then -- music
			local musicinfo = table_util.array_to_hash(redis:hgetall("md:audio:"..feed["cid"]))
			if nil ~= musicinfo and type(musicinfo) ~= "userdata" and _.size(musicinfo) >0 then
				feed["src_muid"] = musicinfo.uid
			else
				feed["src_muid"] = ""
			end
		elseif 4==feed_type then -- activity
			local activityinfo = table_util.array_to_hash(redis:hgetall("activity:info:"..feedInfo.cid))	
			if nil ~= activityinfo and type(activityinfo) ~= "userdata" and _.size(activityinfo) >0 then
				feed["src_muid"] = activityinfo.uid
			else
				feed["src_muid"] = ""
			end
		end
		redis_util:close(redis)
	end
	-- query cid by uid
	feed["is_original"] =is_original(cid,uid) -- todo
	-- local time =  os.date('%Y%m%d%H%M%S', os.time())
	local time =  ngx.time()
	feed["created_time"] = time
	feed["updated_time"] = time
	feed["status"] = 1
	return feed
end

function get_param(request)
	local param = {}
	param["utoken"] =request:get_arg("utoken") or ""
	param["uid"] =request:get_arg("uid") or ""
	param["mood"] = request:get_arg("mood")
	param["mid"] = request:get_arg("mid")
	param["uids"] = request:get_arg("uids")
	param["sourcesid"] = request:get_arg("sourcesid")
	param["is_forward"] = request:get_arg("is_forward") or ""
	param["feed_type"] = request:get_arg("feed_type") or ""
	logger:info(cjson.encode(param))
	return param
end

function send_to_fans_inbox(uid,feedid,redis,now)
	local fans,err1 = redis:zrange("user:followers:"..uid,0,-1) -- SMEMBERS
	 -- >7*24*3600s
	logger:info("set inbox ".. cjson.encode(fans))
	if nil ~= fans and type(fans) ~= "userdata" then
	_.each(fans,function(k,v) 
					local last_login_time = redis:get("feed:time:"..v)
					if nil ~=last_login_time  and type(last_login_time) ~="userdata" and now - tonumber(last_login_time) <= 604800   then
						redis:zadd("feed:inbox:"..v,feedid,feedid) 
					end
			end
	)
	end
end

function save_feed(uid,feed,redis,now)
	local feedid = redis:incr("feed:ids")
	logger:info(feedid)
	is_ok,err = redis:zadd("feed:outbox:"..uid,now,feedid)
	is_ok,err = redis:zadd("feed:inbox:"..uid,feedid,feedid)
	-- uuid rule
	logger:info("----------start param:"..cjson.encode(feed))
	local ok,err = redis:hmset("feed:info:"..feedid,feed)
	return feedid
end

function send_msg_friend(uid,feedid,uids,redis)
	if nil ~= uids and ""~=uids then
		local sendmsg = function(k,receiver_uid)
			    
			    local msgInfo = {}
				local msgid,msgid_err = redis:incr("user:msg:ids")
				msgInfo["sender_uid"] = uid
				msgInfo["receiver_uid"] = receiver_uid
				msgInfo["type"] = 3
				msgInfo["opt_type"] = 9
				msgInfo["fid"] = feedid
				msgInfo["text"] = "推荐了一首歌曲"
				msgInfo["created_time"] = ngx.time()
				
				local msg_ok,msg_err = redis:hmset("user:msg:info:"..msgid,msgInfo)
				redis:zadd("user:msg:inbox:rec:"..receiver_uid,msgid,msgid)
			    
				return true
			end
		-- user:msg:info:${msgid}
		_.each(str_util.split(uids,","),sendmsg)
	end
end

function is_original(cid,uid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local is_original = 1
	if nil ~= cid and ""~=cid then 
		is_original = 0
		local q_song = redis:lrange("user:songs:"..uid,0,-1) 
		if 0 ~= table.getn(q_song)  and _.contains(q_song,cid) then
			is_original = 1
		else 
			local q_activity = redis:lrange("activity:pub:"..uid,0,-1) 
			if 0 ~= table.getn(q_activity)  and _.contains(q_activity,cid) then
				is_original = 1
			end
		end
	end
	redis_util:close(redis)
	return is_original
end

-- get feeds 
function feeds(request, response)
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
	local param = {}
	param["utoken"] =utoken
	param["uid"] =uid
	param["ouid"] = tonumber(request:get_arg("ouid"))
	param["goodcount"] = tonumber(request:get_arg("goodcount"))
	param["commcount"] = tonumber(request:get_arg("commcount"))
	param["count"] = tonumber(request:get_arg("count"))
	param["score"] = tonumber(request:get_arg("score"))
	
	-- check param is nil
	is_ok = _.any(_.pick(param,"utoken","uid"),function(v) if v ==nil or ""==v then return  true end   end)
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
	--[[
	local utoken_hash,token_err = table_util.array_to_hash(redis:hgetall("user:token:"..uid))
	logger:info(" utoken_ok is  "..cjson.encode(utoken_hash))
	if utoken ~= utoken_hash.token or os.time() > tonumber(utoken_hash.timeout) then
		response:writeln(json_util.illegal_argument())
		return
	end
	]]--
    
	-- if ouid is not nil ,then get other user's feedslist
	local feedsList = {}
	local score = nil
	local leftdatanum = nil
	local now = ngx.time()
	if nil ~=param["ouid"] and uid ~= param["ouid"] then
		feedsList,score,leftdatanum = getOtherFeedIds(param["ouid"],param["score"],param["count"],redis)
	else
		feedsList,score,leftdatanum = getMyFeedIds(uid,now,param["score"],param["count"],redis)
	end
	
	
	local res ={}
	local feedinforlist = {}
	local count = tonumber(param["count"]) or 20 
	while true do 
		if _.size(feedsList)  == 0 then
			break
		end
		
		for k,v in ipairs(feedsList) do
			feedinforlist[#feedinforlist+1] = get_feed_info(v,uid,param["goodcount"],param["commcount"])  -- todo
		end
		
		if _.size(feedinforlist) > count then
			feedinforlist = _.sub(feedinforlist,1,count)
			local redis_key = ""
			if nil ~=param["ouid"] and uid ~= param["ouid"] then
				redis_key = "feed:outbox:"..param["ouid"] 
			else
				redis_key = "feed:inbox:"..uid
			end
			
			local last_fid = feedinforlist[#feedinforlist]
			last_fid = last_fid["fid"]
			score,leftdatanum = get_score_and_leftnum(redis_key,last_fid,redis)
			break
		end
		
		if nil ~=param["ouid"] and uid ~= param["ouid"] then
			feedsList,score,leftdatanum = getOtherFeedIds(param["ouid"],score,param["count"],redis)
		else
			feedsList,score,leftdatanum = getMyFeedIds(uid,now,score,param["count"],redis)
		end
	end 
	
	res.feedlist =feedinforlist
	res.score = score
	logger:info(leftdatanum)
	res.leftdatanum = leftdatanum
	logger:info(res.leftdatanum)
	-- update feed:time 
	local is_set_feed_time_ok = redis:set("feed:time:"..uid,now) -- 
	redis_util:close(redis)
	res = json_util.success(res)
	logger:info(res)
	res = res:gsub("{}", "[]")
	logger:info(res)
	response:writeln(res) 
end



function get_score_and_leftnum(redis_key,last_fid,redis)
	local left_score =  redis:zscore(redis_key,last_fid)
	if  type(left_score) ==nil or type(left_score) == "boolean" then
		left_score = 0
	end
	
	local leftdatanum,err2 = redis:zcount(redis_key,"-inf",left_score) 
	logger:info(leftdatanum)
	if  type(leftdatanum) ==nil or type(leftdatanum) == "boolean" or tonumber(leftdatanum)==0 then
		leftdatanum = 0
	else
		leftdatanum = leftdatanum -1
	end
	logger:info(leftdatanum)
	return left_score,leftdatanum
end

function get_feed_hash(fid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local feedInfo,err = table_util.array_to_hash(redis:hgetall("feed:info:"..fid))
	redis_util:close(redis)
	return feedInfo
end

function get_feed_info(fid,uid,goodcount,commcount)
	logger:info(cjson.encode(fid))	
	
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	
	local feed = {}
	feed.fid = fid
	local feedInfo = get_feed_hash(fid)
	
	if nil == feedInfo or type(feedInfo) == "userdata" or _.size(feedInfo) ==0 or tonumber(feedInfo.status) == 2 then
		redis_util:close(redis)
		return nil
	end
	
	local is_followed = redis:zrank("user:following:"..uid,feedInfo.uid)
	if not is_followed then  -- 如果未关注则不返回对应的feed信息
		redis_util:close(redis)
		return nil
	end
	
	logger:info(cjson.encode(feedInfo))
	local userInfo = get_user_info(feedInfo.uid)
	logger:info("pub user info " .. cjson.encode(userInfo))
	feed.pubuserinfo = {uid=feedInfo.uid,username=userInfo.username,picurl=userInfo.picurl}
	feed.is_forward= feedInfo.is_forward
	feed.is_original= feedInfo.is_original
	feed.feed_type= feedInfo.feed_type
	local feed_share_url,err = redis:get("feed:share:url")
	feed.share_url= feed_share_url
	if tonumber(feedInfo.is_forward) ~=0 and tonumber(feedInfo.src_fid) > 0 then
		feed.src_fid= feedInfo.src_fid
		if nil ~= feedInfo.src_muid and ""~=feedInfo.src_muid then
			local srcuserinfo = get_user_info(feedInfo.src_muid)
			feed.srcuserinfo={uid = feedInfo.src_muid,username = srcuserinfo.username}
		else 
			local srcfeedInfo = get_feed_hash(feedInfo.src_fid)
			if nil ~= srcfeedInfo and type(srcfeedInfo) ~= "userdata" and _.size(srcfeedInfo) >0 then
				local srcuserinfo = get_user_info(srcfeedInfo.uid)
				feed.srcuserinfo={uid = srcfeedInfo.uid,username = srcuserinfo.username}
			else
				feed.srcuserinfo={uid = "",username = ""}
			end
		end
	end
	
	local contentinfo = {}
	local feed_type = tonumber(feedInfo.feed_type) 
	if 1==feed_type then -- music
		local musicinfo = table_util.array_to_hash(redis:hgetall("md:audio:"..feedInfo.cid))
		if nil ~= musicinfo and type(musicinfo) ~= "userdata" and _.size(musicinfo) >0 then
			contentinfo.cid = feedInfo.cid
			contentinfo.contentname=musicinfo.audio_name
			contentinfo.contentdesc=musicinfo.introduce
			contentinfo.contenturl=musicinfo.play_url
		else
			contentinfo =  {cid = "",contentname="",contentdesc="",contenturl=""}
		end
		
	elseif 4==feed_type then -- activity
		local activityinfo = table_util.array_to_hash(redis:hgetall("activity:info:"..feedInfo.cid))	
		if nil ~= activityinfo and type(activityinfo) ~= "userdata" and _.size(activityinfo) >0 then
			contentinfo.cid = feedInfo.cid
			contentinfo.contentname=activityinfo.actname
			contentinfo.contentdesc=activityinfo.intro
			local activity_share_url,err = redis:get("activity:share:url")
			contentinfo.contenturl=activity_share_url
		else
			contentinfo =  {cid = "",contentname="",contentdesc="",contenturl=""}
		end
	end
	if _.size(contentinfo) >0 then 
		feed.contentinfo = contentinfo
	end	
	
	feed.pic1= get_picurl_by_id(feedInfo.pid1)  --get pichash by id
	feed.pic2= get_picurl_by_id(feedInfo.pid2)
	feed.pic3= get_picurl_by_id(feedInfo.pid3)
	feed.created_time= feedInfo.created_time
	feed.words= feedInfo.words
	feed.goodnum= get_goodnum(fid)
	feed.ugoodnum= get_goodupsnum(fid)
	feed.commentnum= get_commentnum(fid)
	feed.ispraised= ispraised(fid,uid)
	
	feed.goodlist = get_good_list(fid,goodcount,redis)
	feed.commentlist =  get_commentlist_list(fid,commcount,redis)
	logger:info(cjson.encode(feed))
	redis_util:close(redis)
	return feed
end

function get_good_list(fid,goodcount,redis)
	local goodList = {}
	goodcount = goodcount or 5
	local goods ,err = redis:zrevrangebyscore("feed:good:"..fid,"+inf","-inf","LIMIT",0,goodcount)
	if nil == goods or type(goods) == "userdata" or _.size(goods)==0 then
			return goodList
	end
	logger:info("good list ------"..fid)
	logger:info("good" .. cjson.encode(goods))
		-- break
	goodList = underscore.map(goods,function(v)
			local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..v))	
			return {uid = v,username=userinfo.username}
		end
	)
	logger:info(cjson.encode(goodList))
	return goodList
end

function get_commentlist_list(fid,commcount,redis)
	commcount = commcount or 1
	local commentlist = get_comments(fid,commcount,redis,"feed:comment:music:"..fid) -- music comment 
	
	logger:info(cjson.encode(commentlist))
	if _.size(commentlist) == 0 then
		commentlist = get_comments(fid,commcount,redis,"feed:comment:pub:"..fid)	-- pub comment
	end
	return commentlist
end




function get_comments(fid,commcount,redis,redis_key)
	local commentlist = {}
	local comments ,err = redis:zrevrangebyscore(redis_key,"+inf","-inf","LIMIT",0,commcount)
	logger:info("music ----"..cjson.encode(comments))
	if nil == comments or type(comments) =="userdata"   or _.size(comments)==0 then
		return commentlist
	end
	logger:info("pub ----"..cjson.encode(comments))
	commentlist= _.map(comments,function(k,v)
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
	return commentlist
end

function get_commentnum(fid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local pubcommentnum,err = redis:zcard("feed:comment:pub:"..fid)
	local musiccommentnum,err1 = redis:zcard("feed:comment:music:"..fid)
	redis_util:close(redis)
	return pubcommentnum + musiccommentnum
end



function ispraised(fid,uid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	
	local index,err = redis:zrank("feed:good:"..fid,uid) -- ZRANK
	redis_util:close(redis) 
	local ispraised = 0
	if nil ~=index  and type(index) ~= "userdata" then
		ispraised = 1
	end
	return ispraised
end

function get_goodnum(fid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local goodnum,err = redis:zcard("feed:good:comm:"..fid)
	redis_util:close(redis)
	return goodnum
end

--  大咖点赞 
function get_goodupsnum(fid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local goodupsnum,err = redis:zcard("feed:good:higherups:"..fid)
	
	redis_util:close(redis)
	return goodupsnum
end


function get_picurl_by_id(pid)
	local picurl = ""
	if nil ~= pid and ""~=pid then
		local picinfo =  get_picinfo_by_id(pid)
		if nil ~= picinfo and type(picinfo) ~= "userdata" and _.size(picinfo) >0 and tonumber(picinfo.status) == 1 then
			picurl = picinfo.pic_url
		end
	end
	return picurl
end


function get_picinfo_by_id(pid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local picinfo = table_util.array_to_hash(redis:hgetall("pub:image:"..pid))
	redis_util:close(redis)
	return picinfo
end

function get_user_info(uid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))	
	redis_util:close(redis)
	return userinfo
end


function getOutboxScore(uid,now,redis)
	local outbox_score = nil
	local outbox_score_old = nil
	outbox_score_old,err1 = redis:get("feed:time:outbox:"..uid)
    logger:info(outbox_score_old)
	if nil ~= outbox_score_old and type(outbox_score_old) ~= "userdata" then 
			outbox_score_old = tonumber(outbox_score_old) -1
			outbox_score = tonumber(outbox_score_old) - 604800
	else
			outbox_score = now - 604800
			outbox_score_old = "+inf"
	end	
	local is_ok_outbox_score ,err2 = redis:set("feed:time:outbox:"..uid,outbox_score)
	return outbox_score_old,outbox_score
end

function getOutboxFeedList(uid,now,redis)
	local followings = redis:zrange("user:following:"..uid,0,-1,'WITHSCORES')
	if nil ~= followings and  type(followings) ~= "userdata" and type(followings) ~= "boolean" then
		followings = table_util.array_to_hash(followings)
	end
	followings[uid] = "0"
	outbox_score_old,outbox_score = getOutboxScore(uid,now,redis)
	
	_.each(followings,function(k,v) 
		if outbox_score_old > tonumber(v) then -- TODO
			if tonumber(v) > outbox_score then
				outbox_score = v 
			end
			uoutboxfeeds = 	redis:zrevrangebyscore("feed:outbox:"..k,outbox_score_old,outbox_score or "-inf")  
			_.each(uoutboxfeeds ,function(k1,v1) redis:zadd("feed:inbox:"..uid,v1,v1) end)
		end			
	end
	)
		
end

function is_get_all(uid,redis)
	local get_outbox_time,err1 = redis:get("feed:time:outbox:"..uid)
	local followings,err = redis:zrange("user:following:"..uid,0,-1)
	if nil == followings or  type(followings) == "userdata" or type(followings) == "boolean" then
		followings = {}
	end
	followings[#followings + 1] = uid
	local is_get_over = underscore.any(followings,function(v)
			local is_has ,err2 =  redis:zrevrangebyscore("feed:outbox:"..v,tonumber(get_outbox_time) -1,"-inf")
			return nil ~= is_has and type(is_has)~="userdata" and _.size(is_has) >0
		end
	)
	return not is_get_over
end

function getMyFeedIds(uid,now,score,count,redis)
	count = count or 20
	-- 若为非活跃用户则取outbox
	local last_login_time = redis:get("feed:time:"..uid)
	if nil ==last_login_time  or type(last_login_time) =="userdata" or now - last_login_time > 604800   then -- >7*24*3600s
		-- 清空inbox
		local is_del_inbox_ok ,err = redis:del("feed:inbox:"..uid) -- 清除inbox,不能清除自己发的.
		-- 把自己outbox的数据放到inbox中
		getOutboxFeedList(uid,now,redis)
		-- zrevrangebyscore feed:outbox:715 +inf 10 WITHSCORES
	end
	
	local feedsList = {}
	if nil ~= score and tonumber(score)>0 then
		feedsList = redis:zrevrangebyscore("feed:inbox:"..uid,score-1,"-inf","LIMIT",0,count)  --  ZREVRANGE salary 0 -1 WITHSCORES 
	else
		feedsList = redis:zrevrangebyscore("feed:inbox:"..uid,"+inf","-inf","LIMIT",0,count)  --  ZREVRANGE salary 0 -1 WITHSCORES 
	end	
	
	while true do 
		if _.size(feedsList) < count then
			local is_get_all = is_get_all(uid,redis)
			if is_get_all  then -- 如果所有关注者的outbox取完了，则不再取。结束循环
			   break
			end 
			getOutboxFeedList(uid,now,redis)
			local feedsList_while  = {}
			if _.size(feedsList) == 0 then 
				feedsList_while = redis:zrevrangebyscore("feed:inbox:"..uid,"+inf","-inf","LIMIT",0,count) 
			else
				local last_get_score = redis:zscore("feed:inbox:"..uid,feedsList[#feedsList])
				feedsList_while = redis:zrevrangebyscore("feed:inbox:"..uid,last_get_score-1,"-inf","LIMIT",0,count) 
			end 
			feedsList = _.append(feedsList,feedsList_while)
		else 
			-- 截取
			feedsList = _.sub(feedsList,1,count)
			break
		end
	end
	
	
	logger:info(cjson.encode(feedsList))	
	local left_score =  redis:zscore("feed:inbox:"..uid,feedsList[#feedsList])
	if  type(left_score) ==nil or type(left_score) == "boolean" then
		left_score = 0
	end
	
	local leftdatanum,err2 = redis:zcount("feed:inbox:"..uid,"-inf",left_score) 
	logger:info(leftdatanum)
	if  type(leftdatanum) ==nil or type(leftdatanum) == "boolean" or tonumber(leftdatanum)==0 then
		leftdatanum = 0
	else
		leftdatanum = leftdatanum -1
	end
	logger:info(leftdatanum)
	return feedsList,left_score,leftdatanum
end

function getOtherFeedIds(ouid,score,count,redis)
	local outboxList = {}
	
	if nil ~= score and tonumber(score)>0 then
	    outboxList =  redis:zrevrangebyscore("feed:outbox:"..ouid,score-1,"-inf","LIMIT",0,count or 20)
	else  
		outboxList =  redis:zrevrangebyscore("feed:outbox:"..ouid,"+inf","-inf","LIMIT",0,count or 20)
		
	end
	local left_score,err1 =  redis:zscore("feed:outbox:"..ouid,outboxList[#outboxList])
	if  type(left_score) ==nil or type(left_score) == "boolean" then
		left_score = 0
	end
	local leftdatanum,err2 = redis:zcount("feed:outbox:"..ouid,"-inf",left_score) 
	if  type(leftdatanum) ==nil or type(leftdatanum) == "boolean" or tonumber(leftdatanum)==0 then
		leftdatanum = 0
	else
		leftdatanum = leftdatanum -1
	end
	logger:info(leftdatanum)
	return outboxList,left_score,leftdatanum
end