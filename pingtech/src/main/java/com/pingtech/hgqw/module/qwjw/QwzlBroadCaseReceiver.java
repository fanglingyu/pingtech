package com.pingtech.hgqw.module.qwjw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pingtech.hgqw.module.police.adapter.MyPoliceAdapter;

public class QwzlBroadCaseReceiver extends BroadcastReceiver {

	private static final String TAG = "QwzlBroadCaseReceiver";

	private MyPoliceAdapter adapter = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive");
		if (adapter != null) {
			Log.i(TAG, "adapter != null");
			adapter.notifyDataSetChanged();
		}else{
			Log.i(TAG, "adapter == null");
		}
	}

	public MyPoliceAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(MyPoliceAdapter adapter) {
		this.adapter = adapter;
	}

}
