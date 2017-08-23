package com.pingtech.hgqw.module.login.utils;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.DeviceUtils;

public class LoginUtil {

	private static final String TAG = "LoginUtil";

	public static void about(Context context, Handler handler) {
		AlertDialog.Builder builder = new Builder(context);
		// builder.setTitle("关于");
		View view = LayoutInflater.from(context).inflate(R.layout.about, null);

		builder.setView(view);
		builder.create();
		builder.show();
		initDate(view, context);
	}

	private static void initDate(View view, Context context) {
		TextView version = (TextView) view.findViewById(R.id.version);
		String versionStr = BaseApplication.instent.getVersionName();
		version.setText(versionStr);

		TextView imei = (TextView) view.findViewById(R.id.imei);
		String imeiStr = DeviceUtils.getIMEI();
		imei.setText(imeiStr);
	}

	public static void sing(){
		try {
			PackageInfo packageInfo = BaseApplication.instent.getPackageManager().getPackageInfo("com.pingtech", PackageManager.GET_SIGNATURES);
			Signature[] signatureArr = packageInfo.signatures;
			Signature signature = signatureArr[0];
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
			String pubKey = x509Certificate.getPublicKey().toString();
			String singNumber = x509Certificate.getSerialNumber().toString();
			Log.i(TAG, "pubKey="+pubKey+",singNumber="+singNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
