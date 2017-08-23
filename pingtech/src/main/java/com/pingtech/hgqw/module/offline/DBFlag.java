package com.pingtech.hgqw.module.offline;

import android.os.SystemClock;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.Log;

public class DBFlag {

	private static final String TAG = "DBFlag";

	public static void isDBBusy() {
		int count = 0;
		while (BaseApplication.instent.isDbBusyOnly || BaseApplication.instent.isDbBusy) {
//			Log.i(TAG, "isDbBusy = " + BaseApplication.instent.isDbBusy + " , isDbBusyOnly = " + BaseApplication.instent.isDbBusyOnly);
			count++;
			SystemClock.sleep(100);
			if(count>30){
//				ReadService.getInstent().close();
//				BaseApplication.soundManager.onPlayVibrator(false);
//				BaseApplication.soundManager.onPlayVibrator(false);
				Log.i(TAG, "error isDbBusy = " + BaseApplication.instent.isDbBusy + " , isDbBusyOnly = " + BaseApplication.instent.isDbBusyOnly);
				return;
			}
		}
		BaseApplication.instent.isDbBusyOnly = true;
//		Log.i(TAG, "the db not busy(isDbBusyOnly)");
	}

	public static void setDBOnlyNotBusy() {
		BaseApplication.instent.isDbBusyOnly = false;
	}

}
