package com.lunabox.sdk.session;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lunabox.bean.C2SAccountInfoBean;
import com.lunabox.bean.C2SBaseBean;
import com.lunabox.bean.C2SCurAppInfoBean;
import com.lunabox.bean.C2SDeviceBaseBean;
import com.lunabox.bean.HttpResponseHeadBean;
import com.lunabox.bean.HttpResponsePacketBean;
import com.lunabox.bean.SessionBaseDataBean;
import com.lunabox.data.HttpContentStrategy;
import com.lunabox.data.HttpDataProcessor;
import com.lunabox.data.HttpParseStrategy;
import com.lunabox.data.LocalPublicData;
import com.lunabox.util.Consts;
import com.lunabox.util.LogWriter;
import com.lunabox.util.http.AsyncHttpClient;
import com.lunabox.util.http.BinaryHttpResponseHandler;

/**
 * ClientSession维护一个模拟客户端，控制整个模拟过程的进程，可以进行客户端的各种请求操作和返回数据的解析。</br>
 * 
 * @author AndyWang
 * 
 */
public class ClientSession {
	private Context context;

	private AsyncHttpClient asyncHttpClient;
	private Map<Integer, SessionRequest> requests;

	private SessionBaseDataBean sessionInfo;

	private int maxReqId;

	// private SessionReqListener sessionReqListener;

	public ClientSession(Context context, int appId, String appKey) {
		this.context = context;

		init(appId, appKey);
	}

	// @Deprecated
	// public ClientSession(Context context, SessionBaseDataBean
	// sessionBaseInfo) {
	// this.context = context;
	// this.sessionInfo = sessionBaseInfo;
	//
	// init();
	// }

	//
	// @Deprecated
	// public ClientSession(Context context, int clientSessionId, String desKey,
	// String rsaPublicKey, DeviceInfoBean deviceInfo) {
	// this.context = context;
	//
	// this.sessionBaseData = new SessionBaseDataBean();
	// this.sessionBaseData.clientSessionId = clientSessionId;
	// this.sessionBaseData.desKey = desKey;
	// this.sessionBaseData.rsaPublicKey = rsaPublicKey;
	// this.sessionBaseData.deviceInfo = deviceInfo;
	//
	// init();
	// }

	private void init() {
		this.asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.setTimeout(15000);
		requests = new HashMap<Integer, SessionRequest>();

		sessionInfo = new SessionBaseDataBean();
		sessionInfo.appInfo = C2SDeviceBaseBean.create(context);
	}

	private void init(int appId, String appKey) {
		init();
		sessionInfo.appInfo = C2SCurAppInfoBean.create(context,
				sessionInfo.appInfo, appId, appKey);
	}

	public AsyncHttpClient getHttpClient() {
		return asyncHttpClient;
	}

	private synchronized int getCurReqId() {
		return maxReqId++;
	}

	private synchronized void addReq(SessionRequest req) {
		req.reqId = getCurReqId();
		requests.put(req.reqId, req);
	}

	private void removeReq(SessionRequest req) {
		requests.remove(req);
	}

	private void removeReq(int reqId) {
		requests.remove(requests.get(new Integer(reqId)));
	}

	/**
	 * session启动：获取本地的账号信息进行登录
	 */
	public void start() {
		// 获取本地账号信息
		C2SAccountInfoBean lastAcc = LocalPublicData.getLastAccount(context);

		// login
		if (lastAcc == null) {
			// 默认uuid登录
			uuidLogin();
		} else {
			// 账号密码登录
			login(lastAcc.account, lastAcc.pswd);
		}
	}

	public void destory() {
		asyncHttpClient.cancelRequests(context, true);
	}

	/**
	 * 使用uuid登录
	 */
	private void uuidLogin() {
		byte contentType = HttpContentStrategy.NONE;
		int parseType = HttpParseStrategy.UUID_LOGIN;
		postC2SBean(Consts.URL, sessionInfo.appInfo, contentType, parseType,
				sessionInfo.sessionId, null);
	}

	/**
	 * TODO 账号密码登录
	 * 
	 * @param account
	 * @param pswd
	 */
	private void login(String account, String pswd) {

	}

	/**
	 * post C2SBaseBean对象到url
	 * 
	 * @param url
	 *            服务器url
	 * @param bean
	 *            需要post的原数据对象
	 * @param contentType
	 *            编码策略
	 * @param parseType
	 *            解析策略
	 * @param sessionId
	 *            session id
	 * @param listener
	 */
	private void postC2SBean(String url, C2SBaseBean bean, byte contentType,
			int parseType, String sessionId, SessionReqListener listener) {
		byte[] postContent = HttpDataProcessor.convertHttpEntityBytes(bean,
				contentType, parseType, sessionId);

		// async http host
		SessionRequest req = new SessionRequest(url, listener, null);
		addReq(req);

		LogWriter.print("ClientSession postC2SBean contentLen:" + postContent.length);
		HttpEntity entity = new ByteArrayEntity(postContent);
		asyncHttpClient.post(context, url, entity, null,
				new SessionBinaryHttpResHandler(req));
	}

	/**
	 * 处理解析好的数据包
	 * 
	 * @param packet
	 */
	private void onPacket(SessionRequest req, HttpResponsePacketBean packet) {
		if (packet != null) {
			HttpResponseHeadBean head = packet.headInfo;

			// TODO 内部处理
			try {
				String contentStr = new String(packet.content);
				JSONObject jobj = new JSONObject(contentStr);

				switch (head.parseType) {
				case HttpParseStrategy.UUID_LOGIN: {

				}
					break;
				case HttpParseStrategy.ACC_LOGIN: {

				}
					break;
				case HttpParseStrategy.CUR_REWARD_DETAIL: {

				}
					break;
				case HttpParseStrategy.REWARD_LIST: {

				}
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// 交给上层代理处理
			if (req.listener != null) {
				req.listener.onSuccess(packet);
			}
		}
	}

	class SessionBinaryHttpResHandler extends BinaryHttpResponseHandler {
		private SessionRequest req;

		private HttpResponsePacketBean recvData;

		public SessionBinaryHttpResHandler(SessionRequest req) {
			this.req = req;
		}

		@Override
		public void onSuccess(byte[] binaryData) {
			super.onSuccess(binaryData);

			// 解析数据返回HttpRecvDataBean
			recvData = HttpDataProcessor.parsePacket(binaryData);
			onPacket(req, recvData);
		}

		@Override
		public void onFailure(Throwable error) {
			super.onFailure(error);
			if (req.listener != null) {
				req.listener.onFailure(error);
			}
		}

		@Override
		public void onFinish() {
			super.onFinish();

			removeReq(req);
		}
	}
}
