#!/usr/bin/env lua
--[[

]]
local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local date_util = require("luastar.util.date")
local loginProcess = Class("com.lajin.api.process.loginProcess")
local outTime = 2592000 --
local seed = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'}
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local userinfo_util = require("com.lajin.api.biz.userinfo")
function loginProcess:init()
	
end

--[[
     创建token
--]]
function loginProcess:createToken(uid)
	if nil == uid or ""==uid then
	    return false,"参数错误"
	end
	local tb = {}
	for i=1,32 do
		table.insert(tb,seed[math.random(1,16)])
	end
	local sid = table.concat(tb)
	sid = string.format('%s%s%s%s%s',
			string.sub(sid,1,8),
			string.sub(sid,9,12),
			string.sub(sid,13,16),
			string.sub(date_util.get_timestamp(),-4),
			string.sub(sid,21,32)
		)
	logger:info('create sid '..sid)
	return sid
end

--[[
     缓存 ：用户 <---> token
--]]
function loginProcess:logToken(uid,token,redis)
	if nil == token or ""==token or not token then
	    return false,"参数错误"
	end
	local tokenHash = {}
	--local head = ngx.req.get_headers()
	tokenHash["token"]   = token 
	tokenHash["timeout"]   = os.time()+outTime	
	local ok,err = redis:hmset("user:token:"..uid,tokenHash)
	logger:info("hmset"..cjson.encode(ok).." err " .. cjson.encode(err))
	return ok,err
end

--[[ 检查token有效性--]] 
function loginProcess:checktoken(param,redis)
	
	--[[ 获得用户token--]]
	local usertoken = redis:hgetall("user:token:"..param["uid"])	
	usertoken = table_util.array_to_hash(usertoken)

	if str_util.isnull(usertoken['token']) then
		return false ,'请重新登录!'
	end
	
	--[[ 检查用户状态 --]]
	local userDate = userinfo_util.get_user_info(param["uid"],redis)--read redis user data
	if not str_util.isnull(userDate) then
		if tonumber(userDate['status']) == 4 then
			return false ,'该账号已过期，请重新登录!'
		end
	end

	--[[ 检查是否失效--]]
	local time = 0
	if not str_util.isnull(usertoken['timeout']) then
		time = usertoken['timeout']
	end
	
	local nowtime = os.time()
	time = nowtime - time
	if time > 0 then
		return false ,'该账号已过期，请重新登录!'
	end
	--[[ 对比token是否一致--]]
	if usertoken['token'] ~= param["utoken"] then --check token
		logger:info("usertoken is "..usertoken['token'])
		return false ,'该账号已在其他设备登录，请重新登录!'
	end
	
	return true
end

--[[ 上次登陆时间--]]
function loginProcess:setlastlogintime (uid,redis)
	local number = redis:hlen('user:info:'..uid)
	if number > 0 then
		redis:hset('user:info:'..uid,'lastlogintime',os.time())
	end
end

--[[ 登陆流程--]]
function loginProcess:loginlog(uid,data,redis)
	--[[ 创建token--]]
	local token = self:createToken(uid)

	--[[ 记录token--]]
	local ok,err=self:logToken(uid,token,redis)

	--[[ 记录最后登陆时间--]]
	self:setlastlogintime(uid,redis)
	if not ok then
		ngx.ctx.response:writeln(json_util.fail('生成token失败'))
		return nil
	end
	data["utoken"] = token
	data["uid"] = uid
	data["passwd"] = ''
	--device
	local logDevInfor = beanFactory:getBean("logDevInfor")
	ok,err = logDevInfor:logDev(uid)
	if not ok then
		ngx.ctx.response:writeln(json_util.fail('生成设备信息'))
		return nil
	end
	return true
end
return loginProcess
