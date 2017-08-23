package com.pingtech.hgqw.module.licence.utils;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;

public class LicenceUtils {
	private static final String LICENCE_VERSION_NAME = "licence_version_name";
	private static final String LICENCE_VERSION_DEFVALUE = "1";
	
	/**
	 * 
	 * @方法名：getVersionCode
	 * @功能说明：获取本地授权文件版本
	 * @author liums
	 * @date  2013-11-5 下午6:01:41
	 * @return
	 */
	public static String getVersionCode() {
		SharedPreferences sharedPreferences = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name), Context.MODE_PRIVATE);
		return sharedPreferences.getString(LICENCE_VERSION_NAME, LICENCE_VERSION_DEFVALUE);
		
	}
	public static boolean hasLicenceFile() {
		File file = new File("/data/data/com.pingtech/licence");
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
}
