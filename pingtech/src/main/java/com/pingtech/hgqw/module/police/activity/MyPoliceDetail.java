package com.pingtech.hgqw.module.police.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.activity.MyActivity;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.police.entity.MyPolice;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.module.police.utils.PullXmlMyPolice;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/** 我的警务详情界面的activity类 */
public class MyPoliceDetail extends MyActivity implements OnHttpResult {
	private static final String TAG = "MyPoliceDetail";

	/** 发起获取警务详情的http请求type */
	private static final int HTTPREQUEST_TYPE_FOR_GETDICTATE = 1;

	/** 发送签收指令的http请求type */
	private static final int HTTPREQUEST_TYPE_FOR_SENDQIANSHOU = 2;

	/** 发送反馈的http请求type（暂时不需要该功能） */
	private static final int HTTPREQUEST_TYPE_FOR_SENDFEEDBACK = 3;

	/** 发送查看警务的http请求type */
	private static final int HTTPREQUEST_TYPE_FOR_SENDVIEW = 4;

	/** 警务id */
	private String taskId;

	private MyPolice myPolice = null;

	private ProgressDialog progressDialog = null;

	private boolean unRead;

	private String jwzldwid = "";

	private String cjfzr = "";

	private String cjlx = "";

	private String dwid = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.policedictate);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle(getString(R.string.mypolice) + ">" + getString(R.string.policedetail));
		
		Intent intent = getIntent();
		taskId = intent.getStringExtra("taskid");
		myPolice = (MyPolice) intent.getSerializableExtra("myPolice");

		if (myPolice != null) {
			jwzldwid = myPolice.getJwzldwid();
			dwid = myPolice.getDwid();
			cjfzr = myPolice.getCjfzr();
			cjlx = myPolice.getCjlx();
		}
		unRead = intent.getBooleanExtra("unread", true);
		if (intent.getBooleanExtra("new", true)) {
			findViewById(R.id.oldpolicedictate).setVisibility(View.GONE);
			((Button) findViewById(R.id.policedictate_submit)).setText(R.string.qianshou);
			((Button) findViewById(R.id.policedictate_submit)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					new RequestPolice(MyPoliceDetail.this, handler).requestSignMyTask(taskId, dwid, "JWZL", jwzldwid, cjfzr , cjlx);
				}
			});
		} else {
			Button policedictate_submit_button = (Button) findViewById(R.id.policedictate_submit);
			if (myPolice.getZlzt() != null && "1".equals(myPolice.getZlzt())) {
				policedictate_submit_button.setText(R.string.canceled);
			} else if (myPolice.getQszt() != null && !"0".equals(myPolice.getQszt())) {
				policedictate_submit_button.setText(R.string.yiqianshou);
				policedictate_submit_button.setEnabled(false);
			}

			findViewById(R.id.policedictate_radio).setVisibility(View.GONE);
			findViewById(R.id.policedictate_radio_text).setVisibility(View.GONE);
			((Button) findViewById(R.id.policedictate_submit)).setEnabled(false);
			findViewById(R.id.policedictate_feedback).setVisibility(View.GONE);
		}
//		if (myPolice.getZlnr() != null && !"".equals(myPolice.getZlnr())) {
			((TextView) findViewById(R.id.jqlb)).setText(Html.fromHtml(getString(R.string.jqlb) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getJqlb() == null ? "" : myPolice.getJqlb()) + "</font>"));
			((TextView) findViewById(R.id.fbr)).setText(Html.fromHtml(getString(R.string.fbr) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getFbr() == null ? "" : myPolice.getFbr()) + "</font>"));
			((TextView) findViewById(R.id.fbsj)).setText(Html.fromHtml(getString(R.string.fbsj) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getFbsj() == null ? "" : myPolice.getFbsj()) + "</font>"));
			((TextView) findViewById(R.id.cjfzr)).setText(Html.fromHtml(getString(R.string.cjfzr) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getCjfzr() == null ? "" : myPolice.getCjfzr()) + "</font>"));
			((TextView) findViewById(R.id.pzr)).setText(Html.fromHtml(getString(R.string.pzr) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getPzr() == null ? "" : myPolice.getPzr()) + "</font>"));
			((TextView) findViewById(R.id.zlnr)).setText(Html.fromHtml(getString(R.string.zlnr) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getZlnr() == null ? "" : myPolice.getZlnr()) + "</font>"));
			if (unRead) {
				onSendViewTaskMsg();
			}
