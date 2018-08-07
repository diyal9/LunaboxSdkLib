package com.lunabox.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class DataWriter {
	private static DataOutputStream out;

	public static void open() {
		if (out == null) {
			try {
				String file_path = Environment.getExternalStorageDirectory()
						+ File.separator + "djData.dat";

				out = new DataOutputStream(
						new FileOutputStream(file_path));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void close() {
		if (out != null) {
			try {
				out.close();
				out = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void save(byte[] bytes) {
		try {
			if (out == null) {
				open();
			}
			
			out.writeBytes("========");
			SimpleDateFormat df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]");
			out.writeBytes(df.format(new Date()) + ",pos:" + new Throwable().getStackTrace()[1].toString() + "==");
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}