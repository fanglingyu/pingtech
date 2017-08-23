package com.pingtech.hgqw.module.kakou.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.utils.HgzjxxUtil;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlKk;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlKkService;
import com.pingtech.hgqw.module.offline.zjyf.entity.YfResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.offline.zjyf.yfimpl.PdaYfxxImpl;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.HashBuild;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class KakouAction implements BaseAction {

	/** 通行方向-上船 */
	private static final String TXFX_SC = "0";

	/** 通行方向-下船 */
	private static final String TXFX_XC = "1";

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if ("inspectForKk".equals(method)) {
			return inspectForKk(params);
		} else if ("sendPassInfo".equals(method)) {
			return new Pair<Boolean, Object>(true, XmlUtils.buildXml(sendPassInfo(params).get()));
		} else if ("sendClPassInfo".equals(method)) {
			return new Pair<Boolean, Object>(true, XmlUtils.buildXml(sendClPassInfo(params).get()));
		} else if ("buildKkRelation".equals(method)) {
			return new Pair<Boolean, Object>(false, null);
		} else if ("modifyKkPassDirection".equals(method)) {
			return modifyPassDirection(params);
		} else if ("modifyKkClPassDirection".equals(method)) {
			return modifyClPassDirection(params);
		} else if ("inspectClForKk".equals(method)) {
			return inspectClForKk(params);
		}
		return null;

	}

	private Pair<Boolean, Object> inspectClForKk(Map<String, Object> params) {
		HashBuild datas = new HashBuild(100);
		HashBuild info = new HashBuild(100);
		PdaYfxxImpl pdaYfxx = new PdaYfxxImpl();
		String cardNumber = (String) params.get("cardNumber");
		String defaultickey = (String) params.get("defaultickey");
		String jszbh_sfzh = (String) params.get("jszbh_sfzh");
		String cphm = (String) params.get("cphm");
		String sfsk = (String) params.get("sfsk");
		String kkID = (String) params.get("kkID");
		YfResult yfResult = pdaYfxx.zjIsAvailable(cardNumber, cardNumber, defaultickey, sfsk, null, kkID, YfZjxxConstant.YFFS_PDAKK, cphm, true, jszbh_sfzh);
		if (yfResult != null && (yfResult.getZjxx() != null || yfResult.getCyxx() != null)) {// 卡号存在
			if (YfZjxxConstant.ZJLX_DK.equals(yfResult.getZjlx())) {
				return dkBusiness(datas);
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx()) && YfZjxxConstant.ZJ_QYLXBS_CL.equals(yfResult.getZjxx().getQylxbs())) {
				datas.put("result", "success");
				info = hgzjxxBusinessCl(yfResult, params);
				HgzjxxUtil.addPzxx(info, yfResult);
				datas.put("info", info);
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx()) && YfZjxxConstant.ZJ_QYLXBS_RY.equals(yfResult.getZjxx().getQylxbs())) {
				datas.put("result", "error");
				datas.put("info", "您刷的是人员限定区域通行证，请在刷卡登记模块下刷卡");
			} else if (YfZjxxConstant.ZJLX_DLUN.equals(yfResult.getZjlx())) {
				datas.put("result", "error");
				datas.put("info", "您刷的是登轮许可证，请在刷卡登记模块下刷卡");
			} else {
				// datas.put("result", "success");
				// info = cyxxBusiness(yfResult, params);// 船员
			}
		} else if (yfResult != null && yfResult.isToast()) {
			datas.put("result", "error");
			datas.put("info", yfResult.getTsxx());
			Pair pair = new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			return pair;
		} else {
			datas.put("result", "success");
			datas.put("info", "验证失败，不是边防证件");
			Pair pair = new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			return pair;
		}
		Pair pair = new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
		return pair;
	}

	/**
	 * 
	 * @方法名：inspectForKk
	 * @功能说明：卡口验放业务
	 * @author liums
	 * @date 2013-10-29 下午3:56:10
	 * @param params
	 * @return
	 */
	private Pair<Boolean, Object> inspectForKk(Map<String, Object> params) {
		HashBuild datas = new HashBuild(100);
		HashBuild info = new HashBuild(100);
		PdaYfxxImpl pdaYfxx = new PdaYfxxImpl();
		String cardNumber = (String) params.get("cardNumber");
		String defaultickey = (String) params.get("defaultickey");
		String sfsk = (String) params.get("sfsk");
		String kkID = (String) params.get("kkID");
		YfResult yfResult = pdaYfxx.zjIsAvailable(cardNumber, cardNumber, defaultickey, sfsk, null, kkID, YfZjxxConstant.YFFS_PDAKK, null, false, null);
		if (yfResult != null && (yfResult.getZjxx() != null || yfResult.getCyxx() != null)) {// 卡号存在
			if (YfZjxxConstant.ZJLX_DK.equals(yfResult.getZjlx())) {
				return dkBusiness(datas);
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx()) && yfResult.getZjxx() != null
					&& YfZjxxConstant.ZJ_QYLXBS_CL.equals(yfResult.getZjxx().getQylxbs())) {
				datas.put("result", "error");
				datas.put("isClzj", "true");
				datas.put("info", "您刷的是车辆限定区域通行证，请在车辆验放模块下刷卡");
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			} else if (YfZjxxConstant.ZJLX_DLUN.equals(yfResult.getZjlx()) || YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx())) {
				datas.put("result", "success");
				info = hgzjxxBusiness(yfResult, params);
			} else {
				datas.put("result", "success");
				info = cyxxBusiness(yfResult, params);// 船员
			}
		} else {
			datas.put("result", "success");
			info.put("tsxx", "验证失败，不是边防证件");
			datas.put("info", info);
		}
		HgzjxxUtil.addPzxx(info, yfResult);
		datas.put("info", info);
		return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
	}

	/**
	 * 
	 * @方法名：dkBusiness
	 * @功能说明：搭靠业务
	 * @author liums
	 * @date 2013-10-29 下午3:57:20
	 * @param datas
	 * @return
	 */
	private Pair<Boolean, Object> dkBusiness(HashBuild datas) {
		datas.put("result", "error");
		datas.put("info", "您刷的是搭靠外轮许可证，在卡口不做验证！");
		return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
	}

	/**
	 * 
	 * @方法名：modifyPassDirection
	 * @功能说明：修改通行方向
	 * @author liums
	 * @date 2013-10-23 下午1:57:08
	 * @param params
	 * @return
	 */
	private Pair<Boolean, Object> modifyPassDirection(Map<String, Object> params) {
		String recordid = (String) params.get("recordid");// 获取要修改的通行记录ID
		String hgzl = (String) params.get("hgzl"); // 离线业务使用字段
		String kkid = (String) params.get("kkID"); // 离线业务使用字段
		String kkmc = (String) params.get("kkmc");// 卡口名称
		String ryid = (String) params.get("ryid"); // 离线业务使用字段
		String passDirection = (String) params.get("passDirection");// 修改的方向
		String result = "-1";
		// 删除离线业务的通行记录，插入一条新的记录，并修改方向标识
		OffDataService offDataService = new OffDataService();
		try {
			offDataService.deleteByCzid(recordid);
			// 插入一条新的记录
			if (YfZjxxConstant.ZJLX_DLUN.equals(hgzl) || YfZjxxConstant.ZJLX_XDQY.equals(hgzl)) {
				HgzjxxService hgzjxxService = new HgzjxxService();
				Hgzjxx hgzjxx = null;
				hgzjxx = hgzjxxService.findById(ryid);
				String txjlkkId = StringUtils.UIDGenerator();

				saveTxjlByHgzjxx(hgzjxx, passDirection, txjlkkId, kkid, kkmc, false);
			} else {
				CyxxService cyxxService = new CyxxService();
				TBCyxx tbCyxx = null;
				tbCyxx = cyxxService.findById(ryid);
				String txjltkId = StringUtils.UIDGenerator();
				saveTxjlByCyxx(tbCyxx, passDirection, txjltkId, kkid, kkmc);
			}
			result = "1";
		} catch (SQLException e) {
			e.printStackTrace();
			result = "-1";
		}
		return new Pair<Boolean, Object>(true, result);
	}

	/**
	 * 
	 * @方法名：modifyPassDirection
	 * @功能说明：修改通行方向
	 * @author liums
	 * @date 2013-10-23 下午1:57:08
	 * @param params
	 * @return
	 */
	private Pair<Boolean, Object> modifyClPassDirection(Map<String, Object> params) {
		String recordid = (String) params.get("recordid");// 获取要修改的通行记录ID
		String hgzl = (String) params.get("hgzl"); // 离线业务使用字段
		String kkid = (String) params.get("kkID"); // 离线业务使用字段
		String kkmc = (String) params.get("kkmc");// 卡口名称
		String ryid = (String) params.get("ryid"); // 离线业务使用字段
		String passDirection = (String) params.get("passDirection");// 修改的方向
		String result = "-1";
		// 删除离线业务的通行记录，插入一条新的记录，并修改方向标识
		OffDataService offDataService = new OffDataService();
		try {
			offDataService.deleteByCzid(recordid);
			// 插入一条新的记录
			if (YfZjxxConstant.ZJLX_DLUN.equals(hgzl) || YfZjxxConstant.ZJLX_XDQY.equals(hgzl)) {
				HgzjxxService hgzjxxService = new HgzjxxService();
				Hgzjxx hgzjxx = null;
				hgzjxx = hgzjxxService.findById(ryid);
				String txjlkkId = StringUtils.UIDGenerator();

				saveTxjlByHgzjxx(hgzjxx, passDirection, txjlkkId, kkid, kkmc, true);
			} else {
				CyxxService cyxxService = new CyxxService();
				TBCyxx tbCyxx = null;
				tbCyxx = cyxxService.findById(ryid);
				String txjltkId = StringUtils.UIDGenerator();
				saveTxjlByCyxx(tbCyxx, passDirection, txjltkId, kkid, kkmc);
			}
			result = "1";
		} catch (SQLException e) {
			e.printStackTrace();
			result = "-1";
		}
		return new Pair<Boolean, Object>(true, result);
	}

	private HashBuild sendPassInfo(Map<String, Object> params) {
		String time = null;
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashBuild datas = new HashBuild(10);
		HashBuild info = new HashBuild(10);
		String txjlid = StringUtils.UIDGenerator();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("txgz", YfZjxxConstant.FILO);// 登轮人员FILO先进后出、船员FOLI先出后进
			map.put("txjlid", txjlid);
			map.put("txdx", params.get("xm"));// 通行人员姓名
			map.put("txsj", time);// 通行时间
			map.put("dxlx", "01");// 对象类型，固定传01
			map.put("txkkid", params.get("kkID"));// 卡口id
			map.put("txkkmc", "");// 卡口名称
			map.put("ryxb", params.get("xb"));// 通行人员性别
			map.put("txrylx", "01"); // 通行人员类型，01人员
			map.put("rygj", params.get("gj"));// 通行人员所属国籍代码
			map.put("ssdw", params.get("ssdw"));// 通行人员所属单位
			map.put("zw", params.get("zw")); // 通行人员职务

			map.put("zjlx", YfZjxxConstant.ZJLX_WZ);// 证件类型
			map.put("zjlbdm", params.get("zjzl"));// 证件类别代码
			map.put("jckklx", YfZjxxConstant.JCKKLX_JRZ);
			map.put("sfcyjs", "");// 是否船员家属-0非船员家属，1船员家属
			map.put("lcbz", "");// 离船 登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)
			map.put("skzj", "");// 刷卡主键 记录使用什么证件或船员进出通行的 证件使用zjbh（证件编号）,船员使用船员主键

			map.put("zjhm", params.get("zjhm"));// 证件号码，如果身份证传身份证号
			map.put("skkh", "");// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
			map.put("ickey ", "");// ic卡号，如果身份证传身份证号，如果ic卡传写入的数据
			map.put("pdacode", DeviceUtils.getIMEI(BaseApplication.instent));// 设备编号
			map.put("txfx", params.get("fx"));// 通行方向
			map.put("pdbs", "");// 判断标识
			if ("0".equals(params.get("fx"))) {
				map.put("jrkksj", time);// 进入卡口时间
				map.put("jrkkyffs", "01"); // 进入卡口验放方式
				map.put("jrkkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 进入卡口验放设备标识，设备编号
			} else if ("1".equals(params.get("fx"))) {
				map.put("ckksj", time); // 出卡口验放时间
				map.put("ckkyffs", "01"); // 出卡口验放方式
				map.put("ckkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 出卡口验放设备标识，设备编号
			} else {
				map.put("jrkksj", "");// 进入卡口时间
				map.put("jrkkyffs", ""); // 进入卡口验放方式
				map.put("jrkkyfsbbs", "");// 进入卡口验放设备标识，设备编号
				map.put("ckksj", ""); // 出卡口验放时间
				map.put("ckkyffs", ""); // 出卡口验放方式
				map.put("ckkyfsbbs", "");// 出卡口验放设备标识，设备编号
			}

			map.put("ldfxbs", ""); // 漏打方向标识
			map.put("zqry", LoginUser.getCurrentLoginUser().getName());// 执勤人员姓名
			map.put("zqryid", LoginUser.getCurrentLoginUser().getUserID()); // 执勤人员ID
			map.put("dwdm", "");// 执勤人员单位代码(平台根据执勤人员id查询)
			map.put("kadm", "");// 口岸代码(平台根据执勤人员id查询)
			map.put("zdkadm", "");// 总队口岸代码(平台根据执勤人员id查询)
			String xmldata = XmlUtils.buildXml(map);
			OffData data = new OffData();
			data.setCjsj(new Date());
			data.setClstatus("0");
			data.setCzgn(ManagerFlag.PDA_KKGL_TXYZ + "");
			data.setCzmk(ManagerFlag.PDA_KKGL + "");
			data.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
			data.setUserid(BaseApplication.instent.gainUserID());
			data.setXmldata(xmldata);
			OffDataService service = new OffDataService();
			service.insert(data);
			datas.put("result", "success");
			info.put("txjlid", txjlid);
			info.put("cgcsid", null);
			info.put("xcxsid", null);
			datas.put("info", info);
			return datas;
		} catch (Exception e) {
			datas.put("result", "error");
			datas.put("info", "保存卡口记录错误，请稍后重试！");
			return datas;
		}

	}

	private HashBuild sendClPassInfo(Map<String, Object> params) {
		String time = null;
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashBuild datas = new HashBuild(10);
		HashBuild info = new HashBuild(10);
		String txjlid = StringUtils.UIDGenerator();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("txgz", YfZjxxConstant.FILO);// 登轮人员FILO先进后出、船员FOLI先出后进
			map.put("txjlid", txjlid);
			map.put("txsj", time);// 通行时间
			map.put("dxlx", "02");// 对象类型，固定传01
			map.put("txkkid", params.get("kkID"));// 卡口id
			map.put("txkkmc", params.get("txkkmc"));// 卡口名称
			map.put("ryxb", params.get("xb"));// 通行人员性别
			map.put("txrylx", "01"); // 通行人员类型，01人员
			map.put("rygj", params.get("gj"));// 通行人员所属国籍代码
			map.put("ssdw", params.get("ssdw"));// 通行人员所属单位
			map.put("zw", params.get("zw")); // 通行人员职务

			map.put("zjlx", YfZjxxConstant.ZJLX_WZ);// 证件类型
			map.put("zjlbdm", params.get("zjzl"));// 证件类别代码
			map.put("jckklx", YfZjxxConstant.JCKKLX_JRZ);
			map.put("sfcyjs", "");// 是否船员家属-0非船员家属，1船员家属
			map.put("lcbz", "");// 离船 登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)
			map.put("skzj", "");// 刷卡主键 记录使用什么证件或船员进出通行的 证件使用zjbh（证件编号）,船员使用船员主键

			map.put("zjhm", params.get("zjhm"));// 证件号码，如果身份证传身份证号
			map.put("skkh", "");// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
			map.put("ickey ", "");// ic卡号，如果身份证传身份证号，如果ic卡传写入的数据
			map.put("pdacode", DeviceUtils.getIMEI(BaseApplication.instent));// 设备编号
			map.put("txfx", params.get("fx"));// 通行方向
			map.put("pdbs", "");// 判断标识
			if ("0".equals(params.get("fx"))) {
				map.put("jrkksj", time);// 进入卡口时间
				map.put("jrkkyffs", "01"); // 进入卡口验放方式
				map.put("jrkkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 进入卡口验放设备标识，设备编号
			} else if ("1".equals(params.get("fx"))) {
				map.put("ckksj", time); // 出卡口验放时间
				map.put("ckkyffs", "01"); // 出卡口验放方式
				map.put("ckkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 出卡口验放设备标识，设备编号
			} else {
				map.put("jrkksj", "");// 进入卡口时间
				map.put("jrkkyffs", ""); // 进入卡口验放方式
				map.put("jrkkyfsbbs", "");// 进入卡口验放设备标识，设备编号
				map.put("ckksj", ""); // 出卡口验放时间
				map.put("ckkyffs", ""); // 出卡口验放方式
				map.put("ckkyfsbbs", "");// 出卡口验放设备标识，设备编号
			}

			map.put("qylxbs", "1");// 区域类型标识。（0人员、1车辆）
			map.put("txdx", params.get("cphm"));// 车牌号码
			map.put("jsyxm", params.get("xm"));// 车牌号码
			map.put("cphm", params.get("cphm"));
			map.put("clpp", params.get("clpp"));
			map.put("gs_yyz", params.get("gs_yyz"));
			map.put("gjdq", params.get("gjdq"));
			map.put("fdjh", params.get("fdjh"));

			map.put("cllx", params.get("cllx"));
			map.put("clsbdh", params.get("clsbdh"));
			map.put("clys", params.get("clys"));
			map.put("jszh", params.get("jszh"));

			map.put("lxfs", params.get("lxfs"));
			
			String csrq =  (String) params.get("csrq");
			if(StringUtils.isNotEmpty(csrq)){
				csrq = DateUtils.gainBirthday(csrq)+" 00:00:00";
			}
			map.put("csrq", csrq);
			map.put("zjzl", params.get("zjzl"));

			map.put("ldfxbs", ""); // 漏打方向标识
			map.put("zqry", LoginUser.getCurrentLoginUser().getName());// 执勤人员姓名
			map.put("zqryid", LoginUser.getCurrentLoginUser().getUserID()); // 执勤人员ID
			map.put("dwdm", "");// 执勤人员单位代码(平台根据执勤人员id查询)
			map.put("kadm", "");// 口岸代码(平台根据执勤人员id查询)
			map.put("zdkadm", "");// 总队口岸代码(平台根据执勤人员id查询)
			String xmldata = XmlUtils.buildXml(map);
			OffData data = new OffData();
			data.setCjsj(new Date());
			data.setClstatus("0");
			data.setCzgn(ManagerFlag.PDA_KKGL_CLYZ + "");
			data.setCzmk(ManagerFlag.PDA_KKGL + "");
			data.setPdacode(DeviceUtils.getIMEI(BaseApplication.instent));
			data.setUserid(BaseApplication.instent.gainUserID());
			data.setXmldata(xmldata);
			OffDataService service = new OffDataService();
			service.insert(data);
			datas.put("result", "success");
			info.put("txjlid", txjlid);
			info.put("cgcsid", null);
			info.put("xcxsid", null);
			datas.put("info", info);
			return datas;
		} catch (Exception e) {
			datas.put("result", "error");
			datas.put("info", "保存卡口记录错误，请稍后重试！");
			return datas;
		}

	}

	/**
	 * 
	 * @方法名：hgzjxxBusiness
	 * @功能说明：登轮登陆人员上下船业务
	 * @author liums
	 * @date 2013-10-21 上午11:33:22
	 * @param yfResult
	 * @param params
	 * @return
	 */
	private HashBuild hgzjxxBusinessCl(YfResult yfResult, Map<String, Object> params) {
		HashBuild info = new HashBuild(20);
		Hgzjxx hgzjxx = yfResult.getZjxx();
		String zjlx = yfResult.getZjlx();
		String time = "";
		boolean result = yfResult.isResult();// 验证结果
		if (result) {
			info.put("isPass", "pass");// 是否验证通过
			// 查询上下方向
			String txfx = getHgzjxxTxfx(hgzjxx.getCphm(), (String) params.get("kkID"));
			info.put("fx", txfx);// 本次上下船方向
			// 验证成功保存通行记录
			String txjltkId = StringUtils.UIDGenerator();
			time =	saveTxjlByHgzjxx(hgzjxx, txfx, txjltkId, (String) params.get("kkID"), (String) params.get("kkmc"), true);
			info.put("txjlid", txjltkId);
			info.put("cgcsid", null);
			info.put("xcxsid", null);
		} else {
			info.put("fx", null);// 上下船方向
			info.put("isPass", "false");// 是否验证通过
			info.put("txjlid", null);
			info.put("cgcsid", null);
			info.put("xcxsid", null);
		}

		info.put("sfdk", "0");// 是否搭靠：0否，1是
		info.put("dkjlid", null);// 搭靠记录ID
		info.put("tsxx", yfResult.getTsxx());
		info.put("hgzl", zjlx);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
		// 封装证件信息XML
		HashBuild zjxx = new HashBuild(30);
		zjxx.put("jsyxm", hgzjxx.getXm()); // 驾驶员姓名
		zjxx.put("xb", hgzjxx.getXbdm()); // 性别 男1、女2
		zjxx.put("csrq", DateUtils.gainBirthday(hgzjxx.getCsrq())); // 出生日期,原格式20121212，需要进行格式转换，2012-12-12
		zjxx.put("gj", hgzjxx.getGjdqdm()); // 国籍对应数据字典

		zjxx.put("zjhm", hgzjxx.getZjhm()); // 证件号码
		zjxx.put("txsj", time); // 证件号码
		zjxx.put("ryid", hgzjxx.getCbzjffxxxid()); // 人员ID
		zjxx.put("zjzl", hgzjxx.getZjlbdm()); // 证件类型对应数据字典，
		zjxx.put("jszh", hgzjxx.getJsyhm());// 驾驶证编号

		zjxx.put("ssdw", hgzjxx.getSsdw()); // 所属单位。
		zjxx.put("lxfs", hgzjxx.getLxfs()); // 联系方式

		zjxx.put("cphm", hgzjxx.getCphm()); // 车牌号码
		zjxx.put("clsbdh", hgzjxx.getClsbdh()); // 车牌号码
		zjxx.put("fdjh", hgzjxx.getFdjh()); // 发动机号
		zjxx.put("cllx", hgzjxx.getCllx()); // 车辆类型
		zjxx.put("gjdq", hgzjxx.getGj()); // 国家地区（车辆）
		zjxx.put("clpp", hgzjxx.getClpp()); // 车辆品牌
		zjxx.put("clys", hgzjxx.getClys()); // 车辆颜色
		zjxx.put("gs_yyz", hgzjxx.getGsyyz());// 公司拥有者
		
		String sdcbStr = HgzjxxUtil.getFffwInfo(hgzjxx, true);
		zjxx.put("syfw", sdcbStr);// 所属范围
		// 有效期
		if ("1".equals(hgzjxx.getYcyxbz() == null ? "" : hgzjxx.getYcyxbz())) {
			// 本航次有效
			zjxx.put("yxq", BaseApplication.instent.getString(R.string.bhcyx));
		} else {
			zjxx.put("yxq",
					new StringBuffer().append(DateUtils.DateToString(hgzjxx.getYxqq())).append(BaseApplication.instent.getString(R.string.zhi))
							.append(DateUtils.DateToString(hgzjxx.getYxqz())).toString());
		}

		info.put("zjxx", zjxx);
		return info;
	}

	/**
	 * 
	 * @方法名：hgzjxxBusiness
	 * @功能说明：登轮登陆人员上下船业务
	 * @author liums
	 * @date 2013-10-21 上午11:33:22
	 * @param yfResult
	 * @param params
	 * @return
	 */
	private HashBuild hgzjxxBusiness(YfResult yfResult, Map<String, Object> params) {
		HashBuild info = new HashBuild(20);
		Hgzjxx hgzjxx = yfResult.getZjxx();
		String zjlx = yfResult.getZjlx();

		boolean result = yfResult.isResult();// 验证结果
		if (result) {
			info.put("isPass", "pass");// 是否验证通过
			// 查询上下方向
			String txfx = getHgzjxxTxfx(hgzjxx.getZjhm(), (String) params.get("kkID"));
			info.put("sxcfx", txfx);// 本次上下船方向
			// 验证成功保存通行记录
			String txjltkId = StringUtils.UIDGenerator();
			saveTxjlByHgzjxx(hgzjxx, txfx, txjltkId, (String) params.get("kkID"), (String) params.get("kkmc"), false);
			info.put("txjlid", txjltkId);
			info.put("cgcsid", null);
			info.put("xcxsid", null);
		} else {
			info.put("sxcfx", null);// 上下船方向
			info.put("isPass", "false");// 是否验证通过
			info.put("txjlid", null);
			info.put("cgcsid", null);
			info.put("xcxsid", null);
		}

		info.put("sfdk", "0");// 是否搭靠：0否，1是
		info.put("dkjlid", null);// 搭靠记录ID
		info.put("tsxx", yfResult.getTsxx());
		info.put("hgzl", zjlx);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。

		// 封装证件信息XML
		HashBuild zjxx = new HashBuild(20);
		zjxx.put("xm", hgzjxx.getXm());// 姓名
		zjxx.put("xb", hgzjxx.getXbdm());// 性别 男1、女2
		zjxx.put("csrq", DateUtils.gainBirthday(hgzjxx.getCsrq()));// 出生日期,原格式20121212，需要进行格式转换，2012-12-12
		zjxx.put("gj", hgzjxx.getGjdqdm());// 国籍对应数据字典
		zjxx.put("zw", hgzjxx.getZwdm());// 职务对应数据字典，
		zjxx.put("zjhm", hgzjxx.getZjhm());// 证件号码
		zjxx.put("ryid", hgzjxx.getCbzjffxxxid());// 人员ID
		// 证件种类，字典标识 101173
		zjxx.put("zjlx", hgzjxx.getZjlbdm());// 证件类型对应数据字典，
		zjxx.put("ssdw", this.getCbzjffSsdw(hgzjxx));// 所属单位,如果是登陆证取服务船舶名称，如果是登轮证取所属单位。
		zjxx.put("icpic", null);
		
		String sdcbStr = HgzjxxUtil.getFffwInfo(hgzjxx, false);

		zjxx.put("syfw", sdcbStr);// 所登船舶，船下检查人员时显示（参考原型）。
		zjxx.put("sdcb", sdcbStr);// 所登船舶，船下检查人员时显示（参考原型）。
		
		// zjxx.put("yxq", null);// 有效期限，暂时不返回
		// 有效期
		if ("1".equals(hgzjxx.getYcyxbz() == null ? "" : hgzjxx.getYcyxbz())) {
			// 本航次有效
			zjxx.put("yxq", BaseApplication.instent.getString(R.string.bhcyx));
		} else {
			zjxx.put("yxq",
					new StringBuffer().append(DateUtils.DateToString(hgzjxx.getYxqq())).append(BaseApplication.instent.getString(R.string.zhi))
							.append(DateUtils.DateToString(hgzjxx.getYxqz())).toString());
		}
		info.put("zjxx", zjxx);
		return info;
	}

	/**
	 * 
	 * @方法名：getHgzjxxTxfx
	 * @功能说明：得到本次上下船方向，登轮人员默认上船
	 * @author liums
	 * @param hc
	 * @date 2013-10-21 下午3:45:09
	 * @param txjlTk
	 */
	private String getHgzjxxTxfx(String zjhm, String kkID) {
		TxjlKkService txjlkkService = new TxjlKkService();
		try {
			TxjlKk txjlKk = txjlkkService.getTxjlKkByZjhmAndKkId(zjhm, kkID);
			if (txjlKk == null) {
				// 说明第一是进卡口，插入新的卡口通行记录
				return TXFX_SC;
			} else {
				if (txjlKk.getTxfx().equals("0")) {
					// 最近位置为卡口内，那么当天通行方向为出卡口，更新最近通行记录
					return TXFX_XC;
				} else if (txjlKk.getTxfx().equals("1")) {
					// 最近位置为卡口外，那么当天通行方向为进卡口，插入新的通行记录
					return TXFX_SC;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return TXFX_SC;
		}
		return TXFX_SC;
	}

	/**
	 * 
	 * @方法名：getCyxxTxfx
	 * @功能说明：得到本次上下船方向，船员默认下船
	 * @author liums
	 * @date 2013-10-21 下午3:45:09
	 * @param txjlTk
	 */
	private String getCyxxTxfx(String zjhm, String kkID) {
		TxjlKkService txjlkkService = new TxjlKkService();
		try {
			TxjlKk txjlKk = txjlkkService.getTxjlKkByZjhmAndKkId(zjhm, kkID);
			if (txjlKk == null) {
				// 说明第一是进卡口，插入新的卡口通行记录
				return TXFX_XC;
			}

			String txfx = txjlKk.getTxfx();
			if (YfZjxxConstant.TXFX_XC.equals(txfx)) {
				// 最近位置为卡口内，那么当天通行方向为出卡口，更新最近通行记录
				return TXFX_SC;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return TXFX_XC;
		}
		return TXFX_XC;
	}

	/**
	 * 
	 * @方法名：getCbzjffSsdw
	 * @功能说明：登轮、登陆证所属单位区分
	 * @author liums
	 * @date 2013-1-30 上午10:41:10
	 * @param cbzjffxx
	 *            证件信息
	 * @return 所属单位
	 */
	protected String getCbzjffSsdw(Hgzjxx cbzjffxx) {
		// 48登轮证,50登陆证，登陆证取服务船舶名称，登轮证取所属单位。
		if (YfZjxxConstant.ZJLX_DLUN.equals(cbzjffxx.getZjlb()) || YfZjxxConstant.ZJLX_XDQY.equals(cbzjffxx.getZjlb())) {
			return cbzjffxx.getSsdw();
		} else if ("50".equals(cbzjffxx.getZjlb())) {
			return cbzjffxx.getZwcbm();
		}
		return null;
	}

	/**
	 * 
	 * @方法名：saveTxjlByHgzjxx
	 * @功能说明：根据证件保存梯口通行记录
	 * @author liums
	 * @date 2013-10-22 下午7:06:05
	 * @param hgzjxx
	 * @param txfx
	 * @param txjltkId
	 * @param isClyz
	 *            TODO
	 */
	private String saveTxjlByHgzjxx(Hgzjxx hgzjxx, String txfx, String txjltkId, String kkID, String kkmc, boolean isClyz) {// 离船操作
		Map<String, String> map = new HashMap<String, String>();

		String time = null;
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("txgz", YfZjxxConstant.FILO);// 登轮人员FILO先进后出、船员FOLI先出后进
		if (isClyz) {
			map.put("txdx", hgzjxx.getCphm());// 车牌号码
		} else {
			map.put("txdx", hgzjxx.getXm());// 通行人员姓名
		}
		map.put("txsj", time);// 通行时间
		if (isClyz) {
			map.put("dxlx", "02");// 对象类型 01人员，02车辆
		} else {
			map.put("dxlx", "01");// 对象类型 01人员，02车辆
		}

		map.put("txkkid", kkID);// 卡口id
		map.put("txkkmc", kkmc);// 卡口名称
		map.put("ryxb", hgzjxx.getXbdm());// 通行人员性别
		map.put("txrylx", "01"); // 通行人员类型，01人员
		map.put("rygj", hgzjxx.getGjdqdm());// 通行人员所属国籍代码
		map.put("ssdw", hgzjxx.getSsdw());// 通行人员所属单位
		map.put("zw", hgzjxx.getZwdm()); // 通行人员职务

		map.put("zjlx", hgzjxx.getZjlb());// 证件类型
		map.put("zjlbdm", hgzjxx.getZjlbdm());// 证件类别代码
		// 进出卡口类型 JRZ进入者(为如办理登轮证、证限定区域通行证人员或者车辆等)，CQZ出去者（为 船员）
		map.put("jckklx", YfZjxxConstant.JCKKLX_JRZ);
		map.put("sfcyjs", hgzjxx.getSfcyjs());// 是否船员家属-0非船员家属，1船员家属
		map.put("lcbz", "");// 离船 登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)
		map.put("skzj", hgzjxx.getZjbh());// 刷卡主键 记录使用什么证件或船员进出通行的
											// 证件使用zjbh（证件编号）,船员使用船员主键

		map.put("zjhm", hgzjxx.getZjhm());// 证件号码，如果身份证传身份证号
		map.put("skkh", "");// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
		map.put("ickey ", hgzjxx.getIckey());// ic卡号，如果身份证传身份证号，如果ic卡传写入的数据
		map.put("pdacode", DeviceUtils.getIMEI(BaseApplication.instent));// 设备编号
		map.put("txfx", txfx);// 通行方向
		map.put("pdbs", "");// 判断标识
		if ("0".equals(txfx)) {
			map.put("jrkksj", time);// 进入卡口时间
			map.put("jrkkyffs", "01"); // 进入卡口验放方式
			map.put("jrkkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 进入卡口验放设备标识，设备编号
		} else if ("1".equals(txfx)) {
			map.put("ckksj", time); // 出卡口验放时间
			map.put("ckkyffs", "01"); // 出卡口验放方式
			map.put("ckkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 出卡口验放设备标识，设备编号
		} else {
			map.put("jrkksj", "");// 进入卡口时间
			map.put("jrkkyffs", ""); // 进入卡口验放方式
			map.put("jrkkyfsbbs", "");// 进入卡口验放设备标识，设备编号
			map.put("ckksj", ""); // 出卡口验放时间
			map.put("ckkyffs", ""); // 出卡口验放方式
			map.put("ckkyfsbbs", "");// 出卡口验放设备标识，设备编号
		}
		map.put("ldfxbs", ""); // 漏打方向标识
		map.put("zqry", LoginUser.getCurrentLoginUser().getName());// 执勤人员姓名
		map.put("zqryid", LoginUser.getCurrentLoginUser().getUserID()); // 执勤人员ID
		map.put("dwdm", "");// 执勤人员单位代码(平台根据执勤人员id查询)
		map.put("kadm", "");// 口岸代码(平台根据执勤人员id查询)
		map.put("zdkadm", "");// 总队口岸代码(平台根据执勤人员id查询)

		if (isClyz) {
			map.put("jsyxm", hgzjxx.getXm()); // 驾驶员姓名
			map.put("xb", hgzjxx.getXbdm()); // 性别 男1、女2
			
			String csrq = hgzjxx.getCsrq();
			if(StringUtils.isNotEmpty(csrq)){
				csrq = DateUtils.gainBirthday(csrq)+" 00:00:00";
			}
			map.put("csrq", csrq); // 出生日期,原格式20121212，需要进行格式转换，2012-12-12
			map.put("gj", hgzjxx.getGjdqdm()); // 国籍对应数据字典

			// map.put("zjhm", hgzjxx.getZjhm()); //证件号码
			map.put("ryid", hgzjxx.getCbzjffxxxid()); // 人员ID
			map.put("zjzl", hgzjxx.getZjlbdm()); // 证件类型对应数据字典

			map.put("jszh", hgzjxx.getJsyhm());// 驾驶证编号
			map.put("ssdw", hgzjxx.getSsdw()); // 所属单位。
			map.put("lxfs", hgzjxx.getLxfs()); // 联系方式
//			map.put("csrq", hgzjxx.getCsrq()); // 联系方式

			map.put("cphm", hgzjxx.getCphm()); // 车牌号码
			map.put("fdjh", hgzjxx.getFdjh()); // 发动机号
			map.put("cllx", hgzjxx.getCllx()); // 车辆类型
			map.put("gjdq", hgzjxx.getGj()); // 国家地区（车辆）
			map.put("clpp", hgzjxx.getClpp()); // 车辆品牌
			map.put("clys", hgzjxx.getClys()); // 车辆颜色
			map.put("gs_yyz", hgzjxx.getGsyyz());// 公司拥有者
			map.put("syfw", hgzjxx.getSsfwNameShow());// 所属范围
		}

		String xmlData = XmlUtils.buildXml(map);
		OffData offData = new OffData();
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_KKGL + "");
		if (isClyz) {
			offData.setCzgn(ManagerFlag.PDA_KKGL_CLYZ + "");
		} else {
			offData.setCzgn(ManagerFlag.PDA_KKGL_TXYZ + "");
		}
		offData.setCzid(txjltkId);
		OffDataService dataService = new OffDataService();
		try {
			dataService.insert(offData);
			if (isClyz) {
				updateTxfxByZjhmAndKkid(hgzjxx.getCphm(), txfx, kkID, "zj", isClyz);
			} else {
				updateTxfxByZjhmAndKkid(hgzjxx.getZjhm(), txfx, kkID, "zj", isClyz);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 
	 * @方法名：cyxxBusiness
	 * @功能说明：船员上下船业务
	 * @author liums
	 * @date 2013-10-21 上午11:31:51
	 * @param yfResult
	 * @param params
	 * @param voyageNumber
	 * @return
	 */
	private HashBuild cyxxBusiness(YfResult yfResult, Map<String, Object> params) {
		HashBuild info = new HashBuild(20);
		TBCyxx cyxx = yfResult.getCyxx();
		info.put("tsxx", yfResult.getTsxx());
		boolean result = yfResult.isResult();// 验证结果
		if (result) {
			info.put("isPass", "pass");// 是否验证通过
			// 查询上下方向
			String txfx = getCyxxTxfx(cyxx.getZjhm(), (String) params.get("kkID"));
			info.put("sxcfx", txfx);// 本次上下船方向
			String txjltkId = StringUtils.UIDGenerator();
			saveTxjlByCyxx(cyxx, txfx, txjltkId, (String) params.get("kkID"), (String) params.get("kkmc"));
			info.put("txjlid", txjltkId);
			info.put("cgcsid", null);
			info.put("xcxsid", null);
		} else {
			info.put("txjlid", null);// 通行记录ID
			info.put("sxcfx", null);// 上下船方向
			info.put("isPass", "false");// 是否验证通过
		}

		info.put("sfdk", "0");// 是否搭靠：0否，1是
		info.put("dkjlid", null);// 搭靠记录ID
		info.put("hgzl", null);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。

		// 封装证件信息XML
		HashBuild zjxx = new HashBuild(10);
		zjxx.put("xm", cyxx.getXm());// 姓名
		zjxx.put("xb", cyxx.getXb());// 性别 男1、女2
		zjxx.put("csrq", cyxx.getCsrq() != null ? DateUtils.dayToString(cyxx.getCsrq()) : "");

		zjxx.put("gj", cyxx.getGj());// 国籍对应数据字典
		zjxx.put("zw", cyxx.getZw());// 职务
		zjxx.put("zjhm", cyxx.getZjhm());// 证件号码
		zjxx.put("ryid", cyxx.getHyid());// 海员ID
		zjxx.put("zjlx", cyxx.getZjlx());// 证件类型对应数据字典
		zjxx.put("ssdw", "");// 所属单位根据所属船舶ID获取船舶名称。
		zjxx.put("yxq", null);// 有效期限,不返回
		info.put("zjxx", zjxx);
		return info;
	}

	/**
	 * 
	 * @方法名：saveTxjlByHgzjxx
	 * @功能说明：根据证件保存梯口通行记录
	 * @author liums
	 * @date 2013-10-22 下午7:06:05
	 * @param hgzjxx
	 * @param txfx
	 * @param txjltkId
	 */
	private void saveTxjlByCyxx(TBCyxx cyxx, String txfx, String txjltkId, String kkID, String kkmc) {// 离船操作
		Map<String, String> map = new HashMap<String, String>();

		String time = null;
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("txgz", YfZjxxConstant.FOLI);// 登轮人员FILO先进后出、船员FOLI先出后进
		map.put("txdx", cyxx.getXm());// 通行人员姓名
		map.put("txsj", time);// 通行时间
		map.put("dxlx", "01");// 对象类型，固定传01
		map.put("txkkid", kkID);// 卡口id
		map.put("txkkmc", kkmc);// 卡口名称
		map.put("ryxb", cyxx.getXb());// 通行人员性别
		map.put("txrylx", "01"); // 通行人员类型，01人员
		map.put("rygj", cyxx.getGj());// 通行人员所属国籍代码
		map.put("ssdw", cyxx.getCbzwm());// 通行人员所属单位
		map.put("zw", cyxx.getZw()); // 通行人员职务

		map.put("zjlx", YfZjxxConstant.ZJLX_CY);// 证件类型
		map.put("zjlbdm", cyxx.getZjlx());// 证件类别代码
		map.put("jckklx", YfZjxxConstant.JCKKLX_CQZ);
		map.put("sfcyjs", "");// 是否船员家属-0非船员家属，1船员家属
		map.put("lcbz", cyxx.getLcbz());// 离船 登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)
		map.put("skzj", cyxx.getHyid());// 刷卡主键 记录使用什么证件或船员进出通行的
										// 证件使用zjbh（证件编号）,船员使用船员主键

		map.put("zjhm", cyxx.getZjhm());// 证件号码，如果身份证传身份证号
		map.put("skkh", "");// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
		map.put("ickey ", "");// ic卡号，如果身份证传身份证号，如果ic卡传写入的数据
		map.put("pdacode", DeviceUtils.getIMEI(BaseApplication.instent));// 设备编号
		map.put("txfx", txfx);// 通行方向
		map.put("pdbs", "");// 判断标识
		if ("0".equals(txfx)) {
			map.put("jrkksj", time);// 进入卡口时间
			map.put("jrkkyffs", "01"); // 进入卡口验放方式
			map.put("jrkkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 进入卡口验放设备标识，设备编号
		} else if ("1".equals(txfx)) {
			map.put("ckksj", time); // 出卡口验放时间
			map.put("ckkyffs", "01"); // 出卡口验放方式
			map.put("ckkyfsbbs", DeviceUtils.getIMEI(BaseApplication.instent));// 出卡口验放设备标识，设备编号
		} else {
			map.put("jrkksj", "");// 进入卡口时间
			map.put("jrkkyffs", ""); // 进入卡口验放方式
			map.put("jrkkyfsbbs", "");// 进入卡口验放设备标识，设备编号
			map.put("ckksj", ""); // 出卡口验放时间
			map.put("ckkyffs", ""); // 出卡口验放方式
			map.put("ckkyfsbbs", "");// 出卡口验放设备标识，设备编号
		}

		map.put("ldfxbs", ""); // 漏打方向标识
		map.put("zqry", LoginUser.getCurrentLoginUser().getName());// 执勤人员姓名
		map.put("zqryid", LoginUser.getCurrentLoginUser().getUserID()); // 执勤人员ID
		map.put("dwdm", "");// 执勤人员单位代码(平台根据执勤人员id查询)
		map.put("kadm", "");// 口岸代码(平台根据执勤人员id查询)
		map.put("zdkadm", "");// 总队口岸代码(平台根据执勤人员id查询)

		String xmlData = XmlUtils.buildXml(map);
		OffData offData = new OffData();
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_KKGL + "");
		offData.setCzgn(ManagerFlag.PDA_KKGL_TXYZ + "");
		offData.setCzid(txjltkId);
		OffDataService dataService = new OffDataService();
		try {
			dataService.insert(offData);
			updateTxfxByZjhmAndKkid(cyxx.getZjhm(), txfx, kkID, "cy", false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateTxfxByZjhmAndKkid(String zjhm, String txfx, String kkID, String cyOrZj, boolean isClyz) throws SQLException {
		TxjlKkService kkService = new TxjlKkService();
		if ("cy".equals(cyOrZj)) {
			if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
				kkService.deleteByZjhmAndKkid(zjhm, kkID);
			} else {
				kkService.updateTxjlKkTxfx(zjhm, kkID, txfx);
			}
		} else {
			if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
				kkService.updateTxjlKkTxfx(zjhm, kkID, txfx);
			} else {
				kkService.deleteByZjhmAndKkid(zjhm, kkID);
			}
		}
	}

}
