package com.pingtech.hgqw.module.offline.hgzjxx.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pingtech.hgqw.utils.DateUtils;

@DatabaseTable(tableName = "Hgzjxx")
public class CopyOfHgzjxx {
	@DatabaseField( id= true, generatedId = false, unique= true, useGetSet = true, columnName = "cbzjffxxxid")
	private String cbzjffxxxid;
	// columns START
	/** 发放证件类别 */
	@DatabaseField(useGetSet = true, columnName = "zjlb")
	private String zjlb;

	/** IC卡编码 */
	@DatabaseField(useGetSet = true, columnName = "ickey")
	private String ickey;

	/** 证件编号 */
	@DatabaseField(useGetSet = true, columnName = "zjbh")
	private String zjbh;

	/** 船舶中文名 */
	@DatabaseField(useGetSet = true, columnName = "zwcbm")
	private String zwcbm;

	/** 船舶英文名 */
	@DatabaseField(useGetSet = true, columnName = "ywcbm")
	private String ywcbm;

	/** 服务船舶次 */
	@DatabaseField(useGetSet = true, columnName = "hc")
	private String hc;

	/** 船舶种类代码 */
	@DatabaseField(useGetSet = true, columnName = "cbzldm")
	private String cbzldm;

	/** 船号 */
	@DatabaseField(useGetSet = true, columnName = "ch")
	private String ch;

	/** 所属单位 */
	@DatabaseField(useGetSet = true, columnName = "ssdw")
	private String ssdw;

	/** 船籍港 */
	@DatabaseField(useGetSet = true, columnName = "cjg")
	private String cjg;

	/** 吨位 */
	@DatabaseField(useGetSet = true, columnName = "dw")
	private Long dw;

	/** 马力 */
	@DatabaseField(useGetSet = true, columnName = "ml")
	private Long ml;

	/** 用途 */
	@DatabaseField(useGetSet = true, columnName = "yt")
	private String yt;

	/** 定员 */
	@DatabaseField(useGetSet = true, columnName = "dy")
	private String dy;

	/** 证明书号 */
	@DatabaseField(useGetSet = true, columnName = "zmsh")
	private String zmsh;

	/** 批复文号 */
	@DatabaseField(useGetSet = true, columnName = "pfwh")
	private String pfwh;

	/** 搭靠船舶 */
	@DatabaseField(useGetSet = true, columnName = "dkcb")
	private String dkcb;

	/** 派出单位 */
	@DatabaseField(useGetSet = true, columnName = "pcdw")
	private String pcdw;

	/** 姓名 */
	@DatabaseField(useGetSet = true, columnName = "xm")
	private String xm;

	/** 性别代码 */
	@DatabaseField(useGetSet = true, columnName = "xbdm")
	private String xbdm;

	/** 国籍地区代码 */
	@DatabaseField(useGetSet = true, columnName = "gjdqdm")
	private String gjdqdm;

	/** 出生日期 */
	@DatabaseField(useGetSet = true, columnName = "csrq")
	private String csrq;

	/** 所持证件类别代码 */
	@DatabaseField(useGetSet = true, columnName = "zjlbdm")
	private String zjlbdm;

	/** 所持证件号码 */
	@DatabaseField(useGetSet = true, columnName = "zjhm")
	private String zjhm;

	/** 职务代码 */
	@DatabaseField(useGetSet = true, columnName = "zwdm")
	private String zwdm;

	/** 住宿天数 */
	@DatabaseField(useGetSet = true, columnName = "zsts")
	private String zsts;

	/** 是否登轮住宿 */
	@DatabaseField(useGetSet = true, columnName = "dlzsbz")
	private String dlzsbz;

	/** 登轮证期限类型 */
	@DatabaseField(useGetSet = true, columnName = "qxlx")
	private String qxlx;

	/** 有效期起 */
	@DatabaseField(useGetSet = true, columnName = "yxqq")
	private java.util.Date yxqq;

	/** 有效期止 */
	@DatabaseField(useGetSet = true, columnName = "yxqz")
	private java.util.Date yxqz;

	/** 申请日期 */
	@DatabaseField(useGetSet = true, columnName = "sqrq")
	private String sqrq;

	/** 申请事由 */
	@DatabaseField(useGetSet = true, columnName = "sqsy")
	private String sqsy;

	/** 备注 */
	@DatabaseField(useGetSet = true, columnName = "bz")
	private String bz;

