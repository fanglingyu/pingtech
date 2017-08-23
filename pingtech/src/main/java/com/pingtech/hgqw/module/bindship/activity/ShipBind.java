package com.pingtech.hgqw.module.bindship.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.entity.ShipBindInfo;
import com.pingtech.hgqw.module.bindship.utils.PullXmlShipBind;
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

public class ShipBind extends MyActivity implements OnHttpResult {
	private static final String TAG = "ShipBind";

	/**
	 * 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态-船舶绑定、0201梯口管理-船舶绑定、0101
	 * 卡口管理-船舶绑定、0301巡查巡检-船舶绑定
	 */

	/** 发起获取船舶列表的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GETLIST = 4;

	private EditText ship_bind_card_number;

	private boolean handleFlag = false;

	private String cardNumber;// IC卡号

	private ProgressDialog progressDialog = null;

	private int bindType;

	private int fromType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;

	private String from;

	/**
	 * 进入二维码扫描
	 */
	private static final int STARTACTIVITY_FOR_ZXING = 1;

	/**
	 * 二维码扫描出来的数据
	 */
	private String zxingInfo = "";

	/**
	 * 是否正在执行二维码相关操作
	 */
	private boolean doingZxing = false;

	/**
	 * 将二维码扫描的数据显示文本框，并提交验证
	 */
	private Handler zxingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (StringUtils.isEmpty(zxingInfo)) {
				HgqwToast.makeText(getApplicationContext(), "请刷卡或输入电子标签号！", HgqwToast.LENGTH_SHORT).show();
				doingZxing = false;
				return;
			}
			cardNumber = zxingInfo;
			handleFlag = true;
			onLoadShipList();

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		super.onCreate(savedInstanceState, R.layout.ship_bind_readcard);
		Log.i("日志", getClass().getName() + ":onCreate");
		this.setCustomTitle();
		this.pingtechFindViewById();
	}

	private void readCardSuccess(String result) {
		this.cardNumber = result;
		ship_bind_card_number.setText(cardNumber);
		BaseApplication.soundManager.onPlaySoundNoVb(4, 0);
		onLoadShipList();

	}

	/** 开始获取船舶列表信息 */
	private void onLoadShipList() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (GlobalFlags.BINDSHIP_FROM_KAKOUMANAGER.equals(from)) {
			fromType = GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER;
		} else if (GlobalFlags.BINDSHIP_FROM_TIKOUMANAGER.equals(from)) {
			fromType = GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER;
		} else if (GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN.equals(from)) {
			fromType = GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN;
		} else if (GlobalFlags.BINDSHIP_FROM_SHIPSTATUS.equals(from)) {
			fromType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;
		}
		String url;
		if (GlobalFlags.BINDSHIP_FROM_KAKOUMANAGER.equals(from)) {
			url = "getKkInfo";
			params.add(new BasicNameValuePair("cardNumber", cardNumber));
			params.add(new BasicNameValuePair("kkmc", ""));
		} else {
			url = "getShipList";
			params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
			params.add(new BasicNameValuePair("cardNumber", cardNumber));
			params.add(new BasicNameValuePair("bindType", fromType + ""));
			params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		}
		if (progressDialog != null) {
			doingZxing = false;
			return;
		}
		progressDialog = new ProgressDialog(ShipBind.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		handleFlag = false;
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_GETLIST);
	}

	/**
	 * 
	 * @方法名：setCustomTitle
	 * @功能说明：定义标题栏
	 * @author liums
	 * @date 2013-3-31 下午5:31:23
	 */
	private void setCustomTitle() {
		String titleLeft = "";
		if (GlobalFlags.BINDSHIP_FROM_KAKOUMANAGER.equals(from)) {
			// 手动查询按钮名称赋值
			((TextView) findViewById(R.id.ship_bind_btnsel_title)).setText(getString(R.string.select_kakou));
			titleLeft = getString(R.string.kakoumanager);
			bindType = 3;
			setMyActiveTitle(titleLeft + ">" + getString(R.string.kakou_band));
			return;
		} else if (GlobalFlags.BINDSHIP_FROM_TIKOUMANAGER.equals(from)) {
			titleLeft = getString(R.string.tikoumanager);
			bindType = 1;
		} else if (GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN.equals(from)) {
			titleLeft = getString(R.string.xunchaxunjian);
			bindType = 2;
		} else if (GlobalFlags.BINDSHIP_FROM_SHIPSTATUS.equals(from)) {
			titleLeft = getString(R.string.ShipStatus);
			bindType = 0;
		}
		setMyActiveTitle(titleLeft + ">" + getString(R.string.bindShip));

	}

	/**
	 * 
	 * @方法名：pingtechFindViewById
	 * @功能说明：控件寻址
	 * @author liums
	 * @date 2013-3-29 下午5:39:47
	 */
	private void pingtechFindViewById() {
		ship_bind_card_number = (EditText) this.findViewById(R.id.ship_bind_card_number);
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_GETLIST) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				ShipBindInfo shipBindInfo = PullXmlShipBind.onParseXMLData(str, cardNumber);
				if (shipBindInfo.isResult() && shipBindInfo.getCount() > 0) {
					Intent intent = new Intent();
					intent.putExtra("from", from);
					intent.setClass(ShipBind.this, ShipBindList.class);
					startActivity(intent);
					if (!handleFlag) {
						doingZxing = false;
						finish();
					}

				} else {
					if (shipBindInfo.getInfo() != null) {

						HgqwToast.makeText(ShipBind.this, shipBindInfo.getInfo(), HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(ShipBind.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
				doingZxing = false;
			} else {
				doingZxing = false;
				HgqwToast.makeText(ShipBind.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}
		}

	}

	/**
	 * 
	 * @方法名：buttonCheck
	 * @功能说明：确定按钮点击事件
	 * @author liums
	 * @date 2013-4-7 下午4:40:59
	 * @param v
	 */
	public void buttonCheck(View v) {
		if (v.getId() == R.id.ship_bind_btnok) {
			String num = ship_bind_card_number.getText().toString();
			if (StringUtils.isEmpty(num)) {
				HgqwToast.makeText(getApplicationContext(), "请刷卡或输入电子标签号！", HgqwToast.LENGTH_SHORT).show();
				return;
			}
			this.cardNumber = num;
			handleFlag = true;
			onLoadShipList();
		}
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
			Intent intent = new Intent(ShipBind.this, CaptureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, STARTACTIVITY_FOR_ZXING);
			break;
		}
		
	}

	/**
	 * 
	 * @方法名：onButtonClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-3-29 下午5:38:34
	 * @param v
	 */
	public void searchMethod(View v) {
		Intent intent = new Intent();
		intent.putExtra("frombindship", true);
		intent.putExtra("bindtype", bindType);
		if (bindType == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
			intent.putExtra("fromxuncha", true);
		}
		intent.setClass(getApplicationContext(), SelectShipActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == STARTACTIVITY_FOR_ZXING) {
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
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_HOME:
			return super.onKeyDown(keyCode, event);
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/* 读卡程序开始 */
	private ReadService readService;

	private ReadCardHandler readCardHander;

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		readCardHander = new ReadCardHandler();
		ScanUtils.initScanBarCode(getApplicationContext(), readCardHander);
		readInit();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		ScanUtils.closeScanBarCode(getApplicationContext());
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		if (readService != null) {
			readService.close();
			readService = null;
		}
		super.onPause();
	}

	private void readInit() {
		readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_DEFAULT_ICKEY);
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
			CardInfo cardInfo;
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
				readCardSuccess(cardInfo.getDefaultIckey());
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				readCardSuccess(cardInfo.getIckey());
				break;
			case ReadService.READ_TYPE_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				readCardSuccess(cardInfo.getIckey());
				break;
			case ReadService.READ_TYPE_ID:
				break;
			default:
				break;
			}
		}
	}
	/* 读卡程序结束 */
}
