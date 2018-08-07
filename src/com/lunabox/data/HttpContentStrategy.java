package com.lunabox.data;

import java.io.UnsupportedEncodingException;

import com.lunabox.bean.C2SBaseBean;
import com.lunabox.bean.PostDataBean;

/**
 * 定义数据内容的各种处理策略（加密和编码），并进行相应编解码处理
 * 
 * @author AndyWang
 * 
 */
public class HttpContentStrategy {
	public static final byte NONE = 0;

	/**
	 * TODO 将数据根据contentType进行相应编码处理
	 * 
	 * @param srcBean
	 *            原数据
	 * @param contentType
	 *            数据编码处理方式
	 * @return
	 */
	public static byte[] C2SEncode(final C2SBaseBean srcBean, byte contentType) {
		byte[] content = null;

		switch (contentType) {
		case NONE: {
			try {
				content = srcBean.toJson().getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
			break;
		}

		return content;
	}

	/**
	 * TODO 根据contentType解码原数据内容，返回解码后的byte数组
	 * 
	 * @param contentType
	 *            数据类型
	 * @param content
	 *            原数据内容
	 * @return 解码后的数据内容
	 */
	public static byte[] S2CDecode(final byte[] content, byte contentType) {
		byte[] retContent = null;

		switch (contentType) {
		case NONE: {
			retContent = content;
		}
			break;
		}

		return retContent;
	}
}
