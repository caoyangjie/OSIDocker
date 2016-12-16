package org.osidocker.open.web.converter;

public class DemoConverterVO {
	public String content;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public DemoConverterVO(String content){
		this.content = content;
	}
	
	public String toString(){
		return "hello:"+content;
	}
}
