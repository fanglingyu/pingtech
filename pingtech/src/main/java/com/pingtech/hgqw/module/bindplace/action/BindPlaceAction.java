package com.pingtech.hgqw.module.bindplace.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.bwdm.service.BwdmService;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;
import com.pingtech.hgqw.module.offline.qyxx.service.QyxxService;

public class BindPlaceAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getBaseInfoByCard".equals(method)) {
			getBaseInfoByCard(params);
		}
		return null;
	}

	private void getBaseInfoByCard(Map<String, Object> params) {
		String id = (String) params.get("id");
		String type = (String) params.get("type");
		if ("mt".equals(type)) {
			new MtdmService().getMtdmByMtid(id);
		} else if ("bw".equals(type)) {
			new BwdmService().getBwdmByBwid(id);
		} else if ("qy".equals(type)) {
			new QyxxService().getQyxxByQyid(id);
		}
	}

}
