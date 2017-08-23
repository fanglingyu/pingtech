package com.pingtech.hgqw.module.kacbqk.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.interf.OffLineResult;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.kacbqk.adapter.KacbApdater;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.widget.HgqwToast;

/** 显示查询船舶结果列表界面的activity类 */
public class KacbqkShipList extends MyActivity implements OnHttpResult, OffLineResult, OnCheckedChangeListener {
	private static final String TAG = "SelectShipResultActivity";

	/** 查询船舶 */
	public static int SELECT_SHIP = 0;

	/** 绑定船舶 */
	public static int BIND_SHIP = 1;

	/** 绑定类型，来自船舶动态 0 梯口管理1 卡口管理3 巡查巡检2 */
	private int bindType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;

	/** 预到港船舶Apdater */
	private KacbApdater adapterYuDaoGang;

	/** 在港船舶Apdater */
	private KacbApdater adapterZaiGang;

	/** 预离港Apdater */
	private KacbApdater adapterYuLiGang;

	private ProgressDialog progressDialog = null;

	/** 是否来自绑定船舶 */
	private boolean fromBindShip;

	/** 是否来自巡查巡检 */
	private boolean fromXunCha;

	/** 是否涉外服务船舶 */
	private boolean sheWai;

	/** 预到港船舶列表 */
	private ArrayList<HashMap<String, Object>> yuDaoGangList = new ArrayList<HashMap<String, Object>>();

	/** 在港列表 */
	private ArrayList<HashMap<String, Object>> zaiGangList = new ArrayList<HashMap<String, Object>>();

	/** 预离港列表 */
	private ArrayList<HashMap<String, Object>> yuLiGangList = new ArrayList<HashMap<String, Object>>();

	/** 预到港船舶ListView */
	private ListView yuDaoGangListView;

	/** 在港船舶ListView */
	private ListView zaiGangListView;

	/** 预离港船舶ListView */
	private ListView yuLiGangListView;

	/** Tab */
	private RadioGroup mRadioGroup;

	/** 预到港船舶Tab按钮 */
	private RadioButton rYuDaoGang;

	/** 在港船舶Tab按钮 */
	private RadioButton rZaiGang;

	/** 预离港船舶Tab按钮 */
	private RadioButton rYuLiGang;

	/** 显示内容的布局 */
	private LinearLayout shipContent;

	/**
	 * 标签下面的小图片（用于显示用户打开的是哪个Tab）
	 */
	private ImageView mImageView;

	/** 当前选中的Tab编号 */
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

	/** 预到港列表上面的header */
	private View yuDaoGangHeader;

	/** 在港船舶列表上面的header */
	private View zaiGangHeader;

	/** 预离港船舶列表上面的header */
	private View yuLiGangHeader;

