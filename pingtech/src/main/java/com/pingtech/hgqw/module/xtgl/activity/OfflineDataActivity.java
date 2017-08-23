package com.pingtech.hgqw.module.xtgl.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.base.ActivityInterface;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.xtgl.adapter.OfflineDataAdapter;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;
import com.pingtech.hgqw.module.xtgl.utils.XtglUtil;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 
 * 类描述：离线数据下载
 * 
 * <p>
 * Title: 系统名称-OfflineDataActivity.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author jiajw
 * @version 1.0
 * @date 2014-2-24 上午10:07:15
 */
public class OfflineDataActivity extends MyActivity implements ActivityInterface {

	public static final String TAG = "OfflineDataActivity";

	/** 离线数据列表listView显示组件 */

	private ListView offlinedata_listview = null;

	/**
	 * 离线同步按钮
	 */

	private Button offDataDownBtn;

	/**
	 * 下载提示progressBar
	 */

	private ProgressBar progressBar;

	/** 离线数据下载同步状态的相关信息 */

	private TextView offlinedata_textview_state_info = null;

	/** 下载请求提示 */

	private ProgressDialog progressDialog = null;

	/** 离线数据 */

	private List<Map<String, Object>> data = null;

	/** 列表适配器 */

	private OfflineDataAdapter adapter = null;

	/** 离线下载线程类 */

	private OffDataDownload dataDownload = null;

	/**
	 * 下载成功标识
	 */
	private boolean flag = true;

	/**
	 * 下载不成功项的序号
	 */
	private int state;