	/** 领导审批意见 */
	@DatabaseField(useGetSet = true, columnName = "ldspyj")
	private String ldspyj;

	/** 审批领导 */
	@DatabaseField(useGetSet = true, columnName = "spld")
	private String spld;

	/** 审批日期 */
	@DatabaseField(useGetSet = true, columnName = "sprq")
	private String sprq;

	/** 证件状态 */
	@DatabaseField(useGetSet = true, columnName = "zjzt")
	private String zjzt;

	/** 操作员 */
	@DatabaseField(useGetSet = true, columnName = "czy")
	private String czy;

	/** 操作时间 */
	@DatabaseField(useGetSet = true, columnName = "czsj")
	private String czsj;

	/** 操作部门 */
	@DatabaseField(useGetSet = true, columnName = "czbm")
	private String czbm;

	/** 口岸代码 */
	@DatabaseField(useGetSet = true, columnName = "kadm")
	private String kadm;

	/** 签发机关 */
	@DatabaseField(useGetSet = true, columnName = "qfjg")
	private String qfjg;

	/** 一次有效标志 */
	@DatabaseField(useGetSet = true, columnName = "ycyxbz")
	private String ycyxbz;

	/** 台湾身份证号 */
	@DatabaseField(useGetSet = true, columnName = "twsfzh")
	private String twsfzh;

	/** 台湾住址 */
	@DatabaseField(useGetSet = true, columnName = "twzz")
	private String twzz;

	/** 创建者编号 */
	@DatabaseField(useGetSet = true, columnName = "cjzid")
	private String cjzid;

	/** 创建者所属部门编号 */
	@DatabaseField(useGetSet = true, columnName = "cjzssbmid")
	private String cjzssbmid;

	/** 创建时间 */
	@DatabaseField(useGetSet = true, columnName = "cjsj")
	private java.util.Date cjsj;

	/** 更新者编号 */
	@DatabaseField(useGetSet = true, columnName = "gxzid")
	private String gxzid;

	/** 更新者所属部门编号 */
	@DatabaseField(useGetSet = true, columnName = "gxzssbmid")
	private String gxzssbmid;

	/** 更新时间 */
	@DatabaseField(useGetSet = true, columnName = "gxsj")
	private java.util.Date gxsj;

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

	/** 碰撞时间 */
	@DatabaseField(useGetSet = true, columnName = "pzsj")
	private java.util.Date pzsj;

	/** 碰撞标识（0,空表示未碰撞，1表示已碰撞过） */
	@DatabaseField(useGetSet = true, columnName = "pzbs")
	private String pzbs;

	/** 同步时间 */
	@DatabaseField(useGetSet = true, columnName = "tbsj")
	private java.util.Date tbsj;

	/** 同步修改时间 */
	@DatabaseField(useGetSet = true, columnName = "tbxgsj")
	private java.util.Date tbxgsj;

	/**
	 * 是否船员家属，其中'0'表示非船员家属，'1'表示船员家属
	 * 此字段通过C/S版的证件管理模块赋值，当口岸船舶预离港进行人员平衡统计时，需要依赖此字段
	 */
	@DatabaseField(useGetSet = true, columnName = "sfcyjs")
	private String sfcyjs;

	/** ic卡发放状态 0：未发放；1：已发放；2注销 ；3:已发证不可用 用于对登记离港船舶临时登轮证、登轮证和搭靠外轮许可证进行注销操作 */
	@DatabaseField(useGetSet = true, columnName = "icffzt")
	private String icffzt;

	// columns END
	/** 是否允许发证 0不允许，1允许 */
	@DatabaseField(useGetSet = true, columnName = "sfyxfz")
	private String sfyxfz;

	/** 解禁时间 */
	@DatabaseField(useGetSet = true, columnName = "jjsj")
	private Date jjsj;

	/** 解禁人 */
	@DatabaseField(useGetSet = true, columnName = "jjrid")
	private String jjrid;

	/** 增加数据来源（0表示梅沙，1表示系统添加） */
	@DatabaseField(useGetSet = true, columnName = "sjly")
	private String sjly;

	/** 注销时间 */
	@DatabaseField(useGetSet = true, columnName = "zxsj")
	private java.util.Date zxsj;

	/** 发卡时间 */
	@DatabaseField(useGetSet = true, columnName = "fksj")
	private java.util.Date fksj;

