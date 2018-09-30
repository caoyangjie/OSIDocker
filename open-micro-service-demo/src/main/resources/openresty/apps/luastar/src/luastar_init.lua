#! /usr/bin/env lua
--[[
在“ngx_lua”模块的“init_by_lua_file”命令中执行;
只在启动nginx时初始化一次。
--]]

--luastar全局变量
LUASTAR_G = _G
--luastar全局配置table
LUASTAR_C = {}

--常用库
-- Class = require("30logclean")
Class = require("luastar.core.class")
cjson = require("cjson")
_ = require("moses")
template = require("resty.template")

--luastar缓存模块
luastar_cache = require("luastar.core.cache")
--luastar配置模块
luastar_config = require("luastar.core.config")
--luastar日志模块
luastar_log = require("luastar.log.logger")
--luastar上下文模块
luastar_context = require("luastar.core.context")