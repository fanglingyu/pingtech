package com.pingtech.hgqw.module.offline.zjyf.util;

/**
 * 
 * 
 * 类描述：验放证件信息常量类
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-YfZjxxConstant.java
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
 * @date 2013-7-2 上午10:01:03
 */
public class YfZjxxConstant {
	/** 证件_区域类型标识_人员*/
	public static final String ZJ_QYLXBS_RY = "0";
	
	/** 证件_区域类型标识_车辆*/
	public static final String ZJ_QYLXBS_CL = "1";
	
	/** 通行方向-上船、搭靠 */
	public static final String TXFX_SC = "0";

	/** 通行方向-下船、离船 */
	public static final String TXFX_XC = "1";

	/** 是否船员家属-0非船员家属，1船员家属 */
	public static final String SFCYJS_YES = "1";

	/** 是否船员家属-0非船员家属，1船员家属 */
	public static final String SFCYJS_NO = "0";

	/** 进出卡口类型，进入者 */
	public static final String JCKKLX_JRZ = "JRZ";

	public static final String JCKKLX_CQZ = "CQZ";

	/** 证件类型_登轮 */
	public static final String ZJLX_DLUN = "48";

	/** 证件类型_无证 */
	public static final String ZJLX_WZ = "WZ";

	/** 证件类型_搭靠 */
	public static final String ZJLX_DK = "52";

	/** 证件类型_限定区域通行证 */
	public static final String ZJLX_XDQY = "9x";

	/** 证件类型_船员 */
	public static final String ZJLX_CY = "CY";

	/** 证件发放状态_未发放 */
	public static final String ICFFZT_WFF = "0";

	/** 证件发放状态_已发放 */
	public static final String ICFFZT_YFF = "1";

	/** 证件发放状态_已注销 */
	public static final String ICFFZT_YZX = "2";

	/** 证件查询是否刷卡_刷卡 */
	public static final String ZJCX_SFSK_SK = "0";

	/** 证件查询是否刷卡_手动输入 */
	public static final String ZJCX_SFSK_SDSR = "1";

	/** 证件查询是否刷卡_二维码扫描 */
	public static final String ZJCX_SFSK_ZXING = "2";

	/** 验放方式_PDA梯口 */
	public static final String YFFS_PDATK = "PDATK";

	/** 验放方式_PDA卡口 */
	public static final String YFFS_PDAKK = "PDAKK";

	/** 验放方式_硬件设备_梯口 */
	public static final String YFFS_YJSB_TK = "1";

	/** 验放方式_硬件设备_卡口 */
	public static final String YFFS_YJSB_KK = "0";

	/** 验放类型_PDA */
	public static final String YFLX_PDA = "PDA";

	/** 验放类型_硬件设备 */
	public static final String YFLX_YJSB = "YJSB";

	/** 服务范围 1表示船舶，2表示码头，3表示全港通用，4限定区域通行证 */
	public static final String ZJ_FW_CB = "1";

	/** 服务范围 1表示船舶，2表示码头，3表示全港通用，4限定区域通行证 */
	public static final String ZJ_FW_MT = "2";

	/** 服务范围 1表示船舶，2表示码头，3表示全港通用，4限定区域通行证 */
	public static final String ZJ_FW_QGTY = "3";
	
	/** 服务范围 1表示船舶，2表示码头，3表示全港通用，4限定区域通行证 */
	public static final String ZJ_FW_XDQY = "4";
	
	/** 服务范围 1表示船舶，2表示码头，3表示全港通用，4限定区域通行证，5一证通 */
	public static final String ZJ_FW_YZT = "5";

	/** 证件_是否全港通用_全港通用 */
	public static final String ZJ_SFQGTY_SHI = "1";

	/** 验放结果_成功 */
	public static final boolean YFJG_CG = true;

	/** 验放结果_失败 */
	public static final boolean YFJG_SB = false;

	public static final String SB_LX_JY = "2";// 简易型

	public static final String SB_LX_ZQ = "1";// 增强型

	public static final String ZJXX_BHCYX = "1";// 本航次有效

	/** 登轮人员FILO先进后出、船员FOLI先出后进 */
	public static final String FILO = "filo";

	/** 登轮人员FILO先进后出、船员FOLI先出后进 */
	public static final String FOLI = "foli";
	
	/** 是否为一卡通	1：是*/
	public static final String ZJ_ISYKT_YES = "1";
	
	/** 是否为一卡通	0：否*/
	public static final String ZJ_ISYKT_NO = "0";

}
