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
	param["goodcount"] = request:get_arg("goodcount")
	param["ugoodcount"] = request:get_arg("ugoodcount")
	logger:info(cjson.encode(param))
	return param
end

-- get feeds 
function show(request, response)
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
	is_ok = _.any(_.pick(param,"utoken","uid","fid"),function(v) if v ==nil or ""==v then return  true end   end)
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
	
	local feedInfo = get_feed_info(param["fid"],uid,tonumber(param["goodcount"]),tonumber(param["ugoodcount"]))  -- todo
	if _.size(feedInfo) ==0  then
		redis_util:close(redis)
		response:writeln(json_util.not_found())
		return
	end
	redis_util:close(redis)
	res = json_util.success(feedInfo)
	logger:info(res)
	res = res:gsub("{}", "[]")
	logger:info(res)
	response:writeln(res) 
end


function get_feed_hash(fid)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	local feedInfo,err = table_util.array_to_hash(redis:hgetall("feed:info:"..fid))
	redis_util:close(redis)
	return feedInfo
end

function get_feed_info(fid,uid,goodcount,ugoodcount)
	logger:info(cjson.encode(fid))	
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	
	local feed = {}
	feed.fid = fid
	local feedInfo = get_feed_hash(fid)
	if nil == feedInfo or _.size(feedInfo) ==0  or type(feedInfo) =="userdata" then
		return {}
	end 
	logger:info(cjson.encode(feedInfo))
	local userInfo = get_user_info(feedInfo.uid)
	logger:info("pub user info " .. cjson.encode(userInfo))
	
	
	local is_followed = redis:zrank('user:following:'..uid,feedInfo.uid)
    is_followed = not str_util.isnull(is_followed) and  1 or 0
	feed.pubuserinfo = {uid=feedInfo.uid,username=userInfo.username,picurl=userInfo.picurl,is_follow=is_followed}
	
	
	
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
	
	feed.goodlist = get_good_list(fid,goodcount,redis,"feed:good:comm:"..fid)
	feed.ugoodlist  = get_good_list(fid,ugoodcount,redis,"feed:good:higherups:"..fid)
	-- feed.commentlist =  get_commentlist_list(following,fid,commcount,redis,ismy)
	logger:info(cjson.encode(feed))
	redis_util:close(redis)
	return feed
end

function get_good_list(fid,goodcount,redis,rediskey)
	local goodList = {}
	goodcount = goodcount or 5
	local goods ,err = redis:zrevrangebyscore(rediskey,"+inf","-inf","LIMIT",0,goodcount)
	if nil == goods or type(goods) == "userdata" or _.size(goods)==0 then
			return goodList
	end
	logger:info("good list ------"..fid)
	logger:info("good" .. cjson.encode(goods))
		-- break
	goodList = underscore.map(goods,function(v)
			local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..v))	
			return {uid = v,username=userinfo.username,picurl = userinfo.picurl}
		end
	)
	logger:info(cjson.encode(goodList))
	return goodList
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