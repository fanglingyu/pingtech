package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android_serialport_api.ParseSFZAPI.People;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.GetPersonInfo;
import com.pingtech.hgqw.entity.MessageEntity;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.cyxx.action.CyxxAction;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.readcard.service.ReadService;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 查询人员或手动选择人员界面activity类 */
public class SelectPersonActivity extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "SelectPersonActivity";

	private List<NameValuePair> paramsHis = null;

	/** 进入显示查询结果界面 */
	private static final int STARTACTIVITY_FOR_PERSON_RESULT = 1;

	/** 保存查询结果 */
	public static ArrayList<Map<String, String>> personInfoList = null;

	/**
	 * 巡查巡检-查询人员：公安库数据
	 */
	private GetPersonInfo getPersonInfo = null;

	private Spinner spinner;

	private ArrayAdapter<String> spinnerAdapter;

	private String httpReturnXMLInfo = null;

	private ProgressDialog progressDialog = null;

	/** 是否来自巡查巡检 */
	boolean fromXunCha = false;

	/** 是否来自异常信息 */
	boolean fromException = false;

	/** 是否来自异常信息 */
	boolean fromGoodsCheck = false;

	/** 标记来自哪个模块：梯口管理？卡口管理？巡查巡检？ */
	private String from = null;

	/** 用来记录登轮人员职务条目数，这里职务由登陆人员职务和船舶员工职务合成 */
	private int count;

	/** 绑定船舶航次号 */
	private String voyageNumber;

	/** 口岸船舶情况ID */
	private String kacbqkid = "";

	/** 口岸船舶名称 */
	private String voyagemc = "";

	private boolean flagRegister = true;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		personInfoList = null;
		flagRegister = false;
		if (readService != null) {
			readService.close();
			readService = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		flagRegister = false;
		if (readService != null) {
			readService.close();
			readService = null;
		}
		super.onPause();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.selectperson);

		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		fromXunCha = intent.getBooleanExtra("fromxuncha", false);
		fromException = intent.getBooleanExtra("fromexception", false);
		fromGoodsCheck = intent.getBooleanExtra("fromGoodsCheck", false);
		from = intent.getStringExtra("from");
		voyageNumber = intent.getStringExtra("hc");
		kacbqkid = intent.getStringExtra("kacbqkid");
		voyagemc = intent.getStringExtra("voyagemc");
		boolean tkgl_sailor_list = intent.getBooleanExtra("tkgl_sailor_list", false);
		if (fromXunCha) {
			findViewById(R.id.sel_person_cardtype).setVisibility(View.VISIBLE);
			findViewById(R.id.sel_person_sex).setVisibility(View.GONE);
			findViewById(R.id.sel_person_office).setVisibility(View.GONE);
			setMyActiveTitle(getText(R.string.xunchaxunjian) + ">" + getText(R.string.select_person));
			findViewById(R.id.sel_person_range_rg).setVisibility(View.GONE);
		} else if (fromException) {
			setMyActiveTitle(getText(R.string.exception_info) + ">" + getText(R.string.select_person));
			findViewById(R.id.sel_person_range_rg).setVisibility(View.GONE);
		} else if (fromGoodsCheck) {
			findViewById(R.id.sel_person_range_rg).setVisibility(View.VISIBLE);
			findViewById(R.id.sel_person_cardtype).setVisibility(View.GONE);
			if (tkgl_sailor_list) {
				setMyActiveTitle(getText(R.string.goods_check) + ">" + getText(R.string.tkgl_sailor_list));
			} else {
				setMyActiveTitle(getText(R.string.goods_check) + ">" + getText(R.string.select_person));
			}
			findViewById(R.id.sel_tikou_person_range_rg).setVisibility(View.VISIBLE);
		} else if (tkgl_sailor_list) {
			findViewById(R.id.sel_person_range_rg).setVisibility(View.VISIBLE);
			findViewById(R.id.sel_person_cardtype).setVisibility(View.GONE);
			if (from.equals("01")) {
				setMyActiveTitle(getText(R.string.kakoumanager) + ">" + getText(R.string.select_person));
				findViewById(R.id.sel_tikou_person_range_rg).setVisibility(View.GONE);

			} else if (from.equals("02")) {
				setMyActiveTitle(getText(R.string.tikoumanager) + ">" + getText(R.string.tkgl_sailor_list));
				findViewById(R.id.sel_tikou_person_range_rg).setVisibility(View.VISIBLE);
			}
		} else {
			findViewById(R.id.sel_person_range_rg).setVisibility(View.VISIBLE);
			findViewById(R.id.sel_person_cardtype).setVisibility(View.GONE);
			if (from.equals("01")) {
				setMyActiveTitle(getText(R.string.kakoumanager) + ">" + getText(R.string.select_person));
				findViewById(R.id.sel_tikou_person_range_rg).setVisibility(View.GONE);

			} else if (from.equals("02")) {
				setMyActiveTitle(getText(R.string.tikoumanager) + ">" + getText(R.string.select_person));
				findViewById(R.id.sel_tikou_person_range_rg).setVisibility(View.VISIBLE);
			}
		}
		spinner = (Spinner) findViewById(R.id.sel_person_sex_spinner);
		List<String> list = new ArrayList<String>(Arrays.asList("请选择", "男", "女"));
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		spinner = (Spinner) findViewById(R.id.sel_person_office_spinner);
		list = new ArrayList<String>(Arrays.asList("请选择"));

		// 梯口选择人员，职务，只列出船舶员工职务。
		// List<String> dlryzw =
		// DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_DLRYZW);
		List<String> dlryzw = DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CBYGZW);
		count = dlryzw.size();
		if (count > 0) {
			list.addAll(1, dlryzw);
		}
		List<String> cbygzw = DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CBYGZW);
		if (cbygzw.size() > 0) {
			list.addAll(1 + count, cbygzw);
		}
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		spinner = (Spinner) findViewById(R.id.sel_person_cardtype_spinner);
		list = new ArrayList<String>(Arrays.asList("请选择"));
		list.addAll(1, DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		Button submitbtn = (Button) findViewById(R.id.select_submit);
		submitbtn.setOnClickListener(new OnClickListener() {
			/** 执行查询操作 */
			public void onClick(View v) {
				search(YfZjxxConstant.ZJCX_SFSK_SDSR, "", "");
			}

		});
	}

	private void search(String sfsk, String ickey, String mrickey) {
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(SelectPersonActivity.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		String url = "getPersonInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 增加ic卡读卡，用于巡查，刷卡查询人员
		params.add(new BasicNameValuePair("sfsk", sfsk));
		params.add(new BasicNameValuePair("ickey", ickey));
		params.add(new BasicNameValuePair("mrickey", mrickey));
		String temp_str = null;
		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
		params.add(new BasicNameValuePair("xm", ((EditText) findViewById(R.id.sel_person_name_edit)).getText().toString()));
		spinner = (Spinner) findViewById(R.id.sel_person_sex_spinner);
		if (spinner.getSelectedItemPosition() == 0) {
			params.add(new BasicNameValuePair("xb", null));
		} else {
			temp_str = DataDictionary.getDataDictionaryCode(spinner.getSelectedItem().toString(), DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE);
			params.add(new BasicNameValuePair("xb", temp_str));
		}
		spinner = (Spinner) findViewById(R.id.sel_person_office_spinner);
		int pos = spinner.getSelectedItemPosition();
		if ((from != null && from.equals("02")) || fromException || fromGoodsCheck) {
			if (pos == 0) {
				params.add(new BasicNameValuePair("zw", null));
			} else {
				temp_str = DataDictionary.getDataDictionaryCodeByIndex(pos - 1, DataDictionary.DATADICTIONARY_TYPE_CBYGZW);
				params.add(new BasicNameValuePair("zw", temp_str));
			}
		} else {
			if (pos == 0) {
				params.add(new BasicNameValuePair("zw", null));
			} else if (pos < 1 + count) {
				temp_str = DataDictionary.getDataDictionaryCodeByIndex(pos - 1, DataDictionary.DATADICTIONARY_TYPE_DLRYZW);
				params.add(new BasicNameValuePair("zw", temp_str));
			} else {
				temp_str = DataDictionary.getDataDictionaryCodeByIndex(pos - count - 1, DataDictionary.DATADICTIONARY_TYPE_CBYGZW);
				params.add(new BasicNameValuePair("zw", temp_str));
			}
		}
		spinner = (Spinner) findViewById(R.id.sel_person_cardtype_spinner);
		if (spinner.getSelectedItemPosition() == 0) {
			params.add(new BasicNameValuePair("zjzl", null));
		} else {
			temp_str = DataDictionary.getDataDictionaryCodeByIndex(spinner.getSelectedItemPosition() - 1,
					DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE);
			params.add(new BasicNameValuePair("zjzl", temp_str));
		}
		String zjhm = ((EditText) findViewById(R.id.cardnum)).getText().toString();
		params.add(new BasicNameValuePair("zjhm", zjhm));
		if (fromXunCha) {
			if (!YfZjxxConstant.ZJCX_SFSK_SK.equals(sfsk)) {
				String xm = ((EditText) findViewById(R.id.sel_person_name_edit)).getText().toString();
				if (!((StringUtils.isNotEmpty(xm)) || (StringUtils.isNotEmpty(zjhm)))) {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					HgqwToast.toast(R.string.not_null_xm_zjhm, HgqwToast.LENGTH_LONG);
					return;
				}
			}
			params.add(new BasicNameValuePair("comeFrom", "2"));
		} else if (fromException) {
			params.add(new BasicNameValuePair("comeFrom", "4"));
		} else if (fromGoodsCheck) {
			params.add(new BasicNameValuePair("kacbqkid", kacbqkid));
			params.add(new BasicNameValuePair("comeFrom", "1"));
		} else {
			params.add(new BasicNameValuePair("comeFrom", "1"));
		}
		if (fromXunCha || fromException) {
			params.add(new BasicNameValuePair("cywz", ""));
		} else {
			RadioGroup rg = (RadioGroup) findViewById(R.id.sel_tikou_person_range_rg);
			if (rg.getCheckedRadioButtonId() == R.id.radio_btn_tikou_down) {
				params.add(new BasicNameValuePair("cywz", "1"));
			} else {
				params.add(new BasicNameValuePair("cywz", "0"));
			}
		}
		paramsHis = params;
		// 先查本地，本地没有查询在线
		if (getState(FunctionSetting.bdtxyz, true) && fromXunCha) {
			OffLineManager.request(SelectPersonActivity.this, new CyxxAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
		} else {
			NetWorkManager.request(SelectPersonActivity.this, url, params, 0);
		}
	}

	private void initRfid() {
		if (fromXunCha) {
			readInit();
		}
	}

	/** 处理从结果列表界面返回的操作，并带回不同参数 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_PERSON_RESULT:
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (resultCode == RESULT_OK) {
				if (data.getIntExtra("type", 0) == SelectPersonResultActivity.FROM_XUNJIAN) {
				} else if (data.getIntExtra("type", 0) == SelectPersonResultActivity.FROM_EXCEPTION) {
					Intent intent = null;
					intent = new Intent();
					intent.putExtra("id", data.getStringExtra("id"));
					intent.putExtra("xm", data.getStringExtra("xm"));
					intent.putExtra("xb", data.getStringExtra("xb"));
					intent.putExtra("gj", data.getStringExtra("gj"));
					intent.putExtra("zw", data.getStringExtra("zw"));
					intent.putExtra("zjzl", data.getStringExtra("zjzl"));
					intent.putExtra("zjhm", data.getStringExtra("zjhm"));
					intent.putExtra("ssdw", data.getStringExtra("ssdw"));
					intent.putExtra("csrq", data.getStringExtra("csrq"));
					intent.putExtra("hgzl", data.getStringExtra("hgzl"));
					intent.putExtra("lcbz", data.getStringExtra("lcbz"));
					intent.putExtra("kacbqkid", data.getStringExtra("kacbqkid"));
					setResult(RESULT_OK, intent);
				} else if (data.getIntExtra("type", 0) == SelectPersonResultActivity.FROM_OTHER) {
					Intent intent = null;
					intent = new Intent();
					intent.putExtra("id", data.getStringExtra("id"));
					intent.putExtra("cardnum", data.getStringExtra("cardnum"));
					intent.putExtra("xm", data.getStringExtra("xm"));
					intent.putExtra("xb", data.getStringExtra("xb"));
					intent.putExtra("gj", data.getStringExtra("gj"));
					intent.putExtra("zw", data.getStringExtra("zw"));
					intent.putExtra("zjzl", data.getStringExtra("zjzl"));
					intent.putExtra("zjhm", data.getStringExtra("zjhm"));
					intent.putExtra("ssdw", data.getStringExtra("ssdw"));
					intent.putExtra("csrq", data.getStringExtra("csrq"));
					intent.putExtra("lcbz", data.getStringExtra("lcbz"));
					intent.putExtra("hgzl", data.getStringExtra("hgzl"));
					intent.putExtra("kacbqkid", data.getStringExtra("kacbqkid"));
					setResult(RESULT_OK, intent);
				}
				finish();
			}
			break;
		}
	}

	/** 解析查询结果 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		Map<String, String> map = null;
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
							if (personInfoList == null) {
								personInfoList = new ArrayList<Map<String, String>>();
							} else {
								personInfoList.clear();
							}
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							map = new HashMap<String, String>();
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("gaInfo".equals(parser.getName())) {
						if (success) {
							getPersonInfo = new GetPersonInfo();
						}
					} else if ("id".equals(parser.getName())) {
						map.put("hyid", parser.nextText());
					} else if ("xm".equals(parser.getName())) {
						map.put("xm", parser.nextText());
					} else if ("xb".equals(parser.getName())) {
						map.put("xb", parser.nextText());
					} else if ("hgzl".equals(parser.getName())) {
						map.put("hgzl", parser.nextText());
					} else if ("gj".equals(parser.getName())) {
						map.put("gj", parser.nextText());
					} else if ("lcbz".equals(parser.getName())) {
						map.put("lcbz", parser.nextText());
					} else if ("zw".equals(parser.getName())) {
						map.put("zw", parser.nextText());
					} else if ("zjzl".equals(parser.getName())) {
						map.put("zjlx", parser.nextText());
					} else if ("zjhm".equals(parser.getName())) {
						map.put("zjhm", parser.nextText());
					} else if ("photo".equals(parser.getName())) {
						map.put("photo", parser.nextText());
					} else if ("csrq".equals(parser.getName())) {
						map.put("csrq", parser.nextText());
					} else if ("ssdw".equals(parser.getName())) {
						map.put("ssdw", parser.nextText());
					} else if ("pzxx".equals(parser.getName())) {
						map.put("pzxx", parser.nextText());
					} else if ("cbkazt".equals(parser.getName())) {
						map.put("cbkazt", parser.nextText());
					} else if ("yxq".equals(parser.getName())) {
						map.put("yxq", parser.nextText());
					}
					// ////
					else if ("gaResult".equals(parser.getName())) {
						getPersonInfo.setGaResult(parser.nextText());
					} else if ("sfzh_ga".equals(parser.getName())) {
						getPersonInfo.setSfzh(parser.nextText());
					} else if ("xm_ga".equals(parser.getName())) {
						getPersonInfo.setXm(parser.nextText());
					} else if ("xb_ga".equals(parser.getName())) {
						getPersonInfo.setXb(parser.nextText());
					} else if ("csrq_ga".equals(parser.getName())) {
						getPersonInfo.setCsrq(parser.nextText());
						String csrq = DateUtils.gainBirthday(getPersonInfo.getCsrq());
						getPersonInfo.setCsrq(csrq);
					} else if ("gj_ga".equals(parser.getName())) {
						getPersonInfo.setGj(parser.nextText());
					} else if ("mz_ga".equals(parser.getName())) {
						getPersonInfo.setMz(parser.nextText());
					} else if ("zy_ga".equals(parser.getName())) {
						getPersonInfo.setZy(parser.nextText());
					} else if ("zjzl_ga".equals(parser.getName())) {
						getPersonInfo.setZjzl(parser.nextText());
					} else if ("pzxx_ga".equals(parser.getName())) {
						getPersonInfo.setPzxx(parser.nextText());
					} else if ("zp_ga".equals(parser.getName())) {
						getPersonInfo.setZp(parser.nextText());
						getPersonInfo.setHasPhoto(true);

					} else if ("ztry".equals(parser.getName())) {
						getPersonInfo.setZtry(true);
					} else if ("xmsf".equals(parser.getName())) {
						getPersonInfo.setXmsf(true);
					} else if ("ljjj".equals(parser.getName())) {
						getPersonInfo.setLjjj(true);
					} else if ("sdry".equals(parser.getName())) {
						getPersonInfo.setSdry(true);
					} else if ("wffz".equals(parser.getName())) {
						getPersonInfo.setWffz(true);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (personInfoList == null) {
								personInfoList = new ArrayList<Map<String, String>>();
							}
							personInfoList.add(map);
						}
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
	protected void onResume() {
		initRfid();
		super.onResume();
	}

	/** 处理查询结果，当结果不为空时，进入显示查询列表界面 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (str != null) {
			boolean result = false;
			result = onParseXMLData(str);
			if (result && personInfoList != null && personInfoList.size() > 0) {
				Intent intent = new Intent();
				intent.putExtra("fromxuncha", fromXunCha);
				intent.putExtra("fromexception", fromException);
				intent.putExtra("fromGoodsCheck", fromGoodsCheck);
				intent.putExtra("from", from);
				intent.putExtra("hc", voyageNumber);
				intent.putExtra("gaInfo", getPersonInfo);
				intent.putExtra("kacbqkid", kacbqkid);
				intent.putExtra("voyagemc", voyagemc);
				intent.setClass(getApplicationContext(), SelectPersonResultActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_PERSON_RESULT);
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(SelectPersonActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(SelectPersonActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}
			}
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			HgqwToast.makeText(SelectPersonActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> obj, int httpRequestType) {
		if (obj != null) {
			ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) obj.second;
			if (list != null && list.size() > 0) {
				personInfoList = list;
				Intent intent = new Intent();
				getPersonInfo = new GetPersonInfo();
				getPersonInfo.setGaResult(getString(R.string.no_data_fromplace));
				intent.putExtra("fromxuncha", fromXunCha);
				intent.putExtra("fromexception", fromException);
				intent.putExtra("fromGoodsCheck", fromGoodsCheck);
				intent.putExtra("from", from);
				intent.putExtra("hc", voyageNumber);
				intent.putExtra("gaInfo", getPersonInfo);
				intent.putExtra("kacbqkid", kacbqkid);
				intent.putExtra("voyagemc", voyagemc);
				intent.setClass(getApplicationContext(), SelectPersonResultActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_PERSON_RESULT);
			} else {
				if (getState(FunctionSetting.bdtxyz, true) && fromXunCha) {// 巡检查询人员
					onlineRequese();
					return;
				}
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(SelectPersonActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();

			}
		}

	}

	private void onlineRequese() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(SelectPersonActivity.this);
			progressDialog.setTitle(getString(R.string.waiting));
			progressDialog.setMessage(getString(R.string.no_data_request_web));
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();

		} else {
			progressDialog.setMessage(getString(R.string.no_data_request_web));
		}
		String url = "getPersonInfo";
		NetWorkManager.request(SelectPersonActivity.this, url, paramsHis, 0);
	}

	private CardInfo cardInfo = null;

	private ReadService readService = null;

	private ReadCardHandler readCardHander = null;

	private void readInit() {
		flagRegister = true;
		readCardHander = new ReadCardHandler();
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
				EditText editText1 = (EditText) findViewById(R.id.cardnum);
				if (editText1 != null) {
					editText1.setText("");
				}
				cardInfo = (CardInfo) msg.obj;
				String ickey = cardInfo.getIckey();
				String mrickey = cardInfo.getDefaultIckey();
				if (!flagRegister) {
					Log.i(TAG, "flagRegister=false ,return");
					return;
				}
				BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
				search(YfZjxxConstant.ZJCX_SFSK_SK, ickey, mrickey);
				break;
			case ReadService.READ_TYPE_ID:
				cardInfo = (CardInfo) msg.obj;
				People people = cardInfo.getPeople();

				if (!flagRegister) {
					Log.i(TAG, "flagRegister=false ,return");
					return;
				}

				EditText editText = (EditText) findViewById(R.id.cardnum);
				if (editText != null) {
					BaseApplication.soundManager.onPlaySoundNoVb(4, 0);// 播放声音
					editText.setText(people.getPeopleIDCode());
					search(YfZjxxConstant.ZJCX_SFSK_SDSR, "", "");
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

}