	/** 长期证件再次碰撞日期 */
	@DatabaseField(useGetSet = true, columnName = "cqzjzcpzrq")
	private String cqzjzcpzrq;

	/** 所属码头 */
	@DatabaseField(useGetSet = true, columnName = "ssmt")
	private String ssmt;
	/** 所属区域 */
	@DatabaseField(useGetSet = true, columnName = "ssqy")
	private String ssqy;

	/** 服务范围 1表示船舶，2表示码头，3表示全港通用 */
	@DatabaseField(useGetSet = true, columnName = "fffw")
	private String fffw;
	/** 默认的IC卡编号 */
	@DatabaseField(useGetSet = true, columnName = "mrickey")
	private String mrickey;
	/** 变更原因 */
	@DatabaseField(useGetSet = true, columnName = "bgyy")
	private String bgyy;

	/** 所属范围 所有船舶、所有码头，所有区域 ssfwNameShow */
	@DatabaseField(useGetSet = true, columnName = "ssfwNameShow")
	private String ssfwNameShow;
	
	/**卡口ID，卡口管理下临时证*/
	@DatabaseField(useGetSet = true, columnName = "kkid")
	private String kkid;
	
	/////////

	/** 车牌号码 */
	@DatabaseField(useGetSet = true, columnName = "cphm")
	private String cphm;
	
	/** 驾驶证编号 */
	@DatabaseField(useGetSet = true, columnName = "jsyhm")
	private String jsyhm;
	
	/**发动机号*/
	@DatabaseField(useGetSet = true, columnName = "fdjh")
	private String fdjh;
	
	/**车辆类型*/
	@DatabaseField(useGetSet = true, columnName = "cllx")
	private String cllx;
	
	/**国籍（车辆）*/
	@DatabaseField(useGetSet = true, columnName = "gj")
	private String gj;
	
	/**车辆品牌*/
	@DatabaseField(useGetSet = true, columnName = "clpp")
	private String clpp;
	
	/**车辆颜色*/
	@DatabaseField(useGetSet = true, columnName = "clys")
	private String clys;
	
	/**公司拥有者*/
	@DatabaseField(useGetSet = true, columnName = "gsyyz")
	private String gsyyz;
	
	/////////
	
	
	public String getKkid() {
		return kkid;
	}

	public String getCphm() {
		return cphm;
	}

	public void setCphm(String cphm) {
		this.cphm = cphm;
	}

	public String getJsyhm() {
		return jsyhm;
	}

	public void setJsyhm(String jsyhm) {
		this.jsyhm = jsyhm;
	}

	public String getFdjh() {
		return fdjh;
	}

	public void setFdjh(String fdjh) {
		this.fdjh = fdjh;
	}

	public String getCllx() {
		return cllx;
	}

	public void setCllx(String cllx) {
		this.cllx = cllx;
	}

	public String getGj() {
		return gj;
	}

	public void setGj(String gj) {
		this.gj = gj;
	}

	public String getClpp() {
		return clpp;
	}

	public void setClpp(String clpp) {
		this.clpp = clpp;
	}

	public String getClys() {
		return clys;
	}

	public void setClys(String clys) {
		this.clys = clys;
	}

	public String getGsyyz() {
		return gsyyz;
	}

	public void setGsyyz(String gsyyz) {
		this.gsyyz = gsyyz;
	}

	public void setKkid(String kkid) {
		this.kkid = kkid;
	}

	public String getCbzjffxxxid() {
		return cbzjffxxxid;
	}

	public void setCbzjffxxxid(String cbzjffxxxid) {
		this.cbzjffxxxid = cbzjffxxxid;
	}

	public String getZjlb() {
		return zjlb;
	}

	public void setZjlb(String zjlb) {
		this.zjlb = zjlb;
	}

	public String getIckey() {
		return ickey;
	}

	public void setIckey(String ickey) {
		this.ickey = ickey;
	}

	public String getZjbh() {
		return zjbh;
	}

	public void setZjbh(String zjbh) {
		this.zjbh = zjbh;
	}

	public String getZwcbm() {
		return zwcbm;
	}

	public void setZwcbm(String zwcbm) {
		this.zwcbm = zwcbm;
	}

	public String getYwcbm() {
		return ywcbm;
	}

	public void setYwcbm(String ywcbm) {
		this.ywcbm = ywcbm;
	}

	public String getHc() {
		return hc;
	}

	public void setHc(String hc) {
		this.hc = hc;
	}

	public String getCbzldm() {
		return cbzldm;
	}

