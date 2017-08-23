package com.pingtech.hgqw.module.cfzg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.entity.ShipBindInfo;
import com.pingtech.hgqw.module.bindship.utils.PullXmlShipBind;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.entity.MsTdc;

public class CfzgShipBind extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "ShipBind";

	/**
	 * 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态-船舶绑定、0201梯口管理-船舶绑定、0101
	 * 卡口管理-船舶绑定、0301巡查巡检-船舶绑定
	 */
	private String from;

	private EditText ship_bind_card_number;

	private Button ship_bind_btnok;

	private String cardNumber;// IC卡号

	public static final int STARTACTIVITY_FOR_SELECT_SHIP = 0;

	/** 发起获取船舶列表的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GETLIST = 4;

	private int bindType;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		cfzgFlag = intent.getBooleanExtra("cfzgFlag", false);
		super.onCreateForCfzg(savedInstanceState, R.layout.cfzg_ship_bind_readcard);
		Log.i("日志", getClass().getName() + ":onCreate");
		this.setCustomTitle();
		this.pingtechFindViewById();
		onInitSoundPool();
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
		if ("0101".equals(from)) {
			// 手动查询按钮名称赋值
			((TextView) findViewById(R.id.ship_bind_btnsel_title)).setText(getString(R.string.select_kakou));
			titleLeft = getString(R.string.kakoumanager);
			bindType = 3;
			setMyActiveTitle(titleLeft + ">" + getString(R.string.kakou_band));
			return;
		} else if ("0201".equals(from)) {
			titleLeft = getString(R.string.tikoumanager);
			bindType = 1;
		} else if ("0301".equals(from)) {
			titleLeft = getString(R.string.xunchaxunjian);
			bindType = 2;
		} else if ("0501".equals(from)) {
			titleLeft = getString(R.string.ShipStatus);
			bindType = 0;
		}
		if (cfzgFlag) {
			setMyActiveTitle(getString(R.string.bindShip));
		} else {
			setMyActiveTitle(titleLeft + ">" + getString(R.string.bindShip));
		}

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
		ship_bind_btnok = (Button) this.findViewById(R.id.ship_bind_btnok);
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() httpRequestType:" + httpRequestType + ",result:" + (str != null));
		switch (httpRequestType) {
		case FlagUrls.VALIDATE_PASSWORD:
			if (str != null) {
				if ("success".equals(str)) {
					// 密码验证成功，模拟调用Home键
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
				} else {
					HgqwToast.getToastView(getApplicationContext(), "密码错误！").show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
			}
			break;
		case HTTPREQUEST_TYPE_FOR_GETLIST:
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				ShipBindInfo shipBindInfo = PullXmlShipBind.onParseXMLData(str, cardNumber);
				if (shipBindInfo.isResult() && shipBindInfo.getCount() > 0) {
					Intent intent = new Intent();
					intent.putExtra("from", from);
					intent.setClass(this, CfzgShipBindList.class);
					startActivity(intent);
					finish();
				} else {
					if (shipBindInfo.getInfo() != null) {
						HgqwToast.makeText(this, shipBindInfo.getInfo(), HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				HgqwToast.makeText(this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}

			break;
		default:
			break;
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
		String num = ship_bind_card_number.getText().toString();
		if (num == null || "".equals(num)) {
			HgqwToast.makeText(getApplicationContext(), "请刷卡或输入电子标签号！", HgqwToast.LENGTH_SHORT).show();
		}
		Intent data = null;
		data = new Intent();
		data.putExtra("cardNumber", num);
		setResult(RESULT_OK, data);
		finish();
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
		if (bindType == CfzgShipListActivity.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
			intent.putExtra("fromxuncha", true);
		}
		intent.setClass(getApplicationContext(), CfzgSelectShipActivity.class);
		intent.putExtra("cfzgFlag", cfzgFlag);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FlagManagers.CUSTOM_DIALOG_FOR_EXIT && resultCode == RESULT_OK) {
			String password = data.getStringExtra("password");
			this.validatePassword(password);
		} else if (resultCode == RESULT_OK) {
			Intent intent = null;
			intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	// //////
	private SoundPool sp;

	private HashMap<Integer, Integer> hm;

	private float volume;

	/** 初始化音源，用于提示刷卡未通过验证时 */
	private void onInitSoundPool() {
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		hm = new HashMap<Integer, Integer>();
		hm.put(4, sp.load(getApplicationContext(), R.raw.ic_mag, 1));
	}

	private void onUnInitSoundPool() {
		sp = null;
		hm = null;
	}

	/** 播放声音 */
	private void onPlaySoundNoVb(int num, int loop) {
		Log.i(TAG, "playSound:" + num + "," + loop);
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		float currentSound = am.getStreamVolume(AudioManager.STREAM_RING);
		float maxSound = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		volume = currentSound / maxSound;
		sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
		// sp.play(hm.get(num), maxSound, maxSound, 1, loop, 1.0f);
		// 震动
	}

	/* 监听物理按键 */
	/*
	 * @Override public void onAttachedToWindow() {
	 * getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	 * super.onAttachedToWindow(); }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// return super.onKeyDown(keyCode, event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_HOME:
			/*
			 * if(cfzgFlag){ // dialogActivityForExit();
			 * 
			 * }else{ return super.onKeyDown(keyCode, event); } break;
			 */
			return super.onKeyDown(keyCode, event);
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @方法名：dialogActivity
	 * @功能说明：弹出Home键验证对话框
	 * @author liums
	 * @date 2013-5-7 下午5:34:08
	 */
	private void dialogActivityForExit() {
		Intent intent = new Intent();

		intent.setClass(getApplicationContext(), CfzgCustomDialogForExit.class);
		intent.putExtra("cn", true);
		startActivityForResult(intent, FlagManagers.CUSTOM_DIALOG_FOR_EXIT);
	}

	/**
	 * @方法名：validatePassword
	 * @功能说明：验证当前用户密码
	 * @author liums
	 * @date 2013-5-7 下午5:42:16
	 * @param password
	 * @return
	 */
	private ProgressDialog progressDialog = null;

	@SuppressWarnings("deprecation")
	private void validatePassword(String password) {
		String url = "validatePassword";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", LoginUser.getCurrentLoginUser().getUserName()));
		params.add(new BasicNameValuePair("password", password));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.setButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!((ProgressDialog) dialog).isShowing()) {
					Log.i(TAG, "!((ProgressDialog)dialog).isShowing()");
					progressDialog = null;
					return;
				}
				Log.i(TAG, "progressDialog onClick");
				dialog.dismiss();
				progressDialog = null;
			}
		});
		progressDialog.show();
		NetWorkManager.request(this, url, params, FlagUrls.VALIDATE_PASSWORD);

	}

	/* 读卡程序开始 */
	private ReadService readService;

	private ReadCardHandler readCardHander;

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		readInit();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		onUnInitSoundPool();// 释放音频资源
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		ReadService.getInstent().close();
		super.onPause();
	}

	private void readInit() {
		readCardHander = new ReadCardHandler();
		ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_DEFAULT_ICKEY).init();
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
				if (cardInfo == null) {
					return;
				}
				MsTdc msTdc = cardInfo.getMsTdc();
				// if (msTdc != null) {
				// zxingInfo = msTdc.getZjhm();
				// if (StringUtils.isNotEmpty(zxingInfo)) {
				// zxingHandler.sendEmptyMessage(0);
				// }
				// }
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

	private void readCardSuccess(String result) {
		this.cardNumber = result;
		ship_bind_card_number.setText(cardNumber);
		onPlaySoundNoVb(4, 0);
		String num = ship_bind_card_number.getText().toString();
		onLoadShipList(result);
	}

	/* 读卡程序结束 */

	/**
	 * 开始获取船舶列表信息
	 * 
	 * @param cardNumber
	 */
	private void onLoadShipList(String cardNumber) {
		if (progressDialog != null) {
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String url;
		url = "getShipList";
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("cardNumber", cardNumber));
		params.add(new BasicNameValuePair("bindType", GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + ""));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_GETLIST);
	}
}
