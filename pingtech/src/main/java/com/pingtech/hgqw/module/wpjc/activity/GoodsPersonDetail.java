package com.pingtech.hgqw.module.wpjc.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.module.wpjc.action.GoodsCheckAction;
import com.pingtech.hgqw.module.wpjc.entity.ReadCardPersonInfo;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.ImageFactory;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 类描述：货物检查
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-XCXJSearchPersonDetail.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-4-10 下午12:21:40
 */
public class GoodsPersonDetail extends MyActivity implements OnHttpResult,
		OffLineResult {
	private static final String TAG = "GoodsPersonDetailActivity";
	private ProgressDialog progressDialog = null;
	/** 上下船时间 */
	private String time;
	private String from;
	private String voyageNumber = "";
	private static final int GOODSPERSON_DETAIL_SAVEGOODSINFO = 0;
	ReadCardPersonInfo personInfo = null;
	/** 口岸船舶情况ID */
	private String kacbqkid = "";
	/** 船舶名称 **/
	private String voyagemc = "";

	/**
	 * 物品类型状态(是否被选中)
	 */
	private boolean[] states;

	/**
	 * 物品类型数据
	 */
	private CharSequence[] charSequences;

	TextView goodsRes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.goodscheck_persondetail);
		setMyActiveTitle(getString(R.string.goods_check) + ">"
				+ getString(R.string.record_goods));
		Log.i("日志", "CgcsDetail onCreate");
		Intent intent = getIntent();
		personInfo = (ReadCardPersonInfo) intent
				.getSerializableExtra("personInfo");
		from = intent.getStringExtra("from");
		time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
				System.currentTimeMillis()));
		voyageNumber = intent.getStringExtra("voyageNumber");
		voyagemc = intent.getStringExtra("voyagemc");
		kacbqkid = intent.getStringExtra("kacbqkid");
		String tempstr;
		((TextView) findViewById(R.id.detail_name)).setText(Html
				.fromHtml(getString(R.string.name)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (personInfo.getName() == null ? "" : personInfo
								.getName()) + "</font>"));
		if ((personInfo.getSex() != null) && (personInfo.getSex().length() > 0)) {
			tempstr = DataDictionary.getDataDictionaryName(personInfo.getSex(),
					DataDictionary.DATADICTIONARY_TYPE_SEX_TYPE);
		} else {
			tempstr = null;
		}
		((TextView) findViewById(R.id.detail_sex)).setText(Html
				.fromHtml(getString(R.string.sex) + "："
						+ "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));

		ImageView imageView_bg = (ImageView) findViewById(R.id.imageView_photo);
		if (personInfo.getPhoto() != null && personInfo.getPhoto().length > 0) {
			Bitmap netWorkImage = ImageFactory.getBitmap(personInfo.getPhoto());
			if (netWorkImage != null) {
				LayoutParams para;
				para = imageView_bg.getLayoutParams();
				int height_be = netWorkImage.getHeight() / 130;
				int width_be = netWorkImage.getWidth() / 105;
				if (height_be > width_be) {
					para.height = 130 * 2;
					para.width = 130 * 2 * netWorkImage.getWidth()
							/ netWorkImage.getHeight();
				} else {
					para.width = 105 * 2;
					para.height = 105 * 2 * netWorkImage.getHeight()
							/ netWorkImage.getWidth();
				}
				imageView_bg.setLayoutParams(para);
				imageView_bg.setImageBitmap(netWorkImage);
			}
		}

		if ((personInfo.getCountry() != null)
				&& (personInfo.getCountry().length() > 0)) {
			tempstr = DataDictionary.getCountryName(personInfo.getCountry());
		} else {
			tempstr = null;
		}
		((TextView) findViewById(R.id.detail_country)).setText(Html
				.fromHtml(getString(R.string.country) + "："
						+ "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
		((TextView) findViewById(R.id.detail_birthday)).setText(Html
				.fromHtml(getString(R.string.birthday)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (personInfo.getBirthday() == null ? "" : personInfo
								.getBirthday()) + "</font>"));
		if ((personInfo.getCardtype() != null)
				&& (personInfo.getCardtype().length() > 0)) {
			tempstr = DataDictionary.getDataDictionaryName(
					personInfo.getCardtype(),
					DataDictionary.DATADICTIONARY_TYPE_CERTIFICATES_TYPE);
		} else {
			tempstr = null;
		}
		((TextView) findViewById(R.id.detail_cardtype)).setText(Html
				.fromHtml(getString(R.string.cardtype) + "："
						+ "<font color=\"#acacac\">"
						+ (tempstr == null ? "" : tempstr) + "</font>"));
		((TextView) findViewById(R.id.detail_cardnum)).setText(Html
				.fromHtml(getString(R.string.cardnum)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (personInfo.getCardnumber() == null ? "" : personInfo
								.getCardnumber()) + "</font>"));
		((TextView) findViewById(R.id.detail_unit)).setText(Html
				.fromHtml(getString(R.string.unit)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (personInfo.getUnit() == null ? "" : personInfo
								.getUnit()) + "</font>"));
		if (StringUtils.isNotEmpty(from) && "1".equals(from)) {
			TextView ssdw = ((TextView) findViewById(R.id.detail_unit));
			if (StringUtils.isEmpty(personInfo.getUnit())) {
				ssdw.setText(Html.fromHtml(getString(R.string.unit) + "："
						+ "<font color=\"#acacac\">" + voyagemc + "</font>"));
			}
		}
		((TextView) findViewById(R.id.detail_office)).setText(Html
				.fromHtml(getString(R.string.office)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (personInfo.getOffice() == null ? "" : personInfo
								.getOffice()) + "</font>"));
		((TextView) findViewById(R.id.detail_office))
				.setText(Html.fromHtml(getString(R.string.office)
						+ "："
						+ "<font color=\"#acacac\">"
						+ (personInfo.getOffice() == null ? ""
								: DataDictionary.getDataDictionaryOfficeName(
										personInfo.getOffice(),
										DataDictionary.DATADICTIONARY_TYPE_DLRYZW))
						+ "</font>"));

		// 记录物品
		charSequences = DataDictionary.getDataDictionaryNameList(
				DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE).toArray(
				new CharSequence[DataDictionary.getDataDictionaryNameList(
						DataDictionary.DATADICTIONARY_TYPE_GOODS_TYPE).size()]);

		states = new boolean[charSequences.length];
		goodsRes = (TextView) findViewById(R.id.goods);
		goodsRes.setText("");
		goodsRes.setVisibility(View.GONE);

		((TextView) findViewById(R.id.goods_record_time)).setText(Html
				.fromHtml(getString(R.string.goods_downup_time_tag)
						+ "<font color=\"#acacac\">" + time + "</font>"));

		Button submitbt = (Button) findViewById(R.id.submit);
		submitbt.setOnClickListener(new OnClickListener() {

			@Override
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
				progressDialog = new ProgressDialog(GoodsPersonDetail.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				String url = "sendGoodsInfo";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pdacode", DeviceUtils
						.getIMEI()));
				params.add(new BasicNameValuePair("PDACode", DeviceUtils
						.getIMEI()));
				params.add(new BasicNameValuePair("zjhm", (personInfo
						.getCardnumber() == null ? "" : personInfo
						.getCardnumber())));
				params.add(new BasicNameValuePair("xm",
						(personInfo.getName() == null ? "" : personInfo
								.getName())));
				params.add(new BasicNameValuePair(
						"xb",
						(personInfo.getSex() == null ? "" : personInfo.getSex())));
				params.add(new BasicNameValuePair("zjlx", (personInfo
						.getCardtype() == null ? "" : personInfo.getCardtype())));
				params.add(new BasicNameValuePair("csrq", (personInfo
						.getBirthday() == null ? "" : personInfo.getBirthday())));
				if (StringUtils.isNotEmpty(from) && "1".equals(from)
						&& StringUtils.isEmpty(personInfo.getUnit())) {
					params.add(new BasicNameValuePair("ssdw", voyagemc));
				} else {
					params.add(new BasicNameValuePair("ssdw", (personInfo
							.getUnit() == null ? "" : personInfo.getUnit())));
				}
				params.add(new BasicNameValuePair("zw",
						(personInfo.getOffice() == null ? "" : personInfo
								.getOffice())));
				params.add(new BasicNameValuePair("gj", (personInfo
						.getCountry() == null ? "" : personInfo.getCountry())));
				params.add(new BasicNameValuePair("ryid",
						(personInfo.getRyid() == null ? "" : personInfo
								.getRyid())));
				if ("1".equals(from)) {
					params.add(new BasicNameValuePair("sfcy", "1"));
				} else {
					params.add(new BasicNameValuePair("sfcy", "0"));
				}
				params.add(new BasicNameValuePair("userID", LoginUser
						.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
				params.add(new BasicNameValuePair("time", time));
				params.add(new BasicNameValuePair("type", buffer.toString()));
				RadioGroup downup_rg = (RadioGroup) findViewById(R.id.goods_downup_radio);
				if (downup_rg.getCheckedRadioButtonId() == R.id.radio_btn_up) {
					params.add(new BasicNameValuePair("fx", "0"));
				} else {
					params.add(new BasicNameValuePair("fx", "1"));
				}
				// System.out.println("GoodsPersonDetail:kacbqkid:"+kacbqkid);
				params.add(new BasicNameValuePair("kacbqkid", kacbqkid));
				if (!BaseApplication.instent.getWebState()) {
					OffLineManager.request(GoodsPersonDetail.this,
							new GoodsCheckAction(), url,
							NVPairTOMap.nameValuePairTOMap(params), 0);
				} else {
					NetWorkManager.request(GoodsPersonDetail.this, url, params,
							GOODSPERSON_DETAIL_SAVEGOODSINFO);
				}

			}
		});

	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.good_back:
			finish();
			break;
		case R.id.add_good:
			showAlertDialog();
			break;
		default:
			break;
		}
	}

	private void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GoodsPersonDetail.this);
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

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null && ("1".equals(str))) {
			offLineToast(getString(R.string.save_success));
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), GoodsCheckList.class);
			startActivity(intent);
		} else if (str != null && ("2".equals(str))) {
			HgqwToast.makeText(getApplicationContext(),
					BaseApplication.instent.getString(R.string.ship_has_lg),
					HgqwToast.LENGTH_LONG).show();
		} else {
			offLineToast(getString(R.string.save_failure));
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
		if (res.first) {
			offLineToast(getString(R.string.wpjc_save_success));
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), GoodsCheckList.class);
			startActivity(intent);
		} else {
			offLineToast((String) res.second);
		}
	}
}
