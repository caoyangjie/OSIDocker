--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/8/3
-- Time: 下午2:42
-- To change this template use File | Settings | File Templates.
-- 获取歌曲歌词
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

-- 获取歌曲歌词
function lyrics(request, response)

    local check = beanFactory:getBean("check")
    local isHead = check:head()
    --[[ head参数校验--]]
    if not isHead then
        logger:i(" Head  args is null")
        response:writeln(json_util.illegal_argument())
        return
    end

    --[[ 私有参数校验 --]]
    local param = {}

    param["utoken"] = request:get_arg("utoken");
    param["uid"] = request:get_arg("uid");
    param["mid"] = request:get_arg("mid");

    --[[ 验证sign --]]
    local isSign = check:sign(param)
    if not isSign then
        logger:i(" isSign error")
        response:writeln(json_util.fail("sign error"))
        return
    end

    --[[ 参数检查--]]
    if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) or str_util.isnull(param["mid"]) then
        response:writeln(json_util.illegal_argument())
        return
    end

    --[[ 获得redis链接--]]
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()

    --[[ 校验token--]]
    local loginProcess = beanFactory:getBean("loginProcess")
    local ok ,emsg = loginProcess:checktoken(param,redis)
    if not ok then
        redis_util:close(redis)
        response:writeln(json_util.illegal_token(emsg))
        return
    end

    -- 业务代码start

    local mid = param["mid"]
    local music = {}
    local musicInfo = table_util.array_to_hash(redis:hgetall("md:audio:"..mid))
    if str_util.isnull(musicInfo) then
        logger:info("musicInfo is nil")
        music.lyrics = ""
    else
        logger:info("mid:"..mid)
        local lyr_url = musicInfo.lyr_url or ""
        music.lyrics = lyr_url
    end

    local result_json = json_util.success(music)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end


