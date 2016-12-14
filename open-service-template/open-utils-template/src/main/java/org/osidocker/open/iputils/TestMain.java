package org.osidocker.open.iputils;

import java.io.IOException;

public class TestMain {
	public static void main(String[] args) throws IOException {
		String country = IPSeeker.I.getAddress("222.246.11.219");
		System.out.println(country);
	}
}
