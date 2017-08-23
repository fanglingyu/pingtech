package com.pingtech.hgqw.module.offline.pzxx.request;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.module.offline.pzxx.entity.Pzxx;
import com.pingtech.hgqw.module.offline.pzxx.service.PzxxService;
import com.pingtech.hgqw.module.offline.pzxx.utils.PullXmlUtil;
import com.pingtech.hgqw.module.offline.util.OffLineUtil;
import com.pingtech.hgqw.module.tikou.entity.PersonInfo;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.web.NetWorkManager;

public class RequestPzxx implements OnHttpResult {
	private static final String SENT_OFFLINE_BJTS = "sentOffLineBjts";

	private static final String SEND_MESSAGE_FOR_BJTS = "sendMessageForBjts";

	public static final int HTTPREQUEST_TYPE_FOR_SEND_MESSAGE_FOR_BJTS = 10010;

	private static final String TAG = "RequestPzxx";

	@Override
	public void onHttpResult(String str, int httpRequestType) {
		switch (httpRequestType) {
		case OffLineUtil.RESULT_CODE_SENT_OFFLINE_PZXX:
			sentOfflineBjts(str);
			break;

		default:
			break;
		}
	}

	private void sentOfflineBjts(String str) {
		if (StringUtils.isEmpty(str)) {
			Log.i(TAG, "isEmpty(str)");
			return;
		}

		try {
			ArrayList<Pzxx> pzxxs = PullXmlUtil.pullXml(str);
			if (pzxxs == null || pzxxs.isEmpty()) {
				Log.i(TAG, "pzxxs.isEmpty()");
				return;
			}
			Log.i(TAG, "pzxxs.isNotEmpty(),size=" + pzxxs.size());
			PzxxService pzxxService = new PzxxService();
			pzxxService.deleteAll();
			pzxxService.insertList(pzxxs);

		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Exception" + e.getMessage());
		}

	}

	public void request() {
		Log.i(TAG, "request");
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userID", BaseApplication.instent.gainUserID()));
		params.add(new BasicNameValuePair("pdacode", DeviceUtils.getIMEI()));
		NetWorkManager.request(this, SENT_OFFLINE_BJTS, params, OffLineUtil.RESULT_CODE_SENT_OFFLINE_PZXX);
	}

	public void requestSendMessageForBjts(PersonInfo personInfo, int from) {
		Log.i(TAG, "requestSendMessageForBjts");
		if (personInfo == null || StringUtils.isEmpty(personInfo.getZjhm())) {
			Log.i(TAG, "personInfo == null || StringUtils.isEmpty(personInfo.getZjhm())");
			return;
		}
		Pzxx pzxx = null;
		try {
			pzxx = new PzxxService().getPzxxByZjhm(personInfo.getZjhm());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (pzxx == null) {
			Log.i(TAG, "pzxx==null");
			return;
		}

		// ("pzjlryId");//碰撞记录人员id
		// ("zqryXm");//执勤人员姓名
		// ("zqdxLx");//执勤对象类型:船舶：cb 区域:qy 码头：mt 泊位：bw 浮筒：ft 锚地：md
		// ("zqdxMc");//执勤对象名称
		// ("pzjlryXm");//碰撞记录人员姓名
		// ("dxbs");//短信标志
		HashMap<String, String> map = getParamsForPzxx(from);
		String zqdxLx = "";
		String zqdxMc = "";
		if (map != null) {
			zqdxLx = map.get("zqdxLx");
			zqdxMc = map.get("zqdxMc");
		}

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pzjlryId", pzxx.getPzjlryId()));
		params.add(new BasicNameValuePair("zqryXm", BaseApplication.instent.getUserInfo().getName()));
		params.add(new BasicNameValuePair("zqdxLx", zqdxLx));
		params.add(new BasicNameValuePair("zqdxMc", zqdxMc));
		params.add(new BasicNameValuePair("pzjlryXm", pzxx.getXm()));
		params.add(new BasicNameValuePair("dxbs", pzxx.getDxbs()));
		NetWorkManager.request(this, SEND_MESSAGE_FOR_BJTS, params, HTTPREQUEST_TYPE_FOR_SEND_MESSAGE_FOR_BJTS);

	}

	private HashMap<String, String> getParamsForPzxx(int from) {
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, Object> bindData = SystemSetting.getBindShip(from + "");
		// SystemSetting.xunJianId;
		// ("zqdxLx");//执勤对象类型:船舶：cb 区域:qy 码头：mt 泊位：bw 浮筒：ft 锚地：md
		// ("zqdxMc");//执勤对象名称
		if (bindData != null) {
			map.put("zqdxLx", "cb");
			map.put("zqdxMc", (String) bindData.get("cbzwm"));
		}
		if (GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN == from && bindData == null) {
			map.put("zqdxLx", SystemSetting.xunJianType);
			if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
				map.put("zqdxMc", SystemSetting.xunJianMTname + "" + SystemSetting.xunJianName);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
				map.put("zqdxMc", SystemSetting.xunJianMTname);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
				map.put("zqdxMc", SystemSetting.xunJianName);
			}
		}
		return map;
	}

}
