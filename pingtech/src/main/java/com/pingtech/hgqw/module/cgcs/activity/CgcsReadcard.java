package com.pingtech.hgqw.module.cgcs.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android_serialport_api.ParseSFZAPI.People;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.cgcs.entity.Cgcs;
import com.pingtech.hgqw.module.cgcs.utils.PullXmlCgcs;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.CaptureActivity;
import com.pingtech.hgqw.zxing.Constant;
import com.pingtech.hgqw.zxing.entity.MsTdc;
import com.pingtech.hgqw.zxing.utils.ScanUtils;

/**
 * 
 * 
 * 类描述：查岗查哨读卡页面
 * 
 * <p>
 * Title: 江海港边检勤务-移动管理系统-CgcsReadcard.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author 娄高伟
 * @version 1.0
 * @date 2013-8-16 下午3:11:09
 */
public class CgcsReadcard extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "CgcsReadcard";

	private Cgcs cgcs = null;

	/**
	 * 读IC卡，比如船舶绑定
	 */
	public static final int READCARD_TYPE_IC_CARD = 0;

	/**
	 * 读ID卡，比如刷卡登记
	 */
	public static final int READCARD_TYPE_ID_CARD = 1;

	/** 是否士兵证，也就是是否是查岗查哨 */
	boolean sbz = false;

	/**
	 * 同时读IC、ID卡，比如巡查巡检
	 */
	public static final int READCARD_TYPE_ICID_CARD = 2;

	public static final int HTTPREQUEST_TYPE_FOR_NORMAL_CGCS = 0;

	/**
	 * 进入二维码扫描
	 */
	private static final int STARTACTIVITY_FOR_ZXING = 3;

	/**
	 * 音频播放池
	 */
	private SoundPool sp;

	private HashMap<Integer, Integer> hm;

	private ProgressDialog progressDialog = null;

	/**
	 * 卡号
	 */
	private String idcardnumber = "";

	/**
	 * IC卡默认卡编号
	 */
	private String defaultickey = "";

	/**
	 * 是否允许刷卡，false刷卡后不处理，true允许刷卡
	 */
	// private boolean flagRegister = true;
	/* 是否有证件信息，如果没有，需要进入保存证件信息界面 */
	private float volume;

	/* 读卡程序开始 */
	private ReadService readService;

	private ReadCardHandler readCardHander;

	private CardInfo cardInfo;

	/**
	 * 震动设备
	 */
	private Vibrator vibrator = null;

	/** 证件号码或标签号码输入框控件，便于清空 */
	private EditText input;

	/**
	 * 二维码扫描出来的数据
	 */
	private String zxingInfo = "";

	/**
	 * 是否正在执行二维码相关操作
	 */
	private boolean doingZxing = false;

	/**
	 * 二维码扫描结果
	 */
	private MsTdc msTdc;

	/**
	 * 将二维码扫描的数据显示文本框，并提交验证
	 */
	private Handler zxingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// super.handleMessage(msg);
			if (StringUtils.isEmpty(zxingInfo)) {
				Log.i("TAG", "StringUtils.isEmpty(zxingInfo)");
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.cardnum_empty)).show();
				doingZxing = false;
				// 初始化二维码扫描结果对象
				msTdc = null;
				return;
			}
			idcardnumber = zxingInfo;
			// Log.i("TAG", "idcardnumber = " + idcardnumber);
			onReadComplete(false, true);

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.cgcs_readcard);
		setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.chagangchashao));
		Button btn = ((Button) findViewById(R.id.btnok));
		btn.setOnClickListener(clickOKButtonListener);
	}

	@Override
	protected void onResume() {
		cgcs = null;
		idcardnumber = null;
		input = (EditText) findViewById(R.id.cardtext);
		if (input != null) {
			input.setText("");
		}
		readCardHander = new ReadCardHandler();
		ScanUtils.initScanBarCode(getApplicationContext(), readCardHander);
		readInit();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		zxingHandler = null;
		if (readService != null) {
			readService.close();
			readService = null;
		}
		if (cgcsToast != null) {
			cgcsToast.cancel();
			cgcsToast = null;
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		clickOKButtonListener = null;
		ScanUtils.closeScanBarCode(getApplicationContext());
		super.onDestroy();
	}

	private OnClickListener clickOKButtonListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			String num = input.getText().toString();
			if (StringUtils.isEmpty(num)) {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.cardnum_empty)).show();
				return;
			}
			idcardnumber = num;
			onReadComplete(false, true);
		}
	};

	/**
	 * @方法名：zxingScanMethod
	 * @功能说明：扫描二维码方法
	 * @author zhaotf
	 * @date 2013-10-31 上午10:15:15
	 * @param v
	 */

	public void zxingScanMethod(View v) {
		if (doingZxing) {
			return;
		}
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_CFON640:
			Intent startIntent = new Intent(
					"android.intent.action.SCANNER_BUTTON_DOWN", null);
			sendOrderedBroadcast(startIntent, null);
			return;
		case DeviceUtils.DEVICE_MODEL_PA8:
			ScanUtils.pa8Ewm(readCardHander);
			return;
		case DeviceUtils.DEVICE_MODEL_PA9:
			Log.i("TAG", "zxingScanMethod");
			// input.setEnabled(false);
			// input.setInputType(InputType.TYPE_NULL);
			// input.setFocusable(false);
			ScanUtils.readByPA9(readCardHander);
			return;
		default:
			// 二维码扫描结果对象初始化
			msTdc = null;
			Intent intent = new Intent(CgcsReadcard.this, CaptureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, STARTACTIVITY_FOR_ZXING);
			break;
		}

	}

	/**
	 * 
	 * @方法名：onReadComplete
	 * @功能说明：二代证阅读器读卡完毕后，或者手动输入完毕后，开始发起验证请求，
	 * @author 娄高伟
	 * @date 2013-8-16 下午3:09:51
	 * @param idcard
	 * @param manual_input
	 */
	private void onReadComplete(boolean idcard, boolean manual_input) {
		input = (EditText) findViewById(R.id.cardtext);
		if (input != null) {
			input.setText("");
			input.setText(idcardnumber);
		}
		// 刷卡
		String url = "sendSwipeRecord";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type", SystemSetting.xunJianType));
		params.add(new BasicNameValuePair("ddID", SystemSetting.xunJianId));
		params.add(new BasicNameValuePair("cardNumber", idcardnumber));
		params.add(new BasicNameValuePair("defaultickey", defaultickey));
		params.add(new BasicNameValuePair("xjlx", "06"));
		if (manual_input) {
			params.add(new BasicNameValuePair("sfsk", "1"));
		} else {
			params.add(new BasicNameValuePair("sfsk", "0"));
		}
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		if (progressDialog != null) {
			doingZxing = false;
			return;
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.Validing));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_NORMAL_CGCS);
		// if (BaseApplication.instent.getWebState()) {
		// NetWorkManager.request(this, url, params,
		// HTTPREQUEST_TYPE_FOR_NORMAL_CGCS);
		// } else {
		// OffLineManager.request(this, new XunJianAction(), url,
		// NVPairTOMap.nameValuePairTOMap(params),
		// HTTPREQUEST_TYPE_FOR_NORMAL_CGCS);
		// }
	}

	Toast cgcsToast = null;

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result:" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (HTTPREQUEST_TYPE_FOR_NORMAL_CGCS == httpRequestType) {
			if (str == null) {
				BaseApplication.soundManager.onPlaySound(3, 0);
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
				doingZxing = false;
				// 二维码扫描结果对象初始化
				msTdc = null;
				return;
			}
			cgcs = PullXmlCgcs.onParseCgcsXMLData(str);

			// 解析XML
			if (!cgcs.isResult()) {
				BaseApplication.soundManager.onPlaySound(3, 0);
				HgqwToast.toast(cgcs.getInfo());
				doingZxing = false;
				// 二维码扫描结果对象初始化
				msTdc = null;
				return;
			}
			Intent intent = new Intent(this, CgcsDetail.class);
			if (cgcs != null) {
				cgcs.setZjhm(idcardnumber);
			}
			// 二维码扫描结果对象初始化
			msTdc = null;
			doingZxing = false;
			BaseApplication.soundManager.onPlaySound(2, 0);
			Bundle bundle = new Bundle();
			bundle.putSerializable("cgcs", cgcs);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	private void readInit() {
		View v = findViewById(R.id.btn_ic_id_change);
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_MIMA:
			if (v != null) {
				v.setVisibility(View.VISIBLE);
			}
			readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_DEFAULT_AND_ICKEY);
			break;
		case DeviceUtils.DEVICE_MODEL_M:
		case DeviceUtils.DEVICE_MODEL_CFON640:
		case DeviceUtils.DEVICE_MODEL_PA8:
		case DeviceUtils.DEVICE_MODEL_PA9:
			if (v != null) {
				v.setVisibility(View.GONE);
			}
			readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_ID_IC);
			break;
		case DeviceUtils.DEVICE_MODEL_SDK:
			return;
		default:
			break;
		}
		readService.init();
	}

	/**
	 * 
	 * 
	 * 类描述： 读卡Handler类
	 * 
	 * <p>
	 * Title: 江海港边检勤务-移动管理系统-CgcsReadcard.java
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2012
	 * </p>
	 * <p>
	 * Company: 品恩科技
	 * </p>
	 * 
	 * @author 娄高伟
	 * @version 1.0
	 * @date 2013-8-16 下午3:10:30
	 */
	class ReadCardHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ReadService.READ_TYPE_EWM:
				// input.setFocusable(true);
				if (zxingHandler == null) {
					return;
				}
				Log.i(TAG, "(CardInfo) msg.obj=");
				cardInfo = (CardInfo) msg.obj;
				if (cardInfo == null) {
					return;
				}
				msTdc = cardInfo.getMsTdc();

				if (msTdc != null) {
					zxingInfo = msTdc.getZjhm();
					// Log.i("TAG", "zxingInfo=" + zxingInfo);
					if (StringUtils.isNotEmpty(zxingInfo)) {
						zxingHandler.sendEmptyMessage(0);
					}
				}
				break;
			case MessageEntity.TOAST:
				HgqwToast.getToastView(getApplicationContext(), (String) msg.obj).show();
				break;
			case ReadService.READ_TYPE_DEFAULT_ICKEY:
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				idcardnumber = cardInfo.getIckey();
				defaultickey = cardInfo.getDefaultIckey();
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				onReadComplete(false, false);
				break;
			case ReadService.READ_TYPE_ID:
				cardInfo = (CardInfo) msg.obj;
				People people = cardInfo.getPeople();
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				readIdSuccess(people);
				Log.i("身份证读卡结果", people.getPeopleIDCode());
				break;
			case ReadService.READ_TYPE_ICKEY:
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 
	 * @方法名：readIdSuccess
	 * @功能说明：读身份证成功
	 * @author 娄高伟
	 * @date 2013-8-16 下午3:10:44
	 * @param people
	 */
	private void readIdSuccess(People people) {
		idcardnumber = (people.getPeopleIDCode() == null ? "" : people.getPeopleIDCode());
		onReadComplete(true, false);
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ic_id_change:
//			readService.readChange();
			break;
		default:
			break;
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (offLineRequestType == HTTPREQUEST_TYPE_FOR_NORMAL_CGCS) {
			if (res.second != null) {
				onHttpResult(res.second.toString(), offLineRequestType);
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				doingZxing = false;
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.filed_validate_soldier)).show();
			}
			// 二维码扫描结果对象初始化
			msTdc = null;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// 接收扫描二维码返回来的数据
			boolean isback = data.getBooleanExtra(Constant.ZXING_ISBACK, false);
			if (!isback) {// 判断是不是从按返回按钮进入该界面
				msTdc = (MsTdc) data.getSerializableExtra(Constant.ZXING_DATA);
				if (msTdc != null) {
					zxingInfo = msTdc.getZjhm();
					if (com.pingtech.hgqw.utils.StringUtils.isEmpty(zxingInfo)) {
						zxingInfo = "";
					}
				} else {
					zxingInfo = "";
				}
				zxingHandler.sendEmptyMessage(0);
			}
			doingZxing = false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Boolean isVolumnKey = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_CFON640) {
				Intent startIntent = new Intent(
						"android.intent.action.SCANNER_BUTTON_DOWN", null);
				sendOrderedBroadcast(startIntent, null);
				isVolumnKey = true;
			} else if (DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA8) {
				ScanUtils.pa8Ewm(readCardHander);
				isVolumnKey = true;
			}else if(DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA9){
				ScanUtils.readByPA9(readCardHander);
				isVolumnKey = true;
			}
			break;
		default:
			break;
		}

		if (isVolumnKey) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