	/**
	 * 下载项的名称
	 */
	private String itemName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.offlinedata);
		setMyActiveTitle(R.string.app_name_all);
		this.findView();
		this.initData();
		setMyActiveTitle(getString(R.string.offlinedata));
	}

	/**
	 * 初始化listView界面控件
	 */
	public void findView() {
		offlinedata_listview = (ListView) findViewById(R.id.offlinedata_listview);
		offlinedata_textview_state_info = (TextView) findViewById(R.id.offlinedata_textview_state_info);
		offDataDownBtn = (Button) findViewById(R.id.offlinedata_btn_download);
		progressBar = (ProgressBar) findViewById(R.id.offlinedata_probar_downloading);
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void initData() {
		data = new ArrayList<Map<String, Object>>();
		adapter = new OfflineDataAdapter(this, data, handler);
		offlinedata_listview.setAdapter(adapter);
	}

	/**
	 * 接收下载后的状态改变，来进行界面的更新
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			/*
			 * arg=0:表示第一次下载，arg=5:表示当前项是下载失败后重新下载
			 */
			int arg = msg.arg2;
			switch (what) {
			case OffDataDownload.WHAT_DOWNLOAD_SUCCESS_ONE:
				int key = msg.arg1;// 完成标识(1000~1011)
				if (arg == 0) {
					downloadSuccessOne(key);// 下载成功一项后页面增加一个成功的项
				} else {
					OffDataDownload.loading = false;
					flag = true;
					updataDownloadSuccess(key);
				}

				break;

			case OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_NULL:
				int keyOfNull = msg.arg1;// 完成标识(1000~1011)
				if (arg == 0) {
					downloadFailedOne(keyOfNull);// 下载失败后页面增加一个下载失败的项
				} else {
					OffDataDownload.loading = false;
					flag = false;
					updataDownloadSuccess(keyOfNull);
				}

				break;

			case OffDataDownload.WHAT_DOWNLOAD_ALL_SUCCESS:
				downloadSuccess();
				break;

			case OffDataDownload.WHAT_DOWNLOAD_ALL_HAS_FAILED:
				downloadFailed();
				break;

			case OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_SUCCESS:

				@SuppressWarnings("unchecked")
				Map<Object, Object> mapUrl = (Map<Object, Object>) msg.obj;

				int resultCode = (Integer) mapUrl.get("offlinedata_item_resultcode");// 下载失败项请求服务的键
				itemName = (String) mapUrl.get("offlinedata_item_textview");
				state = msg.arg1;
				downloadFailedItem(resultCode, itemName);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 
	 * @方法名：downloadSuccess
	 * @功能说明：请求成功业务
	 * @author liums
	 * @date 2013-11-12 上午9:46:17
	 */
	private void downloadSuccess() {
		HgqwToast.toast((("null".equals(itemName) || StringUtils.isEmpty(itemName)) ? "" : itemName)
				+ getString(R.string.offlinedata_download_success));
		resetOfSuccess();
	}

	/**
	 * 
	 * @方法名：downloadFailedItem
	 * @功能说明：下载失败后，重新下载失败的数据
	 * @author jiajw
	 * @date 2014-2-25 上午11:56:08
	 * @param url
	 * @param itemName
	 */
	protected void downloadFailedItem(Integer key, String itemName) {

		/**
		 * 开始执行同步下载操作
		 */
		dataDownload = new OffDataDownload(handler);
		/**
		 * 同时下载标识置为true
		 */
		BaseApplication.instent.setDownloadFlag(true);

		dataDownload.requestNext(key);

		OffDataDownload.loading = false;

		// 下载过程中按钮不可点
		adapter.setClikable(false);
		adapter.notifyDataSetChanged();

		offDataDownBtn.setEnabled(false);
		offlinedata_textview_state_info.setVisibility(View.VISIBLE);
		offlinedata_textview_state_info.setText((("null".equals(itemName) || StringUtils.isEmpty(itemName)) ? "" : itemName)
				+ getString(R.string.offlinedata_download_busy));
		progressBar.setVisibility(View.VISIBLE);
		showDownloadDialog();

	}

	/**
	 * 
	 * @方法名：reset
	 * @功能说明：下载成功，页面重置
	 * @author liums
	 * @date 2013-12-19 下午4:49:19
	 */
	private void resetOfSuccess() {
		offDataDownBtn.setEnabled(true);
		offlinedata_textview_state_info.setVisibility(View.VISIBLE);
		offlinedata_textview_state_info.setText((("null".equals(itemName) || StringUtils.isEmpty(itemName)) ? "" : itemName)
				+ getString(R.string.offlinedata_download_success));
		progressBar.setVisibility(View.GONE);
		if (progressDialog != null) {
			progressDialog.cancel();
			progressDialog = null;
		}
		BaseApplication.instent.setDownloadFlag(false);
	}

	/**
	 * 
	 * @方法名：reset
	 * @功能说明：中途停止下载，页面重置
	 * @author liums
	 * @date 2013-12-19 下午4:49:19
	 */
	private void resetOfStop() {
		offDataDownBtn.setEnabled(true);
		offlinedata_textview_state_info.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		offlinedata_listview.setVisibility(View.GONE);
		if (progressDialog != null) {
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	/**
	 * 
	 * @方法名：downloadFailed
	 * @功能说明：请求失败
	 * @author liums
	 * @param failedKey
	 * @date 2013-11-12 上午9:46:45
	 */
	protected void downloadFailed() {

		HgqwToast.toast(R.string.offlinedata_download_faile);
		// findViewById(R.id.offlinedata_btn_download).setEnabled(true);
		// offDataDownBtn.setVisibility(View.GONE);
		offDataDownBtn.setEnabled(true);
		offlinedata_textview_state_info.setVisibility(View.VISIBLE);
		offlinedata_textview_state_info.setText(getString(R.string.offlinedata_download_failed_someone));
		progressBar.setVisibility(View.GONE);

		if (progressDialog != null) {
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	/**
	 * 数据同步按钮的单击事件
	 */
	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.offlinedata_btn_download:
			startDownLoad();
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：startDownLoad
	 * @功能说明：开始下载（从服务端下载离线数据）
	 * @author liums
	 * @date 2013-11-1 上午9:57:02
	 */
	private void startDownLoad() {
		offlinedata_listview.setVisibility(View.VISIBLE);
		// 检查是否删除过数据库，删除数据库,审计id置0
		if (!XtglUtil.hasAlreadyDel()) {
			// 删除数据库
			boolean isOk = XtglUtil.delDB();
			Log.i(TAG, "XtglUtil.delDB(),result=" + isOk);
			// 修改审计id
			XtglUtil.delSjid();
			Log.i(TAG, "XtglUtil.delSjid()");
		}

		if (data != null) {
			data.clear();
		}
		dataDownload = new OffDataDownload(handler);
		/**
		 * 同时下载标识置为true
		 */
		BaseApplication.instent.setDownloadFlag(true);
		dataDownload.requestAgain();
		OffDataDownload.loading = true;
		loadingState();
		showDownloadDialog();
	}

	/**
	 * 
	 * @方法名：downState
	 * @功能说明：下载数据时的相关提示
	 * @author jiajw
	 * @date 2014-2-26 下午2:36:07
	 */
	private void loadingState() {
		offDataDownBtn.setEnabled(false);
		offlinedata_textview_state_info.setVisibility(View.VISIBLE);
		offlinedata_textview_state_info.setText(getString(R.string.offlinedata_download_busy));
		progressBar.setVisibility(View.VISIBLE);

	}

	/**
	 * 
	 * @方法名：showDownloadDialog
	 * @功能说明：下载中提示框
	 * @author liums
	 * @date 2013-11-1 下午2:18:06
	 */
	private void showDownloadDialog() {
		if (progressDialog != null) {
			return;
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.offlinedata_download_busy));
		progressDialog.setCancelable(true);
		progressDialog.setIndeterminate(false);
		// progressDialog.show();
	}

	/**
	 * 
	 * @方法名：downloadSuccessOne
	 * @功能说明：数据下载完成一个，页面显示数据量
	 * @author liums
	 * @date 2013-11-1 上午9:43:30
	 * @param key
	 */
	protected void downloadSuccessOne(int key) {
		Log.i(TAG, "页面增加一个，继续下载其他列表项" + key);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offlinedata_item_textview", OffDataDownload.mapString.get(key));
		map.put("offlinedata_item_img", R.drawable.btn_check_buttonless_on);
		data.add(map);
		adapter.notifyDataSetChanged();
		offlinedata_listview.setSelection(offlinedata_listview.getBottom());

	}

	/**
	 * 
	 * @方法名：updataDownloadSuccess
	 * @功能说明：重新下载的数据，下载完成后的数据处理
	 * @author jiajw
	 * @date 2014-2-27 上午11:02:07
	 * @param key
	 */
	private void updataDownloadSuccess(int key) {
		Log.i(TAG, "重新下载的数据，下载完成后的数据处理");

		if (flag) {
			Log.i(TAG, "下载成功标志");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("offlinedata_item_textview", OffDataDownload.mapString.get(key));
			map.put("offlinedata_item_img", R.drawable.btn_check_buttonless_on);
			data.set(state, map);
			adapter.setClikable(true);
			adapter.notifyDataSetChanged();
			downloadSuccess();

		} else {

			Log.i(TAG, "下载失败标志");
			adapter.setClikable(true);
			adapter.notifyDataSetChanged();
			downloadFailed();
		}

	}

	/**
	 * 
	 * @方法名：downloadFailedOne
	 * @功能说明：数据下载失败一个
	 * @author liums
	 * @date 2013-11-12 上午11:32:54
	 * @param keyOfNull
	 */
	protected void downloadFailedOne(int keyOfNull) {
		Log.i(TAG, "下载失败后继续下载" + keyOfNull);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offlinedata_item_textview", OffDataDownload.mapString.get(keyOfNull));
		map.put("offlinedata_item_img", R.drawable.ic_delete);
		map.put("offlinedata_item_resultcode", keyOfNull);
		map.put("btnclickable", "1");
		data.add(map);
		adapter.notifyDataSetChanged();
		offlinedata_listview.setSelection(offlinedata_listview.getBottom());
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		if (progressDialog != null) {
			/*
			 * if (dataDownload != null) { dataDownload.setDownloadFlag(false);
			 * dataDownload = null; }
			 */

			BaseApplication.instent.setDownloadFlag(false);

			resetOfStop();
			if (progressDialog != null) {
				progressDialog.cancel();
				progressDialog = null;
			}
		}
		super.onStop();
	}

	/* 监听物理按键 */

	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		android.util.Log.i(TAG, "onKeyDown:" + keyCode);

		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:
			return homeClick(keyCode, event);
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * @方法名：homeClick
	 * @功能说明：离线数据下载中，无法使用HOME键
	 * @author liums
	 * @date 2013-12-19 下午5:10:42
	 * @param keyCode
	 * @param event
	 * @return
	 */
	private boolean homeClick(int keyCode, KeyEvent event) {
		if (progressDialog != null) {
			// 离线数据下载中，无法使用HOME键
			HgqwToast.toast(getString(R.string.offlinedata_down_home_disable));
		} else {
			activityForHome();
		}
		return false;
	}

	/**
	 * 
	 * @方法名：activityForExit
	 * @功能说明：模拟HOME键
	 * @author liums
	 * @date 2013-11-29 下午3:35:31
	 */
	private void activityForHome() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
}
