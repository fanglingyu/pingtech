package com.pingtech.hgqw.module.offline.zjyf;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.cyxx.service.CyxxService;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.hgzjxx.service.HgzjxxService;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.kacbqk.service.KacbqkService;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;
import com.pingtech.hgqw.module.offline.zjyf.entity.YfResult;
import com.pingtech.hgqw.module.offline.zjyf.util.YfZjxxConstant;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;

/**
 * 
 * 
 * 类描述：验发信息
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-Yfxx.java
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
 * @date 2013-7-5 上午09:13:31
 */
public abstract class Yfxx {
	private static final String TAG = "Yfxx";

	HgzjxxService hgzjxxService = new HgzjxxService();

	KacbqkService kacbqkService = new KacbqkService();

	/**
	 * 
	 * @方法名：getYfZjxx
	 * @功能说明：得到要验放的证件
	 * @author 赵琳
	 * @date 2013-7-2 下午04:35:37
	 * @param ickey
	 *            卡编号
	 * @param zjhm
	 *            证件号码
	 * @param mrickey
	 *            默认ICKEY
	 * @param sfsk
	 *            是否刷卡：0刷卡、1手动输入
	 * @param zjType
	 *            证件类型 登轮、搭靠等
	 * @param icffzt
	 *            证件发放状态
	 * @param cphm
	 *            TODO
	 * @param isClyz
	 *            TODO
	 * @return
	 */
	public List<Hgzjxx> getYfZjxx(String ickey, String zjhm, String mrickey, String sfsk, String zjType, String icffzt, String cphm, boolean isClyz) {
		List<Hgzjxx> lists = null;
		try {
			lists = hgzjxxService.getYfZjxx(ickey, zjhm, mrickey, sfsk, zjType, icffzt, cphm, isClyz);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			e.printStackTrace();
		}
		return lists;
	}

	/**
	 * 
	 * @方法名：
	 * @功能说明：根据证件号码查询在港船舶该证件号码唯一船员
	 * @author zhujy
	 * @date 2013-7-25 下午04:23:25
	 * @param zjhm
	 *            证件号码
	 * @return
	 */
	public TBCyxx getYfCyxx(String zjhm) {
		CyxxService cyxxService = new CyxxService();
		TBCyxx cyxx = null;
		try {
			cyxx = cyxxService.getTBCyxxByZjhm(zjhm);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			e.printStackTrace();
		}
		return cyxx;

	}

	/**
	 * 
	 * @方法名：yfTypeForCb
	 * @功能说明：验放类型船舶
	 * @author 赵琳
	 * @date 2013-7-5 上午11:31:46
	 * @param zjxxs
	 * @param ship
	 * @param yfjg
	 * @return
	 */
	public YfResult yfTypeForCb(List<Hgzjxx> zjxxs, Kacbqk ship, YfResult yfjg) {
		yfjg.setShip(ship);
		boolean hasDLUN = false;
		boolean hasDK = false;
		boolean hasXDQY = false;

		// 查找有效的登轮证
		for (Hgzjxx zjxx : zjxxs) {
			if (YfZjxxConstant.ZJLX_DLUN.equals(zjxx.getZjlb())) {
				hasDLUN = true;
				yfjg = searchZjxxForTk(ship, yfjg, zjxx);
				if (yfjg.isResult()) {
					return yfjg;
				}
			}
		}

		// 如果没有有效的登轮证，查找有效的搭靠证
		for (Hgzjxx zjxx : zjxxs) {
			if (YfZjxxConstant.ZJLX_DK.equals(zjxx.getZjlb())) {
				hasDK = true;
				yfjg = searchZjxxForTk(ship, yfjg, zjxx);
				if (yfjg.isResult()) {
					return yfjg;
				}
			}
		}
		// 如果没有有效地登轮证、搭靠证，判断是否存在以上两种证件，先判断登轮证，在判断搭靠证，有则返回。
		if (hasDLUN) {
			for (Hgzjxx zjxx : zjxxs) {
				if (YfZjxxConstant.ZJLX_DLUN.equals(zjxx.getZjlb())) {
					yfjg = searchZjxxForTk(ship, yfjg, zjxx);
				}
			}
			return yfjg;
		} else if (hasDK) {
			for (Hgzjxx zjxx : zjxxs) {
				if (YfZjxxConstant.ZJLX_DK.equals(zjxx.getZjlb())) {
					yfjg = searchZjxxForTk(ship, yfjg, zjxx);
				}
			}
			return yfjg;
		}

		// 如果没有登轮证、搭靠证，查找限定区域证件
		for (Hgzjxx zjxx : zjxxs) {
			if (YfZjxxConstant.ZJLX_XDQY.equals(zjxx.getZjlb())) {
				return this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, "限定区域通行证，不能上下船！", zjxx);
			}
		}

