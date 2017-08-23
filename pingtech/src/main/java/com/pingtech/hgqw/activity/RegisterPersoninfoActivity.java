package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.kakou.action.KakouAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.tikou.action.TkglAction;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.xunjian.action.XunJianAction;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 输入证件号码后，如果该号码未登记，将进入该模块，手动保存人员信息
 * */

public class RegisterPersoninfoActivity extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "RegisterPersoninfoActivity";

	/**
	 * true手动选择船员，false非船员
	 */
	private boolean sailorFlag = false;

	/** 进入选择国家信息 */
	private static final int STARTACTIVITY_FOR_SELECT_NATIONALITY = 1;

	private ArrayAdapter<String> adapter;

	private Spinner spinner;

	private String source;

	/** 绑定的船舶航次号 */
	private String voyageNumber;

	/** 证件号码 */
	private String zjhm;

	private ProgressDialog progressDialog = null;

	private String httpReturnXMLInfo;

	/** 巡查巡检id */
	private String xcxsidStr;

	/** 通行记录id */
	private String txjlidStr;

	/** 对象类别 */
	private String objectTypeStr;

	/** 验证返回的结果 */
	private String httpReturnInfo;

	/** 用来记录登轮人员职务条目数，这里职务由登陆人员职务和船舶员工职务合成 */
	private int count;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.register_personinfo);

		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		source = intent.getStringExtra("from");
		voyageNumber = intent.getStringExtra("voyageNumber");
		zjhm = intent.getStringExtra("zjhm");
		sailorFlag = intent.getBooleanExtra("sailorFlag", false);

		if (zjhm != null) {
			((EditText) findViewById(R.id.cardnum)).setText(zjhm);
		}
		httpReturnInfo = intent.getStringExtra("info");
		objectTypeStr = intent.getStringExtra("objectType");
		if (source.equals("02")) {
			setMyActiveTitle(getString(R.string.tikoumanager) + ">" + getString(R.string.paycard) + ">" + getString(R.string.sendPassInfo));
		} else if (source.equals("03") || source.equals("04")) {
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.normalxunjian) + ">" + getString(R.string.sendPassInfo));
			findViewById(R.id.ll_fx).setVisibility(View.GONE);
		} else if (source.equals("01")) {
			setMyActiveTitle(getString(R.string.kakoumanager) + ">" + getString(R.string.paycard) + ">" + getString(R.string.sendPassInfo));
			((RadioButton) findViewById(R.id.radio_btn_down)).setText(R.string.outkakou);
			((RadioButton) findViewById(R.id.radio_btn_up)).setText(R.string.inkakou);
		}
		((TextView) findViewById(R.id.valid_result)).setText(Html.fromHtml(getString(R.string.Valid_results) + "<font color=\"#acacac\">"
				+ httpReturnInfo + "</font>"));
		spinner = (Spinner) findViewById(R.id.card_type_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner = (Spinner) findViewById(R.id.office_spinner);
		List<String> list = new ArrayList<String>();
		List<String> dlryzw = DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_DLRYZW);
		count = dlryzw.size();
		if (count > 0) {
			list.addAll(0, dlryzw);
		}
		List<String> cbygzw = DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CBYGZW);
		if (cbygzw.size() > 0) {
			list.addAll(count, cbygzw);
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(0, true);
		Button btn = (Button) findViewById(R.id.btn_country);
		spinner = (Spinner) findViewById(R.id.country_list);
		btn.setBackgroundDrawable(spinner.getBackground());
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("type", "countrylist");
				intent.putExtra("selectitem", ((Button) v).getText().toString());
				intent.setClass(getApplicationContext(), SelectCountrylistActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_SELECT_NATIONALITY);
			}
		});
		String countryStr = intent.getStringExtra("gj");
		if (countryStr != null && countryStr.length() > 0) {
			btn.setText(DataDictionary.getCountryName(intent.getStringExtra("gj")));
		} else {
			btn.setText(DataDictionary.getCountryName("CHN"));
		}
		if (intent.getStringExtra("xm") != null) {
			((EditText) findViewById(R.id.name)).setText(intent.getStringExtra("xm"));
		}
		if (intent.getStringExtra("ssdw") != null) {
			((EditText) findViewById(R.id.unit)).setText(intent.getStringExtra("ssdw"));
		}
		if (intent.getStringExtra("xb") != null) {
			RadioGroup sex_rg = (RadioGroup) findViewById(R.id.sex_radio);
			if ("女".equals(DataDictionary.getDataDictionaryName(intent.getStringExtra("xb"), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE))) {
				sex_rg.check(R.id.radio_btn_f);
			} else {
				sex_rg.check(R.id.radio_btn_m);
			}
		}
		if (intent.getStringExtra("zjzl") != null) {
			Spinner cardtype_inner = (Spinner) findViewById(R.id.card_type_spinner);
			cardtype_inner.setSelection(DataDictionary.getDataDictionaryIndexByCode(intent.getStringExtra("zjzl"),
					DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		}
		if (intent.getStringExtra("zw") != null) {
			Spinner office_inner = (Spinner) findViewById(R.id.office_spinner);
			office_inner.setSelection(DataDictionary.getDataDictionaryOfficeIndex(intent.getStringExtra("zw")));
		}
		if (intent.getStringExtra("csrq") != null && !"".equals(intent.getStringExtra("csrq"))) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date birthday = df.parse(intent.getStringExtra("csrq"));
				DatePicker datapicker = (DatePicker) findViewById(R.id.datePicker_check);
				datapicker.init(birthday.getYear() + 1900, birthday.getMonth(), birthday.getDate(), null);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Button cancelbtn = (Button) findViewById(R.id.cancel);
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		Button submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(new OnClickListener() {
			/** 提交保存处理 */
			public void onClick(View v) {
				if (((EditText) findViewById(R.id.name)).getText().toString().length() == 0) {
					HgqwToast.makeText(RegisterPersoninfoActivity.this, R.string.name_empty, HgqwToast.LENGTH_LONG).show();
					return;
				}
				if (((EditText) findViewById(R.id.cardnum)).getText().toString().length() == 0) {
					HgqwToast.makeText(RegisterPersoninfoActivity.this, R.string.cardnumber_empty, HgqwToast.LENGTH_LONG).show();
					return;
				}

				String url = "sendPassInfo";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("jcr", BaseApplication.instent.gainUserID()));
				params.add(new BasicNameValuePair("xm", ((EditText) findViewById(R.id.name)).getText().toString()));
				if (source.equals("03") || source.equals("04")) {
					params.add(new BasicNameValuePair("fx", ""));
				} else {
					RadioGroup dire_rg = (RadioGroup) findViewById(R.id.direction_radio);
					if (dire_rg.getCheckedRadioButtonId() == R.id.radio_btn_up) {
						params.add(new BasicNameValuePair("fx", "0"));
					} else {
						params.add(new BasicNameValuePair("fx", "1"));
					}
				}
				params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
				params.add(new BasicNameValuePair("objectType", objectTypeStr));
				RadioGroup sexRadioGroup = (RadioGroup) findViewById(R.id.sex_radio);
				if (sexRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_f) {
					params.add(new BasicNameValuePair("xb", DataDictionary.getDataDictionaryCode("女", DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE)));
				} else {
					params.add(new BasicNameValuePair("xb", DataDictionary.getDataDictionaryCode("男", DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE)));
				}
				Button btn = (Button) findViewById(R.id.btn_country);
				params.add(new BasicNameValuePair("gj", DataDictionary.getCountryCode(btn.getText().toString())));
				params.add(new BasicNameValuePair("zjhm", ((EditText) findViewById(R.id.cardnum)).getText().toString()));
				Spinner cardtype_inner = (Spinner) findViewById(R.id.card_type_spinner);
				if (sailorFlag) {
					params.add(new BasicNameValuePair("zjzl", "17"));// 船员证件种类暂时固定，后续再改
				} else {
					params.add(new BasicNameValuePair("zjzl", DataDictionary.getDataDictionaryCodeByIndex(cardtype_inner.getSelectedItemPosition(),
							DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE)));
				}

				spinner = (Spinner) findViewById(R.id.office_spinner);
				int pos = spinner.getSelectedItemPosition();
				if (pos < count) {
					params.add(new BasicNameValuePair("zw", DataDictionary.getDataDictionaryCodeByIndex(pos,
							DataDictionary.DATADICTIONARY_TYPE_DLRYZW)));

				} else {
					params.add(new BasicNameValuePair("zw", DataDictionary.getDataDictionaryCodeByIndex(pos - count,
							DataDictionary.DATADICTIONARY_TYPE_CBYGZW)));
				}
				params.add(new BasicNameValuePair("ssdw", ((EditText) findViewById(R.id.unit)).getText().toString()));
				if (source.equals("01")) {
					params.add(new BasicNameValuePair("comeFrom", "3"));
					params.add(new BasicNameValuePair("kkID", voyageNumber));
					params.add(new BasicNameValuePair("voyageNumber", ""));
					params.add(new BasicNameValuePair("type", ""));
					params.add(new BasicNameValuePair("ddID", ""));
				} else if (source.equals("02")) {
					params.add(new BasicNameValuePair("comeFrom", "1"));
					params.add(new BasicNameValuePair("kkID", ""));
					params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
					params.add(new BasicNameValuePair("type", ""));
					params.add(new BasicNameValuePair("ddID", ""));
				} else {
					params.add(new BasicNameValuePair("comeFrom", "2"));
					if (voyageNumber == null || voyageNumber.length() == 0) {
						params.add(new BasicNameValuePair("voyageNumber", ""));
						params.add(new BasicNameValuePair("type", SystemSetting.xunJianType));
						params.add(new BasicNameValuePair("ddID", SystemSetting.xunJianId));
					} else {
						params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
						params.add(new BasicNameValuePair("hc", voyageNumber));
						params.add(new BasicNameValuePair("type", "01"));
						params.add(new BasicNameValuePair("ddID", voyageNumber));
					}
				}
				DatePicker checkdate = (DatePicker) findViewById(R.id.datePicker_check);
				params.add(new BasicNameValuePair("csrq", checkdate.getYear() + "-" + (checkdate.getMonth() + 1) + "-" + checkdate.getDayOfMonth()));
				params.add(new BasicNameValuePair("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))));
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				//附加经纬度信息
				params.add(new BasicNameValuePair("longitude", BaseApplication.instent.getLongitude()));//经度
				params.add(new BasicNameValuePair("latitude", BaseApplication.instent.getLatitude()));//纬度
				if (progressDialog != null) {
					return;
				}
				progressDialog = new ProgressDialog(RegisterPersoninfoActivity.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();

				if (!getState(FunctionSetting.bdtxyz, true)) {
					NetWorkManager.request(RegisterPersoninfoActivity.this, url, params, 0);
				} else {
					if (source.equals("03")) {
						// 巡查巡检
						OffLineManager.request(RegisterPersoninfoActivity.this, new XunJianAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					} else if (source.equals("02")) {
						// 梯口管理
						OffLineManager.request(RegisterPersoninfoActivity.this, new TkglAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					} else if (source.equals("01")) {
						// 卡口管理
						OffLineManager.request(RegisterPersoninfoActivity.this, new KakouAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					}
				}
			}
		});

		/*
		 * onInitSoundPool(); onInitVibrator(); onPlaySound(3, 0);
		 */

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		// onUnInitSoundPool();
		super.onDestroy();
	}

	/** 国家选择完成后，在button上显示选择结果 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_SELECT_NATIONALITY:
			if (resultCode == RESULT_OK) {
				int pos = data.getIntExtra("selectitem", 0);
				Button btn = (Button) findViewById(R.id.btn_country);
				btn.setText(DataDictionary.getCountryName(pos));
			}
			break;
		}
	}

	/** 解析保存结果 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			httpReturnXMLInfo = null;
			String text = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("success".equals(text)) {
							success = true;
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("txjlid".equals(parser.getName())) {
						txjlidStr = parser.nextText();
					} else if ("xcxsid".equals(parser.getName())) {
						xcxsidStr = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
					}
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/** 保存成功后，需要把输入的信息返回到刷卡界面，并显示 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		boolean success = false;
		if (str != null) {
			success = onParseXMLData(str);
			if (success) {
				HgqwToast.makeText(RegisterPersoninfoActivity.this, R.string.save_success, HgqwToast.LENGTH_LONG).show();

				Intent data = new Intent();
				data.putExtra("xm", ((EditText) findViewById(R.id.name)).getText().toString());
				if (source.equals("03") || source.equals("04")) {
					data.putExtra("fx", "");
				} else {
					RadioGroup dire_rg = (RadioGroup) findViewById(R.id.direction_radio);
					if (dire_rg.getCheckedRadioButtonId() == R.id.radio_btn_up) {
						data.putExtra("fx", "0");
					} else {
						data.putExtra("fx", "1");
					}
				}
				RadioGroup sex_rg = (RadioGroup) findViewById(R.id.sex_radio);
				if (sex_rg.getCheckedRadioButtonId() == R.id.radio_btn_f) {
					data.putExtra("xb", DataDictionary.getDataDictionaryCode("女", DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
				} else {
					data.putExtra("xb", DataDictionary.getDataDictionaryCode("男", DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE));
				}
				Button btn = (Button) findViewById(R.id.btn_country);
				data.putExtra("gj", DataDictionary.getCountryCode(btn.getText().toString()));
				data.putExtra("yzjg", httpReturnInfo);
				// data.putExtra("zjhm", ((EditText)
				// findViewById(R.id.cardnum)).getText().toString());
				data.putExtra("zjhm", zjhm);
				Spinner cardtype_inner = (Spinner) findViewById(R.id.card_type_spinner);
				data.putExtra("zjzl", DataDictionary.getDataDictionaryCodeByIndex(cardtype_inner.getSelectedItemPosition(),
						DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
				spinner = (Spinner) findViewById(R.id.office_spinner);
				data.putExtra("zw", spinner.getSelectedItem().toString());
				data.putExtra("ssdw", ((EditText) findViewById(R.id.unit)).getText().toString());
				DatePicker checkdate = (DatePicker) findViewById(R.id.datePicker_check);
				data.putExtra("csrq", checkdate.getYear() + "-" + (checkdate.getMonth() + 1) + "-" + checkdate.getDayOfMonth());
				data.putExtra("xcxsid", xcxsidStr);
				data.putExtra("txjlid", txjlidStr);
				setResult(RESULT_OK, data);
				finish();
			} else {
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(RegisterPersoninfoActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(RegisterPersoninfoActivity.this, R.string.save_failure, HgqwToast.LENGTH_LONG).show();
				}
			}
		} else {
			HgqwToast.makeText(RegisterPersoninfoActivity.this, R.string.save_failure, HgqwToast.LENGTH_LONG).show();
		}
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
			HgqwToast.getToastView(getApplicationContext(), getString(R.string.filed_validate_card)).show();
		}
	}

}
