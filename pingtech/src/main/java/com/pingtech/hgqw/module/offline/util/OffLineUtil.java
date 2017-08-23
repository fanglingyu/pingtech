package com.pingtech.hgqw.module.offline.util;

/**
 * 
 * 
 * 类描述：
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-OffLineUtil.java
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
 * @date 2013-10-15 上午9:32:05
 */
public class OffLineUtil {
	/**
	 * 绑定船舶后，下载全部相关信息(梯口)
	 */
	public static final int DOWNLOAD_ALL_OFFLINE_DATA_FOR_HC = 100;

	/**
	 * 绑定卡口后，下载全部相关信息(卡口)
	 */
	public static final int DOWNLOAD_ALL_OFFLINE_DATA_FOR_KKID = 101;

	/** 根据船舶下载 */
	public static final int DOWNLOAD_FOR_KACBQK = 1;

	public static final int DOWNLOAD_FOR_MT_OR_BW_QY = 5;

	/** 只下载长期证、码头、泊位、区域 */
	public static final int DOWNLOAD_CQZ_MT_BW_QY = 0;
	public static final int DOWNLOAD_CQZ_MT_BW_QY_NO_DELETE = 21001;

	/** 更新长期证 */
	public static final int UPDATE_CQZ = -1;

	/** 根据区域下载 */
	public static final int DOWNLOAD_FOR_QYXX = 2;

	/** 离线船舶下载接口 */
	public static final String OFFLINE_KACBQK_ADD_URL = "sentOffLineShipxx";

	/** 离线船舶定时更新时间 */
	public static final long OFFLINE_KACBQK_ADD_WAITTIME = 10 * 60 * 1000;

	/** 离线船舶网络请求标识 */
	public static final int OFFLINE_KACBQK_ADD_RESULT_CODE = 1000;

	/** 离线证件下载接口 */
	public static final String OFFLINE_HGZJXX_ADD_URL = "setOffLineHgzjxx";

	/** 离线证件定时更新时间 */
	public static final long OFFLINE_HGZJXX_ADD_WAITTIME = 10 * 60 * 1000;

	/** 离线证件网络请求标识 */
	public static final int OFFLINE_HGZJXX_ADD_RESULT_CODE = 1001;

	/** 码头代码下载接口 */
	public static final String OFFLINE_MTDM_ADD_URL = "sentOffLineMtdm";

	/** 码头代码定时更新时间 */
	public static final long OFFLINE_MTDM_ADD_WAITTIME = 10 * 60 * 1000;

	/** 码头代码网络请求标识 */
	public static final int OFFLINE_MTDM_ADD_RESULT_CODE = 1002;

	/** 泊位代码下载接口 */
	public static final String OFFLINE_BWDM_ADD_URL = "sentOffLineBwdm";

	/** 泊位代码定时更新时间 */
	public static final long OFFLINE_BWDM_ADD_WAITTIME = 10 * 60 * 1000;

	/** 泊位代码网络请求标识 */
	public static final int OFFLINE_BWDM_ADD_RESULT_CODE = 1003;

	/** 区域信息下载接口QYXX */
	public static final String OFFLINE_QYXX_ADD_URL = "sentOffLineQyxx";

	/** 区域信息定时更新时间 */
	public static final long OFFLINE_QYXX_ADD_WAITTIME = 10 * 60 * 1000;

	/** 区域信息网络请求标识 */
	public static final int OFFLINE_QYXX_ADD_RESULT_CODE = 1004;

	/** 离线证件删除接口HGZJXX */
	public static final String OFFLINE_HGZJXX_DELETE_URL = "setOffLineHgzjxx";

	/** 离线证件定时更新时间 */
	public static final long OFFLINE_HGZJXX_DELETE_WAITTIME = 10 * 60 * 1000;

	/** 离线证件网络请求标识 ---删除 */
	public static final int OFFLINE_HGZJXX_DELETE_RESULT_CODE = 1005;

	/** 船员信息下载接口QYXX */
	public static final String OFFLINE_CYXX_ADD_URL = "sentOffLineCyxx";

	/** 船员信息定时更新时间 */
	public static final long OFFLINE_CYXX_ADD_WAITTIME = 10 * 60 * 1000;

	/** 船员信息网络请求标识 */
	public static final int OFFLINE_CYXX_ADD_RESULT_CODE = 1006;

	/** 服务性船舶信息下载接口Fwxcb */
	public static final String OFFLINE_FWXCB_ADD_URL = "sentOffLineFwxcb";

	/** 服务性船舶信息定时更新时间 */
	public static final long OFFLINE_FWXCB_ADD_WAITTIME = 10 * 60 * 1000;

	/** 服务性船舶信息网络请求标识 */
	public static final int OFFLINE_FWXCB_ADD_RESULT_CODE = 1007;

