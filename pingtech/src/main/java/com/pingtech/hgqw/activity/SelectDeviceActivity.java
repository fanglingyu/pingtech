package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Pair;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.sbxx.action.SbxxAction;
import com.pingtech.hgqw.module.offline.scsb.action.ScsbAction;
import com.pingtech.hgqw.module.offline.sxtgl.action.SxtglAction;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 查询设备界面的activity类
 * */
public class SelectDeviceActivity extends MyActivity implements OnHttpResult,OffLineResult {
	private static final String TAG = "SelectDeviceActivity";
	/** 查询智能设备 */
	public static int SELECT_DEVICE_TYPE_SMART_DEVICE = 0;
	/** 查询手持终端 */
	public static int SELECT_DEVICE_TYPE_HAND_DEVICE = 1;
	/** 查询摄像头 */
	public static int SELECT_DEVICE_TYPE_CAMERA = 2;
	/** 跳转到显示查询结果界面 */
	private static final int STARTACTIVITY_FOR_DEVICE_RESULT = 3;
	private ArrayAdapter<String> spinnerAdapter;
	private Spinner spinner;
	private int selType = SELECT_DEVICE_TYPE_SMART_DEVICE;
	/** 保存查询结果信息 */
	public static ArrayList<HashMap<String, Object>> deviceInfoList = null;
	private String httpReturnXMLInfo = null;
	private ProgressDialog progressDialog = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.selectdevice);

		Log.i(TAG, "onCreate()");

		if (deviceInfoList == null) {
			deviceInfoList = new ArrayList<HashMap<String, Object>>();
		} else {
			deviceInfoList.clear();
		}
		setMyActiveTitle(getString(R.string.exception_info) + ">" + getString(R.string.selectdevice));

		Button submitbtn = (Button) findViewById(R.id.select_submit);
		submitbtn.setOnClickListener(new OnClickListener() {
			/** 执行查询 */
			public void onClick(View v) {
				if (progressDialog != null) {
					return;
				}
				progressDialog = new ProgressDialog(SelectDeviceActivity.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				if (selType == SELECT_DEVICE_TYPE_SMART_DEVICE) {
					String url = "getDeviceInfo";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("sbbh",
							((EditText) findViewById(R.id.selectdevice_smart_device_bh)).getText().toString()));
					params.add(new BasicNameValuePair("name",
							((EditText) findViewById(R.id.selectdevice_smart_device_mc)).getText().toString()));
					RadioGroup rg = (RadioGroup) findViewById(R.id.select_smart_device_type);
					if (rg.getCheckedRadioButtonId() == R.id.radio_btn_zq) {
						params.add(new BasicNameValuePair("type", "1"));
					} else {
						params.add(new BasicNameValuePair("type", "2"));
					}
					if(BaseApplication.instent.getWebState()){
						NetWorkManager.request(SelectDeviceActivity.this, url, params, 0);
					}else{
						OffLineManager.request(SelectDeviceActivity.this, new SbxxAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					}
				} else if (selType == SELECT_DEVICE_TYPE_HAND_DEVICE) {
					String url = "getPdaInfo";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("sbbh",
							((EditText) findViewById(R.id.selectdevice_hand_device_bh)).getText().toString()));
					params.add(new BasicNameValuePair("sbxh",
							((EditText) findViewById(R.id.selectdevice_hand_device_xh)).getText().toString()));
					params.add(new BasicNameValuePair("ip", ((EditText) findViewById(R.id.selectdevice_hand_device_ip))
							.getText().toString()));
					RadioGroup rg = (RadioGroup) findViewById(R.id.select_device_use_type);
					if (rg.getCheckedRadioButtonId() == R.id.radio_btn_in) {
						params.add(new BasicNameValuePair("type", "0"));
					} else {
						params.add(new BasicNameValuePair("type", "1"));
					}
					rg = (RadioGroup) findViewById(R.id.select_device_status);
					if (rg.getCheckedRadioButtonId() == R.id.radio_btn_normal) {
						params.add(new BasicNameValuePair("zt", "1"));
					} else if (rg.getCheckedRadioButtonId() == R.id.radio_btn_sunhuai) {
						params.add(new BasicNameValuePair("zt", "2"));
					} else if (rg.getCheckedRadioButtonId() == R.id.radio_btn_diushi) {
						params.add(new BasicNameValuePair("zt", "3"));
					} else if (rg.getCheckedRadioButtonId() == R.id.radio_btn_weixiu) {
						params.add(new BasicNameValuePair("zt", "4"));
					}
					if(BaseApplication.instent.getWebState()){
						NetWorkManager.request(SelectDeviceActivity.this, url, params, 0);
					}else{
						OffLineManager.request(SelectDeviceActivity.this, new ScsbAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					}
					
				} else {
					String url = "getVidiconInfo";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("sbbh", ((EditText) findViewById(R.id.selectdevice_camera_bh))
							.getText().toString()));
					params.add(new BasicNameValuePair("name", ((EditText) findViewById(R.id.selectdevice_camera_mc))
							.getText().toString()));
					if (spinner.getSelectedItemPosition() == 0) {
						params.add(new BasicNameValuePair("type", ""));
					} else {
						params.add(new BasicNameValuePair("type", DataDictionary.getDataDictionaryCodeByIndex(
								spinner.getSelectedItemPosition() - 1, DataDictionary.DATADICTIONARY_TYPE_CAMERA_TYPE)));
					}
					if(BaseApplication.instent.getWebState()){
						NetWorkManager.request(SelectDeviceActivity.this, url, params, 0);
					}else{
						OffLineManager.request(SelectDeviceActivity.this, new SxtglAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
					}
				}

			}
		});
		RadioGroup selTypeRadioGroup = (RadioGroup) findViewById(R.id.select_device_type);
		selTypeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_btn_smart) {
					if (findViewById(R.id.select_smart_device) != null) {
						findViewById(R.id.select_smart_device).setVisibility(View.VISIBLE);
					}
					if (findViewById(R.id.select_device_hand) != null) {
						findViewById(R.id.select_device_hand).setVisibility(View.GONE);
					}
					if (findViewById(R.id.select_device_camera) != null) {
						findViewById(R.id.select_device_camera).setVisibility(View.GONE);
					}
					selType = SELECT_DEVICE_TYPE_SMART_DEVICE;
				} else if (checkedId == R.id.radio_btn_hand) {
					if (findViewById(R.id.select_smart_device) != null) {
						findViewById(R.id.select_smart_device).setVisibility(View.GONE);
					}
					if (findViewById(R.id.select_device_hand) != null) {
						findViewById(R.id.select_device_hand).setVisibility(View.VISIBLE);
					}
					if (findViewById(R.id.select_device_camera) != null) {
						findViewById(R.id.select_device_camera).setVisibility(View.GONE);
					}
					selType = SELECT_DEVICE_TYPE_HAND_DEVICE;
				} else if (checkedId == R.id.radio_btn_camera) {
					if (findViewById(R.id.select_smart_device) != null) {
						findViewById(R.id.select_smart_device).setVisibility(View.GONE);
					}
					if (findViewById(R.id.select_device_hand) != null) {
						findViewById(R.id.select_device_hand).setVisibility(View.GONE);
					}
					if (findViewById(R.id.select_device_camera) != null) {
						findViewById(R.id.select_device_camera).setVisibility(View.VISIBLE);
					}
					selType = SELECT_DEVICE_TYPE_CAMERA;
				}
			}
		});
		selTypeRadioGroup.check(R.id.radio_btn_smart);
		spinner = (Spinner) findViewById(R.id.selectdevice_camera_type_spinner);
		List<String> list = new ArrayList<String>(Arrays.asList("请选择"));
		list.addAll(1, DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_CAMERA_TYPE));
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		deviceInfoList = null;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_DEVICE_RESULT:
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (resultCode == RESULT_OK) {
				Intent intent = null;
				intent = new Intent();
				intent.putExtra("sbid", data.getStringExtra("sbid"));
				intent.putExtra("sbmc", data.getStringExtra("sbmc"));
				setResult(RESULT_OK, data);
				finish();
			}
			break;
		}
	}

	/** 处理查询结果，结果不为空，跳转到显示查询结果界面 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult() str:" + (str != null));

		if (httpRequestType == 0) {
			if (str != null) {
				if (onParseXMLData(str)) {
					if (deviceInfoList == null || deviceInfoList.size() == 0) {
						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;
						}
						HgqwToast.makeText(SelectDeviceActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					} else {
						Intent intent = new Intent();
						intent.putExtra("selhand", selType);
						intent.setClass(getApplicationContext(), SelectDeviceResultActivity.class);
						startActivityForResult(intent, STARTACTIVITY_FOR_DEVICE_RESULT);
					}
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(SelectDeviceActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(SelectDeviceActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(SelectDeviceActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG)
						.show();
			}
		}
	}

	/** 解析查询结果 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		HashMap<String, Object> map = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
							if (deviceInfoList == null) {
								deviceInfoList = new ArrayList<HashMap<String, Object>>();
							} else {
								deviceInfoList.clear();
							}
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (!success) {
							httpReturnXMLInfo = parser.nextText();
						} else {
							map = new HashMap<String, Object>();
						}
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
					} else if ("sbbh".equals(parser.getName())) {
						map.put("sbbh", parser.nextText());
					} else if ("sbxh".equals(parser.getName())) {
						map.put("sbxh", parser.nextText());
					} else if ("ip".equals(parser.getName())) {
						map.put("ip", parser.nextText());
					} else if ("type".equals(parser.getName())) {
						map.put("type", parser.nextText());
					} else if ("ssdw".equals(parser.getName())) {
						map.put("ssdw", parser.nextText());
					} else if ("zt".equals(parser.getName())) {
						map.put("zt", parser.nextText());
					} else if ("name".equals(parser.getName())) {
						map.put("name", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (map.get("id") != null) {
								if (deviceInfoList == null) {
									deviceInfoList = new ArrayList<HashMap<String, Object>>();
								}
								deviceInfoList.add(map);
							}
						}
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

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "offLineResult() res:" + (res != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (offLineRequestType == 0) {
			if (res.first) {
				if (StringUtils.isNotEmpty(res.second)) {
					List<HashMap<String, Object>> map = (List<HashMap<String, Object>>) (res.second);
					if (map != null && map.size() > 0) {
						if (deviceInfoList == null) {
							deviceInfoList = new ArrayList<HashMap<String, Object>>();
						} else {
							deviceInfoList.clear();
						}
						deviceInfoList.addAll(map);
						Intent intent = new Intent();
						intent.putExtra("selhand", selType);
						intent.setClass(getApplicationContext(), SelectDeviceResultActivity.class);
						startActivityForResult(intent, STARTACTIVITY_FOR_DEVICE_RESULT);
					} else {
						HgqwToast.makeText(this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				} else {
					HgqwToast.makeText(this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}

			} else {
				HgqwToast.makeText(this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
			}
		}
	}

}
