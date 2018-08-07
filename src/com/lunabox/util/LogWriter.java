package com.lunabox.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class LogWriter {

	private static LogWriter mLogWriter;

	private static String mPath;

	private static Writer mWriter = null;

	// private LogWriter(String file_path) {
	// this.mPath = file_path;
	// this.mWriter = null;
	// }

	public static void open() {
		if (mWriter == null) {
			try {
				String file_path = Environment.getExternalStorageDirectory()
						+ File.separator + "lunabox.log";

				mWriter = new BufferedWriter(new FileWriter(file_path, true),
						2048);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//
	// public static LogWriter open(String file_path) throws IOException {
	// if (mLogWriter == null) {
	// mLogWriter = new LogWriter(file_path);
	// }
	// File mFile = new File(mPath);
	// mWriter = new BufferedWriter(new FileWriter(mPath), 2048);
	// df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]: ");
	//
	// return mLogWriter;
	// }

	public static void close() {
		try {
			mWriter.close();
			mWriter = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void print(String log) {
		System.out.println(log);
		try {
			if (mWriter == null)
				open();
			SimpleDateFormat df = new SimpleDateFormat("[yy-MM-dd HH:mm:ss] ");
			mWriter.write(df.format(new Date()));
			mWriter.write(log);
			mWriter.write("\n");
			mWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void wla(String log) {
		try {
			if (mWriter == null)
				open();

			SimpleDateFormat df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss] ");
			mWriter.write(df.format(new Date()));
			mWriter.write("[wla] ");
			mWriter.write(log);
			mWriter.write("\n");
			mWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void print(Class cls, String log) {
		try {
			// SimpleDateFormat df = new
			// SimpleDateFormat("[yy-MM-dd hh:mm:ss] ");
			// mWriter.write(df.format(new Date()));
			mWriter.write(cls.getSimpleName() + " ");
			mWriter.write(log);
			mWriter.write("\n");
			mWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void log(String msg) {
		try {
			print(msg);
		} catch (Exception e) {
			Log.d("log_file", e.getMessage());
		}
	}

	public static void print(final String format, final Object... params) {
		print(String.format(format, params));
	}
	
	public static void mark() {
		print("mark here");
	}
}