	/**
	 * 用于更新界面
	 * 
	 * 1、 预到港船舶 2、在港船舶3、预离港船舶
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			// 当前显示数据的数量
			int num = 0;
			// 提示的信息
			String toastData = "Toast";
			switch (selectNum) {
			case 1:
				// 预到港船舶
				num = yuDaoGangList.size();
				adapterYuDaoGang.setCheckTab(selectNum);
				adapterYuDaoGang.notifyDataSetChanged();
				// toastData = getString(R.string.no_denglun);
				break;
			case 2:
				// 在港船舶
				num = zaiGangList.size();
				adapterZaiGang.setCheckTab(selectNum);
				adapterZaiGang.notifyDataSetChanged();
				// toastData = getString(R.string.no_denglu);
				break;
			case 3:
				// 预离港船舶
				num = yuLiGangList.size();
				adapterYuLiGang.setCheckTab(selectNum);
				adapterYuLiGang.notifyDataSetChanged();
				// toastData = getString(R.string.no_denglu);
				break;
			default:
				break;
			}
			// 如果预到港船舶数为0并且是第一次显示，则显示后面的有数据的Tab
			if (firstShow) {
				firstShow = false;
				if (yuDaoGangList.size() == 0) {
					if (zaiGangList.size() == 0) {
						selectNum = 3;
					} else {
						selectNum = 2;
					}
				}
				isTabChange = false;
				this.sendEmptyMessage(0);
				return;
			}
			viewPager.setCurrentItem(selectNum - 1);
			// 设置人员列表内容是否显示
			if (num > 0) {
				yuDaoGangHeader.setVisibility(View.VISIBLE);
				zaiGangHeader.setVisibility(View.VISIBLE);
				yuLiGangHeader.setVisibility(View.VISIBLE);
			} else {
				yuDaoGangHeader.setVisibility(View.GONE);
				zaiGangHeader.setVisibility(View.GONE);
				yuLiGangHeader.setVisibility(View.GONE);
			}
			toastData = "";
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		fromBindShip = intent.getBooleanExtra("frombindship", false);
		fromXunCha = intent.getBooleanExtra("fromxuncha", false);
		bindType = intent.getIntExtra("bindtype", 0);
		sheWai = intent.getBooleanExtra("shewai", false);
		super.onCreate(savedInstanceState, R.layout.kacbqk_ship_list);

		Log.i(TAG, "onCreate()");
		if (KacbqkShipSearch.shipList == null || KacbqkShipSearch.shipList.size() < 1) {
			setMyActiveTitle(getString(R.string.kacbqk) + ">" + getString(R.string.select_ship) + getString(R.string.result));
		} else {
			setMyActiveTitle(getString(R.string.kacbqk) + ">" + getString(R.string.select_ship) + getString(R.string.result) + "   总数:"
					+ KacbqkShipSearch.shipList.size());
		}

		if (findViewById(R.id.sel_ship_list_title_bind) != null) {
			findViewById(R.id.sel_ship_list_title_bind).setVisibility(View.GONE);
		}
		if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
			findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(View.VISIBLE);
		}
		if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
			findViewById(R.id.sel_ship_list_title_shewai).setVisibility(View.GONE);
		}
		if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
			findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(View.GONE);
		}

		initView();

		initData();
		if (findViewById(R.id.sel_ship_list_title_bind) != null) {
			findViewById(R.id.sel_ship_list_title_bind).setVisibility(View.GONE);
		}
		if (findViewById(R.id.sel_ship_list_title_xuncha) != null) {
			findViewById(R.id.sel_ship_list_title_xuncha).setVisibility(View.GONE);
		}
		if (sheWai) {
			if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
				findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(View.GONE);
			}
			if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
				findViewById(R.id.sel_ship_list_title_shewai).setVisibility(View.VISIBLE);
			}
		} else {
			if (findViewById(R.id.sel_ship_list_title_jianhu) != null) {
				findViewById(R.id.sel_ship_list_title_jianhu).setVisibility(View.VISIBLE);
			}
			if (findViewById(R.id.sel_ship_list_title_shewai) != null) {
				findViewById(R.id.sel_ship_list_title_shewai).setVisibility(View.GONE);
			}
		}
		if (findViewById(R.id.listview_topline) != null) {
			findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
		}
		if (findViewById(R.id.select_result_empty) != null) {
			findViewById(R.id.select_result_empty).setVisibility(View.GONE);
		}
		adapterYuDaoGang.notifyDataSetChanged();
		adapterZaiGang.notifyDataSetChanged();
		adapterYuLiGang.notifyDataSetChanged();
	}

	/**
	 * 
	 * @方法名：initView
	 * @功能说明：注册控件
	 * @author zhaotf
	 * @date 2014-2-18 下午2:50:31
	 */
	private void initView() {
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		mRadioGroup.setOnCheckedChangeListener(this);
		mImageView = (ImageView) findViewById(R.id.img1);
		rYuDaoGang = (RadioButton) findViewById(R.id.rYuDaoGang);
		rZaiGang = (RadioButton) findViewById(R.id.rZaiGang);
		rYuLiGang = (RadioButton) findViewById(R.id.rYuLiGang);
		changeRadioTextColorByOnClick(rYuDaoGang);
		shipContent = (LinearLayout) findViewById(R.id.layout);

		viewList = new ArrayList<View>();
		View viewYuDaoGang = getLayoutInflater().inflate(R.layout.layout_yudaogang, null);
		View viewZaiGang = getLayoutInflater().inflate(R.layout.layout_zaigang, null);
		View viewYuLiGang = getLayoutInflater().inflate(R.layout.layout_yuligang, null);
		yuDaoGangHeader = viewYuDaoGang.findViewById(R.id.yudaogang_header);
		zaiGangHeader = viewZaiGang.findViewById(R.id.zaigang_header);
		yuLiGangHeader = viewYuLiGang.findViewById(R.id.yuligang_header);
		yuDaoGangListView = (ListView) viewYuDaoGang.findViewById(R.id.yudaogang_list);
		zaiGangListView = (ListView) viewZaiGang.findViewById(R.id.zaigang_list);
		yuLiGangListView = (ListView) viewYuLiGang.findViewById(R.id.yuligang_list);
		adapterYuDaoGang = new KacbApdater(this, yuDaoGangList, mHandler);
		adapterZaiGang = new KacbApdater(this, zaiGangList, mHandler);
		adapterYuLiGang = new KacbApdater(this, yuLiGangList, mHandler);
		yuDaoGangListView.setAdapter(adapterYuDaoGang);
		zaiGangListView.setAdapter(adapterZaiGang);
		yuLiGangListView.setAdapter(adapterYuLiGang);
		viewList.add(viewYuDaoGang);
		viewList.add(viewZaiGang);
		viewList.add(viewYuLiGang);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if (!isTabChange) {
					selectNum = arg0 + 1;
					switch (selectNum) {
					case 1:
						// 预到港Tab被选中
						rYuDaoGang.setChecked(true);
						break;
					case 2:
						// 在港Tab被选中
						rZaiGang.setChecked(true);
						break;
					case 3:
						// 预离港Tab被选中
						rYuLiGang.setChecked(true);
						break;

					default:
						break;
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
	 * @方法名：initData
	 * @功能说明：绑定数据
	 * @author zhaotf
	 * @date 2014-2-18 下午3:55:56
	 */
	private void initData() {
		yuDaoGangList.clear();
		zaiGangList.clear();
		yuLiGangList.clear();
		List<HashMap<String, Object>> list = KacbqkShipSearch.shipList;
		// 处理数据，将数据归类到各自数组
		if (list != null && list.size() > 0) {
			int num = list.size();
			for (int i = 0; i < num; i++) {
				HashMap<String, Object> map = list.get(i);
				if (map != null) {
					String kacbzt = (String) map.get("kacbzt");
					if (kacbzt != null && kacbzt.equals(getString(R.string.state_ship_plan_come_gang))) {
						yuDaoGangList.add(map);
					} else if (kacbzt != null && kacbzt.equals(getString(R.string.state_ship_doing_gang))) {
						zaiGangList.add(map);
					} else if (kacbzt != null && kacbzt.equals(getString(R.string.state_ship_plan_out_gang))) {
						yuLiGangList.add(map);
						zaiGangList.add(map);// 预离港船只也属于在港船只
					}
				}
			}
			handler.sendEmptyMessage(0);
		} else {
			// 没有数据
			shipContent.setVisibility(View.GONE);
			HgqwToast.toast(getString(R.string.no_data));
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * ListView Item 按钮监听事件
	 */
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			boolean isOk = false;
			// 判断是否有数据
			HashMap<String, Object> ship = null;
			if (msg.obj != null) {
				ship = (HashMap<String, Object>) msg.obj;
				if (ship != null && ship.size() > 0) {
					isOk = true;
				}
			}
			if (!isOk) {
				HgqwToast.toast(getString(R.string.error_get_data));
				return;
			}

			switch (msg.what) {
			case 100:
				// 跳转到船舶详情页面
				toCbxqView(ship);
				break;
			case 101:
				// 获取船员名单
				getSailorList(ship);
				break;
			case 102:
				// 获取登轮登陆情况
				getDldl(ship);
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 
	 * @方法名：getDldl
	 * @功能说明：获取登轮登陆情况
	 * @author liums
	 * @date 2014-1-16 下午2:30:33
	 * @param position
	 */
	protected void getDldl(HashMap<String, Object> ship) {
		// 离线不查询
		if (!BaseApplication.instent.getWebState()) {
			HgqwToast.toast(R.string.no_web_cannot_check_dldl);
			return;
		}

		if (ship != null) {
			String hc = (String) ship.get("hc");
			Intent intent = new Intent();
			intent.putExtra("hc", hc);
			intent.setClass(getApplicationContext(), KacbqkDldlList.class);
			startActivity(intent);
		}

	}

	/**
	 * 
	 * @方法名：getSailorList
	 * @功能说明：获取船员名单
	 * @author liums
	 * @date 2014-1-16 下午2:22:33
	 * @param position
	 */
	protected void getSailorList(HashMap<String, Object> ship) {
		if (ship != null) {
			String hc = (String) ship.get("hc");
			Intent intent = new Intent();
			intent.putExtra("hc", hc);
			intent.setClass(getApplicationContext(), KacbqkSailorList.class);
			startActivity(intent);
		}
	}

	/**
	 * 
	 * @方法名：toCbxqView
	 * @功能说明：跳转到船舶详情页面
	 * @author liums
	 * @date 2014-1-15 下午6:45:33
	 * @param position
	 */
	protected void toCbxqView(HashMap<String, Object> ship) {
		Intent intent = new Intent();
		HashMap<String, Object> _BindShip = ship;
		intent.putExtra("hc", (String) (_BindShip.get("hc") == null ? "" : _BindShip.get("hc")));
		intent.putExtra("cbzwm", (String) (_BindShip.get("cbzwm") == null ? "" : _BindShip.get("cbzwm")));
		intent.putExtra("cbywm", (String) (_BindShip.get("cbywm") == null ? "" : _BindShip.get("cbywm")));
		intent.putExtra("gj", (String) (_BindShip.get("gj") == null ? "" : _BindShip.get("gj")));
		intent.putExtra("cbxz", (String) (_BindShip.get("cbxz") == null ? "" : _BindShip.get("cbxz")));
		intent.putExtra("bdzt", (String) (_BindShip.get("bdzt") == null ? "" : _BindShip.get("bdzt")));
		intent.putExtra("kacbzt", (String) (_BindShip.get("kacbzt") == null ? "" : _BindShip.get("kacbzt")));
		intent.putExtra("flagManagers", FlagManagers.KACB);
		intent.setClass(getApplicationContext(), KacbqkShipInfo.class);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void offLineResult(Pair<Boolean, Object> res, int offLineRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 根据用户点击设置RadioButton的文本颜色
	 * 
	 * @param radioButton
	 */
	public void changeRadioTextColorByOnClick(RadioButton radioButton) {
		// 设置预到港Tab字体
		if (rYuDaoGang == radioButton) {
			rYuDaoGang.setText(Html.fromHtml("<font color=\"#33b5e5\">" + getResources().getString(R.string.state_ship_plan_come_gang) + "</font>"));
		} else {
			rYuDaoGang.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.state_ship_plan_come_gang) + "</font>"));
		}
		// 设置在港Tab字体
		if (rZaiGang == radioButton) {
			rZaiGang.setText(Html.fromHtml("<font color=\"#33b5e5\">" + getResources().getString(R.string.state_ship_doing_gang) + "</font>"));
		} else {
			rZaiGang.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.state_ship_doing_gang) + "</font>"));
		}
		// 设置预离港Tab字体
		if (rYuLiGang == radioButton) {
			rYuLiGang.setText(Html.fromHtml("<font color=\"#33b5e5\">" + getResources().getString(R.string.state_ship_plan_out_gang) + "</font>"));
		} else {
			rYuLiGang.setText(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.state_ship_plan_out_gang) + "</font>"));
		}
	}

