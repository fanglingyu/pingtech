package com.pingtech.hgqw.module.exception.activity;

//异常信息详细录入界面，包括从梯口管理进入异常信息模块后启动新增或处理或查看详情、从梯口管理的刷卡登记模块进入异常信息登记、从巡查巡检进入异常信息新增或处理或查看详情
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.format.Time;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.SelectCountrylistActivity;
import com.pingtech.hgqw.activity.SelectDeviceActivity;
import com.pingtech.hgqw.activity.SelectPersonActivity;
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.exception.action.YcxxAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.utils.BaseInfoData;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 登记异常信息界面的activity类
 * */
public class Exceptioninfo extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "Exceptioninfo";

	/** 检查地点是梯口时，启动查询船舶 */
	private static final int STARTACTIVITY_FOR_SELECT_SHIP = 1;

	/** 启动查询国家列表 */
	private static final int STARTACTIVITY_FOR_SELECT_NATIONALITY = 2;

	/** 启动查询人员列表 */
	private static final int STARTACTIVITY_FOR_SELECT_PERSON = 3;

	/** 启动查询关联船舶 */
	private static final int STARTACTIVITY_FOR_SELECT_ASSOCIATION_SHIP = 4;

	/** 对象类别是船舶时，启动查询船舶中文名 */
	private static final int STARTACTIVITY_FOR_SELECT_SHIP_ZWM = 5;

	/** 对象类别是设备时，启动查询设备名 */
	private static final int STARTACTIVITY_FOR_SELECT_DEVICE_NAME = 7;

	/** 对象类别是区域时，启动查询区域名 */
	private static final int STARTACTIVITY_FOR_SELECT_AREA_NAME = 9;

	/** 查岗查哨时，启动查询检查地点下的船舶 */
	private static final int STARTACTIVITY_FOR_SELECT_SHIP_CGCS = 10;

	private ArrayAdapter<String> adapter;

	private Spinner spinner;

	private String exceptionId = null;

	/**
	 * 信息来源：卡口验证01、梯口验证02、现场巡查03
	 */
	private String source;

	private String from;

	/**
	 * 检查地点
	 */
	private String windowType;

	private RadioGroup dealTypeRadioGroup;

	private RadioGroup dealTypeRadioGroupForCgcs;

	private ProgressDialog progressDialog = null;

	protected int whetherHandle;

	private String shipId = null;

	private String shipType = null;

	private String shipTypeForObjectShip = null;

	private String xunJianId = null;

	private String yzjgStr = null;

	private String dkjlIdStr = null;

	private String sbkIdStr = null;

	private String cgcsIdStr = null;

	private String relatedShipId = null;

	private String sbIdStr = null;

	/** 对象类型：区域，区域名id */
	private String qyIdStr = null;

	/** 检查方式 */
	private String jcfsStr = null;

	/** 检查地点是梯口时的码头id */
	private String dockIdStr_01 = null;

	/** 检查地点是梯口时的泊位id */
	private String berthIdStr_01 = null;

	/** 检查地点是码头时的码头id */
	private String dockIdStr_02 = null;

	/** 检查地点是泊位时的码头id */
	private String dockIdStr_03 = null;

	/** 检查地点是泊位时的泊位id */
	private String berthIdStr_03 = null;

	/**
	 * 码头名称
	 */
	private String dockname = null;

	/**
	 * 泊位名称
	 */
	private String berthname = null;

	/**
	 * 区域名称
	 */
	private String areaname = null;

	/** 检查地点是区域口时的区域id */
	private String areaIdStr = null;

	private String shipHCStr = null;

	private String nameCgcs = null;

	private String areaIdStrCgcsKakou = null;

	private String areaIdStrCgcsQuyu = null;

	private boolean isCgcs = false;

	private OnCheckedChangeListener onCheckedChangeListener;

	private OnClickListener onClickListener;

	private String handleType;

	private String handleResult;

	private String handleRemark;

	private String handleEventType;

	private ViewFlipper viewFlipper;

	private ImageView imageView;

	private TextView textView;

	private String exceptionID;

	List<NameValuePair> backupparams = new ArrayList<NameValuePair>();

	boolean isFromOffline = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.exceptioninfo);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		if("".equals(from)){
			from = "";
		}
		source = intent.getStringExtra("source");
		boolean isClyz = intent.getBooleanExtra("isClyz" , false);
		if(!isClyz){
			if (source != null && source.equals("04")) {
				source = "03";
			}
			if (source == null || source.length() == 0) {
				source = "11";
			}
		}else{
			//类别不能选择
			findViewById(R.id.line_object_person).setVisibility(View.INVISIBLE);
			findViewById(R.id.line_object_car).setVisibility(View.INVISIBLE);
			findViewById(R.id.line_object_ship).setVisibility(View.INVISIBLE);
			findViewById(R.id.line_object_device).setVisibility(View.INVISIBLE);
//			findViewById(R.id.object_ll_car).setVisibility(View.INVISIBLE);
			findViewById(R.id.object_ll_ship).setVisibility(View.INVISIBLE);
			findViewById(R.id.object_ll_device).setVisibility(View.INVISIBLE);
			findViewById(R.id.object_ll_area).setVisibility(View.INVISIBLE);
			findViewById(R.id.object_ll_person).setVisibility(View.INVISIBLE);
		}
		windowType = intent.getStringExtra("windowtype");
		exceptionID = intent.getStringExtra("exceptionID");
		isFromOffline = intent.getBooleanExtra("isFromOffline", false);

		yzjgStr = intent.getStringExtra("yzjg");
		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		if (StringUtils.isNotEmpty(intent.getStringExtra("fromMain"))) {
			setMyActiveTitle(getString(R.string.exception_info));
		} else {
			if ("02".equals(from)) {
				setMyActiveTitle(getString(R.string.exception_info));
			} else if ("03".equals(from)) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.exception_info));
			} else if ("01".equals(from)) {
				setMyActiveTitle(getString(R.string.exception_info));
			} else if (FlagManagers.XCXJ_CGCS.equals(from)) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.chagangchashao));
			} else {
				setMyActiveTitle(getString(R.string.exception_info));
			}

		}
		((TextView) findViewById(R.id.check_name)).setText(Html.fromHtml("检查人：" + "<font color=\"#acacac\">"
				+ LoginUser.getCurrentLoginUser().getName() + "</font>"));
		((TextView) findViewById(R.id.check_unit)).setText(Html.fromHtml(getString(R.string.goods_check_unit) + "<font color=\"#acacac\">"
				+ LoginUser.getCurrentLoginUser().getUserSsdw() + "</font>"));
		findViewById(R.id.exception_addr_tikou).setVisibility(View.GONE);
		findViewById(R.id.exception_addr_xuncha).setVisibility(View.VISIBLE);

		// 优化检查地点的内存数据，使用前先检测是否为空，如果为空则重新加载
		if (BaseInfoData.mDockList == null || BaseInfoData.mDockList.size() < 1 || BaseInfoData.mBerthList == null
				|| BaseInfoData.mBerthList.size() < 1 || BaseInfoData.mKkAreaList == null || BaseInfoData.mKkAreaList.size() < 1) {
			BaseInfoData.onParseXMLBaseInfoDataOnOffline();
		}

		onCheckedChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (group.getId()) {
				/** 检查地点 */
				case R.id.exception_addr_radio_xc:
					if (checkedId == R.id.radio_btn_chuan) {
						findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.GONE);
						Button button = (Button) findViewById(R.id.exception_xuncha_chuanbo_select);
						button.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent intent = new Intent();
								if (from.equals("01")) {
									intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
								} else if (from.equals("02")) {
									intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
								} else {
									intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
								}
								intent.setClass(getApplicationContext(), SelectShipActivity.class);
								startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_SHIP);
							}
						});
					} else if (checkedId == R.id.radio_btn_matou) {
						findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.GONE);
					} else if (checkedId == R.id.radio_btn_bowei) {
						findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.GONE);
					} else if (checkedId == R.id.radio_btn_quyu) {
						findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.VISIBLE);
					}
					break;
				/** 处理类型 */
				case R.id.deal_radio:
					if (checkedId == R.id.radio_btn_guanzhu) {
						findViewById(R.id.exception_deal_input).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_deal_event_type).setVisibility(View.VISIBLE);
						spinner = (Spinner) findViewById(R.id.deal_event_type_spinner);
						adapter = new ArrayAdapter<String>(Exceptioninfo.this, android.R.layout.simple_spinner_item,
								DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE));
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner.setAdapter(adapter);
					} else if (checkedId == R.id.radio_btn_meisha) {
						findViewById(R.id.exception_deal_input).setVisibility(View.GONE);
						findViewById(R.id.exception_deal_event_type).setVisibility(View.GONE);
					} else if (checkedId == R.id.radio_btn_other) {
						findViewById(R.id.exception_deal_event_type).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_deal_input).setVisibility(View.VISIBLE);
						spinner = (Spinner) findViewById(R.id.deal_event_type_spinner);
						adapter = new ArrayAdapter<String>(Exceptioninfo.this, android.R.layout.simple_spinner_item,
								DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_QT_EVENT_TYPE));
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner.setAdapter(adapter);
					} else {
						findViewById(R.id.exception_deal_input).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_deal_event_type).setVisibility(View.GONE);
					}
					break;
				/** 查岗查哨处理类型 */
				case R.id.deal_radio_cgcs:
					if (checkedId == R.id.radio_btn_other_cgcs) {
						findViewById(R.id.exception_deal_event_type).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_deal_input).setVisibility(View.VISIBLE);
						spinner = (Spinner) findViewById(R.id.deal_event_type_spinner);
						adapter = new ArrayAdapter<String>(Exceptioninfo.this, android.R.layout.simple_spinner_item,
								DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP));
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner.setAdapter(adapter);
					} else {
						findViewById(R.id.exception_deal_input).setVisibility(View.VISIBLE);
						// 增加一层判断，否则非查岗查哨异常时会覆盖设置为GONE
						if (isCgcs) {
							findViewById(R.id.exception_deal_event_type).setVisibility(View.GONE);
						}
					}
					break;
				/** 是否处理 */
				case R.id.exception_radio:
					if (checkedId == R.id.radio_btn_yes) {
						if (viewFlipper.getDisplayedChild() == 3 || viewFlipper.getDisplayedChild() == 4) {
							findViewById(R.id.radio_btn_guanzhu).setVisibility(View.GONE);
							// findViewById(R.id.radio_btn_meisha).setVisibility(View.GONE);//梅沙处理需要显示
							dealTypeRadioGroup.check(R.id.radio_btn_other);
						} else {
							if (isCgcs) {
								findViewById(R.id.exception_deal_event_type).setVisibility(View.GONE);
							}
							findViewById(R.id.radio_btn_guanzhu).setVisibility(View.VISIBLE);
							findViewById(R.id.radio_btn_meisha).setVisibility(View.VISIBLE);
							dealTypeRadioGroup.check(R.id.radio_btn_guanzhu);
						}
						findViewById(R.id.exception_deal).setVisibility(View.VISIBLE);
					} else if (checkedId == R.id.radio_btn_no) {
						findViewById(R.id.exception_deal).setVisibility(View.GONE);
					}
					break;
				/** 查岗查哨检查地点 */
				case R.id.exception_addr_radio_cgcs:
					if (checkedId == R.id.radio_btn_tikou_cgcs) {
						findViewById(R.id.exception_addr_cgcs_tikou).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_addr_cgcs_kakou).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_cgcs_quyu).setVisibility(View.GONE);
						Button button = (Button) findViewById(R.id.exception_cgcs_chuanbo_select);
						button.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.putExtra("fromecgcs", true);
								if (from.equals("01")) {
									intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
								} else if (from.equals("02")) {
									intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
								} else {
									intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
								}
								intent.setClass(getApplicationContext(), SelectShipActivity.class);
								startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_SHIP_CGCS);
							}
						});
					} else if (checkedId == R.id.radio_btn_kakou_cgcs) {
						findViewById(R.id.exception_addr_cgcs_tikou).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_cgcs_kakou).setVisibility(View.VISIBLE);
						findViewById(R.id.exception_addr_cgcs_quyu).setVisibility(View.GONE);
					} else if (checkedId == R.id.radio_btn_quyu_cgcs) {
						findViewById(R.id.exception_addr_cgcs_tikou).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_cgcs_kakou).setVisibility(View.GONE);
						findViewById(R.id.exception_addr_cgcs_quyu).setVisibility(View.VISIBLE);
					}
				}
			}
		};
		RadioGroup addrRadioGroup = (RadioGroup) findViewById(R.id.exception_addr_radio_xc);
		addrRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
		String scene = intent.getStringExtra("scene");
		if (scene != null) {
			if (scene.equals("04")) {
				addrRadioGroup.check(R.id.radio_btn_quyu);
			} else if (scene.equals("02")) {
				addrRadioGroup.check(R.id.radio_btn_bowei);
			} else if (scene.equals("03")) {
				addrRadioGroup.check(R.id.radio_btn_matou);
			} else {
				addrRadioGroup.check(R.id.radio_btn_chuan);
			}
		} else {
			addrRadioGroup.check(R.id.radio_btn_chuan);
		}
		if ("02".equals(windowType)) {
			addrRadioGroup.check(R.id.radio_btn_chuan);
		}
		String extraString;
		spinner = (Spinner) findViewById(R.id.exception_event_type_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_EVENT_TYPE));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		extraString = intent.getStringExtra("eventtype");
		if (extraString != null && extraString.length() > 0) {
			spinner.setSelection(DataDictionary.getDataDictionaryIndexByCode(extraString, DataDictionary.DATADICTIONARY_TYPE_EVENT_TYPE), true);
		}
		spinner = (Spinner) findViewById(R.id.deal_event_type_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner = (Spinner) findViewById(R.id.exception_card_type_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		extraString = intent.getStringExtra("cardtype");
		if (extraString != null && extraString.length() > 0) {
			spinner.setSelection(DataDictionary.getDataDictionaryIndexByCode(extraString, DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE), true);
		}
		exceptionId = intent.getStringExtra("id");
		xunJianId = intent.getStringExtra("xunjian_id");
		sbkIdStr = intent.getStringExtra("sbkid");
		extraString = intent.getStringExtra("cardnumber");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_cardnum)).setText(extraString);
		}
		extraString = intent.getStringExtra("name");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_name)).setText(extraString);
		}
		extraString = intent.getStringExtra("sex");
		if (extraString != null && extraString.length() > 0) {
			RadioGroup sexrg = (RadioGroup) findViewById(R.id.exception_sex_radio);
			if (DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE).equals("女")) {
				sexrg.check(R.id.radio_btn_no);
			} else {
				sexrg.check(R.id.radio_btn_yes);
			}
		}
		extraString = intent.getStringExtra("company");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_unit)).setText(extraString);
			((EditText) findViewById(R.id.exception_car_unit)).setText(extraString);
			((EditText) findViewById(R.id.exception_ship_unit)).setText(extraString);
		}
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				/** 选择国家列表 */
				case R.id.btn_exception_country:
				case R.id.btn_exception_ship_country: {
					Intent intent = new Intent();
					intent.putExtra("type", "countrylist");
					intent.putExtra("selectitem", ((Button) v).getText().toString());
					intent.setClass(getApplicationContext(), SelectCountrylistActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_NATIONALITY);
				}
					break;
				/** 处理类型 */
				case R.id.radio_btn_guanzhu:
				case R.id.radio_btn_meisha:
				case R.id.radio_btn_wubao:
				case R.id.radio_btn_other:
					dealTypeRadioGroup.check(v.getId());
					break;
				/** 查岗查哨处理类型 */
				case R.id.radio_btn_wubao_cgcs:
				case R.id.radio_btn_other_cgcs:
					dealTypeRadioGroupForCgcs.check(v.getId());
					break;
				/** 选择姓名 */
				case R.id.exception_name_select: {
					Intent intent = new Intent();
					intent.putExtra("fromexception", true);
					intent.setClass(getApplicationContext(), SelectPersonActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_PERSON);
				}
					break;
				/** 对象类别是车辆时，选择关联船舶 */
				case R.id.exception_car_select_association_ship: {
					Intent intent = new Intent();
					intent.putExtra("fromglcb", true);
					if (from.equals("01")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
					} else if (from.equals("02")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
					} else {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
					}
					intent.setClass(getApplicationContext(), SelectShipActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_ASSOCIATION_SHIP);
				}
					break;
				/** 对象类别是船舶时，选择中文名 */
				case R.id.exception_ship_select_zwm: {
					Intent intent = new Intent();
					if (from.equals("01")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
					} else if (from.equals("02")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
					} else {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
					}
					intent.setClass(getApplicationContext(), SelectShipActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_SHIP_ZWM);
				}
					break;
				/** 对象类别是船舶时，选择关联船舶 */
				case R.id.exception_ship_select_association_ship: {
					Intent intent = new Intent();
					intent.putExtra("fromglcb", true);
					if (from.equals("01")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
					} else if (from.equals("02")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
					} else {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
					}
					intent.setClass(getApplicationContext(), SelectShipActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_ASSOCIATION_SHIP);
				}
					break;
				/** 对象类别是设备时，选择设备名 */
				case R.id.exception_device_select_name: {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), SelectDeviceActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_DEVICE_NAME);
				}
					break;
				/** 对象类别是设备时，选择关联船舶 */
				case R.id.exception_device_select_association_ship: {
					Intent intent = new Intent();
					intent.putExtra("fromglcb", true);
					if (from.equals("01")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);
					} else if (from.equals("02")) {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
					} else {
						intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
					}
					intent.setClass(getApplicationContext(), SelectShipActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_ASSOCIATION_SHIP);
				}
					break;
				/** 对象类别是区域时，选择区域名 */
				case R.id.exception_area_select_name: {
					String[] tempArray = new String[SystemSetting.getBaseInfoAreaList().size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoAreaList().toArray(tempArray);
					int index = SystemSetting.getBaseInfoAreaIndexByName(((EditText) findViewById(R.id.exception_area_name)).getText().toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((EditText) findViewById(R.id.exception_area_name)).setEnabled(false);
							((EditText) findViewById(R.id.exception_area_name)).setText(SystemSetting.getBaseInfoAreaList().get(which));
							qyIdStr = SystemSetting.getBaseInfoAreaId(which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 检查地点是泊位时，选择码头 */
				case R.id.exception_addr_xuncha_bowei_matou_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoDockList().size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoDockList().toArray(tempArray);
					int index = SystemSetting.getBaseInfoDockIndexByName(((EditText) findViewById(R.id.exception_addr_xuncha_bowei_matou_name))
							.getText().toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dockname = SystemSetting.getBaseInfoDockList().get(which);
							((EditText) findViewById(R.id.exception_addr_xuncha_bowei_matou_name)).setText(dockname);
							// 清空下属泊位信息
							((EditText) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).setText(null);
							dockIdStr_03 = SystemSetting.getBaseInfoDockId(which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 检查地点是泊位时，选择泊位 */
				case R.id.exception_addr_xuncha_bowei_bowei_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoBerthList(dockIdStr_03).size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoBerthList(dockIdStr_03).toArray(tempArray);
					if (arraystr == null || arraystr.length == 0) {
						HgqwToast.makeText(getApplicationContext(), Html.fromHtml("当前位置下无可选泊位信息"), 0).show();
					} else {
						int index = SystemSetting.getBaseInfoBerthIndexByName(dockIdStr_03,
								((EditText) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).getText().toString());
						new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								berthname = SystemSetting.getBaseInfoBerthList(dockIdStr_03).get(which);
								((EditText) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).setText(berthname);
								berthIdStr_03 = SystemSetting.getBaseInfoBerthId(dockIdStr_03, which);
								dialog.dismiss();
							}
						}).show();
					}
				}
					break;
				/** 检查地点是码头时，选择泊位 */
				case R.id.exception_addr_xuncha_matou_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoDockList().size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoDockList().toArray(tempArray);

					int index = SystemSetting.getBaseInfoDockIndexByName(((EditText) findViewById(R.id.exception_addr_xuncha_matou_name)).getText()
							.toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dockname = SystemSetting.getBaseInfoDockList().get(which);
							((EditText) findViewById(R.id.exception_addr_xuncha_matou_name)).setText(dockname);
							dockIdStr_02 = SystemSetting.getBaseInfoDockId(which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 检查地点是区域时，选择区域 */
				case R.id.exception_addr_xuncha_quyu_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoAreaList().size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoAreaList().toArray(tempArray);
					int index = SystemSetting.getBaseInfoAreaIndexByName(((EditText) findViewById(R.id.exception_addr_xuncha_quyu_name)).getText()
							.toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							areaname = SystemSetting.getBaseInfoAreaList().get(which);
							((EditText) findViewById(R.id.exception_addr_xuncha_quyu_name)).setText(areaname);
							areaIdStr = SystemSetting.getBaseInfoAreaId(which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 检查地点是卡口（查岗查哨）时，选择区域 */
				case R.id.exception_addr_cgcs_kakou_quyu_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoKkAreaList().size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoKkAreaList().toArray(tempArray);
					int index = SystemSetting.getBaseInfoAreaIndexByName(((EditText) findViewById(R.id.exception_addr_cgcs_kakou_quyu_name))
							.getText().toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							areaname = SystemSetting.getBaseInfoKkAreaList().get(which);
							((EditText) findViewById(R.id.exception_addr_cgcs_kakou_quyu_name)).setText(areaname);
							areaIdStrCgcsKakou = SystemSetting.getBaseInfoKkAreaId(which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 检查地点是区域（查岗查哨）时，选择区域 */
				case R.id.exception_addr_cgcs_quyu_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoAreaList().size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoAreaList().toArray(tempArray);
					int index = SystemSetting.getBaseInfoAreaIndexByName(((EditText) findViewById(R.id.exception_addr_cgcs_quyu_name)).getText()
							.toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							areaname = SystemSetting.getBaseInfoAreaList().get(which);
							((EditText) findViewById(R.id.exception_addr_cgcs_quyu_name)).setText(areaname);
							areaIdStrCgcsQuyu = SystemSetting.getBaseInfoJkAreaId(which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 检查地点是船舶时时，选择码头 */
				case R.id.exception_xuncha_chuanbo_matou_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoDockList().size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoDockList().toArray(tempArray);
					int index = SystemSetting.getBaseInfoDockIndexByName(((EditText) findViewById(R.id.exception_xuncha_chuanbo_matou_name))
							.getText().toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dockname = SystemSetting.getBaseInfoDockList().get(which);
							((EditText) findViewById(R.id.exception_xuncha_chuanbo_matou_name)).setText(dockname);
							dockIdStr_01 = SystemSetting.getBaseInfoDockId(which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 检查地点是船舶时时，选择泊位 */
				case R.id.exception_xuncha_chuanbo_bowei_select: {
					String[] tempArray = new String[SystemSetting.getBaseInfoBerthList(dockIdStr_01).size()];
					String[] arraystr = (String[]) SystemSetting.getBaseInfoBerthList(dockIdStr_01).toArray(tempArray);
					int index = SystemSetting.getBaseInfoBerthIndexByName(dockIdStr_01,
							((EditText) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).getText().toString());
					new AlertDialog.Builder(Exceptioninfo.this).setSingleChoiceItems(arraystr, index, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							berthname = SystemSetting.getBaseInfoBerthList(dockIdStr_01).get(which);
							((EditText) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).setText(berthname);
							berthIdStr_01 = SystemSetting.getBaseInfoBerthId(dockIdStr_01, which);
							dialog.dismiss();
						}
					}).show();
				}
					break;
				/** 取消，返回上一级 */
				case R.id.exception_cancel:
					finish();
					break;
				/** 保存 */
				case R.id.exception_submit:
					if (progressDialog != null) {
						return;
					}
					String sss = source;
					String url = "sendIllegalInfo";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					if (exceptionId != null && exceptionId.equals("不能切换对象类别")) {
						params.add(new BasicNameValuePair("id", ""));
					} else {
						params.add(new BasicNameValuePair("id", exceptionId));
					}
					params.add(new BasicNameValuePair("xcxsid", xunJianId));
					params.add(new BasicNameValuePair("sbkid", sbkIdStr));
					params.add(new BasicNameValuePair("yzjg", yzjgStr));
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("czr", LoginUser.getCurrentLoginUser().getUserID()));
					String objectTypeStr = "人员";
					if (isCgcs) {
						/** 查岗查哨 */
						Spinner event_inner = (Spinner) findViewById(R.id.exception_event_type_spinner);
						params.add(new BasicNameValuePair("eventType", DataDictionary.getDataDictionaryCodeByIndex(
								event_inner.getSelectedItemPosition(), DataDictionary.DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP)));
						params.add(new BasicNameValuePair("objectType", "06"));
						params.add(new BasicNameValuePair("cardNumber", ""));
						params.add(new BasicNameValuePair("cardType", ""));
						params.add(new BasicNameValuePair("name", nameCgcs));
						params.add(new BasicNameValuePair("sex", ""));
						params.add(new BasicNameValuePair("nationality", ""));
						params.add(new BasicNameValuePair("birthday", ""));
						params.add(new BasicNameValuePair("company", ""));

						params.add(new BasicNameValuePair("glcbmc", ""));
						params.add(new BasicNameValuePair("cphm", ""));
						params.add(new BasicNameValuePair("clpp", ""));
						params.add(new BasicNameValuePair("fdjh", ""));
						params.add(new BasicNameValuePair("cbzwm", ""));
						params.add(new BasicNameValuePair("cbywm", ""));
						params.add(new BasicNameValuePair("sbmc", ""));
						params.add(new BasicNameValuePair("sbid", ""));
						params.add(new BasicNameValuePair("qymc", ""));
						params.add(new BasicNameValuePair("qyid", ""));
					} else {
						/** 非查岗查哨 */
						Spinner event_inner = (Spinner) findViewById(R.id.exception_event_type_spinner);
						params.add(new BasicNameValuePair("eventType", DataDictionary.getDataDictionaryCodeByIndex(
								event_inner.getSelectedItemPosition(), DataDictionary.DATADICTIONARY_TYPE_EVENT_TYPE)));
						Spinner object_inner = (Spinner) findViewById(R.id.exception_object_type_spinner);
						if (object_inner != null) {
							/** 横版设备 */
							objectTypeStr = object_inner.getSelectedItem().toString();
						} else {
							/** 竖版设备 */
							switch (viewFlipper.getDisplayedChild()) {
							case 0:
								objectTypeStr = ((TextView) findViewById(R.id.object_person)).getText().toString();
								break;
							case 1:
								objectTypeStr = ((TextView) findViewById(R.id.object_car)).getText().toString();
								break;
							case 2:
								objectTypeStr = ((TextView) findViewById(R.id.object_ship)).getText().toString();
								break;
							case 3:
								objectTypeStr = ((TextView) findViewById(R.id.object_device)).getText().toString();
								break;
							case 4:
								objectTypeStr = ((TextView) findViewById(R.id.object_area)).getText().toString();
								break;
							}
						}
						params.add(new BasicNameValuePair("objectType", DataDictionary.getDataDictionaryCode(objectTypeStr,
								DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE)));
						if (objectTypeStr.equals("人员")) {
							if (StringUtils.isEmpty(((EditText) findViewById(R.id.exception_name)).getText().toString())) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.name_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							if (StringUtils.isEmpty(((EditText) findViewById(R.id.exception_cardnum)).getText().toString())) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.zhengjiannum_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("cardNumber", ((EditText) findViewById(R.id.exception_cardnum)).getText().toString()));
							Spinner cardtype_inner = (Spinner) findViewById(R.id.exception_card_type_spinner);
							params.add(new BasicNameValuePair("cardType", DataDictionary.getDataDictionaryCodeByIndex(
									cardtype_inner.getSelectedItemPosition(), DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE)));
							params.add(new BasicNameValuePair("name", ((EditText) findViewById(R.id.exception_name)).getText().toString()));
							RadioGroup sex_rg = (RadioGroup) findViewById(R.id.exception_sex_radio);
							if (sex_rg.getCheckedRadioButtonId() == R.id.radio_btn_no) {
								params.add(new BasicNameValuePair("sex", DataDictionary.getDataDictionaryCode("女",
										DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE)));
							} else {
								params.add(new BasicNameValuePair("sex", DataDictionary.getDataDictionaryCode("男",
										DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE)));
							}
							Button btn = (Button) findViewById(R.id.btn_exception_country);
							params.add(new BasicNameValuePair("nationality", DataDictionary.getCountryCode(btn.getText().toString())));
							DatePicker dataPicker = (DatePicker) findViewById(R.id.datePicker);
							params.add(new BasicNameValuePair("birthday", dataPicker.getYear() + "-" + (dataPicker.getMonth() + 1) + "-"
									+ dataPicker.getDayOfMonth()));
							params.add(new BasicNameValuePair("company", ((EditText) findViewById(R.id.exception_unit)).getText().toString()));
							params.add(new BasicNameValuePair("glcbmc", ""));
							params.add(new BasicNameValuePair("cphm", ""));
							params.add(new BasicNameValuePair("clpp", ""));
							params.add(new BasicNameValuePair("fdjh", ""));
							params.add(new BasicNameValuePair("cbzwm", ""));
							params.add(new BasicNameValuePair("cbywm", ""));
							params.add(new BasicNameValuePair("sbmc", ""));
							params.add(new BasicNameValuePair("sbid", ""));
							params.add(new BasicNameValuePair("qymc", ""));
							params.add(new BasicNameValuePair("qyid", ""));
						} else if (objectTypeStr.equals("车辆")) {
							if (StringUtils.isEmpty(((EditText) findViewById(R.id.exception_car_num)).getText().toString())) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.car_num_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("cardNumber", ""));
							params.add(new BasicNameValuePair("cardType", ""));
							params.add(new BasicNameValuePair("name", ""));
							params.add(new BasicNameValuePair("sex", ""));
							params.add(new BasicNameValuePair("nationality", ""));
							params.add(new BasicNameValuePair("birthday", ""));
							params.add(new BasicNameValuePair("company", ((EditText) findViewById(R.id.exception_car_unit)).getText().toString()));

							params.add(new BasicNameValuePair("glcbmc", ((EditText) findViewById(R.id.exception_car_association_ship)).getText()
									.toString()));
							params.add(new BasicNameValuePair("jhhc", relatedShipId));
							params.add(new BasicNameValuePair("swid", null));
							params.add(new BasicNameValuePair("cphm", ((EditText) findViewById(R.id.exception_car_num)).getText().toString()));
							params.add(new BasicNameValuePair("clpp", ((EditText) findViewById(R.id.exception_car_sign)).getText().toString()));
							params.add(new BasicNameValuePair("fdjh", ((EditText) findViewById(R.id.exception_engine_num)).getText().toString()));
							params.add(new BasicNameValuePair("cbzwm", ""));
							params.add(new BasicNameValuePair("cbywm", ""));
							params.add(new BasicNameValuePair("sbmc", ""));
							params.add(new BasicNameValuePair("sbid", ""));
							params.add(new BasicNameValuePair("qymc", ""));
							params.add(new BasicNameValuePair("qyid", ""));
						} else if (objectTypeStr.equals("船舶")) {
							if (StringUtils.isEmpty(((EditText) findViewById(R.id.exception_ship_zwm)).getText().toString())) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.ship_zwm_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("cardNumber", ""));
							params.add(new BasicNameValuePair("cardType", ""));
							params.add(new BasicNameValuePair("name", ""));
							params.add(new BasicNameValuePair("sex", ""));
							Button btn = (Button) findViewById(R.id.btn_exception_ship_country);
							params.add(new BasicNameValuePair("nationality", DataDictionary.getCountryCode(btn.getText().toString())));
							params.add(new BasicNameValuePair("birthday", ""));
							params.add(new BasicNameValuePair("company", ((EditText) findViewById(R.id.exception_ship_unit)).getText().toString()));

							if (shipTypeForObjectShip != null) {
								if (shipTypeForObjectShip.equals("ward")) {
									params.add(new BasicNameValuePair("swid", ""));
									params.add(new BasicNameValuePair("jhhc", shipHCStr));
									params.add(new BasicNameValuePair("glcbmc", ""));
								} else {
									if (shipHCStr.equals("搭靠证")) {
										params.add(new BasicNameValuePair("swid", ""));
									} else {
										params.add(new BasicNameValuePair("swid", shipHCStr));
									}
									params.add(new BasicNameValuePair("jhhc", relatedShipId));
									params.add(new BasicNameValuePair("glcbmc", ((EditText) findViewById(R.id.exception_ship_association_ship))
											.getText().toString()));
								}
							} else {
								params.add(new BasicNameValuePair("swid", ""));
								params.add(new BasicNameValuePair("jhhc", ""));
								params.add(new BasicNameValuePair("glcbmc", ""));
							}
							params.add(new BasicNameValuePair("cphm", ""));
							params.add(new BasicNameValuePair("clpp", ""));
							params.add(new BasicNameValuePair("fdjh", ""));
							params.add(new BasicNameValuePair("cbzwm", ((EditText) findViewById(R.id.exception_ship_zwm)).getText().toString()));
							params.add(new BasicNameValuePair("cbywm", ((EditText) findViewById(R.id.exception_ship_ywm)).getText().toString()));
							params.add(new BasicNameValuePair("sbmc", ""));
							params.add(new BasicNameValuePair("sbid", ""));
							params.add(new BasicNameValuePair("qymc", ""));
							params.add(new BasicNameValuePair("qyid", ""));
						} else if (objectTypeStr.equals("设备")) {
							if (StringUtils.isEmpty(((EditText) findViewById(R.id.exception_device_name)).getText().toString())) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.device_name_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("cardNumber", ""));
							params.add(new BasicNameValuePair("cardType", ""));
							params.add(new BasicNameValuePair("name", ""));
							params.add(new BasicNameValuePair("sex", ""));
							params.add(new BasicNameValuePair("nationality", ""));
							params.add(new BasicNameValuePair("birthday", ""));
							params.add(new BasicNameValuePair("company", ""));

							params.add(new BasicNameValuePair("glcbmc", ((EditText) findViewById(R.id.exception_device_association_ship)).getText()
									.toString()));
							params.add(new BasicNameValuePair("jhhc", relatedShipId));
							params.add(new BasicNameValuePair("swid", ""));
							params.add(new BasicNameValuePair("cphm", ""));
							params.add(new BasicNameValuePair("clpp", ""));
							params.add(new BasicNameValuePair("fdjh", ""));
							params.add(new BasicNameValuePair("cbzwm", ""));
							params.add(new BasicNameValuePair("cbywm", ""));
							params.add(new BasicNameValuePair("sbmc", ((EditText) findViewById(R.id.exception_device_name)).getText().toString()));
							params.add(new BasicNameValuePair("sbid", sbIdStr));
							params.add(new BasicNameValuePair("qymc", ""));
							params.add(new BasicNameValuePair("qyid", ""));
						} else if (objectTypeStr.equals("区域")) {
							if (StringUtils.isEmpty(((EditText) findViewById(R.id.exception_area_name)).getText().toString())) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.area_name_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("cardNumber", ""));
							params.add(new BasicNameValuePair("cardType", ""));
							params.add(new BasicNameValuePair("name", ""));
							params.add(new BasicNameValuePair("sex", ""));
							params.add(new BasicNameValuePair("nationality", ""));
							params.add(new BasicNameValuePair("birthday", ""));
							params.add(new BasicNameValuePair("company", ""));

							params.add(new BasicNameValuePair("glcbmc", ""));
							params.add(new BasicNameValuePair("jhhc", ""));
							params.add(new BasicNameValuePair("swid", ""));
							params.add(new BasicNameValuePair("cphm", ""));
							params.add(new BasicNameValuePair("clpp", ""));
							params.add(new BasicNameValuePair("fdjh", ""));
							params.add(new BasicNameValuePair("cbzwm", ""));
							params.add(new BasicNameValuePair("cbywm", ""));
							params.add(new BasicNameValuePair("sbmc", ""));
							params.add(new BasicNameValuePair("sbid", ""));
							params.add(new BasicNameValuePair("qymc", ((EditText) findViewById(R.id.exception_area_name)).getText().toString()));
							params.add(new BasicNameValuePair("qyid", qyIdStr));
						}
					}
					params.add(new BasicNameValuePair("source", source));
					if (jcfsStr != null && jcfsStr.length() != 0) {
						params.add(new BasicNameValuePair("jcfs", jcfsStr));
					} else {
						if (source.equals("01") || source.equals("02")) {
							params.add(new BasicNameValuePair("jcfs", "03"));
						} else if (source.equals("03")) {
							params.add(new BasicNameValuePair("jcfs", "02"));
						}
					}
					params.add(new BasicNameValuePair("cgcsid", cgcsIdStr));
					params.add(new BasicNameValuePair("dkjlid", dkjlIdStr));
					params.add(new BasicNameValuePair("eventDesc", ((EditText) findViewById(R.id.exception_desc)).getText().toString()));
					params.add(new BasicNameValuePair("eventRemark", ((EditText) findViewById(R.id.exception_remark)).getText().toString()));
					DatePicker checkDate = (DatePicker) findViewById(R.id.datePicker_check);
					TimePicker checktime = (TimePicker) findViewById(R.id.timePicker_check);
					params.add(new BasicNameValuePair("inspectTime", checkDate.getYear() + "-" + (checkDate.getMonth() + 1) + "-"
							+ checkDate.getDayOfMonth() + " " + checktime.getCurrentHour() + ":" + checktime.getCurrentMinute() + ":00"));
					if (isCgcs) {
						/** 查岗查哨，处理检查地点 */
						RadioGroup scent_rg = (RadioGroup) findViewById(R.id.exception_addr_radio_cgcs);
						if (scent_rg.getCheckedRadioButtonId() == R.id.radio_btn_tikou_cgcs) {
							if (((EditText) findViewById(R.id.exception_cgcs_chuanbo_name)).getText().toString().length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("scene", "01"));
							params.add(new BasicNameValuePair("shipName", ((EditText) findViewById(R.id.exception_cgcs_chuanbo_name)).getText()
									.toString()));
							if (shipType != null && shipId != null) {
								if (shipType.equals("ward")) {
									params.add(new BasicNameValuePair("jhhc", shipId));
									params.add(new BasicNameValuePair("swid", null));
								} else {
									params.add(new BasicNameValuePair("jhhc", null));
									params.add(new BasicNameValuePair("swid", shipId));
								}
							} else {
								params.add(new BasicNameValuePair("jhhc", null));
								params.add(new BasicNameValuePair("swid", null));
							}
							params.add(new BasicNameValuePair("dockCode", dockIdStr_01));
							params.add(new BasicNameValuePair("berthCode", berthIdStr_01));
							params.add(new BasicNameValuePair("areaCode", null));
							params.add(new BasicNameValuePair("dockname", dockname));
							params.add(new BasicNameValuePair("berthname", berthname));
							params.add(new BasicNameValuePair("areaname", null));
						} else if (scent_rg.getCheckedRadioButtonId() == R.id.radio_btn_kakou_cgcs) {
							if (areaIdStrCgcsKakou == null || areaIdStrCgcsKakou.length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("scene", "05"));
							params.add(new BasicNameValuePair("shipName", null));
							params.add(new BasicNameValuePair("dockCode", null));
							params.add(new BasicNameValuePair("berthCode", null));
							params.add(new BasicNameValuePair("areaCode", areaIdStrCgcsKakou));
							params.add(new BasicNameValuePair("dockname", null));
							params.add(new BasicNameValuePair("berthname", null));
							params.add(new BasicNameValuePair("areaname", areaname));
							params.add(new BasicNameValuePair("jhhc", null));
							params.add(new BasicNameValuePair("swid", null));
						} else if (scent_rg.getCheckedRadioButtonId() == R.id.radio_btn_quyu_cgcs) {
							if (areaIdStrCgcsQuyu == null || areaIdStrCgcsQuyu.length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("scene", "04"));
							params.add(new BasicNameValuePair("shipName", null));
							params.add(new BasicNameValuePair("dockCode", null));
							params.add(new BasicNameValuePair("berthCode", null));
							params.add(new BasicNameValuePair("areaCode", areaIdStrCgcsQuyu));
							params.add(new BasicNameValuePair("dockname", null));
							params.add(new BasicNameValuePair("berthname", null));
							params.add(new BasicNameValuePair("areaname", areaname));
							params.add(new BasicNameValuePair("jhhc", null));
							params.add(new BasicNameValuePair("swid", null));
						}
					} else {
						/** 非查岗查哨，处理检查地点 */
						RadioGroup scentRadioGroup = (RadioGroup) findViewById(R.id.exception_addr_radio_xc);
						if (scentRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_chuan) {
							params.add(new BasicNameValuePair("scene", "01"));
							if (((EditText) findViewById(R.id.exception_xuncha_chuanbo_name)).getText().toString().length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("shipName", ((EditText) findViewById(R.id.exception_xuncha_chuanbo_name)).getText()
									.toString()));
							if (shipType != null && shipId != null) {
								if (shipType.equals("ward")) {
									if (objectTypeStr.equals("人员")) {
										params.add(new BasicNameValuePair("jhhc", shipId));
										params.add(new BasicNameValuePair("swid", null));
									}
								} else {
									if (objectTypeStr.equals("人员")) {
										params.add(new BasicNameValuePair("jhhc", null));
										params.add(new BasicNameValuePair("swid", shipId));
									}
								}
							} else {
								if (objectTypeStr.equals("人员")) {
									params.add(new BasicNameValuePair("jhhc", null));
									params.add(new BasicNameValuePair("swid", null));
								}
							}
							params.add(new BasicNameValuePair("dockCode", dockIdStr_01));
							params.add(new BasicNameValuePair("berthCode", berthIdStr_01));
							params.add(new BasicNameValuePair("areaCode", null));
							params.add(new BasicNameValuePair("dockname", dockname));
							params.add(new BasicNameValuePair("berthname", berthname));
							params.add(new BasicNameValuePair("areaname", null));
						} else if (scentRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_matou) {
							params.add(new BasicNameValuePair("scene", "03"));
							if (dockIdStr_02 == null || dockIdStr_02.length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("shipName", null));
							params.add(new BasicNameValuePair("dockCode", dockIdStr_02));
							params.add(new BasicNameValuePair("berthCode", null));
							params.add(new BasicNameValuePair("areaCode", null));
							params.add(new BasicNameValuePair("dockname", dockname));
							params.add(new BasicNameValuePair("berthname", null));
							params.add(new BasicNameValuePair("areaname", null));
							if (objectTypeStr.equals("人员")) {
								params.add(new BasicNameValuePair("jhhc", null));
								params.add(new BasicNameValuePair("swid", null));
							}
						} else if (scentRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_bowei) {
							params.add(new BasicNameValuePair("scene", "02"));
							if (dockIdStr_03 == null || dockIdStr_03.length() == 0 || berthIdStr_03 == null || berthIdStr_03.length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("shipName", null));
							params.add(new BasicNameValuePair("dockCode", dockIdStr_03));
							params.add(new BasicNameValuePair("berthCode", berthIdStr_03));
							params.add(new BasicNameValuePair("areaCode", null));
							params.add(new BasicNameValuePair("dockname", dockname));
							params.add(new BasicNameValuePair("berthname", berthname));
							params.add(new BasicNameValuePair("areaname", null));
							if (objectTypeStr.equals("人员")) {
								params.add(new BasicNameValuePair("jhhc", null));
								params.add(new BasicNameValuePair("swid", null));
							}
						} else if (scentRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_quyu) {
							params.add(new BasicNameValuePair("scene", "04"));
							if (areaIdStr == null || areaIdStr.length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("shipName", null));
							params.add(new BasicNameValuePair("dockCode", null));
							params.add(new BasicNameValuePair("berthCode", null));
							params.add(new BasicNameValuePair("areaCode", areaIdStr));
							params.add(new BasicNameValuePair("dockname", null));
							params.add(new BasicNameValuePair("berthname", null));
							params.add(new BasicNameValuePair("areaname", areaname));
							if (objectTypeStr.equals("人员")) {
								params.add(new BasicNameValuePair("jhhc", null));
								params.add(new BasicNameValuePair("swid", null));
							}
						} else {
							params.add(new BasicNameValuePair("scene", "01"));
							if (((EditText) findViewById(R.id.exception_xuncha_chuanbo_name)).getText().toString().length() == 0) {
								HgqwToast.makeText(Exceptioninfo.this, R.string.exception_check_address_empty, HgqwToast.LENGTH_LONG).show();
								return;
							}
							params.add(new BasicNameValuePair("shipName", ((EditText) findViewById(R.id.exception_xuncha_chuanbo_name)).getText()
									.toString()));
							if (shipType != null && shipId != null) {
								if (shipType.equals("ward")) {
									if (objectTypeStr.equals("人员")) {
										params.add(new BasicNameValuePair("jhhc", shipId));
										params.add(new BasicNameValuePair("swid", null));
									}
								} else {
									if (objectTypeStr.equals("人员")) {
										params.add(new BasicNameValuePair("jhhc", null));
										params.add(new BasicNameValuePair("swid", shipId));
									}
								}
							} else {
								if (objectTypeStr.equals("人员")) {
									params.add(new BasicNameValuePair("jhhc", null));
									params.add(new BasicNameValuePair("swid", null));
								}
							}
							params.add(new BasicNameValuePair("dockCode", null));
							params.add(new BasicNameValuePair("berthCode", null));
							params.add(new BasicNameValuePair("areaCode", null));
						}
					}
					if (((EditText) findViewById(R.id.exception_desc)).getText().toString().length() == 0) {
						HgqwToast.makeText(Exceptioninfo.this, R.string.event_desc_empty, HgqwToast.LENGTH_LONG).show();
						return;
					}

					RadioGroup whetherHandleRadioGroup = (RadioGroup) findViewById(R.id.exception_radio);
					if (whetherHandleRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_yes) {
						/** 是否处理，选择“是” */
						if ((findViewById(R.id.exception_deal_input).getVisibility() == View.VISIBLE)
								&& (((EditText) findViewById(R.id.deal_result)).getVisibility() == View.VISIBLE)
								&& (((EditText) findViewById(R.id.deal_result)).getText().toString().length() == 0)) {
							HgqwToast.makeText(Exceptioninfo.this, R.string.deal_result_empty, HgqwToast.LENGTH_LONG).show();
							return;
						}
						params.add(new BasicNameValuePair("whetherHandle", "1"));
						if (isCgcs) {
							/** 是查岗查哨 */
							RadioGroup handleType_rg = (RadioGroup) findViewById(R.id.deal_radio_cgcs);
							if (handleType_rg.getCheckedRadioButtonId() == R.id.radio_btn_wubao_cgcs) {
								handleType = "03";
								handleResult = ((EditText) findViewById(R.id.deal_result)).getText().toString();
								handleRemark = ((EditText) findViewById(R.id.deal_remark)).getText().toString();
								params.add(new BasicNameValuePair("handleType", "03"));
								params.add(new BasicNameValuePair("handleEventType", null));
								params.add(new BasicNameValuePair("handleResult", handleResult));
								params.add(new BasicNameValuePair("handleRemark", handleRemark));
							} else {
								handleType = "04";
								handleEventType = handleResult = ((EditText) findViewById(R.id.deal_result)).getText().toString();
								handleRemark = ((EditText) findViewById(R.id.deal_remark)).getText().toString();
								params.add(new BasicNameValuePair("handleType", "04"));
								Spinner handle_inner = (Spinner) findViewById(R.id.deal_event_type_spinner);
								params.add(new BasicNameValuePair("handleEventType", DataDictionary.getDataDictionaryCodeByIndex(
										handle_inner.getSelectedItemPosition(), DataDictionary.DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP)));
								params.add(new BasicNameValuePair("handleResult", handleResult));
								params.add(new BasicNameValuePair("handleRemark", handleRemark));
							}
						} else {
							/** 非查岗查哨 */
							RadioGroup handleTypeRadioGroup = (RadioGroup) findViewById(R.id.deal_radio);
							if (handleTypeRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_meisha) {
								handleType = "02";
								handleEventType = null;
								handleResult = null;
								handleRemark = null;
								params.add(new BasicNameValuePair("handleType", "02"));
								params.add(new BasicNameValuePair("handleEventType", null));
								params.add(new BasicNameValuePair("handleResult", null));
								params.add(new BasicNameValuePair("remark", null));
							} else if (handleTypeRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_guanzhu) {
								Spinner handle_inner = (Spinner) findViewById(R.id.deal_event_type_spinner);
								handleType = "01";
								handleEventType = DataDictionary.getDataDictionaryCodeByIndex(handle_inner.getSelectedItemPosition(),
										DataDictionary.DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE);
								handleResult = ((EditText) findViewById(R.id.deal_result)).getText().toString();
								handleRemark = ((EditText) findViewById(R.id.deal_remark)).getText().toString();
								params.add(new BasicNameValuePair("handleType", "01"));
								params.add(new BasicNameValuePair("handleEventType", handleEventType));
								params.add(new BasicNameValuePair("handleResult", handleResult));
								params.add(new BasicNameValuePair("handleRemark", handleRemark));
							} else if (handleTypeRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_wubao) {
								handleType = "03";
								handleEventType = null;
								handleResult = ((EditText) findViewById(R.id.deal_result)).getText().toString();
								handleRemark = ((EditText) findViewById(R.id.deal_remark)).getText().toString();
								params.add(new BasicNameValuePair("handleType", "03"));
								params.add(new BasicNameValuePair("handleEventType", null));
								params.add(new BasicNameValuePair("handleResult", handleResult));
								params.add(new BasicNameValuePair("handleRemark", handleRemark));
							} else {
								Spinner handle_inner = (Spinner) findViewById(R.id.deal_event_type_spinner);
								handleType = "04";
								handleEventType = DataDictionary.getDataDictionaryCodeByIndex(handle_inner.getSelectedItemPosition(),
										DataDictionary.DATADICTIONARY_TYPE_QT_EVENT_TYPE);
								handleResult = ((EditText) findViewById(R.id.deal_result)).getText().toString();
								handleRemark = ((EditText) findViewById(R.id.deal_remark)).getText().toString();
								params.add(new BasicNameValuePair("handleType", "04"));
								params.add(new BasicNameValuePair("handleEventType", handleEventType));
								params.add(new BasicNameValuePair("handleResult", handleResult));
								params.add(new BasicNameValuePair("handleRemark", handleRemark));
							}
						}
						whetherHandle = 1;
					} else {
						/** 是否处理，选择“否” */
						handleType = "0";
						handleEventType = null;
						handleResult = null;
						handleRemark = null;
						params.add(new BasicNameValuePair("whetherHandle", "0"));
						params.add(new BasicNameValuePair("handleType", null));
						params.add(new BasicNameValuePair("handleEventType", null));
						params.add(new BasicNameValuePair("handleResult", null));
						params.add(new BasicNameValuePair("handleRemark", null));
						whetherHandle = 0;
					}
					progressDialog = new ProgressDialog(Exceptioninfo.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					// isFromOffline
					if (!isFromOffline) {
						NetWorkManager.request(Exceptioninfo.this, url, params, 0);
						backupparams.addAll(params);
					} else {
						params.add(new BasicNameValuePair("exceptionID", exceptionID));
						OffLineManager.request(Exceptioninfo.this, new YcxxAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					}
					break;
				}
			}
		};
		Button btn = (Button) findViewById(R.id.btn_exception_country);
		spinner = (Spinner) findViewById(R.id.exception_country_list);
		btn.setBackgroundDrawable(spinner.getBackground());
		btn.setOnClickListener(onClickListener);
		extraString = intent.getStringExtra("nationality");
		if (extraString != null && extraString.length() > 0) {
			btn.setText(DataDictionary.getCountryName(extraString));
		} else {
			btn.setText(DataDictionary.getCountryName("CHN"));
		}
		btn = (Button) findViewById(R.id.btn_exception_ship_country);
		spinner = (Spinner) findViewById(R.id.exception_ship_country_list);
		btn.setBackgroundDrawable(spinner.getBackground());
		btn.setOnClickListener(onClickListener);
		extraString = intent.getStringExtra("nationality");
		if (extraString != null && extraString.length() > 0) {
			btn.setText(DataDictionary.getCountryName(extraString));
		} else {
			btn.setText(DataDictionary.getCountryName("CHN"));
		}
		extraString = intent.getStringExtra("eventdesc");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_desc)).setText(extraString);
		}
		extraString = intent.getStringExtra("eventremark");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_remark)).setText(extraString);
		}
		extraString = intent.getStringExtra("birthday");
		if (extraString != null && extraString.length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date birthday = df.parse(extraString);
				DatePicker datapicker = (DatePicker) findViewById(R.id.datePicker);
				datapicker.init(birthday.getYear() + 1900, birthday.getMonth(), birthday.getDate(), null);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		DatePicker checkdate = (DatePicker) findViewById(R.id.datePicker_check);
		TimePicker checktime = (TimePicker) findViewById(R.id.timePicker_check);
		checktime.setIs24HourView(true);
		extraString = intent.getStringExtra("inspecttime");
		if (extraString != null && extraString.length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date checkdatetime = df.parse(extraString);
				checkdate.init(checkdatetime.getYear() + 1900, checkdatetime.getMonth(), checkdatetime.getDate(), null);
				checktime.setCurrentHour(checkdatetime.getHours());
				checktime.setCurrentMinute(checkdatetime.getMinutes());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			Time time = new Time();
			time.setToNow();
			int hour = time.hour;
			int minute = time.minute;
			int second = time.second;
			checktime.setCurrentHour(hour);
			checktime.setCurrentMinute(minute);
		}
		extraString = intent.getStringExtra("jhhc");
		if (extraString != null && extraString.length() > 0) {
			shipId = extraString;
			shipType = "ward";
			shipTypeForObjectShip = "ward";
			relatedShipId = "";
			shipHCStr = shipId;
		}
		extraString = intent.getStringExtra("swid");
		if (extraString != null && extraString.length() > 0) {
			relatedShipId = shipHCStr;
			shipHCStr = extraString;
			shipId = extraString;
			shipType = "service";
			shipTypeForObjectShip = "service";
		}
		dockname = intent.getStringExtra("dockname");
		berthname = intent.getStringExtra("berthname");
		areaname = intent.getStringExtra("areaname");
		if (addrRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_chuan) {
			extraString = intent.getStringExtra("shipname");
			if (extraString != null && extraString.length() > 0) {
				((TextView) findViewById(R.id.exception_xuncha_chuanbo_name)).setText(extraString);
			}
			dockIdStr_01 = intent.getStringExtra("dockcode");
			if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
				((EditText) findViewById(R.id.exception_xuncha_chuanbo_matou_name)).setText(intent.getStringExtra("dockname"));
			}
			berthIdStr_01 = intent.getStringExtra("berthcode");
			if (intent.getStringExtra("berthname") != null && intent.getStringExtra("berthname").length() > 0) {
				((EditText) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).setText(intent.getStringExtra("berthname"));
			}
		} else if (addrRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_bowei) {
			dockIdStr_03 = intent.getStringExtra("dockcode");
			if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
				((EditText) findViewById(R.id.exception_addr_xuncha_bowei_matou_name)).setText(intent.getStringExtra("dockname"));
			}
			berthIdStr_03 = intent.getStringExtra("berthcode");
			if (intent.getStringExtra("berthname") != null && intent.getStringExtra("berthname").length() > 0) {
				((EditText) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).setText(intent.getStringExtra("berthname"));
			}
		} else if (addrRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_matou) {
			dockIdStr_02 = intent.getStringExtra("dockcode");
			if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
				((EditText) findViewById(R.id.exception_addr_xuncha_matou_name)).setText(intent.getStringExtra("dockname"));
			}
		} else if (addrRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_quyu) {
			areaIdStr = intent.getStringExtra("areacode");
			if (intent.getStringExtra("areaname") != null && intent.getStringExtra("areaname").length() > 0) {
				((EditText) findViewById(R.id.exception_addr_xuncha_quyu_name)).setText(intent.getStringExtra("areaname"));
			}
		}
		dealTypeRadioGroup = (RadioGroup) findViewById(R.id.deal_radio);
		dealTypeRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
		dealTypeRadioGroup.check(R.id.radio_btn_guanzhu);
		((RadioButton) findViewById(R.id.radio_btn_guanzhu)).setOnClickListener(onClickListener);
		((RadioButton) findViewById(R.id.radio_btn_meisha)).setOnClickListener(onClickListener);
		((RadioButton) findViewById(R.id.radio_btn_wubao)).setOnClickListener(onClickListener);
		((RadioButton) findViewById(R.id.radio_btn_other)).setOnClickListener(onClickListener);

		dealTypeRadioGroupForCgcs = (RadioGroup) findViewById(R.id.deal_radio_cgcs);
		dealTypeRadioGroupForCgcs.setOnCheckedChangeListener(onCheckedChangeListener);
		((RadioButton) findViewById(R.id.radio_btn_wubao_cgcs)).setOnClickListener(onClickListener);
		((RadioButton) findViewById(R.id.radio_btn_other_cgcs)).setOnClickListener(onClickListener);
		dealTypeRadioGroupForCgcs.check(R.id.radio_btn_wubao_cgcs);

		RadioGroup deal_rg = (RadioGroup) findViewById(R.id.exception_radio);
		deal_rg.setOnCheckedChangeListener(onCheckedChangeListener);
		extraString = intent.getStringExtra("whetherHandle");
		if (extraString != null && extraString.length() > 0) {
			if (extraString.equals("已处理")) {
				findViewById(R.id.deal_radiogroup).setVisibility(View.GONE);
				findViewById(R.id.exception_submit).setEnabled(false);
			}
		}
		extraString = intent.getStringExtra("action");
		if (extraString != null && extraString.length() > 0) {
			if (extraString.equals("deal")) {
				deal_rg.check(R.id.radio_btn_yes);
			}
		}
		((Button) findViewById(R.id.exception_cancel)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_submit)).setOnClickListener(onClickListener);
		TextView textview = (TextView) findViewById(R.id.object_person);
		if (textview != null) {
			textview.setOnClickListener(objectTypeClickListener);
		}
		textview = (TextView) findViewById(R.id.object_car);
		if (textview != null) {
			textview.setOnClickListener(objectTypeClickListener);
		}
		textview = (TextView) findViewById(R.id.object_ship);
		if (textview != null) {
			textview.setOnClickListener(objectTypeClickListener);
		}
		textview = (TextView) findViewById(R.id.object_device);
		if (textview != null) {
			textview.setOnClickListener(objectTypeClickListener);
		}
		textview = (TextView) findViewById(R.id.object_area);
		if (textview != null) {
			textview.setOnClickListener(objectTypeClickListener);
		}
		extraString = intent.getStringExtra("glcbmc");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_car_association_ship)).setText(extraString);
			((EditText) findViewById(R.id.exception_ship_association_ship)).setText(extraString);
			((EditText) findViewById(R.id.exception_device_association_ship)).setText(extraString);
		}
		textView = (TextView) findViewById(R.id.object_person);
		imageView = (ImageView) findViewById(R.id.image_object_person);
		spinner = (Spinner) findViewById(R.id.exception_object_type_spinner);
		if (spinner != null) {
			/** 横版设备 */
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
					DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// TODO Auto-generated method stub
					Spinner tempSpinner = (Spinner) findViewById(R.id.exception_object_type_spinner);
					String name = tempSpinner.getSelectedItem().toString();
					if (name == null || name.length() == 0) {
						return;
					}
					if (name.equals("人员")) {
						onChangeObjectTypeFocus(0, false);
					} else if (name.equals("车辆")) {
						onChangeObjectTypeFocus(1, false);
					} else if (name.equals("船舶")) {
						onChangeObjectTypeFocus(2, false);
					} else if (name.equals("设备")) {
						onChangeObjectTypeFocus(3, false);
					} else if (name.equals("区域")) {
						onChangeObjectTypeFocus(4, false);
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			extraString = intent.getStringExtra("objecttype");
			if (extraString != null && !extraString.equals("06")) {
				spinner.setSelection(DataDictionary.getDataDictionaryIndexByCode(extraString, DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE), true);
				extraString = DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE);
				if (extraString.equals("人员")) {
					onChangeObjectTypeFocus(0, false);
				} else if (extraString.equals("车辆")) {
					if (shipId != null) {
						relatedShipId = shipId;
					}
					onChangeObjectTypeFocus(1, false);
				} else if (extraString.equals("船舶")) {
					if (((EditText) findViewById(R.id.exception_ship_association_ship)).getText().toString().length() > 0) {
						findViewById(R.id.ship_association_ship_ll).setVisibility(View.VISIBLE);
					}
					onChangeObjectTypeFocus(2, false);
				} else if (extraString.equals("设备")) {
					if (shipId != null) {
						relatedShipId = shipId;
					}
					onChangeObjectTypeFocus(3, false);
				} else if (extraString.equals("区域")) {
					onChangeObjectTypeFocus(4, false);
				}
			} else {
				if (source.equals("01")) {
					spinner.setSelection(DataDictionary.getDataDictionaryIndexByName("区域", DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE), true);
					onChangeObjectTypeFocus(4, false);
				}
			}
			if (exceptionId != null && exceptionId.length() != 0) {
				spinner.setEnabled(false);
			}
		} else {
			/** 竖版设备 */
			extraString = intent.getStringExtra("objecttype");
			if (extraString != null && !extraString.equals("06")) {
				extraString = DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE);
				if (extraString.equals("人员")) {
					onChangeObjectTypeFocus(0, true);
				} else if (extraString.equals("车辆")) {
					if (shipId != null) {
						relatedShipId = shipId;
					}
					onChangeObjectTypeFocus(1, true);
				} else if (extraString.equals("船舶")) {
					if (((EditText) findViewById(R.id.exception_ship_association_ship)).getText().toString().length() > 0) {
						findViewById(R.id.ship_association_ship_ll).setVisibility(View.VISIBLE);
					}
					onChangeObjectTypeFocus(2, true);
				} else if (extraString.equals("设备")) {
					if (shipId != null) {
						relatedShipId = shipId;
					}
					onChangeObjectTypeFocus(3, true);
				} else if (extraString.equals("区域")) {
					onChangeObjectTypeFocus(4, true);
				}
				if (exceptionId != null && exceptionId.length() != 0) {
					/** 如果id不为空，就不能切换对象类别，并且隐藏其他不能选择的对象类别标签 */
					findViewById(R.id.line_object_person).setVisibility(View.INVISIBLE);
					findViewById(R.id.line_object_car).setVisibility(View.INVISIBLE);
					findViewById(R.id.line_object_ship).setVisibility(View.INVISIBLE);
					findViewById(R.id.line_object_device).setVisibility(View.INVISIBLE);
					findViewById(R.id.object_ll_car).setVisibility(View.INVISIBLE);
					findViewById(R.id.object_ll_ship).setVisibility(View.INVISIBLE);
					findViewById(R.id.object_ll_device).setVisibility(View.INVISIBLE);
					findViewById(R.id.object_ll_area).setVisibility(View.INVISIBLE);
					((TextView) findViewById(R.id.object_person)).setText(extraString);
					Resources resource = (Resources) getBaseContext().getResources();
					ColorStateList csl_s = (ColorStateList) resource.getColorStateList(R.color.selectTextColor);
					((ImageView) findViewById(R.id.image_object_person)).setImageResource(R.drawable.exception_line_s);
					((TextView) findViewById(R.id.object_person)).setTextColor(csl_s);
				}
			} else {
				if (source.equals("01")) {
					onChangeObjectTypeFocus(4, true);
				}
			}
		}
		if (extraString != null && extraString.equals("06")) {
			/** 查岗查哨特殊处理 */
			isCgcs = true;
			findViewById(R.id.object_ll).setVisibility(View.GONE);
			findViewById(R.id.viewflipper).setVisibility(View.GONE);
			findViewById(R.id.cgcs_ll).setVisibility(View.VISIBLE);
			findViewById(R.id.exception_addr_radio_xc).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_radio_cgcs).setVisibility(View.VISIBLE);
			findViewById(R.id.exception_addr_tikou).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_cgcs).setVisibility(View.VISIBLE);
			findViewById(R.id.deal_radio).setVisibility(View.GONE);
			findViewById(R.id.deal_radio_cgcs).setVisibility(View.VISIBLE);
			nameCgcs = intent.getStringExtra("name");
			if (nameCgcs != null && nameCgcs.length() > 0) {
				((TextView) findViewById(R.id.cgcs_exception_name)).setText(Html.fromHtml(getText(R.string.cgcs_name) + "<font color=\"#acacac\">"
						+ intent.getStringExtra("name") + "</font>"));
			} else {
				((TextView) findViewById(R.id.cgcs_exception_name)).setText(getText(R.string.cgcs_name));
			}
			spinner = (Spinner) findViewById(R.id.exception_event_type_spinner);
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
					DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			extraString = intent.getStringExtra("eventtype");
			if (extraString != null && extraString.length() > 0) {
				spinner.setSelection(DataDictionary.getDataDictionaryIndexByCode(extraString, DataDictionary.DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP),
						true);
			}

			RadioGroup addrRadioGroupCgcs = (RadioGroup) findViewById(R.id.exception_addr_radio_cgcs);
			addrRadioGroupCgcs.setOnCheckedChangeListener(onCheckedChangeListener);
			String sceneCgcs = intent.getStringExtra("scene");
			if (sceneCgcs != null) {
				if (sceneCgcs.equals("04")) {
					addrRadioGroupCgcs.check(R.id.radio_btn_quyu_cgcs);
				} else if (sceneCgcs.equals("05")) {
					addrRadioGroupCgcs.check(R.id.radio_btn_kakou_cgcs);
				} else {
					addrRadioGroupCgcs.check(R.id.radio_btn_tikou_cgcs);
				}
			} else {
				addrRadioGroupCgcs.check(R.id.radio_btn_tikou_cgcs);
			}
			if (addrRadioGroupCgcs.getCheckedRadioButtonId() == R.id.radio_btn_tikou_cgcs) {
				extraString = intent.getStringExtra("shipname");
				if (extraString != null && extraString.length() > 0) {
					((TextView) findViewById(R.id.exception_cgcs_chuanbo_name)).setText(extraString);
				}
				extraString = intent.getStringExtra("jhhc");
				if (extraString != null && extraString.length() > 0) {
					shipId = extraString;
					shipType = "ward";
				}
				extraString = intent.getStringExtra("swid");
				if (extraString != null && extraString.length() > 0) {
					shipId = extraString;
					shipType = "service";
				}
				dockIdStr_01 = intent.getStringExtra("dockcode");
				if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
					((EditText) findViewById(R.id.exception_cgcs_chuanbo_matou_name)).setText(intent.getStringExtra("dockname"));
				}
				berthIdStr_01 = intent.getStringExtra("berthcode");
				if (intent.getStringExtra("berthname") != null && intent.getStringExtra("berthname").length() > 0) {
					((EditText) findViewById(R.id.exception_cgsc_chuanbo_bowei_name)).setText(intent.getStringExtra("berthname"));
				}
			} else if (addrRadioGroupCgcs.getCheckedRadioButtonId() == R.id.radio_btn_kakou_cgcs) {
				areaIdStrCgcsKakou = intent.getStringExtra("areacode");
				if (intent.getStringExtra("areaname") != null && intent.getStringExtra("areaname").length() > 0) {
					((EditText) findViewById(R.id.exception_addr_cgcs_kakou_quyu_name)).setText(intent.getStringExtra("areaname"));
				}
			} else if (addrRadioGroupCgcs.getCheckedRadioButtonId() == R.id.radio_btn_quyu_cgcs) {
				areaIdStrCgcsQuyu = intent.getStringExtra("areacode");
				if (intent.getStringExtra("areaname") != null && intent.getStringExtra("areaname").length() > 0) {
					((EditText) findViewById(R.id.exception_addr_cgcs_quyu_name)).setText(intent.getStringExtra("areaname"));
				}
			}
		}
		extraString = intent.getStringExtra("cphm");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_car_num)).setText(extraString);
		}
		extraString = intent.getStringExtra("clpp");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_car_sign)).setText(extraString);
		}
		extraString = intent.getStringExtra("fdjh");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_engine_num)).setText(extraString);
		}
		extraString = intent.getStringExtra("cbzwm");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_ship_zwm)).setText(extraString);
		}
		extraString = intent.getStringExtra("cbywm");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_ship_ywm)).setText(extraString);
		}
		extraString = intent.getStringExtra("sbmc");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_device_name)).setText(extraString);
		}
		sbIdStr = intent.getStringExtra("sbid");
		extraString = intent.getStringExtra("qymc");
		if (extraString != null && extraString.length() > 0) {
			((EditText) findViewById(R.id.exception_area_name)).setText(extraString);
		}
		qyIdStr = intent.getStringExtra("qyid");
		jcfsStr = intent.getStringExtra("jcfs");
		cgcsIdStr = intent.getStringExtra("cgcsid");
		dkjlIdStr = intent.getStringExtra("dkjlid");
		((Button) findViewById(R.id.exception_name_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_car_select_association_ship)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_ship_select_zwm)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_ship_select_association_ship)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_device_select_name)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_device_select_association_ship)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_area_select_name)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_addr_xuncha_bowei_matou_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_addr_xuncha_bowei_bowei_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_addr_xuncha_matou_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_addr_xuncha_quyu_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_addr_cgcs_kakou_quyu_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_addr_cgcs_quyu_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_xuncha_chuanbo_matou_select)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_xuncha_chuanbo_bowei_select)).setOnClickListener(onClickListener);
		cgcsViewBusiness();

	}

	/**
	 * 
	 * @方法名：cgcsViewBusiness
	 * @功能说明：查岗查哨页面特殊处理
	 * @author liums
	 * @date 2013-12-4 下午4:48:35
	 */
	private void cgcsViewBusiness() {
		if (isCgcs) {
			dealTypeRadioGroup = (RadioGroup) findViewById(R.id.deal_radio);
			dealTypeRadioGroup.check(R.id.radio_btn_guanzhu);
			findViewById(R.id.exception_deal_event_type).setVisibility(View.GONE);
		}
	}

	/**
	 * 切换对象类别标签
	 * 
	 * @param index
	 *            要切换的对象类别索引
	 * @param port
	 *            是否竖版设备（如果竖版设备，需要调整界面layout高度）
	 * */
	private void onChangeObjectTypeFocus(int index, boolean port) {
		if (viewFlipper == null || viewFlipper.getDisplayedChild() == index) {
			return;
		}
		Resources resource = (Resources) getBaseContext().getResources();
		ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.textcolor);
		ColorStateList csl_s = (ColorStateList) resource.getColorStateList(R.color.selectTextColor);
		ViewGroup.LayoutParams params = viewFlipper.getLayoutParams();
		viewFlipper.setDisplayedChild(index);
		if (textView != null) {
			textView.setTextColor(csl);
		}
		if (imageView != null) {
			imageView.setImageResource(R.drawable.exception_line_n);
		}
		switch (index) {
		case 0:
			/** 人员 */
			textView = (TextView) findViewById(R.id.object_person);
			imageView = (ImageView) findViewById(R.id.image_object_person);
			if (port) {
				params.height = 710;
			} else {
				params.height = 230;
			}
			findViewById(R.id.radio_btn_chuan).setVisibility(View.VISIBLE);
			break;
		case 1:
			/** 车辆 */
			textView = (TextView) findViewById(R.id.object_car);
			imageView = (ImageView) findViewById(R.id.image_object_car);
			if (port) {
				params.height = 410;
			} else {
				params.height = 160;
			}
			if (((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).getCheckedRadioButtonId() == R.id.radio_btn_chuan) {
				((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).check(R.id.radio_btn_matou);
			}
			findViewById(R.id.radio_btn_chuan).setVisibility(View.GONE);
			break;
		case 2:
			/** 船舶 */
			textView = (TextView) findViewById(R.id.object_ship);
			imageView = (ImageView) findViewById(R.id.image_object_ship);
			if (port) {
				if (findViewById(R.id.ship_association_ship_ll).getVisibility() == View.GONE) {
					params.height = 328;
				} else {
					params.height = 410;
				}
			} else {
				if (findViewById(R.id.ship_association_ship_ll).getVisibility() == View.GONE) {
					params.height = 106;
				} else {
					params.height = 160;
				}
			}
			if (((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).getCheckedRadioButtonId() == R.id.radio_btn_chuan) {
				((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).check(R.id.radio_btn_matou);
			}
			findViewById(R.id.radio_btn_chuan).setVisibility(View.GONE);
			break;
		case 3:
			/** 设备 */
			textView = (TextView) findViewById(R.id.object_device);
			imageView = (ImageView) findViewById(R.id.image_object_device);
			if (port) {
				params.height = 170;
			} else {
				params.height = 55;
			}
			if (((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).getCheckedRadioButtonId() == R.id.radio_btn_chuan) {
				((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).check(R.id.radio_btn_matou);
			}
			findViewById(R.id.radio_btn_chuan).setVisibility(View.GONE);
			break;
		case 4:
			/** 区域 */
			textView = (TextView) findViewById(R.id.object_area);
			imageView = (ImageView) findViewById(R.id.image_object_area);
			if (port) {
				params.height = 80;
			} else {
				params.height = 55;
			}
			if (((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).getCheckedRadioButtonId() == R.id.radio_btn_chuan) {
				((RadioGroup) findViewById(R.id.exception_addr_radio_xc)).check(R.id.radio_btn_matou);
			}
			findViewById(R.id.radio_btn_chuan).setVisibility(View.GONE);
			break;
		}
		if (viewFlipper.getDisplayedChild() == 3 || viewFlipper.getDisplayedChild() == 4) {
			findViewById(R.id.radio_btn_guanzhu).setVisibility(View.GONE);
			// findViewById(R.id.radio_btn_meisha).setVisibility(View.GONE);
			dealTypeRadioGroup.check(R.id.radio_btn_other);
		} else {
			findViewById(R.id.radio_btn_guanzhu).setVisibility(View.VISIBLE);
			findViewById(R.id.radio_btn_meisha).setVisibility(View.VISIBLE);
			dealTypeRadioGroup.check(R.id.radio_btn_guanzhu);
		}
		if (textView != null) {
			textView.setTextColor(csl_s);
		}
		if (imageView != null) {
			imageView.setImageResource(R.drawable.exception_line_s);
		}
		viewFlipper.setLayoutParams(params);
	}

	/** 处理对象类别标签被点击事件 */
	private OnClickListener objectTypeClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (exceptionId != null && exceptionId.length() != 0) {
				return;
			}
			switch (v.getId()) {
			case R.id.object_person:
				onChangeObjectTypeFocus(0, true);
				break;
			case R.id.object_car:
				onChangeObjectTypeFocus(1, true);
				break;
			case R.id.object_ship:
				onChangeObjectTypeFocus(2, true);
				break;
			case R.id.object_device:
				onChangeObjectTypeFocus(3, true);
				break;
			case R.id.object_area:
				onChangeObjectTypeFocus(4, true);
				break;
			}
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	/** 启动新的一个查询界面的activity返回时被调用改方法 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_SELECT_SHIP:
			if (resultCode == RESULT_OK) {
				((EditText) findViewById(R.id.exception_xuncha_chuanbo_name)).setText(data.getStringExtra("shipname"));
				shipId = data.getStringExtra("shipid");
				shipType = data.getStringExtra("shiptype");
				findViewById(R.id.exception_xuncha_chuanbo_matou_select).setVisibility(View.VISIBLE);
				findViewById(R.id.exception_xuncha_chuanbo_bowei_select).setVisibility(View.VISIBLE);
				dockIdStr_01 = data.getStringExtra("tkmt");
				berthIdStr_01 = data.getStringExtra("tkbw");
				String str = data.getStringExtra("tkwz");
				if (str != null) {
					String[] temp_str = str.split(",");
					if (temp_str.length > 0) {
						dockname = temp_str[0];
						((EditText) findViewById(R.id.exception_xuncha_chuanbo_matou_name)).setText(temp_str[0]);
					} else {
						((EditText) findViewById(R.id.exception_xuncha_chuanbo_matou_name)).setText("");
					}
					if (temp_str.length > 1) {
						berthname = temp_str[1];
						((EditText) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).setText(temp_str[1]);
					} else {
						((EditText) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).setText("");
					}
				} else {
					((EditText) findViewById(R.id.exception_xuncha_chuanbo_matou_name)).setText("");
					((EditText) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).setText("");
				}
			}
			break;
		case STARTACTIVITY_FOR_SELECT_SHIP_CGCS:
			if (resultCode == RESULT_OK) {
				((EditText) findViewById(R.id.exception_cgcs_chuanbo_name)).setText(data.getStringExtra("shipname"));
				shipId = data.getStringExtra("shipid");
				shipType = data.getStringExtra("shiptype");
				dockIdStr_01 = data.getStringExtra("tkmt");
				berthIdStr_01 = data.getStringExtra("tkbw");
				String str = data.getStringExtra("tkwz");
				if (str != null) {
					String[] tempStrSplit = str.split(",");
					if (tempStrSplit.length > 0) {
						dockname = tempStrSplit[0];
						((EditText) findViewById(R.id.exception_cgcs_chuanbo_matou_name)).setText(tempStrSplit[0]);
					} else {
						((EditText) findViewById(R.id.exception_cgcs_chuanbo_matou_name)).setText("");
					}
					if (tempStrSplit.length > 1) {
						berthname = tempStrSplit[1];
						((EditText) findViewById(R.id.exception_cgsc_chuanbo_bowei_name)).setText(tempStrSplit[1]);
					} else {
						((EditText) findViewById(R.id.exception_cgsc_chuanbo_bowei_name)).setText("");
					}
				} else {
					((EditText) findViewById(R.id.exception_cgcs_chuanbo_matou_name)).setText("");
					((EditText) findViewById(R.id.exception_cgsc_chuanbo_bowei_name)).setText("");
				}
			}
			break;
		case STARTACTIVITY_FOR_SELECT_NATIONALITY:
			if (resultCode == RESULT_OK) {
				int pos = data.getIntExtra("selectitem", 0);
				Button btn = (Button) findViewById(R.id.btn_exception_country);
				if (btn != null) {
					btn.setText(DataDictionary.getCountryName(pos));
				}
				btn = (Button) findViewById(R.id.btn_exception_ship_country);
				if (btn != null) {
					btn.setText(DataDictionary.getCountryName(pos));
				}
			}
			break;
		case STARTACTIVITY_FOR_SELECT_PERSON:
			if (resultCode == RESULT_OK) {
				((EditText) findViewById(R.id.exception_name)).setEnabled(false);
				((EditText) findViewById(R.id.exception_name)).setText(data.getStringExtra("xm"));
				RadioGroup sexrg = (RadioGroup) findViewById(R.id.exception_sex_radio);
				if (DataDictionary.getDataDictionaryName(data.getStringExtra("xb"), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE).equals("女")) {
					sexrg.check(R.id.radio_btn_no);
				} else {
					sexrg.check(R.id.radio_btn_yes);
				}
				Button btn = (Button) findViewById(R.id.btn_exception_country);
				String tempExtrastr = data.getStringExtra("gj");
				if (tempExtrastr != null && tempExtrastr.length() > 0) {
					btn.setText(DataDictionary.getCountryName(tempExtrastr));
				} else {
					btn.setText(DataDictionary.getCountryName("CHN"));
				}
				tempExtrastr = data.getStringExtra("csrq");
				if (tempExtrastr != null) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date birthday = df.parse(tempExtrastr);
						DatePicker datapicker = (DatePicker) findViewById(R.id.datePicker);
						datapicker.init(birthday.getYear() + 1900, birthday.getMonth(), birthday.getDate(), null);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				((EditText) findViewById(R.id.exception_unit)).setText(data.getStringExtra("ssdw"));
				((EditText) findViewById(R.id.exception_cardnum)).setText(data.getStringExtra("zjhm"));
				spinner = (Spinner) findViewById(R.id.exception_card_type_spinner);
				tempExtrastr = data.getStringExtra("zjzl");
				if (tempExtrastr != null) {
					spinner.setSelection(
							DataDictionary.getDataDictionaryIndexByCode(tempExtrastr, DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE), true);
				}
			}
			break;
		case STARTACTIVITY_FOR_SELECT_ASSOCIATION_SHIP:
			if (resultCode == RESULT_OK) {
				((EditText) findViewById(R.id.exception_car_association_ship)).setText(data.getStringExtra("shipname"));
				((EditText) findViewById(R.id.exception_ship_association_ship)).setText(data.getStringExtra("shipname"));
				((EditText) findViewById(R.id.exception_device_association_ship)).setText(data.getStringExtra("shipname"));
				relatedShipId = data.getStringExtra("shipid");
			}
			break;
		case STARTACTIVITY_FOR_SELECT_SHIP_ZWM:
			if (resultCode == RESULT_OK) {
				((EditText) findViewById(R.id.exception_ship_zwm)).setEnabled(false);

				((EditText) findViewById(R.id.exception_ship_zwm)).setText(data.getStringExtra("shipname"));
				String tempExtraStr = data.getStringExtra("shipengname");
				if (tempExtraStr != null) {
					((EditText) findViewById(R.id.exception_ship_ywm)).setText(tempExtraStr);
				} else {
					((EditText) findViewById(R.id.exception_ship_ywm)).setText("");
				}
				if (tempExtraStr != null && tempExtraStr.length() > 0) {
					((EditText) findViewById(R.id.exception_ship_ywm)).setEnabled(false);
				} else {
					((EditText) findViewById(R.id.exception_ship_ywm)).setEnabled(true);
				}
				tempExtraStr = data.getStringExtra("gj");
				if (tempExtraStr != null && tempExtraStr.length() > 0) {
					((Button) findViewById(R.id.btn_exception_ship_country)).setText(DataDictionary.getCountryName(tempExtraStr));
					findViewById(R.id.btn_exception_ship_country).setEnabled(false);
				} else {
					findViewById(R.id.btn_exception_ship_country).setEnabled(true);
				}
				shipTypeForObjectShip = data.getStringExtra("shiptype");
				shipHCStr = data.getStringExtra("shipid");
				if (shipTypeForObjectShip != null) {
					ViewGroup.LayoutParams params = viewFlipper.getLayoutParams();
					Spinner tempSpinner = (Spinner) findViewById(R.id.exception_object_type_spinner);
					if (shipTypeForObjectShip.equals("ward")) {
						findViewById(R.id.ship_association_ship_ll).setVisibility(View.GONE);
						if (tempSpinner == null) {
							params.height = 328;
						} else {
							params.height = 106;
						}
						relatedShipId = "";
						((EditText) findViewById(R.id.exception_ship_association_ship)).setText("");
						RadioGroup tempScentRadioGroup = (RadioGroup) findViewById(R.id.exception_addr_radio_xc);
						tempScentRadioGroup.check(R.id.radio_btn_bowei);
						dockIdStr_03 = data.getStringExtra("tkmt");
						berthIdStr_03 = data.getStringExtra("tkbw");
						String tkwzStr = data.getStringExtra("tkwz");
						if (tkwzStr != null) {
							String[] tempStrSplit = tkwzStr.split(",");
							if (tempStrSplit.length > 0) {
								dockname = tempStrSplit[0];
								((EditText) findViewById(R.id.exception_addr_xuncha_bowei_matou_name)).setText(tempStrSplit[0]);
							} else {
								((EditText) findViewById(R.id.exception_addr_xuncha_bowei_matou_name)).setText("");
							}
							if (tempStrSplit.length > 1) {
								berthname = tempStrSplit[1];
								((EditText) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).setText(tempStrSplit[1]);
							} else {
								((EditText) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).setText("");
							}
						} else {
							((EditText) findViewById(R.id.exception_addr_xuncha_bowei_matou_name)).setText("");
							((EditText) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).setText("");
						}
					} else {
						findViewById(R.id.ship_association_ship_ll).setVisibility(View.VISIBLE);
						if (tempSpinner == null) {
							params.height = 410;
						} else {
							params.height = 160;
						}
					}
					viewFlipper.setLayoutParams(params);
				}
			}
			break;
		case STARTACTIVITY_FOR_SELECT_DEVICE_NAME:
			if (resultCode == RESULT_OK) {
				((EditText) findViewById(R.id.exception_device_name)).setEnabled(false);
				((EditText) findViewById(R.id.exception_device_name)).setText(data.getStringExtra("sbmc"));
				sbIdStr = data.getStringExtra("sbid");
			}
			break;
		case STARTACTIVITY_FOR_SELECT_AREA_NAME:
			break;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null && str.equals("1")) {
			HgqwToast.makeText(Exceptioninfo.this, R.string.save_success, HgqwToast.LENGTH_LONG).show();
			Intent data = new Intent();
			data.putExtra("eventdesc", ((EditText) findViewById(R.id.exception_desc)).getText().toString());// 更新事件描述
			data.putExtra("eventremark", ((EditText) findViewById(R.id.exception_remark)).getText().toString());// 更新时间备注
			if (whetherHandle == 1) {
				data.putExtra("whetherHandle", 1);
				data.putExtra("handleType", handleType);
				data.putExtra("handleEventType", handleEventType);
				data.putExtra("handleResult", handleResult);
				data.putExtra("handleRemark", handleRemark);

			}
			setResult(RESULT_OK, data);
			finish();
		} else {
			HgqwToast.makeText(Exceptioninfo.this, R.string.save_failure, HgqwToast.LENGTH_LONG).show();
		}
		// 先注释离线记录异常功能
		/*
		 * else { Message msg=new Message(); handler.sendMessage(msg); }
		 */
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (res.first) {
			HgqwToast.makeText(Exceptioninfo.this, R.string.save_success, HgqwToast.LENGTH_LONG).show();
			if (whetherHandle == 1) {
				Intent data = new Intent();
				data.putExtra("whetherHandle", 1);
				data.putExtra("handleType", handleType);
				data.putExtra("handleEventType", handleEventType);
				data.putExtra("handleResult", handleResult);
				data.putExtra("handleRemark", handleRemark);
				setResult(RESULT_OK, data);
			}
			finish();
		} else {
			HgqwToast.makeText(Exceptioninfo.this, R.string.save_failure, HgqwToast.LENGTH_LONG).show();
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			backupparams.add(new BasicNameValuePair("exceptionID", ""));
			OffLineManager.request(Exceptioninfo.this, new YcxxAction(), "sendIllegalInfo", NVPairTOMap.nameValuePairTOMap(backupparams), 0);
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showBackDialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showBackDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("提示");
		alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
		alertDialogBuilder.setMessage("异常信息未保存，是否确定退出当前页面？");
		alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertDialogBuilder.show();
	}

}
