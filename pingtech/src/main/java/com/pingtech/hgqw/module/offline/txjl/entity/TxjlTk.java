package com.pingtech.hgqw.module.offline.txjl.entity;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 类描述：
 * 
 * <p>
 * Title: 海江港边检勤务综合管理系统-TxjltkNow.java
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
 * @date 2013-10-20 上午11:29:26
 */
@DatabaseTable(tableName = "TxjlTkNow")
public class TxjlTk implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 4653078037987775505L;

	/**
	 * ID
	 */
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "id")
	private String id;

	/**
	 * 通行记录ID
	 */
	@DatabaseField(useGetSet = true, columnName = "tktxjlid")
	private String tktxjlid;

	/**
	 * 证件类型
	 */
	@DatabaseField(useGetSet = true, columnName = "zjlx")
	private String zjlx;

	/**
	 * 证件号码
	 */
	@DatabaseField(useGetSet = true, columnName = "zjhm")
	private String zjhm;

	/**
	 * 国籍;
	 */
	@DatabaseField(useGetSet = true, columnName = "rygj")
	private String rygj;

	/**
	 * 船舶中英文名
	 */
	@DatabaseField(useGetSet = true, columnName = "cbzywmc")
	private String cbzywmc;

	/**
	 * 所属单位
	 */
	@DatabaseField(useGetSet = true, columnName = "ssdw")
	private String ssdw;

	/**
	 * 通行时间
	 */
	@DatabaseField(useGetSet = true, columnName = "txsj")
	private Date txsj;

	/**
	 * 通行方向
	 */
	@DatabaseField(useGetSet = true, columnName = "txfx")
	private String txfx;

	/**
	 * 执勤人员
	 */
	@DatabaseField(useGetSet = true, columnName = "zqry")
	private String zqry;

	/**
	 * 通行人员姓名
	 */
	@DatabaseField(useGetSet = true, columnName = "txryxm")
	private String txryxm;

	/**
	 * 验放方式
	 */
	@DatabaseField(useGetSet = true, columnName = "yffs")
	private String yffs;

	/**
	 * 航次
	 */
	@DatabaseField(useGetSet = true, columnName = "hc")
	private String hc;

	/**
	 * 卡号
	 */
	@DatabaseField(useGetSet = true, columnName = "ickey")
	private String ickey;

	/**
	 * 所在码头
	 */
	@DatabaseField(useGetSet = true, columnName = "szmt")
	private String szmt;

	/**
	 * 所在泊位
	 */
	@DatabaseField(useGetSet = true, columnName = "szbw")
	private String szbw;

	/**
	 * 人员性别
	 */
	@DatabaseField(useGetSet = true, columnName = "ryxb")
	private String ryxb;

	/**
	 * 职务
	 */
	@DatabaseField(useGetSet = true, columnName = "zw")
	private String zw;

	/**
	 * 梯口位置
	 */
	@DatabaseField(useGetSet = true, columnName = "tkwz")
	private String tkwz;

	/**
	 * 登轮登陆类型
	 */
	@DatabaseField(useGetSet = true, columnName = "dldllx")
	private String dldllx;

	/**
	 * 证件类别代码
	 */
	@DatabaseField(useGetSet = true, columnName = "zjlbdm")
	private String zjlbdm;

	/**
	 * 口岸代码
	 */
	@DatabaseField(useGetSet = true, columnName = "kadm")
	private String kadm;

	/**
	 * 单位代码
	 */
	@DatabaseField(useGetSet = true, columnName = "dwdm")
	private String dwdm;

	/**
	 * 漏打方向表示
	 */
	@DatabaseField(useGetSet = true, columnName = "ldfxbs")
	private String ldfxbs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTktxjlid() {
		return tktxjlid;
	}

	public void setTktxjlid(String tktxjlid) {
		this.tktxjlid = tktxjlid;
	}

	public String getZjlx() {
		return zjlx;
	}

	public void setZjlx(String zjlx) {
		this.zjlx = zjlx;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public String getRygj() {
		return rygj;
	}

	public void setRygj(String rygj) {
		this.rygj = rygj;
	}

	public String getCbzywmc() {
		return cbzywmc;
	}

	public void setCbzywmc(String cbzywmc) {
		this.cbzywmc = cbzywmc;
	}

	public String getSsdw() {
		return ssdw;
	}

	public void setSsdw(String ssdw) {
		this.ssdw = ssdw;
	}

	public Date getTxsj() {
		return txsj;
	}

	public void setTxsj(Date txsj) {
		this.txsj = txsj;
	}

	public String getTxfx() {
		return txfx;
	}

	public void setTxfx(String txfx) {
		this.txfx = txfx;
	}

	public String getZqry() {
		return zqry;
	}

	public void setZqry(String zqry) {
		this.zqry = zqry;
	}

	public String getTxryxm() {
		return txryxm;
	}

	public void setTxryxm(String txryxm) {
		this.txryxm = txryxm;
	}

	public String getYffs() {
		return yffs;
	}

	public void setYffs(String yffs) {
		this.yffs = yffs;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

	public String getIckey() {
		return ickey;
	}

	public void setIckey(String ickey) {
		this.ickey = ickey;
	}

	public String getSzmt() {
		return szmt;
	}

	public void setSzmt(String szmt) {
		this.szmt = szmt;
	}

	public String getSzbw() {
		return szbw;
	}

	public void setSzbw(String szbw) {
		this.szbw = szbw;
	}

	public String getRyxb() {
		return ryxb;
	}

	public void setRyxb(String ryxb) {
		this.ryxb = ryxb;
	}

	public String getZw() {
		return zw;
	}

	public void setZw(String zw) {
		this.zw = zw;
	}

	public String getTkwz() {
		return tkwz;
	}

	public void setTkwz(String tkwz) {
		this.tkwz = tkwz;
	}

	public String getDldllx() {
		return dldllx;
	}

	public void setDldllx(String dldllx) {
		this.dldllx = dldllx;
	}

	public String getZjlbdm() {
		return zjlbdm;
	}

	public void setZjlbdm(String zjlbdm) {
		this.zjlbdm = zjlbdm;
	}

	public String getKadm() {
		return kadm;
	}

	public void setKadm(String kadm) {
		this.kadm = kadm;
	}

	public String getDwdm() {
		return dwdm;
	}

	public void setDwdm(String dwdm) {
		this.dwdm = dwdm;
	}

	public String getLdfxbs() {
		return ldfxbs;
	}

	public void setLdfxbs(String ldfxbs) {
		this.ldfxbs = ldfxbs;
	}


}
