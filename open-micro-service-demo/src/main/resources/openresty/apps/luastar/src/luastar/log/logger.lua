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
local str_util = require("luastar.util.str")
local file_util = require("luastar.util.file")
local logging = require("logging")

local log_default_file_name = "/data/logs/luastar/luastar.log"
local log_default_data_pattern = "%Y-%m-%d"
local log_default_level = "DEBUG"

local function open_file(log_config)
    ngx_log_err("-----luastar_log open file")
	local file, err_info = io.open(log_config.file_name, "wa")
    if not file then
		-- 打开文件失败,尝试创建文件目录
        ngx_log_err("-----luastar_log open file fail : "..err_info)
		local dir_index = str_util.lastIndexOf(log_config.file_name, "/")
		local dir = string.sub(log_config.file_name, 1, dir_index)
		ngx_log_err("-----luastar_log try to make dir "..dir)
		local is_ok = file_util.mkdir(dir)
		if not is_ok then
			return nil, string.format("file `%s' could not be opened for writing", log_config.file_name)
		end
		-- 重新打开文件
		file = assert(io.open(log_config.file_name, "wa"))
		if not file then
			return nil, string.format("file `%s' could not be opened for writing", log_config.file_name)
		end
    end
	log_config.file = file
    log_config.file:setvbuf("line")
    return log_config.file
end

local function rollover_file(log_config)
    log_config.file:close()
    log_config.file = nil
    local now_date = os.date(log_config.date_pattern, ngx.time())
    ngx_log_err("-----luastar_log roll over file {file_date=" .. log_config.file_date .. ", now_date=" .. now_date .. "}")
    local rs, msg = os.rename(log_config.file_name, log_config.file_name .. "." .. log_config.file_date)
    if msg then
        ngx_log_err("-----luastar_log rollover file：" .. msg)
        return nil, string.format("error %s on log rollover", msg)
    end
    log_config.file_date = now_date
    return open_file(log_config)
end

local function open_log_file(log_config)
    if not log_config.file then
        return open_file(log_config)
    end
    local now_date = os.date(log_config.date_pattern, ngx.time())
    if now_date == log_config.file_date then
        return log_config.file
    end
    return rollover_file(log_config)
end

function getLog()
    local logger = luastar_cache.get("logger")
    if logger then
        return logger
    end
    -- init logger config
    local log_config = luastar_config.getConfig("log", {})
    log_config = _.defaults(log_config, {
        file_name = log_default_file_name,
        date_pattern = log_default_data_pattern,
        level = log_default_level
    })
    log_config.file_date = os.date(log_config.date_pattern, ngx.time())
    ngx_log_err("-----luastar_log init start:" .. cjson.encode(log_config))
    -- create logger
    logger = logging.new(function(self, level, message)
        local f, msg = open_log_file(log_config)
        if not f then
            return nil, msg
        end
        local posinfo = debug.getinfo(2, "Sl") or { short_src = "", currentline = 0 }
        local s = string.format('[%s] [%s] [%s:%d] %s\n',
            os.date("%Y-%m-%d %H:%M:%S", ngx.time()),
            level,
            posinfo.short_src,
            posinfo.currentline,
            message)
        f:write(s)
        return true
    end)
    logger:setLevel(log_config.level)
    logger.d = logger.debug
    logger.i = logger.info
    logger.w = logger.warn
    logger.e = logger.error
    logger.f = logger.fatal
    luastar_cache.set("logger", logger)
    return logger
end

function ngx_log_err(msg)
    ngx.log(ngx.ERR, msg or "-----luastar")
end