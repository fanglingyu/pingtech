package com.pingtech.hgqw.entity;

import java.util.ArrayList;
import java.util.List;

public class GetUserList {
	private String result;

	private String info;

	private List<UserInfo> userInfoList = new ArrayList<UserInfo>();

	public void addUserInfo(UserInfo userInfo) {
		userInfoList.add(userInfo);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<UserInfo> getUserinfoList() {
		return userInfoList;
	}

}
