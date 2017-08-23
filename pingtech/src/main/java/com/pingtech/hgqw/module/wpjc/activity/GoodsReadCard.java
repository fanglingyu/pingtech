package com.pingtech.hgqw.module.wpjc.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android_serialport_api.ParseSFZAPI.People;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.SelectPersonActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.wpjc.action.GoodsCheckAction;
import com.pingtech.hgqw.module.wpjc.entity.ReadCardPersonInfo;
import com.pingtech.hgqw.module.wpjc.utils.PullXmlGoodsCheck;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringEncoder;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.CaptureActivity;
import com.pingtech.hgqw.zxing.Constant;
import com.pingtech.hgqw.zxing.ScanDataUtil;
import com.pingtech.hgqw.zxing.entity.MsTdc;
import com.pingtech.hgqw.zxing.utils.ScanUtils;

public class GoodsReadCard extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "GoodsReadCardActivity";

	private static final int HTTPREQUEST_TYPE_FOR_GOODSREAD = 0;

	private String idcardnumber_s = "";

	private ProgressDialog progressDialog = null;

	private String time_s = "";

	private String voyageNumber = "";

	private String voyagemc = "";

	private String kacbqkid = "";

	private SharedPreferences prefs;

	/**
	 * 发起验证通行结果的http请求的type
	 */
	/**
	 * true手动选择船员，false非船员
	 */
	private boolean sailorFlag = false;

	private Spinner spinner;

	private ArrayAdapter<String> shipAdapter;

	/** 证件号码或标签号码输入框控件，便于清空 */
	private EditText input;

	private Vibrator vibrator = null;

	private SoundPool sp;

	private float volume;

	private List<String> cbzwmList = new ArrayList<String>();

	private List<String> hcList = new ArrayList<String>();

	private List<String> kacbqkidList = new ArrayList<String>();

	private HashMap<Integer, Integer> hm;

	private ReadCardPersonInfo personInfo = null;

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
	 * 二维码扫描按钮处理
	 */
	private Button zxingButton;
	private TextView zxingTextView;

	/**
	 * 将二维码扫描的数据显示文本框，并提交验证
	 */
	private Handler zxingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (StringUtils.isNotEmpty(zxingInfo)) {
				input.setText(zxingInfo);
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				defaultickey = "";
				sendGoodInfo("2");
			} else {
				BaseApplication.soundManager.onPlaySound(3, 0);
				doingZxing = false;
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.goodscheck_readcard);
		setMyActiveTitle(getText(R.string.goods_check).toString());
		Intent intent = getIntent();
		prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		voyageNumber = intent.getStringExtra("voyageNumber");
		voyagemc = intent.getStringExtra("voyagemc");
		cbzwmList = intent.getStringArrayListExtra("cbzwmList");
		kacbqkid = intent.getStringExtra("kacbqkid");
		kacbqkidList = intent.getStringArrayListExtra("kacbqkidList");
		hcList = intent.getStringArrayListExtra("hcList");
		if (cbzwmList == null) {
			cbzwmList = new ArrayList<String>();
		}
		if (hcList == null) {
			hcList = new ArrayList<String>();
		}
		if (kacbqkidList == null) {
			kacbqkidList = new ArrayList<String>();
		}

		spinner = (Spinner) findViewById(R.id.goodscheck_ship_spinner);
		shipAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cbzwmList);
		shipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(shipAdapter);

		if (SystemSetting.readcardhc != null) {
			if (hcList.contains(SystemSetting.readcardhc)) {
				spinner.setSelection(hcList.indexOf(SystemSetting.readcardhc));
			} else {
				voyageNumber = hcList.get(0);
				voyagemc = cbzwmList.get(0);
				kacbqkid = kacbqkidList.get(0);
			}

		}
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				voyageNumber = hcList.get(position);
				voyagemc = cbzwmList.get(position);
				SystemSetting.readcardhc = voyageNumber;
				kacbqkid = kacbqkidList.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});
		input = (EditText) findViewById(R.id.goodscheck_card_text);
		Button btn = ((Button) findViewById(R.id.goods_check_btnok));
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String num = input.getText().toString();
				if (num.length() == 0) {
					offLineToast(getString(R.string.cardnumber_empty));
					return;
				}
				sendGoodInfo("1");
				idcardnumber_s = num;
				personInfo = new ReadCardPersonInfo();
				personInfo.setCardnumber(idcardnumber_s);
			}

		});

		zxingButton = (Button)findViewById(R.id.goods_check_btnzxing);
		zxingTextView = (TextView)findViewById(R.id.goods_check_zxing);
	}

	/**
	 * 发送信息
	 * 
	 * @param sfsk
	 *            (默认为"0")<br>
	 *            是否刷卡：0刷卡、1手动输入、2二维码扫描
	 */
	private void sendGoodInfo(String sfsk) {
		String url = "goodsCheck";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
		params.add(new BasicNameValuePair("voyagemc", voyagemc));
		params.add(new BasicNameValuePair("kacbqkid", kacbqkid));
		params.add(new BasicNameValuePair("cardNumber", input.getText().toString()));
		params.add(new BasicNameValuePair("defaultickey", defaultickey));
		params.add(new BasicNameValuePair("sfsk", sfsk));
		params.add(new BasicNameValuePair("xjlx", "01"));
		params.add(new BasicNameValuePair("pdacode", DeviceUtils.getIMEI()));
		params.add(new BasicNameValuePair("PDACode", DeviceUtils.getIMEI()));
		if (sailorFlag) {
			params.add(new BasicNameValuePair("sfcy", "1"));// 手动选择船员1
		} else {
			params.add(new BasicNameValuePair("sfcy", "0"));// 其他情况传0
		}
		params.add(new BasicNameValuePair("acrossTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(GoodsReadCard.this);
		progressDialog.setTitle(getString(R.string.Validing));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		/**
		 * 判断逻辑:<br>
		 * 先判断有没有网络，如果没有网络---离线验证。<br>
		 * 有网络但开关打开---离线验证。<br>
		 * 开关关闭---在线验证
		 */
		if (!prefs.getBoolean(FunctionSetting.kqlx, false)) {
			NetWorkManager.request(GoodsReadCard.this, url, params, HTTPREQUEST_TYPE_FOR_GOODSREAD);
		} else {
			OffLineManager.request(this, new GoodsCheckAction(), url, NVPairTOMap.nameValuePairTOMap(params), HTTPREQUEST_TYPE_FOR_GOODSREAD);
		}

	}


	/**
	 * 
	 * @方法名：onButtonClick
	 * @功能说明："手动选择"按钮点击事件
	 * @author liums
	 * @date 2013-3-29 下午5:38:34
	 * @param v
	 */
	public void searchMethod(View v) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), SelectPersonActivity.class);
		intent.putExtra("fromGoodsCheck", true);
		intent.putExtra("hc", voyageNumber);
		intent.putExtra("voyagemc", voyagemc);
		intent.putExtra("kacbqkid", kacbqkid);
		intent.putExtra("tkgl_sailor_list", true);
		intent.putExtra("time", time_s);
		startActivity(intent);
	}

	/**
	 * @方法名：zxingScanMethod
	 * @功能说明：扫描二维码方法
	 * @author zhaotf
	 * @date 2013-10-30 下午2:11:44
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
			ScanUtils.readByPA9View(readCardHander, zxingButton, zxingTextView);
			return;
		default:
			// 二维码扫描结果对象初始化
			msTdc = null;
			Intent intent = new Intent(GoodsReadCard.this, CaptureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, 0);
			break;
		}
		
	}

	/**
	 * 设置readCardPersonInfoTemp和PersonaInfo，专为二维码扫描完成后，设置参数调用
	 * 
	 * @description
	 * @param readCardPersonInfoTemp
	 * @date 2014-1-9
	 * @author zhaotf
	 */
	private ReadCardPersonInfo setPersonInfo(ReadCardPersonInfo readCardPersonInfoTemp) {
		ReadCardPersonInfo PersonInfoTemp = readCardPersonInfoTemp;
		if (msTdc != null) {
			// 设置证件号码
			personInfo.setCardnumber(msTdc.getZjhm());
			if (msTdc.getScanType() == ScanDataUtil.SCAN_TYPE_DENG_LUN) {
				// 设置名字
				personInfo.setName(msTdc.getXm());
				// 设置出生日期
				personInfo.setBirthday(msTdc.getCsrq());
				// 性别
				personInfo.setSex(msTdc.getXbdm());
				// 所属单位
				personInfo.setUnit(msTdc.getSsdw());
				if (PersonInfoTemp != null) {
					PersonInfoTemp.setCardnumber(msTdc.getZjhm());
					PersonInfoTemp.setName(msTdc.getXm());
					PersonInfoTemp.setSex(msTdc.getXbdm());
					PersonInfoTemp.setUnit(msTdc.getSsdw());
				}
			}

		}
		msTdc = null;
		return PersonInfoTemp;
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (sailorFlag) {
			input = (EditText) findViewById(R.id.goodscheck_card_text);
			input.setText("");
			sailorFlag = false;
		}
		if (HTTPREQUEST_TYPE_FOR_GOODSREAD == httpRequestType) {
			if (str == null) {
				offLineToast(getString(R.string.data_download_failure_info));
				personInfo = null;
				doingZxing = false;
				// 初始化二维码扫描结果对象
				msTdc = null;
				return;
			}
			ReadCardPersonInfo readCardPersonInfoTemp = this.personInfo;
			personInfo = PullXmlGoodsCheck.parseXMLData(str);
			if (!personInfo.isResult()) {
				offLineToast(personInfo.getInfo());
				personInfo = null;
				BaseApplication.soundManager.onPlaySound(3, 0);
				doingZxing = false;
				// 初始化二维码扫描结果对象
				msTdc = null;
				return;
			}
			time_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
			if (personInfo.isHasCardInfo() == false) {
				personInfo = new ReadCardPersonInfo();
				Intent intent = new Intent();
				intent.putExtra("time", time_s);
				intent.putExtra("voyageNumber", voyageNumber);
				intent.putExtra("kacbqkid", kacbqkid);

				// 二维码扫描完成后，设置参数
				readCardPersonInfoTemp = setPersonInfo(readCardPersonInfoTemp);

				input = (EditText) findViewById(R.id.goodscheck_card_text);
				String cardNumber = input.getText().toString();
				personInfo.setCardnumber(cardNumber);
				Bundle bundle = new Bundle();
				if (readCardPersonInfoTemp != null) {
					readCardPersonInfoTemp.setCardnumber(cardNumber);
					bundle.putSerializable("personInfo", readCardPersonInfoTemp);
				} else {
					bundle.putSerializable("personInfo", personInfo);
				}

				intent.putExtras(bundle);
				intent.setClass(getApplicationContext(), GoodsPersoninfo.class);
				startActivity(intent);
				doingZxing = false;
				return;
			} else {
				Intent intent = new Intent();
				intent.putExtra("time", time_s);
				intent.putExtra("voyageNumber", voyageNumber);
				intent.putExtra("kacbqkid", kacbqkid);
				Bundle bundle = new Bundle();
				// 二维码扫描完成后，设置参数
				readCardPersonInfoTemp = setPersonInfo(readCardPersonInfoTemp);

				bundle.putSerializable("personInfo", personInfo);
				intent.putExtras(bundle);
				intent.setClass(getApplicationContext(), GoodsPersonDetail.class);
				startActivity(intent);
				doingZxing = false;
			}
			// 初始化二维码扫描结果对象
			msTdc = null;
			doingZxing = false;
		}

	}

	/**
	 * 将List<NameValuePair>格式数据转换成Map格式
	 * 
	 * @param paramsList
	 * @return
	 */
	private Map<String, String> changeParmsToMap(List<NameValuePair> params) {
		int count = 0;
		if (params != null) {
			count = params.size();
		}

		Map<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < count; i++) {
			NameValuePair temp = params.get(i);
			String key = temp.getName();
			try {
				key = StringEncoder.encode(key, "UTF-8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			String value = temp.getValue();
			try {
				value = StringEncoder.encode(value, "UTF-8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			map.put(key, value);
		}

		return map;
	}

	/**
	 * 离线版Toast
	 */
	private void offLineToast(String show) {
		HgqwToast.makeText(getApplicationContext(), show, HgqwToast.LENGTH_LONG).show();
	}

	/* 读卡程序开始 */
	private ReadService readService;

	private ReadCardHandler readCardHander;

	private CardInfo cardInfo;

	private String defaultickey = "";

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		readCardHander = new ReadCardHandler();
		ScanUtils.initScanBarCode(getApplicationContext(), readCardHander);
		readInit();
		input = (EditText) findViewById(R.id.goodscheck_card_text);
		if (input != null) {
			input.setText("");
		}
		personInfo = null;
		super.onResume();
	}

	@Override
	public void onDestroy() {
		ScanUtils.closeScanBarCode(getApplicationContext());
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		if (readService != null) {
			readService.close();
		}
		super.onPause();
	}

	private void readInit() {
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_MIMA:
			View v = findViewById(R.id.btn_ic_id_change);
			if (v != null) {
				v.setVisibility(View.VISIBLE);
			}
			readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_DEFAULT_AND_ICKEY);
			break;
		case DeviceUtils.DEVICE_MODEL_M:
		case DeviceUtils.DEVICE_MODEL_CFON640:
		case DeviceUtils.DEVICE_MODEL_PA8:
		case DeviceUtils.DEVICE_MODEL_PA9:
			readService = ReadService.getInstent(this, readCardHander, ReadService.READ_TYPE_ID_IC);
			break;
		case DeviceUtils.DEVICE_MODEL_SDK:
			return;
		default:
			break;
		}
		readService.init();
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
				msTdc = cardInfo.getMsTdc();
				if (msTdc != null) {
					zxingInfo = msTdc.getZjhm();
					if (StringUtils.isNotEmpty(zxingInfo)) {
						zxingHandler.sendEmptyMessage(0);
					}
				}
				break;
			case MessageEntity.TOAST:
				offLineToast((String) msg.obj);
				break;
			case ReadService.READ_TYPE_DEFAULT_ICKEY:
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				idcardnumber_s = cardInfo.getIckey();
				defaultickey = cardInfo.getDefaultIckey();
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				input.setText(idcardnumber_s);
				sendGoodInfo("0");
				break;
			case ReadService.READ_TYPE_ID:
				cardInfo = (CardInfo) msg.obj;
				defaultickey = "";
				People people = cardInfo.getPeople();
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				input.setText(people.getPeopleIDCode());
				readIdSuccess(people);
				sendGoodInfo("1");// 手动输入1，刷卡0，身份证按手动输入验证
				Log.i("身份证读卡结果", people.getPeopleIDCode());
				break;
			case ReadService.READ_TYPE_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				idcardnumber_s = cardInfo.getIckey();
				defaultickey = cardInfo.getDefaultIckey();
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				input.setText(idcardnumber_s);
				sendGoodInfo("0");// 手动输入1，刷卡0
				break;
			default:
				break;
			}
		}
	}

	private void readIdSuccess(People people) {
		personInfo = new ReadCardPersonInfo();
		personInfo.setBirthday((people.getPeopleBirthday() == null ? "" : people.getPeopleBirthday()));
		personInfo.setCardnumber((people.getPeopleIDCode() == null ? "" : people.getPeopleIDCode()));
		personInfo.setCardtype("10");
		personInfo.setCountry("CHN");
		personInfo.setName((people.getPeopleName() == null ? "" : people.getPeopleName()));
		personInfo.setOffice("");
		personInfo.setPhoto((people.getPhoto() == null ? null : people.getPhoto()));
		personInfo.setSex(DataDictionary.getDataDictionaryCode((people.getPeopleSex() == null ? null : people.getPeopleSex()),
				DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
		personInfo.setUnit("");
	}

	/* 读卡程序结束 */

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (res.second != null) {
			onHttpResult((String) res.second, offLineRequestType);
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			doingZxing = false;
			offLineToast(getString(R.string.no_data));
		}
		// 二维码扫描结果对象初始化
		msTdc = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
