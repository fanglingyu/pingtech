package com.pingtech.hgqw.module.bindplace.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.pingtech.R;
import com.pingtech.hgqw.activity.InspectPlace;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindplace.action.BindPlaceAction;
import com.pingtech.hgqw.module.bindplace.entity.BindPlace;
import com.pingtech.hgqw.module.bindplace.utils.PullXmlBindPlace;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.CaptureActivity;
import com.pingtech.hgqw.zxing.Constant;
import com.pingtech.hgqw.zxing.entity.MsTdc;
import com.pingtech.hgqw.zxing.utils.ScanUtils;

/**
 * 读卡模块，包括读IC卡、二代证，读IC卡时，读到数据就返回；
 * 读二代证时，读到数据后发送到后台请求验证数据，并且显示返回结果（含日常巡检和刷卡登记），同时重新启动读卡流程
 * 如果进入异常模块等其他界面，暂停二代证刷卡流程，返回后又重启。启动二代证阅读器时，会有请稍后的提示框
 * */
public class BindPlaceReadcard extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "BindPlaceReadcardActivity";

	/** 读IC卡，比如船舶绑定 */
	public static final int READCARD_TYPE_IC_CARD = 0;

	/** 读ID卡，比如刷卡登记 */
	public static final int READCARD_TYPE_ID_CARD = 1;

	/** 同时读IC、ID卡，比如巡查巡检 */
	public static final int READCARD_TYPE_ICID_CARD = 2;

	/** 刷信息钉情况下发起的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_BINDPLACE = 0;

	/** 进入二维码扫描 */
	private static final int STARTACTIVITY_FOR_ZXING = 3;

	private String xjddName = "未绑定";

	/** 手动选择巡检地点：0码头、1泊位、2区域 */
	private int placeType = -1;

	/** 手动选择巡检地点：地点ID */
	private String placeId = null;

	private int cardType = 0;

	private String idcardnumber_s = "";

	private String defaultickey = "";

	private String icinput_s = "";

	private Bitmap netWorkImage = null;

	/** 证件号码或标签号码输入框控件，便于清空 */
	private EditText input;

	/** 二维码扫描出来的数据 */
	private String zxingInfo = "";

	/** 是否正在执行二维码相关操作 */
	private boolean doingZxing = false;

	/** 将二维码扫描的数据显示文本框，并提交验证 */
	private Handler zxingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (StringUtils.isEmpty(zxingInfo)) {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.cardnum_empty)).show();
				doingZxing = false;
				return;
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				doingZxing = false;
				return;
			}
			idcardnumber_s = zxingInfo;
			onReadComplete(false, true);

		}

	};

	/** 处理“确认”按钮消息，也就是输入卡号完毕后，如果是绑定船舶时刷电子标签，带上标签号直接返回，其他情况开始向后台发起请求 */
	private OnClickListener clickOKButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int res = v.getId();
			if (res == R.id.btnok) {
				readSuccess();
			}
		}
	};

	private void readSuccess() {
		String num = input.getText().toString();
		if (StringUtils.isEmpty(num)) {
			HgqwToast.getToastView(getApplicationContext(), getString(R.string.cardnum_empty)).show();
			return;
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			return;
		}
		idcardnumber_s = num;
		onReadComplete(false, true);

	}

	/** 监听输入框输入，接收IC卡刷卡器刷卡结果 */
	private EditText.OnKeyListener keyListener = new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_UP)) {
				String num = input.getText().toString();
				if (num.length() != 0) {
					if (cardType != READCARD_TYPE_ID_CARD) {
						Intent data = null;
						data = new Intent();
						data.putExtra("cardNumber", num);
						setResult(RESULT_OK, data);
						finish();
					} else {
						if (progressDialog != null && progressDialog.isShowing()) {
							return true;
						}
						idcardnumber_s = num;
						onReadComplete(false, true);
					}
				}
				return true;
			}
			return false;
		}
	};

	/** 监听button的key消息，刷IC卡时，button不响应 */
	private Button.OnKeyListener btnKeyListener = new Button.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Log.i(TAG, "Button.OnKeyListener onKey():" + keyCode);
			return false;
		}
	};

	private ProgressDialog progressDialog = null;

	private ProgressDialog readerInitProgressDialog = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.bindplace_readcard);
		input = (EditText) findViewById(R.id.cardtext);
		setXjddTextView();
		Button btn = ((Button) findViewById(R.id.btnok));
		btn.setOnClickListener(clickOKButtonListener);
		btn.setOnKeyListener(btnKeyListener);
		input.setOnKeyListener(keyListener);
		setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindPlace));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown:" + keyCode);
		if ((progressDialog == null || !progressDialog.isShowing()) && (readerInitProgressDialog == null)) {
			if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
				if (icinput_s == null) {
					icinput_s = (keyCode - KeyEvent.KEYCODE_0) + "";
				} else {
					icinput_s = icinput_s + (keyCode - KeyEvent.KEYCODE_0);
				}
			} else if (keyCode == KeyEvent.KEYCODE_ENTER) {
				if ((icinput_s != null) && (icinput_s.length() != 0)) {
					idcardnumber_s = icinput_s;
					onReadComplete(false, false);
				}
				return true;
			} else {
				icinput_s = "";
			}
		} else {
			icinput_s = "";
		}
		return super.onKeyDown(keyCode, event);
	}

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
			ScanUtils.readByPA9(readCardHander);
			return;
		default:
			// 二维码扫描结果对象初始化
			Intent intent = new Intent(BindPlaceReadcard.this, CaptureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, STARTACTIVITY_FOR_ZXING);
			break;
		}
		
	}

	/**
	 * 二代证阅读器读卡完毕后，或者手动输入完毕后，开始发起验证请求，
	 * 
	 * @param idcard
	 *            是否是二代证，如果是，需要显示照片
	 * @param manual_input
	 *            是否手动输入
	 * */
	private void onReadComplete(boolean idcard, boolean manual_input) {
		input = (EditText) findViewById(R.id.cardtext);
		if (input != null) {
			input.setText(idcardnumber_s);
		}
		// 电子标签
		String url = "getBaseInfoByCard";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cardNumber", idcardnumber_s));
		params.add(new BasicNameValuePair("type", placeType + ""));
		params.add(new BasicNameValuePair("id", placeId));
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
		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_BINDPLACE);
		} else {
			OffLineManager.request(BindPlaceReadcard.this, new BindPlaceAction(), url, NVPairTOMap.nameValuePairTOMap(params),
					HTTPREQUEST_TYPE_FOR_BINDPLACE);
		}

	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result:" + (str != null));

		if (HTTPREQUEST_TYPE_FOR_BINDPLACE == httpRequestType) {
			if (str != null) {
				BindPlace bindPlace = PullXmlBindPlace.parseXMLData(str, getApplicationContext());
				if (bindPlace.isResult()) {
					toDetail() ;
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (bindPlace.getInfo() != null && !"".equals(bindPlace.getInfo())) {
						HgqwToast.getToastView(getApplicationContext(), bindPlace.getInfo()).show();
					} else {
						HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
					}
				}
				doingZxing = false;
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				doingZxing = false;
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
			}
		}
	}

	private void toDetail() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		Intent intent = new Intent();
		intent.putExtra("BINDPLACE", "0");
		intent.putExtra("isDownload", true);
		intent.setClass(getApplicationContext(), BindPlaceDetail.class);
		startActivity(intent);
		doingZxing = false;
		finish();
	}

	/**
	 * @方法名：setXjddTextView
	 * @功能说明：巡查巡检-日常巡检-巡检地点标识
	 * @author liums
	 * @date 2013-5-9 下午4:41:39
	 */
	private void setXjddTextView() {
		HashMap<String, Object> Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (Binddata != null) {
			xjddName = (String) Binddata.get("cbzwm");
		} else {
			xjddName = SystemSetting.xunJianName;
			if (xjddName == null) {
				xjddName = "未绑定";
			} else {
				if ("bw".equals(SystemSetting.xunJianType)) {
					xjddName = SystemSetting.xunJianMTname + "，" + SystemSetting.xunJianName;
				}
			}
		}

	}

	/**
	 * 
	 * @方法名：iDReaderButtonClick
	 * @功能说明：启动身份证读卡器
	 * @author liums
	 * @date 2013-3-27 下午6:26:25
	 * @param v
	 */

	public void iDReaderButtonClick(View v) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), InspectPlace.class);
		startActivityForResult(intent, FlagManagers.INSPECT_PLACE);

	}

	/**
	 * 从其他模块返回，如果是手动选择人员，直接发起验证流程；如果是绑定船舶，直接返回；其他情况都需要重新起动二代证阅读器
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FlagManagers.INSPECT_PLACE:
			if (data == null) {
				break;
			}
			placeType = -1;
			placeId = "";
			placeType = data.getIntExtra("type", -1);
			placeId = data.getStringExtra("id");
			switch (placeType) {
			case 0:// 码头
				onReadComplete(false, false);
				break;
			case 1:// 泊位
				onReadComplete(false, false);
				break;
			case 2:// 区域
				onReadComplete(false, false);
				break;

			default:
				break;
			}
			return;
		case STARTACTIVITY_FOR_ZXING:
			if (resultCode == RESULT_OK) {
				// 接收扫描二维码返回来的数据
				boolean isback = data.getBooleanExtra(Constant.ZXING_ISBACK, false);
				if (!isback) {// 判断是不是从按返回按钮进入该界面
					zxingInfo = data.getStringExtra(Constant.ZXING_DATA);
					if (com.pingtech.hgqw.utils.StringUtils.isEmpty(zxingInfo)) {
						zxingInfo = "";
					}
					zxingHandler.sendEmptyMessage(0);
				}
				doingZxing = false;
			}
			break;
		}
	}

	/* 读卡程序开始 */
	private ReadService readService;

	private ReadCardHandler readCardHander;

	private CardInfo cardInfo;

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		readCardHander = new ReadCardHandler();
		ScanUtils.initScanBarCode(getApplicationContext(), readCardHander);
		readInit();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		clickOKButtonListener = null;
		if (netWorkImage != null) {
			netWorkImage.recycle();
			netWorkImage = null;
		}
		ScanUtils.closeScanBarCode(getApplicationContext());
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		readService.close();
		super.onPause();
	}

	private void readInit() {
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_MIMA:
			readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_DEFAULT_ICKEY);
			break;
		case DeviceUtils.DEVICE_MODEL_M:
		case DeviceUtils.DEVICE_MODEL_CFON640:
		case DeviceUtils.DEVICE_MODEL_PA8:
		case DeviceUtils.DEVICE_MODEL_PA9:
			readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_DEFAULT_ICKEY);
			break;
		default:
			break;
		}
		readService.init();
	}

	public void readChange(View v) {
		switch (v.getId()) {
		case R.id.btn_ic_id_change:
//			readService.readChange();
			break;
		default:
			break;
		}
	}

	// 读卡Handler类
	class ReadCardHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ReadService.READ_TYPE_EWM:
				cardInfo = (CardInfo) msg.obj;
				if(cardInfo==null){
					return;
				}
				MsTdc msTdc = cardInfo.getMsTdc();
				if (msTdc != null) {
					zxingInfo = msTdc.getZjhm();
					if (StringUtils.isNotEmpty(zxingInfo)) {
						zxingHandler.sendEmptyMessage(0);
					}
				}
				break;
			case MessageEntity.TOAST:
				HgqwToast.getToastView(getApplicationContext(), (String) msg.obj).show();
				break;
			case ReadService.READ_TYPE_DEFAULT_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				idcardnumber_s = cardInfo.getIckey();
				defaultickey = cardInfo.getDefaultIckey();
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				input.setText(defaultickey);
				readSuccess();
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
				break;
			case ReadService.READ_TYPE_ID:
				break;
			case ReadService.READ_TYPE_ICKEY:
				break;
			default:
				break;
			}
		}
	}

	/* 读卡程序结束 */

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		HgqwToast.getToastView(getApplicationContext(), getString(R.string.no_web_cannot_unbind_place)).show();
	}
}