	public void setCbzldm(String cbzldm) {
		this.cbzldm = cbzldm;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	public String getSsdw() {
		return ssdw;
	}

	public void setSsdw(String ssdw) {
		this.ssdw = ssdw;
	}

	public String getCjg() {
		return cjg;
	}

	public void setCjg(String cjg) {
		this.cjg = cjg;
	}

	public Long getDw() {
		return dw;
	}

	public void setDw(Long dw) {
		this.dw = dw;
	}

	public Long getMl() {
		return ml;
	}

	public void setMl(Long ml) {
		this.ml = ml;
	}

	public String getYt() {
		return yt;
	}

	public void setYt(String yt) {
		this.yt = yt;
	}

	public String getDy() {
		return dy;
	}

	public void setDy(String dy) {
		this.dy = dy;
	}

	public String getZmsh() {
		return zmsh;
	}

	public void setZmsh(String zmsh) {
		this.zmsh = zmsh;
	}

	public String getPfwh() {
		return pfwh;
	}

	public void setPfwh(String pfwh) {
		this.pfwh = pfwh;
	}

	public String getDkcb() {
		return dkcb;
	}

	public void setDkcb(String dkcb) {
		this.dkcb = dkcb;
	}

	public String getPcdw() {
		return pcdw;
	}

	public void setPcdw(String pcdw) {
		this.pcdw = pcdw;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getXbdm() {
		return xbdm;
	}

	public void setXbdm(String xbdm) {
		this.xbdm = xbdm;
	}

	public String getGjdqdm() {
		return gjdqdm;
	}

	public void setGjdqdm(String gjdqdm) {
		this.gjdqdm = gjdqdm;
	}

	public String getCsrq() {
		return DateUtils.gainBirthday(csrq);
	}

	public void setCsrq(String csrq) {
		this.csrq = csrq;
	}

	public String getZjlbdm() {
		return zjlbdm;
	}

	public void setZjlbdm(String zjlbdm) {
		this.zjlbdm = zjlbdm;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public String getZwdm() {
		return zwdm;
	}

	public void setZwdm(String zwdm) {
		this.zwdm = zwdm;
	}

	public String getZsts() {
		return zsts;
	}

	public void setZsts(String zsts) {
		this.zsts = zsts;
	}

	public String getDlzsbz() {
		return dlzsbz;
	}

	public void setDlzsbz(String dlzsbz) {
		this.dlzsbz = dlzsbz;
	}

	public String getQxlx() {
		return qxlx;
	}

	public void setQxlx(String qxlx) {
		this.qxlx = qxlx;
	}

	public java.util.Date getYxqq() {
		return yxqq;
	}

	public void setYxqq(java.util.Date yxqq) {
		this.yxqq = yxqq;
	}

	public java.util.Date getYxqz() {
		return yxqz;
	}

	public void setYxqz(java.util.Date yxqz) {
		this.yxqz = yxqz;
	}

	public String getSqrq() {
		return sqrq;
	}

	public void setSqrq(String sqrq) {
		this.sqrq = sqrq;
	}

	public String getSqsy() {
		return sqsy;
	}

	public void setSqsy(String sqsy) {
		this.sqsy = sqsy;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getLdspyj() {
		return ldspyj;
	}

	public void setLdspyj(String ldspyj) {
		this.ldspyj = ldspyj;
	}

	public String getSpld() {
		return spld;
	}

	public void setSpld(String spld) {
		this.spld = spld;
	}

	public String getSprq() {
		return sprq;
	}

	public void setSprq(String sprq) {
		this.sprq = sprq;
	}

	public String getZjzt() {
		return zjzt;
	}

	public void setZjzt(String zjzt) {
		this.zjzt = zjzt;
	}

	public String getCzy() {
		return czy;
	}

	public void setCzy(String czy) {
		this.czy = czy;
	}

	public String getCzsj() {
		return czsj;
	}

	public void setCzsj(String czsj) {
		this.czsj = czsj;
	}

	public String getCzbm() {
		return czbm;
	}

	public void setCzbm(String czbm) {
		this.czbm = czbm;
	}

	public String getKadm() {
		return kadm;
	}

	public void setKadm(String kadm) {
		this.kadm = kadm;
	}

	public String getQfjg() {
		return qfjg;
	}

	public void setQfjg(String qfjg) {
		this.qfjg = qfjg;
	}

	public String getYcyxbz() {
		return ycyxbz;
	}

	public void setYcyxbz(String ycyxbz) {
		this.ycyxbz = ycyxbz;
	}

	public String getTwsfzh() {
		return twsfzh;
	}

	public void setTwsfzh(String twsfzh) {
		this.twsfzh = twsfzh;
	}

	public String getTwzz() {
		return twzz;
	}

	public void setTwzz(String twzz) {
		this.twzz = twzz;
	}

	public String getCjzid() {
		return cjzid;
	}

	public void setCjzid(String cjzid) {
		this.cjzid = cjzid;
	}

	public String getCjzssbmid() {
		return cjzssbmid;
	}

	public void setCjzssbmid(String cjzssbmid) {
		this.cjzssbmid = cjzssbmid;
	}

	public java.util.Date getCjsj() {
		return cjsj;
	}

	public void setCjsj(java.util.Date cjsj) {
		this.cjsj = cjsj;
	}

	public String getGxzid() {
		return gxzid;
	}

	public void setGxzid(String gxzid) {
		this.gxzid = gxzid;
	}

	public String getGxzssbmid() {
		return gxzssbmid;
	}

	public void setGxzssbmid(String gxzssbmid) {
		this.gxzssbmid = gxzssbmid;
	}

	public java.util.Date getGxsj() {
		return gxsj;
	}

	public void setGxsj(java.util.Date gxsj) {
		this.gxsj = gxsj;
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

	public java.util.Date getPzsj() {
		return pzsj;
	}

	public void setPzsj(java.util.Date pzsj) {
		this.pzsj = pzsj;
	}

	public String getPzbs() {
		return pzbs;
	}

	public void setPzbs(String pzbs) {
		this.pzbs = pzbs;
	}

	public java.util.Date getTbsj() {
		return tbsj;
	}

	public void setTbsj(java.util.Date tbsj) {
		this.tbsj = tbsj;
	}

	public java.util.Date getTbxgsj() {
		return tbxgsj;
	}

	public void setTbxgsj(java.util.Date tbxgsj) {
		this.tbxgsj = tbxgsj;
	}

	public String getSfcyjs() {
		return sfcyjs;
	}

	public void setSfcyjs(String sfcyjs) {
		this.sfcyjs = sfcyjs;
	}

	public String getIcffzt() {
		return icffzt;
	}

	public void setIcffzt(String icffzt) {
		this.icffzt = icffzt;
	}

	public String getSfyxfz() {
		return sfyxfz;
	}

	public void setSfyxfz(String sfyxfz) {
		this.sfyxfz = sfyxfz;
	}

	public Date getJjsj() {
		return jjsj;
	}

	public void setJjsj(Date jjsj) {
		this.jjsj = jjsj;
	}

	public String getJjrid() {
		return jjrid;
	}

	public void setJjrid(String jjrid) {
		this.jjrid = jjrid;
	}

	public String getSjly() {
		return sjly;
	}

	public void setSjly(String sjly) {
		this.sjly = sjly;
	}

	public java.util.Date getZxsj() {
		return zxsj;
	}

	public void setZxsj(java.util.Date zxsj) {
		this.zxsj = zxsj;
	}

	public java.util.Date getFksj() {
		return fksj;
	}

	public void setFksj(java.util.Date fksj) {
		this.fksj = fksj;
	}

	public String getCqzjzcpzrq() {
		return cqzjzcpzrq;
	}

	public void setCqzjzcpzrq(String cqzjzcpzrq) {
		this.cqzjzcpzrq = cqzjzcpzrq;
	}

	public String getSsmt() {
		return ssmt;
	}

	public void setSsmt(String ssmt) {
		this.ssmt = ssmt;
	}

	public String getSsqy() {
		return ssqy;
	}

	public void setSsqy(String ssqy) {
		this.ssqy = ssqy;
	}

	public String getFffw() {
		return fffw;
	}

	public void setFffw(String fffw) {
		this.fffw = fffw;
	}

	public String getMrickey() {
		return mrickey;
	}

	public void setMrickey(String mrickey) {
		this.mrickey = mrickey;
	}

	public String getBgyy() {
		return bgyy;
	}

	public void setBgyy(String bgyy) {
		this.bgyy = bgyy;
	}

	public String getSsfwNameShow() {
		return ssfwNameShow;
	}

	public void setSsfwNameShow(String ssfwNameShow) {
		this.ssfwNameShow = ssfwNameShow;
	}

}
