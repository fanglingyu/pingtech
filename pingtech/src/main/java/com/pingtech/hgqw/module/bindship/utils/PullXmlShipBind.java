package com.pingtech.hgqw.module.bindship.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.module.bindship.entity.ShipBindInfo;
import com.pingtech.hgqw.utils.SystemSetting;

public class PullXmlShipBind {
	public static final String FROM_BINDLIST = "bindlist";
	/** 解析平台返回的数据 */
	public static ShipBindInfo onParseXMLData(String str,String cardNumber) {
		ShipBindInfo shipBindInfo =new ShipBindInfo (); 
		HashMap<String, Object> map = null;
		boolean success = false;
		ArrayList<HashMap<String, Object>> shipInfoList = new ArrayList<HashMap<String, Object>>();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("success".equals(text)) {
							shipBindInfo.setResult(true);
							success = true;
							SystemSetting.setShipInfoList(new ArrayList<HashMap<String, Object>>());
						} else {
							shipBindInfo.setResult(false);
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							map = new HashMap<String, Object>();
						} else {
							shipBindInfo.setInfo(parser.nextText());
						}
					} else if ("hc".equals(parser.getName())) {
						map.put("hc", parser.nextText());
					} else if ("cbzwm".equals(parser.getName())) {
						map.put("cbzwm", parser.nextText());
					} else if ("cbywm".equals(parser.getName())) {
						map.put("cbywm", parser.nextText());
					} else if ("gj".equals(parser.getName())) {
						map.put("gj", parser.nextText());
					} else if ("cbxz".equals(parser.getName())) {
						map.put("cbxz", parser.nextText());
					} else if ("tkwz".equals(parser.getName())) {
						map.put("tkwz", parser.nextText());
					} else if ("tkmt".equals(parser.getName())) {
						map.put("tkmt", parser.nextText());
					} else if ("tkbw".equals(parser.getName())) {
						map.put("tkbw", parser.nextText());
					} else if ("bdzt".equals(parser.getName())) {
						String bdzt = parser.nextText();
						if (cardNumber.equals(FROM_BINDLIST)) {
							map.put("bdzt", "未绑定");
						} else {
							if (map.get("bdzt") == null) {
								map.put("bdzt", bdzt);
							}
						}
					} else if ("cardNumber".equals(parser.getName())) {
						map.put("cardNumber", parser.nextText());
					} else if ("id".equals(parser.getName())) {
						map.put("id", parser.nextText());
					} else if ("kkmc".equals(parser.getName())) {
						map.put("kkmc", parser.nextText());
					} else if ("kkfw".equals(parser.getName())) {
						map.put("kkfw", parser.nextText());
					} else if ("kkxx".equals(parser.getName())) {
						map.put("kkxx", parser.nextText());
					} else if ("kacbqkid".equals(parser.getName())) {
						map.put("kacbqkid", parser.nextText());
					} else if ("source".equals(parser.getName())) {
						if (cardNumber.equals(FROM_BINDLIST)) {
							map.put("source", parser.nextText());
						}
					} else if ("kacbzt".equals(parser.getName())) {
						String kacbztstr = parser.nextText();
						if (kacbztstr.equals("0")) {
							map.put("kacbzt", "预到港");
						} else if (kacbztstr.equals("1")) {
							map.put("kacbzt", "在港");
						} else if (kacbztstr.equals("2")) {
							map.put("kacbzt", "预离港");
						} else if (kacbztstr.equals("3")) {
							map.put("kacbzt", "离港");
						} else {
							map.put("kacbzt", kacbztstr);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (cardNumber.equals(FROM_BINDLIST)) {
								String hc = (String) map.get("hc");
								String source = (String) map.get("source");
								if (hc != null
										&& source != null
										&& !source
												.equals(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN
														+ "")) {
									if (shipInfoList == null) {
										shipInfoList = new ArrayList<HashMap<String, Object>>();
										shipInfoList.add(map);
									} else {
										boolean repeat = false;
										int tempcount = shipInfoList.size();
										for (int i = 0; i < tempcount; i++) {
											if (hc.equals(shipInfoList.get(i)
													.get("hc"))) {
												repeat = true;
												break;
											}
										}
										if (!repeat) {
											shipInfoList.add(map);
										}
									}
								}
							} else {
								if (map.get("hc") != null
										|| map.get("id") != null) {
									if (shipInfoList == null) {
										shipInfoList = new ArrayList<HashMap<String, Object>>();
									}
									shipInfoList.add(map);
								}
							}
						}
					}
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			shipBindInfo.setResult(false);
		}
		SystemSetting.setShipInfoList(shipInfoList);
		shipBindInfo.setCount(shipInfoList.size());
		return shipBindInfo;
	}
}
