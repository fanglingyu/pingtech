package com.pingtech.hgqw.module.offline.hgzjxx.utils;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;
import com.pingtech.hgqw.module.offline.pzxx.entity.Pzxx;
import com.pingtech.hgqw.module.offline.pzxx.service.PzxxService;
import com.pingtech.hgqw.module.offline.qyxx.service.QyxxService;
import com.pingtech.hgqw.module.offline.zjyf.entity.YfResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.HashBuild;

public class HgzjxxUtil {

	private static final String TAG = "HgzjxxUtil";

	public static void addPzxx(HashBuild info, YfResult yfResult) {
		String zjhm = getZjhm(yfResult);
		if (StringUtils.isNotEmpty(zjhm)) {
			Pzxx pzxx = null;
			try {
				pzxx = new PzxxService().getPzxxByZjhm(zjhm);
			} catch (SQLException e) {
				e.printStackTrace();
				Log.i(TAG, "new PzxxService().getPzxxByZjhm(zjhm),error:" + e.getMessage());
			}
			if (pzxx != null) {
				info.put("bjtsxx", pzxx.getBjtsxx());// 报警提示信息
				info.put("pzmbly", pzxx.getPzmbly());// 碰撞目标来源
			}
		}

	}

	private static String getZjhm(YfResult yfResult) {
		String zjhm = "";
		if (yfResult.getZjxx() != null) {
			zjhm = yfResult.getZjxx().getZjhm();
		} else if (yfResult.getCyxx() != null) {
			zjhm = yfResult.getCyxx().getZjhm();
		}
		return zjhm;
	}

	public static String getFffwInfo(Hgzjxx hgzjxx, boolean isClyz) {
		if (hgzjxx == null) {
			return "";
		}
		// 查询所有证件号码相同的数据，拼装范围。
		StringBuilder stringBuilder = new StringBuilder();
		HgzjxxService hgzjxxService = new HgzjxxService();
		List<Hgzjxx> list = null;
		if (isClyz) {
			list = hgzjxxService.findListByCphm(hgzjxx);
		} else {
			list = hgzjxxService.findListByZjhm(hgzjxx);
		}
		if(list==null||list.isEmpty()){
			return "";
		}
		for (Hgzjxx hgzjxxTemp : list) {
			String name = getNameByCode(hgzjxxTemp);
			if (StringUtils.isNotEmpty(name)&&!stringBuilder.toString().contains(name)) {
				stringBuilder.append(name);
				stringBuilder.append("，");
			}
		}
		String result = stringBuilder.toString();
		if (result.endsWith("，")) {
			int index = result.lastIndexOf("，");
			result = result.substring(0, index);
		}
		return result;
	}

	private static String getNameByCode(Hgzjxx hgzjxx) {
		String fffwStr = "";
		if (hgzjxx == null) {
			return fffwStr;
		}
		String fffw = hgzjxx.getFffw();
		if (StringUtils.isEmpty(fffw)) {
			return fffwStr;
		}

		try {
			if (YfZjxxConstant.ZJ_FW_CB.equals(fffw)) {
				fffwStr = hgzjxx.getZwcbm();
			} else if (YfZjxxConstant.ZJ_FW_MT.equals(fffw)) {
				fffwStr = new MtdmService().getMtmcStrByMtdm(hgzjxx.getSsmt());
			} else if (YfZjxxConstant.ZJ_FW_QGTY.equals(fffw)) {
				fffwStr = BaseApplication.instent.getString(R.string.hgzjxx_fffw_str_qgty);
			} else if (YfZjxxConstant.ZJ_FW_XDQY.equals(fffw)) {
				fffwStr = new QyxxService().getQymcByQyid(hgzjxx.getSsqy());
			} else if (YfZjxxConstant.ZJ_FW_YZT.equals(fffw)) {
				fffwStr = BaseApplication.instent
						.getString(R.string.hgzjxx_fffw_str_qgty);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isEmpty(fffwStr)) {
			fffwStr = "";
		}
		return fffwStr;
	}
}