	/**
	 * 
	 * 
	 * 类描述：ViewPage适配器
	 * 
	 * <p>
	 * Title: 系统名称-KacbqkShipList.java
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
	 * @date 2014-2-18 下午3:30:46
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

	/**
	 * 每个标签的宽
	 */
	private int with = 0;

	/**
	 * 当前被选中的RadioButton距离左侧的距离
	 */
	private float mCurrentCheckedRadioLeft = 0;

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		isTabChange = true;
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation;
		Log.i("zj", "checkedid=" + checkedId);
		if (checkedId == R.id.rYuDaoGang) {
			// 预到港Tab被选中
			selectNum = 1;
			changeRadioTextColorByOnClick(rYuDaoGang);
			with = group.getChildAt(0).getWidth();
			translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, 0f, 0f, 0f);
			animationSet.addAnimation(translateAnimation);
			animationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft - 0));
			mImageView.startAnimation(animationSet);// 开始上面蓝色横条图片的动画切换
			handler.sendEmptyMessage(1);

		} else if (checkedId == R.id.rZaiGang) {
			// 在港Tab被选中
			selectNum = 2;
			changeRadioTextColorByOnClick(rZaiGang);
			with = group.getChildAt(1).getWidth();
			translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, with, 0f, 0f);

			animationSet.addAnimation(translateAnimation);
			animationSet.setFillAfter(true);
			animationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft - with));
			mImageView.startAnimation(animationSet);
			handler.sendEmptyMessage(2);

		} else if (checkedId == R.id.rYuLiGang) {
			// 预离港Tab被选中
			selectNum = 3;
			changeRadioTextColorByOnClick(rYuLiGang);
			with = group.getChildAt(2).getWidth();
			translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, with * 2 + 2, 0f, 0f);

			animationSet.addAnimation(translateAnimation);
			animationSet.setFillAfter(true);
			animationSet.setDuration((long) Math.abs(mCurrentCheckedRadioLeft - with * 2));
			mImageView.startAnimation(animationSet);

			handler.sendEmptyMessage(3);

		}
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();// 更新当前蓝色横条距离左边的距离
	}

	/**
	 * 获得当前被选中的RadioButton距离左侧的距离
	 */
	private float getCurrentCheckedRadioLeft() {
		// TODO Auto-generated method stub
		if (rYuDaoGang.isChecked()) {
			return 0f;
		} else if (rZaiGang.isChecked()) {
			return with;
		} else if (rYuLiGang.isChecked()) {
			return with * 2;
		}
		return 0f;
	}

}
