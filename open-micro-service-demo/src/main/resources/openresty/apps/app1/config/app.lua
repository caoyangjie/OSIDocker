--[[
应用配置文件
--]]
log = {
  file_name = "/data/apps/app1/logs/api-music/api.log",
  date_pattern = "%Y-%m-%d",
  level = "INFO"
}
mysql = {
  host = "219.234.131.42",
  port = "3306",
  user = "root",
  password = "lajin2015",
  database = "lajin_admin",
  timeout = 30000,
  pool_size = 1000
}
redis = {
  host = "219.234.131.42",
  port = "6379",
  auth = "lajin@2015",
  timeout = 30000,
  pool_size = 1000
}

checkcode = {
  timeout = 180,
  retime = 60 
}

httproot = {
	address = 'http://127.0.0.1:8004'
}

--[[
调用内网接口发送消息，这里配置消息业务id字段值，对应 com.lajin.admin.core.constant.MsgConstants中EVENT_ID_XXX值
--]]
msg = {
  --用户关注
  biz_id_concern = 4,
  biz_aid_apply = 5
}
