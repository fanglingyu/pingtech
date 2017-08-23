package com.pingtech.hgqw.base.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;

public class BaseDialogUtils {
	public static void dismissRequestDialog() {
		if (BaseApplication.instent.progressDialog != null) {
			BaseApplication.instent.progressDialog.dismiss();
			BaseApplication.instent.progressDialog = null;
		}
	}

	public static void showRequestDialog(Context context, boolean isFromOffline) {
		BaseApplication.instent.progressDialog = new ProgressDialog(context , R.style.MyDialog);
		 BaseApplication.instent.progressDialog.setTitle(BaseApplication.instent.getString(R.string.waiting));
		 if(isFromOffline){
			 BaseApplication.instent.progressDialog.setMessage(BaseApplication.instent.getString(R.string.no_data_request_web));
		 }else{
			 BaseApplication.instent.progressDialog.setMessage(BaseApplication.instent.getString(R.string.waiting));
		 }
		 BaseApplication.instent.progressDialog.setCancelable(true);
		 BaseApplication.instent.progressDialog.setIndeterminate(false);
		 BaseApplication.instent.progressDialog.show();
	}
}
