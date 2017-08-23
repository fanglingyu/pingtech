package com.pingtech.hgqw.module.offline.scsb.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @title Scsb.java
 * @description 手持设备概要信息模型
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:36:36
 * @version V1.0
 * @Copyright(c)2013
 */
@DatabaseTable(tableName = "Scsb")
public class Scsb {
	@DatabaseField(id =true, generatedId = false,unique=true ,useGetSet = true, columnName = "id")
	private java.lang.String id;

	/** 设备编号 */
	@DatabaseField(useGetSet = true, columnName = "sbbh")
	private java.lang.String sbbh;

	/** 设备型号 */
	@DatabaseField(useGetSet = true, columnName = "sbxh")
	private java.lang.String sbxh;

	/** IP地址 */
	@DatabaseField(useGetSet = true, columnName = "ip")
	private java.lang.String ip;

	/**使用类型	  0内部   1外部*/
	@DatabaseField(useGetSet = true, columnName = "sylx")
	private java.lang.String sylx;

	/** 状态  1正常，2损坏，3丢失，4维修 */
	@DatabaseField(useGetSet = true, columnName = "zt")
	private java.lang.String zt;

	/** 所属单位*/
	@DatabaseField(useGetSet = true, columnName = "ssdw")
	private java.lang.String ssdw;

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getSbbh() {
		return sbbh;
	}

	public void setSbbh(java.lang.String sbbh) {
		this.sbbh = sbbh;
	}

	public java.lang.String getSbxh() {
		return sbxh;
	}

	public void setSbxh(java.lang.String sbxh) {
		this.sbxh = sbxh;
	}

	public java.lang.String getIp() {
		return ip;
	}

	public void setIp(java.lang.String ip) {
		this.ip = ip;
	}


	public java.lang.String getSylx() {
		return sylx;
	}

	public void setSylx(java.lang.String sylx) {
		this.sylx = sylx;
	}

	public java.lang.String getZt() {
		return zt;
	}

	public void setZt(java.lang.String zt) {
		this.zt = zt;
	}

	public java.lang.String getSsdw() {
		return ssdw;
	}

	public void setSsdw(java.lang.String ssdw) {
		this.ssdw = ssdw;
	}
}
