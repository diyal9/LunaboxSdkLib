package com.lunabox.bean;

/**
 * 定义服务器响应的数据头信息
 * 
 * @author AndyWang
 * 
 */
public class HttpResponseHeadBean {
	public byte contentType;
	public short parseType;
	public int dataLen;
	public byte[] unused = new byte[25];

	public String toString() {
		int i = 0;
		if (this.unused != null) {
			i = this.unused.length;
		}
		return "contentType:" + this.contentType
				+ " parseType:" + this.parseType + " dataLen:" + this.dataLen
				+ " unused.len:" + i;
	}
}