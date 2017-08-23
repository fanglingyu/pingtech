package com.pingtech.hgqw.module.kacbqk.utils;

public class KacbqkUtil {

	/** 绑定类型 船舶动态0 */
	public static final String BIND_TYPE_CBDT = "0";

	/** 绑定类型 梯口管理1 */
	public static final String BIND_TYPE_TKGL = "1";

	/** 绑定类型 巡查巡检2 */
	public static final String BIND_TYPE_XCXJ = "2";

	/** 绑定类型 卡口管理3 */
	public static final String BIND_TYPE_KKGL = "3";
	
	/**模块类型 口岸船舶4 */
	public static final String BIND_TYPE_KACB = "4";

	/** 绑定状态 1绑定 */
	public static final String BIND_STATE_YES = "1";

	/** 绑定状态 0解除绑定 */
	public static final String BIND_STATE_NO = "0";

	/** 船舶口岸状态 0预到港 */
	public static final String CBKAZT_YDG = "0";

	/** 船舶口岸状态 1在港 */
	public static final String CBKAZT_ZG = "1";

	/** 船舶口岸状态 2 预离港 */
	public static final String CBKAZT_YLG = "2";

	/** 船舶口岸状态 3离港 */
	public static final String CBKAZT_LG = "3";

	/**
	 * 
	 * @方法名：getCbkaztStr
	 * @功能说明：解析选择的口岸船舶状态条件
	 * @author liums
	 * @date 2014-1-15 下午4:17:06
	 * @param cbkaztItem
	 * @return
	 */
	public static String getCbkaztStr(int cbkaztItem) {
		String itemStr = null;
		switch (cbkaztItem) {
		case 0:// 未选择
			break;
		case 1:// 预到港
			itemStr = CBKAZT_YDG;
			break;
		case 2:// 在港
			itemStr = CBKAZT_ZG;
			break;
		case 3:// 预离港
			itemStr = CBKAZT_YLG;
			break;
		default:
			break;
		}
		return itemStr;
	}

}
