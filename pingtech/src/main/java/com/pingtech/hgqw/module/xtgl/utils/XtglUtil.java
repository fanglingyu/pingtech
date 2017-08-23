package com.pingtech.hgqw.module.xtgl.utils;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;

public class XtglUtil {

	/**
	 * 
	 * @方法名：ifDelOldData
	 * @功能说明：检测版本是否需要清除旧数据
	 * @author liums
	 * @date 2013-12-11 下午2:18:14
	 * @return
	 */
	public static boolean hasAlreadyDel() {
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		return prefs.getBoolean("hasAlreadyDel", false);
	}

	/**
	 * 
	 * @方法名：delDB
	 * @功能说明：删除数据库文件
	 * @author liums
	 * @return
	 * @date 2013-12-11 下午2:23:32
	 */
	public static boolean delDB() {
		//使用指定路径创建一个File对象，
		String dbUrl = "/data/data/com.pingtech/databases/hgqw";
		File file = new File(dbUrl);
		if (file.exists() && file.isFile()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * 
	 * @方法名：delSjid
	 * @功能说明：删除审计id
	 * @author liums
	 * @date 2013-12-11 下午2:33:32
	 */
	public static void delSjid() {
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("Hgzjxx_addSjid", "0");
		editor.putString("Hgzjxx_delSjid", "0");
		editor.putBoolean("hasAlreadyDel", true);
		editor.commit();
	}

}
