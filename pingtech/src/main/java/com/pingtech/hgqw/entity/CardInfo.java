package com.pingtech.hgqw.entity;

import java.io.Serializable;

import android_serialport_api.ParseSFZAPI.People;

import com.pingtech.hgqw.module.tikou.entity.PersonInfo;
import com.pingtech.hgqw.zxing.entity.MsTdc;

public class CardInfo implements Serializable {
	private static final long serialVersionUID = 211534348849528553L;

	/** 卡片类型：IC卡---0 IC卡, 1 手动输入 , 2 二维码 , 3身份证 */
	public static final int CARD_TYPE_IC = 0;

	/** 卡片类型：IC卡---0 IC卡, 1 手动输入 , 2 二维码 , 3身份证 */
	public static final int CARD_TYPE_SDSR = 1;

	/** 卡片类型：IC卡---0 IC卡, 1 手动输入 , 2 二维码 , 3身份证 */
	public static final int CARD_TYPE_ZXING = 2;

	/** 卡片类型：IC卡---0 IC卡, 1 手动输入 , 2 二维码 , 3身份证 */
	public static final int CARD_TYPE_ID = 3;

	/** 卡片类型：IC卡---0 IC卡, 1 手动输入 , 2 二维码 , 3身份证 */
	private int cardType = CARD_TYPE_SDSR;

	private String ickey;

	private String info;
//通行方向
	private String txfx;

	private String cphm;

	private boolean isPass = false;

	private String fx;

	private String tsxx;

	/** 驾驶证编号/身份证号 */
	private String jszbh_sfzh;

	private String txjlid;

	private String xcxsid;

	private boolean hasCardInfo = false;

	private String defaultIckey;

	private boolean result = false;

	private String zjhm;

	/** 身份证信息 */
	private People people;

	private PersonInfo personInfo;

	private Clzjxx clzjxx;

	private boolean isOffline = false;

	/** 二维码信息 */
	private MsTdc msTdc;

	public boolean isOffline() {
		return isOffline;
	}

	public void setOffline(boolean isOffline) {
		this.isOffline = isOffline;
	}

	public String getCphm() {
		return cphm;
	}

	public void setCphm(String cphm) {
		this.cphm = cphm;
	}

	public String getXcxsid() {
		return xcxsid;
	}

	public void setXcxsid(String xcxsid) {
		this.xcxsid = xcxsid;
	}

	public String getJszbh_sfzh() {
		return jszbh_sfzh;
	}

	public void setJszbh_sfzh(String jszbh_sfzh) {
		this.jszbh_sfzh = jszbh_sfzh;
	}

	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public String getTxjlid() {
		return txjlid;
	}

	public void setTxjlid(String txjlid) {
		this.txjlid = txjlid;
	}

	public String getTsxx() {
		return tsxx;
	}

	public void setTsxx(String tsxx) {
		this.tsxx = tsxx;
	}

	public String getTxfx() {
		return txfx;
	}

	public void setTxfx(String txfx) {
		this.txfx = txfx;
	}

	public Clzjxx getClzjxx() {
		if(clzjxx==null){
			clzjxx = new Clzjxx();	
		}
		return clzjxx;
	}

	public void setClzjxx(Clzjxx clzjxx) {
		this.clzjxx = clzjxx;
	}

	public boolean isHasCardInfo() {
		return hasCardInfo;
	}

	public void setHasCardInfo(boolean hasCardInfo) {
		this.hasCardInfo = hasCardInfo;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public String getIckey() {
		return ickey;
	}

	public void setIckey(String ickey) {
		this.ickey = ickey;
	}

	public String getDefaultIckey() {
		return defaultIckey;
	}

	public void setDefaultIckey(String defaultIckey) {
		this.defaultIckey = defaultIckey;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public People getPeople() {
		return people;
	}

	public void setPeople(People people) {
		this.people = people;
	}

	public PersonInfo getPersonInfo() {
		if (personInfo == null) {
			personInfo = new PersonInfo();
		}
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

	public MsTdc getMsTdc() {
		return msTdc;
	}

	public void setMsTdc(MsTdc msTdc) {
		this.msTdc = msTdc;
	}

}
