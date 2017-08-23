package com.pingtech.hgqw.module.cfzg.utils;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullXmlCfzgShip {
	/** 解析平台返回的船舶详情信息数据 */
	public static HashMap<String, Object> onParseXMLData(String str) {
		/** 用来保存船舶信息 */
		HashMap<String, Object> shipData = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							if (shipData == null) {
								shipData = new HashMap<String, Object>();
								shipData.put("bdzt", "已绑定");
							}
						} else {
						}
					} else if ("hc".equals(parser.getName())) {
						if (success) {
							shipData.put("hc", parser.nextText());
						}
					} else if ("cbzwm".equals(parser.getName())) {
						if (success) {
							shipData.put("cbzwm", parser.nextText());
						}
					} else if ("jcfl".equals(parser.getName())) {
						if (success) {
							shipData.put("jcfl", parser.nextText());
						}
					} else if ("cbywm".equals(parser.getName())) {
						if (success) {
							shipData.put("cbywm", parser.nextText());
						}
					} else if ("gj".equals(parser.getName())) {
						if (success) {
							shipData.put("gj", parser.nextText());
						}
					} else if ("cbxz".equals(parser.getName())) {
						if (success) {
							shipData.put("cbxz", parser.nextText());
						}
					} else if ("tkwz".equals(parser.getName())) {
						if (success) {
							shipData.put("tkwz", parser.nextText());
						}
					} else if ("tkmt".equals(parser.getName())) {
						if (success) {
							shipData.put("tkmt", parser.nextText());
						}
					} else if ("tkbw".equals(parser.getName())) {
						if (success) {
							shipData.put("tkbw", parser.nextText());
						}
					} else if ("cdgs".equals(parser.getName())) {
						if (success) {
							shipData.put("cdgs", parser.nextText());
						}
					} else if ("cys".equals(parser.getName())) {
						if (success) {
							shipData.put("cys", parser.nextText());
						}
					} else if ("dlcys".equals(parser.getName())) {
						if (success) {
							shipData.put("dlcys", parser.nextText());
						}
					} else if ("dlrys".equals(parser.getName())) {
						if (success) {
							shipData.put("dlrys", parser.nextText());
						}
					} else if ("kacbzt".equals(parser.getName())) {
						if (success) {
							String kacbztstr = parser.nextText();
							if (kacbztstr.equals("0")) {
								shipData.put("kacbzt", "预到港");
							} else if (kacbztstr.equals("1")) {
								shipData.put("kacbzt", "在港");
							} else if (kacbztstr.equals("2")) {
								shipData.put("kacbzt", "预离港");
							} else if (kacbztstr.equals("3")) {
								shipData.put("kacbzt", "离港");
							} else {
								shipData.put("kacbzt", "");
							}
						}
					} else if ("dqjczt".equals(parser.getName())) {
						if (success) {
							shipData.put("dqjczt", parser.nextText());
						}
					} else if ("kacbqkid".equals(parser.getName())) {
						shipData.put("kacbqkid", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
			return shipData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
