package com.pingtech.hgqw.module.offline.mtdm.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Mtdm")
public class Mtdm {

	// columns START
	/**
	 * id
	 */
	@DatabaseField(id =true, unique=true ,useGetSet = true, columnName = "id")
	private String id;

	/**
	 * 码头代码
	 */
	@DatabaseField(useGetSet = true, columnName = "mtdm")
	private String mtdm; //

	/**
	 * 码头名称
	 */
	@DatabaseField(useGetSet = true, columnName = "mtmc")
	private String mtmc; //

	/**
	 * 使用标记0：禁用、1：可用
	 */
	@DatabaseField(useGetSet = true, columnName = "sybj")
	private String sybj;

	/**
	 * 英文名称
	 */
	@DatabaseField(useGetSet = true, columnName = "ywmc")
	private String ywmc;

	/**
	 * 行政区划
	 */
	@DatabaseField(useGetSet = true, columnName = "xzqh")
	private String xzqh;

	/**
	 * 航线情况
	 */
	@DatabaseField(useGetSet = true, columnName = "hxqk")
	private String hxqk;

	/**
	 * 面积
	 */
	@DatabaseField(useGetSet = true, columnName = "mj")
	private String mj;

	/**
	 * 码头投入使用年份
	 */
	@DatabaseField(useGetSet = true, columnName = "mttrsynf")
	private String mttrsynf;

	/**
	 * 泊位数量
	 */
	@DatabaseField(useGetSet = true, columnName = "bwsl")
	private String bwsl;

	/**
	 * 开放泊位数
	 */
	@DatabaseField(useGetSet = true, columnName = "kfbes")
	private String kfbes;

	/**
	 * 码头总岸线
	 */
	@DatabaseField(useGetSet = true, columnName = "mtzax")
	private String mtzax;

	/**
	 * 年均停靠国际航行船舶
	 */
	@DatabaseField(useGetSet = true, columnName = "njtkgjhxcb")
	private String njtkgjhxcb;

	/**
	 * 装卸货种
	 */
	@DatabaseField(useGetSet = true, columnName = "xhhz")
	private String xhhz;

	/**
	 * 年吞吐量
	 */
	@DatabaseField(useGetSet = true, columnName = "nttl")
	private String nttl;

	/**
	 * 码头负责人
	 */
	@DatabaseField(useGetSet = true, columnName = "mtfzr")
	private String mtfzr;

	/**
	 * 码头负责人联系方式
	 */
	@DatabaseField(useGetSet = true, columnName = "mtfzrlxfs")
	private String mtfzrlxfs;

	/**
	 * 码头规模
	 */
	@DatabaseField(useGetSet = true, columnName = "mtgm")
	private String mtgm;

	/**
	 * 码头归属公司
	 */
	@DatabaseField(useGetSet = true, columnName = "mtgsgs")
	private String mtgsgs;

	/**
	 * 归属公司地址
	 */
	@DatabaseField(useGetSet = true, columnName = "gsgsdz")
	private String gsgsdz;

	/**
	 * 电子标签号
	 */
	@DatabaseField(useGetSet = true, columnName = "dzbqh")
	private String dzbqh;

	/**
	 * 备注
	 */
	@DatabaseField(useGetSet = true, columnName = "bz")
	private String bz;

	/**
	 * 码头平面布置图
	 */
	@DatabaseField(useGetSet = true, columnName = "mtpmbzt")
	private String mtpmbzt;

	/**
	 * 码头图片
	 */
	@DatabaseField(useGetSet = true, columnName = "mttp")
	private String mttp;

	/**
	 * 定位时间
	 */
	@DatabaseField(useGetSet = true, columnName = "dwsj")
	private Date dwsj;

	/**
	 * 经纬度
	 */
	@DatabaseField(useGetSet = true, columnName = "jwd")
	private String jwd;

	/**
	 * 绑定状态1绑定 0未绑定
	 */
	@DatabaseField(useGetSet = true, columnName = "bdzt")
	private String bdzt;

