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
function edit(request, response)
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
	
	param["utoken"] = request:get_arg("utoken");
	param["uid"] = request:get_arg("uid");
	param["username"]= request:get_arg("username");
	param["namereal"] = request:get_arg("namereal");
	param["sex"] = request:get_arg("sex");
	param["birthday"] = request:get_arg("birthday");
	param["height"] = request:get_arg("height");
	param["bloodtype"] = request:get_arg("bloodtype");
	param["country"]= request:get_arg("country");
	param["province"] = request:get_arg("province");
	param["city"] = request:get_arg("city");
	param["userrole"] = request:get_arg("userrole");
		
	--[[ 验证sign --]]
	local isSign = check:sign(param)
	if not isSign then
		logger:i(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end
	
	--[[ 参数检查--]]
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
		response:writeln(json_util.illegal_argument())
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
	
	--[[ 文件处理--]]
	param["picfile"] = request:get_upload_arg("picfile");
	if not str_util.isnull(param["picfile"])then
		local pic = {name=param["picfile"].filename,data=param["picfile"].value}
		param["picfile"]=pic
	end
	
	param["homepic"] = request:get_upload_arg("homepic");
	if not str_util.isnull(param["homepic"])then
		local pic = {name=param["homepic"].filename,data=param["homepic"].value}
		param["homepic"]=pic
	end
	
	--[[ 查看用户是否存在 --]]
	local uid = redis:hlen("user:info:"..param["uid"])
	if str_util.isnull(uid) or uid<=0 then
		redis_util:close(redis)
		response:writeln(json_util.fail('账号未注册!'))
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
	logger:info(resUpload)
	if not str_util.isnull(resUpload_table)  then
		if resUpload_table["code"] =="A000000" then
			local resdata = resUpload_table["data"]
			local userinfo_util = require("com.lajin.api.biz.userinfo")
			local user = require("com.lajin.model.user")
			local data = user:new()
			local userinfo = userinfo_util.get_user_info(resdata["uid"],redis)
			data=table_util.arryunion(data,userinfo)
			data['passwd']= ''
			data['uid'] = resdata["uid"]
			data['utoken'] = param["utoken"]
			if ok then
				redis_util:close(redis)
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
