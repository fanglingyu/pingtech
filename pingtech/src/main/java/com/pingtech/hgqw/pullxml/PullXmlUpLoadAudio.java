package com.pingtech.hgqw.pullxml;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.entity.AudioInfo;
import com.pingtech.hgqw.entity.AudioInfoList;

public class PullXmlUpLoadAudio {

	public static AudioInfoList pullXml(String str) {
		if (str == null || "".equals(str)) {
			return null;
		}

		AudioInfoList audioInfoList = null;
		AudioInfo audioInfo = null;
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
						audioInfoList = new AudioInfoList();
						result = parser.nextText();
						audioInfoList.setResult(result);
					} else if ("info".equals(parser.getName())) {
						if ("success".equals(result)) {
							audioInfo = new AudioInfo();
						} else {
							audioInfoList.setInfo(parser.nextText());
						}
					} else if ("name".equals(parser.getName())) {
						audioInfo.setFileName(parser.nextText());
					} else if ("nameXml".equals(parser.getName())) {
						audioInfo.setFileNameDetail(parser.nextText());
					} else if ("content".equals(parser.getName())) {
						audioInfo.setContent(parser.nextText());
					} else if ("contentXml".equals(parser.getName())) {
						audioInfo.setContentXml(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if ("success".equals(result)) {
							// 一条语音返回成功
							audioInfoList.add(audioInfo);
							audioInfo = null;
						}
					}
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return audioInfoList;
	}

}
