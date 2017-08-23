package com.pingtech.hgqw.utils.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.pingtech.hgqw.utils.Log;

/**
 * 
 * 
 * 类描述：解压缩工具类
 * 
 * <p>
 * Title: 江海港边检勤务-移动管理系统-ZipUtils.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author 娄高伟
 * @version 1.0
 * @date 2013-9-25 下午2:22:46
 */
public class ZipUtils {
	private static final String TAG = "ZipUtils";
	/**
	 * 
	 * @方法名：uncompress
	 * @功能说明：解压XML
	 * @author 娄高伟
	 * @date 2013-9-25 下午2:23:05
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String uncompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		long beginTime = 0;
		long endTime = 0;
		beginTime = Calendar.getInstance().getTimeInMillis();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		String result = out.toString();
		result = URLDecoder.decode(result, "UTF-8");
		endTime = Calendar.getInstance().getTimeInMillis();
		Log.i(TAG, "解压耗时：" + (endTime - beginTime) + "");
		return result;
	}

	/**
	 * 
	 * @方法名：compress
	 * @功能说明：压缩XML
	 * @author 娄高伟
	 * @date 2013-9-25 下午2:23:29
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String compress(String str) throws IOException {
	    if (str == null || str.length() == 0) {
		      return str;
		    }
		    str = URLEncoder.encode(str, "UTF-8");
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    GZIPOutputStream gzip = new GZIPOutputStream(out);
		    gzip.write(str.getBytes());
		    gzip.close();
		    return out.toString("ISO-8859-1");		   
	 }

}