	/**
	 * 当前码头安全等级
	 */
	@DatabaseField(useGetSet = true, columnName = "dqmtaqjb")
	private String dqmtaqjb;

	/**
	 * 码头类型
	 */
	@DatabaseField(useGetSet = true, columnName = "mtlx")
	private String mtlx;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMtdm() {
		return mtdm;
	}

	public void setMtdm(String mtdm) {
		this.mtdm = mtdm;
	}

	public String getMtmc() {
		return mtmc;
	}

	public void setMtmc(String mtmc) {
		this.mtmc = mtmc;
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

	public String getXzqh() {
		return xzqh;
	}

	public void setXzqh(String xzqh) {
		this.xzqh = xzqh;
	}

	public String getHxqk() {
		return hxqk;
	}

	public void setHxqk(String hxqk) {
		this.hxqk = hxqk;
	}

	public String getMj() {
		return mj;
	}

	public void setMj(String mj) {
		this.mj = mj;
	}

	public String getMttrsynf() {
		return mttrsynf;
	}

	public void setMttrsynf(String mttrsynf) {
		this.mttrsynf = mttrsynf;
	}

	public String getBwsl() {
		return bwsl;
	}

	public void setBwsl(String bwsl) {
		this.bwsl = bwsl;
	}

	public String getKfbes() {
		return kfbes;
	}

	public void setKfbes(String kfbes) {
		this.kfbes = kfbes;
	}

	public String getMtzax() {
		return mtzax;
	}

	public void setMtzax(String mtzax) {
		this.mtzax = mtzax;
	}

	public String getNjtkgjhxcb() {
		return njtkgjhxcb;
	}

	public void setNjtkgjhxcb(String njtkgjhxcb) {
		this.njtkgjhxcb = njtkgjhxcb;
	}

	public String getXhhz() {
		return xhhz;
	}

	public void setXhhz(String xhhz) {
		this.xhhz = xhhz;
	}

	public String getNttl() {
		return nttl;
	}

	public void setNttl(String nttl) {
		this.nttl = nttl;
	}

	public String getMtfzr() {
		return mtfzr;
	}

	public void setMtfzr(String mtfzr) {
		this.mtfzr = mtfzr;
	}

	public String getMtfzrlxfs() {
		return mtfzrlxfs;
	}

	public void setMtfzrlxfs(String mtfzrlxfs) {
		this.mtfzrlxfs = mtfzrlxfs;
	}

	public String getMtgm() {
		return mtgm;
	}

	public void setMtgm(String mtgm) {
		this.mtgm = mtgm;
	}

	public String getMtgsgs() {
		return mtgsgs;
	}

	public void setMtgsgs(String mtgsgs) {
		this.mtgsgs = mtgsgs;
	}

	public String getGsgsdz() {
		return gsgsdz;
	}

	public void setGsgsdz(String gsgsdz) {
		this.gsgsdz = gsgsdz;
	}

	public String getDzbqh() {
		return dzbqh;
	}

	public void setDzbqh(String dzbqh) {
		this.dzbqh = dzbqh;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getMtpmbzt() {
		return mtpmbzt;
	}

	public void setMtpmbzt(String mtpmbzt) {
		this.mtpmbzt = mtpmbzt;
	}

	public String getMttp() {
		return mttp;
	}

	public void setMttp(String mttp) {
		this.mttp = mttp;
	}

	public Date getDwsj() {
		return dwsj;
	}

	public void setDwsj(Date dwsj) {
		this.dwsj = dwsj;
	}

	public String getJwd() {
		return jwd;
	}

	public void setJwd(String jwd) {
		this.jwd = jwd;
	}

	public String getBdzt() {
		return bdzt;
	}

	public void setBdzt(String bdzt) {
		this.bdzt = bdzt;
	}

	public String getDqmtaqjb() {
		return dqmtaqjb;
	}

	public void setDqmtaqjb(String dqmtaqjb) {
		this.dqmtaqjb = dqmtaqjb;
	}

	public String getMtlx() {
		return mtlx;
	}

	public void setMtlx(String mtlx) {
		this.mtlx = mtlx;
	}

}
