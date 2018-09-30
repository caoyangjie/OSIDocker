#!/usr/bin/env lua
--[[

--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local http = require "resty.http"
local str_util = require("luastar.util.str")
local userinfo_util = require("com.lajin.api.biz.userinfo")

function register(request, response)
	local hc = http:new()
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
	
	param["phone"] = request:get_arg("phone") or '';
	param["passwd"] = request:get_arg("passwd") or '';
	param["checkcode"] = request:get_arg("checkcode") or '' ;
	param["thirduid"] = request:get_arg("thirduid");
	param["thridsrc"] = request:get_arg("thridsrc") or '' ;
	param["username"]= request:get_arg("username");
	param["namereal"] = request:get_arg("namereal");
	param["sex"] = request:get_arg("sex");
	param["birthday"] = request:get_arg("birthday");
	param["country"]= request:get_arg("country");
	param["province"] = request:get_arg("province");
	param["city"] = request:get_arg("city");
	param["userrole"] = request:get_arg("userrole");
	
	local is_ok = _.any(_.pick(param,"phone","passwd","checkcode",'thridsrc'),function(v) if v ==nil or ""==v  then return  true end   end)
	logger:info("  param is ok2 "..tostring(is_ok))
	if is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	--[[ 如果是来源不是拉近用户 则第三方账号不能位空--]]
	if param["thridsrc"] and tonumber(param["thridsrc"])  ~= 1 and param["thridsrc"] ~=''then
		
		if not param["thirduid"] or param["thirduid"]=='userdata' or param["thirduid"] == '' then
			response:writeln(json_util.fail('来源为第三方账户，但第三方账号为空!'))
			return
		end
	
	end

	--[[ 验证sign --]]
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	--[[ 文件处理--]]
	param["picfile"] = request:get_upload_arg("picfile");
	if not str_util.isnull(param["picfile"])then
		local pic = {name=param["picfile"].filename,data=param["picfile"].value}
		param["picfile"]=pic
	end
			
	local  uid = redis:get("user:phone:"..param["phone"])
	
	--[[ 检查用户是否存在--]]
	if not str_util.isnull(uid) then
		local userDate = userinfo_util.get_user_info(uid,redis)--read redis user data
		
		--[[ 检查用户状态 --]]
		if not str_util.isnull(userDate) then
			if tonumber(userDate['status']) ==4 then
				redis_util:close(redis)
				response:writeln(json_util.fail('该账号已被限制注册!'))
				return
			end
		end
		redis_util:close(redis)
		response:writeln(json_util.fail('该账号已注册，请登录!'))
		return
	end
	
	--[[ 检查短信验证码是否过期 --]]
	local sms = redis:hgetall("checkcode:"..param["phone"])
	sms = table_util.array_to_hash(sms)
	if  not str_util.isnull(sms['timeout']) then
		local timeOut = sms['timeout']
		timeOut = timeOut-os.time()
		if timeOut <= 0 then
			redis_util:close(redis)
			response:writeln(json_util.fail('验证码失效!'))
			return
		elseif  not str_util.isnull(param["checkcode"]) and param["checkcode"] ~= sms['code'] then
			redis_util:close(redis)
			response:writeln(json_util.fail('验证码错误!'))
			return
		end
	else
		redis_util:close(redis)
		response:writeln(json_util.fail('验证码输入错误!'))
		return
	end

	--[[ 
	local url = "http://127.0.0.1:8004/inner/app/uc/user/save"
	local lua_http_post = (require "multipart-post").lua_http_post
	local resUpload = lua_http_post(url,param)
	--]]
	
	local httpclient = require("luastar.util.httpclient")
	ok, code, headers, status, resUpload = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/app/uc/user/save","POST",param,1500000,{})

	if tonumber(code) ~= 200 or str_util.isnull(resUpload) then
		redis_util:close(redis)
		response:writeln(json_util.fail('http request error'))
		return
	end
	
	local resUpload_table = cjson.decode(resUpload)
	--logger:info(resUpload)
	if not str_util.isnull(resUpload_table)  then
		if resUpload_table["code"] =="A000000" then
			local resdata = resUpload_table["data"]
			local userinfo_util = require("com.lajin.api.biz.userinfo")
			local user = require("com.lajin.model.user")
			local data = user:new()
			local userinfo = userinfo_util.get_user_info(resdata["uid"],redis)
			data=table_util.arryunion(data,userinfo)
			--[[ 登陆流程--]]
			local loginProcess = beanFactory:getBean("loginProcess")
			local ok = loginProcess:loginlog(resdata["uid"],data,redis)
			redis_util:close(redis)
			if ok then
				table_util.tabletoarr(data,arrkey())
				response:writeln(json_util.formatesuccess(data))
				return
			end
		else
			redis_util:close(redis)
			response:writeln(json_util.fail(resUpload_table['message']))
			return
		end		
	else
		redis_util:close(redis)
		response:writeln(json_util.not_found())
	end
end

--[[ 转数组的key--]]
function arrkey()
	local o = {}
	o['userrole']=true
	return o
end
