package com.pingtech.hgqw.utils;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;

public class DeviceUtils {
	/**
	 * 设备型号：虚拟机
	 */
	public static final int DEVICE_MODEL_SDK = -1;

	/**
	 * 设备型号：一代警务通mima_PE43
	 */
	public static final int DEVICE_MODEL_MIMA = 1;

	/**
	 * 设备型号：二代警务通 M802
	 */
	public static final int DEVICE_MODEL_M = 2;

	/**
	 * 设备型号：边检通PA8
	 */
	public static final int DEVICE_MODEL_PA8 = 3;

	/**
	 * 设备型号：新边检通PA9
	 */
	public static final int DEVICE_MODEL_PA9 = 4;

	/**
	 * 设备型号：肯麦思CFON640
	 */
	public static final int DEVICE_MODEL_CFON640 = 5;

	/**
	 * 
	 * @方法名：getDeviceModel
	 * @功能说明：根据设备型号判断启动的读卡器类型
	 * @author liums
	 * @date 2013-6-18
	 * @return 1 DEVICE_MODEL_MIMA 一代警务通mima_PE43, 2 DEVICE_MODEL_M 二代警务通 M802 ,
	 *         3 PA8
	 */
	public static int getDeviceModel() {
		String deviceModel = getModel();
		if (deviceModel.contains("mima")) {
			return DEVICE_MODEL_MIMA;
		} else if (deviceModel.contains("sdk")) {
			return DEVICE_MODEL_SDK;
		} else if (deviceModel.contains("PA8")) {
			return DEVICE_MODEL_PA8;
		} else if (deviceModel.contains("PA9")) {
			return DEVICE_MODEL_PA9;
		} else if (deviceModel.contains("CFON640")) {
			return DEVICE_MODEL_CFON640;
		} else {
			return DEVICE_MODEL_M;
		}
	}

	/**
	 * 获取PDA IMEI
	 * 
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager == null) {
			return "123456";
		}
		return telephonyManager.getDeviceId();
	}

	/**
	 * 
	 * @方法名：getIMEI
	 * @功能说明：获取PDA IMEI
	 * @author liums
	 * @date 2013-10-9 下午5:52:13
	 * @return
	 */
	public static String getIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.instent
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager == null) {
			return "-1";
		}
		return telephonyManager.getDeviceId();
	}

	/**
	 * 
	 * @方法名：getModel
	 * @功能说明：获取设备型号
	 * @author liums
	 * @date 2013-6-6
	 * @return
	 */
	public static String getModel() {
		return android.os.Build.MODEL;// 设备型号
	}

	/**
	 * 
	 * @方法名：getRootPath
	 * @功能说明：获取root路径
	 * @author liums
	 * @date 2013-6-6
	 * @return
	 */
	public static String getRootPath(Context context) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.applicationInfo.dataDir;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getMobileType(Context context) {
		int type = -1;
		TelephonyManager iPhoneManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String iNumeric = iPhoneManager.getSimOperator();
		if (iNumeric.length() > 0) {
			if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
				// 中国移动
				type = Flags.MOBILE_TYPE_CMCC;
				// return "中国移动";
			} else if (iNumeric.equals("46001")) {
				// 中国联通
				type = Flags.MOBILE_TYPE_CUCC;
				// return "中国移动";
			} else if (iNumeric.equals("46003")) {
				// 中国电信
				type = Flags.MOBILE_TYPE_CTCC;
				// return "中国移动";
			}
		}
		return type;
	}

	public static int switchVersion(int defaultView, int sentinelView) {
		int version = defaultView;
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_DEFAULT:
			version = defaultView;
			break;
		case Flags.PDA_VERSION_SENTINEL:
			version = sentinelView;
			break;
		default:
			version = defaultView;
			break;
		}
		return version;

	}

	public static enum CONNECTTYPE {
		UNKNOWN, PA710SIM, PA9SIM, PA718
	};

	public static CONNECTTYPE deviceTypeVerify() {
		String[] verify718 = new String[] { "/dev/ttyv_a0", "/dev/ttyv_b0" };
		File f0 = new File(verify718[0]);
		File f1 = new File(verify718[1]);
		if (f0.exists() && f1.exists()) {
			return CONNECTTYPE.PA718;
		}
		// ----------------------------
		String fileName = "/dev/ttyHSL4";
		File file = new File(fileName);
		if (file.exists())
			return CONNECTTYPE.PA9SIM;
		else
			return CONNECTTYPE.PA710SIM;
	}

}
