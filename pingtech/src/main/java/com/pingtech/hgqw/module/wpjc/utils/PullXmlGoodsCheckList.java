package com.pingtech.hgqw.module.wpjc.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullXmlGoodsCheckList {

	/** 解析平台返回的数据 */
	public static String onParseXMLData(String str,
			ArrayList<Map<String, String>> goodsList) {
		// TODO Auto-generated method stub
		String httpReturnXMLInfo = null;
		Map<String, String> map = null;
		boolean success = false;
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
						if ("success".equals(text)) {
							success = true;
							if (goodsList == null) {
								goodsList = new ArrayList<Map<String, String>>();
							} else {
								goodsList.clear();
							}
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							map = new HashMap<String, String>();
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("ssdw".equals(parser.getName())) {
						map.put("ssdw", parser.nextText());
					} else if ("xm".equals(parser.getName())) {
						map.put("xm", parser.nextText());
					} else if ("type".equals(parser.getName())) {
						map.put("type", parser.nextText());
					} else if ("time".equals(parser.getName())) {
						map.put("time", parser.nextText());
					} else if ("fx".equals(parser.getName())) {
						map.put("fx", parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (goodsList == null) {
								goodsList = new ArrayList<Map<String, String>>();
							}
							goodsList.add(map);
						}
					}
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpReturnXMLInfo;
	}
}
