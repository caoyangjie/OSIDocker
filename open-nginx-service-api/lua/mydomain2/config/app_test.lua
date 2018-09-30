--[[
应用配置文件
--]]
mysql = {
    host = "10.1.1.2",
    port = "3306",
    user = "root",
    password = "lajin2015",
    database = "cms_admin",
    timeout = 30000,
    pool_size = 1000
}
redis = {
    host = "10.1.1.2",
    port = "6379",
    auth = "lajin@2015",
    timeout = 30000,
    pool_size = 1000
}
smscode = {
    timeout = 600,
    resend = 60
}
apihost = {
    inner_url = "http://10.1.1.4:8004",
    sms_url = "http://yunpian.com/v1/sms/send.json"
}
wyapi = {
    search_url = "http://music.163.com/api/search/get",
    detail_url = "http://music.163.com/api/song/detail",
	lyric_url = "http://music.163.com/api/song/lyric?os=pc&lv=-1&kv=-1&tv=-1"
}
weixin = {
    appid = "wx69fce3756870343f",
    secret = "b7196f5349beddf446a217e9081845b9",
    access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token",
    check_token_url = "https://api.weixin.qq.com/sns/auth",
    refresh_token_url = "https://api.weixin.qq.com/sns/oauth2/refresh_token",
    userinfo_url = "https://api.weixin.qq.com/sns/userinfo",
    sdk_token_url = "https://api.weixin.qq.com/cgi-bin/token",
    sdk_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket"
}
default_value = {
    user_icon = "http://pic3.music.lajin.com/image/2015/11/03/915dfef4726a4f2b97282e00807b3e36.jpg"
}
comment_interval={
	{min = 0,max = 5},
	{min = 6,max = 10},
	{min = 11,max = 15}
	}
tplpicpx = {
	startimg = "@960h_540w",
    carousel = "@224h_640w",
	nav = "@224h_640w",
	headfavicon = "@100h_100w",
    thumbnail = "@320h_320w|320x240-5rc",
    recpic = "@640h_640w",
	share_pic = "@200h_200w",
	tplid_1 = { -- tplid_id
        pic1="@320h_320w"
	},
	tplid_2 = { -- tplid_id
		pic1="@1134h_640w",
		pic2="@1134h_640w",
		pic3="@1134h_640w",
		pic4="@1134h_640w",
		pic5="@1134h_640w",
		pic6="@1134h_640w",
		pic7="@1134h_640w",
		pic8="@1134h_640w"	
	},
	tplid_3 = { -- tplid_id
		pic1="@330h_460w",
		pic2="@330h_460w",
		pic3="@330h_460w",
		pic4="@330h_460w",
		pic5="@330h_460w",
		pic6="@330h_460w",
		pic7="@330h_460w",
		pic8="@330h_460w"	
	}
}