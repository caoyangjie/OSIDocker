#!/usr/bin/env lua
--[[
    服务条款
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local beanFactory = luastar_context.getBeanFactory()

function service(request, response)
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local url = redis:hget("config:service", "conUrl")
    redis_util:close(redis)
    if _.isEmpty(url) then
        response:writeln(json_util.exp("获取服务条款失败。"))
        return
    end
    response:writeln(json_util.success({ url = url }))
end