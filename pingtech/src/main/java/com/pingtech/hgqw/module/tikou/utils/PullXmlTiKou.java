package com.pingtech.hgqw.module.tikou.utils;

import java.io.ByteArrayInputStream;

import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.module.tikou.entity.PersonInfo;

public class PullXmlTiKou {
	/** 解析刷卡登记平台返回的数据 */
	public static PersonInfo parseXMLData(String str) {
		PersonInfo personInfo = new PersonInfo();
		personInfo.setHasCardInfo(false);
		personInfo.setSuccessFlag(0);
		personInfo.setResult(false);
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = "";
			boolean zjxx = false;
			boolean dkxx = false;
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
					}else if ("isClzj".equals(parser.getName())) {
						String isClzjStr =  parser.nextText();
						personInfo.setClzj("true".equals(isClzjStr));
					} else if ("tsxx".equals(parser.getName())) {
						// 提示信息
						personInfo.setInfo(parser.nextText());
					} else if ("zjxx".equals(parser.getName())) {
						// 证件信息
						zjxx = true;
						personInfo.setHasCardInfo(true);
					} else if ("dkxx".equals(parser.getName())) {
						// 搭靠信息
						dkxx = true;
						personInfo.setHasCardInfo(true);
					} else if ("dkjlid".equals(parser.getName())) {
						// 搭靠记录id
						personInfo.setDkjlid(parser.nextText());
					} else if ("sfdk".equals(parser.getName())) {
						// 是否搭靠
						personInfo.setSfdk(parser.nextText());
					} else if ("txjlid".equals(parser.getName())) {
						// 通行记录id
						personInfo.setTxjlid(parser.nextText());
					} else if ("isPass".equals(parser.getName())) {
						// 是否验证通过
						String strIsPass = parser.nextText();
						if ("pass".equals(strIsPass)) {
							personInfo.setSuccessFlag(1);
						} else {
							personInfo.setSuccessFlag(2);
						}

					} else if ("sxcfx".equals(parser.getName())) {
						// 上下船方向
						personInfo.setSxcfx(parser.nextText());
					} else if ("hgzl".equals(parser.getName())) {
						// 海港证类
						personInfo.setHgzl(parser.nextText());
					} else if ("fx".equals(parser.getName())) {
						// 上下船方向
						personInfo.setFx(parser.nextText());
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
							personInfo.setZjhm(parser.nextText());
						}

					} else if ("zjlx".equals(parser.getName())) {
						// 证件类型
						if (zjxx) {
							personInfo.setCardtype(parser.nextText());
						} else if (dkxx) {
							personInfo.setDkzjlx(parser.nextText());
						}
					} else if ("ssdw".equals(parser.getName())) {
						// 所属单位
						if (zjxx) {
							personInfo.setUnit(parser.nextText());
						} else if (dkxx) {
							personInfo.setDkssdw(parser.nextText());
						}
					} else if ("icpic".equals(parser.getName())) {
						// 照片信息
						if (zjxx) {
							String icpic_s = parser.nextText();
							if (icpic_s != null && icpic_s.length() > 0) {
								personInfo.setPhoto(Base64.decode(icpic_s));
							}
						}
					} else if ("cbmc".equals(parser.getName())) {
						// 船舶名称
						if (dkxx) {
							personInfo.setDkcbmc(parser.nextText());
						}
					} else if ("cgj".equals(parser.getName())) {
						// 船港籍
						if (dkxx) {
							personInfo.setDkcgj(parser.nextText());
						}
					} else if ("zzdw".equals(parser.getName())) {
						// 载重吨位
						if (dkxx) {
							personInfo.setDkzzdw(parser.nextText());
						}
					} else if ("ml".equals(parser.getName())) {
						// 马力
						if (dkxx) {
							personInfo.setDkml(parser.nextText());
						}
					} else if ("yt".equals(parser.getName())) {
						// 用途
						if (dkxx) {
							personInfo.setDkyt(parser.nextText());
						}
					} else if ("dkfw".equals(parser.getName())) {
						// 搭靠船舶（范围）
						if (dkxx) {
							personInfo.setDkdkfw(parser.nextText());
						}
					} else if ("yxq".equals(parser.getName())) {
						// 有效期
						personInfo.setYxq(parser.nextText());
					} else if ("sdcb".equals(parser.getName())) {
						personInfo.setFw( parser.nextText());
					} else if ("bjtsxx".equals(parser.getName())) {
						// 报警提示信息
						personInfo.setPzmbly(parser.nextText());
					} else if ("pzmbly".equals(parser.getName())) {
						// 碰撞目标来源
						personInfo.setPzmbly(parser.nextText());
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
