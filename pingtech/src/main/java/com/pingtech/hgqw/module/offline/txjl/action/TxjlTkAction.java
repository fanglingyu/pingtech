package com.pingtech.hgqw.module.offline.txjl.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlTkService;

public class TxjlTkAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method,
			Map<String, Object> params) throws SQLException {
		
		if ("getLxCyxxList".equals(method)) {
			TxjlTkService service = new TxjlTkService();
			return new Pair<Boolean, Object>(true, service.findCyxxByHc(params));

		} else {
			return new Pair<Boolean, Object>(false, null);
		}
	}
}
