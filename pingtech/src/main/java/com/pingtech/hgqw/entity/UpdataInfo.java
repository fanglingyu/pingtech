package com.pingtech.hgqw.entity;

/** 版本信息类 */
public class UpdataInfo {
	/** 版本号(int) */
	private int versionCode;

	/** 版本号(string) */
	private String version;

	/** apk url */
	private String url;

	/** apk file size */
	private int size;

	/** 版本更新日志 */
	private String description;

	/** 是否强制升级，true强制升级，false不强制升级 */
	private String force;

	/** 强制升级提示信息 */
	private String forceInfo;
	
	private boolean update;

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public String getUrl() {
		return this.url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public String getForce() {
		return force;
	}

	public void setForce(String force) {
		this.force = force;
	}

	public String getForceInfo() {
		return forceInfo;
	}

	public void setForceInfo(String forceInfo) {
		this.forceInfo = forceInfo;
	}
}
