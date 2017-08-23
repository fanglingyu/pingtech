package com.pingtech.hgqw.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Environment;

import com.pingtech.hgqw.entity.GlobalFlags;

/**
 * 
 * 
 * 类描述：刷新卡口绑定船舶
 * 
 * <p>
 * Title: 江海港边检勤务-移动管理系统-RestoreBindShipInfo.java
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
 * @date 2013-10-22 下午12:14:00
 */
public class RestoreBindShipInfo {
	private static final String XMLFILE = "kakoubindshipinfo.xml";

	/** 从文件读取绑定的船舶信息 */
	public static void restoreBindShipInfo() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			try {
				SAXParser saxParser = spf.newSAXParser();
				File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE);
				if (file.exists()) {
					// 判断卡口是否已经绑定，如果没有绑定则删除文件
					HashMap<String, Object> bindData = SystemSetting.getBindShip(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "");
					if (bindData == null) {
						Log.i("restoreBindShipInfo", "卡口已经解绑，删除卡口船舶的缓存文件。");
						file.delete();
						SystemSetting.setShipOfKK(null);
						return;
					}
					saxParser.parse(file, new BindShipInfoXmlHandler());
				}
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (Exception e) {
				File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE);
				if (file.exists()) {
					file.delete();
					SystemSetting.setShipOfKK(null);
				}
				e.printStackTrace();
			}

		}

	}

	/** 清空所有绑定船舶数据 */
	public static void cleanBindShip() {
		SystemSetting.getShipOfKK();
		if (SystemSetting.getShipOfKK() != null && SystemSetting.getShipOfKK().size() > 0) {
			SystemSetting.setShipOfKK(null);
		}
		String Filename = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE;
		File file = new File(Filename);
		if (file.exists()) {
			file.delete();
		}
	}

	private static class BindShipInfoXmlHandler extends DefaultHandler {
		private String value;

		ArrayList<HashMap<String, Object>> bindShipList = null;

		HashMap<String, Object> BindShipData = null;

		public BindShipInfoXmlHandler() {
			super();
		}

		public void startDocument() throws SAXException {
			bindShipList = new ArrayList<HashMap<String, Object>>();
		}

		public void endDocument() throws SAXException {
			if (bindShipList != null && bindShipList.size() > 0) {
				SystemSetting.setShipOfKK(bindShipList);
			}
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			value = "";
			if (localName.equals("info")) {
				BindShipData = new HashMap<String, Object>();
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (localName.equals("info")) {
				bindShipList.add(BindShipData);
				BindShipData = null;
			} else {
				if (value != null && BindShipData != null) {
					BindShipData.put(localName, value);
				}
			}
		}

		public void characters(char ch[], int start, int length) throws SAXException {
			value += new String(ch, start, length);
		}
	}

}
