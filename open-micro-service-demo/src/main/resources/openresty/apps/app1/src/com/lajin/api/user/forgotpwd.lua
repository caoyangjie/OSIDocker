--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/7/14
-- Time: 下午6:11
-- To change this template use File | Settings | File Templates.
-- 更改密码
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")

function forgotpwd(request, response)
    local check = beanFactory:getBean("check")
    local isHead = check:head()

    --[[ head参数校验--]]
    if not isHead then
        logger:i(" Head  args is null")
        response:writeln(json_util.illegal_argument())
        return
    end

    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()

    --[[ 私有参数校验 --]]
    local param = {}

    param["phone"] = request:get_arg("phone");
    param["newpw"] = request:get_arg("newpw");
    param["checkcode"]= request:get_arg("checkcode");

    --[[ 验证sign --]]
    local isSign = check:sign(param)
    if not isSign then
        logger:i(" isSign error")
        response:writeln(json_util.fail("sign error"))
        return
    end

    --[[ 参数检查--]]
    if str_util.isnull(param["phone"]) or str_util.isnull(param["newpw"]) or str_util.isnull(param["checkcode"]) then
        response:writeln(json_util.illegal_argument())
        return
    end

    --[[ 检查短信验证码是否过期 --]]
    local sms = redis:hgetall("checkcode:"..param["phone"])
    sms = table_util.array_to_hash(sms)
    if  not str_util.isnull(sms['timeout']) then
        local timeOut = sms['timeout']
        timeOut = timeOut-os.time()
        if timeOut <= 0 then
            response:writeln(json_util.fail('短信验证码超时!'))
            return
        elseif  not str_util.isnull(param["checkcode"]) and param["checkcode"] ~= sms['code'] then
            response:writeln(json_util.fail('验证码输入错误!'))
            return
        end
    else
        response:writeln(json_util.fail('验证码输入错误!'))
        return
    end

--    local url = "http://127.0.0.1:8004/inner/app/uc/user/forgotpwd"
--    local lua_http_post = (require "multipart-post").lua_http_post
--    local resUpload = lua_http_post(url,param)

    local httpclient = require("luastar.util.httpclient")
    ok, code, headers, status, resUpload = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/app/uc/user/forgotpwd","POST",param,1500000,{})

    if tonumber(code) ~= 200 then
        response:writeln(json_util.fail('http request error'))
        return
    end

    if str_util.isnull(resUpload) then
        redis_util:close(redis)
        response:writeln(json_util.fail('http request error'))
        return
    end

    local resUpload_table = cjson.decode(resUpload)
    if not str_util.isnull(resUpload_table)  then
        if resUpload_table["code"] =="A000000" then
            local data = {}
            response:writeln(json_util.success(data))
        else
            response:writeln(json_util.fail(resUpload_table['message']))
            return
        end
    else
        response:writeln(json_util.not_found())
    end
end


