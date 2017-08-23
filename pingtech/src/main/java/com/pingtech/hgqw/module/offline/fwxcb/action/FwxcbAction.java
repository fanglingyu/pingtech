package com.pingtech.hgqw.module.offline.fwxcb.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.fwxcb.service.FwxcbService;

public class FwxcbAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getServiceShipList".equals(method)) {
			FwxcbService fwxcbService = new FwxcbService();
			try {
				return new Pair<Boolean, Object>(true, fwxcbService.findFwShipsbyPrams(params));
			} catch (Exception e) {
				return new Pair<Boolean, Object>(false, null);
			}
		}
		return new Pair<Boolean, Object>(false, null);
	}
}
