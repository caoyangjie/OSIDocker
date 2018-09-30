#!/usr/bin/env lua
--[[
关于
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")

function about(request, response)
    -- 参数校验
    local check = beanFactory:getBean("check")
    local isHead = check:head()

    --[[ head参数校验--]]
    if not isHead then
        logger:i(" Head  args is null")
        response:writeln(json_util.illegal_argument())
        return
    end

    -- 获取redis
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local data = {}
    local about_url = redis:get("app:config:about")
    if str_util.isnull(about_url) then
        data.url = ""
        logger:info("about_url is nil")
    else
        data.url = about_url
        logger:info("about_url is"..about_url)
    end

    local result_json = json_util.success(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end