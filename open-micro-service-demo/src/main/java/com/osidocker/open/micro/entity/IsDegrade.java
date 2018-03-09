package com.osidocker.open.micro.entity;

public class IsDegrade {

    private volatile static boolean degrade = false;

	public static boolean isDegrade() {
		return degrade;
	}

	public static void setDegrade(boolean degrade) {
		IsDegrade.degrade = degrade;
	}
	
}
