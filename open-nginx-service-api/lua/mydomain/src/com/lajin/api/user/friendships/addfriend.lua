--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/7/14
-- Time: 上午10:34
-- To change this template use File | Settings | File Templates.
-- 添加好友/推荐感兴趣的用户
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function addfriend(request, response)

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
    param["keywords"]= request:get_arg("keywords");

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

    -- 业务代码 start
    local data = {}
    local friend_list = {}
    local recom_list = {}

    local keywords = param["keywords"]
    local uid = param["uid"]
    -- 推荐好友
    if  str_util.isnull(keywords) then
        logger:info("keywords is nil")
    else
        -- 根据keywords获取用户Id列表
        local uids = get_friend_ids(keywords)
        if table.getn(uids) > 0 then
            local index = 1
            for i=1, #(uids) do
                local friend_str = {}
                local fuid = uids[i]
                local friend_info = table_util.array_to_hash(redis:hgetall("user:info:"..fuid))
                if str_util.isnull(friend_info) then
                    logger:info("friend_info is nil,uid="..fuid)
                else
                    friend_str.uid = tonumber(fuid)
                    if str_util.isnull(friend_info.phone) then
                        friend_str.phone = ""
                    else
                        friend_str.phone = str_util.md5(friend_info.phone)
                    end
                    friend_str.username = friend_info.username or ""
                    friend_str.namereal = friend_info.namereal or ""
                    friend_str.picurl = friend_info.picurl or ""
                    friend_str.userrole = get_musician_role(friend_info.userrole)
                    --friend_str.is_follow = redis:sismember("user:following:"..uid,fuid)
                    local is_follow = redis:zrank('user:following:'..uid,fuid)
                    friend_str.is_follow = not str_util.isnull(is_follow) and  1 or 0

                    friend_list[index] = friend_str
                    index = index + 1
                end
            end
            if table.getn(friend_list) > 0 then
                data.friendlist = friend_list
                data.recommlist = recom_list
            else
                logger:info("friend_list size is 0")
            end
        else
        end

    end

    -- 推荐感兴趣的人(系统)
    if data.friendlist == nil then
        data.friendlist = friend_list
        --好友列表为空,添加系统推荐用户
        local attention_array = redis:zrange("recom:attention",0,-1)
        if table.getn(attention_array) > 0 then
            local index = 1
            for i=1, #(attention_array) do
                local attention_str = {}
                local userId = attention_array[i]
                local userInfo = table_util.array_to_hash(redis:hgetall("user:info:"..userId))
                if str_util.isnull(userInfo) then
                    logger:info("userInfo is nil,userId="..userId)
                else
                    attention_str.uid = tonumber(userId)
                    if str_util.isnull(userInfo.phone) then
                        attention_str.phone = ""
                    else
                        attention_str.phone = str_util.md5(userInfo.phone)
                    end
                    attention_str.username = userInfo.username or ""
                    attention_str.namereal = userInfo.namereal or ""
                    attention_str.picurl = userInfo.picurl or ""
                    attention_str.userrole = get_musician_role(userInfo.userrole)
                    local is_follow = redis:zrank('user:following:'..uid,userId)
                    attention_str.is_follow = not str_util.isnull(is_follow) and  1 or 0
                    recom_list[index] = attention_str
                    index = index + 1
                end
            end
            data.recommlist = recom_list
        else
        end
    else
        --好友列表不为空,不添加系统推荐用户
    end
    table_util.tabletoarr(data,arrkey())
    local result_json = json_util.formatesuccess(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end

function get_friend_ids(keyword)

    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local uids = {}
    -- 根据名称/电话号码查找
    --local url = "http://219.234.131.42:8004/inner/app/uc/user/filter"

    local param = {}
    param["username"] =  str_util.urlencode(keyword)
    local httpclient = require("luastar.util.httpclient")
    ok, code, headers, status, resUpload = httpclient.request(luastar_config.getConfig('httproot')['address'].."/inner/app/uc/user/filter","POST",param,1500000,{})
	--ok, code, headers, status, resUpload = httpclient.request("http://219.234.131.42:8004".."/inner/app/uc/user/filter","POST",param,1500000,{})
    if tonumber(code) ~= 200 then
        response:writeln(json_util.fail('http request error'))
        return
    end

    local resUpload_table = cjson.decode(resUpload)
    if not str_util.isnull(resUpload_table)  then
        if resUpload_table["code"] =="A000000" then
            uids = resUpload_table["data"]
        else
            redis_util:close(redis)
            response:writeln(json_util.fail(resUpload_table['message']))
            return
        end
    else
        redis_util:close(redis)
        response:writeln(json_util.not_found())
    end

    return uids
end

function get_musician_role(rolestr)

    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    if str_util.isnull(rolestr) then
        return ''
    else
    end
    local musicianrole = ''
    local role_array = str_util.split(rolestr,",",0)
    if role_array ~= nil and table.getn(role_array) > 0 then
        for i=1, #(role_array) do
            local rolenum = role_array[i]
            local rolename = redis:hget("musician:role",rolenum)
            if not str_util.isnull(rolename) then
                musicianrole = musicianrole..","..rolename
            else
            end
        end

    else
    end
    local rolelen = string.len(musicianrole)
    if rolelen > 0 then
        musicianrole = string.sub(musicianrole,2,rolelen)
    else
    end
    return musicianrole
end

--[[ 转数组的key--]]
function arrkey()
    local o = {}
    o['friendlist']=true
    o['recommlist']=true
    return o
end
