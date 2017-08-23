package com.pingtech.hgqw.module.offline.kacbqk.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Kacbqk")
public class Kacbqk {
	@DatabaseField(id = true, unique = true, useGetSet = true, columnName = "kacbqkid")
	/** 口岸船舶情况ID */
	private java.lang.String kacbqkid;

	/** 船舶检索标识 */
	@DatabaseField(useGetSet = true, columnName = "cbjsbs")
	private java.lang.String cbjsbs;

	/** 航次 */
	@DatabaseField(useGetSet = true, columnName = "hc")
	private java.lang.String hc;

	/** 船舶中文名 */
	@DatabaseField(useGetSet = true, columnName = "cbzwm")
	private java.lang.String cbzwm;

	/** 船舶英文名 */
	@DatabaseField(useGetSet = true, columnName = "cbywm")
	private java.lang.String cbywm;

	/** 国籍 */
	@DatabaseField(useGetSet = true, columnName = "gj")
	private java.lang.String gj;

	/** 船舶性质 */
	@DatabaseField(useGetSet = true, columnName = "cbxz")
	private java.lang.String cbxz;

	/** 预计到港时间 */
	@DatabaseField(useGetSet = true, columnName = "yjdgsj")
	private Date yjdgsj;

	/** 预计离港时间 */
	@DatabaseField(useGetSet = true, columnName = "yjlgsj")
	private Date yjlgsj;

	/** 执勤人员到位状态 */
	@DatabaseField(useGetSet = true, columnName = "zqrydwzt")
	private java.lang.String zqrydwzt;

	/** 预停靠位置 */
	@DatabaseField(useGetSet = true, columnName = "ytkwz")
	private java.lang.String ytkwz;

	/** 抵港时间 */
	@DatabaseField(useGetSet = true, columnName = "dgsj")
	private Date dgsj;

	/** 移泊时间 */
	@DatabaseField(useGetSet = true, columnName = "ybsj")
	private Date ybsj;

	/** 靠泊时间 */
	@DatabaseField(useGetSet = true, columnName = "kbsj")
	private Date kbsj;

	/** 停靠位置 */
	@DatabaseField(useGetSet = true, columnName = "tkwz")
	private java.lang.String tkwz;

	/** 预检时间 */
	@DatabaseField(useGetSet = true, columnName = "yjsj")
	private java.lang.String yjsj;

	/** 正检时间 */
	@DatabaseField(useGetSet = true, columnName = "zjsj")
	private java.lang.String zjsj;

	/** 执勤单位 */
	@DatabaseField(useGetSet = true, columnName = "zqdw")
	private java.lang.String zqdw;

	/** 执勤方式 */
	@DatabaseField(useGetSet = true, columnName = "zqfs")
	private java.lang.String zqfs;

	/** 原执勤单位 */
	@DatabaseField(useGetSet = true, columnName = "yzqdw")
	private java.lang.String yzqdw;

	/** 原执勤方式 */
	@DatabaseField(useGetSet = true, columnName = "yzqfs")
	private java.lang.String yzqfs;

	/** 来港 */
	@DatabaseField(useGetSet = true, columnName = "slgk")
	private java.lang.String slgk;

	/** 下一港 */
	@DatabaseField(useGetSet = true, columnName = "mdgk")
	private java.lang.String mdgk;

	/** 离港时间 */
	@DatabaseField(useGetSet = true, columnName = "lgsj")
	private Date lgsj;

	/** 船舶口岸状态 */
	@DatabaseField(useGetSet = true, columnName = "cbkazt")
	private java.lang.String cbkazt;

	/** 停靠泊位 */
	@DatabaseField(useGetSet = true, columnName = "tkbw")
	private java.lang.String tkbw;

	/** 停靠码头 */
	@DatabaseField(useGetSet = true, columnName = "tkmt")
	private java.lang.String tkmt;

	/** 风险等级 */
	@DatabaseField(useGetSet = true, columnName = "fxdj")
	private java.lang.String fxdj;

	/** 外国籍船员数量 */
	@DatabaseField(useGetSet = true, columnName = "wgjcysl")
	private java.lang.String wgjcysl;

	/** 是否移舶 */
	@DatabaseField(useGetSet = true, columnName = "sfyb")
	private java.lang.String sfyb;

