package com.pingtech.hgqw.zxing.utils;

import java.io.UnsupportedEncodingException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.readcard.service.EwmService;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.DeviceUtils.CONNECTTYPE;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.ScanDataUtil;
import com.pingtech.hgqw.zxing.entity.MsTdc;
import com.soft.interfaces.BarcodeEngine;
import com.soft.interfaces.PA568PowerControl;

public class ScanUtils {

	private static BarcodeEngine be = new BarcodeEngine();
	private static int mobileType = 1;// 1为盛本机型PE43，2为盛本PA710，3为西姆通机型
	static CONNECTTYPE connectType = CONNECTTYPE.UNKNOWN;

	private static Handler pa8Handler = null;

	private static class Pa8Handler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageEntity.TOAST:
				HgqwToast.toast((String) msg.obj);
				break;
			case ReadService.READ_TYPE_EWM:
				resultBusiness((String) msg.obj);
				break;
			default:
				break;
			}
		}

	}

	public static void pa8Ewm(Handler hander) {

		// pa8Handler = new Pa8Handler();
		EwmService.getInstent().setHandler(hander);
		EwmService.getInstent().readByHand(false);
	}

	public static MsTdc resultBusiness(String result) {
		if (StringUtils.isEmpty(result)) {
			result = "";
		}
		MsTdc msTdc = null;
		try {
			msTdc = ScanDataUtil.getMsTdcForAll(result);
		} catch (Exception e) {
			e.printStackTrace();
			msTdc = null;
			HgqwToast.toast("二维码格式不合法：" + result);

		}
		return msTdc;
	}

	/**
	 * 初始化二维码扫描
	 * 
	 * @param context
	 * @param handler
	 */
	public static void initScanBarCode(Context context, Handler handler) {
		mHandler = handler;
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_CFON640:// 第四代设备的扫描二维码模块需要提前初始化
			initCfonBarCode(context);
			break;
		case DeviceUtils.DEVICE_MODEL_PA9:// 第三代设备的扫描二维码模块需要提前初始化
			initPA9BarCode();
			break;
		default:
			break;
		}
	}

	/**
	 * 关闭二维码扫描
	 * 
	 * @param context
	 */
	public static void closeScanBarCode(Context context) {
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_CFON640:
			closeCfonBarCode(context);
			break;
		case DeviceUtils.DEVICE_MODEL_PA9:
			closePA9BarCode();
			break;
		case DeviceUtils.DEVICE_MODEL_PA8:
			EwmService.getInstent().closeEwmBroadcast();
			break;
		default:
			break;
		}
	}

	public static void initPA9BarCode() {
		connectType = DeviceUtils.deviceTypeVerify();
		if (connectType == CONNECTTYPE.PA9SIM)
			PA568PowerControl.powerOn();

		if (be.OpenPort2("/dev/ttyHSL0", '1',
				"/sys/devices/platform/msm_serial_hsl.0/uart_switch") < 0) {
			if (be.OpenPort4("/dev/ttyHSL2") < 0) {
				// ((Activity) mContext).finish();
			} else {
				mobileType = 5;
			}
		} else {
			mobileType = 3;
		}

		be.SetScanMode();
	}

	public static void closePA9BarCode() {
		if (mobileType == 3) {
			be.ClosePort2();
		} else if (mobileType == 5) {
			be.ClosePort4();
		} else {
			be.ClosePort();
		}
		if (connectType == CONNECTTYPE.PA9SIM) {
			PA568PowerControl.powerOff();
		}
	}

	public static void readByPA9(Handler mHandler) {
		// readCardBtn.setEnabled(false);
		byte[] b = new byte[1024];
		be.ScanBegin(0);
		for (int i = 0; i < 2; i++) {
			be.ReceiveData(b);
			if (b[0] != 0)
				break;
			else
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		be.ScanEnd();
		// readCardBtn.setEnabled(true);
		String result = null;
		try {
			result = new String(b, "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (TextUtils.isEmpty(result)) {
			return;
		}
		// 滴一声
		BaseApplication.soundManager.onPlaySoundNoVb(4, 0);

		MsTdc msTdc = ScanUtils.resultBusiness(result);
		if (msTdc == null) {
			Log.i("TAG", "msTdc == null");
			return;
		}
		CardInfo cardInfo = new CardInfo();
		cardInfo.setCardType(ReadService.READ_TYPE_EWM);
		cardInfo.setMsTdc(msTdc);
		Log.i("TAG", "handler != null , msTdc.getZjhm()=" + msTdc.getZjhm());
		mHandler.obtainMessage(ReadService.READ_TYPE_EWM, cardInfo)
				.sendToTarget();
		/*
		 * Message msg = new Message(); msg.obj = result;
		 * mHandler.sendMessage(msg);
		 */
	}

	public static void readByPA9View(Handler mHandler, View mViewBtn,
			View mViewTex) {
		// readCardBtn.setEnabled(false);
		mViewBtn.setEnabled(false);
		mViewTex.setEnabled(false);
		byte[] b = new byte[1024];
		be.ScanBegin(0);
		for (int i = 0; i < 2; i++) {
			be.ReceiveData(b);
			if (b[0] != 0)
				break;
			else
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		be.ScanEnd();
		// readCardBtn.setEnabled(true);
		mViewBtn.setEnabled(true);
		mViewTex.setEnabled(true);
		String result = null;
		try {
			result = new String(b, "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (TextUtils.isEmpty(result)) {
			return;
		}
		// 滴一声
		BaseApplication.soundManager.onPlaySoundNoVb(4, 0);

		MsTdc msTdc = ScanUtils.resultBusiness(result);
		if (msTdc == null) {
			Log.i("TAG", "msTdc == null");
			return;
		}
		CardInfo cardInfo = new CardInfo();
		cardInfo.setCardType(ReadService.READ_TYPE_EWM);
		cardInfo.setMsTdc(msTdc);
		mViewBtn.setEnabled(true);
		mViewTex.setEnabled(true);
		Log.i("TAG", "handler != null , msTdc.getZjhm()=" + msTdc.getZjhm());
		mHandler.obtainMessage(ReadService.READ_TYPE_EWM, cardInfo)
				.sendToTarget();
	}

	private static Handler mHandler;

	public static void initCfonBarCode(Context context) {
		// 打开扫描仪
		Intent scanneronoffIntent = new Intent(
				"com.android.server.scannerservice.onoff");
		scanneronoffIntent.putExtra("scanneronoff", 1);
		context.sendBroadcast(scanneronoffIntent);

		IntentFilter filter = new IntentFilter(
				"com.android.server.scannerservice.broadcast");
		context.registerReceiver(receiver, filter);
	}

	public static void closeCfonBarCode(Context context) {
		// 关闭扫描仪
		Intent scanneronoffIntent = new Intent(
				"com.android.server.scannerservice.onoff");
		scanneronoffIntent.putExtra("scanneronoff", 0);
		context.sendBroadcast(scanneronoffIntent);

		if (receiver != null) {
			context.unregisterReceiver(receiver);
		}
	}

	private static BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"com.android.server.scannerservice.broadcast")) {
				String result = intent.getExtras().getString("scannerdata");
				if (TextUtils.isEmpty(result)) {
					return;
				}
				// 滴一声
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);

				MsTdc msTdc = ScanUtils.resultBusiness(result);
				if (msTdc == null) {
					Log.i("TAG", "msTdc == null");
					return;
				}
				CardInfo cardInfo = new CardInfo();
				cardInfo.setCardType(ReadService.READ_TYPE_EWM);
				cardInfo.setMsTdc(msTdc);
				mHandler.obtainMessage(ReadService.READ_TYPE_EWM, cardInfo)
						.sendToTarget();
			}
		}
	};

}
