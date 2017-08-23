package com.pingtech.hgqw.module.offline.qyxx.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * 
 * 类描述：区域信息 的 实体表
 * 
 * <p>
 * Title: 江海港边检勤务综合管理系统-Qyxx.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author zhangzq
 * @version 1.0
 * @date 2012-11-23 下午06:30:19
 */

@DatabaseTable(tableName = "Qyxx")
public class Qyxx {

	// columns START
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "id")
	private java.lang.String id;// id
	@DatabaseField(useGetSet = true, columnName = "qybh")
	private java.lang.String qybh;// '区域编号';
	@DatabaseField(useGetSet = true, columnName = "qymc")
	private java.lang.String qymc;// '区域名称';
	@DatabaseField(useGetSet = true, columnName = "qyfw")
	private java.lang.String qyfw;// '区域范围';
	@DatabaseField(useGetSet = true, columnName = "qyfwdm")
	private java.lang.String qyfwdm;// '区域范围代码';
	@DatabaseField(useGetSet = true, columnName = "qyfwmtdm")
	private java.lang.String qyfwmtdm;// '区域范围-码头代码';多个用，分隔
	@DatabaseField(useGetSet = true, columnName = "qyfwbwdm")
	private java.lang.String qyfwbwdm;// '区域范围-泊位代码';多个用，分隔
	@DatabaseField(useGetSet = true, columnName = "qylx")
	private java.lang.String qylx;// '区域类型';
	@DatabaseField(useGetSet = true, columnName = "dzbqh")
	private java.lang.String dzbqh;// '电子标签号';
	@DatabaseField(useGetSet = true, columnName = "sybj")
	private java.lang.String sybj;// 使用标记 0：禁用，1：可用;
	@DatabaseField(useGetSet = true, columnName = "xxdz")
	private java.lang.String xxdz;// '详细地址';
	@DatabaseField(useGetSet = true, columnName = "bz")
	private java.lang.String bz;// '备注';
	@DatabaseField(useGetSet = true, columnName = "bdzt")
	private java.lang.String bdzt;// 绑定状态 1:邦定 0:未绑定
	@DatabaseField(useGetSet = true, columnName = "jwd")
	private java.lang.String jwd;// 经纬度
	@DatabaseField(useGetSet = true, columnName = "dwsj")
	private java.util.Date dwsj;// 定位时间

	// columns END

	public Qyxx() {
	}

	public Qyxx(java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getId() {
		return this.id;
	}

	public void setId(java.lang.String value) {
		this.id = value;
	}

	public String getIdValue() {
		return id;
	}

	public java.lang.String getBdzt() {
		return bdzt;
	}

	public void setBdzt(java.lang.String bdzt) {
		this.bdzt = bdzt;
	}

	public java.lang.String getQybh() {
		return this.qybh;
	}

	public void setQybh(java.lang.String value) {
		this.qybh = value;
	}

	public java.lang.String getQymc() {
		return this.qymc;
	}

	public void setQymc(java.lang.String value) {
		this.qymc = value;
	}

	public java.lang.String getQyfw() {
		return this.qyfw;
	}

	public void setQyfw(java.lang.String value) {
		this.qyfw = value;
	}

	public java.lang.String getQyfwdm() {
		return this.qyfwdm;
	}

	public void setQyfwdm(java.lang.String value) {
		this.qyfwdm = value;
	}

	public java.lang.String getQyfwmtdm() {
		return this.qyfwmtdm;
	}

	public void setQyfwmtdm(java.lang.String value) {
		this.qyfwmtdm = value;
	}

	public java.lang.String getQyfwbwdm() {
		return this.qyfwbwdm;
	}

	public void setQyfwbwdm(java.lang.String value) {
		this.qyfwbwdm = value;
	}

	public java.lang.String getQylx() {
		return this.qylx;
	}

	public void setQylx(java.lang.String value) {
		this.qylx = value;
	}

	public java.lang.String getDzbqh() {
		return this.dzbqh;
	}

	public void setDzbqh(java.lang.String value) {
		this.dzbqh = value;
	}

	public java.lang.String getSybj() {
		return this.sybj;
	}

	public void setSybj(java.lang.String value) {
		this.sybj = value;
	}

	public java.lang.String getXxdz() {
		return this.xxdz;
	}

	public void setXxdz(java.lang.String value) {
		this.xxdz = value;
	}

	public java.lang.String getBz() {
		return this.bz;
	}

	public void setBz(java.lang.String value) {
		this.bz = value;
	}

	public java.lang.String getJwd() {
		return jwd;
	}

	public void setJwd(java.lang.String jwd) {
		this.jwd = jwd;
	}

	public java.util.Date getDwsj() {
		return dwsj;
	}

	public void setDwsj(java.util.Date dwsj) {
		this.dwsj = dwsj;
	}

}
