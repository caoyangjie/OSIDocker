--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/7/14
-- Time: 下午2:50
-- To change this template use File | Settings | File Templates.
-- 获取通讯录大咖用户
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function higherups(request, response)

    local check = beanFactory:getBean("check")
    local isHead = check:head()

    --[[ head参数校验--]]
    if not isHead then
        logger:i(" Head  args is null")
        response:writeln(json_util.illegal_argument())
        return
    end

    --[[ 私有参数校验 --(uid:10000,utoken,addrlist(F4cBoH,KJd111)]]
    local param = {}

    param["utoken"] = request:get_arg("utoken");
    param["uid"] = request:get_arg("uid");
    param["addrlist"] = request:get_arg("addrlist");


    --[[ 验证sign --]]
    local isSign = check:sign(param)
    if not isSign then
        logger:i(" isSign error")
        response:writeln(json_util.fail("sign error"))
        return
    end

    --[[ 参数检查--]]
    if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
        logger:info("utoken error")
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

    -- 业务代码 start
    local addrlist = param["addrlist"]
    local data = {}
    local friend_list = {}

    -- 好友列表
    if str_util.isnull(addrlist) then
        logger:info("addrlist is nil")
    else
        --根据逗号分割获取手机号
        local addr_array = str_util.split(addrlist,",",0)
        if addr_array ~= nil and table.getn(addr_array) > 0 then
            local index = 1
            for i=1, #(addr_array) do
                local friend_str = {}
                local phone_62 = addr_array[i]
                if phone_62 ~= nil and phone_62 ~= '' then
                    --62进制转10进制
                    local phone_10 = str_util.trans62to10(phone_62)
                    --根据电话号码获取用户信息
                    local fuid = redis:get("user:phone:"..phone_10)
                    if str_util.isnull(fuid) then
                        logger:info("get uid error,phone is "..phone_10)
                    else
                        logger:info("fuid is"..fuid)
                        local friend_info = table_util.array_to_hash(redis:hgetall("user:info:"..fuid))
                        if str_util.isnull(friend_info) then
                            logger:info("friend_info is nil,uid="..fuid)
                        elseif str_util.isnull(friend_info.usertype) or friend_info.usertype ~= "1" then
							logger:info("friend is not higherups,uid="..fuid)
						 else
                            friend_str.uid = tonumber(fuid) or -1
                            if str_util.isnull(friend_info.phone) then
                                friend_str.phone = ""
                            else
                                friend_str.phone = str_util.md5(friend_info.phone)
                            end
                            friend_str.username = friend_info.username or ""
                            friend_str.namereal = friend_info.namereal or ""
                            friend_str.picurl = friend_info.picurl or ""
                            friend_str.usertype = friend_info.usertype or ""
                            local roleStr = friend_info.userrole
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
                            friend_str.userrolearr = role_list
                            friend_str.thridsrc = friend_info.thridsrc or ""
                            friend_str.thirduid = friend_info.thirduid or ""

                            friend_list[index] = friend_str
                            index = index + 1
                        end
                    end
                else
					logger:info("phone number is nil")
                end
            end
            data.friendlist = friend_list
        else
            logger:info("addr_array is nil")
        end

    end

    local result_json = json_util.formatesuccess(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end