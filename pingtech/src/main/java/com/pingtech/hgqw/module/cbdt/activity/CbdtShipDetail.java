package com.pingtech.hgqw.module.cbdt.activity;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.DutyPersonlistActivity;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.ShipListActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.cbdt.action.CbdtShipAction;
import com.pingtech.hgqw.module.cbdt.utils.OffLineDataUtils;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.HttpRequestUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 显示船舶详情界面，在该界面可以做船舶绑定、解除绑定、船舶抵港、船舶离港、查看执勤人员等操作
 * */
public class CbdtShipDetail extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "ShipDetailActivity";

	private static final String TAG_OFFLINE = "ShipDetailActivity_Offline";

	/** 发起获取船舶详情的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GETDETAIL = 1;

	/** 发起船舶绑定的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_BINDSHIP = 2;

	/** 发起船舶解除绑定的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_UNBINDSHIP = 3;

	/** 发送船舶抵港或离港时间的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_SENDSHIPTIME = 4;

	/** 发起获取船舶抵港或离港类型的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GET_MOVE_INFO = 5;

	/** 发起是否有船舶抵港时间的http请求的type */
	private static final int HTTPREQUEST_FOR_CBDG_ISYDGSJ = 6;

	/** 发起保存船舶抵港数据的http请求的type */
	private static final int HTTPREQUEST_FOR_CBDG_SAVE_DATA = 7;

	/** 发起是否有船舶离港时间的http请求的type */
	private static final int HTTPREQUEST_FOR_CBDG_ISYLGSJ = 8;

	/** 发起保存船舶离港数据的http请求的type */
	private static final int HTTPREQUEST_FOR_CBLG_SAVE_DATA = 9;

	/** 发起保存船舶靠泊数据的http请求的type */
	private static final int HTTPREQUEST_FOR_CBKB_SAVE_DATA = 10;

	/** 发起保存船舶靠泊数据的http请求的type */
	private static final int HTTPREQUEST_FOR_CBYB_SAVE_DATA = 11;

	/** 发起判断船舶是否具备靠泊条件http请求的type */
	private static final int HTTPREQUEST_FOR_CBKB_CANKB = 12;

	/** 发起判断船舶是否具备移泊条件http请求的type */
	private static final int HTTPREQUEST_FOR_CBYB_CANYB = 13;

	/** 船舶是否有抵港时间 */
	private boolean cbisDgsj = false;

	/** 显示标题 */
	private String title;

	/** 操作功能 */
	private int czgn;

	/** 绑定船舶航次号 */
	private String voyageNumber;

	/**
	 * 区分来自哪个模块
	 * 
	 * @see ShipListActivity
	 */
	private int from;

	private ProgressDialog progressDialog = null;

	private String httpReturnXMLInfo = null;

	/** 用来保存船舶信息 */
	private HashMap<String, Object> shipData = null;

	private String cbybqkIdStr = null;

	private String kacbqkIdStr = null;

	private String qwzlcjIdStr = null;

	private String ymtbwStr = null;

	private String ywmtbwStr = null;

	private String tsxxStr = null;

	private String timeStr = null;

	private boolean fromXunJian = false;

	private boolean fromBindShip = false;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	private boolean saveXjBtnFlag = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		czgn = intent.getIntExtra("czgn", 0);
		voyageNumber = intent.getStringExtra("hc");
		from = intent.getIntExtra("from", 0);
		fromXunJian = intent.getBooleanExtra("fromxunchaxunjian", false);
		fromBindShip = intent.getBooleanExtra("frombindship", false);
		saveXjBtnFlag = intent.getBooleanExtra("saveXjBtnFlag", false);
		super.onCreate(savedInstanceState, R.layout.shipdetail);

		findViewById(R.id.ksyf_ybd_an).setVisibility(View.GONE);
		
		Log.i(TAG, "onCreate()");
		if (title != null) {
			if (from == GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">" + title);
			} else if (from == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">" + title);
			} else if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + title);
			} else if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				setMyActiveTitle(getString(R.string.kakoumanager) + ">" + title);
				findViewById(R.id.ship).setVisibility(View.GONE);
				findViewById(R.id.kk).setVisibility(View.VISIBLE);
			}
		} else {
			if (fromXunJian) {
				if (fromBindShip) {
					setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.shipinfo));
				} else {
					setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.shipinfo));
				}
			} else if (from == GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.shipinfo));
			} else if (from == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.shipinfo));
			} else if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.shipinfo));
			} else if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				setMyActiveTitle(getString(R.string.kakoumanager) + ">" + getString(R.string.kakou_band) + ">" + getString(R.string.kakouinfo));
				findViewById(R.id.ship).setVisibility(View.GONE);
				findViewById(R.id.kk).setVisibility(View.VISIBLE);
			}
		}
		if (fromXunJian) {
			if (!fromBindShip) {
				findViewById(R.id.shipdetail_submit).setVisibility(View.GONE);
			}
			Button dutybtn = (Button) findViewById(R.id.shipdetail_duty);
			if (saveXjBtnFlag) {
				findViewById(R.id.shipdetail_btn_layout02).setVisibility(View.VISIBLE);// 显示保存记录按钮
			}
			dutybtn.setVisibility(View.VISIBLE);
			dutybtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("hc", voyageNumber);
					intent.putExtra("from", "0");
					intent.setClass(getApplicationContext(), DutyPersonlistActivity.class);
					startActivity(intent);
				}
			});
		}
		Button btn = (Button) findViewById(R.id.shipdetail_submit);

		String bdzt = intent.getStringExtra("bdzt");
		if (title == null && bdzt != null && !"未绑定".equals(bdzt)) {
			btn.setText("已绑定");
			btn.setEnabled(false);
		} else {
			if (title == null) {
				if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					btn.setText(R.string.kakou_band);
				} else {
					btn.setText(R.string.bindShip);
				}
			} else {
				btn.setText(title);
			}
			btn.setEnabled(true);
			btn.setOnClickListener(new OnClickListener() {
				/** 执行操作 */
				public void onClick(View v) {
					btnOnClick();
				}
			});
		}

		if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
			((TextView) findViewById(R.id.kk_name)).setText(Html.fromHtml(getString(R.string.kk_name) + "：" + "<font color=\"#acacac\">"
					+ intent.getStringExtra("kkmc") + "</font>"));
			((TextView) findViewById(R.id.kk_rang)).setText(Html.fromHtml(getString(R.string.kk_rang) + "：" + "<font color=\"#acacac\">"
					+ intent.getStringExtra("kkfw") + "</font>"));
			((TextView) findViewById(R.id.kk_addr)).setText(Html.fromHtml(getString(R.string.kk_addr) + "：" + "<font color=\"#acacac\">"
					+ intent.getStringExtra("kkxx") + "</font>"));
			voyageNumber = intent.getStringExtra("id");
		} else {
			// 判断“开启离线”开关是否开启，若开启使用离线数据
			if (!getState(FunctionSetting.kqlx, false)) {
				onLoadShipDetail();
			} else {
				String url = "getShipInfo";
				if (progressDialog != null) {
					return;
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
				progressDialog = new ProgressDialog(this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), url, NVPairTOMap.nameValuePairTOMap(params),
						HTTPREQUEST_TYPE_FOR_GETDETAIL);
			}
		}
	}

	/**
	 * 
	 * @方法名：btnOnClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-9-29 下午2:37:00
	 */
	private void btnOnClick() {
		if (title == null || title.equals(getString(R.string.bindShip))) {
			// 执行船舶绑定
			if (shipData != null) {
				String url = "buildRelation";
				if (progressDialog != null) {
					return;
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("pdacode", DeviceUtils.getIMEI()));
				params.add(new BasicNameValuePair("PDACode", DeviceUtils.getIMEI()));
				params.add(new BasicNameValuePair("bindState", "1"));
				params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
				params.add(new BasicNameValuePair("bindType", from + ""));
				// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
				params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));

				progressDialog = new ProgressDialog(CbdtShipDetail.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(CbdtShipDetail.this, url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
			}
		} else if (title.equals(getString(R.string.unbindShip)) || title.equals(getString(R.string.kakou_unband))) {
			// 离线模式无法进行船舶解绑
			if (!BaseApplication.instent.getWebState()) {
				HgqwToast.toast(getApplicationContext(), getString(R.string.no_web_cannot_unbind), HgqwToast.LENGTH_LONG);
				return;
			}

			// 执行船舶解除绑定
			if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				if (progressDialog != null) {
					return;
				}
				showDialogForUnbind(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER);

			} else {
				if (progressDialog != null) {
					return;
				}
				showDialogForUnbind(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);

			}
		} else if (czgn != 0) {
			// 判断“开启离线”开关是否开启，若开启使用离线数据
			if (getState(FunctionSetting.kqlx, false)) {
				// 离线业务方法
				offLine();
				return;
			}
			// 船舶抵港0、离港1、靠泊2、移泊3
			String url = "getMoveInfo";
			if (progressDialog != null) {
				return;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
			params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
			switch (czgn) {
			case ManagerFlag.PDA_CBDT_CBDG:
				params.add(new BasicNameValuePair("type", "0"));
				break;
			case ManagerFlag.PDA_CBDT_CBLG:
				params.add(new BasicNameValuePair("type", "1"));
				break;
			case ManagerFlag.PDA_CBDT_CBKB:
				params.add(new BasicNameValuePair("type", "2"));
				break;
			case ManagerFlag.PDA_CBDT_CBYB:
				params.add(new BasicNameValuePair("type", "3"));
				break;
			default:
				break;
			}
			progressDialog = new ProgressDialog(CbdtShipDetail.this);
			progressDialog.setTitle(getString(R.string.sending));
			progressDialog.setMessage(getString(R.string.waiting));
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
			NetWorkManager.request(CbdtShipDetail.this, url, params, HTTPREQUEST_TYPE_FOR_GET_MOVE_INFO);
		}
	}

	/**
	 * 
	 * @方法名：offLine
	 * @功能说明：离线业务，保存到本地
	 * @author liums
	 * @date 2013-10-9 下午5:08:32
	 */
	private void offLine() {
		Log.i(TAG_OFFLINE, "offLine()");
		switch (czgn) {
		case ManagerFlag.PDA_CBDT_CBDG:
			offLineCbdg();
			break;
		case ManagerFlag.PDA_CBDT_CBLG:
			offLineCblg();
			break;
		case ManagerFlag.PDA_CBDT_CBKB:
			offLineCbkb();
			break;
		case ManagerFlag.PDA_CBDT_CBYB:
			offLineCbyb();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：offLineCbdg
	 * @功能说明：船舶移泊业务
	 * @author liums
	 * @date 2013-10-9 下午5:21:19
	 */
	private void offLineCbyb() {
		Map map = new HashMap<String, Object>();
		map.put("voyageNumber", voyageNumber);
		// 判断是否能抵港
		OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "canYb", map, HTTPREQUEST_FOR_CBYB_CANYB);

	}

	/**
	 * @方法名：offLineCbybCanybResult
	 * @功能说明：对 "是否可以移泊   请求结果"进行处理
	 * @author zhaotf
	 * @date 2013-10-24 下午5:52:19
	 * @param res
	 */
	private void offLineCbybCanybResult(Pair<Boolean, Object> res) {
		if (res.first) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.ship_shifting_comfirm);
			builder.setTitle(R.string.info);
			builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					OffData offData = OffLineDataUtils.getCbybOffData(voyageNumber);
					if (offData == null) {
						HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
						return;
					}
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("offData", offData);
					params.put("voyageNumber", voyageNumber);
					OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "saveCbybOffData", params, HTTPREQUEST_FOR_CBYB_SAVE_DATA);
				}
			});
			builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} else {
			Object object = res.second;
			if (StringUtils.isNotEmpty(object)) {
				/**
				 * 返回值与对应内容 1:船舶未抵港，不能移泊 2:船舶已离港，不能移泊
				 */
				if ("1".equals(object)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbyb_ship_nodigang), HgqwToast.LENGTH_SHORT);
				} else if ("2".equals(object)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbyb_ship_hasligang), HgqwToast.LENGTH_SHORT);
				} else if ("3".equals(object)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbyb_ship_cannot_yb), HgqwToast.LENGTH_SHORT);
				} else {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbyb_ship_notiaojian), HgqwToast.LENGTH_SHORT);
				}
			} else {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbyb_ship_notiaojian), HgqwToast.LENGTH_SHORT);
			}
		}
	}

	/**
	 * 
	 * @方法名：offLineCblg
	 * @功能说明：船舶离港业务
	 * @author liums
	 * @date 2013-10-9 下午5:21:19
	 */
	private void offLineCblg() {
		Map map = new HashMap<String, Object>();
		map.put("voyageNumber", voyageNumber);
		OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "hasLg", map, HTTPREQUEST_FOR_CBDG_ISYLGSJ);
	}

	/**
	 * 
	 * @方法名：offLineCbdg
	 * @功能说明：船舶靠泊业务
	 * @author liums
	 * @date 2013-10-9 下午5:21:19
	 */
	private void offLineCbkb() {
		Map map = new HashMap<String, Object>();
		map.put("voyageNumber", voyageNumber);
		// 判断是否能靠泊
		OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "canKb", map, HTTPREQUEST_FOR_CBKB_CANKB);
	}

	/**
	 * @方法名：offLineCbkbResult
	 * @功能说明：对 "是否可以靠泊  请求结果"进行处理
	 * @author zhaotf
	 * @date 2013-10-24 下午5:09:34
	 */
	private void offLineCbkbCankbResult(Pair<Boolean, Object> res) {
		if (res.first) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.ship_berthing_comfirm);
			builder.setTitle(R.string.info);
			builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					OffData offData = OffLineDataUtils.getCbkbOffData(voyageNumber);
					if (offData == null) {
						HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
						return;
					}
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("offData", offData);
					params.put("voyageNumber", voyageNumber);
					OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "saveCbkbOffData", params, HTTPREQUEST_FOR_CBKB_SAVE_DATA);
				}
			});
			builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} else {
			Object object = res.second;
			if (StringUtils.isNotEmpty(object)) {
				/**
				 * 返回值与对应内容 1:船舶未抵港，不能靠泊 2:船舶已离港，不能靠泊
				 */
				if ("1".equals(object)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbkb_ship_nodigang), HgqwToast.LENGTH_SHORT);
				} else if ("2".equals(object)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbkb_ship_hasligang), HgqwToast.LENGTH_SHORT);
				} else if ("3".equals(object)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbkb_ship_cannot_kb), HgqwToast.LENGTH_SHORT);
				} else {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbkb_ship_notiaojian), HgqwToast.LENGTH_SHORT);
				}
			} else {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbkb_ship_notiaojian), HgqwToast.LENGTH_SHORT);
			}
		}

	}

	/**
	 * 
	 * @方法名：offLineCbdg
	 * @功能说明：船舶抵港业务
	 * @author liums
	 * @date 2013-10-9 下午5:21:19
	 */
	private void offLineCbdg() {
		Map map = new HashMap<String, Object>();
		map.put("voyageNumber", voyageNumber);
		// 判断是否有抵港时间
		OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "canDg", map, HTTPREQUEST_FOR_CBDG_ISYDGSJ);
	}

	private void showDialogForUnbind(final int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (type) {
		case GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER:
			builder.setMessage(R.string.kk_unbind_comfirm);
			break;
		case GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER:
			builder.setMessage(R.string.ship_unbind_comfirm);
			break;
		default:
			break;
		}
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String url = null;
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				switch (type) {
				case GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER:
					url = "buildKkRelation";
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("pdacode", DeviceUtils.getIMEI()));
					params.add(new BasicNameValuePair("PDACode", DeviceUtils.getIMEI()));
					params.add(new BasicNameValuePair("bindState", "0"));
					params.add(new BasicNameValuePair("kkID", voyageNumber));
					params.add(new BasicNameValuePair("bindType", from + ""));
					// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
					params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_KK + ""));

					progressDialog = new ProgressDialog(CbdtShipDetail.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					break;
				case GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER:
					url = "buildRelation";
					params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("pdacode", DeviceUtils.getIMEI()));
					params.add(new BasicNameValuePair("PDACode", DeviceUtils.getIMEI()));
					params.add(new BasicNameValuePair("bindState", "0"));
					params.add(new BasicNameValuePair("bindType", from + ""));
					// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
					params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));

					progressDialog = new ProgressDialog(CbdtShipDetail.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					break;
				default:
					break;
				}
				NetWorkManager.request(CbdtShipDetail.this, url, params, HTTPREQUEST_TYPE_FOR_UNBINDSHIP);

			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();

	}

	/** 发送抵港或离港时间之前，显示dialog让用户确认 */
	private void onShowSendTimeQuestDialog(int id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(id);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 执行发送离港、抵港时间
				String url = "sendTime";
				if (progressDialog != null) {
					return;
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
				params.add(new BasicNameValuePair("time", timeStr));
				params.add(new BasicNameValuePair("type", tsxxStr));
				params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
				params.add(new BasicNameValuePair("cbybqkid", cbybqkIdStr));
				params.add(new BasicNameValuePair("kacbqkid", kacbqkIdStr));
				params.add(new BasicNameValuePair("qwzlcjid", qwzlcjIdStr));
				params.add(new BasicNameValuePair("ymtbw", ymtbwStr));
				params.add(new BasicNameValuePair("ywmtbw", ywmtbwStr));

				progressDialog = new ProgressDialog(CbdtShipDetail.this);
				progressDialog.setTitle(getString(R.string.sending));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(CbdtShipDetail.this, url, params, HTTPREQUEST_TYPE_FOR_SENDSHIPTIME);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/** 获取船舶详情信息 */
	private void onLoadShipDetail() {

		String url = "getShipInfo";
		if (progressDialog != null) {
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_GETDETAIL);
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

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * HashMap<String, Object> _BindShip =
		 * SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS +
		 * ""); String kacbzt = (String) _BindShip.get("kacbzt"); if
		 * (ManagerFlag.PDA_CBDT_CBDG == czgn && (!"预到港".equals(kacbzt))) {
		 * findViewById(R.id.shipdetail_submit).setEnabled(false); }
		 */

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FlagManagers.CUSTOM_DIALOG_FOR_EXIT && resultCode == RESULT_OK) {
			String password = data.getStringExtra("password");
			this.validatePassword(password);
		}

	}

	/** 处理平台返回的数据 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {

		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_GETDETAIL) {
			boolean success = false;

			if (StringUtils.isNotEmpty(str)) {
				success = onParseXMLData(str);
			}
			if (success) {
				Intent intent = getIntent();
				((TextView) findViewById(R.id.shipdetail_name)).setText(Html.fromHtml(getString(R.string.shipchinaname) + "："
						+ "<font color=\"#acacac\">" + ((String) shipData.get("cbzwm") == null ? "" : (String) shipData.get("cbzwm")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_checkclass)).setText(Html.fromHtml(getString(R.string.shipcheckclass) + "："
						+ "<font color=\"#acacac\">" + ((String) shipData.get("jcfl") == null ? "" : (String) shipData.get("jcfl")) + "</font>"));
				((TextView) findViewById(R.id.jczt)).setText(Html.fromHtml(getString(R.string.shipcheckstatus) + "：" + "<font color=\"#acacac\">"
						+ ((String) shipData.get("dqjczt") == null ? "" : (String) shipData.get("dqjczt")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_enname)).setText(Html.fromHtml("" + getString(R.string.shipenglishname) + "："
						+ "<font color=\"#acacac\">" + (((String) shipData.get("cbywm") == null ? "" : (String) shipData.get("cbywm"))) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_company)).setText(Html.fromHtml("<font color=\"#acacac\">"
						+ ((String) shipData.get("cdgs") == null ? "" : (String) shipData.get("cdgs")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_country)).setText(Html.fromHtml(getString(R.string.shipcountry) + "："
						+ "<font color=\"#acacac\">"
						+ DataDictionary.getCountryName(((String) shipData.get("gj") == null ? "" : (String) shipData.get("gj"))) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_peoplenumber)).setText(Html.fromHtml(getString(R.string.shippeoplenumber) + "："
						+ "<font color=\"#acacac\">" + (((String) shipData.get("cys")) == null ? "" : ((String) shipData.get("cys"))) + "</font>"));
				if (StringUtils.isEmpty(shipData.get("cbxz"))) {
					((TextView) findViewById(R.id.shipdetail_property)).setText(Html.fromHtml(getString(R.string.shipproperty)
							+ "："
							+ "<font color=\"#acacac\">"
							+ DataDictionary.getDataDictionaryName((intent.getStringExtra("cbxz") == null ? "" : intent.getStringExtra("cbxz")),
									DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE) + "</font>"));
				} else {
					((TextView) findViewById(R.id.shipdetail_property)).setText(Html.fromHtml(getString(R.string.shipproperty)
							+ "："
							+ "<font color=\"#acacac\">"
							+ DataDictionary.getDataDictionaryName(((String) shipData.get("cbxz") == null ? "" : (String) shipData.get("cbxz")),
									DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE) + "</font>"));
				}

				((TextView) findViewById(R.id.shipdetail_loginnumber))
						.setText(Html.fromHtml(getString(R.string.shippeopleloginnumber) + "：" + "<font color=\"#acacac\">"
								+ (((String) shipData.get("dlcys")) == null ? "" : ((String) shipData.get("dlcys"))) + "</font>"));
				if (StringUtils.isEmpty(shipData.get("tkwz"))) {
					((TextView) findViewById(R.id.shipdetail_pos)).setText(Html.fromHtml("<font color=\"#acacac\">"
							+ ((intent.getStringExtra("tkwz") == null ? "" : intent.getStringExtra("tkwz"))) + "</font>"));
				} else {
					((TextView) findViewById(R.id.shipdetail_pos)).setText(Html.fromHtml("<font color=\"#acacac\">"
							+ ((String) shipData.get("tkwz") == null ? "" : (String) shipData.get("tkwz")) + "</font>"));
				}
				String yjsj = (String) shipData.get("yjsj");
				String zjsj = (String) shipData.get("zjsj");
				if (findViewById(R.id.yjsj) != null) {
					findViewById(R.id.yjsj).setVisibility(View.VISIBLE);
					if (StringUtils.isNotEmpty(yjsj)) {
						((TextView) findViewById(R.id.yjsj)).setText(Html.fromHtml(getString(R.string.yjsj) + "：" + "<font color=\"#acacac\">" + yjsj
								+ "</font>"));
					} else {
						((TextView) findViewById(R.id.yjsj)).setText(Html.fromHtml(getString(R.string.yjsj) + "：" + "<font color=\"#acacac\">"
								+ "</font>"));

					}
				}
				if (findViewById(R.id.zjsj) != null) {
					findViewById(R.id.zjsj).setVisibility(View.VISIBLE);
					if (StringUtils.isNotEmpty(yjsj)) {
						((TextView) findViewById(R.id.zjsj)).setText(Html.fromHtml(getString(R.string.zjsj) + "：" + "<font color=\"#acacac\">" + zjsj
								+ "</font>"));
					} else {
						((TextView) findViewById(R.id.zjsj)).setText(Html.fromHtml(getString(R.string.zjsj) + "：" + "<font color=\"#acacac\">"
								+ "</font>"));

					}
				}

				((TextView) findViewById(R.id.shipdetail_customnumber))
						.setText(Html.fromHtml(getString(R.string.shipcustomnumber) + "：" + "<font color=\"#acacac\">"
								+ (((String) shipData.get("dlrys")) == null ? "" : ((String) shipData.get("dlrys"))) + "</font>"));
				if (StringUtils.isEmpty(shipData.get("kacbzt"))) {
					((TextView) findViewById(R.id.shipdetail_kacbzt)).setText(Html.fromHtml(getString(R.string.kacbzt) + "："
							+ "<font color=\"#acacac\">" + (intent.getStringExtra("kacbzt") == null ? "" : intent.getStringExtra("kacbzt"))
							+ "</font>"));
				} else {
					((TextView) findViewById(R.id.shipdetail_kacbzt)).setText(Html.fromHtml(getString(R.string.kacbzt) + "："
							+ "<font color=\"#acacac\">" + ((String) shipData.get("kacbzt") == null ? "" : (String) shipData.get("kacbzt"))
							+ "</font>"));

					// 根据口岸船舶情况，显示时间
					if (getString(R.string.ydg).equals(shipData.get("kacbzt"))) {
						if (findViewById(R.id.cbsj_yjdgsj) != null) {
							findViewById(R.id.cbsj_yjdgsj).setVisibility(View.VISIBLE);
							if (StringUtils.isNotEmpty(shipData.get("yjdgsj"))) {
								((TextView) findViewById(R.id.cbsj_yjdgsj)).setText(Html.fromHtml(getString(R.string.yjdgsj) + "："
										+ "<font color=\"#acacac\">" + shipData.get("yjdgsj") + "</font>"));
							}
						}
					} else if (getString(R.string.zg).equals(shipData.get("kacbzt"))) {
						if (findViewById(R.id.cbsj_dgsj) != null) {
							findViewById(R.id.cbsj_dgsj).setVisibility(View.VISIBLE);
							if (StringUtils.isNotEmpty(shipData.get("dgsj"))) {
								((TextView) findViewById(R.id.cbsj_dgsj)).setText(Html.fromHtml(getString(R.string.dgsj) + "："
										+ "<font color=\"#acacac\">" + shipData.get("dgsj") + "</font>"));
							}
						}
					} else if (getString(R.string.ylg).equals(shipData.get("kacbzt"))) {
						if (findViewById(R.id.cbsj_dgsj) != null && findViewById(R.id.cbsj_yjlgsj) != null) {
							findViewById(R.id.cbsj_dgsj).setVisibility(View.VISIBLE);
							findViewById(R.id.cbsj_yjlgsj).setVisibility(View.VISIBLE);
							if (StringUtils.isNotEmpty(shipData.get("dgsj"))) {
								((TextView) findViewById(R.id.cbsj_dgsj)).setText(Html.fromHtml(getString(R.string.dgsj) + "："
										+ "<font color=\"#acacac\">" + shipData.get("dgsj") + "</font>"));
							}
							if (StringUtils.isNotEmpty(shipData.get("yjlgsj"))) {
								((TextView) findViewById(R.id.cbsj_yjlgsj)).setText(Html.fromHtml(getString(R.string.yjlgsj) + "："
										+ "<font color=\"#acacac\">" + shipData.get("yjlgsj") + "</font>"));
							}
						}
					}

				}
				if (ManagerFlag.PDA_CBDT_CBDG == czgn) {
					if (ManagerFlag.PDA_CBDT_CBDG == czgn && (!"预到港".equals(shipData.get("kacbzt")))) {
						findViewById(R.id.shipdetail_submit).setEnabled(false);
					} else {
						findViewById(R.id.shipdetail_submit).setEnabled(true);
					}
				}
			} else {
				Intent intent = getIntent();
				((TextView) findViewById(R.id.shipdetail_name)).setText(Html.fromHtml(getString(R.string.shipchinaname) + "："
						+ "<font color=\"#acacac\">" + (intent.getStringExtra("cbzwm") == null ? "" : intent.getStringExtra("cbzwm")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_checkclass)).setText(Html.fromHtml(getString(R.string.shipcheckclass) + "："
						+ "<font color=\"#acacac\">" + (intent.getStringExtra("jcfl") == null ? "" : intent.getStringExtra("jcfl")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_enname)).setText(Html.fromHtml("" + getString(R.string.shipenglishname) + "："
						+ "<font color=\"#acacac\">" + (intent.getStringExtra("cbywm") == null ? "" : intent.getStringExtra("cbywm")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_company)).setText(Html.fromHtml("<font color=\"#acacac\">"
						+ (intent.getStringExtra("cdgs") == null ? "" : intent.getStringExtra("cdgs")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_country)).setText(Html.fromHtml(getString(R.string.shipcountry) + "："
						+ "<font color=\"#acacac\">"
						+ DataDictionary.getCountryName((intent.getStringExtra("gj") == null ? "" : intent.getStringExtra("gj"))) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_peoplenumber)).setText(Html.fromHtml(getString(R.string.shippeoplenumber) + "："
						+ "<font color=\"#acacac\">" + (intent.getStringExtra("cys") == null ? "" : intent.getStringExtra("cys")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_property)).setText(Html.fromHtml(getString(R.string.shipproperty)
						+ "："
						+ "<font color=\"#acacac\">"
						+ DataDictionary.getDataDictionaryName((intent.getStringExtra("cbxz") == null ? "" : intent.getStringExtra("cbxz")),
								DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_loginnumber)).setText(Html.fromHtml(getString(R.string.shippeopleloginnumber) + "："
						+ "<font color=\"#acacac\">" + (intent.getStringExtra("dlcys") == null ? "" : intent.getStringExtra("dlcys")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_pos)).setText(Html.fromHtml("<font color=\"#acacac\">"
						+ (intent.getStringExtra("tkwz") == null ? "" : intent.getStringExtra("tkwz")) + "</font>"));
				((TextView) findViewById(R.id.jczt)).setText(Html.fromHtml(getString(R.string.shipcheckstatus) + "：" + "<font color=\"#acacac\">"
						+ (intent.getStringExtra("dqjczt") == null ? "" : intent.getStringExtra("dqjczt")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_customnumber)).setText(Html.fromHtml(getString(R.string.shipcustomnumber) + "："
						+ "<font color=\"#acacac\">" + (intent.getStringExtra("dlrys") == null ? "" : intent.getStringExtra("dlrys")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_kacbzt)).setText(Html.fromHtml(getString(R.string.kacbzt) + "：" + "<font color=\"#acacac\">"
						+ (intent.getStringExtra("kacbzt") == null ? "" : intent.getStringExtra("kacbzt")) + "</font>"));
				((Button) findViewById(R.id.shipdetail_submit)).setEnabled(false);
				if (httpReturnXMLInfo != null) {
					HgqwToast.toast(getApplicationContext(), httpReturnXMLInfo, HgqwToast.LENGTH_LONG);
				} else {
					HgqwToast.toast(getApplicationContext(), getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG);
				}
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_BINDSHIP) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				shipData.put("bdzt", "已绑定");
				SystemSetting.setBindShip(shipData, from + "");
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.bindship_success), HgqwToast.LENGTH_LONG);
				Intent data = null;
				data = new Intent();
				setResult(RESULT_OK, data);
				finish();
			} else if (str != null && "3".equals(str)) {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.had_bind_ship), HgqwToast.LENGTH_LONG);
			} else {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.bindship_failure), HgqwToast.LENGTH_LONG);
				SystemSetting.setBindShip(null, from + "");
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_UNBINDSHIP) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.unbindship_success), HgqwToast.LENGTH_LONG);
				SystemSetting.setBindShip(null, from + "");
				Flags.peClickFlag = false;
				finish();
			} else {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.unbindship_failure), HgqwToast.LENGTH_LONG);
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_SENDSHIPTIME) {
			if (str != null && "1".equals(str)) {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.send_success), HgqwToast.LENGTH_LONG);
				HashMap<String, Object> _BindShip = SystemSetting.getBindShip(from + "");
				if (tsxxStr.equals("0")) {
					_BindShip.put("dg_time", timeStr);
				} else if (tsxxStr.equals("1")) {
					_BindShip.put("lg_time", timeStr);
				} else if (tsxxStr.equals("2")) {
					_BindShip.put("kb_time", timeStr);
				} else if (tsxxStr.equals("3")) {
					_BindShip.put("yb_time", timeStr);
				}
				finish();
			} else {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.send_failure), HgqwToast.LENGTH_LONG);
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_GET_MOVE_INFO) {
			boolean success = false;
			cbybqkIdStr = "";
			kacbqkIdStr = "";
			qwzlcjIdStr = "";
			ymtbwStr = "";
			ywmtbwStr = "";
			tsxxStr = "";
			if (str != null) {
				success = onParseGetMoveInfoXMLData(str);
			}
			if (success && tsxxStr != null) {
				if (tsxxStr.equals("0")) {
					if (title.equals(getString(R.string.arrive))) {
						onShowSendTimeQuestDialog(R.string.ship_arrive_comfirm);
					} else {
						HgqwToast.toast(CbdtShipDetail.this, getString(R.string.only_ship_leave), HgqwToast.LENGTH_LONG);
					}
				} else if (tsxxStr.equals("1")) {
					if (title.equals(getString(R.string.leave))) {
						onShowSendTimeQuestDialog(R.string.ship_leave_comfirm);
					} else {
						HgqwToast.toast(CbdtShipDetail.this, getString(R.string.only_ship_arrive), HgqwToast.LENGTH_LONG);
					}
				} else if (tsxxStr.equals("2")) {
					onShowSendTimeQuestDialog(R.string.ship_berthing_comfirm);
				} else if (tsxxStr.equals("3")) {
					onShowSendTimeQuestDialog(R.string.ship_shifting_comfirm);
				} else {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG);
				}
			} else {
				if (httpReturnXMLInfo != null) {
					HgqwToast.toast(CbdtShipDetail.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG);
				} else {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG);
				}
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
		} else if (httpRequestType == HttpRequestUtils.SAVE_XJ) {
			if (str != null) {
				if ("-1".equals(str)) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.save_failure)).show();
				} else if ("1".equals(str)) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.save_success)).show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
			}
		}
	}

	/** 解析平台返回的船舶详情信息数据 */
	private boolean onParseXMLData(String str) {

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							if (shipData == null) {
								shipData = new HashMap<String, Object>();
							}
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("hc".equals(parser.getName())) {
						if (success) {
							shipData.put("hc", parser.nextText());
						}
					} else if ("cbzwm".equals(parser.getName())) {
						if (success) {
							shipData.put("cbzwm", parser.nextText());
						}
					} else if ("jcfl".equals(parser.getName())) {
						if (success) {
							shipData.put("jcfl", parser.nextText());
						}
					} else if ("cbywm".equals(parser.getName())) {
						if (success) {
							shipData.put("cbywm", parser.nextText());
						}
					} else if ("gj".equals(parser.getName())) {
						if (success) {
							shipData.put("gj", parser.nextText());
						}
					} else if ("cbxz".equals(parser.getName())) {
						if (success) {
							shipData.put("cbxz", parser.nextText());
						}
					} else if ("tkwz".equals(parser.getName())) {
						if (success) {
							shipData.put("tkwz", parser.nextText());
						}
					} else if ("tkmt".equals(parser.getName())) {
						if (success) {
							shipData.put("tkmt", parser.nextText());
						}
					} else if ("tkbw".equals(parser.getName())) {
						if (success) {
							shipData.put("tkbw", parser.nextText());
						}
					} else if ("cdgs".equals(parser.getName())) {
						if (success) {
							shipData.put("cdgs", parser.nextText());
						}
					} else if ("cys".equals(parser.getName())) {
						if (success) {
							shipData.put("cys", parser.nextText());
						}
					} else if ("dlcys".equals(parser.getName())) {
						if (success) {
							shipData.put("dlcys", parser.nextText());
						}
					} else if ("dlrys".equals(parser.getName())) {
						if (success) {
							shipData.put("dlrys", parser.nextText());
						}
					} else if ("kacbzt".equals(parser.getName())) {
						if (success) {
							String kacbztstr = parser.nextText();
							if (kacbztstr.equals("0")) {
								shipData.put("kacbzt", getString(R.string.ydg));
							} else if (kacbztstr.equals("1")) {
								shipData.put("kacbzt", getString(R.string.zg));
							} else if (kacbztstr.equals("2")) {
								shipData.put("kacbzt", getString(R.string.ylg));
							} else if (kacbztstr.equals("3")) {
								shipData.put("kacbzt", getString(R.string.lg));
							} else {
								shipData.put("kacbzt", "");
							}
						}
					} else if ("dqjczt".equals(parser.getName())) {
						if (success) {
							shipData.put("dqjczt", parser.nextText());
						}
					} else if ("yjsj".equals(parser.getName())) {
						if (success) {
							shipData.put("yjsj", parser.nextText());
						}
					} else if ("zjsj".equals(parser.getName())) {
						if (success) {
							shipData.put("zjsj", parser.nextText());
						}
					} else if ("kacbqkid".equals(parser.getName())) {
						shipData.put("kacbqkid", parser.nextText());
					}
					// 添加的时间字段（抵港时间，预到港时间等）
					else if ("yjdgsj".equals(parser.getName())) {
						if (success) {
							shipData.put("yjdgsj", parser.nextText());
						}
					} else if ("dgsj".equals(parser.getName())) {
						if (success) {
							shipData.put("dgsj", parser.nextText());
						}
					} else if ("yjlgsj".equals(parser.getName())) {
						if (success) {
							shipData.put("yjlgsj", parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

	/** 解析平台返回的离港或抵港类型数据 */
	private boolean onParseGetMoveInfoXMLData(String str) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("tsxx".equals(parser.getName())) {
						if (success) {
							tsxxStr = parser.nextText();
						}
					} else if ("cbybqkid".equals(parser.getName())) {
						if (success) {
							cbybqkIdStr = parser.nextText();
						}
					} else if ("kacbqkid".equals(parser.getName())) {
						if (success) {
							kacbqkIdStr = parser.nextText();
						}
					} else if ("qwzlcjid".equals(parser.getName())) {
						if (success) {
							qwzlcjIdStr = parser.nextText();
						}
					} else if ("ymtbw".equals(parser.getName())) {
						if (success) {
							ymtbwStr = parser.nextText();
						}
					} else if ("ywmtbw".equals(parser.getName())) {
						if (success) {
							ywmtbwStr = parser.nextText();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
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

	public void click(View v) {
		switch (v.getId()) {
		case R.id.shipdetail_save_xj:

			HttpRequestUtils.saveXj(this, voyageNumber, LoginUser.getCurrentLoginUser().getUserID());
			break;
		default:
			break;
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		switch (offLineRequestType) {
		case HTTPREQUEST_FOR_CBDG_ISYDGSJ:
			this.offLineCbdgCanDgResult(res);
			break;
		case HTTPREQUEST_FOR_CBDG_SAVE_DATA:
			this.offLineCbdgSaveResult(res);
			break;
		case HTTPREQUEST_FOR_CBDG_ISYLGSJ:
			this.offLineIfAllowLg(res);
			break;
		case HTTPREQUEST_FOR_CBLG_SAVE_DATA:
			this.offLineCbLgSaveResult(res);
			break;
		case HTTPREQUEST_FOR_CBKB_SAVE_DATA:
			this.offLineCbkbSaveResult(res);
			break;
		case HTTPREQUEST_FOR_CBYB_SAVE_DATA:
			this.offLineCbybSaveResult(res);
			break;

		case HTTPREQUEST_TYPE_FOR_GETDETAIL:
			onHttpResult((res.second == null ? "" : res.second.toString()), offLineRequestType);
			break;

		case HTTPREQUEST_FOR_CBKB_CANKB:
			this.offLineCbkbCankbResult(res);
			break;
		case HTTPREQUEST_FOR_CBYB_CANYB:
			this.offLineCbybCanybResult(res);
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * @方法名：offLineCbdgSaveResult
	 * @功能说明：离线船舶抵港保存数据返回结果
	 * @author 赵琳
	 * @date 2013-10-16 下午3:24:30
	 * @param res
	 */
	private void offLineCbdgSaveResult(Pair<Boolean, Object> res) {
		if (res.first) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.send_success), HgqwToast.LENGTH_LONG);
			CbdtShipDetail.this.finish();
		} else {
			HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
		}
	}

	/**
	 * 
	 * @方法名：offLineCbLgSaveResult
	 * @功能说明：离线船舶离港保存数据结果
	 * @author 赵琳
	 * @date 2013-10-18 下午2:10:14
	 * @param res
	 */
	private void offLineCbLgSaveResult(Pair<Boolean, Object> res) {
		if (res.first) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.send_success), HgqwToast.LENGTH_LONG);
			CbdtShipDetail.this.finish();
		} else {
			HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
		}
	}

	/**
	 * 
	 * @方法名：offLineCbkbSaveResult
	 * @功能说明：线船舶靠泊保存数据结果
	 * @author 赵琳
	 * @date 2013-10-20 上午10:26:44
	 * @param res
	 */
	private void offLineCbkbSaveResult(Pair<Boolean, Object> res) {
		if (res.first) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.send_success), HgqwToast.LENGTH_LONG);
			CbdtShipDetail.this.finish();
		} else {
			HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
		}
	}

	/**
	 * 
	 * @方法名：offLineCbybSaveResult
	 * @功能说明：线船舶移泊保存数据结果
	 * @author 赵琳
	 * @date 2013-10-20 上午10:43:37
	 * @param res
	 */
	private void offLineCbybSaveResult(Pair<Boolean, Object> res) {
		if (res.first) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.send_success), HgqwToast.LENGTH_LONG);
			CbdtShipDetail.this.finish();
		} else {
			HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
		}
	}

	/**
	 * 
	 * @方法名：offLineCbdgCanDgResult
	 * @功能说明：离线船舶抵港是否运行抵港
	 * @author 赵琳
	 * @date 2013-10-16 下午3:23:44
	 * @param res
	 */
	private void offLineCbdgCanDgResult(Pair<Boolean, Object> res) {
		if (res.first) {
			// 运行抵港
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.ship_arrive_comfirm);
			builder.setTitle(R.string.info);
			builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					OffData offData = OffLineDataUtils.getCbdgOffData(voyageNumber);
					if (offData == null) {
						HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
						return;
					}
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("offData", offData);
					params.put("voyageNumber", voyageNumber);
					OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "saveCbdgOffData", params, HTTPREQUEST_FOR_CBDG_SAVE_DATA);
				}
			});
			builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} else {
			if (StringUtils.isNotEmpty(res.second)) {
				if ("1".equals(res.second)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbdg_ship_hasdigang), HgqwToast.LENGTH_SHORT);
				} else if ("2".equals(res.second)) {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbdg_ship_hasligang), HgqwToast.LENGTH_SHORT);
				} else {
					HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbdg_ship_notiaojian), HgqwToast.LENGTH_SHORT);
				}
			} else {
				HgqwToast.toast(CbdtShipDetail.this, getString(R.string.cbdg_ship_notiaojian), HgqwToast.LENGTH_SHORT);
			}
		}

	}

	/**
	 * 
	 * @方法名：offLineCblgIsylgsjResult
	 * @功能说明：判断是否能够离港
	 * @author 赵琳
	 * @date 2013-10-18 下午2:23:22
	 * @param res
	 */
	private void offLineIfAllowLg(Pair<Boolean, Object> res) {
		if (!res.first) {
			if (StringUtils.isNotEmpty(res.second)) {
				HgqwToast.toast(getApplicationContext(), (String) (res.second), HgqwToast.LENGTH_LONG);
				return;
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.ship_leave_comfirm);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				OffData offData = OffLineDataUtils.getCblgOffData(voyageNumber);
				if (offData == null) {
					HgqwToast.toast(getApplicationContext(), getString(R.string.cbdt_czsb), HgqwToast.LENGTH_LONG);
					return;
				}
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("offData", offData);
				params.put("voyageNumber", voyageNumber);
				OffLineManager.request(CbdtShipDetail.this, new CbdtShipAction(), "saveCblgOffData", params, HTTPREQUEST_FOR_CBLG_SAVE_DATA);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
