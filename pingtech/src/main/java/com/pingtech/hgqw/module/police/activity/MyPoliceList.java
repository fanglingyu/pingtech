package com.pingtech.hgqw.module.police.activity;

import java.util.Map;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.police.adapter.MyPoliceAdapter;
import com.pingtech.hgqw.module.police.entity.MyPolice;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.module.qwjw.utils.QwzlConstant;
import com.pingtech.hgqw.mqtt.receiver.MessageReceiver;
import com.pingtech.hgqw.mqtt.service.MqttService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 我的警务界面activity类
 * */
public class MyPoliceList extends MyActivity implements OnHttpResult {
	private static final String TAG = "MyPoliceList";

	/** 进入签收（详情）界面 */
	private static final int STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU = 1;

	/** 进入反馈界面（暂时不需要该功能） */
	private static final int STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK = 2;

	private ListView listView;

	public static MyPoliceAdapter adapter = null;

	private ProgressDialog progressDialog = null;

	private Map<String, Object> map;

	private TextView mypolice_empty;

	private PoliceHandler policeHandler = null;

	private JwzlBroadCaseReceiver jwzlBroadCaseReceiver = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.police_list);
		setMyActiveTitle(R.string.mypolice);
		listView = (ListView) findViewById(R.id.listview);
		// 注册推送广播
		jwzlBroadCaseReceiver = new JwzlBroadCaseReceiver();
		registerReceiver(jwzlBroadCaseReceiver, new IntentFilter(MqttService.MQTT_MSG_RECEIVED_INTENT));
	}

	private class JwzlBroadCaseReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive");
			NotificationManager manager = (NotificationManager) BaseApplication.instent.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(MessageReceiver.NOTIFICATION_ID_JWZL);
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		unregisterReceiver(jwzlBroadCaseReceiver);
		adapter = null;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		mypolice_empty = (TextView) this.findViewById(R.id.mypolice_empty);
		if ((SystemSetting.taskList == null) || (SystemSetting.taskList.size() == 0)) {
			mypolice_empty.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			policeHandler = new PoliceHandler();
			mypolice_empty.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			adapter = new MyPoliceAdapter(this, policeHandler, 0);
			listView.setAdapter(adapter);
		}
		super.onResume();
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null && str.equals("1")) {
			HgqwToast.makeText(MyPoliceList.this, R.string.qianshou_success, HgqwToast.LENGTH_LONG).show();
			map.put("qszt", "1");
			adapter.notifyDataSetChanged();
		} else {
			HgqwToast.makeText(MyPoliceList.this, R.string.qianshou_failure, HgqwToast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU:
			if (resultCode == RESULT_OK) {
				map.put("qszt", "1");
				adapter.notifyDataSetChanged();
			}
			break;
		case STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK:
			if (resultCode == RESULT_OK) {
				map.put("fkzt", "1");
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	class PoliceHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RequestPolice.SIGN_MY_TASK_SUCCESS:
				if(map!=null){
					map.put("qszt", QwzlConstant.QSZT_YQS);
					adapter.notifyDataSetChanged();
				}
				break;
			case MyPoliceAdapter.POSITIONEXCEPT:
				map = SystemSetting.taskList.get(msg.arg1 / 2);
				String qszt = (String) map.get("qszt");
				if (qszt != null && qszt.equals("0")) {
					// 签收
					String taskid = (String) map.get("taskid");
					String dwid = (String) map.get("dwid");
					String jwzldwid = (String) map.get("jwzldwid");
					String cjfzr = (String) map.get("cjfzr");
					String cjlx = (String) map.get("cjlx");
					new RequestPolice(MyPoliceList.this, this).requestSignMyTask(taskid, dwid, "JWZL", jwzldwid , cjfzr , cjlx);

					//
					// // 签收
					// String url = "signMyTask";
					// if (progressDialog != null) {
					// return;
					// }
					// List<NameValuePair> params = new
					// ArrayList<NameValuePair>();
					// params.add(new BasicNameValuePair("taskid", (String)
					// map.get("taskid")));
					// params.add(new BasicNameValuePair("userID",
					// LoginUser.getCurrentLoginUser().getUserID()));
					// progressDialog = new ProgressDialog(MyPoliceList.this);
					// progressDialog.setTitle(getString(R.string.waiting));
					// progressDialog.setMessage(getString(R.string.waiting));
					// progressDialog.setCancelable(false);
					// progressDialog.setIndeterminate(false);
					// progressDialog.show();
					// NetWorkManager.request(MyPoliceList.this, url, params,
					// 0);
				}

				break;
			case MyPoliceAdapter.POSITIONNOEXCEPT:
				map = SystemSetting.taskList.get((msg.arg1 - 1) / 2);
				Intent intent = new Intent();
				MyPolice myPolice = new MyPolice();
				Bundle bundle = new Bundle();
				intent.putExtra("taskid", (String) map.get("taskid"));
				String qszt1 = (String) map.get("qszt");
				String zlzt = (String) map.get("zlzt");
				if ((qszt1 != null && qszt1.equals("1")) || (zlzt != null && zlzt.equals("1"))) {
					intent.putExtra("new", false);
				} else {
					intent.putExtra("new", true);
				}
				myPolice.setCjfzr((String) map.get("cjfzr"));
				myPolice.setFbr((String) map.get("fbr"));
				myPolice.setFbsj((String) map.get("fbsj"));
				myPolice.setJqlb((String) map.get("jqlb"));
				myPolice.setPzr((String) map.get("pzr"));
				myPolice.setZlnr((String) map.get("zlnr"));
				myPolice.setZlzt((String) map.get("zlzt"));
				myPolice.setQszt((String) map.get("qszt"));
				myPolice.setCjlx((String) map.get("cjlx"));
				myPolice.setJwzldwid((String) map.get("jwzldwid"));
				myPolice.setDwid((String) map.get("dwid"));
				bundle.putSerializable("myPolice", myPolice);
				intent.putExtras(bundle);
				String unReadStr = (String) map.get("unread");
				if (unReadStr == null || unReadStr.length() == 0) {
					intent.putExtra("unread", true);
					map.put("unread", "false");
				} else {
					intent.putExtra("unread", false);
				}
				intent.setClass(getApplicationContext(), MyPoliceDetail.class);
				if (qszt1 != null && qszt1.equals("0")) {
					startActivityForResult(intent, STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU);
				} else {
					startActivityForResult(intent, STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK);
				}

				break;
			default:
				break;
			}
		}
	}
}
