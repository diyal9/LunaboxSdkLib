package com.lunabox.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;
import android.os.StatFs;

public class FileUtil {

	private static final String LB_ROOT_PATH = ".lunabox/"; //
	private static final String PUB_DIR = "pub/";	// 公共数据目录
	private static final String UUID_FILE = "uInfo.dat";
	private static final String ACC_FILE = "aInfo.dat";
	private static final String IMG_CACHE_DIR = "imgCache/";

	public static boolean hasStorage() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static boolean canSave(byte[] byteData) {
		if (byteData.length < getAvailaleSize()) {
			return true;
		}
		return false;
	}

	public static int getAvailaleSize() {
		File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
		StatFs stat = new StatFs(path.getPath());
		int blockSize = stat.getBlockSize();
		int availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static String getUdiskPath() {
		return File.separator + "udisk";
	}

	// Sdcard
	public static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static boolean getSdcardIsReady() {
		String sdready = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(sdready)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdready)) {
			return true;
		}

		return false;
	}

	// root dir
	public static String getRootPath() {
		String fileSavePath = null;
		if (getSdcardIsReady()) {
			fileSavePath = getSdcardPath();
		} else {
			fileSavePath = getUdiskPath();
		}
		return fileSavePath + File.separator + LB_ROOT_PATH;
	}
	
	public static String getUuidFilePath() {
		return getRootPath() + FileUtil.PUB_DIR + FileUtil.UUID_FILE;
	}
	
	public static String getAccFilePath() {
		return getRootPath() + FileUtil.PUB_DIR + FileUtil.ACC_FILE;
	}
	
	public static String getPublicDir() {
		return getRootPath() + FileUtil.PUB_DIR;
	}

	public static String getImgCacheDir() {
		return getRootPath() + FileUtil.IMG_CACHE_DIR;
	}
	
	public static void writeToFile(String filePath, byte[] bytes, boolean append) {
		try {
			File file = new File(filePath);
			if (!file.isFile()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, append);// openFileOutput(filePath,MODE_PRIVATE);
			fos.write(bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> readTextContents(String filePath) {
		ArrayList<String> contentArr = new ArrayList<String>();
		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行
			while ((tempString = reader.readLine()) != null) {
				contentArr.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
		return contentArr;
	}

	public static String getFileContent(String filePath) {
		StringBuilder sb = new StringBuilder();

		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return sb.toString();
	}

	public static boolean isExistFile(String strFile) {
		File f = new File(strFile);
		return f.exists();
	}

	public static void mkdir(String path) {
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
	}

	public static void mkdirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void mkAllDirs() {
		mkdirs(getRootPath());
		mkdirs(getPublicDir());
		mkdirs(getImgCacheDir());
	}

	public static boolean delete(String filePath) {
		LogWriter.print("FileUtil delete filePath:" + filePath);
		File file = new File(filePath);
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}
}
