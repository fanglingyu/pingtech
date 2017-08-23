package com.pingtech.hgqw.module.offline.userinfo.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "TBUserinfo")
public class TBUserinfo {
	/** ID */
	@DatabaseField(id = true, generatedId = false, unique = true, useGetSet = true, columnName = "id")
	private String id;

	/** 人员ID */
	@DatabaseField(useGetSet = true, columnName = "ryid")
	private String ryid;

	/** 证书序列号 */
	@DatabaseField(useGetSet = true, columnName = "zsxlh")
	private String zsxlh;

	/** 所属机构ID */
	@DatabaseField(useGetSet = true, columnName = "ssjgid")
	private String ssjgid;

	/** 管理的部门 */
	@DatabaseField(useGetSet = true, columnName = "glbm")
	private String glbm;

	/** 身份证号 */
	@DatabaseField(useGetSet = true, columnName = "sfzh")
	private String sfzh;

	/** 用户名 */
	@DatabaseField(useGetSet = true, columnName = "yhm")
	private String yhm;

	/** 姓名 */
	@DatabaseField(useGetSet = true, columnName = "xm")
	private String xm;

	/** 别名 */
	@DatabaseField(useGetSet = true, columnName = "bm")
	private String bm;

	/** 密码 */
	@DatabaseField(useGetSet = true, columnName = "mm")
	private String mm;

	/** 用户状态(0:正常 1:删除) */
	@DatabaseField(useGetSet = true, columnName = "yhzt")
	private Integer yhzt;

	/** 用户类别 1：干部，2：士兵 3：船方自管 */
	@DatabaseField(useGetSet = true, columnName = "yhlb")
	private Integer yhlb;

	/** 排序Id */
	@DatabaseField(useGetSet = true, columnName = "pxid")
	private Integer pxid;

	/** 电子邮箱 */
	@DatabaseField(useGetSet = true, columnName = "dzyx")
	private String dzyx;

	/** 电话号码 */
	@DatabaseField(useGetSet = true, columnName = "dhhm")
	private String dhhm;

	/** 手机号码 */
	@DatabaseField(useGetSet = true, columnName = "sjhm")
	private String sjhm;

	/** 证书DN项 */
	@DatabaseField(useGetSet = true, columnName = "zsdnx")
	private String zsdnx;

	/** 警官证号 */
	@DatabaseField(useGetSet = true, columnName = "jgzh")
	private String jgzh;

	/** 职级 */
	@DatabaseField(useGetSet = true, columnName = "zj")
	private String zj;

	/** 职务 */
	@DatabaseField(useGetSet = true, columnName = "zw")
	private String zw;

	/** 警种 */
	@DatabaseField(useGetSet = true, columnName = "jz")
	private String jz;

	/** 任职 */
	@DatabaseField(useGetSet = true, columnName = "rz")
	private String rz;

	/** 工作岗位 */
	@DatabaseField(useGetSet = true, columnName = "gzgw")
	private String gzgw;

	/** 是否是管理员 */
	@DatabaseField(useGetSet = true, columnName = "sfgly")
	private Integer sfgly;

	/** 创建者ID */
	@DatabaseField(useGetSet = true, columnName = "cjzid")
	private String cjzid;

	/** 创建者所属部门ID */
	@DatabaseField(useGetSet = true, columnName = "cjzssbmid")
	private String cjzssbmid;

	/** 更新者ID */
	@DatabaseField(useGetSet = true, columnName = "gxzid")
	private String gxzid;

	/** 职务 非PO字段 */
	@DatabaseField(useGetSet = true, columnName = "zwname")
	private String zwname;

	/** 所属机构 非PO字段 */
	@DatabaseField(useGetSet = true, columnName = "ssjgidname")
	private String ssjgidname;

	/** 更新时间 非PO字段 */
	@DatabaseField(useGetSet = true, columnName = "gxsj")
	private java.util.Date gxsj;

	/** 创建时间 非PO字段 */
	@DatabaseField(useGetSet = true, columnName = "cjsj")
	private java.util.Date cjsj;

	/** 更新者所属部门ID */
	@DatabaseField(useGetSet = true, columnName = "gxzssbmid")
	private String gxzssbmid;

	/** 数据来源类型（0：本地，1：导入） */
	@DatabaseField(useGetSet = true, columnName = "sjlylx")
	private String sjlylx;

	/** 士兵 ＩＣ卡 */
	@DatabaseField(useGetSet = true, columnName = "sbk")
	private String sbk;

