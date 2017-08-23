package com.pingtech.hgqw.module.home.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.Http;
import com.pingtech.hgqw.web.WebService;

/**
 * 
 * 
 * 类描述：更新系统时间
 * 
 * <p>
 * Title: 海江港边检勤务综合管理系统-UpdateSysTime.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-10-31 下午3:51:32
 */
public class UpdateSysTime extends AsyncTask<String, String, String> {
	public static final String GET_CURRENT_TIME = "getCurrentTime";

	public static final String TAG = "UpdateSysTime";

	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG, "doInBackground");
		if (!StringUtils.isNotEmpty(params)) {
			return null;
		}
		String url = params[0];
		if (GET_CURRENT_TIME.equals(url)) {
			return getCurrentTime(params);
		}
		return null;
	}

	/**
	 * 
	 * @方法名：getCurrentTime
	 * @功能说明：请求网络
	 * @author liums
	 * @date 2013-10-31 下午2:48:28
	 * @param params
	 */
	private String getCurrentTime(String[] params) {
		String res = GET_CURRENT_TIME;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("PDACode", params[1]));
		try {
			if (SystemSetting.getWebServiceConnect()) {
				res = WebService.request(GET_CURRENT_TIME, nameValuePairs);
			} else {
				res = Http.httpPost(GET_CURRENT_TIME, nameValuePairs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// NetWorkManager.result2XMLFile(GET_CURRENT_TIME, res);
		return res;
	}

	@Override
	protected void onPostExecute(String result) {
		updateSystemTime(result);
	}

	/**
	 * 
	 * @方法名：updateSystemTime
	 * @功能说明：更新系统时间
	 * @author liums
	 * @date 2013-10-31 下午3:49:21
	 * @param time
	 */
	private void updateSystemTime(String time) {
		Log.i(TAG, "updateSystemTime，time：" + time);
		if (StringUtils.isEmpty(time)) {// 网络请求不通，设置离线标识为false
			Log.i(TAG, "set web state StringUtils.isEmpty(time) BaseApplication.instent.setWebState(false)");
			BaseApplication.instent.setWebState(false);
			return;
		}
		if(time.contains("error")){
			Log.i(TAG, "set web state time.contains(error) BaseApplication.instent.setWebState(false)");
			BaseApplication.instent.setWebState(false);
			return;
		}
		
		
		
		Date date = DateUtils.stringToDate(time, DateUtils.DATE_FMT_2);
		if (date == null) {
			Log.i(TAG, "set web state date == null BaseApplication.instent.setWebState(false)");
			BaseApplication.instent.setWebState(false);
			return;
		}
		
		BaseApplication.instent.setWebState(true);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		long when = calendar.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when);
		}
	}

}
