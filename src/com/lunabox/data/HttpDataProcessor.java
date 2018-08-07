package com.lunabox.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.lunabox.bean.C2SBaseBean;
import com.lunabox.bean.HttpResponsePacketBean;
import com.lunabox.bean.HttpResponseHeadBean;
import com.lunabox.bean.PostDataBean;
import com.lunabox.util.Consts;
import com.lunabox.util.LogWriter;

public class HttpDataProcessor {
	/**
	 * 编码生成PostDataBean
	 * 
	 * @param srcBean
	 * @param contentType
	 * @param parseType
	 * @param sessionId
	 * @return
	 */
	private static PostDataBean encode(C2SBaseBean srcBean, byte contentType,
			int parseType, String sessionId) {
		PostDataBean postBean = null;

		if (srcBean != null) {
			postBean = new PostDataBean();
			postBean.srcBean = srcBean;
			postBean.content = HttpContentStrategy.C2SEncode(srcBean,
					contentType);
			postBean.contentType = contentType;
			postBean.parseType = parseType;
			postBean.sessionId = sessionId;
		}

		return postBean;
	}

	/**
	 * 将C2SBaseBean转换成完整的http post数据内容
	 * 
	 * @param srcBean
	 * @param contentType
	 * @param parseType
	 * @param sessionId
	 * @return
	 */
	public static byte[] convertHttpEntityBytes(C2SBaseBean srcBean,
			byte contentType, int parseType, String sessionId) {
		PostDataBean bean = encode(srcBean, contentType, parseType, sessionId);
		return addPacketHead(bean.content, contentType, parseType, sessionId);
	}

	/**
	 * 为数据添加头部信息 包头信息为定长64字节[ short: 定值0xb byte: 是否有加密处理（0：明文，2：加密） short:
	 * 数据码(用来上层分类解析) int: 请求数据内容的长度 32 byte:
	 * sessionid，默认值为：00000000000000000000000000000000 23 byte: 扩展字段 ]
	 * 
	 * @param content
	 *            内容
	 * @param contentType
	 *            是否有加密处理（0：明文，2：加密）
	 * @param parseType
	 *            数据码(用来上层分类解析)
	 * @param sessionId
	 * @return
	 */
	private static byte[] addPacketHead(byte[] content, byte contentType,
			int parseType, String sessionId) {
		byte[] ret = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		try {
			dos.writeShort(Consts.SDK_VER);
			dos.writeByte(contentType);
			dos.writeShort(parseType);
			dos.writeInt(content.length);
			if (sessionId == null) {
				sessionId = "00000000000000000000000000000000";
			}
			dos.write(sessionId.getBytes("utf-8"));
			dos.write(new byte[23]);

			// write content
			dos.write(content);

			dos.flush();

			ret = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	/**
	 * 解析服务器响应数据包信息
	 * 
	 * @param recvData
	 * @return HttpRecvDataBean对象
	 */
	public static HttpResponsePacketBean parsePacket(byte[] recvData) {
		LogWriter.print("HttpDataProcessor parseRecvContent resDataLen:"
				+ recvData.length);
		HttpResponsePacketBean recvContent = new HttpResponsePacketBean();
		HttpResponseHeadBean headInfo = new HttpResponseHeadBean();

		ByteArrayInputStream bais = new ByteArrayInputStream(recvData);
		DataInputStream dis = new DataInputStream(bais);
		try {
			headInfo.contentType = dis.readByte();
			headInfo.parseType = dis.readShort();
			headInfo.dataLen = dis.readInt();
			dis.read(headInfo.unused);

			recvContent.headInfo = headInfo;

			int restLen = dis.available();
			byte[] restData = new byte[restLen];
			dis.read(restData);

			// 解码
			recvContent.content = HttpContentStrategy.S2CDecode(restData,
					headInfo.contentType);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LogWriter.print("HttpDataProcessor parseRecvContent retRecvDataBean:"
				+ recvContent);
		return recvContent;
	}

}
