package com.pingtech.hgqw.module.exception.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.pingtech.hgqw.utils.Log;

/**
 * 
 * 
 * 类描述：解析XML工具类
 * 
 * <p>
 * Title: 江海港边检勤务-移动管理系统-PullXmlUtils.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author 娄高伟
 * @version 1.0
 * @date 2013-10-9 上午10:42:42
 */
public class PullOffLineExcXml {
	private static final String TAG = "PullOffLineExcXml";

/**
 * 
 * @方法名：parseXMLData
 * @功能说明：异常信息离线解析XML
 * @author 娄高伟
 * @date  2013-10-14 下午4:57:15
 * @param str
 * @return
 */
	public static Map<String, String> parseXMLData(String str) {
		Map<String, String> map = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("info".equals(parser.getName())) {
						map = new HashMap<String, String>();
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
					} else if ("objectType".equals(parser.getName())) {
						map.put("objecttype", parser.nextText());
					} else if ("cardNumber".equals(parser.getName())) {
						map.put("cardnumber", parser.nextText());
					} else if ("cardType".equals(parser.getName())) {
						map.put("cardtype", parser.nextText());
					} else if ("eventType".equals(parser.getName())) {
						map.put("eventtype", parser.nextText());
					} else if ("name".equals(parser.getName())) {
						map.put("name", parser.nextText());
					} else if ("sex".equals(parser.getName())) {
						map.put("sex", parser.nextText());
					} else if ("nationality".equals(parser.getName())) {
						map.put("nationality", parser.nextText());
					} else if ("birthday".equals(parser.getName())) {
						map.put("birthday", parser.nextText());
					} else if ("company".equals(parser.getName())) {
						map.put("company", parser.nextText());
					} else if ("source".equals(parser.getName())) {
						map.put("source", parser.nextText());
					} else if ("eventDesc".equals(parser.getName())) {
						map.put("eventdesc", parser.nextText());
					} else if ("eventRemark".equals(parser.getName())) {
						map.put("eventremark", parser.nextText());
					} else if ("scene".equals(parser.getName())) {
						map.put("scene", parser.nextText());
					} else if ("inspectTime".equals(parser.getName())) {
						map.put("inspecttime", parser.nextText());
					} else if ("shipName".equals(parser.getName())) {
						map.put("shipname", parser.nextText());
					} else if ("swid".equals(parser.getName())) {
						map.put("swid", parser.nextText());
					} else if ("jhhc".equals(parser.getName())) {
						map.put("jhhc", parser.nextText());
					} else if ("dockCode".equals(parser.getName())) {
						map.put("dockcode", parser.nextText());
					} else if ("berthCode".equals(parser.getName())) {
						map.put("berthcode", parser.nextText());
					} else if ("areaCode".equals(parser.getName())) {
						map.put("areacode", parser.nextText());
					} else if ("whetherHandle".equals(parser.getName())) {
						map.put("whetherHandle", parser.nextText());
					} else if ("glcbmc".equals(parser.getName())) {
						map.put("glcbmc", parser.nextText());
					} else if ("cphm".equals(parser.getName())) {
						map.put("cphm", parser.nextText());
					} else if ("clpp".equals(parser.getName())) {
						map.put("clpp", parser.nextText());
					} else if ("fdjh".equals(parser.getName())) {
						map.put("fdjh", parser.nextText());
					} else if ("cbzwm".equals(parser.getName())) {
						map.put("cbzwm", parser.nextText());
					} else if ("cbywm".equals(parser.getName())) {
						map.put("cbywm", parser.nextText());
					} else if ("sbmc".equals(parser.getName())) {
						map.put("sbmc", parser.nextText());
					} else if ("sbid".equals(parser.getName())) {
						map.put("sbid", parser.nextText());
					} else if ("qymc".equals(parser.getName())) {
						map.put("qymc", parser.nextText());
					} else if ("qyid".equals(parser.getName())) {
						map.put("qyid", parser.nextText());
					} else if ("jcfs".equals(parser.getName())) {
						map.put("jcfs", parser.nextText());
					} else if ("cgcsid".equals(parser.getName())) {
						map.put("cgcsid", parser.nextText());
					} else if ("dkjlid".equals(parser.getName())) {
						map.put("dkjlid", parser.nextText());
					} else if ("dockname".equals(parser.getName())) {
						map.put("dockname", parser.nextText());
					} else if ("berthname".equals(parser.getName())) {
						map.put("berthname", parser.nextText());
					} else if ("areaname".equals(parser.getName())) {
						map.put("areaname", parser.nextText());
					} else if ("sbkid".equals(parser.getName())) {
						map.put("sbkid", parser.nextText());
					} else if ("handleType".equals(parser.getName())) {
						map.put("handleType", parser.nextText());
					} else if ("handleEventType".equals(parser.getName())) {
						map.put("handleEventType", parser.nextText());
					} else if ("handleResult".equals(parser.getName())) {
						map.put("handleResult", parser.nextText());
					} else if ("handleRemark".equals(parser.getName())) {
						map.put("handleRemark", parser.nextText());
					}

					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();

			}
		} catch (XmlPullParserException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}
		return map;

	}

}
