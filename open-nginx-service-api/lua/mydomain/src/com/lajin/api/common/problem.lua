--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/7/13
-- Time: 下午4:06
-- To change this template use File | Settings | File Templates.
-- 常见问题
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")

function problem(request, response)
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
    local problem_url = redis:get("app:config:problem")
    if str_util.isnull(problem_url) then
        data.url = ""
        logger:info("problem_url is nil")
    else
        data.url = problem_url
        logger:info("problem_url is"..problem_url)
    end

    local result_json = json_util.success(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end

