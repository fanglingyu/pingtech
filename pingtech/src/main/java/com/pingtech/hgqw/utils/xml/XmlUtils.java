package com.pingtech.hgqw.utils.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.pingtech.hgqw.utils.Log;
/***
 * 
 *
 * 类描述：XML封装工具类
 *
 * <p> Title: 江海港边检勤务-移动管理系统-XmlUtils.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  娄高伟 
 * @version 1.0
 * @date  2013-10-9 上午8:47:39
 */
public class XmlUtils {
	private static final String TAG="XmlUtils";
	/**
	 * 
	 * @方法名：buildXml
	 * @功能说明：封装返回系统同步数据
	 * @author 娄高伟
	 * @date  2013-10-9 上午8:46:21
	 * @param objects
	 * @return
	 */
	public static String buildXml(Object[] objects) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(out, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "datas");
			buildDatas(serializer, objects);
			serializer.endTag(null, "datas");
			serializer.endDocument();
			out.flush();
			out.close();
		} catch (IllegalArgumentException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalStateException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}
		return out.toString();
	}
	/**
	 * 
	 * @方法名：buildXml
	 * @功能说明：封装临时存在本地XML数据
	 * @author 娄高伟
	 * @date  2013-10-9 上午8:46:55
	 * @param map
	 * @param czgn
	 * @return
	 * @throws Exception
	 */
	public static String buildXml(Map map) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(out, "UTF-8");
			serializer.startTag(null, "info");
			Set<Map.Entry<String, Object>> set = map.entrySet();
			for (Iterator<Map.Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
				Map.Entry<String, Object> entity = iterator.next();
				String key = entity.getKey();
				Object value = entity.getValue();
				if (value != null && !"".equals(value)) {
					serializer.startTag(null, key);
					serializer.text(value+"");
					serializer.endTag(null, key);
				} else {
					serializer.startTag(null, key);
					serializer.text("");
					serializer.endTag(null, key);
				}
			}
			serializer.endTag(null, "info");
			serializer.endDocument();
			out.flush();
			out.close();
		} catch (IllegalArgumentException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalStateException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}
		return out.toString();
	}
	/**
	 * 
	 * @方法名：buildXml
	 * @功能说明：实体类封装临时存在本地ＸＭＬ
	 * @author 娄高伟
	 * @date  2013-10-9 上午10:15:07
	 * @param obj
	 * @return
	 */
	public static String buildAsXml(Object obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(out, "UTF-8");
			serializer.startTag(null, "info");
			Class c = obj.getClass();
			String methodName = "";
			String fieldName = "";
			Method m[] = c.getMethods();// 此方法可获得其子类父类以及超类的全部方法
			for (int i = 0; i < m.length; i++) {
				methodName = m[i].getName();
				if (m[i].toString().endsWith("()")) {
					Object value = null;
					if (methodName.indexOf("get") == 0 && !"getClass".equals(methodName)) {
						fieldName = methodName.substring(3).toLowerCase();
						try {
							value = m[i].invoke(obj, new Object[0]);
						} catch (Exception e) {
							Log.log2File(TAG, "error:" + e.getMessage());
						}
						if (value instanceof java.util.Date && !"".equals(value)) {
							if (value != null) {
								value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(value);
							}
						}
						serializer.startTag(null, fieldName);
						if (value != null && !"".equals(value)) {
							serializer.text(value + "");
						} else {
							serializer.text("");
						}
						serializer.endTag(null, fieldName);
					} else if (methodName.indexOf("is") == 0) {
						fieldName = methodName.substring(2).toLowerCase();
						try {
							value = m[i].invoke(obj, new Object[0]);
						} catch (Exception e) {
							Log.log2File(TAG, "error:" + e.getMessage());
						}
						serializer.startTag(null, fieldName);
						if (value != null && !"".equals(value)) {
							serializer.text(value + "");
						} else {
							serializer.text(false + "");
						}
						serializer.endTag(null, fieldName);
					}
				}
			}
			serializer.endTag(null, "info");
			serializer.endDocument();
			out.flush();
			out.close();
		} catch (IllegalArgumentException e1) {
			Log.log2File(TAG, "error:" + e1.getMessage());
		} catch (IllegalStateException e1) {
			Log.log2File(TAG, "error:" + e1.getMessage());
		} catch (IOException e1) {
			Log.log2File(TAG, "error:" + e1.getMessage());
		}
		return out.toString();
	}
	/**
	 * 
	 * @方法名：buildCursor
	 * @功能说明：实体类封装成MAP
	 * @author 娄高伟
	 * @date  2013-10-11 上午10:56:30
	 * @param obj
	 * @return
	 */
	public static Map<String, String> buildCursor(Object obj) {
		Map<String, String> fieldsMap = new HashMap<String, String>();
		Class c = obj.getClass();
		String methodName = "";
		String fieldName = "";
		Method m[] = c.getMethods();// 此方法可获得其子类父类以及超类的全部方法
		for (int i = 0; i < m.length; i++) {
			methodName = m[i].getName();
			if (m[i].toString().endsWith("()")) {
				Object value = null;
				if (methodName.indexOf("get") == 0 && !"getClass".equals(methodName)) {
					fieldName = methodName.substring(3).toLowerCase();
					try {
						value = m[i].invoke(obj, new Object[0]);
					} catch (Exception e) {
						Log.log2File(TAG, "error:" + e.getMessage());
					}
					if (value instanceof java.util.Date && !"".equals(value)) {
						if (value != null) {
							value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(value);
						}
					}
					if (value != null && !"".equals(value)) {
						fieldsMap.put(fieldName, value + "");
					} else {
						fieldsMap.put(fieldName, "");
					}
				} else if (methodName.indexOf("is") == 0) {
					fieldName = methodName.substring(2).toLowerCase();
					try {
						value = m[i].invoke(obj, new Object[0]);
					} catch (Exception e) {
						Log.log2File(TAG, "error:" + e.getMessage());
					}
					if (value != null && !"".equals(value)) {
						fieldsMap.put(fieldName, value + "");
					} else {
						fieldsMap.put(fieldName, "");
					}
				}
			}
		}

		return fieldsMap;
	}

		/**
		 * 
		 * @方法名：buildDatas
		 * @功能说明：使用递归动态组装xml参数
		 * @author 娄高伟
		 * @date  2013-10-9 上午8:47:24
		 * @param serializer
		 * @param objects
		 * @throws IllegalArgumentException
		 * @throws IllegalStateException
		 * @throws IOException
		 */
	private static void buildDatas(XmlSerializer serializer, Object[] objects) throws IllegalArgumentException, IllegalStateException, IOException {
		if (objects != null && objects.length > 1) {
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] != null) {
					Object[] o = (Object[]) objects[i];
					if (o != null && o.length > 1) {
						if (o[1] == null) {
							serializer.startTag(null, o[0].toString());
							serializer.text("");
							serializer.endTag(null, o[0].toString());
						} else if (o[1] instanceof String) {
							serializer.startTag(null, o[0].toString());
							serializer.text(o[1].toString());
							serializer.endTag(null, o[0].toString());
						} else if (o[1] instanceof HashBuild) {
							serializer.startTag(null, o[0].toString());
							buildDatas(serializer, ((HashBuild) o[1]).get());
							serializer.endTag(null, o[0].toString());
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @方法名：buildXmlForOffLineShip
	 * @功能说明：封装临时存在本地XML数据
	 * @author zhaotf
	 * @date  2013-10-9 上午8:46:55
	 * @param map
	 * @param result 传值 success 或 error
	 * @return
	 * @throws Exception
	 */
	public static String buildXmlForOffLineShip(Map map,String result) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(out, "UTF-8");
			serializer.startTag(null, "result");
			serializer.text(result);
			serializer.endTag(null, "result");
			serializer.startTag(null, "info");
			Set<Map.Entry<String, Object>> set = map.entrySet();
			for (Iterator<Map.Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
				Map.Entry<String, Object> entity = iterator.next();
				String key = entity.getKey();
				Object value = entity.getValue();
				if (value != null && !"".equals(value)) {
					serializer.startTag(null, key);
					serializer.text(value+"");
					serializer.endTag(null, key);
				} else {
					serializer.startTag(null, key);
					serializer.text("");
					serializer.endTag(null, key);
				}
			}
			serializer.endTag(null, "info");
			serializer.endDocument();
			out.flush();
			out.close();
		} catch (IllegalArgumentException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IllegalStateException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		} catch (IOException e) {
			Log.log2File(TAG, "error:" + e.getMessage());
		}
		return out.toString();
	}
}
