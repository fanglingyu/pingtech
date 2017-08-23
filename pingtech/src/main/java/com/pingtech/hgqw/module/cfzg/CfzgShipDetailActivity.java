package com.pingtech.hgqw.module.cfzg;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 显示船舶详情界面，在该界面可以做船舶绑定、解除绑定、船舶抵港、船舶离港、查看执勤人员等操作
 * */
public class CfzgShipDetailActivity extends CfzgSuperActivity implements OnHttpResult {
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

	/** 显示标题 */
	private String title;

	/** 绑定船舶航次号 */
	private String voyageNumber;

	/**
	 * 区分来自哪个模块
	 * 
	 * @see CfzgShipListActivity
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

	private boolean hasBind;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		voyageNumber = intent.getStringExtra("hc");
		from = intent.getIntExtra("from", 0);
		fromXunJian = intent.getBooleanExtra("fromxunchaxunjian", false);
		fromBindShip = intent.getBooleanExtra("frombindship", false);
		cfzgFlag = intent.getBooleanExtra("cfzgFlag", false);
		hasBind = intent.getBooleanExtra("hasBind", false);
		if (true) {
			super.onCreateForCfzg(savedInstanceState, R.layout.shipdetail);
		}

		Log.i(TAG, "onCreate()");
		if (title != null) {
			if (from == CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">" + title);
			} else if (from == CfzgShipListActivity.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">" + title);
			} else if (from == CfzgShipListActivity.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + title);
			} else if (from == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
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
			} else if (from == CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS) {
				setMyActiveTitle(getString(R.string.ShipStatus) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.shipinfo));
			} else if (from == CfzgShipListActivity.LIST_TYPE_FROM_TIKOUMANAGER) {
				setMyActiveTitle(getString(R.string.tikoumanager) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.shipinfo));
			} else if (from == CfzgShipListActivity.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
				setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindShip) + ">" + getString(R.string.shipinfo));
			} else if (from == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
				setMyActiveTitle(getString(R.string.kakoumanager) + ">" + getString(R.string.kakou_band) + ">" + getString(R.string.kakouinfo));
				findViewById(R.id.ship).setVisibility(View.GONE);
				findViewById(R.id.kk).setVisibility(View.VISIBLE);
			}
		}
		Button btn = (Button) findViewById(R.id.shipdetail_submit);
		String bdzt = intent.getStringExtra("bdzt");
		if (title == null && bdzt != null && !"未绑定".equals(bdzt)) {
			btn.setText("已绑定");
			btn.setEnabled(false);
		} else {
			if (title == null) {
				if (from == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
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
					if (title == null || title.equals(getString(R.string.bindShip))) {
						// 执行船舶绑定
						if (shipData != null) {
							String url = "buildRelation";
							if (progressDialog != null) {
								return;
							}
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "1"));
							params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
							params.add(new BasicNameValuePair("bindType", from + ""));
							//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
							

							progressDialog = new ProgressDialog(CfzgShipDetailActivity.this);
							progressDialog.setTitle(getString(R.string.waiting));
							progressDialog.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(CfzgShipDetailActivity.this, url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
						}
					} else if (title.equals(getString(R.string.unbindShip)) || title.equals(getString(R.string.kakou_unband))) {
						// 执行船舶解除绑定
						if (from == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
							String url = "buildKkRelation";
							if (progressDialog != null) {
								return;
							}
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
							params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
							params.add(new BasicNameValuePair("bindState", "0"));
							params.add(new BasicNameValuePair("kkID", voyageNumber));
							params.add(new BasicNameValuePair("bindType", from + ""));
							//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
							params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_KK + ""));

							progressDialog = new ProgressDialog(CfzgShipDetailActivity.this);
							progressDialog.setTitle(getString(R.string.waiting));
							progressDialog.setMessage(getString(R.string.waiting));
							progressDialog.setCancelable(false);
							progressDialog.setIndeterminate(false);
							progressDialog.show();
							NetWorkManager.request(CfzgShipDetailActivity.this, url, params, HTTPREQUEST_TYPE_FOR_UNBINDSHIP);
						} else {
							if (progressDialog != null) {
								return;
							}
							showDialogForUnbind(1);
						}
					} else if (title.equals(getString(R.string.leave)) || title.equals(getString(R.string.arrive))) {
						// 船舶抵港、离港
						String url = "getMoveInfo";
						if (progressDialog != null) {
							return;
						}
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
						params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
						params.add(new BasicNameValuePair("type", (title.equals(getString(R.string.leave)) ? "1" : "0")));
						progressDialog = new ProgressDialog(CfzgShipDetailActivity.this);
						progressDialog.setTitle(getString(R.string.sending));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						NetWorkManager.request(CfzgShipDetailActivity.this, url, params, HTTPREQUEST_TYPE_FOR_GET_MOVE_INFO);
					}
				}
			});
		}
		if (from == CfzgShipListActivity.LIST_TYPE_FROM_KAKOUMANAGER) {
			((TextView) findViewById(R.id.kk_name)).setText(Html.fromHtml(getString(R.string.kk_name) + "：" + "<font color=\"#acacac\">"
					+ intent.getStringExtra("kkmc") + "</font>"));
			((TextView) findViewById(R.id.kk_rang)).setText(Html.fromHtml(getString(R.string.kk_rang) + "：" + "<font color=\"#acacac\">"
					+ intent.getStringExtra("kkfw") + "</font>"));
			((TextView) findViewById(R.id.kk_addr)).setText(Html.fromHtml(getString(R.string.kk_addr) + "：" + "<font color=\"#acacac\">"
					+ intent.getStringExtra("kkxx") + "</font>"));
			voyageNumber = intent.getStringExtra("id");
		} else {
			if (intent.getStringExtra("cys") == null) {
				onLoadShipDetail();
			} else {
				((TextView) findViewById(R.id.shipdetail_name)).setText(Html.fromHtml(getString(R.string.shipchinaname) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("cbzwm") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_checkclass)).setText(Html.fromHtml(getString(R.string.shipcheckclass) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("jcfl") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_enname)).setText(Html.fromHtml(getString(R.string.shipenglishname) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("cbywm") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_company)).setText(Html.fromHtml("<font color=\"#acacac\">" + intent.getStringExtra("cdgs")
						+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_country)).setText(Html.fromHtml(getString(R.string.shipcountry) + "："
						+ "<font color=\"#acacac\">" + DataDictionary.getCountryName(intent.getStringExtra("gj")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_peoplenumber)).setText(Html.fromHtml(getString(R.string.shippeoplenumber) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("cys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_property)).setText(Html.fromHtml(getString(R.string.shipproperty) + "："
						+ "<font color=\"#acacac\">"
						+ DataDictionary.getDataDictionaryName(intent.getStringExtra("cbxz"), DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)
						+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_loginnumber)).setText(Html.fromHtml(getString(R.string.shippeopleloginnumber) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("dlcys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_pos)).setText(Html.fromHtml("<font color=\"#acacac\">" + intent.getStringExtra("tkwz")
						+ "</font>"));

				((TextView) findViewById(R.id.shipdetail_customnumber)).setText(Html.fromHtml(getString(R.string.shipcustomnumber) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("dlrys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_kacbzt)).setText(Html.fromHtml(getString(R.string.kacbzt) + "：" + "<font color=\"#acacac\">"
						+ intent.getStringExtra("kacbzt") + "</font>"));
			}
		}
		if (cfzgFlag) {
			setMyActiveTitle(getString(R.string.bindShip));
			if (title != null) {
				setMyActiveTitle(getString(R.string.unbindShip));
			}
		}
	}

	/** 发送抵港或离港时间之前，显示dialog让用户确认 */
	private void onShowSendTimeQuestDialog(int id) {
		// TODO Auto-generated method stub
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

				progressDialog = new ProgressDialog(CfzgShipDetailActivity.this);
				progressDialog.setTitle(getString(R.string.sending));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(CfzgShipDetailActivity.this, url, params, HTTPREQUEST_TYPE_FOR_SENDSHIPTIME);
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
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_GETDETAIL) {
			boolean success = false;

			if (str != null) {
				success = onParseXMLData(str);
			}
			if (success) {
				((TextView) findViewById(R.id.shipdetail_name)).setText(Html.fromHtml(getString(R.string.shipchinaname) + "："
						+ "<font color=\"#acacac\">" + (String) shipData.get("cbzwm") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_checkclass)).setText(Html.fromHtml(getString(R.string.shipcheckclass) + "："
						+ "<font color=\"#acacac\">" + (String) shipData.get("jcfl") + "</font>"));
				((TextView) findViewById(R.id.jczt)).setText(Html.fromHtml(getString(R.string.shipcheckstatus) + "：" + "<font color=\"#acacac\">"
						+ (String) shipData.get("dqjczt") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_enname)).setText(Html.fromHtml(getString(R.string.shipenglishname) + "："
						+ "<font color=\"#acacac\">" + (String) shipData.get("cbywm") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_company)).setText(Html.fromHtml("<font color=\"#acacac\">" + (String) shipData.get("cdgs")
						+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_country)).setText(Html.fromHtml(getString(R.string.shipcountry) + "："
						+ "<font color=\"#acacac\">" + DataDictionary.getCountryName((String) shipData.get("gj")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_peoplenumber)).setText(Html.fromHtml(getString(R.string.shippeoplenumber) + "："
						+ "<font color=\"#acacac\">" + (String) shipData.get("cys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_property)).setText(Html.fromHtml(getString(R.string.shipproperty) + "："
						+ "<font color=\"#acacac\">"
						+ DataDictionary.getDataDictionaryName((String) shipData.get("cbxz"), DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)
						+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_loginnumber)).setText(Html.fromHtml(getString(R.string.shippeopleloginnumber) + "："
						+ "<font color=\"#acacac\">" + (String) shipData.get("dlcys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_pos)).setText(Html.fromHtml("<font color=\"#acacac\">" + (String) shipData.get("tkwz")
						+ "</font>"));

				((TextView) findViewById(R.id.shipdetail_customnumber)).setText(Html.fromHtml(getString(R.string.shipcustomnumber) + "："
						+ "<font color=\"#acacac\">" + (String) shipData.get("dlrys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_kacbzt)).setText(Html.fromHtml(getString(R.string.kacbzt) + "：" + "<font color=\"#acacac\">"
						+ (String) shipData.get("kacbzt") + "</font>"));
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

			} else {
				Intent intent = getIntent();
				((TextView) findViewById(R.id.shipdetail_name)).setText(Html.fromHtml(getString(R.string.shipchinaname) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("cbzwm") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_checkclass)).setText(Html.fromHtml(getString(R.string.shipcheckclass) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("jcfl") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_enname)).setText(Html.fromHtml(getString(R.string.shipenglishname) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("cbywm") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_company)).setText(Html.fromHtml("<font color=\"#acacac\">" + intent.getStringExtra("cdgs")
						+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_country)).setText(Html.fromHtml(getString(R.string.shipcountry) + "："
						+ "<font color=\"#acacac\">" + DataDictionary.getCountryName(intent.getStringExtra("gj")) + "</font>"));
				((TextView) findViewById(R.id.shipdetail_peoplenumber)).setText(Html.fromHtml(getString(R.string.shippeoplenumber) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("cys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_property)).setText(Html.fromHtml(getString(R.string.shipproperty) + "："
						+ DataDictionary.getDataDictionaryName(intent.getStringExtra("cbxz"), DataDictionary.DATADICTIONARY_TYPE_SHIP_TYPE)
						+ "</font>"));
				((TextView) findViewById(R.id.shipdetail_loginnumber)).setText(Html.fromHtml(getString(R.string.shippeopleloginnumber) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("dlcys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_pos)).setText(Html.fromHtml("<font color=\"#acacac\">" + intent.getStringExtra("tkwz")
						+ "</font>"));

				((TextView) findViewById(R.id.shipdetail_customnumber)).setText(Html.fromHtml(getString(R.string.shipcustomnumber) + "："
						+ "<font color=\"#acacac\">" + intent.getStringExtra("dlrys") + "</font>"));
				((TextView) findViewById(R.id.shipdetail_kacbzt)).setText(Html.fromHtml(getString(R.string.kacbzt) + "：" + "<font color=\"#acacac\">"
						+ intent.getStringExtra("kacbzt") + "</font>"));
				((Button) findViewById(R.id.shipdetail_submit)).setEnabled(false);
				if (httpReturnXMLInfo != null) {
					((Button) findViewById(R.id.shipdetail_submit)).setEnabled(true);
					HgqwToast.makeText(CfzgShipDetailActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
				}
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_BINDSHIP) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				shipData.put("bdzt", "已绑定");
				SystemSetting.setBindShip(shipData, from + "");
				HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.bindship_success, HgqwToast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(this, CfzgUseOnly.class);
				startActivity(intent);
				finish();
			} else {
				if (!hasBind) {
					SystemSetting.setBindShip(null, from + "");
				}
				HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.bindship_failure, HgqwToast.LENGTH_LONG).show();
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_UNBINDSHIP) {
			if (!TextUtils.isEmpty(str)) {
				if ("1".equals(str) || "2".equals(str)) {
					HgqwToast.makeText(CfzgShipDetailActivity.this,
							R.string.unbindship_success, HgqwToast.LENGTH_LONG)
							.show();
					SystemSetting.setBindShip(null, from + "");
					finish();
				} else if ("-2".equals(str)) {// 船舶已不存在，解决船方自管船舶解绑不成功问题
					HgqwToast.makeText(CfzgShipDetailActivity.this,
							R.string.unbindship_failure_no_ship, HgqwToast.LENGTH_LONG)
							.show();
					SystemSetting.setBindShip(null, from + "");
					finish();
				}
			} else {
				HgqwToast.makeText(CfzgShipDetailActivity.this,
						R.string.unbindship_failure, HgqwToast.LENGTH_LONG)
						.show();
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_SENDSHIPTIME) {
			if (str != null && "1".equals(str)) {
				HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.send_success, HgqwToast.LENGTH_LONG).show();
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
				HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.send_failure, HgqwToast.LENGTH_LONG).show();
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
						HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.only_ship_leave, HgqwToast.LENGTH_LONG).show();
					}
				} else if (tsxxStr.equals("1")) {
					if (title.equals(getString(R.string.leave))) {
						onShowSendTimeQuestDialog(R.string.ship_leave_comfirm);
					} else {
						HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.only_ship_arrive, HgqwToast.LENGTH_LONG).show();
					}
				} else if (tsxxStr.equals("2")) {
					if (title.equals(getString(R.string.arrive))) {
						onShowSendTimeQuestDialog(R.string.ship_berthing_comfirm);
					} else {
						HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.only_ship_leave, HgqwToast.LENGTH_LONG).show();
					}
				} else if (tsxxStr.equals("3")) {
					if (title.equals(getString(R.string.leave))) {
						onShowSendTimeQuestDialog(R.string.ship_shifting_comfirm);
					} else {
						HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.only_ship_arrive, HgqwToast.LENGTH_LONG).show();
					}
				} else {
					HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
				}
			} else {
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(CfzgShipDetailActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(CfzgShipDetailActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
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
		}
	}

	/** 解析平台返回的船舶详情信息数据 */
	private boolean onParseXMLData(String str) {
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

	private void showDialogForUnbind(final int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.ship_unbind_comfirm);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String url = null;
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				switch (type) {
				case 1:
					url = "buildRelation";
					params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
					params.add(new BasicNameValuePair("bindState", "0"));
					params.add(new BasicNameValuePair("bindType", from + ""));
					//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
					params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
					

					progressDialog = new ProgressDialog(CfzgShipDetailActivity.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
				default:
					break;
				}
				NetWorkManager.request(CfzgShipDetailActivity.this, url, params, HTTPREQUEST_TYPE_FOR_UNBINDSHIP);

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
