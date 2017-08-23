package com.pingtech.hgqw.module.exception.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Pair;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.exception.action.YcxxAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 异常信息列表界面的activity类，包括从梯口管理、巡查巡检、卡口管理等进入异常信息
 * 
 * */
public class Exceptionlist extends MyActivity implements OnHttpResult,
		OffLineResult {
	private static final String TAG = "ExceptionlistActivity";

	/** 向平台请求未处理异常信息列表 */
	private static final int HTTPREQUEST_TYPE_FOR_GETLIST = 1;

	/** 查看未处理异常信息详情，或处理该异常信息 */
	private static final int STARTACTIVITY_FOR_EXCEPTION_DETAIL = 2;

	/** 添加一条新的异常信息 */
	private static final int STARTACTIVITY_FOR_ADD_EXCEPTION_DETAIL = 3;

	private ListView listView;

	private MyAdapter adapter;

	/** 存放异常信息列表 */
	private ArrayList<Map<String, Object>> exceptionInfoList = null;

	/** 一条异常信息对象，比如查看详情或处理时，该条异常信息数据取出来保存在这里 */
	private Map<String, Object> exceptionInfoMap;

	private String httpReturnXMLInfo = null;

	/** 用来区分从梯口管理、卡口管理还是巡查巡检进入该异常信息 */
	private String from;

	private ProgressDialog progressDialog = null;

	/**
	 * 来自主页面
	 */
	private String fromMain;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.exception_list);

		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		fromMain = intent.getStringExtra("fromMain");
		if (StringUtils.isNotEmpty(fromMain)) {
			setMyActiveTitle(getString(R.string.exception_info));
		} else {
			if (from.equals("02")) {// 梯口
				setMyActiveTitle(getString(R.string.tikoumanager) + ">"
						+ getString(R.string.exception_info));
			} else if (from.equals("03")) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
						+ getString(R.string.exception_info));
			} else if (from.equals("01")) {// 卡口
				setMyActiveTitle(getString(R.string.kakoumanager) + ">"
						+ getString(R.string.exception_info));
			} else {
				setMyActiveTitle(getString(R.string.exception_info));
			}

		}
		Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(new OnClickListener() {
			/** 刷新异常信息 */
			public void onClick(View v) {
				onLoadExceptionInfo();
			}
		});
		Button btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {
			/** 添加异常信息 */
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("from", from);
				intent.putExtra("windowtype", "03");
				HashMap<String, Object> ship = null;
				if (from.equals("01")) {
					ship = SystemSetting
							.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER
									+ "");
					if (ship != null) {
						intent.putExtra("areaname", (String) ship.get("kkmc"));
						intent.putExtra("areacode", (String) ship.get("id"));
						intent.putExtra("scene", "04");
					}
				} else if (from.equals("02")) {
					ship = SystemSetting
							.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER
									+ "");
					if (ship != null) {
						String tkwz = (String) ship.get("tkwz");
						String[] tempStr = null;
						if (tkwz != null) {
							tempStr = tkwz.split(",");
						}
						intent.putExtra("shipname", (String) ship.get("cbzwm"));
						intent.putExtra("jhhc", (String) ship.get("hc"));
						intent.putExtra("dockcode", (String) ship.get("tkmt"));
						if (tempStr != null && tempStr.length > 0) {
							intent.putExtra("dockname", tempStr[0]);
						} else {
							intent.putExtra("dockname", "");
						}
						intent.putExtra("berthcode", (String) ship.get("tkbw"));
						if (tempStr != null && tempStr.length > 1) {
							intent.putExtra("berthname", tempStr[1]);
						} else {
							intent.putExtra("berthname", "");
						}
					}
				} else if (from.equals("03")) {
					ship = SystemSetting
							.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN
									+ "");
					if (ship != null) {
						String tkwz = (String) ship.get("tkwz");
						String[] tempStr = null;
						if (tkwz != null) {
							tempStr = tkwz.split(",");
						}
						intent.putExtra("shipname", (String) ship.get("cbzwm"));
						intent.putExtra("jhhc", (String) ship.get("hc"));
						intent.putExtra("dockcode", (String) ship.get("tkmt"));
						if (tempStr != null && tempStr.length > 0) {
							intent.putExtra("dockname", tempStr[0]);
						} else {
							intent.putExtra("dockname", "");
						}
						intent.putExtra("berthcode", (String) ship.get("tkbw"));
						if (tempStr != null && tempStr.length > 1) {
							intent.putExtra("berthname", tempStr[1]);
						} else {
							intent.putExtra("berthname", "");
						}
					} else if (SystemSetting.xunJianId != null
							&& SystemSetting.xunJianId.length() > 0) {
						if (SystemSetting.xunJianType != null
								&& SystemSetting.xunJianType.equals("bw")) {
							intent.putExtra("berthcode",
									SystemSetting.xunJianId);
							intent.putExtra("berthname",
									SystemSetting.xunJianName);
							intent.putExtra("dockcode",
									SystemSetting.xunJianMTid);
							intent.putExtra("dockname",
									SystemSetting.xunJianMTname);
							intent.putExtra("scene", "02");
						} else if (SystemSetting.xunJianType != null
								&& SystemSetting.xunJianType.equals("mt")) {
							intent.putExtra("dockcode", SystemSetting.xunJianId);
							intent.putExtra("dockname",
									SystemSetting.xunJianName);
							intent.putExtra("scene", "03");
						} else if (SystemSetting.xunJianType != null
								&& SystemSetting.xunJianType.equals("qy")) {
							intent.putExtra("areacode", SystemSetting.xunJianId);
							intent.putExtra("areaname",
									SystemSetting.xunJianName);
							intent.putExtra("scene", "04");
						}
					}
				}
				intent.putExtra("fromMain", fromMain);
				intent.setClass(getApplicationContext(), Exceptioninfo.class);
				startActivityForResult(intent,
						STARTACTIVITY_FOR_ADD_EXCEPTION_DETAIL);
			}
		});
		onLoadExceptionInfo();
		adapter = new MyAdapter(this);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
	}

	/** 向平台发起获取异常信息列表的请求 */
	private void onLoadExceptionInfo() {
		// TODO Auto-generated method stub
		String url = "getIllegalInfo";
		if (progressDialog != null) {
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", LoginUser
				.getCurrentLoginUser().getUserID()));
		progressDialog = new ProgressDialog(Exceptionlist.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		if (!getState(FunctionSetting.kqlx, false)) {
			NetWorkManager.request(this, url, params,
					HTTPREQUEST_TYPE_FOR_GETLIST);
		} else {
			OffLineManager.request(this, new YcxxAction(), url,
					NVPairTOMap.nameValuePairTOMap(params),
					HTTPREQUEST_TYPE_FOR_GETLIST);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/** 自定义显示异常信息列表的适配器 */
	static class ViewHolder {
		private TextView index;

		private TextView name;

		private TextView checkTime;

		private TextView eventType;

		private TextView objectType;

		private Button operate;

		private Button detail;
	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			int size = (exceptionInfoList == null ? 0 : exceptionInfoList
					.size());
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

		private OnClickListener clickListener = new OnClickListener() {
			/** 处理异常信息列表左右按钮被点击时的事件 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				Intent intent = new Intent();
				if (position % 2 == 1) {
					// 详情
					exceptionInfoMap = exceptionInfoList
							.get((position - 1) / 2);
					intent.putExtra("action", "detail");
				} else if (position % 2 == 0) {
					// 动作
					exceptionInfoMap = exceptionInfoList.get(position / 2);
					intent.putExtra("action", "deal");
				} else {
					return;
				}
				intent.putExtra("windowtype", "03");
				intent.putExtra("id", (String) exceptionInfoMap.get("id"));
				intent.putExtra("objecttype",
						(String) exceptionInfoMap.get("objecttype"));
				intent.putExtra("cardnumber",
						(String) exceptionInfoMap.get("cardnumber"));
				intent.putExtra("cardtype",
						(String) exceptionInfoMap.get("cardtype"));
				intent.putExtra("eventtype",
						(String) exceptionInfoMap.get("eventtype"));
				intent.putExtra("name", (String) exceptionInfoMap.get("name"));
				intent.putExtra("sex", (String) exceptionInfoMap.get("sex"));
				intent.putExtra("nationality",
						(String) exceptionInfoMap.get("nationality"));
				intent.putExtra("birthday",
						(String) exceptionInfoMap.get("birthday"));
				intent.putExtra("company",
						(String) exceptionInfoMap.get("company"));
				intent.putExtra("source",
						(String) exceptionInfoMap.get("source"));
				intent.putExtra("from", from);
				intent.putExtra("eventdesc",
						(String) exceptionInfoMap.get("eventdesc"));
				intent.putExtra("eventremark",
						(String) exceptionInfoMap.get("eventremark"));
				intent.putExtra("scene", (String) exceptionInfoMap.get("scene"));
				intent.putExtra("inspecttime",
						(String) exceptionInfoMap.get("inspecttime"));
				intent.putExtra("shipname",
						(String) exceptionInfoMap.get("shipname"));
				intent.putExtra("swid", (String) exceptionInfoMap.get("swid"));
				intent.putExtra("jhhc", (String) exceptionInfoMap.get("jhhc"));
				intent.putExtra("dockcode",
						(String) exceptionInfoMap.get("dockcode"));
				intent.putExtra("berthcode",
						(String) exceptionInfoMap.get("berthcode"));
				intent.putExtra("areacode",
						(String) exceptionInfoMap.get("areacode"));
				intent.putExtra("whetherHandle",
						(String) exceptionInfoMap.get("whetherHandle"));
				intent.putExtra("glcbmc",
						(String) exceptionInfoMap.get("glcbmc"));
				intent.putExtra("cphm", (String) exceptionInfoMap.get("cphm"));
				intent.putExtra("clpp", (String) exceptionInfoMap.get("clpp"));
				intent.putExtra("fdjh", (String) exceptionInfoMap.get("fdjh"));
				intent.putExtra("cbzwm", (String) exceptionInfoMap.get("cbzwm"));
				intent.putExtra("cbywm", (String) exceptionInfoMap.get("cbywm"));
				intent.putExtra("sbmc", (String) exceptionInfoMap.get("sbmc"));
				intent.putExtra("sbid", (String) exceptionInfoMap.get("sbid"));
				intent.putExtra("qymc", (String) exceptionInfoMap.get("qymc"));
				intent.putExtra("qyid", (String) exceptionInfoMap.get("qyid"));
				intent.putExtra("jcfs", (String) exceptionInfoMap.get("jcfs"));
				intent.putExtra("cgcsid",
						(String) exceptionInfoMap.get("cgcsid"));
				intent.putExtra("dkjlid",
						(String) exceptionInfoMap.get("dkjlid"));
				intent.putExtra("dockname",
						(String) exceptionInfoMap.get("dockname"));
				intent.putExtra("berthname",
						(String) exceptionInfoMap.get("berthname"));
				intent.putExtra("areaname",
						(String) exceptionInfoMap.get("areaname"));
				intent.putExtra("sbkid", (String) exceptionInfoMap.get("sbkid"));
				intent.putExtra("handleType",
						(String) exceptionInfoMap.get("handleType"));
				intent.putExtra("handleEventType",
						(String) exceptionInfoMap.get("handleEventType"));
				intent.putExtra("handleResult",
						(String) exceptionInfoMap.get("handleResult"));
				intent.putExtra("handleRemark",
						(String) exceptionInfoMap.get("handleRemark"));
				intent.putExtra("exceptionID",
						(String) exceptionInfoMap.get("exceptionID"));
				switch (v.getId()) {
				case R.id.operate_btn:
					intent.setClass(getApplicationContext(),
							Exceptioninfo.class);
					break;
				case R.id.detail_btn:
					intent.setClass(getApplicationContext(),
							ExceptionView.class);
					break;
				default:
					break;
				}
				startActivityForResult(intent,
						STARTACTIVITY_FOR_EXCEPTION_DETAIL);
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			boolean empty = false;
			if ((exceptionInfoList == null) || (exceptionInfoList.size() == 0)) {
				empty = true;
			}
			if (true) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.exception_listview_class, null);
				holder.index = (TextView) convertView.findViewById(R.id.index);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.checkTime = (TextView) convertView
						.findViewById(R.id.checktime);
				holder.eventType = (TextView) convertView
						.findViewById(R.id.eventtype);
				holder.objectType = (TextView) convertView
						.findViewById(R.id.objecttype);
				holder.operate = (Button) convertView
						.findViewById(R.id.operate_btn);
				holder.detail = (Button) convertView
						.findViewById(R.id.detail_btn);
				convertView.setTag(holder);
				holder.operate.setTag(position * 2);
				holder.operate.setOnClickListener(clickListener);
				holder.detail.setTag(position * 2 + 1);
				holder.detail.setOnClickListener(clickListener);
			}
			if (empty) {
				if (holder.index != null) {
					holder.index.setText("无");
				}
				if (holder.name != null) {
					holder.name.setText("无");
				}
				if (holder.checkTime != null) {
					holder.checkTime.setText("无");
				}
				if (holder.eventType != null) {
					holder.eventType.setText("无");
				}
				if (holder.objectType != null) {
					holder.objectType.setText("无");
				}
				if (holder.operate != null) {
					holder.operate.setText("处理");
					holder.operate.setEnabled(false);
				}
				if (holder.detail != null) {
					holder.detail.setText(R.string.detail);
					holder.detail.setEnabled(false);
				}
			} else {
				String objecttype = (String) exceptionInfoList.get(position)
						.get("objecttype");
				String objstr = DataDictionary.getDataDictionaryName(
						objecttype,
						DataDictionary.DATADICTIONARY_TYPE_OBJECT_TYPE);
				if (holder.index != null) {
					holder.index.setText((position + 1) + "");
				}
				if (holder.name != null) {
					if (objstr.equals("人员")) {
						holder.name.setText((String) exceptionInfoList.get(
								position).get("name"));
					} else if (objstr.equals("车辆")) {
						holder.name.setText((String) exceptionInfoList.get(
								position).get("cphm"));
					} else if (objstr.equals("船舶")) {
						holder.name.setText((String) exceptionInfoList.get(
								position).get("cbzwm"));
					} else if (objstr.equals("设备")) {
						holder.name.setText((String) exceptionInfoList.get(
								position).get("sbmc"));
					} else if (objstr.equals("区域")) {
						holder.name.setText((String) exceptionInfoList.get(
								position).get("qymc"));
					} else if (objstr.equals("边检人员")) {
						holder.name.setText((String) exceptionInfoList.get(
								position).get("name"));
					}
				}
				if (holder.checkTime != null) {
					holder.checkTime.setText((String) exceptionInfoList.get(
							position).get("inspecttime"));
				}
				String eventtype = (String) exceptionInfoList.get(position)
						.get("eventtype");
				if (holder.eventType != null) {
					holder.eventType
							.setText(DataDictionary
									.getDataDictionaryName(
											eventtype,
											DataDictionary.DATADICTIONARY_TYPE_EVENT_TYPE));
				}

				if (holder.objectType != null) {
					holder.objectType.setText(objstr);
				}
				String whetherHandle = (String) exceptionInfoList.get(position)
						.get("whetherHandle");
				if (whetherHandle == null || !whetherHandle.equals("已处理")) {
					if (holder.operate != null) {
						holder.operate.setText("处理");
						holder.operate.setEnabled(true);
					}
				} else {
					if (holder.operate != null) {
						holder.operate.setText("处理");
						holder.operate.setEnabled(false);
					}
				}
				if (holder.detail != null) {
					holder.detail.setText(R.string.detail);
					holder.detail.setEnabled(true);
				}
			}
			return convertView;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	/** 从异常信息详情界面返回时，如果已经处理过了，则标记该条信息为已处理，右边“处理”按钮不显示 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_EXCEPTION_DETAIL:
			if (resultCode == RESULT_OK) {
				int whetherHandleInt = data.getIntExtra("whetherHandle", -1);
				if (whetherHandleInt == 1) {
					exceptionInfoMap.put("whetherHandle", "已处理");
					exceptionInfoMap.put("handleType",
							data.getStringExtra("handleType"));
					exceptionInfoMap.put("handleEventType",
							data.getStringExtra("handleEventType"));
					exceptionInfoMap.put("handleResult",
							data.getStringExtra("handleResult"));
					exceptionInfoMap.put("handleRemark",
							data.getStringExtra("handleRemark"));
				}
				exceptionInfoMap.put("eventdesc",
						data.getStringExtra("eventdesc"));// 事件描述
				exceptionInfoMap.put("eventremark",
						data.getStringExtra("eventremark"));// 事件备注
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType
				+ ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_GETLIST) {
			if (str != null) {
				onParseXMLData(str);
				if (adapter.getCount() > 0) {
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(
								View.GONE);
					}
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(
								View.VISIBLE);
					}
					listView.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
				} else {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(
								View.GONE);
					}
					listView.setVisibility(View.GONE);
					if (httpReturnXMLInfo != null) {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty)
									.setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty))
									.setText(httpReturnXMLInfo);
						}
						HgqwToast.makeText(Exceptionlist.this,
								httpReturnXMLInfo, HgqwToast.LENGTH_LONG)
								.show();
					} else {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty)
									.setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty))
									.setText(R.string.no_data);
						}
						HgqwToast.makeText(Exceptionlist.this,
								R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline)
							.setVisibility(View.GONE);
				}
				listView.setVisibility(View.GONE);
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(
							View.VISIBLE);
					((TextView) findViewById(R.id.select_result_empty))
							.setText(R.string.data_download_failure_info);
				}
				HgqwToast.makeText(Exceptionlist.this,
						R.string.data_download_failure_info,
						HgqwToast.LENGTH_LONG).show();
			}
		}
	}

	/** 解析平台返回的数据 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			httpReturnXMLInfo = null;
			String text = null;
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("success".equals(text)) {
							success = true;
							if (exceptionInfoList == null) {
								exceptionInfoList = new ArrayList<Map<String, Object>>();
							} else {
								exceptionInfoList.clear();
							}
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							map = new HashMap<String, Object>();
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
					} else if ("xcxsid".equals(parser.getName())) {
						map.put("xcxsid", parser.nextText());
					} else if ("objecttype".equals(parser.getName())) {
						map.put("objecttype", parser.nextText());
					} else if ("cardnumber".equals(parser.getName())) {
						map.put("cardnumber", parser.nextText());
					} else if ("cardtype".equals(parser.getName())) {
						map.put("cardtype", parser.nextText());
					} else if ("eventtype".equals(parser.getName())) {
						map.put("eventtype", parser.nextText());
					} else if ("name".equals(parser.getName())) {
						map.put("name", parser.nextText());
					} else if ("sex".equals(parser.getName())) {
						map.put("sex", parser.nextText());
					} else if ("nationality".equals(parser.getName())) {
						map.put("nationality", parser.nextText());
					} else if ("birthday".equals(parser.getName())) {
						map.put("birthday", parser.nextText());
					} else if ("company".equals(parser.getName())) {
						map.put("company", parser.nextText());
					} else if ("source".equals(parser.getName())) {
						map.put("source", parser.nextText());
					} else if ("eventdesc".equals(parser.getName())) {
						map.put("eventdesc", parser.nextText());
					} else if ("eventremark".equals(parser.getName())) {
						map.put("eventremark", parser.nextText());
					} else if ("userID".equals(parser.getName())) {
						map.put("userID", parser.nextText());
					} else if ("scene".equals(parser.getName())) {
						map.put("scene", parser.nextText());
					} else if ("inspecttime".equals(parser.getName())) {
						map.put("inspecttime", parser.nextText());
					} else if ("shipname".equals(parser.getName())) {
						map.put("shipname", parser.nextText());
					} else if ("swid".equals(parser.getName())) {
						map.put("swid", parser.nextText());
					} else if ("jhhc".equals(parser.getName())) {
						map.put("jhhc", parser.nextText());
					} else if ("dockcode".equals(parser.getName())) {
						map.put("dockcode", parser.nextText());
					} else if ("berthcode".equals(parser.getName())) {
						map.put("berthcode", parser.nextText());
					} else if ("areacode".equals(parser.getName())) {
						map.put("areacode", parser.nextText());
					} else if ("glcbmc".equals(parser.getName())) {
						map.put("glcbmc", parser.nextText());
					} else if ("cphm".equals(parser.getName())) {
						map.put("cphm", parser.nextText());
					} else if ("clpp".equals(parser.getName())) {
						map.put("clpp", parser.nextText());
					} else if ("fdjh".equals(parser.getName())) {
						map.put("fdjh", parser.nextText());
					} else if ("cbzwm".equals(parser.getName())) {
						map.put("cbzwm", parser.nextText());
					} else if ("cbywm".equals(parser.getName())) {
						map.put("cbywm", parser.nextText());
					} else if ("sbmc".equals(parser.getName())) {
						map.put("sbmc", parser.nextText());
					} else if ("sbid".equals(parser.getName())) {
						map.put("sbid", parser.nextText());
					} else if ("qymc".equals(parser.getName())) {
						map.put("qymc", parser.nextText());
					} else if ("qyid".equals(parser.getName())) {
						map.put("qyid", parser.nextText());
					} else if ("jcfs".equals(parser.getName())) {
						map.put("jcfs", parser.nextText());
					} else if ("cgcsid".equals(parser.getName())) {
						map.put("cgcsid", parser.nextText());
					} else if ("dkjlid".equals(parser.getName())) {
						map.put("dkjlid", parser.nextText());
					} else if ("dockname".equals(parser.getName())) {
						map.put("dockname", parser.nextText());
					} else if ("berthname".equals(parser.getName())) {
						map.put("berthname", parser.nextText());
					} else if ("areaname".equals(parser.getName())) {
						map.put("areaname", parser.nextText());
					} else if ("sbkid".equals(parser.getName())) {
						map.put("sbkid", parser.nextText());
					}

					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (map.get("id") != null) {
								if (exceptionInfoList == null) {
									exceptionInfoList = new ArrayList<Map<String, Object>>();
								}
								exceptionInfoList.add(map);
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
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (offLineRequestType == HTTPREQUEST_TYPE_FOR_GETLIST) {
			if (res.second != null) {
				ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) res.second;
				exceptionInfoList = list;
				if (adapter.getCount() > 0) {
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(
								View.GONE);
					}
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(
								View.VISIBLE);
					}
					listView.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
				} else {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(
								View.GONE);
					}
					listView.setVisibility(View.GONE);

					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(
								View.VISIBLE);
						((TextView) findViewById(R.id.select_result_empty))
								.setText(R.string.no_data);
					}
					HgqwToast.makeText(Exceptionlist.this, R.string.no_data,
							HgqwToast.LENGTH_LONG).show();

				}
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline)
							.setVisibility(View.GONE);
				}
				listView.setVisibility(View.GONE);
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(
							View.VISIBLE);
					((TextView) findViewById(R.id.select_result_empty))
							.setText(R.string.no_data);
				}
				HgqwToast.makeText(Exceptionlist.this, R.string.no_data,
						HgqwToast.LENGTH_LONG).show();
			}
		}

	}
}
