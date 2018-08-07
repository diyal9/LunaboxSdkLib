package com.lunabox.sdk.session;

import com.lunabox.bean.HttpResponsePacketBean;

public interface SessionReqListener {
	public void onSuccess(HttpResponsePacketBean recvData);
	public void onFailure(Throwable error);
}
