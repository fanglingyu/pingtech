package com.pingtech.hgqw.module.kacbqk.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.kacbqk.utils.KacbqkUtil;
import com.pingtech.hgqw.module.kacbqk.utils.PullXmlForKacbList;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.kacbqk.action.KacbqkAction;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 
 * 类描述：口岸船舶情况
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-KacbqkManager.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2014-1-15 上午10:56:31
 */
public class KacbqkShipSearch extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "KacbqkManager";

	public static ArrayList<HashMap<String, Object>> shipList = null;

	private EditText selectship_name_jianhu = null;

	private Spinner kacbqk_select_ship_cbkazt = null;

	private Spinner selectship_property_spinner = null;

	private Spinner selectship_matou_spinner = null;

	private Spinner selectship_bowei_spinner = null;

	private ArrayAdapter<String> spinnerAdapter = null;

	private ProgressDialog progressDialog = null;

	/** 发起普通查询船舶的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_SELECTSHIP = 4;

	/** 进入显示查询结果界面 */
	private static final int STARTACTIVITY_FOR_SHIP_RESULT = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState, R.layout.kacbqk_select_ship);
		setMyActiveTitle(R.string.kacbqk);
		find();
		initData();

	}

	/**
	 * 
	 * @方法名：click
	 * @功能说明：点击事件
	 * @author liums
	 * @date 2014-1-15 上午11:58:55
	 * @param v
	 */
	public void click(View v) {
		Log.i(TAG, "click");
		switch (v.getId()) {
		case R.id.select_ship_submit:
			selectShip();
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：selectShip
	 * @功能说明：查询船舶
	 * @author liums
	 * @date 2014-1-15 上午11:55:19
	 */
	private void selectShip() {
		if (progressDialog != null) {
			return;
		}
		BaseAction action = null;
		String url;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		action = new KacbqkAction();
		url = "getWardShipList";
		params.add(new BasicNameValuePair("shipName", ((EditText) findViewById(R.id.selectship_name_jianhu)).getText().toString()));

		// 口岸状态
		Spinner cbkaztSpinner = (Spinner) findViewById(R.id.kacbqk_select_ship_cbkazt);
		int cbkaztItem = cbkaztSpinner.getSelectedItemPosition();
		if (cbkaztItem == 0) {
			params.add(new BasicNameValuePair("cbkazt", null));
		} else {
			params.add(new BasicNameValuePair("cbkazt", KacbqkUtil.getCbkaztStr(cbkaztItem)));
		}

		// 船舶性质
		Spinner Quality_inner = (Spinner) findViewById(R.id.selectship_property_spinner);
		if (Quality_inner.getSelectedItemPosition() == 0) {
			params.add(new BasicNameValuePair("shipQuality", null));
		} else {
			params.add(new BasicNameValuePair("shipQuality", DataDictionary.getDataDictionaryCodeByIndex(Quality_inner.getSelectedItemPosition() - 1,
					DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)));
		}

		// 码头
		Spinner dock_inner = (Spinner) findViewById(R.id.selectship_matou_spinner);
		String dockid = null;
		if (dock_inner.getSelectedItemPosition() == 0) {
			params.add(new BasicNameValuePair("dock", null));
		} else {
			dockid = SystemSetting.getBaseInfoDockId(dock_inner.getSelectedItemPosition() - 1);
			params.add(new BasicNameValuePair("dock", dockid));
		}

		// 泊位
		Spinner berth_inner = (Spinner) findViewById(R.id.selectship_bowei_spinner);
		if (berth_inner.getSelectedItemPosition() == 0 || dockid == null) {
			params.add(new BasicNameValuePair("berth", null));
		} else {
			params.add(new BasicNameValuePair("berth", SystemSetting.getBaseInfoBerthId(dockid, berth_inner.getSelectedItemPosition() - 1)));
		}
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));

		// 类型（船舶动态0、梯口管理1、巡查巡检2、卡口管理3---用于卡口管理，记录上下轮物品的查询船舶）
		params.add(new BasicNameValuePair("type", KacbqkUtil.BIND_TYPE_KACB));
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		progressDialog = new ProgressDialog(KacbqkShipSearch.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(KacbqkShipSearch.this, url, params, HTTPREQUEST_TYPE_FOR_SELECTSHIP);
		} else {
			OffLineManager.request(KacbqkShipSearch.this, action, url, NVPairTOMap.nameValuePairTOMap(params), HTTPREQUEST_TYPE_FOR_SELECTSHIP);
		}

	}

	private void initData() {
		initSpinner();
	}

	private void initSpinner() {
		// 船舶性质
		List<String> list = new ArrayList<String>(Arrays.asList("请选择"));
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		list.addAll(1, DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectship_property_spinner.setAdapter(spinnerAdapter);

		// 船舶状态
		list = new ArrayList<String>(Arrays.asList("请选择"));
		list.add("预到港船舶");
		list.add("在港船舶");
		list.add("预离港船舶");
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		kacbqk_select_ship_cbkazt.setAdapter(spinnerAdapter);

		// 码头
		list = new ArrayList<String>(Arrays.asList("请选择"));
		list.addAll(1, SystemSetting.getBaseInfoDockList());
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectship_matou_spinner.setAdapter(spinnerAdapter);
		selectship_matou_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String dockid;
				List<String> list = new ArrayList<String>(Arrays.asList("请选择"));
				if (arg2 == 0) {
				} else {
					dockid = SystemSetting.getBaseInfoDockId(arg2 - 1);
					list.addAll(1, SystemSetting.getBaseInfoBerthList(dockid));
				}
				// 泊位
				spinnerAdapter = new ArrayAdapter<String>(KacbqkShipSearch.this, android.R.layout.simple_spinner_item, list);
				spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				selectship_bowei_spinner.setAdapter(spinnerAdapter);
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void find() {
		selectship_name_jianhu = (EditText) findViewById(R.id.selectship_name_jianhu);
		kacbqk_select_ship_cbkazt = (Spinner) findViewById(R.id.kacbqk_select_ship_cbkazt);
		selectship_property_spinner = (Spinner) findViewById(R.id.selectship_property_spinner);
		selectship_matou_spinner = (Spinner) findViewById(R.id.selectship_matou_spinner);
		selectship_bowei_spinner = (Spinner) findViewById(R.id.selectship_bowei_spinner);
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_SELECTSHIP) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				// 解析
				shipList = PullXmlForKacbList.onParseXMLData(str);
				if (shipList != null && shipList.size() > 0) {
					toShipListView();
				} else {
					HgqwToast.makeText(KacbqkShipSearch.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}
			} else {
				HgqwToast.makeText(KacbqkShipSearch.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> obj, int offLineRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (offLineRequestType == HTTPREQUEST_TYPE_FOR_SELECTSHIP) {
			if (shipList != null) {
				shipList.clear();
			}
			if (obj.second != null) {
				if (obj.second != null) {
					ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) obj.second;
					shipList = list;
				}
				if (shipList != null && shipList.size() > 0) {
					toShipListView();
				} else {
					HgqwToast.makeText(KacbqkShipSearch.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}

			} else {
				HgqwToast.makeText(KacbqkShipSearch.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 
	 * @方法名：toShipListView
	 * @功能说明：查询到船舶，跳转到列表页面
	 * @author liums
	 * @date 2014-1-16 上午9:49:27
	 */
	private void toShipListView() {
		Intent intent = new Intent();
		intent.putExtra("frombindship", false);
		intent.putExtra("fromxuncha", false);
		intent.putExtra("fromglcb", false);
		intent.putExtra("bindtype", GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS);
		intent.putExtra("shewai", false);
		intent.putExtra("cfzgFlag", false);
		intent.setClass(getApplicationContext(), KacbqkShipList.class);
		startActivityForResult(intent, STARTACTIVITY_FOR_SHIP_RESULT);
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
}
