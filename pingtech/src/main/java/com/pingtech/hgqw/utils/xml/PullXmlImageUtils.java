package com.pingtech.hgqw.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.pingtech.hgqw.module.bindplace.utils.SharedPreferencesUtil;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.utils.ImageUtil;
import com.pingtech.hgqw.utils.StringUtils;

public class PullXmlImageUtils {

	private static final String TAG = "PullXmlImageUtils";

	private static final String IMAGE_FLAG_YES = "0";

	private static final String IMAGE_FLAG_NO = "1";

	private static final String IMAGE_FLAG_LOCAL = "2";

	private static final String IMAGE_FLAG_REQUEST_EMPTY = "3";

	public static List<Hgzjxx> pullXml(String str) {
		XmlPullParser parser = Xml.newPullParser();
		List<Hgzjxx> hgzjxxs = new ArrayList<Hgzjxx>();
		try {
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
			int type = parser.getEventType();
			String zjhm = null;
			String image = null;
//			Hgzjxx hgzjxx = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("info".equals(parser.getName())) {
						zjhm = null;
						image = null;
//						hgzjxx = new Hgzjxx();
					} else if ("zjhm".equals(parser.getName())) {
						zjhm = parser.nextText();
					}else if ("sjid".equals(parser.getName())) {
						String sjid = parser.nextText();
						Log.i(TAG, "imageSjid="+sjid);
						SharedPreferencesUtil.updateImgSjid(sjid);
					} else if ("zp".equals(parser.getName())) {
						image = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (StringUtils.isNotEmpty(image)) {
							try {
								boolean isOk = ImageUtil.saveImage(zjhm, image);
//								hgzjxx.setZjhm(zjhm);
//								hgzjxx.setIdPic(IMAGE_FLAG_LOCAL);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						} else {
//							hgzjxx.setZjhm(zjhm);
//							hgzjxx.setIdPic(IMAGE_FLAG_REQUEST_EMPTY);
						}
//						hgzjxxs.add(hgzjxx);
					}
					break;

				default:
					break;
				}
				type = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return hgzjxxs;
	}
}
