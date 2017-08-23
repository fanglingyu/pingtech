package com.pingtech.hgqw.module.kakou.utils;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kobjects.base64.Base64;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;

import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.entity.Clzjxx;
import com.pingtech.hgqw.module.tikou.entity.PersonInfo;
import com.pingtech.hgqw.utils.StringUtils;

public class PullXmlUtil {
	private static CardInfo cardInfo = null;
	private static 	boolean zjxx  = false;
	public static CardInfo pullXmlInspectForKkCl(String str) {
		cardInfo = null;
		zjxx  = false;
		if (StringUtils.isEmpty(str)) {
			return null;
		}

		try {
			XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
			xmlPullParser.setInput(new StringReader(str));
			int eventType = xmlPullParser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					inspectForKkClStartTag(xmlPullParser);
					break;
				case XmlPullParser.END_TAG:
					// inspectForKkClEndTag(xmlPullParser, cardInfo);
					break;
				case XmlPullParser.END_DOCUMENT:

					break;
				default:
					break;
				}

				eventType = xmlPullParser.next();

			}
		} catch (Exception e) {
			e.printStackTrace();
			cardInfo = null;
		}
		return cardInfo;
	}

	private static void inspectForKkClStartTag(XmlPullParser xmlPullParser) {
		try {
			if (cardInfo == null) {
				cardInfo = new CardInfo();
				cardInfo.setPersonInfo(new PersonInfo());
			}
			if ("datas".equals(xmlPullParser.getName())) {
			} else if ("result".equals(xmlPullParser.getName())) {
				String result = xmlPullParser.nextText();
				cardInfo.setResult("success".equals(result));
			} else if ("tsxx".equals(xmlPullParser.getName())) {
				cardInfo.setTsxx(xmlPullParser.nextText());
			} else if ("fx".equals(xmlPullParser.getName())) {
				cardInfo.setFx(xmlPullParser.nextText());
			}  else if ("txjlid".equals(xmlPullParser.getName())) {
				String txjlid = xmlPullParser.nextText();
				cardInfo.setTxjlid(txjlid);
				
			} else if ("xcxsid".equals(xmlPullParser.getName())) {
				String xcxsid = xmlPullParser.nextText();
				cardInfo.setXcxsid(xcxsid);
			}else if ("isPass".equals(xmlPullParser.getName())) {
				String isPass = xmlPullParser.nextText();
				cardInfo.setPass(StringUtils.isNotEmpty(isPass) && "pass".equals(isPass));
			} else if ("info".equals(xmlPullParser.getName())) {
				if (!cardInfo.isResult()) {
					cardInfo.setInfo(xmlPullParser.nextText());
				}
			} else if ("zjxx".equals(xmlPullParser.getName())) {
				zjxx = true;
				if (zjxx) {
					cardInfo.setHasCardInfo(true);
				}
			}else if(zjxx){
				if("icpic".equals(xmlPullParser.getName())){
					String icpic_s = xmlPullParser.nextText();
					if (icpic_s != null && icpic_s.length() > 0) {
						byte[] image = Base64.decode(icpic_s);
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						Bitmap netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
						int height_be = opts.outHeight / 130;
						int width_be = opts.outWidth / 105;
						opts.inSampleSize = height_be > width_be ? height_be : width_be;
						if (opts.inSampleSize <= 0) {
							opts.inSampleSize = 1;
						}
						opts.inJustDecodeBounds = false;
						netWorkImage = BitmapFactory.decodeByteArray(image, 0, image.length, opts);
						cardInfo.getClzjxx().setBitmap(netWorkImage);
					}
				}else{
					buildCardInfo(xmlPullParser, cardInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void buildCardInfo(XmlPullParser xmlPullParser, CardInfo cardInfo) {
		Clzjxx clzjxx = cardInfo.getClzjxx();
		if (clzjxx == null) {
			clzjxx = new Clzjxx();
		}
		try {
			Class clazz = clzjxx.getClass();
			String name = xmlPullParser.getName();
			String value = xmlPullParser.nextText();
			Field field = clazz.getDeclaredField(name);
			if (field != null) {
				field.setAccessible(true);
				Type ty = field.getGenericType();
				if (value != null && !"".equals(value)) {
					if (ty == Boolean.class || ty == boolean.class) {
						field.set(clzjxx, Boolean.parseBoolean(value));
					} else if (ty == Byte.class || ty == byte.class) {
						field.set(clzjxx, Byte.parseByte(value));
					} else if (ty == Double.class || ty == double.class) {
						field.set(clzjxx, Double.parseDouble(value));
					} else if (ty == Float.class || ty == float.class) {
						field.set(clzjxx, Float.parseFloat(value));
					} else if (ty == Integer.class || ty == int.class) {
						field.set(clzjxx, Integer.parseInt(value));
					} else if (ty == Long.class || ty == long.class) {
						field.set(clzjxx, Long.parseLong(value));
					} else if (ty == Short.class || ty == short.class) {
						field.set(clzjxx, Short.parseShort(value));
					} else if (ty == Date.class) {
						SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						field.set(clzjxx, form.parse(value));
					} else if (ty == String.class) {
						field.set(clzjxx, value);
					}
				}
				cardInfo.setClzjxx(clzjxx);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void inspectForKkClEndTag(XmlPullParser xmlPullParser, CardInfo cardInfo) {
		if ("info".equals(xmlPullParser.getName())) {
			// cardInfo = new CardInfo();
		} else if ("result".equals(xmlPullParser.getName())) {

		}
	}
	
	/** 解析保存结果 */
	public boolean pullSendPassInfo(String str) {
		// TODO Auto-generated method stub
		boolean success = false;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String httpReturnXMLInfo = null;
			String txjlidStr = null;
			String xcxsidStr = null;
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
						} else {
							httpReturnXMLInfo = parser.nextText();
						}
					} else if ("txjlid".equals(parser.getName())) {
						txjlidStr = parser.nextText();
					} else if ("xcxsid".equals(parser.getName())) {
						xcxsidStr = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
					}
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
