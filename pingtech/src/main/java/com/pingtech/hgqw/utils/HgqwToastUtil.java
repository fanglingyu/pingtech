package com.pingtech.hgqw.utils;

import com.pingtech.R;
import com.pingtech.hgqw.widget.HgqwToast;

public class HgqwToastUtil {

	/** 网络请求失败提示 */
	public static void requestNullToast() {
		HgqwToast.toast(R.string.data_download_failure_info);
	}

}
