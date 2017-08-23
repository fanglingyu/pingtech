package com.pingtech.hgqw.module.offline.zjyf.yfimpl;

import java.sql.SQLException;
import java.util.List;

import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;
import com.pingtech.hgqw.module.offline.qyxx.service.QyxxService;
import com.pingtech.hgqw.module.offline.zjyf.Yfxx;
import com.pingtech.hgqw.module.offline.zjyf.entity.YfResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;

/**
 * 
 * 
 * 类描述：PDA验放实现
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-PdaYfxxImpl.java
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
 * @date 2013-7-5 上午10:15:06
 */

public class PdaYfxxImpl extends Yfxx {

	HgzjxxService hgzjxxService = new HgzjxxService();

	KacbqkService kacbqkService = new KacbqkService();

	QyxxService qyxxService = new QyxxService();

	/**
	 * 
	 * @方法名：zjIsAvailableForZjType
	 * @功能说明：证件是否有效带证件类型的
	 * @author 赵琳
	 * @date 2013-7-5 上午10:20:23
	 * @param zjType
	 *            证件类型 48 登轮 52 搭靠
	 * @param ickey
	 * @param zjhm
	 * @param mrickey
	 *            默认ICKEY
	 * @param sfsk
	 *            是否刷卡：0刷卡、1手动输入
	 * @param hc
	 *            航次
	 * @param kkId
	 *            卡口ID
	 * @param yffs
	 *            验放方式 HC(航次) or KKId(卡口ID)
	 * @return
	 */
	public YfResult zjIsAvailableForZjType(String zjType, String ickey, String zjhm, String mrickey, String sfsk, String hc, String kkId, String yffs) {
		List<Hgzjxx> zjxxs = this.getYfZjxx(ickey, zjhm, mrickey, sfsk, zjType, YfZjxxConstant.ICFFZT_YFF, null, false);
		if (zjxxs != null && zjxxs.size() > 0) {
			if (YfZjxxConstant.YFFS_PDATK.equals(yffs) && hc != null && !"".equals(hc)) {// 梯口(船舶)验证
				// Kacbqk ship = kacbqkService.getKacbqkByHc(hc);
				Kacbqk ship = kacbqkService.byConditionGetKacbqk(null, hc, null);
				if (ship != null && !"".equals(ship.getKacbqkid())) {
					if (YfZjxxConstant.ZJLX_DLUN.equals(zjType)) {// 梯口验证登轮
						return this.yfTypeForCb(zjxxs, ship, new YfResult());
					} else if (YfZjxxConstant.ZJLX_DK.equals(zjType)) {// 搭靠船舶
						return this.yfTypeForCb(zjxxs, ship, new YfResult());
					}
				}
			} else if (YfZjxxConstant.YFFS_PDAKK.equals(yffs)) {// 卡口
				if (YfZjxxConstant.ZJLX_DLUN.equals(zjType)) {// 绑定梯口验证登轮
					Qyxx qyxx = null;
					try {
						qyxx = qyxxService.byIdGetQyxx(kkId);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (qyxx != null && !"".equals(qyxx.getId())) {
						return this.yfTypeForQyxx(zjxxs, qyxx, new YfResult(), zjhm);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @方法名：zjIsAvailable
	 * @功能说明：证件是否有效
	 * @author 赵琳
	 * @date 2013-7-9 下午01:49:55
	 * @param ickey
	 * @param ickey
	 * @param zjhm
	 * @param mrickey
	 *            默认ICKEY
	 * @param sfsk
	 *            是否刷卡：0刷卡、1手动输入
	 * @param hc
	 *            航次
	 * @param kkId
	 *            卡口ID
	 * @param yffs
	 *            验放方式 HC(航次) or KKId(卡口ID)
	 * @param cphm
	 *            TODO
	 * @param isClyz
	 *            TODO
	 * @param jszbh TODO
	 * @return
	 */
	public YfResult zjIsAvailable(String ickey, String zjhm, String mrickey, String sfsk, String hc, String kkId, String yffs, String cphm,
			boolean isClyz, String jszbh) {
		List<Hgzjxx> zjxxs = this.getYfZjxx(ickey, zjhm, mrickey, sfsk, null, YfZjxxConstant.ICFFZT_YFF, cphm, isClyz);
		// 车辆验证，先判断是否需要用户输入驾驶证编号
		/*
		 * if (isClyz && StringUtils.isEmpty(zjhm) && "1".equals(sfsk)) { for
		 * (Hgzjxx hgzjxx : zjxxs) { if
		 * (StringUtils.isNotEmpty(hgzjxx.getJsyhm())) { YfResult result = new
		 * YfResult(); result.setResult(YfZjxxConstant.YFJG_SB);
		 * result.setToast(true); result.setTsxx("请输入驾驶证编号"); return result; } }
		 * }
		 */

		boolean isSrjszh = false;

		if (zjxxs != null && zjxxs.size() > 0) {
			if (YfZjxxConstant.YFFS_PDATK.equals(yffs) && hc != null && !"".equals(hc)) {// 梯口(船舶)验证
				Kacbqk ship = kacbqkService.byConditionGetKacbqk(null, hc, null);
				if (ship != null && !"".equals(ship.getKacbqkid())) {
					return this.yfTypeForCb(zjxxs, ship, new YfResult());
				}

			} else if (YfZjxxConstant.YFFS_PDAKK.equals(yffs)) {// 卡口
				Qyxx qyxx = null;
				try {
					qyxx = qyxxService.byIdGetQyxx(kkId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				boolean isEmpty = false;
//				for (Hgzjxx hgzjxx : zjxxs) {
//					if (StringUtils.isEmpty(hgzjxx.getJsyhm())) {
//						isEmpty = true;
//					}
//				}
				
				YfResult yfResult = null;
				if (qyxx != null && !"".equals(qyxx.getId())) {
					yfResult = this.yfTypeForQyxx(zjxxs, qyxx, new YfResult(), zjhm);
//					if (yfResult.isResult()  ) {
						return yfResult;
//					}
				}

//				// 车辆验证，先判断是否需要用户输入驾驶证编号
//				for (Hgzjxx hgzjxx : zjxxs) {
//					if (StringUtils.isNotEmpty(hgzjxx.getJsyhm())) {
//						isSrjszh = true;
//					}
//				}
//				if (isSrjszh && isClyz && StringUtils.isEmpty(zjhm) && "1".equals(sfsk)) {
//					YfResult result = new YfResult();
//					result.setResult(YfZjxxConstant.YFJG_SB);
//					result.setToast(true);
//					result.setTsxx("请输入驾驶证编号");
//					return result;
//				} else {
//					return yfResult;
//				}

			}
		} else /* if (sfsk.equals("1")) */{
			// 说明是手动输入船员证件号码，查询船员信息。
			TBCyxx cyxx = this.getYfCyxx(zjhm);
			if (cyxx != null) {
				if (YfZjxxConstant.YFFS_PDAKK.equals(yffs)) {// 卡口
					Qyxx qyxx = null;
					try {
						qyxx = qyxxService.byIdGetQyxx(kkId);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (qyxx != null && !"".equals(qyxx.getId())) {
						return this.yfCyForQycc(cyxx, qyxx, new YfResult());
					}
				} else if (YfZjxxConstant.YFFS_PDATK.equals(yffs) && hc != null && !"".equals(hc)) {
					// 手动输入船员梯口验证
					return this.yfCyForTk(cyxx, hc, new YfResult());
				}
			}

		}
		YfResult result = new YfResult();
		result.setResult(YfZjxxConstant.YFJG_SB);
		result.setTsxx("验证失败，不是边防证件");
		return result;
	}

	/**
	 * 
	 * @方法名：
	 * @功能说明：船员梯口验证
	 * @author zhujy
	 * @date 2013-7-26 上午09:54:40
	 * @param cyxx
	 * @param hc
	 * @param result
	 * @return
	 */
	public YfResult yfCyForTk(TBCyxx cyxx, String hc, YfResult result) {
		Kacbqk ship = kacbqkService.byConditionGetKacbqk(null, hc, null);
		result.setCyxx(cyxx);
		result.setZjlx("50");
		result.setShip(ship);
		if (cyxx != null && ship != null && cyxx.getKacbqkid().equals(ship.getKacbqkid())) {
			result.setResult(YfZjxxConstant.YFJG_CG);
			result.setTsxx("验证通过，是本船船员");
		} else {
			result.setResult(YfZjxxConstant.YFJG_SB);
			result.setTsxx("验证失败，非本船船员");
		}
		return result;
	}
}
