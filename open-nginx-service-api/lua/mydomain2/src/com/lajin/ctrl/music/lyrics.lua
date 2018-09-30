--
-- Created by vim.
-- User: fuhao
-- Date: 15/10/14
-- Time: 上午9:56
-- To change this template use File | Settings | File Templates.
-- 歌曲歌词
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()
local cjson = require("cjson")
local underscore = require("underscore")
-- 获取歌曲详情
function lyrics(request, response)

    --[[ 私有参数校验 --]]
    local param = {}
	param["callback"]= request:get_arg("callback");
    param["mid"] = request:get_arg("mid");
    if  str_util.isNil(param["mid"]) then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 业务代码start
    local mid = param["mid"]
    local lyricsinfo = get_musicinfo_by_id(mid)
	ngx.log(ngx.INFO,cjson.encode(lyricsinfo))
    if str_util.isNil(lyricsinfo) or str_util.isNil(lyricsinfo['lrc']) or str_util.isNil(lyricsinfo['lrc']['lyric'])  then
        return response:writeln(json_util.fail('歌词不存在'))
    end
	lyricsinfo = lyricsinfo['lrc']['lyric']
	ngx.log(ngx.INFO, lyricsinfo)
	local lyrics_list = underscore.map(str_util.split(lyricsinfo,"%\n"),function(v)
			local line = v -- string.gsub(v,'^%[.*%]','')
			line = str_util.trim(line)
			if '' == line then
				return nil
			end
			return line
		end
	)
	local result_json = json_util.success({lyrics = lyrics_list})
	if nil ~= param["callback"] and ""~= param["callback"] then
		result_json = param["callback"] .."("..result_json..")"
	end
    response:writeln(result_json)
end


function lyrics_file(request, response)
    --[[ 私有参数校验 --]]
    local param = {}
    param["mid"] = request:get_arg("mid");
	param["callback"]= request:get_arg("callback");
    if  str_util.isNil(param["mid"]) then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 业务代码start
    local mid = param["mid"]
    local lyricsinfo = get_musicinfo_by_id(mid)
	ngx.log(ngx.INFO,cjson.encode(lyricsinfo))
    if str_util.isNil(lyricsinfo) or str_util.isNil(lyricsinfo['lrc']) or str_util.isNil(lyricsinfo['lrc']['lyric'])  then
        return response:writeln(json_util.fail('歌词不存在'))
    end
	lyricsinfo = lyricsinfo['lrc']['lyric']
	-- ngx.log(ngx.INFO, lyricsinfo)
	local result_json = json_util.success({lyrics = lyricsinfo})
	if nil ~= param["callback"] and ""~= param["callback"] then
		result_json = param["callback"] .."("..result_json..")"
	end
    response:writeln(result_json)
end

function get_musicinfo_by_id(mid)
	local httpclient = require("luastar.util.httpclient")
	-- ?ids=[66842]
	local s_param = '&id=' .. mid
	local header = {}
	header['Cookie'] = 'appver=2.0.2'
	header['Referer'] = 'http://music.163.com'
	ngx.log(ngx.INFO,luastar_config.getConfig('wyapi')['detail_url'] .. s_param)
	local ok, code, headers, status, resbody = httpclient.request(luastar_config.getConfig('wyapi')['lyric_url'] .. s_param,"GET",{},1500000,header)
    ngx.log(ngx.INFO,resbody)
	if tonumber(code) ~= 200 or str_util.isNil(resbody) then
		-- response:writeln(json_util.fail('http request error'))
		return
	end
	
	local resbody_table = cjson.decode(resbody)
	return resbody_table
end

-- search_musicinfo_by_name('寂寞难耐')
