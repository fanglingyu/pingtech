package com.pingtech.hgqw.module.cgcs.entity;


import java.io.Serializable;
/**
 * 
 *
 * 类描述：查岗查哨实体类
 *
 * <p> Title: 江海港边检勤务-移动管理系统-Cgcs.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  娄高伟 
 * @version 1.0
 * @date  2013-8-16 下午3:16:04
 */
public class Cgcs implements Serializable {
	/**
	 * 士兵卡号
	 */
	private String sbkid;
	/**
	 * 姓名
	 */
	private String xm;
	/**
	 * 所属单位
	 */
	private String ssdw;
	/**
	 * 职务
	 */
	private String zw;
	/**
	 * 执勤地点
	 */
	private String zqdd;
	/**
	 * 照片
	 */
	private byte[] icpic;
	/**
	 * 证件号码
	 */
	private String zjhm;
	/**
	 * 提示信息
	 */
	private String info;
	/**
	 * 解析结果
	 */
	private boolean result;

	public String getSbkid() {
		return sbkid;
	}

	public void setSbkid(String sbkid) {
		this.sbkid = sbkid;
	}

	public String getZw() {
		return zw;
	}

	public void setZw(String zw) {
		this.zw = zw;
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

	public String getZqdd() {
		return zqdd;
	}

	public void setZqdd(String zqdd) {
		this.zqdd = zqdd;
	}

	public byte[] getIcpic() {
		return icpic;
	}

	public void setIcpic(byte[] icpic) {
		this.icpic = icpic;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

}
