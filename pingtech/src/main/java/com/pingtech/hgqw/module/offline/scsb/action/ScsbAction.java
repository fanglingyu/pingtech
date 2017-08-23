package com.pingtech.hgqw.module.offline.scsb.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.scsb.service.ScsbService;

/**
 * @title ScsbAction.java
 * @description 获取手持设备概要信息接口
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:13:09
 * @version V1.0
 * @Copyright(c)2013
 */
public class ScsbAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getPdaInfo".equals(method)) {
			ScsbService scsbService = new ScsbService();
			try {
				return new Pair<Boolean, Object>(true, scsbService.findAllForException(params));
			} catch (Exception e) {
				return new Pair<Boolean, Object>(false, null);
			}
		}
		return new Pair<Boolean, Object>(false, null);
	}
}
