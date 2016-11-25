package org.osidocker.open.test;

import org.junit.Test;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AccountFreeze;
import com.alipay.api.domain.AccountRecord;

public class AliPayTest {
	
	protected final static String publicKey="MGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDpuOctv6Nydwq9KvktbosrjW93Mak59NuVzZSG6bJiGoP74cW2ADdUevA0UZ0MXz+W9h10HorwpGEw77xvlNQav9m1hPOIf9KW76VOskysjCYD4AhIMIx64QSt7NphFTDUEc6sI+JH+afbEv/dMBCsYc7767aXBHkDKKKPNeDHnwIDAQAB";
	protected final static String privateKey="MICXgIBAAKBgQDpuOctv6Nydwq9KvktbosrjW93Mak59NuVzZSG6bJiGoP74cW2ADdUevA0UZ0MXz+W9h10HorwpGEw77xvlNQav9m1hPOIf9KW76VOskysjCYD4AhIMIx64QSt7NphFTDUEc6sI+JH+afbEv/dMBCsYc7767aXBHkDKKKPNeDHnwIDAQABAoGBAIVV4y0KQyiuGQZKWdU9V1AFZC3PuwdBVXRkz/MXp5ioH9u2taBbBzW+3QI7/Shtvk1VIwB33DPk0z2jxxCxCz8QRzbZ9DMhRAfOo9ERE82lYtFqVU1F7pdzBe1UtagHKWHvQk3UXm3vcwDJ6Z7EVxPNwoOkLp5ktNnsTpTExYOhAkEA+TpxVQ7rHExXzDhsngv2RLenTZzU9CxLF1zNUR+4BBek9BRzy+Y2gfG03P5mG0fWAFYBdmQkyulBMG9YvhL5ewJBAPASmusZhZsE6TPVZkbxYFpvfiRvubYsVLJBsEbp+44k+yDgHkdpsEDKjVKAKNinei95BgZwJSGFB+JlN3xcty0CQDyD1Urq8WmQm+zpcm45x1VOJqBwyUB0lNMaZHwjMIJF33aolKlYv7lzoA/c8ZwEKFEykO5XSJvAYWTKVIjqgIsCQQC7Ybo7gIsxCS9AfvM6slJDpDxwEiiBEa72B6GgaCcptqGw8l2P3eVtXOvblh7LpEURYsQaKFRDCoOx6NDbJ77tAkEAoe3IYty/GS3xCZfjl4YoYlteDaQts+fLP/Rr7Achoz/apD8UC1BqySu9RgBHVouwaCpbPJ6mMkHzST6GgwI9Aw==";
	
	@Test
	public void testAlipay(){
//		功能：构造方法
//		输入：serverUrl 非空，请求服务器地址（调试：http://openapi.alipaydev.com/gateway.do 线上：https://openapi.alipay.com/gateway.do ）
//		      appId 非空，应用ID
//		      privateKey 非空，私钥
//		输出：调用客户端实例对象
		DefaultAlipayClient dac = new DefaultAlipayClient(
					"http://openapi.alipaydev.com/gateway.do", 
					"2016112503274934", 
					privateKey,"json");
//		dac.execute
		
		AccountFreeze af;//支付宝用户冻结明细信息
		AccountRecord ar;//
	}
}
