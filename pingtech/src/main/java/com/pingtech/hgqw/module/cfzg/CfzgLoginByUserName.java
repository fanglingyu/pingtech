package com.pingtech.hgqw.module.cfzg;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.web.vpn.VpnManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 登陆界面的activity类
 * */
public class CfzgLoginByUserName extends Activity implements OnHttpResult {
	private static final String TAG = "loginMain";

	private static final String TAG2 = "生命周期";

	/**
	 * 人员类别标识，用于区分版本：0系统默认，1哨兵，2船方自管，3巡检人员
	 */
	private int rylbFlag = -1;

	/** 高级管理员账号和密码 */
	public static final String ADMINISTRATOR = "admin";

	/** 启动刷卡登陆界面 */
	private static final int STARTACTIVITY_FOR_PAYCARD = 1;

	private Button btnLogin;

	private Button btnLogin_back;

	private Button btnLoginByCard;

	private EditText editBoxAccount;

	private EditText editBoxPsw;

	private String cardNum;

	private ProgressDialog progressDialog = null;

	private String httpReturnXMLInfo = null;

	private VpnManager vpnManager = null;

	private boolean cn = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		Log.save2file = prefs.getBoolean("savelogfile", false);
		Log.i(TAG, "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cfzg_login);
		// startService(new Intent("com.pingtech.PINGTECH_SERVICE"));
		Intent intent = getIntent();
		cn = intent.getBooleanExtra("cn", true);
		settingInit();
		editBoxAccount = (EditText) findViewById(R.id.editBoxAccount);

		editBoxPsw = (EditText) findViewById(R.id.editBoxPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin_back = (Button) findViewById(R.id.btnLogin_back);
		btnLogin.setOnClickListener(new btnLoginClickListener());
		btnLogin_back.setOnClickListener(new btnLoginClickListener());
		btnLoginByCard = (Button) findViewById(R.id.btnLoginByCard);
		btnLoginByCard.setOnClickListener(new btnLoginByCardClickListener());
		editBoxAccount.setText(prefs.getString(getString(R.string.username), ""));
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);// 屏保
		lock.disableKeyguard();
		
		setVersionInfo();
	}

	/**
	 * 
	 * @description 设置版本信息
	 * @date 2014-5-21
	 * @author zhaotf
	 */
	private void setVersionInfo() {
		String versionInfo = "";
		versionInfo = getVersionInfo();
		TextView login_textview_version_info = (TextView) findViewById(R.id.login_textview_version_info);
		if(login_textview_version_info!=null){
			login_textview_version_info.setText(StringUtils.isEmpty(versionInfo)? "" : versionInfo);
		}
	}

