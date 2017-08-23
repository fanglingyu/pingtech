package com.pingtech.hgqw.module.wpjc.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.SelectCountrylistActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.wpjc.action.GoodsCheckAction;
import com.pingtech.hgqw.module.wpjc.entity.ReadCardPersonInfo;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 输入证件号码后，如果该号码未登记，将进入该模块，手动保存人员信息
 * */

public class GoodsPersoninfo extends MyActivity implements OnHttpResult,
		OffLineResult {
	private static final String TAG = "GoodsPersoninfoActivity";

	/** 进入选择国家信息 */
	private static final int STARTACTIVITY_FOR_SELECT_NATIONALITY = 1;

	private ArrayAdapter<String> adapter;

	private Spinner spinner;

	/** 绑定的船舶航次号 */
	private String voyageNumber;

	/** 证件号码 */
	private String zjhm;

	private ProgressDialog progressDialog = null;

	/** 上下船时间 */
	private String time;

	ReadCardPersonInfo personInfo = null;

	/** 用来记录登轮人员职务条目数，这里职务由登陆人员职务和船舶员工职务合成 */
	private int count;

	/** 口岸船舶情况ID */
	private String kacbqkid = "";

	/**
	 * 物品类型状态(是否被选中)
	 */
	private boolean[] states;

	/**
	 * 物品类型数据
	 */
	private CharSequence[] charSequences;

	TextView goodsRes;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.goodscheck_personinfo);

		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		personInfo = (ReadCardPersonInfo) intent
				.getSerializableExtra("personInfo");
		voyageNumber = intent.getStringExtra("voyageNumber");
		kacbqkid = intent.getStringExtra("kacbqkid");
		time = intent.getStringExtra("time");
		if (personInfo != null) {
			if (personInfo.getCardnumber() != null
					&& !"".equals(personInfo.getCardnumber())) {
				((EditText) findViewById(R.id.cardnum)).setText(personInfo
						.getCardnumber());
			}
			if (personInfo.getName() != null
					&& !"".equals(personInfo.getName())) {
				((EditText) findViewById(R.id.name)).setText(personInfo
						.getName());
			}
			if (personInfo.getSex() != null && !"".equals(personInfo.getSex())) {
				RadioGroup sexrg = (RadioGroup) findViewById(R.id.sex_radio);
				if (DataDictionary.getDataDictionaryName(personInfo.getSex(),
						DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE)
						.equals("女")) {
					sexrg.check(R.id.radio_btn_f);
				} else {
					sexrg.check(R.id.radio_btn_m);
				}
			}
			if (personInfo.getBirthday() != null
					&& !"".equals(personInfo.getBirthday())) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date birthday = df.parse(personInfo.getBirthday());
					DatePicker datapicker = (DatePicker) findViewById(R.id.datePicker_check);
					datapicker.init(birthday.getYear() + 1900,
							birthday.getMonth(), birthday.getDate(), null);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else {
			personInfo = new ReadCardPersonInfo();
		}
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
				intent.setClass(getApplicationContext(),
						SelectCountrylistActivity.class);
				startActivityForResult(intent,
						STARTACTIVITY_FOR_SELECT_NATIONALITY);
			}
		});

		if (personInfo.getCountry() != null
				&& !"".equals(personInfo.getCountry())) {
			btn.setText(DataDictionary.getCountryName(personInfo.getCountry()));
		} else {
			btn.setText(DataDictionary.getCountryName("CHN"));
		}
		spinner = (Spinner) findViewById(R.id.card_type_spinner);
		adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_spinner_item,
				DataDictionary
						.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		if (personInfo.getCardtype() != null
				&& !"".equals(personInfo.getCardtype())) {
			spinner.setSelection(DataDictionary.getDataDictionaryIndexByCode(
					personInfo.getCardtype(),
					DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE), true);
		}
		spinner = (Spinner) findViewById(R.id.office_spinner);
		List<String> list = new ArrayList<String>();
		List<String> dlryzw = DataDictionary
				.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_DLRYZW);
		count = dlryzw.size();
		if (count > 0) {
			list.addAll(0, dlryzw);
		}
		List<String> cbygzw = DataDictionary
				.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CBYGZW);
		if (cbygzw.size() > 0) {
			list.addAll(count, cbygzw);
		}
		if (personInfo.getUnit() != null && !"".equals(personInfo.getUnit())) {
			((EditText) findViewById(R.id.unit)).setText(personInfo.getUnit());
		}
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(0, true);
		btn = (Button) findViewById(R.id.btn_country);
		spinner = (Spinner) findViewById(R.id.country_list);
		btn.setBackgroundDrawable(spinner.getBackground());
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("type", "countrylist");
				intent.putExtra("selectitem", ((Button) v).getText().toString());
				intent.setClass(getApplicationContext(),
						SelectCountrylistActivity.class);
				startActivityForResult(intent,
						STARTACTIVITY_FOR_SELECT_NATIONALITY);
			}
		});
		((TextView) findViewById(R.id.goods_record_time)).setText(Html
				.fromHtml(getString(R.string.goods_downup_time_tag)
						+ "<font color=\"#acacac\">" + time + "</font>"));

		// 记录物品
		charSequences = DataDictionary.getDataDictionaryNameList(
				DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE).toArray(
				new CharSequence[DataDictionary.getDataDictionaryNameList(
						DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE).size()]);

		states = new boolean[charSequences.length];
		goodsRes = (TextView) findViewById(R.id.goods);
		goodsRes.setText("");
		goodsRes.setVisibility(View.GONE);

		// Spinner temp_spinner = (Spinner)
		// findViewById(R.id.goods_check_type_spinner);
		// ArrayAdapter<String> temp_adapter = new ArrayAdapter<String>(
		// this,
		// android.R.layout.simple_spinner_item,
		// DataDictionary
		// .getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE));
		// temp_adapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// temp_spinner.setAdapter(temp_adapter);
		//
		//
		//

		Button submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(new OnClickListener() {
			/** 提交保存处理 */
			public void onClick(View v) {
				if (((EditText) findViewById(R.id.name)).getText().toString()
						.length() == 0) {
					offLineToast(getString(R.string.name_empty));
					return;
				}
				if (((EditText) findViewById(R.id.cardnum)).getText()
						.toString().length() == 0) {
					offLineToast(getString(R.string.cardnumber_empty));
					return;
				}
				StringBuffer buffer = new StringBuffer();
				if (states != null && states.length > 0) {

					for (int i = 0; i < states.length; i++) {
						if (states[i]) {
							if (buffer != null && buffer.length() > 0) {
								buffer.append("|");
							}
							buffer.append(DataDictionary
									.getDataDictionaryCodeByIndex(
											i,
											DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE));
						}
					}
				}
				if (buffer == null || buffer.length() == 0) {
					HgqwToast.makeText(getApplicationContext(),
							R.string.plase_choose_good).show();
					return;
				}
				EditText zjhmText = (EditText) findViewById(R.id.cardnum);
				zjhm = zjhmText.getText().toString();
				if (progressDialog != null) {
					return;
				}
				progressDialog = new ProgressDialog(GoodsPersoninfo.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				String url = "sendGoodsInfo";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("xm",
						((EditText) findViewById(R.id.name)).getText()
								.toString()));
				params.add(new BasicNameValuePair("pdacode", DeviceUtils
						.getIMEI()));
				params.add(new BasicNameValuePair("PDACode", DeviceUtils
						.getIMEI()));
				RadioGroup sexRadioGroup = (RadioGroup) findViewById(R.id.sex_radio);
				if (sexRadioGroup.getCheckedRadioButtonId() == R.id.radio_btn_f) {
					params.add(new BasicNameValuePair(
							"xb",
							DataDictionary
									.getDataDictionaryCode(
											"女",
											DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE)));
				} else {
					params.add(new BasicNameValuePair(
							"xb",
							DataDictionary
									.getDataDictionaryCode(
											"男",
											DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE)));
				}
				Button btn = (Button) findViewById(R.id.btn_country);
				params.add(new BasicNameValuePair("gj", DataDictionary
						.getCountryCode(btn.getText().toString())));
				DatePicker checkdate = (DatePicker) findViewById(R.id.datePicker_check);
				params.add(new BasicNameValuePair("csrq", checkdate.getYear()
						+ "-" + (checkdate.getMonth() + 1) + "-"
						+ checkdate.getDayOfMonth()));
				params.add(new BasicNameValuePair("zjhm",
						((EditText) findViewById(R.id.cardnum)).getText()
								.toString()));
				Spinner cardtype_inner = (Spinner) findViewById(R.id.card_type_spinner);
				params.add(new BasicNameValuePair(
						"zjlx",
						DataDictionary.getDataDictionaryCodeByIndex(
								cardtype_inner.getSelectedItemPosition(),
								DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE)));
				spinner = (Spinner) findViewById(R.id.office_spinner);
				int pos = spinner.getSelectedItemPosition();
				if (pos < count) {
					params.add(new BasicNameValuePair("zw", DataDictionary
							.getDataDictionaryCodeByIndex(pos,
									DataDictionary.DATADICTIONARY_TYPE_DLRYZW)));

				} else {
					params.add(new BasicNameValuePair("zw", DataDictionary
							.getDataDictionaryCodeByIndex(pos - count,
									DataDictionary.DATADICTIONARY_TYPE_CBYGZW)));
				}
				params.add(new BasicNameValuePair("ssdw",
						((EditText) findViewById(R.id.unit)).getText()
								.toString()));
				params.add(new BasicNameValuePair("userID", LoginUser
						.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("hc", voyageNumber);

				params.add(new BasicNameValuePair("kacbqkid", kacbqkid));
				params.add(new BasicNameValuePair("time", time));
				params.add(new BasicNameValuePair("type", buffer.toString()));
				RadioGroup downup_rg = (RadioGroup) findViewById(R.id.goods_downup_radio);
				if (downup_rg.getCheckedRadioButtonId() == R.id.radio_btn_up) {
					params.add(new BasicNameValuePair("fx", "0"));
				} else {
					params.add(new BasicNameValuePair("fx", "1"));
				}
				params.add(new BasicNameValuePair("ryid", zjhm));
				if (!BaseApplication.instent.getWebState()) {
					// params.add(new BasicNameValuePair("kacbqkid", kacbqkid));
					OffLineManager.request(GoodsPersoninfo.this,
							new GoodsCheckAction(), url,
							NVPairTOMap.nameValuePairTOMap(params), 0);
				} else {
					NetWorkManager
							.request(GoodsPersoninfo.this, url, params, 0);
				}
			}
		});
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

	/** 保存成功后，需要把输入的信息返回到刷卡界面，并显示 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null && ("1".equals(str))) {
			offLineToast(getString(R.string.save_success));
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), GoodsCheckList.class);
			startActivity(intent);
			finish();
		} else if (str != null && ("2".equals(str))) {
			HgqwToast.makeText(getApplicationContext(),
					BaseApplication.instent.getString(R.string.ship_has_lg),
					HgqwToast.LENGTH_LONG).show();
		} else {
			offLineToast(getString(R.string.save_failure));
		}
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.good_back:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 离线版Toast
	 */
	private void offLineToast(String show) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		HgqwToast
				.makeText(getApplicationContext(), show, HgqwToast.LENGTH_LONG)
				.show();

	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		// TODO Auto-generated method stub
		if (res.first) {
			offLineToast(getString(R.string.wpjc_save_success));
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), GoodsCheckList.class);
			startActivity(intent);
			finish();
		} else {
			offLineToast((String) res.second);
		}
	}

	public void onClickMethod(View v) {
		switch (v.getId()) {
		case R.id.add_good:
			showAlertDialog();
			break;
		default:
			break;
		}
	}

	private void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GoodsPersoninfo.this);
		builder.setTitle(R.string.Goods_check);

		builder.setMultiChoiceItems(charSequences, states,
				new OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1,
							boolean arg2) {
						// TODO Auto-generated method stub
						states[arg1] = arg2;
					}
				});
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.ok,
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

						StringBuffer text = new StringBuffer();
						int num = states.length;
						if (num > 0) {
							for (int i = 0; i < num; i++) {
								if (states[i]) {
									if (text.length() > 0) {
										text.append(";");
									}
									text.append(charSequences[i]);
								}
							}
						}
						goodsRes.setVisibility(View.VISIBLE);
						goodsRes.setText(text.toString());

					}
				});
		builder.create().show();
	}
}
