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
import android.text.Html;
import android.util.Pair;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.cbdt.action.CbdtShipAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class KacbqkShipInfo extends MyActivity implements OnHttpResult, OffLineResult {
	private static final String TAG = "ShipDetailActivity";

	/** 发起获取船舶详情的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_GETDETAIL = 1;

	/** 船舶解绑梯口管理 */
	private static final int REQUEST_TYPE_FOR_UNBINDTKGL = 6;

	/** 卡口管理解绑 */
	private static final int REQUEST_TYPE_FOR_UNBINDKAKOU = 7;

	/** 绑定船舶航次号 */
	private String voyageNumber;

	public static final int SAVE_XJ = 1853853770;// 接口名称->十六进制->十进制->取后十位

	private ProgressDialog progressDialog = null;

	private String httpReturnXMLInfo = null;

	/** 用来保存船舶信息 */
	private HashMap<String, Object> shipData = null;

	private String flagManagers = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();
		voyageNumber = intent.getStringExtra("hc");
		flagManagers = intent.getStringExtra("flagManagers");
		super.onCreate(savedInstanceState, R.layout.kacbqk_ship_info);

		setMyActiveTitle(getString(R.string.kacbqk) + ">" + getString(R.string.kacbqk_cbxq));

		if (FlagManagers.KACB.equals(flagManagers)) {
			Button dutybtn = (Button) findViewById(R.id.shipdetail_duty);
			dutybtn.setText(getString(R.string.back));
			dutybtn.setVisibility(View.VISIBLE);
			dutybtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}

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
			OffLineManager.request(KacbqkShipInfo.this, new CbdtShipAction(), url, NVPairTOMap.nameValuePairTOMap(params),
					HTTPREQUEST_TYPE_FOR_GETDETAIL);

		}
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
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
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
			e.printStackTrace();
			return false;
		}
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.shipdetail_save_xj:
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
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.save_success)).show();
				} else {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.save_failure)).show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.save_failure)).show();
			}
		} else if (offLineRequestType == REQUEST_TYPE_FOR_UNBINDKAKOU) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.no_web_cannot_unbind_kakou), HgqwToast.LENGTH_LONG);
		} else if (offLineRequestType == REQUEST_TYPE_FOR_UNBINDTKGL) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.no_web_cannot_unbind), HgqwToast.LENGTH_LONG);
		} else if (offLineRequestType == HTTPREQUEST_TYPE_FOR_GETDETAIL) {
			onHttpResult((res.second == null ? "" : res.second.toString()), offLineRequestType);
		}

	}
}
