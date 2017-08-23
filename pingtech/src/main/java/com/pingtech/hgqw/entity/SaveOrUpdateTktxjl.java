package com.pingtech.hgqw.entity;

public class SaveOrUpdateTktxjl {
	/**
	 * 请求结果
	 */
	private String result = "";

	/**
	 * 结果提示
	 */
	private String info = "";

	/**
	 * 通行记录ID
	 */
	private String txjlid = "";

	/**
	 * 巡查巡检ID
	 */
	private String xcxsid = "";

	/**
	 * 查岗查哨ID
	 */
	private String cgcsid = "";

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTxjlid() {
		return txjlid;
	}

	public void setTxjlid(String txjlid) {
		this.txjlid = txjlid;
	}

	public String getXcxsid() {
		return xcxsid;
	}

	public void setXcxsid(String xcxsid) {
		this.xcxsid = xcxsid;
	}

	public String getCgcsid() {
		return cgcsid;
	}

	public void setCgcsid(String cgcsid) {
		this.cgcsid = cgcsid;
	}
}
