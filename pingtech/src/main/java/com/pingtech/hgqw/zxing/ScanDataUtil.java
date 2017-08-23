package com.pingtech.hgqw.zxing;

import java.util.ArrayList;
import java.util.List;

import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.zxing.entity.MsTdc;

/**
 * 二维码扫描结果解析类
 * 
 * @description
 * @date 2014-1-7
 * @author zhaotf
 */
public class ScanDataUtil {
	// 483562013102149^王伟^员工^1^19860627^国电常州发电^在港外轮^20140101^20140630
	// zjhm,xm,zw,xbdm,csrq,ssdw,sdcb,yxqq,yxqz
	// 按顺序对应
	/**
	 * ^标识
	 */
	private static final String FLAG = "^";
	private static final String FLAG_FOR_SPLIT = "\\^";

	/**
	 * ^^标识
	 */
	private static final String FLAGS = "^^";

	/**
	 * 字符串不存在默认值
	 */
	private static final int NO_STRING = -1;

	/**
	 * 数字1
	 */
	private static final int NUM_ONE = 1;

	/**
	 * 当前扫描类型
	 */
	private static int scanType = NO_STRING;

	/**
	 * 扫描类型：登轮许可证
	 */
	public static final int SCAN_TYPE_DENG_LUN = 100;

	/**
	 * 扫描类型：搭靠外轮许可证
	 */
	public static final int SCAN_TYPE_DA_KAO = 101;

	/** 扫描类型：临时入境许可证 */
	public static final int SCAN_TYPE_LSRJXKZ = 102;

	/**
	 * 性别未知
	 */
	private static final int XB_UN_KOWN = 0;

	/**
	 * 男
	 */
	private static final int XB_NAN = 1;

	/**
	 * 女
	 */
	private static final int XB_NV = 2;

	/**
	 * 未说明
	 */
	private static final int XB_UN_DECLARE = 9;

	/**
	 * 
	 * @description 从扫描的数据中解析值(登陆证解析)
	 * @param scanResult
	 *            扫描得到的数据
	 * @return
	 * @date 2014-1-7
	 * @author zhaotf
	 */
	private static MsTdc getMsTdc(String scanResult) {
		String scanResul = scanResult;
		MsTdc msTdc = new MsTdc();
		List<String> listData = getScanData(scanResul);
		msTdc.setScanType(scanType);
		int sum;
		if (listData != null) {
			if ((sum = listData.size()) >= 5) {
				// 表示 扫描结果中含有^标识
				switch (scanType) {
				case SCAN_TYPE_DENG_LUN:
					// zjhm,xm,zw,xbdm,csrq,ssdw,zwcbm,yxqq,yxqz
					for (int i = 0; i < sum; i++) {
						switch (i) {
						case 0:
							msTdc.setZjhm(listData.get(i));
							break;
						case 1:
							msTdc.setXm(listData.get(i));
							break;
						case 2:
							msTdc.setZw(listData.get(i));
							break;
						case 3:
							msTdc.setXbdm(listData.get(i));
							break;
						case 4:
							msTdc.setCsrq(listData.get(i));
							break;
						case 5:
							msTdc.setSsdw(listData.get(i));
							break;
						case 6:
							msTdc.setZwcbm(listData.get(i));
							break;

						default:
							break;
						}
					}
					break;
				case SCAN_TYPE_DA_KAO:
					// zjhm,cbmc,yt,dw,ml,cjg,ssdw,yxqq,yxqz
					for (int i = 0; i < sum; i++) {
						switch (i) {
						case 0:
							msTdc.setZjhm(listData.get(i));
							break;
						case 1:
							msTdc.setCbmc(listData.get(i));
							break;
						case 2:
							msTdc.setYt(listData.get(i));
							break;
						case 3:
							msTdc.setDw(listData.get(i));
							break;
						case 4:
							msTdc.setMl(listData.get(i));
							break;
						case 5:
							msTdc.setCjg(listData.get(i));
							break;
						case 6:
							msTdc.setSsdw(listData.get(i));
							break;

						default:
							break;
						}
					}
					break;

				default:
					break;
				}
			} else if (sum == 1) {
				// 表示扫描的结果中没有^字段，将数据默认设置为“证件号码”字段
				msTdc.setZjhm(listData.get(0));
			}
		}
		return msTdc;
	}

