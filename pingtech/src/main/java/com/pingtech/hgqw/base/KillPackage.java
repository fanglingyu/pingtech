package com.pingtech.hgqw.base;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

public class KillPackage {
	private static final String TAG = "KillPackage";

	private static Process process = null;

	public static boolean kill(String packageName) {
		Log.i(TAG, "kill");
		init();
		killProcess(packageName);
		close();
		return false;
	}

	private static void close() {
		Log.i(TAG, "close");
		if (process != null) {
			Log.i(TAG, "close process != null");
			try {
				process.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			process = null;
		}
	}

	private static void killProcess(String packageName) {
		OutputStream outputStream = process.getOutputStream();
//		String cmd = "am force-stop " + packageName + " \n";
		String cmd = "mkdir /mnt/sdcard/aaaaaa/";
		try {
			Log.i(TAG, "killProcess cmd=" + cmd);
			outputStream.write(cmd.getBytes());
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "killProcess Exception e");
		}
	}

	private static void init() {
		if (process == null) {
			Log.i(TAG, "init process == null");
			try {
				process = Runtime.getRuntime().exec("sh");
			} catch (IOException e) {
				e.printStackTrace();
				Log.i(TAG, "init IOException e");
			}
		}
	}

}
