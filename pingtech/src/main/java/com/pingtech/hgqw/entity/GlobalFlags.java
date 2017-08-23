package com.pingtech.hgqw.entity;

public class GlobalFlags {
	/** 表示船舶动态 0 */
	public static final int LIST_TYPE_FROM_SHIPSTATUS = 0;

	/** 表示梯口管理 1 */
	public static final int LIST_TYPE_FROM_TIKOUMANAGER = 1;

	/** 表示巡查巡检 2 */
	public static final int LIST_TYPE_FROM_XUNCHAXUNJIAN = 2;

	/** 表示卡口管理 3 */
	public static final int LIST_TYPE_FROM_KAKOUMANAGER = 3;

	/** 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3 */
	public static final int ZQDXLX_CB = 0;

	/** 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3 */
	public static final int ZQDXLX_KK = 1;

	/** 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3 */
	public static final int ZQDXLX_MT = 2;

	/** 执勤对象类型:船舶0 卡口(区域)1 码头2 泊位3 */
	public static final int ZQDXLX_BW = 3;

	/*** 船舶动态-船舶绑定 */
	public static final String BINDSHIP_FROM_SHIPSTATUS = "0501";

	/*** 梯口管理-船舶绑定 */
	public static final String BINDSHIP_FROM_TIKOUMANAGER = "0201";

	/*** 巡查巡检-船舶绑定 */
	public static final String BINDSHIP_FROM_XUNCHAXUNJIAN = "0301";

	/*** 卡口管理-船舶绑定 */
	public static final String BINDSHIP_FROM_KAKOUMANAGER = "0101";

}
