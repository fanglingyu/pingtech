package com.pingtech.hgqw.module.licence.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.Handler;

public class LicenceService extends AsyncTask<String, String[], String> {
	private Handler handler;
	public LicenceService(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			getLicenceFile(params);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		
	}

	private String getLicenceFile (String[] params) throws IOException {
		String uri = "http://10.10.2.202:9080/hgqw/activex/pda/default/update.xml";
		File file = new File(uri);
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		fileInputStream.read(buffer);
		return null;
	}
}
