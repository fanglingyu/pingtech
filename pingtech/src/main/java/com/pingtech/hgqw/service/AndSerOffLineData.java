package com.pingtech.hgqw.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.web.NetWorkManager;

@SuppressLint("HandlerLeak")
public class AndSerOffLineData extends Service implements OnHttpResult {
	private static final String TAG = "AndSerOffLineData";

	/** 离线业务数据上传周期 */
	private static final long loopTime = 1 * 60 * 1000;

	private static final int RESULT_CODE = 1000;

	private Timer mTimer = null;

	private TimerTask mTimerTask = null;

	/** 离线业务数据上传接口 */
	private static final String upUrl = "receivePdaSendData";

	private Handler handler = null;

	private List<Integer> offLineIds = null;

	private List<NameValuePair> params = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart");
		super.onStart(intent, startId);
		init();
	}

	private void init() {
		Log.i(TAG, "init");

		handler = new MyHandler();

		stopTimer();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "time again");
				if (BaseApplication.instent.isDownloadFlag()) {
					Log.i(TAG, "BaseApplication.instent.isDownloadFlag()==true");
					return;
				}
				if (handler != null) {
					OffDataService offDataService = new OffDataService();
					params = new ArrayList<NameValuePair>();
					offLineIds = new ArrayList<Integer>();
					offDataService.offLineDataPackage(params, offLineIds);
					if (offLineIds == null || offLineIds.size() < 1) {
						Log.i(TAG, "offLineIds==null||offLineIds.size()<1");
						return;
					}
					handler.sendEmptyMessage(RESULT_CODE);
				} else {
					Log.i(TAG, "time again handler == null");
				}
			}
		};
		// 调用频率
		mTimer.schedule(mTimerTask, 0, 1 * 60 * 1000);

		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { while (true) { SystemClock.sleep(time);
		 * Log.i(TAG, "time again"); if (handler != null) { OffDataService
		 * offDataService = new OffDataService(); params = new
		 * ArrayList<NameValuePair>(); offLineIds = new ArrayList<Integer>();
		 * offDataService.offLineDataPackage(params, offLineIds); if (offLineIds
		 * == null || offLineIds.size() < 1) { Log.i(TAG,
		 * "offLineIds==null||offLineIds.size()<1"); continue; }
		 * handler.sendEmptyMessage(RESULT_CODE); } else { Log.i(TAG,
		 * "time again handler == null"); } } } }).start();
		 */
	}

	/**
	 * 
	 * @方法名：stopTimer
	 * @功能说明：停止
	 * @author liums
	 * @date 2013-12-13 上午10:59:48
	 */
	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		stopTimer();
		super.onDestroy();
	}

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			request();
		}

	}

	/**
	 * 
	 * @方法名：request
	 * @功能说明：定时上传离线数据
	 * @author liums
	 * @date 2013-9-27 上午10:35:17
	 */
	private void request() {
		Log.i(TAG, "request");
		// 先判断网络是否可用，通过Handler
		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(AndSerOffLineData.this, upUrl, params, RESULT_CODE);
		} else {
			Log.i(TAG, "request webState=false");
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "httpResult,httpRequestType=" + httpRequestType + ",str=" + str);
		switch (httpRequestType) {
		case RESULT_CODE:
			resultBusiness(str);
			break;

		default:
			break;
		}
	}

	private void resultBusiness(String str) {
		if (str == null) {
			return;
		} else if ("Y".equals(str)) {
			delByIds();// 删除离线数据
		}
	}

	/**
	 * 
	 * @方法名：delByIds
	 * @功能说明：删除已经上传的离线数据
	 * @author liums
	 * @date 2013-9-27 下午4:20:35
	 */
	private void delByIds() {
		Log.i(TAG, "delByIds");
		try {
			OffDataService offDataService = new OffDataService();
			offDataService.deleteByIds(offLineIds);
			// 删除通行方向记录表
			/*DkqkService dkqkService = new DkqkService();
			dkqkService.deleteAll();

			TxjlKkService txjlKkService = new TxjlKkService();
			txjlKkService.deleteAll();

			TxjlTkService txjlTkService = new TxjlTkService();
			txjlTkService.deleteAll();*/
		} catch (SQLException e) {
			Log.i(TAG, "delByIds,error");
			e.printStackTrace();
		}
	}

}
