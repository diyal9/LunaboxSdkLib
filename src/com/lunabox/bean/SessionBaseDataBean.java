package com.lunabox.bean;

public class SessionBaseDataBean {
	public String desKey;
	public String rsaPublicKey;

	public String sessionId;

	public C2SDeviceBaseBean appInfo;

	public String toString() {
		return "desKey:" + desKey + " rsaPublicKey:" + rsaPublicKey
				+ " sessionId:" + sessionId + " appInfo:" + appInfo;
	}
}