		return yfjg;

		/*
		 * for(Hgzjxx zjxx : zjxxs){
		 * if(YfZjxxConstant.ZJLX_XDQY.equals(zjxx.getZjlb())){ return
		 * this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, "限定区域通行证，不能上下船！",
		 * zjxx); }
		 *//** 验放证件发放范围一个船舶的 */
		/*
		 * if(YfZjxxConstant.ZJ_FW_CB.equals(zjxx.getFffw())){ if(ship != null
		 * && ship.getHc() != null &&
		 * ship.getHc().equals(zjxx.getHc())){//办理的证件为一条船舶的
		 * if(YfZjxxConstant.ZJXX_BHCYX.equals(zjxx.getYcyxbz()) ){//本航次有效的船舶
		 * //--验证成功可以通行 return this.returnYfjg(yfjg, YfZjxxConstant.YFJG_CG,
		 * "验证通过，证件有效",zjxx); }else{//此船舶在一定时间内有效
		 * if(this.yzZjxxDateSfYx(zjxx,yfjg)){ return yfjg;//验证成功 } }
		 * 
		 * }else {//所登船舶为无效船舶 //---验证失败 没有匹配的船舶 this.returnYfjg(yfjg,
		 * YfZjxxConstant.YFJG_SB,null,zjxx); yfFlag = false; }
		 *//** 验放证件发放范围一个码头的 */
		/*
		 * }else if(ship != null &&
		 * YfZjxxConstant.ZJ_FW_MT.equals(zjxx.getFffw())){//码头 boolean isSsmt =
		 * this.cbIsInZjqy(zjxx, ship, yfjg); if(isSsmt){ return yfjg; }
		 *//** 验放证件发放范围全港通用的 */
		/*
		 * }else if(ship != null &&
		 * YfZjxxConstant.ZJ_FW_QGTY.equals(zjxx.getFffw())){//全港通用 boolean
		 * isqgty = this.zjIsQgty(zjxx,yfjg);//是否全港通用 if(isqgty){ return yfjg; }
		 * } }
		 * 
		 * return yfjg;
		 */
	}

	/**
	 * 
	 * @方法名：searchZjxx
	 * @功能说明：梯口刷卡登记筛选证件信息
	 * @author liums
	 * @date 2013-12-25 下午3:33:12
	 * @param ship
	 * @param yfjg
	 * @param zjxx
	 * @return
	 */
	private YfResult searchZjxxForTk(Kacbqk ship, YfResult yfjg, Hgzjxx zjxx) {
		/** 验放证件发放范围一个船舶的 */
		if (YfZjxxConstant.ZJ_FW_CB.equals(zjxx.getFffw())) {
			if (ship != null && ship.getHc() != null && ship.getHc().equals(zjxx.getHc())) {// 办理的证件为一条船舶的
				if (YfZjxxConstant.ZJXX_BHCYX.equals(zjxx.getYcyxbz())) {// 本航次有效的船舶
					// --验证成功可以通行
					return this.returnYfjg(yfjg, YfZjxxConstant.YFJG_CG, "验证通过，证件有效", zjxx);
				} else {// 此船舶在一定时间内有效
					if (this.yzZjxxDateSfYx(zjxx, yfjg)) {
						return yfjg;// 验证成功
					}
				}
			} else {// 所登船舶为无效船舶
				// ---验证失败 没有匹配的船舶
				this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, null, zjxx);
			}
			/** 验放证件发放范围一个码头的 */
		} else if (ship != null && YfZjxxConstant.ZJ_FW_MT.equals(zjxx.getFffw())) {// 码头
			boolean isSsmt = this.cbIsInZjqy(zjxx, ship, yfjg);
			if (isSsmt) {
				return yfjg;
			}
			/** 验放证件发放范围全港通用的 */
		} else if (ship != null && YfZjxxConstant.ZJ_FW_QGTY.equals(zjxx.getFffw())) {// 全港通用
			boolean isqgty = this.zjIsQgty(zjxx, yfjg);// 是否全港通用
			if (isqgty) {
				return yfjg;
			}
			/** 验放证件发放范围一证通用的 */
		} else if (ship != null
				&& YfZjxxConstant.ZJ_FW_YZT.equals(zjxx.getFffw())) {// 一证通
			boolean sfyx = this.yzZjxxDateSfYx(zjxx, yfjg);// 验证有效期
			if (sfyx) {
				return yfjg;
			}
		}
		return yfjg;
	}
	
	/**
	 * 
	 * @方法名：
	 * @功能说明：验证船员是否可以进出卡口
	 * @author zhujy
	 * @date 2013-7-25 下午05:26:50
	 * @param cyxx
	 *            船员信息
	 * @param qyxx
	 * @param yfjg
	 * @return
	 */
	public YfResult yfCyForQycc(TBCyxx cyxx, Qyxx qyxx, YfResult yfjg) {

		Kacbqk ship = kacbqkService.byConditionGetKacbqk(cyxx.getKacbqkid(), null, null);
		boolean iscyKk = this.cyIsInQy(cyxx, ship, qyxx, yfjg);
		if (iscyKk) {
			return yfjg;
		} else {
			yfjg.setQyxx(qyxx);
			yfjg.setResult(YfZjxxConstant.YFJG_SB);
			yfjg.setTsxx("验证失败，非卡口内船员");
			yfjg.setCyxx(cyxx);
			yfjg.setZjlx(cyxx.getZjlx());
		}
		return yfjg;

	}

	/**
	 * 
	 * @方法名：yfTypeForQyxx
	 * @功能说明：验证类型区域信息
	 * @author 赵琳
	 * @date 2013-7-5 下午03:52:17
	 * @param zjxxs
	 * @param qyxx
	 * @param yfjg
	 * @param jszbh
	 *            TODO
	 * @return
	 */
	public YfResult yfTypeForQyxx(List<Hgzjxx> zjxxs, Qyxx qyxx, YfResult yfjg, String jszbh) {
		yfjg.setQyxx(qyxx);// 设置区域信息

		boolean hasDLUN = false;
		boolean hasDK = false;
		boolean hasXDQY = false;

		// 当边检通传来的jszbh为空时，判断结果集中时否有
		// jszbh为空的证件，若jszbh为空的所有证件都验证不通过，并且有jszbh不为空的证件则返回“请输入驾驶证编号”
		if (!StringUtils.isNotEmpty(jszbh)) {
			boolean hasJszbh = false;
			for (Hgzjxx zjxx : zjxxs) {
				if (YfZjxxConstant.ZJLX_XDQY.equals(zjxx.getZjlb()) && YfZjxxConstant.ZJ_QYLXBS_CL.equals(zjxx.getQylxbs())
						&& StringUtils.isEmpty(zjxx.getJsyhm())) {// 车辆限定区域通行证,jsyhm为空的才进入判断
					yfjg = searchXdqyZjxxForKk(qyxx, yfjg, zjxx);
					if (yfjg.isResult()) {
						return yfjg;
					}
				}
				if (!StringUtils.isEmpty(zjxx.getJsyhm())) {
					hasJszbh = true;
				}
			}

			if (hasJszbh) { // 提示请输入驾驶证号，单独处理
				// Hgzjxx hgzjxx = new Hgzjxx();
				// hgzjxx.setZjlb("srjsz");
				YfResult result = new YfResult();
				result.setResult(YfZjxxConstant.YFJG_SB);
				result.setToast(true);
				result.setTsxx("请输入驾驶证编号");
				return result;
				// return this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB,
				// "请输入驾驶证号！", hgzjxx);
			}
		}

		// 先找有效的限定区域通行证
		for (Hgzjxx zjxx : zjxxs) {
			if (YfZjxxConstant.ZJLX_XDQY.equals(zjxx.getZjlb())) {// 限定区域通行证
				hasXDQY = true;
				yfjg = searchXdqyZjxxForKk(qyxx, yfjg, zjxx);
				if (yfjg.isResult()) {
					return yfjg;
				}
			}
		}

		// 没有限定区域通行证查找有效的登轮证
		for (Hgzjxx zjxx : zjxxs) {
			if (YfZjxxConstant.ZJLX_DLUN.equals(zjxx.getZjlb())) {// 登轮证验证
				hasDLUN = true;
				yfjg = searchDLunZjxxForKk(qyxx, yfjg, zjxx);
				if (yfjg.isResult()) {
					return yfjg;
				}
			}
		}
		// 限定区域通行证和登轮证都没有有效地，判断是否存在以上两种证件，先判断限定区域通行证，再判断登轮证，有则返回
		if (hasXDQY) {
			for (Hgzjxx zjxx : zjxxs) {
				if (YfZjxxConstant.ZJLX_XDQY.equals(zjxx.getZjlb())) {
					yfjg = searchXdqyZjxxForKk(qyxx, yfjg, zjxx);
				}
			}
			return yfjg;
		} else if (hasDLUN) {
			for (Hgzjxx zjxx : zjxxs) {
				if (YfZjxxConstant.ZJLX_DLUN.equals(zjxx.getZjlb())) {
					yfjg = searchDLunZjxxForKk(qyxx, yfjg, zjxx);
				}
			}
			return yfjg;
		}

		// 以上两种证件都不存在
		for (Hgzjxx zjxx : zjxxs) {
			if (YfZjxxConstant.ZJLX_DK.equals(zjxx.getZjlb())) {
				this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, "您刷的是搭靠外轮许可证，在卡口不做验证！", zjxx);
			}
		}
		return yfjg;

		/*
		 * for(Hgzjxx zjxx : zjxxs){
		 * if(YfZjxxConstant.ZJLX_DK.equals(zjxx.getZjlb())){
		 * this.returnYfjg(yfjg,YfZjxxConstant.YFJG_SB,
		 * "您刷的是搭靠外轮许可证，在卡口不做验证！",zjxx); }else
		 * if(YfZjxxConstant.ZJLX_DLUN.equals(zjxx.getZjlb())){//登轮证验证
		 * if(YfZjxxConstant.ZJ_FW_CB.equals(zjxx.getFffw())){ if(zjxx.getHc()
		 * != null && !"".equals(zjxx.getHc())){//办理的证件为一条船舶的 Kacbqk
		 * ship=kacbqkService.byConditionGetKacbqk(null, zjxx.getHc(), null);
		 * if(ship != null && !"".equals(ship.getKacbqkid())){ boolean isSsmt =
		 * this.zjCbIsInSsyq(qyxx,zjxx, ship, yfjg); if(isSsmt){ return yfjg; }
		 * } }
		 * 
		 * }else if(YfZjxxConstant.ZJ_FW_MT.equals(zjxx.getFffw())){//码头 boolean
		 * isssyq = this.zjqyIsInSsqy(zjxx, qyxx, yfjg); if(isssyq){ return
		 * yfjg; } }else
		 * if(YfZjxxConstant.ZJ_FW_QGTY.equals(zjxx.getFffw())){//全港通用 boolean
		 * isqgty = this.zjIsQgty(zjxx,yfjg);//是否全港通用 if(isqgty){ return yfjg; }
		 * } }else if(YfZjxxConstant.ZJLX_XDQY.equals(zjxx.getZjlb())){//限定区域通行证
		 * if(YfZjxxConstant.ZJ_FW_QGTY.equals(zjxx.getFffw())){ //说明是全港通用
		 * boolean isxdqyqg=this.zjIsQgty(zjxx, yfjg); if(isxdqyqg){ return
		 * yfjg; } }else{ //不是全港通用 boolean isxdqynqg=this.zjqyIsXdqy(zjxx, qyxx,
		 * yfjg); if(isxdqynqg){ return yfjg; } }
		 * 
		 * } } return yfjg;
		 */
	}

	/**
	 * 
	 * @方法名：searchDLunZjxxForKk
	 * @功能说明：卡口通行验证筛选登轮证证件信息
	 * @author liums
	 * @date 2013-12-25 下午3:42:26
	 * @param qyxx
	 * @param yfjg
	 * @param zjxx
	 * @return
	 */
	private YfResult searchDLunZjxxForKk(Qyxx qyxx, YfResult yfjg, Hgzjxx zjxx) {
		if (YfZjxxConstant.ZJ_FW_CB.equals(zjxx.getFffw())) {
			if (zjxx.getHc() != null && !"".equals(zjxx.getHc())) {// 办理的证件为一条船舶的
				Kacbqk ship = kacbqkService.getKacbqkByHC(zjxx.getHc());
				// Kacbqk ship = kacbqkService.byConditionGetKacbqk(null,
				// zjxx.getHc(), null);
				if (ship != null && !"".equals(ship.getKacbqkid())) {
					boolean isSsmt = this.zjCbIsInSsyq(qyxx, zjxx, ship, yfjg);
					if (isSsmt) {
						return yfjg;
					}
				}
			}
		} else if (YfZjxxConstant.ZJ_FW_MT.equals(zjxx.getFffw())) {// 码头
			boolean isssyq = this.zjqyIsInSsqy(zjxx, qyxx, yfjg);
			if (isssyq) {
				return yfjg;
			}
		} else if (YfZjxxConstant.ZJ_FW_QGTY.equals(zjxx.getFffw())) {// 全港通用
			boolean isqgty = this.zjIsQgty(zjxx, yfjg);// 是否全港通用
			if (isqgty) {
				return yfjg;
			}
			/** 验放证件发放范围一证通用的 */
		} else if (YfZjxxConstant.ZJ_FW_YZT.equals(zjxx.getFffw())) {// 一证通
			boolean sfyx = this.yzZjxxDateSfYx(zjxx, yfjg);// 验证有效期
			if (sfyx) {
				return yfjg;
			}
		}
		return yfjg;
	}

	/**
	 * 
	 * @方法名：searchXdqyZjxxForKk
	 * @功能说明：卡口刷卡登记筛选限定区域证件信息
	 * @author liums
	 * @date 2013-12-25 下午3:41:02
	 * @param qyxx
	 * @param yfjg
	 * @param zjxx
	 * @return
	 */
	private YfResult searchXdqyZjxxForKk(Qyxx qyxx, YfResult yfjg, Hgzjxx zjxx) {
		if (YfZjxxConstant.ZJ_FW_QGTY.equals(zjxx.getFffw())) {
			// 说明是全港通用
			boolean isxdqyqg = this.zjIsQgty(zjxx, yfjg);
			if (isxdqyqg) {
				return yfjg;
			}
		} else if (YfZjxxConstant.ZJ_FW_YZT.equals(zjxx.getFffw())) {// 一证通
			boolean sfyx = this.yzZjxxDateSfYx(zjxx, yfjg);// 验证有效期
			if (sfyx) {
				return yfjg;
			}
		} else {
			// 不是全港通用
			boolean isxdqynqg = this.zjqyIsXdqy(zjxx, qyxx, yfjg);
			if (isxdqynqg) {
				return yfjg;
			}
		}
		return yfjg;
	}

	/**
	 * 
	 * @方法名：yzZjxxDateSfYx
	 * @功能说明：验证证件日期是否有效
	 * @author 赵琳
	 * @date 2013-7-3 下午01:57:14
	 * @param zj
	 */
	public boolean yzZjxxDateSfYx(Hgzjxx zj, YfResult yfjg) {
		if (zj.getYxqq() != null && zj.getYxqz() != null) {
			Date now = new Date();
			SimpleDateFormat db1 = new SimpleDateFormat("yyyy-MM-dd");
			try {
				now = db1.parse(db1.format(now));
			} catch (ParseException e) {
				Log.i(TAG, "yzZjxxDateSfYx(Hgzjxx, YfResult)");
				Log.i(TAG, "Yfxx  验信息日期转化错误===");
			}
			if (now.before(zj.getYxqq()) || now.after(zj.getYxqz())) {
				this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, "验证失败，证件过期", zj);
				return false;
			} else { // --证件有效 可以通行
				this.returnYfjg(yfjg, YfZjxxConstant.YFJG_CG, getYktTsxx(zj), zj);
				return true;
			}
		} else {// --证件无效
			this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, null, zj);
			return false;
		}
	}

	private String getYktTsxx(Hgzjxx hgzjxx) {
		String isykt = hgzjxx.getIsykt();
		if (StringUtils.isNotEmpty(isykt) && YfZjxxConstant.ZJ_ISYKT_YES.equals(isykt)) {
			// String bzkamc = hgzjxx.getBzkamc();
			return "验证通过，一证通证件有效";
		}
		return "验证通过，证件有效";
	}

	/**
	 * 
	 * @方法名：returnYfjg
	 * @功能说明：返回验证结果
	 * @author 赵琳
	 * @date 2013-7-9 上午11:28:45
	 * @param yfjg
	 * @param result
	 * @param tsxx
	 * @param zjxx
	 * @return
	 */
	public YfResult returnYfjg(YfResult yfjg, boolean result, String tsxx, Hgzjxx zjxx) {
		yfjg.setResult(result);
		if (!result && tsxx == null) {
			if (yfjg.getShip() != null && yfjg.getQyxx() == null) {
				if (YfZjxxConstant.ZJLX_DLUN.equals(zjxx.getZjlb())) {
					yfjg.setTsxx("验证失败，无权登轮");
				} else if (YfZjxxConstant.ZJLX_DK.equals(zjxx.getZjlb())) {
					yfjg.setTsxx("验证失败，无权搭靠");
				}
			} else if (yfjg.getShip() == null && yfjg.getQyxx() != null) {
				if (YfZjxxConstant.ZJLX_DLUN.equals(zjxx.getZjlb()) || YfZjxxConstant.ZJLX_XDQY.equals(zjxx.getZjlb())) {
					yfjg.setTsxx("验证失败，无权进卡口");
				}
			}
		} else {
			yfjg.setTsxx(tsxx);
		}
		yfjg.setZjlx(zjxx.getZjlb());
		yfjg.setZjxx(zjxx);
		return yfjg;
	}

	/**
	 * 
	 * @方法名：cbIsInZjqy
	 * @功能说明：绑定的船舶是否在证件区域
	 * @author 赵琳
	 * @date 2013-7-6 下午03:34:04
	 * @param zjxx
	 * @param cb
	 * @param yfjg
	 * @return
	 */
	public boolean cbIsInZjqy(Hgzjxx zjxx, Kacbqk cb, YfResult yfjg) {
		String tkmt = cb.getTkmt();
		String zjssmt = zjxx.getSsmt();// 证件所属码头
		if (tkmt != null && zjssmt != null && tkmt.equals(zjssmt)) {// 证件的码头与船的码头相同
			if (this.yzZjxxDateSfYx(zjxx, yfjg)) { // 证件是否有效
				this.returnYfjg(yfjg, YfZjxxConstant.YFJG_CG, "验证通过，证件有效", zjxx);
				return true;
			} else {
				return false;
			}
		} else {
			this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, null, zjxx);
			return false;
		}
	}

	/**
	 * 
	 * @方法名：
	 * @功能说明：船员是否可以进入限定区域验证
	 * @author zhujy
	 * @date 2013-7-25 下午05:29:35
	 * @param cyxx
	 * @param ship
	 * @param qyxx
	 * @param yfjg
	 * @return
	 */
	public boolean cyIsInQy(TBCyxx cyxx, Kacbqk ship, Qyxx qyxx, YfResult yfjg) {
		if (ship != null) {
			String qufw = qyxx.getQyfwbwdm() + "," + qyxx.getQyfwmtdm();// 得到区域范围
			String[] fw = qufw.split(",");
			String tkwz = ship.getTkmt() + "|" + ship.getTkbw();
			if (ship.getTkbw() != null) {
				if (fw != null && fw.length > 0) {
					// 判断船舶停靠位置是否在卡口区域范围内
					for (int j = 0; j < fw.length; j++) {
						if (fw[j].equals(tkwz)) {
							// 船在卡口内并且证件为已发状态
							yfjg.setQyxx(qyxx);
							yfjg.setResult(YfZjxxConstant.YFJG_CG);
							yfjg.setTsxx("验证通过，是卡口内船员");
							yfjg.setCyxx(cyxx);
							yfjg.setZjlx(cyxx.getZjlx());
							return true;
						}
					}
				}
			} else {
				if (qufw.contains(ship.getTkmt())) {
					yfjg.setQyxx(qyxx);
					yfjg.setResult(YfZjxxConstant.YFJG_CG);
					yfjg.setTsxx("验证通过，是卡口内船员");
					yfjg.setCyxx(cyxx);
					yfjg.setZjlx(cyxx.getZjlx());
					return true;
				}
			}

		}
		return false;

	}

	/**
	 * 
	 * @方法名：zjCbIsInSsyq
	 * @功能说明：证件船舶是否在所属区域
	 * @author 赵琳
	 * @date 2013-7-6 下午03:13:52
	 * @param qyxx
	 * @param zjxx
	 * @param cb
	 * @param yfjg
	 * @return
	 */
	public boolean zjCbIsInSsyq(Qyxx qyxx, Hgzjxx zjxx, Kacbqk cb, YfResult yfjg) {
		String tkmt = cb.getTkmt();
		String qyfws = qyxx.getQyfwbwdm() + "," + qyxx.getQyfwmtdm();// 得到区域范围
		if (qyIsContainsZjQy(qyfws, tkmt)) {// 证件的码头与船的码头相同
			if (YfZjxxConstant.ZJ_FW_CB.equals(zjxx.getFffw()) && YfZjxxConstant.ZJXX_BHCYX.equals(zjxx.getYcyxbz())) {
				this.returnYfjg(yfjg, YfZjxxConstant.YFJG_CG, "验证通过，证件有效", zjxx);
				return true;
			} else {
				return this.yzZjxxDateSfYx(zjxx, yfjg);
			}

		} else {
			this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, null, zjxx);
			return false;
		}
	}

	/**
	 * 
	 * @方法名：zjqyIsInSsqy
	 * @功能说明：证件区域是否在所属区域
	 * @author 赵琳
	 * @date 2013-7-5 下午04:19:30
	 * @param zjxx
	 * @param qyxx
	 * @param yfjg
	 * @return
	 */
	public boolean zjqyIsInSsqy(Hgzjxx zjxx, Qyxx qyxx, YfResult yfjg) {
		String ssmt = zjxx.getSsmt();// 证件的范围码头
		String qyfwmts = qyxx.getQyfwbwdm() + "," + qyxx.getQyfwmtdm();// 得到区域范围
		if (qyIsContainsZjQy(qyfwmts, ssmt)) {
			return this.yzZjxxDateSfYx(zjxx, yfjg);

		} else {
			this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, null, zjxx);
			return false;
		}
	}

	/**
	 * 
	 * @方法名：zjqyIsXdqy
	 * @功能说明：判断限定区域是否是证件所属区域
	 * @author zhujy
	 * @date 2013-7-25 上午10:03:25
	 * @param zjxx
	 *            海港证件信息
	 * @param qyxx
	 *            区域信息
	 * @param yfjg
	 *            验放结果
	 * @return
	 */
	public boolean zjqyIsXdqy(Hgzjxx zjxx, Qyxx qyxx, YfResult yfjg) {
		if (StringUtils.isNotEmpty(zjxx.getSsqy()) && zjxx.getSsqy().equals(qyxx.getId())) {
			// 是所属区域判断证件是否过期
			return this.yzZjxxDateSfYx(zjxx, yfjg);
		} else {
			this.returnYfjg(yfjg, YfZjxxConstant.YFJG_SB, null, zjxx);
			return false;
		}

	}

	/**
	 * 
	 * @方法名：qyIsContainsZjQy
	 * @功能说明： 区域是否包含证件范围区域
	 * @author 赵琳
	 * @date 2013-7-6 下午03:01:35
	 * @param qy
	 * @param zjqy
	 * @return
	 */
	public boolean qyIsContainsZjQy(String qy, String zjqy) {
		return qy != null && zjqy != null && qy.contains(zjqy);
	}

	/**
	 * 
	 * @方法名：zjIsQgty
	 * @功能说明：证件是否全港通用
	 * @author 赵琳
	 * @date 2013-7-5 下午03:26:30
	 * @param zjxx
	 * @param yfjg
	 * @return
	 */
	public boolean zjIsQgty(Hgzjxx zjxx, YfResult yfjg) {
		return this.yzZjxxDateSfYx(zjxx, yfjg);
	}

}
