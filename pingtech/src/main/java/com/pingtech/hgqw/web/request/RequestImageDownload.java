package com.pingtech.hgqw.web.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import com.pingtech.hgqw.module.bindplace.utils.SharedPreferencesUtil;
import com.pingtech.hgqw.utils.BasicNameValuePair;

public class RequestImageDownload {

	public static List<NameValuePair> getParams() {
		// 查看本地图片缓存审计ID
		String imgSjid = SharedPreferencesUtil.getImgSjid();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sjid", imgSjid));
		return params;
	}

	private static String buildZjhms(List<String> zjhms) {
		if (zjhms == null || zjhms.isEmpty()) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : zjhms) {
			stringBuilder.append(string);
			stringBuilder.append("_");
		}
		return stringBuilder.toString();
	}

}
