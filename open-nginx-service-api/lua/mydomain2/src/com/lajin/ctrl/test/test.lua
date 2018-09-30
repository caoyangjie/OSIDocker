#!/usr/bin/env lua
--[[

--]]
module(..., package.seeall)
local beanFactory = luastar_context.getBeanFactory()

function test1(req, response)
    local mysql_util = beanFactory:getBean("mysql")
    local mysql = mysql_util:getConnect()
    local res1, err1, errno1, sqlstate1 = mysql:query("START TRANSACTION;")
    response:writeln(cjson.encode({
        sql = "START TRANSACTION;",
        res = res1,
        err = err1,
        errno = errno1,
        sqlstate = sqlstate1
    }))
    local res2, err2, errno2, sqlstate2 = mysql:query("update COMMENT_TPL_1 set STATUS=2 where ID=1;")
    response:writeln(cjson.encode({
        sql = "update COMMENT_TPL_1 set STATUS=2 where ID=1;",
        res = res2,
        err = err2,
        errno = errno2,
        sqlstate = sqlstate2
    }))
    local res3, err3, errno3, sqlstate3 = mysql:query("update COMMENT_TPL_2 set STATUS=3 where ID=1;")
    response:writeln(cjson.encode({
        sql = "update COMMENT_TPL_2 set STATUS=3 where ID=1;",
        res = res3,
        err = err3,
        errno = errno3,
        sqlstate = sqlstate3
    }))
    if not _.isEmpty(res3) then
        local res4, err4, errno4, sqlstate4 = mysql:query("COMMIT;")
        response:writeln(cjson.encode({
            sql = "COMMIT;",
            res = res4,
            err = err4,
            errno = errno4,
            sqlstate = sqlstate4
        }))
    else
        local res5, err5, errno5, sqlstate5 = mysql:query("ROLLBACK;")
        response:writeln(cjson.encode({
            sql = "ROLLBACK;",
            res = res5,
            err = err5,
            errno = errno5,
            sqlstate = sqlstate5
        }))
    end
    mysql_util:close(mysql)
end
