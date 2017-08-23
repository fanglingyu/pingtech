package com.pingtech.hgqw.service.test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pingtech.R;
import com.pingtech.hgqw.service.ImageDownload;
import com.pingtech.hgqw.service.ImageDownloadService;

public class TestServiceActivity extends Activity {

	private static final String TAG = "TestServiceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_service);
		find();
		init();
	}

	private void init() {
		BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBTAdapter.getState() == BluetoothAdapter.STATE_OFF) {// 此判断代表蓝牙当前为开，反之为关
		// mBTAdapter.disable();//关闭蓝牙
			mBTAdapter.enable();
		}
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.request_image:
			startService();
			// requeseByZjhm("i");
			break;

		default:
			break;
		}
	}

	private void requeseByZjhm(String zjhm) {
		new ImageDownload(null).startDownload();
	}

	private void startService() {
		Log.i(TAG, "startService");
		stopService(new Intent(this, ImageDownloadService.class));
		startService(new Intent(this, ImageDownloadService.class));
	}

	private void find() {
	}

	@Override
	public void onBackPressed() {
		stopService(new Intent(this , ImageDownloadService.class));
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		stopService(new Intent(this, ImageDownloadService.class));
		super.onDestroy();
	}

}
