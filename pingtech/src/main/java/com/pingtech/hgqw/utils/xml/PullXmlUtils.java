package com.pingtech.hgqw.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Xml;

import com.pingtech.R;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringUtils;

/**
 * 
 * 
 * 类描述：解析XML工具类
 * 
 * <p>
 * Title: 江海港边检勤务-移动管理系统-PullXmlUtils.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author 娄高伟
 * @version 1.0
 * @date 2013-10-9 上午10:42:42
 */
public class PullXmlUtils {
	private static final String TAG = "PullXmlUtils";

	/**
	 * 
	 * @方法名：onParseXMLData
	 * @功能说明：解析XML封装实体类不知是那种实体类
	 * @author 娄高伟
	 * @date 2013-10-9 上午10:40:45
	 * @param str
	 * @return
	 */
	public static Map<String, List> onParseXMLData(String str) {
		List<String> fields = new ArrayList<String>();
		Map<String, List> map = new HashMap<String, List>();
		List entitys = new ArrayList();
		Object object = null;
		Class clazz = null;
		String title = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("title".equals(parser.getName())) {
						title = parser.nextText().toLowerCase();
						if ("hgzjxx".equalsIgnoreCase(title)) {
							object = new Hgzjxx();
						} else {
							return null;
						}
						clazz = object.getClass();
						Field[] f = clazz.getDeclaredFields();
						for (int i = 0; i < f.length; i++) {
							fields.add(f[i].getName());
						}
					} else if ("info".equals(parser.getName())) {
						object = clazz.newInstance();
					} else {
						String name = parser.getName();
						if (fields.contains(name)) {
							String value = parser.nextText();
							Field field = null;
							try {
								field = clazz.getDeclaredField(name);
							} catch (NoSuchFieldException e) {
								Log.log2File(TAG, "error:" + e.getMessage());
							}
							if (field != null) {
								field.setAccessible(true);
								Type ty = field.getGenericType();
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

							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						entitys.add(object);
					}
					break;
				}
				type = parser.next();
			}
			map.put(title, entitys);

		} catch (ParseException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalAccessException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (NumberFormatException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (InstantiationException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (XmlPullParserException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}

		return map;

	}

	/**
	 * 
	 * @方法名：parseXMLData
	 * @功能说明：得知是那种实体类解析XML封装实体类
	 * @author 娄高伟
	 * @date 2013-10-9 上午10:42:00
	 * @param clazz
	 * @param str
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static List parseXMLData(Class clazz, String str) throws Exception {
		List<String> fields = new ArrayList<String>();
		List entitys = new ArrayList();
		Object object = null;
		String titleStr = null;
		String operateType = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("title".equals(parser.getName())) {
					titleStr = parser.nextText();
					Field[] f = clazz.getDeclaredFields();
					for (int i = 0; i < f.length; i++) {
						fields.add(f[i].getName());
					}
				} else if ("operateType".equals(parser.getName())) {
					String operate = parser.nextText();
					if (StringUtils.isEmpty(operate) || "D".equals(operate)) {
						operateType = titleStr + "_delSjid";
					} else {
						operateType = titleStr + "_addSjid";
					}

				} else if ("sjid".equals(parser.getName())) {
					SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
							Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					String value = parser.nextText();
					if (value != null && !"".equals(value)) {
						editor.putString(operateType, value);
					}
					editor.commit();
				} else if ("info".equals(parser.getName())) {
					object = clazz.newInstance();
				} else {
					String name = parser.getName();
					if (fields.contains(name)) {
						String value = parser.nextText();
						Field field = null;
						field = clazz.getDeclaredField(name);
						if (field != null) {
							field.setAccessible(true);
							Type ty = field.getGenericType();
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

						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("info".equals(parser.getName())) {
					entitys.add(object);
				}
				break;
			}
			type = parser.next();
		}

		return entitys;

	}

	/**
	 * 
	 * @方法名：parseXMLData
	 * @功能说明：得知是那种实体类解析XML封装实体类
	 * @author 娄高伟
	 * @date 2013-10-9 上午10:42:00
	 * @param clazz
	 * @param str
	 * @return
	 */
	public static List parseXMLData(Class clazz, String str, int flag) {
		List<String> fields = new ArrayList<String>();
		List entitys = new ArrayList();
		Object object = null;
		String titleStr = null;
		String operateType = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("title".equals(parser.getName())) {
						titleStr = parser.nextText();
						Field[] f = clazz.getDeclaredFields();
						for (int i = 0; i < f.length; i++) {
							fields.add(f[i].getName());
						}
					} else if ("operateType".equals(parser.getName())) {
						String operate = parser.nextText();
						if (StringUtils.isEmpty(operate) || "D".equals(operate)) {
							operateType = titleStr + "_delSjid";
						} else {
							operateType = titleStr + "_addSjid";
						}

					} else if ("sjid".equals(parser.getName())) {
						SharedPreferences prefs = BaseApplication.instent.getSharedPreferences(BaseApplication.instent.getString(R.string.app_name),
								Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						String value = parser.nextText();
						if (value != null && !"".equals(value)) {
							editor.putString(operateType, value);
						}
						editor.commit();
					} else if ("info".equals(parser.getName())) {
						object = clazz.newInstance();
					} else {
						String name = parser.getName();
						if (fields.contains(name)) {
							String value = parser.nextText();
							Field field = null;
							try {
								field = clazz.getDeclaredField(name);
							} catch (NoSuchFieldException e) {
								Log.log2File(TAG, "error:" + e.getMessage());
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
									Log.log2File(TAG, "error:" + e.getMessage());
									break;
								}

							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						// 绑定卡口下载的临时证，将卡口id赋值
						// if(object!=null&& (object instanceof Hgzjxx )&&
						// OffLineUtil.OFFLINE_HGZJXX_ADD_RESULT_CODE_FOR_QYXX==flag){
						// ((Hgzjxx)object).setKkid("fromkk");
						// }
						entitys.add(object);
					}
					break;
				}
				type = parser.next();
			}

		} catch (IllegalAccessException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (NumberFormatException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (InstantiationException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (XmlPullParserException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}

		return entitys;

	}

	/**
	 * 
	 * @方法名：parseXMLData
	 * @功能说明：解析单个业务XML
	 * @author 娄高伟
	 * @date 2013-10-14 下午12private static SharedPreferences
	 *       getSharedPreferences(String string, int modePrivate) { // TODO
	 *       Auto-generated method stub return null; } :50:39
	 * @param str
	 * @return
	 */
	public static Map<String, String> parseXMLData(String str) {
		Map<String, String> map = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("info".equals(parser.getName())) {
						map = new HashMap<String, String>();
					} else {
						String name = parser.getName();
						String value = parser.nextText();
						if (value != null && !"".equals(value)) {
							map.put(name, value);
						} else {
							map.put(name, "");
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();

			}
		} catch (XmlPullParserException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}
		return map;

	}

	private static List hgzjxxs = null;

	private static List cyxxs = null;

	private static List kacbqks = null;

	/**
	 * 
	 * @description 绑定船舶或卡口后，获取全部相关信息 —— 获取 证件信息<br>
	 *              注：必须在执行parseXMLDataForAllOfflineData(String str)方法后调用
	 * @return
	 * @date 2014-5-9
	 * @author zhaotf
	 */
	public static List getHgzjxxs() {
		return hgzjxxs;
	}

	/**
	 * 
	 * @description 绑定船舶或卡口后，获取全部相关信息 —— 获取 船员信息<br>
	 *              注：必须在执行parseXMLDataForAllOfflineData(String str)方法后调用
	 * @return
	 * @date 2014-5-9
	 * @author zhaotf
	 */
	public static List getCyxxs() {
		return cyxxs;
	}

	/**
	 * 
	 * @description 绑定船舶后，获取全部相关信息 —— 获取 口岸船舶情况信息<br>
	 *              注：必须在执行parseXMLDataForAllOfflineData(String str)方法后调用
	 * @return
	 * @date 2014-5-9
	 * @author zhaotf
	 */
	public static List getKacbqks() {
		return kacbqks;
	}

	/**
	 * 
	 * @description 绑定船舶后，对 获取的全部相关信息进行解析
	 * @param str
	 * @date 2014-5-9
	 * @author zhaotf
	 */
	public static void parseXMLDataForAllOfflineData(String str) {

		hgzjxxs = new ArrayList<Hgzjxx>();
		cyxxs = new ArrayList<TBCyxx>();
		kacbqks = new ArrayList<Kacbqk>();
		hgzjxxs.clear();
		cyxxs.clear();
		kacbqks.clear();

		if (StringUtils.isEmpty(str)) {
			return;
		}

		Object object = null;
		String className = "";
		Class clazz = null;
		boolean isGoon = false;
		String titleStr = null;
		String operateType = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("title".equals(parser.getName())) {
						titleStr = parser.nextText();
					} else if ("operateType".equals(parser.getName())) {
						String operate = parser.nextText();
						if (StringUtils.isEmpty(operate) || "D".equals(operate)) {
							operateType = titleStr + "_delSjid";
						} else {
							operateType = titleStr + "_addSjid";
						}

					} else if ("info".equals(parser.getName())) {
						object = clazz.newInstance();
						className = clazz.getSimpleName();
						if (className.contains("Cyxx")) {
							className = "Cyxx";
						}
						isGoon = true;
					} else if ("Kacbqk".equals(parser.getName())) {
						clazz = Kacbqk.class;
					} else if ("Cyxx".equals(parser.getName())) {
						clazz = TBCyxx.class;
					} else if ("Hgzjxx".equals(parser.getName())) {
						clazz = Hgzjxx.class;
					} else {
						if (isGoon) {
							String name = parser.getName();
							String value = parser.nextText();
							Field field = null;
							try {
								field = clazz.getDeclaredField(name);
							} catch (NoSuchFieldException e) {
								Log.log2File(TAG, "error:" + e.getMessage());
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
									Log.log2File(TAG, "error:" + e.getMessage());
									break;
								}
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						isGoon = false;
						if ("Kacbqk".equals(className)) {
							kacbqks.add(object);
						} else if ("Cyxx".equals(className)) {
							cyxxs.add(object);
						} else if ("Hgzjxx".equals(className)) {
							hgzjxxs.add(object);
						}
					}
					break;
				}
				type = parser.next();
			}
		} catch (IllegalAccessException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (NumberFormatException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (InstantiationException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (XmlPullParserException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}

	}
}
