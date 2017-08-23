package com.pingtech.hgqw.web.vpn;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;

public class VpnManager {

	/** VPN状态：正在初始化 */
	public static final int START_VPNSERVICE_RESULT_NONE = -1;

	/** 三所VPN客户端名称 */
	public static final String VPN_PACKAGE_NAME_SS = "com.wonder.vpnClient";

	/** 三所VPN后台服务名称 */
	public static final String VPN_SERVICE_NAME_SS = "com.wonder.vpnClient.VPNClientService";

	/** VPN正在启动中 */
	public static final int START_VPNSERVICE_RESULT_STARTING = 6;

	/** 安全客户端启动成功 */
	public static final int START_VPNSERVICE_RESULT_START_SUCCESS = 7;

	/** 安全客户端启动超时 */
	public static final int START_VPNSERVICE_RESULT_START_TIMEOUT = 4;

	/** 安全客户端未安装 */
	public static final int START_VPNSERVICE_RESULT_NOT_FOUNT = 5;

	/** 安全客户端启动失败 */
	public static final int START_VPNSERVICE_RESULT_START_FAILED = 2;

	/** 安全通道连接成功 */
	public static final int START_VPNSERVICE_RESULT_CONNECT_SUCCESS = 8;

	/** 安全通道连接超时 */
	public static final int START_VPNSERVICE_RESULT_CONNECT_TIMEOUT = 1100;

	/** 安全通道连接失败 */
	public static final int START_VPNSERVICE_RESULT_CONNECT_FAILED = 3;

	/** VPN状态：客户端启动成功，安全通道连接成功 */
	public static final int START_VPNSERVICE_RESULT_SUCCESS = 0;

	private static String TAG = "VpnManager";

	public int status;

	/** 当前状态：正在启动 */

	private Handler handler;

	private Context context;

	public String getTAG() {
		return TAG;
	}

	public void setTAG(String tAG) {
		TAG = TAG + "," + tAG;
	}

	public VpnManager(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
		status = (START_VPNSERVICE_RESULT_NONE);
	}

