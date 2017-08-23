package com.pingtech.hgqw.module.tikou.action;

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
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.module.offline.txjl.entity.Dkqk;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlTk;
import com.pingtech.hgqw.module.offline.txjl.service.DkqkService;
import com.pingtech.hgqw.module.offline.txjl.service.TxjlTkService;
import com.pingtech.hgqw.module.offline.zjyf.entity.YfResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.offline.zjyf.yfimpl.PdaYfxxImpl;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.HashBuild;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class TkglAction implements BaseAction {
	private static final String INSPECT_FOR_ACROSS = "inspectForAcross";

	private static final String MODIFY_PASS_DIRECTION = "modifyPassDirection";

	private static final String SEND_PASS_INFO = "sendPassInfo";

	private static final String UNBIND_SHIP = "buildRelation";

	/** 当前选择的船舶 */
	private Kacbqk kacbqk = null;

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {
		if (INSPECT_FOR_ACROSS.equals(method)) {
			return inspectForAcross(params);// 梯口通行验证
		} else if (MODIFY_PASS_DIRECTION.equals(method)) {
			return modifyPassDirection(params);// 修改通行方向
		} else if (SEND_PASS_INFO.equals(method)) {// 手动保存通行记录
			return sendPassInfo(params);
		} else if (UNBIND_SHIP.equals(method)) {
			return new Pair<Boolean, Object>(false, null);
		}
		return null;
	}

	/**
	 * 
	 * @方法名：sendPassInfo
	 * @功能说明：手动保存通行记录接口
	 * @author liums
	 * @date 2013-10-23 下午8:16:16
	 * @param params
	 * @return
	 */
	private Pair<Boolean, Object> sendPassInfo(Map<String, Object> params) {
		String voyageNumber = (String) params.get("voyageNumber");// 航次
		String kkID = (String) params.get("kkID");// 卡口ID(卡口管理-刷卡登记)
		String comeFrom = (String) params.get("comeFrom");// 模块名称（梯口管理1、巡查巡检2、卡口管理3、巡查巡检---查岗查哨20）
		String xm = (String) params.get("xm");// 姓名
		String xb = (String) params.get("xb");// 性别（代码）
		String gj = (String) params.get("gj");// 国籍（数据字典对应代码）
		String zjzl = (String) params.get("zjzl");// 证件种类（数据字典对应代码）
		String csrq = (String) params.get("csrq");// 出生日期
		String zjhm = (String) params.get("zjhm");// 证件号码
		String ssdw = (String) params.get("ssdw");// 所属单位
		String zw = (String) params.get("zw");// 职务
		String fx = (String) params.get("fx");// 方向（0上船、进卡口，1下船、出卡口）
		String time = (String) params.get("time");// 时间（2012-12-12
		String type = (String) params.get("type");
		String ddID = (String) params.get("ddID");// 巡查地点对象ID
		String userID = (String) params.get("userID");// 当前登录PDA用户ID
		String pdaCode = (String) params.get("PDACode");
		String objectType = (String) params.get("objectType");// 06表示查岗查哨，01表示普通人员

		HashBuild datas = new HashBuild(10);
		HashBuild info = new HashBuild(10);
		String result = "success";
		Map<String, String> map = new HashMap<String, String>();
		KacbqkService kacbqkService = new KacbqkService();
		kacbqk = kacbqkService.getKacbqkByHC(voyageNumber);
		if (kacbqk == null) {
			result = "error";
			datas.put("info", "船舶信息查询失败！");
		}

		String pdacode = DeviceUtils.getIMEI();
		map.put("txryxm", xm);// 通行人员姓名
		map.put("ryxb", xb);// 通行人员性别
		map.put("rygj", gj);// 通行人员所属国籍代码
		map.put("ssdw", ssdw);// 所属单位
		map.put("txrylx", "01");// 通行人员类型，01人员
		map.put("dldllx", "lun");// 登轮登陆类型，lun为登轮、lu为登陆
		map.put("zw", zw);// 通行人员职位

		map.put("zjlx", YfZjxxConstant.ZJLX_WZ);
		map.put("zjlbdm", zjzl);// 证件类别代码
		map.put("sfcyjs", "");// 是否船员家属-0非船员家属，1船员家属
		map.put("lcbz", "");// 离船 登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)
		map.put("skzj", "");// 刷卡主键 记录使用什么证件或船员进出通行的 证件使用zjbh（证件编号）,船员使用船员主键

		map.put("zjhm", zjhm);// 证件号码，如果身份证传身份证号
		map.put("skkh", zjhm);// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
		map.put("ickey", zjhm);// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号

		if (YfZjxxConstant.TXFX_SC.equals(fx)) {
			map.put("scyfsb", pdacode);// 上船验放设备，传设备编号
			map.put("scyffs", "01");// 上船验放方式，01警务通
			map.put("scsj", time);// 上船时间
		} else {
			map.put("xcyfsb", pdacode);// 下船验放设备，传设备编号
			map.put("xcyffs", "01");// 下船验放方式，01警务通
			map.put("xcsj", time);// 下船时间
		}
		map.put("txfx", fx);// 通行方向
		map.put("txsj", time);// 通行方向
		map.put("cbid", kacbqk.getKacbqkid());// 船舶id
		map.put("hc", kacbqk.getHc());// 航次
		map.put("cbzywmc", kacbqk.getCbzwm());// 船舶中文名称
		map.put("cbywmc", kacbqk.getCbywm());// 船舶英文名称
		map.put("szmt", kacbqk.getTkmt());// 停靠码头
		map.put("szbw", kacbqk.getTkbw());// 停靠泊位
		map.put("tkwz", kacbqk.getTkwz());// 停靠位置
		map.put("zqry", LoginUser.getCurrentLoginUser().getName());// 船舶英文名称
		map.put("zqryid", LoginUser.getCurrentLoginUser().getUserID());// 执勤人员ID
		String xmlData = XmlUtils.buildXml(map);

		OffData offData = new OffData();
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_TKGL + "");
		offData.setCzgn(ManagerFlag.PDA_TKGL_TXYZ + "");

		String txjltkId = StringUtils.UIDGenerator();

		offData.setCzid(txjltkId);
		OffDataService dataService = new OffDataService();
		try {
			dataService.insert(offData);
			// 保存成功，更新Txjltk表通行方向
			updateTxjltkTxfx(zjhm, kacbqk.getHc(), fx, "zj");
		} catch (SQLException e) {
			e.printStackTrace();
			result = "error";
			datas.put("info", "保存失败！");
		}

		datas.put("result", result);
		info.put("txjlid", txjltkId);
		info.put("cgcsid", null);
		info.put("xcxsid", null);
		datas.put("info", info);
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
		String hc = (String) params.get("hc"); // 离线业务使用字段
		String ryid = (String) params.get("ryid"); // 离线业务使用字段
		String passDirection = (String) params.get("passDirection");// 修改的方向
		String time = (String) params.get("time");// 时间（2012-12-12 10:36:10精确到秒）
		String result = "-1";
		// 删除离线业务的通行记录，插入一条新的记录，并修改方向标识
		OffDataService offDataService = new OffDataService();
		KacbqkService kacbqkService = new KacbqkService();
		try {
			kacbqk = kacbqkService.getKacbqkByHC(hc);
			offDataService.deleteByCzid(recordid);
			// 插入一条新的记录
			if (YfZjxxConstant.ZJLX_DLUN.equals(hgzl) || YfZjxxConstant.ZJLX_XDQY.equals(hgzl)) {
				HgzjxxService hgzjxxService = new HgzjxxService();
				Hgzjxx hgzjxx = null;
				hgzjxx = hgzjxxService.findById(ryid);
				String txjltkId = StringUtils.UIDGenerator();

				saveTxjlByHgzjxx(hgzjxx, passDirection, txjltkId);
			} else {
				CyxxService cyxxService = new CyxxService();
				TBCyxx tbCyxx = null;
				tbCyxx = cyxxService.findById(ryid);
				String txjltkId = StringUtils.UIDGenerator();
				saveTxjlByCyxx(tbCyxx, passDirection, txjltkId);
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
	 * @方法名：inspectForAcross
	 * @功能说明：梯口通行验证业务
	 * @author liums
	 * @date 2013-10-23 下午12:16:11
	 * @param params
	 * @return
	 */
	private Pair<Boolean, Object> inspectForAcross(Map<String, Object> params) {
		HashBuild datas = new HashBuild(100);
		HashBuild info = new HashBuild(100);
		String cardNumber = (String) params.get("cardNumber");
		String defaultickey = (String) params.get("defaultickey");
		String sfsk = (String) params.get("sfsk");
		String voyageNumber = (String) params.get("voyageNumber");
		String sfcy = (String) params.get("sfcy");// 是否船员：0否、1是
		KacbqkService kacbqkService = new KacbqkService();
		kacbqk = kacbqkService.getKacbqkByHC(voyageNumber);
		if (kacbqk != null && KacbqkService.CBKAZT_LG.equals(kacbqk.getCbkazt())) {
			datas.put("result", "error");
			datas.put("info", "船舶【" + kacbqk.getCbzwm() + "】已经离港，无法验证。");
			return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
		}

		// 手动选择船员
		if ("1".equals(sfcy)) {
			return selectCyBusiness(params, datas, cardNumber, voyageNumber);
		}

		PdaYfxxImpl pdaYfxx = new PdaYfxxImpl();
		YfResult yfResult = pdaYfxx.zjIsAvailable(cardNumber, cardNumber, defaultickey, sfsk, voyageNumber, null, YfZjxxConstant.YFFS_PDATK, null,
				false, null);

		if (yfResult != null && (yfResult.getZjxx() != null || yfResult.getCyxx() != null)) {// 卡号存在
			datas.put("result", "success");
			// 证件类型 48:登轮证, 50登陆证,17海员证 , 52搭靠外轮许可证,sbk士兵卡。
			if (YfZjxxConstant.ZJLX_DK.equals(yfResult.getZjlx())) {
				info = dkOperation(yfResult);// 搭靠业务
			} else if (YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx()) && yfResult.getZjxx() != null
					&& YfZjxxConstant.ZJ_QYLXBS_CL.equals(yfResult.getZjxx().getQylxbs())) {
				datas = new HashBuild(10);
				datas.put("result", "error");
				datas.put("isClzj", "true");
				datas.put("info", "您刷的是车辆限定区域通行证，请在卡口车辆验放模块下刷卡");
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			} else if (YfZjxxConstant.ZJLX_DLUN.equals(yfResult.getZjlx()) || YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx())) {
				info = hgzjxxBusiness(yfResult, params);
			} else {// 船员
				info = cyxxBusiness(yfResult, params, voyageNumber);
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
	 * @方法名：selectCyBusiness
	 * @功能说明：手动选择船员
	 * @author liums
	 * @date 2013-11-28 下午5:06:08
	 * @param params
	 * @param datas
	 * @param cardNumber
	 * @param voyageNumber
	 * @return
	 */
	private Pair<Boolean, Object> selectCyBusiness(Map<String, Object> params, HashBuild datas, String cardNumber, String voyageNumber) {
		YfResult yfResult = new YfResult();
		yfResult.setResult(true);
		yfResult.setTsxx("验证通过，是本船船员");
		try {
			TBCyxx cyxx = new CyxxService().findById(cardNumber);
			if (cyxx == null) {
				datas.put("result", "success");
				HashBuild info = new HashBuild(5);
				info.put("tsxx", "验证失败，不是边防证件");
				datas.put("info", info);
			} else {
				yfResult.setCyxx(cyxx);
				datas = cyxxBusiness(yfResult, params, voyageNumber);
			}
		} catch (SQLException e) {
			datas.put("result", "success");
			HashBuild info = new HashBuild(5);
			info.put("tsxx", "验证失败，不是边防证件");
			datas.put("info", info);
		}
		return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
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
	private HashBuild cyxxBusiness(YfResult yfResult, Map<String, Object> params, String voyageNumber) {
		HashBuild info = new HashBuild(20);
		TBCyxx cyxx = yfResult.getCyxx();
		boolean result = yfResult.isResult();// 验证结果
		if (result) {
			info.put("isPass", "pass");// 是否验证通过
			// 查询上下方向
			String txfx = getCyxxTxfx(cyxx.getZjhm(), kacbqk.getHc());
			info.put("sxcfx", txfx);// 本次上下船方向
			String txjltkId = StringUtils.UIDGenerator();
			info.put("txjlid", txjltkId);// 通行记录ID
			// 验证成功保存通行记录
			saveTxjlByCyxx(cyxx, txfx, txjltkId);
		} else {
			info.put("sxcfx", null);// 上下船方向
			info.put("isPass", "false");// 是否验证通过
		}

		info.put("sfdk", "0");// 是否搭靠：0否，1是
		info.put("dkjlid", null);// 搭靠记录ID
		info.put("hgzl", null);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
		info.put("tsxx", yfResult.getTsxx());
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

		if (kacbqk != null) {
			zjxx.put("ssdw", kacbqk.getCbzwm());// 所属单位根据所属船舶ID获取船舶名称。
		} else {
			zjxx.put("ssdw", "");// 所属单位根据所属船舶ID获取船舶名称。

		}

		zjxx.put("yxq", null);// 有效期限,不返回
		info.put("zjxx", zjxx);
		return info;
	}

	private void saveTxjlByCyxx(TBCyxx cyxx, String txfx, String txjltkId) {// 离船操作
		Map<String, String> map = new HashMap<String, String>();

		String time = null;
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String pdacode = DeviceUtils.getIMEI();

		map.put("txryxm", cyxx.getXm());// 通行人员姓名
		map.put("ryxb", cyxx.getXb());// 通行人员性别
		map.put("rygj", cyxx.getGj());// 通行人员所属国籍代码
		map.put("ssdw", kacbqk.getCbzwm());// 所属单位，船员保存船舶中文名
		map.put("txrylx", "01");// 通行人员类型，01人员
		map.put("dldllx", "lu");// 登轮登陆类型，lun为登轮、lu为登陆
		map.put("zw", cyxx.getZw());// 通行人员职位

		map.put("zjlx", YfZjxxConstant.ZJLX_CY);// 通行人员证件类型
		map.put("zjlbdm", cyxx.getZjlx());// 证件类别代码
		map.put("sfcyjs", "");// 是否船员家属-0非船员家属，1船员家属
		map.put("lcbz", cyxx.getLcbz());// 离船 登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)
		map.put("skzj", cyxx.getHyid());// 刷卡主键 记录使用什么证件或船员进出通行的
										// 证件使用zjbh（证件编号）,船员使用船员主键

		map.put("zjhm", cyxx.getZjhm());// 证件号码，如果身份证传身份证号
		map.put("skkh", cyxx.getZjhm());// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
		map.put("ickey", cyxx.getZjhm());// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
		if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
			map.put("scyfsb", pdacode);// 上船验放设备，传设备编号
			map.put("scyffs", "01");// 上船验放方式，01警务通
			map.put("scsj", time);// 上船时间
		} else {
			map.put("xcyfsb", pdacode);// 下船验放设备，传设备编号
			map.put("xcyffs", "01");// 下船验放方式，01警务通
			map.put("xcsj", time);// 下船时间
		}
		map.put("txfx", txfx);// 通行方向
		map.put("txsj", time);// 通行时间
		map.put("cbid", kacbqk.getKacbqkid());// 船舶id
		map.put("hc", kacbqk.getHc());// 航次
		map.put("cbzywmc", kacbqk.getCbzwm());// 船舶中文名称
		map.put("cbywmc", kacbqk.getCbywm());// 船舶英文名称
		map.put("szmt", kacbqk.getTkmt());// 停靠码头
		map.put("szbw", kacbqk.getTkbw());// 停靠泊位
		map.put("tkwz", kacbqk.getTkwz());// 停靠位置
		map.put("zqry", LoginUser.getCurrentLoginUser().getName());// 船舶英文名称
		map.put("zqryid", LoginUser.getCurrentLoginUser().getUserID());// 执勤人员ID
		String xmlData = XmlUtils.buildXml(map);

		OffData offData = new OffData();
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_TKGL + "");
		offData.setCzgn(ManagerFlag.PDA_TKGL_TXYZ + "");
		offData.setCzid(txjltkId);
		OffDataService dataService = new OffDataService();
		try {
			dataService.insert(offData);
			// 保存成功，更新Txjltk表通行方向
			updateTxjltkTxfx(cyxx.getZjhm(), kacbqk.getHc(), txfx, "cy");
		} catch (SQLException e) {
			e.printStackTrace();
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
	private HashBuild hgzjxxBusiness(YfResult yfResult, Map<String, Object> params) {
		HashBuild info = new HashBuild(20);
		Hgzjxx hgzjxx = yfResult.getZjxx();
		String zjlx = yfResult.getZjlx();
		boolean result = yfResult.isResult();// 验证结果
		if (result) {
			info.put("isPass", "pass");// 是否验证通过
			// 查询上下方向
			String txfx = getHgzjxxTxfx(hgzjxx.getZjhm(), kacbqk.getHc());
			info.put("sxcfx", txfx);// 本次上下船方向
			// 验证成功保存通行记录
			String txjltkId = StringUtils.UIDGenerator();
			info.put("txjlid", txjltkId);// 通行记录ID
			saveTxjlByHgzjxx(hgzjxx, txfx, txjltkId);
		} else {
			info.put("sxcfx", null);// 上下船方向
			info.put("isPass", "false");// 是否验证通过
		}

		info.put("sfdk", "0");// 是否搭靠：0否，1是
		info.put("dkjlid", null);// 搭靠记录ID
		info.put("tsxx", yfResult.getTsxx());
		info.put("hgzl", zjlx);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。

		/*if ("50".equals(zjlx)) {
			info.put("hgzl", "50");// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
		} else if (YfZjxxConstant.ZJLX_DLUN.equals(zjlx)) {
			info.put("hgzl", YfZjxxConstant.ZJLX_DLUN);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
		} else if (YfZjxxConstant.ZJLX_XDQY.equals(zjlx)) {
		} else {
			info.put("hgzl", null);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
		}*/

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
		zjxx.put("sdcb", sdcbStr);// 所登船舶，船下检查人员时显示（参考原型）。
		zjxx.put("syfw", sdcbStr);// 所登船舶，船下检查人员时显示（参考原型）。

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
	 * @方法名：saveTxjlByHgzjxx
	 * @功能说明：根据证件保存梯口通行记录
	 * @author liums
	 * @date 2013-10-22 下午7:06:05
	 * @param hgzjxx
	 * @param txfx
	 * @param txjltkId
	 */
	private void saveTxjlByHgzjxx(Hgzjxx hgzjxx, String txfx, String txjltkId) {// 离船操作
		Map<String, String> map = new HashMap<String, String>();

		String time = null;
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String pdacode = DeviceUtils.getIMEI();

		map.put("txryxm", hgzjxx.getXm());// 通行人员姓名
		map.put("ryxb", hgzjxx.getXbdm());// 通行人员性别
		map.put("rygj", hgzjxx.getGjdqdm());// 通行人员所属国籍代码
		map.put("ssdw", hgzjxx.getSsdw());// 所属单位
		map.put("txrylx", "01");// 通行人员类型，01人员
		map.put("dldllx", "lun");// 登轮登陆类型，lun为登轮、lu为登陆
		map.put("zw", hgzjxx.getZwdm());// 通行人员职位

		map.put("zjlx", hgzjxx.getZjlb());// 通行人员证件类型
		map.put("zjlbdm", hgzjxx.getZjlbdm());// 证件类别代码
		map.put("sfcyjs", hgzjxx.getSfcyjs());// 是否船员家属-0非船员家属，1船员家属
		map.put("lcbz", "");// 离船 登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)
		map.put("skzj", hgzjxx.getZjbh());// 刷卡主键 记录使用什么证件或船员进出通行的
											// 证件使用zjbh（证件编号）,船员使用船员主键

		map.put("zjhm", hgzjxx.getZjhm());// 证件号码，如果身份证传身份证号
		map.put("skkh", hgzjxx.getMrickey());// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
		map.put("ickey", hgzjxx.getIckey());// 刷卡卡号，如果身份证传身份证号，如果ic卡传默认卡编号
		if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
			map.put("scyfsb", pdacode);// 上船验放设备，传设备编号
			map.put("scyffs", "01");// 上船验放方式，01警务通
			map.put("scsj", time);// 上船时间
		} else {
			map.put("xcyfsb", pdacode);// 下船验放设备，传设备编号
			map.put("xcyffs", "01");// 下船验放方式，01警务通
			map.put("xcsj", time);// 下船时间
		}

		map.put("txfx", txfx);// 通行方向
		map.put("txsj", time);// 通行时间
		map.put("cbid", kacbqk.getKacbqkid());// 船舶id
		map.put("hc", kacbqk.getHc());// 航次
		map.put("cbzywmc", kacbqk.getCbzwm());// 船舶中文名称
		map.put("cbywmc", kacbqk.getCbywm());// 船舶英文名称
		map.put("szmt", kacbqk.getTkmt());// 停靠码头
		map.put("szbw", kacbqk.getTkbw());// 停靠泊位
		map.put("tkwz", kacbqk.getTkwz());// 停靠位置
		map.put("zqry", LoginUser.getCurrentLoginUser().getName());// 船舶英文名称
		map.put("zqryid", LoginUser.getCurrentLoginUser().getUserID());// 执勤人员ID
		String xmlData = XmlUtils.buildXml(map);

		OffData offData = new OffData();
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_TKGL + "");
		offData.setCzgn(ManagerFlag.PDA_TKGL_TXYZ + "");
		offData.setCzid(txjltkId);
		OffDataService dataService = new OffDataService();
		try {
			dataService.insert(offData);
			// 保存成功，更新Txjltk表通行方向
			updateTxjltkTxfx(hgzjxx.getZjhm(), kacbqk.getHc(), txfx, "zj");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：updateTxfx
	 * @功能说明：修改本地通行方向
	 * @author liums
	 * @date 2013-10-22 下午7:58:50
	 * @param zjhm
	 * @param kacbqkid
	 * @param txfx
	 */
	private void updateTxjltkTxfx(String zjhm, String hc, String txfx, String cyOrZj) {
		TxjlTkService txjlTkService = new TxjlTkService();
		try {
			if ("cy".equals(cyOrZj)) {
				if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
					txjlTkService.deleteByZjhmAndHc(zjhm, hc);
				} else {
					txjlTkService.updateTxjlTkTxfx(zjhm, hc, txfx);
				}

			} else {
				// 默认上船，如果本次是上船，则保存。本次是下船则删除本条记录。下次自动判断为上船
				if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
					txjlTkService.updateTxjlTkTxfx(zjhm, hc, txfx);
				} else {
					txjlTkService.deleteByZjhmAndHc(zjhm, hc);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	 * @方法名：getHgzjxxTxfx
	 * @功能说明：得到本次上下船方向，登轮人员默认上船
	 * @author liums
	 * @param hc
	 * @date 2013-10-21 下午3:45:09
	 * @param txjlTk
	 */
	private String getHgzjxxTxfx(String zjhm, String hc) {
		TxjlTkService txjlTkService = new TxjlTkService();
		TxjlTk txjlTk = txjlTkService.getTxjlTkByZjhmAndHc(zjhm, hc);
		if (txjlTk == null) {
			return YfZjxxConstant.TXFX_SC;
		}
		String txfx = txjlTk.getTxfx();
		if (YfZjxxConstant.TXFX_XC.equals(txfx)) {
			return YfZjxxConstant.TXFX_SC;
		}
		return YfZjxxConstant.TXFX_XC;
	}

	/**
	 * 
	 * @方法名：getCyxxTxfx
	 * @功能说明：得到本次上下船方向，船员默认下船
	 * @author liums
	 * @date 2013-10-21 下午3:45:09
	 * @param txjlTk
	 */
	private String getCyxxTxfx(String zjhm, String hc) {
		TxjlTkService txjlTkService = new TxjlTkService();
		TxjlTk txjlTk = txjlTkService.getTxjlTkByZjhmAndHc(zjhm, hc);
		if (txjlTk == null) {
			return YfZjxxConstant.TXFX_XC;
		}
		String txfx = txjlTk.getTxfx();
		if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
			return YfZjxxConstant.TXFX_XC;
		}
		return YfZjxxConstant.TXFX_SC;
	}

	/********* 登轮证 **********/

	/********* 搭靠业务 **********/
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
	private HashBuild dkOperation(YfResult yfResult) {
		HashBuild info = new HashBuild(30);
		Hgzjxx hgzjxx = yfResult.getZjxx();
		boolean result = yfResult.isResult();// 验证结果
		if (result) {
			String dkid = StringUtils.UIDGenerator();
			info.put("dkjlid", dkid);// 搭靠记录ID
			info.put("isPass", "pass");// 是否验证通过
			// String txfx = getHgzjxxTxfx(hgzjxx.getZjhm(), kacbqk.getHc());//
			// 查询上下方向
			// info.put("sxcfx", txfx);// 本次上下船方向
			// 验证通过保存一条搭靠记录
			saveDkjl(hgzjxx, dkid);

		} else {
			info.put("sxcfx", null);// 上下船方向
			info.put("isPass", "false");// 是否验证通过
			info.put("dkjlid", null);// 搭靠记录ID
		}

		info.put("sfdk", "1");// 是否搭靠：0否，1是
		info.put("tsxx", yfResult.getTsxx());
		info.put("hgzl", YfZjxxConstant.ZJLX_DK);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。

		HashBuild dkxx = new HashBuild(20);
		dkxx.put("zjhm", hgzjxx.getZjhm());// 证件号码
		dkxx.put("ryid", hgzjxx.getCbzjffxxxid());// 人员ID
		dkxx.put("cbmc", hgzjxx.getZwcbm());// 船舶名称
		dkxx.put("cgj", hgzjxx.getCjg());// 船港籍
		dkxx.put("zzdw", hgzjxx.getDw() == null ? "" : hgzjxx.getDw() + "");// 载重吨位
		dkxx.put("ml", hgzjxx.getMl() == null ? "" : hgzjxx.getMl() + "");// 马力
		dkxx.put("ssdw", hgzjxx.getSsdw());// 所属单位
		dkxx.put("yt", hgzjxx.getYt());// 用途
		// 有效期
		if ("1".equals(hgzjxx.getYcyxbz() == null ? "" : hgzjxx.getYcyxbz())) {
			// 本航次有效
			dkxx.put("yxq", BaseApplication.instent.getString(R.string.bhcyx));
		} else {
			dkxx.put("yxq",
					new StringBuffer().append(DateUtils.DateToString(hgzjxx.getYxqq())).append(BaseApplication.instent.getString(R.string.zhi))
							.append(DateUtils.DateToString(hgzjxx.getYxqz())).toString());
		}
		String sdcbStr = HgzjxxUtil.getFffwInfo(hgzjxx, false);
		// if (hgzjxx.getFffw().equals("1")) {
		// dkxx.put("dkfw", hgzjxx.getDkcb());
		// } else if (hgzjxx.getFffw().equals("2")) {
		// dkxx.put("dkfw", "");
		// } else {
		dkxx.put("dkfw", sdcbStr);
		// }
		dkxx.put("zjlx", hgzjxx.getZjlb());// 证件类型对应数据字典
		info.put("dkxx", dkxx);
		return info;
	}

	/**
	 * 
	 * @方法名：saveDkjl
	 * @功能说明：保存搭靠记录，更新搭靠方向记录表
	 * @author liums
	 * @date 2013-10-23 上午10:35:28
	 * @param hgzjxx
	 * @param dkid
	 */
	private void saveDkjl(Hgzjxx hgzjxx, String dkid) {// 离船操作
		String time = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			time = DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("zjffxxid", hgzjxx.getCbzjffxxxid());// 证件 ID
		map.put("zjlx", hgzjxx.getZjlb());// 证件 ID
		map.put("zjlbdm", hgzjxx.getZjlbdm());// 证件 ID
		map.put("kacbqkid", kacbqk.getKacbqkid());// 搭靠船舶ID
		map.put("cm", hgzjxx.getZwcbm());// 船舶中文名
		map.put("ssdw", hgzjxx.getSsdw());// 所属单位
		map.put("pdacode", DeviceUtils.getIMEI());// 所属单位
		map.put("txsj", time);// 通行时间
		// 判断本次搭靠方向
		String dkfx = getDkqkDkfx(hgzjxx, kacbqk.getKacbqkid());
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
			saveOrUpdateDkqkDkfx(hgzjxx, kacbqk.getKacbqkid(), dkfx, dkid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：updateDkqk 功能说明：更新搭靠方向
	 * @author liums
	 * @date 2013-10-22 下午2:15:55
	 * @param hgzjxx
	 * @param kacbqkid
	 */
	private void saveOrUpdateDkqkDkfx(Hgzjxx hgzjxx, String kacbqkid, String dkfx, String dkqkid) {
		DkqkService dkqkService = new DkqkService();
		try {
			// 搭靠默认上船，如果本次是上船，则保存。本次是下船则删除本条记录。下次自动判断为上船
			if (YfZjxxConstant.TXFX_SC.equals(dkfx)) {
				dkqkService.saveOrUpdateDkfx(hgzjxx, kacbqkid, dkfx, dkqkid);
			} else {
				dkqkService.deleteByKacbqkidAndZjxxid(hgzjxx.getCbzjffxxxid(), kacbqkid);
			}
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
			return YfZjxxConstant.TXFX_SC;
		}
		String txfx = dkqk.getDkfx();
		if (YfZjxxConstant.TXFX_SC.equals(txfx)) {
			return YfZjxxConstant.TXFX_XC;
		}
		return YfZjxxConstant.TXFX_SC;
	}
}
