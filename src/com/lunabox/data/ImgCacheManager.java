package com.lunabox.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import com.lunabox.util.FileUtil;
import com.lunabox.util.GlobalsUtil;
import com.lunabox.util.LogWriter;
import com.lunabox.util.MD5Util;

public class ImgCacheManager {
	private static final int IMG_THREAD_CNT = 6;
	private static final int IMG_QUEUE_LEN = 20;

	private ThreadPoolExecutor mPoolExecutor;
	private HashMap<String, SoftReference<Drawable>> imageCache;
	private Handler mMainThreadHandler;

	private static ImgCacheManager self;

	public static ImgCacheManager getInstance() {
		if (self == null) {
			// 双重检查锁
			synchronized (ImgCacheManager.class) {
				if (self == null) {
					self = new ImgCacheManager();
				}
			}

		}

		return self;
	}

	/**
	 * 创建一个异步图片加载器，默认最大6个工作线程，最大等待队列20
	 */
	private ImgCacheManager() {
		this(IMG_THREAD_CNT, IMG_QUEUE_LEN);
	}

	/**
	 * 创建一个异步图片加载器，当等待下载的图片超过设置的最大等待数量之后，会从等待队列中放弃一个最早加入队列的任务
	 * 
	 * @param maxPoolSize
	 *            最大工作线程数
	 * @param queueSize
	 *            最大等待数
	 */
	private ImgCacheManager(int maxPoolSize, int queueSize) {
		this(2, maxPoolSize, 3, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(queueSize),
				new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	/**
	 * 自定义线程池的加载器,请参考:{@link ThreadPoolExecutor}
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param unit
	 * @param workQueue
	 * @param handler
	 */
	private ImgCacheManager(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
		mPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, unit, workQueue, handler);
		mMainThreadHandler = new Handler(Looper.getMainLooper());
		
		checkImgCache();
	}

	public void loadDrawable(final String imageUrl, final boolean needSaveFile,
			final ImageCallback imageCallback) {
		// 优先内存缓存
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				LogWriter.print("load image from cache url:" + imageUrl);
				imageCallback.onLoaded(drawable);
				return;
			}
		}

