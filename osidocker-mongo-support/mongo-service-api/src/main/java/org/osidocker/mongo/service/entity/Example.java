package org.osidocker.mongo.service.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Example implements Serializable{
	private String title;
	private String content;
	private Date time;
	
	public Example(String title,String content,Date time){
		this.title = title;
		this.content = content;
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public String toString(){
		return "title->"+title+",content->"+content+",time->"+time.getTime();
	}
}
