package com.pingtech.hgqw.module.home.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.utils.SystemSetting;

public class PullXmlJwzl {
	/** 解析我的警务返回的数据 */
	public static boolean onParseXMLDataMyTask(String str) {
		HashMap<String, Object> map = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			boolean newtask = false;
			boolean success = false;
			String text = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("success".equals(text)) {
							success = true;
							if (SystemSetting.taskList == null) {
								SystemSetting.taskList = new ArrayList<Map<String, Object>>();
							} else {
								SystemSetting.taskList.clear();
							}
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							map = new HashMap<String, Object>();
						} else {
						}
					} else if ("taskid".equals(parser.getName())) {
						map.put("taskid", parser.nextText());
					} else if ("jqlb".equals(parser.getName())) {
						map.put("jqlb", parser.nextText());
					} else if ("cjfzr".equals(parser.getName())) {
						map.put("cjfzr", parser.nextText());
					} else if ("pzr".equals(parser.getName())) {
						map.put("pzr", parser.nextText());
					} else if ("fbr".equals(parser.getName())) {
						map.put("fbr", parser.nextText());
					} else if ("fbsj".equals(parser.getName())) {
						map.put("fbsj", parser.nextText());
					} else if ("xtry".equals(parser.getName())) {
						map.put("xtry", parser.nextText());
					} else if ("zlnr".equals(parser.getName())) {
						map.put("zlnr", parser.nextText());
					} else if ("zlzt".equals(parser.getName())) {
						map.put("zlzt", parser.nextText());
					} else if ("qszt".equals(parser.getName())) {
						map.put("qszt", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (map.get("taskid") != null) {
								if (SystemSetting.taskList == null) {
									SystemSetting.taskList = new ArrayList<Map<String, Object>>();
								}
								SystemSetting.taskList.add(map);
								String qszt = (String) map.get("qszt");
								if (qszt != null && qszt.equals("0")) {
									newtask = true;
								}
								// }
							}
						}
					}
					break;
				}
				type = parser.next();
			}
			return newtask;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
