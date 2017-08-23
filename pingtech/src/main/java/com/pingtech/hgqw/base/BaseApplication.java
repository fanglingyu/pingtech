package com.pingtech.hgqw.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.pingtech.R;
import com.pingtech.hgqw.base.dialog.BaseDialog;
import com.pingtech.hgqw.base.entity.SystemSettingInfo;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.module.bindplace.utils.SharedPreferencesUtil;
import com.pingtech.hgqw.module.xtgl.utils.XtglUtil;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.SoundManager;
import com.pingtech.hgqw.utils.SystemSetting;

public class BaseApplication extends Application {
	private static final String TAG = "BaseApplication";

	/** 网络是否可用标识,后台会自动有个服务定时更新此标识 */
	private boolean webState = true;
 
	public static BaseApplication instent;

	public ProgressDialog progressDialog = null;
	public Dialog dialog = null;
	public BaseDialog.Builder builder = null;

	/** 音频播放类 */
	public static SoundManager soundManager = null;

	/** 日志保存路径 */
	public static String logPath = null;

	public static String logDir = null;

	/** sd卡缓存路径 */
	private String localPath = null;

	private String imagePath = null;

	public static int deviceModel = 0;

	private LoginUser userInfo = null;

	/** 将内存参数加载到全局 */
	private SystemSettingInfo systemSettingInfo = null;

	/** 下载标识，用于停止下载 */
	private boolean downloadFlag = false;

	public boolean isDbBusy = false;
	public boolean isDbBusyOnly = false;

	private String versionName = "";

	private HandlerThread handlerThread;

	/** 读卡器标识：true可以读卡，false跳出本次循环，用于控制读卡成功后请求数据期间不进行寻卡操作。 */
	private boolean canRead = true;

	public String lock = "daoLock";

	/** 经度 */
	private String longitude = "";

	/** 纬度 */
	private String latitude = "";

