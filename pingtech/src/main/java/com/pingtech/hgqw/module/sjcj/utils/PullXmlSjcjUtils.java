package com.pingtech.hgqw.module.sjcj.utils;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.pingtech.hgqw.module.sjcj.entity.BindXxdAndBaseInfo;
import com.pingtech.hgqw.utils.StringUtils;

public class PullXmlSjcjUtils {
	/** 绑定信息钉与基础数据，http请求类型 */
	public static final int HTTPREQUEST_TYPE_BIND_XXD_AND_BASEINFO = 100;

	/**
	 * 
	 * @方法名：parseXMLData
	 * @功能说明：解析指定接口的返回数据
	 * @author liums
	 * @date 2013-11-15 下午2:49:37
	 * @param str
	 * @param httprequest
	 */
	public static Object parseXMLData(String str, int httprequest) {
		if(StringUtils.isEmpty(str)){
			return null;
		}
		
		switch (httprequest) {
		case HTTPREQUEST_TYPE_BIND_XXD_AND_BASEINFO:
			return pullXmlForBindXxdAndBaseInfo(str);
		default:
			break;
		}
		return null;
	}

	private static Object pullXmlForBindXxdAndBaseInfo(String str) {
		BindXxdAndBaseInfo bindXxdAndBaseInfo = new BindXxdAndBaseInfo();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						bindXxdAndBaseInfo.setResult(parser.nextText());
					} else if ("info".equals(parser.getName())) {
						bindXxdAndBaseInfo.setInfo(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bindXxdAndBaseInfo;
	}

}
