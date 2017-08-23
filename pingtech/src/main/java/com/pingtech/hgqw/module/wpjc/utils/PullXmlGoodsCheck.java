package com.pingtech.hgqw.module.wpjc.utils;

import java.io.ByteArrayInputStream;

import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.module.wpjc.entity.ReadCardPersonInfo;

public class PullXmlGoodsCheck {
	/** 解析刷卡登记平台返回的数据 */
	public static ReadCardPersonInfo parseXMLData(String str) {
		ReadCardPersonInfo personInfo = new ReadCardPersonInfo();
		personInfo.setHasCardInfo(false);
		personInfo.setSuccessFlag(0);
		personInfo.setResult(false);
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = "";
			boolean zjxx = false;
			boolean success = false;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							personInfo.setResult(false);
							success = false;
						} else if ("success".equals(text)) {
							personInfo.setResult(true);
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
						} else {
							personInfo.setInfo(parser.nextText());
						}
					} else if ("tsxx".equals(parser.getName())) {
						// 提示信息
						personInfo.setInfo(parser.nextText());
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = true;
						personInfo.setHasCardInfo(true);
					} else if ("isPass".equals(parser.getName())) {
						// 是否验证通过
						String strIsPass = parser.nextText();
						if ("pass".equals(strIsPass)) {
							personInfo.setSuccessFlag(1);
						} else {
							personInfo.setSuccessFlag(2);
						}

					} else if ("xm".equals(parser.getName())) {
						// 姓名
						if (zjxx) {
							personInfo.setName(parser.nextText());
						}
					} else if ("xb".equals(parser.getName())) {
						// 性别
						if (zjxx) {
							personInfo.setSex(parser.nextText());
						}
					} else if ("zw".equals(parser.getName())) {
						// 职务
						if (zjxx) {
							personInfo.setOffice(parser.nextText());
						}
					} else if ("gj".equals(parser.getName())) {
						// 国籍
						if (zjxx) {
							personInfo.setCountry(parser.nextText());
						}
					} else if ("csrq".equals(parser.getName())) {
						// 出生日期
						if (zjxx) {
							personInfo.setBirthday(parser.nextText());
						}
					} else if ("ryid".equals(parser.getName())) {
						// 人员id
						if (zjxx) {
							personInfo.setRyid(parser.nextText());
						}
					} else if ("zjhm".equals(parser.getName())) {
						// 证件号码
						if (zjxx) {
							personInfo.setCardnumber(parser.nextText());
						}

					} else if ("zjlx".equals(parser.getName())) {
						// 证件类型
						if (zjxx) {
							personInfo.setCardtype(parser.nextText());
						}
					} else if ("ssdw".equals(parser.getName())) {
						// 所属单位
						if (zjxx) {
							personInfo.setUnit(parser.nextText());
						}
					} else if ("icpic".equals(parser.getName())) {
						// 照片信息
						if (zjxx) {
							String icpic_s = parser.nextText();
							if (icpic_s != null && icpic_s.length() > 0) {
								personInfo.setPhoto(Base64.decode(icpic_s));
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = false;
					}
					break;
				}
				type = parser.next();
			}
			return personInfo;
		} catch (Exception e) {
			e.printStackTrace();
			personInfo.setResult(false);
			return personInfo;
		}
	}

}
