package com.pingtech.hgqw.module.police.entity;

/**
 * <b>类名</b>：Qwzlqwjs.java<br>
 * <p>
 * <b>标题</b>：品恩产品研发
 * </p>
 * <p>
 * <b>描述</b>： 勤务指令接收信息
 * </p>
 * <p>
 * <b>版权声明</b>：Copyright (c) 2012
 * </p>
 * <p>
 * <b>公司</b>：品恩科技
 * </p>
 * 
 * @author <font color='blue'>zohan</font>
 * @version 1.0
 * @date 2012-7-26 下午02:13:37
 */

public class Qwzlqwjs implements java.io.Serializable {

	/** 单位类型0：处警 */
	public static final String DWLX_CJ = "0";

	/** 单位类型1:协同 */
	public static final String DWLX_XT = "1";

	/** serialVersionUID */

	private static final long serialVersionUID = 5454155825314635342L;

	private String id = "";

	/** 勤务指令基本ID */
	private String qwzljbid = "";

	/** 勤务协同单位ID */
	private String qwzldwid = "";

	/** 指令类型 01 船舶监护;02 船舶巡查;03 执勤变更;04 船舶移泊;05 船体检查;06 物品检查;07搭靠检查;99其它 */
	private String zllx = "";

	/** 船舶中文名 */
	private String cbzwm = "";

	/** 船舶英文名 */
	private String cbywm = "";

	/** 船舶国籍 */
	private String cbgj = "";

	/** 船舶性质 */
	private String cbxj = "";

	/** 口岸船舶情况id */
	private String kacbqkid = "";

	/** 处警责任人 */
	private java.lang.String cjzrr = "";

	/** 监护方式 */
	private java.lang.String jhfs = "";

	/** 批准人 */
	private String pzr = "";

	/** 发布人 */
	private String fbr = "";

	/** 发布单位 */
	private String fbdw = "";

	private String bgyy = "";

	/** 显示发布单位名称，不保存到数据库 */
	private String fbdwname = "";

	/** 参与单位 */
	private String cydw = "";

	/** 签收人 */
	private String qsr = "";

	/** 签收时间 */
	private String qssj = "";

	/** 发布时间 */
	private String fbsj = "";

	/** 签收状态 0：未签收 1：已签收 */
	private String qszt;

	/** 超时未签收状态 0：正常1：超未签收 */
	private java.lang.String csqszt = "";

	/** 单位类型(0：处警,1:协同) */
	private String dwlx = "";

	/** 单位ID */
	private String dwid = "";

	/** 单位代码 */
	private String dwdm = "";

	/** 指令状态 */
	private String zlzt = "";

	/** 反馈状态 */
	private String fkzt = "";

	/** 挂起状态，0：正常， 1：已经挂起 */
	private Integer gqzt = 0;

	/** 移泊时间 */
	private String yybsj = "";

	/** 靠泊时间 */
	private String ykbsj = "";

	/** 监护方式 */
	private java.lang.String cjxxjhfs = "";

	/** 单位类型（zqdw：执勤单位，sldw：受令单位，cydw：参与单位） */
	private java.lang.String cjxxdwlx = "";

	/** 反馈人 */
	private java.lang.String cjxxfkr = "";

	/** 反馈时间 */
	private String cjxxfksj;

	/** 勤务派遣权限，true表示有勤务修改和取消指令权限 */
	private String qwpqQx = "false";

	/** 勤务签收权限，true表示有勤务指令的签收和反馈操作权限 */
	private String qwjsQx = "false";

	/** 船舶名称 */
	private String cbmc = "";

	/** 新监护方式 */
	private String xjhfs = "";

	/** 原监护方式 */
	private String yjhfs = "";

	/** 当前停靠位置（移泊指令） */
	private String dqtkwz = "";

	/** 移往位置（移泊指令） */
	private String ywwz = "";

	/** 工作要求 */
	private String gzyq = "";

	/** 带队人 */
	private String ddr = "";

	/** 出勤人员 */
	private String cqry = "";

	public String getBgyy() {
		return bgyy;
	}

	public void setBgyy(String bgyy) {
		this.bgyy = bgyy;
	}

	public String getCqry() {
		return cqry;
	}

	public void setCqry(String cqry) {
		this.cqry = cqry;
	}

	public String getDdr() {
		return ddr;
	}

	public void setDdr(String ddr) {
		this.ddr = ddr;
	}

	public String getGzyq() {
		return gzyq;
	}

	public void setGzyq(String gzyq) {
		this.gzyq = gzyq;
	}

	public String getYwwz() {
		return ywwz;
	}

	public void setYwwz(String ywwz) {
		this.ywwz = ywwz;
	}

	public String getDqtkwz() {
		return dqtkwz;
	}

	public void setDqtkwz(String dqtkwz) {
		this.dqtkwz = dqtkwz;
	}

	public String getYjhfs() {
		return yjhfs;
	}

	public void setYjhfs(String yjhfs) {
		this.yjhfs = yjhfs;
	}

	public String getXjhfs() {
		return xjhfs;
	}

