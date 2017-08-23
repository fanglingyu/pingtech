package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.UpdataInfo;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.login.activity.Login;
import com.pingtech.hgqw.module.xtgl.activity.FunctionSetting;
import com.pingtech.hgqw.module.xtgl.activity.OfflineDataActivity;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.utils.UpdataVersionManager;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 系统管理界面的activity类 */
public class SystemActivity extends MyActivity implements OnHttpResult {
	private static final String TAG = "SystemActivity";

	/** 网络设置 */
	private static final int STARTACTIVITY_FOR_NETWORK_SETTING = 1;

	/** GPS设置 */
	private static final int STARTACTIVITY_FOR_GPSSET = 2;

	/** 数据字典同步 */
	private static final int STARTACTIVITY_FOR_DICTSYNC = 3;

	/** 清除缓存 */
	private static final int STARTACTIVITY_FOR_CLEAN = 4;

	/** 修改密码 */
	private static final int STARTACTIVITY_FOR_CHANGEPSW = 5;

	/** 软件升级 */
	private static final int STARTACTIVITY_FOR_SWUPDATE = 6;

	/** 发起数据字典同步的http请求type */
	private static final int HTTPREQUEST_TYPE_GET_DD = 7;

	/** 发起软件更新的http请求type */
	private static final int HTTPREQUEST_TYPE_GET_SW_VERSION = 8;

	/** 离线数据维护 */
	private static final int HTTPREQUEST_TYPE_GET_OFFLINE_DATA = 9;

	/** 功能设置 */
	private static final int STARTACTIVITY_FOR_FUNCTION_SETTING = 10;

	private final int gridViewMenuItemId[] = { STARTACTIVITY_FOR_NETWORK_SETTING, STARTACTIVITY_FOR_GPSSET, STARTACTIVITY_FOR_DICTSYNC,
			STARTACTIVITY_FOR_CHANGEPSW, STARTACTIVITY_FOR_SWUPDATE, STARTACTIVITY_FOR_CLEAN, STARTACTIVITY_FOR_FUNCTION_SETTING };

	private final int gridViewMenuItemString[] = { R.string.network_setting, R.string.gpsset, R.string.dictionarysync, R.string.changepassword,
			R.string.swupdate, R.string.clean, R.string.function_setting };

	private final int gridViewMenuItemImage[] = { R.drawable.serverset, R.drawable.gpsset, R.drawable.dictsync, R.drawable.changepsw,
			R.drawable.swupdate, R.drawable.clean, R.drawable.system };

	private GridView mGridView;

	private int oldWhich = 0;

	private ProgressDialog progressDialog = null;

