package com.pingtech.hgqw.readcard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.entity.MsTdc;
import com.pingtech.hgqw.zxing.utils.ScanUtils;

/**
 * 安软二维码扫描
 * 
 * @author root
 * 
 */
public class EwmService {
	private static EwmService instent = null;

	private static String EWM_ACTION = "com.android.server.scannerservice.startAndStopScanner";

	private static String EWM_FILTER_ACTION = "com.android.server.scannerservice.broadcast";

	private Handler handler = null;

	private BroadcastReceiver receiver = null;

	private EwmService() {
	}

	public static EwmService getInstent() {
		if (instent == null) {
			instent = new EwmService();
		}
		return instent;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void readByHand(boolean fhysj) {
		// 取消上次广播
		if (receiver != null) {
			BaseApplication.instent.unregisterReceiver(receiver);
		}
		// 监听广播
		setEwm();// 给底层传输指令，设置读出二维码数据后不自动填充输入框
		broadcastReceiver();
		closeEwmBroadcast();
		openEwmBroadcast();
	}

	public static final int OUTPUT_DIRECT = 0;
	public static final int OUTPUT_EMU_KEY = 1;
	public static final int OUTPUT_CLIPBOARD = 2;
	public static final String SCN_CUST_DB_OUTPUT_MODE = "SCANNER_OUTPUT_MODE";

	/**
	 * 给底层传输指令，设置读出二维码数据后不自动填充输入框
	 */
	private void setEwm() {
		Settings.System.putInt(BaseApplication.instent.getContentResolver(),
				"SCANNER_OUTPUT_MODE", OUTPUT_CLIPBOARD);
		Intent intent = new Intent(
				"com.android.server.scannerservice.settingchange");
		BaseApplication.instent.sendBroadcast(intent);
	}

	private void openEwmBroadcast() {
		Intent intent = new Intent();
		intent.setAction(EWM_ACTION);
		// 1打开扫描头，0关闭扫描头
		intent.putExtra("operate", 1);
		BaseApplication.instent.sendBroadcast(intent);
	}

	public void closeEwmBroadcast() {
		Intent intent = new Intent();
		intent.setAction(EWM_ACTION);
		// 1打开扫描头，0关闭扫描头
		intent.putExtra("operate", 0);
		BaseApplication.instent.sendBroadcast(intent);
	}

	private void broadcastReceiver() {
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// closeEwmBroadcast();
				String action = intent.getAction();
				if (StringUtils.isNullOrEmpty(action)) {
					return;
				}
				if (action.equals(EWM_FILTER_ACTION)) {
					String result = intent.getStringExtra("scannerdata");
					if (StringUtils.isEmpty(result)) {
						HgqwToast.toast("数据读取失败，请调整位置重试！");
						// closeEwmBroadcast();
						return;
					}
					result = result.trim();
					Log.i("TAG", "*****result=" + result);
					MsTdc msTdc = ScanUtils.resultBusiness(result);
					if (msTdc == null) {
						Log.i("TAG", "msTdc == null");
						return;
					}
					CardInfo cardInfo = new CardInfo();
					cardInfo.setCardType(ReadService.READ_TYPE_EWM);
					cardInfo.setMsTdc(msTdc);
					if (handler != null) {
						Log.i("TAG", "handler != null , msTdc.getZjhm()="
								+ msTdc.getZjhm());
						handler.obtainMessage(ReadService.READ_TYPE_EWM,
								cardInfo).sendToTarget();
						handler = null;
					}
					BaseApplication.instent.unregisterReceiver(receiver);
					receiver = null;
				}
			}
		};
		IntentFilter filter = new IntentFilter(EWM_FILTER_ACTION);
		BaseApplication.instent.registerReceiver(receiver, filter);
	}
}
