package com.pingtech.hgqw.service.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.widget.HgqwToast;

public class GpsTestActivity extends Activity {
	private TextView jd = null;

	private TextView wd = null;

	private TextView ts = null;

	private boolean flag = true;

	private Handler handler = null;
	private static final String BROADCAST_FOR_CONTROL_LOCATION  = "com.sim.intent.action.gps_bd.MODE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_test);
		find();
		init();
	}
	public void click(View v){
		switch (v.getId()) {
		//定位方式：1北斗，2GPS，3北斗+GPS，4关闭北斗和GPS
		case R.id.gps:
			sendBroadcastForLocation(2);
			HgqwToast.toast("GPS");
			break;
		case R.id.bd:
			sendBroadcastForLocation(1);
			HgqwToast.toast("北斗");
			break;
		case R.id.gps_bd:
			sendBroadcastForLocation(3);
			HgqwToast.toast("北斗+GPS");
			break;

		default:
			break;
		}
	}
	private void sendBroadcastForLocation(int type) {
		Intent intent = new Intent();
		intent.setAction(BROADCAST_FOR_CONTROL_LOCATION);
		//定位方式：1北斗，2GPS，3北斗+GPS，4关闭北斗和GPS
		intent.putExtra("operate", type);
		this.sendBroadcast(intent);
	}
	private void init() {
		startService(new Intent("com.pingtech.PINGTECH_SERVICE"));// 启动后台GPS定位服务
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String jdD = BaseApplication.instent.getLongitude();
				String wdD = BaseApplication.instent.getLatitude();
				jd.setText("精度：" + jdD);
				wd.setText("纬度：" + wdD);
			}
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (flag) {
					SystemClock.sleep(1000);
					handler.obtainMessage().sendToTarget();
				}
			}
		}).start();
	}

	private void find() {
		jd = (TextView) findViewById(R.id.jd);
		wd = (TextView) findViewById(R.id.wd);
		ts = (TextView) findViewById(R.id.ts);
	}

	@Override
	protected void onDestroy() {
		flag = false;
		stopService(new Intent("com.pingtech.PINGTECH_SERVICE"));
		super.onDestroy();
	}

}
