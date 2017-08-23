package com.pingtech.hgqw.module.offline.fwxcb.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
 
/**
 * 
 *
 * 类描述：服务性船舶
 *
 * <p> Title: 江海港边检勤务综合管理系统-Fwxcb.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  赵琳 
 * @version 1.0
 * @date  2013-10-15 下午3:24:02
 */
@DatabaseTable(tableName = "Fwxcb")
public class Fwxcb {
	
	private static final long serialVersionUID = 5454155825314635342L;  

	//columns START
	
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "id")
	private java.lang.String id;//id
	
	@DatabaseField(useGetSet = true, columnName = "cbmc")
	private java.lang.String cbmc;//	'船舶名称';
	
	@DatabaseField(useGetSet = true, columnName = "dbwz")
	private java.lang.String dbwz;//	'停泊位置';
	
	@DatabaseField(useGetSet = true, columnName = "cz")
	private java.lang.String cz;//	'船主姓名';
	
	@DatabaseField(useGetSet = true, columnName = "cbzdw")
	private java.lang.String cbzdw;//	'船舶总吨位';
	
	@DatabaseField(useGetSet = true, columnName = "xs")
	private Float xs;//	'型深';
	
	@DatabaseField(useGetSet = true, columnName = "ctcl")
	private java.lang.String ctcl;//	'船体材料';
	
	@DatabaseField(useGetSet = true, columnName = "xk")
	private Float xk;//	'型宽';
	
	@DatabaseField(useGetSet = true, columnName = "cbcjg")
	private java.lang.String cbcjg;//	'船舶船籍港';
	
	@DatabaseField(useGetSet = true, columnName = "ml")
	private java.lang.String ml;//	'马力';
	
	@DatabaseField(useGetSet = true, columnName = "cbzyyt")
	private java.lang.String cbzyyt;//	'船舶主要用途';
	
	@DatabaseField(useGetSet = true, columnName = "sybj")
	private java.lang.String sybj;//	'使用标记 0：禁用，1：可用';
	
	@DatabaseField(useGetSet = true, columnName = "bz")
	private java.lang.String bz;//	'备注';
	
	@DatabaseField(useGetSet = true, columnName = "swfwdwmc")
	private java.lang.String swfwdwmc;//使用单位名称(涉外服务单位名称)
	
	@DatabaseField(useGetSet = true, columnName = "swfwdwid")
	private java.lang.String swfwdwid;//使用单位ID(涉外服务单位id)
	@DatabaseField(useGetSet = true, columnName = "cjsj")
	private java.lang.String cjsj;//创建时间
	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getCbmc() {
		return cbmc;
	}

	public void setCbmc(java.lang.String cbmc) {
		this.cbmc = cbmc;
	}

	public java.lang.String getDbwz() {
		return dbwz;
	}

	public void setDbwz(java.lang.String dbwz) {
		this.dbwz = dbwz;
	}

	public java.lang.String getCz() {
		return cz;
	}

	public void setCz(java.lang.String cz) {
		this.cz = cz;
	}

	public java.lang.String getCbzdw() {
		return cbzdw;
	}

	public void setCbzdw(java.lang.String cbzdw) {
		this.cbzdw = cbzdw;
	}

	public Float getXs() {
		return xs;
	}

	public void setXs(Float xs) {
		this.xs = xs;
	}

	public java.lang.String getCtcl() {
		return ctcl;
	}

	public void setCtcl(java.lang.String ctcl) {
		this.ctcl = ctcl;
	}

	public Float getXk() {
		return xk;
	}

	public void setXk(Float xk) {
		this.xk = xk;
	}

	public java.lang.String getCbcjg() {
		return cbcjg;
	}

	public void setCbcjg(java.lang.String cbcjg) {
		this.cbcjg = cbcjg;
	}

	public java.lang.String getMl() {
		return ml;
	}

	public void setMl(java.lang.String ml) {
		this.ml = ml;
	}

	public java.lang.String getCbzyyt() {
		return cbzyyt;
	}

	public void setCbzyyt(java.lang.String cbzyyt) {
		this.cbzyyt = cbzyyt;
	}

	public java.lang.String getSybj() {
		return sybj;
	}

	public void setSybj(java.lang.String sybj) {
		this.sybj = sybj;
	}

	public java.lang.String getBz() {
		return bz;
	}

	public void setBz(java.lang.String bz) {
		this.bz = bz;
	}

	public java.lang.String getSwfwdwmc() {
		return swfwdwmc;
	}

	public void setSwfwdwmc(java.lang.String swfwdwmc) {
		this.swfwdwmc = swfwdwmc;
	}

	public java.lang.String getSwfwdwid() {
		return swfwdwid;
	}

	public void setSwfwdwid(java.lang.String swfwdwid) {
		this.swfwdwid = swfwdwid;
	}

	public java.lang.String getCjsj() {
		return cjsj;
	}

	public void setCjsj(java.lang.String cjsj) {
		this.cjsj = cjsj;
	}
	
	//columns END
	
	

}

