package com.pingtech.hgqw.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.module.cfzg.CfzgIndex;
import com.pingtech.hgqw.module.login.activity.Login;
import com.pingtech.hgqw.utils.Log;

/** 监听设备广播消息，这里主要监听设备开机完成，一旦开机完成，就自启动程序 */
public class PingtechReceiver extends BroadcastReceiver {
	private static final String TAG = "PingtechReceiver";
	/** 开机完成 */
	static final String POWER_ON_ACTION = "android.intent.action.BOOT_COMPLETED";
	/** 即将关机 */
	static final String POWER_OFF_ACTION = "android.intent.action.ACTION_SHUTDOWN";
	private Handler handler = new Handler();
	private Context context;
	/** 启动程序 */
	private Runnable autoStartServiceRunnable = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "autoStartServiceRunnable");
			if (handler == null) {
				return;
			}
			
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			switch (Flags.PDA_VERSION) {
			case Flags.PDA_VERSION_CFZG:
				intent.setClass(context, CfzgIndex.class);
				break;
			default:
				intent.setClass(context, Login.class);
				break;
			}
			context.startActivity(intent);
		}
	};

	/** 收到开机初始化完成的广播后，启动5秒钟的timer，timer时间到时启动程序 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive(): " + intent.toString());
		String action = intent.getAction();

		if (POWER_ON_ACTION.equals(action)) {
			Log.i(TAG, "boot completed");
			this.context = context;
			handler.postDelayed(autoStartServiceRunnable, 5000);

		} else if (POWER_OFF_ACTION.equals(action)) {
			Log.i(TAG, "ACTION_SHUTDOWN");
			context.stopService(new Intent("com.pingtech.PINGTECH_SERVICE"));
			this.context = null;
			handler = null;
		}
	}
}
