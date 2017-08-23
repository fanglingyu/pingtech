package com.pingtech.hgqw.module.offline.qyxx.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.qyxx.service.QyxxService;

public class QyxxAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getKkInfo".equals(method)) {
			QyxxService service = new QyxxService();
			return new Pair<Boolean, Object>(true, service.findQyxxByPrams(params));

		}
		return new Pair<Boolean, Object>(false, null);
	}

}
