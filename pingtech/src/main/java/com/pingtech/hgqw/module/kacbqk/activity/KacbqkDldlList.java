package com.pingtech.hgqw.module.kacbqk.activity;

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
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
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
import com.pingtech.hgqw.activity.MyActivity;
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
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class KacbqkDldlList extends MyActivity implements OnHttpResult, OffLineResult, OnCheckedChangeListener {
	private static final String TAG = "KacbqkDldlList";

	/**
	 * 没有返回的船员adapter
	 */
	private PeopleBalanceApdater adapterNotReturn;

	/**
	 * 没有下船的人员
	 */
	private PeopleBalanceApdater adapterNotDown;

	/** 存放未返回人员列表 */
	private List<Map<String, String>> notReturnList = new ArrayList<Map<String, String>>();

	/** 存放未下船人员列表 */
	private List<Map<String, String>> notDownList = new ArrayList<Map<String, String>>();

	private ListView notReturnListView;

	private ListView notDownListView;

	private String httpReturnXMLInfo = null;

	private ProgressDialog progressDialog;

	/** 绑定的船舶航次号 */
	private String voyageNumber;

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
	 * ViewPager子布局
	 */
	List<View> viewList;

	/**
	 * 判断左右切屏是不是Tab改变触发的
	 */
	private boolean isTabChange = false;

	ViewPager viewPager;

	/**
	 * 请求网络数据后，是否第一次显示
	 */
	private boolean firstShow = true;

	/**
	 * 登轮列表上面的header
	 */
	private View denglunHeader;

	/**
	 * 登陆列表上面的header
	 */
	private View dengluHeader;

	/**
	 * 用于更新界面
	 * 
	 * 1、当前登轮人员 2、当前登陆船员
	 */
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			// 当前显示数据的数量
			int num = 0;
			// 提示的信息
			String toastData = "";
			View listViewHeader;
			switch (selectNum) {
			case 1:
				// 登轮人员
				num = notDownList.size();
				adapterNotDown.setCheckTab(selectNum);
				adapterNotDown.notifyDataSetChanged();
				toastData = getString(R.string.no_denglun);
				break;
			case 2:
				// 登陆船员
				num = notReturnList.size();
				adapterNotReturn.setCheckTab(selectNum);
				adapterNotReturn.notifyDataSetChanged();
				toastData = getString(R.string.no_denglu);
				break;
			default:
				break;
			}
			// 如果登轮人员数为0并且是第一次显示，则显示登陆船员
			if (firstShow) {
				firstShow = false;
				if (num == 0) {
					rBNowDenglu.setChecked(true);
					return;
				}
			}
			viewPager.setCurrentItem(selectNum - 1);
			// 设置人员列表内容是否显示
			if (num > 0) {
				dengluHeader.setVisibility(View.VISIBLE);
				denglunHeader.setVisibility(View.VISIBLE);
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(KacbqkDldlList.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				}
			} else {
				dengluHeader.setVisibility(View.GONE);
				denglunHeader.setVisibility(View.GONE);
				if (httpReturnXMLInfo != null) {
					((TextView) findViewById(R.id.select_result_empty)).setText(httpReturnXMLInfo);
					HgqwToast.makeText(KacbqkDldlList.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_data);
					HgqwToast.makeText(KacbqkDldlList.this, toastData, HgqwToast.LENGTH_LONG).show();
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
		super.onCreate(savedInstanceState, R.layout.kacbqkddl_list);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle(getText(R.string.kacbqk) + ">" + getText(R.string.kacbqk_dldl));
		Intent intent = getIntent();
		voyageNumber = intent.getStringExtra("hc");

		initView();
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();
		reqestData();
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
		// rBNowDenglun.setChecked(true);
		changeRadioTextColorByOnClick(rBNowDenglun);
		peopleContent = (LinearLayout) findViewById(R.id.people_content);

		viewList = new ArrayList<View>();
		View viewDenglun = getLayoutInflater().inflate(R.layout.layout_denglun, null);
		View viewDenglu = getLayoutInflater().inflate(R.layout.layout_denglu, null);
		denglunHeader = viewDenglun.findViewById(R.id.denglun_header);
		dengluHeader = viewDenglu.findViewById(R.id.denglu_header);
		notDownListView = (ListView) viewDenglun.findViewById(R.id.denglun_list);
		notReturnListView = (ListView) viewDenglu.findViewById(R.id.denglu_list);
		adapterNotDown = new PeopleBalanceApdater(this, notDownList);
		adapterNotReturn = new PeopleBalanceApdater(this, notReturnList);
		notDownListView.setAdapter(adapterNotDown);
		notReturnListView.setAdapter(adapterNotReturn);
		viewList.add(viewDenglun);
		viewList.add(viewDenglu);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if (!isTabChange) {
					selectNum = arg0 + 1;
					if (selectNum == 1) {
						rBNowDenglun.setChecked(true);
					} else {
						rBNowDenglu.setChecked(true);
					}
				}
				isTabChange = false;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
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
		progressDialog = new ProgressDialog(KacbqkDldlList.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		notDownList.clear();
		notReturnList.clear();
		String url = "getPEInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		params.add(new BasicNameValuePair("voyageNumber", voyageNumber));
		if (BaseApplication.instent.getWebState()) {
			NetWorkManager.request(KacbqkDldlList.this, url, params, 0);
		} else {
			OffLineManager.request(KacbqkDldlList.this, new CbdtShipAction(), url, NVPairTOMap.nameValuePairTOMap(params), 0);
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
					} else if ("xcsj".equals(parser.getName())) {
						map.put("xcsj", parser.nextText());
					} else if ("scsj".equals(parser.getName())) {
						map.put("scsj", parser.nextText());
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
		if (str != null) {
			onParseXMLData(str);
			if (notReturnList != null && notReturnList.size() == 0 && notDownList != null && notDownList.size() == 0) {
				finish();
				HgqwToast.makeText(KacbqkDldlList.this, R.string.no_denglun_denglu, HgqwToast.LENGTH_LONG).show();
			} else {
				peopleContent.setVisibility(View.VISIBLE);
				findViewById(R.id.select_result_empty).setVisibility(View.GONE);
				handler.sendEmptyMessage(0);
			}
		} else {
			((TextView) findViewById(R.id.select_result_empty)).setText(R.string.data_download_failure_info);
			HgqwToast.makeText(KacbqkDldlList.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		((TextView) findViewById(R.id.select_result_empty)).setText(R.string.no_web_cannot_person_balance);
		HgqwToast.makeText(KacbqkDldlList.this, R.string.no_web_cannot_person_balance, HgqwToast.LENGTH_LONG).show();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		isTabChange = true;
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation;
		Log.i("zj", "checkedid=" + checkedId);
		if (checkedId == R.id.denglun_people) {
			selectNum = 1;
			changeRadioTextColorByOnClick(rBNowDenglun);
			with = group.getChildAt(0).getWidth();
			translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, 0f, 0f, 0f);
			animationSet.addAnimation(translateAnimation);
			animationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft - 0) / 2);
			mImageView.startAnimation(animationSet);// 开始上面蓝色横条图片的动画切换
			handler.sendEmptyMessage(1);

		} else if (checkedId == R.id.denglu_people) {
			selectNum = 2;
			changeRadioTextColorByOnClick(rBNowDenglu);
			with = group.getChildAt(1).getWidth();
			translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, with, 0f, 0f);

			animationSet.addAnimation(translateAnimation);
			animationSet.setFillAfter(true);
			animationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft - with) / 2);
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
			rBNowDenglun.setText(Html.fromHtml("<font color=\"#33b5e5\">" + getResources().getString(R.string.now_denglun_people) + "</font>"));
		} else {
			rBNowDenglun.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.now_denglun_people) + "</font>"));
		}
		if (rBNowDenglu == radioButton) {
			rBNowDenglu.setText(Html.fromHtml("<font color=\"#33b5e5\">" + getResources().getString(R.string.now_denglu_ship_people) + "</font>"));
		} else {
			rBNowDenglu.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.now_denglu_ship_people) + "</font>"));
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

	/**
	 * 
	 * 
	 * 类描述：ViewPage适配器
	 * 
	 * <p>
	 * Title: 系统名称-PersonBalanceActivity.java
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2012
	 * </p>
	 * <p>
	 * Company: 品恩科技
	 * </p>
	 * 
	 * @author zhaotf
	 * @version 1.0
	 * @date 2014-1-17 上午10:21:26
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View v, int position, Object obj) {
			// TODO Auto-generated method stub
			((ViewPager) v).removeView(viewList.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewList.size();
		}

		@Override
		public Object instantiateItem(View v, int position) {
			((ViewPager) v).addView(viewList.get(position));
			return viewList.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

}
