package com.pingtech.hgqw.module.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;

import com.pingtech.R;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 刷卡登陆时，弹出的刷卡提示界面的activity类
 * */
public class LoginByCard extends Activity {
	private static final String TAG = "LoginByCard";

	/** 接收IC卡卡号 */
	private String cardNumber;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loginbycard);
		cardNumber = "";
	}

	/**
	 * 接收IC卡读卡器刷卡输入的数据，并返回给上级activity
	 * */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode + ",action=" + event.getAction());
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
					cardNumber = cardNumber + (keyCode - KeyEvent.KEYCODE_0);
				} else if (keyCode == KeyEvent.KEYCODE_ENTER) {
					Log.i(TAG, "dispatchKeyEvent,finish cardnum:" + cardNumber);
					Intent data = null;
					data = new Intent();
					data.putExtra("cardNumber", cardNumber);
					setResult(RESULT_OK, data);
					finish();
				}
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	/* 读卡程序开始 */
	private ReadService readService;
	private ReadCardHandler readCardHander;

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		readInit();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		readService.close();
		super.onPause();
	}
	private void readInit() {
		readCardHander = new ReadCardHandler();
		readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_ID_IC);
		readService.init();
	}

	/**
	 * 读卡Handler类
	 * 
	 * @author Administrator
	 * 
	 */
	class ReadCardHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MessageEntity.TOAST:
				HgqwToast.getToastView(getApplicationContext(), (String) msg.obj).show();
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
			case ReadService.READ_TYPE_ICKEY:
				CardInfo cardInfo = (CardInfo) msg.obj;
				Intent data = null;
				data = new Intent();
				data.putExtra("cardNumber", cardInfo.getIckey());
				setResult(RESULT_OK, data);
				finish();
				break;
			default:
				break;
			}
		}
	}
	/* 读卡程序结束 */
}