	/**
	 * 为首页封装的船员总数
	 */
	@DatabaseField(useGetSet = true, columnName = "cyzs")
	private String cyzs;

	/** 取消原因 */
	@DatabaseField(useGetSet = true, columnName = "qxyy")
	private java.lang.String qxyy;

	/** 经纬度 */
	@DatabaseField(useGetSet = true, columnName = "jwd")
	private java.lang.String jwd;

	/** 定位时间 */
	@DatabaseField(useGetSet = true, columnName = "dwsj")
	private java.lang.String dwsj;

	/** 检查分类 */
	@DatabaseField(useGetSet = true, columnName = "jcfl")
	private java.lang.String jcfl;

	/** 当前检查状态 */
	@DatabaseField(useGetSet = true, columnName = "dqjczt")
	private java.lang.String dqjczt;

	/** 执勤单位代码 */
	@DatabaseField(useGetSet = true, columnName = "zqdwdm")
	private java.lang.String zqdwdm;

	/** 参与执勤单位 */
	@DatabaseField(useGetSet = true, columnName = "cyzqdw")
	private java.lang.String cyzqdw;

	/** 原参与执勤单位 */
	@DatabaseField(useGetSet = true, columnName = "ycyzqdw")
	private java.lang.String ycyzqdw;

	/** 监护等级 */
	@DatabaseField(useGetSet = true, columnName = "jhdj")
	private java.lang.String jhdj;

	// columns END

	/** 船舶勤务状态 */
	@DatabaseField(useGetSet = true, columnName = "cbqwzt")
	private String cbqwzt;

	/** 超时未离状态,0:超时;1:未超时 */
	@DatabaseField(useGetSet = true, columnName = "cswlzt")
	private String cswlzt;

	/** 船代公司 */
	@DatabaseField(useGetSet = true, columnName = "cdgs")
	private String cdgs;

	/** 船舶临时经纬度，以","分隔，不需要存库 */
	@DatabaseField(useGetSet = true, columnName = "longlatit")
	private String longlatit;

	@DatabaseField(useGetSet = true, columnName = "imo")
	private String imo;

	@DatabaseField(useGetSet = true, columnName = "cbdh")
	private String cbdh;

	@DatabaseField(useGetSet = true, columnName = "ygsdwsj")
	private String ygsdwsj;// 遥感所定位时间

	@DatabaseField(useGetSet = true, columnName = "ygsjwd")
	private String ygsjwd;// 遥感所经纬度

	@DatabaseField(useGetSet = true, columnName = "dgkssjStr")
	private String dgkssjStr; // 抵港开始时间

	@DatabaseField(useGetSet = true, columnName = "dgjssjStr")
	private String dgjssjStr; // 抵港开始时间

	@DatabaseField(useGetSet = true, columnName = "lgkssjStr")
	private String lgkssjStr; // 抵港开始时间

	@DatabaseField(useGetSet = true, columnName = "lgjssjStr")
	private String lgjssjStr; // 抵港开始时间

	@DatabaseField(useGetSet = true, columnName = "dlucyCount")
	private int dlucyCount;// 登陆船员人数

	@DatabaseField(useGetSet = true, columnName = "dlunryCount")
	private int dlunryCount;// 登轮人员数

	@DatabaseField(useGetSet = true, columnName = "zqryxm")
	private String zqryxm;// 当前执勤人员

	public java.lang.String getDqjczt() {
		return dqjczt;
	}

	public void setDqjczt(java.lang.String dqjczt) {
		this.dqjczt = dqjczt;
	}

	public String getCdgs() {
		return cdgs;
	}

	public void setCdgs(String cdgs) {
		this.cdgs = cdgs;
	}

	public java.lang.String getKacbqkid() {
		return kacbqkid;
	}

	public void setKacbqkid(java.lang.String kacbqkid) {
		this.kacbqkid = kacbqkid;
	}

	public java.lang.String getCbjsbs() {
		return cbjsbs;
	}

	public void setCbjsbs(java.lang.String cbjsbs) {
		this.cbjsbs = cbjsbs;
	}

	public java.lang.String getHc() {
		return hc;
	}

	public void setHc(java.lang.String hc) {
		this.hc = hc;
	}

