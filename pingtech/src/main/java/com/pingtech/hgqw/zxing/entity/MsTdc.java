package com.pingtech.hgqw.zxing.entity;

import java.io.Serializable;

/**
 * 
 * 
 * 类描述：梅沙二维码，刷卡登记扫描二维码
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-MsTdcInfo.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2014-1-8 上午10:52:51
 */
public class MsTdc implements Serializable {
	private static final long serialVersionUID = 1L;

	// 临时入境许可证 TIN HTAY AUNG^119840714^MMR^MA249039^14^WOO JUN^356201401100029
	// 临时入境许可证 姓名^ 出生日期^国籍^证件号码^证件种类^服务船舶^船舶航次号

	// 登轮证、搭靠证， 483562013102149^王伟^员工^1^19860627^国电常州发电^在港外轮^20140101^20140630
	// zjbh,xm,zw,xbdm,csrq,ssdw,sdcb,yxqq,yxqz
	// 按顺序对应 

	/** 证件号码 */
	private String zjhm;

	/** 国籍代码 */
	private String gjdm;

	/** 姓名 */
	private String xm;

	/** 职务 */
	private String zw;

	/** 证件类别代码 */
	private String zjlbdm;

	/** 性别代码 */
	private String xbdm;

	/** 出生日期 */
	private String csrq;

	/** 所属单位 */
	private String ssdw;

	/** 中文船舶名 */
	private String zwcbm;
	/** 服务船舶航次 */
	private String fwcbHc;

	/** 一次有效标志 ：1本航次有效，0默认值 */
	private String ycyxbz;

	/** 有效期起 */
	private String yxqq;

	/** 有效期至 */
	private String yxqz;

	/** 扫描类型 ，用于区分是什么证件 */
	private int scanType;

	/**
	 * 
	 * @方法名：getZjhm
	 * @功能说明：证件号码<br> 登轮许可证和搭靠外轮许可证公用
	 * @author zhaotf
	 * @date 2014-1-9 下午5:00:42
	 * @return
	 */
	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	/**
	 * 
	 * @方法名：getXm
	 * @功能说明：姓名 <br>
	 *          登轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午5:00:31
	 * @return
	 */
	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	/**
	 * 
	 * @方法名：getXbdm
	 * @功能说明：性别代码<br> 登轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午5:00:22
	 * @return
	 */
	public String getXbdm() {
		return xbdm;
	}

	public void setXbdm(String xbdm) {
		this.xbdm = xbdm;
	}

	/**
	 * 
	 * @方法名：getCsrq
	 * @功能说明：出生日期 <br>
	 *            登轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:59:42
	 * @return
	 */
	public String getCsrq() {
		return csrq;
	}

	public void setCsrq(String csrq) {
		this.csrq = csrq;
	}

	/**
	 * 
	 * @方法名：getZwcbm
	 * @功能说明：中文船舶名<br> 登轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:59:32
	 * @return
	 */
	public String getZwcbm() {
		return zwcbm;
	}

	public void setZwcbm(String zwcbm) {
		this.zwcbm = zwcbm;
	}

	/**
	 * 
	 * @方法名：getYcyxbz
	 * @功能说明：一次有效标志<br> 登轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:59:23
	 * @return
	 */
	public String getYcyxbz() {
		return ycyxbz;
	}

	public void setYcyxbz(String ycyxbz) {
		this.ycyxbz = ycyxbz;
	}

	/**
	 * 
	 * @方法名：getZw
	 * @功能说明：职务<br> 登轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:59:14
	 * @return
	 */
	public String getZw() {
		return zw;
	}

	public void setZw(String zw) {
		this.zw = zw;
	}

	/**
	 * 
	 * @方法名：getSsdw
	 * @功能说明：所属单位<br> 登轮许可证和搭靠外轮许可证共用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:59:04
	 * @return
	 */
	public String getSsdw() {
		return ssdw;
	}

