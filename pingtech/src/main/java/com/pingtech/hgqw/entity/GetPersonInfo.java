package com.pingtech.hgqw.entity;

import java.io.Serializable;

public class GetPersonInfo implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 5144646037988864879L;

	/**
	 * 身份证号
	 */
	private String sfzh;
	/**
	 * 公安库数据结果
	 */
	private String gaResult;

	/**
	 * 姓名
	 */
	private String xm;

	/**
	 * 性别
	 */
	private String xb;

	/**
	 * 出生日期
	 */
	private String csrq;

	/**
	 * 国籍
	 */
	private String gj;

	/**
	 * 民族
	 */
	private String mz;

	/**
	 * 职业
	 */
	private String zy;

	/**
	 * 证件种类
	 */
	private String zjzl;

	/**
	 * 照片
	 */
	private String zp;

	/**
	 * 是否返回照片
	 */
	private boolean hasPhoto;

	/**
	 * 碰撞信息概要
	 */
	private String pzxx;

	/**
	 * 在逃人员
	 */
	private boolean ztry;

	/**
	 * 刑满释放
	 */
	private boolean xmsf;

	/**
	 * 劳教解教
	 */
	private boolean ljjj;

	/**
	 * 涉毒人员
	 */
	private boolean sdry;

	/**
	 * 违法犯罪
	 */
	private boolean wffz;

	public String getGaResult() {
		return gaResult;
	}

	public void setGaResult(String gaResult) {
		this.gaResult = gaResult;
	}

	public String getSfzh() {
		return sfzh;
	}

	public void setSfzh(String sfzh) {
		this.sfzh = sfzh;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getXb() {
		return xb;
	}

	public void setXb(String xb) {
		this.xb = xb;
	}

	public String getCsrq() {
		return csrq;
	}

	public void setCsrq(String csrq) {
		this.csrq = csrq;
	}

	public String getGj() {
		return gj;
	}

	public void setGj(String gj) {
		this.gj = gj;
	}

	public String getMz() {
		return mz;
	}

	public void setMz(String mz) {
		this.mz = mz;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getZjzl() {
		return zjzl;
	}

	public void setZjzl(String zjzl) {
		this.zjzl = zjzl;
	}

	public String getZp() {
		return zp;
	}

	public void setZp(String zp) {
		this.zp = zp;
	}

	public boolean isHasPhoto() {
		return hasPhoto;
	}

	public void setHasPhoto(boolean hasPhoto) {
		this.hasPhoto = hasPhoto;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPzxx() {
		return pzxx;
	}

	public void setPzxx(String pzxx) {
		this.pzxx = pzxx;
	}

	public boolean isZtry() {
		return ztry;
	}

	public void setZtry(boolean ztry) {
		this.ztry = ztry;
	}

	public boolean isXmsf() {
		return xmsf;
	}

	public void setXmsf(boolean xmsf) {
		this.xmsf = xmsf;
	}

	public boolean isLjjj() {
		return ljjj;
	}

	public void setLjjj(boolean ljjj) {
		this.ljjj = ljjj;
	}

	public boolean isSdry() {
		return sdry;
	}

	public void setSdry(boolean sdry) {
		this.sdry = sdry;
	}

	public boolean isWffz() {
		return wffz;
	}

	public void setWffz(boolean wffz) {
		this.wffz = wffz;
	}

}