	public java.lang.String getCbzwm() {
		return cbzwm;
	}

	public void setCbzwm(java.lang.String cbzwm) {
		this.cbzwm = cbzwm;
	}

	public java.lang.String getCbywm() {
		return cbywm;
	}

	public void setCbywm(java.lang.String cbywm) {
		this.cbywm = cbywm;
	}

	public java.lang.String getGj() {
		return gj;
	}

	public void setGj(java.lang.String gj) {
		this.gj = gj;
	}

	public java.lang.String getCbxz() {
		return cbxz;
	}

	public void setCbxz(java.lang.String cbxz) {
		this.cbxz = cbxz;
	}

	public Date getYjdgsj() {
		return yjdgsj;
	}

	public Date getYbsj() {
		return ybsj;
	}

	public void setYbsj(Date ybsj) {
		this.ybsj = ybsj;
	}

	public Date getKbsj() {
		return kbsj;
	}

	public void setKbsj(Date kbsj) {
		this.kbsj = kbsj;
	}

	public void setYjdgsj(Date yjdgsj) {
		this.yjdgsj = yjdgsj;
	}

	public Date getYjlgsj() {
		return yjlgsj;
	}

	public void setYjlgsj(Date yjlgsj) {
		this.yjlgsj = yjlgsj;
	}

	public java.lang.String getZqrydwzt() {
		return zqrydwzt;
	}

	public void setZqrydwzt(java.lang.String zqrydwzt) {
		this.zqrydwzt = zqrydwzt;
	}

	public java.lang.String getYtkwz() {
		return ytkwz;
	}

	public void setYtkwz(java.lang.String ytkwz) {
		this.ytkwz = ytkwz;
	}

	public Date getDgsj() {
		return dgsj;
	}

	public void setDgsj(Date dgsj) {
		this.dgsj = dgsj;
	}

	public java.lang.String getTkwz() {
		return tkwz;
	}

	public void setTkwz(java.lang.String tkwz) {
		this.tkwz = tkwz;
	}

	public java.lang.String getZqdw() {
		return zqdw;
	}

	public void setZqdw(java.lang.String zqdw) {
		this.zqdw = zqdw;
	}

	public java.lang.String getZqfs() {
		return zqfs;
	}

	public void setZqfs(java.lang.String zqfs) {
		this.zqfs = zqfs;
	}

	public java.lang.String getYzqdw() {
		return yzqdw;
	}

	public void setYzqdw(java.lang.String yzqdw) {
		this.yzqdw = yzqdw;
	}

	public java.lang.String getYzqfs() {
		return yzqfs;
	}

	public void setYzqfs(java.lang.String yzqfs) {
		this.yzqfs = yzqfs;
	}

	public java.lang.String getSlgk() {
		return slgk;
	}

	public void setSlgk(java.lang.String slgk) {
		this.slgk = slgk;
	}

	public java.lang.String getMdgk() {
		return mdgk;
	}

	public void setMdgk(java.lang.String mdgk) {
		this.mdgk = mdgk;
	}

	public Date getLgsj() {
		return lgsj;
	}

	public void setLgsj(Date lgsj) {
		this.lgsj = lgsj;
	}

	public java.lang.String getCbkazt() {
		return cbkazt;
	}

	public void setCbkazt(java.lang.String cbkazt) {
		this.cbkazt = cbkazt;
	}

	public java.lang.String getTkbw() {
		return tkbw;
	}

	public void setTkbw(java.lang.String tkbw) {
		this.tkbw = tkbw;
	}

	public java.lang.String getTkmt() {
		return tkmt;
	}

	public void setTkmt(java.lang.String tkmt) {
		this.tkmt = tkmt;
	}

	public java.lang.String getFxdj() {
		return fxdj;
	}

	public void setFxdj(java.lang.String fxdj) {
		this.fxdj = fxdj;
	}

	public java.lang.String getWgjcysl() {
		return wgjcysl;
	}

	public void setWgjcysl(java.lang.String wgjcysl) {
		this.wgjcysl = wgjcysl;
	}

	public java.lang.String getSfyb() {
		return sfyb;
	}

	public void setSfyb(java.lang.String sfyb) {
		this.sfyb = sfyb;
	}