	public void setSsdw(String ssdw) {
		this.ssdw = ssdw;
	}

	/**
	 * 
	 * @方法名：getYxqq
	 * @功能说明：有效期起<br> 登轮许可证和搭靠外轮许可证共用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:58:54
	 * @return
	 */
	public String getYxqq() {
		return yxqq;
	}

	public void setYxqq(String yxqq) {
		this.yxqq = yxqq;
	}

	/**
	 * 
	 * @方法名：getYxqz
	 * @功能说明：有效期至<br> 登轮许可证和搭靠外轮许可证共用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:58:39
	 * @return
	 */
	public String getYxqz() {
		return yxqz;
	}

	public void setYxqz(String yxqz) {
		this.yxqz = yxqz;
	}

	/**
	 * 
	 * @方法名：getScanType
	 * @功能说明：扫描证件标识
	 * @author zhaotf
	 * @date 2014-1-9 下午4:58:26
	 * @return
	 */
	public int getScanType() {
		return scanType;
	}

	public void setScanType(int scanType) {
		this.scanType = scanType;
	}

	// *****搭靠外轮许可证专用*****************************************
	/**
	 * 船舶名称
	 */
	private String cbmc;

	/**
	 * 用途
	 */
	private String yt;

	/**
	 * 吨位
	 */
	private String dw;

	/**
	 * 马力
	 */
	private String ml;

	/**
	 * 船籍港
	 */
	private String cjg;

	/**
	 * 
	 * @方法名：getCbmc
	 * @功能说明： 船舶名称<br>
	 *        搭靠外轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:58:09
	 * @return
	 */
	public String getCbmc() {
		return cbmc;
	}

	public void setCbmc(String cbmc) {
		this.cbmc = cbmc;
	}

	/**
	 * 
	 * @方法名：getYt
	 * @功能说明：用途<br> 搭靠外轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:57:57
	 * @return
	 */
	public String getYt() {
		return yt;
	}

	public void setYt(String yt) {
		this.yt = yt;
	}

	/**
	 * 
	 * @方法名：getDw
	 * @功能说明：吨位<br> 搭靠外轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:57:44
	 * @return
	 */
	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	/**
	 * 
	 * @方法名：getMl
	 * @功能说明：马力<br> 搭靠外轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:57:24
	 * @return
	 */
	public String getMl() {
		return ml;
	}

	public void setMl(String ml) {
		this.ml = ml;
	}

	/**
	 * 
	 * @方法名：getCjg
	 * @功能说明：得到船籍港<br> 搭靠外轮许可证专用
	 * @author zhaotf
	 * @date 2014-1-9 下午4:56:56
	 * @return
	 */
	public String getCjg() {
		return cjg;
	}

	public void setCjg(String cjg) {
		this.cjg = cjg;
	}

	public String getZjlbdm() {
		return zjlbdm;
	}

	public void setZjlbdm(String zjlbdm) {
		this.zjlbdm = zjlbdm;
	}

	public String getGjdm() {
		return gjdm;
	}

	public void setGjdm(String gjdm) {
		this.gjdm = gjdm;
	}

	public String getFwcbHc() {
		return fwcbHc;
	}

	public void setFwcbHc(String fwcbHc) {
		this.fwcbHc = fwcbHc;
	}

	@Override
	public String toString() {
		return "MsTdc [zjhm证件号码=" + zjhm + ", xm=" + xm + ", zw职务=" + zw + ", xbdm=" + xbdm + ", csrq=" + csrq + ", ssdw=" + ssdw + ", zwcbm中文船舶名="
				+ zwcbm + ", ycyxbz一次有效标志=" + ycyxbz + ", yxqq有效期起=" + yxqq + ", yxqz有效期至=" + yxqz + ", scanType=" + scanType + ", cbmc船舶名称=" + cbmc
				+ ", yt用途=" + yt + ", dw吨位=" + dw + ", ml马力=" + ml + ", cjg船籍港=" + cjg + "]";
	}

}
