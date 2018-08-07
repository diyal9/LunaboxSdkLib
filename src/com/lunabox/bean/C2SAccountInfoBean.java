package com.lunabox.bean;

import com.google.gson.Gson;

/**
 * 定义账号登录的数据结构
 * @author AndyWang
 *
 */
public class C2SAccountInfoBean extends C2SCurAppInfoBean {
	public String account;
	public String pswd;

	public static C2SAccountInfoBean create(C2SCurAppInfoBean baseBean,
			String account, String pswd) {
		Gson gson = new Gson();
		C2SAccountInfoBean bean = gson.fromJson(baseBean.toJson(),
				C2SAccountInfoBean.class);

		bean.account = account;
		bean.pswd = pswd;

		return bean;
	}
}
