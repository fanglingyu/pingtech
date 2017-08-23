package com.pingtech.hgqw.web.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import com.pingtech.hgqw.base.BaseApplication;

public class WebUtils {
	
	/**
	 * 
	 * @方法名：isNetworkAvailable
	 * @功能说明：判断是否离线
	 * @author liums
	 * @date  2013-9-27 下午2:00:22
	 * @return
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager connManager = (ConnectivityManager) BaseApplication.instent.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
			return true;
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			return true;
		}
		return false;
	}
}
