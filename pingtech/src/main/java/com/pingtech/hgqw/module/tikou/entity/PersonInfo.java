package com.pingtech.hgqw.module.tikou.entity;

import java.io.Serializable;

/**
 * 刷卡返回人员字段
 * 
 * @author lougw
 * 
 */
public class PersonInfo implements Serializable {
	private static final long serialVersionUID = -5484876548593167417L;

	private String zjhm;

	private String name = "";

	private String sex;

	private String cardtype;

	private boolean isClzj;

	private String birthday;

	private String unit;

	private String office;

	private String country;

	private byte[] photo;

	private String ryid;

	private boolean hasCardInfo;

	private int successFlag;

	private boolean result;

	private String info = "";

	private String hgzl;

	private String sxcfx;

	private String txjlid;

	private String dkjlid;

	private String sfdk;

	private String fx;

	private String yxq;

	private String fw;

	private String dkzjlx;

	private String dkssdw;

	private String dkcbmc;

	private String dkcgj;

	private String dkzzdw;

	private String dkml;

	private String dkyt;

	private String dkdkfw;

	/** 报警提示信息 */
	private String bjtsxx;

	/** 碰撞目标来源 */
	private String pzmbly;

	public String getFw() {
		return fw;
	}

	public void setFw(String fw) {
		this.fw = fw;
	}

	public boolean isClzj() {
		return isClzj;
	}

	public void setClzj(boolean isClzj) {
		this.isClzj = isClzj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public String getCardtype() {
		return cardtype;
	}

	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}

	public String getRyid() {
		return ryid;
	}

	public void setRyid(String ryid) {
		this.ryid = ryid;
	}

	public boolean isHasCardInfo() {
		return hasCardInfo;
	}

	public void setHasCardInfo(boolean hasCardInfo) {
		this.hasCardInfo = hasCardInfo;
	}

	public int getSuccessFlag() {
		return successFlag;
	}

	public void setSuccessFlag(int successFlag) {
		this.successFlag = successFlag;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getHgzl() {
		return hgzl;
	}

	public void setHgzl(String hgzl) {
		this.hgzl = hgzl;
	}

	public String getSxcfx() {
		return sxcfx;
	}

	public void setSxcfx(String sxcfx) {
		this.sxcfx = sxcfx;
	}

	public String getTxjlid() {
		return txjlid;
	}

	public void setTxjlid(String txjlid) {
		this.txjlid = txjlid;
	}

	public String getSfdk() {
		return sfdk;
	}

	public void setSfdk(String sfdk) {
		this.sfdk = sfdk;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public String getDkjlid() {
		return dkjlid;
	}

	public void setDkjlid(String dkjlid) {
		this.dkjlid = dkjlid;
	}

	public String getDkzjlx() {
		return dkzjlx;
	}

	public void setDkzjlx(String dkzjlx) {
		this.dkzjlx = dkzjlx;
	}

	public String getDkssdw() {
		return dkssdw;
	}

	public void setDkssdw(String dkssdw) {
		this.dkssdw = dkssdw;
	}

	public String getDkcbmc() {
		return dkcbmc;
	}

	public void setDkcbmc(String dkcbmc) {
		this.dkcbmc = dkcbmc;
	}

	public String getDkcgj() {
		return dkcgj;
	}

	public void setDkcgj(String dkcgj) {
		this.dkcgj = dkcgj;
	}

	public String getDkzzdw() {
		return dkzzdw;
	}

	public void setDkzzdw(String dkzzdw) {
		this.dkzzdw = dkzzdw;
	}

	public String getDkml() {
		return dkml;
	}

	public void setDkml(String dkml) {
		this.dkml = dkml;
	}

	public String getDkyt() {
		return dkyt;
	}

	public void setDkyt(String dkyt) {
		this.dkyt = dkyt;
	}

	public String getDkdkfw() {
		return dkdkfw;
	}

	public void setDkdkfw(String dkdkfw) {
		this.dkdkfw = dkdkfw;
	}

	public String getYxq() {
		return yxq;
	}

	public void setYxq(String yxq) {
		this.yxq = yxq;
	}

	public String getBjtsxx() {
		return bjtsxx;
	}

	public void setBjtsxx(String bjtsxx) {
		this.bjtsxx = bjtsxx;
	}

	public String getPzmbly() {
		return pzmbly;
	}

	public void setPzmbly(String pzmbly) {
		this.pzmbly = pzmbly;
	}

}
