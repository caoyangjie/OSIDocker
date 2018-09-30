#!  /usr/bin/env lua
--[[

--]]
module(..., package.seeall)

function split(str, delim, maxNb)
    -- Eliminate bad cases...
    if string.find(str, delim) == nil then
        return { str }
    end
    if maxNb == nil or maxNb < 1 then
        maxNb = 0 -- No limit
    end
    local result = {}
    local pat = "(.-)" .. delim .. "()"
    local nb = 0
    local lastPos
    for part, pos in string.gfind(str, pat) do
        nb = nb + 1
        result[nb] = part
        lastPos = pos
        if nb == maxNb then break end
    end
    -- Handle the last field
    if nb ~= maxNb then
        result[nb + 1] = string.sub(str, lastPos)
    end
    return result
end

function startsWith(str, substr)
    if str == nil or substr == nil then
        return false
    end
    if string.find(str, substr) ~= 1 then
        return false
    else
        return true
    end
end

function endsWith(str, substr)
    if str == nil or substr == nil then
        return false
    end
    local str_tmp, substr_tmp = string.reverse(str), string.reverse(substr)
    if string.find(str_tmp, substr_tmp) ~= 1 then
        return false
    else
        return true
    end
end

function indexOf(str, substr)
    return string.find(str, substr, 1, true)
end

function lastIndexOf(str, substr)
    return string.match(str, '.*()' .. substr)
end

function trim(str)
    return str:match '^()%s*$' and '' or str:match '^%s*(.*%S)'
end

-- 对字符串进行 URL 编码
function escape_uri(str)
    return ngx.escape_uri(str)
end

-- 对字符串进行反 URL 编码
function unescape_uri(str)
    return ngx.unescape_uri(str)
end

-- encode base64  
function encode_base64(str)
    return ngx.encode_base64(str)
end


-- decode base64  
function decode_base64(str)
    return ngx.decode_base64(str)
end

-- md5 
function md5(str)
    return ngx.md5(str)
end

function remove(str, mvstr)
    local lcSubStrTab = {}
    while true do
        local lcPos = string.find(str, mvstr)
        if not lcPos then
            lcSubStrTab[#lcSubStrTab + 1] = str
            break
        end
        local lcSubStr = string.sub(str, 1, lcPos - 1)
        lcSubStrTab[#lcSubStrTab + 1] = lcSubStr
        str = string.sub(str, lcPos + 1, #str)
    end
    local lcMergeStr = ""
    local lci = 1
    while true do
        if lcSubStrTab[lci] then
            lcMergeStr = lcMergeStr .. lcSubStrTab[lci]
            lci = lci + 1
        else
            break
        end
    end
    return lcMergeStr
end

--62to10
function trans62to10(ident62)
    local decimal = 0
    local base = 62
    local keisu = 0
    local ident_array = gsplit(ident62)
    if isnull(ident_array) or table.getn(ident_array) < 1 then
        return -1
    else
    end
    local cnt = table.getn(ident_array) - 1
    if table.getn(ident_array) > 0 then
        for i = 1, #(ident_array) do
            local number_byte = string.byte(ident_array[i])
            local number_rel = 0
            if number_byte > 48 and number_byte <= 57 then
                number_rel = number_byte - 48
            elseif number_byte >= 65 and number_byte <= 90 then
                number_rel = number_byte - 65 + 10
            elseif number_byte >= 97 and number_byte <= 122 then
                number_rel = number_byte - 97 + 10 + 26
            end
            keisu = math.pow(base, cnt)
            decimal = decimal + number_rel * keisu
            cnt = cnt - 1
        end
        return decimal
    else
        return -1
    end
end

function gsplit(str)
    local str_tb = {}
    if string.len(str) ~= 0 then
        for i = 1, string.len(str) do
            local new_str = string.sub(str, i, i)
            if (string.byte(new_str) >= 48 and string.byte(new_str) <= 57)
                    or (string.byte(new_str) >= 65 and string.byte(new_str) <= 90)
                    or (string.byte(new_str) >= 97 and string.byte(new_str) <= 122) then
                table.insert(str_tb, new_str)
            else
                return nil
            end
        end
        return str_tb
    else
        return nil
    end
end

function isnull(val)
    if val == nil then return true end
    if type(val) == 'string' then return #val == 0 end
    if type(val) == 'table' then return _.size(val) == 0 end
    if type(val) == 'userdata' then return true end
    return false
end

function formatarry(arry)
    local arrys = {}
    if not isnull(arry) then
        local temp = split(arry, ',')
        for k, v in pairs(temp) do
            arrys[k] = v
        end
    end
    return arrys
end

function urlencode(str)
    if (str) then
        str = string.gsub(str, "\n", "\r\n")
        str = string.gsub(str, "([^%w %-%_%.%~])",
            --str = string.gsub (str, "([^%w %-%_%.%!%~%*%'%(%,%)])",
            function(c) return string.format("%%%02X", string.byte(c)) end)
        str = string.gsub(str, " ", "+")
    end
    return str
end

function urldecode(str)
    str = string.gsub(str, "+", " ")
    str = string.gsub(str, "%%(%x%x)",
        function(h) return string.char(tonumber(h, 16)) end)
    str = string.gsub(str, "\r\n", "\n")
    return str
end
	
