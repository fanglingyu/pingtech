package com.pingtech.hgqw.pullxml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.pingtech.hgqw.entity.SaveOrUpdateTktxjl;

public class PullXmlSaveOrUpdateTktxjl {

	public static SaveOrUpdateTktxjl pullXml(String str) throws XmlPullParserException, IOException {
		SaveOrUpdateTktxjl saveOrUpdateTktxjl = null;
		String result = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(str));
		int event = parser.getEventType();
		boolean success = false;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				saveOrUpdateTktxjl = new SaveOrUpdateTktxjl();
				break;
			case XmlPullParser.START_TAG:
				if ("result".equals(parser.getName())) {
					result = parser.nextText();
					saveOrUpdateTktxjl.setResult(result);
					if ("success".equals(result)) {
						success = true;
					} else {
						success = false;
					}
				} else if ("info".equals(parser.getName())) {
					if (success) {
					} else {
						saveOrUpdateTktxjl.setInfo(parser.nextText());
						return saveOrUpdateTktxjl;
					}
				} else if ("txjlid".equals(parser.getName())) {
					saveOrUpdateTktxjl.setTxjlid(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			event = parser.next();
		}
		return saveOrUpdateTktxjl;
	}
}