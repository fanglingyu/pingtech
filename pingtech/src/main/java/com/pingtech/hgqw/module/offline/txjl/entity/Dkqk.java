package com.pingtech.hgqw.module.offline.txjl.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Dkqk")
public class Dkqk implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	/** 搭靠情况ID */
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "dkqkid")
	private java.lang.String dkqkid;

	/** 口岸船舶情况ID */
	@DatabaseField(useGetSet = true, columnName = "kacbqkid")
	private java.lang.String kacbqkid;

	/*** 证件发放ID */
	@DatabaseField(useGetSet = true, columnName = "zjffxxId")
	private String zjffxxId;

	/*** 搭靠方向 */
	@DatabaseField(useGetSet = true, columnName = "dkfx")
	private String dkfx;
	
	/** 航次 */
	@DatabaseField(useGetSet = true, columnName = "hc")
	private String hc;

	/** 船名 */
	private java.lang.String cm;

	/** 所属单位 */
	private java.lang.String ssdw;

	/** 搭靠时间 */
	private java.util.Date dksj;

	/** 离船时间 */
	private java.util.Date lcsj;
	
	/** 撘靠执勤人 */
	private java.lang.String dkzqr;
	
	/** 离船执勤人 */
	private java.lang.String lczqr;

	/** 创建者id */
	private java.lang.String cjzid;

	/** 创建者所属部门id */
	private java.lang.String cjzssbmid;

	/** 创建时间 */
	private java.lang.Long cjsj;

	/** 更新者id */
	private java.lang.String gxzid;

	/** 更新者所属部门id */
	private java.lang.String gxzssbmid;

	/** 更新时间 */
	private java.lang.Long gxsj;

	public java.lang.String getDkqkid() {
		return dkqkid;
	}

	public void setDkqkid(java.lang.String dkqkid) {
		this.dkqkid = dkqkid;
	}

	public java.lang.String getKacbqkid() {
		return kacbqkid;
	}

	public void setKacbqkid(java.lang.String kacbqkid) {
		this.kacbqkid = kacbqkid;
	}

	public String getZjffxxId() {
		return zjffxxId;
	}

	public void setZjffxxId(String zjffxxId) {
		this.zjffxxId = zjffxxId;
	}

	public String getDkfx() {
		return dkfx;
	}

	public void setDkfx(String dkfx) {
		this.dkfx = dkfx;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

}
