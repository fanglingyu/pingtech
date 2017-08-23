package com.pingtech.hgqw.module.qwjw.fragment;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.ShipDetailActivity;
import com.pingtech.hgqw.module.police.adapter.MyPoliceAdapter;
import com.pingtech.hgqw.module.police.entity.Qwzlqwjs;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.module.qwjw.QwzlBroadCaseReceiver;
import com.pingtech.hgqw.module.qwjw.activity.QwzlDetail;
import com.pingtech.hgqw.mqtt.service.MqttService;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.widget.HgqwToast;

public class QwzlFragment extends Fragment {

	private static final String TAG = "JwzlFragment";

	public static MyPoliceAdapter adapter = null;

	private TextView mypolice_empty;

	private ListView listView;

	private View mView = null;

	private Handler handler = null;

	/** 进入签收（详情）界面 */
	private static final int STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU = 1;

	/** 进入反馈界面（暂时不需要该功能） */
	private static final int STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK = 2;

	private Context context = null;

	private Activity acitvity = null;

	private QwzlBroadCaseReceiver qwzlBroadCaseReceiver = null;

	// private Map<String, Object> map;
	private Qwzlqwjs qwzlqwjs;

	public QwzlFragment(Context context, Activity acitvity) {
		this.context = context;
		this.acitvity = acitvity;
	}

	@Override
	public View getView() {
		Log.i(TAG, "getView");
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		handler = new PoliceHandler();
		// 定时刷新页面
		if (acitvity != null && qwzlBroadCaseReceiver != null) {
			acitvity.registerReceiver(qwzlBroadCaseReceiver, new IntentFilter(
					MqttService.MQTT_MSG_RECEIVED_INTENT));
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		View root = inflater.inflate(R.layout.police_list, null, true);
		// policeHandler = new PoliceHandler();
		find(root);
		mypolice_empty.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		adapter = new MyPoliceAdapter(context, handler, 1);

		qwzlBroadCaseReceiver = new QwzlBroadCaseReceiver();
		qwzlBroadCaseReceiver.setAdapter(adapter);
		acitvity.registerReceiver(qwzlBroadCaseReceiver, new IntentFilter(
				"com.pingtech.hgqw.module.qwjw.QwzlBroadCaseReceiver"));
		listView.setAdapter(adapter);
		mView = root;
		return mView;
	}

	private void find(View root) {
		listView = (ListView) root.findViewById(R.id.listview);
		mypolice_empty = (TextView) root.findViewById(R.id.mypolice_empty);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		if (acitvity != null && qwzlBroadCaseReceiver != null) {
			acitvity.unregisterReceiver(qwzlBroadCaseReceiver);
		}
		adapter = null;
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	private class PoliceHandler extends Handler {
		/** 进入签收（详情）界面 */
		private static final int STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU = 1;

		/** 进入反馈界面（暂时不需要该功能） */
		private static final int STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK = 2;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyPoliceAdapter.POSITIONEXCEPT:// 签收
				qwzlqwjs = SystemSetting.qwzlList.get(msg.arg1);
				String qszt = (String) qwzlqwjs.getQszt();
				if (qszt != null && qszt.equals("0")) {
					// 签收
					new RequestPolice(context, this).requestSignMyTask(
							qwzlqwjs.getQwzljbid(), qwzlqwjs.getQwzldwid(),
							"QWZL", "", "", "");
				}

				break;
			case RequestPolice.SIGN_MY_TASK_SUCCESS:// 签收成功

				String str = (String) msg.obj;

				SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				
				
				try {
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
					int type = parser.getEventType();
					String text = null;
					// httpReturnXMLInfo = null;
					while (type != XmlPullParser.END_DOCUMENT) {
						switch (type) {
						case XmlPullParser.START_TAG:
							if ("result".equals(parser.getName())) {
								text = parser.nextText();
								/*
								 * if ("error".equals(text)) { success = false; } else
								 * if ("success".equals(text)) { success = true; }
								 */
								//mSing = text;
								if(text.equals("1")){
									qwzlqwjs.setQszt("1");
									adapter.notifyDataSetChanged();
								}else if(text.equals("1")){
									HgqwToast.toast(R.string.qianshou_failure_already_cancel);
								}
							} else if ("hc".equals(parser.getName())) {
								// 信息
								// httpReturnXMLInfo = parser.nextText();
								Intent intent = new Intent();
								intent.putExtra("hc", parser.nextText());
								intent.putExtra("fromQwzlqs", true);
								intent.setClass(getActivity().getApplicationContext(), ShipDetailActivity.class);
								startActivity(intent);
							}
							break;
						case XmlPullParser.END_TAG:
							break;
						}
						type = parser.next();
					}
					//return success;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//return false;
				}
				
				//qwzlqwjs.setQszt("1");
				//adapter.notifyDataSetChanged();
				break;
			case MyPoliceAdapter.POSITIONNOEXCEPT:// 详情
				qwzlqwjs = SystemSetting.qwzlList.get(msg.arg1);
				Intent intent = new Intent();
				intent.putExtra("qwzlqwjs", qwzlqwjs);
				intent.setClass(context, QwzlDetail.class);
				startActivityForResult(intent,
						STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU);

				break;
			default:
				break;
			}
		}
	}

	/** 解析签收勤务指令返回的数据 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		
	
		 
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			// httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						/*
						 * if ("error".equals(text)) { success = false; } else
						 * if ("success".equals(text)) { success = true; }
						 */
						//mSing = text;
					} else if ("hc".equals(parser.getName())) {
						// 信息
						// httpReturnXMLInfo = parser.nextText();

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case STARTACTIVITY_FOR_POLICE_DICTATE_QIANSHOU:
			if (resultCode == Activity.RESULT_OK) {
				qwzlqwjs.setQszt("1");
				adapter.notifyDataSetChanged();
			}
			break;
		case STARTACTIVITY_FOR_POLICE_DICTATE_FEEDBACK:
			break;
		}
	}

}
