package com.pingtech.hgqw.module.xunjian.utils;

import java.sql.SQLException;
import java.util.HashMap;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.module.bindplace.utils.SharedPreferencesUtil;
import com.pingtech.hgqw.module.offline.base.utils.DbUtil;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.bwdm.service.BwdmService;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;
import com.pingtech.hgqw.module.offline.mtdm.service.MtdmService;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;

public class XcUtil {
	/** 执勤对象类型:船舶：cb 区域:qy 码头：mt 泊位：bw 浮筒：ft 锚地：md */
	public static final String XUN_JIAN_TYPE_CB = "cb";

	/** 执勤对象类型:船舶：cb 区域:qy 码头：mt 泊位：bw 浮筒：ft 锚地：md */
	public static final String XUN_JIAN_TYPE_MT = "mt";

	/** 执勤对象类型:船舶：cb 区域:qy 码头：mt 泊位：bw 浮筒：ft 锚地：md */
	public static final String XUN_JIAN_TYPE__BW = "bw";

	/** 执勤对象类型:船舶：cb 区域:qy 码头：mt 泊位：bw 浮筒：ft 锚地：md */
	public static final String XUN_JIAN_TYPE__QY = "qy";

	public static String buildJwd() {
		String longitude = BaseApplication.instent.getLongitude();
		String latitude = BaseApplication.instent.getLatitude();
		if (StringUtils.isNotEmpty(longitude) && StringUtils.isNotEmpty(latitude)) {
			StringBuffer jwd = new StringBuffer();
			return jwd.append(longitude).append(",").append(latitude).toString();
		}
		return "";
	}

	public static String getXunjianName() {
		String xunjianName = "";
		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("bw")) {
				xunjianName = SystemSetting.xunJianMTname + "" + SystemSetting.xunJianName;
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("mt")) {
				xunjianName = SystemSetting.xunJianName;
			} else if (SystemSetting.xunJianType != null && SystemSetting.xunJianType.equals("qy")) {
				xunjianName = SystemSetting.xunJianName;
			}
		}
		return xunjianName;
	}

	public static String getXunchaBindName() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("cbzwm"))) {
			return (String) xuncha_Binddata.get("cbzwm");
		}
		String xunjianDdName = XcUtil.getXunjianName();
		if (StringUtils.isNotEmpty(xunjianDdName)) {
			return xunjianDdName;
		}
		return null;
	}

	public static String getXunjianHC() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("hc"))) {
			return (String) xuncha_Binddata.get("hc");
		}
		return "";

	}

	public static String getXunjianId() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("hc"))) {
			return (String) xuncha_Binddata.get("hc");
		}

		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			return SystemSetting.xunJianId;
		}
		return "";
	}

	public static String getXunjianType02() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("hc"))) {
			return XUN_JIAN_TYPE_CB;
		}

		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			return SystemSetting.xunJianType;
		}
		return "";
	}

	public static String getXunjianTypeOnLine() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("hc"))) {
			String tkmt = (String) xuncha_Binddata.get("tkmt");
			String tkbw = (String) xuncha_Binddata.get("tkbw");
			if (StringUtils.isEmpty(tkbw)) {
				return "mt";
			} else {
				return "bw";
			}
		}

		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			return SystemSetting.xunJianType;
		}
		return "";
	}
	public static String getXunjianDdidOnLine() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("hc"))) {
			String tkmt = (String) xuncha_Binddata.get("tkmt");
			String tkbw = (String) xuncha_Binddata.get("tkbw");
			if (StringUtils.isEmpty(tkbw)) {
				Mtdm mtdm = null;
				try {
					mtdm = new MtdmService().getMtmcByMtdm(tkmt);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(mtdm!=null){
					return mtdm.getId();
				}
				return tkmt;
			} else {
				Bwdm bwdm = null;
				try {
					bwdm = new BwdmService().getBwmcByMtdmAndBwdm(tkmt, tkbw);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(bwdm!=null){
					return bwdm.getId();
				}
				
				return tkbw;
			}
		}
		
		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			return SystemSetting.xunJianId;
		}
		return "";
	}

	public static String getXunjianType() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("hc"))) {
			return "0";
		}

		String zqdxlx = "";
		if (SystemSetting.xunJianType != null) {
			if (SystemSetting.xunJianType.equals("bw")) {
				zqdxlx = "3";
			} else if (SystemSetting.xunJianType.equals("md")) {
				zqdxlx = "3";
			} else if (SystemSetting.xunJianType.equals("ft")) {
				zqdxlx = "3";
			} else if (SystemSetting.xunJianType.equals("mt")) {
				zqdxlx = "2";
			} else if (SystemSetting.xunJianType.equals("qy")) {
				zqdxlx = "1";
			} else {
				zqdxlx = "";
			}
		}
		return zqdxlx;
	}

	public static String getXunjianTypeForYcxx() {
		// 检查地点(在船上01、在码头02、在泊位03、在区域04)
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null && StringUtils.isNotEmpty(xuncha_Binddata.get("hc"))) {
			return "01";
		}

		String zqdxlx = "";
		if (SystemSetting.xunJianType != null) {
			if (SystemSetting.xunJianType.equals("bw")) {
				zqdxlx = "03";
			} else if (SystemSetting.xunJianType.equals("md")) {
				zqdxlx = "03";
			} else if (SystemSetting.xunJianType.equals("ft")) {
				zqdxlx = "03";
			} else if (SystemSetting.xunJianType.equals("mt")) {
				zqdxlx = "02";
			} else if (SystemSetting.xunJianType.equals("qy")) {
				zqdxlx = "04";
			} else {
				zqdxlx = "";
			}
		}
		return zqdxlx;
	}

	public static void deleteBindInfo() {
		HashMap<String, Object> xuncha_Binddata = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
		if (xuncha_Binddata != null) {
			String hc = (String) xuncha_Binddata.get("hc");
			String kacbqkid = (String) xuncha_Binddata.get("kacbqkid");
			SystemSetting.setBindShip(null, GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "");
			DbUtil.deleteKacbqkByHc(hc);
			DbUtil.deleteHgzjxxByHc(hc);
			DbUtil.deleteCyxxByCbid(kacbqkid);
		}
		if (StringUtils.isNotEmpty(SystemSetting.xunJianId)) {
			XcUtil.clearDdBindInfo();
		}

	}

	public static void clearDdBindInfo() {
		SystemSetting.xunJianMTname = null;
		SystemSetting.xunJianMtgsgs = null;
		SystemSetting.xunJianMtgm = null;
		SystemSetting.xunJianKbnl = null;
		SystemSetting.xunJianZxhz = null;
		SystemSetting.xunJianQyfw = null;
		SystemSetting.xunJianXxxx = null;
		SystemSetting.xunJianWz = null;
		SystemSetting.xunJianZdmbcbdw = null;
		SystemSetting.xunJianSs = null;
		SystemSetting.xunJianMbdsl = null;
		SystemSetting.xunJianZdgkcbdw = null;
		SystemSetting.xunJianMtzax = null;
		SystemSetting.xunJianQylx = null;
		SystemSetting.xunJianType = null;
		SystemSetting.xunJianId = null;
		SystemSetting.xunJianName = null;
		SystemSetting.xunJianMTid = null;
		SharedPreferencesUtil.deleteDdbd();// 删除本地缓存
		DbUtil.deleteForXjdd();
	}
}
