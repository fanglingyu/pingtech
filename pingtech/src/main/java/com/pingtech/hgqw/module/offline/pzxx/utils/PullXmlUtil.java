package com.pingtech.hgqw.module.offline.pzxx.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;

import com.pingtech.hgqw.module.offline.pzxx.entity.Pzxx;

@SuppressLint("SimpleDateFormat")
public class PullXmlUtil {

	public static ArrayList<Pzxx> pullXmlToClass(String str) throws Exception {
		ArrayList<Pzxx> pzxxs = new ArrayList<Pzxx>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源

		int type = parser.getEventType();
		Pzxx pzxx = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("info".equals(parser.getName())) {
					pzxx = new Pzxx();
				} else if ("bjtsxx".equals(parser.getName())) {
					pzxx.setBjtsxx(parser.nextText());
				} else if ("pzmbly".equals(parser.getName())) {
					pzxx.setPzmbly(parser.nextText());
				} else if ("zjhm".equals(parser.getName())) {
					pzxx.setZjhm(parser.nextText());
				} else if ("pzjlryId".equals(parser.getName())) {
					pzxx.setPzjlryId(parser.nextText());
				} else if ("xm".equals(parser.getName())) {
					pzxx.setXm(parser.nextText());
				} else if ("dxbs".equals(parser.getName())) {
					pzxx.setDxbs(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("info".equals(parser.getName())) {
					pzxxs.add(pzxx);
					pzxx = null;
				}
				break;
			}
			type = parser.next();
		}
		return pzxxs;
	}

	public static <T> ArrayList<T> pullXml (String str) throws Exception {
		ArrayList<T> pzxxs = new ArrayList<T>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源

		int type = parser.getEventType();
		T object = null;
		Class clazz = Pzxx.class;
		boolean hasData = false;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("info".equals(parser.getName())) {
					object = (T) clazz.newInstance();
					hasData = true;
				} else if (hasData) {
					dataToClass(parser, clazz, object);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("info".equals(parser.getName())) {
					pzxxs.add(object);
					object = null;
				}
				break;
			}
			type = parser.next();
		}
		return pzxxs;
	}

	private static void dataToClass(XmlPullParser parser, Class clazz, Object object) {
		String xmlName = parser.getName();
		String xmlValue = null;
		try {
			xmlValue = parser.nextText();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Field clazzField = null;
		try {
			clazzField = clazz.getDeclaredField(xmlName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (clazzField != null) {
			clazzField.setAccessible(true);
			Type ty = clazzField.getGenericType();
			try {
				if (xmlValue != null && !"".equals(xmlValue)) {
					if (ty == Boolean.class || ty == boolean.class) {
						clazzField.set(object, Boolean.parseBoolean(xmlValue));
					} else if (ty == Byte.class || ty == byte.class) {
						clazzField.set(object, Byte.parseByte(xmlValue));
					} else if (ty == Double.class || ty == double.class) {
						clazzField.set(object, Double.parseDouble(xmlValue));
					} else if (ty == Float.class || ty == float.class) {
						clazzField.set(object, Float.parseFloat(xmlValue));
					} else if (ty == Integer.class || ty == int.class) {
						clazzField.set(object, Integer.parseInt(xmlValue));
					} else if (ty == Long.class || ty == long.class) {
						clazzField.set(object, Long.parseLong(xmlValue));
					} else if (ty == Short.class || ty == short.class) {
						clazzField.set(object, Short.parseShort(xmlValue));
					} else if (ty == Date.class) {
						SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						clazzField.set(object, form.parse(xmlValue));
					} else if (ty == String.class) {
						clazzField.set(object, xmlValue);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
