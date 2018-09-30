--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/7/13
-- Time: 下午4:05
-- To change this template use File | Settings | File Templates.
-- 关于(H5配置使用)
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")

function aboutinfo(request, response)
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
    local aboutInfo = table_util.array_to_hash(redis:hgetall("app:config:aboutinfo"))
    if str_util.isnull(aboutInfo) then
        logger:info("aboutInfo is nil")
    else
        data.logo = aboutInfo.logo or ""
        data.appversion = aboutInfo.appversion or ""
        data.content = aboutInfo.content or ""
        data.copyright = aboutInfo.copyright or ""
    end

    local result_json = json_util.success(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end