	public void setXjhfs(String xjhfs) {
		this.xjhfs = xjhfs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQwzljbid() {
		return qwzljbid;
	}

	public void setQwzljbid(String qwzljbid) {
		this.qwzljbid = qwzljbid;
	}

	public String getQwzldwid() {
		return qwzldwid;
	}

	public void setQwzldwid(String qwzldwid) {
		this.qwzldwid = qwzldwid;
	}

	public String getZllx() {
		return zllx;
	}

	public void setZllx(String zllx) {
		this.zllx = zllx;
	}

	public String getCbzwm() {
		return cbzwm;
	}

	public void setCbzwm(String cbzwm) {
		this.cbzwm = cbzwm;
	}

	public String getCbywm() {
		return cbywm;
	}

	public void setCbywm(String cbywm) {
		this.cbywm = cbywm;
	}

	public String getCbgj() {
		return cbgj;
	}

	public void setCbgj(String cbgj) {
		this.cbgj = cbgj;
	}

	public String getCbxj() {
		return cbxj;
	}

	public void setCbxj(String cbxj) {
		this.cbxj = cbxj;
	}

	public String getKacbqkid() {
		return kacbqkid;
	}

	public void setKacbqkid(String kacbqkid) {
		this.kacbqkid = kacbqkid;
	}

	public java.lang.String getCjzrr() {
		return cjzrr;
	}

	public void setCjzrr(java.lang.String cjzrr) {
		this.cjzrr = cjzrr;
	}

	public java.lang.String getJhfs() {
		return jhfs;
	}

	public void setJhfs(java.lang.String jhfs) {
		this.jhfs = jhfs;
	}

	public String getPzr() {
		return pzr;
	}

	public void setPzr(String pzr) {
		this.pzr = pzr;
	}

	public String getFbr() {
		return fbr;
	}

	public void setFbr(String fbr) {
		this.fbr = fbr;
	}

	public String getFbdw() {
		return fbdw;
	}

	public void setFbdw(String fbdw) {
		this.fbdw = fbdw;
	}

	public String getFbdwname() {
		return fbdwname;
	}

	public void setFbdwname(String fbdwname) {
		this.fbdwname = fbdwname;
	}

	public String getCydw() {
		return cydw;
	}

	public void setCydw(String cydw) {
		this.cydw = cydw;
	}

	public String getQsr() {
		return qsr;
	}

	public void setQsr(String qsr) {
		this.qsr = qsr;
	}

	public String getQszt() {
		return qszt;
	}

	public void setQszt(String qszt) {
		this.qszt = qszt;
	}

	public java.lang.String getCsqszt() {
		return csqszt;
	}

	public void setCsqszt(java.lang.String csqszt) {
		this.csqszt = csqszt;
	}

	public String getDwlx() {
		return dwlx;
	}

	public void setDwlx(String dwlx) {
		this.dwlx = dwlx;
	}

	public String getDwid() {
		return dwid;
	}

	public void setDwid(String dwid) {
		this.dwid = dwid;
	}

	public String getDwdm() {
		return dwdm;
	}

	public void setDwdm(String dwdm) {
		this.dwdm = dwdm;
	}

	public String getZlzt() {
		return zlzt;
	}

	public void setZlzt(String zlzt) {
		this.zlzt = zlzt;
	}

	public String getFkzt() {
		return fkzt;
	}

	public void setFkzt(String fkzt) {
		this.fkzt = fkzt;
	}

	public Integer getGqzt() {
		return gqzt;
	}

	public void setGqzt(Integer gqzt) {
		this.gqzt = gqzt;
	}

	public java.lang.String getCjxxjhfs() {
		return cjxxjhfs;
	}

	public void setCjxxjhfs(java.lang.String cjxxjhfs) {
		this.cjxxjhfs = cjxxjhfs;
	}

	public java.lang.String getCjxxdwlx() {
		return cjxxdwlx;
	}

	public void setCjxxdwlx(java.lang.String cjxxdwlx) {
		this.cjxxdwlx = cjxxdwlx;
	}

	public java.lang.String getCjxxfkr() {
		return cjxxfkr;
	}

	public void setCjxxfkr(java.lang.String cjxxfkr) {
		this.cjxxfkr = cjxxfkr;
	}

	public String getQwpqQx() {
		return qwpqQx;
	}

	public void setQwpqQx(String qwpqQx) {
		this.qwpqQx = qwpqQx;
	}

	public String getQwjsQx() {
		return qwjsQx;
	}

	public void setQwjsQx(String qwjsQx) {
		this.qwjsQx = qwjsQx;
	}

	public static String getDwlxCj() {
		return DWLX_CJ;
	}

	public static String getDwlxXt() {
		return DWLX_XT;
	}

	public String getCbmc() {
		return cbmc;
	}

	public void setCbmc(String cbmc) {
		this.cbmc = cbmc;
	}

	public String getQssj() {
		return qssj;
	}

	public void setQssj(String qssj) {
		this.qssj = qssj;
	}

	public String getFbsj() {
		return fbsj;
	}

	public void setFbsj(String fbsj) {
		this.fbsj = fbsj;
	}

	public String getYybsj() {
		return yybsj;
	}

	public void setYybsj(String yybsj) {
		this.yybsj = yybsj;
	}

	public String getYkbsj() {
		return ykbsj;
	}

	public void setYkbsj(String ykbsj) {
		this.ykbsj = ykbsj;
	}

	public String getCjxxfksj() {
		return cjxxfksj;
	}

	public void setCjxxfksj(String cjxxfksj) {
		this.cjxxfksj = cjxxfksj;
	}

}
