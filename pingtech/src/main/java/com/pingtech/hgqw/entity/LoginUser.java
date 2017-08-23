package com.pingtech.hgqw.entity;

import com.pingtech.hgqw.base.BaseApplication;

/** 当前登录用户信息类 */
public class LoginUser {
	private static LoginUser _CurrentUser;

	private String _UserName;

	private String _Name;

	private String _zqjlid;

	private String _ID;

	private String _ssdw;

	private String password;

	/** 授权标志，0未授权，1已授权 */
	private String licence = "1";

	public String getLicence() {
		return licence;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 人员类别标识，用于区分版本：0系统默认，1哨兵，2船方自管，3巡检人员
	 */
	private int rylb;

	public int getRylb() {
		return rylb;
	}

	public void setRylb(int rylb) {
		this.rylb = rylb;
	}

	/** 管理用户权限 */
	private int _Permission = 0;

	public int getPermission() {
		return _Permission;

	}

	public void SetPermission(int permission) {
		_Permission = permission;
	}

	public String getUserName() {
		return _UserName;

	}

	public void SetUserName(String username) {
		_UserName = username;
	}

	public String getUserID() {
		return _ID;

	}

	public void SetUserID(String id) {
		_ID = id;
	}

	public String getUserSsdw() {
		return _ssdw;

	}

	public void SetUserSsdw(String ssdw) {
		_ssdw = ssdw;
	}

	public String getName() {
		return _Name;

	}

	public void SetName(String name) {
		_Name = name;
	}

	public String getzqjlid() {
		return _zqjlid;

	}

	public void Setzqjlid(String id) {
		_zqjlid = id;
	}

	public static LoginUser getCurrentLoginUser() {
		return BaseApplication.instent.getUserInfo();
		// return _CurrentUser;
	}

	public static void SetCurrentLoginUser(LoginUser user) {
		_CurrentUser = user;
		BaseApplication.instent.setUserInfo(user);
	}
}
