package com.pingtech.hgqw.module.cgcs.utils;

import java.io.ByteArrayInputStream;

import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.module.cgcs.entity.Cgcs;
/**
 * 
 *
 * 类描述：解析查岗查哨返回数据
 *
 * <p> Title: 江海港边检勤务-移动管理系统-PullXmlCgcs.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  娄高伟 
 * @version 1.0
 * @date  2013-8-16 下午3:16:30
 */
public class PullXmlCgcs {
	public static Cgcs onParseCgcsXMLData(String str) {
		Cgcs cgcs = new Cgcs();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = "";
			boolean zjxx = false;
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							cgcs.setResult(false);
							success = false;
						} else if ("success".equals(text)) {
							cgcs.setResult(true);
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
						} else {
							cgcs.setInfo(parser.nextText());
						}
					} else if ("tsxx".equals(parser.getName())) {
						// 提示信息
						cgcs.setInfo(parser.nextText());
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = true;
					} else if ("xm".equals(parser.getName())) {
						// 姓名
						if (zjxx) {
							cgcs.setXm(parser.nextText());
						}
					} else if ("sbkid".equals(parser.getName())) {
						// 士兵卡id
						if (zjxx) {
							cgcs.setSbkid(parser.nextText());
						}
						// 增加参数执勤地点
					} else if ("zqdd".equals(parser.getName())) {
						// 士兵卡id
						if (zjxx) {
							cgcs.setZqdd(parser.nextText());
						}
					} else if ("zw".equals(parser.getName())) {
						// 职务
						if (zjxx) {
							cgcs.setZw(parser.nextText());
						}
					} else if ("zjhm".equals(parser.getName())) {
						// 证件号码
						if (zjxx) {
							cgcs.setZjhm(parser.nextText());
						}

					} else if ("ssdw".equals(parser.getName())) {
						// 所属单位
						if (zjxx) {
							cgcs.setSsdw(parser.nextText());
						}
					} else if ("icpic".equals(parser.getName())) {
						// 照片信息
						if (zjxx) {
							String icpic_s = parser.nextText();
							if (icpic_s != null && icpic_s.length() > 0) {
								cgcs.setIcpic(Base64.decode(icpic_s));
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = false;
					}
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			cgcs.setResult(false);
		}
		return cgcs;
	}
}
