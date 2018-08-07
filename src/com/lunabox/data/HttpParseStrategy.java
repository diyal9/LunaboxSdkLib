package com.lunabox.data;

/**
 * 定义数据包的解析策略
 * @author AndyWang
 *
 */
public class HttpParseStrategy {
	/**
	 * uuid登录
	 */
	public static final int UUID_LOGIN = 0;
	
	/**
	 * 账号登录
	 */
	public static final int ACC_LOGIN = 1;
	
	/**
	 * 当前奖励详情
	 */
	public static final int CUR_REWARD_DETAIL = 2;
	
	/**
	 * 奖励列表信息
	 */
	public static final int REWARD_LIST = 3;
	
}
