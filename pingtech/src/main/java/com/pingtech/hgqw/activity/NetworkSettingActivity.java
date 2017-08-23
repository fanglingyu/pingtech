package com.pingtech.hgqw.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.base.entity.SystemSettingInfo;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.widget.HgqwToast;

/**
 * 网络设置界面的activity类
 * */

public class NetworkSettingActivity extends MyActivity {
	private static final String TAG = "NetworkSettingActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.networksetting);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Log.i(TAG, "onCreate()");
		setMyActiveTitle(R.string.network_setting);
		TextView curHost = (TextView) findViewById(R.id.currentaddr);
		curHost.setText(SystemSetting.getServerHost() + ":" + SystemSetting.getServerPort());
		if (SystemSetting.getServerHost() != null) {
			((EditText) findViewById(R.id.serverhost)).setText(SystemSetting.getServerHost());
		}
		if (SystemSetting.getServerKadm() != null) {
			((EditText) findViewById(R.id.server_kadm)).setText(SystemSetting.getServerKadm());
		}
		if (SystemSetting.getServerPort() != null) {
			((EditText) findViewById(R.id.server_comm)).setText(SystemSetting.getServerPort());
		}
		if (SystemSetting.getWebServiceNamespace() != null) {
			((EditText) findViewById(R.id.server_namespace)).setText(SystemSetting.getWebServiceNamespace());
		}
		if (SystemSetting.getWebServiceUserName() != null) {
			((EditText) findViewById(R.id.server_username)).setText(SystemSetting.getWebServiceUserName());
		}
		if (SystemSetting.getWebServicePassword() != null) {
			((EditText) findViewById(R.id.server_password)).setText(SystemSetting.getWebServicePassword());
		}
		if (SystemSetting.getWebServiceCode() != null) {
			((EditText) findViewById(R.id.server_code)).setText(SystemSetting.getWebServiceCode());
		}
		if (SystemSetting.getWebServiceWSDLUrl() != null) {
			((EditText) findViewById(R.id.server_wsdlurl)).setText(SystemSetting.getWebServiceWSDLUrl());
		}
		if (SystemSetting.getWebServiceArg1() != null) {
			((EditText) findViewById(R.id.server_arg1)).setText(SystemSetting.getWebServiceArg1());
		}
		if (SystemSetting.getWebServiceArg2() != null) {
			((EditText) findViewById(R.id.server_arg2)).setText(SystemSetting.getWebServiceArg2());
		}
		if (SystemSetting.getWebServiceArg3() != null) {
			((EditText) findViewById(R.id.server_arg3)).setText(SystemSetting.getWebServiceArg3());
		}
		if (SystemSetting.getWebServiceArg4() != null) {
			((EditText) findViewById(R.id.server_arg4)).setText(SystemSetting.getWebServiceArg4());
		}
		if (SystemSetting.getWebServiceArg5() != null) {
			((EditText) findViewById(R.id.server_arg5)).setText(SystemSetting.getWebServiceArg5());
		}
		((CheckBox) findViewById(R.id.webservice_cb)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (((CheckBox) findViewById(R.id.webservice_cb)).isChecked()) {
					findViewById(R.id.webservice_setting).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.webservice_setting).setVisibility(View.GONE);
				}
			}
		});
		((CheckBox) findViewById(R.id.webservice_cb)).setChecked(SystemSetting.getWebServiceConnect());
		if (((CheckBox) findViewById(R.id.webservice_cb)).isChecked()) {
			findViewById(R.id.webservice_setting).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.webservice_setting).setVisibility(View.GONE);
		}
		((Button) findViewById(R.id.networksetting_submit)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();

				EditText kadm = (EditText) findViewById(R.id.server_kadm);
				String kadmStr = kadm.getText().toString();

				EditText host = (EditText) findViewById(R.id.serverhost);
				String hoststr = host.getText().toString();
				EditText comm = (EditText) findViewById(R.id.server_comm);
				String portstr = comm.getText().toString();
				EditText namespace = (EditText) findViewById(R.id.server_namespace);
				String namespacestr = namespace.getText().toString();
				EditText username = (EditText) findViewById(R.id.server_username);
				String usernamestr = username.getText().toString();
				EditText password = (EditText) findViewById(R.id.server_password);
				String passwordstr = password.getText().toString();
				EditText code = (EditText) findViewById(R.id.server_code);
				String codestr = code.getText().toString();
				EditText wsdlurl = (EditText) findViewById(R.id.server_wsdlurl);
				String wsdlurlstr = wsdlurl.getText().toString();
				EditText arg1 = (EditText) findViewById(R.id.server_arg1);
				String arg1str = arg1.getText().toString();
				EditText arg2 = (EditText) findViewById(R.id.server_arg2);
				String arg2str = arg2.getText().toString();
				EditText arg3 = (EditText) findViewById(R.id.server_arg3);
				String arg3str = arg3.getText().toString();
				EditText arg4 = (EditText) findViewById(R.id.server_arg4);
				String arg4str = arg4.getText().toString();
				EditText arg5 = (EditText) findViewById(R.id.server_arg5);
				String arg5str = arg5.getText().toString();

				editor.putString(getString(R.string.server_kadm), kadmStr);

				editor.putString(getString(R.string.server_host), hoststr);
				editor.putString(getString(R.string.server_port), portstr);
				editor.putBoolean(getString(R.string.webservice_connect), ((CheckBox) findViewById(R.id.webservice_cb)).isChecked());
				editor.putString(getString(R.string.webservice_namespace), namespacestr);
				editor.putString(getString(R.string.webservice_username), usernamestr);
				editor.putString(getString(R.string.webservice_password), passwordstr);
				editor.putString(getString(R.string.webservice_code), codestr);
				editor.putString(getString(R.string.webservice_url), wsdlurlstr);
				editor.putString(getString(R.string.webservice_arg1), arg1str);
				editor.putString(getString(R.string.webservice_arg2), arg2str);
				editor.putString(getString(R.string.webservice_arg3), arg3str);
				editor.putString(getString(R.string.webservice_arg4), arg4str);
				editor.putString(getString(R.string.webservice_arg5), arg5str);
				editor.commit();
				SystemSetting.setServerKadm(kadmStr);
				SystemSetting.setServerHost(hoststr);
				SystemSetting.setServerPort(portstr);
				SystemSetting.setWebServiceConnect(((CheckBox) findViewById(R.id.webservice_cb)).isChecked());
				SystemSetting.setWebServiceNamespace(namespacestr);
				SystemSetting.setWebServiceUserName(usernamestr);
				SystemSetting.setWebServicePassword(passwordstr);
				SystemSetting.setWebServiceCode(codestr);
				SystemSetting.setWebServiceWSDLUrl(wsdlurlstr);
				SystemSetting.setWebServiceArg1(arg1str);
				SystemSetting.setWebServiceArg2(arg2str);
				SystemSetting.setWebServiceArg3(arg3str);
				SystemSetting.setWebServiceArg4(arg4str);
				SystemSetting.setWebServiceArg5(arg5str);

				SystemSettingInfo systemSettingInfo = new SystemSettingInfo();
				systemSettingInfo.setServerKadm(kadmStr);
				systemSettingInfo.setServerHost(hoststr);
				systemSettingInfo.setServerPort(portstr);
				systemSettingInfo.setWebServiceConnect(((CheckBox) findViewById(R.id.webservice_cb)).isChecked());
				systemSettingInfo.setWebServiceNamespace(namespacestr);
				systemSettingInfo.setWebServiceUserName(usernamestr);
				systemSettingInfo.setWebServicePassword(passwordstr);
				systemSettingInfo.setWebServiceCode(codestr);
				systemSettingInfo.setWebServiceWSDLUrl(wsdlurlstr);
				systemSettingInfo.setWebServiceArg1(arg1str);
				systemSettingInfo.setWebServiceArg2(arg2str);
				systemSettingInfo.setWebServiceArg3(arg3str);
				systemSettingInfo.setWebServiceArg4(arg4str);
				systemSettingInfo.setWebServiceArg5(arg5str);
				BaseApplication.instent.setSystemSettingInfo(systemSettingInfo);

				HgqwToast.makeText(NetworkSettingActivity.this, getString(R.string.save_success), HgqwToast.LENGTH_LONG).show();
				finish();
			}
		});
		((Button) findViewById(R.id.networksetting_cancel)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
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

	/* 监听物理按键 */
	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		return super.onKeyDown(keyCode, event , false);
//	}
}
