package com.pingtech.hgqw.module.exception.activity;

//异常信息详细录入界面，包括从梯口管理进入异常信息模块后启动新增或处理或查看详情、从梯口管理的刷卡登记模块进入异常信息登记、从巡查巡检进入异常信息新增或处理或查看详情
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 登记异常信息界面的activity类
 * */
public class ExceptionView extends MyActivity implements OnHttpResult {
	private static final String TAG = "ExceptionView";

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

	private ProgressDialog progressDialog = null;

	protected int whetherHandle;
	private String handle;
	private String nameCgcs = null;

	private OnClickListener onClickListener;
	private ImageView imageView;
	private TextView textView;
	private Intent intent;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.exceptionview);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Log.i(TAG, "onCreate()");
		intent = getIntent();
		from = intent.getStringExtra("from");
		source = intent.getStringExtra("source");
		if (source != null && source.equals("04")) {
			source = "03";
		}
		if (source == null || source.length() == 0) {
			source = from;
		}
		handle =intent.getStringExtra("whetherHandle");
		Button button =(Button)findViewById(R.id.exception_deal);
		  if (handle!=null&&!"0".equals(handle)) {
			  button.setEnabled(false);
			  findViewById(R.id.weatherdeal).setVisibility(View.VISIBLE);
		}
		windowType = intent.getStringExtra("windowtype");
		if (from.equals("02")) {
			setMyActiveTitle(getString(R.string.exception_info));
		} else if (from.equals("03")) {
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.exception_info));
		} else if (from.equals("01")) {
			setMyActiveTitle( getString(R.string.exception_info));
		}else if ( FlagManagers.XCXJ_CGCS.equals(from)) {
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.chagangchashao));
		}
		((TextView) findViewById(R.id.check_name)).setText(Html.fromHtml("<font color=\"#acacac\">"
				+ LoginUser.getCurrentLoginUser().getName() + "</font>"));
		((TextView) findViewById(R.id.check_unit)).setText(Html.fromHtml( "<font color=\"#acacac\">"
				+ LoginUser.getCurrentLoginUser().getUserSsdw() + "</font>"));
		findViewById(R.id.exception_addr_xuncha).setVisibility(View.VISIBLE);

		String extraString;
		extraString = intent.getStringExtra("eventtype");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_event_type_spinner)).setText(DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_EVENT_TYPE));
		}
		extraString = intent.getStringExtra("cardtype");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_card_type_spinner)).setText(DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		}
		exceptionId = intent.getStringExtra("id");
		extraString = intent.getStringExtra("cardnumber");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_cardnum)).setText(extraString);
		}
		extraString = intent.getStringExtra("name");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_name)).setText(extraString);
		}
		extraString = intent.getStringExtra("sex");
		if (extraString != null && extraString.length() > 0) {
			if (DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE).equals("女")) {
				((TextView) findViewById(R.id.exception_sex_radio)).setText("女");
			} else {
				((TextView) findViewById(R.id.exception_sex_radio)).setText("男");
			}
		}
		extraString = intent.getStringExtra("company");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_unit)).setText(extraString);
			((TextView) findViewById(R.id.exception_car_unit)).setText(extraString);
			((TextView) findViewById(R.id.exception_ship_unit)).setText(extraString);
		}
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {

				/** 取消，返回上一级 */
				case R.id.exception_back:
					finish();
					break;
				/** 保存 */
				case R.id.exception_deal:
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					Intent itn=new Intent();
					itn.putExtra("action", "deal");
					itn.putExtra("windowtype", "03");
					itn.putExtra("id", intent.getStringExtra("id"));
					itn.putExtra("objecttype", intent.getStringExtra("objecttype"));
					itn.putExtra("cardnumber", intent.getStringExtra("cardnumber"));
					itn.putExtra("cardtype", intent.getStringExtra("cardtype"));
					itn.putExtra("eventtype", intent.getStringExtra("eventtype"));
					itn.putExtra("name", intent.getStringExtra("name"));
					itn.putExtra("sex", intent.getStringExtra("sex"));
					itn.putExtra("nationality", intent.getStringExtra("nationality"));
					itn.putExtra("birthday", intent.getStringExtra("birthday"));
					itn.putExtra("company", intent.getStringExtra("company"));
					itn.putExtra("source", intent.getStringExtra("source"));
					itn.putExtra("from", intent.getStringExtra("from"));
					itn.putExtra("eventdesc", intent.getStringExtra("eventdesc"));
					itn.putExtra("eventremark", intent.getStringExtra("eventremark"));
					itn.putExtra("scene", intent.getStringExtra("scene"));
					itn.putExtra("inspecttime", intent.getStringExtra("inspecttime"));
					itn.putExtra("shipname", intent.getStringExtra("shipname"));
					itn.putExtra("swid", intent.getStringExtra("swid"));
					itn.putExtra("jhhc", intent.getStringExtra("jhhc"));
					itn.putExtra("dockcode", intent.getStringExtra("dockcode"));
					itn.putExtra("berthcode", intent.getStringExtra("berthcode"));
					itn.putExtra("areacode", intent.getStringExtra("areacode"));
					itn.putExtra("whetherHandle", intent.getStringExtra("whetherHandle"));
					itn.putExtra("glcbmc", intent.getStringExtra("glcbmc"));
					itn.putExtra("cphm", intent.getStringExtra("cphm"));
					itn.putExtra("clpp", intent.getStringExtra("clpp"));
					itn.putExtra("fdjh", intent.getStringExtra("fdjh"));
					itn.putExtra("cbzwm", intent.getStringExtra("cbzwm"));
					itn.putExtra("cbywm", intent.getStringExtra("cbywm"));
					itn.putExtra("sbmc", intent.getStringExtra("sbmc"));
					itn.putExtra("sbid", intent.getStringExtra("sbid"));
					itn.putExtra("qymc", intent.getStringExtra("qymc"));
					itn.putExtra("qyid", intent.getStringExtra("qyid"));
					itn.putExtra("jcfs", intent.getStringExtra("jcfs"));
					itn.putExtra("cgcsid", intent.getStringExtra("cgcsid"));
					itn.putExtra("dkjlid", intent.getStringExtra("dkjlid"));
					itn.putExtra("dockname", intent.getStringExtra("dockname"));
					itn.putExtra("berthname", intent.getStringExtra("berthname"));
					itn.putExtra("areaname", intent.getStringExtra("areaname"));
					itn.putExtra("sbkid", intent.getStringExtra("sbkid"));
					itn.putExtra("exceptionID", intent.getStringExtra("exceptionID"));
					itn.setClass(ExceptionView.this, Exceptioninfo.class);
					//private static final int STARTACTIVITY_FOR_EXCEPTION_DETAIL = 2;
					startActivityForResult(itn, 2);
					break;
				}
			}
		};

		extraString = intent.getStringExtra("nationality");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.btn_exception_country)).setText(DataDictionary.getCountryName(extraString));
		} else {
			((TextView) findViewById(R.id.btn_exception_country)).setText(DataDictionary.getCountryName("CHN"));
		}
		extraString = intent.getStringExtra("nationality");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.btn_exception_ship_country)).setText(DataDictionary.getCountryName(extraString));
		} else {
			((TextView) findViewById(R.id.btn_exception_ship_country)).setText(DataDictionary.getCountryName("CHN"));
		}
		extraString = intent.getStringExtra("eventdesc");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_desc)).setText(extraString);
		}
		extraString = intent.getStringExtra("eventremark");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_remark)).setText(extraString);
		}
		extraString = intent.getStringExtra("birthday");
		if (extraString != null && extraString.length() > 0) {
				((TextView) findViewById(R.id.datePicker)).setText(extraString);
		}
	
		extraString =intent.getStringExtra("inspecttime");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dfy = new SimpleDateFormat("yyyy-MM-dd");
		if (extraString != null && extraString.length() > 0) {
			try {
				Date checkdatetime = df.parse(extraString);
				((TextView) findViewById(R.id.datePicker_check)).setText(dfy.format(checkdatetime));
				((TextView) findViewById(R.id.timePicker_check)).setText(checkdatetime.getHours()+":"+checkdatetime.getMinutes());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}else{
			Time time = new Time();
			time.setToNow();
			int hour = time.hour;
			int minute = time.minute;
			((TextView) findViewById(R.id.timePicker_check)).setText(hour+":"+minute);
			((TextView) findViewById(R.id.datePicker_check)).setText(dfy.format(new Date()));
		}


		String scene = intent.getStringExtra("scene");
		 if (scene.equals("02")) {
			findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.VISIBLE);
			findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.GONE);
		}else if (scene.equals("03")) {
			findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.VISIBLE);
			findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.GONE);
		}  else if (scene.equals("04")) {
			findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.exception_addr_xuncha_chuanbo).setVisibility(View.VISIBLE);
			findViewById(R.id.exception_addr_xuncha_matou).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_bowei).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_xuncha_quyu).setVisibility(View.GONE);
		}
		
	
			if (scene.equals("04")) {
				((TextView) findViewById(R.id.exception_addr_radio_xc)).setText("区域");

				if (intent.getStringExtra("areaname") != null && intent.getStringExtra("areaname").length() > 0) {
					((TextView) findViewById(R.id.exception_addr_xuncha_quyu_name)).setText(intent.getStringExtra("areaname"));
				}
			
			} else if (scene.equals("02")) {
				((TextView) findViewById(R.id.exception_addr_radio_xc)).setText("泊位");

				if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
					((TextView) findViewById(R.id.exception_addr_xuncha_bowei_matou_name)).setText(intent.getStringExtra("dockname"));
				}
				if (intent.getStringExtra("berthname") != null && intent.getStringExtra("berthname").length() > 0) {
					((TextView) findViewById(R.id.exception_addr_xuncha_bowei_bowei_name)).setText(intent.getStringExtra("berthname"));
				}
			
			} else if (scene.equals("03")) {
				((TextView) findViewById(R.id.exception_addr_radio_xc)).setText("码头");

				if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
					((TextView) findViewById(R.id.exception_addr_xuncha_matou_name)).setText(intent.getStringExtra("dockname"));
				}
			
			} else {
				((TextView) findViewById(R.id.exception_addr_radio_xc)).setText("船舶");
				extraString = intent.getStringExtra("shipname");
				if (extraString != null && extraString.length() > 0) {
					((TextView) findViewById(R.id.exception_xuncha_chuanbo_name)).setText(extraString);
				}
				if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
					((TextView) findViewById(R.id.exception_xuncha_chuanbo_matou_name)).setText(intent.getStringExtra("dockname"));
				}
				if (intent.getStringExtra("berthname") != null && intent.getStringExtra("berthname").length() > 0) {
					((TextView) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).setText(intent.getStringExtra("berthname"));
				}
			}
		if ("02".equals(windowType)) {
			((TextView) findViewById(R.id.exception_addr_radio_xc)).setText("船舶");
			extraString = intent.getStringExtra("shipname");
			if (extraString != null && extraString.length() > 0) {
				((TextView) findViewById(R.id.exception_xuncha_chuanbo_name)).setText(extraString);
			}
			if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
				((EditText) findViewById(R.id.exception_xuncha_chuanbo_matou_name)).setText(intent.getStringExtra("dockname"));
			}
			if (intent.getStringExtra("berthname") != null && intent.getStringExtra("berthname").length() > 0) {
				((EditText) findViewById(R.id.exception_xuncha_chuanbo_bowei_name)).setText(intent.getStringExtra("berthname"));
			}
		}

		((Button) findViewById(R.id.exception_back)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.exception_deal)).setOnClickListener(onClickListener);
		extraString = intent.getStringExtra("glcbmc");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_car_association_ship)).setText(extraString);
			((TextView) findViewById(R.id.exception_ship_association_ship)).setText(extraString);
			((TextView) findViewById(R.id.exception_device_association_ship)).setText(extraString);
		}
		textView = (TextView) findViewById(R.id.object_person);
		imageView = (ImageView) findViewById(R.id.image_object_person);
			/** 竖版设备 */
			extraString = intent.getStringExtra("objecttype");
			if (extraString != null && !extraString.equals("06")) {
				extraString = DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE);
				if (extraString.equals("人员")) {
					onChangeObjectTypeFocus(0, true);
				} else if (extraString.equals("车辆")) {
					onChangeObjectTypeFocus(1, true);
				} else if (extraString.equals("船舶")) {
					if (((TextView) findViewById(R.id.exception_ship_association_ship)).getText().toString().length() > 0) {
						findViewById(R.id.ship_association_ship_ll).setVisibility(View.VISIBLE);
					}
					onChangeObjectTypeFocus(2, true);
				} else if (extraString.equals("设备")) {
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
	
		if (extraString != null && extraString.equals("06")) {
			/** 查岗查哨特殊处理 */
			findViewById(R.id.object_ll).setVisibility(View.GONE);
			findViewById(R.id.viewflipper).setVisibility(View.GONE);
			findViewById(R.id.cgcs_ll).setVisibility(View.VISIBLE);
			findViewById(R.id.exception_addr_radio_xc).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_radio_cgcs).setVisibility(View.VISIBLE);
			findViewById(R.id.exception_addr_xuncha).setVisibility(View.GONE);
			findViewById(R.id.exception_addr_cgcs).setVisibility(View.VISIBLE);
			nameCgcs = intent.getStringExtra("name");
			if (nameCgcs != null && nameCgcs.length() > 0) {
				((TextView) findViewById(R.id.cgcs_exception_name)).setText(Html.fromHtml(getText(R.string.cgcs_name) + "<font color=\"#acacac\">"
						+ intent.getStringExtra("name") + "</font>"));
			} else {
				((TextView) findViewById(R.id.cgcs_exception_name)).setText(getText(R.string.cgcs_name));
			}
			extraString = intent.getStringExtra("eventtype");
			if (extraString != null && extraString.length() > 0) {
				((TextView) findViewById(R.id.exception_event_type_spinner)).setText(DataDictionary.getDataDictionaryName(extraString,  DataDictionary.DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP));
			}

				String sceneCgcs = intent.getStringExtra("scene");
				if (sceneCgcs.equals("04")) {
					((TextView) findViewById(R.id.exception_addr_radio_cgcs)).setText("区域");
					findViewById(R.id.exception_addr_cgcs_tikou).setVisibility(View.GONE);
					findViewById(R.id.exception_addr_cgcs_kakou).setVisibility(View.GONE);
					findViewById(R.id.exception_addr_cgcs_quyu).setVisibility(View.VISIBLE);
				} else if (sceneCgcs.equals("05")) {
					((TextView) findViewById(R.id.exception_addr_radio_cgcs)).setText("卡口");
					findViewById(R.id.exception_addr_cgcs_tikou).setVisibility(View.GONE);
					findViewById(R.id.exception_addr_cgcs_kakou).setVisibility(View.VISIBLE);
					findViewById(R.id.exception_addr_cgcs_quyu).setVisibility(View.GONE);
				} else {
					((TextView) findViewById(R.id.exception_addr_radio_cgcs)).setText("梯口");
					findViewById(R.id.exception_addr_cgcs_tikou).setVisibility(View.VISIBLE);
					findViewById(R.id.exception_addr_cgcs_kakou).setVisibility(View.GONE);
					findViewById(R.id.exception_addr_cgcs_quyu).setVisibility(View.GONE);
				}

			if (sceneCgcs.equals("05")) {
				if (intent.getStringExtra("areaname") != null && intent.getStringExtra("areaname").length() > 0) {
					((TextView) findViewById(R.id.exception_addr_cgcs_kakou_quyu_name)).setText(intent.getStringExtra("areaname"));
				}
			} else if (sceneCgcs.equals("04")) {
				if (intent.getStringExtra("areaname") != null && intent.getStringExtra("areaname").length() > 0) {
					((TextView) findViewById(R.id.exception_addr_cgcs_quyu_name)).setText(intent.getStringExtra("areaname"));
				}
			}else {

				extraString = intent.getStringExtra("shipname");
				if (extraString != null && extraString.length() > 0) {
					((TextView) findViewById(R.id.exception_cgcs_chuanbo_name)).setText(extraString);
				}
				if (intent.getStringExtra("dockname") != null && intent.getStringExtra("dockname").length() > 0) {
					((TextView) findViewById(R.id.exception_cgcs_chuanbo_matou_name)).setText(intent.getStringExtra("dockname"));
				}
				if (intent.getStringExtra("berthname") != null && intent.getStringExtra("berthname").length() > 0) {
					((TextView) findViewById(R.id.exception_cgsc_chuanbo_bowei_name)).setText(intent.getStringExtra("berthname"));
				}
			
			}
			
		}

		extraString = intent.getStringExtra("cphm");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_car_num)).setText(extraString);
		}
		extraString = intent.getStringExtra("clpp");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_car_sign)).setText(extraString);
		}
		extraString = intent.getStringExtra("fdjh");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_engine_num)).setText(extraString);
		}
		extraString = intent.getStringExtra("cbzwm");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_ship_zwm)).setText(extraString);
		}
		extraString = intent.getStringExtra("cbywm");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_ship_ywm)).setText(extraString);
		}
		extraString = intent.getStringExtra("sbmc");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_device_name)).setText(extraString);
		}
		extraString = intent.getStringExtra("qymc");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.exception_area_name)).setText(extraString);
		}
		extraString = intent.getStringExtra("objecttype");
		if (extraString != null && extraString.equals("06")) {
			extraString = intent.getStringExtra("handleType");
			if (extraString != null && extraString.length() > 0) {
			 if ("03".equals(extraString)) {
					((TextView) findViewById(R.id.relativelayout)).setText("误报");
					findViewById(R.id.deal_result_linear).setVisibility(View.VISIBLE);
					findViewById(R.id.deal_remark_linear).setVisibility(View.VISIBLE);
				} else {
					((TextView) findViewById(R.id.relativelayout)).setText("其他");
					findViewById(R.id.deal_result_linear).setVisibility(View.VISIBLE);
					findViewById(R.id.deal_remark_linear).setVisibility(View.VISIBLE);
					extraString = intent.getStringExtra("handleEventType");
					if (extraString != null && extraString.length() > 0) {
						findViewById(R.id.dealtype).setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.deal_event_type_spinner)).setText(DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP));
					}
				}
			}

		} else {
			extraString = intent.getStringExtra("handleType");
			if (extraString != null && extraString.length() > 0) {
				if ("01".equals(extraString)) {
					((TextView) findViewById(R.id.relativelayout)).setText("重点关注");
					findViewById(R.id.deal_result_linear).setVisibility(View.VISIBLE);
					findViewById(R.id.deal_remark_linear).setVisibility(View.VISIBLE);
					extraString = intent.getStringExtra("handleEventType");
					if (extraString != null && extraString.length() > 0) {
						findViewById(R.id.dealtype).setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.deal_event_type_spinner)).setText(DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE));
					}

				} else if ("02".equals(extraString)) {
					((TextView) findViewById(R.id.relativelayout)).setText("梅沙处理");
				} else if ("03".equals(extraString)) {
					((TextView) findViewById(R.id.relativelayout)).setText("误报");
					findViewById(R.id.deal_result_linear).setVisibility(View.VISIBLE);
					findViewById(R.id.deal_remark_linear).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.deal_result_linear).setVisibility(View.VISIBLE);
					findViewById(R.id.deal_remark_linear).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.relativelayout)).setText("其他");
					extraString = intent.getStringExtra("handleEventType");
					if (extraString != null && extraString.length() > 0) {
						findViewById(R.id.dealtype).setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.deal_event_type_spinner)).setText(DataDictionary.getDataDictionaryName(extraString, DataDictionary.DATADICTIONARY_TYPE_QT_EVENT_TYPE));
					}
				}
			}

		}
		extraString = intent.getStringExtra("handleRemark");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.deal_remark)).setText(extraString);
		}
		extraString = intent.getStringExtra("handleResult");
		if (extraString != null && extraString.length() > 0) {
			((TextView) findViewById(R.id.deal_result)).setText(extraString);
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

		Resources resource = (Resources) getBaseContext().getResources();
		ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.textcolor);
		ColorStateList csl_s = (ColorStateList) resource.getColorStateList(R.color.selectTextColor);
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
			findViewById(R.id.person).setVisibility(View.VISIBLE);
			break;
		case 1:
			/** 车辆 */
			findViewById(R.id.car).setVisibility(View.VISIBLE);
			textView = (TextView) findViewById(R.id.object_car);
			imageView = (ImageView) findViewById(R.id.image_object_car);
		
			break;
		case 2:
			/** 船舶 */
			textView = (TextView) findViewById(R.id.object_ship);
			imageView = (ImageView) findViewById(R.id.image_object_ship);
			findViewById(R.id.ship).setVisibility(View.VISIBLE);
		
			break;
		case 3:
			/** 设备 */
			textView = (TextView) findViewById(R.id.object_device);
			imageView = (ImageView) findViewById(R.id.image_object_device);
			findViewById(R.id.device).setVisibility(View.VISIBLE);
		
			break;
		case 4:
			/** 区域 */
			textView = (TextView) findViewById(R.id.object_area);
			imageView = (ImageView) findViewById(R.id.image_object_area);
			findViewById(R.id.area).setVisibility(View.VISIBLE);
		
			break;
		}

		if (textView != null) {
			textView.setTextColor(csl_s);
		}
		if (imageView != null) {
			imageView.setImageResource(R.drawable.exception_line_s);
		}
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			int whetherHandle = data.getIntExtra("whetherHandle", 0);
			if (whetherHandle == 1) {
				data.putExtra("whetherHandle", 1);
				data.putExtra("handleType", data.getStringExtra("handleType"));
				data.putExtra("handleEventType", data.getStringExtra("handleEventType"));
				data.putExtra("handleResult", data.getStringExtra("handleResult"));
				data.putExtra("handleRemark", data.getStringExtra("handleRemark"));
				data.putExtra("ifUpdate", true);
				setResult(RESULT_OK, data);
			}
		}
		finish();
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
			HgqwToast.makeText(ExceptionView.this, R.string.save_success, HgqwToast.LENGTH_LONG).show();
			if (whetherHandle == 1) {
				Intent data = new Intent();
				setResult(RESULT_OK, data);
			}
			finish();
		} else {
			HgqwToast.makeText(ExceptionView.this, R.string.save_failure, HgqwToast.LENGTH_LONG).show();
		}
	}
}
