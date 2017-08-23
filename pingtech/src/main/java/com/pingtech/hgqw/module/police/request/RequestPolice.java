package com.pingtech.hgqw.module.police.request;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.util.Xml;

import com.pingtech.R;
import com.pingtech.hgqw.base.dialog.BaseDialogUtils;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.qwjw.utils.QwzlConstant;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class RequestPolice implements OnHttpResult, OffLineResult {
	private static final int SIGN_MY_TASK = 0;

	private static final int SIGN_MY_TASK_QWZL = 2;

	private static final int SEND_VIEW_TASKMSG = 1;

	private static final String TAG = "RequestPolice";

	public static final int SIGN_MY_TASK_SUCCESS = 1000;

	/** 获取警务指令 */
	public static final int HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK = 11;

	/** 获取勤务指令 */
	public static final int HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL = 12;

	private Context context = null;

	private Handler handler = null;
	
	private String mSing = "";

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK:
			case HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL:
				requestReceiveMyTask(msg.what);
			default:
				break;
			}
		}

	};

	public RequestPolice(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/**
	 * 
	 * @param taskid
	 *            指令基本ID
	 * @param dwid
	 *            单位ID
	 * @param zlzl
	 *            指令种类 JWZL警务指令，QWZL 勤务指令
	 */
	public void requestSignMyTask(String taskid, String dwid, String zlzl, String jwzldwid, String cjfzr, String cjlx) {
		// 签收
		String url = "signMyTask";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("taskid", taskid));
		params.add(new BasicNameValuePair("dwid", dwid));
		params.add(new BasicNameValuePair("jwzldwid", jwzldwid));
		params.add(new BasicNameValuePair("zlzl", zlzl));
		params.add(new BasicNameValuePair("cjfzr", cjfzr));
		params.add(new BasicNameValuePair("cjlx", cjlx));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		BaseDialogUtils.showRequestDialog(context, false);
		NetWorkManager.request(this, url, params, SIGN_MY_TASK);

	}

	public void requestSendViewTaskMsg(String taskid, String dwid, String zlzl, String jwzldwid, String cjfzr, String cjlx) {
		// 查看
		String url = "sendViewTaskMsg";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("taskid", taskid));
		params.add(new BasicNameValuePair("dwid", dwid));
		params.add(new BasicNameValuePair("jwzldwid", jwzldwid));
		params.add(new BasicNameValuePair("zlzl", zlzl));
		params.add(new BasicNameValuePair("cjfzr", cjfzr));
		params.add(new BasicNameValuePair("cjlx", cjlx));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		// BaseDialogUtils.showRequestDialog(context);
		NetWorkManager.request(this, url, params, SEND_VIEW_TASKMSG);
	}

	public void requestReceiveMyTask(int what) {
		Log.i(TAG, "requestReceiveMyTask,what=" + what);
		String str = "receiveMyTask";
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		String zlzl = getZlzl(what);
		params.add(new BasicNameValuePair("zlzl", zlzl));

		NetWorkManager.request(this, str, params, what);
		if (HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK == what) {
			myHandler.postDelayed(autoReceiveTaskRunnable, 10 * 1000);
		}
	}

	private String getZlzl(int what) {
		String zlzl = "";
		switch (what) {
		case HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK:
			zlzl = QwzlConstant.ZLZL_JWZL;
			break;
		case HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL:
			zlzl = QwzlConstant.ZLZL_QWZL;
			break;
		default:
			break;
		}
		return zlzl;
	}

	private Runnable autoReceiveTaskRunnable = new Runnable() {
		@Override
		public void run() {
			if (myHandler == null) {
				return;
			}
			myHandler.obtainMessage(HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL).sendToTarget();
		}
	};

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		switch (httpRequestType) {
		case SIGN_MY_TASK:
			responseSignMyTask(str);
			//handler.obtainMessage(RequestPolice.SIGN_MY_TASK_SUCCESS, str).sendToTarget();
			break;
		case SEND_VIEW_TASKMSG:
			responseSendViewTaskMsg(str);
			break;
		case HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK:
		case HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL:
			handler.obtainMessage(httpRequestType, str).sendToTarget();
			break;

		default:
			break;
		}
	}

	private void responseSignMyTask(String str) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
	/*	if (str != null && str.equals("1")) {
			HgqwToast.toast(R.string.qianshou_success);
			handler.obtainMessage(RequestPolice.SIGN_MY_TASK_SUCCESS).sendToTarget();
		} else if (str != null && str.equals("2")) {
			HgqwToast.toast(R.string.qianshou_failure_already_cancel);
		} else {
			HgqwToast.toast(R.string.qianshou_failure);
		}*/
		
		if(str != null){
			handler.obtainMessage(RequestPolice.SIGN_MY_TASK_SUCCESS, str).sendToTarget();
		}else{
			HgqwToast.toast(R.string.qianshou_failure);
		}
		BaseDialogUtils.dismissRequestDialog();
	}
	
	/** 解析签收勤务指令返回的数据 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		/*SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();*/
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			//httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						/*if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}*/
						mSing = text;
					} else if ("hc".equals(parser.getName())) {
						// 信息
						//httpReturnXMLInfo = parser.nextText();
						
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private void responseSendViewTaskMsg(String str) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		/*
		 * if (str != null && str.equals("1")) {
		 * HgqwToast.toast(R.string.qianshou_success);
		 * handler.obtainMessage(RequestPolice
		 * .SIGN_MY_TASK_SUCCESS).sendToTarget(); } else {
		 * HgqwToast.toast(R.string.qianshou_failure); }
		 * BaseDialogUtils.dismissRequestDialog();
		 */
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {

	}

}
