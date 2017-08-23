package com.pingtech.hgqw.module.cfzg;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
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
 * 查询船舶（卡口）模块，根据船舶名称模糊查询、根据区域、泊位、码头等任意搭配查询
 */
public class CfzgSelectShipActivity extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "SelectShipActivity";

	/** 查询监护船舶 */
	private static final int SELECT_SHIP_TYPE_WARD = 0;

	/** 查询涉外服务型船舶 */
	private static final int SELECT_SHIP_TYPE_SERVICE = 1;

	/** 发起普通查询船舶的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_SELECTSHIP = 4;

	/** 发起查询卡口的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_SELECTKK = 5;

	/** 发起查询绑定船舶的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_BINDSHIP = 6;

	/** 进入显示查询结果界面 */
	private static final int STARTACTIVITY_FOR_SHIP_RESULT = 7;

	private ListView listView;

	/** 绑定类型，来自船舶动态？梯口管理？卡口管理？巡查巡检？ */
	private int bindType = CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS;

	private MyAdapter adapter;

	private ArrayAdapter<String> spinnerAdapter;

	private Spinner spinner;

	private int selType = SELECT_SHIP_TYPE_WARD;

	/** 用于保存通常查询结果信息 */
	public static ArrayList<HashMap<String, Object>> normalShipList = null;

	/** 用于保存从巡查巡检模块进入查询船舶的查询结果信息，单独定义一个list是为了防止由此进入查看执勤人员及异常信息，再进入查询船舶时，查询结果冲突 */
	public static ArrayList<HashMap<String, Object>> xunjianShipList = null;

	private String httpReturnXMLInfo = null;

	private ProgressDialog progressDialog = null;

	/** 是否来自船舶绑定 */
	private boolean fromBindShip;

	/** 是否来自巡查巡检 */
	private boolean fromXunCha;

	/** 是否查询关联船舶 */
	private boolean fromGlcb;

	/** 是否来自查岗查哨 */
	private boolean fromCgcs;

	private HashMap<String, Object> bindMap;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	/**
	 * 梯口管理的物品检查标志位：true ，false
	 */
	private boolean tk_good_flag = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		fromBindShip = intent.getBooleanExtra("frombindship", false);
		fromXunCha = intent.getBooleanExtra("fromxuncha", false);
		fromCgcs = intent.getBooleanExtra("fromecgcs", false);
		fromGlcb = intent.getBooleanExtra("fromglcb", false);
		bindType = intent.getIntExtra("bindtype", 0);
		cfzgFlag = intent.getBooleanExtra("cfzgFlag", false);
		tk_good_flag = intent.getBooleanExtra("tk_good_flag", false);
		if(true){
			super.onCreateForCfzg(savedInstanceState, R.layout.selectship);
		}else{
			super.onCreate(savedInstanceState, R.layout.selectship);
		}
		Log.i(TAG, "onCreate()");
		if (fromXunCha) {
			if (xunjianShipList == null) {
				xunjianShipList = new ArrayList<HashMap<String, Object>>();
			} else {
				xunjianShipList.clear();
			}
		} else {
			if (normalShipList == null) {
				normalShipList = new ArrayList<HashMap<String, Object>>();
			} else {
				normalShipList.clear();
			}
		}
		if (fromBindShip) {
			if (bindType == CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.select_ship));
			} else if (bindType == CfzgShipListActivity.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.select_ship));
			} else if (bindType == CfzgShipListActivity.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.select_ship));
			} else if (bindType == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
				setMyActiveTitle(getString(R.string.kakoumanager) + ">" + getString(R.string.kakou_band) + ">" + getString(R.string.select_kakou));
				setContentView(R.layout.selectkk);
			}
			if (bindType != CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
				findViewById(R.id.select_rang).setVisibility(View.GONE);

			}
		} else if (fromXunCha) {
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.select_ship));
			findViewById(R.id.select_rang).setVisibility(View.GONE);
		} else {
			setMyActiveTitle(getString(R.string.exception_info) + ">" + getString(R.string.select_ship));
			if (fromGlcb || fromCgcs) {
				findViewById(R.id.select_rang).setVisibility(View.GONE);
			}
		}
		if (fromXunCha) {
			if (findViewById(R.id.sel_ship_list_title_bind) != null) {
				findViewById(R.id.sel_ship_list_title_bind).setVisibility(View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
				findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
				findViewById(R.id.sel_ship_list_title_shewai).setVisibility(View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
				findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(View.VISIBLE);
			}
		} else if (fromBindShip) {
			if (bindType != CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
				if (findViewById(R.id.sel_ship_list_title_bind) != null) {
					findViewById(R.id.sel_ship_list_title_bind).setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
					findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(View.GONE);
				}
				if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
					findViewById(R.id.sel_ship_list_title_shewai).setVisibility(View.GONE);
				}
				if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
					findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(View.GONE);
				}
			}
		} else {
			if (findViewById(R.id.sel_ship_list_title_bind) != null) {
				findViewById(R.id.sel_ship_list_title_bind).setVisibility(View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
				findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(View.VISIBLE);
			}
			if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
				findViewById(R.id.sel_ship_list_title_shewai).setVisibility(View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
				findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(View.GONE);
			}
		}
		adapter = new MyAdapter(this);

		listView = (ListView) findViewById(R.id.listview);
		if (listView != null) {
			listView.setAdapter(adapter);
		}
		if (fromBindShip && (bindType == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER)) {
			Button submitbtn = (Button) findViewById(R.id.select_submit_kk);
			submitbtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String url = "getKkInfo";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("kkmc", ((EditText) findViewById(R.id.select_kk_name)).getText().toString()));
					params.add(new BasicNameValuePair("cardNumber", ""));
					params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
					params.add(new BasicNameValuePair("bindType", CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER + ""));
					if (progressDialog != null) {
						return;
					}
					progressDialog = new ProgressDialog(CfzgSelectShipActivity.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					NetWorkManager.request(CfzgSelectShipActivity.this, url, params, HTTPREQUEST_TYPE_FOR_SELECTKK);
				}
			});
		} else {
			RadioGroup sel_type_rg = (RadioGroup) findViewById(R.id.select_ship_type);
			sel_type_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if (checkedId == R.id.radio_btn_jh) {
						if (findViewById(R.id.select_ship_jianhu) != null) {
							findViewById(R.id.select_ship_jianhu).setVisibility(View.VISIBLE);
						}
						if (findViewById(R.id.select_ship_shewai) != null) {
							findViewById(R.id.select_ship_shewai).setVisibility(View.GONE);
						}
						selType = SELECT_SHIP_TYPE_WARD;
					} else if (checkedId == R.id.radio_btn_sw) {
						if (findViewById(R.id.select_ship_jianhu) != null) {
							findViewById(R.id.select_ship_jianhu).setVisibility(View.GONE);
						}
						if (findViewById(R.id.select_ship_shewai) != null) {
							findViewById(R.id.select_ship_shewai).setVisibility(View.VISIBLE);
						}
						selType = SELECT_SHIP_TYPE_SERVICE;
					}
				}
			});
			sel_type_rg.check(R.id.radio_btn_jh);
			spinner = (Spinner) findViewById(R.id.selectship_property_spinner);
			List<String> list = new ArrayList<String>(Arrays.asList("请选择"));
			list.addAll(1, DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
			spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(spinnerAdapter);
			spinner = (Spinner) findViewById(R.id.selectship_purpose_spinner);
			list = new ArrayList<String>(Arrays.asList("请选择"));
			list.addAll(1, DataDictionary.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_SHIP_PURPOSE));
			spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(spinnerAdapter);
			spinner = (Spinner) findViewById(R.id.selectship_matou_spinner);
			list = new ArrayList<String>(Arrays.asList("请选择"));
			list.addAll(1, SystemSetting.getBaseInfoDockList());
			spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(spinnerAdapter);
			spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// TODO Auto-generated method stub
					String dockid;
					List<String> list = new ArrayList<String>(Arrays.asList("请选择"));
					if (arg2 == 0) {
					} else {
						dockid = SystemSetting.getBaseInfoDockId(arg2 - 1);
						list.addAll(1, SystemSetting.getBaseInfoBerthList(dockid));
					}
					spinner = (Spinner) findViewById(R.id.selectship_bowei_spinner);
					spinnerAdapter = new ArrayAdapter<String>(CfzgSelectShipActivity.this, android.R.layout.simple_spinner_item, list);
					spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(spinnerAdapter);
				}

				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

			Button submitbtn = (Button) findViewById(R.id.select_ship_submit);
			submitbtn.setOnClickListener(new OnClickListener() {
				/** 提交查询操作 */
				public void onClick(View v) {
					if (progressDialog != null) {
						return;
					}
					String url;
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					if (selType == SELECT_SHIP_TYPE_WARD) {
						url = "getWardShipList";
						params.add(new BasicNameValuePair("shipName", ((EditText) findViewById(R.id.selectship_name_jianhu)).getText().toString()));
						Spinner Quality_inner = (Spinner) findViewById(R.id.selectship_property_spinner);
						if (Quality_inner.getSelectedItemPosition() == 0) {
							params.add(new BasicNameValuePair("shipQuality", null));
						} else {
							params.add(new BasicNameValuePair("shipQuality", DataDictionary.getDataDictionaryCodeByIndex(
									Quality_inner.getSelectedItemPosition() - 1, DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)));
						}
						Spinner dock_inner = (Spinner) findViewById(R.id.selectship_matou_spinner);
						String dockid = null;
						if (dock_inner.getSelectedItemPosition() == 0) {
							params.add(new BasicNameValuePair("dock", null));
						} else {
							dockid = SystemSetting.getBaseInfoDockId(dock_inner.getSelectedItemPosition() - 1);
							params.add(new BasicNameValuePair("dock", dockid));
						}
						Spinner berth_inner = (Spinner) findViewById(R.id.selectship_bowei_spinner);
						if (berth_inner.getSelectedItemPosition() == 0 || dockid == null) {
							params.add(new BasicNameValuePair("berth", null));
						} else {
							params.add(new BasicNameValuePair("berth", SystemSetting.getBaseInfoBerthId(dockid,
									berth_inner.getSelectedItemPosition() - 1)));
						}
						params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));

						// 类型（船舶动态0、梯口管理1、巡查巡检2、梯口管理3---用于梯口管理，记录上下轮物品的查询船舶）
						if (tk_good_flag) {
							HashMap<String, Object> bindData = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER + "");
							if (bindData != null) {
								params.add(new BasicNameValuePair("kkid", (String) bindData.get("id")));
							}
							params.add(new BasicNameValuePair("type", "3"));
						} else {
							params.add(new BasicNameValuePair("type", bindType + ""));
						}
						params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					} else {
						url = "getServiceShipList";
						params.add(new BasicNameValuePair("shipName", ((EditText) findViewById(R.id.selectship_name_shewai)).getText().toString()));
						Spinner purpose_inner = (Spinner) findViewById(R.id.selectship_purpose_spinner);
						if (purpose_inner.getSelectedItemPosition() == 0) {
							params.add(new BasicNameValuePair("shipPurpose", null));
						} else {
							params.add(new BasicNameValuePair("shipPurpose", DataDictionary.getDataDictionaryCodeByIndex(
									purpose_inner.getSelectedItemPosition() - 1, DataDictionary.DATADICTIONARY_TYPE_SHIP_PURPOSE)));
						}
						params.add(new BasicNameValuePair("shipMaster", ((EditText) findViewById(R.id.selectship_master_shewai)).getText().toString()));
						params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					}
					progressDialog = new ProgressDialog(CfzgSelectShipActivity.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();

					NetWorkManager.request(CfzgSelectShipActivity.this, url, params, HTTPREQUEST_TYPE_FOR_SELECTSHIP);
				}
			});
		}

		if (cfzgFlag) {// 来自船方自管
			setMyActiveTitle(getString(R.string.select_ship));
		}
		if (tk_good_flag) {// 来自卡口管理-上下物品,设置标题
			setMyActiveTitle(getText(R.string.kakoumanager) + ">" + getText(R.string.record_goods) + ">" + getString(R.string.select_ship));
			findViewById(R.id.select_rang).setVisibility(View.GONE);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

private	static class ViewHolder {
		private TextView index;

		private TextView name;

		private TextView en_name;

		private TextView country;

		private TextView protry;

		private Button operate;
	}

	/** 自定义适配器，用来显示卡口的查询结果 */
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (fromXunCha) {
				return xunjianShipList == null ? 0 : xunjianShipList.size();
			} else {
				return normalShipList == null ? 0 : normalShipList.size();
			}
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
			/** 执行卡口绑定 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				if (fromBindShip) {
					if (position % 2 == 0) {
						// 动作
						if (bindType == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
							String url = "buildKkRelation";
							if (progressDialog != null) {
								return;
							}
							bindMap = normalShipList.get(position / 2);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "1"));
							params.add(new BasicNameValuePair("kkID", (String) bindMap.get("id")));
							params.add(new BasicNameValuePair("bindType", bindType + ""));
							//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_KK + ""));

							progressDialog = new ProgressDialog(CfzgSelectShipActivity.this);
							progressDialog.setTitle(getString(R.string.waiting));
							progressDialog.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(CfzgSelectShipActivity.this, url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
						}
					}
				}
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				if (fromBindShip) {
					convertView = mInflater.inflate(R.layout.kk_listview_class, null);
					holder.index = (TextView) convertView.findViewById(R.id.index);
					holder.name = (TextView) convertView.findViewById(R.id.name);
					holder.en_name = (TextView) convertView.findViewById(R.id.rang);
					holder.country = (TextView) convertView.findViewById(R.id.addr);
					holder.operate = (Button) convertView.findViewById(R.id.operate_btn);
					holder.operate.setOnClickListener(clickListener);
				}
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (fromBindShip) {
				holder.operate.setTag(position * 2);
			}
			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (fromBindShip) {
				if (holder.name != null) {
					holder.name.setText((String) normalShipList.get(position).get("kkmc"));
				}
				if (holder.en_name != null) {
					holder.en_name.setText((String) normalShipList.get(position).get("kkfw"));
				}
				if (holder.country != null) {
					holder.country.setText((String) normalShipList.get(position).get("kkxx"));
				}
				if (holder.protry != null) {
					String cbxz_str = (String) normalShipList.get(position).get("cbxz");
					if (cbxz_str == null || cbxz_str.length() == 0) {
						holder.protry.setText("");
					} else {
						holder.protry.setText(DataDictionary.getDataDictionaryName(cbxz_str, DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
					}
				}
				if (normalShipList.get(position).get("bdzt") == null || ((String) normalShipList.get(position).get("bdzt")).equals("未绑定")) {
					holder.operate.setText("绑定");
					holder.operate.setEnabled(true);
				} else {
					holder.operate.setText("已绑定");
					holder.operate.setEnabled(false);
					SystemSetting.setBindShip(normalShipList.get(position), bindType + "");
				}
			}
			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		if (fromXunCha) {
			xunjianShipList = null;
		} else {
			normalShipList = null;
		}
		super.onDestroy();
	}

	/** 处理从结果显示界面返回后，根据来自不同的模块，返回不同的数据 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_SHIP_RESULT:
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (resultCode == RESULT_OK) {
				if (data.getIntExtra("type", 0) == CfzgSelectShipResult.BIND_SHIP) {
					Intent intent = null;
					intent = new Intent();
					setResult(RESULT_OK, intent);
				} else if (data.getIntExtra("type", 0) == CfzgSelectShipResult.SELECT_SHIP) {
					Intent intent = null;
					intent = new Intent();
					intent.putExtra("shipname", data.getStringExtra("shipname"));
					intent.putExtra("shipengname", data.getStringExtra("shipengname"));
					intent.putExtra("shipid", data.getStringExtra("shipid"));
					intent.putExtra("shiptype", data.getStringExtra("shiptype"));
					intent.putExtra("tkmt", data.getStringExtra("tkmt"));
					intent.putExtra("tkbw", data.getStringExtra("tkbw"));
					intent.putExtra("tkwz", data.getStringExtra("tkwz"));
					intent.putExtra("gj", data.getStringExtra("gj"));
					setResult(RESULT_OK, data);
				}
				finish();
			} else {
				if (fromBindShip) {
					HashMap<String, Object> Binddata = SystemSetting.getBindShip(bindType + "");
					if (Binddata != null) {
						Intent intent = null;
						intent = new Intent();
						setResult(RESULT_OK, intent);
						finish();
					}
				}
			}
			break;
		case FlagManagers.CUSTOM_DIALOG_FOR_EXIT:
			if (resultCode == RESULT_OK) {
				String password = data.getStringExtra("password");
				this.validatePassword(password);
			}
			break;
		}
	}

	/** 处理查询结果，如果是查询卡口，就直接显示，如果是查询船舶，就以一个新的界面显示 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_SELECTSHIP) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				if (onParseXMLData(str)) {
					if (adapter.getCount() == 0) {
						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;
						}
						HgqwToast.makeText(CfzgSelectShipActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					} else {
						Intent intent = new Intent();
						intent.putExtra("frombindship", fromBindShip);
						intent.putExtra("fromxuncha", fromXunCha);
						intent.putExtra("fromglcb", fromGlcb);
						intent.putExtra("bindtype", bindType);
						intent.putExtra("shewai", selType != SELECT_SHIP_TYPE_WARD);
						intent.putExtra("cfzgFlag", cfzgFlag);
						intent.setClass(getApplicationContext(), CfzgSelectShipResult.class);
						startActivityForResult(intent, STARTACTIVITY_FOR_SHIP_RESULT);
					}
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(CfzgSelectShipActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(CfzgSelectShipActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				HgqwToast.makeText(CfzgSelectShipActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}
		} else if (HTTPREQUEST_TYPE_FOR_BINDSHIP == httpRequestType) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				bindMap.put("bdzt", "已绑定");
				SystemSetting.setBindShip(bindMap, bindType + "");
				HgqwToast.makeText(CfzgSelectShipActivity.this, R.string.bindship_success, HgqwToast.LENGTH_LONG).show();
				/*
				 * Intent data = null; data = new Intent(); setResult(RESULT_OK, data); finish();
				 */

				// 直接跳回二级页面
				Intent intent = new Intent();
				if (bindType == 0) {
					intent.setClass(getApplicationContext(), CfzgShipStatus.class);
				} else if (bindType == 1) {
//					intent.setClass(getApplicationContext(), TikouManagerActivity.class);
				} else if (bindType == 3) {
//					intent.setClass(getApplicationContext(), KakouManagerActivity.class);
				} else if (bindType == 2) {
//					intent.setClass(getApplicationContext(), XunchaxunjianActivity.class);
				}
				startActivity(intent);
			} else {
				HgqwToast.makeText(CfzgSelectShipActivity.this, R.string.bindship_failure, HgqwToast.LENGTH_LONG).show();
				SystemSetting.setBindShip(null, bindType + "");
			}
		} else if (HTTPREQUEST_TYPE_FOR_SELECTKK == httpRequestType) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				onParseXMLData(str);
				if (adapter.getCount() > 0) {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
					}
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(View.GONE);
					}
					if (listView != null) {
						listView.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				} else {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(View.GONE);
					}

					if (listView != null) {
						listView.setVisibility(View.GONE);
					}
					if (httpReturnXMLInfo != null) {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty).setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty)).setText(httpReturnXMLInfo);
						}
						HgqwToast.makeText(CfzgSelectShipActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
					} else {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty).setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
						}
						HgqwToast.makeText(CfzgSelectShipActivity.this, R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline).setVisibility(View.GONE);
				}
				if (listView != null) {
					listView.setVisibility(View.GONE);
				}
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.select_result_empty)).setText(R.string.data_download_failure_info);
				}
				HgqwToast.makeText(CfzgSelectShipActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}
		} else if (httpRequestType == FlagUrls.VALIDATE_PASSWORD) {
			if (str != null) {
				if ("success".equals(str)) {
					// 密码验证成功，模拟调用Home键
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
				} else {
					HgqwToast.getToastView(getApplicationContext(), "密码错误！").show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
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
							if (fromXunCha) {
								if (xunjianShipList == null) {
									xunjianShipList = new ArrayList<HashMap<String, Object>>();
								} else {
									xunjianShipList.clear();
								}
							} else {
								if (normalShipList == null) {
									normalShipList = new ArrayList<HashMap<String, Object>>();
								} else {
									normalShipList.clear();
								}
							}
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (!success) {
							httpReturnXMLInfo = parser.nextText();
						} else {
							map = new HashMap<String, Object>();
						}
					} else if ("hc".equals(parser.getName())) {
						map.put("hc", parser.nextText());
					} else if ("bdzt".equals(parser.getName())) {
						String bdzt = parser.nextText();
						if (map.get("bdzt") == null) {
							map.put("bdzt", bdzt);
						}
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
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
					} else if ("cjsj".equals(parser.getName())) {
						map.put("cjsj", parser.nextText());
					} else if ("ss".equals(parser.getName())) {
						map.put("ssdw", parser.nextText());
					} else if ("cbzyyt".equals(parser.getName())) {
						map.put("cbzyyt", parser.nextText());
					} else if ("cbmc".equals(parser.getName())) {
						map.put("cbzwm", parser.nextText());
					} else if ("czmc".equals(parser.getName())) {
						map.put("czmc", parser.nextText());
					} else if ("cardNumber".equals(parser.getName())) {
						map.put("cardNumber", parser.nextText());
					} else if ("kkmc".equals(parser.getName())) {
						map.put("kkmc", parser.nextText());
					} else if ("kkfw".equals(parser.getName())) {
						map.put("kkfw", parser.nextText());
					} else if ("kkxx".equals(parser.getName())) {
						map.put("kkxx", parser.nextText());
					} else if ("tkmt".equals(parser.getName())) {
						map.put("tkmt", parser.nextText());
					} else if ("tkbw".equals(parser.getName())) {
						map.put("tkbw", parser.nextText());
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
							map.put("kacbzt", "");
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (fromBindShip && (bindType == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER)) {
								if (map.get("id") != null) {
									if (fromXunCha) {
										if (xunjianShipList == null) {
											xunjianShipList = new ArrayList<HashMap<String, Object>>();
										}
										xunjianShipList.add(map);
									} else {
										if (normalShipList == null) {
											normalShipList = new ArrayList<HashMap<String, Object>>();
										}
										normalShipList.add(map);
									}
								}
							} else {
								if (map.get("cbzwm") != null) {
									if (fromXunCha) {
										if (xunjianShipList == null) {
											xunjianShipList = new ArrayList<HashMap<String, Object>>();
										}
										if (selType == SELECT_SHIP_TYPE_WARD) {
											map.put("cblx", "ward");
										} else {
											map.put("cblx", "service");
										}
										xunjianShipList.add(map);
									} else {
										if (normalShipList == null) {
											normalShipList = new ArrayList<HashMap<String, Object>>();
										}
										if (selType == SELECT_SHIP_TYPE_WARD) {
											map.put("cblx", "ward");
										} else {
											map.put("cblx", "service");
										}
										normalShipList.add(map);
									}
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

	/* 监听物理按键 */
	/*@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// return super.onKeyDown(keyCode, event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_HOME:
		/*	if (cfzgFlag) {
				dialogActivityForExit();
			} else {
				return super.onKeyDown(keyCode, event);
			}
			break;*/
			return super.onKeyDown(keyCode, event);
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @方法名：dialogActivity
	 * @功能说明：弹出Home键验证对话框
	 * @author liums
	 * @date 2013-5-7 下午5:34:08
	 */
	private void dialogActivityForExit() {
		Intent intent = new Intent();

		intent.setClass(getApplicationContext(), CfzgCustomDialogForExit.class);
		intent.putExtra("cn", true);
		startActivityForResult(intent, FlagManagers.CUSTOM_DIALOG_FOR_EXIT);
	}

	/**
	 * @方法名：validatePassword
	 * @功能说明：验证当前用户密码
	 * @author liums
	 * @date 2013-5-7 下午5:42:16
	 * @param password
	 * @return
	 */
	private void validatePassword(String password) {
		String url = "validatePassword";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", LoginUser.getCurrentLoginUser().getUserName()));
		params.add(new BasicNameValuePair("password", password));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.setButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!((ProgressDialog) dialog).isShowing()) {
					Log.i(TAG, "!((ProgressDialog)dialog).isShowing()");
					progressDialog = null;
					return;
				}
				Log.i(TAG, "progressDialog onClick");
				dialog.dismiss();
				progressDialog = null;
			}
		});
		progressDialog.show();
		NetWorkManager.request(this, url, params, FlagUrls.VALIDATE_PASSWORD);
	}
}
