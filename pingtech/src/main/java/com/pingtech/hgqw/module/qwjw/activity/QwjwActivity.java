package com.pingtech.hgqw.module.qwjw.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.pingtech.R;
import com.pingtech.hgqw.base.activity.BaseActivity;
import com.pingtech.hgqw.module.qwjw.adapter.ViewPagetAdapter;
import com.pingtech.hgqw.module.qwjw.fragment.JwzlFragment;
import com.pingtech.hgqw.module.qwjw.fragment.QwzlFragment;
import com.pingtech.hgqw.module.qwjw.utils.FragmentTag;

@SuppressLint("NewApi")
public class QwjwActivity<qwzlFragment> extends BaseActivity {

	private static final String TAG = "QwjwActivity";

	private ViewPager viewPager = null;

	private Button text1 = null;

	private Button text2 = null;

	private int width = 0;

	private int vertical_line_width = 0;

	private int mWidth = 0;

	private ImageView cursor = null;

	private ImageView vertical_line = null;

	private ViewPagetAdapter viewPagetAdapter = null;

	private PagerTabStrip pagerTabStrip = null;

	private Fragment qwzlFragment = null;

	private Tab tab = null;

	private Fragment jwzlFragment = null;

	private FragmentManager fragmentManager = null;

	private ArrayList<Fragment> data = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		String title = getString(R.string.qwjw);
		super.onCreate(savedInstanceState, R.layout.qwjw_layout, title);
		// ActivityUtil.openTab(this);
		find();
		initViewPager();

	}

	protected void find() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		text1 = (Button) findViewById(R.id.text1);
		text2 = (Button) findViewById(R.id.text2);
		cursor = (ImageView) findViewById(R.id.cursor);
		vertical_line = (ImageView) findViewById(R.id.vertical_line);

	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.text1:
			viewPager.setCurrentItem(0);
			break;
		case R.id.text2:
			viewPager.setCurrentItem(1);
			break;

		default:
			break;
		}
	}

	private void initViewPager() {
		initFragment();
		initPagerTabStrip();
		viewPagetAdapter = new ViewPagetAdapter(fragmentManager, data);

		viewPager.setAdapter(viewPagetAdapter);

		viewPager.setOnPageChangeListener(new ViewPagerOnPageChangeListener());
	}

	private void initPagerTabStrip() {
	}

	private class ViewPagerOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			changeFragment(arg0);
		}

	}

	private void changeFragment(int arg0) {
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation = null;
		width = text1.getWidth();
		vertical_line_width = vertical_line.getWidth();
		switch (arg0) {
		case 0:
			text1.setTextColor(getResources().getColor(R.color.selectTextColor));
			text2.setTextColor(getResources().getColor(R.color.textcolor));

			translateAnimation = new TranslateAnimation(mWidth, 0, 0, 0);
			animationSet.setFillAfter(true);
			animationSet.setDuration(200);
			animationSet.addAnimation(translateAnimation);
			cursor.startAnimation(animationSet);
			mWidth = 0;
			break;
		case 1:
			text1.setTextColor(getResources().getColor(R.color.textcolor));
			text2.setTextColor(getResources().getColor(R.color.selectTextColor));
			translateAnimation = new TranslateAnimation(mWidth, width + vertical_line_width, 0, 0);
			animationSet.setDuration(200);

			animationSet.setFillAfter(true);
			animationSet.addAnimation(translateAnimation);
			cursor.startAnimation(animationSet);
			mWidth = width + vertical_line_width;
			break;
		default:
			break;
		}
	}

	private void initFragment() {
		fragmentManager = getFragmentManager();
		qwzlFragment = new QwzlFragment(this, this);
		jwzlFragment = new JwzlFragment(this, this);

//		data.add(jwzlFragment);
		data.add(qwzlFragment);

		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		transaction.add(R.id.viewpager, jwzlFragment, FragmentTag.TAG_0);
		transaction.add(R.id.viewpager, qwzlFragment, FragmentTag.TAG_1);

		transaction.commitAllowingStateLoss();
		fragmentManager.executePendingTransactions();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG, "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed");
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}

	};

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}
}
