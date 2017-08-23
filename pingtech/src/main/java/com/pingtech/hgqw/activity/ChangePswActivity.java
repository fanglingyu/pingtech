package com.pingtech.hgqw.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.pingtech.R;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 
 * 修改密码界面的activity类
 */
public class ChangePswActivity extends MyActivity implements OnHttpResult {
	private static final String TAG = "ChangePswActivity";
	private static final int HTTPREQUEST_TYPE_FOR_CHANGEPSW = 1;

	private ProgressDialog progressDialog = null;
	private String httpReturnXMLInfo = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.changepassword);

		Log.i(TAG, "onCreate()");
		setMyActiveTitle(getString(R.string.system) + ">" + getString(R.string.changepassword));
		Button ok_btn = (Button) findViewById(R.id.submit);
		ok_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (progressDialog != null) {
					return;
				}
				String oldpsw = ((EditText) findViewById(R.id.oldpassword)).getText().toString();
				String newpsw = ((EditText) findViewById(R.id.newpassword)).getText().toString();
				String confirmpsw = ((EditText) findViewById(R.id.confirmpassword)).getText().toString();
				if (oldpsw.length() == 0) {
					HgqwToast.makeText(ChangePswActivity.this, R.string.old_password_empty, HgqwToast.LENGTH_LONG).show();
					return;
				}
				if (newpsw.length() == 0) {
					HgqwToast.makeText(ChangePswActivity.this, R.string.new_password_empty, HgqwToast.LENGTH_LONG).show();
					return;
				}
				if (!newpsw.equals(confirmpsw)) {
					HgqwToast.makeText(ChangePswActivity.this, R.string.new_password_diff, HgqwToast.LENGTH_LONG).show();
					return;
				}
				String str = "modifyPwd";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("oldPwd", oldpsw));
				params.add(new BasicNameValuePair("newPwd", newpsw));
				params.add(new BasicNameValuePair("userID", LoginUser.getCurrentLoginUser().getUserID()));
				progressDialog = new ProgressDialog(ChangePswActivity.this);
				progressDialog.setTitle(getString(R.string.waiting));
				progressDialog.setMessage(getString(R.string.waiting));
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
				NetWorkManager.request(ChangePswActivity.this, str, params, HTTPREQUEST_TYPE_FOR_CHANGEPSW);
			}
		});
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

	/** 解析修改密码返回的数据 */
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
						// 信息
						httpReturnXMLInfo = parser.nextText();
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

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHttpResult()httpRequestType:" + httpRequestType + ",result" + (str != null));
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (HTTPREQUEST_TYPE_FOR_CHANGEPSW == httpRequestType) {
			if (str != null) {
				if (onParseXMLData(str)) {
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(ChangePswActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(ChangePswActivity.this, R.string.change_password_success, HgqwToast.LENGTH_LONG)
								.show();
					}

					finish();
				} else {
					if (httpReturnXMLInfo != null) {
						HgqwToast.makeText(ChangePswActivity.this, httpReturnXMLInfo, HgqwToast.LENGTH_LONG).show();
					} else {
						HgqwToast.makeText(ChangePswActivity.this, R.string.change_password_failed, HgqwToast.LENGTH_LONG)
								.show();
					}
				}
			} else {
				HgqwToast.makeText(ChangePswActivity.this, R.string.logout_failure, HgqwToast.LENGTH_LONG).show();
			}
		}
		httpRequestType = 0;
	}
}
