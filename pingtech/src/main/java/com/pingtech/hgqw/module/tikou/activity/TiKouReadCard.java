package com.pingtech.hgqw.module.tikou.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android_serialport_api.ParseSFZAPI.People;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.RegisterPersoninfoActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.tikou.action.TkglAction;
import com.pingtech.hgqw.module.tikou.entity.PersonInfo;
import com.pingtech.hgqw.module.tikou.utils.PullXmlTiKou;
import com.pingtech.hgqw.module.wpjc.activity.GoodsCheckView;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.ImageFactory;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.ShowViewUtil;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.CaptureActivity;
import com.pingtech.hgqw.zxing.Constant;
import com.pingtech.hgqw.zxing.ScanDataUtil;
import com.pingtech.hgqw.zxing.entity.MsTdc;
import com.pingtech.hgqw.zxing.utils.ScanUtils;

public class TiKouReadCard extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "TiKouReadCard";

	/**
	 * 进入手动查询人员
	 */
	private static final int STARTACTIVITY_FOR_SELECT_PERSON = 0;

	/**
	 * 发起验证通行结果的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID = 1;

	/**
	 * 发起验证通行结果的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID_FOR_OFFLINE = 10000;

	/**
	 * 进入保存人员信息（刷卡登记时，该卡未在平台注册过）
	 */
	private static final int STARTACTIVITY_FOR_REGISTER_PERSONINFO = 2;

	/**
	 * 发起修改通行方向的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION = 3;

	/**
	 * 进入登记异常信息
	 */
	private static final int STARTACTIVITY_FOR_RECORD_EXCEPTION = 4;

	/**
	 * 进入二维码扫描
	 */
	private static final int STARTACTIVITY_FOR_ZXING = 5;

	private String voyageNumber = "";

	private String idcardnumber = "";

	private String defaultickey = "";

	private String from = "";

	private List<String> cbzwmList = null;

	private List<String> hcList = null;

	/** 证件号码或标签号码输入框控件，便于清空 */
	private EditText input;

	private ProgressDialog progressDialog = null;

	private PersonInfo tempPersonInfo = null;

	/**
	 * 是否允许刷卡，false刷卡后不处理，true允许刷卡
	 */
	private boolean flagRegister = true;

	/**
	 * true手动选择船员，false非船员
	 */
	private boolean sailorFlag = false;

	private boolean inputByHand = false;

	private SharedPreferences prefs;

	/**
	 * 二维码扫描出来的对象
	 */
	private MsTdc msTdc;

	/**
	 * 二维码扫描出来的证件号码
	 */
	private String zxingInfo = "";

	/**
	 * 是否正在执行二维码相关操作
	 */
	private boolean doingZxing = false;

	boolean isFromOffline = true;
	
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
			if (StringUtils.isEmpty(zxingInfo)) {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.cardnum_empty)).show();
				doingZxing = false;
				// 二维码扫描结果对象初始化
				msTdc = null;
				return;
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				doingZxing = false;
				return;
			}
			idcardnumber = zxingInfo;
			// <Old Data>onReadComplete(false, true);
			onReadComplete(false, "2");
			inputByHand = true;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.readcard_tikou);
		setMyActiveTitle(getString(R.string.tikoumanager) + ">" + getString(R.string.paycard));
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		voyageNumber = intent.getStringExtra("hc");
		Button btn = ((Button) findViewById(R.id.btnok));
		btn.setOnClickListener(clickOKButtonListener);
		btn = ((Button) findViewById(R.id.btnsel));
		btn.setOnClickListener(selPersonClickListener);
		TextView btn_title = (TextView) findViewById(R.id.btnsel_title);
		btn_title.setOnClickListener(selPersonClickListener);
		prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		sbGoodsCheck();
		input = (EditText) findViewById(R.id.cardtext);
		if (input != null) {
			input.setOnKeyListener(keyListener);
		}

		zxingButton = (Button)findViewById(R.id.tikou_check_btnzxing);
		zxingTextView = (TextView)findViewById(R.id.tikou_check_zxing);
	}

	/** 处理“确认”按钮消息，也就是输入卡号完毕后，如果是绑定船舶时刷电子标签，带上标签号直接返回，其他情况开始向后台发起请求 */
	private OnClickListener clickOKButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int res = v.getId();
			if (res == R.id.btnok) {
				input = (EditText) findViewById(R.id.cardtext);
				String num = input.getText().toString();
				if (StringUtils.isEmpty(num)) {
					// 号码不能为空！
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.cardnum_empty)).show();
					return;
				}
				if (progressDialog != null && progressDialog.isShowing()) {
					return;
				}
				idcardnumber = num;
				// <Old Data>onReadComplete(false, true);
				onReadComplete(false, "1");
				inputByHand = true;
			}
		}
	};

	List<HashMap<String, Object>> listShip = new ArrayList<HashMap<String, Object>>();

	private void sbGoodsCheck() {
		findViewById(R.id.tikou_downup_ship_imageview).setVisibility(View.VISIBLE);
		findViewById(R.id.tikou_downup_ship_linear).setVisibility(View.VISIBLE);
		List<HashMap<String, Object>> list = SystemSetting.shipOfKK;
		HashMap<String, Object> tikoumBindShip = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");

		if (tikoumBindShip != null && list != null && list.size() > 0) {
			listShip.clear();
			listShip.add(tikoumBindShip);
			String tkHc = (String) tikoumBindShip.get("hc");
			for (HashMap<String, Object> map : list) {
				String kkHc = (String) map.get("hc");
				if (StringUtils.isNotEmpty(kkHc) && StringUtils.isNotEmpty(tkHc) && kkHc.equals(tkHc)) {
					continue;
				}
				listShip.add(map);
			}
		} else if (tikoumBindShip != null && list == null) {
			listShip.clear();
			listShip.add(tikoumBindShip);
		} else if (tikoumBindShip == null && list != null && list.size() > 0) {
			listShip.addAll(list);
		}

		cbzwmList = new ArrayList<String>();
		hcList = new ArrayList<String>();
		for (int i = 0; i < listShip.size(); i++) {
			HashMap<String, Object> ship = listShip.get(i);
			if (ship != null) {
				cbzwmList.add((String) ship.get("cbzwm"));
				hcList.add((String) ship.get("hc"));
			}
		}
		Spinner spinner = (Spinner) findViewById(R.id.tikou_ship_spinner);
		ArrayAdapter<String> shipAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cbzwmList);
		shipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(shipAdapter);
		if (SystemSetting.readcardhc != null) {
			if (hcList.contains(SystemSetting.readcardhc)) {
				spinner.setSelection(hcList.indexOf(SystemSetting.readcardhc));
			} else {
				voyageNumber = hcList.get(0);

			}

		}
		if (cbzwmList != null && cbzwmList.size() > 0) {
			spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String hc = hcList.get(position);
					voyageNumber = hc;
					SystemSetting.readcardhc = hc;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
				}

			});

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		input = (EditText) findViewById(R.id.cardtext);
		if (input != null) {
			input.setOnKeyListener(keyListener);
		}
		switch (requestCode) {
		case STARTACTIVITY_FOR_SELECT_PERSON:
			if (!inputByHand) {
				input.setText("");
			}
			if (resultCode == RESULT_OK) {
				// 已经离船，直接提示无法上下船
				/** 船员离船标识，用于边检通通行验证，离船标识为1时禁止上下船 */
				String lcbz = data.getStringExtra("lcbz");
				if (StringUtils.isNotEmpty(lcbz) && "1".equals(lcbz)) {
					HgqwToast.toast("该船员已经离船，无法通行");
					return;
				}
				sailorFlag = true;
				idcardnumber = data.getStringExtra("id");
				tempPersonInfo = (PersonInfo) data.getSerializableExtra("personInfo");
				// <Old Data>onReadComplete(false, true);
				onReadComplete(false, "1");
				return;
			}
			break;
		case STARTACTIVITY_FOR_REGISTER_PERSONINFO: // 手动保存记录页面返回
			sailorFlag = false;
			flagRegister = true;
			if (!inputByHand && input != null) {
				input.setText("");
			}
			/*
			 * sailorFlag = false; flagRegister = true; if (resultCode ==
			 * RESULT_OK) { sxcfx_s = data.getStringExtra("fx");
			 * httpReturnXMLInfo = data.getStringExtra("yzjg"); name_s =
			 * data.getStringExtra("xm"); sex_s = data.getStringExtra("xb");
			 * country_s = data.getStringExtra("gj"); unit_s =
			 * data.getStringExtra("ssdw"); office_s =
			 * data.getStringExtra("zw"); birthday_s =
			 * data.getStringExtra("csrq"); idcardtype_s =
			 * data.getStringExtra("zjzl"); idcardnumber_s =
			 * data.getStringExtra("zjhm"); zjhm = idcardnumber_s; txjlid_s =
			 * data.getStringExtra("txjlid"); xcxsid_s =
			 * data.getStringExtra("xcxsid"); if (from.equals("03") ||
			 * from.equals("04")) {
			 * setContentView(R.layout.xunchaxunjiandetail);
			 * onUpdateDisplayXunjianInfo(true, false); } else {
			 * setContentView(R.layout.persondetail); onUpdateDisplayInfo(true,
			 * false); Button _btngoods = (Button)
			 * findViewById(R.id.btnRecordGoods);
			 * _btngoods.setVisibility(View.GONE); Button
			 * _btnmodifyPassDirection = (Button)
			 * findViewById(R.id.btnmodifyPassDirection);
			 * _btnmodifyPassDirection.setVisibility(View.GONE); }}
			 */
			break;
		case STARTACTIVITY_FOR_ZXING:
			setContentView(R.layout.readcard_tikou);
			if (from.equals("02")) {
				sbGoodsCheck();
			}
			// 修改刷卡标志位
			flagRegister = true;
			TextView textTemp = ((TextView) findViewById(R.id.ic_or_id_str));
			if (textTemp != null) {
				if (Flags.iCIDFlag) {
					textTemp.setText("IC读卡器已启动");
				} else {
					textTemp.setText("身份证读卡器已启动");
				}
			}
			flagRegister = true;
			Button btn = ((Button) findViewById(R.id.btnok));
			btn.setOnClickListener(clickOKButtonListener);
			btn = ((Button) findViewById(R.id.btnsel));
			btn.setOnClickListener(selPersonClickListener);
			TextView btn_title = (TextView) findViewById(R.id.btnsel_title);
			btn_title.setOnClickListener(selPersonClickListener);
			idcardnumber = "";
			input = (EditText) findViewById(R.id.cardtext);
			if (input != null) {
				input.setText("");
				input.setOnKeyListener(keyListener);
				input.requestFocus();
			}
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
			break;

		default:
			break;
		}
		return;
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result:" + (str != null));

		if (httpRequestType == HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID_FOR_OFFLINE) {
			if (StringUtils.isEmpty(str)) {
				if (progressDialog != null) {
					progressDialog.setMessage(getString(R.string.no_data_request_web));
				}
				onReadCompleteOnLine();
				return;
			}
			
			tempPersonInfo = PullXmlTiKou.parseXMLData(str);
			if (tempPersonInfo != null && tempPersonInfo.isClzj()) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.getToastView(getApplicationContext(), tempPersonInfo.getInfo()).show();
				BaseApplication.soundManager.onPlaySound(3, 0);
				doingZxing = false;
				msTdc = null;
				return;
			}

			if (!tempPersonInfo.isResult()) {
				HgqwToast.getToastView(getApplicationContext(), tempPersonInfo.getInfo()).show();
				BaseApplication.soundManager.onPlaySound(3, 0);
				doingZxing = false;
				// 二维码扫描结果对象初始化
				msTdc = null;
				return;
			}
			if (StringUtils.isEmpty(str) || !tempPersonInfo.isHasCardInfo()) {// 离线没有请求到数据，请求平台
				if (progressDialog != null) {
					progressDialog.setMessage(getString(R.string.no_data_request_web));
				}
				onReadCompleteOnLine();
				return;
			}
			isFromOffline = true;
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			// ////////////
			if (sailorFlag) {
				input = (EditText) findViewById(R.id.cardtext);
				input.setText("");
				sailorFlag = false;
				doingZxing = false;
			}
			if (str == null) {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
				doingZxing = false;
				// 二维码扫描结果对象初始化
				msTdc = null;
				return;
			}
			if (tempPersonInfo.isHasCardInfo() == false) {
				toSdbcActivity();
				return;
			}
			setContentView(R.layout.persondetail_tikou);
			onUpdateDisplayInfo(tempPersonInfo.isResult(), true, true);

			findViewById(R.id.btnScan).setVisibility(View.VISIBLE);

			// 修改通行方向按钮和声音控制。
			if (tempPersonInfo.getSuccessFlag() == 1) {
				BaseApplication.soundManager.onPlaySound(2, 0);// 成功提示音
			} else if (tempPersonInfo.getSuccessFlag() == 2) {
				BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
			}
			doingZxing = false;
			// 二维码扫描结果对象初始化
			msTdc = null;
		}
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID) {
			isFromOffline = false;
			paramsHis = null;
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (sailorFlag) {
				input = (EditText) findViewById(R.id.cardtext);
				input.setText("");
				sailorFlag = false;
				doingZxing = false;
			}
			// 如果只允许在线验证，则提示网络连接失败，否则进入手动保存页面
			if ((StringUtils.isEmpty(str) && !getState(FunctionSetting.bdtxyz, true))) {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
				doingZxing = false;
				// 二维码扫描结果对象初始化
				msTdc = null;
				return;
			} else if (StringUtils.isEmpty(str)) {
				toSdbcActivity();
				return;
			}
			tempPersonInfo = PullXmlTiKou.parseXMLData(str);
			if (!tempPersonInfo.isResult()) {
				HgqwToast.getToastView(getApplicationContext(), tempPersonInfo.getInfo()).show();
				BaseApplication.soundManager.onPlaySound(3, 0);
				doingZxing = false;
				// 二维码扫描结果对象初始化
				msTdc = null;
				return;
			}
			if (tempPersonInfo.isHasCardInfo() == false) {
				toSdbcActivity();
				return;
			}
			setContentView(R.layout.persondetail_tikou);
			onUpdateDisplayInfo(tempPersonInfo.isResult(), true, false);

			findViewById(R.id.btnScan).setVisibility(View.VISIBLE);

			// 修改通行方向按钮和声音控制。
			if (tempPersonInfo.getSuccessFlag() == 1) {
				BaseApplication.soundManager.onPlaySound(2, 0);// 成功提示音
			} else if (tempPersonInfo.getSuccessFlag() == 2) {
				BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
			}
			doingZxing = false;
			// 二维码扫描结果对象初始化
			msTdc = null;
		} else if (HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION == httpRequestType) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			boolean success = false;
			if (str != null && "1".equals(str)) {
				success = true;
			}
			if (success) {
				if ("0".equals(tempPersonInfo.getSxcfx())) {
					if (from.equals("02")) {
						((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
								+ "<font color=\"#acacac\">" + "下船" + "</font>"));
					} else {
						((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction_kk)
								+ "<font color=\"#acacac\">" + "出卡口" + "</font>"));
					}
					findViewById(R.id.imageView_Traffic_direction).setVisibility(View.VISIBLE);
					((ImageView) findViewById(R.id.imageView_Traffic_direction)).setImageResource(R.drawable.down);
					tempPersonInfo.setSxcfx("1");
				} else if ("1".equals(tempPersonInfo.getSxcfx())) {
					if (from.equals("02")) {
						((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
								+ "<font color=\"#acacac\">" + "上船" + "</font>"));
					} else {
						((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction_kk)
								+ "<font color=\"#acacac\">" + "进卡口" + "</font>"));
					}
					findViewById(R.id.imageView_Traffic_direction).setVisibility(View.VISIBLE);
					((ImageView) findViewById(R.id.imageView_Traffic_direction)).setImageResource(R.drawable.up);
					tempPersonInfo.setSxcfx("0");
				}
				// Toast.makeText(ReadcardActivity.this,
				// R.string.modify_success,
				// Toast.LENGTH_LONG).show();
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.modify_success)).show();
				((Button) findViewById(R.id.btnmodifyPassDirection)).setEnabled(false);
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.modify_failure)).show();
			}

		}

	}

	private void toSdbcActivity() {
		flagRegister = false;
		BaseApplication.soundManager.onPlaySound(3, 0);
		Intent intent = new Intent();
		intent.putExtra("from", from);
		intent.putExtra("info", tempPersonInfo.getInfo());
		intent.putExtra("voyageNumber", voyageNumber);
		intent.putExtra("zjhm", idcardnumber);
		// 传值二维码扫描的字段
		if (msTdc != null) {
			// System.out.println(msTdc.toString());
			if (msTdc.getScanType() == ScanDataUtil.SCAN_TYPE_DENG_LUN) {
				// 所属单位
				intent.putExtra("ssdw", msTdc.getSsdw());
				// 姓名
				intent.putExtra("xm", msTdc.getXm());
				// 性别
				intent.putExtra("xb", msTdc.getXbdm());
				// 出生日期
				String date = msTdc.getCsrq();
				if (StringUtils.isNotEmpty(date) && date.length() == 8) {
					StringBuffer dateSb = new StringBuffer(date.substring(0, 4)).append("-").append(date.substring(4, 6)).append("-")
							.append(date.substring(6, 8));
					intent.putExtra("csrq", String.valueOf(dateSb));
				}

				// 证件号码(zjhm)上面已经传入，不需要再次传入

				// *后面界面暂时未用到（扫描出来）的字段******************
				// (用途)预留字段1
				// 所登船舶
				// 本航次有效
			}
			// else
			// if(msTdc.getScanType()==ScanDataUtil.SCAN_TYPE_DA_KAO){
			// //搭靠外轮许可证数据处理
			// }
		}
		msTdc = null;
		intent.setClass(getApplicationContext(), RegisterPersoninfoActivity.class);
		startActivityForResult(intent, STARTACTIVITY_FOR_REGISTER_PERSONINFO);
		doingZxing = false;
	}

	/**
	 * 显示刷卡登记的结果
	 * 
	 * @param success
	 *            是否成功
	 * @param 职务是否是代码
	 *            ，true表示代码，需要从数据字典翻译，否则直接显示
	 * */
	private void onUpdateDisplayInfo(boolean success, boolean officecode, boolean isOffline) {
		boolean dkwl = false;

		if (tempPersonInfo.getSfdk() != null && "1".equals(tempPersonInfo.getSfdk())) {
			dkwl = true;
		}
		((TextView) findViewById(R.id.Validation_results)).setText(Html.fromHtml(getString(R.string.Valid_results) + "<font color=\"#acacac\">"
				+ tempPersonInfo.getInfo() + "</font>"));
		((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.sdcb)).setVisibility(View.GONE);
		// 显示有效期标准：
		// 只有登轮证、搭靠证、限定区域显示
		if (tempPersonInfo.getHgzl() != null && !"".equals(tempPersonInfo.getHgzl())) {
			if ("48".equals(tempPersonInfo.getHgzl())) {
				// 登轮证
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_denglunz) + getString(R.string.cardinfo));
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.detail_yxqz)).setText(Html.fromHtml(getString(R.string.yxq) + "<font color=\"#acacac\">"
						+ (tempPersonInfo.getYxq() == null ? "" : tempPersonInfo.getYxq()) + "</font>"));
				((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml(getString(R.string.xunjian_sdcb) + "：" + "<font color=\"#acacac\">"
						+ (tempPersonInfo.getFw() == null ? "" : tempPersonInfo.getFw()) + "</font>"));
			} else if ("50".equals(tempPersonInfo.getHgzl())) {
				// 登陆证
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_dengluz) + getString(R.string.cardinfo));
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.GONE);
				// ((TextView)
				// findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml(getString(R.string.xunjian_sdcb) + "：" + "<font color=\"#acacac\">"
						+ (tempPersonInfo.getFw() == null ? "" : tempPersonInfo.getFw()) + "</font>"));
			} else if ("52".equals(tempPersonInfo.getHgzl())) {
				// 搭靠证
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_dakaoz) + getString(R.string.cardinfo));
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.VISIBLE);
				// ((TextView)
				// findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.detail_yxqz)).setText(Html.fromHtml(getString(R.string.yxq) + "<font color=\"#acacac\">"
						+ (tempPersonInfo.getYxq() == null ? "" : tempPersonInfo.getYxq()) + "</font>"));
				// ((TextView)
				// findViewById(R.id.sdcb)).setText(Html.fromHtml("搭靠船舶（范围）" +
				// "：" + "<font color=\"#acacac\">"
				// + (tempPersonInfo.getFw() == null ? "" :
				// tempPersonInfo.getFw()) + "</font>"));
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(tempPersonInfo.getHgzl())) {
				// 限定区域通行证
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_xdqy) + getString(R.string.cardinfo));
				
				// 限定区域通行证
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.VISIBLE);
				 ((TextView)
				 findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.detail_yxqz)).setText(Html.fromHtml(getString(R.string.yxq) + "<font color=\"#acacac\">"
						+ (tempPersonInfo.getYxq() == null ? "" : tempPersonInfo.getYxq()) + "</font>"));
				((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml("区域范围" + "：" + "<font color=\"#acacac\">"
						+ (tempPersonInfo.getFw() == null ? "" : tempPersonInfo.getFw()) + "</font>"));
			} else {
				((TextView) findViewById(R.id.sdcb)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.cardinfo));
			}

		}
		if (dkwl) {
			findViewById(R.id.Traffic_direction).setVisibility(View.GONE);
			findViewById(R.id.imageView_Traffic_direction).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.shipname) + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getDkcbmc() == null ? "" : tempPersonInfo.getDkcbmc()) + "</font>"));
			((TextView) findViewById(R.id.detail_sex)).setText(Html.fromHtml(getString(R.string.ship_gj) + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getDkcgj() == null ? "" : tempPersonInfo.getDkcgj()) + "</font>"));
			((TextView) findViewById(R.id.detail_country)).setText(Html.fromHtml(getString(R.string.ship_zzdw) + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getDkzzdw() == null ? "" : tempPersonInfo.getDkzzdw()) + "</font>"));
			((TextView) findViewById(R.id.detail_birthday)).setText(Html.fromHtml(getString(R.string.ship_ml) + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getDkml() == null ? "" : tempPersonInfo.getDkml()) + "</font>"));
			((TextView) findViewById(R.id.detail_cardtype)).setText(Html.fromHtml(getString(R.string.goods_check_unit) + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getDkssdw() == null ? "" : tempPersonInfo.getDkssdw()) + "</font>"));
			((TextView) findViewById(R.id.detail_cardnum)).setText(Html.fromHtml(getString(R.string.ship_yt) + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getDkyt() == null ? "" : tempPersonInfo.getDkyt()) + "</font>"));
			((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.ship_dkcb) + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getDkdkfw() == null ? "" : tempPersonInfo.getDkdkfw()) + "</font>"));
			((TextView) findViewById(R.id.sdcb)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_office)).setVisibility(View.GONE);
		} else {
			if (tempPersonInfo.getSuccessFlag() == 1) {
				if ("0".equals(tempPersonInfo.getSxcfx())) {
					((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
							+ "<font color=\"#acacac\">" + "上船" + "</font>"));
					findViewById(R.id.imageView_Traffic_direction).setVisibility(View.VISIBLE);
					((ImageView) findViewById(R.id.imageView_Traffic_direction)).setImageResource(R.drawable.up);
				} else if ("1".equals(tempPersonInfo.getSxcfx())) {
					((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
							+ "<font color=\"#acacac\">" + "下船" + "</font>"));
					findViewById(R.id.imageView_Traffic_direction).setVisibility(View.VISIBLE);
					((ImageView) findViewById(R.id.imageView_Traffic_direction)).setImageResource(R.drawable.down);
				} else {
					((TextView) findViewById(R.id.Traffic_direction)).setVisibility(View.GONE);
					findViewById(R.id.imageView_Traffic_direction).setVisibility(View.GONE);
				}
			} else {
				((TextView) findViewById(R.id.Traffic_direction)).setVisibility(View.GONE);
				findViewById(R.id.imageView_Traffic_direction).setVisibility(View.GONE);
			}

			((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.name) + "：" + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getName() == null ? "" : tempPersonInfo.getName()) + "</font>"));
			String tempstr;
			if ((tempPersonInfo.getSex() != null) && (tempPersonInfo.getSex().length() > 0)) {
				tempstr = DataDictionary.getDataDictionaryName(tempPersonInfo.getSex(), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE);
			} else {
				tempstr = null;
			}

			((TextView) findViewById(R.id.detail_sex)).setText(Html.fromHtml(getString(R.string.sex) + "：" + "<font color=\"#acacac\">"
					+ (tempstr == null ? "" : tempstr) + "</font>"));
			if ((tempPersonInfo.getCountry() != null) && (tempPersonInfo.getCountry().length() > 0)) {
				tempstr = DataDictionary.getCountryName(tempPersonInfo.getCountry());
			} else {
				tempstr = null;
			}
			((TextView) findViewById(R.id.detail_country)).setText(Html.fromHtml(getString(R.string.country) + "：" + "<font color=\"#acacac\">"
					+ (tempstr == null ? "" : tempstr) + "</font>"));
			((TextView) findViewById(R.id.detail_birthday)).setText(Html.fromHtml(getString(R.string.birthday) + "：" + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getBirthday() == null ? "" : tempPersonInfo.getBirthday()) + "</font>"));
			if ((tempPersonInfo.getCardtype() != null) && (tempPersonInfo.getCardtype().length() > 0)) {
				tempstr = DataDictionary.getDataDictionaryName(tempPersonInfo.getCardtype(), DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE);
			} else {
				tempstr = null;
			}
			((TextView) findViewById(R.id.detail_cardtype)).setText(Html.fromHtml(getString(R.string.cardtype) + "：" + "<font color=\"#acacac\">"
					+ (tempstr == null ? "" : tempstr) + "</font>"));
			((TextView) findViewById(R.id.detail_cardnum)).setText(Html.fromHtml(getString(R.string.cardnum) + "：" + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getZjhm() == null ? "" : tempPersonInfo.getZjhm()) + "</font>"));
			((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.unit) + "：" + "<font color=\"#acacac\">"
					+ (tempPersonInfo.getUnit() == null ? "" : tempPersonInfo.getUnit()) + "</font>"));
			if (officecode) {
				((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (tempPersonInfo.getOffice() == null ? ""
								: DataDictionary.getDataDictionaryOfficeName(tempPersonInfo.getOffice(), ((tempPersonInfo.getHgzl() != null && "50"
										.equals(tempPersonInfo.getHgzl())) ? DataDictionary.DATADICTIONARY_TYPE_CBYGZW
										: DataDictionary.DATADICTIONARY_TYPE_DLRYZW))) + "</font>"));
			} else {
				((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office) + "：" + "<font color=\"#acacac\">"
						+ (tempPersonInfo.getOffice() == null ? "" : tempPersonInfo.getOffice()) + "</font>"));
			}
		}

		ShowViewUtil.showPzbjxx(tempPersonInfo, this, GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER, isOffline);

		ImageView imageView_bg = (ImageView) findViewById(R.id.imageView_photo);
		ImageView imageView = (ImageView) findViewById(R.id.imageView_network_photo);
		if (tempPersonInfo.getPhoto() != null) {
			Bitmap netWorkImage = ImageFactory.getBitmap(tempPersonInfo.getPhoto());
			ImageFactory.setImage(imageView_bg, netWorkImage);
		} else if (isOffline) {
			Bitmap netWorkImage = ImageFactory.loadImage(tempPersonInfo.getZjhm());
			if (netWorkImage == null && cardInfo != null && cardInfo.getPeople() != null) {
				netWorkImage = ImageFactory.getBitmap(cardInfo.getPeople().getPhoto());
			}
			ImageFactory.setImage(imageView_bg, netWorkImage);
		} else {
			Bitmap netWorkImage = null;
			if (cardInfo != null && cardInfo.getPeople() != null) {
				netWorkImage = ImageFactory.getBitmap(cardInfo.getPeople().getPhoto());
				if (netWorkImage != null) {
					ImageFactory.setImage(imageView_bg, netWorkImage);
				} else {
					imageView.setVisibility(View.GONE);
				}
			}
		}

		Button _btnmodifyPassDirection = (Button) findViewById(R.id.btnmodifyPassDirection);
		_btnmodifyPassDirection.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (tempPersonInfo.getTxjlid() != null && tempPersonInfo.getTxjlid().length() > 0) {
					String url = "modifyPassDirection";

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("recordid", tempPersonInfo.getTxjlid()));
					params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("ryid", tempPersonInfo.getRyid()));// 离线业务使用字段
					params.add(new BasicNameValuePair("hc", voyageNumber));// 离线业务使用字段
					params.add(new BasicNameValuePair("hgzl", tempPersonInfo.getHgzl()));// 离线业务使用字段
					params.add(new BasicNameValuePair("passDirection",
							(tempPersonInfo.getSxcfx() != null && tempPersonInfo.getSxcfx().equals("0")) ? "1" : "0"));
					params.add(new BasicNameValuePair("time",
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
					if (progressDialog != null) {
						return;
					}
					progressDialog = new ProgressDialog(TiKouReadCard.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();

					if (!isFromOffline) {
						NetWorkManager.request(TiKouReadCard.this, url, params, HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION);
					} else {
						OffLineManager.request(TiKouReadCard.this, new TkglAction(), url, NVPairTOMap.nameValuePairTOMap(params),
								HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION);
					}

				}

			}
		});
		Button _btnnoidcard = (Button) findViewById(R.id.btnNoIDCard);
		Button _btnexcept = (Button) findViewById(R.id.btnExceptionRegist);
		_btnexcept.setOnClickListener(exceptRegistListener);
		Button _btngoods = (Button) findViewById(R.id.btnRecordGoods);
		_btngoods.setOnClickListener(goodsRecordListener);
		if (dkwl || success == false || tempPersonInfo.getTxjlid() == null || "".equals(tempPersonInfo.getTxjlid())) {
			_btnmodifyPassDirection.setVisibility(View.INVISIBLE);
		} else {
			_btnmodifyPassDirection.setVisibility(View.VISIBLE);
		}

		_btnnoidcard.setOnClickListener(retryInputCardNumListener);

		if ((!dkwl) && success && tempPersonInfo.getSuccessFlag() == 1 && tempPersonInfo.getHgzl() != null
				&& (tempPersonInfo.getTxjlid() != null && !"".equals(tempPersonInfo.getTxjlid()))) {
			_btngoods.setVisibility(View.VISIBLE);
		} else {
			_btngoods.setVisibility(View.INVISIBLE);
		}

	}

	/** 手动查询人员 */
	private OnClickListener selPersonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("fromxuncha", false);
			intent.putExtra("from", from);
			if (voyageNumber != null) {
				intent.putExtra("hc", voyageNumber);
			}
			intent.putExtra("tkgl_sailor_list", true);
			intent.setClass(getApplicationContext(), TikoucymdActivity.class);
			// intent.setClass(getApplicationContext(),
			// SelectPersonActivity.class);
			// startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_PERSON);
			startActivity(intent);
			flagRegister = false;
		}
	};

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
			Intent intent = new Intent(TiKouReadCard.this, CaptureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, STARTACTIVITY_FOR_ZXING);
			break;
		}

	}

	/**
	 * 点击“异常信息”按钮的处理，进入异常信息模块，同时带上必要信息
	 * 如果不允许切换对象类别，对象id传"不能切换对象类别"，在异常信息模块就会有相应的处理
	 * */
	private OnClickListener exceptRegistListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("name", tempPersonInfo.getName());
			intent.putExtra("nationality", tempPersonInfo.getCountry());
			intent.putExtra("sex", tempPersonInfo.getSex());
			intent.putExtra("cardtype", tempPersonInfo.getCardtype());
			intent.putExtra("birthday", tempPersonInfo.getBirthday());
			// 添加异常传递证件号码，不使用IC卡号
			intent.putExtra("cardnumber", tempPersonInfo.getZjhm());
			intent.putExtra("company", tempPersonInfo.getUnit());
			if (tempPersonInfo.getSfdk() != null && "1".equals(tempPersonInfo.getSfdk())) {
				intent.putExtra("objecttype", "03");
				intent.putExtra("windowtype", "03");
			} else {
				intent.putExtra("objecttype", "01");
				intent.putExtra("windowtype", "02");
			}
			HashMap<String, Object> ship = null;
			// 循环查找当前选择的船舶
			if (listShip != null) {
				ship = checkSelectedShip(voyageNumber);
			} else {
				ship = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
			}
			if (ship == null) {
				ship = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
			}

			if (ship != null) {
				String tkwz = (String) ship.get("tkwz");
				String[] temp_str = null;
				if (tkwz != null) {
					temp_str = tkwz.split(",");
				}
				intent.putExtra("shipname", (String) ship.get("cbzwm"));
				intent.putExtra("jhhc", (String) ship.get("hc"));
				intent.putExtra("dockcode", (String) ship.get("tkmt"));
				if (temp_str != null && temp_str.length > 0) {
					intent.putExtra("dockname", temp_str[0]);
				} else {
					intent.putExtra("dockname", "");
				}
				intent.putExtra("berthcode", (String) ship.get("tkbw"));
				if (temp_str != null && temp_str.length > 1) {
					intent.putExtra("berthname", temp_str[1]);
				} else {
					intent.putExtra("berthname", "");
				}
				if (tempPersonInfo != null && tempPersonInfo.getSfdk() != null && "1".equals(tempPersonInfo.getSfdk())) {
					intent.putExtra("cbzwm", tempPersonInfo.getDkcbmc());
					intent.putExtra("glcbmc", (String) ship.get("cbzwm"));
					intent.putExtra("jhhc", "");// 搭靠证航次，和船舶名称使用搭靠证的数据
					intent.putExtra("swid", "搭靠证");
					intent.putExtra("scene", "02");
				}
			}

			intent.putExtra("id", "不能切换对象类别");
			intent.putExtra("dkjlid", tempPersonInfo.getDkjlid());

			intent.putExtra("from", from);
			intent.putExtra("source", "02");
			intent.putExtra("yzjg", tempPersonInfo.getInfo());
			intent.setClass(getApplicationContext(), Exceptioninfo.class);
			startActivityForResult(intent, STARTACTIVITY_FOR_RECORD_EXCEPTION);
		}
	};

	/**
	 * 点击信息详情界面上“返回”按钮时的处理，重新回到刷卡界面。 如果是由查询人员模块进入记录巡检记录，按返回按钮不再重新到刷卡界面
	 * */
	private OnClickListener retryInputCardNumListener = new OnClickListener() {
		public void onClick(View v) {
			setContentView(R.layout.readcard_tikou);
			if (from.equals("02")) {
				sbGoodsCheck();
			}
			// 修改刷卡标志位
			flagRegister = true;
			TextView textTemp = ((TextView) findViewById(R.id.ic_or_id_str));
			if (textTemp != null) {
				if (Flags.iCIDFlag) {
					textTemp.setText("IC读卡器已启动");
				} else {
					textTemp.setText("身份证读卡器已启动");
				}
			}
			flagRegister = true;
			Button btn = ((Button) findViewById(R.id.btnok));
			btn.setOnClickListener(clickOKButtonListener);
			btn = ((Button) findViewById(R.id.btnsel));
			btn.setOnClickListener(selPersonClickListener);
			TextView btn_title = (TextView) findViewById(R.id.btnsel_title);
			btn_title.setOnClickListener(selPersonClickListener);
			idcardnumber = "";
			input = (EditText) findViewById(R.id.cardtext);
			if (input != null) {
				input.setText("");
				input.setOnKeyListener(keyListener);
				input.requestFocus();
			}
		}
	};

	/** 点击“登记货物”按钮的处理 */
	private OnClickListener goodsRecordListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();

			intent.putExtra("ryid", tempPersonInfo.getRyid());
			intent.putExtra("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
			intent.putExtra("voyageNumber", voyageNumber);
			intent.putExtra("zjhm", tempPersonInfo.getZjhm());
			intent.putExtra("name", tempPersonInfo.getName());
			intent.putExtra("xb", tempPersonInfo.getSex());
			intent.putExtra("zjzl", tempPersonInfo.getCardtype());
			intent.putExtra("csrq", tempPersonInfo.getBirthday());
			intent.putExtra("ssdw", tempPersonInfo.getUnit());
			intent.putExtra("zw", tempPersonInfo.getOffice());
			intent.putExtra("gj", tempPersonInfo.getCountry());
			intent.putExtra("fx", tempPersonInfo.getSxcfx());
			if (sailorFlag) {// 是否船员：0否、1是
				intent.putExtra("sfcy", "1");
			} else {
				intent.putExtra("sfcy", "0");
			}
			intent.putExtra("from", from);
			intent.setClass(getApplicationContext(), GoodsCheckView.class);
			startActivity(intent);
		}
	};

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

	/**
	 * 
	 * @方法名：checkSelectedShip
	 * @功能说明：获取当前选择的船舶
	 * @author liums
	 * @date 2013-12-3 下午4:32:29
	 * @param voyageNumber2
	 * @return
	 */
	protected HashMap<String, Object> checkSelectedShip(String voyageNumber) {
		for (HashMap<String, Object> map : listShip) {
			String hc = (String) map.get("hc");
			if (hc != null && voyageNumber.equals(hc)) {
				return map;
			}
		}
		return null;
	}

	@Override
	public void onDestroy() {
		clickOKButtonListener = null;
		flagRegister = false;
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
		flagRegister = true;
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
			case MessageEntity.TOAST:
				HgqwToast.getToastView(getApplicationContext(), (String) msg.obj).show();
				break;
			case ReadService.READ_TYPE_DEFAULT_ICKEY:
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
				if (flagRegister) {
					cardInfo = (CardInfo) msg.obj;
					idcardnumber = cardInfo.getIckey();
					defaultickey = cardInfo.getDefaultIckey();
					BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
					// <Old Data>onReadComplete(false, false);
					onReadComplete(false, "0");
					Log.i("智能卡读取成功", defaultickey + "," + idcardnumber);
				}
				break;
			case ReadService.READ_TYPE_ID:
				if (flagRegister) {
					cardInfo = (CardInfo) msg.obj;
					People people = cardInfo.getPeople();
					BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
					readIdSuccess(people);
					//Log.i("身份证读卡成功", people.getPeopleIDCode());
				}
				break;
			case ReadService.READ_TYPE_ICKEY:
				break;
			case ReadService.READ_TYPE_EWM:
				cardInfo = (CardInfo) msg.obj;
				if (cardInfo == null) {
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
			default:
				break;
			}
		}
	}

	private void readIdSuccess(People people) {
		if (tempPersonInfo == null) {
			tempPersonInfo = new PersonInfo();
		}
		idcardnumber = people.getPeopleIDCode();
		tempPersonInfo.setZjhm(people.getPeopleIDCode());
		tempPersonInfo.setName(people.getPeopleName());
		tempPersonInfo.setSex(people.getPeopleSex());
		tempPersonInfo.setBirthday(people.getPeopleBirthday());
		tempPersonInfo.setCountry(people.getPeopleNation());
		tempPersonInfo.setPhoto(people.getPhoto());
		// <Old Data>onReadComplete(true, true);// 刷身份证按手动输入业务验证，按zjhm查询
		onReadComplete(true, "1");// 刷身份证按手动输入业务验证，按zjhm查询
	}

	/**
	 * 
	 * @方法名：onReadComplete
	 * @功能说明：二代证阅读器读卡完毕后，或者手动输入完毕后，开始发起验证请求，
	 * @author zhaotf
	 * @date 2014-2-25 下午3:14:07
	 * @param idcard
	 *            是否是二代证，如果是，需要显示照片
	 * @param inputType
	 *            输入方式(默认为"0")<br>
	 *            是否刷卡：0刷卡、1手动输入、2二维码扫描
	 */
	private void onReadComplete(boolean idcard, String inputType) {
		tempPersonInfo = null;
		input = (EditText) findViewById(R.id.cardtext);
		if (input != null) {
			input.setText(idcardnumber);
		}
		String url = "inspectForAcross";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
		params.add(new BasicNameValuePair("cardNumber", idcardnumber));
		params.add(new BasicNameValuePair("defaultickey", defaultickey));
		if (StringUtils.isNotEmpty(inputType)) {
			if ("0".equals(inputType.trim()) || "1".equals(inputType.trim()) || "2".equals(inputType.trim())) {
				params.add(new BasicNameValuePair("sfsk", inputType.trim()));
			} else {
				params.add(new BasicNameValuePair("sfsk", "0"));
			}
		} else {
			params.add(new BasicNameValuePair("sfsk", "0"));
		}
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		if (sailorFlag) {
			params.add(new BasicNameValuePair("sfcy", "1"));// 手动选择船员1
		} else {
			params.add(new BasicNameValuePair("sfcy", "0"));// 其他情况传0
		}
		params.add(new BasicNameValuePair("acrossTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
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
		// 隐藏软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (input != null) {
			imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
		}
		paramsHis = params;
		if (!getState(FunctionSetting.bdtxyz, true)) {
			if (progressDialog != null) {
				progressDialog.setMessage(getString(R.string.offline_off_request_web));
			}
			NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID);
		} else {
			OffLineManager.request(this, new TkglAction(), url, NVPairTOMap.nameValuePairTOMap(params),
					HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID_FOR_OFFLINE);
		}
	}

	/**
	 * 
	 * @方法名：onReadComplete
	 * @功能说明：二代证阅读器读卡完毕后，或者手动输入完毕后，开始发起验证请求，
	 * @author zhaotf
	 * @date 2014-2-25 下午3:14:07
	 * @param idcard
	 *            是否是二代证，如果是，需要显示照片
	 * @param inputType
	 *            输入方式(默认为"0")<br>
	 *            是否刷卡：0刷卡、1手动输入、2二维码扫描
	 */
	private List<NameValuePair> paramsHis = null;//

	private void onReadCompleteOnLine() {
		String url = "inspectForAcross";
		NetWorkManager.request(this, url, paramsHis, HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID);
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (res.second != null) {
			onHttpResult(res.second.toString(), offLineRequestType);
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			doingZxing = false;
			msTdc = null;
			HgqwToast.getToastView(getApplicationContext(), getString(R.string.filed_validate_card)).show();
		}
		// 二维码扫描结果对象初始化
		// msTdc = null;
	}

	/** 监听输入框输入，接收IC卡刷卡器刷卡结果 */
	private EditText.OnKeyListener keyListener = new EditText.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_UP)) {
				input = (EditText) findViewById(R.id.cardtext);
				String num = input.getText().toString();
				if (StringUtils.isEmpty(num)) {
					// 号码不能为空！
					HgqwToast.toast(getString(R.string.cardnum_empty));
					return false;
				}
				if (progressDialog != null && progressDialog.isShowing()) {
					return false;
				}
				idcardnumber = num;
				// <Old Data>onReadComplete(false, true);
				onReadComplete(false, "1");
				inputByHand = true;

			}
			return false;
		}
	};

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
