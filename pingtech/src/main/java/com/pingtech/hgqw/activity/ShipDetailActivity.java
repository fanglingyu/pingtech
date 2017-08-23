package com.pingtech.hgqw.activity;

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
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Pair;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.action.BindShipAction;
import com.pingtech.hgqw.module.bindship.activity.ShipBind;
import com.pingtech.hgqw.module.cbdt.action.CbdtShipAction;
import com.pingtech.hgqw.module.kakou.action.KakouAction;
import com.pingtech.hgqw.module.kakou.activity.KaKouReadCard;
import com.pingtech.hgqw.module.kakou.activity.KakouManager;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.tikou.action.TkglAction;
import com.pingtech.hgqw.module.tikou.activity.TiKouReadCard;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownloadForBd;
import com.pingtech.hgqw.module.xunjian.action.XunJianAction;
import com.pingtech.hgqw.module.xunjian.activity.ReadcardActivity;
import com.pingtech.hgqw.module.xunjian.activity.XunChaXunJian;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.RestoreBindShipInfo;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 显示船舶详情界面，在该界面可以做船舶绑定、解除绑定、船舶抵港、船舶离港、查看执勤人员等操作
 * */
public class ShipDetailActivity extends MyActivity implements OnHttpResult,
		OffLineResult {
	private static final String TAG = "ShipDetailActivity";

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

	/**
	 * 船舶解绑梯口管理
	 */
	private static final int REQUEST_TYPE_FOR_UNBINDTKGL = 6;

	/**
	 * 卡口管理解绑
	 */
	private static final int REQUEST_TYPE_FOR_UNBINDKAKOU = 7;

	/** 显示标题 */
	private String title;

	/** 绑定船舶航次号 */
	private String voyageNumber;

	private String kacbqkid;

	public static final int SAVE_XJ = 1853853770;// 接口名称->十六进制->十进制->取后十位

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
	 * 是否来自梯口快速核查
	 */
	private boolean fromTkkshc = false;

	/**
	 * 是否来自卡口快速核查
	 */
	private boolean fromKkkshc = false;

	/** 是否来自巡查巡检快速核查 */
	private boolean fromXcxjkshc = false;
	
	/** 是否来自勤务指令签收 */
	private boolean fromQwzlqs = false;

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
		voyageNumber = intent.getStringExtra("hc");
		from = intent.getIntExtra("from", 0);
		kacbqkid = intent.getStringExtra("kacbqkid");
		fromXunJian = intent.getBooleanExtra("fromxunchaxunjian", false);
		fromBindShip = intent.getBooleanExtra("frombindship", false);
		cfzgFlag = intent.getBooleanExtra("cfzgFlag", false);
		saveXjBtnFlag = intent.getBooleanExtra("saveXjBtnFlag", false);
		fromTkkshc = intent.getBooleanExtra("fromtkkshc", false);
		fromKkkshc = intent.getBooleanExtra("fromkkkshc", false);
		fromXcxjkshc = intent.getBooleanExtra("fromXCXJkshc", false);
		fromQwzlqs = intent.getBooleanExtra("fromQwzlqs", false);
		
		if (cfzgFlag) {
			super.onCreateForCfzg(savedInstanceState, R.layout.shipdetail);
		} else {
			super.onCreate(savedInstanceState, R.layout.shipdetail);
		}

		if (fromTkkshc || fromKkkshc || fromXcxjkshc) {
			findViewById(R.id.ksyf_ybd_an).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.ksyf_ybd_an).setVisibility(View.GONE);
		}

		Log.i(TAG, "onCreate()");
		if (title != null) {
			if (from == GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">" + title);
			} else if (from == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">" + title);
			} else if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
						+ title);
			} else if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				setMyActiveTitle(getString(R.string.kakoumanager) + ">" + title);
				findViewById(R.id.ship).setVisibility(View.GONE);
				findViewById(R.id.kk).setVisibility(View.VISIBLE);
			}
		} else {
			if (fromXunJian) {
				if (fromBindShip) {
					setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
							+ getString(R.string.bindShip) + ">"
							+ getString(R.string.shipinfo));
				} else {
					setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
							+ getString(R.string.shipinfo));
				}
			} else if (from == GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.shipinfo));
			} else if (from == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.shipinfo));
			} else if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">"
						+ getString(R.string.bindShip) + ">"
						+ getString(R.string.shipinfo));
			} else if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
				setMyActiveTitle(getString(R.string.kakoumanager) + ">"
						+ getString(R.string.kakou_band) + ">"
						+ getString(R.string.kakouinfo));
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
				findViewById(R.id.shipdetail_btn_layout02).setVisibility(
						View.VISIBLE);// 显示保存记录按钮
			}
			dutybtn.setVisibility(View.VISIBLE);
			dutybtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// 判断网络是否可用，如果不可用则提示
					if (BaseApplication.instent.getWebState()) {
						// 查看执勤人员
						Intent intent = new Intent();
						intent.putExtra("hc", voyageNumber);
						intent.putExtra("from", "0");
						intent.setClass(getApplicationContext(),
								DutyPersonlistActivity.class);
						startActivity(intent);
					} else {
						HgqwToast
								.toast(R.string.no_web_cannot_check_duty_person);
					}

				}
			});
		}
		Button btn = (Button) findViewById(R.id.shipdetail_submit);

		// 解绑
		Button kshcUnbindButton = (Button) findViewById(R.id.ship_unbind);

		kshcUnbindButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
					showDialogForUnbind(1);
				} else {
					showDialogForUnbind(from);
				}

				// showDialogForUnbind(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER);
			}
		});

		// 刷卡登记
		Button kshcSlotCardButton = (Button) findViewById(R.id.slotcard_dengji);

		kshcSlotCardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (from == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
					Intent intent = new Intent();
					intent.putExtra("title", getString(R.string.tikoumanager)
							+ ">" + getString(R.string.paycard));
					intent.putExtra("from", "02");
					intent.setClass(getApplicationContext(),
							TiKouReadCard.class);
					startActivity(intent);
				} else if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					HashMap<String, Object> bindData = SystemSetting
							.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER
									+ "");
					Intent intent = new Intent();
					intent.putExtra("hc", (String) bindData.get("id"));
					intent.putExtra("kkmc", (String) bindData.get("kkmc"));
					intent.putExtra("title", getString(R.string.kakoumanager)
							+ ">" + getString(R.string.paycard));
					intent.putExtra("cardtype",
							KaKouReadCard.READCARD_TYPE_ID_CARD);
					intent.putExtra("from", "01");
					intent.setClass(getApplicationContext(),
							KaKouReadCard.class);
					startActivity(intent);
					// startActivityForResult(intent,
					// STARTACTIVITY_FOR_READICCARD);
				} else if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {

					HashMap<String, Object> bindData = SystemSetting
							.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN
									+ "");
					Intent intent = new Intent();
					if (StringUtils.isEmpty(SystemSetting.xunJianId)
							&& bindData == null) {
						HgqwToast.makeText(ShipDetailActivity.this,
								R.string.no_ship_place, HgqwToast.LENGTH_LONG)
								.show();
						return;
					}
					intent.putExtra("title", getString(R.string.xunchaxunjian)
							+ ">" + getString(R.string.normalxunjian));
					intent.putExtra("cardtype",
							ReadcardActivity.READCARD_TYPE_ID_CARD);
					intent.putExtra("from", "03");
					if (bindData != null) {
						intent.putExtra("hc", bindData.get("hc") + "");
						intent.putExtra("kacbqkid", bindData.get("kacbqkid")
								+ "");
						intent.putExtra("cbzwm", bindData.get("cbzwm") + "");
					}
					intent.setClass(getApplicationContext(),
							ReadcardActivity.class);
					startActivity(intent);
					// startActivityForResult(intent,
					// STARTACTIVITY_FOR_READICCARD);
				}

			}
		});

		// 查询
		Button kshcQueryButton = (Button) findViewById(R.id.select_ship_query);

		kshcQueryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("frombindship", true);

				if (from == GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER) {
					intent.putExtra("bindtype", 1);
					intent.putExtra("fromtkkshc", true);
					intent.setClass(getApplicationContext(),
							SelectShipActivity.class);
					startActivity(intent);
				} else if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					intent.putExtra("bindtype", 3);
					intent.putExtra("fromkkkshc", true);
					intent.setClass(getApplicationContext(),
							SelectShipActivity.class);
					startActivity(intent);
				} else if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
					if (SystemSetting
							.getBindShipAllSize(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) > 0) {
						onShowQuestFromBindListDialog();
					} else {
						if (BaseApplication.instent.getWebState()) {
							// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定

							intent.putExtra("frombindship", true);
							intent.putExtra("bindtype",
									GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
							intent.putExtra("fromxuncha", true);
							intent.putExtra("fromXCXJkshc", true);
							intent.setClass(getApplicationContext(),
									SelectShipActivity.class);
							startActivity(intent);
						} else {
							HgqwToast.makeText(getApplicationContext(),
									R.string.no_web_cannot_bind_place,
									HgqwToast.LENGTH_LONG).show();
						}
					}
				}

			}
		});

		if (fromTkkshc || fromKkkshc) {
			btn.setVisibility(View.GONE);
		} else {
			btn.setVisibility(View.VISIBLE);
		}

		if(fromQwzlqs){
			
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String url = "buildRelation";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userID", LoginUser
							.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("PDACode",
							SystemSetting.getPDACode()));
					params.add(new BasicNameValuePair("bindState", "1"));
					params.add(new BasicNameValuePair("voyageNumber",
							voyageNumber));
					switch(Flags.PDA_VERSION){
					case Flags.PDA_VERSION_DEFAULT:
						params.add(new BasicNameValuePair("bindType", 2
								+ ""));
						break;
					case Flags.PDA_VERSION_SENTINEL:
						params.add(new BasicNameValuePair("bindType", 1
								+ ""));
						break;
					}
					
					// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
					params.add(new BasicNameValuePair("zqdxlx",
							GlobalFlags.ZQDXLX_CB + ""));
					
					if (progressDialog != null) {
						return;
					}
					progressDialog = new ProgressDialog(
							ShipDetailActivity.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(true);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					if (BaseApplication.instent.getWebState()) {
						NetWorkManager.request(
								ShipDetailActivity.this, url, params,
								HTTPREQUEST_TYPE_FOR_BINDSHIP);
					}
				}
			});
			
		}else{
			if (title == null && !intent.getStringExtra("bdzt").equals("未绑定")) {
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
						if (title == null
								|| title.equals(getString(R.string.bindShip))) {
							// 执行船舶绑定
							if (shipData != null) {
								String url = "buildRelation";
								if (progressDialog != null) {
									return;
								}
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("userID",
										LoginUser.getCurrentLoginUser().getUserID()));
								params.add(new BasicNameValuePair("PDACode",
										SystemSetting.getPDACode()));
								params.add(new BasicNameValuePair("bindState", "1"));
								params.add(new BasicNameValuePair("voyageNumber",
										voyageNumber));
								params.add(new BasicNameValuePair("bindType", from
										+ ""));
								// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
								params.add(new BasicNameValuePair("zqdxlx",
										GlobalFlags.ZQDXLX_CB + ""));

								progressDialog = new ProgressDialog(
										ShipDetailActivity.this);
								progressDialog
										.setTitle(getString(R.string.waiting));
								progressDialog
										.setMessage(getString(R.string.waiting));
								progressDialog.setCancelable(true);
								progressDialog.setIndeterminate(false);
								progressDialog.show();
								NetWorkManager.request(ShipDetailActivity.this,
										url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
							}
						} else if (title.equals(getString(R.string.unbindShip))
								|| title.equals(getString(R.string.kakou_unband))) {
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
						} else if (title.equals(getString(R.string.leave))
								|| title.equals(getString(R.string.arrive))) {
							// 船舶抵港、离港
							String url = "getMoveInfo";
							if (progressDialog != null) {
								return;
							}
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("userID", LoginUser
									.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("voyageNumber",
									voyageNumber));
							params.add(new BasicNameValuePair("type", (title
									.equals(getString(R.string.leave)) ? "1" : "0")));
							progressDialog = new ProgressDialog(
									ShipDetailActivity.this);
							progressDialog.setTitle(getString(R.string.sending));
							progressDialog.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(ShipDetailActivity.this, url,
									params, HTTPREQUEST_TYPE_FOR_GET_MOVE_INFO);
						}
					}

				});
			}
		}
		
	

		if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
			((TextView) findViewById(R.id.kk_name)).setText(Html
					.fromHtml(getString(R.string.kk_name)
							+ "："
							+ "<font color=\"#acacac\">"
							+ (intent.getStringExtra("kkmc") == null ? ""
									: intent.getStringExtra("kkmc"))
							+ "</font>"));
			((TextView) findViewById(R.id.kk_rang)).setText(Html
					.fromHtml(getString(R.string.kk_rang)
							+ "："
							+ "<font color=\"#acacac\">"
							+ (intent.getStringExtra("kkfw") == null ? ""
									: intent.getStringExtra("kkfw"))
							+ "</font>"));
			((TextView) findViewById(R.id.kk_addr)).setText(Html
					.fromHtml(getString(R.string.kk_addr)
							+ "："
							+ "<font color=\"#acacac\">"
							+ (intent.getStringExtra("kkxx") == null ? ""
									: intent.getStringExtra("kkxx"))
							+ "</font>"));
			voyageNumber = intent.getStringExtra("id");
		} else {
			// 如果网络不可用取本地数据,网络可用取网络数据
			if (BaseApplication.instent.getWebState()) {
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
				OffLineManager.request(ShipDetailActivity.this,
						new CbdtShipAction(), url,
						NVPairTOMap.nameValuePairTOMap(params),
						HTTPREQUEST_TYPE_FOR_GETDETAIL);

				// ((TextView)
				// findViewById(R.id.shipdetail_name)).setText(Html.fromHtml(getString(R.string.shipchinaname)
				// + "：" + "<font color=\"#acacac\">"
				// + intent.getStringExtra("cbzwm") + "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_checkclass)).setText(Html.fromHtml(getString(R.string.shipcheckclass)
				// + "："
				// + "<font color=\"#acacac\">" + intent.getStringExtra("jcfl")
				// + "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_enname)).setText(Html.fromHtml(getString(R.string.shipenglishname)
				// + "：" + "<font color=\"#acacac\">"
				// + intent.getStringExtra("cbywm") + "</font>"));
				// ((TextView) findViewById(R.id.shipdetail_company))
				// .setText(Html.fromHtml("<font color=\"#acacac\">" +(
				// intent.getStringExtra("cdgs")==null?"":intent.getStringExtra("cdgs"))
				// + "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_country)).setText(Html.fromHtml(getString(R.string.shipcountry)
				// + "：" + "<font color=\"#acacac\">"
				// + DataDictionary.getCountryName(intent.getStringExtra("gj"))
				// + "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_peoplenumber)).setText(Html.fromHtml(getString(R.string.shippeoplenumber)
				// + "："
				// + "<font color=\"#acacac\">" +
				// (intent.getStringExtra("cys")==null?"":intent.getStringExtra("cys"))
				// + "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_property)).setText(Html.fromHtml(getString(R.string.shipproperty)
				// + "：" + "<font color=\"#acacac\">"
				// +
				// DataDictionary.getDataDictionaryName(intent.getStringExtra("cbxz"),
				// DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE) + "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_loginnumber)).setText(Html.fromHtml(getString(R.string.shippeopleloginnumber)
				// + "："
				// + "<font color=\"#acacac\">" +
				// (intent.getStringExtra("dlcys")==null?"":intent.getStringExtra("dlcys")
				// )+ "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_pos)).setText(Html.fromHtml("<font color=\"#acacac\">"
				// + intent.getStringExtra("tkwz") + "</font>"));
				//
				// ((TextView)
				// findViewById(R.id.shipdetail_customnumber)).setText(Html.fromHtml(getString(R.string.shipcustomnumber)
				// + "："
				// + "<font color=\"#acacac\">" +
				// (intent.getStringExtra("dlrys")==null?"":intent.getStringExtra("dlrys"))
				// + "</font>"));
				// ((TextView)
				// findViewById(R.id.shipdetail_kacbzt)).setText(Html.fromHtml(getString(R.string.kacbzt)
				// + "：" + "<font color=\"#acacac\">"
				// + intent.getStringExtra("kacbzt") + "</font>"));

				// 如果离线，部分字段重新赋值：
			}
		}
		if (cfzgFlag) {
			setMyActiveTitle(getString(R.string.bindShip));
			if (title != null) {
				setMyActiveTitle(getString(R.string.unbindShip));
			}
		}
	}

	/** 进入船舶绑定时，如果存在快速绑定条件，提示是否选择快速绑定 */
	private void onShowQuestFromBindListDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.query_from_bindlist);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.putExtra("title", getString(R.string.bindShip));
						intent.putExtra("cardNumber",
								ShipListActivity.FROM_BINDLIST);
						intent.putExtra("from",
								GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN);
						intent.setClass(getApplicationContext(),
								ShipListActivity.class);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(R.string.no,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
						intent.putExtra("from",
								GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN);
						intent.setClass(getApplicationContext(), ShipBind.class);
						startActivity(intent);
					}
				});
		builder.create().show();
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
		builder.setPositiveButton(R.string.yes,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String url = null;
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						switch (type) {
						case GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER:
							url = "buildKkRelation";
							params.add(new BasicNameValuePair("userID",
									LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode",
									SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "0"));
							params.add(new BasicNameValuePair("kkID",
									voyageNumber));
							params.add(new BasicNameValuePair("bindType", from
									+ ""));
							// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx",
									GlobalFlags.ZQDXLX_KK + ""));

							progressDialog = new ProgressDialog(
									ShipDetailActivity.this);
							progressDialog
									.setTitle(getString(R.string.waiting));
							progressDialog
									.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							break;
						case GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER:
							url = "buildRelation";
							params.add(new BasicNameValuePair("voyageNumber",
									voyageNumber));
							params.add(new BasicNameValuePair("userID",
									LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode",
									SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "0"));
							params.add(new BasicNameValuePair("bindType", from
									+ ""));
							// 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx",
									GlobalFlags.ZQDXLX_CB + ""));

							progressDialog = new ProgressDialog(
									ShipDetailActivity.this);
							progressDialog
									.setTitle(getString(R.string.waiting));
							progressDialog
									.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							break;
						default:
							break;
						}

						if (BaseApplication.instent.getWebState()) {
							NetWorkManager.request(ShipDetailActivity.this,
									url, params,
									HTTPREQUEST_TYPE_FOR_UNBINDSHIP);
						} else {
							switch (type) {
							case GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER:
								OffLineManager.request(ShipDetailActivity.this,
										new KakouAction(), url,
										NVPairTOMap.nameValuePairTOMap(params),
										REQUEST_TYPE_FOR_UNBINDKAKOU);
								break;
							case GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER:
								OffLineManager.request(ShipDetailActivity.this,
										new TkglAction(), url,
										NVPairTOMap.nameValuePairTOMap(params),
										REQUEST_TYPE_FOR_UNBINDTKGL);

								break;
							default:
								break;
							}

						}

					}
				});
		builder.setNegativeButton(R.string.no,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		builder.create().show();

	}

	/** 发送抵港或离港时间之前，显示dialog让用户确认 */
	private void onShowSendTimeQuestDialog(int id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(id);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 执行发送离港、抵港时间
						String url = "sendTime";
						if (progressDialog != null) {
							return;
						}
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("userID", LoginUser
								.getCurrentLoginUser().getUserID()));
						timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date(System.currentTimeMillis()));
						params.add(new BasicNameValuePair("time", timeStr));
						params.add(new BasicNameValuePair("type", tsxxStr));
						params.add(new BasicNameValuePair("voyageNumber",
								voyageNumber));
						params.add(new BasicNameValuePair("cbybqkid",
								cbybqkIdStr));
						params.add(new BasicNameValuePair("kacbqkid",
								kacbqkIdStr));
						params.add(new BasicNameValuePair("qwzlcjid",
								qwzlcjIdStr));
						params.add(new BasicNameValuePair("ymtbw", ymtbwStr));
						params.add(new BasicNameValuePair("ywmtbw", ywmtbwStr));

						progressDialog = new ProgressDialog(
								ShipDetailActivity.this);
						progressDialog.setTitle(getString(R.string.sending));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						NetWorkManager.request(ShipDetailActivity.this, url,
								params, HTTPREQUEST_TYPE_FOR_SENDSHIPTIME);
					}
				});
		builder.setNegativeButton(R.string.no,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/** 获取船舶详情信息 */
	private void onLoadShipDetail() {
		// TODO Auto-generated method stub
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
		NetWorkManager.request(this, url, params,
				HTTPREQUEST_TYPE_FOR_GETDETAIL);
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
		if (requestCode == FlagManagers.CUSTOM_DIALOG_FOR_EXIT
				&& resultCode == RESULT_OK) {
			String password = data.getStringExtra("password");
			this.validatePassword(password);
		}

	}

	/** 处理平台返回的数据 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType
				+ ",result" + (str != null));

		if (httpRequestType == HTTPREQUEST_TYPE_FOR_GETDETAIL) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			boolean success = false;

			if (StringUtils.isNotEmpty(str)) {
				success = onParseXMLData(str);
			}
			// System.out.println("success:"+success);
			if (success) {
				Intent intent = getIntent();
				((TextView) findViewById(R.id.shipdetail_name)).setText(Html
						.fromHtml(getString(R.string.shipchinaname)
								+ "："
								+ "<font color=\"#acacac\">"
								+ ((String) shipData.get("cbzwm") == null ? ""
										: (String) shipData.get("cbzwm"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_checkclass))
						.setText(Html.fromHtml(getString(R.string.shipcheckclass)
								+ "："
								+ "<font color=\"#acacac\">"
								+ ((String) shipData.get("jcfl") == null ? ""
										: (String) shipData.get("jcfl"))
								+ "</font>"));
				((TextView) findViewById(R.id.jczt)).setText(Html
						.fromHtml(getString(R.string.shipcheckstatus)
								+ "："
								+ "<font color=\"#acacac\">"
								+ ((String) shipData.get("dqjczt") == null ? ""
										: (String) shipData.get("dqjczt"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_enname)).setText(Html
						.fromHtml(""
								+ getString(R.string.shipenglishname)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (((String) shipData.get("cbywm") == null ? ""
										: (String) shipData.get("cbywm")))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_company)).setText(Html
						.fromHtml("<font color=\"#acacac\">"
								+ ((String) shipData.get("cdgs") == null ? ""
										: (String) shipData.get("cdgs"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_country)).setText(Html
						.fromHtml(getString(R.string.shipcountry)
								+ "："
								+ "<font color=\"#acacac\">"
								+ DataDictionary
										.getCountryName(((String) shipData
												.get("gj") == null ? ""
												: (String) shipData.get("gj")))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_peoplenumber))
						.setText(Html.fromHtml(getString(R.string.shippeoplenumber)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (((String) shipData.get("cys")) == null ? ""
										: ((String) shipData.get("cys")))
								+ "</font>"));
				if (StringUtils.isEmpty(shipData.get("cbxz"))) {
					((TextView) findViewById(R.id.shipdetail_property))
							.setText(Html.fromHtml(getString(R.string.shipproperty)
									+ "："
									+ "<font color=\"#acacac\">"
									+ DataDictionary.getDataDictionaryName(
											(intent.getStringExtra("cbxz") == null ? ""
													: intent.getStringExtra("cbxz")),
											DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)
									+ "</font>"));
				} else {
					((TextView) findViewById(R.id.shipdetail_property))
							.setText(Html.fromHtml(getString(R.string.shipproperty)
									+ "："
									+ "<font color=\"#acacac\">"
									+ DataDictionary.getDataDictionaryName(
											((String) shipData.get("cbxz") == null ? ""
													: (String) shipData
															.get("cbxz")),
											DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)
									+ "</font>"));
				}

				String yjsj = (String) shipData.get("yjsj");
				String zjsj = (String) shipData.get("zjsj");
				if (findViewById(R.id.yjsj) != null) {
					findViewById(R.id.yjsj).setVisibility(View.VISIBLE);
					if (StringUtils.isNotEmpty(yjsj)) {
						((TextView) findViewById(R.id.yjsj)).setText(Html
								.fromHtml(getString(R.string.yjsj) + "："
										+ "<font color=\"#acacac\">" + yjsj
										+ "</font>"));
					} else {
						((TextView) findViewById(R.id.yjsj)).setText(Html
								.fromHtml(getString(R.string.yjsj) + "："
										+ "<font color=\"#acacac\">"
										+ "</font>"));

					}
				}
				if (findViewById(R.id.zjsj) != null) {
					findViewById(R.id.zjsj).setVisibility(View.VISIBLE);
					if (StringUtils.isNotEmpty(zjsj)) {
						((TextView) findViewById(R.id.zjsj)).setText(Html
								.fromHtml(getString(R.string.zjsj) + "："
										+ "<font color=\"#acacac\">" + zjsj
										+ "</font>"));
					} else {
						((TextView) findViewById(R.id.zjsj)).setText(Html
								.fromHtml(getString(R.string.zjsj) + "："
										+ "<font color=\"#acacac\">"
										+ "</font>"));

					}
				}

				((TextView) findViewById(R.id.shipdetail_loginnumber))
						.setText(Html.fromHtml(getString(R.string.shippeopleloginnumber)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (((String) shipData.get("dlcys")) == null ? ""
										: ((String) shipData.get("dlcys")))
								+ "</font>"));
				if (StringUtils.isEmpty(shipData.get("tkwz"))) {
					((TextView) findViewById(R.id.shipdetail_pos))
							.setText(Html.fromHtml("<font color=\"#acacac\">"
									+ ((intent.getStringExtra("tkwz") == null ? ""
											: intent.getStringExtra("tkwz")))
									+ "</font>"));
				} else {
					((TextView) findViewById(R.id.shipdetail_pos))
							.setText(Html.fromHtml("<font color=\"#acacac\">"
									+ ((String) shipData.get("tkwz") == null ? ""
											: (String) shipData.get("tkwz"))
									+ "</font>"));
				}

				((TextView) findViewById(R.id.shipdetail_customnumber))
						.setText(Html.fromHtml(getString(R.string.shipcustomnumber)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (((String) shipData.get("dlrys")) == null ? ""
										: ((String) shipData.get("dlrys")))
								+ "</font>"));
				if (StringUtils.isEmpty(shipData.get("kacbzt"))) {
					((TextView) findViewById(R.id.shipdetail_kacbzt))
							.setText(Html.fromHtml(getString(R.string.kacbzt)
									+ "："
									+ "<font color=\"#acacac\">"
									+ (intent.getStringExtra("kacbzt") == null ? ""
											: intent.getStringExtra("kacbzt"))
									+ "</font>"));
				} else {
					((TextView) findViewById(R.id.shipdetail_kacbzt))
							.setText(Html.fromHtml(getString(R.string.kacbzt)
									+ "："
									+ "<font color=\"#acacac\">"
									+ ((String) shipData.get("kacbzt") == null ? ""
											: (String) shipData.get("kacbzt"))
									+ "</font>"));
					// 根据口岸船舶情况，显示时间
					if (getString(R.string.ydg).equals(shipData.get("kacbzt"))) {
						if (findViewById(R.id.cbsj_yjdgsj) != null) {
							findViewById(R.id.cbsj_yjdgsj).setVisibility(
									View.VISIBLE);
							if (StringUtils.isNotEmpty(shipData.get("yjdgsj"))) {
								((TextView) findViewById(R.id.cbsj_yjdgsj))
										.setText(Html
												.fromHtml(getString(R.string.yjdgsj)
														+ "："
														+ "<font color=\"#acacac\">"
														+ shipData
																.get("yjdgsj")
														+ "</font>"));
							}
						}
					} else if (getString(R.string.zg).equals(
							shipData.get("kacbzt"))) {
						if (findViewById(R.id.cbsj_dgsj) != null) {
							findViewById(R.id.cbsj_dgsj).setVisibility(
									View.VISIBLE);
							if (StringUtils.isNotEmpty(shipData.get("dgsj"))) {
								((TextView) findViewById(R.id.cbsj_dgsj))
										.setText(Html
												.fromHtml(getString(R.string.dgsj)
														+ "："
														+ "<font color=\"#acacac\">"
														+ shipData.get("dgsj")
														+ "</font>"));
							}
						}
					} else if (getString(R.string.ylg).equals(
							shipData.get("kacbzt"))) {
						if (findViewById(R.id.cbsj_dgsj) != null
								&& findViewById(R.id.cbsj_yjlgsj) != null) {
							findViewById(R.id.cbsj_dgsj).setVisibility(
									View.VISIBLE);
							findViewById(R.id.cbsj_yjlgsj).setVisibility(
									View.VISIBLE);
							if (StringUtils.isNotEmpty(shipData.get("dgsj"))) {
								((TextView) findViewById(R.id.cbsj_dgsj))
										.setText(Html
												.fromHtml(getString(R.string.dgsj)
														+ "："
														+ "<font color=\"#acacac\">"
														+ shipData.get("dgsj")
														+ "</font>"));
							}
							if (StringUtils.isNotEmpty(shipData.get("yjlgsj"))) {
								((TextView) findViewById(R.id.cbsj_yjlgsj))
										.setText(Html
												.fromHtml(getString(R.string.yjlgsj)
														+ "："
														+ "<font color=\"#acacac\">"
														+ shipData
																.get("yjlgsj")
														+ "</font>"));
							}
						}
					}
				}

			} else {
				Intent intent = getIntent();
				((TextView) findViewById(R.id.shipdetail_name)).setText(Html
						.fromHtml(getString(R.string.shipchinaname)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("cbzwm") == null ? ""
										: intent.getStringExtra("cbzwm"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_checkclass))
						.setText(Html.fromHtml(getString(R.string.shipcheckclass)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("jcfl") == null ? ""
										: intent.getStringExtra("jcfl"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_enname)).setText(Html
						.fromHtml(""
								+ getString(R.string.shipenglishname)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("cbywm") == null ? ""
										: intent.getStringExtra("cbywm"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_company)).setText(Html
						.fromHtml("<font color=\"#acacac\">"
								+ (intent.getStringExtra("cdgs") == null ? ""
										: intent.getStringExtra("cdgs"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_country)).setText(Html
						.fromHtml(getString(R.string.shipcountry)
								+ "："
								+ "<font color=\"#acacac\">"
								+ DataDictionary.getCountryName((intent
										.getStringExtra("gj") == null ? ""
										: intent.getStringExtra("gj")))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_peoplenumber))
						.setText(Html.fromHtml(getString(R.string.shippeoplenumber)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("cys") == null ? ""
										: intent.getStringExtra("cys"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_property))
						.setText(Html.fromHtml(getString(R.string.shipproperty)
								+ "："
								+ "<font color=\"#acacac\">"
								+ DataDictionary.getDataDictionaryName(
										(intent.getStringExtra("cbxz") == null ? ""
												: intent.getStringExtra("cbxz")),
										DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_loginnumber))
						.setText(Html.fromHtml(getString(R.string.shippeopleloginnumber)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("dlcys") == null ? ""
										: intent.getStringExtra("dlcys"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_pos)).setText(Html
						.fromHtml("<font color=\"#acacac\">"
								+ (intent.getStringExtra("tkwz") == null ? ""
										: intent.getStringExtra("tkwz"))
								+ "</font>"));
				((TextView) findViewById(R.id.jczt)).setText(Html
						.fromHtml(getString(R.string.shipcheckstatus)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("dqjczt") == null ? ""
										: intent.getStringExtra("dqjczt"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_customnumber))
						.setText(Html.fromHtml(getString(R.string.shipcustomnumber)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("dlrys") == null ? ""
										: intent.getStringExtra("dlrys"))
								+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_kacbzt)).setText(Html
						.fromHtml(getString(R.string.kacbzt)
								+ "："
								+ "<font color=\"#acacac\">"
								+ (intent.getStringExtra("kacbzt") == null ? ""
										: intent.getStringExtra("kacbzt"))
								+ "</font>"));
				((Button) findViewById(R.id.shipdetail_submit))
						.setEnabled(false);
				if (httpReturnXMLInfo != null) {
					HgqwToast.toast(getApplicationContext(), httpReturnXMLInfo,
							HgqwToast.LENGTH_LONG);
				} else {
					HgqwToast.toast(getApplicationContext(),
							getString(R.string.data_download_failure_info),
							HgqwToast.LENGTH_LONG);
				}
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_BINDSHIP) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				shipData.put("bdzt", "已绑定");
				SystemSetting.setBindShip(shipData, from + "");
				HgqwToast.makeText(ShipDetailActivity.this,
						R.string.bindship_success, HgqwToast.LENGTH_LONG)
						.show();
				Intent data = null;
				data = new Intent();
				setResult(RESULT_OK, data);
				// 梯口、卡口、巡检，提示下载离线数据
				if (GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == from
						|| GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER == from
						|| GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == from) {
					downloadOfflineData(shipData);
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					finish();
				}
			} else if (str != null && "3".equals(str)) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(ShipDetailActivity.this,
						R.string.had_bind_ship, HgqwToast.LENGTH_LONG).show();
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(ShipDetailActivity.this,
						R.string.bindship_failure, HgqwToast.LENGTH_LONG)
						.show();
				SystemSetting.setBindShip(null, from + "");
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_UNBINDSHIP) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				HgqwToast.makeText(ShipDetailActivity.this,
						R.string.unbindship_success, HgqwToast.LENGTH_LONG)
						.show();
				HashMap<String, Object> bindData = null;
				if (from == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
					bindData = SystemSetting
							.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN
									+ "");
				} else {
					bindData = SystemSetting
							.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER
									+ "");
				}
				String hc = "";
				String kacbqkid = "";
				if (bindData != null) {
					hc = (String) bindData.get("hc");
					kacbqkid = (String) bindData.get("kacbqkid");
				}
				SystemSetting.setBindShip(null, from + "");
				ArrayList<HashMap<String, Object>> mapsForKk = SystemSetting
						.getShipOfKK();
				if (from == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					RestoreBindShipInfo.cleanBindShip();
				}
				Flags.peClickFlag = false;

				// 删除历史数据，如果开启了离线开关则不删除。巡查版巡查模块解绑删除船舶、证件、船员
				if (GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == from
						|| GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER == from
						|| GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == from) {
					deleteHistory(hc, kacbqkid, mapsForKk);
				}
				finish();
			} else {
				HgqwToast.makeText(ShipDetailActivity.this,
						R.string.unbindship_failure, HgqwToast.LENGTH_LONG)
						.show();
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_SENDSHIPTIME) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null && "1".equals(str)) {
				HgqwToast.makeText(ShipDetailActivity.this,
						R.string.send_success, HgqwToast.LENGTH_LONG).show();
				HashMap<String, Object> _BindShip = SystemSetting
						.getBindShip(from + "");
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
				HgqwToast.makeText(ShipDetailActivity.this,
						R.string.send_failure, HgqwToast.LENGTH_LONG).show();
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_GET_MOVE_INFO) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
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
						HgqwToast
								.makeText(ShipDetailActivity.this,
										R.string.only_ship_leave,
										HgqwToast.LENGTH_LONG).show();
					}
				} else if (tsxxStr.equals("1")) {
					if (title.equals(getString(R.string.leave))) {
						onShowSendTimeQuestDialog(R.string.ship_leave_comfirm);
					} else {
						HgqwToast.makeText(ShipDetailActivity.this,
								R.string.only_ship_arrive,
								HgqwToast.LENGTH_LONG).show();
					}
				} else if (tsxxStr.equals("2")) {
					if (title.equals(getString(R.string.arrive))) {
						onShowSendTimeQuestDialog(R.string.ship_berthing_comfirm);
					} else {
						HgqwToast
								.makeText(ShipDetailActivity.this,
										R.string.only_ship_leave,
										HgqwToast.LENGTH_LONG).show();
					}
				} else if (tsxxStr.equals("3")) {
					if (title.equals(getString(R.string.leave))) {
						onShowSendTimeQuestDialog(R.string.ship_shifting_comfirm);
					} else {
						HgqwToast.makeText(ShipDetailActivity.this,
								R.string.only_ship_arrive,
								HgqwToast.LENGTH_LONG).show();
					}
				} else {
					HgqwToast.makeText(ShipDetailActivity.this,
							R.string.data_download_failure_info,
							HgqwToast.LENGTH_LONG).show();
				}
			} else {
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(ShipDetailActivity.this,
							httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(ShipDetailActivity.this,
							R.string.data_download_failure_info,
							HgqwToast.LENGTH_LONG).show();
				}
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
		} else if (httpRequestType == SAVE_XJ) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				if ("-1".equals(str)) {
					HgqwToast.getToastView(getApplicationContext(),
							getString(R.string.save_failure)).show();
				} else if ("1".equals(str)) {
					HgqwToast.getToastView(getApplicationContext(),
							getString(R.string.save_success)).show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(),
						getString(R.string.data_download_failure_info)).show();
			}
		}
	}

	/**
	 * 删除历史数据。巡查版巡查模块解绑删除船舶、证件、船员
	 * 
	 * @param hc
	 * @param mapsForKk
	 */
	private void deleteHistory(String hc, String kacbqkid,
			ArrayList<HashMap<String, Object>> mapsForKk) {
		HashMap<String, Object> kaKouBinddata = SystemSetting
				.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		HashMap<String, Object> bindDataForTiKou = SystemSetting
				.getBindShip(GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER + "");

		if (GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == from) {
			DbUtil.deleteKacbqkByHc(hc);
			DbUtil.deleteHgzjxxByHc(hc);
			DbUtil.deleteCyxxByCbid(kacbqkid);
		} else if (GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == from) {
			boolean isInKk = false;
			if (kaKouBinddata != null && mapsForKk != null
					&& mapsForKk.size() > 0) {// 卡口没有解绑,并且卡口内有船舶
				for (HashMap<String, Object> hashMap : mapsForKk) {
					String hcTemp = (String) hashMap.get("hc");
					if (hcTemp != null && hcTemp.equals(hc)) {
						isInKk = true;
						break;
					}
				}
			}

			if (bindDataForTiKou == null && !isInKk) {
				DbUtil.deleteKacbqkByHc(hc);
				DbUtil.deleteHgzjxxByHc(hc);
				DbUtil.deleteCyxxByCbid(kacbqkid);
			}
			// 删除梯口通行记录
			// DbUtil.deleteTxjlForTiKou();
		} else if (GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER == from) {
			if (bindDataForTiKou != null) {// 如果梯口没有解绑
				hc = (String) bindDataForTiKou.get("hc");
				kacbqkid = (String) bindDataForTiKou.get("kacbqkid");
			} else {
				hc = "";
				kacbqkid = "";
			}
			if (kaKouBinddata == null) {// 卡口已经解绑
				String hcForKk = "";
				String kacbqkidForKk = "";
				if (mapsForKk != null && mapsForKk.size() > 0) {
					for (HashMap<String, Object> hashMap : mapsForKk) {
						hcForKk = (String) hashMap.get("hc");
						kacbqkidForKk = (String) hashMap.get("kacbqkid");
						if (kacbqkidForKk != null
								&& !kacbqkidForKk.equals(kacbqkid)) {
							DbUtil.deleteKacbqkByHc(hcForKk);
							DbUtil.deleteHgzjxxByHc(hcForKk);
							DbUtil.deleteCyxxByCbid(kacbqkidForKk);
						}
					}
				}
				// 删除卡口通行记录
				// DbUtil.deleteTxjlForKakou();
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
					} else if ("yjsj".equals(parser.getName())) {
						if (success) {
							shipData.put("yjsj", parser.nextText());
						}
					} else if ("zjsj".equals(parser.getName())) {
						if (success) {
							shipData.put("zjsj", parser.nextText());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/** 解析平台返回的离港或抵港类型数据 */
	private boolean onParseGetMoveInfoXMLData(String str) {
		// TODO Auto-generated method stub

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

	public void click(View v) {
		switch (v.getId()) {
		case R.id.shipdetail_save_xj:
			String url = "saveXj";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
			params.add(new BasicNameValuePair("userid", LoginUser
					.getCurrentLoginUser().getUserID()));
			params.add(new BasicNameValuePair("dxlx", "03"));// 人员01、车辆02、船舶03、设备04、区域05、查岗查哨06）
			params.add(new BasicNameValuePair("jcsj", DateUtils
					.dateToString(new Date())));
			// 附加经纬度信息
			params.add(new BasicNameValuePair("longitude",
					BaseApplication.instent.getLongitude()));// 经度
			params.add(new BasicNameValuePair("latitude",
					BaseApplication.instent.getLatitude()));// 纬度

			if (BaseApplication.instent.getWebState()) {
				NetWorkManager.request(this, url, params, SAVE_XJ);
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("jcsj", DateUtils.dateToString(new Date()));
				map.put("dxlx", "03");
				map.put("kacbqkid", kacbqkid);
				map.put("jcr", LoginUser.getCurrentLoginUser().getUserID());
				map.put("hc", voyageNumber);
				OffLineManager.request(this, new XunJianAction(), url, map,
						SAVE_XJ);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (offLineRequestType == SAVE_XJ) {
			if (res != null) {
				if (res.first) {
					HgqwToast.getToastView(getApplicationContext(),
							getString(R.string.save_success)).show();
				} else {
					HgqwToast.getToastView(getApplicationContext(),
							getString(R.string.save_failure)).show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(),
						getString(R.string.save_failure)).show();
			}
		} else if (offLineRequestType == REQUEST_TYPE_FOR_UNBINDKAKOU) {
			HgqwToast.toast(getApplicationContext(),
					getString(R.string.no_web_cannot_unbind_kakou),
					HgqwToast.LENGTH_LONG);
		} else if (offLineRequestType == REQUEST_TYPE_FOR_UNBINDTKGL) {
			HgqwToast.toast(getApplicationContext(),
					getString(R.string.no_web_cannot_unbind),
					HgqwToast.LENGTH_LONG);
		} else if (offLineRequestType == HTTPREQUEST_TYPE_FOR_GETDETAIL) {
			onHttpResult((res.second == null ? "" : res.second.toString()),
					offLineRequestType);
		}
	}

	private OffDataDownloadForBd dataDownload = null;

	private void downloadOfflineData(HashMap<String, Object> bindMap) {
		if (GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == from) {
			dataDownload = new OffDataDownloadForBd(handler, bindMap,
					OffLineUtil.DOWNLOAD_FOR_KACBQK, 3);
			// dataDownload = new OffDataDownloadForBd(handler, bindMap,
			// OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_HC, 3);
		} else if (GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER == from) {
			dataDownload = new OffDataDownloadForBd(handler, bindMap,
					OffLineUtil.DOWNLOAD_FOR_QYXX, 3);
			// dataDownload = new OffDataDownloadForBd(handler, bindMap,
			// OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_KKID, 3);
		} else if (GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == from) {
			dataDownload = new OffDataDownloadForBd(handler, bindMap,
					OffLineUtil.DOWNLOAD_FOR_KACBQK, 3);
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
				finish();
				break;

			default:
				break;
			}

		}

	};
}
