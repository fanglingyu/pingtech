package com.pingtech.hgqw.module.offline.sbxx.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @title Sbxx.java
 * @description 智能设备概要信息类
 * @author zhaotf
 * @company PingTech
 * @date 2013-10-18 下午5:28:19
 * @version V1.0
 * @Copyright(c)2013
 */

@DatabaseTable(tableName = "Sbxx")
public class Sbxx {
	@DatabaseField(id =true, generatedId = false,unique=true ,useGetSet = true, columnName = "id")
	private java.lang.String id;

	/** 设备编号 */
	@DatabaseField(useGetSet = true, columnName = "sbbh")
	private java.lang.String sbbh;

	/** 设备名称 */
	@DatabaseField(useGetSet = true, columnName = "sbmc")
	private java.lang.String sbmc;

	/** IP地址 */
	@DatabaseField(useGetSet = true, columnName = "ip")
	private java.lang.String ip;

	/** 设备类型*/
	@DatabaseField(useGetSet = true, columnName = "lx")
	private java.lang.String lx;

	/** 绑定状态，1 绑定，0 未绑定 */
	@DatabaseField(useGetSet = true, columnName = "bdzt")
	private java.lang.String bdzt;

	/** 操作人*/
	@DatabaseField(useGetSet = true, columnName = "czr")
	private java.lang.String czr;

	/** 操作时间 */
	@DatabaseField(useGetSet = true, columnName = "czsj")
	private java.lang.String czsj;

	/** 备注 */
	@DatabaseField(useGetSet = true, columnName = "bz")
	private java.lang.String bz;

	/** 使用单位*/
	@DatabaseField(useGetSet = true, columnName = "sydw")
	private java.lang.String sydw;

	/** 验证方式,1普通验放 ，0增强验放 */
	@DatabaseField(useGetSet = true, columnName = "yzfs")
	private java.lang.String yzfs;

	/** 监控对象类型,1 船舶 ，0 卡口 */
	@DatabaseField(useGetSet = true, columnName = "jkdxlx")
	private java.lang.String jkdxlx;

	/** 绑定对象*/
	@DatabaseField(useGetSet = true, columnName = "bddx")
	private java.lang.Long bddx;

	/** 使用操作人 */
	@DatabaseField(useGetSet = true, columnName = "syczr")
	private java.lang.String syczr;

	/** 使用操作时间 */
	@DatabaseField(useGetSet = true, columnName = "syczsj")
	private java.lang.String syczsj;

	/** 指令类型（为船舶选择特殊选项） */
	@DatabaseField(useGetSet = true, columnName = "zllx")
	private java.lang.Long zllx;

	/** 设备经纬度(首页定位用) */
	@DatabaseField(useGetSet = true, columnName = "jwd")
	private java.lang.String jwd;

	/** 通行方向 进卡口为0  出卡口为1 */
	@DatabaseField(useGetSet = true, columnName = "txfx")
	private java.lang.String txfx;

	/** 关联摄像头 */
	@DatabaseField(useGetSet = true, columnName = "glsxt")
	private java.lang.String glsxt;

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

	public java.lang.String getSbmc() {
		return sbmc;
	}

	public void setSbmc(java.lang.String sbmc) {
		this.sbmc = sbmc;
	}

	public java.lang.String getIp() {
		return ip;
	}

	public void setIp(java.lang.String ip) {
		this.ip = ip;
	}

	public java.lang.String getLx() {
		return lx;
	}

	public void setLx(java.lang.String lx) {
		this.lx = lx;
	}

	public java.lang.String getBdzt() {
		return bdzt;
	}

	public void setBdzt(java.lang.String bdzt) {
		this.bdzt = bdzt;
	}

	public java.lang.String getCzr() {
		return czr;
	}

	public void setCzr(java.lang.String czr) {
		this.czr = czr;
	}

	public java.lang.String getCzsj() {
		return czsj;
	}

	public void setCzsj(java.lang.String czsj) {
		this.czsj = czsj;
	}

	public java.lang.String getBz() {
		return bz;
	}

	public void setBz(java.lang.String bz) {
		this.bz = bz;
	}

	public java.lang.String getSydw() {
		return sydw;
	}

	public void setSydw(java.lang.String sydw) {
		this.sydw = sydw;
	}

	public java.lang.String getYzfs() {
		return yzfs;
	}

	public void setYzfs(java.lang.String yzfs) {
		this.yzfs = yzfs;
	}

	public java.lang.String getJkdxlx() {
		return jkdxlx;
	}

	public void setJkdxlx(java.lang.String jkdxlx) {
		this.jkdxlx = jkdxlx;
	}

	public java.lang.Long getBddx() {
		return bddx;
	}

	public void setBddx(java.lang.Long bddx) {
		this.bddx = bddx;
	}

	public java.lang.String getSyczr() {
		return syczr;
	}

	public void setSyczr(java.lang.String syczr) {
		this.syczr = syczr;
	}

	public java.lang.String getSyczsj() {
		return syczsj;
	}

	public void setSyczsj(java.lang.String syczsj) {
		this.syczsj = syczsj;
	}

	public java.lang.Long getZllx() {
		return zllx;
	}

	public void setZllx(java.lang.Long zllx) {
		this.zllx = zllx;
	}

	public java.lang.String getJwd() {
		return jwd;
	}

	public void setJwd(java.lang.String jwd) {
		this.jwd = jwd;
	}

	public java.lang.String getTxfx() {
		return txfx;
	}

	public void setTxfx(java.lang.String txfx) {
		this.txfx = txfx;
	}

	public java.lang.String getGlsxt() {
		return glsxt;
	}

	public void setGlsxt(java.lang.String glsxt) {
		this.glsxt = glsxt;
	}

	

}
