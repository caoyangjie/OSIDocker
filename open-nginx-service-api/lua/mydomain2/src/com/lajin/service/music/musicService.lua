#!  /usr/bin/env lua
--[[

--]]
local musicService = Class("com.lajin.service.music.musicService")

local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local underscore = require("underscore")

function musicService:init(redis_util)
    self.redis_util = redis_util
end

--[[
-- 根据uid获取用户状态
--]]
function musicService:get_musicinfo_by_id(mid)
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


function musicService:get_music_detail_by_id(mid)
	local music = self:get_musicinfo_by_id(mid)
	if str_util.isNil(music) then
        return response:writeln(json_util.fail('搜索音乐错误'))
    end
	
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
			if tonumber(mid) == 27937682 then
				musicinfo['songurl'] = "http://lajin-audio.oss-cn-hangzhou.aliyuncs.com/music/27937682.mp3"
			end
			return musicinfo
		end
	)
	return res_music[1] or {}
end



return musicService
