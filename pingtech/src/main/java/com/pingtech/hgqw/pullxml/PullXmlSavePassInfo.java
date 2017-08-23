package com.pingtech.hgqw.pullxml;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.entity.SavePassInfo;
import com.pingtech.hgqw.utils.Log;

public class PullXmlSavePassInfo {

	public static SavePassInfo pullXml(String str) {
		if (str == null || "".equals(str)) {
			return null;
		}
		SavePassInfo savePassInfo = new SavePassInfo();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
			// 设置解析的数据源
			int event = parser.getEventType();
			String result = "";
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						result = parser.nextText();
						savePassInfo.setResult(result);
					} else if ("info".equals(parser.getName())) {
						if ("success".equals(result)) {
						} else {
							savePassInfo.setInfo(parser.nextText());
						}
					} else if ("txjlid".equals(parser.getName())) {
						savePassInfo.setTxjlid(parser.nextText());
					} else if ("xcxsid".equals(parser.getName())) {
						savePassInfo.setXcxsid(parser.nextText());
					} else if ("cgcsid".equals(parser.getName())) {
						savePassInfo.setCgcsid(parser.nextText());
					}
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			Log.log2File("报错", "解析SavePassInfo报错：" + e.getMessage());
		}
		return savePassInfo;
	}

}
