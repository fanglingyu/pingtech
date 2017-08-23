package com.pingtech.hgqw.module.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.module.cfzg.CfzgIndex;
import com.pingtech.hgqw.module.login.activity.Login;

public class Welcome extends Activity {
	private static final String TAG = "Welcome";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.welcome);
		init();
	}

	private void init() {
		Intent intent = new Intent();
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_CFZG:
			intent.setClass(this, CfzgIndex.class);
			break;
		default:
			intent.setClass(this, Login.class);
			break;
		}
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
	}

}
