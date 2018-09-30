--
-- Created by IntelliJ IDEA.
-- User: guozhangxiao
-- Date: 15/8/3
-- Time: 上午9:56
-- To change this template use File | Settings | File Templates.
-- 获取歌曲详情/批量获取歌曲详情
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

-- 获取歌曲详情
function info(request, response)

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
    local music = get_musicinfo_by_id(mid,redis)
    local result_json = json_util.success(music)
    if str_util.isnull(music) then
        result_json = json_util.not_found()
    end

    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end

-- 批量获取歌曲详情
function batchinfo(request, response)

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
    param["mids"] = request:get_arg("mids");

    --[[ 验证sign --]]
    local isSign = check:sign(param)
    if not isSign then
        logger:i(" isSign error")
        response:writeln(json_util.fail("sign error"))
        return
    end

    --[[ 参数检查--]]
    if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) or str_util.isnull(param["mids"]) then
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

    local mids = param["mids"]
    local mid_array = str_util.split(mids,",",0)
    local batchmusic = {}
    local music_list = {}
    if mid_array ~= nil and table.getn(mid_array) > 0 then
        local index = 1
        for i=1, #(mid_array) do
            local mid = mid_array[i]
            if mid ~= nil and mid ~= "" then
                local music = get_musicinfo_by_id(mid,redis)
                if not str_util.isnull(music) then
                    music_list[index] = music
                    index = index + 1
                end
            end
        end
    else
        logger:info("batchinfo mids is nil")
    end
    batchmusic.musiclist = music_list

    local result_json = json_util.formatesuccess(batchmusic)
    if table.getn(music_list) == 0 then
        result_json = json_util.not_found()
    end

    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end

-- 根据歌曲Id获取歌曲详情
function get_musicinfo_by_id(mid,redis)
    local music = {}
    local musicInfo = table_util.array_to_hash(redis:hgetall("md:audio:"..mid))
    if str_util.isnull(musicInfo) then
        logger:info("musicInfo is nil")
    else
        logger:info("mid:"..mid)
        music.mid = mid
        music.songname = musicInfo.audio_name or ""
        music.singer = get_singername_by_id(musicInfo.uid,redis)
        music.desc = musicInfo.introduce or ""
        music.songurl = musicInfo.play_url or ""
        music.songpic = get_picurl_by_id(musicInfo.poster_h_uuid,redis)
        music.audioDuration = musicInfo.duration or ""
        local shareUrl = redis:get("md:audio:shareurl");
        if str_util.isnull(shareUrl) then
            logger:info("md audio shareUrl is nil")
            shareUrl = ""
        end
        music.share_url = shareUrl

    end
    return music
end

-- 根据图片id获取图片地址
function get_picurl_by_id(pid,redis)
    local picurl = ""
    if nil ~= pid and ""~=pid then
        local picinfo = table_util.array_to_hash(redis:hgetall("pub:image:"..pid))
        picurl = picinfo.pic_url
    end
    return picurl
end

--根据歌手id获取歌手名称
function get_singername_by_id(uid,redis)
    local singername = ""
    if not str_util.isnull(uid) then
        local singerInfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
        singername = singerInfo.username
    end
    return singername
end


