--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/7/14
-- Time: 上午11:29
-- To change this template use File | Settings | File Templates.
-- 获取个人资料
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function show(request, response)

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

    --[[ 验证sign --]]
    local isSign = check:sign(param)
    if not isSign then
        logger:i(" isSign error")
        response:writeln(json_util.fail("sign error"))
        return
    end

    --[[ 参数检查--]]
    if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
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

    local uid = param["uid"]
    local data = {}
    local userInfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
    if str_util.isnull(userInfo) then
        logger:info("userInfo is nil")
    else
        logger:info("uid:"..uid)
        if str_util.isnull(userInfo.phone) then
            data.phone = ""
        else
            data.phone = str_util.md5(userInfo.phone)
        end
        data.username = userInfo.username or ""
        data.namereal = userInfo.namereal or ""
        data.sex = userInfo.sex or ""
        data.birthday = userInfo.birthday or ""
        data.height = userInfo.height or ""
        data.bloodtype = userInfo.bloodtype or ""
        data.country = userInfo.country or ""
        data.province = userInfo.province or ""
        data.city = userInfo.city or ""
        data.picurl = userInfo.picurl or ""
        data.homepic = userInfo.homepic or ""
        data.usertype = userInfo.usertype or ""
        local roleStr = userInfo.userrole
        local role_list = {}
        if roleStr ~= nil and roleStr ~= '' then
            local roleArray = str_util.split(roleStr,",",0)
            if roleArray ~= nil and table.getn(roleArray) > 0 then
                logger:info(table.getn(roleArray))
                for i=1, #(roleArray) do
                    role_list[i] = tonumber(roleArray[i])
                end
            else
            end
        else
            logger:info("userrole is nil")
        end
        data.userrolearr = role_list
        data.thridsrc = userInfo.thridsrc or ""
        data.thirduid = userInfo.thirduid or ""
        data.lastlogintime = userInfo.lastlogintime or ""
        data['uid'] = param["uid"]
        data['utoken'] = param["utoken"]
    end
    local result_json = json_util.formatesuccess(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end

