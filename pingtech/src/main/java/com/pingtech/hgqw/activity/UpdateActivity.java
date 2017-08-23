package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.Flags;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 软件升级activity类 */
public class UpdateActivity extends Activity implements OnHttpResult {
	private static final String TAG = "UpdateActivity";

	/** 下载的apk临时文件的保存绝对路径 */
	// private static final String TEMP_APK_ABSOLUTE_FILEPATH =
	// Environment.getExternalStorageDirectory().getPath() + File.separator
	// + "PingtechSystem.apk";
	private String TEMP_APK_ABSOLUTE_FILEPATH = null;;

	/** 消息指令类型，获取文件总size */
	private static final int MSG_TYPE_GET_FILEINFO = 1;

	/** 消息指令类型，获取文件总size出错 */
	private static final int MSG_TYPE_GET_FILEINFO_ERROR = 2;

	/** 消息指令类型，下载未完成，继续下载 */
	private static final int MSG_TYPE_CONTINUE_DOWNLOAD = 3;

	/** 消息指令类型，下载过程中发生错误 */
	private static final int MSG_TYPE_DOWNLOAD_ERROR = 4;

	/** 消息指令类型，下载完成 */
	private static final int MSG_TYPE_DOWNLOAD_COMPLETED = 5;

	/** 下载完成后安装APK */
	private static final int STARTACTIVITY_FOR_INSTALLAPK = 6;

	/** http type，下载apk文件 */
	private static final int HTTPREQUEST_TYPE_GET_APK = 7;

	/** http type，获取apk文件size */
	private static final int HTTPREQUEST_TYPE_GET_APK_LENGTH = 8;

	private ProgressDialog progressDialog;

	private int curLength;

	private int curLengthTemp;

	private int fileLength;

	public static boolean downloading;

	public boolean stopFlag = false;

	/**
	 * 区分版本：0默认版本，1哨兵版，2船方自管
	 */
	private String version = "";

	private String httpReturnXMLInfo = null;

	/** 是否强制升级，true强制升级，false不强制升级 */
	private boolean force = false;

	private String forceFlag = "false";

