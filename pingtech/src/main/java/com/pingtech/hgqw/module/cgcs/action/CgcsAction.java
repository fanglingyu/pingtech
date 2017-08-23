package com.pingtech.hgqw.module.cgcs.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.HashBuild;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class CgcsAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("sendPassInfo".equals(method)) {
			HashBuild datas = new HashBuild(10);
			HashBuild info = new HashBuild(10);
			String cgcsid = StringUtils.UIDGenerator();
			params.put("cgcsid", cgcsid);
			try {
				String xmldata = XmlUtils.buildXml(params);
				OffData data = new OffData();
				data.setCjsj(new Date());
				data.setClstatus("0");
				data.setCzgn(ManagerFlag.PDA_XCXJ_CGCS + "");
				data.setCzmk(ManagerFlag.PDA_XCXJ + "");
				data.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
				data.setUserid(BaseApplication.instent.gainUserID());
				data.setXmldata(xmldata);
				OffDataService service = new OffDataService();
				service.insert(data);
				datas.put("result", "success");
				info.put("txjlid", null);
				info.put("cgcsid", cgcsid);
				info.put("xcxsid", null);
				datas.put("info", info);
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			} catch (Exception e) {
				datas.put("result", "error");
				datas.put("info", "保存查岗查哨记录错误，请稍后重试！");
				return new Pair<Boolean, Object>(false, XmlUtils.buildXml(datas.get()));
			}
		}
		return new Pair<Boolean, Object>(false, null);
	}

}
