package com.pingtech.hgqw.module.offline.base.utils;

public class DataBaseInfo {
	public static final String DB_NAME = "hgqw";

	/**
	 * 2014-07-29版本号 10->11，增加Pzxx表 。 11-12，证件表增加字段
	 * 2014-11-20版本号12-13，口岸船舶情况增加正检时间、预检时间字段
	 * 2015-06-30版本号13-14，证件表增加isykt
	 * */
	public static final int DATABASE_VERSION = 14;

	/**
	 * 2014-09-22版本号
	 * 11->12，更行Hgzjxx表，增加字段：cllx、clsbdh、cphm、clys、jsyhm、fdjh、gj、gsyyz
	 * 、glpp、lxfs、qylxbs
	 */
	// public static final int DATABASE_VERSION = 12;
}
