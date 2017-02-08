package org.osidocker.open.api.service;

public class TestString {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str1 = new StringBuffer("计算机").append("软件").toString();
		System.out.println(str1.intern()==str1);
		
		String str2 = new StringBuffer("j").append("ava").toString();
		System.out.println(str2.intern()==str2);
	}

}