	/** 一线用户信息下载接口 Userinfo */
	public static final String OFFLINE_USERINFO_ADD_URL = "sentOffLineUserInfo";

	/** 一线用户信息定时更新时间 */
	public static final long OFFLINE_USERINFO_ADD_WAITTIME = 10 * 60 * 1000;

	/** 一线用户信息网络请求标识 */
	public static final int OFFLINE_USERINFO_ADD_RESULT_CODE = 1008;

	/** 智能设备下载接口SBXX */
	public static final String OFFLINE_SBXX_ADD_URL = "sentOffLineSbxx";

	/** 智能设备定时更新时间 */
	public static final long OFFLINE_SBXX_ADD_WAITTIME = 10 * 60 * 1000;

	/** 智能设备网络请求标识 */
	public static final int OFFLINE_SBXX_ADD_RESULT_CODE = 1009;

	/** 手持设备下载接口SCSB */
	public static final String OFFLINE_SCSB_ADD_URL = "sentOffLineScsb";

	/** 手持设备定时更新时间 */
	public static final long OFFLINE_SCSB_ADD_WAITTIME = 10 * 60 * 1000;

	/** 手持设备网络请求标识 */
	public static final int OFFLINE_SCSB_ADD_RESULT_CODE = 1010;

	/** 摄像头下载接口SXTGL */
	public static final String OFFLINE_SXTGL_ADD_URL = "sentOffLineSxtgl";

	/** 摄像头定时更新时间 */
	public static final long OFFLINE_SXTGL_ADD_WAITTIME = 10 * 60 * 1000;

	/** 摄像头网络请求标识 */
	public static final int OFFLINE_SXTGL_ADD_RESULT_CODE = 1011;

	// 2014-04-16////////////////////
	/** 根据船舶下载证件 */
	public static final String OFFLINE_HGZJXX_ADD_URL_FOR_KACBQK = "sentOffLineHgzjxxByHc";

	/** 根据船舶下载证件 */
	public static final long OFFLINE_HGZJXX_ADD_WAITTIME_FOR_KACBQK = 10 * 60 * 1000;

	/** 根据船舶下载证件 */
	public static final int OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_KACBQK = 10001;

	/** 根据区域下载证件 */
	public static final String OFFLINE_HGZJXX_ADD_URL_FOR_QYXX = "sentOffLineHgzjxxByKkId";

	/** 根据区域下载证件 */
	public static final long OFFLINE_HGZJXX_ADD_WAITTIME_FOR_QYXX = 10 * 60 * 1000;

	/** 根据区域下载证件 */
	public static final int OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX = 10002;

	/** 下载长期证 */
	public static final String OFFLINE_HGZJXX_ADD_URL_FOR_CQZ = "setOffLineHgzjxx";

	/** 下载长期证 */
	public static final long OFFLINE_HGZJXX_ADD_WAITTIME_FOR_CQZ = 10 * 60 * 1000;

	/** 下载长期证 */
	public static final int OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_CQZ = 10007;

	/** 根据航次下载船舶 */
	public static final String OFFLINE_KACBQK_ADD_URL_FOR_KACBQK = "sentOffLineShipxxByHc";

	/** 根据航次下载船舶 */
	public static final long OFFLINE_KACBQK_ADD_WAITTIME_FOR_KACBQK = 10 * 60 * 1000;

	/** 根据航次下载船舶 */
	public static final int OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_KACBQK = 10003;

	/** 根据区域下载船舶 */
	public static final String OFFLINE_KACBQK_ADD_URL_FOR_QYXX = "sentOffLineShipxxByKkId";

	/** 根据区域下载船舶 */
	public static final long OFFLINE_KACBQK_ADD_WAITTIME_FOR_QYXX = 10 * 60 * 1000;

	/** 根据区域下载船舶 */
	public static final int OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_QYXX = 10004;

	/** 根据船舶下载船员信息 */
	public static final String OFFLINE_CYXX_ADD_URL_FOR_KACBQK = "sentOffLineCyxxByHc";

	/** 根据船舶下载船员信息 */
	public static final long OFFLINE_CYXX_ADD_WAITTIME_FOR_KACBQK = 10 * 60 * 1000;

	/** 根据船舶下载船员信息 */
	public static final int OFFLINE_CYXX_ADD_RESULT_CODE_FOR_KACBQK = 10005;

	/** 根据区域下载船员信息 */
	public static final String OFFLINE_CYXX_ADD_URL_FOR_QYXX = "sentOffLineCyxxByKkId";

	/** 根据区域下载船员信息 */
	public static final long OFFLINE_CYXX_ADD_WAITTIME_FOR_QYXX = 10 * 60 * 1000;

	/** 根据区域下载船员信息 */
	public static final int OFFLINE_CYXX_ADD_RESULT_CODE_FOR_QYXX = 10006;

