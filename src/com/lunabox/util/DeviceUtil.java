package com.lunabox.util;

import java.io.File;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


public class DeviceUtil {
	public static int getNetwork(Context paramContext) {
		ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
				.getSystemService("connectivity");
		
		if ((localConnectivityManager != null)
				&& (localConnectivityManager.getActiveNetworkInfo() != null)) {
			if (localConnectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
				return 1;
			}
		}

		return 2;
	}
	
	public static String getLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
	public static String getCountry() {
		return Locale.getDefault().getCountry();
	}
	
	public static String getIMEI(Context context) {
		String imei = null;
		
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		imei = mTelephonyMgr.getDeviceId();
		
		LogWriter.print("DataProvider.getIMEI imei:" + imei);
		if (TextUtils.isEmpty(imei) == false) {
			
			return imei.toLowerCase();
		} else {
			return "123456789012345";
		}
	}
	
	public static String getMac(Context context) {
		String str1 = ((WifiManager) context.getSystemService("wifi"))
				.getConnectionInfo().getMacAddress();
		LogWriter.print("DataProvider.getMac mac:" + str1);
		
		if (TextUtils.isEmpty(str1)) {
			return "";
		}
		return str1.replaceAll(":", "");
	}
	
	public static String getPkgName(Context context) {
		LogWriter.print("DataProvider.getPkgName");
		if (context != null) {
			return context.getPackageName();
		} else {
			return "";
		}
	}
	
	public static String getModel() {
		LogWriter.print("DataProvider.getModel MODEL:" + Build.MODEL);
		return Build.MODEL;
	}
	
	/**
	 * 如：4.0.4
	 * @param context
	 * @return
	 */
	public static String getVersion() {
		LogWriter.print("DataProvider.getVersion version:" + Build.VERSION.RELEASE);
		return Build.VERSION.RELEASE;
	}
	
	public static String getAndroidId(Context context) {
		if (context != null) {
			return System.getString(context.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID);
		}

		return null;
	}
	
	/**
	 * 获取手机号
	 * @param context
	 * @return
	 */
	public static String getMobileNum(Context context) {
		if (context != null) {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String num = telephonyManager.getLine1Number();

			return num;
		}
		return null;
	}
}
