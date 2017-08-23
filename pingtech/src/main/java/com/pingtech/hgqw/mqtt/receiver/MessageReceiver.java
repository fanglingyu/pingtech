package com.pingtech.hgqw.mqtt.receiver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.pingtech.R;
import com.pingtech.hgqw.activity.UpdateActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.AudioFileUtils;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.module.home.utils.PullXmlJwzl;
import com.pingtech.hgqw.module.police.activity.MyPoliceList;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.yydj.activity.TalkBack;
import com.pingtech.hgqw.mqtt.MqttContent;
import com.pingtech.hgqw.mqtt.interfaces.MessageHandler;
import com.pingtech.hgqw.mqtt.service.MqttService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.web.ThreadPool;
import com.pingtech.hgqw.web.WebService;

/**
 * 
 * 
 * 类描述：推送消息Receiver类
 * 
 * <p>
 * Title: 系统名称-MessageReceiver.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author zhaotf
 * @version 1.0
 * @date 2014-3-24 下午2:56:52
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MessageReceiver extends BroadcastReceiver {

	private static String Tag = "MessageReceiver";

	private static final String LISTEN_URL = "listen";

	public void registerHandler(MessageHandler handler) {
		if (!MqttContent.messageHandlers.contains(handler)) {
			MqttContent.messageHandlers.add(handler);
		}
	}

	public void unregisterHandler(MessageHandler handler) {
		if (MqttContent.messageHandlers.contains(handler)) {
			MqttContent.messageHandlers.remove(handler);
		}
	}

	public void clearHandlers() {
		MqttContent.messageHandlers.clear();
	}

	public boolean hasHandlers() {
		return MqttContent.messageHandlers.size() > 0;
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.i(Tag, "onReceive");
		if (intent != null) {
			Bundle notificationData = intent.getExtras();
			if (notificationData == null) {
				return;
			}
			final String topic = notificationData.getString(MqttService.MQTT_MSG_RECEIVED_TOPIC);
			final byte[] payload = notificationData.getByteArray(MqttService.MQTT_MSG_RECEIVED_MSG);
			if (null != payload && null != new String(payload)) {
				// 监听到推送广播，判断是语音消息还是警务指令
				if (MqttContent.MQTT_MESSAGE_YYXX.equals(new String(payload))) {
					SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), context.MODE_PRIVATE);
					if (!prefs.getBoolean(FunctionSetting.yydj, false)) {
						return;
					}
					// 通知下载语音文件
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							requestAgain(context, topic, payload);
						}
					}).start();
				} else if (MqttContent.MQTT_MESSAGE_JWZL.equals(new String(payload))) {
					ThreadPool.getInstance().addTask(new Runnable() {
						@Override
						public void run() {
							jwzlBusiness();
						}
					});
				}
			}
		}
	}

	private void jwzlBusiness() {
		Log.i(Tag, "onReceive jwzlBusiness");
		String url = "receiveMyTask";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		String res = null;
		try {
			res = WebService.request(url, params);
			if (res != null) {
				boolean newTask = PullXmlJwzl.onParseXMLDataMyTask(res);
				// if (MyPoliceList.adapter != null) {
				// (MyPoliceList.adapter).notifyDataSetChanged();
				// }
				if (newTask) {// 有新警务指令
					notifyForJwzl();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(Tag, "下载警务指令失败");
		}
	}

	private void notifyForJwzl() {
		Intent intent = new Intent();
		intent.setClass(BaseApplication.instent, MyPoliceList.class);

		Notification notification = new Notification(R.drawable.logo_s1, BaseApplication.instent.getString(R.string.have_order_msg),
				System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(BaseApplication.instent, 10, intent, 0);
		notification.setLatestEventInfo(BaseApplication.instent, BaseApplication.instent.getString(R.string.have_audio),
				BaseApplication.instent.getString(R.string.have_order_msg), pendingIntent);
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		NotificationManager manager = (NotificationManager) BaseApplication.instent.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTIFICATION_ID_JWZL, notification);
	}

	protected void requestAgain(Context context, String topic, byte[] payload) {
		// if (!SystemSetting.isYydjOnOrOff()) {
		// Log.i(Tag, "SystemSetting.isYydjOnOrOff() = false,return");
		// return;
		// }
		// if (!BaseApplication.instent.getWebState()) {
		// Log.i(Tag, "WebState = false,return");
		// return;
		// }
		Log.i(Tag, "onReceive yyxxBusiness");
		if (UpdateActivity.downloading) {
			Log.i(Tag, "UpdateActivity.downloading , return");
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
			Log.i(Tag, "WebState = false");
		}

		String res = null;
		try {
			// if (SystemSetting.isYydjOnOrOff()) {
			// Log.i(Tag, "SystemSetting.isYydjOnOrOff() = true,request");
			res = WebService.request(LISTEN_URL, params);
			// }
			resultBusiness(context, res, topic, payload);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(Tag, "下载语音失败");
		}
	}

	private void resultBusiness(Context context, String result, String topic, byte[] payload) {
		Context context2 = context;
		if (StringUtils.isNotEmpty(result)) {
			// 保存文件
			ArrayList<String> nameList = AudioFileUtils.saveFiles(result);
			if (nameList != null && nameList.size() > 0 && context2 != null) {
				if (hasHandlers()) {
					for (MessageHandler messageHandler : MqttContent.messageHandlers) {
						messageHandler.handleMessage(topic, payload);
					}
				} else {
					onNotifyTalkBack(context2, nameList);
				}
			}
		}
	}

	public static final int NOTIFICATION_ID = 0x123;

	public static final int NOTIFICATION_ID_JWZL = 0x124;

	/**
	 * 有新语音消息时，弹出通知并播放声音
	 * 
	 * @param nameList
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void onNotifyTalkBack(Context context, ArrayList<String> nameList) {
		context = BaseApplication.instent;
		Intent intent = new Intent();
		intent.putExtra("nameList", nameList);
		intent.putExtra("type", "listen");
		intent.setClass(context, TalkBack.class);

		Notification notification = new Notification(R.drawable.logo_s1, BaseApplication.instent.getString(R.string.have_audio),
				System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, intent, 0);
		notification.setLatestEventInfo(context, BaseApplication.instent.getString(R.string.have_audio),
				BaseApplication.instent.getString(R.string.have_audio_msg), pendingIntent);
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTIFICATION_ID, notification);
	}
}
