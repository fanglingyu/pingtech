package com.pingtech.hgqw.module.offline.bwdm.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Bwdm")
public class Bwdm {
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "id")
	private String id;
	// columns START

	/**
	 * 泊位代码
	 */
	@DatabaseField(useGetSet = true, columnName = "bwdm")
	private String bwdm;

	/**
	 * 泊位名称
	 */
	@DatabaseField(useGetSet = true, columnName = "bwmc")
	private String bwmc;

	/**
	 * 使用标记 0：禁用，1：可用
	 */
	@DatabaseField(useGetSet = true, columnName = "sybj")
	private String sybj;

	/**
	 * 英文名称0
	 */
	@DatabaseField(useGetSet = true, columnName = "ywmc")
	private String ywmc;

	/**
	 * 码头代码
	 */
	@DatabaseField(useGetSet = true, columnName = "mtdm")
	private String mtdm;

	/**
	 * 长度
	 */
	@DatabaseField(useGetSet = true, columnName = "cd")
	private Double cd;

	/**
	 * 水深
	 */
	@DatabaseField(useGetSet = true, columnName = "ss")
	private Double ss;

	/**
	 * 装卸货种
	 */
	@DatabaseField(useGetSet = true, columnName = "zxhz")
	private String zxhz;

	/**
	 * 经纬度
	 */
	@DatabaseField(useGetSet = true, columnName = "jwd")
	private String jwd;
	/**
	 * 电子标签号
	 */

	@DatabaseField(useGetSet = true, columnName = "dzbqh")
	private String dzbqh;

	/**
	 * 绑定状态
	 */
	@DatabaseField(useGetSet = true, columnName = "bdzt")
	private String bdzt;

	/**
	 * 备注
	 */
	@DatabaseField(useGetSet = true, columnName = "bz")
	private String bz;

	/**
	 * 码头名称
	 */
	@DatabaseField(useGetSet = true, columnName = "mtmc")
	private String mtmc;

	/**
	 * 开放时间
	 */
	@DatabaseField(useGetSet = true, columnName = "kfsj")
	private String kfsj;

	/**
	 * 停泊能力
	 */
	@DatabaseField(useGetSet = true, columnName = "tbnl")
	private Double tbnl;

	/**
	 * 定位时间
	 */
	@DatabaseField(useGetSet = true, columnName = "dwsj")
	private Date dwsj;

	/**
	 * 当前泊位安全级别
	 */
	@DatabaseField(useGetSet = true, columnName = "dqbwaqjb")
	private String dqbwaqjb;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBwdm() {
		return bwdm;
	}

	public void setBwdm(String bwdm) {
		this.bwdm = bwdm;
	}

	public String getBwmc() {
		return bwmc;
	}

	public void setBwmc(String bwmc) {
		this.bwmc = bwmc;
	}

	public String getSybj() {
		return sybj;
	}

	public void setSybj(String sybj) {
		this.sybj = sybj;
	}

	public String getYwmc() {
		return ywmc;
	}

	public void setYwmc(String ywmc) {
		this.ywmc = ywmc;
	}

	public String getMtdm() {
		return mtdm;
	}

	public void setMtdm(String mtdm) {
		this.mtdm = mtdm;
	}

	public Double getCd() {
		return cd;
	}

	public void setCd(Double cd) {
		this.cd = cd;
	}

	public Double getSs() {
		return ss;
	}

	public void setSs(Double ss) {
		this.ss = ss;
	}

	public String getZxhz() {
		return zxhz;
	}

	public void setZxhz(String zxhz) {
		this.zxhz = zxhz;
	}

	public String getJwd() {
		return jwd;
	}

	public void setJwd(String jwd) {
		this.jwd = jwd;
	}

	public String getDzbqh() {
		return dzbqh;
	}

	public void setDzbqh(String dzbqh) {
		this.dzbqh = dzbqh;
	}

	public String getBdzt() {
		return bdzt;
	}

	public void setBdzt(String bdzt) {
		this.bdzt = bdzt;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getMtmc() {
		return mtmc;
	}

	public void setMtmc(String mtmc) {
		this.mtmc = mtmc;
	}

	public String getKfsj() {
		return kfsj;
	}

	public void setKfsj(String kfsj) {
		this.kfsj = kfsj;
	}

	public Double getTbnl() {
		return tbnl;
	}

	public void setTbnl(Double tbnl) {
		this.tbnl = tbnl;
	}

	public Date getDwsj() {
		return dwsj;
	}

	public void setDwsj(Date dwsj) {
		this.dwsj = dwsj;
	}

	public String getDqbwaqjb() {
		return dqbwaqjb;
	}

	public void setDqbwaqjb(String dqbwaqjb) {
		this.dqbwaqjb = dqbwaqjb;
	}

}
