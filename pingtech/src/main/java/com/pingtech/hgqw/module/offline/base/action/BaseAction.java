package com.pingtech.hgqw.module.offline.base.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

public interface BaseAction {
	Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException;
}
