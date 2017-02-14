package org.osidocker.open.entity;

import java.io.Serializable;

public class ActiveMsgBaseEntity implements Serializable {
	
	public ActiveMsgBaseEntity(String data) {
		super();
		this.data = data;
	}

	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
