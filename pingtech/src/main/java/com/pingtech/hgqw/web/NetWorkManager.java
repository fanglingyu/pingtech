package com.pingtech.hgqw.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;

/** 网络管理模块，在这里根据用户设置，分成http和webservice */
public class NetWorkManager {

	private static final String TAG = "NetWorkManager";

	private static final int MSG_HTTP_RESULT = 0;

	private static final int MSG_HTTP_IMAGE_RESULT = 1;

	/** 网络返回后，通过onHttpResult接口返回给上层 */
	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HTTP_RESULT:
				@SuppressWarnings("unchecked")
				Pair<OnHttpResult, String> res = (Pair<OnHttpResult, String>) msg.obj;
				res.first.onHttpResult(res.second, msg.arg1);
				break;
			case MSG_HTTP_IMAGE_RESULT:
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 发起一个请求
	 * 
	 * @param onHttpResult
	 *            onHttpResult接口
	 * 
	 * @param path
	 *            接口名
	 * @param params
	 *            参数列表
	 * @param httpRequestType
	 *            http type
	 * */
	public static void request(final OnHttpResult onHttpResult, final String path, final List<NameValuePair> params, final int httpRequestType) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String res = null;
				try {
					if (SystemSetting.getWebServiceConnect()) {
						res = WebService.request(path, params);
					} else {
						res = Http.httpPost(path, params);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i(TAG, "~~~" + path + "~~~,request Exception");
				}
				result2XMLFile(path, res);
				Message msg = handler.obtainMessage(MSG_HTTP_RESULT, httpRequestType, 0, new Pair<OnHttpResult, String>(onHttpResult, res));
				handler.sendMessage(msg);
			}
		};
				ThreadPool.getInstance().addTask(runnable);
//		ThreadPool.addToSingleThreadExecutor(runnable);
	}

	/** 把平台返回的结果保存到本机，便于跟踪问题 ，如果需要此功能，需要把request中调用该函数的接口打开 */
	public static void result2XMLFile(String path, String content) {
		try {
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				return;
			}
			if (content == null) {
				return;
			}
			String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + "xml";
			File dir = new File(projectDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			FileWriter writer = new FileWriter(projectDir + File.separator + path + ".xml");
			writer.write(content);
			writer.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
