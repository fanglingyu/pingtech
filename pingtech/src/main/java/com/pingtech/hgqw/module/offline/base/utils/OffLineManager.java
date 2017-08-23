package com.pingtech.hgqw.module.offline.base.utils;

import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;

public class OffLineManager {
	private static final int MSG_HTTP_RESULT = 0;

	private static final int MSG_HTTP_IMAGE_RESULT = 1;

	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HTTP_RESULT:
				@SuppressWarnings("unchecked")
				Pair<OffLineResult, Pair<Boolean, Object>> res = (Pair<OffLineResult, Pair<Boolean, Object>>) msg.obj;
				res.first.offLineResult(res.second, msg.arg1);
				break;
			case MSG_HTTP_IMAGE_RESULT:
				break;
			default:
				break;
			}
		}
	};

	public static void request(final OffLineResult offLineResult, final BaseAction action, final String method, final Map<String, Object> params,
			final int httpRequestType) {
		new Thread() {
			@Override
			public void run() {
				Pair<Boolean, Object> res = null;
				BaseAction baseAction = action;
				try {
					res = baseAction.request(method, params);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage(MSG_HTTP_RESULT, httpRequestType, 0, new Pair<OffLineResult, Object>(offLineResult, res));
				handler.sendMessage(msg);
			}
		}.start();
	}

}
