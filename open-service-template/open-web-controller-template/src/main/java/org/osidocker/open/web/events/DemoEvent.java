package org.osidocker.open.web.events;

import org.springframework.context.ApplicationEvent;

public class DemoEvent extends ApplicationEvent {
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public DemoEvent(Object source,String msg) {
		super(source);
		this.msg = msg;
	}

}
