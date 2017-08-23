package com.pingtech.hgqw.pullxml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.pingtech.hgqw.entity.GetUserList;
import com.pingtech.hgqw.entity.UserInfo;

public class PullXmlGetUserList {

	public static GetUserList pullXml(String str) throws XmlPullParserException, IOException {
		GetUserList getUserList = null;
		UserInfo userInfo = null;
		String result = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(str));
		int event = parser.getEventType();
		boolean success = false;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				getUserList = new GetUserList();
				break;
			case XmlPullParser.START_TAG:
				if ("result".equals(parser.getName())) {
					result = parser.nextText();
					getUserList.setResult(result);
					if ("success".equals(result)) {
						success = true;
					} else {
						success = false;
					}
				} else if ("info".equals(parser.getName())) {
					if (success) {
						userInfo = new UserInfo();
					} else {
						getUserList.setInfo(parser.nextText());
						return getUserList;
					}
				} else if ("yhm".equals(parser.getName())) {
					userInfo.setYhm(parser.nextText());
				} else if ("id".equals(parser.getName())) {
					userInfo.setId(parser.nextText());
				} else if ("xm".equals(parser.getName())) {
					userInfo.setXm(parser.nextText());
				} else if ("ssdw".equals(parser.getName())) {
					userInfo.setSsdw(parser.nextText());
				} else if ("ssdwid".equals(parser.getName())) {
					userInfo.setSsdwid(parser.nextText());
				} else if ("sska".equals(parser.getName())) {
					userInfo.setSska(parser.nextText());
				} else if ("kadm".equals(parser.getName())) {
					userInfo.setKadm(parser.nextText());
				} else if ("zqjlid".equals(parser.getName())) {
					userInfo.setZqjlid(parser.nextText());
				} else if ("pdazt".equals(parser.getName())) {
					userInfo.setPdazt(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("info".equals(parser.getName())) {
					getUserList.addUserInfo(userInfo);
					userInfo = null;
				}
				break;
			}
			event = parser.next();
		}
		return getUserList;
	}
}