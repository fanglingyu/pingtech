package com.pingtech.hgqw.module.bindplace.utils;


import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

import com.pingtech.R;
import com.pingtech.hgqw.module.bindplace.entity.BindPlace;
import com.pingtech.hgqw.utils.SystemSetting;

public class PullXmlBindPlace {
	/**
	 * 解析日常巡检时，刷电子标签（巡查地点）的返回数据
	 * 
	 * */
	public static BindPlace parseXMLData(String str, Context context) {
		BindPlace bindPlace = new BindPlace();
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = "";
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							bindPlace.setResult(false);
							success = false;
						} else if ("success".equals(text)) {
							bindPlace.setResult(true);
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (!success) {
							bindPlace.setInfo(parser.nextText());
						}
					} else if ("id".equals(parser.getName())) {
						// id
						SystemSetting.xunJianId = parser.nextText();
					} else if ("type".equals(parser.getName())) {
						// type
						SystemSetting.xunJianType = parser.nextText();
					} else if ("name".equals(parser.getName())) {
						// name
						SystemSetting.xunJianName = parser.nextText();
					} else if ("bwmc".equals(parser.getName())) {
						// bw
						SystemSetting.xunJianName = parser.nextText();
					} else if ("ssmt".equals(parser.getName())) {
						// bw
						SystemSetting.xunJianMTname = parser.nextText();
					} else if ("mtid".equals(parser.getName())) {
						// bw
						SystemSetting.xunJianMTid = parser.nextText();
					} else if ("kbnl".equals(parser.getName())) {
						// bw
						SystemSetting.xunJianKbnl = parser.nextText();
					} else if ("zxhz".equals(parser.getName())) {
						// bw
						SystemSetting.xunJianZxhz = parser.nextText();
					} else if ("mtzax".equals(parser.getName())) {
						// mt
						SystemSetting.xunJianMtzax = parser.nextText();
					} else if ("mtgsgs".equals(parser.getName())) {
						// mt
						SystemSetting.xunJianMtgsgs = parser.nextText();
					} else if ("mtgm".equals(parser.getName())) {
						// mt

						SystemSetting.xunJianMtgm = parser.nextText();
					} else if ("qylx".equals(parser.getName())) {
						// qy
						String xxd_arg2_s = parser.nextText();
						SystemSetting.xunJianQylx = xxd_arg2_s;
						// 区域类型需要翻译成中文, 区域类型,kkqy 卡口区域、jkqy 监控区域
						if (xxd_arg2_s != null && !"".equals(xxd_arg2_s)) {
							if ("kkqy".equals(xxd_arg2_s)) {
								xxd_arg2_s = context
										.getString(R.string.qy_lx_kkqy);
							} else if ("jkqy".equals(xxd_arg2_s)) {
								xxd_arg2_s = context
										.getString(R.string.qy_lx_jkqy);
							}
						}
					} else if ("qyfw".equals(parser.getName())) {
						// qy
						SystemSetting.xunJianQyfw = parser.nextText();
					} else if ("xxxx".equals(parser.getName())) {
						// qy
						SystemSetting.xunJianXxxx = parser.nextText();
					} else if ("wz".equals(parser.getName())) {
						// md/ft
						SystemSetting.xunJianWz = parser.nextText();
					} else if ("zdmbcbdw".equals(parser.getName())) {
						// md

						SystemSetting.xunJianZdmbcbdw = parser.nextText();
					} else if ("ss".equals(parser.getName())) {
						// md/ft

						SystemSetting.xunJianSs = parser.nextText();
					} else if ("mbdsl".equals(parser.getName())) {
						// md

						SystemSetting.xunJianMbdsl = parser.nextText();
					} else if ("zdgkcbdw".equals(parser.getName())) {
						// ft
						SystemSetting.xunJianZdgkcbdw = parser.nextText();
					}
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
			bindPlace.setResult(false);
		}
		return bindPlace;
	}

}
