package com.pingtech.hgqw.service;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.pingtech.hgqw.utils.Log;
import com.softsz.deviceInterface.DeviceGPSInterface;
import com.softsz.deviceInterface.ListenerBDGPSInfoInterface;
import com.softsz.deviceInterface.LocationGPSData;

@SuppressLint("Instantiatable")
public class Pa8GPSListener {
	protected static final String TAG = "Pa8GPSListener";

	private DeviceGPSInterface gps = null;

	private Context context;

	private Handler handler;

	private ServiceConnection gpsConnect = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			gps = null;
			Log.i(TAG, "gps onServiceDisconnected");

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "gps onServiceConnected");
			gps = DeviceGPSInterface.Stub.asInterface(service);
			try {
				gps.startGPS();// 1
				// gps.startGPSFromBackGround(true, 30);// 2
				gps.setGPSListener(changelister);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	private ListenerBDGPSInfoInterface.Stub changelister = new ListenerBDGPSInfoInterface.Stub() {

		@Override
		public void changeGpsInfo(LocationGPSData locationGPSData) throws RemoteException {
			if (handler != null) {
				handler.obtainMessage(-1000, locationGPSData).sendToTarget();
			}
//			if (locationGPSData != null) {
//				Log.i(TAG, "changeGpsInfo=" + locationGPSData);
//			} else {
//				Log.i(TAG, "locationGPSData == null");
//
//			}
		}
	};

	public Pa8GPSListener(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	protected Location reBuildLocation(LocationGPSData locationGPSData) {
		if (locationGPSData == null) {
			Log.i(TAG, "locationGPSData==null");
			return null;
		}
		return null;
	}

	public void onCreate() {
		Log.i(TAG, "onCreate");
		context.bindService(new Intent("com.softsz.GPSACTION"), gpsConnect, Context.BIND_AUTO_CREATE);
	}

	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		try {
			if (gps != null) {
				gps.stopGPS();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		context.unbindService(gpsConnect);
	}
}
