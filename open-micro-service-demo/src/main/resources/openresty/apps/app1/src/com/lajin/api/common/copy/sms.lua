--[[
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/7/8
-- Time: 下午2:41
-- To change this template use File | Settings | File Templates.
--短信邀请文案
--]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")

function sms(request, response)
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
    local sms_content = redis:get("app:config:copy:sms1")
    if str_util.isnull(sms_content) then
        data.content = ""
        logger:info("sms_content is nil")
    else
        data.content = sms_content
        logger:info("sms_content is"..sms_content)
    end

    local result_json = json_util.success(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end
