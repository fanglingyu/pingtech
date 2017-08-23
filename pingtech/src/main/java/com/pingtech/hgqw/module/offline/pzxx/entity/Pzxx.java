package com.pingtech.hgqw.module.offline.pzxx.entity;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Pzxx")
public class Pzxx implements Serializable {
	private static final long serialVersionUID = -5582104210420577284L;

	// pzjlValueArr[0] = bjtsxx;
	// pzjlValueArr[1] = pzjlryId;
	// pzjlValueArr[2] = zjhm;
	// pzjlValueArr[3] = xm;
	// pzjlValueArr[4] = dxbs;
	// pzjlValueArr[5] = pzmbly;
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "zjhm")
	private String zjhm;

	/** 报警提示信息 */
	@DatabaseField(useGetSet = true, columnName = "bjtsxx")
	private String bjtsxx;

	/** 碰撞记录人员ID */
	@DatabaseField(useGetSet = true, columnName = "pzjlryId")
	private String pzjlryId;

	/** 姓名 */
	@DatabaseField(useGetSet = true, columnName = "xm")
	private String xm;

	/** 短信标识 */
	@DatabaseField(useGetSet = true, columnName = "dxbs")
	private String dxbs;

	/** 碰撞目标来源 */
	@DatabaseField(useGetSet = true, columnName = "pzmbly")
	private String pzmbly;


	public String getPzjlryId() {
		return pzjlryId;
	}

	public void setPzjlryId(String pzjlryId) {
		this.pzjlryId = pzjlryId;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getDxbs() {
		return dxbs;
	}

	public void setDxbs(String dxbs) {
		this.dxbs = dxbs;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
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
