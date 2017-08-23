package com.pingtech.hgqw.entity;

public class UserInfo {
	/**
	 * 用户名
	 */
	private String yhm;

	/**
	 * 用户id
	 */
	private String id;

	/**
	 * 姓名
	 */
	private String xm;

	/**
	 * 所属单位
	 */
	private String ssdw;

	/**
	 * 状态：1在线，0离线
	 */
	private String zt;

	/**
	 * 所属单位id
	 */
	private String ssdwid;

	/**
	 * 所属口岸
	 */
	private String sska;

	/**
	 * 口岸代码
	 */
	private String kadm;

	/**
	 * 执勤记录id
	 */
	private String zqjlid;
	
	/**PDA状态（1:PDA自己正常注销  0:平台判断异常注销）*/
	private String pdazt;

	public String getZt() {
		return zt;
	}

	public void setZt(String zt) {
		this.zt = zt;
	}

	public String getYhm() {
		return yhm;
	}

	public void setYhm(String yhm) {
		this.yhm = yhm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getSsdw() {
		return ssdw;
	}

	public void setSsdw(String ssdw) {
		this.ssdw = ssdw;
	}

	public String getSsdwid() {
		return ssdwid;
	}

	public void setSsdwid(String ssdwid) {
		this.ssdwid = ssdwid;
	}

	public String getSska() {
		return sska;
	}

	public void setSska(String sska) {
		this.sska = sska;
	}

	public String getKadm() {
		return kadm;
	}

	public void setKadm(String kadm) {
		this.kadm = kadm;
	}

	public String getZqjlid() {
		return zqjlid;
	}

	public void setZqjlid(String zqjlid) {
		this.zqjlid = zqjlid;
	}

	public String getPdazt() {
		return pdazt;
	}

	public void setPdazt(String pdazt) {
		this.pdazt = pdazt;
	}

	@Override
	public String toString() {
		return "[" + yhm + "," + id + "," + xm + "," + ssdw + "," + ssdwid + "," + sska + "," + kadm + "," + zqjlid + "]";
	}

}