	/** 强制升级提示信息 */
	private String forceInfo = "";

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private String newApkName = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);// 屏保
		lock.disableKeyguard();
		downloading = false;
		Intent intent = getIntent();
		version = intent.getStringExtra("version");
		forceFlag = intent.getStringExtra("force");
		newApkName = intent.getStringExtra("Url");
		initFile();
		if ("true".equals(forceFlag)) {
			force = true;
		} else {
			force = false;
		}
		forceInfo = intent.getStringExtra("forceInfo");
		onShowUpdateDialog();
	}

	private void initFile() {
		String url = BaseApplication.instent.getLocalPath() + "updates" + File.separator;
		File fileUrlLocal = new File(BaseApplication.instent.getLocalPath());
		if (!fileUrlLocal.exists()) {
			fileUrlLocal.mkdirs();
		}

		File fileUrl = new File(url);
		if (!fileUrl.exists()) {
			fileUrl.mkdirs();
		}
		switch (Flags.PDA_VERSION) {
		case Flags.PDA_VERSION_CFZG:
			url += "cfzg";
			break;
		case Flags.PDA_VERSION_DEFAULT:
			url += "xuncha";
			break;
		case Flags.PDA_VERSION_SENTINEL:
			url += "shaobing";
			break;
		default:
			break;
		}
		if (!url.endsWith(File.separator)) {
			url += File.separator;
			File file = new File(url);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		url += newApkName;
		TEMP_APK_ABSOLUTE_FILEPATH = url;
	}

	/** 提示版本更新内容，询问用户是否升级 */
	private void onShowUpdateDialog() {
		Intent intent = getIntent();
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle(R.string.UpdateInfo_Title);
		if (force) {
			builer.setMessage(forceInfo);
			builer.setPositiveButton(getString(R.string.ok), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onDownLoadApk();
				}
			});
		} else {
			builer.setMessage(intent.getStringExtra("Description"));
			builer.setPositiveButton(getString(R.string.ok), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onDownLoadApk();
				}
			});
			builer.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					setResult(RESULT_OK, new Intent());
					finish();
				}
			});
		}
		builer.setCancelable(false);
		builer.show();

	}

	/** 用户选择同意下载， 显示下载进度条，并初始化变量及删除同名文件 */
	private void onDownLoadApk() {
		downloading = true;
		Intent intent = getIntent();
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		Resources res = getResources();
		progressDialog.setMessage(res.getText(R.string.UpdateInfo_Downloading));
		progressDialog.setMax(intent.getIntExtra("Size", 100));
		progressDialog.setCancelable(false);
		// progressDialog.setIndeterminate(false);
		progressDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				Log.i(TAG, "onDismiss");
				stopFlag = true;
				progressDialog = null;
			}
		});
		progressDialog.show();
		curLength = 0;
		fileLength = 0;
		/*
		 * File file = new File(TEMP_APK_ABSOLUTE_FILEPATH); if (file.exists())
		 * { file.delete(); }
		 */
		Message msg = new Message();
		msg.what = MSG_TYPE_GET_FILEINFO;
		handler.sendMessage(msg);
	}

	/** 从服务器获取文件数据 */
	private void getFileInfoFromServer(boolean getFileInfo, int curLength) {
		if (stopFlag) {
			return;
		}
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String str = "checkUpdate";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", "1"));
			params.add(new BasicNameValuePair("flag", curLength + ""));
			params.add(new BasicNameValuePair("count", 102403 + ""));
			params.add(new BasicNameValuePair("version", version));
			if (getFileInfo) {
				params.add(new BasicNameValuePair("longth", "0"));
				NetWorkManager.request(this, str, params, HTTPREQUEST_TYPE_GET_APK_LENGTH);
			} else {
				NetWorkManager.request(this, str, params, HTTPREQUEST_TYPE_GET_APK);
			}
		}
	}

	/** 下载完成后，安装apk */
	private void installApk() {
		Log.i(TAG, "installApk");
		try {
			File file = new File(TEMP_APK_ABSOLUTE_FILEPATH);
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(file);
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
			startActivityForResult(intent, STARTACTIVITY_FOR_INSTALLAPK);
		} catch (Exception e) {
			HgqwToast.makeText(getApplicationContext(), getString(R.string.install_new_file_error), HgqwToast.LENGTH_LONG).show();
		}
	}

	/**
	 * 从安装模块返回时，结束本activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		switch (requestCode) {
		case STARTACTIVITY_FOR_INSTALLAPK:
			Log.i(TAG, "onActivityResult,startactivity_for_installapk");
			if (resultCode == RESULT_OK) {

			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			finish();
		}
	}

	/** 处理下载返回状态 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_TYPE_GET_FILEINFO:
				getFileInfoFromServer(true, curLength);
				break;
			case MSG_TYPE_GET_FILEINFO_ERROR:
				downloading = false;
				if (httpReturnXMLInfo != null) {
					HgqwToast.makeText(getApplicationContext(), httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(getApplicationContext(), getString(R.string.get_file_info_error), HgqwToast.LENGTH_LONG).show();
				}
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				updateError();
				finish();
				break;
			case MSG_TYPE_DOWNLOAD_ERROR:
				getFileInfoFromServer(false, curLengthTemp);
				/*
				 * downloading = false;
				 * HgqwToast.makeText(getApplicationContext(),
				 * getString(R.string.download_new_file_error),
				 * HgqwToast.LENGTH_LONG).show(); if (progressDialog != null) {
				 * progressDialog.dismiss(); progressDialog = null; }
				 * updateError(); finish();
				 */
				break;
			case MSG_TYPE_CONTINUE_DOWNLOAD:
				getFileInfoFromServer(false, curLength);
				break;
			case MSG_TYPE_DOWNLOAD_COMPLETED:
				installNewFile();
				break;
			}
		}
	};

	/** 解析apk文件长度 */
	private boolean onParseXMLData(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							// size
							fileLength = Integer.parseInt(parser.nextText());
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @方法名：updateError
	 * @功能说明：升级失败返回结果
	 * @author liums
	 * @date 2014-1-20 下午3:54:37
	 */
	protected void updateError() {
		Intent intent = new Intent();
		intent.putExtra("version", version);
		intent.putExtra("isOk", false);
		intent.putExtra("force", force);
		intent.putExtra("forceInfo", forceInfo);
		setResult(RESULT_CANCELED, intent);
	}

	FileOutputStream fos = null;

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		Log.i(TAG, "onHttpResult:" + (str != null));
		if (httpRequestType == HTTPREQUEST_TYPE_GET_APK_LENGTH) {
			if (str != null && onParseXMLData(str)) {
				progressDialog.setMax(fileLength);
				File file = new File(TEMP_APK_ABSOLUTE_FILEPATH);
				if (file.exists()) {
					if (file.length() == fileLength) {// 已存在大小相同的文件，直接使用不下载
						installNewFile();
						return;
					} else {
						file.delete();
					}
				}
				Log.i(TAG, "onHttpResult:fileLength:" + fileLength);
				Message msg = new Message();
				msg.what = MSG_TYPE_CONTINUE_DOWNLOAD;
				handler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = MSG_TYPE_GET_FILEINFO_ERROR;
				handler.sendMessage(msg);
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_GET_APK) {
			if (stopFlag) {
				return;
			}
			if (str != null && str.length() > 0) {
				try {
					File file = new File(TEMP_APK_ABSOLUTE_FILEPATH);

					if (fos == null) {
						fos = new FileOutputStream(file, true);
					}
					byte[] buf = Base64.decode(str);
					int len = buf.length;
					fos.write(buf, 0, len);
					curLengthTemp = curLength;
					curLength += len;
					Log.i(TAG, "length:" + len + ",curLength:" + curLength);
					if (progressDialog != null) {
						progressDialog.setProgress(curLength);
					}
					Message msg = new Message();
					if (curLength < fileLength) {
						msg.what = MSG_TYPE_CONTINUE_DOWNLOAD;
					} else {
						msg.what = MSG_TYPE_DOWNLOAD_COMPLETED;
					}
					handler.sendMessage(msg);
				}/*
				 * catch (FileNotFoundException e) { Log.i(TAG,
				 * "FileNotFoundException:"); Message msg = new Message();
				 * msg.what = MSG_TYPE_DOWNLOAD_ERROR; handler.sendMessage(msg);
				 * e.printStackTrace(); try { if (fos != null) { fos.close(); }
				 * } catch (IOException e1) { e1.printStackTrace(); } } catch
				 * (IOException e) { Log.i(TAG, "IOException:"); Message msg =
				 * new Message(); msg.what = MSG_TYPE_DOWNLOAD_ERROR;
				 * handler.sendMessage(msg); e.printStackTrace(); try { if (fos
				 * != null) { fos.close(); } } catch (IOException e1) {
				 * e1.printStackTrace(); } }
				 */catch (Exception e) {
					Log.i(TAG, "Exception:");
					Message msg = new Message();
					msg.what = MSG_TYPE_DOWNLOAD_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				Message msg = new Message();
				msg.what = MSG_TYPE_DOWNLOAD_ERROR;
				handler.sendMessage(msg);
			}
		}
	}

	private void installNewFile() {
		try {
			if (fos != null) {
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		downloading = false;
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		installApk();
	}

	@Override
	protected void onResume() {
		stopFlag = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

}