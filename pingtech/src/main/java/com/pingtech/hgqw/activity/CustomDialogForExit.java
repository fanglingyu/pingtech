package com.pingtech.hgqw.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.pingtech.R;
import com.pingtech.hgqw.entity.FlagUrls;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.pullxml.PullXmlUrgencyWarningInfo;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;
import com.pingtech.hgqw.widget.HgqwToast;

public class CustomDialogForExit extends Activity implements OnHttpResult {
	/**
	 * 中英文标识：cn=true中文，cn=false英文。
	 */
	private boolean cn = true;

	private ProgressDialog progressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog_for_exit);
		cn = true;
		if (cn) {
			findViewById(R.id.custom_dialog_layout_cn).setVisibility(View.VISIBLE);
			findViewById(R.id.custom_dialog_layout_en).setVisibility(View.GONE);
		} else {
			findViewById(R.id.custom_dialog_layout_cn).setVisibility(View.GONE);
			findViewById(R.id.custom_dialog_layout_en).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @方法名：buttonClick
	 * @功能说明：按钮点击事件
	 * @author liums
	 * @date 2013-5-7 下午4:35:13
	 * @param v
	 */
	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.custom_dialog_Confirm_en:
			String password = ((EditText)findViewById(R.id.custom_dialog_for_exit_password)).getText().toString();
			Intent intent = new Intent();
			intent.putExtra("password", password);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.custom_dialog_Cancel_en:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * @方法名：sendAlarmInfo
	 * @功能说明：发送紧急报警信息
	 * @author liums
	 * @param hc
	 * @date 2013-5-3 下午5:37:19
	 */
	private void sendAlarmInfo() {

		if (progressDialog != null) {
			return;
		}
		HashMap<String, Object> _BindShipShipInfo = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_SHIPSTATUS + "");
		String url = "urgencyWarningInfo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hc", (String) _BindShipShipInfo.get("hc")));
		params.add(new BasicNameValuePair("userid", LoginUser.getCurrentLoginUser().getUserID()));
		progressDialog = new ProgressDialog(CustomDialogForExit.this);
		progressDialog.setTitle(getString(R.string.waiting));
		progressDialog.setMessage(getString(R.string.waiting));
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		NetWorkManager.request(CustomDialogForExit.this, url, params, FlagUrls.URGENCY_WARNING_INFO);
	}

	/* 监听物理按键 */
	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 返回键
			break;
		case KeyEvent.KEYCODE_HOME:
			return false;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		switch (httpRequestType) {
		case FlagUrls.URGENCY_WARNING_INFO:
			if (str != null) {
				try {
					if (PullXmlUrgencyWarningInfo.pullXml(str)) {
						if (cn) {
							HgqwToast.getToastView(getApplicationContext(), "发送成功！").show();
						} else {
							HgqwToast.getToastView(getApplicationContext(), "Success!").show();
						}

					} else {
						if (cn) {
							HgqwToast.getToastView(getApplicationContext(), "发送失败，请稍后再试！").show();
						} else {
							HgqwToast.getToastView(getApplicationContext(), "error!").show();
						}
					}
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (cn) {
					HgqwToast.getToastView(getApplicationContext(), getString(R.string.data_download_failure_info)).show();
				} else {
					HgqwToast.getToastView(getApplicationContext(), "error!").show();

				}
			}
			finish();
			break;
		}
	}
}