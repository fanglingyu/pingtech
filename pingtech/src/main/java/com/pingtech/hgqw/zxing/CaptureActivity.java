/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pingtech.hgqw.zxing;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.pingtech.R;
import com.pingtech.hgqw.widget.HgqwToast;
import com.pingtech.hgqw.zxing.client.android.camera.CameraManager;
import com.pingtech.hgqw.zxing.entity.MsTdc;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";

	private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";

	private static final String[] ZXING_URLS = { "http://zxing.appspot.com/scan", "zxing://scan/" };

	public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

	private CameraManager cameraManager;

	private CaptureActivityHandler handler;

	private Result savedResultToShow;

	private ViewfinderView viewfinderView;

	private TextView statusView;

	private View resultView;

	private boolean hasSurface;

	private Collection<BarcodeFormat> decodeFormats;

	private Map<DecodeHintType, ?> decodeHints;

	private String characterSet;

	private InactivityTimer inactivityTimer;

	private BeepManager beepManager;

	private AmbientLightManager ambientLightManager;

	// 闪光灯是否亮着
	private boolean isLight = false;

	View view;

	private TextView ligthtBtn;

	// private Dialog dialog;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture_zxing);

		/*
		 * dialog = new Dialog(CaptureActivity.this, R.style.MyDialog);
		 * dialog.setContentView(R.layout.dialog_zxing);
		 * dialog.setCanceledOnTouchOutside(false); dialog.show();
		 */

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		ligthtBtn = (TextView) findViewById(R.id.light_btn);
		setLightState();
		view = findViewById(R.id.light_layout);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cameraManager != null) {
					isLight = !isLight;
					cameraManager.setLight(isLight);
					setLightState();
					if (isLight) {
						view.setBackgroundResource(R.drawable.qb_scan_btn_flash_down);
					} else {
						view.setBackgroundResource(R.drawable.qb_scan_btn_flash_disable);
					}
				}
			}
		});
	}

	/**
	 * 
	 * @方法名：setLightState
	 * @功能说明：设置闪光灯状态显示
	 * @author zhaotf
	 * @date 2014-1-22 上午10:40:36
	 */
	private void setLightState() {
		if (isLight) {
			ligthtBtn.setText(Html.fromHtml("<font color='#2477ab'><b>" + getString(R.string.on) + "</b></font>"));
		} else {
			ligthtBtn.setText(Html.fromHtml("<font color='#ffffff'><b>" + getString(R.string.off) + "</b></font>"));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());
		cameraManager.setTorch(false);
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		resultView = findViewById(R.id.result_view);
		statusView = (TextView) findViewById(R.id.status_view);
		if (ligthtBtn == null) {
			ligthtBtn = (TextView) findViewById(R.id.light_btn);
		}
		isLight = false;
		setLightState();
		if (isLight) {
			view.setBackgroundResource(R.drawable.qb_scan_btn_flash_down);
		} else {
			view.setBackgroundResource(R.drawable.qb_scan_btn_flash_disable);
		}
		handler = null;
		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		surfaceView.setBackgroundColor(Color.TRANSPARENT);
		// surfaceView.setZOrderOnTop(true);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		Intent intent = getIntent();

		decodeFormats = null;
		characterSet = null;

		if (intent != null) {

			String action = intent.getAction();
			String dataString = intent.getDataString();

			if (Intents.Scan.ACTION.equals(action)) {

				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
				decodeHints = DecodeHintManager.parseDecodeHints(intent);

				if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if (width > 0 && height > 0) {
						cameraManager.setManualFramingRect(width, height);
					}
				}

				String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
				if (customPromptMessage != null) {
					statusView.setText(customPromptMessage);
				}

			} else if (dataString != null && dataString.contains(PRODUCT_SEARCH_URL_PREFIX) && dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {

				decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

			} else if (isZXingURL(dataString)) {

				Uri inputUri = Uri.parse(dataString);
				// new ScanFromWebPageManager(inputUri);
				decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
				// Allow a sub-set of the hints to be specified by the caller.
				decodeHints = DecodeHintManager.parseDecodeHints(inputUri);

			}

			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

		}
	}

	private static boolean isZXingURL(String dataString) {
		if (dataString == null) {
			return false;
		}
		for (String url : ZXING_URLS) {
			if (dataString.startsWith(url)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.setTorch(false);
		isLight = false;
		if (isLight) {
			view.setBackgroundResource(R.drawable.qb_scan_btn_flash_down);
		} else {
			view.setBackgroundResource(R.drawable.qb_scan_btn_flash_disable);
		}
		cameraManager.closeDriver();

		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			surfaceView.setBackgroundColor(Color.TRANSPARENT);
			// surfaceView.setZOrderOnTop(true);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (cameraManager != null) {
			cameraManager.closeDriver();
		}

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// IntentUtil.MyIntent(CaptureActivity.this, Constant.ZXING_MAIN,
			// "");
			Intent intent = new Intent();
			intent.putExtra(Constant.ZXING_DATA, "");
			intent.putExtra(Constant.ZXING_ISBACK, true);
			setResult(RESULT_OK, intent);
			finish();

			break;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
			// Use volume up/down to turn on light
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			// cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			// cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			if (requestCode == HISTORY_REQUEST_CODE) {
				int itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1);
				if (itemNumber >= 0) {
				}
			}
		}
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
		// dialog.cancel();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// dialog.cancel();
	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		Intent intent = new Intent();

		// System.out.println("sqqqq:"+s+"^^^^^:"+s.length());
		// System.out.println("^^^^^:"+s.length());
		// System.out.println("displayContents:"+displayContents+"  编码："+rawResult.getBarcodeFormat().toString()+"   类型："+resultHandler.getType().toString());
		// System.out.println("  编码："+rawResult.getBarcodeFormat().toString());
		// System.out.println("   类型："+resultHandler.getType().toString());
		// System.out.println("长度s："+s.length());
		// System.out.println("长度displayContents："+displayContents.length());
		String result = rawResult.getText();
		if (com.pingtech.hgqw.utils.StringUtils.isEmpty(result)) {
			result = "";
		}
		// else{
		// if(result.length()>1){
		// result = result.substring(0,result.length()-1);
		// }else{
		// result = "";
		// }
		// }
		// System.out.println("result:" + result);
		// intent.putExtra(Constant.ZXING_DATA, ScanDataUtil.getMsTdc(result));
		MsTdc msTdc = null;
		try {
			msTdc = ScanDataUtil.getMsTdcForAll(result);
		} catch (Exception e) {
			e.printStackTrace();
			msTdc = null;
			HgqwToast.toast("二维码格式不合法：" + result);
			handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			
		}
		if (msTdc != null) {
			intent.putExtra(Constant.ZXING_DATA, ScanDataUtil.getMsTdcForAll(result));
			intent.putExtra(Constant.ZXING_ISBACK, false);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 * We want the help screen to be shown automatically the first time a new
	 * version of the app is run. The easiest way to do this is to check
	 * android:versionCode from the manifest, and compare it to a value stored
	 * as a preference.
	 */

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		resultView.setVisibility(View.GONE);
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

}
