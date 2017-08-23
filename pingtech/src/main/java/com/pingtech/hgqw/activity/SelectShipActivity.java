package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
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
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.activity.ReadSlotCardActivity;
import com.pingtech.hgqw.module.bindship.activity.ShipBind;
import com.pingtech.hgqw.module.cbdt.activity.ShipStatus;
import com.pingtech.hgqw.module.kakou.activity.KaKouReadCard;
import com.pingtech.hgqw.module.kakou.activity.KakouManager;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.fwxcb.action.FwxcbAction;
import com.pingtech.hgqw.module.offline.kacbqk.action.KacbqkAction;
import com.pingtech.hgqw.module.offline.qyxx.action.QyxxAction;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.tikou.activity.TikouManager;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownloadForBd;
import com.pingtech.hgqw.module.xunjian.activity.XunChaXunJian;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 查询船舶（卡口）模块，根据船舶名称模糊查询、根据区域、泊位、码头等任意搭配查询
 */
public class SelectShipActivity extends MyActivity implements OnHttpResult,
		OffLineResult {
	private static final String TAG = "SelectShipActivity";

	/**
	 * 是否有已经绑定的船舶：false没有，true有
	 */
	// private boolean hasBindFlag = false;
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

	/**
	 * 卡口绑定
	 */
	private static final int REQUEST_TYPE_FOR_KKBIND = 8;

	private ListView listView;

	/** 绑定类型，来自船舶动态？梯口管理？卡口管理？巡查巡检？ */
	private int bindType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;

	private MyAdapter adapter;

	private ArrayAdapter<String> spinnerAdapter;

	private Spinner spinner;

	private int selType = SELECT_SHIP_TYPE_WARD;

	/** 用于保存通常查询结果信息 */
	public static ArrayList<HashMap<String, Object>> normalShipList = null;

	/** 用于保存从巡查巡检模块进入查询船舶的查询结果信息，单独定义一个list是为了防止由此进入查看执勤人员及异常信息，再进入查询船舶时，查询结果冲突 */
	public static ArrayList<HashMap<String, Object>> xunjianShipList = null;

	//
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

	/** 是否来自梯口快速核查 */
	private boolean fromTkkshc = false;

	/** 是否来自卡口快速核查 */
	private boolean fromKkkshc = false;
	
	/** 是否来自巡查巡检快速核查 */
	private boolean fromXcxjkshc = false;

	private HashMap<String, Object> bindMap;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	/**
	 * 梯口管理的物品检查标志位：true ，false
	 */
	private boolean tk_good_flag = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");

		Intent intent = getIntent();
		fromTkkshc = intent.getBooleanExtra("fromtkkshc", false);
		fromKkkshc = intent.getBooleanExtra("fromkkkshc", false);
		fromXcxjkshc = intent.getBooleanExtra("fromXCXJkshc", false);
		fromBindShip = intent.getBooleanExtra("frombindship", false);
		fromXunCha = intent.getBooleanExtra("fromxuncha", false);
		fromCgcs = intent.getBooleanExtra("fromecgcs", false);
		fromGlcb = intent.getBooleanExtra("fromglcb", false);
		bindType = intent.getIntExtra("bindtype", 0);
		cfzgFlag = intent.getBooleanExtra("cfzgFlag", false);
		tk_good_flag = intent.getBooleanExtra("tk_good_flag", false);
		super.onCreate(savedInstanceState, R.layout.selectship);

		// 梯口刷卡按钮
		Button slotCardButton = (Button) findViewById(R.id.select_ship_slotcard);

		Log.i(TAG, "onCreate()");
		if (fromTkkshc || fromXcxjkshc) {
			slotCardButton.setVisibility(View.VISIBLE);
		} else {
			slotCardButton.setVisibility(View.GONE);
		}

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
			if (bindType == GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.select_ship));
			} else if (bindType == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
				if (fromTkkshc) {
					setMyActiveTitle(getString(R.string.tikoumanager) + ">"
							+ getString(R.string.quickCheck) + ">"
							+ getString(R.string.select_ship));
				} else {
					setMyActiveTitle(getString(R.string.tikoumanager) + ">"
							+ getString(R.string.bindShip) + ">"
							+ getString(R.string.select_ship));
				}

			} else if (bindType == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.select_ship));
			} else if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {

				if (fromKkkshc) {
					setMyActiveTitle(getString(R.string.kakoumanager) + ">"
							+ getString(R.string.quickCheck) + ">"
							+ getString(R.string.select_kakou));
					setContentView(R.layout.selectkk);
				} else {
					setMyActiveTitle(getString(R.string.kakoumanager) + ">"
							+ getString(R.string.kakou_band) + ">"
							+ getString(R.string.select_kakou));
					setContentView(R.layout.selectkk);
				}

			}
			if (bindType != GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				findViewById(R.id.select_rang).setVisibility(View.GONE);

			}
		} else if (fromXunCha) {
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
					+ getString(R.string.select_ship));
			findViewById(R.id.select_rang).setVisibility(View.GONE);
		} else {
			setMyActiveTitle(getString(R.string.exception_info) + ">"
					+ getString(R.string.select_ship));
			if (fromGlcb || fromCgcs) {
				findViewById(R.id.select_rang).setVisibility(View.GONE);
			}
		}
		if (fromXunCha) {
			if (findViewById(R.id.sel_ship_list_title_bind) != null) {
				findViewById(R.id.sel_ship_list_title_bind).setVisibility(
						View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
				findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(
						View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
				findViewById(R.id.sel_ship_list_title_shewai).setVisibility(
						View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
				findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(
						View.VISIBLE);
			}
		} else if (fromBindShip) {
			if (bindType != GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				if (findViewById(R.id.sel_ship_list_title_bind) != null) {
					findViewById(R.id.sel_ship_list_title_bind).setVisibility(
							View.VISIBLE);
				}
				if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
					findViewById(R.id.sel_ship_list_title_jianhu)
							.setVisibility(View.GONE);
				}
				if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
					findViewById(R.id.sel_ship_list_title_shewai)
							.setVisibility(View.GONE);
				}
				if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
					findViewById(R.id.sel_ship_list_title_xuncha)
							.setVisibility(View.GONE);
				}
			}
		} else {
			if (findViewById(R.id.sel_ship_list_title_bind) != null) {
				findViewById(R.id.sel_ship_list_title_bind).setVisibility(
						View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
				findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(
						View.VISIBLE);
			}
			if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
				findViewById(R.id.sel_ship_list_title_shewai).setVisibility(
						View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
				findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(
						View.GONE);
			}
		}

		// 卡口刷卡按钮
		Button slotCardKKButton = (Button) findViewById(R.id.select_slotcard_kk);

		if (slotCardKKButton != null) {
			if (fromKkkshc) {
				slotCardKKButton.setVisibility(View.VISIBLE);
				
				slotCardKKButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_KAKOUMANAGER);
						intent.setClass(getApplicationContext(), ReadSlotCardActivity.class);
						startActivity(intent);
					}
				});
			} else {
				slotCardKKButton.setVisibility(View.GONE);
			}
		}

		adapter = new MyAdapter(this);

		listView = (ListView) findViewById(R.id.listview);
		if (listView != null) {
			listView.setAdapter(adapter);
		}
		if (fromBindShip
				&& (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER)) {
			Button submitbtn = (Button) findViewById(R.id.select_submit_kk);
			submitbtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String url = "getKkInfo";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					String kkmc = ((EditText) findViewById(R.id.select_kk_name))
							.getText().toString();
					params.add(new BasicNameValuePair("kkmc", kkmc));
					params.add(new BasicNameValuePair("cardNumber", ""));
					params.add(new BasicNameValuePair("PDACode", SystemSetting
							.getPDACode()));
					params.add(new BasicNameValuePair("bindType",
							GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + ""));
					if (progressDialog != null) {
						return;
					}
					progressDialog = new ProgressDialog(SelectShipActivity.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					if (!getState(FunctionSetting.kqlx, false)) {
						NetWorkManager.request(SelectShipActivity.this, url,
								params, HTTPREQUEST_TYPE_FOR_SELECTKK);
					} else {
						OffLineManager.request(SelectShipActivity.this,
								new QyxxAction(), url,
								NVPairTOMap.nameValuePairTOMap(params),
								HTTPREQUEST_TYPE_FOR_SELECTKK);
					}
				}
			});
		} else {
			RadioGroup sel_type_rg = (RadioGroup) findViewById(R.id.select_ship_type);
			sel_type_rg
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							if (checkedId == R.id.radio_btn_jh) {
								if (findViewById(R.id.select_ship_jianhu) != null) {
									findViewById(R.id.select_ship_jianhu)
											.setVisibility(View.VISIBLE);
								}
								if (findViewById(R.id.select_ship_shewai) != null) {
									findViewById(R.id.select_ship_shewai)
											.setVisibility(View.GONE);
								}
								selType = SELECT_SHIP_TYPE_WARD;
							} else if (checkedId == R.id.radio_btn_sw) {
								if (findViewById(R.id.select_ship_jianhu) != null) {
									findViewById(R.id.select_ship_jianhu)
											.setVisibility(View.GONE);
								}
								if (findViewById(R.id.select_ship_shewai) != null) {
									findViewById(R.id.select_ship_shewai)
											.setVisibility(View.VISIBLE);
								}
								selType = SELECT_SHIP_TYPE_SERVICE;
							}
						}
					});
			sel_type_rg.check(R.id.radio_btn_jh);
			spinner = (Spinner) findViewById(R.id.selectship_property_spinner);
			List<String> list = new ArrayList<String>(Arrays.asList("请选择"));
			list.addAll(
					1,
					DataDictionary
							.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
			spinnerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list);
			spinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(spinnerAdapter);
			spinner = (Spinner) findViewById(R.id.selectship_purpose_spinner);
			list = new ArrayList<String>(Arrays.asList("请选择"));
			list.addAll(
					1,
					DataDictionary
							.getDataDictionaryNameList(DataDictionary.DATADICTIONARY_TYPE_SHIP_PURPOSE));
			spinnerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list);
			spinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(spinnerAdapter);
			spinner = (Spinner) findViewById(R.id.selectship_matou_spinner);
			list = new ArrayList<String>(Arrays.asList("请选择"));
			list.addAll(1, SystemSetting.getBaseInfoDockList());
			spinnerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list);
			spinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(spinnerAdapter);
			spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					String dockid;
					List<String> list = new ArrayList<String>(Arrays
							.asList("请选择"));
					if (arg2 == 0) {
					} else {
						dockid = SystemSetting.getBaseInfoDockId(arg2 - 1);
						list.addAll(1,
								SystemSetting.getBaseInfoBerthList(dockid));
					}
					spinner = (Spinner) findViewById(R.id.selectship_bowei_spinner);
					spinnerAdapter = new ArrayAdapter<String>(
							SelectShipActivity.this,
							android.R.layout.simple_spinner_item, list);
					spinnerAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
					BaseAction action = null;
					String url;
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					if (selType == SELECT_SHIP_TYPE_WARD) {
						action = new KacbqkAction();
						url = "getWardShipList";
						params.add(new BasicNameValuePair(
								"shipName",
								((EditText) findViewById(R.id.selectship_name_jianhu))
										.getText().toString()));
						Spinner Quality_inner = (Spinner) findViewById(R.id.selectship_property_spinner);
						if (Quality_inner.getSelectedItemPosition() == 0) {
							params.add(new BasicNameValuePair("shipQuality",
									null));
						} else {
							params.add(new BasicNameValuePair(
									"shipQuality",
									DataDictionary.getDataDictionaryCodeByIndex(
											Quality_inner
													.getSelectedItemPosition() - 1,
											DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)));
						}
						Spinner dock_inner = (Spinner) findViewById(R.id.selectship_matou_spinner);
						String dockid = null;
						if (dock_inner.getSelectedItemPosition() == 0) {
							params.add(new BasicNameValuePair("dock", null));
						} else {
							dockid = SystemSetting.getBaseInfoDockId(dock_inner
									.getSelectedItemPosition() - 1);
							params.add(new BasicNameValuePair("dock", dockid));
						}
						Spinner berth_inner = (Spinner) findViewById(R.id.selectship_bowei_spinner);
						if (berth_inner.getSelectedItemPosition() == 0
								|| dockid == null) {
							params.add(new BasicNameValuePair("berth", null));
						} else {
							params.add(new BasicNameValuePair(
									"berth",
									SystemSetting
											.getBaseInfoBerthId(
													dockid,
													berth_inner
															.getSelectedItemPosition() - 1)));
						}
						params.add(new BasicNameValuePair("PDACode",
								SystemSetting.getPDACode()));

						// 类型（船舶动态0、梯口管理1、巡查巡检2、卡口管理3---用于卡口管理，记录上下轮物品的查询船舶）
						if (tk_good_flag) {
							HashMap<String, Object> bindData = SystemSetting
									.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER
											+ "");
							if (bindData != null) {
								params.add(new BasicNameValuePair("kkid",
										(String) bindData.get("id")));
							}
							params.add(new BasicNameValuePair("type", "3"));
						} else {
							params.add(new BasicNameValuePair("type", bindType
									+ ""));
						}
						params.add(new BasicNameValuePair("userID", LoginUser
								.getCurrentLoginUser().getUserID()));
					} else {
						action = new FwxcbAction();
						url = "getServiceShipList";
						params.add(new BasicNameValuePair(
								"shipName",
								((EditText) findViewById(R.id.selectship_name_shewai))
										.getText().toString()));
						Spinner purpose_inner = (Spinner) findViewById(R.id.selectship_purpose_spinner);
						if (purpose_inner.getSelectedItemPosition() == 0) {
							params.add(new BasicNameValuePair("shipPurpose",
									null));
						} else {
							params.add(new BasicNameValuePair(
									"shipPurpose",
									DataDictionary.getDataDictionaryCodeByIndex(
											purpose_inner
													.getSelectedItemPosition() - 1,
											DataDictionary.DATADICTIONARY_TYPE_SHIP_PURPOSE)));
						}
						params.add(new BasicNameValuePair(
								"shipMaster",
								((EditText) findViewById(R.id.selectship_master_shewai))
										.getText().toString()));
						params.add(new BasicNameValuePair("userID", LoginUser
								.getCurrentLoginUser().getUserID()));
					}
					progressDialog = new ProgressDialog(SelectShipActivity.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					// if (!getState(FunctionSetting.kqlx, false)) {
					NetWorkManager.request(SelectShipActivity.this, url,
							params, HTTPREQUEST_TYPE_FOR_SELECTSHIP);
					// } else {
					// OffLineManager.request(SelectShipActivity.this, action,
					// url, NVPairTOMap.nameValuePairTOMap(params),
					// HTTPREQUEST_TYPE_FOR_SELECTSHIP);
					// }

				}
			});

			// 梯口刷卡
			slotCardButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					Intent intent = new Intent();
					if(fromTkkshc){
						intent.putExtra("from",
								GlobalFlags.BINDSHIP_FROM_TIKOUMANAGER);
						intent.setClass(getApplicationContext(),
								ReadSlotCardActivity.class);
					}
					
					if(fromXcxjkshc){
						intent.putExtra("from", GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN);
						intent.setClass(getApplicationContext(), ReadSlotCardActivity.class);
					}
					startActivity(intent);
				}
			});

		}

		if (cfzgFlag) {// 来自船方自管
			setMyActiveTitle(getString(R.string.select_ship));
		}
		if (tk_good_flag) {// 来自卡口管理-上下物品,设置标题
			setMyActiveTitle(getText(R.string.kakoumanager) + ">"
					+ getText(R.string.record_goods) + ">"
					+ getString(R.string.select_ship));
			findViewById(R.id.select_rang).setVisibility(View.GONE);
		}
	}

	private void isBind() {
		if (!fromBindShip) {
			return;
		}
		if (normalShipList != null) {
			for (HashMap<String, Object> map : normalShipList) {
				String isbind = (String) map.get("bdzt");
				if (!"未绑定".equals(isbind)) {
					showBondDialog();
					SystemSetting.setBindShip(map, bindType + "");
					break;
				}
			}
		} else if (xunjianShipList != null) {
			for (HashMap<String, Object> map : xunjianShipList) {
				String isbind = (String) map.get("bdzt");
				if (!"未绑定".equals(isbind)) {
					showBondDialog();
					SystemSetting.setBindShip(map, bindType + "");
					break;
				}
			}
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	static class ViewHolder {
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
						if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
							String url = "buildKkRelation";
							if (progressDialog != null) {
								return;
							}
							bindMap = normalShipList.get(position / 2);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("userID",
									LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode",
									SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "1"));
							params.add(new BasicNameValuePair("kkID",
									(String) bindMap.get("id")));
							params.add(new BasicNameValuePair("bindType",
									bindType + ""));
							// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx",
									GlobalFlags.ZQDXLX_KK + ""));

							progressDialog = new ProgressDialog(
									SelectShipActivity.this);
							progressDialog
									.setTitle(getString(R.string.waiting));
							progressDialog
									.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();

							if (!getState(FunctionSetting.kqlx, false)) {
								NetWorkManager.request(SelectShipActivity.this,
										url, params,
										HTTPREQUEST_TYPE_FOR_BINDSHIP);
							} else {
								OffLineManager.request(SelectShipActivity.this,
										new KacbqkAction(), url,
										NVPairTOMap.nameValuePairTOMap(params),
										REQUEST_TYPE_FOR_KKBIND);
							}

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
					convertView = mInflater.inflate(R.layout.kk_listview_class,
							null);
					holder.index = (TextView) convertView
							.findViewById(R.id.index);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					holder.en_name = (TextView) convertView
							.findViewById(R.id.rang);
					holder.country = (TextView) convertView
							.findViewById(R.id.addr);
					holder.operate = (Button) convertView
							.findViewById(R.id.operate_btn);
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
					holder.name.setText((normalShipList.get(position).get(
							"kkmc") == null ? "" : (String) normalShipList.get(
							position).get("kkmc")));
				}
				if (holder.en_name != null) {
					holder.en_name.setText((normalShipList.get(position).get(
							"kkfw") == null ? "" : (String) normalShipList.get(
							position).get("kkfw")));
				}
				if (holder.country != null) {
					holder.country.setText(normalShipList.get(position).get(
							"kkxx") == null ? "" : (String) normalShipList.get(
							position).get("kkxx"));
				}
				if (holder.protry != null) {
					String cbxz_str = (String) normalShipList.get(position)
							.get("cbxz");
					if (cbxz_str == null || cbxz_str.length() == 0) {
						holder.protry.setText("");
					} else {
						holder.protry
								.setText(DataDictionary
										.getDataDictionaryName(
												cbxz_str,
												DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
					}
				}
				if (normalShipList.get(position).get("bdzt") == null
						|| ((String) normalShipList.get(position).get("bdzt"))
								.equals("未绑定")) {
					holder.operate.setText("绑定");
					holder.operate.setEnabled(true);
				} else {
					holder.operate.setText("已绑定");
					holder.operate.setEnabled(false);
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
				if (data.getIntExtra("type", 0) == SelectShipResultActivity.BIND_SHIP) {
					Intent intent = null;
					intent = new Intent();
					setResult(RESULT_OK, intent);
				} else if (data.getIntExtra("type", 0) == SelectShipResultActivity.SELECT_SHIP) {
					Intent intent = null;
					intent = new Intent();
					intent.putExtra("shipname", data.getStringExtra("shipname"));
					intent.putExtra("shipengname",
							data.getStringExtra("shipengname"));
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
					HashMap<String, Object> Binddata = SystemSetting
							.getBindShip(bindType + "");
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
						HgqwToast.makeText(SelectShipActivity.this,
								R.string.no_data, HgqwToast.LENGTH_LONG).show();
					} else {
						Intent intent = new Intent();
						intent.putExtra("frombindship", fromBindShip);
						intent.putExtra("fromxuncha", fromXunCha);
						intent.putExtra("fromglcb", fromGlcb);
						intent.putExtra("bindtype", bindType);
						intent.putExtra("shewai",
								selType != SELECT_SHIP_TYPE_WARD);
						intent.putExtra("cfzgFlag", cfzgFlag);
						intent.putExtra("fromtkkshc", fromTkkshc);
						intent.putExtra("fromXCXJkshc", fromXcxjkshc);
						intent.setClass(getApplicationContext(),
								SelectShipResultActivity.class);
						startActivityForResult(intent,
								STARTACTIVITY_FOR_SHIP_RESULT);
					}
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(SelectShipActivity.this,
								httpReturnXMLInfo, HgqwToast.LENGTH_LONG)
								.show();
					} else {
						HgqwToast.makeText(SelectShipActivity.this,
								R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				HgqwToast.makeText(SelectShipActivity.this,
						R.string.data_download_failure_info,
						HgqwToast.LENGTH_LONG).show();
			}
		} else if (HTTPREQUEST_TYPE_FOR_BINDSHIP == httpRequestType) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				bindMap.put("bdzt", "已绑定");
				SystemSetting.setBindShip(bindMap, bindType + "");
				HgqwToast.makeText(SelectShipActivity.this,
						R.string.bindship_success, HgqwToast.LENGTH_LONG)
						.show();
				// 绑定卡口后下载离线数据
				if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					downloadOfflineData(bindMap);
					if (fromKkkshc) {
						Intent intent = new Intent();
						HashMap<String, Object> bindData = SystemSetting
								.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER
										+ "");
						if (bindData == null) {
							HgqwToast.makeText(SelectShipActivity.this,
									R.string.no_bindkakou,
									HgqwToast.LENGTH_LONG).show();
							return;
						}
						intent.putExtra("hc", (String) bindData.get("id"));
						intent.putExtra("kkmc", (String) bindData.get("kkmc"));
						intent.putExtra("title",
								getString(R.string.kakoumanager) + ">"
										+ getString(R.string.paycard));
						intent.putExtra("cardtype",
								KaKouReadCard.READCARD_TYPE_ID_CARD);
						intent.putExtra("from", "01");
						intent.setClass(getApplicationContext(),
								KaKouReadCard.class);
						startActivity(intent);
						// startActivityForResult(intent,
						// STARTACTIVITY_FOR_READICCARD);
					}
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					Intent intent = new Intent();
					if (bindType == 0) {
						intent.setClass(getApplicationContext(),
								ShipStatus.class);
					} else if (bindType == 1) {
						intent.setClass(getApplicationContext(),
								TikouManager.class);
					} else if (bindType == 3) {
						intent.setClass(getApplicationContext(),
								KakouManager.class);
					} else if (bindType == 2) {
						intent.setClass(getApplicationContext(),
								XunChaXunJian.class);
					}
					startActivity(intent);
				}

				// 直接跳回二级页面
			} else if (str != null && "3".equals(str)) {
				HgqwToast.makeText(SelectShipActivity.this,
						R.string.had_bind_ship, HgqwToast.LENGTH_LONG).show();
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(SelectShipActivity.this,
						R.string.bindship_failure, HgqwToast.LENGTH_LONG)
						.show();
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
						findViewById(R.id.listview_topline).setVisibility(
								View.VISIBLE);
					}
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(
								View.GONE);
					}
					if (listView != null) {
						listView.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				} else {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(
								View.GONE);
					}

					if (listView != null) {
						listView.setVisibility(View.GONE);
					}
					if (httpReturnXMLInfo != null) {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty)
									.setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty))
									.setText(httpReturnXMLInfo);
						}
						HgqwToast.makeText(SelectShipActivity.this,
								httpReturnXMLInfo, HgqwToast.LENGTH_LONG)
								.show();
					} else {
						if (findViewById(R.id.select_result_empty) != null) {
							findViewById(R.id.select_result_empty)
									.setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.select_result_empty))
									.setText(R.string.no_data);
						}
						HgqwToast.makeText(SelectShipActivity.this,
								R.string.no_data, HgqwToast.LENGTH_LONG).show();
					}
				}
				isBind();
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline)
							.setVisibility(View.GONE);
				}
				if (listView != null) {
					listView.setVisibility(View.GONE);
				}
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(
							View.VISIBLE);
					((TextView) findViewById(R.id.select_result_empty))
							.setText(R.string.data_download_failure_info);
				}
				HgqwToast.makeText(SelectShipActivity.this,
						R.string.data_download_failure_info,
						HgqwToast.LENGTH_LONG).show();
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
					HgqwToast.getToastView(getApplicationContext(), "密码错误！")
							.show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(),
						getString(R.string.data_download_failure_info)).show();
			}
		}
	}

	private OffDataDownloadForBd dataDownload = null;

	private StringBuilder stringBuilder = new StringBuilder();

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case OffDataDownload.WHAT_DOWNLOAD_SUCCESS_ONE:// 下载完成一个
				// 码头、泊位、区域、船舶、证件、船员
				String str = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str + "，下载完成");
				stringBuilder.append("\n");
				progressDialog.setMessage(stringBuilder.toString());
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_NULL:// 下载失败一个
			case OffDataDownload.WHAT_INSERT_DATA_FAILED_ONE:// 下载失败一个
				// 码头、泊位、区域、船舶、证件、船员
				String str1 = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str1 + "，下载失败");
				stringBuilder.append("\n");
				progressDialog.setCancelable(true);
				progressDialog.setMessage(stringBuilder.toString());
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ALL_SUCCESS:// 下载完成
				HgqwToast.toast("下载完成");
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				finish();
				break;

			default:
				break;
			}

		}

	};

	private void downloadOfflineData(HashMap<String, Object> bindMap) {
		if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
			dataDownload = new OffDataDownloadForBd(handler, bindMap,
					OffLineUtil.DOWNLOAD_FOR_QYXX, 3);
		}
		dataDownload.requestAgain();
		// progressDialog = new ProgressDialog(getApplicationContext());
		progressDialog.setMessage("正在下载离线所需数据");
		// progressDialog.show();
	}

	/** 解析查询结果 */
	private boolean onParseXMLData(String str) {
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

						if (map.get("bdzt") == null) {
							String bdzt = parser.nextText();
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
						// 离线用到数据
					} else if ("dqjczt".equals(parser.getName())) {
						map.put("dqjczt", parser.nextText());
					} else if ("cys".equals(parser.getName())) {
						map.put("cys", parser.nextText());
					} else if ("cdgs".equals(parser.getName())) {
						map.put("cdgs", parser.nextText());
					} else if ("jcfl".equals(parser.getName())) {
						map.put("jcfl", parser.nextText());
					} else if ("kacbqkid".equals(parser.getName())) {
						map.put("kacbqkid", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (fromBindShip
									&& (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER)) {
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
	/*
	 * @Override public void onAttachedToWindow() {
	 * getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	 * super.onAttachedToWindow(); }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_HOME:
			/*
			 * if (cfzgFlag) { dialogActivityForExit(); } else { return
			 * super.onKeyDown(keyCode, event); } break;
			 */
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

		intent.setClass(getApplicationContext(), CustomDialogForExit.class);
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
	@SuppressWarnings("deprecation")
	private void validatePassword(String password) {
		String url = "validatePassword";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", LoginUser
				.getCurrentLoginUser().getUserName()));
		params.add(new BasicNameValuePair("password", password));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.setButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
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

	@Override
	public void offLineResult(Pair<Boolean, Object> obj, int httpRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_SELECTSHIP) {

			if (obj.second != null) {
				if (obj.second != null) {
					ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) obj.second;
					if (fromXunCha) {
						xunjianShipList = list;
					} else {
						normalShipList = list;
					}
				} else {
					HgqwToast.makeText(SelectShipActivity.this,
							R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}

				if (adapter.getCount() == 0) {
					HgqwToast.makeText(SelectShipActivity.this,
							R.string.no_data, HgqwToast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent();
					intent.putExtra("frombindship", fromBindShip);
					intent.putExtra("fromxuncha", fromXunCha);
					intent.putExtra("fromglcb", fromGlcb);
					intent.putExtra("bindtype", bindType);
					intent.putExtra("shewai", selType != SELECT_SHIP_TYPE_WARD);
					intent.putExtra("cfzgFlag", cfzgFlag);
					intent.setClass(getApplicationContext(),
							SelectShipResultActivity.class);
					startActivityForResult(intent,
							STARTACTIVITY_FOR_SHIP_RESULT);
				}

			} else {
				HgqwToast.makeText(SelectShipActivity.this,
						R.string.data_download_failure_info,
						HgqwToast.LENGTH_LONG).show();
			}
		} else if (HTTPREQUEST_TYPE_FOR_SELECTKK == httpRequestType) {
			if (obj.second != null) {
				if (obj.second != null) {
					ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) obj.second;
					if (fromXunCha) {
						xunjianShipList = list;
					} else {
						normalShipList = list;
					}
				} else {
					HgqwToast.makeText(SelectShipActivity.this,
							R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}

				if (adapter.getCount() > 0) {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(
								View.VISIBLE);
					}
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(
								View.GONE);
					}
					if (listView != null) {
						listView.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				} else {
					if (findViewById(R.id.listview_topline) != null) {
						findViewById(R.id.listview_topline).setVisibility(
								View.GONE);
					}
					if (listView != null) {
						listView.setVisibility(View.GONE);
					}
					if (findViewById(R.id.select_result_empty) != null) {
						findViewById(R.id.select_result_empty).setVisibility(
								View.VISIBLE);
						((TextView) findViewById(R.id.select_result_empty))
								.setText(R.string.no_data);
					}
					HgqwToast.makeText(SelectShipActivity.this,
							R.string.no_data, HgqwToast.LENGTH_LONG).show();
				}
			} else {
				if (findViewById(R.id.listview_topline) != null) {
					findViewById(R.id.listview_topline)
							.setVisibility(View.GONE);
				}
				if (listView != null) {
					listView.setVisibility(View.GONE);
				}
				if (findViewById(R.id.select_result_empty) != null) {
					findViewById(R.id.select_result_empty).setVisibility(
							View.VISIBLE);
					((TextView) findViewById(R.id.select_result_empty))
							.setText(R.string.no_data);
				}
				HgqwToast.makeText(SelectShipActivity.this, R.string.no_data,
						HgqwToast.LENGTH_LONG).show();
			}
		} else if (REQUEST_TYPE_FOR_KKBIND == httpRequestType) {
			HgqwToast.makeText(SelectShipActivity.this,
					R.string.no_web_cannot_bind_kakou, HgqwToast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 
	 * @description 显示已经绑定卡口的对话框
	 * @date 2014-4-8
	 * @author zhaotf
	 */
	private void showBondDialog() {

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setMessage(getString(R.string.tishi_content_kakou))
				.setCancelable(false)
				.setPositiveButton(R.string.queding,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								// 直接跳回二级页面
								arg0.cancel();
								// 直接跳回二级页面
								Intent intent = new Intent();
								if (bindType == 0) {
									intent.setClass(getApplicationContext(),
											ShipStatus.class);
								} else if (bindType == 1) {
									intent.setClass(getApplicationContext(),
											TikouManager.class);
								} else if (bindType == 3) {
									intent.setClass(getApplicationContext(),
											KakouManager.class);
								} else if (bindType == 2) {
									intent.setClass(getApplicationContext(),
											XunChaXunJian.class);
								}
								startActivity(intent);
							}
						}).setNegativeButton(R.string.cancel, new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.cancel();
							}
						}).create();
		alertDialog.show();
	}
}
