#! /usr/bin/env lua
--[===[
mysql操作库
insert res
{"insert_id":170,"server_status":2,"warning_count":0,"affected_rows":1}

update res
{"insert_id":0,"server_status":2,"warning_count":0,"affected_rows":1,"message":"(Rows matched: 1  Changed: 1  Warnings: 0"}

select res
note null value type is userdata
[{"AREA_NAME":"中国","AREA_STATUS":1,"AREA_CODE":"CN","ID":1,"AREA_LEVEL":1,"PARENT_AREA_CODE":null,"UPDATE_TIME":null,"CREATE_TIME":null},{"AREA_NAME":"北京市","AREA_STATUS":1,"AREA_CODE":"110000","ID":2,"AREA_LEVEL":2,"PARENT_AREA_CODE":"CN","UPDATE_TIME":null,"CREATE_TIME":null}]

--]===]
local logger = luastar_log.getLog()
local resty_mysql = require("resty.mysql")

Mysql = Class("luastar.db.Mysql")

function Mysql:init(datasource)
    self.datasource = _.defaults(datasource, {
        host = "127.0.0.1",
        port = "3306",
        database = "",
        user = "",
        password = "",
        timeout = 3000,
        max_idle_timeout = 60000,
        pool_size = 1000,
        charset = "UTF8"
    })
    logger:debug("[Mysql:init] datasource : " .. cjson.encode(self.datasource))
end

function Mysql:getConnect()
    local connect, err = resty_mysql:new()
    if not connect then
        logger:error("[Mysql:initConnect] failed to create mysql : " .. err)
        return nil
    end
    connect:set_timeout(self.datasource.timeout)
    local ok, err, errno, sqlstate = connect:connect(self.datasource)
    if not ok then
        logger:error("[Mysql:getConnect] failed to connect mysql : " .. err)
        return nil
    end
    -- set charset
    local res, err, errno, sqlstate = connect:query("SET NAMES " .. self.datasource.charset)
    if not res then
        logger:error("[Mysql:getConnect] set charset fail : " .. err)
    end
    return connect
end

function Mysql:query(sql, nrows)
    local connect = self:getConnect()
    if not connect then
        logger:warn("[Mysql:query] failed to get mysql connect.")
        return nil
    end
    -- exec sql
    local res, err, errno, sqlstate = connect:query(sql, nrows)
    self:close(connect)
    return res, err, errno, sqlstate
end

function Mysql:close(connect)
    if not connect then
        return
    end
    if self.datasource.pool_size <= 0 then
        connect:close()
        return
    end
    -- put it into the connection pool of size 100,
    -- with 10 seconds max idle timeout
    local ok, err = connect:set_keepalive(self.datasource.max_idle_timeout,
        self.datasource.pool_size)
    if not ok then
        logger:error("[Mysql:close] set keepalive failed : " .. err)
    else
        logger:debug("[Mysql:close] set keepalive ok.")
    end
end

return Mysql

