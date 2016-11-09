package org.osidocker.zbus.service.entity;

import java.io.Serializable;

public class TransformData implements Serializable {
	private String head;
	private String body;
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
