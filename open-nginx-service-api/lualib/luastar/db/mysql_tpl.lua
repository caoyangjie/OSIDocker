#! /usr/bin/env lua
--[===[

--]===]
local logger = luastar_log.getLog()
local util_file = require("luastar.util.file")
local util_str = require("luastar.util.str")

MysqlTpl = Class("luastar.db.MysqlTpl")

function MysqlTpl:init(config_file, mysql)
    self:set_config(config_file)
    self.mysql = mysql
end

function MysqlTpl:set_config(config_file)
    self.config_file = config_file
    if not self.config_file then
        logger:error("[MysqlTpl:set_config] illegal argument : config_file can't nil.")
        return
    end
    self.config = util_file.loadlua(ngx.var.APP_PATH .. self.config_file) or {}
end

function MysqlTpl:set_mysql(mysql)
    self.mysql = mysql
end

function MysqlTpl:getsql(id, data)
    if not self.config then
        logger:error("[MysqlTpl:getsql] config is nil.")
        return nil
    end
    local id_config = self.config[id]
    if not id_config then
        logger:error("[MysqlTpl:getsql] config is nil. id=%s", id)
        return nil
    end
    -- set tag value
    local sql, tag = id_config.sql, {}
    for word in string.gmatch(sql, "@{%w+}") do
        var = string.sub(word, 3, string.len(word) - 1) -- sub @{}
        table.insert(tag, var)
    end
    local value = nil
    for i, key in ipairs(tag) do
        value = ""
        if key == "set" then
            value = self:getsql_set(id_config.set, data)
        elseif key == "where" then
            value = self:getsql_where(id_config.where, data)
        end
        value = string.gsub(value, "%%", "%%%%")
        sql = string.gsub(sql, "@{" .. key .. "}", value)
    end
    -- set var value
    return self:getsql_value(sql, data)
end

function MysqlTpl:getsql_set(set, data)
    if not set then
        return nil
    end
    if not _.isArray(set) then
        return nil
    end
    local s, st = nil, {}
    for i, key in ipairs(set) do
        s = self:getsql_value(key, data, true)
        if s and s ~= "" then
            table.insert(st, s)
        end
    end
    return " set " .. table.concat(st, ",")
end

function MysqlTpl:getsql_where(where, data)
    if not where then
        return nil
    end
    if not _.isArray(where) then
        return nil
    end
    local w, wt = nil, {}
    for i, key in ipairs(where) do
        w = self:getsql_value(key, data, true)
        if w and w ~= "" then
            table.insert(wt, w)
        end
    end
    if #wt == 0 then
        return " where 1=1 "
    end
    local rs = util_str.trim(table.concat(wt, " \n"))
    if util_str.startsWith(rs, "and") then
        rs = string.sub(rs, 4, string.len(rs))
    elseif util_str.startsWith(rs, "or") then
        rs = string.sub(rs, 3, string.len(rs))
    end
    return " where " .. rs
end

function MysqlTpl:getsql_value(sql, data, nv)
    local var, var1, var2 = nil, {}, {}
    -- #{}
    for word in string.gmatch(sql, "#{%w+}") do
        var = string.sub(word, 3, string.len(word) - 1) -- sub #{}
        table.insert(var1, var)
    end
    -- ${}
    for word in string.gmatch(sql, "%${%w+}") do
        var = string.sub(word, 3, string.len(word) - 1) -- sub ${}
        table.insert(var2, var)
    end
    local value = nil
    for i, key in ipairs(var1) do
        value = "null"
        if data[key] then
            if _.isString(data[key]) then
                value = "'" .. string.gsub(data[key], "'", "''") .. "'"
            elseif _.isNumber(data[key]) then
                value = data[key]
            end
        end
        if nv and value == "null" then
            sql = ""
        else
            value = string.gsub(value, "%%", "%%%%")
            sql = string.gsub(sql, "#{" .. key .. "}", value)
        end
    end
    for i, key in ipairs(var2) do
        value = ""
        if data[key] then
            value = string.gsub(data[key], "'", "''")
        end
        if nv and value == "null" then
            sql = ""
        else
            value = string.gsub(value, "%%", "%%%%")
            sql = string.gsub(sql, "${" .. key .. "}", value)
        end
    end
    return sql
end

function MysqlTpl:exec_sql(id, data)
    if not self.mysql then
        logger:error("[MysqlTpl:exec_sql] mysql is nil.")
        return nil
    end
    local sql = self:getsql(id, data)
    if not sql then
        logger:error("[MysqlTpl:exec_sql] get sql error. id=%s", id)
        return nil
    end
    return self.mysql:query(sql)
end

function MysqlTpl:select_one(id, data)
    local res = self:exec_sql(id, data)
    if _.isArray(res) then
        return res[1]
    end
    return nil
end

function MysqlTpl:select_list(id, data)
    return self:exec_sql(id, data)
end

function MysqlTpl:insert(id, data)
    return self:exec_sql(id, data)
end

function MysqlTpl:update(id, data)
    return self:exec_sql(id, data)
end

function MysqlTpl:delete(id, data)
    return self:exec_sql(id, data)
end

return MysqlTpl