#!/usr/bin/env lua
--[[

--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local http = require "resty.http"
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")

function checkcode(request, response)
	local beanFactory = luastar_context.getBeanFactory() 
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	local hc = http:new()
	local sms=tostring(os.time()):reverse():sub(1,6)
	
	--[[ 参数校验--]]
	if not isHead then
		logger:i(" Head  args is null")
		response:writeln(json_util.illegal_argument())
		return
	end
	
	local phone = request:get_arg("phone");
	local istype = request:get_arg("type");
    logger:i(" args phone is "..cjson.encode(phone))
    
	if str_util.isnull(phone) or  str_util.isnull(istype) then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	
	--[[ 
		1 、注册时，如果该用户已注册返回： stauts为4 数据异常  msg：该账号已注册  
		2、忘记密码，如果该用户未注册返回：stauts为4 数据异常 msg：该账号不存在  
	--]]
	local is_exit,status = exituser(phone,redis)
	if tonumber(istype) == 1 and tonumber(is_exit) ==1 then
		if tonumber(status) == 4 then
			redis_util:close(redis)
			response:writeln(json_util.dataexp('该账号已被限制注册!'))
			return
		end
		redis_util:close(redis)
		response:writeln(json_util.dataexp('该账号已注册，请登录!'))
		return
	elseif tonumber(istype) == 2 and tonumber(is_exit) ==0 then
		redis_util:close(redis)
		response:writeln(json_util.dataexp('该账号不存在!'))
		return
	end

	--[[检查验证码时效性--]]
	local timeOut = redis:hget("checkcode:"..phone,'timeout')
	if  not str_util.isnull(timeOut) then
		timeOut = timeOut-os.time()
		if timeOut > 0 then
			local code = getcode(phone,redis)
			--[[ 创建短信--]]
			sms = code['code']
		else
			--[[ 创建短信--]]
			sms = tostring(os.time()):reverse():sub(1,6)
			
		end
	end
	
	--[[ 发送短信 --]]
	local ok = sendsms(phone,sms,hc,response)
	
	if ok == false then
		redis_util:close(redis)
		return
	end

	--[[ 写短信验证码缓存 --]]
	setcode(sms,phone,redis)
	

	--[[ 返回客户端数据 --]]
	responseclient(redis_util,response)

end

--[[ 写短信验证码缓存--]]
function setcode (sms,phone,redis)
	local setdata = {code = sms,timeout=luastar_config.getConfig('checkcode')['timeout']+os.time()}
	redis:hmset("checkcode:"..phone,setdata)
end

--[[ 读取短信验证缓存 --]]
function getcode(phone,redis) 
	local code = redis:hgetall("checkcode:"..phone)
	code = table_util.array_to_hash(code)
	--code = cjson.decode(code)
	return code
end

--[[ 向前段输出数据 --]]
function responseclient (redis_util,response)
	local codedata = {timeout=luastar_config.getConfig('checkcode')['timeout'],retime=luastar_config.getConfig('checkcode')['retime']}
	local codesjon = json_util.success(codedata)
	redis_util:close(redis)
	response:writeln(codesjon)
	return
end

--[[ 发送短信 --]]
function sendsms (phone,sms,hc,response)
	local hcParam ="apikey=a6bb2f5d1bf12896c121f16abeb06174&mobile="..phone.."&text=".."【拉近音乐】您的验证码是:" .. sms
	logger:i(phone.." 短信验证码 :"..cjson.encode(sms))
	local ok, code, headers, status, body  = hc:request {
			url = "http://yunpian.com/v1/sms/send.json",
			method = "POST", -- POST or GET
			-- add post content-type and cookie
			headers = { Cookie = {"ABCDEFG"}, ["Content-Type"] = "application/x-www-form-urlencoded" },
			body = hcParam
		}
		
	if str_util.isnull(body) then
		response:writeln(json_util.fail('http request error: body is null'))
		return false
	end
	
	body = cjson.decode(body)		
	if not ok or tonumber(body["code"]) ~= 0 or not body then
		response:writeln(json_util.fail('http request error'))
		return false
	end
	return true
end

--[[ 判断用户是否存在 --]]
function exituser(phone,redis)
	local  uid = redis:get("user:phone:"..phone)
	local status = 0
	local exit = 0
	if not str_util.isnull(uid) then
		exit = 1
		status = redis:hget("user:info:"..uid,'status')
	else	
		exit = 0
		status = 0
	end
	return exit,status
end