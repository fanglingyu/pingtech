package com.pingtech.hgqw.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.yydj.service.ListenerTask;

@SuppressLint("HandlerLeak")
public class ListenerService extends Service {
	private static final String TAG = "ListenerService";

	public static final int HAVE_NEW_AUDIO = 1;

	private ListenerTask listenerTask = null;

	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public ListenerService getService() {
			return ListenerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public void startListen(Handler handler) {
		// 启动线程循环获取语音消息
		listenerTask = new ListenerTask();
		listenerTask.execute(handler);
	}

	@Override
	public void onDestroy() {
		if (listenerTask != null) {
			listenerTask.stopTimer();
			listenerTask = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (listenerTask != null) {
			listenerTask.stopTimer();
			listenerTask = null;
		}
		return super.onUnbind(intent);
	}

	public void setOnHttpResult(OnHttpResult onHttpResult) {
	}
}
