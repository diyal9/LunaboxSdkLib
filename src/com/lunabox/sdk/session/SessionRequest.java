package com.lunabox.sdk.session;



/**
 * 定义session中每个请求的相关信息
 * @author AndyWang
 *
 */
public class SessionRequest {
	public int reqId;
	public String url;
	public SessionReqListener listener;
	public Object obj;
	
	public SessionRequest(String url, SessionReqListener listener, Object o) {
		reqId = -1;
		
		this.url = url;
		this.listener = listener;
		this.obj = o;
	}
	
	public SessionRequest(int reqId, String url, SessionReqListener listener, Object o) {
		this.reqId = reqId;
		this.url = url;
		this.listener = listener;
		this.obj = o;
	}
}
