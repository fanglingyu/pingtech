package com.pingtech.hgqw.service;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.NameValuePair;

import android.os.Handler;
import android.util.Log;

import com.pingtech.hgqw.entity.RequestFlags;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.PullXmlImageUtils;
import com.pingtech.hgqw.web.ThreadPool;
import com.pingtech.hgqw.web.WebService;
import com.pingtech.hgqw.web.request.RequestImageDownload;

public class ImageDownload {
	private final String TAG = "ImageDownload";

	private Future future = null;

	private boolean isContinue = true;

	private Handler handler = null;

	private String res = null;

	private int againCount = 3;

	public ImageDownload(Handler handler) {
		this.handler = handler;
	}

	public Future startDownload() {
		ThreadPool threadPool = ThreadPool.getInstance();
		future = threadPool.addTask(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "run()");
				request();
			}

		});
		return future;
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
			Log.i(TAG, "(StringUtils.isEmpty(res),againCount=" + againCount);
			if (againCount <= 0) {
				handler.obtainMessage(-100).sendToTarget();
				return;
			}
			againCount--;
			request();
			return;
		}
		if ("OK".equals(res)) {
			Log.i(TAG, "res ok");
			handler.obtainMessage(100).sendToTarget();
			return;
		}
		PullXmlImageUtils.pullXml(res);
		Log.i(TAG, "res not ok,next request");
		request();

	}

	public void stop() {
		isContinue = false;
		future.cancel(true);
	}

}
