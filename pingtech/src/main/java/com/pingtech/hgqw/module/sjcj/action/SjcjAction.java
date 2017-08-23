package com.pingtech.hgqw.module.sjcj.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class SjcjAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {

		OffDataService offDataService = new OffDataService();
		if ("orientBaseInfo".equals(method)) {

			OffData offData = new OffData();
			params.put("userid", LoginUser.getCurrentLoginUser().getUserID());//增加userid
			String xmlData = XmlUtils.buildXml(params);
			offData.setPdacode(DeviceUtils.getIMEI());
			SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name), Context.MODE_PRIVATE);
			String userid = prefs.getString("userid", "");
			offData.setUserid(userid);
			offData.setXmldata(xmlData);
			offData.setGxsj(new Date());
			offData.setCjsj(new Date());
			offData.setCzmk(ManagerFlag.PDA_SJCJ + "");
			offData.setCzgn(ManagerFlag.PDA_SJCJ_SJDW + "");

			try {
				offDataService.create(offData);
				return new Pair<Boolean, Object>(true, null);
			} catch (SQLException e) {
				return new Pair<Boolean, Object>(false, null);
			}
		}
		return new Pair<Boolean, Object>(false, null);
	}
}
