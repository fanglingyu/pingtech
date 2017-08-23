package com.pingtech.hgqw.module.police.utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.module.police.entity.MyPolice;
import com.pingtech.hgqw.module.police.entity.Qwzlqwjs;
import com.pingtech.hgqw.module.police.request.RequestPolice;
import com.pingtech.hgqw.module.qwjw.utils.QwzlConstant;
import com.pingtech.hgqw.utils.StringUtils;
import com.pingtech.hgqw.utils.SystemSetting;

public class PullXmlMyPolice {
	private static boolean newtask = false;

	/**
	 * 解析我的警务返回的数据
	 * 
	 * @param what
	 */
	public static boolean onParseXMLDataMyTask(String str, int what) {
		clear(what);

		HashMap<String, Object> map = null;
		boolean isJwzl = false;
		boolean isQwzl = false;
		Object object = null;
		Class clazz = Qwzlqwjs.class;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();

			boolean success = false;
			String text = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("success".equals(text)) {
							success = true;
						} else {
							success = false;
						}
					} else if ("info".equals(parser.getName())) {
						if (success) {
							map = new HashMap<String, Object>();
							object = clazz.newInstance();
						} else {
							// httpReturnXMLInfo = parser.nextText();
						}
					} else if ("jwzl".equals(parser.getName())) {
						isJwzl = true;
						isQwzl = false;
						if (success) {
							if (SystemSetting.taskList == null) {
								SystemSetting.taskList = new ArrayList<Map<String, Object>>();
							}
							SystemSetting.taskList.clear();
						}
					} else if ("qwzl".equals(parser.getName())) {
						isQwzl = true;
						isJwzl = false;
						if (success) {
							if (SystemSetting.qwzlList == null) {
								SystemSetting.qwzlList = new ArrayList<Qwzlqwjs>();
							}
							SystemSetting.qwzlList.clear();
						}
					} else if (isQwzl) {
						qwzlPull(parser, object, clazz);
					} else if ("taskid".equals(parser.getName())) {
						map.put("taskid", parser.nextText());
					} else if ("jqlb".equals(parser.getName())) {
						map.put("jqlb", parser.nextText());
					} else if ("cjfzr".equals(parser.getName())) {
						map.put("cjfzr", parser.nextText());
					} else if ("pzr".equals(parser.getName())) {
						map.put("pzr", parser.nextText());
					} else if ("fbr".equals(parser.getName())) {
						map.put("fbr", parser.nextText());
					} else if ("fbsj".equals(parser.getName())) {
						map.put("fbsj", parser.nextText());
					} else if ("jwzldwid".equals(parser.getName())) {
						map.put("jwzldwid", parser.nextText());
					} else if ("dwid".equals(parser.getName())) {
						map.put("dwid", parser.nextText());
					} else if ("xtry".equals(parser.getName())) {
						map.put("xtry", parser.nextText());
					} else if ("zlnr".equals(parser.getName())) {
						map.put("zlnr", parser.nextText());
					} else if ("zlzt".equals(parser.getName())) {
						map.put("zlzt", parser.nextText());
					} else if ("qszt".equals(parser.getName())) {
						map.put("qszt", parser.nextText());
					} else if ("cjzrr".equals(parser.getName())) {
						map.put("cjzrr", parser.nextText());
					} else if ("cjlx".equals(parser.getName())) {
						map.put("cjlx", parser.nextText());
					}

					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						if (success) {
							if (isJwzl) {
								jwzl(map);
							} else if (isQwzl) {// 勤务
								qwzl(object);

							}
						}
					}
					break;
				}
				type = parser.next();
			}
			return newtask;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void clear(int what) {
		newtask = false;
		if (SystemSetting.taskList != null && RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK == what) {
			SystemSetting.taskList.clear();
		}

		if (SystemSetting.qwzlList != null && RequestPolice.HTTPREQUEST_TYPE_FOR_RECEIVE_MY_TASK_QWZL == what) {
			SystemSetting.qwzlList.clear();
		}
	}

	private static void qwzlPull(XmlPullParser parser, Object object, Class clazz) throws Exception {
		String name = parser.getName();
		String value = null;
		try {
			value = parser.nextText();

		} catch (Exception e) {
		}
		if (value == null) {
			return;
		}
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {

		}
		if (field != null) {
			field.setAccessible(true);
			Type ty = field.getGenericType();
			try {
				if (value != null && !"".equals(value)) {
					if (ty == Boolean.class || ty == boolean.class) {
						field.set(object, Boolean.parseBoolean(value));
					} else if (ty == Byte.class || ty == byte.class) {
						field.set(object, Byte.parseByte(value));
					} else if (ty == Double.class || ty == double.class) {
						field.set(object, Double.parseDouble(value));
					} else if (ty == Float.class || ty == float.class) {
						field.set(object, Float.parseFloat(value));
					} else if (ty == Integer.class || ty == int.class) {
						field.set(object, Integer.parseInt(value));
					} else if (ty == Long.class || ty == long.class) {
						field.set(object, Long.parseLong(value));
					} else if (ty == Short.class || ty == short.class) {
						field.set(object, Short.parseShort(value));
					} else if (ty == Date.class) {
						SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						field.set(object, form.parse(value));
					} else if (ty == String.class) {
						field.set(object, value);
					}
				}
			} catch (Exception e) {

			}

		}
	}

	private static void jwzl(HashMap<String, Object> map) {
		if (map.get("taskid") != null) {
			if (SystemSetting.taskList == null) {
				SystemSetting.taskList = new ArrayList<Map<String, Object>>();
			}
			SystemSetting.taskList.add(map);
			String qszt = (String) map.get("qszt");
			String zlzt = (String) map.get("zlzt");// 0正常 1取消
			if (qszt != null && qszt.equals("0") && !"1".equals(zlzt)) {
				newtask = true;
			}
		}
	}

	private static void qwzl(Object object) {
		Qwzlqwjs qwzlqwjs = (Qwzlqwjs) object;
		String qszt = qwzlqwjs.getQszt();
		String zlzt = qwzlqwjs.getZlzt();
		if (StringUtils.isNotEmpty(qszt) && QwzlConstant.QSZT_WQS.equals(qszt) && !"1".equals(zlzt)) {
			newtask = true;
		}
		if (StringUtils.isNotEmpty(qwzlqwjs.getQwzljbid())) {
			if (SystemSetting.qwzlList == null) {
				SystemSetting.qwzlList = new ArrayList<Qwzlqwjs>();
			}
			SystemSetting.qwzlList.add(qwzlqwjs);
		}
	}

	/** 解析平台返回的警务详情数据 */
	public static MyPolice onParseXMLData(String str) {
		MyPolice myPolice = new MyPolice();
		boolean success = false;
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
						if ("error".equals(text)) {
							myPolice.setResult(false);
							success = false;
						} else if ("success".equals(text)) {
							myPolice.setResult(true);
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (!success) {
							myPolice.setInfo(parser.nextText());
						}
					} else if ("jqlb".equals(parser.getName())) {
						if (success) {
							// 警情级别
							myPolice.setJqlb(parser.nextText());
						}
					} else if ("cjfzr".equals(parser.getName())) {
						if (success) {
							// 处警负责人
							myPolice.setCjfzr(parser.nextText());
						}
					} else if ("pzr".equals(parser.getName())) {
						if (success) {
							// 批准人
							myPolice.setPzr(parser.nextText());
						}
					} else if ("fbr".equals(parser.getName())) {
						if (success) {
							// 发布人
							myPolice.setFbr(parser.nextText());
						}
					} else if ("fbsj".equals(parser.getName())) {
						if (success) {
							// 发布时间
							myPolice.setFbsj(parser.nextText());
						}
					} else if ("zlnr".equals(parser.getName())) {
						if (success) {
							// 指令内容
							myPolice.setZlnr(parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myPolice;
	}
}
