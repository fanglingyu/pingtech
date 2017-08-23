package com.pingtech.hgqw.service;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.NameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.RequestFlags;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.PullXmlImageUtils;
import com.pingtech.hgqw.web.ThreadPool;
import com.pingtech.hgqw.web.WebService;
import com.pingtech.hgqw.web.request.RequestImageDownload;

public class ImageDownloadService extends Service {
	private static final String TAG = "ImageDownloadService";

	private Future future = null;

	private boolean isContinue = true;

	private String res = null;

	private int againCount = 0;

	private int count = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		startDownload();
	}

	public void startDownload() {
		ThreadPool threadPool = ThreadPool.getInstance();
		future = threadPool.addTask(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "run()");
				BaseApplication.instent.imageDownlond = true;
				request();
			}

		});
	}

	private void request() {
		res = null;
		if (!isContinue) {
			Log.i(TAG, "!isContinue");
			return;
		}
		List<NameValuePair> params = RequestImageDownload.getParams();
		res = WebService.request(RequestFlags.DOWNLOAD_IMAGE, params);
		// FileUtils.result2XMLFile(RequestFlags.DOWNLOAD_IMAGE, res);
		if (StringUtils.isEmpty(res)) {
			
			againCount++;
			Log.i(TAG, "(StringUtils.isEmpty(res),againCount=" + againCount);
			if (againCount >= 20) {
				 return;
			}
			request();
			return;
		}
		if ("OK".equals(res)) {
			Log.i(TAG, "res ok");
			// handler.obtainMessage(100).sendToTarget();
			BaseApplication.instent.imageDownlond = false;
			if (SynchDataService.handler != null) {
				SynchDataService.handler.obtainMessage(100000).sendToTarget();
			}
			return;
		}
		Log.i(TAG, "res length = " + res.length());
		List<Hgzjxx> hgzjxxs = PullXmlImageUtils.pullXml(res);
		if (hgzjxxs != null) {
			count += hgzjxxs.size();
			Log.i(TAG, "count=" + count);
		}
		Log.i(TAG, "ImageDownloadService *** res not ok,next request");
		request();

	}

	protected void updateHgzjxxs(List<Hgzjxx> hgzjxxs) {
		HgzjxxService hgzjxxService = new HgzjxxService();
		hgzjxxService.updateList(hgzjxxs);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		isContinue = false;
		future.cancel(true);
		BaseApplication.instent.imageDownlond = false;
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

}
