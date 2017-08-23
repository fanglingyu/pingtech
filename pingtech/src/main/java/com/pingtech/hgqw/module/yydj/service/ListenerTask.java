package com.pingtech.hgqw.module.yydj.service;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;

import com.pingtech.hgqw.activity.UpdateActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.AudioFileUtils;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.service.ListenerService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.WebService;

@SuppressLint("NewApi")
public class ListenerTask extends AsyncTask<Handler, String, String> {

	private static final String TAG = "ListenerTask";

	private static final String LISTEN_URL = "listen";

	private static final long loopTime = 5 * 1000;

	private Timer mTimer = null;

	private TimerTask mTimerTask = null;

	private Handler handler = null;

	@Override
	protected String doInBackground(Handler... handler) {
		this.handler = handler[0];
		request();
		return null;
	}

	private void request() {
		stopTimer();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
//				Log.i(TAG, "time again");
				requestAgain();
			}
		};
		// 调用频率
		mTimer.schedule(mTimerTask, 0, loopTime);

	}

	protected void requestAgain() {
		if (!SystemSetting.isYydjOnOrOff()) {
//			Log.i(TAG, "SystemSetting.isYydjOnOrOff() = false,return");
			return;
		}
		if (!BaseApplication.instent.getWebState()) {
			Log.i(TAG, "WebState = false,return");
			return;
		}
		if (UpdateActivity.downloading) {
			Log.i(TAG, "UpdateActivity.downloading , return");
			return;
		}
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("talkerOrListener", "listener"));
		if (BaseApplication.instent.getWebState()) {
			LoginUser loginUser = LoginUser.getCurrentLoginUser();
			if (loginUser != null) {
				String userid = loginUser.getUserID();
				if (userid != null && !"".equals(userid)) {
					params.add(new BasicNameValuePair("userID", userid));
				}
			}
		} else {
			Log.i(TAG, "WebState = false");
		}

		String res = null;
		try {
			if (SystemSetting.isYydjOnOrOff()) {
				Log.i(TAG, "SystemSetting.isYydjOnOrOff() = true,request");
				res = WebService.request(LISTEN_URL, params);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		resultBusiness(res);
	}

	/**
	 * 
	 * @方法名：stopTimer
	 * @功能说明：停止
	 * @author liums
	 * @date 2013-12-13 上午10:59:48
	 */
	public void stopTimer() {
		Log.i(TAG, "stopTimer");
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
			Log.i(TAG, "stopTimer,mTimer != null");
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
			Log.i(TAG, "stopTimer,mTimerTask != null");
		}

	}

	@Override
	protected void onCancelled(String result) {
		Log.i(TAG, "onCancelled(String result)");
		stopTimer();
		super.onCancelled(result);
	}

	@Override
	protected void onCancelled() {
		Log.i(TAG, "onCancelled");
		stopTimer();
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
	}

	private void resultBusiness(String result) {
		if (StringUtils.isNotEmpty(result)) {
			// 保存文件
			ArrayList<String> nameList = AudioFileUtils.saveFiles(result);
			if (nameList != null && nameList.size() > 0) {
				if (handler != null) {
					handler.obtainMessage(ListenerService.HAVE_NEW_AUDIO, nameList).sendToTarget();
				} else {
					Log.i(TAG, "handler==null");
				}
			}
		}
	}
}
