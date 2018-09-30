--
-- Created by vim.
-- User: fuhao
-- Date: 15/10/14
-- Time: 上午9:56
-- To change this template use File | Settings | File Templates.
-- 歌曲详情
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local underscore = require("underscore")
local beanFactory = luastar_context.getBeanFactory()
-- 获取歌曲详情
function detail(request, response)

    --[[ 私有参数校验 --]]
    local param = {}
    param["mid"] = request:get_arg("mid");
	param["callback"]= request:get_arg("callback");
    --[[ 参数检查--]]
    if str_util.isNil(param["mid"]) then
        response:writeln(json_util.illegal_argument())
        return
    end

    -- 业务代码start

    local mid = param["mid"]
	local musicService = beanFactory:getBean("musicService")
	local music = musicService:get_music_detail_by_id(mid)
    -- local music = get_musicinfo_by_id(mid)
    
    if str_util.isNil(music) then
        return response:writeln(json_util.fail('音乐信息不存在'))
    end
	
	--[[
	local songs = music['songs']
	local res_music = underscore.map(songs,function(v)
			local musicinfo = {} 
			musicinfo['songurl'] = v['mp3Url']
			musicinfo['mid'] = v['id']
			musicinfo['songname'] = v['name']
			local artists = v['artists']
			musicinfo['singer'] = artists[1]['name']
			musicinfo['album'] = v['album']['name']
			musicinfo['albumid'] = v['album']['id']
			return musicinfo
		end
	)
	--]]
	
	local result_json = json_util.success(music)
	if nil ~= param["callback"] and ""~= param["callback"] then
		result_json = param["callback"] .."("..result_json..")"
	end
    response:writeln(result_json)
end

--[[
function get_musicinfo_by_id(mid)
	local httpclient = require("luastar.util.httpclient")
	
	-- ?ids=[66842]
	local s_param = '?ids=[' .. mid ..  ']'
	
	local header = {}
	header['Cookie'] = 'appver=2.0.2'
	header['Referer'] = 'http://music.163.com'
	
	local ok, code, headers, status, resbody = httpclient.request(luastar_config.getConfig('wyapi')['detail_url'] .. s_param,"GET",{},1500000,header)
    ngx.log(ngx.INFO,resbody)
	if tonumber(code) ~= 200 or str_util.isNil(resbody) then
		-- response:writeln(json_util.fail('http request error'))
		return
	end
	
	local resbody_table = cjson.decode(resbody)
	return resbody_table
end
--]]
-- search_musicinfo_by_name('寂寞难耐')
