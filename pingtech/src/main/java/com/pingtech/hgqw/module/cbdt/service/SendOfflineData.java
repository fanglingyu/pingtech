package com.pingtech.hgqw.module.cbdt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.utils.Log;

public class SendOfflineData {

	private static final String TAG = "SendOfflineData";

	public static Map<String, Object> isRequest() {
		Map<String, Object> map = null;
		OffDataService offDataService = new OffDataService();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		List<Integer> offLineIds = new ArrayList<Integer>();
		offDataService.offLineDataPackage(params, offLineIds);
		if (offLineIds == null || offLineIds.size() < 1) {
			Log.i(TAG, "offLineIds==null||offLineIds.size()<1");
			return null;
		}
		map = new HashMap<String, Object>();
		map.put("params", params);
		map.put("offLineIds", offLineIds);
		return map;
	}
}
