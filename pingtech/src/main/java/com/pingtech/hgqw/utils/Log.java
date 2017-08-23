package com.pingtech.hgqw.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pingtech.hgqw.base.BaseApplication;

/** 自定义log处理类，保存到文件中 */
public class Log {
	/** 是否保存到文件 */
	public static boolean save2file = false;

	public static int i(String tag, String msg) {
		if (save2file) {
			log2File(tag, msg);
		}
		return android.util.Log.i(tag, msg);
	}

	public static int e(String tag, String msg, Throwable tr) {
		if (save2file) {
			log2File(tag, msg);
		}
		return android.util.Log.e(tag, msg, tr);
	}

	public static int w(String tag, String msg) {
		if (save2file) {
			log2File(tag, msg);
		}
		return android.util.Log.w(tag, msg);
	}

	/** 写入文件 */
	public static void log2File(String tag, String content) {
		// return;
		FileWriter writer = null;
		try {
			if (BaseApplication.logPath == null) {
				BaseApplication.instent.initFilePath();
			}
			writer = new FileWriter(BaseApplication.logPath, true);
			writer.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date(System.currentTimeMillis())) + tag + " " + content + "\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}