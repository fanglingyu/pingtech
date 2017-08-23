package com.pingtech.hgqw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.action.BindShipAction;
import com.pingtech.hgqw.module.cbdt.activity.ShipStatus;
import com.pingtech.hgqw.module.kakou.activity.KakouManager;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.tikou.activity.TiKouReadCard;
import com.pingtech.hgqw.module.tikou.activity.TikouManager;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownloadForBd;
import com.pingtech.hgqw.module.xunjian.activity.ReadcardActivity;
import com.pingtech.hgqw.module.xunjian.activity.XunChaXunJian;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 显示查询船舶结果列表界面的activity类 */
public class SelectShipResultActivity extends MyActivity implements
		OnHttpResult, OffLineResult, OnCheckedChangeListener {
	private static final String TAG = "SelectShipResultActivity";

	/** 查询船舶 */
	public static int SELECT_SHIP = 0;

	/** 绑定船舶 */
	public static int BIND_SHIP = 1;

	/** 发起绑定船舶的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_BINDSHIP = 6;

	/** 进入查看船舶详情界面 */
	private static final int STARTACTIVITY_FOR_SHIP_DETAIL = 7;

	/** 进入查看执勤人员界面 */
	private static final int STARTACTIVITY_FOR_DUTY = 8;

	private ListView listView;

	/** 绑定类型，来自船舶动态 0 梯口管理1 卡口管理3 巡查巡检2 */
	private int bindType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;

	private MyAdapter adapter;

	private MyAdapter_ShaiXuan adapter_ShaiXuan;

	private ProgressDialog progressDialog = null;

	/** 是否来自绑定船舶 */
	private boolean fromBindShip;

	/** 是否来自巡查巡检 */
	private boolean fromXunCha;

	/** 是否涉外服务船舶 */
	private boolean sheWai;

	/** 当执行绑定船舶时，用来临时保存船舶数据 */
	private HashMap<String, Object> bindMap;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	/**
	 * Tab标签
	 */
	private RadioGroup mRadioGroup;

	private RadioButton mRadioButton1;

	private RadioButton mRadioButton2;

	private RadioButton mRadioButton3;

	/**
	 * 标签下面的小图片（用于显示用户打开的是哪个Tab）
	 */
	private ImageView mImageView;

	/**
	 * 当前被选中的RadioButton距离左侧的距离
	 */
	private float mCurrentCheckedRadioLeft = 0;

	/**
	 * 每个标签的宽
	 */
	private int with = 0;

	/**
	 * 用户点击的是哪个TAB
	 */
	private String tab_postion = "";

	/**
	 * 船舶状态（如，预到港，在港，预出港）来自哪种类型的数据 数据分为三种类型，分别用1、2、3表示 默认为0
	 * 
	 */
	private int data_state = 0;

	/**
	 * 预到港船舶信息
	 */
	ArrayList<HashMap<String, Object>> Temp_plan_come_gang = new ArrayList<HashMap<String, Object>>();

	/**
	 * 在港船舶信息
	 */
	ArrayList<HashMap<String, Object>> tempDoingGang = new ArrayList<HashMap<String, Object>>();

	/**
	 * 预离港船舶信息
	 */
	ArrayList<HashMap<String, Object>> Temp_plan_out_gang = new ArrayList<HashMap<String, Object>>();

	/** 是否来自梯口快速核查 */
	private boolean fromTkkshc = false;

	/** 是否来自巡查巡检快速核查 */
	private boolean fromXcxjkshc = false;

	/**
	 * 用于更新界面
	 * 
	 * 1、预进港 2、在港 3、预出港
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				adapter_ShaiXuan = new MyAdapter_ShaiXuan(
						SelectShipResultActivity.this, Temp_plan_come_gang);
				listView.clearDisappearingChildren();
				listView.setAdapter(adapter_ShaiXuan);
				break;
			case 2:
				adapter_ShaiXuan = new MyAdapter_ShaiXuan(
						SelectShipResultActivity.this, tempDoingGang);
				listView.clearDisappearingChildren();
				listView.setAdapter(adapter_ShaiXuan);
				break;
			case 3:
				adapter_ShaiXuan = new MyAdapter_ShaiXuan(
						SelectShipResultActivity.this, Temp_plan_out_gang);
				listView.clearDisappearingChildren();
				listView.setAdapter(adapter_ShaiXuan);
				break;
			case OffDataDownload.WHAT_DOWNLOAD_SUCCESS_ONE:// 下载完成一个
				// 码头、泊位、区域、船舶、证件、船员
				String str = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str + "，下载完成");
				stringBuilder.append("\n");
				if (progressDialog != null) {
					progressDialog.setMessage(stringBuilder.toString());
				}
				break;

			case OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_NULL:// 下载失败一个
			case OffDataDownload.WHAT_INSERT_DATA_FAILED_ONE:// 下载失败一个
				// 码头、泊位、区域、船舶、证件、船员
				String str1 = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str1 + "，下载失败");
				stringBuilder.append("\n");
				if (progressDialog != null) {
					progressDialog.setCancelable(true);
					progressDialog.setMessage(stringBuilder.toString());
				}
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ALL_SUCCESS:// 下载完成
				HgqwToast.toast("下载完成");
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				returnActivity();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 是否有船舶状态信息（状态： 1、预进港 2、在港 3、预出港）
	 */
	private boolean ship_state = true;

	/**
	 * 是否要总的数据归类处理
	 */
	private boolean Tochuli = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		fromBindShip = intent.getBooleanExtra("frombindship", false);
		fromXunCha = intent.getBooleanExtra("fromxuncha", false);
		bindType = intent.getIntExtra("bindtype", 0);
		sheWai = intent.getBooleanExtra("shewai", false);
		cfzgFlag = intent.getBooleanExtra("cfzgFlag", false);
		fromTkkshc = intent.getBooleanExtra("fromtkkshc", false);
		fromXcxjkshc = intent.getBooleanExtra("fromXCXJkshc", false);
		super.onCreate(savedInstanceState, R.layout.selectship_result_list);

		Log.i(TAG, "onCreate()");

		if (fromBindShip) {
			if (bindType == GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.select_ship)
						+ getString(R.string.result));
			} else if (bindType == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.select_ship)
						+ getString(R.string.result));
			} else if (bindType == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.select_ship)
						+ getString(R.string.result));
			}
		} else if (fromXunCha) {
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
					+ getString(R.string.select_ship)
					+ getString(R.string.result));
		} else {
			setMyActiveTitle(getString(R.string.exception_info) + ">"
					+ getString(R.string.select_ship)
					+ getString(R.string.result));
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
		adapter = new MyAdapter(this);
		// 遍历数组，查看是否已经绑定船舶
		isBind();
		iniController();
		iniListener();
		// mRadioButton1.setChecked(true);
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();
		data_deal_with();
		listView = (ListView) findViewById(R.id.listview);
		if (listView != null) {
			listView.setAdapter(adapter);
			setRadioTextColorAllNoCheck();
			if (!ship_state) {
				mRadioGroup.setVisibility(View.GONE);
			} else {
				mRadioGroup.setVisibility(View.VISIBLE);
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
			if (findViewById(R.id.sel_ship_list_title_bind) != null) {
				findViewById(R.id.sel_ship_list_title_bind).setVisibility(
						View.VISIBLE);
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
						View.GONE);
			}
		} else {
			if (findViewById(R.id.sel_ship_list_title_bind) != null) {
				findViewById(R.id.sel_ship_list_title_bind).setVisibility(
						View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
				findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(
						View.GONE);
			}
			if (sheWai) {
				if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
					findViewById(R.id.sel_ship_list_title_jianhu)
							.setVisibility(View.GONE);
				}
				if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
					findViewById(R.id.sel_ship_list_title_shewai)
							.setVisibility(View.VISIBLE);
				}
			} else {
				if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
					findViewById(R.id.sel_ship_list_title_jianhu)
							.setVisibility(View.VISIBLE);
				}
				if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
					findViewById(R.id.sel_ship_list_title_shewai)
							.setVisibility(View.GONE);
				}
			}
		}
		if (findViewById(R.id.listview_topline) != null) {
			findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
		}
		if (findViewById(R.id.select_result_empty) != null) {
			findViewById(R.id.select_result_empty).setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		if (cfzgFlag) {
			setMyActiveTitle(getString(R.string.selectship));
		}
	}

	private void isBind() {
		if (!fromBindShip) {
			return;
		}
		if (SelectShipActivity.normalShipList != null) {
			for (HashMap<String, Object> map : SelectShipActivity.normalShipList) {
				String isbind = (String) map.get("bdzt");
				if (!"未绑定".equals(isbind)) {
					showBondDialog();
					SystemSetting.setBindShip(map, bindType + "");
					break;
				}
			}
		} else if (SelectShipActivity.xunjianShipList != null) {
			for (HashMap<String, Object> map : SelectShipActivity.xunjianShipList) {
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

		private TextView port_en_name;

		private TextView pos;

		private TextView kacbzt;

		private TextView country;

		private TextView port_country;

		private TextView protry;

		private TextView port_protry;

		private Button operate;

		private Button detail;

		private Button duty;
	}

	/** 自定义列表显示适配器 */
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (fromXunCha) {
				return SelectShipActivity.xunjianShipList == null ? 0
						: SelectShipActivity.xunjianShipList.size();
			} else {
				return SelectShipActivity.normalShipList == null ? 0
						: SelectShipActivity.normalShipList.size();
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
			/** 处理点击右边按钮操作 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				if (fromXunCha) {
					if (position % 3 == 2) {
						// 判断网络是否可用，如果不可用则提示
						if (BaseApplication.instent.getWebState()) {
							// 查看执勤人员
							Intent intent = new Intent();
							HashMap<String, Object> _Ship = SelectShipActivity.xunjianShipList
									.get((position - 2) / 3);
							intent.putExtra("hc",
									(String) (_Ship.get("hc") == null ? ""
											: _Ship.get("hc")));
							intent.putExtra("from", "0");
							intent.setClass(getApplicationContext(),
									DutyPersonlistActivity.class);
							startActivityForResult(intent,
									STARTACTIVITY_FOR_DUTY);
						} else {
							HgqwToast
									.toast(R.string.no_web_cannot_check_duty_person);
						}

					} else if (position % 3 == 1) {
						// 详情
						Intent intent = new Intent();
						HashMap<String, Object> _BindShip = SelectShipActivity.xunjianShipList
								.get((position - 1) / 3);
						intent.putExtra("hc",
								(String) (_BindShip.get("hc") == null ? ""
										: _BindShip.get("hc")));
						intent.putExtra("cbzwm",
								(String) (_BindShip.get("cbzwm") == null ? ""
										: _BindShip.get("cbzwm")));
						intent.putExtra("cbywm",
								(String) (_BindShip.get("cbywm") == null ? ""
										: _BindShip.get("cbywm")));
						intent.putExtra("gj",
								(String) (_BindShip.get("gj") == null ? ""
										: _BindShip.get("gj")));
						intent.putExtra("cbxz",
								(String) (_BindShip.get("cbxz") == null ? ""
										: _BindShip.get("cbxz")));
						intent.putExtra("bdzt",
								(String) (_BindShip.get("bdzt") == null ? ""
										: _BindShip.get("bdzt")));
						intent.putExtra("kacbzt",
								(String) (_BindShip.get("kacbzt") == null ? ""
										: _BindShip.get("kacbzt")));
						intent.putExtra("from", bindType);
						intent.putExtra("cfzgFlag", cfzgFlag);
						intent.putExtra("fromxunchaxunjian", true);
						intent.putExtra("frombindship", fromBindShip);
						intent.setClass(getApplicationContext(),
								ShipDetailActivity.class);
						startActivityForResult(intent,
								STARTACTIVITY_FOR_SHIP_DETAIL);
					} else if (position % 3 == 0) {
						// 动作
						String url = "buildRelation";
						bindMap = SelectShipActivity.xunjianShipList
								.get(position / 3);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("userID", LoginUser
								.getCurrentLoginUser().getUserID()));
						params.add(new BasicNameValuePair("PDACode",
								SystemSetting.getPDACode()));
						params.add(new BasicNameValuePair("bindState", "1"));
						params.add(new BasicNameValuePair("voyageNumber",
								(String) bindMap.get("hc")));
						params.add(new BasicNameValuePair("bindType", bindType
								+ ""));
						// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
						params.add(new BasicNameValuePair("zqdxlx",
								GlobalFlags.ZQDXLX_CB + ""));
						if (progressDialog != null) {
							return;
						}
						progressDialog = new ProgressDialog(
								SelectShipResultActivity.this);
						progressDialog.setTitle(getString(R.string.waiting));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(true);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						if (BaseApplication.instent.getWebState()) {
							NetWorkManager.request(
									SelectShipResultActivity.this, url, params,
									HTTPREQUEST_TYPE_FOR_BINDSHIP);
						} else {
							OffLineManager.request(
									SelectShipResultActivity.this,
									new BindShipAction(), url,
									NVPairTOMap.nameValuePairTOMap(params),
									HTTPREQUEST_TYPE_FOR_BINDSHIP);
						}

					}
				} else if (fromBindShip) {
					if (position % 2 == 1) {
						// 详情
						Intent intent = new Intent();
						HashMap<String, Object> _BindShip = SelectShipActivity.normalShipList
								.get((position - 1) / 2);
						intent.putExtra("hc",
								(String) (_BindShip.get("hc") == null ? ""
										: _BindShip.get("hc")));
						intent.putExtra("cbzwm",
								(String) (_BindShip.get("cbzwm") == null ? ""
										: _BindShip.get("cbzwm")));
						intent.putExtra("cbywm",
								(String) (_BindShip.get("cbywm") == null ? ""
										: _BindShip.get("cbywm")));
						intent.putExtra("gj",
								(String) (_BindShip.get("gj") == null ? ""
										: _BindShip.get("gj")));
						intent.putExtra("cbxz",
								(String) (_BindShip.get("cbxz") == null ? ""
										: _BindShip.get("cbxz")));
						intent.putExtra("bdzt",
								(String) (_BindShip.get("bdzt") == null ? ""
										: _BindShip.get("bdzt")));
						intent.putExtra("kacbzt",
								(String) (_BindShip.get("kacbzt") == null ? ""
										: _BindShip.get("kacbzt")));
						intent.putExtra("from", bindType);
						intent.putExtra("cfzgFlag", cfzgFlag);
						intent.setClass(getApplicationContext(),
								ShipDetailActivity.class);
						startActivityForResult(intent,
								STARTACTIVITY_FOR_SHIP_DETAIL);
					} else if (position % 2 == 0) {
						// 动作
						if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
							String url = "buildKkRelation";
							if (progressDialog != null) {
								return;
							}
							bindMap = SelectShipActivity.normalShipList
									.get(position / 2);
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
									SelectShipResultActivity.this);
							progressDialog
									.setTitle(getString(R.string.waiting));
							progressDialog
									.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(
									SelectShipResultActivity.this, url, params,
									HTTPREQUEST_TYPE_FOR_BINDSHIP);
						} else {
							String url = "buildRelation";
							if (progressDialog != null) {
								return;
							}
							bindMap = SelectShipActivity.normalShipList
									.get(position / 2);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("userID",
									LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode",
									SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "1"));
							params.add(new BasicNameValuePair("voyageNumber",
									(String) bindMap.get("hc")));
							params.add(new BasicNameValuePair("bindType",
									bindType + ""));
							// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx",
									GlobalFlags.ZQDXLX_CB + ""));

							progressDialog = new ProgressDialog(
									SelectShipResultActivity.this);
							progressDialog
									.setTitle(getString(R.string.waiting));
							progressDialog
									.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(
									SelectShipResultActivity.this, url, params,
									HTTPREQUEST_TYPE_FOR_BINDSHIP);
						}
					}
				} else {
					Intent data = null;
					data = new Intent();
					data.putExtra("type", SELECT_SHIP);
					data.putExtra("shipname",
							(String) (SelectShipActivity.normalShipList
									.get(position).get("cbzwm")));
					data.putExtra("shipengname",
							(String) (SelectShipActivity.normalShipList
									.get(position).get("cbywm")));
					if (((String) (SelectShipActivity.normalShipList
							.get(position).get("cblx"))).equals("ward")) {
						data.putExtra("shipid",
								(String) (SelectShipActivity.normalShipList
										.get(position).get("hc")));
					} else {
						data.putExtra("shipid",
								(String) (SelectShipActivity.normalShipList
										.get(position).get("id")));
					}
					data.putExtra("shiptype",
							(String) (SelectShipActivity.normalShipList
									.get(position).get("cblx")));
					data.putExtra("gj",
							(String) (SelectShipActivity.normalShipList
									.get(position).get("gj")));
					data.putExtra("tkmt",
							(String) (SelectShipActivity.normalShipList
									.get(position).get("tkmt")));
					data.putExtra("tkbw",
							(String) (SelectShipActivity.normalShipList
									.get(position).get("tkbw")));
					data.putExtra("tkwz",
							(String) (SelectShipActivity.normalShipList
									.get(position).get("tkwz")));
					setResult(RESULT_OK, data);
					finish();
				}
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				if (fromXunCha) {
					convertView = mInflater.inflate(
							R.layout.xuncha_sel_ship_listview_class, null);
					holder.index = (TextView) convertView
							.findViewById(R.id.index);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					holder.en_name = (TextView) convertView
							.findViewById(R.id.en_name);
					holder.port_en_name = (TextView) convertView
							.findViewById(R.id.port_en_name);
					holder.country = (TextView) convertView
							.findViewById(R.id.country);
					holder.port_country = (TextView) convertView
							.findViewById(R.id.port_country);
					holder.protry = (TextView) convertView
							.findViewById(R.id.protry);
					holder.port_protry = (TextView) convertView
							.findViewById(R.id.port_protry);
					holder.operate = (Button) convertView
							.findViewById(R.id.operate_btn);
					holder.detail = (Button) convertView
							.findViewById(R.id.detail_btn);
					holder.duty = (Button) convertView
							.findViewById(R.id.duty_btn);
					holder.pos = (TextView) convertView.findViewById(R.id.pos);
					holder.kacbzt = (TextView) convertView
							.findViewById(R.id.kacbzt);
					holder.operate.setOnClickListener(clickListener);
					holder.detail.setOnClickListener(clickListener);
					holder.duty.setOnClickListener(clickListener);
				} else if (fromBindShip) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						convertView = mInflater.inflate(
								R.layout.kk_listview_class, null);
						holder.index = (TextView) convertView
								.findViewById(R.id.index);
						holder.name = (TextView) convertView
								.findViewById(R.id.name);
						holder.en_name = (TextView) convertView
								.findViewById(R.id.rang);
						holder.country = (TextView) convertView
								.findViewById(R.id.addr);
					} else {
						convertView = mInflater.inflate(
								R.layout.ship_listview_class, null);
						holder.index = (TextView) convertView
								.findViewById(R.id.index);
						holder.name = (TextView) convertView
								.findViewById(R.id.name);
						holder.en_name = (TextView) convertView
								.findViewById(R.id.en_name);
						holder.country = (TextView) convertView
								.findViewById(R.id.country);
						holder.pos = (TextView) convertView
								.findViewById(R.id.pos);
						holder.kacbzt = (TextView) convertView
								.findViewById(R.id.kacbzt);
						holder.protry = (TextView) convertView
								.findViewById(R.id.protry);
					}
					holder.operate = (Button) convertView
							.findViewById(R.id.operate_btn);
					holder.detail = (Button) convertView
							.findViewById(R.id.detail_btn);
					holder.operate.setOnClickListener(clickListener);
					if (holder.detail != null) {
						holder.detail.setOnClickListener(clickListener);
					}
				} else {
					convertView = mInflater.inflate(
							R.layout.sel_ship_listview_class, null);
					holder.index = (TextView) convertView
							.findViewById(R.id.index);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					holder.en_name = (TextView) convertView
							.findViewById(R.id.en_name);
					holder.port_en_name = (TextView) convertView
							.findViewById(R.id.port_en_name);
					holder.pos = (TextView) convertView.findViewById(R.id.pos);
					holder.kacbzt = (TextView) convertView
							.findViewById(R.id.kacbzt);
					holder.country = (TextView) convertView
							.findViewById(R.id.country);
					holder.port_country = (TextView) convertView
							.findViewById(R.id.port_country);
					holder.protry = (TextView) convertView
							.findViewById(R.id.protry);
					holder.port_protry = (TextView) convertView
							.findViewById(R.id.port_protry);
					holder.operate = (Button) convertView
							.findViewById(R.id.operate_btn);
					holder.operate.setOnClickListener(clickListener);
				}
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (fromXunCha) {
				holder.operate.setTag(position * 3);
				holder.detail.setTag(position * 3 + 1);
				holder.duty.setTag(position * 3 + 2);
			} else if (fromBindShip) {
				holder.operate.setTag(position * 2);
				if (holder.detail != null) {
					holder.detail.setTag(position * 2 + 1);
				}
			} else {
				holder.operate.setTag(position);
			}
			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (fromXunCha) {
				if (holder.name != null) {
					holder.name
							.setText((String) SelectShipActivity.xunjianShipList
									.get(position).get("cbzwm"));
				}
				String cblx = (String) (SelectShipActivity.xunjianShipList
						.get(position).get("cblx"));
				if (cblx.equals("ward")) {
					if (holder.en_name != null) {
						holder.en_name
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("cbywm")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("cbywm")));
					}
					if (holder.pos != null) {
						holder.pos
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("tkwz")));
					}
					/**
					 * 2
					 */
					if (holder.kacbzt != null) {
						data_state = 2;
						holder.kacbzt
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("kacbzt")));
					}
					if (holder.country != null) {
						String gj_str = (String) (SelectShipActivity.xunjianShipList
								.get(position).get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.country.setText("");
						} else {
							holder.country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}

					if (holder.port_country != null) {
						String gj_str = (String) (SelectShipActivity.xunjianShipList
								.get(position).get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.port_country.setText("");
						} else {
							holder.port_country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
					if (holder.protry != null) {
						String cbxz_str = (String) SelectShipActivity.xunjianShipList
								.get(position).get("cbxz");
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
					if (holder.port_protry != null) {
						String cbxz_str = (String) SelectShipActivity.xunjianShipList
								.get(position).get("cbxz");
						if (cbxz_str == null || cbxz_str.length() == 0) {
							holder.port_protry.setText("");
						} else {
							holder.port_protry
									.setText(DataDictionary
											.getDataDictionaryName(
													cbxz_str,
													DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
						}
					}
					if (fromBindShip) {
						holder.operate.setVisibility(View.VISIBLE);
						if (SelectShipActivity.xunjianShipList.get(position)
								.get("bdzt") == null
								|| ((String) SelectShipActivity.xunjianShipList
										.get(position).get("bdzt"))
										.equals("未绑定")) {
							if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
								holder.operate.setText("绑定");
							} else {
								holder.operate.setText("绑定");
							}
							holder.operate.setEnabled(true);
						} else {
							holder.operate.setText("已绑定");
							holder.operate.setEnabled(false);
							SystemSetting.setBindShip(
									SelectShipActivity.xunjianShipList
											.get(position), bindType + "");
						}
					} else {
						holder.operate.setVisibility(View.GONE);
					}
					holder.detail.setText(R.string.detail);
					holder.duty.setText(R.string.duty);
				} else {
					if (holder.en_name != null) {
						holder.en_name
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("cjsj")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("czmc")));
					}
					if (holder.pos != null) {
						holder.pos
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("ssdw")));
					}
					if (holder.kacbzt != null) {
						holder.kacbzt.setVisibility(View.GONE);
					}
					if (holder.country != null) {
						holder.country
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("cbzyyt")));
					}
					if (holder.port_country != null) {
						holder.port_country
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("cjsj")));
					}
					if (holder.protry != null) {
						holder.protry
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("czmc")));
					}
					if (holder.port_protry != null) {
						holder.port_protry
								.setText((String) (SelectShipActivity.xunjianShipList
										.get(position).get("cbzyyt")));
					}
					if (fromBindShip) {
						holder.operate.setText("绑定");
						holder.operate.setEnabled(false);
						holder.operate.setVisibility(View.VISIBLE);
					} else {
						holder.operate.setVisibility(View.GONE);
					}
					holder.detail.setText(R.string.detail);
					holder.detail.setEnabled(false);
					holder.duty.setText(R.string.duty);
					holder.duty.setEnabled(false);
				}

			} else if (fromBindShip) {
				if (holder.name != null) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.name
								.setText((String) SelectShipActivity.normalShipList
										.get(position).get("kkmc"));
					} else {
						holder.name
								.setText((String) SelectShipActivity.normalShipList
										.get(position).get("cbzwm"));
					}
				}
				if (holder.en_name != null) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.en_name
								.setText((String) SelectShipActivity.normalShipList
										.get(position).get("kkfw"));
					} else {
						holder.en_name
								.setText((String) SelectShipActivity.normalShipList
										.get(position).get("cbywm"));
					}
				}
				if (holder.country != null) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.country
								.setText((String) SelectShipActivity.normalShipList
										.get(position).get("kkxx"));
					} else {
						String gj_str = (String) (SelectShipActivity.normalShipList
								.get(position).get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.country.setText("");
						} else {
							holder.country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
				}
				if (holder.protry != null) {
					String cbxz_str = (String) SelectShipActivity.normalShipList
							.get(position).get("cbxz");
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
				if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					if (holder.pos != null) {
						holder.pos.setVisibility(View.GONE);
					}
				} else {
					if (holder.pos != null) {
						holder.pos.setVisibility(View.VISIBLE);
						holder.pos
								.setText((String) SelectShipActivity.normalShipList
										.get(position).get("tkwz"));
					}
					/**
					 * 3
					 */
					if (holder.kacbzt != null) {
						data_state = 3;
						holder.kacbzt
								.setText((String) SelectShipActivity.normalShipList
										.get(position).get("kacbzt"));
					}
				}
				if (SelectShipActivity.normalShipList.get(position).get("bdzt") == null
						|| ((String) SelectShipActivity.normalShipList.get(
								position).get("bdzt")).equals("未绑定")) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.operate.setText("绑定");
					} else {
						holder.operate.setText("绑定");
					}
					holder.operate.setEnabled(true);
				} else {
					holder.operate.setText("已绑定");
					holder.operate.setEnabled(false);
					SystemSetting.setBindShip(
							SelectShipActivity.normalShipList.get(position),
							bindType + "");

				}
				if (holder.detail != null) {
					holder.detail.setText(R.string.detail);
				}
			} else {
				if (holder.name != null) {
					holder.name
							.setText((String) SelectShipActivity.normalShipList
									.get(position).get("cbzwm"));
				}
				String cblx = (String) (SelectShipActivity.normalShipList
						.get(position).get("cblx"));
				if (cblx.equals("ward")) {
					if (holder.en_name != null) {
						holder.en_name
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("cbywm")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("cbywm")));
					}
					if (holder.pos != null) {
						holder.pos
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("tkwz")));
					}
					/**
					 * 1
					 */
					if (holder.kacbzt != null) {
						data_state = 1;
						holder.kacbzt
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("kacbzt")));
					}
					if (holder.country != null) {
						String gj_str = (String) (SelectShipActivity.normalShipList
								.get(position).get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.country.setText("");
						} else {
							holder.country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
					if (holder.port_country != null) {
						String gj_str = (String) (SelectShipActivity.normalShipList
								.get(position).get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.port_country.setText("");
						} else {
							holder.port_country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
					if (holder.protry != null) {
						String cbxz_str = (String) SelectShipActivity.normalShipList
								.get(position).get("cbxz");
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
					if (holder.port_protry != null) {
						String cbxz_str = (String) SelectShipActivity.normalShipList
								.get(position).get("cbxz");
						if (cbxz_str == null || cbxz_str.length() == 0) {
							holder.port_protry.setText("");
						} else {
							holder.port_protry
									.setText(DataDictionary
											.getDataDictionaryName(
													cbxz_str,
													DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
						}
					}

				} else {
					if (holder.en_name != null) {
						holder.en_name
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("cjsj")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("czmc")));
					}
					if (holder.pos != null) {
						holder.pos
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("ssdw")));
					}
					if (holder.kacbzt != null) {
						holder.kacbzt.setVisibility(View.GONE);
					}
					if (holder.country != null) {
						holder.country
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("cbzyyt")));
					}
					if (holder.port_country != null) {
						holder.port_country
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("cjsj")));
					}
					if (holder.protry != null) {
						holder.protry
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("czmc")));
					}
					if (holder.port_protry != null) {
						holder.port_protry
								.setText((String) (SelectShipActivity.normalShipList
										.get(position).get("cbzyyt")));
					}

				}
				if (holder.index != null) {
					holder.operate.setText(R.string.selectship);
				} else {
					holder.operate.setText("选择");
				}
			}

			// if (data_state == 3 || data_state == 1) {
			// String state_ship = (String) (SelectShipActivity.normalShipList
			// .get(position).get("kacbzt"));
			// if (state_ship.equals(getResources().getString(
			// R.string.state_ship_plan_come_gang))) {
			// normalShipListTemp_plan_come_gang
			// .add(SelectShipActivity.normalShipList
			// .get(position));
			// } else if (state_ship.equals(getResources().getString(
			// R.string.state_ship_doing_gang))) {
			// normalShipListTemp_doing_gang
			// .add(SelectShipActivity.normalShipList
			// .get(position));
			// } else if (state_ship.equals(getResources().getString(
			// R.string.state_ship_plan_out_gang))) {
			// normalShipListTemp_plan_out_gang
			// .add(SelectShipActivity.normalShipList
			// .get(position));
			// }
			// }

			// if ((tab_postion != null) && (!tab_postion.equals(""))) {
			// if (data_state == 2) {
			// /*
			// * if(!((String) (SelectShipActivity.xunjianShipList
			// * .get(position).get("kacbzt"))).equals(tab_postion)){
			// * convertView.setVisibility(View.GONE);
			// * notifyDataSetChanged();
			// * System.out.println("BBBBBBBBBBBBBBBBBB:"+tab_postion);
			// * if(position>getCount()){ View view = new
			// * View(SelectShipResultActivity.this); return view; }else{
			// * return getView(position+1, convertView, parent); } }else{
			// * convertView.setVisibility(View.VISIBLE); }
			// */
			// } else if (data_state == 3) {
			// if (!((String) (SelectShipActivity.normalShipList
			// .get(position).get("kacbzt"))).equals(tab_postion)) {
			// convertView.setVisibility(View.GONE);
			// convertView
			// .setLayoutParams(new RelativeLayout.LayoutParams(
			// 0, 0));
			// notifyDataSetChanged();
			//
			// System.out.println("BBBBBBBBBBBBBBBBBB:" + tab_postion);
			// // if(position>getCount()){
			// // View view = new View(SelectShipResultActivity.this);
			// // return view;
			// // }else{
			// // return getView(position+1, convertView, parent);
			// // }
			// } else {
			// convertView.setVisibility(View.VISIBLE);
			// }
			// } else if (data_state == 1) {
			// if (!((String) (SelectShipActivity.normalShipList
			// .get(position).get("kacbzt"))).equals(tab_postion)) {
			// convertView.setVisibility(View.GONE);
			// convertView
			// .setLayoutParams(new RelativeLayout.LayoutParams(
			// 0, 0));
			// notifyDataSetChanged();
			// // System.out.println("BBBBBBBBBBBBBBBBBB:"+tab_postion);
			// // if(position>getCount()){
			// // View view = new View(SelectShipResultActivity.this);
			// // return view;
			// // }else{
			// // return getView(position+1, convertView, parent);
			// // }
			//
			// } else {
			// convertView.setVisibility(View.VISIBLE);
			// }
			// }
			// }

			return convertView;
		}
	}

	/** 自定义列表显示适配器 */
	private class MyAdapter_ShaiXuan extends BaseAdapter {
		private LayoutInflater mInflater;

		private ArrayList<HashMap<String, Object>> arrayList;

		public MyAdapter_ShaiXuan(Context context,
				ArrayList<HashMap<String, Object>> arrayList) {
			this.mInflater = LayoutInflater.from(context);
			this.arrayList = arrayList;
		}

		@Override
		public int getCount() {
			return arrayList.size();
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
			/** 处理点击右边按钮操作 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position = Integer.parseInt(v.getTag().toString());
				if (fromXunCha) {
					if (position % 3 == 2) {
						// 查看执勤人员
						Intent intent = new Intent();
						HashMap<String, Object> _Ship = arrayList
								.get((position - 2) / 3);
						intent.putExtra(
								"hc",
								(String) (_Ship.get("hc") == null ? "" : _Ship
										.get("hc")));
						intent.putExtra("from", "0");
						intent.setClass(getApplicationContext(),
								DutyPersonlistActivity.class);
						startActivityForResult(intent, STARTACTIVITY_FOR_DUTY);
					} else if (position % 3 == 1) {
						// 详情
						Intent intent = new Intent();
						HashMap<String, Object> _BindShip = arrayList
								.get((position - 1) / 3);
						intent.putExtra("hc",
								(String) (_BindShip.get("hc") == null ? ""
										: _BindShip.get("hc")));
						intent.putExtra("cbzwm",
								(String) (_BindShip.get("cbzwm") == null ? ""
										: _BindShip.get("cbzwm")));
						intent.putExtra("cbywm",
								(String) (_BindShip.get("cbywm") == null ? ""
										: _BindShip.get("cbywm")));
						intent.putExtra("gj",
								(String) (_BindShip.get("gj") == null ? ""
										: _BindShip.get("gj")));
						intent.putExtra("cbxz",
								(String) (_BindShip.get("cbxz") == null ? ""
										: _BindShip.get("cbxz")));
						intent.putExtra("bdzt",
								(String) (_BindShip.get("bdzt") == null ? ""
										: _BindShip.get("bdzt")));
						intent.putExtra("kacbzt",
								(String) (_BindShip.get("kacbzt") == null ? ""
										: _BindShip.get("kacbzt")));
						intent.putExtra("from", bindType);
						intent.putExtra("cfzgFlag", cfzgFlag);
						intent.putExtra("fromxunchaxunjian", true);
						intent.putExtra("frombindship", fromBindShip);
						intent.setClass(getApplicationContext(),
								ShipDetailActivity.class);
						startActivityForResult(intent,
								STARTACTIVITY_FOR_SHIP_DETAIL);
					} else if (position % 3 == 0) {
						// 动作
						String url = "buildRelation";
						bindMap = arrayList.get(position / 3);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("userID", LoginUser
								.getCurrentLoginUser().getUserID()));
						params.add(new BasicNameValuePair("PDACode",
								SystemSetting.getPDACode()));
						params.add(new BasicNameValuePair("bindState", "1"));
						params.add(new BasicNameValuePair("voyageNumber",
								(String) bindMap.get("hc")));
						params.add(new BasicNameValuePair("bindType", bindType
								+ ""));
						// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
						params.add(new BasicNameValuePair("zqdxlx",
								GlobalFlags.ZQDXLX_CB + ""));

						if (progressDialog != null) {
							return;
						}
						progressDialog = new ProgressDialog(
								SelectShipResultActivity.this);
						progressDialog.setTitle(getString(R.string.waiting));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						NetWorkManager.request(SelectShipResultActivity.this,
								url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
					}
				} else if (fromBindShip) {
					if (position % 2 == 1) {
						// 详情
						Intent intent = new Intent();
						HashMap<String, Object> _BindShip = arrayList
								.get((position - 1) / 2);
						intent.putExtra("hc",
								(String) (_BindShip.get("hc") == null ? ""
										: _BindShip.get("hc")));
						intent.putExtra("cbzwm",
								(String) (_BindShip.get("cbzwm") == null ? ""
										: _BindShip.get("cbzwm")));
						intent.putExtra("cbywm",
								(String) (_BindShip.get("cbywm") == null ? ""
										: _BindShip.get("cbywm")));
						intent.putExtra("gj",
								(String) (_BindShip.get("gj") == null ? ""
										: _BindShip.get("gj")));
						intent.putExtra("cbxz",
								(String) (_BindShip.get("cbxz") == null ? ""
										: _BindShip.get("cbxz")));
						intent.putExtra("bdzt",
								(String) (_BindShip.get("bdzt") == null ? ""
										: _BindShip.get("bdzt")));
						intent.putExtra("kacbzt",
								(String) (_BindShip.get("kacbzt") == null ? ""
										: _BindShip.get("kacbzt")));
						intent.putExtra("from", bindType);
						intent.putExtra("cfzgFlag", cfzgFlag);
						intent.setClass(getApplicationContext(),
								ShipDetailActivity.class);
						startActivityForResult(intent,
								STARTACTIVITY_FOR_SHIP_DETAIL);
					} else if (position % 2 == 0) {
						// 动作
						if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
							String url = "buildKkRelation";
							if (progressDialog != null) {
								return;
							}
							bindMap = arrayList.get(position / 2);
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
									SelectShipResultActivity.this);
							progressDialog
									.setTitle(getString(R.string.waiting));
							progressDialog
									.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(
									SelectShipResultActivity.this, url, params,
									HTTPREQUEST_TYPE_FOR_BINDSHIP);
						} else {
							String url = "buildRelation";
							if (progressDialog != null) {
								return;
							}
							bindMap = arrayList.get(position / 2);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("userID",
									LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode",
									SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "1"));
							params.add(new BasicNameValuePair("voyageNumber",
									(String) bindMap.get("hc")));
							params.add(new BasicNameValuePair("bindType",
									bindType + ""));
							// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx",
									GlobalFlags.ZQDXLX_CB + ""));

							progressDialog = new ProgressDialog(
									SelectShipResultActivity.this);
							progressDialog
									.setTitle(getString(R.string.waiting));
							progressDialog
									.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(
									SelectShipResultActivity.this, url, params,
									HTTPREQUEST_TYPE_FOR_BINDSHIP);
						}
					}
				} else {
					Intent data = null;
					data = new Intent();
					data.putExtra("type", SELECT_SHIP);
					data.putExtra("shipname",
							(String) (arrayList.get(position).get("cbzwm")));
					data.putExtra("shipengname",
							(String) (arrayList.get(position).get("cbywm")));
					if (((String) (arrayList.get(position).get("cblx")))
							.equals("ward")) {
						data.putExtra("shipid",
								(String) (arrayList.get(position).get("hc")));
					} else {
						data.putExtra("shipid",
								(String) (arrayList.get(position).get("id")));
					}
					data.putExtra("shiptype",
							(String) (arrayList.get(position).get("cblx")));
					data.putExtra("gj",
							(String) (arrayList.get(position).get("gj")));
					data.putExtra("tkmt",
							(String) (arrayList.get(position).get("tkmt")));
					data.putExtra("tkbw",
							(String) (arrayList.get(position).get("tkbw")));
					data.putExtra("tkwz",
							(String) (arrayList.get(position).get("tkwz")));
					setResult(RESULT_OK, data);
					finish();
				}
			}
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				if (fromXunCha) {
					convertView = mInflater.inflate(
							R.layout.xuncha_sel_ship_listview_class, null);
					holder.index = (TextView) convertView
							.findViewById(R.id.index);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					holder.en_name = (TextView) convertView
							.findViewById(R.id.en_name);
					holder.port_en_name = (TextView) convertView
							.findViewById(R.id.port_en_name);
					holder.country = (TextView) convertView
							.findViewById(R.id.country);
					holder.port_country = (TextView) convertView
							.findViewById(R.id.port_country);
					holder.protry = (TextView) convertView
							.findViewById(R.id.protry);
					holder.port_protry = (TextView) convertView
							.findViewById(R.id.port_protry);
					holder.operate = (Button) convertView
							.findViewById(R.id.operate_btn);
					holder.detail = (Button) convertView
							.findViewById(R.id.detail_btn);
					holder.duty = (Button) convertView
							.findViewById(R.id.duty_btn);
					holder.pos = (TextView) convertView.findViewById(R.id.pos);
					holder.kacbzt = (TextView) convertView
							.findViewById(R.id.kacbzt);
					holder.operate.setOnClickListener(clickListener);
					holder.detail.setOnClickListener(clickListener);
					holder.duty.setOnClickListener(clickListener);
				} else if (fromBindShip) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						convertView = mInflater.inflate(
								R.layout.kk_listview_class, null);
						holder.index = (TextView) convertView
								.findViewById(R.id.index);
						holder.name = (TextView) convertView
								.findViewById(R.id.name);
						holder.en_name = (TextView) convertView
								.findViewById(R.id.rang);
						holder.country = (TextView) convertView
								.findViewById(R.id.addr);
					} else {
						convertView = mInflater.inflate(
								R.layout.ship_listview_class, null);
						holder.index = (TextView) convertView
								.findViewById(R.id.index);
						holder.name = (TextView) convertView
								.findViewById(R.id.name);
						holder.en_name = (TextView) convertView
								.findViewById(R.id.en_name);
						holder.country = (TextView) convertView
								.findViewById(R.id.country);
						holder.pos = (TextView) convertView
								.findViewById(R.id.pos);
						holder.kacbzt = (TextView) convertView
								.findViewById(R.id.kacbzt);
						holder.protry = (TextView) convertView
								.findViewById(R.id.protry);
					}
					holder.operate = (Button) convertView
							.findViewById(R.id.operate_btn);
					holder.detail = (Button) convertView
							.findViewById(R.id.detail_btn);
					holder.operate.setOnClickListener(clickListener);
					if (holder.detail != null) {
						holder.detail.setOnClickListener(clickListener);
					}
				} else {
					convertView = mInflater.inflate(
							R.layout.sel_ship_listview_class, null);
					holder.index = (TextView) convertView
							.findViewById(R.id.index);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					holder.en_name = (TextView) convertView
							.findViewById(R.id.en_name);
					holder.port_en_name = (TextView) convertView
							.findViewById(R.id.port_en_name);
					holder.pos = (TextView) convertView.findViewById(R.id.pos);
					holder.kacbzt = (TextView) convertView
							.findViewById(R.id.kacbzt);
					holder.country = (TextView) convertView
							.findViewById(R.id.country);
					holder.port_country = (TextView) convertView
							.findViewById(R.id.port_country);
					holder.protry = (TextView) convertView
							.findViewById(R.id.protry);
					holder.port_protry = (TextView) convertView
							.findViewById(R.id.port_protry);
					holder.operate = (Button) convertView
							.findViewById(R.id.operate_btn);
					holder.operate.setOnClickListener(clickListener);
				}
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (fromXunCha) {
				holder.operate.setTag(position * 3);
				holder.detail.setTag(position * 3 + 1);
				holder.duty.setTag(position * 3 + 2);
			} else if (fromBindShip) {
				holder.operate.setTag(position * 2);
				if (holder.detail != null) {
					holder.detail.setTag(position * 2 + 1);
				}
			} else {
				holder.operate.setTag(position);
			}
			if (holder.index != null) {
				holder.index.setText((position + 1) + "");
			}
			if (fromXunCha) {
				if (holder.name != null) {
					holder.name.setText((String) arrayList.get(position).get(
							"cbzwm"));
				}
				String cblx = (String) (arrayList.get(position).get("cblx"));
				if (cblx.equals("ward")) {
					if (holder.en_name != null) {
						holder.en_name.setText((String) (arrayList
								.get(position).get("cbywm")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name.setText((String) (arrayList
								.get(position).get("cbywm")));
					}
					if (holder.pos != null) {
						holder.pos.setText((String) (arrayList.get(position)
								.get("tkwz")));
					}
					if (holder.kacbzt != null) {
						holder.kacbzt.setText((String) (arrayList.get(position)
								.get("kacbzt")));
					}
					if (holder.country != null) {
						String gj_str = (String) (arrayList.get(position)
								.get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.country.setText("");
						} else {
							holder.country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}

					if (holder.port_country != null) {
						String gj_str = (String) (arrayList.get(position)
								.get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.port_country.setText("");
						} else {
							holder.port_country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
					if (holder.protry != null) {
						String cbxz_str = (String) arrayList.get(position).get(
								"cbxz");
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
					if (holder.port_protry != null) {
						String cbxz_str = (String) arrayList.get(position).get(
								"cbxz");
						if (cbxz_str == null || cbxz_str.length() == 0) {
							holder.port_protry.setText("");
						} else {
							holder.port_protry
									.setText(DataDictionary
											.getDataDictionaryName(
													cbxz_str,
													DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
						}
					}
					if (fromBindShip) {
						holder.operate.setVisibility(View.VISIBLE);
						if (arrayList.get(position).get("bdzt") == null
								|| ((String) arrayList.get(position)
										.get("bdzt")).equals("未绑定")) {
							if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
								holder.operate.setText("绑定");
							} else {
								holder.operate.setText("绑定");
							}
							holder.operate.setEnabled(true);
						} else {
							holder.operate.setText("已绑定");
							holder.operate.setEnabled(false);
							SystemSetting.setBindShip(arrayList.get(position),
									bindType + "");
						}
					} else {
						holder.operate.setVisibility(View.GONE);
					}
					holder.detail.setText(R.string.detail);
					holder.duty.setText(R.string.duty);
				} else {
					if (holder.en_name != null) {
						holder.en_name.setText((String) (arrayList
								.get(position).get("cjsj")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name.setText((String) (arrayList
								.get(position).get("czmc")));
					}
					if (holder.pos != null) {
						holder.pos.setText((String) (arrayList.get(position)
								.get("ssdw")));
					}
					if (holder.kacbzt != null) {
						holder.kacbzt.setVisibility(View.GONE);
					}
					if (holder.country != null) {
						holder.country.setText((String) (arrayList
								.get(position).get("cbzyyt")));
					}
					if (holder.port_country != null) {
						holder.port_country.setText((String) (arrayList
								.get(position).get("cjsj")));
					}
					if (holder.protry != null) {
						holder.protry.setText((String) (arrayList.get(position)
								.get("czmc")));
					}
					if (holder.port_protry != null) {
						holder.port_protry.setText((String) (arrayList
								.get(position).get("cbzyyt")));
					}
					if (fromBindShip) {
						holder.operate.setText("绑定");
						holder.operate.setEnabled(false);
						holder.operate.setVisibility(View.VISIBLE);
					} else {
						holder.operate.setVisibility(View.GONE);
					}
					holder.detail.setText(R.string.detail);
					holder.detail.setEnabled(false);
					holder.duty.setText(R.string.duty);
					holder.duty.setEnabled(false);
				}

			} else if (fromBindShip) {
				if (holder.name != null) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.name.setText((String) arrayList.get(position)
								.get("kkmc"));
					} else {
						holder.name.setText((String) arrayList.get(position)
								.get("cbzwm"));
					}
				}
				if (holder.en_name != null) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.en_name.setText((String) arrayList.get(position)
								.get("kkfw"));
					} else {
						holder.en_name.setText((String) arrayList.get(position)
								.get("cbywm"));
					}
				}
				if (holder.country != null) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.country.setText((String) arrayList.get(position)
								.get("kkxx"));
					} else {
						String gj_str = (String) (arrayList.get(position)
								.get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.country.setText("");
						} else {
							holder.country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
				}
				if (holder.protry != null) {
					String cbxz_str = (String) arrayList.get(position).get(
							"cbxz");
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
				if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					if (holder.pos != null) {
						holder.pos.setVisibility(View.GONE);
					}
				} else {
					if (holder.pos != null) {
						holder.pos.setVisibility(View.VISIBLE);
						holder.pos.setText((String) arrayList.get(position)
								.get("tkwz"));
					}
					if (holder.kacbzt != null) {
						holder.kacbzt.setText((String) arrayList.get(position)
								.get("kacbzt"));
					}
				}
				if (arrayList.get(position).get("bdzt") == null
						|| ((String) arrayList.get(position).get("bdzt"))
								.equals("未绑定")) {
					if (bindType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
						holder.operate.setText("绑定");
					} else {
						holder.operate.setText("绑定");
					}
					holder.operate.setEnabled(true);
				} else {
					holder.operate.setText("已绑定");
					holder.operate.setEnabled(false);
				}
				if (holder.detail != null) {
					holder.detail.setText(R.string.detail);
				}
			} else {
				if (holder.name != null) {
					holder.name.setText((String) arrayList.get(position).get(
							"cbzwm"));
				}
				String cblx = (String) (arrayList.get(position).get("cblx"));
				if (cblx.equals("ward")) {
					if (holder.en_name != null) {
						holder.en_name.setText((String) (arrayList
								.get(position).get("cbywm")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name.setText((String) (arrayList
								.get(position).get("cbywm")));
					}
					if (holder.pos != null) {
						holder.pos.setText((String) (arrayList.get(position)
								.get("tkwz")));
					}
					if (holder.kacbzt != null) {
						holder.kacbzt.setText((String) (arrayList.get(position)
								.get("kacbzt")));
					}
					if (holder.country != null) {
						String gj_str = (String) (arrayList.get(position)
								.get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.country.setText("");
						} else {
							holder.country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
					if (holder.port_country != null) {
						String gj_str = (String) (arrayList.get(position)
								.get("gj"));
						if (gj_str == null || gj_str.length() == 0) {
							holder.port_country.setText("");
						} else {
							holder.port_country.setText(DataDictionary
									.getCountryName(gj_str));
						}
					}
					if (holder.protry != null) {
						String cbxz_str = (String) arrayList.get(position).get(
								"cbxz");
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
					if (holder.port_protry != null) {
						String cbxz_str = (String) arrayList.get(position).get(
								"cbxz");
						if (cbxz_str == null || cbxz_str.length() == 0) {
							holder.port_protry.setText("");
						} else {
							holder.port_protry
									.setText(DataDictionary
											.getDataDictionaryName(
													cbxz_str,
													DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE));
						}
					}

				} else {
					if (holder.en_name != null) {
						holder.en_name.setText((String) (arrayList
								.get(position).get("cjsj")));
					}
					if (holder.port_en_name != null) {
						holder.port_en_name.setText((String) (arrayList
								.get(position).get("czmc")));
					}
					if (holder.pos != null) {
						holder.pos.setText((String) (arrayList.get(position)
								.get("ssdw")));
					}
					if (holder.kacbzt != null) {
						holder.kacbzt.setVisibility(View.GONE);
					}
					if (holder.country != null) {
						holder.country.setText((String) (arrayList
								.get(position).get("cbzyyt")));
					}
					if (holder.port_country != null) {
						holder.port_country.setText((String) (arrayList
								.get(position).get("cjsj")));
					}
					if (holder.protry != null) {
						holder.protry.setText((String) (arrayList.get(position)
								.get("czmc")));
					}
					if (holder.port_protry != null) {
						holder.port_protry.setText((String) (arrayList
								.get(position).get("cbzyyt")));
					}

				}
				if (holder.index != null) {
					holder.operate.setText(R.string.selectship);
				} else {
					holder.operate.setText("选择");
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

	/** 处理从船舶详情界面返回时的动作，如果选择了船舶绑定，就直接返回 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_SHIP_DETAIL:
			if (resultCode == RESULT_OK) {
				Intent intent = null;
				intent = new Intent();
				intent.putExtra("type", BIND_SHIP);
				setResult(RESULT_OK, intent);
				finish();
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

	/** 处理船舶绑定结果 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (HTTPREQUEST_TYPE_FOR_BINDSHIP == httpRequestType) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {

				bindMap.put("bdzt", "已绑定");
				SystemSetting.setBindShip(bindMap, bindType + "");
				HgqwToast.makeText(SelectShipResultActivity.this,
						R.string.bindship_success, HgqwToast.LENGTH_LONG)
						.show();

				// 直接跳回二级页面
				if (GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == bindType
						|| GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == bindType
						|| GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER == bindType) {
					downloadOfflineData(bindMap, bindType);
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					returnActivity();
				}

			} else if (str != null && "3".equals(str)) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(SelectShipResultActivity.this,
						R.string.had_bind_ship, HgqwToast.LENGTH_LONG).show();
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(SelectShipResultActivity.this,
						R.string.bindship_failure, HgqwToast.LENGTH_LONG)
						.show();
				SystemSetting.setBindShip(null, bindType + "");
			}
		} else if (httpRequestType == FlagUrls.VALIDATE_PASSWORD) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
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

	private void returnActivity() {
		Intent intent = new Intent();
		if (cfzgFlag) {// 如果是船方自管
		} else {
			if (bindType == 0) {
				intent.setClass(getApplicationContext(), ShipStatus.class);
			} else if (bindType == 1) {

				if (fromTkkshc) {
					intent.putExtra("title", getString(R.string.tikoumanager)
							+ ">" + getString(R.string.paycard));
					intent.putExtra("from", "02");
					intent.setClass(getApplicationContext(),
							TiKouReadCard.class);
				} else {
					intent.setClass(getApplicationContext(), TikouManager.class);
				}
			} else if (bindType == 3) {
				intent.setClass(getApplicationContext(), KakouManager.class);
			} else if (bindType == 2) {
				if (fromXcxjkshc) {
					HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
					intent.putExtra("title", getString(R.string.xunchaxunjian) + ">" + getString(R.string.normalxunjian));
					intent.putExtra("cardtype", ReadcardActivity.READCARD_TYPE_ID_CARD);
					intent.putExtra("from", "03");
					if (bindData != null) {
						intent.putExtra("hc", bindData.get("hc") + "");
						intent.putExtra("kacbqkid", bindData.get("kacbqkid") + "");
						intent.putExtra("cbzwm", bindData.get("cbzwm") + "");
					}
					intent.setClass(getApplicationContext(), ReadcardActivity.class);
					//startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
					
				} else {
					intent.setClass(getApplicationContext(),
							XunChaXunJian.class);
				}
			}
		}
		startActivity(intent);
		finish();
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

	/**
	 * 注册控件
	 */
	private void iniController() {
		// TODO Auto-generated method stub
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		mRadioButton1 = (RadioButton) findViewById(R.id.btn1);
		mRadioButton2 = (RadioButton) findViewById(R.id.btn2);
		mRadioButton3 = (RadioButton) findViewById(R.id.btn3);

		mImageView = (ImageView) findViewById(R.id.img1);
		mImageView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 注册控件单击事件
	 */
	private void iniListener() {
		// TODO Auto-generated method stub

		mRadioGroup.setOnCheckedChangeListener(this);
	}

	/**
	 * 获得当前被选中的RadioButton距离左侧的距离
	 */
	private float getCurrentCheckedRadioLeft() {
		// TODO Auto-generated method stub
		if (mRadioButton1.isChecked()) {
			return 0f;
		} else if (mRadioButton2.isChecked()) {
			return with;
		} else if (mRadioButton3.isChecked()) {
			return with * 2;
		}
		return 0f;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		mImageView.setVisibility(View.VISIBLE);
		AnimationSet _AnimationSet = new AnimationSet(true);
		TranslateAnimation _TranslateAnimation;
		Log.i("zj", "checkedid=" + checkedId);
		data_deal_with();
		if (!ship_state) {
			HgqwToast.toast(R.string.no_ship_state);
			mImageView.setVisibility(View.INVISIBLE);
			return;
		}
		if (checkedId == R.id.btn1) {
			changeRadioTextColorByOnClick(mRadioButton1);
			tab_postion = getString(R.string.state_ship_plan_come_gang);
			with = group.getChildAt(0).getWidth();
			_TranslateAnimation = new TranslateAnimation(
					mCurrentCheckedRadioLeft, 0f, 0f, 0f);
			_AnimationSet.addAnimation(_TranslateAnimation);
			// _AnimationSet.setFillBefore(false);
			// _AnimationSet.setFillAfter(true);
			_AnimationSet.setDuration((long) Math
					.abs(mCurrentCheckedRadioLeft - 0));

			// mImageView.bringToFront();
			mImageView.startAnimation(_AnimationSet);// 开始上面蓝色横条图片的动画切换

			handler.sendEmptyMessage(1);

		} else if (checkedId == R.id.btn2) {
			changeRadioTextColorByOnClick(mRadioButton2);
			tab_postion = getString(R.string.state_ship_doing_gang);
			with = group.getChildAt(1).getWidth();
			_TranslateAnimation = new TranslateAnimation(
					mCurrentCheckedRadioLeft, with, 0f, 0f);

			_AnimationSet.addAnimation(_TranslateAnimation);
			// _AnimationSet.setFillBefore(false);
			_AnimationSet.setFillAfter(true);
			_AnimationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft
					- with));

			// mImageView.bringToFront();
			mImageView.startAnimation(_AnimationSet);

			handler.sendEmptyMessage(2);

		} else if (checkedId == R.id.btn3) {
			changeRadioTextColorByOnClick(mRadioButton3);
			tab_postion = getString(R.string.state_ship_plan_out_gang);
			with = group.getChildAt(2).getWidth();
			_TranslateAnimation = new TranslateAnimation(
					mCurrentCheckedRadioLeft, with * 2 + 2, 0f, 0f);

			_AnimationSet.addAnimation(_TranslateAnimation);
			// _AnimationSet.setFillBefore(false);
			_AnimationSet.setFillAfter(true);
			_AnimationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft
					- with * 2));
			// mImageView.bringToFront();
			mImageView.startAnimation(_AnimationSet);

			handler.sendEmptyMessage(3);

		}
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();// 更新当前蓝色横条距离左边的距离
	}

	private void data_deal_with() {
		// TODO Auto-generated method stub
		if (!Tochuli) {
			return;
		}
		if (fromXunCha) {
			if (SelectShipActivity.xunjianShipList != null
					&& SelectShipActivity.xunjianShipList.size() > 0) {
				int the_size = SelectShipActivity.xunjianShipList.size();
				for (int i = 0; i < the_size; i++) {
					String state_ship = (String) (SelectShipActivity.xunjianShipList
							.get(i).get("kacbzt"));
					if (state_ship != null) {
						ship_state = true;
						if (state_ship.equals(getResources().getString(
								R.string.state_ship_plan_come_gang))) {
							Temp_plan_come_gang
									.add(SelectShipActivity.xunjianShipList
											.get(i));
						} else if (state_ship.equals(getResources().getString(
								R.string.state_ship_doing_gang))) {
							tempDoingGang
									.add(SelectShipActivity.xunjianShipList
											.get(i));
						} else if (state_ship.equals(getResources().getString(
								R.string.state_ship_plan_out_gang))) {
							Temp_plan_out_gang
									.add(SelectShipActivity.xunjianShipList
											.get(i));
							tempDoingGang
									.add(SelectShipActivity.xunjianShipList
											.get(i));// 在港船舶中包括预离港
						}
					} else {
						ship_state = false;
					}

				}
			}
		} else {
			if (SelectShipActivity.normalShipList != null
					&& SelectShipActivity.normalShipList.size() > 0) {
				int the_size = SelectShipActivity.normalShipList.size();
				for (int i = 0; i < the_size; i++) {
					String state_ship = (String) (SelectShipActivity.normalShipList
							.get(i).get("kacbzt"));
					if (state_ship != null) {
						ship_state = true;
						if (state_ship.equals(getResources().getString(
								R.string.state_ship_plan_come_gang))) {
							Temp_plan_come_gang
									.add(SelectShipActivity.normalShipList
											.get(i));
						} else if (state_ship.equals(getResources().getString(
								R.string.state_ship_doing_gang))) {
							tempDoingGang.add(SelectShipActivity.normalShipList
									.get(i));
						} else if (state_ship.equals(getResources().getString(
								R.string.state_ship_plan_out_gang))) {
							Temp_plan_out_gang
									.add(SelectShipActivity.normalShipList
											.get(i));
							tempDoingGang.add(SelectShipActivity.normalShipList
									.get(i));// 在港船舶中包括预离港
						}
					} else {
						ship_state = false;
					}

				}
			}
		}
		Tochuli = false;

	}

	/**
	 * 根据用户点击设置RadioButton的文本颜色
	 * 
	 * @param radioButton
	 */
	public void changeRadioTextColorByOnClick(RadioButton radioButton) {
		if (mRadioButton1 == radioButton) {
			mRadioButton1.setText(Html.fromHtml("<font color=\"#33b5e5\">"
					+ getResources().getString(
							R.string.state_ship_plan_come_gang) + "</font>"));
		} else {
			mRadioButton1.setText(Html.fromHtml("<font color=\"#FFFFFF\">"
					+ getResources().getString(
							R.string.state_ship_plan_come_gang) + "</font>"));
		}
		if (mRadioButton2 == radioButton) {
			mRadioButton2.setText(Html.fromHtml("<font color=\"#33b5e5\">"
					+ getResources().getString(R.string.state_ship_doing_gang)
					+ "</font>"));
		} else {
			mRadioButton2.setText(Html.fromHtml("<font color=\"#FFFFFF\">"
					+ getResources().getString(R.string.state_ship_doing_gang)
					+ "</font>"));
		}
		if (mRadioButton3 == radioButton) {
			mRadioButton3.setText(Html.fromHtml("<font color=\"#33b5e5\">"
					+ getResources().getString(
							R.string.state_ship_plan_out_gang) + "</font>"));
		} else {
			mRadioButton3.setText(Html.fromHtml("<font color=\"#FFFFFF\">"
					+ getResources().getString(
							R.string.state_ship_plan_out_gang) + "</font>"));

		}
	}

	/**
	 * 设置所有未选中
	 */
	public void setRadioTextColorAllNoCheck() {
		// mRadioButton1.setTextColor(android.R.color.white);
		// mRadioButton2.setTextColor(android.R.color.white);
		// mRadioButton3.setTextColor(android.R.color.white);
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (HTTPREQUEST_TYPE_FOR_BINDSHIP == offLineRequestType) {
			HgqwToast.makeText(SelectShipResultActivity.this,
					R.string.no_web_cannot_bind, HgqwToast.LENGTH_LONG).show();
		}

	}

	/**
	 * 
	 * @description 显示已经绑定船舶的对话框
	 * @date 2014-4-4
	 * @author zhaotf
	 */
	private void showBondDialog() {

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setMessage(getString(R.string.tishi_content))
				.setCancelable(false)
				.setPositiveButton(R.string.queding,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								// 直接跳回二级页面
								arg0.cancel();
								returnActivity();
							}
						})
				.setNegativeButton(R.string.cancel,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.cancel();
							}
						}).create();
		alertDialog.show();
	}

	private OffDataDownloadForBd dataDownload = null;

	private void downloadOfflineData(HashMap<String, Object> bindMap,
			int bindType) {
		if (GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == bindType
				|| GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == bindType) {
			dataDownload = new OffDataDownloadForBd(handler, bindMap,
					OffLineUtil.DOWNLOAD_FOR_KACBQK, 3);
			// dataDownload = new OffDataDownloadForBd(handler, bindMap,
			// OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_HC, 3);
		} else {
			dataDownload = new OffDataDownloadForBd(handler, bindMap,
					OffLineUtil.DOWNLOAD_FOR_QYXX, 3);
			// dataDownload = new OffDataDownloadForBd(handler, bindMap,
			// OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_KKID, 3);
		}
		dataDownload.requestAgain();
		// progressDialog = new ProgressDialog(getApplicationContext());
		if (progressDialog != null) {
			progressDialog.setMessage("正在下载离线所需数据");
		}
		// progressDialog.show();
	}

	private StringBuilder stringBuilder = new StringBuilder();
}
