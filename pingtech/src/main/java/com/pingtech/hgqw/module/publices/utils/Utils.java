package com.pingtech.hgqw.module.publices.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.StringUtils;

public class Utils {
	private static Bitmap bitmap = null;

	public static boolean getState(String key, boolean def) {
		if (StringUtils.isEmpty(key)) {
			return def;
		}
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		return prefs == null ? def : prefs.getBoolean(key, def);
	}

	public static Bitmap getBitmap() {
		return bitmap;
	}

	public static void setBitmap(Bitmap bitmap) {
		Utils.bitmap = bitmap;
	}

}
