package com.pingtech.hgqw.entity;

public class FlagManagers {
	// 01卡口、02梯口、03巡查巡检、04查询人员模块、05船舶动态、0501船舶动态>>>船舶绑定、0201梯口管理>>>船舶绑定、0101卡口管理>>>卡口绑定、0301巡查巡检>>>船舶绑定
	/**
	 * 船舶动态
	 */
	public final static String CBDT = "05";

	/**
	 * 梯口管理
	 */
	public final static String TKGL = "02";

	/**
	 * 卡口管理
	 */
	public final static String KKGL = "01";

	/**
	 * 巡查巡检
	 */
	public final static String XCXJ = "03";
	
	/* 口岸船舶	 */
	public final static String KACB = "04";

	/**
	 * 船舶动态-船舶绑定
	 */
	public final static String CBDT_CBBD = "0501";

	/**
	 * 梯口管理-船舶绑定
	 */
	public final static String TKGL_CBBD = "0201";

	/**
	 * 卡口管理-卡口绑定
	 */
	public final static String KKGL_KKBD = "0101";

	/**
	 * 巡查巡检-船舶绑定
	 */
	public final static String XCXJ_CBBD = "0301";

	/**
	 * 查询人员
	 */
	public final static String CXRY = "04";

	/**
	 * 巡查巡检-查岗查哨
	 */
	public final static String XCXJ_CGCS = "20";
	/**
	 * 巡查巡检-巡检地点-手动选择
	 */
	public final static int INSPECT_PLACE = 3130;
	/**
	 * 船方自管---CustomDialog
	 */
	public final static int CUSTOM_DIALOG = 26;
	/**
	 * 船方自管---CustomDialogForExit
	 */
	public final static int CUSTOM_DIALOG_FOR_EXIT = 27;
	/**
	 * 船方自管---扫描二维码
	 */
	public final static int CUSTOM_SCAN_ZXING = 28;
}
