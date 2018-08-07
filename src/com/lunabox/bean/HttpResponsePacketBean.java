package com.lunabox.bean;

public class HttpResponsePacketBean {
	public HttpResponseHeadBean headInfo;
	public byte[] content;	// 明文信息
	
	public String toString() {
		String s = content == null ? null : new String(content);
		return "headInfo:" + headInfo + " content:" + s;
	}
}
