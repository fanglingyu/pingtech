package com.pingtech.hgqw.module.kakou.activity;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.util.Pair;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android_serialport_api.ParseSFZAPI.People;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.RegisterPersoninfoActivity;
import com.pingtech.hgqw.activity.SelectPersonActivity;
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.kakou.action.KakouAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.tikou.entity.PersonInfo;
import com.pingtech.hgqw.module.wpjc.activity.GoodsCheckView;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.readcard.entity.ICReadEntity;
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

/**
 * 读卡模块，包括读IC卡、二代证，读IC卡时，读到数据就返回；
 * 读二代证时，读到数据后发送到后台请求验证数据，并且显示返回结果（含日常巡检和刷卡登记），同时重新启动读卡流程
 * 如果进入异常模块等其他界面，暂停二代证刷卡流程，返回后又重启。启动二代证阅读器时，会有请稍后的提示框
 * */
public class KaKouReadCard extends MyActivity implements OnHttpResult, OffLineResult {
	private String xjddName = "未绑定";

	/**
	 * 手动选择巡检地点：0码头、1泊位、2区域
	 */
	private int placeType = -1;

	/**
	 * 手动选择巡检地点：地点ID
	 */
	private String placeId = null;

	/**
	 * true手动选择船员，false非船员
	 */
	private boolean sailorFlag = false;

	/**
	 * 是否允许刷卡，false刷卡后不处理，true允许刷卡
	 */
	private boolean flagRegister = true;

	/**
	 * 证件号码，验证人员时，如果平台有人员信息，手动添加异常需要传递zjhm。
	 */
	private String zjhm = "";

	private String bjtsxx = "";

	private String pzmbly = "";

	/**
	 * android震动设备
	 */
	private Vibrator vibrator = null;

	/**
	 * IC读卡区域识别
	 */
	private int readMode = 0;

	private byte[] image_cgcs;

	private static final String TAG = "KaKouReadCard";

	/**
	 * 读IC卡，比如船舶绑定
	 */
	public static final int READCARD_TYPE_IC_CARD = 0;

	/**
	 * 读ID卡，比如刷卡登记
	 */
	public static final int READCARD_TYPE_ID_CARD = 1;

	/**
	 * 同时读IC、ID卡，比如巡查巡检
	 */
	public static final int READCARD_TYPE_ICID_CARD = 2;

	/**
	 * 进入登记异常信息
	 */
	private static final int STARTACTIVITY_FOR_RECORD_EXCEPTION = 3;

	/**
	 * 进入手动查询人员
	 */
	private static final int STARTACTIVITY_FOR_SELECT_PERSON = 4;

	/**
	 * 进入保存人员信息（刷卡登记时，该卡未在平台注册过）
	 */
	private static final int STARTACTIVITY_FOR_REGISTER_PERSONINFO = 5;

	/**
	 * 查岗查哨页面
	 */
	private static final int STARTACTIVITY_FOR_REGISTER_CGCS = 206;

	/**
	 * 进入查询船舶
	 */
	private static final int STARTACTIVITY_FOR_SELECT_SHIP = 6;

	/**
	 * 发起验证通行结果的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID = 7;

	private static final int HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID_FOR_OFFLINE = 10000;

	/**
	 * 发起修改通行方向的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION = 8;

	/**
	 * 巡查巡检时，日常巡检、查岗查哨情况下发起验证的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_NORMAL_XUNJIAN_ID = 9;

	/**
	 * 巡查巡检时，刷信息钉情况下发起的http请求的type
	 */
	private static final int HTTPREQUEST_TYPE_FOR_NORMAL_XUNJIAN_IC = 10;

	/**
	 * 进入二维码扫描
	 */
	private static final int STARTACTIVITY_FOR_ZXING = 11;

	private int cardType = 0;

	private String xcxsid_s = "";

	private String cgcsid_s = "";

	private String idcardtype_s = "";

	private String old_idcardtype_s = "";

	private String birthday_s = "";

	private String old_birthday_s = "";

	private String idcardnumber_s = "";

	private String defaultickey = "";

	private String old_idcardnumber_s = "";

	private String name_s = "";

	private String old_name_s = "";

	private String time_s = "";

	private String sex_s = "";

	private String old_sex_s = "";

	private String country_s = "";

	private String old_country_s = "";

	private String hgzl_s = "";

	private String unit_s = "";

	private String old_unit_s = "";

	private String office_s = "";

	private String old_office_s = "";

	private String txjlid_s = "";

	/**
	 * 验证是否成功：1验证通过，2验证失败 播放声音：0不播放，1播放验证成功提示音，2播放验证失败提示音
	 * 
	 */
	private int successFlag = 0;

	private String dkjlid_s = "";

	private String sfdk_s = "";

	private String sxcfx_s = "";

	private String ryid_s = "";

	private String icinput_s = "";

	private Bitmap netWorkImage = null;

	private boolean hasImage = false;

	/* 是否是二代证，如果是(true)，需要显示照片 */
	private boolean mIdcard = false;

	/* 是否有证件信息，如果没有，需要进入保存证件信息界面 */
	private boolean hasCardInfo = false;

	/**
	 * 标记从哪个模块进入该界面，01卡口、02梯口、03巡查巡检、04查询人员模块
	 * 、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
	 */
	private String from = "";

	private String sdcb_s = "";

	private String yxq_s = "";

	private String bhcyx_s = "";

	private String dkzjhm_s = "";

	private String sbkid_s = "";

	private String zqdd_s = "";// 执勤地点

	private String dkzjlx_s = "";

	private String dkssdw_s = "";

	private String dkcbmc_s = "";

	private String dkcgj_s = "";

	private String dkzzdw_s = "";

	private String dkml_s = "";

	private String dkyt_s = "";

	private String dkdkfw_s = "";

	private String xxd_arg1_s = "";

	private String xxd_arg2_s = "";

	private String xxd_arg3_s = "";

	private String xxd_arg4_s = "";

	private String xxd_arg5_s = "";

	private String voyageNumber = "";

	private String pzxx_s = "";

	private String photo_s = "";

	/** 是否士兵证，也就是是否是查岗查哨 */
	boolean sbz = false;

	/** 保存通行记录 */
	private ArrayList<Map<String, Object>> txjl = null;

	private List<String> cbzwmList = null;

	private List<String> hcList = null;

	private SharedPreferences prefs;

	/**
	 * 绑定船舶时，也就是刷泊位信息钉时，标记来自哪个模块
	 * 
	 * @see ShiplistActivity
	 * */
	private int bindType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;

	/** 证件号码或标签号码输入框控件，便于清空 */
	private EditText input;

	/**
	 * 卡口名称
	 */
	private String kkmc;

	/**
	 * 二维码扫描出来的数据
	 */
	private String zxingInfo = "";

	/**
	 * 是否正在执行二维码相关操作
	 */
	private boolean doingZxing = false;

	private boolean isClzj = false;

