#!/usr/bin/env lua
--[[
	首页轮播图接口
]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local date_util = require("luastar.util.date")
local sql_util = require("luastar.util.sql")
local beanFactory = luastar_context.getBeanFactory()

local function saveFeedback(param)
    local sql_table = {
        sql = [[
            insert into FB_INFO (
			    FB_CODE,LINK_WAY,LINK_MAN,APP_ID,APP_VERSION,OS_NAME,FB_CONTENT,STATUS,CREATED_TIME
    	    ) values (
			    #{fbCode},#{linkWay},#{linkMan},#{appId},#{appVersion},#{osName},#{fbContent},#{status},#{createdTime}
    	    )
        ]]
    }
    param["fbCode"] = tostring(date_util.get_ngx_time() % 100000)
    while (string.len(param["fbCode"]) < 5) do
        param["fbCode"] = "0" .. param["fbCode"]
    end
    local data = {
        fbCode = param["fbCode"],
        linkWay = param["linkway"],
        linkMan = param["linkman"],
        appId = 1,
        appVersion = param["appversion"],
        osName = param["osrelease"],
        fbContent = param["content"],
        status = 1,
        createdTime = date_util.get_timestamp2()
    }
    local mysql_util = beanFactory:getBean("mysql")
    local mysql = mysql_util:getConnect()
    local sql = sql_util.getsql(sql_table, data)
    ngx.log(logger.i("保存反馈sql语句：", sql))
    local res, err, errcode, sqlstate = mysql:query(sql)
    ngx.log(logger.i("保存反馈结果：res=", cjson.encode(res), ", err=", err, ",errcode=", errcode, ",sqlstate=", sqlstate))
    mysql_util:close(mysql)
    return res, err, errcode, sqlstate
end

function feedback(request, response)
    -- 校验必填参数
    -- head参数
    local paramService = beanFactory:getBean("paramService")
    local headParam = paramService:getHeadParam()
    -- 私有参数
    local param = {}
    param["content"] = request:get_arg("content") or "";
    param["linkman"] = request:get_arg("linkman") or "";
    param["linkway"] = request:get_arg("linkway") or "";
    -- 合并参数
    param = _.defaults(param, headParam)
    -- 校验必填参数
    local hasEmpty = _.any(_.pick(param, "content", "linkway"), function(v) if _.isEmpty(v) then return true end end)
    if hasEmpty then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 保存反馈信息
    local res, err = saveFeedback(param)
    if _.isEmpty(res) then
        response:writeln(json_util.exp("save feedback fail : " .. err))
        return
    end
    local rs = {
        fbCode = param["fbCode"]
    }
    response:writeln(json_util.success(rs))
end