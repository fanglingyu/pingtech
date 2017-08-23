package com.pingtech.hgqw.module.login.utils;

import com.pingtech.hgqw.entity.LoginUser;

public class LitenceUtil {

	/** 用户名密码登录接口参数名 */
	private static final String DO_LOGIN_BY_USERNAME = "doLoginByUserName";

	/** 刷卡登录接口参数名 */
	private static final String DO_LOGIN_BY_CARD = "doLoginByCard";

	/** 设备编号参数名 */
	private static final String PDA_CODE = "PDACode";

	/** 返回到客户端的未授权提示信息 */
	private static final String NOT_LICENCED_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><datas><result>error</result><info>设备未授权，请联系管理员</info></datas>";

	/** 授权状态：0未授权，1已授权 */
	public static final String LICENCED_STATE_NO = "0";

	/** 授权状态：0未授权，1已授权 */
	public static final String LICENCED_STATE_YES = "1";

	/**
	 * 
	 * @方法名：idLicence
	 * @功能说明：验证设备是否授权
	 * @author liums
	 * @date 2013-11-6 下午2:53:21
	 * @param loginUser
	 *            请求参数：只有登录接口doLoginByUserName、doLoginByCard时做Licence验证
	 * @return
	 */
	public static boolean isLicenced(LoginUser loginUser) {
		String licenceInfo = loginUser.getLicence();
		if (LICENCED_STATE_NO.equals(licenceInfo)) {
			return false;
		}
		return true;

	}

	/**
	 * 
	 * @方法名：getPdaCode
	 * @功能说明：从请求参数中解析出设备编号
	 * @author liums
	 * @date 2013-11-6 下午3:17:47
	 * @param context
	 * @return
	 */
	private static String getPdaCode(String context) {
		String[] paramArr = context.split(",");
		for (String str : paramArr) {
			String[] strArr = str.split("=");
			if (strArr != null && strArr.length == 2 && strArr[0] != null) {
				if (strArr[0].contains(PDA_CODE)) {
					return strArr[1];
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @方法名：isLogin
	 * @功能说明：是否是登录请求：只有登录接口doLoginByUserName、doLoginByCard时做Licence验证
	 * @author liums
	 * @date 2013-11-6 下午3:08:54
	 * @param context
	 *            请求参数
	 * @return
	 */
	public static boolean isLogin(String context) {
		if (context == null || "".equals(context.trim())) {// 请求参数为空，返回false，由应用服务器处理
			return false;
		}
		String[] paramArr = context.split(",");
		for (String str : paramArr) {
			String[] strArr = str.split("=");
			if (strArr != null && strArr.length == 2 && strArr[1] != null) {
				if (strArr[1].contains(DO_LOGIN_BY_USERNAME) || strArr[1].contains(DO_LOGIN_BY_CARD)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @方法名：getNotLicencedResult
	 * @功能说明：未授权提示信息
	 * @author liums
	 * @date 2013-11-6 下午3:13:55
	 * @return
	 */
	public static String getNotLicencedResult() {
		return NOT_LICENCED_RESULT;
	}

}