	/**
	 * 二维码扫描结果
	 */
	private MsTdc msTdc;

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
				// 初始化二维码扫描结果对象
				msTdc = null;
				return;
			}
			if (cardType != READCARD_TYPE_ID_CARD) {
				Intent data = null;
				data = new Intent();
				data.putExtra("cardNumber", zxingInfo);
				setResult(RESULT_OK, data);
				doingZxing = false;
				finish();
			} else {
				if (progressDialog != null && progressDialog.isShowing()) {
					doingZxing = false;
					return;
				}
				idcardnumber_s = zxingInfo;
				idcardtype_s = "";
				birthday_s = "";
				name_s = "";
				sex_s = "";
				country_s = "";
				unit_s = "";
				office_s = "";
				// <Old Data>onReadComplete(false, true);
				onReadComplete(false, "2");
			}

		}

	};

	/**
	 * 点击信息详情界面上“返回”按钮时的处理，重新回到刷卡界面。 如果是由查询人员模块进入记录巡检记录，按返回按钮不再重新到刷卡界面
	 * */
	private OnClickListener retryInputCardNumListener = new OnClickListener() {
		public void onClick(View v) {
			if (from != null && from.equals("04")) {
				finish();
				return;
			}
			setContentView(R.layout.readcard_kakou);
			switch (DeviceUtils.getDeviceModel()) {
			case DeviceUtils.DEVICE_MODEL_MIMA:
				((Button) findViewById(R.id.xcxj_rcxj_select_place)).setVisibility(View.VISIBLE);
				break;
			case DeviceUtils.DEVICE_MODEL_M:
				((Button) findViewById(R.id.xcxj_rcxj_select_place)).setVisibility(View.GONE);
				break;
			case DeviceUtils.DEVICE_MODEL_SDK:
				break;
			default:
				break;
			}

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
			RadioGroup rg = (RadioGroup) findViewById(R.id.readcard_radio);
			rg.check(R.id.radio_btn_id);
			if (from != null && from.equals("03")) {
				findViewById(R.id.readcard_radio).setVisibility(View.GONE);
				setXjddTextView();
				if (findViewById(R.id.bigIcon) != null) {
					findViewById(R.id.bigIcon).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.smallIcon) != null) {
					findViewById(R.id.smallIcon).setVisibility(View.GONE);
				}
			} else {
				findViewById(R.id.readcard_radio).setVisibility(View.GONE);
				if (findViewById(R.id.bigIcon) != null) {
					findViewById(R.id.bigIcon).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.smallIcon) != null) {
					findViewById(R.id.smallIcon).setVisibility(View.GONE);
				}
			}
			Button btn = ((Button) findViewById(R.id.btnok));
			btn.setOnClickListener(clickOKButtonListener);
			btn.setOnKeyListener(btnKeyListener);
			btn = ((Button) findViewById(R.id.btnsel));
			TextView btn_title = (TextView) findViewById(R.id.btnsel_title);
			if (bindType != -1) {
				btn.setOnClickListener(selShipClickListener);
				btn.setOnKeyListener(btnKeyListener);
				if (btn_title != null) {
					btn_title.setOnClickListener(selShipClickListener);
				}
				if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					if (btn_title == null) {
						btn.setText(R.string.select_kakou);
					} else {
						btn_title.setText(R.string.select_kakou);
					}
				}
			} else if (from.equals("02")) {
				if (btn_title == null) {
					btn.setText(R.string.Manual_Select);
				} else {
					btn_title.setText(R.string.Manual_Select);
				}

				btn.setOnClickListener(selPersonClickListener);
				btn.setOnKeyListener(btnKeyListener);
				if (btn_title != null) {
					btn_title.setOnClickListener(selPersonClickListener);
				}
				// }
			} else {
				btn.setVisibility(View.GONE);
				// if (findViewById(R.id.bindship_search) != null) {
				// findViewById(R.id.bindship_search).setVisibility(View.GONE);
				// }
				/**
				 * 二维码扫描按钮
				 */
				if (findViewById(R.id.kakou_check_btnzxing) != null) {
					findViewById(R.id.kakou_check_btnzxing).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.kakou_check_zxing) != null) {
					findViewById(R.id.kakou_check_zxing).setVisibility(View.VISIBLE);
				}
				

				/**
				 * 手动查找按钮
				 */
				if (findViewById(R.id.btnsel) != null) {
					findViewById(R.id.btnsel).setVisibility(View.GONE);
				}
				if (findViewById(R.id.btnsel_title) != null) {
					findViewById(R.id.btnsel_title).setVisibility(View.GONE);
				}
			}
			idcardnumber_s = "";
			input = (EditText) findViewById(R.id.cardtext);
			if (input != null) {
				input.setText("");
				input.setOnKeyListener(keyListener);
				input.requestFocus();
			}
		}
	};

	/**
	 * 点击“异常信息”按钮的处理，进入异常信息模块，同时带上必要信息
	 * 如果不允许切换对象类别，对象id传"不能切换对象类别"，在异常信息模块就会有相应的处理
	 * */
	private OnClickListener exceptRegistListener = new OnClickListener() {
		public void onClick(View v) {
			// iDReaderClose();
			int res = v.getId();
			Intent intent = new Intent();
			if (res != R.id.btnExceptionRegist_object) {
				intent.putExtra("name", name_s);
				intent.putExtra("nationality", country_s);
				intent.putExtra("sex", sex_s);
				intent.putExtra("cardtype", idcardtype_s);
				intent.putExtra("birthday", birthday_s);
				// intent.putExtra("cardnumber", idcardnumber_s);
				// 添加异常传递证件号码，不使用IC卡号
				intent.putExtra("cardnumber", zjhm);
				intent.putExtra("company", unit_s);
				if (from.equals("03") && sbz) {
					intent.putExtra("objecttype", "06");
					intent.putExtra("windowtype", "03");
				} else {
					if (sfdk_s != null && sfdk_s.equals("1")) {
						intent.putExtra("objecttype", "03");
						intent.putExtra("windowtype", "03");
					} else {
						intent.putExtra("objecttype", "01");
						intent.putExtra("windowtype", "02");
					}
				}
				if (from.equals("01")) {
					intent.putExtra("windowtype", "03");
					HashMap<String, Object> ship = null;
					ship = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
					if (ship != null) {
						intent.putExtra("areaname", (String) ship.get("kkmc"));
						intent.putExtra("areacode", (String) ship.get("id"));
						intent.putExtra("scene", "04");
					}
				} else {
					HashMap<String, Object> ship = null;
					if (from.equals("02")) {
						ship = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
					} else {
						ship = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
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
						if (sfdk_s != null && sfdk_s.equals("1")) {
							intent.putExtra("cbzwm", dkcbmc_s);
							intent.putExtra("glcbmc", (String) ship.get("cbzwm"));
							intent.putExtra("jhhc", (String) ship.get("hc"));
							intent.putExtra("swid", "搭靠证");
							intent.putExtra("scene", "02");
						}
					}
				}

				intent.putExtra("id", "不能切换对象类别");
				if (sbz) {
					intent.putExtra("sbkid", sbkid_s);
				}
				intent.putExtra("xunjian_id", xcxsid_s);
				intent.putExtra("cgcsid", cgcsid_s);
				intent.putExtra("dkjlid", dkjlid_s);
			} else {
				intent.putExtra("objecttype", "05");
				intent.putExtra("windowtype", "03");
				if (SystemSetting.xunJianId != null && SystemSetting.xunJianId.length() > 0) {
					if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
						intent.putExtra("berthcode", SystemSetting.xunJianId);
						intent.putExtra("berthname", SystemSetting.xunJianName);
						intent.putExtra("dockcode", SystemSetting.xunJianMTid);
						intent.putExtra("dockname", SystemSetting.xunJianMTname);
						intent.putExtra("scene", "02");
					} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
						intent.putExtra("dockcode", SystemSetting.xunJianId);
						intent.putExtra("dockname", SystemSetting.xunJianName);
						intent.putExtra("scene", "03");
					} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
						intent.putExtra("areacode", SystemSetting.xunJianId);
						intent.putExtra("areaname", SystemSetting.xunJianName);
						intent.putExtra("scene", "04");
					}
				}
			}
			if ("04".equals(from)) {
				intent.putExtra("jcfs", "02");
				intent.putExtra("source", "03");
			}else{
				intent.putExtra("source", "01");
			}
			intent.putExtra("from", from);
			intent.putExtra("yzjg", httpReturnXMLInfo);
			intent.setClass(getApplicationContext(), Exceptioninfo.class);
			startActivityForResult(intent, STARTACTIVITY_FOR_RECORD_EXCEPTION);
		}
	};

	/** 点击“登记货物”按钮的处理 */
	private OnClickListener goodsRecordListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();

			intent.putExtra("ryid", ryid_s);
			intent.putExtra("time", time_s);
			intent.putExtra("voyageNumber", voyageNumber);
			intent.putExtra("zjhm", zjhm);
			intent.putExtra("name", name_s);
			intent.putExtra("xb", sex_s);
			intent.putExtra("zjzl", idcardtype_s);
			intent.putExtra("csrq", birthday_s);
			intent.putExtra("ssdw", unit_s);
			intent.putExtra("zw", office_s);
			intent.putExtra("gj", country_s);
			intent.putExtra("fx", sxcfx_s);

			/*
			 * intent.putExtra("name", name_s); intent.putExtra("time", time_s);
			 * intent.putExtra("ryid", ryid_s);
			 */
			if (sailorFlag) {// 是否船员：0否、1是
				intent.putExtra("sfcy", "1");
			} else {
				intent.putExtra("sfcy", "0");
			}
			intent.putExtra("from", from);
			// intent.putExtra("voyageNumber", voyageNumber);
			intent.setClass(getApplicationContext(), GoodsCheckView.class);
			startActivity(intent);
		}
	};

	/** 处理“确认”按钮消息，也就是输入卡号完毕后，如果是绑定船舶时刷电子标签，带上标签号直接返回，其他情况开始向后台发起请求 */
	private OnClickListener clickOKButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int res = v.getId();
			if (res == R.id.btnok) {
				String num = input.getText().toString();
				if (StringUtils.isEmpty(num)) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.cardnum_empty)).show();
					return;
				}
				if (cardType != READCARD_TYPE_ID_CARD) {
					Intent data = null;
					data = new Intent();
					data.putExtra("cardNumber", num);
					setResult(RESULT_OK, data);
					finish();
				} else {
					if (progressDialog != null && progressDialog.isShowing()) {
						return;
					}
					idcardnumber_s = num;
					idcardtype_s = "";
					birthday_s = "";
					name_s = "";
					sex_s = "";
					country_s = "";
					unit_s = "";
					office_s = "";
					// <Old Data>onReadComplete(false, true);
					onReadComplete(false, "1");
				}
			}
		}
	};

	/** 手动查询船舶 */
	private OnClickListener selShipClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int res = v.getId();
			if (res == R.id.btnsel || res == R.id.btnsel_title) {
				Intent intent = new Intent();
				intent.putExtra("frombindship", true);
				intent.putExtra("bindtype", bindType);
				if (bindType == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
					intent.putExtra("fromxuncha", true);
				}
				intent.setClass(getApplicationContext(), SelectShipActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_SHIP);
			}
		}
	};

	/** 手动查询人员 */
	private OnClickListener selPersonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int res = v.getId();
			if (res == R.id.btnsel || res == R.id.btnsel_title) {
				// iDReaderClose();
				Intent intent = new Intent();
				intent.putExtra("fromxuncha", false);
				intent.putExtra("from", from);
				if (voyageNumber != null) {
					intent.putExtra("hc", voyageNumber);
				}
				intent.setClass(getApplicationContext(), SelectPersonActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_PERSON);
				flagRegister = false;
			}
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
			ScanUtils.readByPA9View(readCardHander, zxingButton, zxingTextView);
			return;
		default:
			// 二维码扫描结果对象初始化
			msTdc = null;
			Intent intent = new Intent(KaKouReadCard.this, CaptureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, STARTACTIVITY_FOR_ZXING);
			break;
		}

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
						idcardtype_s = "";
						birthday_s = "";
						name_s = "";
						sex_s = "";
						country_s = "";
						unit_s = "";
						office_s = "";
						// <Old Data>onReadComplete(false, true);
						onReadComplete(false, "1");
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

	private String httpReturnXMLInfo = "";

	private String tempHttpReturnXMLInfo = "";

	private ListView listView;

	private MyAdapter adapter;

	private Bitmap mPhotoBg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		cardType = intent.getIntExtra("cardtype", 0);
		voyageNumber = intent.getStringExtra("hc");
		kkmc = intent.getStringExtra("kkmc");
		bindType = intent.getIntExtra("bindtype", -1);
		prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		Resources res = getResources();
		mPhotoBg = BitmapFactory.decodeResource(res, R.drawable.photo);

		if (from != null && from.equals("04")) {
			super.onCreate(savedInstanceState, R.layout.persondetail_kakou);
			ryid_s = intent.getStringExtra("id");
			name_s = intent.getStringExtra("xm");
			sex_s = intent.getStringExtra("xb");
			country_s = intent.getStringExtra("gj");
			office_s = intent.getStringExtra("zw");
			idcardtype_s = intent.getStringExtra("zjzl");
			idcardnumber_s = intent.getStringExtra("zjhm");
			zjhm = intent.getStringExtra("zjhm");
			birthday_s = intent.getStringExtra("csrq");
			unit_s = intent.getStringExtra("ssdw");
			hgzl_s = intent.getStringExtra("hgzl");
			pzxx_s = intent.getStringExtra("pzxx");
			photo_s = intent.getStringExtra("photo");
			if (photo_s != null && !"".equals(photo_s)) {
				byte[] image = Base64.decode(photo_s);
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
				int height_be = opts.outHeight / 130;
				int width_be = opts.outWidth / 105;
				opts.inSampleSize = height_be > width_be ? height_be : width_be;
				if (opts.inSampleSize <= 0) {
					opts.inSampleSize = 1;
				}
				Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth + "," + opts.inSampleSize);
				opts.inJustDecodeBounds = false;
				netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
				hasImage = true;
				Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth);
			}
			onUpdateDisplayInfo(true, true, false);
		} else {
			super.onCreate(savedInstanceState, R.layout.readcard_kakou);
			input = (EditText) findViewById(R.id.cardtext);
			RadioGroup rg = (RadioGroup) findViewById(R.id.readcard_radio);
			if (cardType == READCARD_TYPE_ID_CARD) {
				rg.check(R.id.radio_btn_id);
			} else if (cardType == READCARD_TYPE_IC_CARD) {
				rg.check(R.id.radio_btn_ic);
			} else {
				rg.check(R.id.radio_btn_ic);
			}
			if (from != null && from.equals("03")) {
				findViewById(R.id.readcard_radio).setVisibility(View.GONE);
				setXjddTextView();
				if (findViewById(R.id.bigIcon) != null) {
					findViewById(R.id.bigIcon).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.smallIcon) != null) {
					findViewById(R.id.smallIcon).setVisibility(View.GONE);
				}
			} else {
				findViewById(R.id.readcard_radio).setVisibility(View.GONE);
				if (findViewById(R.id.bigIcon) != null) {
					findViewById(R.id.bigIcon).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.smallIcon) != null) {
					findViewById(R.id.smallIcon).setVisibility(View.GONE);
				}
			}
			Button btn = ((Button) findViewById(R.id.btnok));
			btn.setOnClickListener(clickOKButtonListener);
			btn.setOnKeyListener(btnKeyListener);
			btn = ((Button) findViewById(R.id.btnsel));
			TextView btn_title = (TextView) findViewById(R.id.btnsel_title);
			if (bindType != -1) {
				btn.setOnClickListener(selShipClickListener);
				btn.setOnKeyListener(btnKeyListener);

				// 船舶绑定---启动IC读卡器
				if (/* bindType == GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS && */cardType == KaKouReadCard.READCARD_TYPE_IC_CARD) {
					// 船舶绑定读默认区域，刷卡读加密区域
					readMode = ICReadEntity.READ_TYPE_DEFAULT;

				}
				if (btn_title != null) {
					btn_title.setOnClickListener(selShipClickListener);
				}
				if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					if (btn_title == null) {
						btn.setText(R.string.select_kakou);
					} else {
						btn_title.setText(R.string.select_kakou);
					}
				}
			} else if (from.equals("02")) {
				sbGoodsCheck();
				readMode = ICReadEntity.READ_TYPE_SAFE;
				if (btn_title == null) {
					btn.setText(R.string.Manual_Select);
				} else {
					btn_title.setText(R.string.Manual_Select);
				}
				btn.setOnClickListener(selPersonClickListener);
				btn.setOnKeyListener(btnKeyListener);
				if (btn_title != null) {
					btn_title.setOnClickListener(selPersonClickListener);
				}
			} else {
				btn.setVisibility(View.GONE);
				// if (findViewById(R.id.bindship_search) != null) {
				// findViewById(R.id.bindship_search).setVisibility(View.GONE);
				// }
				/**
				 * 二维码扫描按钮
				 */
				
				zxingButton = (Button)findViewById(R.id.kakou_check_btnzxing);
				zxingTextView = (TextView)findViewById(R.id.kakou_check_zxing);
				
				if (findViewById(R.id.kakou_check_btnzxing) != null) {
					findViewById(R.id.kakou_check_btnzxing).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.kakou_check_zxing) != null) {
					findViewById(R.id.kakou_check_zxing).setVisibility(View.VISIBLE);
				}


				/**
				 * 手动查找按钮
				 */
				if (findViewById(R.id.btnsel) != null) {
					findViewById(R.id.btnsel).setVisibility(View.GONE);
				}
				if (findViewById(R.id.btnsel_title) != null) {
					findViewById(R.id.btnsel_title).setVisibility(View.GONE);
				}

			}

			input.setOnKeyListener(keyListener);

		}
		setMyActiveTitle(intent.getStringExtra("title"));
	}

	private void sbGoodsCheck() {
		findViewById(R.id.tikou_downup_ship_imageview).setVisibility(View.VISIBLE);
		findViewById(R.id.tikou_downup_ship_linear).setVisibility(View.VISIBLE);
		List<HashMap<String, Object>> list = SystemSetting.shipOfKK;
		HashMap<String, Object> tikoumBindShip = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");
		List<HashMap<String, Object>> listShip = new ArrayList<HashMap<String, Object>>();
		boolean contain = false;
		if (tikoumBindShip != null && list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (tikoumBindShip.get("hc").equals(list.get(i).get("hc"))) {
					contain = true;
				}
			}
			if (!contain) {
				listShip.add(tikoumBindShip);
			}
		} else if (tikoumBindShip != null && list == null) {
			listShip.add(tikoumBindShip);
		}
		if (list != null && list.size() > 0) {
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
					icinput_s = "";
					idcardtype_s = "";
					birthday_s = "";
					name_s = "";
					sex_s = "";
					country_s = "";
					unit_s = "";
					office_s = "";
					// <Old Data>onReadComplete(false, false);
					onReadComplete(false, "0");
				}
				return true;
			} else {
				icinput_s = "";
			}
		} else {
			icinput_s = "";
		}
		Boolean isVolumnKey = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