//		} else {
//			onLoadPolicedictate();
//		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RequestPolice.SIGN_MY_TASK_SUCCESS:// 签收成功
				HgqwToast.makeText(MyPoliceDetail.this, R.string.qianshou_success, HgqwToast.LENGTH_LONG).show();
				Intent data = new Intent();
				setResult(RESULT_OK, data);
				finish();
				break;
			default:
				break;
			}
		}

	};

	private void onSendViewTaskMsg() {
		new RequestPolice(MyPoliceDetail.this, handler).requestSendViewTaskMsg(taskId, dwid, "JWZL", jwzldwid, cjfzr , cjlx);

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		Log.i(TAG, "dispatchKeyEvent,keycode=" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}
		if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/** 如果指令内容为空，就发起请求警务指令详情 */
	private void onLoadPolicedictate() {
		// TODO Auto-generated method stub
		String url = "getDictateInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("taskid", taskId));
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(this, url, params, HTTPREQUEST_TYPE_FOR_GETDICTATE);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (httpRequestType == HTTPREQUEST_TYPE_FOR_GETDICTATE) {
			if (str != null) {
				myPolice = PullXmlMyPolice.onParseXMLData(str);
			}
			if (!myPolice.isResult()) {
				if (myPolice.getInfo() != null && !"".equals(myPolice.getInfo())) {
					HgqwToast.makeText(MyPoliceDetail.this, myPolice.getInfo(), HgqwToast.LENGTH_LONG).show();
				} else {
					HgqwToast.makeText(MyPoliceDetail.this, R.string.data_download_failure_info, HgqwToast.LENGTH_LONG).show();
				}
			}
			((TextView) findViewById(R.id.jqlb)).setText(Html.fromHtml(getString(R.string.jqlb) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getJqlb() == null ? "" : myPolice.getJqlb()) + "</font>"));
			((TextView) findViewById(R.id.fbr))
					.setText(Html.fromHtml(getString(R.string.fbr) + "：" + "<font color=\"#acacac\">" + myPolice.getFbr() == null ? "" : myPolice
							.getFbr() + "</font>"));
			((TextView) findViewById(R.id.fbsj)).setText(Html.fromHtml(getString(R.string.fbsj) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getFbsj() == null ? "" : myPolice.getFbsj()) + "</font>"));
			((TextView) findViewById(R.id.cjfzr)).setText(Html.fromHtml(getString(R.string.cjfzr) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getCjfzr() == null ? "" : myPolice.getCjfzr()) + "</font>"));
			((TextView) findViewById(R.id.pzr)).setText(Html.fromHtml(getString(R.string.pzr) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getPzr() == null ? "" : myPolice.getPzr()) + "</font>"));
			((TextView) findViewById(R.id.zlnr)).setText(Html.fromHtml(getString(R.string.zlnr) + "：" + "<font color=\"#acacac\">"
					+ (myPolice.getZlnr() == null ? "" : myPolice.getZlnr()) + "</font>"));
			if (unRead) {
				onSendViewTaskMsg();
			}
			httpRequestType = 0;
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_SENDQIANSHOU) {
			if (str != null && str.equals("1")) {
				HgqwToast.makeText(MyPoliceDetail.this, R.string.qianshou_success, HgqwToast.LENGTH_LONG).show();
				Intent data = new Intent();
				setResult(RESULT_OK, data);
				finish();
			} else {
				HgqwToast.makeText(MyPoliceDetail.this, R.string.qianshou_failure, HgqwToast.LENGTH_LONG).show();
			}
		} else if (httpRequestType == HTTPREQUEST_TYPE_FOR_SENDFEEDBACK) {
			if (str != null && str.equals("1")) {
				HgqwToast.makeText(MyPoliceDetail.this, R.string.save_success, HgqwToast.LENGTH_LONG).show();
				Intent data = new Intent();
				setResult(RESULT_OK, data);
				finish();
			} else {
				HgqwToast.makeText(MyPoliceDetail.this, R.string.save_failure, HgqwToast.LENGTH_LONG).show();
			}
		}
	}

}
