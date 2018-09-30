#!  /usr/bin/env lua
--[[

]]
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function upgrade(request, response)
    -- 参数校验
    local check = beanFactory:getBean("check")
    local isHead = check:head()
    --[[ head参数校验--]]
    if not isHead then
        logger:info("Head  args is null.")
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 获取参数
    local param = {}
    param["appkey"] = request:get_header("appkey")
    param["verstr"] = request:get_header("appversion")
    param["verint"] = tonumber(request:get_arg("verint", 0))
    param["ip"] = request.remote_addr
    param["devid"] = request:get_header("devid")
    param["devmac"] = request:get_header("devmac")
    param["devmanufacturer"] = request:get_header("devmanufacturer")
    param["devmodel"] = request:get_header("devmodel")
    logger:info("----------upgrade param:" .. cjson.encode(param))
    -- 参数校验
    if not _.isNumber(param["verint"]) or param["verint"] <= 0 then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 获取redis
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local pkgid = redis:get("up:pkg:key:" .. param["appkey"])
    if str_util.isnull(pkgid) then
        response:writeln(json_util.not_found("pkgid is null."))
        return
    end
    -- 取最新的二个版本
    local ver_last_ary = redis:zrange("up:pkg:ver:" .. pkgid, -2, -1, "WITHSCORES")
    if not ver_last_ary or type(ver_last_ary) == "userdata" or #ver_last_ary == 0 then
        response:writeln(json_util.not_found("ver_last is null."))
        return
    end
    local data = {
        upgrade = 0,
        verstr = "",
        verint = 0,
        vername = "",
        title = "",
        desc = "",
        uptype = 1,
        silent_download = 0,
        silent_install = 0,
        prompt_up = 1,
        prompt_always = 0,
        prompt_interval = 24,
        filemd5 = "",
        fileurl = ""
    }
    local result_json = json_util.success(data)
    for i = #ver_last_ary, 1, -2 do
        if param["verint"] >= tonumber(ver_last_ary[i]) then
            break
        else
            local verid = ver_last_ary[i - 1]
            local isup, data = is_upgrade(redis, verid, param)
            if isup then
                result_json = json_util.success(data)
                break
            end
        end
    end
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end

function is_upgrade(redis, verid, param)
    local verinfo = table_util.array_to_hash(redis:hgetall("up:ver:info:" .. verid))
    if str_util.isnull(verinfo) then
        logger:info("----------up:ver:info:" .. verid .. " is not found.")
        return false
    end
    -- 规则过滤逻辑
    local filter_result = filter_upgrade(redis, verid, param)
    if not filter_result.upgrade then
        logger:info("----------filter to not upgrade.")
        return false
    end
    local data = {}
    data.upgrade = 1
    if filter_result.forceup then
        logger:info("----------filter to forceup.")
        data.uptype = 2
        data.title = verinfo.ver_title_force
        data.desc = verinfo.ver_desc_force
    else
        data.uptype = tonumber(verinfo.up_type)
        data.title = verinfo.ver_title
        data.desc = verinfo.ver_desc
    end
    data.verstr = verinfo.ver_str
    data.verint = tonumber(verinfo.ver_int)
    data.vername = verinfo.ver_name
    data.silent_download = tonumber(verinfo.silent_download)
    data.silent_install = tonumber(verinfo.silent_install)
    data.prompt_up = tonumber(verinfo.prompt_up)
    data.prompt_always = tonumber(verinfo.prompt_always)
    data.prompt_interval = tonumber(verinfo.prompt_interval)
    data.filemd5 = verinfo.file_md5
    data.fileurl = verinfo.file_url
    return true, data
end

