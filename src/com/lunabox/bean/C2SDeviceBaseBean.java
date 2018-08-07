package com.lunabox.bean;

import android.content.Context;

import com.google.gson.Gson;
import com.lunabox.data.LocalPublicData;
import com.lunabox.util.DeviceUtil;

/**
 * android设备基础信息，后续可能要扩展
 * @author AndyWang
 *
 */
public class C2SDeviceBaseBean extends C2SBaseBean{
	public String uuid;
	public String imei;
	public String mac;
	public String androidid;
	
	public static void setBaseInfo(C2SDeviceBaseBean bean, Context context) {
		if (bean != null) {
			bean.uuid = LocalPublicData.getUUID(context);
			bean.mac = DeviceUtil.getMac(context);
			bean.imei = DeviceUtil.getIMEI(context);
			bean.androidid = DeviceUtil.getAndroidId(context);
		}
	}
	
	public static C2SDeviceBaseBean create(Context context) {
		C2SDeviceBaseBean bean = new C2SDeviceBaseBean();
		setBaseInfo(bean, context);
		return bean;
	}
}
