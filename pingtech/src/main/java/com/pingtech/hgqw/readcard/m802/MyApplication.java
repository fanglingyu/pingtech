package com.pingtech.hgqw.readcard.m802;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.util.Log;

public class MyApplication extends Application {
	private String rootPath;


	public String getRootPath() {
		return rootPath;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setRootPath();
		copyDataFromAssets();
	}

	private void setRootPath() {
		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			rootPath = info.applicationInfo.dataDir;
			Log.i("rootPath", "################rootPath=" + rootPath);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void copyDataFromAssets() {

		String path = rootPath + File.separator + "wltlib";
		File myFile = new File(path);
		if (!myFile.exists()) {
			myFile.mkdir();
		}

		String basePath = path + File.separator + "base.dat";
		File baseFile = new File(basePath);
		if (!baseFile.exists()) {
			try {
				baseFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			copyFile("base.dat", baseFile);
		}

		String licensePath = path + File.separator + "license.lic";
		File licenseFile = new File(licensePath);
		if (!licenseFile.exists()) {
			try {
				licenseFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			copyFile("license.lic", licenseFile);
		}
	}

	private void copyFile(String fileName, File file) {
		AssetManager manage = getAssets();
		try {
			InputStream in = manage.open(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[512];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
