package com.pingtech.hgqw.activity;

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
import android.graphics.Color;
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
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.exception.activity.Exceptioninfo;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.xunjian.action.XunJianAction;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 查看执勤人员界面的activity类 */
public class DutyPersonlistActivity extends MyActivity implements OnHttpResult,OffLineResult {
	private static final String TAG = "DutyPersonlistActivity";
	private static final int STARTACTIVITY_FOR_EXCEPTION = 1;
	/** 航次号或卡口id */
	private String cardNumber;
	/** 标记从哪个activity过来，0是船舶，3是卡口 */
	private String from;
	private ListView listView;
	private MyAdapter adapter;
	/** 保存执勤人员信息 */
	private ArrayList<HashMap<String, String>> dutyPersonList = null;
	private String httpReturnXMLInfo = null;
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.dutyperson_list);

		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		cardNumber = intent.getStringExtra("hc");
		from = intent.getStringExtra("from");
		setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.view_duty_person));
		adapter = new MyAdapter(this);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
		progressDialog = new ProgressDialog(DutyPersonlistActivity.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		String url = "getPersonOnDuty";
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if (from.equals("0")) {
			params.add(new BasicNameValuePair("voyageNumber", cardNumber));
			params.add(new BasicNameValuePair("kkID", ""));
		} else {
			params.add(new BasicNameValuePair("voyageNumber", ""));
			params.add(new BasicNameValuePair("kkID", cardNumber));
		}
		params.add(new BasicNameValuePair("bindObject", from));
		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(DutyPersonlistActivity.this, url, params, 0);
		} else {
			OffLineManager.request(DutyPersonlistActivity.this, new XunJianAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	static class ViewHolder {
		private TextView index;
		private TextView name;
		private TextView office;
		private TextView unit;
		private TextView time;
		private TextView zt;
		private Button operate;
	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return dutyPersonList == null ? 0 : dutyPersonList.size();
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
			/** 记录异常 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				Intent intent = new Intent();
				Map<String, String> DutyPerson = dutyPersonList.get(position);
				intent.putExtra("name", DutyPerson.get("xm"));
				intent.putExtra("nationality", DutyPerson.get("gj"));
				intent.putExtra("sex", DutyPerson.get("xb"));
				intent.putExtra("cardtype", DutyPerson.get("zjzl"));
				intent.putExtra("birthday", DutyPerson.get("csrq"));
				intent.putExtra("cardnumber", DutyPerson.get("zjhm"));
				intent.putExtra("company", DutyPerson.get("ssdw"));

				intent.putExtra("from", "03");
				intent.putExtra("objecttype", "06");
				intent.putExtra("windowtype", "03");
				intent.putExtra("sbkid", DutyPerson.get("id"));
				intent.setClass(getApplicationContext(), Exceptioninfo.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_EXCEPTION);
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.dutyperson_listview_class, null);
				holder.index = (TextView) convertView.findViewById(R.id.index);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.office = (TextView) convertView.findViewById(R.id.office);
				holder.unit = (TextView) convertView.findViewById(R.id.unit);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.zt = (TextView) convertView.findViewById(R.id.zt);
				holder.operate = (Button) convertView.findViewById(R.id.operate_btn);
				convertView.setTag(holder);
				holder.operate.setOnClickListener(clickListener);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.operate.setTag(position);
			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (holder.name != null) {
				holder.name.setText(dutyPersonList.get(position).get("xm"));
			}
			if (holder.office != null) {
				String office_s = dutyPersonList.get(position).get("zw");
				holder.office.setText(getString(R.string.person_balance_office_tag)
						+ (office_s == null ? "" : office_s));
			}
			if (holder.unit != null) {
				String ssdw_s = dutyPersonList.get(position).get("ssdw");
				holder.unit.setText(getString(R.string.person_balance_uint) + "：" + (ssdw_s == null ? "" : ssdw_s));
			}
			if (holder.time != null) {
				String kszq_s = dutyPersonList.get(position).get("kkzqsj");
				holder.time.setText(getString(R.string.start_duty_time) + "：" + (kszq_s == null ? "" : kszq_s));
			}
			if (holder.zt != null) {
				String ztStr = dutyPersonList.get(position).get("zt");
				holder.zt.setText(getString(R.string.zt) + "：" + (ztStr == null ? "" : ztStr));
				if("正在执勤".equals(ztStr)){
					holder.zt.setTextColor(Color.RED);
				}else{
					holder.zt.setTextColor(0xFFACACAC);
				}
			}
			if (holder.operate != null) {
				holder.operate.setText(R.string.Exception_regist);
			}
			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_EXCEPTION:
			if (resultCode == RESULT_OK) {
				/** 保存异常成功后，退出该界面 */
				finish();
			}
			break;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null) {
			onParseXMLData(str);
			if (adapter.getCount() > 0) {
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(View.GONE);
				}
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
				}
				listView.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
			} else {

				if (httpReturnXMLInfo != null) {
					if (findViewById(R.id.select_result_empty) != null) {
						((TextView) findViewById(R.id.select_result_empty)).setText(httpReturnXMLInfo);
					}
					HgqwToast.makeText(DutyPersonlistActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					if (findViewById(R.id.select_result_empty) != null) {
						((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
					}
					HgqwToast.makeText(DutyPersonlistActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}
			}
		} else {
			if (findViewById(R.id.select_result_empty) != null) {
				((TextView) findViewById(R.id.select_result_empty)).setText(R.string.data_download_failure_info);
			}
			HgqwToast.makeText(DutyPersonlistActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
		}
	}

	/** 向平台请求执勤人员信息后，解析平台返回的数据 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = null;
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
							if (dutyPersonList == null) {
								dutyPersonList = new ArrayList<HashMap<String, String>>();
							} else {
								dutyPersonList.clear();
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
					} else if ("xm".equals(parser.getName())) {
						map.put("xm", parser.nextText());
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
					} else if ("xb".equals(parser.getName())) {
						map.put("xb", parser.nextText());
					} else if ("zw".equals(parser.getName())) {
						map.put("zw", parser.nextText());
					} else if ("zjzl".equals(parser.getName())) {
						map.put("zjzl", parser.nextText());
					} else if ("zjhm".equals(parser.getName())) {
						map.put("zjhm", parser.nextText());
					} else if ("ssdw".equals(parser.getName())) {
						map.put("ssdw", parser.nextText());
					} else if ("gj".equals(parser.getName())) {
						map.put("gj", parser.nextText());
					} else if ("csrq".equals(parser.getName())) {
						map.put("csrq", parser.nextText());
					} else if ("kkzqsj".equals(parser.getName())) {
						map.put("kkzqsj", parser.nextText());
					}else if ("zt".equals(parser.getName())) {//// 执勤状态：zzzq,bq
						String zt = parser.nextText();
						if("zzzq".equals(zt)){
							map.put("zt", "正在执勤");
						}else{
							map.put("zt", "备勤");
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (dutyPersonList == null) {
								dutyPersonList = new ArrayList<HashMap<String, String>>();
							}
							dutyPersonList.add(map);
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
		if (res.second != null) {
			onHttpResult((String) res.second, offLineRequestType);
		} else {
			if (findViewById(R.id.select_result_empty) != null) {
				((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
			}
			HgqwToast.makeText(DutyPersonlistActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
		}

	}
}
