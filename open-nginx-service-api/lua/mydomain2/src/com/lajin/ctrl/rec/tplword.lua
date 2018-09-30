#!/usr/bin/env lua
--[[
模版标签
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local underscore = require("underscore")


function tplword(request, response)
	
    local mid = request:get_arg("mid")
	local wtype = tonumber(request:get_arg("wtype",0))
	local score = tonumber(request:get_arg("score"))
	local limit = tonumber(request:get_arg("limit",20))
	 if str_util.isNil(wtype) or str_util.isNil(mid) and wtype == 0 then
        response:writeln(json_util.illegal_argument())
        return
    end
	
	if wtype ==0 then
		local lyricsinfo = get_musicinfo_by_id(mid)
		ngx.log(ngx.INFO,cjson.encode(lyricsinfo))
    
		if str_util.isNil(lyricsinfo)  or str_util.isNil(lyricsinfo['lrc']) or str_util.isNil(lyricsinfo['lrc']['lyric']) then
			return response:writeln(json_util.fail('歌词不存在'))
		end
	
		lyricsinfo = lyricsinfo['lrc']['lyric']
		ngx.log(ngx.INFO, lyricsinfo)
		local lyrics_list = underscore.map(str_util.split(lyricsinfo,"%\n"),function(v)
			local line = string.gsub(v,'^%[.*%]','')
			line = str_util.trim(line)
			if '' == line then
				return nil
			end
			local lyr = {}
			lyr['wid'] = ''
			lyr['wtype'] = 1
			lyr['word'] = line
			return lyr
		end
		)
	
		local result_json = json_util.success({tplwlist = lyrics_list or {}})
		result_json = string.gsub(result_json,"{}","[]")
		response:writeln(result_json)
		return
	end
	
	
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local old_score = '+inf'
	if not str_util.isNil(score) and tonumber(score) >0 then
		old_score = score -1
	end 
	local rec_word_list = redis:zrevrangebyscore('rec:tpl:word:' .. wtype,old_score,'-inf','limit',0,limit) 
	print(cjson.encode(rec_word_list))
	
	local word_list = underscore.map(rec_word_list,function(v)
			    print('rec:tpl:word:'..wtype..':'..v)
			    local wordInfo = table_util.array_to_hash(redis:hgetall('rec:tpl:word:'..wtype..':'..v))
				print(cjson.encode(wordInfo))
				if not str_util.isNil(wordInfo) then
					local lyr = {}
					lyr['wid'] = v	
					lyr['wtype'] = wtype	
					lyr['word'] = wordInfo.conDesc
					return lyr
				end
			end
		)
	
	local res_word = {}
	res_word['tplwlist'] = word_list or {}
	print(rec_word_list[#rec_word_list])
	score = redis:zscore('rec:tpl:word:' .. wtype,rec_word_list[#rec_word_list])  or 0-- ZSCORE key member
	redis_util:close(redis)
	res_word['score'] = score
    local result_json = json_util.success(res_word)
	result_json = string.gsub(result_json,"{}","[]")
    response:writeln(result_json)
end



function get_musicinfo_by_id(mid)
	local httpclient = require("luastar.util.httpclient")
	local s_param = '&id=' .. mid
	local header = {}
	header['Cookie'] = 'appver=2.0.2'
	header['Referer'] = 'http://music.163.com'
	ngx.log(ngx.INFO,luastar_config.getConfig('wyapi')['lyric_url'] .. s_param)
	local ok, code, headers, status, resbody = httpclient.request(luastar_config.getConfig('wyapi')['lyric_url'] .. s_param,"GET",{},1500000,header)
    ngx.log(ngx.INFO,resbody)
	if tonumber(code) ~= 200 or str_util.isNil(resbody) then
		-- response:writeln(json_util.fail('http request error'))
		return
	end
	
	local resbody_table = cjson.decode(resbody)
	return resbody_table
end
