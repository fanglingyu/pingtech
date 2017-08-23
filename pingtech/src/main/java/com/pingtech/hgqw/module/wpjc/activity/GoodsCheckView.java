package com.pingtech.hgqw.module.wpjc.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.SelectShipActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.wpjc.action.GoodsCheckAction;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 梯口管理下，刷卡后登记货物界面的activity类
 * */
public class GoodsCheckView extends MyActivity implements OnHttpResult,
		OffLineResult {
	private static final String TAG = "GoodsRecordActivity";

	private ProgressDialog progressDialog = null;

	/** 姓名 */
	private String name;

	/** 上下船时间 */
	private String time;

	/** 方向 */
	private String fx;

	private String sfcy;

	/** 人员id */
	private String ryid;

	/** 船舶航次号 */
	private String voyageNumber = null;

	private Button submitBtn;

	/**
	 * 标记从哪个模块进入该界面，01卡口、02梯口、03巡查巡检、04查询人员模块
	 * 、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>船舶绑定、0301巡查巡检>>>船舶绑定
	 */
	private String from = "";

	/**
	 * 物品类型状态(是否被选中)
	 */
	private boolean[] states;

	/**
	 * 物品类型数据
	 */
	private CharSequence[] charSequences;

	/**
	 * 显示选择的物品
	 */
	TextView goodsRes;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		charSequences = null;
		states = null;
		super.onDestroy();
	}

	private String gj;

	private String zw;

	private String birthday_s;

	private String ssdw;

	private String zjzl;

	private String xb;

	private String zjhm;

	private String csrq;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.goodscheck_view);

		Log.i(TAG, "onCreate()");
		Intent intent = getIntent();

		gj = intent.getStringExtra("gj");
		zw = intent.getStringExtra("zw");
		birthday_s = intent.getStringExtra("birthday_s");
		ssdw = intent.getStringExtra("ssdw");
		zjzl = intent.getStringExtra("zjzl");
		xb = intent.getStringExtra("xb");
		zjhm = intent.getStringExtra("zjhm");

		ryid = intent.getStringExtra("ryid");
		csrq = intent.getStringExtra("csrq");
		name = intent.getStringExtra("name");
		time = intent.getStringExtra("time");
		fx = intent.getStringExtra("fx");
		sfcy = intent.getStringExtra("sfcy");
		from = intent.getStringExtra("from");
		voyageNumber = intent.getStringExtra("voyageNumber");
		submitBtn = (Button) findViewById(R.id.record_submit);
		if ("01".equals(from)) {// 卡口记录上下轮物品提供选择卡口内监护船舶功能
			setMyActiveTitle(getText(R.string.kakoumanager) + ">"
					+ getText(R.string.record_goods));
			findViewById(R.id.send_good_info_layout_select_ship).setVisibility(
					View.VISIBLE);
			findViewById(R.id.send_good_info_ship_czsm).setVisibility(
					View.VISIBLE);
			submitBtn.setEnabled(false);
		} else {
			setMyActiveTitle(getText(R.string.tikoumanager) + ">"
					+ getText(R.string.record_goods));
			findViewById(R.id.send_good_info_layout_select_ship).setVisibility(
					View.GONE);
			findViewById(R.id.send_good_info_ship_czsm)
					.setVisibility(View.GONE);
		}

		((TextView) findViewById(R.id.goods_record_name)).setText(Html
				.fromHtml(getString(R.string.name_s)
						+ "<font color=\"#acacac\">" + name + "</font>"));
		((TextView) findViewById(R.id.goods_record_time)).setText(Html
				.fromHtml(getString(R.string.goods_downup_time_tag)
						+ "<font color=\"#acacac\">" + time + "</font>"));

		charSequences = DataDictionary.getDataDictionaryNameList(
				DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE).toArray(
				new CharSequence[DataDictionary.getDataDictionaryNameList(
						DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE).size()]);

		states = new boolean[charSequences.length];

		goodsRes = (TextView) findViewById(R.id.goods);
		goodsRes.setText("");
		goodsRes.setVisibility(View.GONE);

		submitBtn.setOnClickListener(new OnClickListener() {
			/** 向后台提交数据 */
			public void onClick(View v) {
				if (progressDialog != null) {
					return;
				}
				StringBuffer buffer = new StringBuffer();
				if (states != null && states.length > 0) {

					for (int i = 0; i < states.length; i++) {
						if (states[i]) {
							if (buffer != null && buffer.length() > 0) {
								buffer.append("|");
							}
							buffer.append(DataDictionary
									.getDataDictionaryCodeByIndex(
											i,
											DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE));
						}
					}
				}
				if (buffer == null || buffer.length() == 0) {
					HgqwToast.makeText(getApplicationContext(),
							R.string.plase_choose_good).show();
					return;
				}
				progressDialog = new ProgressDialog(GoodsCheckView.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				String url = "sendGoodsInfo";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser
						.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("pdacode", DeviceUtils
						.getIMEI()));
				params.add(new BasicNameValuePair("PDACode", DeviceUtils
						.getIMEI()));
				params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
				params.add(new BasicNameValuePair("time", time));
				params.add(new BasicNameValuePair("type", buffer.toString()));
				params.add(new BasicNameValuePair("fx", fx));
				params.add(new BasicNameValuePair("sfcy", sfcy));
				params.add(new BasicNameValuePair("ryid", ryid));

				params.add(new BasicNameValuePair("zjhm", zjhm));
				params.add(new BasicNameValuePair("xm", name));
				params.add(new BasicNameValuePair("xb", xb));
				params.add(new BasicNameValuePair("zjlx", zjzl));
				params.add(new BasicNameValuePair("csrq", csrq));
				params.add(new BasicNameValuePair("ssdw", ssdw));
				params.add(new BasicNameValuePair("zw", zw));
				params.add(new BasicNameValuePair("gj", gj));

				// 开启离线开关后统一保存到本地，否则传送到服务器
				// if (!BaseApplication.instent.getWebState()) {
				if (getState(FunctionSetting.kqlx, false)) {
					OffLineManager.request(GoodsCheckView.this,
							new GoodsCheckAction(), url,
							NVPairTOMap.nameValuePairTOMap(params), 0);
				} else {
					NetWorkManager.request(GoodsCheckView.this, url, params, 0);
				}

			}
		});
	}

	public void onClickMethod(View v) {
		switch (v.getId()) {
		case R.id.send_good_info_btn_select_ship:
			Intent intent = new Intent();
			intent.putExtra("tk_good_flag", true);
			intent.setClass(getApplicationContext(), SelectShipActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.add_good:
			showAlertDialog();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			switch (resultCode) {
			case RESULT_OK:
				String hc = data.getStringExtra("shipid");
				voyageNumber = hc;
				String shipname = data.getStringExtra("shipname");
				String shipengname = data.getStringExtra("shipengname");
				submitBtn.setEnabled(true);
				if ("".equals(shipname.trim())) {
					((EditText) findViewById(R.id.send_good_info_ship_name))
							.setText(shipengname);
				} else {
					((EditText) findViewById(R.id.send_good_info_ship_name))
							.setText(shipname);
				}
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType
				+ ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null && ("1".equals(str))) {
			HgqwToast.makeText(GoodsCheckView.this, R.string.save_success,
					HgqwToast.LENGTH_LONG).show();
			finish();
		} else if (str != null && ("2".equals(str))) {
			HgqwToast.makeText(GoodsCheckView.this,
					BaseApplication.instent.getString(R.string.ship_has_lg),
					HgqwToast.LENGTH_LONG).show();
		} else {
			HgqwToast.makeText(GoodsCheckView.this, R.string.save_failure,
					HgqwToast.LENGTH_LONG).show();
		}
	}

	/**
	 * 离线版Toast
	 */
	private void offLineToast(String show) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		HgqwToast
				.makeText(getApplicationContext(), show, HgqwToast.LENGTH_LONG)
				.show();
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "offLineResult()offLineRequestType:" + offLineRequestType
				+ ",res.second" + (res.second != null));
		if (res.first) {
			offLineToast(getString(R.string.save_success));
			finish();
		} else {
			offLineToast((String) res.second);
		}
	}

	private void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GoodsCheckView.this);
		builder.setTitle(R.string.Goods_check);

		builder.setMultiChoiceItems(charSequences, states,
				new OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1,
							boolean arg2) {
						// TODO Auto-generated method stub
						states[arg1] = arg2;
					}
				});
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.ok,
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

						StringBuffer text = new StringBuffer();
						int num = states.length;
						if (num > 0) {
							for (int i = 0; i < num; i++) {
								if (states[i]) {
									if (text.length() > 0) {
										text.append(";");
									}
									text.append(charSequences[i]);
								}
							}
						}
						goodsRes.setVisibility(View.VISIBLE);
						goodsRes.setText(text.toString());

					}
				});
		builder.create().show();
	}
}
