package com.pingtech.hgqw.module.bindship.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.DutyPersonlistActivity;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.activity.ShipDetailActivity;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.bindship.adapter.ShipBindAdapter;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownload;
import com.pingtech.hgqw.module.xtgl.service.OffDataDownloadForBd;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 根据电子标签号，显示船舶列表界面
 * 
 * */
public class ShipBindList extends MyActivity implements OnHttpResult {
	private static final String TAG = "ShipBindList";

	public static final String FROM_BINDLIST = "bindlist";

	/** 发起船舶绑定的http请求的type */
	private static final int HTTPREQUEST_TYPE_FOR_BINDSHIP = 5;

	/** 进入船舶详情界面 */
	private static final int STARTACTIVITY_FOR_SHIP_DETAIL = 6;

	/** 进入查看执勤人员的界面 */
	private static final int STARTACTIVITY_FOR_SHIP_DUTY = 7;

	private int fromType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;

	private ListView listView;

	private ShipBindAdapter adapter;

	private ArrayList<HashMap<String, Object>> shipInfoList = null;

	private HashMap<String, Object> bindMap;

	private ProgressDialog progressDialog = null;

	private ShipBindHandler handler = null;

	/**
	 * 船方自管标志位：true来自船方自管，false默认版本
	 */
	private boolean cfzgFlag = false;

	private String from;

	/**
	 * 是否有已经绑定的船舶：false没有，true有
	 */
	// private boolean hasBindFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState, R.layout.shipbind_list);
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		shipInfoList = SystemSetting.shipInfoList;
		if (GlobalFlags.BINDSHIP_FROM_KAKOUMANAGER.equals(from)) {
			setMyActiveTitle(getString(R.string.kakoumanager) + ">" + getString(R.string.bindShip));
			if (findViewById(R.id.ship) != null) {
				findViewById(R.id.ship).setVisibility(View.GONE);
			}
			if (findViewById(R.id.kk) != null) {
				findViewById(R.id.kk).setVisibility(View.VISIBLE);
			}
			fromType = GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER;

		} else if (GlobalFlags.BINDSHIP_FROM_TIKOUMANAGER.equals(from)) {
			fromType = GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER;
			setMyActiveTitle(getString(R.string.tikoumanager) + ">" + getString(R.string.bindShip));
		} else if (GlobalFlags.BINDSHIP_FROM_XUNCHAXUNJIAN.equals(from)) {
			fromType = GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN;
			setMyActiveTitle(getString(R.string.xunchaxunjian) + ">" + getString(R.string.bindShip));
		} else if (GlobalFlags.BINDSHIP_FROM_SHIPSTATUS.equals(from)) {
			fromType = GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS;
			setMyActiveTitle(getString(R.string.ShipStatus) + ">" + getString(R.string.bindShip));
		}
		listView = (ListView) findViewById(R.id.listview);
		if (shipInfoList != null && shipInfoList.size() > 0) {
			if (findViewById(R.id.listview_topline) != null) {
				findViewById(R.id.listview_topline).setVisibility(View.VISIBLE);
			}
			if (findViewById(R.id.select_result_empty) != null) {
				findViewById(R.id.select_result_empty).setVisibility(View.GONE);
			}
			listView.setVisibility(View.VISIBLE);
		} else {
			shipInfoList = new ArrayList<HashMap<String, Object>>();

		}