	/** 口岸代码 add by pijl 2012-11-13 */
	@DatabaseField(useGetSet = true, columnName = "kadm")
	private String kadm;

	/** 总队口岸代码 */
	@DatabaseField(useGetSet = true, columnName = "zdkadm")
	private String zdkadm;

	/**
	 * 边检站的部门id，通过单位代码先获取此人所属的最后一级口岸，然后通过口岸关联的单位，取出此人所属的全站单位id
	 */
	@DatabaseField(useGetSet = true, columnName = "bjzdeptid")
	private String bjzdeptid;

	/** 人员类别 */
	@DatabaseField(useGetSet = true, columnName = "rylb")
	private String rylb;

	/** 保存当前登录人的sessionId 用来与当前客户端的sessionId进行匹配，相同则正常访问 不同则跳出 重新登录 */
	@DatabaseField(useGetSet = true, columnName = "sessionId")
	private String sessionId;

	/** 用户创建方式，用于区别人员信息是来源于一体化还是自建创建 默认值为0 */
	@DatabaseField(useGetSet = true, columnName = "yhcjfs")
	private String yhcjfs;

	/** 证件状态 */
	@DatabaseField(useGetSet = true, columnName = "zjzt")
	private String zjzt;

	/** 登录方式 （0 ：本地 1： 一体化单点） */
	@DatabaseField(useGetSet = true, columnName = "dlfs")
	private String dlfs;

	/** 梅沙账号 */
	@DatabaseField(useGetSet = true, columnName = "mszh")
	private String mszh;

	// 新增加四个字段，荣誉、警衔、政治面貌 ,文件路径，2013年 4月 15号
	/** 荣誉 */
	@DatabaseField(useGetSet = true, columnName = "ry")
	private String ry;

	/** 警衔 */
	@DatabaseField(useGetSet = true, columnName = "jx")
	private String jx;

	/** 政治面貌 */
	@DatabaseField(useGetSet = true, columnName = "zzmm")
	private String zzmm;

	/** 文件路径 */
	@DatabaseField(useGetSet = true, columnName = "fileUrl")
	private String fileUrl;

	/** 单位代码 非PO字段 */
	@DatabaseField(useGetSet = true, columnName = "dwdm")
	private String dwdm;

	/** 性别 */
	@DatabaseField(useGetSet = true, columnName = "xb")
	private String xb;

	/** 出生日期 */
	@DatabaseField(useGetSet = true, columnName = "csrq")
	private java.util.Date csrq;

	/** 出生日期(字符串类型，用于页面传值) */
	@DatabaseField(useGetSet = true, columnName = "csrqStr")
	private String csrqStr;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRyid() {
		return ryid;
	}

	public void setRyid(String ryid) {
		this.ryid = ryid;
	}

	public String getZsxlh() {
		return zsxlh;
	}

	public void setZsxlh(String zsxlh) {
		this.zsxlh = zsxlh;
	}

	public String getSsjgid() {
		return ssjgid;
	}

	public void setSsjgid(String ssjgid) {
		this.ssjgid = ssjgid;
	}

	public String getGlbm() {
		return glbm;
	}

	public void setGlbm(String glbm) {
		this.glbm = glbm;
	}

	public String getSfzh() {
		return sfzh;
	}

	public void setSfzh(String sfzh) {
		this.sfzh = sfzh;
	}

	public String getYhm() {
		return yhm;
	}