	private String httpReturnXMLInfo = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.main);

		Log.i(TAG, "onCreate()");
		setMyActiveTitle(R.string.system);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent;
				switch (gridViewMenuItemId[arg2]) {
				case STARTACTIVITY_FOR_NETWORK_SETTING:
					// if (BaseApplication.instent.getWebState()) {
					intent = new Intent();
					intent.setClass(getApplicationContext(), NetworkSettingActivity.class);
					startActivity(intent);
					// } else {
					// HgqwToast.makeText(SystemActivity.this,
					// R.string.no_web_cannot_webset,
					// HgqwToast.LENGTH_LONG).show();
					// }
					return;
				case STARTACTIVITY_FOR_GPSSET:
					if (BaseApplication.instent.getWebState()) {
						onSetGPSReportTimer();
					} else {
						HgqwToast.makeText(SystemActivity.this, R.string.no_web_cannot_gpsset, HgqwToast.LENGTH_LONG).show();
					}
					return;
				case STARTACTIVITY_FOR_DICTSYNC: {
					if (progressDialog != null) {
						return;
					}
					if (BaseApplication.instent.getWebState()) {
						String str = "getDD";
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						progressDialog = new ProgressDialog(SystemActivity.this);
						progressDialog.setTitle(getString(R.string.syncdata));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						NetWorkManager.request(SystemActivity.this, str, params, HTTPREQUEST_TYPE_GET_DD);
					} else {
						HgqwToast.makeText(SystemActivity.this, R.string.no_web_cannot_dictionary, HgqwToast.LENGTH_LONG).show();
					}
					return;
				}
				case STARTACTIVITY_FOR_CHANGEPSW:
					if (BaseApplication.instent.getWebState()) {
						if (Login.ADMINISTRATOR.equals(LoginUser.getCurrentLoginUser().getUserName())) {
							HgqwToast.makeText(SystemActivity.this, getString(R.string.no_permission), HgqwToast.LENGTH_LONG).show();
							return;
						}
						intent = new Intent();
						intent.setClass(getApplicationContext(), ChangePswActivity.class);
						startActivity(intent);
					} else {
						HgqwToast.makeText(SystemActivity.this, R.string.no_web_cannot_passwordset, HgqwToast.LENGTH_LONG).show();
					}
					return;
				case STARTACTIVITY_FOR_SWUPDATE:
					if (BaseApplication.instent.getWebState()) {
						progressDialog = new ProgressDialog(SystemActivity.this);
						progressDialog.setTitle(getString(R.string.swupdate));
						progressDialog.setMessage(getString(R.string.waiting));
						progressDialog.setCancelable(false);
						progressDialog.setIndeterminate(false);
						progressDialog.show();
						String str = "checkUpdate";
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("type", "0"));
						// 版本：0默认版本，1哨兵版，2船方自管
						params.add(new BasicNameValuePair("version", Flags.PDA_VERSION + ""));
						NetWorkManager.request(SystemActivity.this, str, params, HTTPREQUEST_TYPE_GET_SW_VERSION);
					} else {
						HgqwToast.makeText(SystemActivity.this, R.string.no_web_cannot_updatesoft, HgqwToast.LENGTH_LONG).show();
					}
					return;
				case STARTACTIVITY_FOR_CLEAN:
					if (BaseApplication.instent.getWebState()) {
						onCleanQuestDialog();
					} else {
						HgqwToast.makeText(SystemActivity.this, R.string.no_web_cannot_clearemsmemory, HgqwToast.LENGTH_LONG).show();
					}
					return;
				case HTTPREQUEST_TYPE_GET_OFFLINE_DATA:
					if (BaseApplication.instent.getWebState()) {
						offLineData();
					} else {
						HgqwToast.makeText(SystemActivity.this, R.string.no_web_cannot_download_offline_data, HgqwToast.LENGTH_LONG).show();
					}

					return;
				case STARTACTIVITY_FOR_FUNCTION_SETTING:
					intent = new Intent();
					intent.setClass(SystemActivity.this, FunctionSetting.class);
					startActivity(intent);
					return;
				}
			}

		});
		onUpdateGridViewmenu();
	}

	/**
	 * 
	 * @方法名：offLineData
	 * @功能说明：离线业务维护
	 * @author liums
	 * @date 2013-10-30 下午6:29:11
	 */
	protected void offLineData() {
		Intent intent = new Intent();
		intent.setClass(this, OfflineDataActivity.class);
		startActivity(intent);
	}

	/** 清除缓存前提示用户 */
	private void onCleanQuestDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.clean_quest);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SystemSetting.cleanBindShip();
				HgqwToast.makeText(SystemActivity.this, R.string.clean_success, HgqwToast.LENGTH_LONG).show();
			}
		});
		builder.setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
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

	/** 设置GPS上报时间间隔 */
	private void onSetGPSReportTimer() {
		// TODO Auto-generated method stub
		oldWhich = SystemSetting.getGPSTimer();
		if (oldWhich == 3600) {
			oldWhich = 5;
		} else if (oldWhich == 1800) {
			oldWhich = 4;
		} else if (oldWhich == 600) {
			oldWhich = 3;
		} else if (oldWhich == 300) {
			oldWhich = 2;
		} else if (oldWhich == 60) {
			oldWhich = 1;
		} else if (oldWhich == 30) {
			oldWhich = 0;
		} else {
			oldWhich = 0;
		}
		new AlertDialog.Builder(this)
				.setTitle(R.string.gps_stting_title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(
						new String[] { getString(R.string.set_30second), getString(R.string.set_60second), getString(R.string.set_300second),
								getString(R.string.set_600second), getString(R.string.set_1800second), getString(R.string.set_3600second) },
						oldWhich, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (oldWhich == which) {
									return;
								}
								SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
								SharedPreferences.Editor editor = prefs.edit();
								int value = 30;
								switch (which) {
								case 0:
									value = 30;
									break;
								case 1:
									value = 60;
									break;
								case 2:
									value = 300;
									break;
								case 3:
									value = 600;
									break;
								case 4:
									value = 1800;
									break;
								case 5:
									value = 3600;
									break;
								}
								editor.putInt(getString(R.string.gps_timer), value);
								editor.commit();
								SystemSetting.setGPSTimer(value);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	private void onUpdateGridViewmenu() {
		ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gridViewMenuItemString.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", gridViewMenuItemImage[i]);
			map.put("itemText", this.getResources().getString(gridViewMenuItemString[i]));
			lst.add(map);
		}
		SimpleAdapter adpter = new SimpleAdapter(this, lst, R.layout.mainmenu_item, new String[] { "itemImage", "itemText" }, new int[] {
				R.id.imageView_ItemImage, R.id.textView_ItemText });

		mGridView.setAdapter(adpter);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult:" + resultCode + ",resultCode:" + resultCode);
		switch (requestCode) {
		case STARTACTIVITY_FOR_SWUPDATE:
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

	/** 解析数据字典同步返回结果， */
	private boolean onParseDataDictionaryXMLData(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
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
						// 信息
						if (!success) {
							httpReturnXMLInfo = parser.nextText();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 处理平台返回结果。如果数据字典同步成功，更新本地的数据字典信息；如果 是软件升级，解析版本信息文件
	 * （获取版本号、获取版本详情等），根据版本号判断是否需要升级，如果需要升级，调用updateactivity启动升级。
	 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (httpRequestType == HTTPREQUEST_TYPE_GET_DD) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str != null) {
				if (onParseDataDictionaryXMLData(str)) {
					try {
						if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
							HgqwToast.makeText(SystemActivity.this, R.string.sdcardunmounted, HgqwToast.LENGTH_LONG).show();
							return;
						}
						String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech";
						File dir = new File(projectDir);
						if (!dir.exists()) {
							dir.mkdir();
						}
						FileWriter writer = new FileWriter(projectDir + File.separator + "datadict.xml");
						writer.write(str);
						writer.close();
						HgqwToast.makeText(SystemActivity.this, R.string.syncdatasuccess, HgqwToast.LENGTH_LONG).show();
						DataDictionary.restoreDataDictionary();
					} catch (IOException e) {
						e.printStackTrace();
						HgqwToast.makeText(SystemActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
					}
				} else {
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(SystemActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(SystemActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
					}
				}
			} else {
				HgqwToast.makeText(SystemActivity.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_GET_SW_VERSION) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (str == null) {
				HgqwToast.makeText(SystemActivity.this, getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG).show();
				return;
			}
			Intent intent = new Intent(SystemActivity.this, UpdateActivity.class);
			UpdataVersionManager manager = new UpdataVersionManager();
			UpdataInfo updataInfo = manager.updateVersion(str, this.getApplicationContext(), intent, Flags.PDA_VERSION_BYHAND);
			if (updataInfo != null) {
				boolean update = updataInfo.isUpdate();
				if (update) {
					startActivityForResult(intent, STARTACTIVITY_FOR_SWUPDATE);
				}
			}
		}
		httpRequestType = 0;
	}
}
