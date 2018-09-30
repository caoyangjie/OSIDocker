#!      /usr/bin/env lua
--[[

--]]
module(..., package.seeall)

function test1(req, res)
    local a = "123"
    local b = { 1, 2, 3 }
    local c = { i = 1, j = 2, k = 3 }
    test2(a, b, c, req, res)
    res:writeln(cjson.encode(a))
    res:writeln(cjson.encode(b))
    res:writeln(cjson.encode(c))
end

function test2(a, b, c)
    a = "456"
    b[2] = 7            -- 修改的是老的table
    b = { 4, 5, 6 }     -- 指向了新的地址
    b[2] = 8            -- 修改的是新的table
    c.j = 9
    c = { i = 4, j = 5, k = 6 }
    c.j = 10
end

