#!/usr/bin/env lua
--[[
	
]]
module(..., package.seeall)
local date_util = require("luastar.util.date")

function illegal_argument(msg)
	local rs = {
		head = {
			status = 5,
			msg = msg or "illegal argument.",
			datakey = date_util.get_timestamp(),
			timestamp= ngx.time()
		}
	}
	return cjson.encode(rs)
end

function illegal_token(msg)
	local rs = {
		head = {
			status = 7,
			msg = msg or "token illegal.",
			datakey = date_util.get_timestamp(),
			timestamp= ngx.time()
		}
	}
	return cjson.encode(rs)
end

function not_found(msg)
	local rs = {
		head = {
			status = 3,
			msg = msg or "data not found.",
			datakey = date_util.get_timestamp(),
			timestamp=  ngx.time()
		}
	}
	return cjson.encode(rs)
end

function success(data)
	local body = data or {}
	local rs = {
		head = {
			status = 1,
			msg = "ok.",
			datakey = date_util.get_timestamp(),
			timestamp=  ngx.time()
		},
		body = body
	}
	return cjson.encode(rs)
end

function formatesuccess(data)
	local body = data or {}
	local rs = {
		head = {
			status = 1,
			msg = "ok.",
			datakey = date_util.get_timestamp(),
			timestamp=  ngx.time()
		},
		body = body
	}
	local temp  = cjson.encode(rs)
	local str = string.gsub(temp,"{}","[]")
	return str
end


function fail(msg)
	local rs = {
		head = {
			status = 6,
			msg = msg or "system error.",
			datakey = date_util.get_timestamp(),
			timestamp= ngx.time()
		}
	}
	return cjson.encode(rs)
end

function dataexp(msg)
	local rs = {
		head = {
			status = 4,
			msg = msg or "Data exception.",
			datakey = date_util.get_timestamp(),
			timestamp=  ngx.time()
		}
	}
	return cjson.encode(rs)
end