package com.pingtech.hgqw.module.offline.cyxx.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "TBCyxx")
public class TBCyxx {
	@DatabaseField(id =true, generatedId = false,unique=true ,useGetSet = true, columnName = "hyid")
	private java.lang.String hyid;

	/** 口岸船舶情况ID */
	@DatabaseField(useGetSet = true, columnName = "kacbqkid")
	private java.lang.String kacbqkid;

	/** 姓名 */
	@DatabaseField(useGetSet = true, columnName = "xm")
	private java.lang.String xm;

	/** 性别 */
	@DatabaseField(useGetSet = true, columnName = "xb")
	private java.lang.String xb;

	/** 国籍 */
	@DatabaseField(useGetSet = true, columnName = "gj")
	private java.lang.String gj;

	/** 出生日期 */
	@DatabaseField(useGetSet = true, columnName = "csrq")
	private java.util.Date csrq;

	/** 职务 */
	@DatabaseField(useGetSet = true, columnName = "zw")
	private java.lang.String zw;

	/** 状态 */
	@DatabaseField(useGetSet = true, columnName = "zt")
	private java.lang.String zt;

	/** 证件类型 */
	@DatabaseField(useGetSet = true, columnName = "zjlx")
	private java.lang.String zjlx;

	/** 证件号码 */
	@DatabaseField(useGetSet = true, columnName = "zjhm")
	private java.lang.String zjhm;

	/** 是否申请登陆 */
	@DatabaseField(useGetSet = true, columnName = "sqdl")
	private java.lang.String sqdl;

	/** 创建者id */
	@DatabaseField(useGetSet = true, columnName = "cjzid")
	private java.lang.String cjzid;

	/** 创建者所属部门id */
	@DatabaseField(useGetSet = true, columnName = "cjzssbmid")
	private java.lang.String cjzssbmid;

	/** 创建时间 */
	@DatabaseField(useGetSet = true, columnName = "cjsj")
	private java.lang.Long cjsj;

	/** 更新者id */
	@DatabaseField(useGetSet = true, columnName = "gxzid")
	private java.lang.String gxzid;

	/** 更新者所属部门id */
	@DatabaseField(useGetSet = true, columnName = "gxzssbmid")
	private java.lang.String gxzssbmid;

	/** 更新时间 */
	@DatabaseField(useGetSet = true, columnName = "gxsj")
	private java.lang.Long gxsj;

	/** 停留期 */
	@DatabaseField(useGetSet = true, columnName = "tlq")
	private java.lang.String tlq;

	/** 签证 */
	@DatabaseField(useGetSet = true, columnName = "qz")
	private java.lang.String qz;

	/** 出入事由 */
	@DatabaseField(useGetSet = true, columnName = "crsy")
	private java.lang.String crsy;

	/** 自定代码 */
	@DatabaseField(useGetSet = true, columnName = "zddm")
	private java.lang.String zddm;

	/** 人员序号 */
	@DatabaseField(useGetSet = true, columnName = "ryxh")
	private java.lang.String ryxh;

	/** 签证号 */
	@DatabaseField(useGetSet = true, columnName = "qzh")
	private java.lang.String qzh;

	/** 前往国 */
	@DatabaseField(useGetSet = true, columnName = "qwg")
	private java.lang.String qwg;

	/** 第二证号 */
	@DatabaseField(useGetSet = true, columnName = "dezh")
	private java.lang.String dezh;

	/** 第二姓名 */
	@DatabaseField(useGetSet = true, columnName = "dexm")
	private java.lang.String dexm;

	/** 第二证类 */
	@DatabaseField(useGetSet = true, columnName = "dezl")
	private java.lang.String dezl;

	/** 第二生日 */
	@DatabaseField(useGetSet = true, columnName = "desr")
	private java.util.Date desr;

	/** 疑难字 */
	@DatabaseField(useGetSet = true, columnName = "ynz")
	private java.lang.String ynz;