	/**
	 * 得到扫描数据
	 * 
	 * @description
	 * @param scanResult
	 * @return
	 * @date 2014-1-9
	 * @author zhaotf
	 */
	private static List<String> getScanData(String scanResult) {
		String scanResul = scanResult;
		// 出生日期
		String csrq = null;
		// 性别标识
		String xbbs = null;
		List<String> listData = new ArrayList<String>();
		if (StringUtils.isNotEmpty(scanResul)) {
			int firstNum;
			int num = NUM_ONE;
			// 验证方式，先验证登轮证的时间，是否为空，不为空，验证其长度，在验证性别标识
			while ((firstNum = scanResul.indexOf(FLAG)) != NO_STRING) {
				String value = scanResul.substring(0, firstNum);
				// 如果第一个字段开始字符为英文则判定为临时入境许可证（船员）

				switch (num) {
				// zjhm,xm,zw,xbdm,csrq,ssdw,zwcbm,yxqq,yxqz
				// 登轮证
				// 证件号码 ^ 姓名 ^ 职务 ^ 性别标识 ^ 出生日期 ^ 单位 ^ 所登船舶 ^ 有效期开始时间 ^ 有效期截至时间
				// 证件号码 ^ 姓名 ^ 职务 ^ 性别标识 ^ 出生日期 ^ 单位 ^ 所登船舶 ^ 有效期(本航次有效) ^
				// 搭靠外轮许可证
				// 证件号码 ^ 船舶名称 ^ 用途 ^ 吨位 ^ 马力 ^ 船籍港 ^ 所属单位 ^ 有效期开始时间 ^ 有效期截至时间
				case 1:
					// 证件号码
					listData.add(value);
					break;
				case 2:
					// 姓名
					listData.add(value);
					break;
				case 3:
					// 职务
					listData.add(value);
					break;
				case 4:
					// 性别代码
					listData.add(value);
					xbbs = value;
					break;
				case 5:
					// 出生日期
					listData.add(value);
					csrq = value;
					break;
				case 6:
					// 所属单位
					listData.add(value);
					break;
				case 7:
					// 中文船舶名
					listData.add(value);
					break;
				// case 8:
				// // 设置一次有效标志
				// msTdc.setYcyxbz(value);
				// break;
				default:
					break;
				}
				scanResul = scanResul.substring(firstNum + NUM_ONE);
				num++;
			}
			// 如果num为1，表示扫描的结果中没有^
			if (num == NUM_ONE) {
				listData.add(scanResul);
				return listData;
			}

			boolean xbOk = isOkOfxb(xbbs);
			// 如果日期不为空
			if (StringUtils.isNotEmpty(csrq)) {
				if (csrq.length() == 8) {
					// if (xbOk) {
					scanType = SCAN_TYPE_DENG_LUN;
					// } else {
					// scanType = SCAN_TYPE_DA_KAO;
					// }
				} else {
					// 如果长度不是8，那就是搭靠外轮许可证
					scanType = SCAN_TYPE_DA_KAO;
				}
			} else {
				// 如果性别标识是对的
				if (xbOk) {
					scanType = SCAN_TYPE_DENG_LUN;
				} else {
					scanType = SCAN_TYPE_DA_KAO;
				}
			}
		}
		return listData;

	}

	/**
	 * 判断性别标识是否适合
	 * 
	 * @description
	 * @return
	 * @date 2014-1-9
	 * @author zhaotf
	 */
	private static boolean isOkOfxb(String xbbs) {
		if (StringUtils.isNotEmpty(xbbs)
				&& (String.valueOf(XB_UN_KOWN).equals(xbbs) || String.valueOf(XB_NAN).equals(xbbs) || String.valueOf(XB_NV).equals(xbbs) || String
						.valueOf(XB_UN_DECLARE).equals(xbbs))) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @方法名：getMsTdcForAll
	 * @功能说明：
	 * @author liums
	 * @date 2014-3-25 下午4:24:01
	 * @param result
	 * @return
	 */
	// 临时入境许可证TIN HTAY AUNG^119840714^MMR^MA249039^14^WOO JUN^356201401100029
	// 姓名^ 出生日期^国籍^证件号码^证件种类^服务船舶^船舶航次号

	// 登轮证、搭靠证， 483562013102149^王伟^员工^1^19860627^国电常州发电^在港外轮^20140101^20140630
	// 登轮证：证件号码 ^ 姓名 ^ 职务 ^ 性别标识 ^ 出生日期 ^ 单位 ^ 所登船舶 ^ 有效期(本航次有效) ^
	// 搭靠证：证件号码 ^ 船舶名称 ^ 用途 ^ 吨位 ^ 马力 ^ 船籍港 ^ 所属单位 ^ 有效期开始时间 ^ 有效期截至时间
	public static MsTdc getMsTdcForAll(String result) {
		result = result.trim();
//		result = "TIN HTAY AUNG^119840714^MMR^MA249039^14^WOO JUN^356201401100029";
		if (StringUtils.isEmpty(result)) {
			return null;
		}

		String[] resultArr = result.split(FLAG_FOR_SPLIT);
		if (resultArr.length < 1) {
			return null;
		}

		if (Character.isDigit(result.charAt(0))) {// 首字母是数字,登轮证或搭靠证
			// return getDlOrDkInfo(resultArr);
			return getMsTdc(result);
		} else {// 临时入境许可证
			return getLsrjxkInfo(resultArr);
		}
	}

	// 姓名^性别代码^出生日期^国籍^证件号码^证件种类^服务船舶^船舶航次号
	private static MsTdc getLsrjxkInfo(String[] resultArr) {
		MsTdc msTdc = new MsTdc();
		msTdc.setXm(resultArr[0]);
		msTdc.setXbdm(resultArr[1]);
		msTdc.setCsrq(resultArr[2]);
		msTdc.setGjdm(resultArr[3]);
		msTdc.setZjhm(resultArr[4]);
		msTdc.setZjlbdm(resultArr[5]);
		msTdc.setCbmc(resultArr[6]);
		msTdc.setFwcbHc(resultArr[7]);
		msTdc.setScanType(SCAN_TYPE_LSRJXKZ);
		return msTdc;
	}
}
