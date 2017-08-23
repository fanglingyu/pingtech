package com.pingtech.hgqw.entity;

public class AudioInfo {
	/**
	 * 音频Base64编码
	 */
	private String content = "";
	/**
	 * 详情文件Base64编码
	 */
	private String contentXml = "";

	/**
	 * 左右标识：0本人右侧，1他人左侧
	 */
	private String type = "";

	/**
	 * 音频文件名
	 */
	private String fileName = "";
	/**
	 * 音频详情文件名
	 */
	private String fileNameDetail = "";

	/**
	 * 文件大小
	 */
	private long fileSize = 0;

	/**
	 * 音频时长
	 */
	private String time = "";

	/**
	 * 发送人姓名
	 */
	private String userFromName = "";

	/**
	 * 接收人姓名
	 */
	private String userToName = "";

	/**
	 * 发送人ID
	 */
	private String userFromId = "";

	/**
	 * 接收人ID
	 */
	private String userToId = "";

	/**
	 * 最后修改时间
	 */
	private long lastModifiedTime = 0;

	public String getContentXml() {
		return contentXml;
	}

	public void setContentXml(String contentXml) {
		this.contentXml = contentXml;
	}

	public String getFileNameDetail() {
		return fileNameDetail;
	}

	public void setFileNameDetail(String fileNameDetail) {
		this.fileNameDetail = fileNameDetail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserFromName() {
		return userFromName;
	}

	public void setUserFromName(String userFromName) {
		this.userFromName = userFromName;
	}

	public String getUserToName() {
		return userToName;
	}

	public void setUserToName(String userToName) {
		this.userToName = userToName;
	}

	public String getUserFromId() {
		return userFromId;
	}

	public void setUserFromId(String userFromId) {
		this.userFromId = userFromId;
	}

	public String getUserToId() {
		return userToId;
	}

	public void setUserToId(String userToId) {
		this.userToId = userToId;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "AudioInfo [fileName=" + fileName + ", fileNameDetail=" + fileNameDetail + ", fileSize=" + fileSize + ", time=" + time
				+ ", userFromName=" + userFromName + ", userToName=" + userToName + ", userFromId=" + userFromId + ", userToId=" + userToId
				+ ", lastModifiedTime=" + lastModifiedTime + "]";
	}

	
}
