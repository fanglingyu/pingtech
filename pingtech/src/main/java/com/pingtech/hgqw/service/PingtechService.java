package com.pingtech.hgqw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.TelephonyManager;

import com.pingtech.R;
import com.pingtech.hgqw.activity.UpdateActivity;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.web.vpn.VpnManager;
import com.softsz.deviceInterface.LocationGPSData;

/** 运行的一个service，用户更新GPS信息 */
public class PingtechService extends Service implements LocationListener {
	private static final String TAG = "PingtechService";

	/** 发送GPS数据的http type */
	private static final int HTTPREQUEST_TYPE_FOR_GPS = 8;

	private LocationManager lm = null;

	private static String bestProvider;

	public static Location mLocation = null;

	/** 是否已发送但http未发送 */
	private static boolean mSending = false;

	/** 最后一次上报时间 */
	private static long lastReportTime;

	/** 网络状态定时更新时间2分钟 */
	private static final long WEB_STATE_TIME = 5000;

	private Timer mTimer = null;

	private TimerTask mTimerTask = null;

	private Pa8GPSListener pa8gpsListener;

	/** 检查VPN周期 */
	private static final long CHECK_VPN_LOOP_TIME = 3 * 60 * 1000;

	private Handler handler = new Handler() {
		/** 由于发送GPS就要联网，那么也应该启动VPN，这里是处理VPN启动结果 */
		public void handleMessage(Message msg) {
			Log.i(TAG, "VpnServer start ret:" + msg.what);
			int vpnstates = msg.what;
			SystemSetting.setVPNStatus(vpnstates);
			switch (vpnstates) {
			case VpnManager.START_VPNSERVICE_RESULT_SUCCESS:
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_TIMEOUT:
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_FAILED:
				break;
			case VpnManager.START_VPNSERVICE_RESULT_CONNECT_FAILED:
				break;
			case VpnManager.START_VPNSERVICE_RESULT_START_TIMEOUT:
				break;
			case VpnManager.START_VPNSERVICE_RESULT_NOT_FOUNT:
				break;
			case -1000:
				pa8GpsChanged(msg);
				break;
			}

		}
	};

	private Messenger messenger = new Messenger(handler);

	@Override
	public IBinder onBind(Intent intent) {

		return messenger.getBinder();
	}

	/** 初始化，包括启动VPN */
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();

		lastReportTime = 0;
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		int which = prefs.getInt(getString(R.string.gps_timer), 60 * 5);// 默认5分钟调用一次
		SystemSetting.setGPSTimer(which);
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		SystemSetting.setPDACode(telephonyManager.getDeviceId());

		// if (DeviceUtils.getDeviceModel() != DeviceUtils.DEVICE_MODEL_PA8) {
		onGetLocation();
		// } else {
		// pa8GPSBusiness();
		// }

