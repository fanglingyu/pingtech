package com.pingtech.hgqw.pullxml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.pingtech.hgqw.entity.Cyxx;

public class PullXmlShipList {

	public static Map<String, Object> pullXml(String str) throws XmlPullParserException, IOException {
		String result = null;

		Map<String, Object> map = null;
		Cyxx cyxx = null;
		List<Cyxx> cyxxList = new ArrayList<Cyxx>();

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(str));
		int event = parser.getEventType();
		boolean success = false;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				map = new HashMap<String, Object>();
				break;
			case XmlPullParser.START_TAG:
				if ("result".equals(parser.getName())) {
					result = parser.nextText();
					map.put("result", result);
					if ("success".equals(result)) {
						success = true;
					} else {
						success = false;
					}
				} else if ("info".equals(parser.getName())) {
					if (success) {
						cyxx = new Cyxx();
					} else {
						map.put("info", parser.nextText());
						return map;
					}
				} else if ("hc".equals(parser.getName())) {
					cyxx.setHc(parser.nextText());
				} else if ("xm".equals(parser.getName())) {
					cyxx.setXm(parser.nextText());
				} else if ("zw".equals(parser.getName())) {
					cyxx.setZw(parser.nextText());
				} else if ("zjhm".equals(parser.getName())) {
					cyxx.setZjhm(parser.nextText());
				} else if ("cywz".equals(parser.getName())) {
					cyxx.setCywz(parser.nextText());

				} else if ("hyid".equals(parser.getName())) {
					cyxx.setHyid(parser.nextText());
				} else if ("zqjlid".equals(parser.getName())) {

				}
				break;
			case XmlPullParser.END_TAG:
				if ("info".equals(parser.getName())) {
					cyxxList.add(cyxx);
					cyxx = null;
				}
				break;
			}
			event = parser.next();
		}
		map.put("cyxxList", cyxxList);
		return map;
	}
}