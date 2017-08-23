package com.pingtech.hgqw.module.offline.txjl.entity;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * 
 * 类描述：
 * 
 * <p>
 * Title: 海江港边检勤务综合管理系统-TxjlKkNow.java
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
 * @date 2013-10-20 上午11:23:07
 */
@DatabaseTable(tableName = "TxjlKkNow")
public class TxjlKk implements Serializable {
	private static final long serialVersionUID = 11455L;
	/** ID */
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "id")
	private String id;

	/** 卡口通行记录ID */
	@DatabaseField(useGetSet = true, columnName = "kktxjlid")
	private String kktxjlid;

	/** 证件类型 */
	@DatabaseField(useGetSet = true, columnName = "zjlx")
	private String zjlx;

	/** 证件号码 */
	@DatabaseField(useGetSet = true, columnName = "zjhm")
	private String zjhm;

	/** 人员国籍 */
	@DatabaseField(useGetSet = true, columnName = "rygj")
	private String rygj;

	/** 所属单位 */
	@DatabaseField(useGetSet = true, columnName = "ssdw")
	private String ssdw;

	/** 通行时间 */
	@DatabaseField(useGetSet = true, columnName = "txsj")
	private Date txsj;

	/** 执勤人员 */
	@DatabaseField(useGetSet = true, columnName = "zqry")
	private String zqry;

	/** 验放方式 */
	@DatabaseField(useGetSet = true, columnName = "yffs")
	private String yffs;

	/** 通行对象 */
	@DatabaseField(useGetSet = true, columnName = "txdx")
	private String txdx;

	/** 通行方向 */
	@DatabaseField(useGetSet = true, columnName = "txfx")
	private String txfx;

	/** ICkey */
	@DatabaseField(useGetSet = true, columnName = "ickey")
	private String ickey;

	/** 漏打方向标识 */
	@DatabaseField(useGetSet = true, columnName = "ldfxbs")
	private String ldfxbs;

	/** 对象类型 */
	@DatabaseField(useGetSet = true, columnName = "dxlx")
	private String dxlx;

	/** 通行卡口id */
	@DatabaseField(useGetSet = true, columnName = "txkkid")
	private String txkkid;

	/** 人员性别 */
	@DatabaseField(useGetSet = true, columnName = "ryxb")
	private String ryxb;

	/** 职务 */
	@DatabaseField(useGetSet = true, columnName = "zw")
	private String zw;

	/** 口岸代码 */
	@DatabaseField(useGetSet = true, columnName = "kadm")
	private String kadm;

	/** 单位代码 */
	@DatabaseField(useGetSet = true, columnName = "dwdm")
	private String dwdm;

	/** 证件类别代码 */
	@DatabaseField(useGetSet = true, columnName = "zjlbdm")
	private String zjlbdm;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKktxjlid() {
		return kktxjlid;
	}

	public void setKktxjlid(String kktxjlid) {
		this.kktxjlid = kktxjlid;
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

	public String getZqry() {
		return zqry;
	}

	public void setZqry(String zqry) {
		this.zqry = zqry;
	}

	public String getYffs() {
		return yffs;
	}

	public void setYffs(String yffs) {
		this.yffs = yffs;
	}

	public String getTxdx() {
		return txdx;
	}

	public void setTxdx(String txdx) {
		this.txdx = txdx;
	}

	public String getTxfx() {
		return txfx;
	}

	public void setTxfx(String txfx) {
		this.txfx = txfx;
	}

	public String getIckey() {
		return ickey;
	}

	public void setIckey(String ickey) {
		this.ickey = ickey;
	}

	public String getLdfxbs() {
		return ldfxbs;
	}

	public void setLdfxbs(String ldfxbs) {
		this.ldfxbs = ldfxbs;
	}

	public String getDxlx() {
		return dxlx;
	}

	public void setDxlx(String dxlx) {
		this.dxlx = dxlx;
	}

	public String getTxkkid() {
		return txkkid;
	}

	public void setTxkkid(String txkkid) {
		this.txkkid = txkkid;
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

	public String getZjlbdm() {
		return zjlbdm;
	}

	public void setZjlbdm(String zjlbdm) {
		this.zjlbdm = zjlbdm;
	}
}