	public void init() {
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_MIMA:
			Log.i(TAG, "******DEVICE_MODEL_MIMA******");
			initMima();
			break;
		case DeviceUtils.DEVICE_MODEL_M:
		case DeviceUtils.DEVICE_MODEL_PA8:
		case DeviceUtils.DEVICE_MODEL_PA9:
			Log.i(TAG, "******DEVICE_MODEL_M******");
			initM802();
			break;
		case DeviceUtils.DEVICE_MODEL_SDK:
			break;

		default:
			break;
		}
	}

	private void sendMessage(int what) {
		handler.obtainMessage(what).sendToTarget();
	}

	
	/**
	 * 
	 * @方法名：initM802
	 * @功能说明：
	 * @author liums
	 * @date  2014-3-3 上午10:06:16
	 */
	private void initM802() {
		status = (START_VPNSERVICE_RESULT_STARTING);

		// 查看客户端vpn服务是否已启动
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);
		List<RunningAppProcessInfo> runningAppNames = mActivityManager.getRunningAppProcesses();

		for (int i = 0; i < mServiceList.size(); i++) {
			String string = mServiceList.get(i).service.getClassName();
			if (mServiceList.get(i).service.getClassName().equals(VPN_SERVICE_NAME_SS)) {
				Log.i(TAG, "Vpn客户端服务已经启动");
				status = (START_VPNSERVICE_RESULT_START_SUCCESS);
				sendMessage(START_VPNSERVICE_RESULT_START_SUCCESS);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// if (startM802()) {
						connM802();// 服务启动成功，开始建立安全通道
						// }
					}
				}).start();
				return;
			}
		}

		// 判断客户端是否存在,并启动
		// 启动安全平台客户端
		ComponentName comp = new ComponentName(VPN_PACKAGE_NAME_SS, VPN_SERVICE_NAME_SS);
		Intent intent = new Intent();
		intent.setComponent(comp);
		intent.setAction("android.intent.action.MAIN");
		comp = context.startService(intent);
		if (comp == null) {
			Log.i(TAG, "没有找到Vpn客户端");
			status = (START_VPNSERVICE_RESULT_NOT_FOUNT);
			sendMessage(START_VPNSERVICE_RESULT_NOT_FOUNT);
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (startM802()) {
					connM802();// 服务启动成功，开始建立安全通道
				}
			}
		}).start();

	}

	private boolean connM802() {
		// 检查一遍通道状态
		int count = 0;
		SystemClock.sleep(2 * 1000);
		while (count < 2) {
			Log.i(TAG, "*********************************************");
			Log.i(TAG, "启动前查看通道状态，查看次数：" + count);
			int status = checkStatusM802(count);
			if (status == STATUSM802_YJLJ) {
				Log.i(TAG, "当前安全通道已连接，不调用CStart接口，直接返回。");
				return true;
			}
			/*
			 * if ((status == STATUSM802_TZZT || status == STATUSM802_ERROR)) {
			 * Log.i(TAG, "停止或错误状态，跳出循环，直接调用CStart接口"); break; }
			 */
			count++;
//			SystemClock.sleep(3 * 1000);
		}
		status = (START_VPNSERVICE_RESULT_STARTING);
		Log.i(TAG, "*********************************************");
		Log.i(TAG, "通道未连接，开始调用启动接口");
		// 发送建立通道命令
		DatagramSocket socket = null;
		try {
			DatagramPacket packet;
			byte[] data = new byte[1024];
			data = "<CMD>CSTART</CMD>".getBytes();

			socket = new DatagramSocket();
			packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 50039);
			socket.send(packet);

			packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			byte[] byteData = packet.getData();
			String startData = new String(byteData);
			Log.i(TAG, "调用启动接口返回结果：" + startData);
		} catch (UnknownHostException e) {
			Log.i(TAG, "安全通道连接失败，" + e.getMessage());
			status = (START_VPNSERVICE_RESULT_CONNECT_FAILED);
			sendMessage(START_VPNSERVICE_RESULT_CONNECT_FAILED);
			return false;
		} catch (Exception e) {
			Log.i(TAG, "安全通道连接失败，" + e.getMessage());
			status = (START_VPNSERVICE_RESULT_CONNECT_FAILED);
			sendMessage(START_VPNSERVICE_RESULT_CONNECT_FAILED);
			return false;
		} finally {
			if (socket != null) {
				socket.close();
			}
		}

		// 查询通道是否建立成功
		Log.i(TAG, "*********************************************");
		Log.i(TAG, "调用启动接口后再次检查通道状态，先等3秒");
		SystemClock.sleep(3 * 1000);
		int countAfter = 0;
		int realStatus = -100;
		while (countAfter < 2) {
			Log.i(TAG, "*********************************************");
			Log.i(TAG, "启动后再次查看通道状态，查看次数：" + countAfter);
			realStatus = checkStatusAfterStartM802(countAfter);
			if (realStatus == STATUSM802_YJLJ) {
				Log.i(TAG, "启动后再次查看通道状态，安全通道已连接，直接返回。");
				return true;
			}
			countAfter++;
//			SystemClock.sleep(3 * 1000);
		}

		if (realStatus == STATUSM802_TZZT || realStatus == STATUSM802_ERROR) {
			Log.i(TAG, "启动后再次查看通道状态，停止或错误状态，跳出循环，直接提示前台");
			return false;
		}

		status = START_VPNSERVICE_RESULT_CONNECT_TIMEOUT;
		sendMessage(START_VPNSERVICE_RESULT_CONNECT_TIMEOUT);
		return false;
	}

	private int checkStatusAfterStartM802(int count) {
		String connStatus;
		DatagramSocket socket = null;
		try {
			DatagramPacket packet;
			byte[] data = new byte[128];
			byte[] data2 = new byte[128];
			data = "<CMD>QSTATUS</CMD>".getBytes();

			socket = new DatagramSocket();

			packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 50039);
			socket.send(packet);

			packet = new DatagramPacket(data2, data2.length);
			socket.receive(packet);
			connStatus = new String(packet.getData());
			Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，调用状态查询接口返回结果：" + connStatus);
			int codeIndex = connStatus.indexOf("<CODE>");
			String temp = connStatus.substring(codeIndex, connStatus.length());
			int cmodIndex = temp.indexOf(">");
			String respontCode = temp.substring(cmodIndex + 1, cmodIndex + 2);

			if (respontCode.equals("3")) {
				Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，安全通道连接成功");
				status = (START_VPNSERVICE_RESULT_SUCCESS);
				sendMessage(START_VPNSERVICE_RESULT_SUCCESS);
				return STATUSM802_YJLJ;
			}

			if (respontCode.equals("1")) {
				Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，安全通道连接状态：连接服务器中 ， 等待10秒");
				status = START_VPNSERVICE_RESULT_STARTING;
				SystemClock.sleep(5 * 1000);
				return STATUSM802_ZZLJ;
			} else if (respontCode.equals("2")) {
				Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，安全通道连接状态：接受认证结果中，等待10秒");
				status = START_VPNSERVICE_RESULT_STARTING;
				SystemClock.sleep(5 * 1000);
				return STATUSM802_ZZLJ;
			}

			if (respontCode.equals("0")) {
				Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，安全通道连接状态：停止");
				status = START_VPNSERVICE_RESULT_CONNECT_FAILED;
				sendMessage(START_VPNSERVICE_RESULT_CONNECT_FAILED);
				return STATUSM802_TZZT;
			} else if (respontCode.equals("4")) {
				Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，安全通道连接状态：有错误的状态");
				status = START_VPNSERVICE_RESULT_CONNECT_FAILED;
				sendMessage(START_VPNSERVICE_RESULT_CONNECT_FAILED);
				return STATUSM802_ZZRZ;
			}

		} catch (UnknownHostException e) {
			Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，安全通道连接失败，" + e.getMessage());
			sendMessage(START_VPNSERVICE_RESULT_CONNECT_FAILED);
			status = (START_VPNSERVICE_RESULT_CONNECT_FAILED);
			return STATUSM802_ERROR;
		} catch (Exception e) {
			Log.i(TAG, "count:" + count + ",启动后再次查看通道状态，安全通道连接失败，" + e.getMessage());
			sendMessage(START_VPNSERVICE_RESULT_CONNECT_FAILED);
			status = (START_VPNSERVICE_RESULT_CONNECT_FAILED);
			return STATUSM802_ERROR;
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return STATUSM802_TZZT;
	}

	private static final int STATUSM802_TZZT = 0;

	private static final int STATUSM802_ZZLJ = 1;

	private static final int STATUSM802_ZZRZ = 2;

	private static final int STATUSM802_YJLJ = 3;

	private static final int STATUSM802_ERROR = 4;

	private int checkStatusM802(int count) {
		// 查询通道是否建立成功
		String connStatus;
		DatagramSocket socket = null;
		try {
			DatagramPacket packet;
			byte[] data = new byte[128];
			byte[] data2 = new byte[128];
			data = "<CMD>QSTATUS</CMD>".getBytes();

			socket = new DatagramSocket();

			packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 50039);
			socket.send(packet);

			packet = new DatagramPacket(data2, data2.length);
			socket.receive(packet);
			connStatus = new String(packet.getData());
			Log.i(TAG, "count:" + count + ",调用状态查询接口返回结果：" + connStatus);
			int codeIndex = connStatus.indexOf("<CODE>");
			String temp = connStatus.substring(codeIndex, connStatus.length());
			int cmodIndex = temp.indexOf(">");
			String respontCode = temp.substring(cmodIndex + 1, cmodIndex + 2);

			if (respontCode.equals("3")) {
				Log.i(TAG, "count:" + count + ",安全通道连接成功");
				status = (START_VPNSERVICE_RESULT_SUCCESS);
				sendMessage(START_VPNSERVICE_RESULT_SUCCESS);
				return STATUSM802_YJLJ;
			}
			if (respontCode.equals("1")) {
				Log.i(TAG, "count:" + count + ",安全通道连接状态：连接服务器中，等待连接，线程等待10秒");
				status = (START_VPNSERVICE_RESULT_STARTING);
				if (count == 2) {// 第二次查询直接返回,线程不等待
					return STATUSM802_ZZLJ;
				}
				SystemClock.sleep(5 * 1000);
				return STATUSM802_ZZLJ;
			} else if (respontCode.equals("2")) {
				Log.i(TAG, "count:" + count + ",安全通道连接状态：接受认证结果中，等待连接，线程等待10秒");
				status = (START_VPNSERVICE_RESULT_STARTING);
				if (count == 2) {// 第二次查询直接返回,线程不等待
					return STATUSM802_ZZRZ;
				}
				SystemClock.sleep(5 * 1000);
				return STATUSM802_ZZRZ;
			}

			if (respontCode.equals("0")) {
				Log.i(TAG, "count:" + count + ",安全通道连接状态：停止，将调用启动接口");
				return STATUSM802_TZZT;
			} else if (respontCode.equals("4")) {
				Log.i(TAG, "count:" + count + ",安全通道连接状态：有错误的状态，将调用启动接口");
				return STATUSM802_ERROR;
			}

			socket.close();
		} catch (UnknownHostException e) {
			Log.i(TAG, "count:" + count + ",检查安全通道连接状态报错，" + e.getMessage());
			return STATUSM802_ERROR;
		} catch (Exception e) {
			Log.i(TAG, "count:" + count + ",检查安全通道连接状态报错，" + e.getMessage());
			return STATUSM802_ERROR;
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return STATUSM802_TZZT;
	}

	private boolean startM802() {
		// 查看客户端vpn服务是否启动成功
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);

		for (int i = 0; i < mServiceList.size(); i++) {
			if (mServiceList.get(i).service.getClassName().equals(VPN_SERVICE_NAME_SS)) {
				Log.i(TAG, "Vpn客户端服务启动成功");
				status = (START_VPNSERVICE_RESULT_START_SUCCESS);
				sendMessage(START_VPNSERVICE_RESULT_START_SUCCESS);
				return true;
			}
		}
		Log.i(TAG, "Vpn客户端启动失败");
		status = (START_VPNSERVICE_RESULT_START_FAILED);
		sendMessage(START_VPNSERVICE_RESULT_START_FAILED);
		return false;
	}

	/**
	 * 
	 * @方法名：close
	 * @功能说明：退出客户端
	 * @author liums
	 * @date 2014-1-6 上午11:33:05
	 */
	public static void close() {
		Log.i(TAG, "******close()******");
		switch (DeviceUtils.getDeviceModel()) {
		case DeviceUtils.DEVICE_MODEL_MIMA:
			Log.i(TAG, "******DEVICE_MODEL_MIMA******");
			break;
		case DeviceUtils.DEVICE_MODEL_M:
			if (hasVpnClient()) {
				stopM802();
				closeM802();
			}
			Log.i(TAG, "******DEVICE_MODEL_M******");
			break;
		case DeviceUtils.DEVICE_MODEL_SDK:
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @方法名：hasVpnC
	 * @功能说明：查看是否已启动安全客户端连接服务
	 * @author liums
	 * @date 2014-1-6 上午11:31:30
	 * @return
	 */
	private static boolean hasVpnClient() {
		ActivityManager mActivityManager = (ActivityManager) BaseApplication.instent.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);

		for (int i = 0; i < mServiceList.size(); i++) {
			if (mServiceList.get(i).service.getClassName().equals(VPN_SERVICE_NAME_SS)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @方法名：stopM802
	 * @功能说明：停止客户端
	 * @author liums
	 * @date 2014-1-2 下午2:55:16
	 */
	private static void stopM802() {
		Log.i(TAG, "stopM802");
		String fortune;
		try {
			DatagramSocket socket;
			DatagramPacket packet;
			byte[] data = new byte[1024];
			data = "<CMD>CSTOP</CMD>".getBytes();

			socket = new DatagramSocket();

			packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 50039);
			socket.send(packet);

			packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			fortune = new String(packet.getData());
			Log.i(TAG, "stopM802 fortune = " + fortune);
			socket.close();
		} catch (UnknownHostException e) {
			System.err.println("Exception: host could not be found");
			// return null;
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			e.printStackTrace();

		}
	}

	/**
	 * 
	 * @方法名：stopM802
	 * @功能说明：停止客户端
	 * @author liums
	 * @date 2014-1-2 下午2:55:16
	 */
	private static void closeM802() {
		Log.i(TAG, "closeM802");
		String fortune;
		try {
			DatagramSocket socket;
			DatagramPacket packet;
			byte[] data = new byte[1024];
			data = "<CMD>CQUIT</CMD>".getBytes();

			socket = new DatagramSocket();

			packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 50039);
			socket.send(packet);

			packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			fortune = new String(packet.getData());
			Log.i(TAG, "closeM802 fortune = " + fortune);
			socket.close();
		} catch (UnknownHostException e) {
			Log.i(TAG, "Exception: host could not be found");
		} catch (Exception e) {
			Log.i(TAG, "Exception: " + e);
			e.printStackTrace();
		}
	}

	private void initMima() {
		status = (VpnManager.START_VPNSERVICE_RESULT_STARTING);
		Intent intent = new Intent("com.xdja.safeclient.VpnService");
		ComponentName ret = context.startService(intent);
		Log.i(TAG, "startService complete" + ret);
		if (ret != null) {
			status = (START_VPNSERVICE_RESULT_START_SUCCESS);
			sendMessage(START_VPNSERVICE_RESULT_START_SUCCESS);
			new Thread() {
				@Override
				public void run() {
					status = START_VPNSERVICE_RESULT_STARTING;
					int result = VpnMimaServer.start();
					status = (result);
					sendMessage(result);
				}
			}.start();
		} else {
			Log.i(TAG, "没有找到Vpn客户端");
			status = (START_VPNSERVICE_RESULT_NOT_FOUNT);
			sendMessage(START_VPNSERVICE_RESULT_NOT_FOUNT);
		}
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