	/**
	 * 
	 * @方法名：getVersionInfo
	 * @功能说明：获取版本信息
	 * @author liums
	 * @date 2014-1-14 下午8:45:47
	 * @return
	 */
	private String getVersionInfo() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		if (packageInfo != null) {
			return packageInfo.versionName;
		}
		return "";
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		initSvn();
		super.onResume();
	}

	private void initSvn() {
		vpnManager = new VpnManager(handler, this);
		Log.i(TAG, "onResume,status=" + vpnManager.getStatus());
		vpnManager.init();// vpn初始化
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "onCreate()");
		super.onConfigurationChanged(newConfig);
	}

	/** 接收vpn连接结果 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
		}
	};

	/** 初始化操作，包括启动VPN、初始化webservice各种参数以及系统用到的其他参数，如数据字典等等 */
	private void settingInit() {
		// TODO Auto-generated method stub
		if (SystemSetting.isInit == false) {
			Log.i(TAG, "initsetting start");

			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int width = dm.widthPixels;
			int height = dm.heightPixels;
			SystemSetting.init(height > width);
			SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
			SystemSetting.setServerHost(prefs.getString(getString(R.string.server_host), "127.0.0.1"));
			SystemSetting.setServerPort(prefs.getString(getString(R.string.server_port), "8080"));
			SystemSetting.setServerKadm(prefs.getString(getString(R.string.server_kadm), "209"));
			SystemSetting.setWebServiceConnect(prefs.getBoolean(getString(R.string.webservice_connect), true));
			SystemSetting.setWebServiceNamespace(prefs.getString(getString(R.string.webservice_namespace),
					"http://service.webservice.pda.hgqw.pingtech.com.cn/"));
			SystemSetting.setWebServiceUserName(prefs.getString(getString(R.string.webservice_username), "zjgbjz"));
			SystemSetting.setWebServicePassword(prefs.getString(getString(R.string.webservice_password), "111111"));
			SystemSetting.setWebServiceCode(prefs.getString(getString(R.string.webservice_code), "bianfangpda"));
			SystemSetting.setWebServiceWSDLUrl(prefs.getString(getString(R.string.webservice_url), "http://" + SystemSetting.getServerHost() + ":"
					+ SystemSetting.getServerPort() + "/pda3g/services/pda3GService"));
			SystemSetting.setWebServiceArg1(prefs.getString(getString(R.string.webservice_arg1), "userName"));
			SystemSetting.setWebServiceArg2(prefs.getString(getString(R.string.webservice_arg2), "password"));
			SystemSetting.setWebServiceArg3(prefs.getString(getString(R.string.webservice_arg3), "code"));
			SystemSetting.setWebServiceArg4(prefs.getString(getString(R.string.webservice_arg4), "context"));
			SystemSetting.setWebServiceArg5(prefs.getString(getString(R.string.webservice_arg5), "kadm"));
			// int which = prefs.getInt(getString(R.string.gps_timer), 30);
			int which = prefs.getInt(getString(R.string.gps_timer), 5 * 60);// 默认时间5分钟
			SystemSetting.setGPSTimer(which);
			TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			SystemSetting.setPDACode(telephonyManager.getDeviceId());
		}
	}

	/** 退出时，弹出提示确认dialog */
	protected void onShowExitQuestDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.quest_quit);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SystemSetting.destroy();
				CfzgLoginByUserName.this.finish();
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

	private class btnLoginClickListener implements OnClickListener {
		/**
		 * 点击登陆按钮的处理。如果是开或关log的指令，就把log打开或关闭 检测VPN启动流程是否完成，如果未完成，提示启动VPN中，不能登录
		 * VPN启动流程完成后，如果不是成功的，提示VPN未正常启动是否确认登陆
		 * */
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnLogin_back:
				goToActivity(CfzgIndex.class);
				break;
			case R.id.btnLogin:

				String name = editBoxAccount.getText().toString();
				if (name.length() == 0) {
					HgqwToast.makeText(CfzgLoginByUserName.this, R.string.usernameempty, HgqwToast.LENGTH_LONG).show();
					return;
				}
				if (name.equals("openpdalog2file")) {
					SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean("savelogfile", true);
					editor.commit();
					Log.save2file = true;
					HgqwToast.makeText(CfzgLoginByUserName.this, "log保存到文件已打开", HgqwToast.LENGTH_LONG).show();
					return;
				}
				if (name.equals("closepdalog2file")) {
					SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean("savelogfile", false);
					editor.commit();
					Log.save2file = false;
					HgqwToast.makeText(CfzgLoginByUserName.this, "log保存到文件已打开", HgqwToast.LENGTH_LONG).show();
					return;
				}
				String psw = editBoxPsw.getText().toString();
				if (psw.length() == 0) {
					HgqwToast.makeText(CfzgLoginByUserName.this, R.string.passwordempty, HgqwToast.LENGTH_LONG).show();
					return;
				}

				int vpnstates = vpnManager.getStatus();
				// 如果客户端不存在，提示是否直接登录
				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_NOT_FOUNT) {
					onShowLoginWithNoVPNQuestDialog();
					return;

				}
				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_STARTING) {
					HgqwToast.makeText(CfzgLoginByUserName.this, "VPN启动中，请稍后！", HgqwToast.LENGTH_LONG).show();
					return;
				}
				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_SUCCESS) {
					onLogin(false);
					return;
				}
				if (vpnstates != VpnManager.START_VPNSERVICE_RESULT_SUCCESS) {
					onShowLoginWithNoVPNQuestDialog();
					return;
				}

				break;

			default:
				break;
			}

		}
	}

	private class btnLoginByCardClickListener implements OnClickListener {
		/** 启动刷卡登陆 */
		public void onClick(View v) {
		}
	}

	/** 显示VPN未成功启动下是否登陆（用户名密码登陆时） */
	private void onShowLoginWithNoVPNQuestDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.login_no_vpn);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onLogin(false);
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

	/** 显示VPN未成功启动下是否登陆（刷卡登录时） */
	private void onShowLoginWithNoVPNQuestDialogForLoginByCard() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.login_no_vpn);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onLogin(true);
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

	/**
	 * 登陆操作
	 * 
	 * @param loginbycard
	 *            是否刷卡登陆
	 * */
	@SuppressWarnings("deprecation")
	private void onLogin(boolean loginbycard) {
		if (progressDialog != null) {
			return;
		}
		String str;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (loginbycard) {
			str = "doLoginByCard";
			params.add(new BasicNameValuePair("cardNumber", cardNum));
			params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		} else {
			String name = editBoxAccount.getText().toString();
			String psw = editBoxPsw.getText().toString();
			str = "doLoginByUserName";
			params.add(new BasicNameValuePair("userName", name));
			params.add(new BasicNameValuePair("password", psw));
			params.add(new BasicNameValuePair("version", "2"));
			params.add(new BasicNameValuePair("versionName", BaseApplication.instent.getVersionName()));
			params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.logining));
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
		NetWorkManager.request(this, str, params, 0);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		handler = null;
		super.onDestroy();
	}

	/**
	 * 解析登陆结果数据
	 * */

	private boolean onParseXMLData(String str) {
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
							LoginUser user = new LoginUser();
							LoginUser.SetCurrentLoginUser(user);
						}
					} else if ("yhm".equals(parser.getName())) {
						if (success) {
							// 用户名
							LoginUser.getCurrentLoginUser().SetUserName(parser.nextText());
						}
					} else if ("id".equals(parser.getName())) {
						if (success) {
							// id
							LoginUser.getCurrentLoginUser().SetUserID(parser.nextText());
						}
					} else if ("xm".equals(parser.getName())) {
						if (success) {
							// 姓名
							LoginUser.getCurrentLoginUser().SetName(parser.nextText());
						}
					} else if ("zqjlid".equals(parser.getName())) {
						if (success) {
							// 执勤记录ID
							LoginUser.getCurrentLoginUser().Setzqjlid(parser.nextText());
						}
					} else if ("ssdw".equals(parser.getName())) {
						if (success) {
							// 所属单位
							LoginUser.getCurrentLoginUser().SetUserSsdw(parser.nextText());
						}
					} else if ("ssdwid".equals(parser.getName())) {
						if (success) {
							// 所属单位ID
						}
					} else if ("sska".equals(parser.getName())) {
						if (success) {
							// 所属口岸
						}
					} else if ("kadm".equals(parser.getName())) {
						if (success) {
							// 口岸代码
						}
					} else if ("rylb".equals(parser.getName())) {
						if (success) {
							// 人员类别
							String rylbStr = parser.nextText();
							if ("0".equals(rylbStr)) {
								rylbFlag = 0;
							} else if ("1".equals(rylbStr)) {
								rylbFlag = 1;
							} else if ("2".equals(rylbStr)) {
								rylbFlag = 2;
							} else if ("3".equals(rylbStr)) {
								rylbFlag = 3;
							}
							LoginUser.getCurrentLoginUser().setRylb(rylbFlag);
						}
					} else if ("info".equals(parser.getName())) {
						if (!success) {
							// 错误信息
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
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STARTACTIVITY_FOR_PAYCARD:
			if (resultCode == RESULT_OK) {
				cardNum = data.getStringExtra("cardNumber");
				if (cardNum == null || cardNum.length() == 0) {
					HgqwToast.makeText(CfzgLoginByUserName.this, R.string.cardnumempty, HgqwToast.LENGTH_LONG).show();
					return;
				}

				int vpnstates = vpnManager.getStatus();
				// 如果客户端不存在，提示是否直接登录
				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_NOT_FOUNT) {
					onShowLoginWithNoVPNQuestDialogForLoginByCard();
					return;

				}
				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_STARTING) {
					HgqwToast.makeText(this, "VPN启动中，请稍后！", HgqwToast.LENGTH_LONG).show();
					return;
				}
				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_SUCCESS) {
					onLogin(true);
					return;
				}
				if (vpnstates != VpnManager.START_VPNSERVICE_RESULT_SUCCESS) {
					onShowLoginWithNoVPNQuestDialogForLoginByCard();
					return;
				}

			}
			break;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "loginmain onHttpResult(): " + (str != null));
		if (progressDialog == null || !progressDialog.isShowing()) {
			Log.i(TAG, "!progressDialog.isShowing()");
			return;
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str != null) {
			if (onParseXMLData(str)) {
				LoginUser.getCurrentLoginUser().SetPermission(0xFF);
				SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(getString(R.string.username), LoginUser.getCurrentLoginUser().getUserName());
				editor.putString("currentusername", LoginUser.getCurrentLoginUser().getUserName());
				editor.putString("userid", LoginUser.getCurrentLoginUser().getUserID());
				editor.putString("userssdw", LoginUser.getCurrentLoginUser().getUserSsdw());
				editor.putString("name", LoginUser.getCurrentLoginUser().getName());
				editor.putString("zqjlid", LoginUser.getCurrentLoginUser().getzqjlid());
				editor.putInt("permission", LoginUser.getCurrentLoginUser().getPermission());
				// 人员类别
				editor.putInt("rylb", LoginUser.getCurrentLoginUser().getRylb());
				editor.commit();
				Intent intent = new Intent();
				rylbFlag = LoginUser.getCurrentLoginUser().getRylb();
				if (rylbFlag == 3) {// 用户类别 1：干部，2：士兵 3：船方自管
					intent.setClass(getApplicationContext(), CfzgUseOnly.class);
					intent.putExtra("cn", cn);
					startActivity(intent);
					CfzgLoginByUserName.this.finish();
				} else {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.cfzg_login_toast), HgqwToast.LENGTH_LONG).show();
				}

			} else {
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(CfzgLoginByUserName.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(CfzgLoginByUserName.this, R.string.login_failure, HgqwToast.LENGTH_LONG).show();
				}
			}
		} else {
			HgqwToast.makeText(CfzgLoginByUserName.this, R.string.login_failure, HgqwToast.LENGTH_LONG).show();
		}
	}

	public boolean onMenuOpened(int featureId, Menu menu) {
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_menu_settings:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), CfzgNetworkSettingActivity.class);
			startActivity(intent);
			break;
		case R.id.login_menu_quit:
			onShowExitQuestDialog();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.cfzg_login_menu, menu);
		return true;
	}

	/* 船方自管监听物理按键 */
	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			this.goToActivity(CfzgIndex.class);
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_HOME:
			return false;
		default:
			break;
		}
		return false;
	}

	/**
	 * 跳转到指定Activity
	 * 
	 * @param class1
	 */
	private void goToActivity(Class clasz) {
		Intent intent = new Intent(this, clasz);
		intent.putExtra("cn", cn);
		intent.putExtra("cnEnFlag", true);
		startActivity(intent);
		finish();
	}

	/**
	 * 虚拟键盘点击事件
	 * 
	 * @param v
	 */
	public void onKeyBoardClick(View v) {
	}

	@Override
	protected void onStart() {
		Log.i(TAG2, this.getClass().getName() + " onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG2, this.getClass().getName() + " onRestart");
		super.onRestart();
	}

	@Override
	protected void onPause() {
		Log.i(TAG2, this.getClass().getName() + " onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG2, this.getClass().getName() + " onStop");
		super.onStop();
	}
}