		handler = new ShipBindHandler();
		adapter = new ShipBindAdapter(this, shipInfoList, handler, fromType);
		listView.setAdapter(adapter);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
		SystemSetting.shipInfoList.clear();
	}

	/** 从详情界面返回时，如果已经执行船舶绑定了，立即返回 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_SHIP_DETAIL:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		}
	}

	/** 处理平台返回的数据 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {

		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (HTTPREQUEST_TYPE_FOR_BINDSHIP == httpRequestType) {
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				bindMap.put("bdzt", "已绑定");
				SystemSetting.setBindShip(bindMap, fromType + "");
				HgqwToast.makeText(ShipBindList.this, R.string.bindship_success, HgqwToast.LENGTH_LONG).show();

				if (GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == fromType || GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == fromType
						|| GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER == fromType) {
					downloadOfflineData(bindMap, fromType);
				}else{
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					finish();
				}
			} else if (str != null && "3".equals(str)) {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(ShipBindList.this, R.string.had_bind_ship, HgqwToast.LENGTH_LONG).show();
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				HgqwToast.makeText(ShipBindList.this, R.string.bindship_failure, HgqwToast.LENGTH_LONG).show();
				SystemSetting.setBindShip(null, fromType + "");
			}
		}
	}

	private OffDataDownloadForBd dataDownload = null;

	private void downloadOfflineData(HashMap<String, Object> bindMap, int fromType) {
		if (GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == fromType || GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == fromType) {
			dataDownload = new OffDataDownloadForBd(handler, bindMap, OffLineUtil.DOWNLOAD_FOR_KACBQK, 3);
			// dataDownload = new OffDataDownloadForBd(handler, bindMap,
			// OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_HC, 3);
		} else {
			dataDownload = new OffDataDownloadForBd(handler, bindMap, OffLineUtil.DOWNLOAD_FOR_QYXX, 3);
			// dataDownload = new OffDataDownloadForBd(handler, bindMap,
			// OffLineUtil.DOWNLOAD_ALL_OFFLINE_DATA_FOR_KKID, 3);
		}
		dataDownload.requestAgain();
		// progressDialog = new ProgressDialog(getApplicationContext());
		if (progressDialog != null) {
			progressDialog.setMessage("正在下载离线所需数据");
		}
		// progressDialog.show();
	}
	private StringBuilder stringBuilder = new StringBuilder();
	class ShipBindHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case OffDataDownload.WHAT_DOWNLOAD_SUCCESS_ONE:// 下载完成一个
				// 码头、泊位、区域、船舶、证件、船员
				String str = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str + "，下载完成");
				stringBuilder.append("\n");
				if (progressDialog != null) {
					progressDialog.setMessage(stringBuilder.toString());
				}
				break;

			case OffDataDownload.WHAT_DOWNLOAD_ONE_RESULT_NULL:// 下载失败一个
			case OffDataDownload.WHAT_INSERT_DATA_FAILED_ONE:// 下载失败一个
				// 码头、泊位、区域、船舶、证件、船员
				String str1 = dataDownload.mapString.get(msg.arg1);
				stringBuilder.append(str1 + "，下载失败");
				stringBuilder.append("\n");
				if (progressDialog != null) {
					progressDialog.setCancelable(true);
					progressDialog.setMessage(stringBuilder.toString());
				}
				break;
			case OffDataDownload.WHAT_DOWNLOAD_ALL_SUCCESS:// 下载完成
				HgqwToast.toast("下载完成");
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				finish();
				break;
			case ShipBindAdapter.POSITIONZERO:
				// 动作
				if (fromType == GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER) {
					String url = "buildKkRelation";
					if (progressDialog != null) {
						return;
					}
					bindMap = shipInfoList.get(msg.arg1);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
					params.add(new BasicNameValuePair("bindState", "1"));
					params.add(new BasicNameValuePair("kkID", (String) bindMap.get("id")));
					params.add(new BasicNameValuePair("bindType", fromType + ""));
					//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
					params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_KK + ""));

					progressDialog = new ProgressDialog(ShipBindList.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					NetWorkManager.request(ShipBindList.this, url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
				} else {
					String url = "buildRelation";
					if (progressDialog != null) {
						return;
					}
					bindMap = shipInfoList.get(msg.arg1);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
					params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
					params.add(new BasicNameValuePair("bindState", "1"));
					params.add(new BasicNameValuePair("voyageNumber", (String) bindMap.get("hc")));
					params.add(new BasicNameValuePair("bindType", fromType + ""));
					//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
					params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
					

					progressDialog = new ProgressDialog(ShipBindList.this);
					progressDialog.setTitle(getString(R.string.waiting));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					NetWorkManager.request(ShipBindList.this, url, params, HTTPREQUEST_TYPE_FOR_BINDSHIP);
				}

				break;
			case ShipBindAdapter.POSITIONONE:
				// 详情
				Map<String, Object> _BindShip = shipInfoList.get(msg.arg1);
				intent.putExtra("hc", (String) _BindShip.get("hc"));
				intent.putExtra("cbzwm", (String) _BindShip.get("cbzwm"));
				intent.putExtra("cbywm", (String) _BindShip.get("cbywm"));
				intent.putExtra("gj", (String) _BindShip.get("gj"));
				intent.putExtra("cbxz", (String) _BindShip.get("cbxz"));
				intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
				intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
				intent.putExtra("from", fromType);
				intent.putExtra("cfzgFlag", cfzgFlag);
				if (fromType == GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN) {
					intent.putExtra("fromxunchaxunjian", true);
					intent.putExtra("frombindship", true);
				}
				intent.setClass(getApplicationContext(), ShipDetailActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_SHIP_DETAIL);

				break;
			case ShipBindAdapter.POSITIONSECOND:

				// 执勤人员
				HashMap<String, Object> _Ship = shipInfoList.get(msg.arg1);
				intent.putExtra("hc", (String) (_Ship.get("hc") == null ? "" : _Ship.get("hc")));
				intent.putExtra("from", "0");
				intent.setClass(getApplicationContext(), DutyPersonlistActivity.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_SHIP_DUTY);

				break;

			default:
				break;
			}
		}

	}

}