	public void setYhm(String yhm) {
		this.yhm = yhm;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getBm() {
		return bm;
	}

	public void setBm(String bm) {
		this.bm = bm;
	}

	public String getMm() {
		return mm;
	}

	public void setMm(String mm) {
		this.mm = mm;
	}

	public Integer getYhzt() {
		return yhzt;
	}

	public void setYhzt(Integer yhzt) {
		this.yhzt = yhzt;
	}

	public Integer getYhlb() {
		return yhlb;
	}

	public void setYhlb(Integer yhlb) {
		this.yhlb = yhlb;
	}

	public Integer getPxid() {
		return pxid;
	}

	public void setPxid(Integer pxid) {
		this.pxid = pxid;
	}

	public String getDzyx() {
		return dzyx;
	}

	public void setDzyx(String dzyx) {
		this.dzyx = dzyx;
	}

	public String getDhhm() {
		return dhhm;
	}

	public void setDhhm(String dhhm) {
		this.dhhm = dhhm;
	}

	public String getSjhm() {
		return sjhm;
	}

	public void setSjhm(String sjhm) {
		this.sjhm = sjhm;
	}

	public String getZsdnx() {
		return zsdnx;
	}

	public void setZsdnx(String zsdnx) {
		this.zsdnx = zsdnx;
	}

	public String getJgzh() {
		return jgzh;
	}

	public void setJgzh(String jgzh) {
		this.jgzh = jgzh;
	}

	public String getZj() {
		return zj;
	}

	public void setZj(String zj) {
		this.zj = zj;
	}

	public String getZw() {
		return zw;
	}

	public void setZw(String zw) {
		this.zw = zw;
	}

	public String getJz() {
		return jz;
	}

	public void setJz(String jz) {
		this.jz = jz;
	}

	public String getRz() {
		return rz;
	}

	public void setRz(String rz) {
		this.rz = rz;
	}

	public String getGzgw() {
		return gzgw;
	}

	public void setGzgw(String gzgw) {
		this.gzgw = gzgw;
	}

	public Integer getSfgly() {
		return sfgly;
	}

	public void setSfgly(Integer sfgly) {
		this.sfgly = sfgly;
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

	public String getGxzid() {
		return gxzid;
	}

	public void setGxzid(String gxzid) {
		this.gxzid = gxzid;
	}

	public String getZwname() {
		return zwname;
	}

	public void setZwname(String zwname) {
		this.zwname = zwname;
	}

	public String getSsjgidname() {
		return ssjgidname;
	}

	public void setSsjgidname(String ssjgidname) {
		this.ssjgidname = ssjgidname;
	}

	public java.util.Date getGxsj() {
		return gxsj;
	}

	public void setGxsj(java.util.Date gxsj) {
		this.gxsj = gxsj;
	}

	public java.util.Date getCjsj() {
		return cjsj;
	}

	public void setCjsj(java.util.Date cjsj) {
		this.cjsj = cjsj;
	}

	public String getGxzssbmid() {
		return gxzssbmid;
	}

	public void setGxzssbmid(String gxzssbmid) {
		this.gxzssbmid = gxzssbmid;
	}

	public String getSjlylx() {
		return sjlylx;
	}

	public void setSjlylx(String sjlylx) {
		this.sjlylx = sjlylx;
	}

	public String getSbk() {
		return sbk;
	}

	public void setSbk(String sbk) {
		this.sbk = sbk;
	}

	public String getKadm() {
		return kadm;
	}

	public void setKadm(String kadm) {
		this.kadm = kadm;
	}

	public String getZdkadm() {
		return zdkadm;
	}

	public void setZdkadm(String zdkadm) {
		this.zdkadm = zdkadm;
	}

	public String getBjzdeptid() {
		return bjzdeptid;
	}

	public void setBjzdeptid(String bjzdeptid) {
		this.bjzdeptid = bjzdeptid;
	}

	public String getRylb() {
		return rylb;
	}

	public void setRylb(String rylb) {
		this.rylb = rylb;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getYhcjfs() {
		return yhcjfs;
	}

	public void setYhcjfs(String yhcjfs) {
		this.yhcjfs = yhcjfs;
	}

	public String getZjzt() {
		return zjzt;
	}

	public void setZjzt(String zjzt) {
		this.zjzt = zjzt;
	}

	public String getDlfs() {
		return dlfs;
	}

	public void setDlfs(String dlfs) {
		this.dlfs = dlfs;
	}

	public String getMszh() {
		return mszh;
	}

	public void setMszh(String mszh) {
		this.mszh = mszh;
	}

	public String getRy() {
		return ry;
	}

	public void setRy(String ry) {
		this.ry = ry;
	}

	public String getJx() {
		return jx;
	}

	public void setJx(String jx) {
		this.jx = jx;
	}

	public String getZzmm() {
		return zzmm;
	}

	public void setZzmm(String zzmm) {
		this.zzmm = zzmm;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getDwdm() {
		return dwdm;
	}

	public void setDwdm(String dwdm) {
		this.dwdm = dwdm;
	}

	public String getXb() {
		return xb;
	}

	public void setXb(String xb) {
		this.xb = xb;
	}

	public java.util.Date getCsrq() {
		return csrq;
	}

	public void setCsrq(java.util.Date csrq) {
		this.csrq = csrq;
	}

	public String getCsrqStr() {
		return csrqStr;
	}

	public void setCsrqStr(String csrqStr) {
		this.csrqStr = csrqStr;
	}


}
