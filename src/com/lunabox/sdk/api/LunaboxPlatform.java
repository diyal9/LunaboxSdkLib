package com.lunabox.sdk.api;

import android.content.Context;

import com.lunabox.sdk.session.ClientSession;
import com.lunabox.sdk.session.SessionReqListener;
import com.lunabox.util.FileUtil;
import com.lunabox.util.LogWriter;

public class LunaboxPlatform {
	private static LunaboxPlatform self;

	private Context mContext;

	private int appId;
	private String appKey;

	// 当前应用的lunabox sdk session
	private ClientSession cSession;

	private LunaboxPlatform(Context context, int appId, String appKey) {
		mContext = context;
		this.appId = appId;
		this.appKey = appKey;

		init();
	}

	private void init() {
		FileUtil.mkAllDirs();

		// 启动session
		cSession = new ClientSession(mContext, appId, appKey);
		cSession.start();
	}

	private synchronized static LunaboxPlatform getInstance() {
		return self;
	}

	private synchronized static LunaboxPlatform getInstance(Context context,
			int appId, String appKey) {
		if (self == null) {
			self = new LunaboxPlatform(context, appId, appKey);
		}

		return self;
	}

	/**
	 * 初始化LunaBox SDK
	 * 
	 * @param context
	 * @param appId
	 * @param appKey
	 */
	public static LunaboxPlatform initialize(Context context, int appId,
			String appKey) {
		LogWriter.print("LunaboxPlatform initialize appId:" + appId
				+ " appKey:" + appKey);
		return getInstance(context, appId, appKey);
	}

	/**
	 * destory lunabox sdk
	 * 
	 * @param context
	 */
	public void destory(Context context) {
		LogWriter.print("LunaboxPlatform destory");

		if (getInstance() != null) {
			cSession.destory();
		}
	}

	/**
	 * 获取对应奖励点数的奖励信息
	 * 
	 * @param gameRewardVal
	 *            游戏中的奖励点数
	 * @param listener
	 *            请求监听器
	 */
	public void getRewardInfo(int gameRewardVal, SessionReqListener listener) {

	}

	/**
	 * 显示奖励对话框
	 * 
	 * @param context
	 * @param gameRewardVal
	 *            游戏中的奖励点数
	 * @param listener
	 *            请求监听器
	 */
	public void showRewardDialog(Context context, int gameRewardVal,
			SessionReqListener listener) {

	}

}
