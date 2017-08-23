package com.pingtech.hgqw.module.qwjw.utils;

public class QwzlConstant {
	/** 超时时间标示 */
	public static final String QWZL_TIMEOUT_KEY = "zl.timeout";

	/** zlzl 指令种类 JWZL警务指令，QWZL 勤务指令 */
	public static final String ZLZL_QWZL = "QWZL";

	/** zlzl 指令种类 JWZL警务指令，QWZL 勤务指令 */
	public static final String ZLZL_JWZL = "JWZL";

	/** 签收状态 0：未签收 1：已签收 */
	public static final String QSZT_YQS = "1";

	/** 签收状态 0：未签收 1：已签收 */
	public static final String QSZT_WQS = "0";

	/** 菜单横标签 */
	public static final String QWZL_MESSAGE_TOP = "qwzh";

	/** 菜单左标签 */
	public static final String QWZL_MESSAGE_LEFT = "qwjs";

	public static final String QWZL_MESSAGE_LEFTPQ = "qwpq";

	/** 勤务指令处警信息表中，单位类型字段 ：参与单位 */
	public static final String QWZL_QWZLCJXX_DWLX_CYDW = "cydw";

	/** 勤务指令处警信息表中，单位类型字段 ：受令单位 */
	public static final String QWZL_QWZLCJXX_DWLX_SLDW = "sldw";

	/** 勤务指令处警信息表中，单位类型字段 ：值勤单位 */
	public static final String QWZL_QWZLCJXX_DWLX_ZQDW = "zqdw";

	/** 勤务指令处警信息表中，单位类型字段 ：值勤受令单位（执勤单位和受令单位是一个单位） */
	public static final String QWZL_QWZLCJXX_DWLX_ZQSLDW = "zqsldw";

	/** 指令类型 船舶监管01 */
	public static final String JBXX_ZLLX_CBJH = "01";

	/** 指令类型 船舶巡查02 */
	public static final String JBXX_ZLLX_CBXC = "02";

	/** 指令类型 执勤变更03 */
	public static final String JBXX_ZLLX_ZQBG = "03";

	/** 指令类型 船舶移泊04 */
	public static final String JBXX_ZLLX_CBYB = "04";

	/** 指令类型 船体检查05 */
	public static final String JBXX_ZLLX_CTJC = "05";

	/** 指令类型 物品检查06 */
	public static final String JBXX_ZLLX_WPJC = "06";

	/** 指令类型 搭靠检查07 */
	public static final String JBXX_ZLLX_DKJC = "07";

	/** 指令类型 其他99 */
	public static final String JBXX_ZLLX_QTGZ = "99";

	/** 指令类型 数据字典 */
	public static final String JBXX_ZLLX_SJZD = "101184";

	public static String getZllxName(String zllx) {
		if ("01".equals(zllx)) {
			return "船舶监管";
		} else if ("02".equals(zllx)) {
			return "船舶巡查";
		} else if ("03".equals(zllx)) {
			return "执勤变更";
		} else if ("04".equals(zllx)) {
			return "船舶移泊";
		} else if ("05".equals(zllx)) {
			return "船体检查";
		} else if ("06".equals(zllx)) {
			return "物品检查";
		} else if ("07".equals(zllx)) {
			return "搭靠检查";
		} else if ("99".equals(zllx)) {
			return "其他";
		} else {
			return "错误指令类型：" + zllx;
		}
	}
}
