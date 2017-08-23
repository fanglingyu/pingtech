package com.pingtech.hgqw.module.home.utils;

import android.content.Context;
import android.content.Intent;

import com.pingtech.hgqw.module.qwjw.activity.QwjwActivity;

public class SkipUtil {

	public static void skipToQwjw(Context context) {
		Intent intent = new Intent(context, QwjwActivity.class);
		context.startActivity(intent);
	}

}
