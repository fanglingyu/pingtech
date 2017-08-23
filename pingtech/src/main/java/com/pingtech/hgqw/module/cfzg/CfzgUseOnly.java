package com.pingtech.hgqw.module.cfzg;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.pingtech.R;
import com.pingtech.hgqw.entity.FlagManagers;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.UpdataInfo;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BaseInfoData;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.utils.UpdataVersionManager;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 船舶动态界面的activity类 */
public class CfzgUseOnly extends CfzgSuperActivity implements OnHttpResult {
	private static final String TAG = "CfzgUseOnly";

	/** 船舶绑定 */
	private static final int STARTACTIVITY_FOR_BIND = 1;

	/** 船舶离港 */
	private static final int STARTACTIVITY_FOR_LEAVE = 2;

	/** 人员平衡 */
	private static final int STARTACTIVITY_FOR_PERSONNEL_BALANCE = 3;

	/** 船舶抵港 */
	private static final int STARTACTIVITY_FOR_ARRIVE = 4;

	/** 船舶解除绑定 */
	private static final int STARTACTIVITY_FOR_UNBIND = 5;

	/** 进入刷电子标签 */
	private static final int STARTACTIVITY_FOR_READICCARD = 6;

	/** 进入人员平衡 */
	private static final int STARTACTIVITY_FOR_PERSON_BALANCE = 7;

	/** 软件升级 */
	private static final int STARTACTIVITY_FOR_SWUPDATE = 600;

	/** 发起软件更新的http请求type */
	private static final int HTTPREQUEST_TYPE_GET_SW_VERSION = 800;

	private final int gridViewMenuItemId[] = { STARTACTIVITY_FOR_BIND, STARTACTIVITY_FOR_UNBIND, STARTACTIVITY_FOR_SWUPDATE };

	private final int gridViewMenuItemString[] = { R.string.bindShip, R.string.unbindShip, R.string.swupdate };

	private final int gridViewMenuItemImage[] = { R.drawable.bindship, R.drawable.unbindship, R.drawable.swupdate };

	private GridView mGridView;

	private int mType;

	private boolean cn = true;

	private ProgressDialog progressDialog = null;