	/** 根据航次下载全部相关离线信息 */
	public static final String OFFLINE_ALL_OFFLINE_DATA_URL_FOR_HC = "sentPDAOffLineByHc";

	/** 根据航次下载全部相关离线信息 */
	public static final long OFFLINE_ALL_OFFLINE_DATA_WAITTIME_FOR_HC = 10 * 60 * 1000;

	/** 根据航次下载全部相关离线信息 */
	public static final int OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_HC = 10008;

	/** 根据卡口下载全部相关离线信息 */
	public static final String OFFLINE_ALL_OFFLINE_DATA_URL_FOR_KKID = "sentPDAOffLineByKkid";

	/** 根据卡口下载全部相关离线信息 */
	public static final long OFFLINE_ALL_OFFLINE_DATA_WAITTIME_FOR_KKID = 10 * 60 * 1000;

	/** 根据卡口下载全部相关离线信息 */
	public static final int OFFLINE_ALL_OFFLINE_DATA_CODE_FOR_KKID = 10009;

	// ////
	/** 根据码头下载船舶 */
	public static final String OFFLINE_KACBQK_ADD_URL_FOR_MTID = "sentOffLineShipxxByMtId";

	/** 根据码头下载船舶 */
	public static final long OFFLINE_KACBQK_ADD_WAITTIME_FOR_MTID = 10 * 60 * 1000;

	/** 根据码头下载船舶 */
	public static final int OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_MTID = 10010;

	// ////
	/** 根据码头下载证件 */
	public static final String OFFLINE_HGZJXX_ADD_URL_FOR_MTID = "sentOffLineHgzjxxByMtId";

	/** 根据码头下载证件 */
	public static final long OFFLINE_HGZJXX_ADD_WAITTIME_FOR_MTID = 10 * 60 * 1000;

	/** 根据码头下载证件 */
	public static final int OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_MTID = 10011;

	// ////
	/** 根据码头下载船员 */
	public static final String OFFLINE_CYXX_ADD_URL_FOR_MTID = "sentOffLineCyxxByMtId";

	/** 根据码头下载船员 */
	public static final long OFFLINE_CYXX_ADD_WAITTIME_FOR_MTID = 10 * 60 * 1000;

	/** 根据码头下载证件船员 */
	public static final int OFFLINE_CYXX_ADD_RESULT_CODE_FOR_MTID = 10012;

	// ////
	/** 根据泊位下载船舶 */
	public static final String OFFLINE_KACBQK_ADD_URL_FOR_BWID = "sentOffLineShipxxByMtId";

	/** 根据泊位下载船舶 */
	public static final long OFFLINE_KACBQK_ADD_WAITTIME_FOR_BWID = 10 * 60 * 1000;

	/** 根据泊位下载船舶 */
	public static final int OFFLINE_KACBQK_ADD_RESULT_CODE_FOR_BWID = 10013;

	// ////
	/** 根据泊位下载证件 */
	public static final String OFFLINE_HGZJXX_ADD_URL_FOR_BWID = "sentOffLineHgzjxxByMtId";

	/** 根据泊位下载证件 */
	public static final long OFFLINE_HGZJXX_ADD_WAITTIME_FOR_BWID = 10 * 60 * 1000;

	/** 根据泊位下载证件 */
	public static final int OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_BWID = 10014;

	// ////
	/** 根据泊位下载船员 */
	public static final String OFFLINE_CYXX_ADD_URL_FOR_BWID = "sentOffLineCyxxByMtId";

	/** 根据泊位下载船员 */
	public static final long OFFLINE_CYXX_ADD_WAITTIME_FOR_BWID = 10 * 60 * 1000;

	/** 根据泊位下载证件船员 */
	public static final int OFFLINE_CYXX_ADD_RESULT_CODE_FOR_BWID = 10015;
	// ////
	/** 根据泊位下载船员 */
	public static final String URL_SENT_OFFLINE_HGZJXX_ZP = "sentOffLineCyxxByMtId";
	
	/** 根据泊位下载船员 */
	public static final long WAITTIME_SENT_OFFLINE_HGZJXX_ZP = 10 * 60 * 1000;
	
	/** 根据泊位下载证件船员 */
	public static final int RESULT_CODE_SENT_OFFLINE_HGZJXX_ZP = 10016;
	
	// ////
	/** 下载离线碰撞信息 */
	public static final String URL_SENT_OFFLINE_PZXX = "sentOffLineBjts";
	
	/**下载离线碰撞信息 */
	public static final long WAITTIME_SENT_OFFLINE_PZXX = 10 * 60 * 1000;
	
	/**下载离线碰撞信息 */
	public static final int RESULT_CODE_SENT_OFFLINE_PZXX = 10017;
}
