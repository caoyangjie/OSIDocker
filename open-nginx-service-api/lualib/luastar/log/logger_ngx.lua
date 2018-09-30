#! /usr/bin/env lua
--[[
日志配置属性：
log = {
  file_name = "",
  date_pattern = "",
  level = ""
}
--]]

module(..., package.seeall)

local Logger = Class("luastar.log.Logger")

local function log_fmt(message)
    local posinfo = debug.getinfo(3, "Sl") or { short_src = "", currentline = 0 }
    return string.format('-----luastar-----[%s %s] %s\n',
        posinfo.short_src,
        posinfo.currentline,
        message)
end

function Logger.getLog()
    local logger = luastar_cache.get("logger")
    if logger then
        return logger
    end
    logger = Logger:new()
    luastar_cache.set("logger", logger)
    return logger
end

function Logger:debug(message)
    ngx.log(ngx.DEBUG, log_fmt(message))
end

function Logger:info(message)
    ngx.log(ngx.INFO, log_fmt(message))
end

function Logger:notice(message)
    --ngx.log(ngx.NOTICE, logfmt(message))
    print(log_fmt(message))
end

function Logger:warn(message)
    ngx.log(ngx.WARN, log_fmt(message))
end

function Logger:error(message)
    ngx.log(ngx.ERR, log_fmt(message))
end

Logger.d = Logger.debug
Logger.i = Logger.info
Logger.w = Logger.warn
Logger.e = Logger.error

return Logger