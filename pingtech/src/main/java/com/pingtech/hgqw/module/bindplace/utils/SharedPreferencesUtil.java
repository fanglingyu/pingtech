package com.pingtech.hgqw.module.bindplace.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;

public class SharedPreferencesUtil {

	public static void deleteDdbd() {
		SharedPreferences sharedPreferences = BaseApplication.instent.getSharedPreferences(
				BaseApplication.instent.getString(R.string.shared_init_name), Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("xunJianName", SystemSetting.xunJianName);
		editor.remove("xunJianType");
		editor.remove("xunJianId");
		editor.remove("xunJianMTid");
		editor.remove("xunJianName");

		editor.remove("xunJianMTname");
		editor.remove("xunJianMtgsgs");
		editor.remove("xunJianMtgm");
		editor.remove("xunJianKbnl");
		editor.remove("xunJianZxhz");
		editor.remove("xunJianQyfw");
		editor.remove("xunJianXxxx");
		editor.remove("xunJianWz");
		editor.remove("xunJianZdmbcbdw");
		editor.remove("xunJianSs");
		editor.remove("xunJianMbdsl");
		editor.remove("xunJianZdgkcbdw");
		editor.remove("xunJianMtzax");
		editor.remove("xunJianQylx");
		editor.remove("readcardhc");
		editor.commit();
	}

	/**
	 * 
	 * @方法名：saveDdbd
	 * @功能说明：将
	 * @author liums
	 * @date 2014-5-16 上午11:56:23
	 */
	public static void saveDdbdToSharedPre() {
		// SystemSetting.xunJianId;
		// SystemSetting.xunJianType;
		// SystemSetting.xunJianMTid;
		SharedPreferences sharedPreferences = BaseApplication.instent.getSharedPreferences(
				BaseApplication.instent.getString(R.string.shared_init_name), Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("xunJianType", SystemSetting.xunJianType);
		editor.putString("xunJianId", SystemSetting.xunJianId);
		editor.putString("xunJianMTid", SystemSetting.xunJianMTid);
		editor.putString("xunJianName", SystemSetting.xunJianName);

		editor.putString("xunJianMTname", SystemSetting.xunJianMTname);
		editor.putString("xunJianMtgsgs", SystemSetting.xunJianMtgsgs);
		editor.putString("xunJianMtgm", SystemSetting.xunJianMtgm);
		editor.putString("xunJianKbnl", SystemSetting.xunJianKbnl);
		editor.putString("xunJianZxhz", SystemSetting.xunJianZxhz);
		editor.putString("xunJianQyfw", SystemSetting.xunJianQyfw);
		editor.putString("xunJianXxxx", SystemSetting.xunJianXxxx);
		editor.putString("xunJianWz", SystemSetting.xunJianWz);
		editor.putString("xunJianZdmbcbdw", SystemSetting.xunJianZdmbcbdw);
		editor.putString("xunJianSs", SystemSetting.xunJianSs);
		editor.putString("xunJianMbdsl", SystemSetting.xunJianMbdsl);
		editor.putString("xunJianZdgkcbdw", SystemSetting.xunJianZdgkcbdw);
		editor.putString("xunJianMtzax", SystemSetting.xunJianMtzax);
		editor.putString("xunJianQylx", SystemSetting.xunJianQylx);
		editor.putString("readcardhc", SystemSetting.readcardhc);
		editor.commit();
	}

	/**
	 * 
	 * @方法名：initDdbdToSystemSetting
	 * @功能说明：将绑定地点加载到内存中
	 * @author liums
	 * @date 2014-5-16 下午3:54:28
	 */
	public static void initDdbdToSystemSetting() {
		SharedPreferences sharedPreferences = BaseApplication.instent.getSharedPreferences(
				BaseApplication.instent.getString(R.string.shared_init_name), Context.MODE_PRIVATE);
		SystemSetting.xunJianId = sharedPreferences.getString("xunJianId", null);
		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			SystemSetting.xunJianType = sharedPreferences.getString("xunJianType", null);
			SystemSetting.xunJianMTid = sharedPreferences.getString("xunJianMTid", null);
			SystemSetting.xunJianName = sharedPreferences.getString("xunJianName", null);

			SystemSetting.xunJianMTname = sharedPreferences.getString("xunJianMTname", null);
			SystemSetting.xunJianMtgsgs = sharedPreferences.getString("xunJianMtgsgs", null);
			SystemSetting.xunJianMtgm = sharedPreferences.getString("xunJianMtgm", null);
			SystemSetting.xunJianKbnl = sharedPreferences.getString("xunJianKbnl", null);
			SystemSetting.xunJianZxhz = sharedPreferences.getString("xunJianZxhz", null);
			SystemSetting.xunJianQyfw = sharedPreferences.getString("xunJianQyfw", null);
			SystemSetting.xunJianXxxx = sharedPreferences.getString("xunJianXxxx", null);
			SystemSetting.xunJianWz = sharedPreferences.getString("xunJianWz", null);
			SystemSetting.xunJianZdmbcbdw = sharedPreferences.getString("xunJianZdmbcbdw", null);
			SystemSetting.xunJianSs = sharedPreferences.getString("xunJianSs", null);
			SystemSetting.xunJianMbdsl = sharedPreferences.getString("xunJianMbdsl", null);
			SystemSetting.xunJianZdgkcbdw = sharedPreferences.getString("xunJianZdgkcbdw", null);
			SystemSetting.xunJianMtzax = sharedPreferences.getString("xunJianMtzax", null);
			SystemSetting.xunJianQylx = sharedPreferences.getString("xunJianQylx", null);
			SystemSetting.readcardhc = sharedPreferences.getString("readcardhc", null);
		}
	}

	public static String getImgSjid() {
		SharedPreferences sharedPreferences = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		return sharedPreferences.getString("imgSjid", "0");
	}

	public static void updateImgSjid(String imgSjid) {
		SharedPreferences sharedPreferences = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("imgSjid", imgSjid);
		editor.commit();
	}

}
