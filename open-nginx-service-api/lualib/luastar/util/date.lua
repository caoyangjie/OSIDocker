#! /usr/bin/env lua
--[[
	
]]

module(..., package.seeall)
local str_util = require("luastar.util.str")
function get_timestamp()
    return os.date('%Y%m%d%H%M%S', ngx.time())
end

function get_ngxtime()
    return ngx.time()
end

function get_ngxtoday()
    return ngx.today()
end

function GetTimeByDate(r)
    local a = str_util.split(r, " ")
    local b = str_util.split(a[1], "-")
    local c = str_util.split(a[2], ":")
    local t = os.time({year=b[1],month=b[2],day=b[3], hour=c[1], min=c[2], sec=c[3]})
    return t
end
