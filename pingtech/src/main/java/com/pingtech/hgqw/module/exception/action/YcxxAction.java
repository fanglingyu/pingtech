package com.pingtech.hgqw.module.exception.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.exception.utils.PullOffLineExcXml;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.module.offline.txjl.service.DkqkService;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class YcxxAction implements BaseAction {
	private static final String TAG = "YcxxAction";

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		OffDataService offDataService = new OffDataService();
		if ("getIllegalInfo".equals(method)) {
			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
			try {
				List<OffData> OffDatalist = offDataService.findAllByGN(ManagerFlag.PDA_YCXX, ManagerFlag.PDA_YCXX_TJCL, "0", null, BaseApplication.instent.gainUserID());
				for (int i = 0; i < OffDatalist.size(); i++) {
					String xml = OffDatalist.get(i).getXmldata();
					if (xml != null && !"".equals(xml)) {
						Map<String, String> map = PullOffLineExcXml.parseXMLData(xml);
						map.put("exceptionID", Integer.toString(OffDatalist.get(i).getId()));
						map.put("id", Integer.toString(OffDatalist.get(i).getId()));
						lists.add(map);
					}
				}

			} catch (SQLException e) {
				Log.i(TAG, e.getMessage());
				return new Pair<Boolean, Object>(false, null);
			}
			return new Pair<Boolean, Object>(true, lists);
		} else if ("sendIllegalInfo".equals(method)) {
			String xcxsid = (String) params.get("xcxsid");
			String dkjlid = (String) params.get("dkjlid");
			if (StringUtils.isNotEmpty(xcxsid)) {
				OffData offData = offDataService.findByCzid(xcxsid);
				if (offData != null) {
					int delStatus = offDataService.deleteByCzid(xcxsid);
					if (delStatus == 1) {
						params.put("xcxsid", "");
					}
				} else {
					params.put("xcxsid", "");
				}
			} else if (StringUtils.isNotEmpty(dkjlid)) {
				offDataService.deleteByCzid(dkjlid);
				//更新搭靠方向
				changDkfxByDkjlid(dkjlid);
			}
			params.put("jcqk", "02");
			OffData offData = new OffData();
			String xmlData = XmlUtils.buildXml(params);
			offData.setXmldata(xmlData);
			offData.setCjsj(new Date());
			if (params.get("whetherHandle") != null && "1".equals(params.get("whetherHandle"))) {
				offData.setClstatus("1");
			} else {
				offData.setClstatus("0");
			}
			offData.setCzgn(Integer.toString(ManagerFlag.PDA_YCXX_TJCL));
			offData.setCzmk(Integer.toString(ManagerFlag.PDA_YCXX));
			offData.setGxsj(new Date());
			offData.setPdacode(DeviceUtils.getIMEI());
			SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name), Context.MODE_PRIVATE);
			String userid = prefs.getString("userid", "");
			offData.setUserid(userid);
			if (StringUtils.isNotEmpty(params.get("exceptionID").toString())) {
				offData.setId(Integer.parseInt(params.get("exceptionID").toString()));
			}
			try {
				offDataService.insert(offData);
			} catch (SQLException e) {
				Log.i(TAG, e.getMessage());
				return new Pair<Boolean, Object>(false, null);
			}
		}
		return new Pair<Boolean, Object>(true, null);
	}

	
	/**
	 * 
	 * @方法名：changDkfxByDkjlid
	 * @功能说明：更改指定ID的搭靠方向
	 * @author liums
	 * @date  2013-10-24 下午7:42:18
	 * @param dkjlid
	 */
	private void changDkfxByDkjlid(String dkjlid) {
		DkqkService dkqkService = new DkqkService();
		try {
			dkqkService.changDkfxByDkjlid(dkjlid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
