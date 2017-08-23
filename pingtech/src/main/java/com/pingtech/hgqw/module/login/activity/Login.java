package com.pingtech.hgqw.module.login.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.NetworkSettingActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.home.activity.Index;
import com.pingtech.hgqw.module.login.utils.ActivityJump;
import com.pingtech.hgqw.module.login.utils.LitenceUtil;
import com.pingtech.hgqw.module.login.utils.LoginUtil;
import com.pingtech.hgqw.service.AndSerOffLineData;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.web.vpn.VpnManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 登陆界面的activity类
 * 
 */
public class Login extends Activity implements OnHttpResult {
	private static final String TAG = "Login";

	/** 高级管理员账号和密码 */
	public static final String ADMINISTRATOR = "admin";

	/** 启动刷卡登陆界面 */
	private static final int STARTACTIVITY_FOR_PAYCARD = 1;

	private Button btnLogin;

	private Button btnLoginByCard;

	private EditText editBoxAccount;

	private EditText editBoxPsw;

	private String cardNum;

	private String httpReturnXMLInfo = null;

	private TextView login_vpn_status;

	private VpnManager vpnManager = null;

	public ProgressDialog progressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		Log.save2file = prefs.getBoolean("savelogfile", false);
		Log.i(TAG, "onCreate()");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.login);
		login_vpn_status = (TextView) findViewById(R.id.login_vpn_status);
		BaseApplication.instent.settingInit();// 初始化
		editBoxAccount = (EditText) findViewById(R.id.editBoxAccount);
		editBoxPsw = (EditText) findViewById(R.id.editBoxPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new btnLoginClickListener());
		btnLoginByCard = (Button) findViewById(R.id.btnLoginByCard);
		btnLoginByCard.setOnClickListener(new btnLoginByCardClickListener());
		editBoxAccount.setText(prefs.getString(getString(R.string.username), ""));

		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);// 屏保
		lock.disableKeyguard();

		initVpn();
		// initAndSer();// 启动后台服务
		setVersionInfo();
		otherBusiness();		
	}

	private void otherBusiness() {
		// checkGpsState();
		// 版本232需要删除数据库，修改审计表
		reBuildDatabase();
	}

	private void checkGpsState() {
		// 判断GPS状态，未开启提示升级
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showGpsDialog();
		}
	}

	private void showGpsDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage(getString(R.string.gps_not_open));

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ActivityJump.toSysSetting(Login.this);
			}
		});

		// builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		// {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// dialog.cancel();
		// }
		// });
		builder.setCancelable(true);
		builder.create();
		builder.show();
	}

	/**
	 * 
	 * @方法名：setVersionInfo
	 * @功能说明：设置版本信息
	 * @author liums
	 * @date 2014-1-14 下午8:45:33
	 */
	private void setVersionInfo() {
		String versionInfo = "";
		versionInfo = getVersionInfo();
		TextView login_textview_version_info = (TextView) findViewById(R.id.login_textview_version_info);
		login_textview_version_info.setText(versionInfo);
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

	private int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		if (packageInfo != null) {
			return packageInfo.versionCode;
		}
		return 0;
	}

	/**
	 * 
	 * @方法名：initAndSer
	 * @功能说明：启动后台服务
	 * @author liums
	 * @date 2013-9-27 下午2:28:58
	 */
	private void initAndSer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				startService(new Intent("com.pingtech.PINGTECH_SERVICE"));// 启动后台GPS定位服务

				if (Flags.IF_OPEN_OFFLINE_MODULE) {
					Intent intent = new Intent(Login.this, AndSerOffLineData.class);
					stopService(intent);
					SystemClock.sleep(200);
					startService(intent);
				}
			}
		}).start();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		// BaseApplication.instent.settingInit();// 初始化操作
		checkGpsState();
		super.onResume();
	}

	private void initVpn() {
		login_vpn_status.setText("Vpn：");
		vpnManager = new VpnManager(handler, this);
		Log.i(TAG, "onResume,status=" + vpnManager.getStatus());
		vpnManager.init();// vpn初始化
		/*
		 * int status = vpnManager.getStatus(); Log.i(TAG, "onResume,status=" +
		 * status); if (status != 0) { } else { login_vpn_status.append("\n" +
		 * getString(R.string.login_vpn_status) + "安全通道已连接"); }
		 */
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "onCreate()");
		super.onConfigurationChanged(newConfig);
	}

	/** 接收vpn连接结果 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Log.i(TAG, "VpnServer start ret:" + msg.what);
			int vpnstates = msg.what;
			// SystemSetting.setVPNStatus(vpnstates);
			switch (vpnstates) {
			case VpnManager.START_VPNSERVICE_RESULT_SUCCESS:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "安全通道已连接");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_TIMEOUT:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "安全通道连接超时");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_FAILED:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "安全客户端启动失败");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_FAILED:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "安全通道连接失败");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_TIMEOUT:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "安全通道连接超时");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_NOT_FOUNT:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + getString(R.string.start_vpnservice_result_not_fount));
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_SUCCESS:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "安全客户端启动成功");
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "正在建立安全通道");
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_SUCCESS:
				login_vpn_status.append("\n" + getString(R.string.login_vpn_status) + "安全通道已连接");
				break;

			}
		}
	};

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
				VpnManager.close();
				Login.this.finish();
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
			Log.i(TAG, "onClick");
			String name = editBoxAccount.getText().toString();
			if (name.length() == 0) {
				HgqwToast.makeText(Login.this, R.string.usernameempty, HgqwToast.LENGTH_LONG).show();
				return;
			}
			if (name.equals("openpdalog2file")) {
				SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("savelogfile", true);
				editor.commit();
				Log.save2file = true;
				HgqwToast.makeText(Login.this, "log保存到文件已打开", HgqwToast.LENGTH_LONG).show();
				return;
			}
			if (name.equals("closepdalog2file")) {
				SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("savelogfile", false);
				editor.commit();
				Log.save2file = false;
				HgqwToast.makeText(Login.this, "log保存到文件已打开", HgqwToast.LENGTH_LONG).show();
				return;
			}
			String psw = editBoxPsw.getText().toString();
			if (psw.length() == 0) {
				HgqwToast.makeText(Login.this, R.string.passwordempty, HgqwToast.LENGTH_LONG).show();
				return;
			}

//			int vpnstates = vpnManager.getStatus();
//			// 如果客户端不存在，提示是否直接登录
//			if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_NOT_FOUNT) {
//				Log.i(TAG, "onShowLoginWithNoVPNQuestDialog();");
//				onShowLoginWithNoVPNQuestDialog();
//				return;
//
//			}
//			if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_STARTING) {
//				HgqwToast.makeText(Login.this, "VPN启动中，请稍后！", HgqwToast.LENGTH_LONG).show();
//				Log.i(TAG, "VPN启动中，请稍后！");
//				return;
//			}
//			if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_SUCCESS) {
//				Log.i(TAG, "vpnstates == VpnManager.START_VPNSERVICE_RESULT_SUCCESS,onLogin(false);");
//				onLogin(false);
//				return;
//			}
//			if (vpnstates != VpnManager.START_VPNSERVICE_RESULT_CONNECT_FAILED) {
//				Log.i(TAG, "vpnstates != VpnManager.START_VPNSERVICE_RESULT_CONNECT_FAILED,onShowLoginWithNoVPNQuestDialog()");
//				onShowLoginWithNoVPNQuestDialog();
//				return;
//			}
//			Log.i(TAG, "*end method*: vpnstates = " + vpnstates + ";onShowLoginWithNoVPNQuestDialog()");
//			onShowLoginWithNoVPNQuestDialog();
			onLogin(false);
		}
	}

	private class btnLoginByCardClickListener implements OnClickListener {
		/** 启动刷卡登陆 */
		public void onClick(View v) {
			// 判断离线不支持刷卡
			if (!BaseApplication.instent.getWebState()) {
				HgqwToast.toast(getApplicationContext(), getString(R.string.no_web_cannot_use_card), HgqwToast.LENGTH_LONG);
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("type", "loginbycard");
			intent.setClass(Login.this, LoginByCard.class);
			startActivityForResult(intent, STARTACTIVITY_FOR_PAYCARD);
			return;
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
	 * 
	 * @方法名：offLineLogin
	 * @功能说明：离线模式下登录流程
	 * @author liums
	 * @date 2013-10-10 上午10:39:54
	 */
	private void offLineLogin() {
		Log.i(TAG, "offLineLogin ");
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		// 从SharedPreferences获取历史用户
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		String userid = prefs.getString("userid", "");
		// 没有登录历史，提示用户
		if (userid == null || "".equals(userid.trim())) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.no_userid), HgqwToast.LENGTH_LONG);
			return;
		}

		// 判断用户名密码是否匹配
		String historyUserName = prefs.getString("username", "");// 历史登录用户
		String historyPassword = prefs.getString("password", "");// 历史登录用户密码
		String userName = editBoxAccount.getText().toString();// 当前输入用户
		String password = editBoxPsw.getText().toString();// 当前输入用户密码
		if (!provingUser(historyUserName, historyPassword, userName, password)) {
			HgqwToast.toast(getApplicationContext(), getString(R.string.history_user_not_equal), HgqwToast.LENGTH_LONG);
			return;
		}
		String userssdw = prefs.getString("userssdw", "");
		String name = prefs.getString("name", "");
		String zqjlid = prefs.getString("zqjlid", "");
		String licence = prefs.getString("licence", LitenceUtil.LICENCED_STATE_NO);

		LoginUser user = new LoginUser();
		LoginUser.SetCurrentLoginUser(user);
		LoginUser loginUser = LoginUser.getCurrentLoginUser();
		loginUser.SetUserID(userid);
		loginUser.SetUserName(userName);
		loginUser.setPassword(password);
		loginUser.SetUserSsdw(userssdw);
		loginUser.SetName(name);
		loginUser.Setzqjlid(zqjlid);
		loginUser.setLicence(licence);

		Intent intent = new Intent();
		intent.putExtra("fromLogin", true);
		intent.putExtra("offLineLogin", true);
		intent.setClass(getApplicationContext(), Index.class);
		startActivity(intent);
		Login.this.finish();
	}

	/**
	 * 
	 * @方法名：provingUser
	 * @功能说明：离线下验证用户权限
	 * @author liums
	 * @param password2
	 * @param userName2
	 * @param historyPassword2
	 * @param historyUserName2
	 * @date 2013-10-10 上午10:25:10
	 * @return
	 */
	private boolean provingUser(String historyUserName, String historyPassword, String userName, String password) {
		if (historyUserName == null || "".equals(historyUserName.trim())) {
			return false;
		}
		if (historyPassword == null || "".equals(historyPassword.trim())) {
			return false;
		}
		if (userName == null || "".equals(userName.trim())) {
			return false;
		}
		if (password == null || "".equals(password.trim())) {
			return false;
		}
		if (historyUserName.equals(userName) && historyPassword.equals(password)) {
			return true;
		}
		return false;
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
			int version = Flags.PDA_VERSION;
			if (name.equals(ADMINISTRATOR) && psw.equals(ADMINISTRATOR) && version != Flags.PDA_VERSION_DEFAULT) {// 哨兵版不适用
				LoginUser user = new LoginUser();
				LoginUser.SetCurrentLoginUser(user);
				LoginUser.getCurrentLoginUser().SetUserName(name);
				LoginUser.getCurrentLoginUser().SetUserID(name);
				LoginUser.getCurrentLoginUser().SetUserSsdw("高级管理员");
				LoginUser.getCurrentLoginUser().SetName("高级管理员");
				LoginUser.getCurrentLoginUser().Setzqjlid(name);
				LoginUser.getCurrentLoginUser().SetPermission(-1);
				SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("currentusername", LoginUser.getCurrentLoginUser().getUserName());
				editor.putString("userid", LoginUser.getCurrentLoginUser().getUserID());
				editor.putString("userssdw", LoginUser.getCurrentLoginUser().getUserSsdw());
				editor.putString("name", LoginUser.getCurrentLoginUser().getName());
				editor.putString("zqjlid", LoginUser.getCurrentLoginUser().getzqjlid());
				editor.putInt("permission", LoginUser.getCurrentLoginUser().getPermission());
				editor.commit();
				Intent intent = new Intent();
				intent.putExtra("fromLogin", true);
				intent.setClass(getApplicationContext(), Index.class);
				startActivity(intent);
				Login.this.finish();
				return;
			}
			str = "doLoginByUserName";
			params.add(new BasicNameValuePair("userName", name));
			params.add(new BasicNameValuePair("password", psw));
			params.add(new BasicNameValuePair("version", Flags.PDA_VERSION + ""));
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
					Log.i(TAG, "!((progressDialog)dialog).isShowing()");
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

	/**
	 * 
	 * @方法名：offLine
	 * @功能说明：离线登录
	 * @author liums
	 * @date 2013-10-10 上午9:38:29
	 */
	private void offLine() {
		// 提示是否离线登录
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.login_no_web);
		builder.setTitle(R.string.info);
		builder.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				offLineLogin();
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
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		handler = null;
		testClickCount = 0;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		testClickCount = 0;
		super.onPause();
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

							// 将密码保存到本地，用于离线验证
							String password = editBoxPsw.getText().toString();
							LoginUser.getCurrentLoginUser().setPassword((password));
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
					} else if ("info".equals(parser.getName())) {
						// 错误信息
						if (!success) {
							httpReturnXMLInfo = parser.nextText();
						}
					} /*
					 * else if ("licence".equals(parser.getName())) { if
					 * (success) { // 授权标志，0未授权，1已授权
					 * LoginUser.getCurrentLoginUser
					 * ().setLicence(parser.nextText()); } }
					 */
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		switch (requestCode) {
		case STARTACTIVITY_FOR_PAYCARD:
			if (resultCode == RESULT_OK) {
				cardNum = data.getStringExtra("cardNumber");
				if (cardNum == null || cardNum.length() == 0) {
					HgqwToast.makeText(Login.this, R.string.cardnumempty, HgqwToast.LENGTH_LONG).show();
					return;
				}

//				int vpnstates = vpnManager.getStatus();
//				// 如果客户端不存在，提示是否直接登录
//				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_NOT_FOUNT) {
//					onShowLoginWithNoVPNQuestDialogForLoginByCard();
//					return;
//
//				}
//				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_STARTING) {
//					HgqwToast.makeText(Login.this, "VPN启动中，请稍后！", HgqwToast.LENGTH_LONG).show();
//					return;
//				}
//				if (vpnstates == VpnManager.START_VPNSERVICE_RESULT_SUCCESS) {
//					onLogin(true);
//					return;
//				}
//				if (vpnstates != VpnManager.START_VPNSERVICE_RESULT_SUCCESS) {
//					onShowLoginWithNoVPNQuestDialogForLoginByCard();
//					return;
//				}
//
//				/*
//				 * int VPNStates = SystemSetting.getvpnstatus(); if (VPNStates
//				 * == SystemSetting.START_VPNSERVICE_RESULT_STARTING) {
//				 * Toast.makeText(loginMain.this, "VPN启动中，请稍后！",
//				 * Toast.LENGTH_LONG).show(); return; } if (VPNStates != 0) {
//				 * onShowLoginWithNoVPNQuestDialogForLoginByCard(); } else {
//				 * onLogin(true); }
//				 */
				onLogin(true);
		}
			
			break;
		}
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "loginmain onHttpResult(): " + (str != null));
		if (progressDialog == null || !progressDialog.isShowing()) {
			Log.i(TAG, "!progressDialog.isShowing()");
			return;
		}
		if (str != null) {
			if (onParseXMLData(str)) {
				SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();

				LoginUser.getCurrentLoginUser().SetPermission(1);
				editor.putString(getString(R.string.username), LoginUser.getCurrentLoginUser().getUserName());
				editor.putString("currentusername", LoginUser.getCurrentLoginUser().getUserName());
				editor.putString("userid", LoginUser.getCurrentLoginUser().getUserID());
				editor.putString("userssdw", LoginUser.getCurrentLoginUser().getUserSsdw());
				editor.putString("name", LoginUser.getCurrentLoginUser().getName());
				editor.putString("zqjlid", LoginUser.getCurrentLoginUser().getzqjlid());

				editor.putString("password", LoginUser.getCurrentLoginUser().getPassword());
				editor.putInt("permission", LoginUser.getCurrentLoginUser().getPermission());
				editor.commit();
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				Intent intent = new Intent();
				intent.putExtra("fromLogin", true);
				intent.setClass(getApplicationContext(), Index.class);
				startActivity(intent);
				Login.this.finish();
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				if (httpReturnXMLInfo != null) {// 可以请求通，但登录失败
					HgqwToast.makeText(Login.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {// 请求不通，提示离线登录
					HgqwToast.makeText(Login.this, R.string.login_failure, HgqwToast.LENGTH_LONG).show();
					offLine();
				}
			}
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			HgqwToast.makeText(Login.this, R.string.login_failure, HgqwToast.LENGTH_LONG).show();
			offLine();
		}
	}

	/**
	 * 
	 * @方法名：click
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2014-1-8 下午5:32:01
	 * @param v
	 */
	private int testClickCount = 0;

	public void click(View v) {
		switch (v.getId()) {
		case R.id.login_btn_back:
			onShowExitQuestDialog();
			break;
		case R.id.test:
			// test();
			break;
		default:
			break;
		}
	}

	private void test() {
		// startActivity(new Intent(this, GpsTestActivity.class));
		// BaseDialogUtils.showRequestDialog(this);
		// new RequestPzxx().request();
		// ImageUtil.testSaveImageLooper();
	}

	private void reBuildDatabase() {
		Log.i(TAG, "reBuildDatabase");
		if (getVersionCode() != 232 && getVersionCode() != 233) {
			Log.i(TAG, "getVersionCode() != 232 && getVersionCode() != 233" + getVersionCode());
			return;
		}
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
				Context.MODE_PRIVATE);
		boolean delDb232 = prefs.getBoolean("delDb232", false);
		if (delDb232) {
			Log.i(TAG, "delDb232==true");
			return;
		}

		File file = new File("/data/data/com.pingtech/databases/hgqw");
		if (file.exists()) {
			Log.i(TAG, "delete /data/data/com.pingtech/databases/hgqw" + file.delete());
		} else {
			Log.i(TAG, "file.exists()==false");

		}
		// Runtime runtime = Runtime.getRuntime();
		// try {
		// Process proc =
		// runtime.exec("rm -r /data/data/com.pingtech/databases/hgqw");
		// Log.i(TAG, "rm -r /data/data/com.pingtech/databases/*");
		// } catch (IOException e) {
		// Log.i(TAG, "rm -r /data/data/com.pingtech/databases/* IOException");
		// e.printStackTrace();
		// }
		Log.i(TAG, "editor.putBoolean delDb232 , true);");
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("Hgzjxx_addSjid", "0");
		editor.putBoolean("delDb232", true);
		editor.commit();
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_menu_settings:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), NetworkSettingActivity.class);
			startActivity(intent);
			break;
		case R.id.login_menu_quit:
			onShowExitQuestDialog();
			break;
		case R.id.login_menu_about:
			LoginUtil.about(this, handler);
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.login_menu, menu);
		return true;

	}

	/* 监听物理按键 */

	// @Override
	// public void onAttachedToWindow() {
	// getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	// super.onAttachedToWindow();
	// }

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// onShowExitQuestDialog();
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		android.util.Log.i(TAG, "onKeyDown:" + keyCode);

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:// 音量增大
			AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + 1, 1);
			playSound(currentVolume + 1);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:// 音量减小
			AudioManager mAudioManagerDown = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int currentVolumeDown = mAudioManagerDown.getStreamVolume(AudioManager.STREAM_MUSIC);
			mAudioManagerDown.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolumeDown - 1, 1);
			playSound(currentVolumeDown - 1);
			return true;
		case KeyEvent.KEYCODE_BACK:// 返回键
		case KeyEvent.KEYCODE_HOME:
			onShowExitQuestDialog();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void playSound(int currentVolume) {
		Log.i("currentVolume", "currentVolume=" + currentVolume);
		ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
		tone.startTone(ToneGenerator.TONE_PROP_BEEP, 300);
		tone.release();
	}
}