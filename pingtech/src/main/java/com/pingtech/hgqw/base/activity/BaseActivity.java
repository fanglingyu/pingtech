package com.pingtech.hgqw.base.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.module.qwjw.utils.ActivityUtil;

public abstract class BaseActivity extends Activity {
	protected static final String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	public void onCreate(Bundle savedInstanceState, int layoutResID, String title) {
		Log.i(TAG, "custom onCreate");
		setTheme(R.style.CustomTheme);
		ActivityUtil.initCustomActionBar(this, title);
		super.onCreate(savedInstanceState);
		setEachContentView(layoutResID);

	}

	public void setEachContentView(int layoutResID) {
		super.setContentView(R.layout.base);
		String userName = "";
		try {
			userName = LoginUser.getCurrentLoginUser().getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (findViewById(R.id.current_user) != null) {
			((TextView) findViewById(R.id.current_user)).setText(getString(R.string.currentuser) + userName + "    ");
		}
		LinearLayout baseview = (LinearLayout) findViewById(R.id.baselayout);
		View overlay = (View) getLayoutInflater().inflate(layoutResID, null);
		if (baseview != null) {
			baseview.addView(overlay);
		}
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

	protected abstract void find();

	protected abstract void init();

	public abstract void click(View v);

}
