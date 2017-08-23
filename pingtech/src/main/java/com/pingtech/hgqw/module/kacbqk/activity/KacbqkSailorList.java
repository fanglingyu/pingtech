package com.pingtech.hgqw.module.kacbqk.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Xml;
import android.view.View;
import android.widget.ListView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.adapter.CymdListAdapter;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.cyxx.action.CyxxAction;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 
 * 类描述：从口岸船舶列表中查看相应船舶的船员名单
 * 
 * <p>
 * Title: 系统名称-KacbqkSailorList.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author jiajw
 * @version 1.0
 * @date 2014-1-16 下午3:02:41
 */
public class KacbqkSailorList extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "KacbqkSailorList";

	/** 发起获取船员名单的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GETDETAIL = 1;

	/** 绑定船舶航次号 */
	private String voyageNumber;

	/** 保存查询结果 */
	private ArrayList<HashMap<String, String>> personInfoList = null;

	private ProgressDialog progressDialog = null;

	private ListView listView;

	private View listview_topline;

	private CymdListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.shipper_list);
		initView();
		bindData();

	}

	/**
	 * 
	 * @方法名：initView
	 * @功能说明：初始化控件
	 * @author jiajw
	 * @date 2014-1-17 上午11:55:05
	 */
	private void initView() {
		setMyActiveTitle(getString(R.string.kacbqk) + ">" + getString(R.string.tkgl_sailor_list));
		listView = (ListView) findViewById(R.id.cymd_listview);
		listview_topline = findViewById(R.id.topline_lv);
	}

	/**
	 * 
	 * @方法名：bindData
	 * @功能说明：初始化数据
	 * @author jiajw
	 * @date 2014-1-17 上午11:56:25
	 */
	private void bindData() {

		Intent intent = getIntent();
		voyageNumber = intent.getStringExtra("hc");
		String url = "getPersonInfo";

		// 判断网络是否可用，如果网络不可用则取本地数据,否则取网络数据

		if (BaseApplication.instent.getWebState()) {
			onLoadShiperList(voyageNumber, url);

		} else {

			if (progressDialog != null) {
				return;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
			params.add(new BasicNameValuePair("xm", ""));
			params.add(new BasicNameValuePair("zw", ""));
			params.add(new BasicNameValuePair("xb", ""));
			params.add(new BasicNameValuePair("zjzl", ""));
			params.add(new BasicNameValuePair("zjhm", ""));
			params.add(new BasicNameValuePair("comeFrom", "1"));
			params.add(new BasicNameValuePair("cywz", ""));

			showDialog().show();

			OffLineManager.request(KacbqkSailorList.this, new CyxxAction(), url, NVPairTOMap.nameValuePairTOMap(params),
					HTTPREQUEST_TYPE_FOR_GETDETAIL);
		}
	}

	/***
	 * 
	 * @方法名：onLoadShiperList
	 * @功能说明：联网获取船员名单信息
	 * @author jiajw
	 * @date 2014-1-16 下午3:54:23
	 */
	private void onLoadShiperList(String hc, String url) {

		if (progressDialog != null) {
			return;
		}
		// xm=,xb=,zw=,zjzl=,zjhm=,comeFrom=1,cywz=0

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", hc));
		params.add(new BasicNameValuePair("xm", ""));
		params.add(new BasicNameValuePair("zw", ""));
		params.add(new BasicNameValuePair("xb", ""));
		params.add(new BasicNameValuePair("zjzl", ""));
		params.add(new BasicNameValuePair("zjhm", ""));
		params.add(new BasicNameValuePair("comeFrom", "1"));
		params.add(new BasicNameValuePair("cywz", ""));
		showDialog().show();
		NetWorkManager.request(KacbqkSailorList.this, url, params, HTTPREQUEST_TYPE_FOR_GETDETAIL);

	}

	/**
	 * 
	 * @方法名：showDialog
	 * @功能说明：请求服务时弹出dialog,提示正在请求，请稍后
	 * @author jiajw
	 * @date 2014-1-17 下午12:37:46
	 * @return
	 */
	private ProgressDialog showDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		return progressDialog;

	}

	/** 解析查询结果 */
	public static ArrayList<HashMap<String, String>> onParseXMLData(String str) {
		ArrayList<HashMap<String, String>> cymdList = null;
		HashMap<String, String> map = null;
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();

			String text = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("success".equals(text)) {
							success = true;
							if (cymdList == null) {
								cymdList = new ArrayList<HashMap<String, String>>();
							} else {
								cymdList.clear();
							}
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (!success) {

						} else {
							map = new HashMap<String, String>();
						}
					} else if ("id".equals(parser.getName())) {
						map.put("ryid", parser.nextText());// 人员ID
					} else if ("xm".equals(parser.getName())) {
						map.put("xm", parser.nextText());// 姓名
					} else if ("xb".equals(parser.getName())) {
						map.put("xb", parser.nextText());// 性别
					} else if ("hgzl".equals(parser.getName())) {
						map.put("hgzl", parser.nextText());// 海港证类
					} else if ("gj".equals(parser.getName())) {
						map.put("gj", parser.nextText());// 国籍
					} else if ("lcbz".equals(parser.getName())) {
						map.put("lcbz", parser.nextText());// 离船标识
					} else if ("zw".equals(parser.getName())) {
						map.put("zw", parser.nextText());// 职务
					} else if ("zjzl".equals(parser.getName())) {
						map.put("zjlx", parser.nextText());// 证件种类
					} else if ("zjhm".equals(parser.getName())) {
						map.put("zjhm", parser.nextText());// 证件号码
					} else if ("photo".equals(parser.getName())) {
						map.put("photo", parser.nextText());// 照片
					} else if ("csrq".equals(parser.getName())) {
						map.put("csrq", parser.nextText());// 出生日期
					} else if ("ssdw".equals(parser.getName())) {
						map.put("ssdw", parser.nextText());// 所属单位
					} else if ("pzxx".equals(parser.getName())) {
						map.put("pzxx", parser.nextText());// 碰撞
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (cymdList == null) {
								cymdList = new ArrayList<HashMap<String, String>>();
							}
							cymdList.add(map);
						}
					}
					break;
				}
				type = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cymdList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (res != null) {
			ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) res.second;
			if (list != null && list.size() > 0) {
				personInfoList = list;
				sortList(personInfoList);

				adapter = new CymdListAdapter(getApplicationContext(), personInfoList);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				setMyActiveTitle(getString(R.string.kacbqk) + ">" + getString(R.string.tkgl_sailor_list) + "   总数:" + personInfoList.size());
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(KacbqkSailorList.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();

			}
		}

	}

	/**
	 * 
	 * @方法名：sortList
	 * @功能说明：列表重新排序，离船的放到最后, 0：在船 , 1：离船、2：登船
	 * @author liums
	 * @date 2014-1-21 下午3:26:35
	 * @param list
	 */
	private void sortList(ArrayList<HashMap<String, String>> list) {
		String lcbz = "";
		ArrayList<HashMap<String, String>> lcList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> notLcList = new ArrayList<HashMap<String, String>>();
		for (HashMap<String, String> map : list) {
			lcbz = map.get("lcbz");
			if ("1".equals(lcbz)) {// 如果是离船
				lcList.add(map);
			} else {
				notLcList.add(map);
			}
		}
		list.clear();
		list.addAll(notLcList);
		list.addAll(lcList);
	}

	/**
	 * 处理平台返回的数据 onHttpResult()httpRequestType:1,resulttrue
	 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + "***************" + (str != null));

		if (str != null) {
			personInfoList = onParseXMLData(str);

			if (personInfoList != null && personInfoList.size() > 0) {
				sortList(personInfoList);
				listview_topline.setVisibility(View.VISIBLE);
				adapter = new CymdListAdapter(KacbqkSailorList.this, personInfoList);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				setMyActiveTitle(getString(R.string.kacbqk) + ">" + getString(R.string.tkgl_sailor_list) + "   总数:" + personInfoList.size());
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}

				HgqwToast.makeText(KacbqkSailorList.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();

			}
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			HgqwToast.makeText(KacbqkSailorList.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
		}

	}
}
