package com.pingtech.hgqw.entity;

import java.io.Serializable;

public class Cyxx implements Serializable {

	private static final long serialVersionUID = 5144646037988864800L;

	/**
	 * 航次
	 */
	private String hc;

	/**
	 * 姓名
	 */
	private String xm;

	/**
	 * 职务
	 */
	private String zw;

	/**
	 * 证件号码
	 */
	private String zjhm;

	/**
	 * 海员ID
	 */
	private String hyid;

	/**
	 * 船员位置
	 */
	private String cywz;

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getZw() {
		return zw;
	}

	public void setZw(String zw) {
		this.zw = zw;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public String getHyid() {
		return hyid;
	}

	public void setHyid(String hyid) {
		this.hyid = hyid;
	}

	public String getCywz() {
		return cywz;
	}

	public void setCywz(String cywz) {
		this.cywz = cywz;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