	public boolean imageDownlond = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		instent = this;
		init();
	}

	private void init() {
		initErrorLog();
		initSound();
		initFilePath();
		initDeviceModel();
		// 读卡接口使用
		initReadCardBusiness();
		iniVersion();
		deleteDb();
	}

	private void initErrorLog() {
		// 内部测试时添加保存crash log
		CrashHandler crashHandler = CrashHandler.getInstance(true);
		crashHandler.init(this );		
	}

	/**
	 * 
	 * @方法名：deleteDb
	 * @功能说明：删除数据库
	 * @author liums
	 * @date 2014-5-8 下午3:14:28
	 */
	private void deleteDb() {
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
		boolean delDbFlag = prefs.getBoolean("delDbFlag", false);
		if (!delDbFlag) {
			XtglUtil.delDB();
			Editor editor = prefs.edit();
			editor.putBoolean("delDbFlag", true);
			editor.putString("Hgzjxx_addSjid", "0");
			editor.putString("Hgzjxx_delSjid", "0");
			editor.commit();
		}
	}

	/**
	 * 
	 * @方法名：iniVersion
	 * @功能说明：版本号放入内存中
	 * @author liums
	 * @date 2014-1-9 下午2:45:51
	 */
	private void iniVersion() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(packInfo!=null){
			versionName = packInfo.versionName;
		}
		setVersionName(versionName);
		Log.i(TAG, "versionName：" + versionName + ",versionCode：" + packInfo.versionCode);
	}

	private void initReadCardBusiness() {
		handlerThread = new HandlerThread("handlerThread");
		handlerThread.start();

	}

	private void initDeviceModel() {
		deviceModel = DeviceUtils.getDeviceModel();
	}

	private void initSound() {
		if (soundManager == null) {
			soundManager = new SoundManager();
			soundManager.init();
		}
	}

	/**
	 * 
	 * @方法名：gainUserID
	 * @功能说明：获取当前用户
	 * @author 娄高伟
	 * @date 2013-10-14 下午4:10:23
	 * @return
	 */
	public String gainUserID() {
		if (userInfo != null) {
			return userInfo.getUserID();
		}
		SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name), MODE_PRIVATE);
		return prefs.getString("userid", "");
	}

	/**
	 * 
	 * @方法名：initLogFile
	 * @功能说明：初始化日志保存路径
	 * @author liums
	 * @date 2013-9-23 下午2:05:52
	 */
	public void initFilePath() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowDay = simpleDateFormat.format(calendar.getTime());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
		stringBuilder.append(File.separator);
		stringBuilder.append("pingtech");
		stringBuilder.append(File.separator);
		String localFileDir = stringBuilder.toString();
		localPath = localFileDir;
		imagePath = localPath + "photo" + File.separator;
		stringBuilder.append("log");
		stringBuilder.append(File.separator);
		logDir = stringBuilder.toString();
		stringBuilder.append("pda_");
		stringBuilder.append(nowDay);
		stringBuilder.append(".txt");
		logPath = stringBuilder.toString();

		File file = new File(localFileDir);
		if (!file.exists()) {
			boolean result = file.mkdir();
		}

		file = new File(logDir);
		if (!file.exists()) {
			boolean result = file.mkdir();
		}

		file = new File(logPath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		file = new File(imagePath);
		if (!file.exists()) {
			boolean result = file.mkdir();
		}
	}

	/**
	 * 
	 * @方法名：getWebState
	 * @功能说明：获取网络状态
	 * @author liums
	 * @date 2013-10-9 下午5:00:16
	 * @return true网络可用，false网络不可用
	 */
	public boolean getWebState() {
		return true;
		// return webState;
	}

	public void setWebState(boolean state) {
		Log.i(TAG, "setWebState:" + state);
		this.webState = state;
	}

	public LoginUser getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(LoginUser userInfo) {
		this.userInfo = userInfo;
	}

	public SystemSettingInfo getSystemSettingInfo() {
		return systemSettingInfo;
	}

	public void setSystemSettingInfo(SystemSettingInfo systemSettingInfo) {
		this.systemSettingInfo = systemSettingInfo;
	}

	/**
	 * 
	 * @方法名：settingInit
	 * @功能说明：初始化内存
	 * @author liums
	 * @date 2013-11-8 下午3:41:27
	 */
	public void settingInit() {
		Log.i(TAG, "initsetting start");
		SystemSettingInfo systemSettingInfo = new SystemSettingInfo();
		SystemSetting.init(false);
		// 加载巡查版，绑定地点信息
		initXunchaPlace();
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

		SystemSetting.setServerKadm(prefs.getString(getString(R.string.server_kadm), "209"));
		systemSettingInfo.setServerKadm(prefs.getString(getString(R.string.server_kadm), "209"));

		SystemSetting.setServerHost(prefs.getString(getString(R.string.server_host), "127.0.0.1"));
		systemSettingInfo.setServerHost(prefs.getString(getString(R.string.server_host), "127.0.0.1"));

		SystemSetting.setServerPort(prefs.getString(getString(R.string.server_port), "8080"));
		systemSettingInfo.setServerPort(prefs.getString(getString(R.string.server_port), "8080"));

		SystemSetting.setWebServiceConnect(prefs.getBoolean(getString(R.string.webservice_connect), true));
		systemSettingInfo.setWebServiceConnect(prefs.getBoolean(getString(R.string.webservice_connect), true));

		SystemSetting.setWebServiceNamespace(prefs.getString(getString(R.string.webservice_namespace),
				"http://service.webservice.pda.hgqw.pingtech.com.cn/"));
		systemSettingInfo.setWebServiceNamespace(prefs.getString(getString(R.string.webservice_namespace),
				"http://service.webservice.pda.hgqw.pingtech.com.cn/"));

		SystemSetting.setWebServiceUserName(prefs.getString(getString(R.string.webservice_username), "zjgbjz"));
		systemSettingInfo.setWebServiceUserName(prefs.getString(getString(R.string.webservice_username), "zjgbjz"));

		SystemSetting.setWebServicePassword(prefs.getString(getString(R.string.webservice_password), "111111"));
		systemSettingInfo.setWebServicePassword(prefs.getString(getString(R.string.webservice_password), "111111"));

		SystemSetting.setWebServiceCode(prefs.getString(getString(R.string.webservice_code), "bianfangpda"));
		systemSettingInfo.setWebServiceCode(prefs.getString(getString(R.string.webservice_code), "bianfangpda"));

		SystemSetting.setWebServiceWSDLUrl(prefs.getString(getString(R.string.webservice_url), "http://" + SystemSetting.getServerHost() + ":"
				+ SystemSetting.getServerPort() + "/pda3g/services/pda3GService"));
		systemSettingInfo.setWebServiceWSDLUrl(prefs.getString(getString(R.string.webservice_url), "http://" + SystemSetting.getServerHost() + ":"
				+ SystemSetting.getServerPort() + "/pda3g/services/pda3GService"));

		SystemSetting.setWebServiceArg1(prefs.getString(getString(R.string.webservice_arg1), "userName"));
		systemSettingInfo.setWebServiceArg1(prefs.getString(getString(R.string.webservice_arg1), "userName"));

		SystemSetting.setWebServiceArg2(prefs.getString(getString(R.string.webservice_arg2), "password"));
		systemSettingInfo.setWebServiceArg2(prefs.getString(getString(R.string.webservice_arg2), "password"));

		SystemSetting.setWebServiceArg3(prefs.getString(getString(R.string.webservice_arg3), "code"));
		systemSettingInfo.setWebServiceArg3(prefs.getString(getString(R.string.webservice_arg3), "code"));

		SystemSetting.setWebServiceArg4(prefs.getString(getString(R.string.webservice_arg4), "context"));
		systemSettingInfo.setWebServiceArg4(prefs.getString(getString(R.string.webservice_arg4), "context"));

		SystemSetting.setWebServiceArg5(prefs.getString(getString(R.string.webservice_arg5), "kadm"));
		systemSettingInfo.setWebServiceArg5(prefs.getString(getString(R.string.webservice_arg5), "kadm"));

		int which = prefs.getInt(getString(R.string.gps_timer), 60 * 5);
		SystemSetting.setGPSTimer(which);
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		SystemSetting.setPDACode(telephonyManager.getDeviceId());
		systemSettingInfo.setPdaCode(telephonyManager.getDeviceId());
		BaseApplication.instent.setSystemSettingInfo(systemSettingInfo);

	}

	private void initXunchaPlace() {
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			SharedPreferencesUtil.initDdbdToSystemSetting();
			break;

		default:
			break;
		}
	}

	public HandlerThread getHandlerThread() {
		return handlerThread;
	}

	public boolean isDownloadFlag() {
		return downloadFlag;
	}

	public void setDownloadFlag(boolean downloadFlag) {
		this.downloadFlag = downloadFlag;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	/** 读卡器标识：true可以读卡，false跳出本次循环，用于控制读卡成功后请求数据期间不进行寻卡操作。 */
	public boolean isCanRead() {
		return canRead;
	}

	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
