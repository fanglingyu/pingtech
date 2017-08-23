package com.pingtech.hgqw.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.os.Environment;
import android.util.Xml;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.entity.BaseInfoElement;
import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.interf.OnHttpResult;
import com.pingtech.hgqw.web.NetWorkManager;

/**
 * 
 * 
 * 
 * 由于程序启动后就需要向平台请求码头泊位区域等数据，因此会立马发起请求， 同时进入主菜单的数据采集页面， 也会发起请求，为了不重复发起请求，采用该类统一管理
 * 同时，该类也管理泊位、码头、区域等数据，以及数据采集界面treeview上显示的内容
 * 
 */
public class BaseInfoData {

	private static final String TAG = "BaseInfoData";

	/** 当前在数据采集界面显示的基础数据信息 */
	public static ArrayList<BaseInfoElement> mBaseInfoDataDisplay = null;

	/** 全部基础数据信息 */
	public static ArrayList<BaseInfoElement> mBaseInfoDataAll = null;

	/** 码头列表 */
	public static ArrayList<BaseInfoElement> mDockList = null;

	/** 泊位列表 */
	public static ArrayList<BaseInfoElement> mBerthList = null;

	/** 区域列表 */
	public static ArrayList<BaseInfoElement> mAreaList = null;

	/** 卡口列表 */
	public static ArrayList<BaseInfoElement> mKkAreaList = null;

	/** 监控区域列表 */
	public static ArrayList<BaseInfoElement> mJkAreaList = null;

	/** 节点id分隔符 */
	public static String TREEVIEW_NODE_SERARATE = "@_@";

	/** 获取基础信息的http type */
	private static final int HTTPREQUEST_TYPE_GET_BASEINFO = 1;

	private static boolean isInit;

	/** 获取基础信息完成后，通过该callback通知上层 */
	private static OnCallBack pCallBack;

	private static boolean sending = false;

	public static void init() {
		if (isInit) {
			return;
		}
		if (mBaseInfoDataDisplay == null) {
			mBaseInfoDataDisplay = new ArrayList<BaseInfoElement>();
		}
		if (mBaseInfoDataAll == null) {
			mBaseInfoDataAll = new ArrayList<BaseInfoElement>();
		}
		if (mDockList == null) {
			mDockList = new ArrayList<BaseInfoElement>();
		}
		if (mBerthList == null) {
			mBerthList = new ArrayList<BaseInfoElement>();
		}
		if (mAreaList == null) {
			mAreaList = new ArrayList<BaseInfoElement>();
		}
		if (mKkAreaList == null) {
			mKkAreaList = new ArrayList<BaseInfoElement>();
		}
		if (mJkAreaList == null) {
			mJkAreaList = new ArrayList<BaseInfoElement>();
		}

		isInit = true;
	}

	public static void destroy() {
		isInit = false;
		mBaseInfoDataDisplay = null;
		mBaseInfoDataAll = null;
		mDockList = null;
		mBerthList = null;
		mAreaList = null;
		mKkAreaList = null;
		mJkAreaList = null;
	}

