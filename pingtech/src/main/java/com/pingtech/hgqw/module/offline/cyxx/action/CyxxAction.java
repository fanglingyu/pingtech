package com.pingtech.hgqw.module.offline.cyxx.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.StringUtils;

public class CyxxAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("getPersonInfo".equals(method)) {
			CyxxService cyxxService = new CyxxService();
			Object comeFrom = params.get("comeFrom");
			String sfsk = (String) params.get("sfsk");
			String ickey = (String) params.get("ickey");
			String mrickey = (String) params.get("mrickey");
			if (comeFrom != null) {
				comeFrom = comeFrom.toString();
			}
			if (comeFrom != null && "1".equals(comeFrom)) {
				Object hc = params.get("voyageNumber");
				if (StringUtils.isNotEmpty(hc)) {
					Kacbqk kacbqk = new KacbqkService().getKacbqkByHC(hc.toString());
					if (kacbqk != null) {
						String kacbqkid = kacbqk.getKacbqkid();
						if (StringUtils.isNotEmpty(kacbqkid)) {
							params.put("kacbqkid", kacbqkid);
						}
						kacbqkid = null;
					}
				}
				return new Pair<Boolean, Object>(true, cyxxService.findAll(params));
			} else if ("2".equals(comeFrom)) {
				HgzjxxService hgzjxxService = new HgzjxxService();
				List<Map<String, String>> cy = new ArrayList<Map<String, String>>();
				List<Map<String, String>> cyxx = cyxxService.findAll(params);
				List<Map<String, String>> zjxx = new ArrayList<Map<String, String>>();
				if (sfsk != null && YfZjxxConstant.ZJCX_SFSK_SK.equals(sfsk)) {
					List<Hgzjxx> lists = hgzjxxService.getYfZjxx(ickey, null, mrickey, sfsk, false, YfZjxxConstant.ICFFZT_YFF, null, false);
					zjxx = reBuildHgzjxx(lists, zjxx);
				} else {
					zjxx = hgzjxxService.getZjxx(params);
				}
				if (sfsk != null && YfZjxxConstant.ZJCX_SFSK_SK.equals(sfsk)) {
					
				}else{
					if (cyxx != null && cyxx.size() > 0) {
						cy.addAll(cyxx);
					}
				}
				if (zjxx != null && zjxx.size() > 0) {
					cy.addAll(zjxx);
				}
				return new Pair<Boolean, Object>(true, cy);
			} else if ("4".equals(comeFrom)) {
				try {
					return new Pair<Boolean, Object>(true, cyxxService.getCyxxForException(params));
				} catch (Exception e) {
					return new Pair<Boolean, Object>(false, null);
				}

			}
		}
		return null;
	}

	private List<Map<String, String>> reBuildHgzjxx(List<Hgzjxx> lists, List<Map<String, String>> zjxx) {
		if (lists == null || lists.isEmpty()) {
			return zjxx;
		}
		for (int i = 0; i < lists.size(); i++) {
			Hgzjxx hgzjxx = lists.get(i);
			if (hgzjxx != null) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zw", hgzjxx.getZwdm());
				map.put("xm", hgzjxx.getXm());
				map.put("xb", hgzjxx.getXbdm());
				map.put("gj", hgzjxx.getGjdqdm());
				map.put("zjhm", hgzjxx.getZjhm());
				map.put("ssdw", hgzjxx.getSsdw());
				map.put("csrq", DateUtils.gainBirthday(hgzjxx.getCsrq()));
				map.put("hyid", hgzjxx.getCbzjffxxxid());
				map.put("zjlx", hgzjxx.getZjlbdm());
				map.put("hgzl", hgzjxx.getZjlb());
				if ("1".equals(hgzjxx.getYcyxbz() == null ? "" : hgzjxx.getYcyxbz())) {
					// 本航次有效
					map.put("yxq", BaseApplication.instent.getString(R.string.bhcyx));
				} else {
					map.put("yxq",
							new StringBuffer().append(DateUtils.DateToString(hgzjxx.getYxqq())).append(BaseApplication.instent.getString(R.string.zhi))
									.append(DateUtils.DateToString(hgzjxx.getYxqz())).toString());
				}
				zjxx.add(map);
			}
		}

		return zjxx;
	}

}