	/** 进入船舶绑定时，如果存在快速绑定条件，提示是否选择快速绑定 */
	private void onShowQuestFromBindListDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.query_from_bindlist);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("title", getString(R.string.bindShip));
				intent.putExtra("cardNumber", CfzgShipListActivity.FROM_BINDLIST);
				intent.putExtra("cfzgFlag", true);
				intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
				intent.setClass(getApplicationContext(), CfzgShipListActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("from", "0501");
				intent.setClass(getApplicationContext(), CfzgShipBind.class);
				startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
			}
		});
		builder.create().show();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateForCfzg(savedInstanceState, R.layout.cfzg_main);
		// 注销按钮显示
		findViewById(R.id.cfzg_btn_logout).setVisibility(View.VISIBLE);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle("边检专用页面");
		Intent intent = getIntent();
		cn = intent.getBooleanExtra("cn", true);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mType = gridViewMenuItemId[arg2];
				HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
				Intent intent = new Intent();
				switch (gridViewMenuItemId[arg2]) {

				case STARTACTIVITY_FOR_SWUPDATE:
					progressDialog = new ProgressDialog(CfzgUseOnly.this);
					progressDialog.setTitle(getString(R.string.swupdate));
					progressDialog.setMessage(getString(R.string.waiting));
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(false);
					progressDialog.show();
					String strCheckUpdate = "checkUpdate";
					List<NameValuePair> paramsCheckUpdate = new ArrayList<NameValuePair>();
					paramsCheckUpdate.add(new BasicNameValuePair("type", "0"));
					// 版本：0默认版本，1哨兵版，2船方自管
					paramsCheckUpdate.add(new BasicNameValuePair("version", Flags.PDA_VERSION_CFZG + ""));
					NetWorkManager.request(CfzgUseOnly.this, strCheckUpdate, paramsCheckUpdate, Flags.PDA_VERSION_SYSTEMAUTO);
					return;

				case STARTACTIVITY_FOR_BIND:
					if (_BindShip != null) {
						intent.putExtra("hc", (String) _BindShip.get("hc"));
						intent.putExtra("cbzwm", (String) _BindShip.get("cbzwm"));
						intent.putExtra("jcfl", (String) _BindShip.get("jcfl"));
						intent.putExtra("cbywm", (String) _BindShip.get("cbywm"));
						intent.putExtra("gj", (String) _BindShip.get("gj"));
						intent.putExtra("cbxz", (String) _BindShip.get("cbxz"));
						intent.putExtra("tkwz", (String) _BindShip.get("tkwz"));
						intent.putExtra("cdgs", (String) _BindShip.get("cdgs"));
						// 注释掉，让详情页面每次都请求接口。（详情页面判断船员数为空则请求接口）
						// intent.putExtra("cys", (String)
						// _BindShip.get("cys"));
						intent.putExtra("dlcys", (String) _BindShip.get("dlcys"));
						intent.putExtra("dlrys", (String) _BindShip.get("dlrys"));
						intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
						intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
						intent.putExtra("cfzgFlag", true);
						intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
						intent.setClass(getApplicationContext(), CfzgShipDetailActivity.class);
						startActivity(intent);
						return;
					} else {
						if (SystemSetting.getBindShipAllSize(-11) > 0) {
							onShowQuestFromBindListDialog();
						} else {
							intent.putExtra("from", "0501");
							intent.putExtra("cfzgFlag", true);
							intent.setClass(getApplicationContext(), CfzgShipBind.class);
							startActivityForResult(intent, STARTACTIVITY_FOR_READICCARD);
						}
						return;
					}
				case STARTACTIVITY_FOR_LEAVE:
					if (_BindShip == null) {
						HgqwToast.makeText(CfzgUseOnly.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.leave));
					break;
				case STARTACTIVITY_FOR_PERSONNEL_BALANCE:
					/*
					 * if (_BindShip == null) {
					 * BaseToast.makeText(CfzgUseOnly.this,
					 * R.string.no_bindship, BaseToast.LENGTH_LONG).show();
					 * return; } intent.putExtra("title",
					 * getString(R.string.Personnel_balance));
					 * intent.putExtra("hc", (String) _BindShip.get("hc"));
					 * intent.setClass(getApplicationContext(),
					 * PersonBalanceActivity.class);
					 * startActivityForResult(intent,
					 * STARTACTIVITY_FOR_PERSON_BALANCE);
					 */

					return;
				case STARTACTIVITY_FOR_ARRIVE:
					if (_BindShip == null) {
						HgqwToast.makeText(CfzgUseOnly.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.arrive));
					break;
				case STARTACTIVITY_FOR_UNBIND:
					if (_BindShip == null) {
						HgqwToast.makeText(CfzgUseOnly.this, R.string.no_bindship, HgqwToast.LENGTH_LONG).show();
						return;
					}
					intent.putExtra("title", getString(R.string.unbindShip));
					break;
				}
				intent.putExtra("hc", (String) _BindShip.get("hc"));
				intent.putExtra("cbzwm", (String) _BindShip.get("cbzwm"));
				intent.putExtra("jcfl", (String) _BindShip.get("jcfl"));
				intent.putExtra("cbywm", (String) _BindShip.get("cbywm"));
				intent.putExtra("gj", (String) _BindShip.get("gj"));
				intent.putExtra("cbxz", (String) _BindShip.get("cbxz"));
				intent.putExtra("tkwz", (String) _BindShip.get("tkwz"));
				intent.putExtra("cdgs", (String) _BindShip.get("cdgs"));
				// intent.putExtra("cys", (String) _BindShip.get("cys"));
				intent.putExtra("dlcys", (String) _BindShip.get("dlcys"));
				intent.putExtra("dlrys", (String) _BindShip.get("dlrys"));
				intent.putExtra("bdzt", (String) _BindShip.get("bdzt"));
				intent.putExtra("kacbzt", (String) _BindShip.get("kacbzt"));
				intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
				intent.putExtra("cfzgFlag", true);
				intent.setClass(getApplicationContext(), CfzgShipDetailActivity.class);
				startActivity(intent);
			}

		});

		String strCheckUpdate = "checkUpdate";
		List<NameValuePair> paramsCheckUpdate = new ArrayList<NameValuePair>();
		paramsCheckUpdate.add(new BasicNameValuePair("type", "0"));
		// 版本：0默认版本，1哨兵版，2船方自管
		paramsCheckUpdate.add(new BasicNameValuePair("version", Flags.PDA_VERSION_CFZG + ""));
		NetWorkManager.request(CfzgUseOnly.this, strCheckUpdate, paramsCheckUpdate, FlagUrls.CHECK_UPDATE);
		BaseInfoData.onRequestBaseInfoData(null);// 加载基本信息
		onUpdateGridViewmenu();
	}

	public void onUpdateGridViewmenu() {
		ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gridViewMenuItemString.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", gridViewMenuItemImage[i]);
			map.put("itemText", this.getResources().getString(gridViewMenuItemString[i]));
			lst.add(map);
		}
		SimpleAdapter adpter = new SimpleAdapter(this, lst, R.layout.cfzg_mainmenu_item, new String[] { "itemImage", "itemText" }, new int[] {
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

	/** 退出该模块时，如果存在为解除绑定，提示是否解除绑定 */
	private void onShowUnBindQuestDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.unbind_quit);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 执行船舶解除绑定
				String url = "buildRelation";
				if (progressDialog != null) {
					return;
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
				params.add(new BasicNameValuePair("bindState", "0"));
				params.add(new BasicNameValuePair("voyageNumber", SystemSetting.getVoyageNumber(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "")));
				params.add(new BasicNameValuePair("bindType", "0"));
				//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
				params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
				

				progressDialog = new ProgressDialog(CfzgUseOnly.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(CfzgUseOnly.this, url, params, STARTACTIVITY_FOR_BIND);
			}
		});
		builder.setNegativeButton(R.string.no, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				CfzgUseOnly.this.finish();
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

	/**
	 * 刷电子标签完毕后，立即进入根据电子标签号查询船舶列表界面
	 * 
	 * @see CfzgShipListActivity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_READICCARD:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				switch (mType) {
				case STARTACTIVITY_FOR_BIND:
					intent.putExtra("title", getString(R.string.bindShip));
					break;
				}
				if (data.getStringExtra("cardNumber") != null) {
					intent.putExtra("cardNumber", data.getStringExtra("cardNumber"));
					intent.putExtra("cfzgFlag", true);
					intent.putExtra("from", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS);
					intent.setClass(getApplicationContext(), CfzgShipListActivity.class);
					startActivity(intent);
				}
			}
			break;
		case FlagManagers.CUSTOM_DIALOG_FOR_EXIT:
			if (resultCode == RESULT_OK) {
				String password = data.getStringExtra("password");
				this.validatePassword(password);
			}
			break;
		}
	}

	/** 处理解除绑定结果 */
	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult() str:" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		Intent intent = new Intent();
		UpdataVersionManager manager = new UpdataVersionManager();
		switch (httpRequestType) {
		case STARTACTIVITY_FOR_UNBIND:
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				HgqwToast.makeText(CfzgUseOnly.this, R.string.unbindship_success, HgqwToast.LENGTH_LONG).show();
				// 调用注销接口
				onLogOutRequest(true);

			} else {
				// 解绑不成功提示用户
				HgqwToast.makeText(CfzgUseOnly.this, R.string.cfzg_logout_error, HgqwToast.LENGTH_LONG).show();
			}
			break;
		case STARTACTIVITY_FOR_BIND:
			if (str != null && ("1".equals(str) || "2".equals(str))) {
				SystemSetting.setBindShip(null, CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
				finish();
			} else {
				HgqwToast.makeText(CfzgUseOnly.this, R.string.unbindship_failure, HgqwToast.LENGTH_LONG).show();
			}
			break;
		case FlagUrls.DO_LOGOUT:
			if (onParseXMLDataLogOut(str)) {
				SystemSetting.setBindShip(null, CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
				// 解除绑定成功，清缓存、跳到首页
				SystemSetting.destroyCfzg();
				goToCfzgIndex("logout");
				CfzgUseOnly.this.finish();
			} else {
				HgqwToast.getToastView(getApplicationContext(), "注销失败，请稍后再试！").show();
			}
			break;
		case Flags.PDA_VERSION_SYSTEMAUTO:
			if (str == null) {
				HgqwToast.makeText(CfzgUseOnly.this, getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG).show();
				return;
			}
			intent.setClass(CfzgUseOnly.this, CfzgUpdateActivity.class);
			intent.putExtra("version", Flags.PDA_VERSION_CFZG + "");
			UpdataInfo updataInfo = manager.updateVersion(str, this.getApplicationContext(), intent, Flags.PDA_VERSION_BYHAND);
			boolean update = updataInfo.isUpdate();
			if (update) {
				startActivityForResult(intent, STARTACTIVITY_FOR_SWUPDATE);
			}
			break;
		case FlagUrls.CHECK_UPDATE:
			if (str == null) {
				HgqwToast.makeText(CfzgUseOnly.this, getString(R.string.data_download_failure_info), HgqwToast.LENGTH_LONG).show();
				return;
			}
			intent.setClass(CfzgUseOnly.this, CfzgUpdateActivity.class);
			intent.putExtra("version", Flags.PDA_VERSION_CFZG + "");
			UpdataInfo updataInfo2 = manager.updateVersion(str, this.getApplicationContext(), intent, Flags.PDA_VERSION_SYSTEMAUTO);
			boolean update2 = updataInfo2.isUpdate();
			if (update2) {
				startActivityForResult(intent, STARTACTIVITY_FOR_SWUPDATE);
			}
			break;
		case FlagUrls.VALIDATE_PASSWORD:
			if (str != null) {
				if ("success".equals(str)) {
					// 密码验证成功，模拟调用Home键
					intent.setAction(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
				} else {
					HgqwToast.getToastView(getApplicationContext(), "密码错误！").show();
				}
			} else {
				HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
			}
			break;
		default:
			break;
		}

	}

	/**
	 * @方法名：cfzgButtonClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-4-27 下午3:18:37
	 * @param v
	 */
	public void cfzgButtonClick(View v) {
		switch (v.getId()) {
		case R.id.cfzg_btn_logout:// 注销
			onShowExitQuestDialog();
			break;
		case R.id.cfzg_btn_back:// 返回
			goToCfzgIndex("back");
			finish();
			break;
		default:
			break;
		}

	}

	/**
	 * @方法名：goToCfzgIndex
	 * @功能说明：跳转到首页
	 * @author liums
	 * @date 2013-4-28 下午3:53:44
	 */
	private void goToCfzgIndex(String flag) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), CfzgIndex.class);
		intent.putExtra("flag", flag);
		intent.putExtra("cnEnFlag", true);
		intent.putExtra("cn", cn);
		startActivity(intent);
	}

	/** 退出时，弹出提示确认dialog */
	protected void onShowExitQuestDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.cfzg_quest_quit);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 调用船舶解绑接口
				unBindShip();
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

	private void unBindShip() {
		HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");
		String url = "buildRelation";
		if (progressDialog != null) {
			return;
		}
		if (_BindShip != null) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
			params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
			params.add(new BasicNameValuePair("bindState", "0"));
			params.add(new BasicNameValuePair("voyageNumber", (String) _BindShip.get("hc")));
			params.add(new BasicNameValuePair("bindType", CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + ""));

			//执勤对象类型:船舶0 卡口(区域)1  码头2 泊位3
			params.add(new BasicNameValuePair("zqdxlx", GlobalFlags.ZQDXLX_CB + ""));
			
			progressDialog = new ProgressDialog(CfzgUseOnly.this);
			progressDialog.setTitle(getString(R.string.waiting));
			progressDialog.setMessage(getString(R.string.waiting));
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
			NetWorkManager.request(CfzgUseOnly.this, url, params, STARTACTIVITY_FOR_UNBIND);
		} else {
			// 调用注销接口
			onLogOutRequest(true);
		}
	}

	/**
	 * 
	 * @param exit
	 */
	private void onLogOutRequest(boolean exit) {
		String str = "doLoginOut";
		if (progressDialog != null) {
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", LoginUser.getCurrentLoginUser().getUserName()));
		params.add(new BasicNameValuePair("dutyid", LoginUser.getCurrentLoginUser().getzqjlid()));
		params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.logouting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, str, params, FlagUrls.DO_LOGOUT);
	}

	/** 解析注销请求返回的数据 */
	private boolean onParseXMLDataLogOut(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
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
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/* 监听物理按键 */
	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			goToCfzgIndex("back");
			finish();
			break;
		case KeyEvent.KEYCODE_HOME:
			// dialogActivityForExit();
			// break;
			return super.onKeyDown(keyCode, event);
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);

	}

	/**
	 * @方法名：dialogActivity
	 * @功能说明：弹出Home键验证对话框
	 * @author liums
	 * @date 2013-5-7 下午5:34:08
	 */
	private void dialogActivityForExit() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), CfzgCustomDialogForExit.class);
		intent.putExtra("cn", true);
		startActivityForResult(intent, FlagManagers.CUSTOM_DIALOG_FOR_EXIT);
	}

	/**
	 * @方法名：validatePassword
	 * @功能说明：验证当前用户密码
	 * @author liums
	 * @date 2013-5-7 下午5:42:16
	 * @param password
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private void validatePassword(String password) {
		String url = "validatePassword";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", LoginUser.getCurrentLoginUser().getUserName()));
		params.add(new BasicNameValuePair("password", password));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.setButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!((ProgressDialog) dialog).isShowing()) {
					Log.i(TAG, "!((ProgressDialog)dialog).isShowing()");
					progressDialog = null;
					return;
				}
				Log.i(TAG, "progressDialog onClick");
				dialog.dismiss();
				progressDialog = null;
			}
		});
		progressDialog.show();
		NetWorkManager.request(this, url, params, FlagUrls.VALIDATE_PASSWORD);
	}
}
