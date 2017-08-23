package com.pingtech.hgqw.module.exception.activity;

import android.os.Bundle;
import android.util.Pair;
import android.view.WindowManager;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.Log;

/**
 * 登记异常信息界面的activity类
 * */
public class ExceptioninfoCl extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "ExceptioninfoCl";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.exceptioninfo_cl);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Log.i(TAG, "onCreate()");

	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {

	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {

	}

}
