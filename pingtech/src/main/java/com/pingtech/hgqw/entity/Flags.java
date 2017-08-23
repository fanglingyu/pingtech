package com.pingtech.hgqw.entity;

public class Flags {

	/** 警务通开启离线标识：true启动，false不启动 */
	public static final boolean IF_OPEN_OFFLINE_MODULE = true;
	/**
	 * 播放音频
	 */
	public static final int PLAY_AUDIO = 0;

	/**
	 * 删除音频
	 */
	public static final int DELETE_AUDIO = 1;

	/**
	 * Activity标识：船方自管刷卡页面
	 */
	public static final int FROM_CFZG_READCARD = 3;

	/**
	 * 警务通版本标识：-1测试版 ， 0巡查版，1哨兵版，2船方自管
	 */
	public static final int PDA_VERSION = 0;

	/** 演示版 */
	public static final int PDA_VERSION_BATE = -1;

	/** 警务通版本标识：0默认版本 */
	public static final int PDA_VERSION_DEFAULT = 0;

	/**
	 * 警务通版本标识： 1哨兵版
	 */
	public static final int PDA_VERSION_SENTINEL = 1;

	/**
	 * 警务通版本标识： 2船方自管
	 */
	public static final int PDA_VERSION_CFZG = 2;
	/**
	 * 警务通系统自动升级
	 */
	public static final int PDA_VERSION_SYSTEMAUTO = 0;
	/**
	 * 警务通手动升级
	 */
	public static final int PDA_VERSION_BYHAND = 1;

	/**
	 * 关闭读卡器标志：true允许关闭，false禁止关闭。
	 */
	public static boolean closeTimerFlag = true;

	/**
	 * true：IC读卡器已启动；false:ID读卡器已启动
	 */
	public static boolean iCIDFlag = true;

	/**
	 * 关闭读卡器标志：true允许关闭，false禁止关闭。
	 */
	public static boolean closeTimerFlagForBind = true;

	/**
	 * 是否点击人员平衡标识
	 */
	public static boolean peClickFlag = false;

	/**
	 * Handler：启动身份证刷卡器失败
	 */
	public static final int OPEN_FAILED = 1;

	/**
	 * Handler：启动身份证刷卡器成功
	 */
	public static final int OPEN_SUCCESS = 2;

	/**
	 * Handler：身份证刷卡成功
	 */
	public static final int READ_ID_SUCCESS = 3;

	/**
	 * Handler：智能卡读取成功
	 */
	public static final int READ_IC_SUCCESS = 4;

	/**
	 * 网络类型：中国移动
	 */
	public static final int MOBILE_TYPE_CMCC = 0;

	/**
	 * 网络类型：中国联通
	 */
	public static final int MOBILE_TYPE_CUCC = 1;

	/**
	 * 网络类型：中国电信
	 */
	public static final int MOBILE_TYPE_CTCC = 2;

}