	public String getCyzs() {
		return cyzs;
	}

	public void setCyzs(String cyzs) {
		this.cyzs = cyzs;
	}

	public java.lang.String getQxyy() {
		return qxyy;
	}

	public void setQxyy(java.lang.String qxyy) {
		this.qxyy = qxyy;
	}

	public java.lang.String getJwd() {
		return jwd;
	}

	public void setJwd(java.lang.String jwd) {
		this.jwd = jwd;
	}

	public java.lang.String getDwsj() {
		return dwsj;
	}

	public void setDwsj(java.lang.String dwsj) {
		this.dwsj = dwsj;
	}

	public java.lang.String getJcfl() {
		return jcfl;
	}

	public void setJcfl(java.lang.String jcfl) {
		this.jcfl = jcfl;
	}

	public java.lang.String getZqdwdm() {
		return zqdwdm;
	}

	public void setZqdwdm(java.lang.String zqdwdm) {
		this.zqdwdm = zqdwdm;
	}

	public java.lang.String getCyzqdw() {
		return cyzqdw;
	}

	public void setCyzqdw(java.lang.String cyzqdw) {
		this.cyzqdw = cyzqdw;
	}

	public java.lang.String getYcyzqdw() {
		return ycyzqdw;
	}

	public void setYcyzqdw(java.lang.String ycyzqdw) {
		this.ycyzqdw = ycyzqdw;
	}

	public java.lang.String getJhdj() {
		return jhdj;
	}

	public void setJhdj(java.lang.String jhdj) {
		this.jhdj = jhdj;
	}

	public String getCbqwzt() {
		return cbqwzt;
	}

	public void setCbqwzt(String cbqwzt) {
		this.cbqwzt = cbqwzt;
	}

	public String getCswlzt() {
		return cswlzt;
	}

	public void setCswlzt(String cswlzt) {
		this.cswlzt = cswlzt;
	}

	public String getLonglatit() {
		return longlatit;
	}

	public void setLonglatit(String longlatit) {
		this.longlatit = longlatit;
	}

	public String getImo() {
		return imo;
	}

	public void setImo(String imo) {
		this.imo = imo;
	}

	public String getCbdh() {
		return cbdh;
	}

	public void setCbdh(String cbdh) {
		this.cbdh = cbdh;
	}

	public String getYgsdwsj() {
		return ygsdwsj;
	}

	public void setYgsdwsj(String ygsdwsj) {
		this.ygsdwsj = ygsdwsj;
	}

	public String getYgsjwd() {
		return ygsjwd;
	}

	public void setYgsjwd(String ygsjwd) {
		this.ygsjwd = ygsjwd;
	}

	public String getDgkssjStr() {
		return dgkssjStr;
	}

	public void setDgkssjStr(String dgkssjStr) {
		this.dgkssjStr = dgkssjStr;
	}

	public String getDgjssjStr() {
		return dgjssjStr;
	}

	public void setDgjssjStr(String dgjssjStr) {
		this.dgjssjStr = dgjssjStr;
	}

	public String getLgkssjStr() {
		return lgkssjStr;
	}

	public void setLgkssjStr(String lgkssjStr) {
		this.lgkssjStr = lgkssjStr;
	}

	public String getLgjssjStr() {
		return lgjssjStr;
	}

	public void setLgjssjStr(String lgjssjStr) {
		this.lgjssjStr = lgjssjStr;
	}

	public int getDlucyCount() {
		return dlucyCount;
	}

	public void setDlucyCount(int dlucyCount) {
		this.dlucyCount = dlucyCount;
	}

	public int getDlunryCount() {
		return dlunryCount;
	}

	public void setDlunryCount(int dlunryCount) {
		this.dlunryCount = dlunryCount;
	}

	public String getZqryxm() {
		return zqryxm;
	}

	public void setZqryxm(String zqryxm) {
		this.zqryxm = zqryxm;
	}

	public java.lang.String getYjsj() {
		return yjsj;
	}

	public void setYjsj(java.lang.String yjsj) {
		this.yjsj = yjsj;
	}

	public java.lang.String getZjsj() {
		return zjsj;
	}

	public void setZjsj(java.lang.String zjsj) {
		this.zjsj = zjsj;
	}

}
