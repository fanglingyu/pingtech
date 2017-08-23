package com.pingtech.hgqw.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.utils.BasicNameValuePair;
import com.pingtech.hgqw.utils.SystemSetting;

public class HttpRequestUtils {
	
	public static final int GET_SHIP_BY_KK = 0;
	public static final int SAVE_XJ = 1853853770;//接口名称->十六进制->十进制->取后十位
	/**
	 * 获取卡口下的船舶
	 */
	public static void getShipByKK(OnHttpResult onHttpResult) {
		HashMap<String, Object> bindData = SystemSetting
				.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
		if(bindData==null){
			SystemSetting.setShipOfKK(null);
			return;
		}
		String url = "getShipByKK";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("kkID", SystemSetting
				.getVoyageNumber(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "")));
		NetWorkManager.request(onHttpResult, url, params, GET_SHIP_BY_KK);
	}
	/**
	 * 获取卡口下的船舶
	 */
	public static void saveXj(OnHttpResult onHttpResult , String hc , String userid) {
		String url = "saveXj";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("voyageNumber", hc));
		params.add(new BasicNameValuePair("userid", userid));
		NetWorkManager.request(onHttpResult, url, params, SAVE_XJ);
	}
}
