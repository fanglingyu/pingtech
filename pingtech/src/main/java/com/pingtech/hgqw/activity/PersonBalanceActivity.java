package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.adapter.PeopleBalanceApdater;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.cbdt.action.CbdtShipAction;
import com.pingtech.hgqw.module.offline.base.utils.OffLineManager;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.NVPairTOMap;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 船舶动态下人员平衡界面的activity类
 * */
public class PersonBalanceActivity extends MyActivity implements OnHttpResult, OffLineResult, OnCheckedChangeListener {
	private static final String TAG = "PersonBalanceActivity";

	private ListView listView;

	private PeopleBalanceApdater adapter;

	/**
	 * 没有返回的船员adapter
	 */
	// private PeopleBalanceApdater adapterNotReturn;

	/**
	 * 没有下船的人员
	 */
	// private PeopleBalanceApdater adapterNotDown;

	/** 存放未返回人员列表 */
	private List<Map<String, String>> notReturnList = new ArrayList<Map<String, String>>();

	/** 存放未下船人员列表 */
	private List<Map<String, String>> notDownList = new ArrayList<Map<String, String>>();

	private List<Map<String, String>> nowData = new ArrayList<Map<String, String>>();

	private String httpReturnXMLInfo = null;

	private ProgressDialog progressDialog;

	/** 绑定的船舶航次号 */
	private String voyageNumber;

	/** 卡口id */
	private String kkid;

	/**
	 * Tab
	 */
	private RadioGroup mRadioGroup;

	/**
	 * 当前登轮人员Tab按钮
	 */
	private RadioButton rBNowDenglun;

	/**
	 * 当前登陆船员
	 */
	private RadioButton rBNowDenglu;

	/**
	 * 显示内容的布局
	 */
	private LinearLayout peopleContent;

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

	private int selectNum = 1;

	/**
	 * 请求网络数据后，是否第一次显示
	 */
	private boolean firstShow = true;