/*			if ((DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA8)||(DeviceUtils.getDeviceModel() == DeviceUtils.DEVICE_MODEL_PA9)) {
				ScanUtils.pa8Ewm(readCardHander);
				isVolumnKey = true;
			}*/
			
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

	/**
	 * 二代证阅读器读卡完毕后，或者手动输入完毕后，开始发起验证请求，
	 * 
	 * @param idcard
	 *            是否是二代证，如果是，需要显示照片
	 * @param inputType
	 *            输入方式(默认为"0")<br>
	 *            是否刷卡：0刷卡、1手动输入、2二维码扫描
	 * */
	private List<NameValuePair> paramsHis = null;//

	private void onReadComplete(boolean idcard, String inputType) {
		input = (EditText) findViewById(R.id.cardtext);
		if (input != null) {
			input.setText(idcardnumber_s);
		}
		if ("02".equals(from) || "01".equals(from)) {
			String url;
			if ("02".equals(from)) {
				url = "inspectForAcross";
			} else {
				url = "inspectForKk";
			}
			mIdcard = idcard;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (from.equals("02")) {
				params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
			} else {
				params.add(new BasicNameValuePair("kkID", voyageNumber));
				params.add(new BasicNameValuePair("kkmc", kkmc));
			}
			params.add(new BasicNameValuePair("cardNumber", idcardnumber_s));
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
				OffLineManager.request(this, new KakouAction(), url, NVPairTOMap.nameValuePairTOMap(params),
						HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID_FOR_OFFLINE);
			}
		} else if ("03".equals(from) || "04".equals(from)) {
			mIdcard = idcard;
			RadioGroup rg = (RadioGroup) findViewById(R.id.readcard_radio);
			if ((rg != null) && (rg.getCheckedRadioButtonId() == R.id.radio_btn_ic)) {
				// 电子标签
				String url = "getBaseInfoByCard";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("cardNumber", idcardnumber_s));
				params.add(new BasicNameValuePair("type", placeType + ""));
				params.add(new BasicNameValuePair("id", placeId));
				if (progressDialog != null) {
					return;
				}
				progressDialog = new ProgressDialog(this);
				progressDialog.setTitle(getString(R.string.Validing));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_NORMAL_XUNJIAN_IC);
			} else {
				HashMap<String, Object> Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
				String url = "sendSwipeRecord";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				if (Binddata != null) {
					params.add(new BasicNameValuePair("voyageNumber", (String) Binddata.get("hc")));
				} else {
					params.add(new BasicNameValuePair("voyageNumber", ""));
				}
				params.add(new BasicNameValuePair("type", SystemSetting.xunJianType));
				params.add(new BasicNameValuePair("ddID", SystemSetting.xunJianId));
				params.add(new BasicNameValuePair("cardNumber", idcardnumber_s));
				params.add(new BasicNameValuePair("defaultickey", defaultickey));
				if (sbz) {
					params.add(new BasicNameValuePair("xjlx", "06"));
				} else {
					params.add(new BasicNameValuePair("xjlx", "01"));
				}
				if (StringUtils.isNotEmpty(inputType)) {
					if ("0".equals(inputType.trim()) || "1".equals(inputType.trim()) || "2".equals(inputType.trim())) {
						params.add(new BasicNameValuePair("sfsk", inputType.trim()));
					} else {
						params.add(new BasicNameValuePair("sfsk", "0"));
					}
				} else {
					params.add(new BasicNameValuePair("sfsk", "0"));
				}
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
				params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
				if (progressDialog != null) {
					return;
				}
				progressDialog = new ProgressDialog(this);
				progressDialog.setTitle(getString(R.string.Validing));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_NORMAL_XUNJIAN_ID);
			}
		} else if (GlobalFlags.BINDSHIP_FROM_KAKOUMANAGER.equals(from) || GlobalFlags.BINDSHIP_FROM_TIKOUMANAGER.equals(from)
				|| GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN.equals(from) || GlobalFlags.BINDSHIP_FROM_SHIPSTATUS.equals(from)) {// 船舶动态---船舶绑定
			String num = input.getText().toString();
			Intent data = null;
			data = new Intent();
			data.putExtra("cardNumber", num);
			setResult(RESULT_OK, data);
			finish();
		} else {
			return;
		}
	}

	/** 解析刷卡登记平台返回的数据 */
	private boolean onParseXMLData(String str) {
		isClzj = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = "";
			boolean zjxx = false;
			boolean dkxx = false;
			HashMap<String, Object> map = null;
			tempHttpReturnXMLInfo = "";
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
						} else {
							tempHttpReturnXMLInfo = parser.nextText();
						}
					} else if ("isClzj".equals(parser.getName())) {
						String isClzjStr = parser.nextText();
						isClzj = "true".equals(isClzjStr);
					} else if ("tsxx".equals(parser.getName())) {
						// 提示信息
						tempHttpReturnXMLInfo = parser.nextText();
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = true;
						hasCardInfo = true;
					} else if ("dkxx".equals(parser.getName())) {
						// 搭靠信息
						dkxx = true;
						hasCardInfo = true;
					} else if ("txjlid".equals(parser.getName())) {
						// 通行记录id
						txjlid_s = parser.nextText();
					} else if ("isPass".equals(parser.getName())) {
						// 是否验证通过
						String strIsPass = parser.nextText();
						if ("pass".equals(strIsPass)) {
							successFlag = 1;
						} else {
							successFlag = 2;
						}

					} else if ("dkjlid".equals(parser.getName())) {
						// 搭靠记录id
						dkjlid_s = parser.nextText();
					} else if ("sfdk".equals(parser.getName())) {
						// 是否搭靠
						sfdk_s = parser.nextText();
					} else if ("sxcfx".equals(parser.getName())) {
						// 上下船方向
						sxcfx_s = parser.nextText();
					} else if ("fx".equals(parser.getName())) {
						// 上下船方向
						sxcfx_s = parser.nextText();
					} else if ("xm".equals(parser.getName())) {
						// 姓名
						if (zjxx) {
							name_s = parser.nextText();
						}
					} else if ("xb".equals(parser.getName())) {
						// 性别
						if (zjxx) {
							sex_s = parser.nextText();
						}
					} else if ("zw".equals(parser.getName())) {
						// 职务
						if (zjxx) {
							office_s = parser.nextText();
						}
					} else if ("gj".equals(parser.getName())) {
						// 国籍
						if (zjxx) {
							country_s = parser.nextText();
						}
					} else if ("hgzl".equals(parser.getName())) {
						// 海港证类
						hgzl_s = parser.nextText();
					} else if ("csrq".equals(parser.getName())) {
						// 出生日期
						if (zjxx) {
							birthday_s = parser.nextText();
						}
					} else if ("ryid".equals(parser.getName())) {
						// 人员id
						if (zjxx) {
							ryid_s = parser.nextText();
						}
					} else if ("zjhm".equals(parser.getName())) {
						// 证件号码
						if (zjxx) {
							zjhm = parser.nextText();
						} else if (dkxx) {
							dkzjhm_s = parser.nextText();
						}

					} else if ("zjlx".equals(parser.getName())) {
						// 证件类型
						if (zjxx) {
							idcardtype_s = parser.nextText();
						} else if (dkxx) {
							dkzjlx_s = parser.nextText();
						}
					} else if ("ssdw".equals(parser.getName())) {
						// 所属单位
						if (zjxx) {
							unit_s = parser.nextText();
						} else if (dkxx) {
							dkssdw_s = parser.nextText();
							unit_s = dkssdw_s;
						}
					} else if ("icpic".equals(parser.getName())) {
						// 照片信息
						if (zjxx) {
							String icpic_s = parser.nextText();
							if (icpic_s != null && icpic_s.length() > 0) {
								hasImage = true;
								byte[] image = Base64.decode(icpic_s);
								image_cgcs = Base64.decode(icpic_s);
								BitmapFactory.Options opts = new BitmapFactory.Options();
								opts.inJustDecodeBounds = true;
								netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
								int height_be = opts.outHeight / 130;
								int width_be = opts.outWidth / 105;
								opts.inSampleSize = height_be > width_be ? height_be : width_be;
								if (opts.inSampleSize <= 0) {
									opts.inSampleSize = 1;
								}
								Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth + "," + opts.inSampleSize);
								opts.inJustDecodeBounds = false;
								netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
								Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth);
							}
						}
					} else if ("yxq".equals(parser.getName())) {
						// 有效期限
						yxq_s = parser.nextText();
					} else if ("sdcb".equals(parser.getName())) {
						sdcb_s = parser.nextText();
					} else if ("cbmc".equals(parser.getName())) {
						// 船舶名称
						if (dkxx) {
							dkcbmc_s = parser.nextText();
						}
					} else if ("cgj".equals(parser.getName())) {
						// 船港籍
						if (dkxx) {
							dkcgj_s = parser.nextText();
						}
					} else if ("zzdw".equals(parser.getName())) {
						// 载重吨位
						if (dkxx) {
							dkzzdw_s = parser.nextText();
						}
					} else if ("ml".equals(parser.getName())) {
						// 马力
						if (dkxx) {
							dkml_s = parser.nextText();
						}
					} else if ("yt".equals(parser.getName())) {
						// 用途
						if (dkxx) {
							dkyt_s = parser.nextText();
						}
					} else if ("dkfw".equals(parser.getName())) {
						// 搭靠船舶（范围）
						if (dkxx) {
							dkdkfw_s = parser.nextText();
						}

					} else if ("txjl".equals(parser.getName())) {
						// 通行记录
						if (txjl == null) {
							txjl = new ArrayList<Map<String, Object>>();
						} else {
							txjl.clear();
						}
					} else if ("jl".equals(parser.getName())) {
						map = new HashMap<String, Object>();
					} else if ("txsj".equals(parser.getName())) {
						// 通行时间
						map.put("txsj", parser.nextText());
					} else if ("txfx".equals(parser.getName())) {
						// 通行方向
						map.put("txfx", parser.nextText());
					} else if ("xgcb".equals(parser.getName())) {
						// 船舶名称
						map.put("xgcb", parser.nextText());
					} else if ("txdd".equals(parser.getName())) {
						// 通行地点
						map.put("txdd", parser.nextText());
					} else if ("bjtsxx".equals(parser.getName())) {
						// 报警提示信息
						bjtsxx = parser.nextText();
					} else if ("pzmbly".equals(parser.getName())) {
						// 碰撞目标来源
						pzmbly = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if ("jl".equals(parser.getName())) {
						if (txjl == null) {
							txjl = new ArrayList<Map<String, Object>>();
						}
						txjl.add(map);
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = false;
					} else if ("dkxx".equals(parser.getName())) {
						// 搭靠信息
						dkxx = false;
					}

					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		isClzj = false;
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result:" + (str != null));
		pzmbly = null;
		bjtsxx = null;
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID_FOR_OFFLINE) {
			if (StringUtils.isEmpty(str)) {
				if (progressDialog != null) {
					progressDialog.setMessage(getString(R.string.no_data_request_web));
				}
				onReadCompleteOnLine();
				return;
			}
			isFromOffline = true;
			if (sailorFlag) {
				input = (EditText) findViewById(R.id.cardtext);
				input.setText("");
				sailorFlag = false;
			}

			httpRequestType = 0;
			setViewValueToNull();
			boolean success = false;
			success = onParseXMLData(str);
			if (isClzj) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.getToastView(getApplicationContext(), tempHttpReturnXMLInfo).show();
				BaseApplication.soundManager.onPlaySound(3, 0);
				doingZxing = false;
				msTdc = null;
				return;
			}
			if (!success || hasCardInfo == false) {
				if (progressDialog != null) {
					progressDialog.setMessage(getString(R.string.no_data_request_web));
				}
				onReadCompleteOnLine();
				return;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			old_idcardnumber_s = "";
			old_name_s = "";
			old_country_s = "";
			old_idcardtype_s = "";
			old_sex_s = "";
			old_birthday_s = "";
			old_unit_s = "";
			old_office_s = "";
			httpReturnXMLInfo = tempHttpReturnXMLInfo;
			if (hasCardInfo == false) {
				hasNotCardInfo();
				doingZxing = false;
				return;
			}
			// 初始化二维码扫描结果对象
			msTdc = null;
			setContentView(R.layout.persondetail_kakou);
			findViewById(R.id.btnScan).setVisibility(View.VISIBLE);
			onUpdateDisplayInfo(success, true, true);
			// 修改通行方向按钮和声音控制。
			if (successFlag == 1) {
				BaseApplication.soundManager.onPlaySound(2, 0);// 成功提示音
			} else if (successFlag == 2) {
				BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
			}
			doingZxing = false;

		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID) {
			isFromOffline = false;
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

			if (sailorFlag) {
				input = (EditText) findViewById(R.id.cardtext);
				input.setText("");
				sailorFlag = false;
			}
			boolean success = false;
			httpRequestType = 0;
			if ((StringUtils.isEmpty(str) && !getState(FunctionSetting.bdtxyz, true))) {
				httpRequestNull();
				doingZxing = false;
				return;
			} else if (StringUtils.isEmpty(str)) {
				hasNotCardInfo();
				doingZxing = false;
				return;
			}
			setViewValueToNull();
			success = onParseXMLData(str);
			if (!success) {
				provingFail();
				doingZxing = false;
				return;
			}
			old_idcardnumber_s = "";
			old_name_s = "";
			old_country_s = "";
			old_idcardtype_s = "";
			old_sex_s = "";
			old_birthday_s = "";
			old_unit_s = "";
			old_office_s = "";
			httpReturnXMLInfo = tempHttpReturnXMLInfo;
			if (hasCardInfo == false) {
				hasNotCardInfo();
				doingZxing = false;
				return;
			}
			// 初始化二维码扫描结果对象
			msTdc = null;
			setContentView(R.layout.persondetail_kakou);
			findViewById(R.id.btnScan).setVisibility(View.VISIBLE);
			onUpdateDisplayInfo(success, true, false);
			// 修改通行方向按钮和声音控制。
			if (successFlag == 1) {
				BaseApplication.soundManager.onPlaySound(2, 0);// 成功提示音
			} else if (successFlag == 2) {
				BaseApplication.soundManager.onPlaySound(3, 0);// 验证失败提示音
			}
			doingZxing = false;

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
				modifySuccess();
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.modify_failure)).show();
			}

		}
		zxingInfo = "";
	}

	private void onReadCompleteOnLine() {
		String url = "inspectForKk";
		NetWorkManager.request(this, url, paramsHis, HTTPREQUEST_TYPE_FOR_TRAFFIC_VALID);
	}

	/**
	 * 
	 * @方法名：setViewValueToNull
	 * @功能说明：历史数据清空
	 * @author liums
	 * @date 2013-10-30 下午5:27:52
	 */
	private void setViewValueToNull() {
		txjlid_s = "";
		dkjlid_s = "";
		sfdk_s = "";
		sxcfx_s = "";
		ryid_s = "";
		hgzl_s = "";
		yxq_s = "";
		dkzjhm_s = "";
		dkzjlx_s = "";
		dkssdw_s = "";
		dkcbmc_s = "";
		dkcgj_s = "";
		dkzzdw_s = "";
		dkml_s = "";
		dkyt_s = "";
		dkdkfw_s = "";
		hasCardInfo = false;
		hasImage = false;
		successFlag = 0;
		time_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 
	 * @方法名：modifySuccess
	 * @功能说明：通行方向修改成功
	 * @author liums
	 * @date 2013-10-30 下午5:25:36
	 */
	private void modifySuccess() {
		if (sxcfx_s.equals("0")) {
			if (from.equals("02")) {
				((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
						+ "<font color=\"#acacac\">" + "下船" + "</font>"));
			} else {
				((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction_kk)
						+ "<font color=\"#acacac\">" + "出卡口" + "</font>"));
			}
			findViewById(R.id.imageView_Traffic_direction).setVisibility(View.VISIBLE);
			((ImageView) findViewById(R.id.imageView_Traffic_direction)).setImageResource(R.drawable.down);
			sxcfx_s = "1";
		} else if (sxcfx_s.equals("1")) {
			if (from.equals("02")) {
				((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
						+ "<font color=\"#acacac\">" + "上船" + "</font>"));
			} else {
				((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction_kk)
						+ "<font color=\"#acacac\">" + "进卡口" + "</font>"));
			}
			findViewById(R.id.imageView_Traffic_direction).setVisibility(View.VISIBLE);
			((ImageView) findViewById(R.id.imageView_Traffic_direction)).setImageResource(R.drawable.up);
			sxcfx_s = "0";
		}
		// Toast.makeText(ReadcardActivity.this,
		// R.string.modify_success,
		// Toast.LENGTH_LONG).show();
		HgqwToast.getToastView(getApplicationContext(), getString(R.string.modify_success)).show();
		((Button) findViewById(R.id.btnmodifyPassDirection)).setEnabled(false);
	}

	private void httpRequestNull() {
		HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
		if (old_idcardnumber_s.length() > 0) {
			idcardnumber_s = old_idcardnumber_s;
			old_idcardnumber_s = "";
		}
		if (old_name_s.length() > 0) {
			name_s = old_name_s;
			old_name_s = "";
		}
		if (old_country_s.length() > 0) {
			country_s = old_country_s;
			old_country_s = "";
		}
		if (old_idcardtype_s.length() > 0) {
			idcardtype_s = old_idcardtype_s;
			old_idcardtype_s = "";
		}
		if (old_sex_s.length() > 0) {
			sex_s = old_sex_s;
			old_sex_s = "";
		}
		if (old_birthday_s.length() > 0) {
			birthday_s = old_birthday_s;
			old_birthday_s = "";
		}
		if (old_unit_s.length() > 0) {
			unit_s = old_unit_s;
			old_unit_s = "";
		}
		if (old_office_s.length() > 0) {
			office_s = old_office_s;
			old_office_s = "";
		}
		return;
	}

	private void hasNotCardInfo() {
		flagRegister = false;
		BaseApplication.soundManager.onPlaySound(3, 0);
		if (msTdc != null) {
			idcardnumber_s = msTdc.getZjhm();
			if (msTdc.getScanType() == ScanDataUtil.SCAN_TYPE_DENG_LUN) {
				// 所属单位
				unit_s = msTdc.getSsdw();
				// 姓名
				name_s = msTdc.getXm();
				// 性别
				sex_s = msTdc.getXbdm();
				// 出生日期
				String date = msTdc.getCsrq();
				if (StringUtils.isNotEmpty(date) && date.length() == 8) {
					StringBuffer dateSb = new StringBuffer(date.substring(0, 4)).append("-").append(date.substring(4, 6)).append("-")
							.append(date.substring(6, 8));
					birthday_s = String.valueOf(dateSb);
				}
			}
		}
		msTdc = null;
		Intent intent = new Intent();
		intent.putExtra("from", from);
		intent.putExtra("info", httpReturnXMLInfo);
		intent.putExtra("voyageNumber", voyageNumber);
		intent.putExtra("zjhm", idcardnumber_s);
		intent.putExtra("xm", name_s);
		intent.putExtra("xb", sex_s);
		intent.putExtra("zjzl", idcardtype_s);
		intent.putExtra("csrq", birthday_s);
		intent.putExtra("ssdw", unit_s);
		intent.putExtra("zw", office_s);
		intent.putExtra("gj", country_s);
		// intent.putExtra("sailorFlag", sailorFlag);

		intent.setClass(getApplicationContext(), RegisterPersoninfoActivity.class);
		startActivityForResult(intent, STARTACTIVITY_FOR_REGISTER_PERSONINFO);
		return;
	}

	/**
	 * 
	 * @方法名：provingFail
	 * @功能说明：验证失败逻辑
	 * @author liums
	 * @date 2013-10-30 下午5:21:28
	 */
	private void provingFail() {
		HgqwToast.getToastView(getApplicationContext(), tempHttpReturnXMLInfo).show();
		if (old_idcardnumber_s.length() > 0) {
			idcardnumber_s = old_idcardnumber_s;
			old_idcardnumber_s = "";
		}
		if (old_name_s.length() > 0) {
			name_s = old_name_s;
			old_name_s = "";
		}
		if (old_country_s.length() > 0) {
			country_s = old_country_s;
			old_country_s = "";
		}
		if (old_idcardtype_s.length() > 0) {
			idcardtype_s = old_idcardtype_s;
			old_idcardtype_s = "";
		}
		if (old_sex_s.length() > 0) {
			sex_s = old_sex_s;
			old_sex_s = "";
		}
		if (old_birthday_s.length() > 0) {
			birthday_s = old_birthday_s;
			old_birthday_s = "";
		}
		if (old_unit_s.length() > 0) {
			unit_s = old_unit_s;
			old_unit_s = "";
		}
		if (old_office_s.length() > 0) {
			office_s = old_office_s;
			old_office_s = "";
		}
		BaseApplication.soundManager.onPlaySound(3, 0);
		return;
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

		((RadioButton) findViewById(R.id.radio_btn_ic)).setText("巡查地点：" + xjddName);
	}

	/**
	 * 显示巡查巡检的结果
	 * 
	 * @param success
	 *            是否成功
	 * @param 职务是否是代码
	 *            ，true表示代码，需要从数据字典翻译，否则直接显示
	 * */
	private void onUpdateDisplayXunjianInfo(boolean success, boolean officecode) {
		boolean dkwl = false;
		if (sfdk_s != null && sfdk_s.equals("1")) {
			dkwl = true;
		}
		HashMap<String, Object> Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (sbz) {
			// 士兵证
			((TextView) findViewById(R.id.Validation_results)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.name) + "：" + "<font color=\"#acacac\">"
					+ (name_s == null ? "" : name_s) + "</font>"));

			((TextView) findViewById(R.id.detail_sex)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_country)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_birthday)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_cardtype)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_cardnum)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.unit) + "：" + "<font color=\"#acacac\">"
					+ (unit_s == null ? "" : unit_s) + "</font>"));
			((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office) + "：" + "<font color=\"#acacac\">"
					+ (office_s == null ? "" : office_s) + "</font>"));
			((TextView) findViewById(R.id.detail_sbk_zqdd)).setText(Html.fromHtml(getString(R.string.zqdd) + "：" + "<font color=\"#acacac\">"
					+ (zqdd_s == null ? "" : zqdd_s) + "</font>"));
			((TextView) findViewById(R.id.yxq)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.sdcb)).setVisibility(View.GONE);
		} else if (dkwl) {
			((TextView) findViewById(R.id.Validation_results)).setText(Html.fromHtml(getString(R.string.Valid_results) + "<font color=\"#acacac\">"
					+ httpReturnXMLInfo + "</font>"));
			if (hgzl_s != null && hgzl_s.length() > 0) {
				if (hgzl_s.equals("48")) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_denglunz) + getString(R.string.cardinfo));
				} else if (hgzl_s.equals("50")) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_dengluz) + getString(R.string.cardinfo));
				} else if (hgzl_s.equals("52")) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_dakaoz) + getString(R.string.cardinfo));
				} else if (YfZjxxConstant.ZJLX_XDQY.equals(hgzl_s)) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_xdqy) + getString(R.string.cardinfo));
				} else {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.cardinfo));
				}

			}
			((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.shipname) + "<font color=\"#acacac\">"
					+ (dkcbmc_s == null ? "" : dkcbmc_s) + "</font>"));
			((TextView) findViewById(R.id.detail_sex)).setText(Html.fromHtml(getString(R.string.ship_gj) + "<font color=\"#acacac\">"
					+ (dkcgj_s == null ? "" : dkcgj_s) + "</font>"));
			((TextView) findViewById(R.id.detail_country)).setText(Html.fromHtml(getString(R.string.ship_zzdw) + "<font color=\"#acacac\">"
					+ (dkzzdw_s == null ? "" : dkzzdw_s) + "</font>"));
			((TextView) findViewById(R.id.detail_birthday)).setText(Html.fromHtml(getString(R.string.ship_ml) + "<font color=\"#acacac\">"
					+ (dkml_s == null ? "" : dkml_s) + "</font>"));
			((TextView) findViewById(R.id.detail_cardtype)).setText(Html.fromHtml(getString(R.string.goods_check_unit) + "<font color=\"#acacac\">"
					+ (dkssdw_s == null ? "" : dkssdw_s) + "</font>"));
			((TextView) findViewById(R.id.detail_cardnum)).setText(Html.fromHtml(getString(R.string.ship_yt) + "<font color=\"#acacac\">"
					+ (dkyt_s == null ? "" : dkyt_s) + "</font>"));
			((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.ship_dkcb) + "<font color=\"#acacac\">"
					+ (dkdkfw_s == null ? "" : dkdkfw_s) + "</font>"));
			((TextView) findViewById(R.id.detail_office)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.yxq)).setVisibility(View.GONE);
			// ((TextView) findViewById(R.id.bhcyx)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.sdcb)).setVisibility(View.GONE);
		} else {
			if (hgzl_s != null && hgzl_s.length() > 0) {
				if (hgzl_s.equals("48")) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_denglunz) + getString(R.string.cardinfo));
				} else if (hgzl_s.equals("50")) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_dengluz) + getString(R.string.cardinfo));
				} else if (hgzl_s.equals("52")) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_dakaoz) + getString(R.string.cardinfo));
				} else if (YfZjxxConstant.ZJLX_XDQY.equals(hgzl_s)) {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_xdqy) + getString(R.string.cardinfo));
				} else {
					((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.cardinfo));
				}

			}
			if (Binddata != null) {
				// 船上
				((TextView) findViewById(R.id.Validation_results)).setText(Html.fromHtml(getString(R.string.Valid_results)
						+ "<font color=\"#acacac\">" + httpReturnXMLInfo + "</font>"));
				((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.name) + "：" + "<font color=\"#acacac\">"
						+ (name_s == null ? "" : name_s) + "</font>"));
				String tempstr;
				if ((sex_s != null) && (sex_s.length() > 0)) {
					tempstr = DataDictionary.getDataDictionaryName(sex_s, DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE);
				} else {
					tempstr = null;
				}

				((TextView) findViewById(R.id.detail_sex)).setText(Html.fromHtml(getString(R.string.sex) + "：" + "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
				if ((country_s != null) && (country_s.length() > 0)) {
					tempstr = DataDictionary.getCountryName(country_s);
				} else {
					tempstr = null;
				}
				((TextView) findViewById(R.id.detail_country)).setText(Html.fromHtml(getString(R.string.country) + "：" + "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
				((TextView) findViewById(R.id.detail_birthday)).setText(Html.fromHtml(getString(R.string.birthday) + "：" + "<font color=\"#acacac\">"
						+ (birthday_s == null ? "" : birthday_s) + "</font>"));
				if ((idcardtype_s != null) && (idcardtype_s.length() > 0)) {
					tempstr = DataDictionary.getDataDictionaryName(idcardtype_s, DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE);
				} else {
					tempstr = null;
				}
				((TextView) findViewById(R.id.detail_cardtype)).setText(Html.fromHtml(getString(R.string.cardtype) + "：" + "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
				((TextView) findViewById(R.id.detail_cardnum)).setText(Html.fromHtml(getString(R.string.cardnum) + "：" + "<font color=\"#acacac\">"
						+ (idcardnumber_s == null ? "" : idcardnumber_s) + "</font>"));
				((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.unit) + "：" + "<font color=\"#acacac\">"
						+ (unit_s == null ? "" : unit_s) + "</font>"));
				if (officecode) {
					((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office)
							+ "："
							+ "<font color=\"#acacac\">"
							+ (office_s == null ? "" : DataDictionary.getDataDictionaryOfficeName(office_s,
									((hgzl_s != null && hgzl_s.equals("50")) ? DataDictionary.DATADICTIONARY_TYPE_CBYGZW
											: DataDictionary.DATADICTIONARY_TYPE_DLRYZW))) + "</font>"));
				} else {
					((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office) + "：" + "<font color=\"#acacac\">"
							+ (office_s == null ? "" : office_s) + "</font>"));
				}
				((TextView) findViewById(R.id.yxq)).setVisibility(View.GONE);
				// ((TextView)
				// findViewById(R.id.bhcyx)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.sdcb)).setVisibility(View.GONE);
			} else {
				// 船下
				if (hgzl_s != null && hgzl_s.equals("48")) {
					// 登轮许可证
				} else {
					// 登陆证、其他证件
				}
				((TextView) findViewById(R.id.Validation_results)).setText(Html.fromHtml(getString(R.string.Valid_results)
						+ "<font color=\"#acacac\">" + httpReturnXMLInfo + "</font>"));
				((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.name) + "：" + "<font color=\"#acacac\">"
						+ (name_s == null ? "" : name_s) + "</font>"));
				String tempstr;
				if ((sex_s != null) && (sex_s.length() > 0)) {
					tempstr = DataDictionary.getDataDictionaryName(sex_s, DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE);
				} else {
					tempstr = null;
				}

				((TextView) findViewById(R.id.detail_sex)).setText(Html.fromHtml(getString(R.string.sex) + "：" + "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
				if ((country_s != null) && (country_s.length() > 0)) {
					tempstr = DataDictionary.getCountryName(country_s);
				} else {
					tempstr = null;
				}
				((TextView) findViewById(R.id.detail_country)).setText(Html.fromHtml(getString(R.string.country) + "：" + "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
				((TextView) findViewById(R.id.detail_birthday)).setText(Html.fromHtml(getString(R.string.birthday) + "：" + "<font color=\"#acacac\">"
						+ (birthday_s == null ? "" : birthday_s) + "</font>"));
				if ((idcardtype_s != null) && (idcardtype_s.length() > 0)) {
					tempstr = DataDictionary.getDataDictionaryName(idcardtype_s, DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE);
				} else {
					tempstr = null;
				}
				((TextView) findViewById(R.id.detail_cardtype)).setText(Html.fromHtml(getString(R.string.cardtype) + "：" + "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
				((TextView) findViewById(R.id.detail_cardnum)).setText(Html.fromHtml(getString(R.string.cardnum) + "：" + "<font color=\"#acacac\">"
						+ (idcardnumber_s == null ? "" : idcardnumber_s) + "</font>"));
				if (officecode) {
					((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office)
							+ "："
							+ "<font color=\"#acacac\">"
							+ (office_s == null ? "" : DataDictionary.getDataDictionaryOfficeName(office_s,
									((hgzl_s != null && hgzl_s.equals("50")) ? DataDictionary.DATADICTIONARY_TYPE_CBYGZW
											: DataDictionary.DATADICTIONARY_TYPE_DLRYZW))) + "</font>"));
				} else {
					((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office) + "：" + "<font color=\"#acacac\">"
							+ (office_s == null ? "" : office_s) + "</font>"));
				}
				if (hgzl_s != null && (hgzl_s.equals("48") || hgzl_s.equals("50"))) {
					((TextView) findViewById(R.id.yxq)).setText(Html.fromHtml(getString(R.string.yxqx) + "：" + "<font color=\"#acacac\">"
							+ (yxq_s == null ? "" : yxq_s) + "</font>"));
					// ((TextView)
					// findViewById(R.id.bhcyx)).setText(Html.fromHtml(getString(R.string.bhcyx)
					// + "：" + "<font color=\"#acacac\">"
					// + (bhcyx_s) + "</font>"));
				} else {
					((TextView) findViewById(R.id.yxq)).setVisibility(View.GONE);
				}
				if (hgzl_s != null && hgzl_s.equals("50")) {
					((TextView) findViewById(R.id.detail_unit)).setVisibility(View.GONE);
					((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml(getString(R.string.xunjian_fwcb) + "：" + "<font color=\"#acacac\">"
							+ (sdcb_s == null ? "" : sdcb_s) + "</font>"));
				} else {
					((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.unit) + "：" + "<font color=\"#acacac\">"
							+ (unit_s == null ? "" : unit_s) + "</font>"));
					((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml(getString(R.string.xunjian_sdcb) + "：" + "<font color=\"#acacac\">"
							+ (sdcb_s == null ? "" : sdcb_s) + "</font>"));

				}
			}
		}

		ImageView imageView_bg = (ImageView) findViewById(R.id.imageView_photo);
		ImageView imageView = (ImageView) findViewById(R.id.imageView_network_photo);
		if (hasImage && netWorkImage != null) {
			imageView_bg.setVisibility(View.GONE);
			LayoutParams para;
			para = imageView.getLayoutParams();
			int height_be = netWorkImage.getHeight() / 130;
			int width_be = netWorkImage.getWidth() / 105;
			if (height_be > width_be) {
				para.height = 130 * 2;
				para.width = 130 * 2 * netWorkImage.getWidth() / netWorkImage.getHeight();
			} else {
				para.width = 105 * 2;
				para.height = 105 * 2 * netWorkImage.getHeight() / netWorkImage.getWidth();
			}
			imageView.setLayoutParams(para);
			imageView.setImageBitmap(netWorkImage);
		} else {
			imageView.setVisibility(View.GONE);
		}
		Button _btnnoidcard = (Button) findViewById(R.id.btnNoIDCard);
		_btnnoidcard.setOnClickListener(retryInputCardNumListener);
		Button _btnexcept = (Button) findViewById(R.id.btnExceptionRegist);
		_btnexcept.setOnClickListener(exceptRegistListener);
		if (Binddata != null || !(hgzl_s != null && hgzl_s.equals("48"))) {
			if (dkwl) {
				findViewById(R.id.xunjian_txjl_ll).setVisibility(View.GONE);
			} else {
				adapter = new MyAdapter(this);
				listView = (ListView) findViewById(R.id.listview);
				listView.setAdapter(adapter);
				listView.setFocusable(false);

				if (adapter.getCount() > 0) {
					findViewById(R.id.xunjian_txjl_ll).setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					int totalHeight = 0;
					for (int i = 0; i < adapter.getCount(); i++) {
						View listItem = adapter.getView(i, null, listView);
						listItem.measure(0, 0);
						totalHeight += listItem.getMeasuredHeight();
					}
					ViewGroup.LayoutParams params = listView.getLayoutParams();
					params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
					listView.setLayoutParams(params);
				} else {
					findViewById(R.id.xunjian_txjl_ll).setVisibility(View.GONE);
				}
			}
		} else {
			findViewById(R.id.xunjian_txjl_ll).setVisibility(View.GONE);
		}
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
		// TODO Auto-generated method stub
		boolean dkwl = false;
		if (from.equals("02") && (sfdk_s != null && sfdk_s.equals("1"))) {
			dkwl = true;
		}
		if (from.equals("04")) {
			findViewById(R.id.Validation_results).setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.Validation_results)).setText(Html.fromHtml(getString(R.string.Valid_results) + "<font color=\"#acacac\">"
					+ httpReturnXMLInfo + "</font>"));
		}
		((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.sdcb)).setVisibility(View.GONE);
		if (hgzl_s != null && hgzl_s.length() > 0) {
			// 显示有效期标准：
			// 只有登轮证、搭靠证、限定区域显示
			if (hgzl_s.equals("48")) {
				// 登轮证
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_denglunz) + getString(R.string.cardinfo));
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.detail_yxqz)).setText(Html.fromHtml(getString(R.string.yxq) + "<font color=\"#acacac\">"
						+ (yxq_s == null ? "" : yxq_s) + "</font>"));
				((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml(getString(R.string.xunjian_sdcb) + "：" + "<font color=\"#acacac\">"
						+ (sdcb_s == null ? "" : sdcb_s) + "</font>"));

			} else if (hgzl_s.equals("50")) {
				// 登陆证
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_dengluz) + getString(R.string.cardinfo));
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.GONE);
				((TextView) findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml(getString(R.string.xunjian_sdcb) + "：" + "<font color=\"#acacac\">"
						+ (sdcb_s == null ? "" : sdcb_s) + "</font>"));
			} else if (hgzl_s.equals("52")) {
				// 搭靠证
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.detail_yxqz)).setText(Html.fromHtml(getString(R.string.yxq) + "<font color=\"#acacac\">"
						+ (yxq_s == null ? "" : yxq_s) + "</font>"));
				((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml("搭靠船舶（范围）" + "：" + "<font color=\"#acacac\">"
						+ (sdcb_s == null ? "" : sdcb_s) + "</font>"));
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(hgzl_s)) {
				// 限定区域通行证
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.hgzl_xdqy) + getString(R.string.cardinfo));
				((TextView) findViewById(R.id.detail_yxqz)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.sdcb)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.detail_yxqz)).setText(Html.fromHtml(getString(R.string.yxq) + "<font color=\"#acacac\">"
						+ (yxq_s == null ? "" : yxq_s) + "</font>"));
				((TextView) findViewById(R.id.sdcb)).setText(Html.fromHtml("区域范围" + "：" + "<font color=\"#acacac\">"
						+ (sdcb_s == null ? "" : sdcb_s) + "</font>"));
			} else {
				((TextView) findViewById(R.id.cardtypetitle)).setText(getString(R.string.cardinfo));
				// ((TextView)
				// findViewById(R.id.detail_yxqz)).setVisibility(View.GONE);
			}

		}
		if (dkwl) {
			findViewById(R.id.Traffic_direction).setVisibility(View.GONE);
			findViewById(R.id.imageView_Traffic_direction).setVisibility(View.GONE);
			((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.shipname) + "<font color=\"#acacac\">"
					+ (dkcbmc_s == null ? "" : dkcbmc_s) + "</font>"));
			((TextView) findViewById(R.id.detail_sex)).setText(Html.fromHtml(getString(R.string.ship_gj) + "<font color=\"#acacac\">"
					+ (dkcgj_s == null ? "" : dkcgj_s) + "</font>"));
			((TextView) findViewById(R.id.detail_country)).setText(Html.fromHtml(getString(R.string.ship_zzdw) + "<font color=\"#acacac\">"
					+ (dkzzdw_s == null ? "" : dkzzdw_s) + "</font>"));
			((TextView) findViewById(R.id.detail_birthday)).setText(Html.fromHtml(getString(R.string.ship_ml) + "<font color=\"#acacac\">"
					+ (dkml_s == null ? "" : dkml_s) + "</font>"));
			((TextView) findViewById(R.id.detail_cardtype)).setText(Html.fromHtml(getString(R.string.goods_check_unit) + "<font color=\"#acacac\">"
					+ (dkssdw_s == null ? "" : dkssdw_s) + "</font>"));
			((TextView) findViewById(R.id.detail_cardnum)).setText(Html.fromHtml(getString(R.string.ship_yt) + "<font color=\"#acacac\">"
					+ (dkyt_s == null ? "" : dkyt_s) + "</font>"));
			((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.ship_dkcb) + "<font color=\"#acacac\">"
					+ (dkdkfw_s == null ? "" : dkdkfw_s) + "</font>"));
			((TextView) findViewById(R.id.detail_office)).setVisibility(View.GONE);
		} else {
			if (from.equals("04")) {
				((TextView) findViewById(R.id.Traffic_direction)).setVisibility(View.GONE);
				findViewById(R.id.imageView_Traffic_direction).setVisibility(View.GONE);
			} else {
				if (successFlag==1) {
					if (sxcfx_s.equals("0")) {
						if (from.equals("02")) {
							((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
									+ "<font color=\"#acacac\">" + "上船" + "</font>"));
						} else {
							((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction_kk)
									+ "<font color=\"#acacac\">" + "进卡口" + "</font>"));
						}
						findViewById(R.id.imageView_Traffic_direction).setVisibility(View.VISIBLE);
						((ImageView) findViewById(R.id.imageView_Traffic_direction)).setImageResource(R.drawable.up);
					} else if (sxcfx_s.equals("1")) {
						if (from.equals("02")) {
							((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction)
									+ "<font color=\"#acacac\">" + "下船" + "</font>"));
						} else {
							((TextView) findViewById(R.id.Traffic_direction)).setText(Html.fromHtml(getString(R.string.Traffic_direction_kk)
									+ "<font color=\"#acacac\">" + "出卡口" + "</font>"));
						}
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
			}
			((TextView) findViewById(R.id.detail_name)).setText(Html.fromHtml(getString(R.string.name) + "：" + "<font color=\"#acacac\">"
					+ (name_s == null ? "" : name_s) + "</font>"));
			String tempstr;
			if ((sex_s != null) && (sex_s.length() > 0)) {
				tempstr = DataDictionary.getDataDictionaryName(sex_s, DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE);
			} else {
				tempstr = null;
			}

			((TextView) findViewById(R.id.detail_sex)).setText(Html.fromHtml(getString(R.string.sex) + "：" + "<font color=\"#acacac\">"
					+ (tempstr == null ? "" : tempstr) + "</font>"));
			if ((country_s != null) && (country_s.length() > 0)) {
				tempstr = DataDictionary.getCountryName(country_s);
			} else {
				tempstr = null;
			}
			((TextView) findViewById(R.id.detail_country)).setText(Html.fromHtml(getString(R.string.country) + "：" + "<font color=\"#acacac\">"
					+ (tempstr == null ? "" : tempstr) + "</font>"));
			((TextView) findViewById(R.id.detail_birthday)).setText(Html.fromHtml(getString(R.string.birthday) + "：" + "<font color=\"#acacac\">"
					+ (birthday_s == null ? "" : birthday_s) + "</font>"));
			if ((idcardtype_s != null) && (idcardtype_s.length() > 0)) {
				tempstr = DataDictionary.getDataDictionaryName(idcardtype_s, DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE);
			} else {
				tempstr = null;
			}
			((TextView) findViewById(R.id.detail_cardtype)).setText(Html.fromHtml(getString(R.string.cardtype) + "：" + "<font color=\"#acacac\">"
					+ (tempstr == null ? "" : tempstr) + "</font>"));
			((TextView) findViewById(R.id.detail_cardnum)).setText(Html.fromHtml(getString(R.string.cardnum) + "：" + "<font color=\"#acacac\">"
					+ (zjhm == null ? "" : zjhm) + "</font>"));
			((TextView) findViewById(R.id.detail_unit)).setText(Html.fromHtml(getString(R.string.unit) + "：" + "<font color=\"#acacac\">"
					+ (unit_s == null ? "" : unit_s) + "</font>"));
			if (officecode) {
				((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (office_s == null ? "" : DataDictionary.getDataDictionaryOfficeName(office_s,
								((hgzl_s != null && hgzl_s.equals("50")) ? DataDictionary.DATADICTIONARY_TYPE_CBYGZW
										: DataDictionary.DATADICTIONARY_TYPE_DLRYZW))) + "</font>"));
			} else {
				((TextView) findViewById(R.id.detail_office)).setText(Html.fromHtml(getString(R.string.office) + "：" + "<font color=\"#acacac\">"
						+ (office_s == null ? "" : office_s) + "</font>"));
			}
			if (from.equals("04") && pzxx_s != null && pzxx_s.length() != 0) {
				((TextView) findViewById(R.id.detail_pzxx)).setText(Html.fromHtml("<font color=\"#acacac\">" + pzxx_s + "</font>"));
				findViewById(R.id.detail_pzxx).setVisibility(View.VISIBLE);
				findViewById(R.id.detail_pzxx_static).setVisibility(View.VISIBLE);
			}
		}
		PersonInfo personInfo = new PersonInfo();
		personInfo.setBjtsxx(bjtsxx);
		personInfo.setPzmbly(pzmbly);
		personInfo.setName(name_s);
		ShowViewUtil.showPzbjxx(personInfo, this, GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER, isOffline);

		ImageView imageView_bg = (ImageView) findViewById(R.id.imageView_photo);
		ImageView imageView = (ImageView) findViewById(R.id.imageView_network_photo);
		if (hasImage && netWorkImage != null) {
			ImageFactory.setImage(imageView_bg, netWorkImage);
		} else if (isOffline) {
			Bitmap netWorkImage = ImageFactory.loadImage(zjhm);
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
				if (from.equals("04")) {
					// <Old Data>onReadComplete(false, true);
					onReadComplete(false, "1");
				} else {
					if (txjlid_s != null && txjlid_s.length() > 0) {
						String url;
						if (from.equals("02")) {
							url = "modifyPassDirection";
						} else {
							url = "modifyKkPassDirection";
						}
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("recordid", txjlid_s));
						params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
						params.add(new BasicNameValuePair("passDirection", (sxcfx_s != null && sxcfx_s.equals("0")) ? "1" : "0"));
						params.add(new BasicNameValuePair("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System
								.currentTimeMillis()))));
						if (progressDialog != null) {
							return;
						}
						progressDialog = new ProgressDialog(KaKouReadCard.this);
						progressDialog.setTitle(getString(R.string.waiting));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();

						if (!isFromOffline) {
							NetWorkManager.request(KaKouReadCard.this, url, params, HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION);
						} else {
							params.add(new BasicNameValuePair("kkID", voyageNumber));
							params.add(new BasicNameValuePair("hgzl", hgzl_s));
							params.add(new BasicNameValuePair("ryid", ryid_s));
							params.add(new BasicNameValuePair("kkmc", kkmc));
							OffLineManager.request(KaKouReadCard.this, new KakouAction(), url, NVPairTOMap.nameValuePairTOMap(params),
									HTTPREQUEST_TYPE_FOR_MODIFY_PASSDIRECTION);
						}
					}
				}
			}
		});
		Button _btnnoidcard = (Button) findViewById(R.id.btnNoIDCard);
		Button _btnexcept = (Button) findViewById(R.id.btnExceptionRegist);
		_btnexcept.setOnClickListener(exceptRegistListener);
		Button _btngoods = (Button) findViewById(R.id.btnRecordGoods);
		_btngoods.setOnClickListener(goodsRecordListener);
		if (from.equals("04")) {
			_btnmodifyPassDirection.setText(R.string.xunjian_person_save_record);
			_btngoods.setText(R.string.Exception_regist);
			_btngoods.setOnClickListener(exceptRegistListener);
			_btnexcept.setVisibility(View.GONE);
			_btnnoidcard.setVisibility(View.GONE);
		} else {
			if (dkwl || success == false || txjlid_s == null || txjlid_s.length() == 0) {
				_btnmodifyPassDirection.setVisibility(View.INVISIBLE);
			} else {
				_btnmodifyPassDirection.setVisibility(View.VISIBLE);
			}

			_btnnoidcard.setOnClickListener(retryInputCardNumListener);
			if ((!dkwl) && success && successFlag == 1 && hgzl_s != null && (txjlid_s != null && txjlid_s.length() > 0)) {
				_btngoods.setVisibility(View.VISIBLE);
			} else {
				_btngoods.setVisibility(View.INVISIBLE);
			}
		}
	}

	/** 解析巡查巡检返回的数据 */
	private boolean onParseXunJianXMLData(String str) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = "";
			boolean zjxx = false;
			boolean dkxx = false;
			HashMap<String, Object> map = null;
			tempHttpReturnXMLInfo = "";
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
						} else {
							tempHttpReturnXMLInfo = parser.nextText();
						}
					} else if ("tsxx".equals(parser.getName())) {
						// 提示信息
						tempHttpReturnXMLInfo = parser.nextText();
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = true;
						hasCardInfo = true;
					} else if ("dkxx".equals(parser.getName())) {
						// 搭靠信息
						dkxx = true;
						hasCardInfo = true;
					} else if ("xcxsid".equals(parser.getName())) {
						// 巡查巡检id
						xcxsid_s = parser.nextText();
					} else if ("isPass".equals(parser.getName())) {
						// 是否验证通过
						String strIsPass = parser.nextText();
						if ("pass".equals(strIsPass)) {
							successFlag = 1;
						} else {
							successFlag = 2;
						}
					} else if ("dkjlid".equals(parser.getName())) {
						// 巡查巡检id
						dkjlid_s = parser.nextText();
					} else if ("sfdk".equals(parser.getName())) {
						// 0否，1是;如证件类型为搭靠证返回1，否则返回0。 2 士兵证
						sfdk_s = parser.nextText();
					} else if ("cgcsid".equals(parser.getName())) {
						// 查岗查哨id
						cgcsid_s = parser.nextText();
					} else if ("xm".equals(parser.getName())) {
						// 姓名
						if (zjxx) {
							name_s = parser.nextText();
						}
					} else if ("sbkid".equals(parser.getName())) {
						// 士兵卡id
						if (zjxx) {
							sbkid_s = parser.nextText();
						}
						// 增加参数执勤地点
					} else if ("zqdd".equals(parser.getName())) {
						// 士兵卡id
						if (zjxx) {
							zqdd_s = parser.nextText();
						}
					} else if ("xb".equals(parser.getName())) {
						// 性别
						if (zjxx) {
							sex_s = parser.nextText();
						}
					} else if ("zw".equals(parser.getName())) {
						// 职务
						if (zjxx) {
							office_s = parser.nextText();
						}
					} else if ("hgzl".equals(parser.getName())) {
						// 海港证类
						hgzl_s = parser.nextText();
					} else if ("gj".equals(parser.getName())) {
						// 国籍
						if (zjxx) {
							country_s = parser.nextText();
						}
					} else if ("csrq".equals(parser.getName())) {
						// 出生日期
						if (zjxx) {
							birthday_s = parser.nextText();
						}
					} else if ("ryid".equals(parser.getName())) {
						// 人员id
						if (zjxx) {
							ryid_s = parser.nextText();
						}
					} else if ("zjhm".equals(parser.getName())) {
						// 证件号码
						if (zjxx) {
							zjhm = parser.nextText();
						} else if (dkxx) {
							dkzjhm_s = parser.nextText();
						}

					} else if ("zjlx".equals(parser.getName())) {
						// 证件类型
						if (zjxx) {
							idcardtype_s = parser.nextText();
						} else if (dkxx) {
							dkzjlx_s = parser.nextText();
						}
					} else if ("ssdw".equals(parser.getName())) {
						// 所属单位
						if (zjxx) {
							unit_s = parser.nextText();
						} else if (dkxx) {
							dkssdw_s = parser.nextText();
							unit_s = dkssdw_s;
						}
					} else if ("icpic".equals(parser.getName())) {
						// 照片信息
						if (zjxx) {
							String icpic_s = parser.nextText();
							if (icpic_s != null && icpic_s.length() > 0) {
								hasImage = true;
								byte[] image = Base64.decode(icpic_s);
								BitmapFactory.Options opts = new BitmapFactory.Options();
								opts.inJustDecodeBounds = true;
								netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
								int height_be = opts.outHeight / 130;
								int width_be = opts.outWidth / 105;
								opts.inSampleSize = height_be > width_be ? height_be : width_be;
								if (opts.inSampleSize <= 0) {
									opts.inSampleSize = 1;
								}
								Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth + "," + opts.inSampleSize);
								opts.inJustDecodeBounds = false;
								netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
								Log.i(TAG, "decodeByteArray:" + opts.outHeight + "," + opts.outWidth);
							}
						}
					} else if ("sdcb".equals(parser.getName())) {
						// 有效期限
						if (zjxx) {
							sdcb_s = parser.nextText();
						}
					} else if ("yxq".equals(parser.getName())) {
						// 有效期限
						if (zjxx) {
							yxq_s = parser.nextText();
						}
					} else if ("bhcyx".equals(parser.getName())) {
						// 本航次有效
						if (zjxx) {
							bhcyx_s = parser.nextText();
						}
					} else if ("cbmc".equals(parser.getName())) {
						// 船舶名称
						if (dkxx) {
							dkcbmc_s = parser.nextText();
						}
					} else if ("cgj".equals(parser.getName())) {
						// 船港籍
						if (dkxx) {
							dkcgj_s = parser.nextText();
						}
					} else if ("zzdw".equals(parser.getName())) {
						// 载重吨位
						if (dkxx) {
							dkzzdw_s = parser.nextText();
						}
					} else if ("ml".equals(parser.getName())) {
						// 马力
						if (dkxx) {
							dkml_s = parser.nextText();
						}
					} else if ("yt".equals(parser.getName())) {
						// 用途
						if (dkxx) {
							dkyt_s = parser.nextText();
						}
					} else if ("dkfw".equals(parser.getName())) {
						// 搭靠船舶（范围）
						if (dkxx) {
							dkdkfw_s = parser.nextText();
						}
					} else if ("txjl".equals(parser.getName())) {
						// 通行记录
						if (txjl == null) {
							txjl = new ArrayList<Map<String, Object>>();
						} else {
							txjl.clear();
						}
					} else if ("jl".equals(parser.getName())) {
						map = new HashMap<String, Object>();
					} else if ("txsj".equals(parser.getName())) {
						// 通行时间
						map.put("txsj", parser.nextText());
					} else if ("txfx".equals(parser.getName())) {
						// 通行方向
						map.put("txfx", parser.nextText());
					} else if ("txdd".equals(parser.getName())) {
						// 船舶名称
						map.put("txdd", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("jl".equals(parser.getName())) {
						txjl.add(map);
					}
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 从其他模块返回，如果是手动选择人员，直接发起验证流程；如果是绑定船舶，直接返回；其他情况都需要重新起动二代证阅读器
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_RECORD_EXCEPTION:
			break;
		case STARTACTIVITY_FOR_REGISTER_CGCS:
			flagRegister = true;
			break;
		case STARTACTIVITY_FOR_SELECT_PERSON:
			flagRegister = true;
			if (resultCode == RESULT_OK) {
				sailorFlag = true;
				idcardnumber_s = data.getStringExtra("id");
				name_s = data.getStringExtra("xm");
				sex_s = data.getStringExtra("xb");
				country_s = data.getStringExtra("gj");
				office_s = data.getStringExtra("zw");
				idcardtype_s = data.getStringExtra("zjzl");
				unit_s = data.getStringExtra("ssdw");
				birthday_s = data.getStringExtra("csrq");
				// <Old Data>onReadComplete(false, true);
				onReadComplete(false, "1");
				return;
			}
			break;
		case STARTACTIVITY_FOR_REGISTER_PERSONINFO: // 手动保存记录页面返回
			sailorFlag = false;
			flagRegister = true;
			if (resultCode == RESULT_OK) {
				sxcfx_s = data.getStringExtra("fx");
				httpReturnXMLInfo = data.getStringExtra("yzjg");
				name_s = data.getStringExtra("xm");
				sex_s = data.getStringExtra("xb");
				country_s = data.getStringExtra("gj");
				unit_s = data.getStringExtra("ssdw");
				office_s = data.getStringExtra("zw");
				birthday_s = data.getStringExtra("csrq");
				idcardtype_s = data.getStringExtra("zjzl");
				idcardnumber_s = data.getStringExtra("zjhm");
				zjhm = idcardnumber_s;
				txjlid_s = data.getStringExtra("txjlid");
				xcxsid_s = data.getStringExtra("xcxsid");
				if (from.equals("03") || from.equals("04")) {
					setContentView(R.layout.xunchaxunjiandetail);
					onUpdateDisplayXunjianInfo(true, false);
				} else {
					setContentView(R.layout.persondetail_kakou);
					onUpdateDisplayInfo(true, false, false);
					Button _btngoods = (Button) findViewById(R.id.btnRecordGoods);
					_btngoods.setVisibility(View.GONE);
					Button _btnmodifyPassDirection = (Button) findViewById(R.id.btnmodifyPassDirection);
					_btnmodifyPassDirection.setVisibility(View.GONE);
				}
			} else {

			}
			break;

		case STARTACTIVITY_FOR_SELECT_SHIP:
			if (resultCode == RESULT_OK) {
				Intent intent = null;
				intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
			return;
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
					// <Old Data>onReadComplete(false, false);
				onReadComplete(false, "0");
				break;
			case 1:// 泊位
					// <Old Data>onReadComplete(false, false);
				onReadComplete(false, "0");
				break;
			case 2:// 区域
					// <Old Data>onReadComplete(false, false);
				onReadComplete(false, "0");
				break;

			default:
				break;
			}
			return;
		case STARTACTIVITY_FOR_ZXING:
			setContentView(R.layout.readcard_kakou);
			/**
			 * 手动查找按钮
			 */
			if (findViewById(R.id.btnsel) != null) {
				findViewById(R.id.btnsel).setVisibility(View.GONE);
			}
			if (findViewById(R.id.btnsel_title) != null) {
				findViewById(R.id.btnsel_title).setVisibility(View.GONE);
			}
			// sbGoodsCheck();
			Button btn = ((Button) findViewById(R.id.btnok));
			btn.setOnClickListener(clickOKButtonListener);
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
		}

		/*
		 * if
		 * (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED
		 * )) { Toast.makeText(ReadcardActivity.this,
		 * R.string.sdcardunmounted_idcard_disable,
		 * BaseToast.LENGTH_LONG).show(); return; }
		 */
		/*
		 * if (!IDCReadInterf.isReady(true)) {
		 * Toast.makeText(ReadcardActivity.this,
		 * R.string.reader_initcomm_failure, BaseToast.LENGTH_LONG).show();
		 * return; }
		 */
	}

	static class ViewHolder {
		private TextView index;

		private TextView txsj;

		private TextView txfx;

		private TextView txdd;
	}

	/**
	 * 自定义显示通行记录适配器
	 * */
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			int size = (txjl == null ? 0 : txjl.size());
			if (size > 5) {
				size = 5;
			}
			return size;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.xunchaxunjian_listview_class, null);
				holder.index = (TextView) convertView.findViewById(R.id.index);
				holder.txsj = (TextView) convertView.findViewById(R.id.tx_time);
				holder.txfx = (TextView) convertView.findViewById(R.id.tx_fx);
				holder.txdd = (TextView) convertView.findViewById(R.id.tx_dd);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if ((txjl == null) || (txjl.size() == 0)) {
				if (holder.index != null) {
					holder.index.setText("无");
				}
				holder.txsj.setText("无");
				holder.txfx.setText("无");
				holder.txdd.setText("无");
			} else {
				if (holder.index != null) {
					holder.index.setText((position + 1) + "");
				}
				String str = (String) txjl.get(position).get("txsj");
				if (str != null) {
					holder.txsj.setText(str);
				} else {
					holder.txsj.setText("");
				}
				str = (String) txjl.get(position).get("txfx");
				if (str != null) {
					holder.txfx.setText(str);
				} else {
					holder.txfx.setText("");
				}
				str = (String) txjl.get(position).get("txdd");
				if (str != null) {
					holder.txdd.setText(str);
				} else {
					holder.txdd.setText("");
				}
			}
			return convertView;
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
		flagRegister = false;
		selShipClickListener = null;
		selPersonClickListener = null;
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
		if (readService != null) {
			readService.close();
			readService = null;
		}
		super.onPause();
	}

	private void readInit() {
		if ("04".equals(from)) {
			return;
		}
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
			case MessageEntity.TOAST:
				HgqwToast.getToastView(getApplicationContext(), (String) msg.obj).show();
				break;
			case ReadService.READ_TYPE_DEFAULT_ICKEY:
				break;
			case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
				cardInfo = (CardInfo) msg.obj;
				idcardnumber_s = cardInfo.getIckey();
				defaultickey = cardInfo.getDefaultIckey();
				if (flagRegister) {
					BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
					clearData();
					// <Old Data>onReadComplete(false, false);
					onReadComplete(false, "0");
				}
				Log.i("智能卡读取成功", defaultickey + "," + idcardnumber_s);
				break;
			case ReadService.READ_TYPE_ID:
				cardInfo = (CardInfo) msg.obj;
				People people = cardInfo.getPeople();
				if (flagRegister) {
					BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
					clearData();
					readIdSuccess(people);
				}
				Log.i("身份证读卡成功", people.getPeopleIDCode());
				break;
			case ReadService.READ_TYPE_ICKEY:
				break;
			default:
				break;
			}
		}
	}

	private void readIdSuccess(People people) {
		// 控件赋值
		old_idcardnumber_s = idcardnumber_s;
		idcardnumber_s = people.getPeopleIDCode();
		old_name_s = name_s;
		name_s = people.getPeopleName();
		old_sex_s = sex_s;
		sex_s = DataDictionary.getDataDictionaryCode(people.getPeopleSex(), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE);
		old_birthday_s = birthday_s;
		birthday_s = people.getPeopleBirthday();
		old_idcardtype_s = idcardtype_s;
		idcardtype_s = "10";
		old_country_s = country_s;
		country_s = "CHN";
		old_unit_s = unit_s;
		unit_s = "";
		old_office_s = office_s;
		office_s = "";
		// <Old Data>onReadComplete(true, true);// 刷身份证按手动输入业务验证，按zjhm查询
		onReadComplete(true, "1");// 刷身份证按手动输入业务验证，按zjhm查询
	}

	public void clearData() {
		name_s = "";
		sex_s = "";
		idcardtype_s = "";
		birthday_s = "";
		unit_s = "";
		office_s = "";
		country_s = "";
	}

	/* 读卡程序结束 */

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (res.second != null) {
			onHttpResult(res.second.toString(), offLineRequestType);
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			HgqwToast.getToastView(getApplicationContext(), getString(R.string.filed_validate_card)).show();
			doingZxing = false;
			msTdc = null;
		}
	}
}
