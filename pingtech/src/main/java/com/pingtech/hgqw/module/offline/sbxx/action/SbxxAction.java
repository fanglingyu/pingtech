package com.pingtech.hgqw.module.offline.sbxx.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.sbxx.service.SbxxService;

/**
 * @title SbxxAction.java
 * @description 返回智能设备概要信息
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:12:43
 * @version V1.0
 * @Copyright(c)2013
 */
public class SbxxAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getDeviceInfo".equals(method)) {
			if (params != null) {
				SbxxService sbxxService = new SbxxService();
				return new Pair<Boolean, Object>(true, sbxxService.findAllForException(params));
			}else{
				return new Pair<Boolean, Object>(false, null);	
			}
		}
		return new Pair<Boolean, Object>(false, null);
	}
}
