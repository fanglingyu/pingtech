package com.pingtech.hgqw.module.cfzg;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import android.os.Environment;
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
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 根据电子标签号，显示船舶列表界面
 * 
 * */
public class CfzgShipListActivity extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "ShipListActivity";

	public static final String FROM_BINDLIST = "bindlist";

	/** 表示船舶动态 */
	public static final int LIST_TYPE_FROM_SHIPSTATUS = 0;

	/** 表示梯口管理 */
	public static final int LIST_TYPE_FROM_TIKOUMANAGER = 1;

	/** 表示巡查巡检 */
	public static final int LIST_TYPE_FROM_XUNCHAXUNJIAN = 2;

	/** 表示卡口管理 */
	public static final int LIST_TYPE_FROM_KAKOUMANAGER = 3;

	/** 发起获取船舶列表的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GETLIST = 4;

	/** 发起船舶绑定的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_BINDSHIP = 5;

	/** 进入船舶详情界面 */
	private static final int STARTACTIVITY_FOR_SHIP_DETAIL = 6;

	/** 进入查看执勤人员的界面 */
	private static final int STARTACTIVITY_FOR_SHIP_DUTY = 7;

	private String title;

	/** 电子标签号 */
	private String cardNumber;

	private int fromType = LIST_TYPE_FROM_SHIPSTATUS;

	private ListView listView;

	private MyAdapter adapter;

	private ArrayList<HashMap<String, Object>> shipInfoList = null;

	private HashMap<String, Object> bindMap;

	private String httpReturnXMLInfo = null;

	private ProgressDialog progressDialog = null;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		// fromType = intent.getIntExtra("from", 0);
		cardNumber = intent.getStringExtra("cardNumber");
		cfzgFlag = intent.getBooleanExtra("cfzgFlag", false);
		super.onCreateForCfzg(savedInstanceState, R.layout.ship_list);

		Log.i(TAG, "onCreate()");
		if (fromType == LIST_TYPE_FROM_SHIPSTATUS) {
			setMyActiveTitle(getString(R.string.ShipStatus) + ">" + title);
		} else if (fromType == LIST_TYPE_FROM_TIKOUMANAGER) {
			setMyActiveTitle(getString(R.string.tikoumanager) + ">" + title);
		} else if (fromType == LIST_TYPE_FROM_XUNCHAXUNJIAN) {
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + title);
		} else if (fromType == LIST_TYPE_FROM_KAKOUMANAGER) {
			setMyActiveTitle(getString(R.string.kakoumanager) + ">" + title);
			if (findViewById(R.id.ship) != null) {
				findViewById(R.id.ship).setVisibility(View.GONE);
			}
			if (findViewById(R.id.kk) != null) {
				findViewById(R.id.kk).setVisibility(View.VISIBLE);
			}
		}
		adapter = new MyAdapter(this);
		if (shipInfoList == null) {
			listView = (ListView) findViewById(R.id.listview);
		}
		listView.setAdapter(adapter);
		if (cardNumber.equals(FROM_BINDLIST)) {
			try {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					HgqwToast.makeText(CfzgShipListActivity.this, R.string.sdcardunmounted, HgqwToast.LENGTH_LONG).show();
					return;
				}
				String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech";
				File dir = new File(projectDir);
				if (!dir.exists()) {
					dir.mkdir();
				}
				BufferedReader br = new BufferedReader(new FileReader(projectDir + File.separator + "bindshipinfo.xml"));
				String line = "";
				StringBuffer buffer = new StringBuffer();
				while ((line = br.readLine()) != null) {
					buffer.append(line);
				}
				br.close();
				String fileContent = buffer.toString();
				onHttpResult(fileContent, HTTPREQUEST_TYPE_FOR_GETLIST);
			} catch (IOException e) {
				e.printStackTrace();
				HgqwToast.makeText(CfzgShipListActivity.this, R.string.sdcardunmounted, HgqwToast.LENGTH_LONG).show();
				return;
			}
		} else {
			onLoadShipList();

		}
	}

	/** 开始获取船舶列表信息 */
	private void onLoadShipList() {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String url;
		if (fromType == LIST_TYPE_FROM_KAKOUMANAGER) {
			url = "getKkInfo";
			params.add(new BasicNameValuePair("cardNumber", cardNumber));
			params.add(new BasicNameValuePair("kkmc", ""));
		} else {
			url = "getShipList";
			params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
			params.add(new BasicNameValuePair("cardNumber", cardNumber));
			params.add(new BasicNameValuePair("bindType", fromType + ""));
			params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		}
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(CfzgShipListActivity.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_GETLIST);
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

		private TextView en_name;

		private TextView country;

		private TextView protry;

		private TextView pos;

		private TextView kacbzt;

		private Button operate;

		private Button detail;

		private Button duty;
	}

	/** 自定义适配器，用于显示船舶列表 */
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			int count = shipInfoList == null ? 0 : shipInfoList.size();
			return count;
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
			/** 执行相关操作 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				if (position % 3 == 2) {
				} else if (position % 3 == 1) {
					// 详情
					Intent intent = new Intent();
					Map<String, Object> _BindShip = shipInfoList.get((position - 1) / 3);
					intent.putExtra("hc", (String) _BindShip.get("hc"));
					intent.putExtra("cbzwm", (String) _BindShip.get("cbzwm"));
					intent.putExtra("cbywm", (String) _BindShip.get("cbywm"));
					intent.putExtra("gj", (String) _BindShip.get("gj"));
					intent.putExtra("cbxz", (String) _BindShip.get("cbxz"));
					intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
					intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
					intent.putExtra("from", fromType);
					if (fromType == LIST_TYPE_FROM_XUNCHAXUNJIAN) {
						intent.putExtra("fromxunchaxunjian", true);
						intent.putExtra("frombindship", true);
					}
					intent.setClass(getApplicationContext(), CfzgShipDetailActivity.class);
					startActivityForResult(intent, STARTACTIVITY_FOR_SHIP_DETAIL);
				} else if (position % 3 == 0) {
					// 动作
					if (fromType == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
						String url = "buildKkRelation";
						if (progressDialog != null) {
							return;
						}
						bindMap = shipInfoList.get(position / 3);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
						params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
						params.add(new BasicNameValuePair("bindState", "1"));
						params.add(new BasicNameValuePair("kkID", (String) bindMap.get("id")));
						params.add(new BasicNameValuePair("bindType", fromType + ""));
						//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
						params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_KK + ""));

						progressDialog = new ProgressDialog(CfzgShipListActivity.this);
						progressDialog.setTitle(getString(R.string.waiting));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						NetWorkManager.request(CfzgShipListActivity.this, url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
					} else {
						String url = "buildRelation";
						if (progressDialog != null) {
							return;
						}
						bindMap = shipInfoList.get(position / 3);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
						params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
						params.add(new BasicNameValuePair("bindState", "1"));
						params.add(new BasicNameValuePair("voyageNumber", (String) bindMap.get("hc")));
						params.add(new BasicNameValuePair("bindType", fromType + ""));
						//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
						params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
						

						progressDialog = new ProgressDialog(CfzgShipListActivity.this);
						progressDialog.setTitle(getString(R.string.waiting));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						NetWorkManager.request(CfzgShipListActivity.this, url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
					}
				}
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				if (fromType == LIST_TYPE_FROM_KAKOUMANAGER) {
					convertView = mInflater.inflate(R.layout.kk_listview_class, null);
					holder.index = (TextView) convertView.findViewById(R.id.index);
					holder.name = (TextView) convertView.findViewById(R.id.name);
					holder.en_name = (TextView) convertView.findViewById(R.id.rang);
					holder.country = (TextView) convertView.findViewById(R.id.addr);
				} else {
					convertView = mInflater.inflate(R.layout.ship_listview_class, null);
					holder.index = (TextView) convertView.findViewById(R.id.index);
					holder.name = (TextView) convertView.findViewById(R.id.name);
					holder.pos = (TextView) convertView.findViewById(R.id.pos);
					holder.kacbzt = (TextView) convertView.findViewById(R.id.kacbzt);
					holder.en_name = (TextView) convertView.findViewById(R.id.en_name);
					holder.country = (TextView) convertView.findViewById(R.id.country);
					holder.protry = (TextView) convertView.findViewById(R.id.protry);
				}
				holder.operate = (Button) convertView.findViewById(R.id.operate_btn);
				holder.detail = (Button) convertView.findViewById(R.id.detail_btn);
				holder.duty = (Button) convertView.findViewById(R.id.duty_btn);
				convertView.setTag(holder);
				holder.operate.setOnClickListener(clickListener);
				if (holder.detail != null) {
					holder.detail.setOnClickListener(clickListener);
				}
				if (holder.duty != null) {
					holder.duty.setOnClickListener(clickListener);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.operate.setTag(position * 3);
			if (holder.detail != null) {
				holder.detail.setTag(position * 3 + 1);
			}
			if (holder.duty != null) {
				holder.duty.setTag(position * 3 + 2);
			}
			if (shipInfoList == null || shipInfoList.size() == 0) {
				if (holder.index != null) {
					holder.index.setText("无");
				}
				if (holder.name != null) {
					holder.name.setText("无");
				}
				if (holder.en_name != null) {
					holder.en_name.setText("无");
				}
				if (holder.country != null) {
					holder.country.setText("无");
				}
				if (holder.protry != null) {
					holder.protry.setText("无");
				}
				if (holder.pos != null) {
					holder.pos.setText("无");
				}
				if (holder.kacbzt != null) {
					holder.kacbzt.setText("无");
				}
				holder.operate.setText("绑定");
				holder.operate.setEnabled(false);
				if (holder.detail != null) {
					holder.detail.setText(R.string.detail);
					holder.detail.setEnabled(false);
				}
			} else {
				if (holder.index != null) {
					holder.index.setText((position + 1) + "");
				}
				if (holder.name != null) {
					if (fromType == LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.name.setText((String) shipInfoList.get(position).get("kkmc"));
					} else {
						holder.name.setText((String) shipInfoList.get(position).get("cbzwm"));
					}
				}
				if (holder.en_name != null) {
					if (fromType == LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.en_name.setText((String) shipInfoList.get(position).get("kkfw"));
					} else {
						holder.en_name.setText((String) shipInfoList.get(position).get("cbywm"));
					}
				}
				if (holder.country != null) {
					if (fromType == LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.country.setText((String) shipInfoList.get(position).get("kkxx"));
					} else {
						String gj_str = (String) (shipInfoList.get(position).get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.country.setText("");
						} else {
							holder.country.setText(DataDictionary.getCountryName(gj_str));
						}
					}
				}
				if (holder.protry != null) {
					String cbxz_str = (String) shipInfoList.get(position).get("cbxz");
					if (cbxz_str == null || cbxz_str.length() == 0) {
						holder.protry.setText("");
					} else {
						holder.protry.setText(DataDictionary.getDataDictionaryName(cbxz_str, DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
					}
				}
				if (holder.pos != null) {
					if (fromType == LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.pos.setVisibility(View.GONE);
					} else {
						holder.pos.setVisibility(View.VISIBLE);
						holder.pos.setText((String) shipInfoList.get(position).get("tkwz"));
					}
				}
				if (holder.kacbzt != null) {
					holder.kacbzt.setText((String) shipInfoList.get(position).get("kacbzt"));
				}
				String bdzt = ((String) shipInfoList.get(position).get("bdzt"));
				if (bdzt == null || bdzt.equals("未绑定")) {
					holder.operate.setText("绑定");
					holder.operate.setEnabled(true);
				} else {
					holder.operate.setText("已绑定");
					holder.operate.setEnabled(false);
					SystemSetting.setBindShip(shipInfoList.get(position), fromType + "");
				}
				if (holder.detail != null) {
					holder.detail.setText(R.string.detail);
				}
				if (fromType == CfzgShipListActivity.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
					if (holder.duty != null) {
						holder.duty.setVisibility(View.VISIBLE);
					}
				}
			}

			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	/** 从详情界面返回时，如果已经执行船舶绑定了，立即返回 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_SHIP_DETAIL:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		}
	}

	/** 处理平台返回的数据 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_GETLIST) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				boolean ret = false;
				ret = onParseXMLData(str);
				if (ret && shipInfoList != null && shipInfoList.size() > 0) {
					adapter.notifyDataSetChanged();
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
					}
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(View.GONE);
					}
					listView.setVisibility(View.VISIBLE);
				} else {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(View.GONE);
					}
					listView.setVisibility(View.GONE);
					if (httpReturnXMLInfo != null) {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty).setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty)).setText(httpReturnXMLInfo);
						}
						HgqwToast.makeText(CfzgShipListActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
					} else {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty).setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
						}
						HgqwToast.makeText(CfzgShipListActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline).setVisibility(View.GONE);
				}
				listView.setVisibility(View.GONE);
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.select_result_empty)).setText(R.string.data_download_failure_info);
				}
				HgqwToast.makeText(CfzgShipListActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}
		} else if (HTTPREQUEST_TYPE_FOR_BINDSHIP == httpRequestType) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				bindMap.put("bdzt", "已绑定");
				SystemSetting.setBindShip(bindMap, fromType + "");
				HgqwToast.makeText(CfzgShipListActivity.this, R.string.bindship_success, HgqwToast.LENGTH_LONG).show();
				finish();
			} else {
				HgqwToast.makeText(CfzgShipListActivity.this, R.string.bindship_failure, HgqwToast.LENGTH_LONG).show();
				SystemSetting.setBindShip(null, fromType + "");
			}
		}
	}

	/** 解析平台返回的数据 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = null;
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
							if (shipInfoList == null) {
								shipInfoList = new ArrayList<HashMap<String, Object>>();
							} else {
								shipInfoList.clear();
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
					} else if ("hc".equals(parser.getName())) {
						map.put("hc", parser.nextText());
					} else if ("cbzwm".equals(parser.getName())) {
						map.put("cbzwm", parser.nextText());
					} else if ("cbywm".equals(parser.getName())) {
						map.put("cbywm", parser.nextText());
					} else if ("gj".equals(parser.getName())) {
						map.put("gj", parser.nextText());
					} else if ("cbxz".equals(parser.getName())) {
						map.put("cbxz", parser.nextText());
					} else if ("tkwz".equals(parser.getName())) {
						map.put("tkwz", parser.nextText());
					} else if ("tkmt".equals(parser.getName())) {
						map.put("tkmt", parser.nextText());
					} else if ("tkbw".equals(parser.getName())) {
						map.put("tkbw", parser.nextText());
					} else if ("bdzt".equals(parser.getName())) {
						if (cardNumber.equals(FROM_BINDLIST)) {
							map.put("bdzt", "未绑定");
						} else {
							if (map.get("bdzt") == null) {
								map.put("bdzt", parser.nextText());
							}
						}
					} else if ("cardNumber".equals(parser.getName())) {
						map.put("cardNumber", parser.nextText());
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
					} else if ("kkmc".equals(parser.getName())) {
						map.put("kkmc", parser.nextText());
					} else if ("kkfw".equals(parser.getName())) {
						map.put("kkfw", parser.nextText());
					} else if ("kkxx".equals(parser.getName())) {
						map.put("kkxx", parser.nextText());
					} else if ("source".equals(parser.getName())) {
						if (cardNumber.equals(FROM_BINDLIST)) {
							map.put("source", parser.nextText());
						}
					} else if ("kacbzt".equals(parser.getName())) {
						String kacbztstr = parser.nextText();
						if (kacbztstr.equals("0")) {
							map.put("kacbzt", "预到港");
						} else if (kacbztstr.equals("1")) {
							map.put("kacbzt", "在港");
						} else if (kacbztstr.equals("2")) {
							map.put("kacbzt", "预离港");
						} else if (kacbztstr.equals("3")) {
							map.put("kacbzt", "离港");
						} else {
							map.put("kacbzt", kacbztstr);
						}
					} else if ("kacbqkid".equals(parser.getName())) {
						map.put("kacbqkid", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (cardNumber.equals(FROM_BINDLIST)) {
								String hc = (String) map.get("hc");
								String source = (String) map.get("source");
								if (hc != null && source != null && !source.equals(CfzgShipListActivity.LIST_TYPE_FROM_XUNCHAXUNJIAN + "")) {
									if (shipInfoList == null) {
										shipInfoList = new ArrayList<HashMap<String, Object>>();
										shipInfoList.add(map);
									} else {
										boolean repeat = false;
										int tempcount = shipInfoList.size();
										for (int i = 0; i < tempcount; i++) {
											if (hc.equals(shipInfoList.get(i).get("hc"))) {
												repeat = true;
												break;
											}
										}
										if (!repeat) {
											shipInfoList.add(map);
										}
									}
								}
							} else {
								if (map.get("hc") != null || map.get("id") != null) {
									if (shipInfoList == null) {
										shipInfoList = new ArrayList<HashMap<String, Object>>();
									}
									shipInfoList.add(map);
								}
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
}
