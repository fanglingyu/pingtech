package com.pingtech.hgqw.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.offline.pzxx.request.RequestPzxx;
import com.pingtech.hgqw.module.tikou.entity.PersonInfo;
import com.pingtech.hgqw.widget.CustomTextView;

public class ShowViewUtil {
	public static void showPzbjxx(PersonInfo personInfo, Activity activity , int from, boolean isOffline) {
		// 显示报警提示信息detail_pzmbly
		if (personInfo != null && StringUtils.isNotEmpty(personInfo.getPzmbly())) {
			TextView textViewTitle = (TextView) activity.findViewById(R.id.detail_btpzmbly);
			TextView textViewNr = (TextView) activity.findViewById(R.id.detail_pzmbly);
			CustomTextView detail_pzbjtsxx = (CustomTextView) activity.findViewById(R.id.detail_pzbjtsxx);
			if (textViewTitle != null) {
				textViewTitle.setVisibility(View.VISIBLE);
			}
			if (textViewNr != null) {
				textViewNr.setVisibility(View.VISIBLE);
			}
			if (detail_pzbjtsxx != null) {
//				detail_pzbjtsxx.setVisibility(View.VISIBLE);
			}

			TextView textView = (TextView) activity.findViewById(R.id.detail_pzmbly);
			// String source = "<font color=\"#ff0000\">" +
			// tempPersonInfo.getPzmbly() + "</font>";
			if (textView != null) {
				textView.setText(Html.fromHtml(personInfo.getPzmbly()));
			}
			
			if (detail_pzbjtsxx != null) {
				detail_pzbjtsxx.setText(personInfo.getBjtsxx());
			}
			
			if(isOffline){
				new RequestPzxx().requestSendMessageForBjts(personInfo , from);
			}
			showDialog(personInfo, activity);
		} else {
			TextView textViewTitle = (TextView) activity.findViewById(R.id.detail_btpzmbly);
			TextView textViewNr = (TextView) activity.findViewById(R.id.detail_pzmbly);
			CustomTextView detail_pzbjtsxx = (CustomTextView) activity.findViewById(R.id.detail_pzbjtsxx);
			if (textViewTitle != null) {
				textViewTitle.setVisibility(View.GONE);
			}
			if (textViewNr != null) {
				textViewNr.setVisibility(View.GONE);
			}
			if (detail_pzbjtsxx != null) {
				detail_pzbjtsxx.setVisibility(View.GONE);
			}

		}
	}

	private static void showDialog(PersonInfo personInfo, Activity activity ) {
		AlertDialog.Builder builder = new Builder(activity);
		String name = personInfo.getName();
		String message = BaseApplication.instent.getString(R.string.pzbj_message);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		String title = "提示";/* "<font color=\"#ff0000\">" + "提示" + "</font>";*/
		builder.setTitle(Html.fromHtml(title));
		builder.setMessage(name + message);
		builder.setNegativeButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create();
		builder.show();
	}

}
