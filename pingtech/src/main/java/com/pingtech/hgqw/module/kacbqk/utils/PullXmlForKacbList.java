package com.pingtech.hgqw.module.kacbqk.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullXmlForKacbList {
	public static ArrayList<HashMap<String, Object>> onParseXMLData(String str) {
		boolean success = false;
		ArrayList<HashMap<String, Object>> shipList = null;
		HashMap<String, Object> map = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
							if (shipList == null) {
								shipList = new ArrayList<HashMap<String, Object>>();
							} else {
								shipList.clear();
							}
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (!success) {
						} else {
							map = new HashMap<String, Object>();
						}
					} else if ("hc".equals(parser.getName())) {
						map.put("hc", parser.nextText());
					} else if ("bdzt".equals(parser.getName())) {

						if (map.get("bdzt") == null) {
							String bdzt = parser.nextText();
							map.put("bdzt", bdzt);
						}
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
					} else if ("cbzwm".equals(parser.getName())) {
						map.put("cbzwm", parser.nextText());
					} else if ("cbywm".equals(parser.getName())) {
						map.put("cbywm", parser.nextText());
					} else if ("gj".equals(parser.getName())) {
						map.put("gj", parser.nextText());
					} else if ("cbxz".equals(parser.getName())) {
						map.put("cbxz", parser.nextText());
					} else if ("tkwz".equals(parser.getName())) {
						map.put("tkwz", parser.nextText());
					} else if ("cjsj".equals(parser.getName())) {
						map.put("cjsj", parser.nextText());
					} else if ("ss".equals(parser.getName())) {
						map.put("ssdw", parser.nextText());
					} else if ("cbzyyt".equals(parser.getName())) {
						map.put("cbzyyt", parser.nextText());
					} else if ("cbmc".equals(parser.getName())) {
						map.put("cbzwm", parser.nextText());
					} else if ("czmc".equals(parser.getName())) {
						map.put("czmc", parser.nextText());
					} else if ("cardNumber".equals(parser.getName())) {
						map.put("cardNumber", parser.nextText());
					} else if ("kkmc".equals(parser.getName())) {
						map.put("kkmc", parser.nextText());
					} else if ("kkfw".equals(parser.getName())) {
						map.put("kkfw", parser.nextText());
					} else if ("kkxx".equals(parser.getName())) {
						map.put("kkxx", parser.nextText());
					} else if ("tkmt".equals(parser.getName())) {
						map.put("tkmt", parser.nextText());
					} else if ("tkbw".equals(parser.getName())) {
						map.put("tkbw", parser.nextText());
					} else if ("kacbzt".equals(parser.getName())) {
						String kacbztstr = parser.nextText();
						if (kacbztstr.equals("0")) {
							map.put("kacbzt", "预到港");
						} else if (kacbztstr.equals("1")) {
							map.put("kacbzt", "在港");
						} else if (kacbztstr.equals("2")) {
							map.put("kacbzt", "预离港");
						} else if (kacbztstr.equals("3")) {
							map.put("kacbzt", "离港");
						} else {
							map.put("kacbzt", "");
						}
						// 离线用到数据
					} else if ("dqjczt".equals(parser.getName())) {
						map.put("dqjczt", parser.nextText());
					} else if ("cys".equals(parser.getName())) {
						map.put("cys", parser.nextText());
					} else if ("cdgs".equals(parser.getName())) {
						map.put("cdgs", parser.nextText());
					} else if ("jcfl".equals(parser.getName())) {
						map.put("jcfl", parser.nextText());
					} else if ("kacbqkid".equals(parser.getName())) {
						map.put("kacbqkid", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (map.get("kacbqkid") != null) {
								if (shipList == null) {
									shipList = new ArrayList<HashMap<String, Object>>();
								}
								map.put("cblx", "ward");
								shipList.add(map);
							}
						}
					}
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return shipList;
	}
}
