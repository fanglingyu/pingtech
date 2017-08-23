package com.pingtech.hgqw.module.offline.sxtgl.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.sxtgl.service.SxtglService;

/**
 * @title SxtglAction.java
 * @description 获取摄像头概要信息接口
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:12:27
 * @version V1.0
 * @Copyright(c)2013
 */
public class SxtglAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getVidiconInfo".equals(method)) {
			if (params != null && params.size() > 0) {
				SxtglService sxtglService = new SxtglService();
				return new Pair<Boolean, Object>(true, sxtglService.findAllForException(params));
			} else {
				return new Pair<Boolean, Object>(false, null);
			}
		}
		return new Pair<Boolean, Object>(false, null);
	}
}
