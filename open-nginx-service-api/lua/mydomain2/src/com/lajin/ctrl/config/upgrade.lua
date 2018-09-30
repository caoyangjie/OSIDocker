#! /usr/bin/env lua
--[[
    检查新版本
]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function upgrade(request, response)
    -- 获取参数
    local param = {}
    param["appkey"] = request:get_header("appkey")
    param["appversion"] = request:get_header("appversion")
    param["ostype"] = request:get_header("ostype")
    param["osrelease"] = request:get_header("osrelease")
    param["verint"] = tonumber(request:get_arg("verint", 0))
    param["devid"] = request:get_arg("devid")
    param["devmac"] = request:get_arg("devmac")
    param["devmanufacturer"] = request:get_arg("devmanufacturer")
    param["devmodel"] = request:get_arg("devmodel")
    param["ip"] = request.remote_addr
    ngx.log(logger.i("upgrade param:", cjson.encode(param)))
    -- 参数校验
    if not _.isNumber(param["verint"]) or param["verint"] <= 0 then
        response:writeln(json_util.illegal_argument())
        return
    end
    local upgradeService = beanFactory:getBean("upgradeService")
    local pkgid = upgradeService:getPkgId(param["appkey"])
    if _.isEmpty(pkgid) then
        response:writeln(json_util.exp("pkgid is null."))
        return
    end
    -- 取最新的二个版本
    local last_ver_ary = upgradeService:getLastVerAry(pkgid)
    if _.isEmpty(last_ver_ary) then
        response:writeln(json_util.exp("last version is null."))
        return
    end
    local data = getDefaultResult()
    local result_json = json_util.success(data)
    for i = #last_ver_ary, 1, -2 do
        if param["verint"] >= tonumber(last_ver_ary[i]) then
            break
        else
            local verid = last_ver_ary[i - 1]
            local isup, data = is_upgrade(verid, param)
            if isup then
                result_json = json_util.success(data)
                break
            end
        end
    end
    response:writeln(result_json)
end

function is_upgrade(verid, param)
    local upgradeService = beanFactory:getBean("upgradeService")
    local verinfo = upgradeService:getVerInfo(verid)
    if _.isEmpty(verinfo) then
        ngx.log(logger.i("版本详细信息为空--up:ver:info:", verid))
        return false
    end
    -- 规则过滤逻辑
    local filter_result = filter_upgrade(verid, param)
    if not filter_result["upgrade"] then
        ngx.log(logger.i("----------filter to not upgrade."))
        return false
    end
    local data = {}
    data["upgrade"] = 1
    if filter_result["forceup"] then
        ngx.log(logger.i("----------filter to forceup."))
        data["uptype"] = 2
        data["title"] = verinfo["ver_title_force"]
        data["desc"] = verinfo["ver_desc_force"]
    else
        data["uptype"] = tonumber(verinfo["up_type"])
        data["title"] = verinfo["ver_title"]
        data["desc"] = verinfo["ver_desc"]
    end
    data["verstr"] = verinfo["ver_str"]
    data["verint"] = tonumber(verinfo["ver_int"])
    data["vername"] = verinfo["ver_name"]
    data["silent_download"] = tonumber(verinfo["silent_download"])
    data["silent_install"] = tonumber(verinfo["silent_install"])
    data["prompt_up"] = tonumber(verinfo["prompt_up"])
    data["prompt_always"] = tonumber(verinfo["prompt_always"])
    data["prompt_interval"] = tonumber(verinfo["prompt_interval"])
    data["filemd5"] = verinfo["file_md5"]
    data["fileurl"] = verinfo["file_url"]
    return true, data
end

function filter_upgrade(verid, param)
    local filter_result = {
        upgrade = true,
        forceup = false
    }
    local upgradeService = beanFactory:getBean("upgradeService")
    local ver_rule_ary = upgradeService:getVerRuleAry(verid)
    if _.isEmpty(ver_rule_ary) then
        return filter_result
    end
    _.any(ver_rule_ary, function(ver_rule_id)
        local ver_rule_info = upgradeService:getVerRuleInfo(ver_rule_id)
        if _.isEmpty(ver_rule_info) then
            return false
        end
        local rule_info = upgradeService:getRuleInfo(ver_rule_info["rule_id"])
        if _.isEmpty(rule_info) then
            return false
        end
        local rule_con_id_ary = upgradeService:getVerRuleConAry(ver_rule_id)
        if _.isEmpty(rule_con_id_ary) then
            return false
        end
        local rule_con_ary = _.select(_.map(rule_con_id_ary, function(key1, con_id)
            -- 针对每个条件id获取其详细信息
            return upgradeService:getConInfo(con_id)
        end), function(key2, rule_con_info)
            -- 过滤掉信息为空的条件
            return not _.isEmpty(rule_con_info)
        end)
        if _.isEmpty(rule_con_ary) then
            return false
        end
        local judge_result = rule_judge(param, rule_info, ver_rule_info, rule_con_ary, true)
        -- 影响结果类型（1-是否可升级，2-是否强制升级）
        local return_type = tonumber(rule_info["return_type"]) or 1
        if return_type == 1 then
            if not judge_result then
                -- 限制升级
                filter_result["upgrade"] = false
                return true
            end
        elseif return_type == 2 then
            if judge_result then
                -- 强制升级
                filter_result["forceup"] = true
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
    if _.isEmpty(client_value_ary) then
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

function getDefaultResult()
    return {
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
end