	/**
	 * 用于更新界面
	 * 
	 * 1、当前登轮人员 2、当前登陆船员
	 */
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			nowData.clear();
			// 提示的信息
			String toastData = "";
			switch (selectNum) {
			case 1:
				// 登轮人员
				nowData.addAll(notDownList);
				if (StringUtils.isNotEmpty(kkid)) {
					toastData = getString(R.string.no_denglun_kk);
				} else {
					toastData = getString(R.string.no_denglun);
				}
				break;
			case 2:
				// 登陆船员
				nowData.addAll(notReturnList);
				if (StringUtils.isNotEmpty(kkid)) {
					toastData = getString(R.string.no_denglu_kk);
				} else {
					toastData = getString(R.string.no_denglu);
				}
				break;
			default:
				break;
			}
			// 如果登轮人员数为0并且是第一次显示，则显示登陆船员
			if (firstShow) {
				firstShow = false;
				if (nowData.size() == 0) {
					rBNowDenglu.setChecked(true);
					return;
				}
			}
			adapter.setCheckTab(selectNum);
			adapter.notifyDataSetChanged();
			if (nowData.size() > 0) {
				findViewById(R.id.listview_header).setVisibility(View.VISIBLE);
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(PersonBalanceActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				}
			} else {
				findViewById(R.id.listview_header).setVisibility(View.GONE);
				if (httpReturnXMLInfo != null) {
					((TextView) findViewById(R.id.select_result_empty)).setText(httpReturnXMLInfo);
					HgqwToast.makeText(PersonBalanceActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
					HgqwToast.makeText(PersonBalanceActivity.this, toastData, HgqwToast.LENGTH_LONG).show();
				}
			}
			toastData = "";
		};
	};

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.personbalance_list);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle(getText(R.string.ShipStatus) + ">" + getText(R.string.Personnel_balance));
		Intent intent = getIntent();
		voyageNumber = intent.getStringExtra("hc");
		kkid = intent.getStringExtra("kkid");
		find();
		initView();
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();
		reqestData();
	}

	private void find() {
	}

	/**
	 * 
	 * @方法名：initView
	 * @功能说明：注册控件
	 * @author zhaotf
	 * @date 2014-1-17 上午10:18:29
	 */
	private void initView() {

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		mRadioGroup.setOnCheckedChangeListener(this);
		mImageView = (ImageView) findViewById(R.id.img1);
		rBNowDenglun = (RadioButton) findViewById(R.id.denglun_people);
		rBNowDenglu = (RadioButton) findViewById(R.id.denglu_people);
		if (StringUtils.isNotEmpty(kkid)) {
			rBNowDenglun.setText(getString(R.string.jkkwx));
			rBNowDenglu.setText(getString(R.string.ckkwf));
		}
		// rBNowDenglun.setChecked(true);
		changeRadioTextColorByOnClick(rBNowDenglun);
		peopleContent = (LinearLayout) findViewById(R.id.people_content);

		adapter = new PeopleBalanceApdater(this, nowData, kkid);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
	}

	/**
	 * 
	 * @方法名：reqestData
	 * @功能说明：请求数据
	 * @author zhaotf
	 * @date 2014-1-17 上午11:30:00
	 */
	private void reqestData() {
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(PersonBalanceActivity.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		notDownList.clear();
		notReturnList.clear();
		String url = "getPEInfo";
		if (StringUtils.isNotEmpty(kkid)) {
			url = "getPEKkInfo";
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
		params.add(new BasicNameValuePair("kkid", kkid));
		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(PersonBalanceActivity.this, url, params, 0);
		} else {
			OffLineManager.request(PersonBalanceActivity.this, new CbdtShipAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
		}
		firstShow = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	/** 解析向平台发起请求人员平衡信息的返回数据 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		Map<String, String> map = null;
		boolean success = false;
		boolean not_down = true;
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
							if (notReturnList == null) {
								notReturnList = new ArrayList<Map<String, String>>();
							} else {
								notReturnList.clear();
							}
							if (notDownList == null) {
								notDownList = new ArrayList<Map<String, String>>();
							} else {
								notDownList.clear();
							}
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (!success) {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("yrph".equals(parser.getName())) {
						if (success) {
							map = new HashMap<String, String>();
						}
					} else if ("wf".equals(parser.getName())) {
						not_down = false;
					} else if ("wx".equals(parser.getName())) {
						not_down = true;
					} else if ("ryphID".equals(parser.getName())) {
						map.put("ryphID", parser.nextText());
					} else if ("xm".equals(parser.getName())) {
						map.put("xm", parser.nextText());
					} else if ("xb".equals(parser.getName())) {
						map.put("xb", parser.nextText());
					} else if ("gj".equals(parser.getName())) {
						map.put("gj", parser.nextText());
					} else if ("zjzl".equals(parser.getName())) {
						map.put("zjzl", parser.nextText());
					} else if ("zjhm".equals(parser.getName())) {
						map.put("zjhm", parser.nextText());
					} else if ("downtime".equals(parser.getName())) {
						map.put("downtime", parser.nextText());
					} else if ("uptime".equals(parser.getName())) {
						map.put("uptime", parser.nextText());
					} else if ("time".equals(parser.getName())) {
						map.put("time", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("yrph".equals(parser.getName())) {
						if (success) {
							if (not_down) {
								if (notDownList == null) {
									notDownList = new ArrayList<Map<String, String>>();
								}
								notDownList.add(map);
							} else {
								if (notReturnList == null) {
									notReturnList = new ArrayList<Map<String, String>>();
								}
								notReturnList.add(map);
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

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (StringUtils.isNotEmpty(str)) {
			onParseXMLData(str);
			// if (notReturnList != null && notReturnList.size() == 0 &&
			// notDownList != null && notDownList.size() == 0) {
			if ((notReturnList == null || notReturnList.isEmpty()) && (notDownList == null || notDownList.isEmpty())) {
				finish();
				if (StringUtils.isEmpty(httpReturnXMLInfo)) {
					if (StringUtils.isNotEmpty(voyageNumber)) {
						httpReturnXMLInfo = getString(R.string.crew_has_now_been_balanced);
					} else {
						httpReturnXMLInfo = getString(R.string.kk_ryph);
					}
				}
				HgqwToast.toast(httpReturnXMLInfo);
			} else {
				peopleContent.setVisibility(View.VISIBLE);
				findViewById(R.id.select_result_empty).setVisibility(View.GONE);
				handler.sendEmptyMessage(0);
			}
		} else {
			((TextView) findViewById(R.id.select_result_empty)).setText(R.string.data_download_failure_info);
			HgqwToast.toast(R.string.data_download_failure_info);
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_web_cannot_person_balance);
		HgqwToast.makeText(PersonBalanceActivity.this, R.string.no_web_cannot_person_balance, HgqwToast.LENGTH_LONG).show();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation;
		Log.i("zj", "checkedid=" + checkedId);
		if (checkedId == R.id.denglun_people) {
			selectNum = 1;
			changeRadioTextColorByOnClick(rBNowDenglun);
			with = group.getChildAt(0).getWidth();
			translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, 0f, 0f, 0f);
			animationSet.addAnimation(translateAnimation);
			// _AnimationSet.setFillBefore(false);
			// _AnimationSet.setFillAfter(true);
			animationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft - 0) / 2);

			// mImageView.bringToFront();
			mImageView.startAnimation(animationSet);// 开始上面蓝色横条图片的动画切换
			handler.sendEmptyMessage(1);

		} else if (checkedId == R.id.denglu_people) {
			selectNum = 2;
			changeRadioTextColorByOnClick(rBNowDenglu);
			// tab_postion = getString(R.string.state_ship_doing_gang);
			with = group.getChildAt(1).getWidth();
			translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, with, 0f, 0f);

			animationSet.addAnimation(translateAnimation);
			// _AnimationSet.setFillBefore(false);
			animationSet.setFillAfter(true);
			animationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft - with) / 2);

			// mImageView.bringToFront();
			mImageView.startAnimation(animationSet);

			handler.sendEmptyMessage(2);

		}
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();// 更新当前蓝色横条距离左边的距离
	}

	/**
	 * 根据用户点击设置RadioButton的文本颜色
	 * 
	 * @param radioButton
	 */
	public void changeRadioTextColorByOnClick(RadioButton radioButton) {
		if (rBNowDenglun == radioButton) {
			// rBNowDenglun.setText(Html.fromHtml("<font color=\"#33b5e5\">" +
			// getResources().getString(R.string.now_denglun_people) +
			// "</font>"));
			rBNowDenglun.setTextColor(getResources().getColor(R.color.radio_text_press_color));
		} else {
			// rBNowDenglun.setText(Html.fromHtml("<font color=\"#FFFFFF\">" +
			// getResources().getString(R.string.now_denglun_people) +
			// "</font>"));
			rBNowDenglun.setTextColor(getResources().getColor(R.color.radio_text_color_n));
		}
		if (rBNowDenglu == radioButton) {
			// rBNowDenglu.setText(Html.fromHtml("<font color=\"#33b5e5\">" +
			// getResources().getString(R.string.now_denglu_ship_people) +
			// "</font>"));
			rBNowDenglu.setTextColor(getResources().getColor(R.color.radio_text_press_color));
		} else {
			// rBNowDenglu.setText(Html.fromHtml("<font color=\"#FFFFFF\">" +
			// getResources().getString(R.string.now_denglu_ship_people) +
			// "</font>"));
			rBNowDenglu.setTextColor(getResources().getColor(R.color.radio_text_color_n));
		}
	}

	/**
	 * 获得当前被选中的RadioButton距离左侧的距离
	 */
	private float getCurrentCheckedRadioLeft() {
		// TODO Auto-generated method stub
		if (rBNowDenglun.isChecked()) {
			return 0f;
		} else if (rBNowDenglu.isChecked()) {
			return with;
		}
		return 0f;
	}
}