		UrlInfo urlInfo = new UrlInfo(imageUrl, needSaveFile);
		LoadImageTask task = new LoadImageTask(urlInfo, this,
				mMainThreadHandler, imageCallback);
		mPoolExecutor.execute(task);
	}

	/**
	 * 异步加载一张图片
	 * 
	 * @param imageUrl
	 * @param imageCallback
	 */
	public void loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		loadDrawable(imageUrl, false, imageCallback);
	}

	/**
	 * 停止线程池运行，停止之后，将不能在继续调用 {@link #loadDrawable(String, ImageCallback)}
	 */
	public void destory() {
		mPoolExecutor.shutdown();
		imageCache.clear();
	}

	private void cache(UrlInfo urlInfo, Drawable drawable) {
		// 内存缓存
		imageCache.put(urlInfo.url, new SoftReference<Drawable>(drawable));
	}
	
	/**
	 * 检查img缓存文件夹中的文件数是否>200，如果>200则删除前50个
	 */
	private void checkImgCache() {
		File directory = new File(FileUtil.getImgCacheDir());
		File[] files = directory.listFiles();
		if(files == null)
			return;
		ArrayList<File> fileArr = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			fileArr.add(files[i]);
		}
		Collections.sort(fileArr, new FileComparator());

		// del
		if (fileArr.size() > 200) {
			final int delSize = 50;
			for (int j=0; j<delSize; j++) {
				File f = fileArr.get(j);
				LogWriter.print("checkImgCache del file:" + f.getName() + " time:"
						+ f.lastModified() + " size:" + f.length());
				f.delete();
			}
		}
	}

	/**
	 * 比较两个文件哪个更旧（更近修改时间更往前），旧的排在前面
	 * 
	 * @author laian.wang
	 */
	public class FileComparator implements Comparator<File> {

		@Override
		public int compare(File lhs, File rhs) {
			if (lhs != null && rhs != null) {
				return (int) (lhs.lastModified() - rhs.lastModified());
			}
			return 0;
		}

	}


	private static final class UrlInfo {
		public String url;
		public boolean needSaveFile;

		public UrlInfo(String url, boolean needSaveFile) {
			this.url = url;
			this.needSaveFile = needSaveFile;
		}
	}

	/**
	 * 下载任务
	 * 
	 */
	private static final class LoadImageTask implements Runnable {

		private Handler mHandler;
		private ImageCallback mCallback;
		private ImgCacheManager mLoader;
		private UrlInfo uInfo;

		/**
		 * @param imgPath
		 *            要下载的图片地址
		 * @param loader
		 *            图片加载器
		 * @param handler
		 *            主线程Handler
		 * @param imageCallback
		 *            图片加载回调
		 */
		public LoadImageTask(UrlInfo uInfo, ImgCacheManager loader,
				Handler handler, ImageCallback imageCallback) {
			LogWriter.print("start a task for load imageUrl:" + uInfo.url);
			this.mHandler = handler;
			this.uInfo = uInfo;
			this.mLoader = loader;
			this.mCallback = imageCallback;
		}

		@Override
		public void run() {
			URL url;
			InputStream is = null;

			// 优先文件缓存
			if (uInfo.needSaveFile) {
				is = getImgStreamFromFile(uInfo.url);
			}

			if (is == null) {
				try {
					url = new URL(uInfo.url);
					URLConnection conn = url.openConnection();
					conn.connect();
					is = conn.getInputStream();

					// save file
					if (uInfo.needSaveFile) {
						saveImgStreamFile(uInfo.url, is);
					}
				} catch (final Exception e) {
					LogWriter.print(e.getMessage() + "", e);
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							LogWriter.print("load image failed url:"
									+ uInfo.url);
							mCallback.onError(e);
						}
					});
				}
			}

			if (is != null) {
				// 从文件加载
				final Drawable drawable = Drawable
						.createFromPath(getImgPath(uInfo.url));
				mLoader.cache(uInfo, drawable);
				if (drawable != null) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LogWriter.print("load image success url:"
									+ uInfo.url);
							mCallback.onLoaded(drawable);
						}
					});
				} else {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LogWriter.print("load image failed url:"
									+ uInfo.url);
							mCallback.onError(new Exception("Image load error!"));
						}
					});
				}
			}
		}

		private InputStream getImgStreamFromFile(String url) {
			InputStream is = null;
			String path = getImgPath(url);
			if (FileUtil.isExistFile(path)) {
				File file = new File(path);
				try {
					is = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					LogWriter.print("Stack callback trace: "
							+ GlobalsUtil.getStackTrace(e));
				}
			}
			return is;
		}

		private void saveImgStreamFile(String url, InputStream is) {
			if (url != null && is != null) {
				BufferedInputStream bis = new BufferedInputStream(is);
				String imgPath = getImgPath(url);

				// del
				File file = new File(imgPath);
				if (file.exists()) {
					file.delete();
				}

				try {
					BufferedOutputStream bos = new BufferedOutputStream(
							new FileOutputStream(file));
					int len = 0;
					byte[] buffer = new byte[10240];
					while ((len = bis.read(buffer)) != -1) {
						bos.write(buffer, 0, len);
					}
					bos.close();
					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private String getImgPath(String imgUrl) {
			return FileUtil.getImgCacheDir() + MD5Util.toMD5(imgUrl);
		}

	}

	/**
	 * 回调接口,在主线程中运行
	 * 
	 */
	public static interface ImageCallback {
		/**
		 * 加载成功
		 * 
		 * @param imageDrawable
		 *            下载下来的图片
		 */
		public void onLoaded(Drawable drawable);

		/**
		 * 加载失败
		 * 
		 * @param e
		 *            异常
		 */
		public void onError(Exception e);
	}
}