		initvpn();
	}

	private void pa8GPSBusiness() {
		pa8gpsListener = new Pa8GPSListener(this, handler);
		pa8gpsListener.onCreate();
	}

	/**
	 * 服务启动后先等待一分钟后再连接vpn
	 */
	private void initvpn() {
		stopTimer();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "initvpn time again");
				VpnManager vpnManager = new VpnManager(handler, PingtechService.this);
				vpnManager.init();// vpn初始化
				vpnManager.setTAG("From PingtechService");
			}
		};
		Log.i(TAG, "initvpn begin");
		// 调用频率
		mTimer.schedule(mTimerTask, 60 * 1000, CHECK_VPN_LOOP_TIME);
	}

	/**
	 * 
	 * @方法名：stopTimer
	 * @功能说明：停止
	 * @author liums
	 * @date 2013-12-13 上午10:59:48
	 */
	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

	}

	/** 启动GPS，根据需求及当前实际情况获取最佳GPS提供者，注册监听消息 */
	private void onGetLocation() {
		Log.i(TAG, "onGetLocation");
		lm = null;
		if (lm == null) {
			lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		}
		/*if (!lm.isProviderEnabled("gps")) {
			HgqwToast.toast("!lm.isProviderEnabled(\"gps\")");
			BaseApplication.soundManager.onPlayVibrator(false);
			BaseApplication.soundManager.onPlayVibrator(false);
			BaseApplication.soundManager.onPlayVibrator(false);
			BaseApplication.soundManager.onPlayVibrator(false);
			BaseApplication.soundManager.onPlayVibrator(false);
			// android.provider.Settings.Secure.setLocationProviderEnabled(getContentResolver(),
			// "gps", true);
		}*/
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(true);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		bestProvider = lm.getBestProvider(criteria, true);
		Log.i(TAG, "GetLocation:" + bestProvider);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1F, this);
		lm.addGpsStatusListener(listener);
		reLoadLocation();
	}

	private void reLoadLocation() {
		return;
		// Log.i(TAG, "reLoadLocation");
		// if (bestProvider != null && lm != null) {
		// Location location = lm.getLastKnownLocation(bestProvider);
		// if (location != null) {
		// long lastTime = location.getTime();
		// long newTime = Calendar.getInstance().getTimeInMillis();
		// if ((lastTime > newTime) || (lastTime < newTime && (newTime -
		// lastTime) > 1000 * 60 * 2)) {// 二分钟前获取的数据
		// // HgqwToast.toast("当前位置信息为历史数据");
		// Log.i(TAG, "当前位置信息为历史数据");
		// mLocation = null;
		// changeAppJwd(null, false);
		// } else {
		// Log.i(TAG, "mLocation = location");
		// // HgqwToast.toast("lastTime >newTime");
		// mLocation = location;
		// onReportWithNewLocation(null, false);
		// }
		// } else {
		// // HgqwToast.toast("location==null");
		// Log.i(TAG, "location==null");
		// mLocation = location;
		// onReportWithNewLocation(null, false);
		// }
		// } else {
		// Log.i(TAG, "bestProvider == null && lm == null");
		// }

	}

	protected void pa8GpsChanged(Message msg) {
		LocationGPSData data = (LocationGPSData) msg.obj;
		if (data == null) {
//			HgqwToast.toast(new Date().toLocaleString() + "\nLocationGPSData data== null");
			Log.i(TAG, "pa8GpsChanged data==null set mLocation = null");
			mLocation = null;
			changeAppJwd(null, false);
			return;
		}
//		HgqwToast.toast(new Date().toLocaleString() + "\n精度：" + data.getLongitude() + "，纬度：" + data.getLatitude());

		onReportWithNewLocation(data, true);
	}

	/**
	 * 处理上报GPS数据
	 * 
	 * @param locationGPSData
	 *            TODO
	 * @param isPa8NewInterface
	 *            TODO
	 */
	@SuppressLint("SimpleDateFormat")
	private void onReportWithNewLocation(LocationGPSData locationGPSData, boolean isPa8NewInterface) {
		changeAppJwd(locationGPSData, isPa8NewInterface);
		int gpstimer = SystemSetting.getGPSTimer();
		if (gpstimer == 0) {
			Log.i(TAG, "arm info error gpstimer==0");
			SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
			gpstimer = prefs.getInt("gps_timer", -1);
			SystemSetting.setGPSTimer(gpstimer);
		}
		if (gpstimer == -1) {
			gpstimer = 5 * 60;
			setGpsTimer(gpstimer);
			Log.i(TAG, "local file error gpstimer==-1");
			return;
		}

		Log.i(TAG, "onReportWithNewLocation , gpstimer =" + gpstimer);

		if (gpstimer < 30) {// 如时间小于30秒，重新设定
			gpstimer = 5 * 60;
			setGpsTimer(gpstimer);
		}

		long nowtime = System.currentTimeMillis();
		if (UpdateActivity.downloading) {
			Log.i(TAG, "UpdateActivity.downloading ");
			return;
		}
		if ((gpstimer * 1000) > (nowtime - lastReportTime)) {
			Log.i(TAG, "gpstimer * 1000=" + gpstimer * 1000 + " > nowtime - lastReportTime=" + (nowtime - lastReportTime));
			return;
		}

		if (mSending) {
			Log.i(TAG, "mSending");
			return;
		}

		Log.i(TAG, "ReportWithNewLocation:" + (nowtime - lastReportTime));

		if (mLocation != null) {
			String str = "sendGPSData";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (SystemSetting.getPDACode() == null) {
				TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
				SystemSetting.setPDACode(telephonyManager.getDeviceId());
			}

			String longitude = "";
			String latitude = "";
			String speed = "";
			String altitude = "";
			String bearing = "";
			String time = "";

			if (isPa8NewInterface) {
				longitude = locationGPSData.getLongitude();
				latitude = locationGPSData.getLatitude();
				// speed =locationGPSData.getspeed;
				altitude = locationGPSData.getAltitude();
				// bearing = locationGPSData.getbearing
				time = locationGPSData.getTime();

			} else {
				longitude = Double.toString(mLocation.getLongitude());
				latitude = Double.toString(mLocation.getLatitude());
				speed = Double.toString(mLocation.getSpeed());
				altitude = Double.toString(mLocation.getAltitude());
				bearing = Double.toString(mLocation.getBearing());
				Date date = new Date(mLocation.getTime());
				SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				time = dataFormat.format(date);
			}

			params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
			params.add(new BasicNameValuePair("speed", speed));
			params.add(new BasicNameValuePair("altitude", altitude));
			params.add(new BasicNameValuePair("bearing", bearing));
			params.add(new BasicNameValuePair("time", time));

			if (LoginUser.getCurrentLoginUser() != null) {
				params.add(new BasicNameValuePair("userName", LoginUser.getCurrentLoginUser().getUserName()));
			} else {
				params.add(new BasicNameValuePair("userName", ""));
			}
			mSending = true;
			lastReportTime = System.currentTimeMillis();
			NetWorkManager.request(new OnHttpResult() {

				@Override
				public void onHttpResult(String str, int httpRequestType) {
					Log.i(TAG, "onHttpResult:" + (str != null) + "," + httpRequestType);
					if (httpRequestType == HTTPREQUEST_TYPE_FOR_GPS) {
						mSending = false;
					}
				}
			}, str, params, HTTPREQUEST_TYPE_FOR_GPS);
		} else {
			Log.i(TAG, "mLocation == null");
		}
	}

	private void changeAppJwd(LocationGPSData locationGPSData, boolean isPa8NewInterface) {
		if (isPa8NewInterface && locationGPSData != null) {
			BaseApplication.instent.setLongitude(locationGPSData.getLongitude());
			BaseApplication.instent.setLatitude(locationGPSData.getLatitude());
			return;
		} else if (isPa8NewInterface) {
			BaseApplication.instent.setLongitude("");
			BaseApplication.instent.setLatitude("");
			return;
		}
		if (mLocation != null) {
			Log.i(TAG, "changeAppJwd,mLocation != null");
			BaseApplication.instent.setLongitude(mLocation.getLongitude() + "");
			BaseApplication.instent.setLatitude(mLocation.getLatitude() + "");
		} else {
			Log.i(TAG, "changeAppJwd,mLocation == null");
			BaseApplication.instent.setLongitude("");
			BaseApplication.instent.setLatitude("");
		}
		// 通知其他监听接口

	}

	/**
	 * 
	 * @方法名：setGpsTimer
	 * @功能说明：本地文件未设置，默认5分钟
	 * @author liums
	 * @date 2014-1-6 下午3:16:54
	 * @param gpstimer
	 */
	private void setGpsTimer(int gpstimer) {
		SystemSetting.setGPSTimer(gpstimer);
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(getString(R.string.gps_timer), gpstimer);
		editor.commit();
		SystemSetting.setGPSTimer(gpstimer);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		if (lm != null) {
			lm.removeGpsStatusListener(listener);
			lm.removeUpdates(this);
			lm = null;
		}
		if (handler != null) {
			handler.removeCallbacks(autoCheckOutService);
			handler = null;
		}
		stopService(new Intent(this, Pa8GPSListener.class));
		stopTimer();
		super.onDestroy();
	}

	/** GPS信息发生改变的callback */
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		// Log.i(TAG, "onLocationChanged:" + location.toString());
		mLocation = location;
		bestProvider = mLocation.getProvider();
		onReportWithNewLocation(null, false);
		// if (handler != null) {
		// handler.removeCallbacks(autoCheckOutService);
		// handler.postDelayed(autoCheckOutService, 10000);
		// }
	}

	/** GPS状态发生改变的callback GPS状态变化时触发 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// HgqwToast.toast("GPS状态为服务区外，onStatusChanged , status=" + status);
		switch (status) {
		// GPS状态变为可见时
		case LocationProvider.AVAILABLE:
			Log.i(TAG, "GPS状态变为可见");
			reLoadLocation();
			break;
		// GPS状态变为服务区外时
		case LocationProvider.OUT_OF_SERVICE:
			onReportWithNewLocation(null, false);
			Log.i(TAG, "GPS状态变为服务区外");
			mLocation = null;
			changeAppJwd(null, false);
			break;
		// GPS状态变变为暂停服务
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			onReportWithNewLocation(null, false);
			Log.i(TAG, "GPS状态变为暂停服务");
			mLocation = null;
			changeAppJwd(null, false);
			break;
		}
	}

	/** GPS enabled GPS开启时触发 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onProviderEnabled:" + provider);
		onGetLocation();
	}

	/** GPS disabled GPS禁用时触发 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onProviderDisabled:" + provider);
		onReportWithNewLocation(null, false);
		mLocation = null;
		changeAppJwd(null, false);
	}

	/** 监听GPS状态消息 GPS状态监听，包括GPS启动、停止、第一次定位、卫星变化等事件。 */
	private GpsStatus.Listener listener = new GpsStatus.Listener() {

		public void onGpsStatusChanged(int event) {
			if (lm == null) {
				return;
			}
			// GpsStatus，GPS状态信息，上面在卫星状态变化时，用到了GpsStatus。
			GpsStatus gpsStatus = lm.getGpsStatus(null);
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				// gpsStatus.getTimeToFirstFix();
				// HgqwToast.toast("第一次定位");
				Log.i(TAG, "第一次定位 ,GPS_EVENT_FIRST_FIX  = " + event);
				Log.i(TAG, "GpsStatus.GPS_EVENT_FIRST_FIX:" + gpsStatus.getTimeToFirstFix());
				reLoadLocation();
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				// Log.i(TAG, "GPS_EVENT_SATELLITE_STATUS");
				gps_event_satellite_status(gpsStatus);
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "GpsStatus.GPS_EVENT_STARTED");
				// HgqwToast.toast("定位启动，changeAppJwd");
				Log.i(TAG, "定位启动,GPS_EVENT_STARTED = " + event);
				reLoadLocation();
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "GpsStatus.GPS_EVENT_STOPPED");
				// HgqwToast.toast("定位结束   ，changeAppJwd");
				Log.i(TAG, "定位结束,GPS_EVENT_STOPPED = " + event);
				mLocation = null;
				changeAppJwd(null, false);
				break;
			}
		}
	};

	/**
	 * 如果5秒钟内没有收到GPS改变的消息，就认为是失去与卫星的连接，进入outService
	 * 
	 * @see onLocationChanged
	 */
	private Runnable autoCheckOutService = new Runnable() {
		@Override
		public void run() {
			if (handler == null) {
				return;
			}
			onReportWithNewLocation(null, false);
			mLocation = null;
			changeAppJwd(null, false);
			Log.i(TAG, "autoCheckOutService timer out");
		}
	};

	protected void gps_event_satellite_status(GpsStatus gpsStatus) {

		int maxSatellites = gpsStatus.getMaxSatellites();// 卫星数量
		Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
		int prnAndSnrCount = 0;// 可见卫星数量
		int usedInFixCount = 0;// 可见卫星数量
		while (iters.hasNext()) {
			GpsSatellite satellite = iters.next();
			if (satellite.getPrn() <= 200 && satellite.getSnr() > 0) {
				prnAndSnrCount++;
			}
			if (satellite.usedInFix()) {
				usedInFixCount++;
			}
			// 包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
			// satellite.getElevation(); // 卫星仰角
			// satellite.getAzimuth();// 卫星方位角
			// satellite.getSnr(); // 信噪比
			// satellite.getPrn();// 伪随机数，可以认为他就是卫星的编号
			// 历书与星历都是表示卫星运行的参数。历书包括全部卫星的大概位置，用于卫星预报；星历只是当前接收机观测到的卫星的精确位置，用于定位。
			// satellite.hasAlmanac(); //卫星历书
			// satellite.hasEphemeris(); // GPS星历
			// satellite.usedInFix();

		}
		// 伪随机数小于200，且信噪比大于0的卫星数量小于三的时候认为是从定位不到。
		if (prnAndSnrCount < 2 && usedInFixCount < 3 && mLocation != null) {
			onReportWithNewLocation(null, false);
			// HgqwToast.toast("卫星可见数小于3");
			Log.i(TAG, "卫星可见数小于3 ， 认为失去位置信息");
			mLocation = null;
			changeAppJwd(null, false);
		}
	}
}
