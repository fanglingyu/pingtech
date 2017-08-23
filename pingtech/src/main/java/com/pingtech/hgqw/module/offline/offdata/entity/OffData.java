package com.pingtech.hgqw.module.offline.offdata.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "OffData")
public class OffData {
	/**
	 * id
	 */
	@DatabaseField( generatedId = true, unique= true, useGetSet = true, columnName = "id")
	private int id;
	/**
	 * 用户id
	 */
	@DatabaseField(useGetSet = true, columnName = "userid")
	private String userid;
	/**
	 * pdacode
	 */
	@DatabaseField(useGetSet = true, columnName = "pdacode")
	private String pdacode;

	/**
	 * 操作模块
	 */
	@DatabaseField(useGetSet = true, columnName = "czmk")
	private String czmk;
	/**
	 * 操作功能
	 */
	@DatabaseField(useGetSet = true, columnName = "czgn")
	private String czgn;
	/**
	 * '处理状态0 未处理，1已处理'
	 */
	@DatabaseField(useGetSet = true, columnName = "clstatus")
	private String clstatus;

	/**
	 * 创建时间
	 */
	@DatabaseField(useGetSet = true, columnName = "cjsj")
	private Date cjsj;
	/**
	 * 更新时间
	 */
	@DatabaseField(useGetSet = true, columnName = "gxsj")
	private Date gxsj;

	/**
	 * 业务数据，xml格式
	 */
	@DatabaseField(useGetSet = true, columnName = "xmldata")
	private String xmldata;
	/**
	 * 操作ID
	 */
	@DatabaseField(useGetSet = true, columnName = "czid")
	private String czid;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCzmk() {
		return czmk;
	}

	public void setCzmk(String czmk) {
		this.czmk = czmk;
	}

	public String getCzgn() {
		return czgn;
	}

	public void setCzgn(String czgn) {
		this.czgn = czgn;
	}

	public String getClstatus() {
		return clstatus;
	}

	public void setClstatus(String clstatus) {
		this.clstatus = clstatus;
	}

	public Date getCjsj() {
		return cjsj;
	}

	public void setCjsj(Date cjsj) {
		this.cjsj = cjsj;
	}

	public Date getGxsj() {
		return gxsj;
	}

	public void setGxsj(Date gxsj) {
		this.gxsj = gxsj;
	}

	public String getXmldata() {
		return xmldata;
	}

	public void setXmldata(String xmldata) {
		this.xmldata = xmldata;
	}

	public String getPdacode() {
		return pdacode;
	}

	public void setPdacode(String pdacode) {
		this.pdacode = pdacode;
	}

	public String getCzid() {
		return czid;
	}

	public void setCzid(String czid) {
		this.czid = czid;
	}

}
