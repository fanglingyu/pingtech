package com.pingtech.hgqw.module.cfzg;

import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.utils.BaseInfoData;
import com.pingtech.hgqw.utils.DataDictionary;
import com.pingtech.hgqw.utils.SystemSetting;

/**
 * 
 * 所有界面的基类，定义该基类的目的是为了保证在屏幕的最下方显示当前登录用户 同时，在基类中也对内存是否有效的验证（防止长时间不操作时，内存被系统回收）
 */
public class CfzgSuperActivity extends Activity {
	/**
	 * 电池电量广播是否监听
	 */
	private boolean isReceiver = false;

	/**
	 * true竖屏，false横屏
	 */
	boolean port;

	/**
	 * 1来自语音列表，2来自船方自管首页
	 */
	private int comeFrom = 0;

	/**
	 * true，显示底部用户信息，false不显示
	 */
	private boolean showBottomUser = true;

	/**
	 * 
	 * 每个界面再创建时，先都按照base.xml创建一个layout， 然后再把每个activity定义的layout加到该layout的baselayout下
	 * 
	 * @param layoutResID
	 *            每个activity自定义的layout
	 */
	public void onCreate(Bundle savedInstanceState, int layoutResID) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if (width > height) {
			port = false;
			setTheme(R.style.titlebar);
		} else {
			port = true;
			setTheme(R.style.titlebar_port);
		}

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// 船方自管全屏显示控制
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.setContentView(R.layout.base);

