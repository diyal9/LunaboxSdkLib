package com.lunabox.data;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

import com.lunabox.bean.C2SAccountInfoBean;
import com.lunabox.util.FileUtil;

/**
 * 本地公共数据操作工具类
 * 
 * @author AndyWang
 * 
 */
public class LocalPublicData {
	/**
	 * 读取本地数据，获得已注册的账号信息
	 * 
	 * @param context
	 * @return 账号信息或者为null
	 */
	public static C2SAccountInfoBean getLastAccount(Context context) {
		C2SAccountInfoBean acc = null;

		String accFile = FileUtil.getAccFilePath();
		ArrayList<String> contentArr = FileUtil.readTextContents(accFile);
		if (contentArr != null && contentArr.size() == 2) {
			acc = new C2SAccountInfoBean();
			acc.account = contentArr.get(0);
			acc.pswd = contentArr.get(1);
			
			contentArr.clear();
			contentArr = null;
		}

		return acc;
	}
	
	public static void saveLastAccount(C2SAccountInfoBean acc) {
		if (acc != null) {
			String s = acc.account + "\n" + acc.pswd;
			
			String accFile = FileUtil.getAccFilePath();
			
			FileUtil.writeToFile(accFile, s.getBytes(), false);
		}
	}

	private static String uuid = null;

	public synchronized static String getUUID(Context context) {

		if (uuid == null) {
			String uPath = FileUtil.getUuidFilePath();
			File cfgFile = new File(uPath);
			try {
				if (!cfgFile.exists()) {
					// 生成新的
					uuid = UUID.randomUUID().toString();

					FileUtil.writeToFile(uPath, uuid.getBytes(), false);
				} else {
					uuid = FileUtil.getFileContent(uPath);

					if (uuid == null || uuid.length() == 0) {
						// 生成新的
						uuid = UUID.randomUUID().toString();

						FileUtil.writeToFile(uPath, uuid.getBytes(), false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return uuid;
	}
}
