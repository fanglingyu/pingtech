package com.pingtech.hgqw.module.offline.sxtgl.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * @title Sxtgl.java
 * @description 摄像头概要信息模型
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:41:14
 * @version V1.0
 * @Copyright(c)2013
 */
@DatabaseTable(tableName = "Sxtgl")
public class Sxtgl {
	@DatabaseField(id =true, generatedId = false,unique=true ,useGetSet = true, columnName = "id")
	private java.lang.String id;

	/** 摄像机编号 */
	@DatabaseField(useGetSet = true, columnName = "sxjbh")
	private java.lang.String sxjbh;

	/** 摄像机名称 */
	@DatabaseField(useGetSet = true, columnName = "sxjmc")
	private java.lang.String sxjmc;
	
	/** 摄像机ip */
	@DatabaseField(useGetSet = true, columnName = "ip")
	private java.lang.String ip;
	
	/** 摄像机使用单位名称 */
	@DatabaseField(useGetSet = true, columnName = "sydwmc")
	private java.lang.String sydwmc;

	/** 摄像头类型   1室内固定，2室外固定，3室外云台，4室内云台，5内弦设备（双通道），6外弦设备（双通道） */
	@DatabaseField(useGetSet = true, columnName = "sxtlx")
	private java.lang.String sxtlx;

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getSxjbh() {
		return sxjbh;
	}

	public void setSxjbh(java.lang.String sxjbh) {
		this.sxjbh = sxjbh;
	}

	public java.lang.String getSxjmc() {
		return sxjmc;
	}

	public void setSxjmc(java.lang.String sxjmc) {
		this.sxjmc = sxjmc;
	}

	public java.lang.String getSxtlx() {
		return sxtlx;
	}

	public void setSxtlx(java.lang.String sxtlx) {
		this.sxtlx = sxtlx;
	}

	public java.lang.String getIp() {
		return ip;
	}

	public void setIp(java.lang.String ip) {
		this.ip = ip;
	}

	public java.lang.String getSydwmc() {
		return sydwmc;
	}

	public void setSydwmc(java.lang.String sydwmc) {
		this.sydwmc = sydwmc;
	}
}