		if ((layoutResID == R.layout.main || layoutResID == R.layout.police_list || layoutResID == R.layout.baseinfo_list) && (width < height)) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.maintitlebar);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}
		/** 内存有效性检验 begin */
		if (LoginUser.getCurrentLoginUser() == null) {
			SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
			LoginUser user = new LoginUser();
			LoginUser.SetCurrentLoginUser(user);
			LoginUser.getCurrentLoginUser().SetUserName(prefs.getString("currentusername", ""));
			LoginUser.getCurrentLoginUser().SetUserID(prefs.getString("userid", ""));
			LoginUser.getCurrentLoginUser().SetUserSsdw(prefs.getString("userssdw", ""));
			LoginUser.getCurrentLoginUser().SetName(prefs.getString("name", ""));
			LoginUser.getCurrentLoginUser().Setzqjlid(prefs.getString("zqjlid", ""));
			LoginUser.getCurrentLoginUser().SetPermission(prefs.getInt("permission", 0x60));
			LoginUser.getCurrentLoginUser().SetPermission(prefs.getInt("rylb", 0));
		}
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		if (SystemSetting.getServerHost() == null) {
			SystemSetting.setServerHost(prefs.getString(getString(R.string.server_host), "127.0.0.1"));
			SystemSetting.setServerPort(prefs.getString(getString(R.string.server_port), "8080"));
		}
		if (SystemSetting.getWebServiceNamespace() == null) {
			SystemSetting.setWebServiceNamespace(prefs.getString(getString(R.string.webservice_namespace),
					"http://service.webservice.pda.hgqw.pingtech.com.cn/"));
		}
		if (SystemSetting.getWebServiceUserName() == null) {
			SystemSetting.setWebServiceConnect(prefs.getBoolean(getString(R.string.webservice_connect), true));
			SystemSetting.setWebServiceUserName(prefs.getString(getString(R.string.webservice_username), "zjgbjz"));
		}
		if (SystemSetting.getWebServicePassword() == null) {
			SystemSetting.setWebServicePassword(prefs.getString(getString(R.string.webservice_password), "111111"));
		}
		if (SystemSetting.getWebServiceCode() == null) {
			SystemSetting.setWebServiceCode(prefs.getString(getString(R.string.webservice_code), "bianfangpda"));
		}
		if (SystemSetting.getWebServiceWSDLUrl() == null) {
			SystemSetting.setWebServiceWSDLUrl(prefs.getString(getString(R.string.webservice_url), "http://" + SystemSetting.getServerHost() + ":"
					+ SystemSetting.getServerPort() + "/pda3g/services/pda3GService"));
		}
		if (SystemSetting.getWebServiceArg1() == null) {
			SystemSetting.setWebServiceArg1(prefs.getString(getString(R.string.webservice_arg1), "userName"));
		}
		if (SystemSetting.getWebServiceArg2() == null) {
			SystemSetting.setWebServiceArg2(prefs.getString(getString(R.string.webservice_arg2), "password"));
		}
		if (SystemSetting.getWebServiceArg3() == null) {
			SystemSetting.setWebServiceArg3(prefs.getString(getString(R.string.webservice_arg3), "code"));
		}
		if (SystemSetting.getWebServiceArg4() == null) {
			SystemSetting.setWebServiceArg4(prefs.getString(getString(R.string.webservice_arg4), "context"));
		}
		if (SystemSetting.getPDACode() == null) {
			TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			SystemSetting.setPDACode(telephonyManager.getDeviceId());
		}
		DataDictionary.init();
		BaseInfoData.init();
		/** 内存有效性检验 end */
		if (findViewById(R.id.current_user) != null) {
			((TextView) findViewById(R.id.current_user))
					.setText(getString(R.string.currentuser) + LoginUser.getCurrentLoginUser().getName() + "    ");
		}
		LinearLayout baseview = (LinearLayout) findViewById(R.id.baselayout);
		View overlay = (View) getLayoutInflater().inflate(layoutResID, null);
		if (baseview != null) {
			baseview.addView(overlay);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(R.layout.base);
		if (findViewById(R.id.current_user) != null) {
			((TextView) findViewById(R.id.current_user))
					.setText(getString(R.string.currentuser) + LoginUser.getCurrentLoginUser().getName() + "    ");
		}
		LinearLayout baseview = (LinearLayout) findViewById(R.id.baselayout);
		View overlay = (View) getLayoutInflater().inflate(layoutResID, null);
		if (baseview != null) {
			baseview.addView(overlay);
		}
		// 如果来自语音对讲页面，将底部用户显示视图底色更换
		if (comeFrom == 1) {
			findViewById(R.id.base_layout_bottom_user).setBackgroundResource(R.drawable.bottom_user_bg);
			findViewById(R.id.base_layout_bottom_user).setVisibility(View.GONE);
		} else if (comeFrom == 2) {
			HashMap<String, Object> _BindShip = SystemSetting.getBindShip(CfzgShipListActivity.LIST_TYPE_FROM_SHIPSTATUS + "");

			// 船方自管首页显示船舶名称
			if (_BindShip != null) {
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + (String) _BindShip.get("cbzwm"));
			} else {
				((TextView) findViewById(R.id.current_user)).setText("  " + getString(R.string.currentuser) + "未绑定船舶");

			}
			((TextView) findViewById(R.id.current_user)).setTextSize(20);
			findViewById(R.id.base_layout_bottom_user).setBackgroundDrawable(null);
			findViewById(R.id.layout).setBackgroundResource(R.drawable.mainmenu_bg_v);
		}

		if (!showBottomUser) {
			((TextView) findViewById(R.id.current_user)).setVisibility(View.GONE);
			findViewById(R.id.base_layout_bottom_user).setBackgroundDrawable(null);
			findViewById(R.id.layout).setBackgroundResource(R.drawable.mainmenu_bg_v);
		}
	}

	/**
	 * @方法名：setContentView
	 * @功能说明：需要自定义底部用户显示样式的页面调用此方法
	 * @author liums
	 * @date 2013-4-24 下午8:32:49
	 * @param layoutResID
	 * @param comeFrom
	 *            1来自语音列表，2来自船方自管首页
	 */
	public void setContentView(int layoutResID, int comeFrom) {
		this.comeFrom = comeFrom;
		setContentView(layoutResID);
	}

	/**
	 * 
	 * @方法名：setContentView
	 * @功能说明：需要自定义底部用户显示样式的页面调用此方法
	 * @author liums
	 * @date 2013-4-28 下午12:18:26
	 * @param layoutResID
	 * @param showBottomUser
	 *            ： true，显示底部用户信息，false不显示
	 */
	public void setContentView(int layoutResID, boolean showBottomUser) {
		this.showBottomUser = showBottomUser;
		setContentView(layoutResID);
	}

	/**
	 * 处理每个界面标题上个级别之间的>分隔符
	 * */
	public void setMyActiveTitle(String title) {
		if (port) {
			String subtitle[] = title.split(">");
			if (subtitle.length > 0 && subtitle[0] != null) {
				((TextView) findViewById(R.id.current_dialog_title)).setText(subtitle[0]);
			}
			if (subtitle.length > 1 && subtitle[1] != null) {
				findViewById(R.id.title_sep1).setVisibility(View.VISIBLE);
				findViewById(R.id.current_dialog_title2).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.current_dialog_title2)).setText(subtitle[1]);
			} else {
				if (findViewById(R.id.title_sep1) != null) {
					findViewById(R.id.title_sep1).setVisibility(View.GONE);
					findViewById(R.id.current_dialog_title2).setVisibility(View.GONE);
				}
			}
			if (subtitle.length > 2 && subtitle[2] != null) {
				findViewById(R.id.title_sep2).setVisibility(View.VISIBLE);
				findViewById(R.id.current_dialog_title3).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.current_dialog_title3)).setText(subtitle[2]);
			} else {
				if (findViewById(R.id.title_sep2) != null) {
					findViewById(R.id.title_sep2).setVisibility(View.GONE);
					findViewById(R.id.current_dialog_title3).setVisibility(View.GONE);
				}
			}
		} else {
			((TextView) findViewById(R.id.current_dialog_title)).setText(title);
		}
		// 如果是船员列表页面，将船员数量显示到标题栏
	}

	public void setMyActiveTitle(int resId) {
		((TextView) findViewById(R.id.current_dialog_title)).setText(resId);
		if (findViewById(R.id.title_sep1) != null) {
			findViewById(R.id.title_sep1).setVisibility(View.GONE);
			findViewById(R.id.current_dialog_title2).setVisibility(View.GONE);
			findViewById(R.id.title_sep2).setVisibility(View.GONE);
			findViewById(R.id.current_dialog_title3).setVisibility(View.GONE);
		}
	}

	/* 船方自管自定义任务栏 */

	/**
	 * 电池电量监听
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", 0);
			int status = intent.getIntExtra("status", 0);//
			// 更新标题栏电量百分百
			((TextView) findViewById(R.id.cfzg_titlebar_textview_batteries)).setText(level + "%");
			ImageView batteriesImg = ((ImageView) findViewById(R.id.cfzg_titlebar_textview_batteries_img));
			if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
				batteriesImg.setImageResource(R.drawable.stat_sys_battery_charge);
			} else {
				batteriesImg.setImageResource(R.drawable.stat_sys_battery);
			}
			batteriesImg.setImageLevel(level);
		}
	};

	public void onCreateForCfzg(Bundle savedInstanceState, int layoutResID) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(R.layout.base);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.cfzg_maintitlebar);
		/** 内存有效性检验 begin */
		if (LoginUser.getCurrentLoginUser() == null) {
			SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
			LoginUser user = new LoginUser();
			LoginUser.SetCurrentLoginUser(user);
			LoginUser.getCurrentLoginUser().SetUserName(prefs.getString("currentusername", ""));
			LoginUser.getCurrentLoginUser().SetUserID(prefs.getString("userid", ""));
			LoginUser.getCurrentLoginUser().SetUserSsdw(prefs.getString("userssdw", ""));
			LoginUser.getCurrentLoginUser().SetName(prefs.getString("name", ""));
			LoginUser.getCurrentLoginUser().Setzqjlid(prefs.getString("zqjlid", ""));
			LoginUser.getCurrentLoginUser().SetPermission(prefs.getInt("permission", 0x60));
			LoginUser.getCurrentLoginUser().SetPermission(prefs.getInt("rylb", 0));
		}
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		if (SystemSetting.getServerHost() == null) {
			SystemSetting.setServerHost(prefs.getString(getString(R.string.server_host), "127.0.0.1"));
			SystemSetting.setServerPort(prefs.getString(getString(R.string.server_port), "8080"));
		}
		if (SystemSetting.getWebServiceNamespace() == null) {
			SystemSetting.setWebServiceNamespace(prefs.getString(getString(R.string.webservice_namespace),
					"http://service.webservice.pda.hgqw.pingtech.com.cn/"));
		}
		if (SystemSetting.getWebServiceUserName() == null) {
			SystemSetting.setWebServiceConnect(prefs.getBoolean(getString(R.string.webservice_connect), true));
			SystemSetting.setWebServiceUserName(prefs.getString(getString(R.string.webservice_username), "zjgbjz"));
		}
		if (SystemSetting.getWebServicePassword() == null) {
			SystemSetting.setWebServicePassword(prefs.getString(getString(R.string.webservice_password), "111111"));
		}
		if (SystemSetting.getWebServiceCode() == null) {
			SystemSetting.setWebServiceCode(prefs.getString(getString(R.string.webservice_code), "bianfangpda"));
		}
		if (SystemSetting.getWebServiceWSDLUrl() == null) {
			SystemSetting.setWebServiceWSDLUrl(prefs.getString(getString(R.string.webservice_url), "http://" + SystemSetting.getServerHost() + ":"
					+ SystemSetting.getServerPort() + "/pda3g/services/pda3GService"));
		}
		if (SystemSetting.getWebServiceArg1() == null) {
			SystemSetting.setWebServiceArg1(prefs.getString(getString(R.string.webservice_arg1), "userName"));
		}
		if (SystemSetting.getWebServiceArg2() == null) {
			SystemSetting.setWebServiceArg2(prefs.getString(getString(R.string.webservice_arg2), "password"));
		}
		if (SystemSetting.getWebServiceArg3() == null) {
			SystemSetting.setWebServiceArg3(prefs.getString(getString(R.string.webservice_arg3), "code"));
		}
		if (SystemSetting.getWebServiceArg4() == null) {
			SystemSetting.setWebServiceArg4(prefs.getString(getString(R.string.webservice_arg4), "context"));
		}
		if (SystemSetting.getPDACode() == null) {
			TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			SystemSetting.setPDACode(telephonyManager.getDeviceId());
		}
		DataDictionary.init();
		BaseInfoData.init();
		/** 内存有效性检验 end */
		if (findViewById(R.id.current_user) != null) {
			((TextView) findViewById(R.id.current_user))
					.setText(getString(R.string.currentuser) + LoginUser.getCurrentLoginUser().getName() + "    ");
		}
		LinearLayout baseview = (LinearLayout) findViewById(R.id.baselayout);
		View overlay = (View) getLayoutInflater().inflate(layoutResID, null);
		if (baseview != null) {
			baseview.addView(overlay);
		}

		Time time = new Time();
		time.setToNow();
		String timeStr = time.format("%H:%M");
		((TextView) findViewById(R.id.cfzg_titlebar_textview_time)).setText(timeStr);
		// 启动线程更新标题
		new Thread(titleBarRunnable).start();
		isReceiver = true;
		// 电池电量监听
		this.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	private Runnable titleBarRunnable = new Runnable() {

		@Override
		public void run() {
			while (true) {
				SystemClock.sleep(30 * 1000);
				titleBarHandler.sendEmptyMessage(1);
			}
		}

	};

	private Handler titleBarHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Time time = new Time();
				time.setToNow();
				String timeStr = time.format("%H:%M");
				((TextView) findViewById(R.id.cfzg_titlebar_textview_time)).setText(timeStr);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	/* 监听物理按键 */

	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	protected void onDestroy() {
		if (isReceiver) {
			unregisterReceiver(broadcastReceiver);
			isReceiver = false;
		}
		super.onDestroy();
	}
	
	boolean flag = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10) {
			flag = true;
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
//		if (!flag) {
//			startOther();
//		}
		super.onResume();
	}

	private void startOther() {
		Intent intent = new Intent();
		 intent.setClass(this, FullScreenActivity.class);
		// ComponentName component = new
		// ComponentName("com.example.testfullscreen",
		// "com.example.testfullscreen.MainActivity");
		// intent.setComponent(component);
		startActivityForResult(intent, 10);

	}
}
