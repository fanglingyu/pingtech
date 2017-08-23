package com.pingtech.hgqw.module.wpjc.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.offline.base.action.BaseAction;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.offdata.service.OffDataService;
import com.pingtech.hgqw.module.offline.zjyf.entity.YfResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.module.offline.zjyf.yfimpl.PdaYfxxImpl;
import com.pingtech.hgqw.utils.DateUtils;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.xml.HashBuild;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class GoodsCheckAction implements BaseAction {

	@Override
	public Pair<Boolean, Object> request(String method, Map<String, Object> params) throws SQLException {

		OffDataService offDataService = new OffDataService();
		if ("goodsCheck".equals(method)) {

			String cardNumber = (String) params.get("cardNumber");
			String voyagemc = (String) params.get("voyagemc");
			String defaultickey = (String) params.get("defaultickey");
			String sfsk = (String) params.get("sfsk");
			String voyageNumber = (String) params.get("voyageNumber");

			HashBuild info = new HashBuild(100);
			HashBuild datas = new HashBuild(100);

			KacbqkService kacbqkService = new KacbqkService();
			Kacbqk kacbqk = null;
			kacbqk = kacbqkService.getKacbqkByHC(voyageNumber);
			if (kacbqk != null && KacbqkService.CBKAZT_LG.equals(kacbqk.getCbkazt())) {
				datas.put("result", "error");
				datas.put("info", BaseApplication.instent.getString(R.string.ship_has_lg));
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			}

			PdaYfxxImpl pdaYfxx = new PdaYfxxImpl();

			YfResult yfResult = pdaYfxx.zjIsAvailable(cardNumber, cardNumber, defaultickey, sfsk, voyageNumber, null, YfZjxxConstant.YFFS_PDATK, null, false, null);
			if (yfResult == null) {
				datas.put("result", "success");// result必须为success，否则不能进入证件编辑页面。
				info.put("tsxx", "验证失败，不是边防证件");
				datas.put("info", info);
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));

			}
			if (yfResult.getZjxx() != null) {
				if (YfZjxxConstant.ZJLX_DK.equals(yfResult.getZjlx())) {
					datas.put("result", "error");
					datas.put("info", "物品检查不能使用搭靠证！");
					return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
				} else if (YfZjxxConstant.ZJLX_DLUN.equals(yfResult.getZjlx()) || YfZjxxConstant.ZJLX_XDQY.equals(yfResult.getZjlx())) {
					datas = buildInfoByEnterCard(yfResult, voyageNumber, cardNumber, sfsk);
				}
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			}

			if (yfResult.getCyxx() != null) {
				datas = getCyxx(yfResult.getCyxx());
				return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
			}
			datas.put("result", "success");// result必须为success，否则不能进入证件编辑页面。
			info.put("tsxx", "验证失败，不是边防证件");
			datas.put("info", info);
			return new Pair<Boolean, Object>(true, XmlUtils.buildXml(datas.get()));
		} else if ("sendGoodsInfo".equals(method)) {
			Object sfcy = params.get("sfcy");
			Object hc = params.get("voyageNumber");
			Object ssdw = (Object) params.get("ssdw");
			Kacbqk kacbqk = new KacbqkService().byConditionGetKacbqk(null, hc.toString(), null);
			if (kacbqk == null) {
				return new Pair<Boolean, Object>(false, "船舶信息查询失败，请确认离线数据是否同步完成");
			}
			if (kacbqk != null && "3".equals(kacbqk.getCbkazt())) {
				return new Pair<Boolean, Object>(false, BaseApplication.instent.getString(R.string.ship_has_lg));
			}

			if (StringUtils.isEmpty(ssdw) && StringUtils.isNotEmpty(hc) && StringUtils.isNotEmpty(sfcy) && "1".equals(sfcy.toString())) {
				if (kacbqk != null && StringUtils.isNotEmpty(kacbqk.getCbzwm())) {
					params.put("ssdw", kacbqk.getCbzwm());
				}
			}

			Object kacbqkid = params.get("kacbqkid");
			if (StringUtils.isEmpty(kacbqkid) && StringUtils.isNotEmpty(hc)) {
				if (kacbqk != null && StringUtils.isNotEmpty(kacbqk.getKacbqkid())) {
					params.put("kacbqkid", kacbqk.getKacbqkid());
				}
			}

			OffData offData = new OffData();
			String xmlData = XmlUtils.buildXml(params);
			offData.setPdacode(DeviceUtils.getIMEI());
			SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
					Context.MODE_PRIVATE);
			String userid = prefs.getString("userid", "");
			offData.setUserid(userid);
			offData.setXmldata(xmlData);
			offData.setGxsj(new Date());
			offData.setCjsj(new Date());
			offData.setCzmk(ManagerFlag.PDA_WPJC + "");
			offData.setCzgn(ManagerFlag.PDA_WPJC_JLWP + "");
			Object czid = params.get("voyageNumber");
			if (StringUtils.isNotEmpty(czid)) {
				offData.setCzid(czid.toString());
			} else {
				offData.setCzid("");
			}

			try {
				offDataService.create(offData);
				return new Pair<Boolean, Object>(true, null);
			} catch (SQLException e) {
				return new Pair<Boolean, Object>(false, "保存失败");
			}
		} else if ("getGoodsList".equals(method)) {
			Object hc = params.get("voyageNumber");

			if (hc != null) {
				if (StringUtils.isNotEmpty(hc)) {
					return new Pair<Boolean, Object>(true, offDataService.findModuleByGN(ManagerFlag.PDA_WPJC, ManagerFlag.PDA_WPJC_JLWP, null,
							hc.toString()));
				}
			}
			return new Pair<Boolean, Object>(true, offDataService.findModuleByGN(ManagerFlag.PDA_WPJC, ManagerFlag.PDA_WPJC_JLWP, null, method));
		}

		return new Pair<Boolean, Object>(false, null);
	}

	/**
	 * 
	 * @方法名：buildInfoByEnterCard
	 * @功能说明：登轮证限定区域证封装返回数据
	 * @author 娄高伟
	 * @date 2013-10-21 下午5:40:45
	 * @param yfResult
	 * @param voyageNumber
	 * @param cardNumber
	 * @param sfsk
	 * @return
	 */
	private HashBuild buildInfoByEnterCard(YfResult yfResult, String voyageNumber, String cardNumber, String sfsk) {
		Hgzjxx hgzjxx = yfResult.getZjxx();
		HashBuild datas = new HashBuild(20);
		HashBuild info = new HashBuild(20);
		String xcxsidAndRes = null;
		datas.put("result", "success");
		info.put("tsxx", yfResult.getTsxx());// 验证结果后续增加。
		info.put("xcxsid", xcxsidAndRes);// 巡查巡视记录ID
		info.put("sfdk", "0");// 是否搭靠：0否，1是
		info.put("dkjlid", null);// 搭靠记录ID
		info.put("cgcsid", null);// 查岗查哨记录ID
		info.put("hgzl", YfZjxxConstant.ZJLX_DLUN);// 海港证类，48登轮证,50登陆证，52搭靠外轮许可证。
		info.put("zjxx", buildInfoZjxxByCbzjffxx(hgzjxx));
		datas.put("info", info);

		return datas;

	}

	/**
	 * 
	 * @方法名：buildInfoZjxxByCbzjffxx
	 * @功能说明：构建不同业务下的info对象
	 * @author 娄高伟
	 * @date 2013-10-21 下午5:41:37
	 * @param hgzjxx
	 * @return
	 */
	private HashBuild buildInfoZjxxByCbzjffxx(Hgzjxx hgzjxx) {
		HashBuild zjxx = new HashBuild(10);
		zjxx.put("xm", hgzjxx.getXm());// 姓名
		zjxx.put("xb", hgzjxx.getXbdm());// 性别 男1、女2
		zjxx.put("csrq", hgzjxx.getCsrq());// 出生日期
		zjxx.put("zw", hgzjxx.getZwdm());// 职务代码
		zjxx.put("gj", hgzjxx.getGjdqdm());// 国籍代码取数据字典
		zjxx.put("zjhm", hgzjxx.getZjhm());// 证件号码
		zjxx.put("zjlx", hgzjxx.getZjlbdm());// 证件类型代码取数据字典
		zjxx.put("ssdw", hgzjxx.getSsdw());// 所属单位
		zjxx.put("ryid", hgzjxx.getCbzjffxxxid());
		// 登轮证，增加全港通用判断。
		String sdcbStr = hgzjxx.getZwcbm();
		if (hgzjxx.getHc() == null || "".equals(hgzjxx.getHc())) {
			sdcbStr = "全港通用";
		}
		zjxx.put("sdcb", sdcbStr);// 所登船舶，船下检查人员时显示（参考原型）。

		Date dateYxqq = hgzjxx.getYxqq();
		Date dateYxqz = hgzjxx.getYxqz();
		String yxqqStr = "";
		String yxqzStr = "";
		if (dateYxqq != null) {
			yxqqStr = DateUtils.dayToString(dateYxqq);
		}
		if (dateYxqz != null) {
			yxqzStr = DateUtils.dayToString(dateYxqz);
		}
		zjxx.put("yxq", yxqqStr + "至" + yxqzStr);// 有效期限

		// ******2013-2-1新增参数******
		String bhcyxStr = hgzjxx.getYcyxbz();
		if (bhcyxStr != null && !"".equals(bhcyxStr)) {
			if ("1".equals(bhcyxStr)) {
				bhcyxStr = "是";
			} else {
				bhcyxStr = "否";
			}
		}
		zjxx.put("bhcyx", bhcyxStr);// 本航次有效 ycyxbz,1是，2否
		return zjxx;
	}

	/**
	 * 
	 * @方法名：getCyxx
	 * @功能说明：封装船员信息
	 * @author 娄高伟
	 * @date 2013-10-21 下午5:41:54
	 * @param cyxx
	 * @param voyagemc
	 * @return
	 */
	private HashBuild getCyxx(TBCyxx cyxx) {

		HashBuild datas = new HashBuild(20);
		HashBuild info = new HashBuild(20);
		HashBuild zjxx = new HashBuild(10);
		datas.put("result", "success");
		zjxx.put("xm", cyxx.getXm());// 姓名
		zjxx.put("xb", cyxx.getXb());// 性别 男1、女2
		zjxx.put("csrq", cyxx.getCsrq());
		zjxx.put("gj", cyxx.getGj());// 国籍对应数据字典
		zjxx.put("zw", cyxx.getZw());// 职务
		zjxx.put("zjhm", cyxx.getZjhm());// 证件号码
		zjxx.put("ryid", cyxx.getHyid());// 海员ID
		zjxx.put("zjlx", cyxx.getZjlx());// 证件类型对应数据字典

		String kacbqkid = cyxx.getKacbqkid();

		zjxx.put("ssdw", "");// 所属单位根据所属船舶ID获取船舶名称。
		if (StringUtils.isNotEmpty(kacbqkid)) {
			try {
				Kacbqk kacbqk = new KacbqkService().getKacbqkByKacbqkId(kacbqkid);
				if (kacbqk != null && StringUtils.isNotEmpty(kacbqk.getCbzwm())) {
					zjxx.put("ssdw", kacbqk.getCbzwm());// 所属单位根据所属船舶ID获取船舶名称。
				}
			} catch (SQLException e) {
			}
		}
		info.put("zjxx", zjxx);
		datas.put("info", info);
		return datas;

	}

}
