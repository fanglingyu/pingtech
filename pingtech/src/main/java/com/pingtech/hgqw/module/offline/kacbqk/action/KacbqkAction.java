package com.pingtech.hgqw.module.offline.kacbqk.action;

import java.sql.SQLException;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.utils.StringUtils;

public class KacbqkAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getWardShipList".equals(method)) {
			KacbqkService kacbqkService = new KacbqkService();
			try {
				return new Pair<Boolean, Object>(true, kacbqkService.findShipsbyPrams(params));
			} catch (Exception e) {
				return new Pair<Boolean, Object>(false, null);
			}
		}else if("getKacbqkByHC".equals(method)){ //物品检查模块(离线模式)，通过"航次"取得对象，用于将其中的数据保存到本地数据库中
			KacbqkService kacbqkService = new KacbqkService();
			if(params!=null&&params.size()>0){
				String hc = (String) params.get("hc");
				if(StringUtils.isNotEmpty(hc)){
					return new Pair<Boolean, Object>(true, kacbqkService.findShipsbyPrams(params));
				}
			}
			return new Pair<Boolean, Object>(false, null);
		}
		return new Pair<Boolean, Object>(false, null);
	}

}