	/** IC卡号 */
	@DatabaseField(useGetSet = true, columnName = "ic_key")
	private String ic_key;

	/** 人脸特征 */
	@DatabaseField(useGetSet = true, columnName = "faceData")
	private String faceData;

	/** 人脸图片 */
	@DatabaseField(useGetSet = true, columnName = "facePic")
	private String facePic;

	/** ID图片 */
	@DatabaseField(useGetSet = true, columnName = "idPic")
	private String idPic;
	/** 指纹特征 **/
	@DatabaseField(useGetSet = true, columnName = "fingerData")
	private String fingerData;

	/**  登/离船标志(0：在船1：离船、2：登船、3：在船（信息变更）)*/
	@DatabaseField(useGetSet = true, columnName = "lcbz")
	private java.lang.String lcbz;

	/** 检查分类 */
	@DatabaseField(useGetSet = true, columnName = "jcfl")
	private java.lang.String jcfl;

	/**
	 * 当前船舶的状态 0，有效 默认值 1是离岗
	 * 
	 */
	@DatabaseField(useGetSet = true, columnName = "cbzt")
	private java.lang.String cbzt;

	// 以下为非PO字段
	/** 船舶中文名 */
	@DatabaseField(useGetSet = true, columnName = "cbzwm")
	private String cbzwm;
	/* 航次 */
	@DatabaseField(useGetSet = true, columnName = "hc")
	private String hc;
	@DatabaseField(useGetSet = true, columnName = "cywz")
	private String cywz; // 船员位置 0：在船上的船员 ，1：在船下的船员
	public java.lang.String getHyid() {
		return hyid;
	}
	public void setHyid(java.lang.String hyid) {
		this.hyid = hyid;
	}
	public java.lang.String getKacbqkid() {
		return kacbqkid;
	}
	public void setKacbqkid(java.lang.String kacbqkid) {
		this.kacbqkid = kacbqkid;
	}
	public java.lang.String getXm() {
		return xm;
	}
	public void setXm(java.lang.String xm) {
		this.xm = xm;
	}
	public java.lang.String getXb() {
		return xb;
	}
	public void setXb(java.lang.String xb) {
		this.xb = xb;
	}
	public java.lang.String getGj() {
		return gj;
	}
	public void setGj(java.lang.String gj) {
		this.gj = gj;
	}
	public java.util.Date getCsrq() {
		return csrq;
	}
	public void setCsrq(java.util.Date csrq) {
		this.csrq = csrq;
	}
	public java.lang.String getZw() {
		return zw;
	}
	public void setZw(java.lang.String zw) {
		this.zw = zw;
	}
	public java.lang.String getZt() {
		return zt;
	}
	public void setZt(java.lang.String zt) {
		this.zt = zt;
	}
	public java.lang.String getZjlx() {
		return zjlx;
	}
	public void setZjlx(java.lang.String zjlx) {
		this.zjlx = zjlx;
	}
	public java.lang.String getZjhm() {
		return zjhm;
	}
	public void setZjhm(java.lang.String zjhm) {
		this.zjhm = zjhm;
	}
	public java.lang.String getSqdl() {
		return sqdl;
	}
	public void setSqdl(java.lang.String sqdl) {
		this.sqdl = sqdl;
	}
	public java.lang.String getCjzid() {
		return cjzid;
	}
	public void setCjzid(java.lang.String cjzid) {
		this.cjzid = cjzid;
	}
	public java.lang.String getCjzssbmid() {
		return cjzssbmid;
	}
	public void setCjzssbmid(java.lang.String cjzssbmid) {
		this.cjzssbmid = cjzssbmid;
	}
	public java.lang.Long getCjsj() {
		return cjsj;
	}
	public void setCjsj(java.lang.Long cjsj) {
		this.cjsj = cjsj;
	}
	public java.lang.String getGxzid() {
		return gxzid;
	}
	public void setGxzid(java.lang.String gxzid) {
		this.gxzid = gxzid;
	}
	public java.lang.String getGxzssbmid() {
		return gxzssbmid;
	}
	public void setGxzssbmid(java.lang.String gxzssbmid) {
		this.gxzssbmid = gxzssbmid;
	}
	public java.lang.Long getGxsj() {
		return gxsj;
	}
	public void setGxsj(java.lang.Long gxsj) {
		this.gxsj = gxsj;
	}
	public java.lang.String getTlq() {
		return tlq;
	}
	public void setTlq(java.lang.String tlq) {
		this.tlq = tlq;
	}
	public java.lang.String getQz() {
		return qz;
	}
	public void setQz(java.lang.String qz) {
		this.qz = qz;
	}
	public java.lang.String getCrsy() {
		return crsy;
	}
	public void setCrsy(java.lang.String crsy) {
		this.crsy = crsy;
	}
	public java.lang.String getZddm() {
		return zddm;
	}
	public void setZddm(java.lang.String zddm) {
		this.zddm = zddm;
	}
	public java.lang.String getRyxh() {
		return ryxh;
	}
	public void setRyxh(java.lang.String ryxh) {
		this.ryxh = ryxh;
	}
	public java.lang.String getQzh() {
		return qzh;
	}
	public void setQzh(java.lang.String qzh) {
		this.qzh = qzh;
	}
	public java.lang.String getQwg() {
		return qwg;
	}
	public void setQwg(java.lang.String qwg) {
		this.qwg = qwg;
	}
	public java.lang.String getDezh() {
		return dezh;
	}
	public void setDezh(java.lang.String dezh) {
		this.dezh = dezh;
	}
	public java.lang.String getDexm() {
		return dexm;
	}
	public void setDexm(java.lang.String dexm) {
		this.dexm = dexm;
	}
	public java.lang.String getDezl() {
		return dezl;
	}
	public void setDezl(java.lang.String dezl) {
		this.dezl = dezl;
	}
	public java.util.Date getDesr() {
		return desr;
	}
	public void setDesr(java.util.Date desr) {
		this.desr = desr;
	}
	public java.lang.String getYnz() {
		return ynz;
	}
	public void setYnz(java.lang.String ynz) {
		this.ynz = ynz;
	}
	public String getIc_key() {
		return ic_key;
	}
	public void setIc_key(String ic_key) {
		this.ic_key = ic_key;
	}
	public String getFaceData() {
		return faceData;
	}
	public void setFaceData(String faceData) {
		this.faceData = faceData;
	}
	public String getFacePic() {
		return facePic;
	}
	public void setFacePic(String facePic) {
		this.facePic = facePic;
	}
	public String getIdPic() {
		return idPic;
	}
	public void setIdPic(String idPic) {
		this.idPic = idPic;
	}
	public String getFingerData() {
		return fingerData;
	}
	public void setFingerData(String fingerData) {
		this.fingerData = fingerData;
	}
	public java.lang.String getLcbz() {
		return lcbz;
	}
	public void setLcbz(java.lang.String lcbz) {
		this.lcbz = lcbz;
	}
	public java.lang.String getJcfl() {
		return jcfl;
	}
	public void setJcfl(java.lang.String jcfl) {
		this.jcfl = jcfl;
	}
	public java.lang.String getCbzt() {
		return cbzt;
	}
	public void setCbzt(java.lang.String cbzt) {
		this.cbzt = cbzt;
	}
	public String getCbzwm() {
		return cbzwm;
	}
	public void setCbzwm(String cbzwm) {
		this.cbzwm = cbzwm;
	}
	public String getHc() {
		return hc;
	}
	public void setHc(String hc) {
		this.hc = hc;
	}
	public String getCywz() {
		return cywz;
	}
	public void setCywz(String cywz) {
		this.cywz = cywz;
	}

	

}