function filter_upgrade(redis, verid, param)
    local filter_result = {
        upgrade = true,
        forceup = false
    }
    local ver_rule_ary = redis:smembers("up:ver:rule:list:" .. verid)
    if str_util.isnull(ver_rule_ary) then
        return filter_result
    end
    _.any(ver_rule_ary, function(ver_rule_id)
        local ver_rule_info = table_util.array_to_hash(redis:hgetall("up:ver:rule:info:" .. ver_rule_id))
        if str_util.isnull(ver_rule_info) then
            return false
        end
        local rule_info = table_util.array_to_hash(redis:hgetall("up:rule:" .. ver_rule_info["rule_id"]))
        if str_util.isnull(rule_info) then
            return false
        end
        local rule_con_id_ary = redis:smembers("up:ver:rule:conlist:" .. ver_rule_id)
        if str_util.isnull(rule_con_id_ary) then
            return false
        end
        local rule_con_ary = _.select(_.map(rule_con_id_ary, function(key1, con_id)
            -- 针对每个条件id获取其详细信息
            return table_util.array_to_hash(redis:hgetall("up:ver:rule:coninfo:" .. con_id))
        end), function(key2, rule_con_info)
            -- 过滤掉信息为空的条件
            return not str_util.isnull(rule_con_info)
        end)
        if str_util.isnull(rule_con_ary) then
            return false
        end
        local judge_result = rule_judge(param, rule_info, ver_rule_info, rule_con_ary, true)
        -- 影响结果类型（1-是否可升级，2-是否强制升级）
        local return_type = tonumber(rule_info["return_type"]) or 1
        if return_type == 1 then
            if not judge_result then
                -- 限制升级
                filter_result.upgrade = false
                return true
            end
        elseif return_type == 2 then
            if judge_result then
                -- 强制升级
                filter_result.forceup = true
            end
        end
        return false
    end)
    return filter_result
end

function rule_judge(param, rule_info, ver_rule_info, rule_con_ary, default_value)
    -- 1-在范围内，2-不在范围内
    local judge_way = tonumber(ver_rule_info["judge_way"]) or 1
    -- 1-等于value，2-小于max，3-小于等于max，4-大于min，5-大于等于min，6-大于min小于max，7-大于min小于等于max，8-大于等于min小于max，9-大于等于min小于等于max
    local compare_type = tonumber(rule_info["compare_type"]) or 1
    -- 比较字段，多个用“,”隔开
    local compare_col_ary = str_util.split(rule_info["compare_col"], ",")
    local client_value_ary = {}
    _.each(compare_col_ary, function(key, value)
        if param[value] then
            _.push(client_value_ary, param[value])
        end
    end)
    if str_util.isnull(client_value_ary) then
        return default_value
    end
    if judge_way == 1 then
        if compare_type == 1 then
            return _.any(rule_con_ary, function(rule_con_info)
                return _.any(client_value_ary, function(client_value)
                    return string.upper(rule_con_info["eq_value"]) == string.upper(client_value)
                end)
            end)
        elseif compare_type == 2 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] < tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 3 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] <= tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 4 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] > tonumber(rule_con_info['min_value'])
            end)
        elseif compare_type == 5 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] >= tonumber(rule_con_info['min_value'])
            end)
        elseif compare_type == 6 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] > tonumber(rule_con_info['min_value']) and client_value_ary[1] < tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 7 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] > tonumber(rule_con_info['min_value']) and client_value_ary[1] <= tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 8 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] >= tonumber(rule_con_info['min_value']) and client_value_ary[1] < tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 9 then
            return _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] >= tonumber(rule_con_info['min_value']) and client_value_ary[1] <= tonumber(rule_con_info['max_value'])
            end)
        end
    elseif judge_way == 2 then
        if compare_type == 1 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return _.any(client_value_ary, function(client_value)
                    return string.upper(rule_con_info["eq_value"]) == string.upper(client_value)
                end)
            end)
        elseif compare_type == 2 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] < tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 3 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] <= tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 4 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] > tonumber(rule_con_info['min_value'])
            end)
        elseif compare_type == 5 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] >= tonumber(rule_con_info['min_value'])
            end)
        elseif compare_type == 6 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] > tonumber(rule_con_info['min_value']) and client_value_ary[1] < tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 7 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] > tonumber(rule_con_info['min_value']) and client_value_ary[1] <= tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 8 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] >= tonumber(rule_con_info['min_value']) and client_value_ary[1] < tonumber(rule_con_info['max_value'])
            end)
        elseif compare_type == 9 then
            return not _.any(rule_con_ary, function(rule_con_info)
                return client_value_ary[1] >= tonumber(rule_con_info['min_value']) and client_value_ary[1] <= tonumber(rule_con_info['max_value'])
            end)
        end
    end
    return default_value
end