	/** 发起http请求，获取基础数据信息 */
	public static boolean onRequestBaseInfoData(OnCallBack onCallBack) {
		if (BaseApplication.instent.getWebState()) {
			String str = "getBaseInfo";
			pCallBack = onCallBack;
			if (sending) {
				return true;
			}
			LoginUser loginUser = LoginUser.getCurrentLoginUser();
			if (loginUser == null) {
				Log.i(TAG, "loginUser == null");
				return false;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", loginUser.getUserID()));
			params.add(new BasicNameValuePair("PDACode", SystemSetting.getPDACode()));
			sending = true;
			NetWorkManager.request(new OnHttpResult() {
				/** 处理平台返回的结果 */
				@Override
				public void onHttpResult(String str, int httpRequestType) {
					Log.i(TAG, "onHttpResult:" + (str != null) + "," + httpRequestType);
					if (httpRequestType == HTTPREQUEST_TYPE_GET_BASEINFO) {
						sending = false;
						if (str != null) {
							destroy();
							init();
							if (onParseXMLBaseInfoData(str, true)) {
								try {
									if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
										if (pCallBack != null) {
											pCallBack.onCallBack(true);
											pCallBack = null;
										}
										return;
									}
									String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech";
									File dir = new File(projectDir);
									if (!dir.exists()) {
										dir.mkdir();
									}
									FileWriter writer = new FileWriter(projectDir + File.separator + "baseinfo.xml");
									writer.write(str);
									writer.close();
									if (pCallBack != null) {
										pCallBack.onCallBack(true);
										pCallBack = null;
									}
									return;
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							try {
								if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
									if (pCallBack != null) {
										pCallBack.onCallBack(false);
									}
									return;
								}
								String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech";
								File dir = new File(projectDir);
								if (!dir.exists()) {
									dir.mkdir();
								}
								BufferedReader br = new BufferedReader(new FileReader(projectDir + File.separator + "baseinfo.xml"));
								String line = "";
								StringBuffer buffer = new StringBuffer();
								while ((line = br.readLine()) != null) {
									buffer.append(line);
								}
								br.close();
								String fileContent = buffer.toString();
								destroy();
								init();
								BaseInfoData.onParseXMLBaseInfoData(fileContent, false);
								if (pCallBack != null) {
									pCallBack.onCallBack(false);
									pCallBack = null;
								}
							} catch (IOException e) {
								e.printStackTrace();
								if (pCallBack != null) {
									pCallBack.onCallBack(false);
								}
							}
						} else {
							if (pCallBack != null) {
								pCallBack.onCallBack(false);
								pCallBack = null;
							}
							return;
						}
					}
				}
			}, str, params, HTTPREQUEST_TYPE_GET_BASEINFO);
		} else {
			// 离线直接取本地文件
			onParseXMLBaseInfoDataOnOffline();
		}
		return true;
	}

	/** 解析返回的基础数据 */
	public static boolean onParseXMLBaseInfoData(String str, boolean result) {
		// TODO Auto-generated method stub
		boolean success = false;
		BaseInfoElement pdfOutlineElement;
		String mt = null;
		int CurTag = 0;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new ByteArrayInputStream(str.getBytes()), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			String temp_str;
			// httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (success) {
							CurTag = 1;
						} else {
							// httpReturnXMLInfo = parser.nextText();
						}
					} else {
						switch (CurTag) {
						case 0:
							if ("cheliang".equals(parser.getName())) {
								CurTag = 2;
							} else if ("chuanbo".equals(parser.getName())) {
								CurTag = 3;
							} else if ("kk".equals(parser.getName())) {
								CurTag = 6;
							} else if ("sxt".equals(parser.getName())) {
								CurTag = 7;
							} else if ("jcxx".equals(parser.getName())) {
								CurTag = 8;
							} else if ("ka".equals(parser.getName())) {
								CurTag = 9;
							} else if ("mt".equals(parser.getName())) {
								CurTag = 10;
							} else if ("bw".equals(parser.getName())) {
								CurTag = 11;
							} else if ("qy".equals(parser.getName())) {
								CurTag = 12;
							} else if ("ft".equals(parser.getName())) {
								CurTag = 13;
							} else if ("md".equals(parser.getName())) {
								CurTag = 14;
							} else if ("dlbzxx".equals(parser.getName())) {
								CurTag = 17;
							} else if ("jkqy".equals(parser.getName())) {
								CurTag = 18;
							}
							break;
						case 1:
							temp_str = parser.getName();
							if (temp_str.equals("cheliang") || temp_str.equals("chuanbo") || temp_str.equals("sxt") || temp_str.equals("jcxx")) {
								pdfOutlineElement = new BaseInfoElement(temp_str, "", parser.nextText(), false, true, "", 0, false, null);
								mBaseInfoDataDisplay.add(pdfOutlineElement);
								mBaseInfoDataAll.add(pdfOutlineElement);
							}
							break;
						case 2:
							String ids2 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("cheliang" + TREEVIEW_NODE_SERARATE + ids2, "cheliang", parser.nextText(), true,
									false, "cheliang", 1, false, ids2);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 3:
							String ids3 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("chuanbo" + TREEVIEW_NODE_SERARATE + ids3, "chuanbo", parser.nextText(), true,
									false, "chuanbo", 1, false, ids3);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 4:
							pdfOutlineElement = new BaseInfoElement("shebei" + TREEVIEW_NODE_SERARATE + parser.getName(), "shebei",
									parser.nextText(), true, true, "shebei", 1, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 5:
							String ids5 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("shebei" + TREEVIEW_NODE_SERARATE + "tk" + TREEVIEW_NODE_SERARATE + ids5, "tk",
									parser.nextText(), true, false, "shebei" + TREEVIEW_NODE_SERARATE + "tk", 2, false, ids5);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 6:
							String ids6 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement(ids6, "kk", parser.nextText(), true, false, "kk", 2, false, ids6);
							mKkAreaList.add(pdfOutlineElement);
							break;
						case 7:
							String ids7 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("sxt" + TREEVIEW_NODE_SERARATE + ids7, "sxt", parser.nextText(), true, false,
									"sxt", 1, false, ids7);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 8:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + parser.getName(), "jcxx", parser.nextText(),
									true, true, "jcxx", 1, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 9:
							String ids9 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "ka" + TREEVIEW_NODE_SERARATE + ids9, "ka",
									parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "ka", 2, false, ids9);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 10:
							String ids10 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "mt" + TREEVIEW_NODE_SERARATE + ids10, "mt",
									parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "mt", 2, false, ids10);
							mBaseInfoDataAll.add(pdfOutlineElement);
							mDockList.add(pdfOutlineElement);
							break;
						case 11:
							mt = parser.getName().substring(1);
							CurTag = 255;
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "bw" + TREEVIEW_NODE_SERARATE + mt, "bw", null,
									true, true, "jcxx" + TREEVIEW_NODE_SERARATE + "bw", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 255:
							String ids255 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "bw" + TREEVIEW_NODE_SERARATE + mt
									+ TREEVIEW_NODE_SERARATE + ids255, "bw", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "bw"
									+ TREEVIEW_NODE_SERARATE + mt, 3, false, ids255);
							mBaseInfoDataAll.add(pdfOutlineElement);
							mBerthList.add(pdfOutlineElement);
							break;
						case 12:
							String ids12 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "qy" + TREEVIEW_NODE_SERARATE + ids12, "qy",
									parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "qy", 2, false, ids12);
							mBaseInfoDataAll.add(pdfOutlineElement);
							mAreaList.add(pdfOutlineElement);
							break;
						case 13:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "ft" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "ft", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "ft", 2,
									false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 14:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "md" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "md", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "md", 2,
									false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 15:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "swfwdw" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "swfwdw", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE
									+ "swfwdw", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 16:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "swfwcb" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "swfwcb", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE
									+ "swfwcb", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 17:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "dlbzxx" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "dlbzxx", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE
									+ "dlbzxx", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 18:
							String ids18 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement(ids18, "jkqy", parser.nextText(), true, false, "jkqy", 2, false, ids18);
							mJkAreaList.add(pdfOutlineElement);
							break;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						// 信息
						if (success) {
							CurTag = 0;
						}
					} else if (CurTag == 2 && "cheliang".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 3 && "chuanbo".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 4 && "shebei".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 5 && "tk".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 6 && "kk".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 7 && "sxt".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 8 && "jcxx".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 9 && "ka".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 10 && "mt".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 11 && "bw".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 12 && "qy".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 13 && "ft".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 14 && "md".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 15 && "swfwdw".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 16 && "swfwcb".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 17 && "dlbzxx".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 18 && "jkqy".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 255 && mt != null && mt.equals(parser.getName().substring(1))) {
						CurTag = 11;
						mt = null;
					}
					break;
				}
				type = parser.next();
			}
			return success;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/** 解析返回的基础数据 */
	public static boolean onParseXMLBaseInfoDataOnOffline() {
		destroy();
		init();
		boolean success = false;
		BaseInfoElement pdfOutlineElement;
		String mt = null;
		int CurTag = 0;
		try {
			if (StringUtils.isEmpty(BaseApplication.instent.getLocalPath())) {
				BaseApplication.instent.initFilePath();
			}
			File file = new File(BaseApplication.instent.getLocalPath() + "baseinfo.xml");
			if (!file.exists()) {
				return false;
			}
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new FileInputStream(file), "utf-8");// 设置解析的数据源
			int type = parser.getEventType();
			String text = null;
			String temp_str;
			// httpReturnXMLInfo = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("result".equals(parser.getName())) {
						text = parser.nextText();
						if ("error".equals(text)) {
							success = false;
						} else if ("success".equals(text)) {
							success = true;
						}
					} else if ("info".equals(parser.getName())) {
						// 信息
						if (success) {
							CurTag = 1;
						} else {
							// httpReturnXMLInfo = parser.nextText();
						}
					} else {
						switch (CurTag) {
						case 0:
							if ("cheliang".equals(parser.getName())) {
								CurTag = 2;
							} else if ("chuanbo".equals(parser.getName())) {
								CurTag = 3;
							} else if ("kk".equals(parser.getName())) {
								CurTag = 6;
							} else if ("sxt".equals(parser.getName())) {
								CurTag = 7;
							} else if ("jcxx".equals(parser.getName())) {
								CurTag = 8;
							} else if ("ka".equals(parser.getName())) {
								CurTag = 9;
							} else if ("mt".equals(parser.getName())) {
								CurTag = 10;
							} else if ("bw".equals(parser.getName())) {
								CurTag = 11;
							} else if ("qy".equals(parser.getName())) {
								CurTag = 12;
							} else if ("ft".equals(parser.getName())) {
								CurTag = 13;
							} else if ("md".equals(parser.getName())) {
								CurTag = 14;
							} else if ("dlbzxx".equals(parser.getName())) {
								CurTag = 17;
							} else if ("jkqy".equals(parser.getName())) {
								CurTag = 18;
							}
							break;
						case 1:
							temp_str = parser.getName();
							if (temp_str.equals("cheliang") || temp_str.equals("chuanbo") || temp_str.equals("sxt") || temp_str.equals("jcxx")) {
								pdfOutlineElement = new BaseInfoElement(temp_str, "", parser.nextText(), false, true, "", 0, false, null);
								mBaseInfoDataDisplay.add(pdfOutlineElement);
								mBaseInfoDataAll.add(pdfOutlineElement);
							}
							break;
						case 2:
							String ids2 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("cheliang" + TREEVIEW_NODE_SERARATE + ids2, "cheliang", parser.nextText(), true,
									false, "cheliang", 1, false, ids2);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 3:
							String ids3 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("chuanbo" + TREEVIEW_NODE_SERARATE + ids3, "chuanbo", parser.nextText(), true,
									false, "chuanbo", 1, false, ids3);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 4:
							pdfOutlineElement = new BaseInfoElement("shebei" + TREEVIEW_NODE_SERARATE + parser.getName(), "shebei",
									parser.nextText(), true, true, "shebei", 1, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 5:
							String ids5 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("shebei" + TREEVIEW_NODE_SERARATE + "tk" + TREEVIEW_NODE_SERARATE + ids5, "tk",
									parser.nextText(), true, false, "shebei" + TREEVIEW_NODE_SERARATE + "tk", 2, false, ids5);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 6:
							String ids6 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement(ids6, "kk", parser.nextText(), true, false, "kk", 2, false, ids6);
							mKkAreaList.add(pdfOutlineElement);
							break;
						case 7:
							String ids7 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("sxt" + TREEVIEW_NODE_SERARATE + ids7, "sxt", parser.nextText(), true, false,
									"sxt", 1, false, ids7);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 8:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + parser.getName(), "jcxx", parser.nextText(),
									true, true, "jcxx", 1, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 9:
							String ids9 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "ka" + TREEVIEW_NODE_SERARATE + ids9, "ka",
									parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "ka", 2, false, ids9);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 10:
							String ids10 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "mt" + TREEVIEW_NODE_SERARATE + ids10, "mt",
									parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "mt", 2, false, ids10);
							mBaseInfoDataAll.add(pdfOutlineElement);
							mDockList.add(pdfOutlineElement);
							break;
						case 11:
							mt = parser.getName().substring(1);
							CurTag = 255;
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "bw" + TREEVIEW_NODE_SERARATE + mt, "bw", null,
									true, true, "jcxx" + TREEVIEW_NODE_SERARATE + "bw", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 255:
							String ids255 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "bw" + TREEVIEW_NODE_SERARATE + mt
									+ TREEVIEW_NODE_SERARATE + ids255, "bw", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "bw"
									+ TREEVIEW_NODE_SERARATE + mt, 3, false, ids255);
							mBaseInfoDataAll.add(pdfOutlineElement);
							mBerthList.add(pdfOutlineElement);
							break;
						case 12:
							String ids12 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "qy" + TREEVIEW_NODE_SERARATE + ids12, "qy",
									parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "qy", 2, false, ids12);
							mBaseInfoDataAll.add(pdfOutlineElement);
							mAreaList.add(pdfOutlineElement);
							break;
						case 13:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "ft" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "ft", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "ft", 2,
									false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 14:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "md" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "md", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE + "md", 2,
									false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 15:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "swfwdw" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "swfwdw", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE
									+ "swfwdw", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 16:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "swfwcb" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "swfwcb", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE
									+ "swfwcb", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 17:
							pdfOutlineElement = new BaseInfoElement("jcxx" + TREEVIEW_NODE_SERARATE + "dlbzxx" + TREEVIEW_NODE_SERARATE
									+ parser.getName().substring(1), "dlbzxx", parser.nextText(), true, false, "jcxx" + TREEVIEW_NODE_SERARATE
									+ "dlbzxx", 2, false, null);
							mBaseInfoDataAll.add(pdfOutlineElement);
							break;
						case 18:
							String ids18 = parser.getName().substring(1);
							pdfOutlineElement = new BaseInfoElement(ids18, "jkqy", parser.nextText(), true, false, "jkqy", 2, false, ids18);
							mJkAreaList.add(pdfOutlineElement);
							break;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("info".equals(parser.getName())) {
						// 信息
						if (success) {
							CurTag = 0;
						}
					} else if (CurTag == 2 && "cheliang".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 3 && "chuanbo".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 4 && "shebei".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 5 && "tk".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 6 && "kk".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 7 && "sxt".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 8 && "jcxx".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 9 && "ka".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 10 && "mt".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 11 && "bw".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 12 && "qy".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 13 && "ft".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 14 && "md".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 15 && "swfwdw".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 16 && "swfwcb".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 17 && "dlbzxx".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 18 && "jkqy".equals(parser.getName())) {
						CurTag = 0;
					} else if (CurTag == 255 && mt != null && mt.equals(parser.getName().substring(1))) {
						CurTag = 11;
						mt = null;
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

	/**
	 * 
	 * 
	 * activity向平台发起基础数据采集请求后，收到平台的返回后通过该接口通知activity
	 * 
	 * @param ret
	 *            请求成功或失败
	 */
	public interface OnCallBack {
		void onCallBack(boolean ret);
	}
}
