#!/usr/bin/env lua
--[[

]]
local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local cjson = require("cjson")
local check = Class("com.lajin.common.util.check")

function check:init()
end

function check:sign(param)
	local head = ngx.req.get_headers()
	local sign = head["sign"]
	local secretkey="UJMpkYFiq4YDMLkEXgqYUltbfWCb7p67"
	local param_array ={}
	local strUtils = require("luastar.util.str")
	if type(param) == "string" then
		param_array = strUtils.split("&")
	elseif type(param) == "table" then
		local tableUtils,err = require("luastar.util.table")
		param_array = tableUtils.table2arr(param)
	end	
	table.sort(param_array)
	local param_base =  table.concat(param_array,"")
	logger:info("sort is "..param_base)
	param_base = param_base .. secretkey
	logger:info("urlencode is "..strUtils.urlencode(param_base))
	param_base = strUtils.md5(strUtils.urlencode(param_base))
	
	logger:info("md5 is "..param_base)
	logger:info("sign is "..sign)
	local res = false
	if sign == param_base then
		res = true
	end
	logger:info("res is "..tostring(res))
	return res
end

function check:head()
	local head = ngx.req.get_headers()
	local headparam = {}
	headparam["apiversion"] = head["apiversion"] or ""
	headparam["appkey"] = head["appkey"] or ""
	headparam["verstr"] = head["appversion"] or ""
	headparam["datakey"] = head["datakey"] or ""
	-- head["sign"] = request:get_header("sign")
	-- head["ip"] = request.remote_addr
	headparam["devid"] = head["devid"] or ""
	headparam["devmac"] = head["devmac"] or ""
	headparam["devmanufacturer"] = head["devmanufacturer"] or ""
	headparam["devmodel"] = head["devmodel"] or ""
	headparam["platform"] = head["platform"] or ""
	logger:info("headparam is "..cjson.encode(headparam))
	local ok = _.any(_.values(headparam),function(v) if nil == v or ""==v then return true end end)
	logger:info(" check head is ok "..tostring(ok))
	if ok then
	    return false
	end
	return true	
end

return check
