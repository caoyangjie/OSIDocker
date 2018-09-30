#! /usr/bin/env lua
--[[
	
]]
module(..., package.seeall)

local ok, new_tab = pcall(require, "table.new")
local str_util = require("luastar.util.str")
if not ok then
    new_tab = function(narr, nrec) return {} end
end

function array_to_hash(t)
    if not t or not _.isArray(t) then
        return nil
    end
    local n = #t
    local h = new_tab(0, n / 2)
    for i = 1, n, 2 do
        h[t[i]] = t[i + 1]
    end
    return h
end

function table_print(tt, indent, done)
    local done = done or {}
    local indent = indent or 0
    local space = string.rep(" ", indent)
    if type(tt) == "table" then
        local sb = {}
        for key, value in pairs(tt) do
            table.insert(sb, space) -- indent it
            if type(value) == "table" and not done[value] then
                done[value] = true
                table.insert(sb, key .. " = {\n");
                table.insert(sb, table_print(value, indent + 2, done))
                table.insert(sb, space) -- indent it
                table.insert(sb, "}\n");
            elseif "number" == type(key) then
                table.insert(sb, string.format("\"%s\" ", tostring(value)))
            else
                table.insert(sb, string.format("%s = \"%s\"\n", tostring(key), tostring(value)))
            end
        end
        return table.concat(sb)
    else
        return tt .. "\n"
    end
end

function table2str(t, seq)

    local t_arr = _.values(_.map(t, function(k, v) return k .. "=" .. v end))
    return table.concat(t_arr, seq or "")
end

function table2arr(t, seq)
    seq = seq or "="
    return _.values(_.map(t, function(k, v) return k .. seq .. v end))
end

function arryunion(t,t1)
	for k,v in pairs(t) do
		if t1[k] then
			if k == 'phone' and not str_util.isnull(t1[k]) then
				local phone = t1[k]
				t[k]=str_util.md5(phone)
			else
				t[k] = t1[k]
			end
		end
	end
	return t
end

function tabletoarr(t,t1)
	if type(t) == 'table'then
		for k,v in pairs(t) do
			if t1[k] == true and type(t[k]) ~='table' then
				if k == 'userrole' then
					t['userrolearr']=str_util.formatarry(t[k])
				elseif k == 'userrolearr' then
				
				else
					t[k]=str_util.formatarry(t[k])
				end
			else
				t[k] = tabletoarr(t[k],t1)
			end
		end
	end
	return t
end