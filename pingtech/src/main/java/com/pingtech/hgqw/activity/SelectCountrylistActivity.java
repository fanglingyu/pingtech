package com.pingtech.hgqw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.adapter.OptionsAdapter;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;

/**
 * 选择国家列表界面的activity类，由于国家数量较多，如果采用普通的单选框界面，选择费劲，为增强用户体验，采用自定义界面，在界面最右边设置英文字母，
 * 点击英文字母后，自动跳到对应汉字的位置
 * */

public class SelectCountrylistActivity extends Activity {
	private static final String TAG = "SelectCountrylistActivity";
	private String type;
	private ListView listView = null;
	private OptionsAdapter optionsAdapter;
	/** 用来显示放大的首汉字 */
	private TextView overlay = null;
	boolean startScroll = false;
	/** 处理点击右边英文字母快捷按钮 */
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String str = ((TextView) v).getText().toString();
			startScroll = true;
			if (str.equals("A")) {
				listView.setSelectionFromTop(0, 0);
			} else if (str.equals("B")) {
				listView.setSelectionFromTop(18, 0);
			} else if (str.equals("C")) {
				listView.setSelectionFromTop(44, 0);
			} else if (str.equals("D")) {
				listView.setSelectionFromTop(46, 0);
			} else if (str.equals("E")) {
				listView.setSelectionFromTop(52, 0);
			} else if (str.equals("F")) {
				listView.setSelectionFromTop(55, 0);
			} else if (str.equals("G")) {
				listView.setSelectionFromTop(66, 0);
			} else if (str.equals("H")) {
				listView.setSelectionFromTop(79, 0);
			} else if (str.equals("J")) {
				listView.setSelectionFromTop(88, 0);
			} else if (str.equals("K")) {
				listView.setSelectionFromTop(100, 0);
			} else if (str.equals("L")) {
				listView.setSelectionFromTop(110, 0);
			} else if (str.equals("M")) {
				listView.setSelectionFromTop(123, 0);
			} else if (str.equals("N")) {
				listView.setSelectionFromTop(150, 0);
			} else if (str.equals("P")) {
				listView.setSelectionFromTop(163, 0);
			} else if (str.equals("R")) {
				listView.setSelectionFromTop(166, 0);
			} else if (str.equals("S")) {
				listView.setSelectionFromTop(169, 0);
			} else if (str.equals("T")) {
				listView.setSelectionFromTop(195, 0);
			} else if (str.equals("W")) {
				listView.setSelectionFromTop(207, 0);
			} else if (str.equals("X")) {
				listView.setSelectionFromTop(218, 0);
			} else if (str.equals("Y")) {
				listView.setSelectionFromTop(226, 0);
			} else if (str.equals("Z")) {
				listView.setSelectionFromTop(241, 0);
			}
		}
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		if (type.equals("countrylist")) {
			setContentView(R.layout.nationality_options);
			overlay = (TextView) getLayoutInflater().inflate(R.layout.nationality_option_overlay, null);
			getWindowManager().addView(
					overlay,
					new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
							WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
									| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT));

			listView = (ListView) findViewById(R.id.list);
			listView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					Log.i(TAG, "onScrollStateChanged：" + scrollState);
					startScroll = true;
					if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
						if (overlay != null) {
							overlay.setVisibility(View.INVISIBLE);
							startScroll = false;
						}
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					if (totalItemCount <= 0 || !startScroll) {
						return;
					}
					Log.i(TAG, "onScroll：" + firstVisibleItem);
					overlay.setText(SystemSetting.getCountryList().get(firstVisibleItem).substring(0, 1));
					overlay.setVisibility(View.VISIBLE);
				}
			});
			// 设置自定义Adapter
			optionsAdapter = new OptionsAdapter(this, SystemSetting.getCountryList());
			listView.setAdapter(optionsAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					Intent data = new Intent();
					data.putExtra("selectitem", position);
					setResult(RESULT_OK, data);
					finish();
				}
			});
			listView.setSelectionFromTop(DataDictionary.getCountryIndexByName(intent.getStringExtra("selectitem")), 0);

			findViewById(R.id.a).setOnClickListener(clickListener);
			findViewById(R.id.b).setOnClickListener(clickListener);
			findViewById(R.id.c).setOnClickListener(clickListener);
			findViewById(R.id.d).setOnClickListener(clickListener);
			findViewById(R.id.e).setOnClickListener(clickListener);
			findViewById(R.id.f).setOnClickListener(clickListener);
			findViewById(R.id.g).setOnClickListener(clickListener);
			findViewById(R.id.h).setOnClickListener(clickListener);
			findViewById(R.id.j).setOnClickListener(clickListener);
			findViewById(R.id.k).setOnClickListener(clickListener);
			findViewById(R.id.l).setOnClickListener(clickListener);
			findViewById(R.id.m).setOnClickListener(clickListener);
			findViewById(R.id.n).setOnClickListener(clickListener);
			findViewById(R.id.p).setOnClickListener(clickListener);
			findViewById(R.id.r).setOnClickListener(clickListener);
			findViewById(R.id.s).setOnClickListener(clickListener);
			findViewById(R.id.t).setOnClickListener(clickListener);
			findViewById(R.id.w).setOnClickListener(clickListener);
			findViewById(R.id.x).setOnClickListener(clickListener);
			findViewById(R.id.y).setOnClickListener(clickListener);
			findViewById(R.id.z).setOnClickListener(clickListener);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		getWindowManager().removeView(overlay);
		overlay = null;
		super.onDestroy();
	}

}