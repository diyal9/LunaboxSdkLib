package com.lunabox.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GzipUtil {
	public static byte[] compress(byte[] data) throws IOException {
		byte[] b = null;
		if (data != null) {
			LogWriter.print("GzipUtil compress resByteCnt:" + data.length
					+ " resStr:" + new String(data));

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();

			LogWriter.print("GzipUtil compress destByteCnt:" + b.length);
		}
		return b;
	}

	/***
	 * 解压GZip
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] decompress(byte[] data) throws IOException {
		byte[] b = null;
		if (data != null) {
			LogWriter.print("GzipUtil decompress resByteCnt:" + data.length);

			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();

			LogWriter.print("GzipUtil decompress destByteCnt:" + b.length
					+ " destStr:" + new String(b));
		}
		return b;
	}
}
