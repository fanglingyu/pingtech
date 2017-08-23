package com.pingtech.framework.web.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class BaseWebUtils {
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
