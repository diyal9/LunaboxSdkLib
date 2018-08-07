package com.lunabox.bean;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.lunabox.util.DeviceUtil;

/**
 * 应用相关信息
 * 
 * @author AndyWang
 */
public class C2SCurAppInfoBean extends C2SDeviceBaseBean {
	public int appid;
	public String appkey;
	@SerializedName("package")
	public String packageName;

	public static C2SCurAppInfoBean create(Context context,
			C2SDeviceBaseBean baseBean, int appid, String appkey) {
		Gson gson = new Gson();
		C2SCurAppInfoBean bean = gson.fromJson(baseBean.toJson(),
				C2SCurAppInfoBean.class);

		bean.appid = appid;
		bean.appkey = appkey;
		bean.packageName = DeviceUtil.getPkgName(context);

		return bean;
	}
}
