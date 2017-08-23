package com.pingtech.hgqw.entity;

public class FlagUrls {
	/**
	 * 请求在线用户列表
	 */
	public final static int GET_USER_LIST = 990;

	/**
	 * 上传语音
	 */
	public final static int TALK = 991;

	/**
	 * 获取语音
	 */
	public final static int LISTEN = 992;
	
	/**
	 * 更新人员动态
	 */
	public final static int ANNUN = 993;

	/**
	 * 船方自管：船员上下船验证
	 */
	public final static int SAVE_OR_UPDATE_TKTXJL = 1001;

	/**
	 * 船方自管：紧急报警
	 */
	public final static int URGENCY_WARNING_INFO = 2001;

	/**
	 * 船方自管：更新本船船下船员，非本船船上人员
	 */
	public final static int GET_COUNT_OF_TKTXJL = 2002;
	
	/**
	 * 注销接口
	 */
	public final static int  DO_LOGOUT = 23;
	
	/**
	 * 软件升级
	 */
	public static final int CHECK_UPDATE = 8001;
	/**
	 * 验证用户密码
	 */
	public static final int VALIDATE_PASSWORD = 22;

}
