package com.pingtech.hgqw.module.cbdt.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import android.util.Pair;

import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.HashBuild;
import com.pingtech.hgqw.utils.xml.XmlUtils;

/**
 * 
 * 
 * 类描述：船舶动态
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-CbdtShipAction.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author 赵琳
 * @version 1.0
 * @date 2013-10-16 上午10:09:46
 */
public class CbdtShipAction implements BaseAction {
	private static final String TAG = "CbdtShipAction";

	/** 船舶口岸状态 0预到港 */
	private static final String CBKAZT_YDG = "0";

	/** 船舶口岸状态 1在港 */
	private static final String CBKAZT_ZG = "1";

	/** 船舶口岸状态 2 预离港 */
	private static final String CBKAZT_YLG = "2";

	/** 船舶口岸状态 3离港 */
	private static final String CBKAZT_LG = "3";

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("hasDg".equals(method)) {// 是否有抵港时间
			return new Pair<Boolean, Object>(new KacbqkService().hasDg((String) params.get("voyageNumber")), null);
		} else if ("hasLg".equals(method)) {// 是否有离港时间
			String hc = (String) params.get("voyageNumber");// 获取航次号
			if (StringUtils.isNotEmpty(hc)) { // 判断hc是否为空
				KacbqkService kacbqkService = new KacbqkService();
				Kacbqk kacbqk = kacbqkService.getKacbqkByHC(hc);

				// 船舶信息查询失败，请确认离线数据是否同步完成
				if (kacbqk == null) {
					return new Pair<Boolean, Object>(false, "船舶信息查询失败，请确认离线数据是否同步完成");
				}

				// 离港操作失败，只有预离港船舶才能进行离港操作
				if (!CBKAZT_YLG.equals(kacbqk.getCbkazt())) {
					return new Pair<Boolean, Object>(false, "离港操作失败，只有预离港船舶才能进行离港操作");
				}

				// 离港操作失败，船舶已经离港
				if (CBKAZT_LG.equals(kacbqk.getCbkazt())) {
					return new Pair<Boolean, Object>(false, "离港操作失败，船舶已经离港");
				}

				if (StringUtils.isNotEmpty(kacbqk.getLgsj())) {
					return new Pair<Boolean, Object>(false, "离港操作失败，船舶已经离港");
				}

			}
			return new Pair<Boolean, Object>(true, null);

		} else if ("saveCbdgOffData".equals(method)) {
			try {
				new OffDataService().insert((OffData) params.get("offData"));
				// 更新船舶对应字段
				new KacbqkService().setDgsj((String) params.get("voyageNumber"), new Date());
				return new Pair<Boolean, Object>(true, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new Pair<Boolean, Object>(false, null);
		} else if ("saveCblgOffData".equals(method)) {
			try {
				new OffDataService().insert((OffData) params.get("offData"));
				// 更新船舶对应字段
				new KacbqkService().setLgsj((String) params.get("voyageNumber"), new Date());
				return new Pair<Boolean, Object>(true, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new Pair<Boolean, Object>(false, null);
		} else if ("saveCbkbOffData".equals(method)) {
			try {
				new OffDataService().insert((OffData) params.get("offData"));
				return new Pair<Boolean, Object>(true, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("saveCbybOffData".equals(method)) {
			try {
				new OffDataService().insert((OffData) params.get("offData"));
				return new Pair<Boolean, Object>(true, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("getShipInfo".equals(method)) {// 船舶详情界面
			String hc = (String) params.get("voyageNumber");// 获取航次号
			if (StringUtils.isNotEmpty(hc)) { // 判断hc是否为空
				return new Pair<Boolean, Object>(true, getInfo(hc));
			}
		} else if ("canKb".equals(method)) {// 判断能否靠泊
			String hc = (String) params.get("voyageNumber");// 获取航次号
			if (StringUtils.isNotEmpty(hc)) { // 判断hc是否为空
				KacbqkService kacbqkService = new KacbqkService();
				boolean hasDg = kacbqkService.hasDg(hc);
				boolean hasLg = kacbqkService.hasLg(hc);

				/**
				 * 返回值与对应内容 1:船舶未抵港，不能靠泊 2:船舶已离港，不能靠泊3:已经靠泊不能再靠泊
				 */

				if (hasDg && !hasLg) {

					OffDataService dataService = new OffDataService();
					OffData kbData = null;
					try {
						kbData = dataService.findOneByGN(ManagerFlag.PDA_CBDT, ManagerFlag.PDA_CBDT_CBKB);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (kbData == null) {
						return new Pair<Boolean, Object>(true, null);
					} else {
						return new Pair<Boolean, Object>(false, "3");
					}
				}
				if (!hasDg) {
					return new Pair<Boolean, Object>(false, "1");
				}
				if (hasLg) {
					return new Pair<Boolean, Object>(false, "2");
				}
			}
		} else if ("canYb".equals(method)) {// 判断能否移泊
			String hc = (String) params.get("voyageNumber");// 获取航次号
			if (StringUtils.isNotEmpty(hc)) { // 判断hc是否为空
				KacbqkService kacbqkService = new KacbqkService();
				boolean hasDg = kacbqkService.hasDg(hc);
				boolean hasLg = kacbqkService.hasLg(hc);

				/**
				 * 返回值与对应内容 1:船舶未抵港，不能移泊 2:船舶已离港，不能移泊
				 */

				if (hasDg && !hasLg) {
					OffDataService dataService = new OffDataService();
					OffData ybdData = null;
					try {
						ybdData = dataService.findOneByGN(ManagerFlag.PDA_CBDT, ManagerFlag.PDA_CBDT_CBYB);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (ybdData == null) {
						return new Pair<Boolean, Object>(true, null);
					} else {
						return new Pair<Boolean, Object>(false, "3");
					}
				}
				if (!hasDg) {
					return new Pair<Boolean, Object>(false, "1");
				}
				if (hasLg) {
					return new Pair<Boolean, Object>(false, "2");
				}
			}
		} else if ("canDg".equals(method)) {// 判断能否抵港
			// 判断船舶状态，只有预到港的才有条件抵港
			// 判断有没有抵港时间
			String hc = (String) params.get("voyageNumber");// 获取航次号
			if (StringUtils.isNotEmpty(hc)) { // 判断hc是否为空
				KacbqkService kacbqkService = new KacbqkService();
				Kacbqk kacbqk = kacbqkService.getKacbqkByHC(hc);
				// 口岸船舶状态，0预到港、1在港、2预离港、3离港
				/**
				 * 返回值与对应内容 1:抵港操作失败，船舶已经抵港！ 2:抵港操作失败，只有预到港船舶才能进行抵港操作！
				 */
				if (kacbqk != null && StringUtils.isNotEmpty(kacbqk.getCbkazt())) {
					if ("0".equals(kacbqk.getCbkazt())) {
						return new Pair<Boolean, Object>(true, null);
					} else if ("1".equals(kacbqk.getCbkazt())) {
						return new Pair<Boolean, Object>(false, "1");
					} else if ("2".equals(kacbqk.getCbkazt())) {
						return new Pair<Boolean, Object>(false, "1");
					} else if ("3".equals(kacbqk.getCbkazt())) {
						return new Pair<Boolean, Object>(false, "2");
					}
				}
			}
		} else if ("getPEInfo".equals(method)) {
			return new Pair<Boolean, Object>(true, null);
		}

		return new Pair<Boolean, Object>(false, null);
	}

	/**
	 * @方法名：getInfo
	 * @功能说明：封装船舶详情信息
	 * @author zhaotf
	 * @date 2013-10-24 下午4:17:40
	 * @param hc
	 *            航次
	 * @return
	 */
	public String getInfo(String hc) {
		Kacbqk kacbqk;
		try {
			kacbqk = new KacbqkService().getKacbqkByHC(hc);
			String xmlData = null;
			HashBuild has = new HashBuild(100);
			HashBuild info = new HashBuild(20);
			if (kacbqk != null) {
				String tkmtDm = kacbqk.getTkmt();
				String tkbwDm = kacbqk.getTkbw();
				String tkwz = new KacbqkService().getTkwzStr(tkmtDm, tkbwDm);

				info.put("kacbqkid", kacbqk.getKacbqkid() == null ? "" : kacbqk.getKacbqkid());// 口岸情况id
				info.put("hc", kacbqk.getHc() == null ? "" : kacbqk.getHc());// 航次
				info.put("cbzwm", kacbqk.getCbzwm() == null ? "" : kacbqk.getCbzwm());// 船舶中文名
				info.put("cbywm", kacbqk.getCbywm() == null ? "" : kacbqk.getCbywm());// 船舶英文名
				info.put("gj", kacbqk.getGj() == null ? "" : kacbqk.getGj());// 船舶国籍
				info.put("cbxz", kacbqk.getCbxz() == null ? "" : kacbqk.getCbxz());// 船舶性质
				info.put("tkwz", tkwz);// 停靠位置
				info.put("tkmt", kacbqk.getTkmt() == null ? "" : kacbqk.getTkmt());// 停靠码头
				info.put("tkbw", kacbqk.getTkbw() == null ? "" : kacbqk.getTkbw());// 停靠泊位
				info.put("jcfl", kacbqk.getJcfl() == null ? "" : getJcflmc(kacbqk.getJcfl()));// 检查分类
				info.put("dqjczt", kacbqk.getDqjczt() == null ? "" : kacbqk.getDqjczt());// 当前检查状态
				info.put("cdgs", kacbqk.getCdgs() == null ? "" : kacbqk.getCdgs());// 船代公司
				String kacbqkid = kacbqk.getKacbqkid();
				CyxxService cyxxService = new CyxxService();
				int cys = cyxxService.gainCyxxNum(kacbqkid);
				if (cys > 0) {
					info.put("cys", cys + "");// 船员数
				} else {
					info.put("cys", "");// 船员数
				}
				info.put("dlcys", kacbqk.getDlucyCount() + "");// 登陆船员数
				info.put("dlrys", kacbqk.getDlunryCount() + "");// 登轮人员数
				info.put("kacbzt", kacbqk.getCbkazt() == null ? "" : kacbqk.getCbkazt());// 口岸船舶状态，0预到港、1在港、2预离港、3离港

				// 根据口岸船舶状态，添加相应的时间字段（如，抵港时间，遇到港时间）
				if (StringUtils.isNotEmpty(kacbqk.getCbkazt())) {
					if ("0".equals(kacbqk.getCbkazt())) {
						// 预计到港时间
						info.put("yjdgsj", getDateString(kacbqk.getYjdgsj()));
					} else if ("1".equals(kacbqk.getCbkazt())) {
						// 抵港时间
						info.put("dgsj", getDateString(kacbqk.getDgsj()));
					} else if ("2".equals(kacbqk.getCbkazt())) {
						// 抵港时间
						info.put("dgsj", getDateString(kacbqk.getDgsj()));
						// 预离港时间
						info.put("yjlgsj", getDateString(kacbqk.getYjlgsj()));
					}
				}

				has.put("result", "success");
				has.put("info", info);
			} else {
				has.put("result", "error");
				has.put("info", "船舶基本信息获取失败，请下载离线数据后重试！");
			}
			xmlData = XmlUtils.buildXml(has.get());
			return xmlData;
		} catch (SQLException e) {
			Log.i(TAG, "getInfo ERROR：" + e.getMessage());
		}
		return null;
	}

	/**
	 * 
	 * @方法名：getJcflmc
	 * @功能说明：根据检查分类代码获取名称
	 * @author zhangyl
	 * @date 2013-1-4 下午2:49:51
	 * @param jcfl
	 * @return 1 入境 2 出境 3 入港 4 出港
	 */
	protected String getJcflmc(String jcfl) {
		if (StringUtils.isEmpty(jcfl)) {
			return "";
		}
		switch (Integer.parseInt(jcfl)) {
		case 1:
			return "入境";
		case 2:
			return "出境";
		case 3:
			return "入港";
		case 4:
			return "出港";
		default:
			return "";
		}
	}

	/**
	 * 将Date转换成String
	 * 
	 * @param date
	 * @return
	 */
	private String getDateString(Date date) {
		String dateStr = "";
		if (null != date) {
			try {
				dateStr = DateUtils.DateToString(date, "yyyy-MM-dd hh:mm:ss");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dateStr;
	}
}
