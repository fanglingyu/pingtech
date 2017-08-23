package com.pingtech.hgqw.entity;

/**
 * 
 * 
 * 类描述：模块及功能点标识
 * 
 * <p>
 * Title: 海江港边检勤务综合管理系统-ManagerFlag.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-9-27 上午11:38:24
 */
public class ManagerFlag {

	/** 船舶动态 */
	public static final int PDA_CBDT = 101;

	/** 船舶动态_船舶抵港 */
	public static final int PDA_CBDT_CBDG = 10101;

	/** 船舶动态_船舶移泊 */
	public static final int PDA_CBDT_CBYB = 10102;

	/** 船舶动态_船舶靠泊 */
	public static final int PDA_CBDT_CBKB = 10103;

	/** 船舶动态_船舶离港 */
	public static final int PDA_CBDT_CBLG = 10104;

	/** 梯口管理 */
	public static final int PDA_TKGL = 102;

	/** 梯口管理_通行验证 */
	public static final int PDA_TKGL_TXYZ = 10201;

	/** 梯口管理_船舶搭靠 */
	public static final int PDA_TKGL_CBDK = 10202;

	/** 梯口管理_车辆验证 */
	public static final int PDA_TKGL_CLYZ = 10203;

	/** 卡口管理 */
	public static final int PDA_KKGL = 103;

	/** 卡口管理_通行验证 */
	public static final int PDA_KKGL_TXYZ = 10301;
	/** 卡口管理_车辆验证 */
	public static final int PDA_KKGL_CLYZ = 10303;
	/** 巡查巡检 */
	public static final int PDA_XCXJ = 104;

	/** 巡查巡检_巡检记录 */
	public static final int PDA_XCXJ_XJJL = 10401;

	/** 巡查巡检_保存查岗查哨记录 */
	public static final int PDA_XCXJ_CGCS = 10402;

	/** 巡查巡检_车辆检查 */
	public static final int PDA_XCXJ_CLYZ = 10403;

	/** 物品检查 */
	public static final int PDA_WPJC = 105;

	/** 物品检查_记录物品 */
	public static final int PDA_WPJC_JLWP = 10501;

	/** 异常信息 */
	public static final int PDA_YCXX = 106;

	/** 异常信息_添加处理 */
	public static final int PDA_YCXX_TJCL = 10601;

	/** 语音对讲 */
	public static final int PDA_YYDJ = 107;

	/** 我的警务 */
	public static final int PDA_WDJW = 108;

	/** 数据采集 */
	public static final int PDA_SJCJ = 109;

	/** 数据采集-数据定位 */
	public static final int PDA_SJCJ_SJDW = 10901;

	/** 系统管理 */
	public static final int PDA_XTGL = 110;
}
