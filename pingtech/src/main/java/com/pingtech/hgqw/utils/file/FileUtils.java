package com.pingtech.hgqw.utils.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.pingtech.hgqw.base.BaseApplication;

/**
 * 
 * 
 * 类描述：文件操作工具类
 * 
 * <p>
 * Title: 海江港边检勤务综合管理系统-FileUtils.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-9-25 下午2:48:24
 */
public class FileUtils {

	/**
	 * 
	 * @方法名：strToFile
	 * @功能说明：将指定字符串保存进指定目录下的指定文件。
	 * @author liums
	 * @date 2013-9-25 下午2:49:38
	 * @param tag
	 * @param content
	 * @param path
	 * @param fileName
	 */
	public static void strToFile(String tag, String content, String path, String fileName) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(path + "/" + fileName);
			if (file.exists()) {
				file.delete();
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(path + "/" + fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void result2XMLFile(String path, String content) {
		try {
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				return;
			}
			if (content == null) {
				return;
			}
			String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + "xml";
			File dir = new File(projectDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			FileWriter writer = new FileWriter(projectDir + File.separator + path + ".xml");
			writer.write(content);
			writer.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeErrorLog(String content) {
		try {
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				return;
			}
			if (content == null) {
				return;
			}
			String projectDir = BaseApplication.instent.logDir;
			File dir = new File(projectDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			FileWriter writer = new FileWriter(projectDir + "error.txt" , true);
			writer.write("\n");
			writer.write("******error start****** time="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date(System.currentTimeMillis())));
			writer.write("\n");
			writer.write(content);
			writer.write("******error end******\n");
			writer.flush();
			writer.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
