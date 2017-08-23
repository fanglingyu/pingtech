package com.pingtech.hgqw.pullxml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class PullXmlGetCountOfTktxjl {

	public static String[] pullXml(String str) throws XmlPullParserException, IOException {
		String[] arr = { "0", "0" };
		String result = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(str));
		int event = parser.getEventType();
		boolean success = false;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				if ("result".equals(parser.getName())) {
					result = parser.nextText();
					if ("success".equals(result)) {
						success = true;
					} else {
						success = false;
					}
				} else if ("info".equals(parser.getName())) {

				} else if ("countLu".equals(parser.getName())) {
					arr[0] = parser.nextText();
				} else if ("countLun".equals(parser.getName())) {
					arr[1] = parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			event = parser.next();
		}
		return arr;
	}
}