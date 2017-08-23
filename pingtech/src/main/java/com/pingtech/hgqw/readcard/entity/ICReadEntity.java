package com.pingtech.hgqw.readcard.entity;

public class ICReadEntity {
	/**
	 * 加密区密钥
	 */
	public final static byte[] SAFE_PASSWORD_KEY = { 0x01, 0x00, 0x02, 0x03, 0x00, 0x09 };

	/**
	 * 默认卡号：0
	 */
	public final static int READ_TYPE_DEFAULT = 0;

	/**
	 * 加密区域：1
	 */
	public final static int READ_TYPE_SAFE = 1;

	/**
	 * 读卡区域：默认区域 READ_TYPE_DEFAULT: 0，加密区域 READ_TYPE_SAFE：1
	 */
	private int readType;

	public int getReadType() {
		return readType;
	}

	public void setReadType(int readType) {
		this.readType = readType;
	}

}
