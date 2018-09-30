#!/usr/bin/env lua
--[[
	开机启动接口
]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")

local function saveDevid(param,headParam)
    -- 获取redis
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    redis:sadd("config:dev:"..headParam["ostype"], param['devid'])
    -- 关闭redis
    redis_util:close(redis)
end

local function saveUserDevid(param)
    if _.isEmpty(param["uid"]) then
        ngx.log(logger.i("开机启动无uid，不保存用户设备信息。"))
        return
    end
    -- 获取redis
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    -- 保存用户设备信息
    redis:hset("user:info:" .. param["uid"], { devid = param["devid"] })
    -- 关闭redis
    redis_util:close(redis)
end

local function getAppstartImglist(param)
    -- 获取redis
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local imglist = redis:lrange("config:appstart:imglist", 0, -1)
    -- 关闭redis
    redis_util:close(redis)
    return imglist
end

function start(request, response)
    -- head参数
    local paramService = beanFactory:getBean("paramService")
    local headParam = paramService:getHeadParam()
    -- 私有参数
    local param = {}
    param["uid"] = request:get_arg("uid") or "";
    param["devid"] = request:get_arg("devid") or "";
    param["devmac"] = request:get_arg("devmac") or "";
    param["devmanufacturer"] = request:get_arg("devmanufacturer") or "";
    param["devmodel"] = request:get_arg("devmodel") or "";
    -- 合并参数
    param = _.defaults(param, headParam)
    -- 校验必填参数
    local hasEmpty = _.any(_.pick(param, "devid"), function(v) if _.isEmpty(v) then return true end end)
    if hasEmpty then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 保存设备id
    saveDevid(param,headParam)
    -- 更新用户设备id
    saveUserDevid(param)
    -- 输出开机启动图片列表结果
    local imglist = getAppstartImglist(param)
    if _.isEmpty(imglist) then
        response:writeln(json_util.exp("no start imglist data found."))
        return
    end
	local startimgpx = luastar_config.getConfig("tplpicpx")["startimg"]
	local pic_url = imglist[1] or ''
	if not str_util.isNil(pic_url) and not str_util.isNil(startimgpx)  then 
			pic_url = pic_url..startimgpx
	end
    local rs = {
        imglist = {
            pic_url
        }
    }
    response:writeln(json_util.success(rs))
end