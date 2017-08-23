package com.pingtech.hgqw.pullxml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.pingtech.hgqw.entity.AudioInfo;

public class PullXmlAudioInfoDetail {

	public static AudioInfo pullXml(String str) throws XmlPullParserException, IOException {
		AudioInfo audioInfo = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(str));
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				
				break;
			case XmlPullParser.START_TAG:
				if ("audio".equals(parser.getName())) {
					audioInfo = new AudioInfo();
				} else if ("fileName".equals(parser.getName())) {
					audioInfo.setFileName(parser.nextText());
				} else if ("time".equals(parser.getName())) {
					audioInfo.setTime(parser.nextText());
				} else if ("userFromId".equals(parser.getName())) {
					audioInfo.setUserFromId(parser.nextText());
				} else if ("userToId".equals(parser.getName())) {
					audioInfo.setUserToId(parser.nextText());
				} else if ("userFromName".equals(parser.getName())) {
					audioInfo.setUserFromName(parser.nextText());
				} else if ("userToName".equals(parser.getName())) {
					audioInfo.setUserToName(parser.nextText());
				} 
				break;
			case XmlPullParser.END_TAG:
			}
			event = parser.next();
		}
		return audioInfo;
	}
}