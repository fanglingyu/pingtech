package com.pingtech.hgqw.module.xunjian.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.bwdm.utils.BwdmUtil;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.utils.HgzjxxUtil;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.mtdm.utils.MtdmUtil;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.module.offline.txjl.entity.Dkqk;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlTk;
import com.pingtech.hgqw.module.offline.txjl.service.DkqkService;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlTkService;
import com.pingtech.hgqw.module.offline.userinfo.entity.TBUserinfo;
import com.pingtech.hgqw.module.offline.userinfo.service.TBUserinfoService;
import com.pingtech.hgqw.module.offline.zjyf.entity.YfResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.offline.zjyf.yfimpl.PdaYfxxImpl;
import com.pingtech.hgqw.module.xunjian.utils.XcUtil;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;
import com.pingtech.hgqw.utils.xml.HashBuild;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class XunJianAction implements BaseAction {
	/** 通行方向-上船 */
	private static final String TXFX_SC = "0";

	/** 通行方向-下船 */
	private static final String TXFX_XC = "1";

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("sendSwipeRecord".equals(method)) {
			return sendSwipeRecord(params);
		} else if ("sendClSwipeRecord".equals(method)) {
			return sendClSwipeRecord(params);
		} else if ("sendPassInfo".equals(method)) {
			return sendPassInfo(params, false);
		} else if ("sendClPassInfo".equals(method)) {
			return sendPassInfo(params, true);
		} else if ("getPersonOnDuty".equals(method)) {
			return getPersonOnDuty();
		} else if ("saveXj".equals(method)) {
			return saveXj(params);
		}
		return new Pair<Boolean, Object>(false, null);
	}

	private Pair<Boolean, Object> getPersonOnDuty() {
		HashBuild datas = new HashBuild(10);
		datas.put("result", "error");
		datas.put("info", "未找到执勤人员！");
		return new Pair<Boolean, Object>(false, XmlUtils.buildXml(datas.get()));
	}

	private Pair<Boolean, Object> saveXj(Map<String, Object> params) {
		try {
			String xmldata = XmlUtils.buildXml(params);
			OffData data = new OffData();
			data.setCjsj(new Date());
			data.setClstatus("0");
			data.setCzgn(ManagerFlag.PDA_XCXJ_XJJL + "");
			data.setCzmk(ManagerFlag.PDA_XCXJ + "");
			data.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
			data.setUserid(BaseApplication.instent.gainUserID());
			data.setXmldata(xmldata);
			OffDataService service = new OffDataService();
			service.insert(data);
			return new Pair<Boolean, Object>(true, null);
		} catch (Exception e) {
			return new Pair<Boolean, Object>(false, null);
		}
	}

	private Pair<Boolean, Object> sendPassInfo(Map<String, Object> params, boolean isClyz) {
		HashBuild datas = new HashBuild(30);
		HashBuild info = new HashBuild(30);
		String xcxsid = StringUtils.UIDGenerator();
		params.put("xcxsid", xcxsid);
		params.put("sjid", StringUtils.UIDGenerator());
		params.put("zjlx", (String) params.get("zjzl"));// 离线使用
		if (isClyz) {
			params.put("gsyyz", (String) params.get("gs_yyz"));// 离线使用
			params.put("dxlx", "02");// 对象类型
			params.put("jcdx", (String) params.get("cphm"));// 检查对象
		} else {
			params.put("jcdx", (String) params.get("xm"));// 检查对象
			params.put("dxlx", "01");// 对象类型
		}
		params.put("jcfs", "02");// 检查方式
		params.put("jcsj", DateUtils.dateToString(new Date()));// 检查方式
		params.put("jcqk", "01");// 检查方式
		params.put("voyageNumber", (String) params.get("voyageNumber"));
		params.put("txsj", DateUtils.dateToString(new Date()));// 检查方式
		// params.put("jcdd", getJcdd(SystemSetting.xunJianType, (String)
		// params.get("voyageNumber")));// 检查地点
		// 附加经纬度信息
		params.put("jwd", XcUtil.buildJwd());

		// KacbqkService kacbqkService = new KacbqkService();
		// Kacbqk kacbqk = kacbqkService.getKacbqkByHC((String)
		// params.get("voyageNumber"));
		// if (kacbqk != null) {
		// params.put("cbmc", kacbqk.getCbzwm());// 检查地点
		// params.put("mt", kacbqk.getTkmt());
		// // params.put("dockname", kacbqk.getTkmt());
		// params.put("bw", kacbqk.getTkbw());
		// params.put("kacbqkid", kacbqk.getKacbqkid());
		// }
		// ////////
		if (StringUtils.isNotEmpty(params.get("voyageNumber"))) {
			params.put("voyageNumber", (String) params.get("voyageNumber"));
			KacbqkService kacbqkService = new KacbqkService();
			Kacbqk kacbqk = kacbqkService.getKacbqkByHC((String) params.get("voyageNumber"));
			params.put("cbmc", kacbqk.getCbzwm());
			params.put("glcbmc", kacbqk.getCbzwm());
			if (kacbqk != null) {
				params.put("jcdd", getJcddByCl(SystemSetting.xunJianType, (String) params.get("voyageNumber"), kacbqk));
				params.put("berthcode", kacbqk.getTkbw());
				params.put("dockcode", kacbqk.getTkmt());
				params.put("berthname", SystemSetting.xunJianName);
				params.put("dockname", SystemSetting.xunJianMTname);
			}
		} else {
			params.put("jcdd", getJcdd(SystemSetting.xunJianType, (String) params.get("voyageNumber")));
		}

		if (SystemSetting.xunJianId != null && SystemSetting.xunJianId.length() > 0) {
			if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
				String bwid = SystemSetting.xunJianId;
				String mtid = SystemSetting.xunJianMTid;
				String bwdm = BwdmUtil.getBwdmByBwid(bwid);
				String mtdm = MtdmUtil.getMtdmByMtid(mtid);

				params.put("berthcode", bwdm);
				params.put("berthname", SystemSetting.xunJianName);
				params.put("dockcode", mtdm);
				params.put("dockname", SystemSetting.xunJianMTname);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
				String mtid = SystemSetting.xunJianId;
				String mtdm = MtdmUtil.getMtdmByMtid(mtid);
				params.put("dockcode", mtdm);
				params.put("dockname", SystemSetting.xunJianMTname);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
				params.put("areacode", SystemSetting.xunJianId);
				params.put("areaname", SystemSetting.xunJianName);
			}
		}
		// ////////

		try {
			String xmldata = XmlUtils.buildXml(params);
			OffData data = new OffData();
			data.setCjsj(new Date());
			data.setClstatus("0");
			if (isClyz) {
				data.setCzgn(ManagerFlag.PDA_XCXJ_CLYZ + "");
			} else {
				data.setCzgn(ManagerFlag.PDA_XCXJ_XJJL + "");
			}
			data.setCzmk(ManagerFlag.PDA_XCXJ + "");
			data.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
			data.setUserid(BaseApplication.instent.gainUserID());
			data.setCzid(xcxsid);
			data.setXmldata(xmldata);
			OffDataService service = new OffDataService();
			service.insert(data);
			datas.put("result", "success");
			info.put("txjlid", null);
			info.put("cgcsid", null);
			info.put("xcxsid", xcxsid);
			datas.put("info", info);
			return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
		} catch (Exception e) {
			datas.put("result", "error");
			datas.put("info", "保存巡查巡视记录错误，请稍后重试！");
			return new Pair<Boolean, Object>(false, XmlUtils.buildXml(datas.get()));
		}
	}

	private Pair<Boolean, Object> sendSwipeRecord(Map<String, Object> params) throws SQLException {
		HashBuild datas = new HashBuild(100);
		PdaYfxxImpl pdaYfxx = new PdaYfxxImpl();
		String cardNumber = (String) params.get("cardNumber");
		String defaultickey = (String) params.get("defaultickey");
		String sfsk = (String) params.get("sfsk");
		String voyageNumber = (String) params.get("voyageNumber");
		String kacbqkid = (String) params.get("kacbqkid");
		String cbzwm = (String) params.get("cbzwm");
		String type = (String) params.get("type");
		String ddID = (String) params.get("ddID");
		if (voyageNumber != null && !"".equals(voyageNumber)) {
			YfResult yfResult = pdaYfxx.zjIsAvailable(cardNumber, cardNumber, defaultickey, sfsk, voyageNumber, null, YfZjxxConstant.YFFS_PDATK,
					null, false,   null);
			if (yfResult != null && (yfResult.getZjxx() != null || yfResult.getCyxx() != null)) {// 卡号存在
				if ("01".equals(params.get("xjlx"))) {// 巡检类型：01日常巡检人员、06查岗查哨
					// 证件类型 48:登轮证, 50登陆证,17海员证 , 52搭靠外轮许可证,sbk士兵卡。
					if (YfZjxxConstant.ZJLX_DK.equals(yfResult.getZjlx())) {
						datas = dkOperation(yfResult, params);// 搭靠业务
						// &&
						// !YfZjxxConstant.ZJ_QYLXBS_CL.equals(zjxx.getQylxbs())
					} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx()) && yfResult.getZjxx() != null
							&& YfZjxxConstant.ZJ_QYLXBS_CL.equals(yfResult.getZjxx().getQylxbs())) {
						datas.put("result", "error");
						datas.put("isClzj", "true");
						datas.put("info", "您刷的是车辆限定区域通行证，请在车辆巡检模块下刷卡");
						// datas = buildInfoByEnterCard(yfResult, params,
						// false);
					} else if (YfZjxxConstant.ZJLX_DLUN.equals(yfResult.getZjlx()) || YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx())) {
						datas = buildInfoByEnterCard(yfResult, params, false);
					} else {// 船员
						datas.put("result", "success");
						datas = buildInfoByCyxx(yfResult, params);
					}
				}
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			}
		}
		// 绑定卡口验证
		if ("qy".equals(type) && StringUtils.isNotEmpty(ddID)) {
			YfResult yfResult = pdaYfxx.zjIsAvailable(cardNumber, cardNumber, defaultickey, sfsk, null, ddID, YfZjxxConstant.YFFS_PDAKK, null, false, null);
			if (yfResult != null && (yfResult.getZjxx() != null || yfResult.getCyxx() != null)) {// 卡号存在
				if ("01".equals(params.get("xjlx"))) {// 巡检类型：01日常巡检人员、06查岗查哨
					// 证件类型 48:登轮证, 50登陆证,17海员证 , 52搭靠外轮许可证,sbk士兵卡。
					if (YfZjxxConstant.ZJLX_DK.equals(yfResult.getZjlx())) {
						datas = dkOperation(yfResult, params);// 搭靠业务
					} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx()) && yfResult.getZjxx() != null
							&& YfZjxxConstant.ZJ_QYLXBS_CL.equals(yfResult.getZjxx().getQylxbs())) {
						datas.put("result", "error");
						datas.put("isClzj", "true");
						datas.put("info", "您刷的是车辆限定区域通行证，请在车辆巡检模块下刷卡");
						// datas = buildInfoByEnterCard(yfResult, params,
						// false);
					} else if (YfZjxxConstant.ZJLX_DLUN.equals(yfResult.getZjlx()) || YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx())) {
						datas = buildInfoByEnterCard(yfResult, params, false);
					} else {// 船员
						datas.put("result", "success");
						datas = buildInfoByCyxx(yfResult, params);
					}
				}
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			}
		}

		String xjlx = (String) params.get("xjlx");
		if ("06".equals(xjlx)) {
			TBUserinfoService service = new TBUserinfoService();
			TBUserinfo user = service.gainUserByZjhm(cardNumber);
			if (user != null) {
				datas = cgcsOperation(user, cbzwm);
			} else {
				datas.put("result", "error");
				datas.put("info", "士兵卡不存在！");
			}
		} else {
			HgzjxxService service = new HgzjxxService();
			CyxxService cyxxService = new CyxxService();
			Hgzjxx hgzjxx = service.gainXJHgzjxx(cardNumber, sfsk, voyageNumber);
			TBCyxx cyxx = cyxxService.gainCyxxCx(cardNumber, voyageNumber, kacbqkid);
			if (hgzjxx != null) {
				if (YfZjxxConstant.ZJLX_XDQY.equals(hgzjxx.getZjlb()) && YfZjxxConstant.ZJ_QYLXBS_CL.equals(hgzjxx.getQylxbs())) {
					datas.put("result", "error");
					datas.put("isClzj", "true");
					datas.put("info", "您刷的是车辆限定区域通行证，请在车辆巡检模块下刷卡");
					return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
				}
				datas = buildInfoWithoutShip(hgzjxx, params);
			} else if (cyxx != null) {
				YfResult yfResultTemp = new YfResult();
				yfResultTemp.setCyxx(cyxx);
				yfResultTemp.setTsxx("船下(非限定区域)检查不进行验证");
				datas = buildInfoByCyxx(yfResultTemp, params);
			} else {
				HashBuild info = new HashBuild(10);
				datas.put("result", "success");// result必须为success，否则不能进入证件编辑页面。
				info.put("tsxx", "验证失败，不是边防证件");
				datas.put("info", info);
			}

		}

		return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
	}

	private Pair<Boolean, Object> sendClSwipeRecord(Map<String, Object> params) throws SQLException {
		HashBuild datas = new HashBuild(100);
		PdaYfxxImpl pdaYfxx = new PdaYfxxImpl();
		String cardNumber = (String) params.get("cardNumber");
		String defaultickey = (String) params.get("defaultickey");
		String sfsk = (String) params.get("sfsk");
		String voyageNumber = (String) params.get("voyageNumber");
		String kacbqkid = (String) params.get("kacbqkid");
		String cbzwm = (String) params.get("cbzwm");
		String type = (String) params.get("type");
		String ddID = (String) params.get("ddID");
		String cphm = (String) params.get("cphm");
		String jszbh_sfzh = (String) params.get("jszbh_sfzh");

		// 绑定卡口验证
		if ("qy".equals(type) && StringUtils.isNotEmpty(ddID)) {
			YfResult yfResult = pdaYfxx.zjIsAvailable(cardNumber, cardNumber, defaultickey, sfsk, null, ddID, YfZjxxConstant.YFFS_PDAKK, cphm, true, jszbh_sfzh);
			if (yfResult != null && (yfResult.getZjxx() != null || yfResult.getCyxx() != null)) {// 卡号存在
				if ("01".equals(params.get("xjlx"))) {// 巡检类型：01日常巡检人员、06查岗查哨
					// 证件类型 48:登轮证, 50登陆证,17海员证 , 52搭靠外轮许可证,sbk士兵卡。
					if (YfZjxxConstant.ZJLX_DK.equals(yfResult.getZjlx())) {
						datas.put("result", "error");
						datas.put("info", "您刷的是搭靠外轮许可证，请在日常巡检模块下刷卡");
					} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx()) && yfResult.getZjxx() != null
							&& YfZjxxConstant.ZJ_QYLXBS_CL.equals(yfResult.getZjxx().getQylxbs())) {
						datas = buildInfoByEnterCard(yfResult, params, true);
					} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx())
							&& YfZjxxConstant.ZJ_QYLXBS_RY.equals(yfResult.getZjxx().getQylxbs())) {
						datas.put("result", "error");
						datas.put("info", "您刷的是人员限定区域通行证，请在日常巡检模块下刷卡");
					} else if (YfZjxxConstant.ZJLX_DLUN.equals(yfResult.getZjlx())) {
						datas.put("result", "error");
						datas.put("info", "您刷的是登轮许可证，请在日常巡检模块下刷卡");
					} else {// 船员
						// datas.put("result", "success");
						// datas = buildInfoByCyxx(yfResult, params);
					}
				}
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			} else if (yfResult != null && yfResult.isToast()) {
				datas.put("result", "error");
				datas.put("info", yfResult.getTsxx());
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			} else {
				datas.put("result", "success");
				datas.put("info", "验证失败，不是边防证件");
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			}
		}

		List<Hgzjxx> zjxxs = new HgzjxxService().getYfZjxx(cardNumber, cardNumber, defaultickey, sfsk, null, YfZjxxConstant.ICFFZT_YFF, cphm, true);
		/*if (zjxxs != null && StringUtils.isEmpty(jszbh_sfzh) && "1".equals(sfsk)) {
			for (Hgzjxx hgzjxx : zjxxs) {
				if (StringUtils.isNotEmpty(hgzjxx.getJsyhm())) {
					datas.put("result", "error");
					datas.put("info", "请输入驾驶证编号");
					Pair pair = new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
					return pair;
				}
			}
		}*/
		if (zjxxs != null && !zjxxs.isEmpty()) {
			Hgzjxx hgzjxx = zjxxs.get(0);
			if (YfZjxxConstant.ZJLX_DK.equals(hgzjxx.getZjlb())) {
				datas.put("result", "error");
				datas.put("info", "您刷的是搭靠外轮许可证，请在日常巡检模块下刷卡");
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(hgzjxx.getZjlb()) && YfZjxxConstant.ZJ_QYLXBS_CL.equals(hgzjxx.getQylxbs())) {
				YfResult yfResult = new YfResult();
				yfResult.setTsxx("未绑定限定区域，不做车辆验证");
				yfResult.setZjxx(hgzjxx);
				datas = buildInfoByEnterCard(yfResult, params, true);
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(hgzjxx.getZjlb()) && YfZjxxConstant.ZJ_QYLXBS_RY.equals(hgzjxx.getQylxbs())) {
				datas.put("result", "error");
				datas.put("info", "您刷的是人员限定区域通行证，请在日常巡检模块下刷卡");
			} else if (YfZjxxConstant.ZJLX_DLUN.equals(hgzjxx.getZjlb())) {
				datas.put("result", "error");
				datas.put("info", "您刷的是登轮许可证，请在日常巡检模块下刷卡");
			} else {// 船员
				// datas.put("result", "success");
				// datas = buildInfoByCyxx(yfResult, params);
			}
		} else {
			datas.put("result", "success");
			datas.put("info", "验证失败，不是边防证件");
		}

		return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
	}

	/**
	 * 
	 * @方法名：buildInfoByCyxx
	 * @功能说明：封装刷卡船员记录异常信息
	 * @author 娄高伟
	 * @date 2013-10-18 下午5:47:35
	 * @param yfResult
	 * @param params
	 * @return
	 */
	private HashBuild buildInfoByCyxx(YfResult yfResult, Map<String, Object> params) {
		TBCyxx cyxx = yfResult.getCyxx();
		HashBuild datas = new HashBuild(20);
		HashBuild info = new HashBuild(20);
		String xcxsid = StringUtils.UIDGenerator();
		String xcxj = buildYcxxByCyxx(cyxx, xcxsid, params);
		try {
			OffDataService dataService = new OffDataService();
			OffData offData = new OffData();
			offData.setCjsj(new Date());
			offData.setClstatus("0");
			offData.setCzgn(ManagerFlag.PDA_XCXJ_XJJL + "");
			offData.setCzmk(ManagerFlag.PDA_XCXJ + "");
			offData.setGxsj(new Date());
			offData.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
			offData.setUserid(BaseApplication.instent.gainUserID());
			offData.setCzid(xcxsid);
			offData.setXmldata(xcxj);
			dataService.insert(offData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		datas.put("result", "success");
		info.put("tsxx", yfResult.getTsxx());
		if (yfResult.isResult()) {
			info.put("isPass", "pass");// 是否验证通过
		} else {
			info.put("isPass", "false");// 是否验证通过
		}
		info.put("xcxsid", xcxsid);// 巡查巡视记录ID
		info.put("sfdk", "0");// 是否搭靠：0否，1是
		info.put("dkjlid", null);// 搭靠记录ID
		info.put("cgcsid", null);// 查岗查哨记录ID
		info.put("hgzl", null);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
		info.put("zjxx", buildInfoZjxxByCyxx(cyxx));
		datas.put("info", info);
		return datas;
	}

	/**
	 * 
	 * @方法名：buildYcxxByCyxx
	 * @功能说明：封装巡查巡检
	 * @author 娄高伟
	 * @date 2013-10-22 下午3:08:10
	 * @param cyxx
	 * @param xcxsid
	 * @return
	 */
	private String buildYcxxByCyxx(TBCyxx cyxx, String xcxsid, Map<String, Object> params) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("xcxsid", xcxsid);
		map.put("jcdx", cyxx.getXm());
		map.put("jcr", BaseApplication.instent.gainUserID());
		map.put("dxlx", "01");
		map.put("zjhm", cyxx.getZjhm());
		map.put("zjlx", cyxx.getZjlx());
		map.put("xm", cyxx.getXm());
		map.put("xb", cyxx.getXb());
		map.put("gj", cyxx.getGj());
		map.put("sjid", StringUtils.UIDGenerator());
		map.put("csrq", DateUtils.DateToString(cyxx.getCsrq()));
		map.put("ssdw", (String) params.get("cbzwm"));
		map.put("xxly", "03");
		map.put("jcdd", getJcdd(SystemSetting.xunJianType, (String) params.get("voyageNumber")));
		map.put("jcsj", DateUtils.dateToString(new Date()));

		if (StringUtils.isNotEmpty(params.get("voyageNumber"))) {
			KacbqkService kacbqkService = new KacbqkService();
			Kacbqk kacbqk = kacbqkService.getKacbqkByHC((String) params.get("voyageNumber"));
			map.put("cbmc", (String) params.get("cbzwm"));
			if (kacbqk != null) {
				map.put("berthcode", kacbqk.getTkbw());
				map.put("dockcode", kacbqk.getTkmt());
			}
		}

		map.put("hc", (String) params.get("voyageNumber"));
		map.put("jcfs", "02");
		map.put("jcqk", "01");
		map.put("txsj", DateUtils.dateToString(new Date()));// 通行时间
		if (SystemSetting.xunJianId != null && SystemSetting.xunJianId.length() > 0) {
			if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
				map.put("berthcode", SystemSetting.xunJianId);
				map.put("berthname", SystemSetting.xunJianName);
				map.put("dockcode", SystemSetting.xunJianMTid);
				map.put("dockname", SystemSetting.xunJianMTname);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
				map.put("dockcode", SystemSetting.xunJianMTid);
				map.put("dockname", SystemSetting.xunJianMTname);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
				map.put("areacode", SystemSetting.xunJianId);
				map.put("areaname", SystemSetting.xunJianName);
			}
		}
		String xcxj = XmlUtils.buildXml(map);
		return xcxj;
	}

	/**
	 * 
	 * @方法名：getJcdd
	 * @功能说明：检查地点赋值
	 * @author liums
	 * @date 2013-8-7 下午04:42:33
	 * @return 检查地点
	 */
	private String getJcdd(String type, String voyageNumber) {
		if (voyageNumber != null && !"".equals(voyageNumber.trim())) {
			return "01";
		} else if ("mt".equals(type)) {
			return "02";
		} else if ("bw".equals(type)) {
			return "03";
		} else if ("qy".equals(type)) {
			return "04";
		}
		return null;
	}

	/**
	 * 如果是船舶，则检查地点为码头或泊位
	 * 
	 * @param type
	 * @param voyageNumber
	 * @param kacbqk
	 * @return
	 */
	private String getJcddByCl(String type, String voyageNumber, Kacbqk kacbqk) {
		if (voyageNumber != null && !"".equals(voyageNumber.trim())) {
			if (kacbqk != null) {
				String tkmt = kacbqk.getTkmt();
				String tkbw = kacbqk.getTkbw();
				if (StringUtils.isEmpty(tkbw)) {
					return "02";
				} else {
					return "03";
				}
			}
			return "01";
		} else if ("mt".equals(type)) {
			return "02";
		} else if ("bw".equals(type)) {
			return "03";
		} else if ("qy".equals(type)) {
			return "04";
		}
		return null;
	}

	/**
	 * 
	 * @方法名：buildInfoByEnterCard
	 * @功能说明：登轮证巡检业务
	 * @author liums
	 * @date 2013-8-7 下午04:39:18
	 * @param yfResult
	 *            验放结果
	 * @param isClyz
	 *            TODO
	 * @param user
	 *            执勤人员信息
	 * @return 返回到客户端的数据
	 */
	private HashBuild buildInfoByEnterCard(YfResult yfResult, Map<String, Object> params, boolean isClyz) {
		Hgzjxx hgzjxx = yfResult.getZjxx();
		HashBuild datas = new HashBuild(100);
		HashBuild info = new HashBuild(100);
		// 登轮证有两种信息页面，一个是在船上，一个是在船下，字段不同。船下不显示通行记录，显示有效期、所登船舶。
		String xcxsid = StringUtils.UIDGenerator();
		if (StringUtils.isNotEmpty(params.get("voyageNumber"))) {// 船上
			String ycxx = buildYcxxByCbzjffxx(hgzjxx, xcxsid, params, isClyz);
			try {
				OffDataService dataService = new OffDataService();
				OffData offData = new OffData();
				offData.setCjsj(new Date());
				offData.setClstatus("0");
				if (isClyz) {
					offData.setCzgn(ManagerFlag.PDA_XCXJ_CLYZ + "");
				} else {
					offData.setCzgn(ManagerFlag.PDA_XCXJ_XJJL + "");
				}
				offData.setCzmk(ManagerFlag.PDA_XCXJ + "");
				offData.setGxsj(new Date());
				offData.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
				offData.setUserid(BaseApplication.instent.gainUserID());
				offData.setCzid(xcxsid);
				offData.setXmldata(ycxx);
				dataService.insert(offData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			datas.put("result", "success");
			info.put("tsxx", yfResult.getTsxx());// 验证结果后续增加。
			if (yfResult.isResult()) {
				info.put("isPass", "pass");// 是否验证通过
			} else {
				info.put("isPass", "false");// 是否验证通过
			}

			info.put("xcxsid", xcxsid);// 巡查巡视记录ID
			info.put("sfdk", "0");// 是否搭靠：0否，1是
			info.put("dkjlid", null);// 搭靠记录ID
			info.put("cgcsid", null);// 查岗查哨记录ID
			info.put("hgzl", hgzjxx.getZjlb());// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。

			// 船上：针对登轮证或者登陆证展示验证结果、证件信息及该登轮人员针对此船舶的通行记录
			// 船下：只显示登陆证或者登轮证的证件信息，不做验证。如果是登陆证显示该登陆人员服务船舶的通行记录
			info.put("zjxx", buildInfoZjxxByCbzjffxx(hgzjxx, isClyz));
			// HashBuild txjl = buildTxjl(hgzjxx);// 最近五条通行记录
			// info.put("txjl", txjl);

			datas.put("info", info);

		} else {// 船下
			// 判断是否有巡检地点，没有则返回提示信息
			if (StringUtils.isEmpty(params.get("ddID") + "")) {
				datas.put("result", "error");
				datas.put("info", "请刷所在位置的信息钉！");
			} else {
				String ycxx = buildYcxxByCbzjffxx(hgzjxx, xcxsid, params, isClyz);
				try {
					OffDataService dataService = new OffDataService();
					OffData offData = new OffData();
					offData.setCjsj(new Date());
					offData.setClstatus("0");
					offData.setCzgn(ManagerFlag.PDA_XCXJ_XJJL + "");
					offData.setCzmk(ManagerFlag.PDA_XCXJ + "");
					offData.setGxsj(new Date());
					offData.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
					offData.setUserid(BaseApplication.instent.gainUserID());
					offData.setCzid(xcxsid);
					offData.setXmldata(ycxx);
					dataService.insert(offData);
				} catch (Exception e) {

				}
				datas.put("result", "success");
				info.put("tsxx", yfResult.getTsxx());// 船下不验证
				info.put("xcxsid", xcxsid);// 巡查巡视记录ID
				if (yfResult.isResult()) {
					info.put("isPass", "pass");// 是否验证通过
				} else {
					info.put("isPass", "false");// 是否验证通过
				}
				info.put("sfdk", "0");// 是否搭靠：0否，1是
				info.put("dkjlid", null);// 搭靠记录ID
				info.put("hgzl", hgzjxx.getZjlb());// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
				info.put("zjxx", buildInfoZjxxByCbzjffxx(hgzjxx, isClyz));
				datas.put("info", info);
			}
		}
		return datas;
	}

	/**
	 * 
	 * @方法名：buildYcxxByCbzjffxx
	 * @功能说明：根据船舶证件发放对象构建异常信息
	 * @author liums
	 * @date 2012-12-26 下午03:18:48
	 * @param hgzjxx
	 *            船舶证件发放对象
	 * @param isClyz
	 *            TODO
	 * @return 异常信息对象
	 */
	private String buildYcxxByCbzjffxx(Hgzjxx hgzjxx, String xcxsid, Map<String, Object> params, boolean isClyz) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("xcxsid", xcxsid);
		map.put("sjid", StringUtils.UIDGenerator());
		map.put("jcr", BaseApplication.instent.gainUserID());
		if (isClyz) {
			map.put("jcdx", hgzjxx.getCphm());
			map.put("dxlx", "02");// 01人员 ， 02车辆
		} else {
			map.put("jcdx", hgzjxx.getXm());
			map.put("dxlx", "01");// 01人员 ， 02车辆
		}
		map.put("zjhm", hgzjxx.getZjhm());
		map.put("zjlx", hgzjxx.getZjlbdm());
		map.put("xm", hgzjxx.getXm());
		map.put("xb", hgzjxx.getXbdm());
		// 附加经纬度信息
		map.put("jwd", XcUtil.buildJwd());
		map.put("gj", hgzjxx.getGjdqdm());
		map.put("csrq", hgzjxx.getCsrq());
		map.put("ssdw", (String) params.get("cbzwm"));
		map.put("xxly", "03");
		map.put("jcsj", DateUtils.dateToString(new Date()));
		map.put("jcfs", "02");
		map.put("jcqk", "01");
		if (isClyz) {
			map.put("jsyxm", hgzjxx.getXm()); // 驾驶员姓名x
			// map.put("xb", hgzjxx.getXbdm()); //性别 男1、女2x
			// map.put("csrq", DateUtils.gainBirthday(hgzjxx.getCsrq()));
			// //出生日期,原格式20121212，需要进行格式转换，2012-12-12x
			// map.put("gj", hgzjxx.getGjdqdm()); //国籍对应数据字典x

			// map.put("zjhm", hgzjxx.getZjhm()); //证件号码x
			map.put("ryid", hgzjxx.getCbzjffxxxid()); // 人员IDx
			map.put("zjzl", hgzjxx.getZjlbdm()); // 证件类型对应数据字典

			map.put("jszh", hgzjxx.getJsyhm());// 驾驶证编号
			map.put("ssdw", hgzjxx.getSsdw()); // 所属单位。
			map.put("lxfs", hgzjxx.getLxfs()); // 联系方式

			map.put("cphm", hgzjxx.getCphm()); // 车牌号码
			map.put("clsbdh", hgzjxx.getClsbdh()); // 车牌号码
			map.put("fdjh", hgzjxx.getFdjh()); // 发动机号
			map.put("cllx", hgzjxx.getCllx()); // 车辆类型
			map.put("gjdq", hgzjxx.getGj()); // 国家地区（车辆）
			map.put("clpp", hgzjxx.getClpp()); // 车辆品牌
			map.put("clys", hgzjxx.getClys()); // 车辆颜色
			map.put("gs_yyz", hgzjxx.getGsyyz());// 公司拥有者
			map.put("syfw", hgzjxx.getSsfwNameShow());// 所属范围
		}

		if (StringUtils.isNotEmpty(params.get("voyageNumber"))) {
			params.put("voyageNumber", (String) params.get("voyageNumber"));
			KacbqkService kacbqkService = new KacbqkService();
			Kacbqk kacbqk = kacbqkService.getKacbqkByHC((String) params.get("voyageNumber"));
			map.put("cbmc", kacbqk.getCbzwm());
			map.put("glcbmc", kacbqk.getCbzwm());
			if (kacbqk != null) {
				map.put("jcdd", getJcddByCl(SystemSetting.xunJianType, (String) params.get("voyageNumber"), kacbqk));
				map.put("berthcode", kacbqk.getTkbw());
				map.put("dockcode", kacbqk.getTkmt());
				map.put("berthname", SystemSetting.xunJianName);
				map.put("dockname", SystemSetting.xunJianMTname);
			}
		} else {
			map.put("jcdd", getJcdd(SystemSetting.xunJianType, (String) params.get("voyageNumber")));
		}

		map.put("txsj", DateUtils.dateToString(new Date()));// 通行时间
		if (SystemSetting.xunJianId != null && SystemSetting.xunJianId.length() > 0) {
			if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
				String bwid = SystemSetting.xunJianId;
				String mtid = SystemSetting.xunJianMTid;
				String bwdm = BwdmUtil.getBwdmByBwid(bwid);
				String mtdm = MtdmUtil.getMtdmByMtid(mtid);

				map.put("berthcode", bwdm);
				map.put("berthname", SystemSetting.xunJianName);
				map.put("dockcode", mtdm);
				map.put("dockname", SystemSetting.xunJianMTname);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
				String mtid = SystemSetting.xunJianId;
				String mtdm = MtdmUtil.getMtdmByMtid(mtid);
				map.put("dockcode", mtdm);
				map.put("dockname", SystemSetting.xunJianMTname);
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
				map.put("areacode", SystemSetting.xunJianId);
				map.put("areaname", SystemSetting.xunJianName);
			}
			
//			if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
//				map.put("berthcode", SystemSetting.xunJianId);
//				map.put("berthname", SystemSetting.xunJianName);
//				map.put("dockcode", SystemSetting.xunJianMTid);
//				map.put("dockname", SystemSetting.xunJianMTname);
//			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
//				map.put("dockcode", SystemSetting.xunJianMTid);
//				map.put("dockname", SystemSetting.xunJianMTname);
//			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
//				map.put("areacode", SystemSetting.xunJianId);
//				map.put("areaname", SystemSetting.xunJianName);
//			}
		}
		String xcxj = XmlUtils.buildXml(map);
		return xcxj;

	}

	/**
	 * 
	 * @方法名：buildInfoZjxxByCbzjffxx
	 * @功能说明：构建不同业务下的info对象
	 * @author liums
	 * @date 2013-8-7 下午04:41:47
	 * @param hgzjxx
	 *            证件信息
	 * @param isClyz
	 *            TODO
	 * @return
	 */
	private HashBuild buildInfoZjxxByCbzjffxx(Hgzjxx hgzjxx, boolean isClyz) {

		HashBuild zjxx = new HashBuild(100);
		zjxx.put("xm", hgzjxx.getXm());// 姓名
		zjxx.put("xb", hgzjxx.getXbdm());// 性别 男1、女2
		zjxx.put("csrq", hgzjxx.getCsrq());// 出生日期
		zjxx.put("zw", hgzjxx.getZwdm());// 职务代码
		zjxx.put("gj", hgzjxx.getGjdqdm());// 国籍代码取数据字典
		zjxx.put("zjhm", hgzjxx.getZjhm());// 证件号码
		zjxx.put("zjlx", hgzjxx.getZjlbdm());// 证件类型代码取数据字典
		zjxx.put("ssdw", hgzjxx.getSsdw());// 所属单位
		if (isClyz) {
			zjxx.put("jszh", hgzjxx.getJsyhm());// 驾驶证编号x
			zjxx.put("lxfs", hgzjxx.getLxfs()); // 联系方式
			zjxx.put("cphm", hgzjxx.getCphm()); // 车牌号码
			zjxx.put("fdjh", hgzjxx.getFdjh()); // 发动机号
			zjxx.put("cllx", hgzjxx.getCllx()); // 车辆类型
			zjxx.put("gjdq", hgzjxx.getGj()); // 国家地区（车辆）
			zjxx.put("clpp", hgzjxx.getClpp()); // 车辆品牌
			zjxx.put("clsbdh", hgzjxx.getClsbdh()); // 车牌号码
			zjxx.put("clys", hgzjxx.getClys()); // 车辆颜色
			zjxx.put("gs_yyz", hgzjxx.getGsyyz());// 公司拥有者
		}
		// 登轮证，增加全港通用判断。
		// String sdcbStr = hgzjxx.getZwcbm();
		String sdcbStr = HgzjxxUtil.getFffwInfo(hgzjxx, isClyz);
		// if (hgzjxx.getHc() == null || "".equals(hgzjxx.getHc())) {
		// sdcbStr = "全港通用";
		// }

		zjxx.put("sdcb", sdcbStr);// 所登船舶，船下检查人员时显示（参考原型）。
		zjxx.put("syfw", sdcbStr);// 所登船舶，船下检查人员时显示（参考原型）。

		// 有效期
		if ("1".equals(hgzjxx.getYcyxbz() == null ? "" : hgzjxx.getYcyxbz())) {
			// 本航次有效
			zjxx.put("yxq", BaseApplication.instent.getString(R.string.bhcyx));
		} else {
			zjxx.put("yxq",
					new StringBuffer().append(DateUtils.DateToString(hgzjxx.getYxqq())).append(BaseApplication.instent.getString(R.string.zhi))
							.append(DateUtils.DateToString(hgzjxx.getYxqz())).toString());
		}
		// zjxx.put("bhcyx", bhcyxStr);// 本航次有效 ycyxbz,1是，2否
		YfResult yfResult = new YfResult();
		yfResult.setZjxx(hgzjxx);
		HgzjxxUtil.addPzxx(zjxx, yfResult);
		return zjxx;
	}

	private HashBuild buildInfoZjxxByCyxx(TBCyxx cyxx) {
		HashBuild zjxx = new HashBuild(100);
		zjxx.put("xm", cyxx.getXm());// 姓名
		zjxx.put("xb", cyxx.getXb());// 性别 男1、女2
		zjxx.put("csrq", DateUtils.DateToString(cyxx.getCsrq()));// 出生日期
		zjxx.put("zw", cyxx.getZw());// 职务代码
		zjxx.put("gj", cyxx.getGj());// 国籍代码取数据字典
		zjxx.put("zjhm", cyxx.getZjhm());// 证件号码
		zjxx.put("zjlx", cyxx.getZjlx());// 证件类型代码取数据字典
		zjxx.put("ssdw", cyxx.getCbzwm());// 所属单位
		zjxx.put("yxq", null);// 有效期限
		zjxx.put("bhcyx", "是");// 本航次有效
		YfResult yfResult = new YfResult();
		yfResult.setCyxx(cyxx);
		HgzjxxUtil.addPzxx(zjxx, yfResult);
		return zjxx;
	}

	/**
	 * 
	 * @方法名：dkOperation
	 * @功能说明：搭靠业务
	 * @author liums
	 * @date 2013-8-7 下午04:40:17
	 * @param yfResult
	 *            验放结果
	 * @return 返回到客户端的数据
	 */
	private HashBuild dkOperation(YfResult yfResult, Map<String, Object> params) {
		HashBuild datas = new HashBuild(20);
		HashBuild info = new HashBuild(20);
		Hgzjxx hgzjxx = yfResult.getZjxx();
		datas.put("result", "success");
		boolean result = yfResult.isResult();// 验证结果
		if (result) {
			String dkid = StringUtils.UIDGenerator();
			info.put("dkjlid", dkid);// 搭靠记录ID
			info.put("isPass", "pass");// 是否验证通过
			String txfx = getHgzjxxTxfx(hgzjxx.getZjhm(), (String) params.get("voyageNumber"));// 查询上下方向
			info.put("sxcfx", txfx);// 本次上下船方向
			// 验证通过保存一条搭靠记录
			saveDkjl(hgzjxx, dkid, params);

		} else {
			info.put("sxcfx", null);// 上下船方向
			info.put("isPass", "false");// 是否验证通过
			info.put("dkjlid", null);// 搭靠记录ID
		}

		info.put("sfdk", "1");// 是否搭靠：0否，1是
		info.put("tsxx", yfResult.getTsxx());
		info.put("hgzl", YfZjxxConstant.ZJLX_DK);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。

		HashBuild dkxx = new HashBuild(10);
		dkxx.put("zjhm", hgzjxx.getZjhm());// 证件号码
		dkxx.put("ryid", hgzjxx.getCbzjffxxxid());// 人员ID
		dkxx.put("cbmc", hgzjxx.getZwcbm());// 船舶名称
		dkxx.put("cgj", hgzjxx.getCjg());// 船港籍
		dkxx.put("zzdw", hgzjxx.getDw() == 0 ? "" : hgzjxx.getDw().toString());// 载重吨位
		dkxx.put("ml", hgzjxx.getMl() == 0 ? "" : hgzjxx.getMl().toString());// 马力
		dkxx.put("ssdw", hgzjxx.getSsdw());// 所属单位
		dkxx.put("yt", hgzjxx.getYt());// 用途
		if (hgzjxx.getFffw().equals("1")) {
			dkxx.put("dkfw", hgzjxx.getDkcb());
		} else if (hgzjxx.getFffw().equals("2")) {
			dkxx.put("dkfw", "");
		} else {
			dkxx.put("dkfw", "全港通用");
		}
		// 有效期
		if ("1".equals(hgzjxx.getYcyxbz() == null ? "" : hgzjxx.getYcyxbz())) {
			// 本航次有效
			dkxx.put("yxq", BaseApplication.instent.getString(R.string.bhcyx));
		} else {
			dkxx.put("yxq",
					new StringBuffer().append(DateUtils.DateToString(hgzjxx.getYxqq())).append(BaseApplication.instent.getString(R.string.zhi))
							.append(DateUtils.DateToString(hgzjxx.getYxqz())).toString());
		}
		dkxx.put("zjlx", hgzjxx.getZjlb());// 证件类型对应数据字典
		info.put("dkxx", dkxx);
		datas.put("info", info);
		return datas;
	}

	/**
	 * 
	 * @方法名：getHgzjxxTxfx
	 * @功能说明：得到本次上下船方向，登轮人员默认上船
	 * @author liums
	 * @date 2013-10-21 下午3:45:09
	 * @param txjlTk
	 */
	private String getHgzjxxTxfx(String zjhm, String hc) {
		TxjlTkService txjlTkService = new TxjlTkService();
		TxjlTk txjlTk = txjlTkService.getTxjlTkByZjhmAndHc(zjhm, hc);
		if (txjlTk == null) {
			return TXFX_SC;
		}
		String txfx = txjlTk.getTxfx();
		if (TXFX_XC.equals(txfx)) {
			return TXFX_SC;
		}
		return TXFX_XC;
	}

	/***
	 * 
	 * @方法名：saveDkjl
	 * @功能说明：保存搭靠记录
	 * @author 娄高伟
	 * @date 2013-10-22 下午3:04:07
	 * @param hgzjxx
	 * @param dkid
	 */
	private void saveDkjl(Hgzjxx hgzjxx, String dkid, Map<String, Object> params) {// 离船操作
		String time = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("zjffxxid", hgzjxx.getCbzjffxxxid());// 证件 ID
		map.put("kacbqkid", params.get("kacbqkid") + "");// 搭靠船舶ID
		map.put("zjlx", hgzjxx.getZjlb());// 证件 ID
		map.put("zjlbdm", hgzjxx.getZjlbdm());// 证件 ID
		map.put("cm", hgzjxx.getZwcbm());// 船舶中文名
		map.put("ssdw", hgzjxx.getSsdw());// 所属单位
		map.put("txsj", DateUtils.dateToString(new Date()));// 通行时间
		// 判断本次搭靠方向
		String dkfx = getDkqkDkfx(hgzjxx, params.get("kacbqkid") + "");
		map.put("dkfx", dkfx);// 离船时间
		if (YfZjxxConstant.TXFX_SC.equals(dkfx)) {// 本次搭靠
			map.put("dkzqr", LoginUser.getCurrentLoginUser().getUserID());// 搭靠执勤人
			map.put("cbdksj", time);// 搭靠时间
		} else {
			map.put("lczqr", LoginUser.getCurrentLoginUser().getUserID());// 离船执勤人
			map.put("cblcsj", time);// 离船时间
		}
		String xmlData = XmlUtils.buildXml(map);

		OffData offData = new OffData();
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_TKGL + "");
		offData.setCzgn(ManagerFlag.PDA_TKGL_CBDK + "");
		offData.setCzid(dkid);
		OffDataService dataService = new OffDataService();
		try {
			dataService.insert(offData);
			// 保存成功，更新Dkqk表通行方向
			updateDkqk(hgzjxx, params.get("kacbqkid") + "", dkfx, dkid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：getDkqkTxfx
	 * @功能说明：获取搭靠方向
	 * @author liums
	 * @date 2013-10-22 下午1:52:20
	 * @param hgzjxx
	 * @param kacbqkid
	 * @return
	 */
	private String getDkqkDkfx(Hgzjxx hgzjxx, String kacbqkid) {
		DkqkService dkqkService = new DkqkService();
		Dkqk dkqk = null;
		try {
			dkqk = dkqkService.findDkqkByHgzjxx(hgzjxx, kacbqkid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (dkqk == null) {
			return TXFX_SC;
		}
		String txfx = dkqk.getDkfx();
		if (TXFX_SC.equals(txfx)) {
			return TXFX_XC;
		}
		return TXFX_SC;
	}

	/**
	 * 
	 * @方法名：updateDkqk 功能说明：更新搭靠方向
	 * @author liums
	 * @date 2013-10-22 下午2:15:55
	 * @param hgzjxx
	 * @param kacbqkid
	 */
	private void updateDkqk(Hgzjxx hgzjxx, String kacbqkid, String dkfx, String dkqkid) {
		DkqkService dkqkService = new DkqkService();
		try {
			dkqkService.saveOrUpdateDkfx(hgzjxx, kacbqkid, dkfx, dkqkid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private HashBuild buildInfoWithoutShip(Hgzjxx hgzjxx, Map<String, Object> params) {
		HashBuild datas = new HashBuild(20);
		HashBuild info = new HashBuild(20);
		String xcxsid = StringUtils.UIDGenerator();
		// 判断是否有巡检地点，没有则返回提示信息
		if (StringUtils.isEmpty((String) params.get("ddID"))) {
			datas.put("result", "error");
			datas.put("info", "请刷所在位置的信息钉！");
		} else {
			if (YfZjxxConstant.ZJLX_DK.equals(hgzjxx.getZjlb())) {
				datas.put("result", "error");
				datas.put("info", "船舶搭靠，请先绑定船舶！");
				return datas;
			}
			String ycxx = buildYcxxByCbzjffxx(hgzjxx, xcxsid, params, false);
			try {
				OffDataService dataService = new OffDataService();
				OffData offData = new OffData();
				offData.setCjsj(new Date());
				offData.setClstatus("0");
				offData.setCzgn(ManagerFlag.PDA_XCXJ_XJJL + "");
				offData.setCzmk(ManagerFlag.PDA_XCXJ + "");
				offData.setGxsj(new Date());
				offData.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
				offData.setUserid(BaseApplication.instent.gainUserID());
				offData.setCzid(xcxsid);
				offData.setXmldata(ycxx);
				dataService.insert(offData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			datas.put("result", "success");
			info.put("tsxx", "船下检查不进行验证");// 船下不验证
			info.put("xcxsid", xcxsid);// 巡查巡视记录ID
			info.put("sfdk", "0");// 是否搭靠：0否，1是
			info.put("dkjlid", null);// 搭靠记录ID
			info.put("hgzl", hgzjxx.getZjlb());// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
			info.put("zjxx", buildInfoZjxxByCbzjffxx(hgzjxx, false));
			datas.put("info", info);
		}
		return datas;
	}

	/**
	 * 
	 * @方法名：cgcsOperation
	 * @功能说明：查岗查哨业务，不保存查岗查哨记录。
	 * @author liums
	 * @date 2013-3-26 下午05:25:11
	 * @return
	 */
	private HashBuild cgcsOperation(TBUserinfo userInfoBySBK, String cbzwm) {
		String zqdd = null;
		if (StringUtils.isNotEmpty(cbzwm)) {
			zqdd = cbzwm;
		} else {
			zqdd = SystemSetting.xunJianName;
		}
		HashBuild datas = new HashBuild(20);
		HashBuild info = new HashBuild(20);
		HashBuild zjxx = new HashBuild(20);
		if (userInfoBySBK != null) {
			datas.put("result", "success");
			info.put("tsxx", null);// 验证结果后续增加。
			info.put("xcxsid", null);// 巡查巡视记录ID
			info.put("sfdk", "0");// 是否搭靠：0否，1是
			info.put("dkjlid", null);// 搭靠记录ID
			info.put("cgcsid", null);// 查岗查哨记录ID
			info.put("hgzl", null);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。

			zjxx.put("xm", userInfoBySBK.getXm());// 姓名
			zjxx.put("zw", userInfoBySBK.getZw());// 职务代码,转换职务代码到名称
			zjxx.put("ssdw", userInfoBySBK.getSsjgid());// 所属单位
			zjxx.put("zjlx", "sbk");
			zjxx.put("sbkid", userInfoBySBK.getId());
			zjxx.put("zqdd", zqdd);// 执勤地点
			info.put("zjxx", zjxx);
			datas.put("info", info);
		} else {
			datas.put("result", "error");
			datas.put("info", "证件信息不存在，请核对后重试！");
		}
		return datas;
	}
}
