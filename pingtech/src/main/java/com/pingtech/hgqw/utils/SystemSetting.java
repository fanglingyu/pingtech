package com.pingtech.hgqw.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

import com.pingtech.hgqw.entity.BaseInfoElement;
import com.pingtech.hgqw.entity.GlobalFlags;
import com.pingtech.hgqw.module.police.entity.Qwzlqwjs;

/** 系统用到的一些全局变量、系统设置等 */
public class SystemSetting {
	private static final String XMLFILE = "bindshipinfo.xml";

	/** 我的警务信息 */
	public static ArrayList<Map<String, Object>> taskList = null;
	/**勤务指令 */
	public static ArrayList<Qwzlqwjs> qwzlList = null;

	public static ArrayList<HashMap<String, Object>> shipOfKK = null;

	public static ArrayList<HashMap<String, Object>> shipInfoList = null;

	/** 口岸代码 */
	private static String serverKadm = null;

	/** 服务器ip */
	private static String _serverHost = null;

	/** 服务器端口号 */
	private static String _serverPort = null;

	/** 是否用webservice连接 */
	private static boolean webServiceConnect = true;

	/** webservice命名空间 */
	private static String webServiceNamespace = null;

	/** webservice用户名 */
	private static String webServiceUsername = null;

	/** webservice密码 */
	private static String webServicePassword = null;

	/** webservice code */
	private static String webServiceCode = null;

	/** webservice wsdl url */
	private static String webServiceWsdlUrl = null;

	/** webservice 参数名称1 */
	private static String webServiceArg1 = null;

	/** webservice参数名称2 */
	private static String webServiceArg2 = null;

	/** webservice参数名称3 */
	private static String webServiceArg3 = null;

	/** webservice参数名称4 */
	private static String webServiceArg4 = null;

	/** webservice参数名称5 */
	private static String webServiceArg5 = null;

	/** pda终端编号 */
	private static String pdaCode = null;

	/** 巡查巡检，刷巡检地点时，巡检地点类型 */
	public static String xunJianType = null;

	/** 巡查巡检，刷巡检地点时，巡检地点id */
	public static String xunJianId = null;

	/** 巡查巡检，刷巡检地点时，巡检地点名称 */
	public static String xunJianName = null;

	/** 巡查巡检，刷巡检地点时，巡检地点为泊位时，码头id */
	public static String xunJianMTid = null;

	/** 巡查巡检，刷巡检地点时，巡检地点为泊位时，码头名称 */
	public static String xunJianMTname = null;

	public static String xunJianMtgsgs = null;

	public static String xunJianMtgm = null;

	public static String xunJianKbnl = null;

	public static String xunJianZxhz = null;

	public static String xunJianQyfw = null;

	public static String xunJianXxxx = null;

	public static String xunJianWz = null;

	public static String xunJianZdmbcbdw = null;

	public static String xunJianSs = null;

	public static String xunJianMbdsl = null;

	public static String xunJianZdgkcbdw = null;

	public static String xunJianMtzax = null;

	public static String xunJianQylx = null;

	public static String readcardhc = null;

	/**
	 * 语音对讲
	 */
	public static boolean yydjOnOrOff;

	/** 绑定船舶列表 */
	private static ArrayList<HashMap<String, Object>> bindShipList = null;

	/** GPS上报时间间隔 */
	private static int mGPSTimer = 0;

	/** 国家列表 */
	private static List<String> countryList = null;

	/** 是否已经初始化完毕 */
	public static boolean isInit = false;

	/** VPN未启动 */
	public static final int START_VPNSERVICE_RESULT_NONE = -1;

	public static int mVPNStatus = START_VPNSERVICE_RESULT_NONE;

	public static boolean isYydjOnOrOff() {
		return yydjOnOrOff;
	}

	public static void setYydjOnOrOff(boolean yydjOnOrOff) {
		SystemSetting.yydjOnOrOff = yydjOnOrOff;
	}

	public static String getServerKadm() {
		return serverKadm;
	}

	public static void setServerKadm(String serverKadm) {
		SystemSetting.serverKadm = serverKadm;
	}

	public static String getServerHost() {
		return _serverHost;

	}

	public static void setServerHost(String host) {
		_serverHost = host;
	}

	public static String getServerPort() {
		return _serverPort;

	}

	public static void setServerPort(String port) {
		_serverPort = port;
	}

	public static boolean getWebServiceConnect() {
		return webServiceConnect;

	}

	public static void setWebServiceConnect(boolean connect) {
		webServiceConnect = connect;
	}

	public static String getWebServiceNamespace() {
		return webServiceNamespace;

	}

	public static void setWebServiceNamespace(String namespace) {
		webServiceNamespace = namespace;
	}

	public static String getWebServiceUserName() {
		return webServiceUsername;

	}

	public static void setWebServiceUserName(String username) {
		webServiceUsername = username;
	}

	public static String getWebServicePassword() {
		return webServicePassword;

	}

	public static void setWebServicePassword(String password) {
		webServicePassword = password;
	}

	public static String getWebServiceCode() {
		return webServiceCode;

	}

	public static void setWebServiceCode(String code) {
		webServiceCode = code;
	}

	public static String getWebServiceWSDLUrl() {
		return webServiceWsdlUrl;

	}

	public static void setWebServiceWSDLUrl(String url) {
		webServiceWsdlUrl = url;
	}

	public static String getWebServiceArg1() {
		return webServiceArg1;

	}

	public static void setWebServiceArg1(String arg1) {
		webServiceArg1 = arg1;
	}

	public static String getWebServiceArg2() {
		return webServiceArg2;

	}

	public static void setWebServiceArg2(String arg2) {
		webServiceArg2 = arg2;
	}

	public static String getWebServiceArg3() {
		return webServiceArg3;

	}

	public static void setWebServiceArg3(String arg3) {
		webServiceArg3 = arg3;
	}

	public static String getWebServiceArg4() {
		return webServiceArg4;

	}

	public static void setWebServiceArg4(String arg4) {
		webServiceArg4 = arg4;
	}

	public static String getWebServiceArg5() {
		return webServiceArg5;
	}

	public static void setWebServiceArg5(String webServiceArg5) {
		SystemSetting.webServiceArg5 = webServiceArg5;
	}

	/** 清空所有绑定船舶数据 */
	public static void cleanBindShip() {
		if (bindShipList == null) {
			return;
		}
		bindShipList = new ArrayList<HashMap<String, Object>>();
		String Filename = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE;
		File file = new File(Filename);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 加入一个绑定船舶数据，由于一个模块只能绑定一只船，如果存在相同数据，就先删除
	 * 
	 * @param map
	 *            船舶信息
	 * @param 来自哪个模块
	 * */
	public static void setBindShip(HashMap<String, Object> map, String source) {
		if (bindShipList == null) {
			bindShipList = new ArrayList<HashMap<String, Object>>();
		}
		int size = bindShipList.size();
		for (int i = 0; i < size; i++) {
			Map<String, Object> old_map = bindShipList.get(i);
			if (source.equals((String) old_map.get("source"))) {
				bindShipList.remove(i);
				break;
			}
		}
		if (map != null) {
			map.put("source", source);
			bindShipList.add(map);
		}
		size = bindShipList.size();
		if (size > 0) {
			saveBindShipInfo();
		} else {
			String Filename = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE;
			File file = new File(Filename);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/** 获取绑定船舶数，但不包括卡口管理和巡查巡检，用于判断是否存在快速绑定条件 */
	public static int getBindShipAllSize(int from) {
		if (bindShipList == null) {
			return 0;
		}
		int count = 0;
		int size = bindShipList.size();
		for (int i = 0; i < size; i++) {
			HashMap<String, Object> old_map = bindShipList.get(i);
			if (!((String) old_map.get("source")).equals(GlobalFlags.LIST_TYPE_FROM_KAKOUMANAGER + "")
					&& !((String) old_map.get("source")).equals(GlobalFlags.LIST_TYPE_FROM_XUNCHAXUNJIAN + "")) {
				if (GlobalFlags.LIST_TYPE_FROM_TIKOUMANAGER == from) {
					if ("预到港".equals(old_map.get("kacbzt"))) {
						continue;
					}
				}
				count++;
			}
		}
		return count;
	}

	/**
	 * 从列表中获取绑定的船舶信息
	 * 
	 * @param source
	 *            模块代号，船舶动态？梯口管理？卡口管理？巡查巡检？
	 * @return 返回船舶信息
	 * 
	 * */
	public static HashMap<String, Object> getBindShip(String source) {
		if (bindShipList == null) {
			return null;
		}
		int size = bindShipList.size();
		for (int i = 0; i < size; i++) {
			HashMap<String, Object> old_map = bindShipList.get(i);
			if (source.equals((String) old_map.get("source"))) {
				return old_map;
			}
		}
		return null;
	}

	/**
	 * 从列表中获取绑定的船舶航次号
	 * 
	 * @param source
	 *            模块代号，船舶动态？梯口管理？卡口管理？巡查巡检？
	 * @return 返回船舶航次号
	 * 
	 * */
	public static String getVoyageNumber(String source) {
		if (bindShipList == null) {
			return null;
		}
		int size = bindShipList.size();
		for (int i = 0; i < size; i++) {
			Map<String, Object> old_map = bindShipList.get(i);
			if (source.equals((String) old_map.get("source"))) {
				if ((String) old_map.get("hc") == null) {
					return (String) old_map.get("id");
				} else {
					return (String) old_map.get("hc");
				}
			}
		}
		return null;
	}

	/**
	 * 从列表中获取绑定的船舶名称
	 * 
	 * @param source
	 *            模块代号，船舶动态？梯口管理？卡口管理？巡查巡检？
	 * @return 返回船舶名称
	 * 
	 * */
	public static String getBindShipName(String source) {
		if (bindShipList == null) {
			return null;
		}
		int size = bindShipList.size();
		for (int i = 0; i < size; i++) {
			Map<String, Object> old_map = bindShipList.get(i);
			if (source.equals((String) old_map.get("source"))) {
				return (String) old_map.get("cbzwm");
			}
		}
		return null;
	}

	public static String getPDACode() {
		return pdaCode;

	}

	public static void setPDACode(String code) {
		pdaCode = code;
	}

	public static int getGPSTimer() {
		return mGPSTimer;

	}

	public static void setGPSTimer(int timer) {
		mGPSTimer = timer;
	}

	public static List<String> getCountryList() {
		return countryList;
	}

	/**
	 * 获取基础信息中码头列表
	 * 
	 * 
	 * */
	public static List<String> getBaseInfoDockList() {
		List<String> list = new ArrayList<String>();
		if (BaseInfoData.mDockList == null || BaseInfoData.mDockList.size() == 0) {
			return list;
		}
		int i;
		int count = BaseInfoData.mDockList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mDockList.get(i);
			list.add(temp.getOutlineTitle());
		}
		return list;
	}

	/**
	 * 获取基础信息中泊位信息
	 * 
	 * @param dockid
	 *            码头id
	 * @param index
	 *            泊位索引
	 * 
	 * @return 泊位id
	 * 
	 * */
	public static String getBaseInfoBerthId(String dockid, int index) {
		int i;
		int count = BaseInfoData.mBerthList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mBerthList.get(i);
			if (temp.getParent().equals("jcxx" + BaseInfoData.TREEVIEW_NODE_SERARATE + "bw" + BaseInfoData.TREEVIEW_NODE_SERARATE + dockid)) {
				if (index == 0) {
					return temp.getId().substring(temp.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length());
				}
				index--;

			}
		}
		return null;
	}

	/**
	 * 获取基础信息中码头信息
	 * 
	 * @param index
	 *            码头索引
	 * 
	 * @return 码头id
	 * 
	 * */
	public static String getBaseInfoDockId(int index) {
		BaseInfoElement temp = BaseInfoData.mDockList.get(index);
		return temp.getId().substring(temp.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length());
	}

	/**
	 * 获取基础信息中区域id
	 * 
	 * @param index
	 *            区域索引
	 * 
	 * @return 区域id
	 * 
	 * */
	public static String getBaseInfoAreaId(int index) {
		BaseInfoElement temp = BaseInfoData.mAreaList.get(index);
		return temp.getId().substring(temp.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length());
	}

	/**
	 * 获取基础信息中卡口区域id
	 * 
	 * @param index
	 *            卡口区域索引
	 * 
	 * @return 卡口区域id
	 * 
	 * */
	public static String getBaseInfoKkAreaId(int index) {
		BaseInfoElement temp = BaseInfoData.mKkAreaList.get(index);
		return temp.getId();
	}

	/**
	 * 获取基础信息中监控区域id
	 * 
	 * @param index
	 *            监控区域索引
	 * 
	 * @return 监控区域id
	 * 
	 * */
	public static String getBaseInfoJkAreaId(int index) {
		BaseInfoElement temp = BaseInfoData.mJkAreaList.get(index);
		return temp.getId();
	}

	/**
	 * 根据码头id获取码头索引
	 * 
	 * @param id
	 *            码头id
	 * 
	 * @return 码头索引
	 * 
	 * */
	public static int getBaseInfoDockIndexByCode(String id) {
		if (id == null) {
			return 0;
		}
		int i;
		int count = BaseInfoData.mDockList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mDockList.get(i);
			if (temp.getId().substring(temp.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length()).equals(id)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 根据泊位id获取泊位索引
	 * 
	 * @param dockid
	 *            码头id
	 * @param berthid
	 *            泊位id
	 * 
	 * @return 泊位索引
	 * 
	 * */
	public static int getBaseInfoberthIndexByCode(String dockid, String berthid) {
		if (dockid == null || berthid == null) {
			return 0;
		}
		int i;
		int count = BaseInfoData.mBerthList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mBerthList.get(i);
			if (temp.getId().substring(temp.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length()).equals(berthid)
					&& temp.getParent().equals("jcxx" + BaseInfoData.TREEVIEW_NODE_SERARATE + "bw" + BaseInfoData.TREEVIEW_NODE_SERARATE + dockid)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 根据码头id获取泊位列表
	 * 
	 * @param dock
	 *            码头id
	 * 
	 * @return 泊位列表
	 * 
	 * */
	public static List<String> getBaseInfoBerthList(String dock) {
		List<String> list = new ArrayList<String>();
		if (BaseInfoData.mBerthList == null || BaseInfoData.mBerthList.size() == 0) {
			return list;
		}
		int i;
		int count = BaseInfoData.mBerthList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mBerthList.get(i);
			if (temp.getParent().equals("jcxx" + BaseInfoData.TREEVIEW_NODE_SERARATE + "bw" + BaseInfoData.TREEVIEW_NODE_SERARATE + dock)) {
				list.add(temp.getOutlineTitle());
			}
		}
		return list;
	}

	/**
	 * 根据区域id获取区域索引
	 * 
	 * @param id
	 *            区域id
	 * 
	 * @return 区域索引
	 * 
	 * */
	public static int getBaseInfoAreaIndexByCode(String id) {
		if (id == null) {
			return 0;
		}
		int i;
		int count = BaseInfoData.mAreaList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mAreaList.get(i);
			if (temp.getId().substring(temp.getParent().length() + BaseInfoData.TREEVIEW_NODE_SERARATE.length()).equals(id)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 根据区域名称获取区域索引
	 * 
	 * @param name
	 *            区域名称
	 * 
	 * @return 区域索引
	 * 
	 * */
	public static int getBaseInfoAreaIndexByName(String name) {
		if (name == null) {
			return 0;
		}
		int i;
		int count = BaseInfoData.mAreaList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mAreaList.get(i);
			if (temp.getOutlineTitle().equals(name)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 根据码头名称获取码头索引
	 * 
	 * @param name
	 *            码头名称
	 * 
	 * @return 码头索引
	 * 
	 * */
	public static int getBaseInfoDockIndexByName(String name) {
		if (name == null) {
			return 0;
		}
		int i;
		int count = BaseInfoData.mDockList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mDockList.get(i);
			if (temp.getOutlineTitle().equals(name)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 根据泊位名称获取泊位索引
	 * 
	 * @param dockid
	 *            码头id
	 * @param name
	 *            泊位名称
	 * 
	 * @return 泊位索引
	 * 
	 * */
	public static int getBaseInfoBerthIndexByName(String dockid, String name) {
		if (dockid == null || name == null) {
			return 0;
		}
		int i;
		int count = BaseInfoData.mBerthList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mBerthList.get(i);
			if (temp.getOutlineTitle().equals(name)
					&& temp.getParent().equals("jcxx" + BaseInfoData.TREEVIEW_NODE_SERARATE + "bw" + BaseInfoData.TREEVIEW_NODE_SERARATE + dockid)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 获取区域列表
	 * 
	 * @return 区域列表
	 * 
	 * */
	public static List<String> getBaseInfoAreaList() {
		List<String> list = new ArrayList<String>();
		if (BaseInfoData.mAreaList == null || BaseInfoData.mAreaList.size() == 0) {
			return list;
		}
		int i;
		int count = BaseInfoData.mAreaList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mAreaList.get(i);
			list.add(temp.getOutlineTitle());
		}
		return list;
	}

	/**
	 * 获取卡口区域列表
	 * 
	 * @return 卡口区域列表
	 * 
	 * */
	public static List<String> getBaseInfoKkAreaList() {
		List<String> list = new ArrayList<String>();
		if (BaseInfoData.mKkAreaList == null || BaseInfoData.mKkAreaList.size() == 0) {
			return list;
		}
		int i;
		int count = BaseInfoData.mKkAreaList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mKkAreaList.get(i);
			list.add(temp.getOutlineTitle());
		}
		return list;
	}

	/**
	 * 获取监控区域列表
	 * 
	 * @return 监控区域列表
	 * 
	 * */
	public static List<String> getBaseInfoJkAreaList() {
		List<String> list = new ArrayList<String>();
		if (BaseInfoData.mJkAreaList == null || BaseInfoData.mJkAreaList.size() == 0) {
			return list;
		}
		int i;
		int count = BaseInfoData.mJkAreaList.size();
		for (i = 0; i < count; i++) {
			BaseInfoElement temp = BaseInfoData.mJkAreaList.get(i);
			list.add(temp.getOutlineTitle());
		}
		return list;
	}

	public static void init(boolean port) {
		if (isInit) {
			return;
		}
		isInit = true;
		xunJianType = null;
		xunJianId = null;
		xunJianName = null;
		xunJianMTid = null;
		xunJianMTname = null;
		xunJianMTname = null;
		xunJianMtgsgs = null;
		xunJianMtgm = null;
		xunJianKbnl = null;
		xunJianZxhz = null;
		xunJianQyfw = null;
		xunJianXxxx = null;
		xunJianWz = null;
		xunJianZdmbcbdw = null;
		xunJianSs = null;
		xunJianMbdsl = null;
		xunJianZdgkcbdw = null;
		xunJianMtzax = null;
		xunJianQylx = null;
		DataDictionary.init();
		BaseInfoData.init();
		// IDCReadInterf.init(port);
		if (bindShipList == null) {
			bindShipList = new ArrayList<HashMap<String, Object>>();
		}
		if (countryList == null) {
			countryList = new ArrayList<String>();
		}
		int Count = DataDictionary.getCountryCodeLen();
		for (int i = 0; i < Count; i++) {
			countryList.add(DataDictionary.getCountryName(i));
		}

		SystemSetting.restoreBindShipInfo();
	}

	public static void destroy() {
		int size = 0;
		if (bindShipList != null) {
			size = bindShipList.size();
		}
		if (size > 0) {
			saveBindShipInfo();
		} else {
			String Filename = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE;
			File file = new File(Filename);
			if (file.exists()) {
				file.delete();
			}
		}
		xunJianType = null;
		xunJianId = null;
		xunJianName = null;
		xunJianMTid = null;
		xunJianMTname = null;
		xunJianMTname = null;
		xunJianMtgsgs = null;
		xunJianMtgm = null;
		xunJianKbnl = null;
		xunJianZxhz = null;
		xunJianQyfw = null;
		xunJianXxxx = null;
		xunJianWz = null;
		xunJianZdmbcbdw = null;
		xunJianSs = null;
		xunJianMbdsl = null;
		xunJianZdgkcbdw = null;
		xunJianMtzax = null;
		xunJianQylx = null;
		serverKadm = null;
		_serverHost = null;
		_serverPort = null;
		webServiceConnect = true;
		webServiceNamespace = null;
		webServiceUsername = null;
		webServicePassword = null;
		webServiceWsdlUrl = null;
		webServiceCode = null;
		webServiceArg1 = null;
		webServiceArg2 = null;
		webServiceArg3 = null;
		webServiceArg4 = null;
		pdaCode = null;
		mGPSTimer = 0;
		isInit = false;
		taskList = null;
		bindShipList = null;
		countryList = null;
		// IDCReadInterf.destroy(false);
		DataDictionary.destroy();
		BaseInfoData.destroy();
	}

	/**
	 * @方法名：destroyCfzg
	 * @功能说明：船方自管注销调用方法,清空绑定类别
	 * @author liums
	 * @date 2013-4-28 下午4:02:03
	 */
	public static void destroyCfzg() {
		int size = 0;
		if (bindShipList != null) {
			size = bindShipList.size();
		}
		if (size > 0) {
			// saveBindShipInfo();
			cleanBindShip();
		} else {
			String Filename = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE;
			File file = new File(Filename);
			if (file.exists()) {
				file.delete();
			}
		}
		xunJianType = null;
		xunJianId = null;
		xunJianName = null;
		xunJianMTid = null;
		xunJianMTname = null;
		xunJianMTname = null;
		xunJianMtgsgs = null;
		xunJianMtgm = null;
		xunJianKbnl = null;
		xunJianZxhz = null;
		xunJianQyfw = null;
		xunJianXxxx = null;
		xunJianWz = null;
		xunJianZdmbcbdw = null;
		xunJianSs = null;
		xunJianMbdsl = null;
		xunJianZdgkcbdw = null;
		xunJianMtzax = null;
		xunJianQylx = null;
		serverKadm = null;
		_serverHost = null;
		_serverPort = null;
		webServiceConnect = true;
		webServiceNamespace = null;
		webServiceUsername = null;
		webServicePassword = null;
		webServiceWsdlUrl = null;
		webServiceCode = null;
		webServiceArg1 = null;
		webServiceArg2 = null;
		webServiceArg3 = null;
		webServiceArg4 = null;
		pdaCode = null;
		mGPSTimer = 0;
		isInit = false;
		taskList = null;
		bindShipList = null;
		countryList = null;
		// IDCReadInterf.destroy(false);
		DataDictionary.destroy();
		BaseInfoData.destroy();
	}

	public static void setVPNStatus(int status) {
		mVPNStatus = status;
	}

	public static int getvpnstatus() {
		return mVPNStatus;
	}

	/** 把绑定船舶信息保存到文件 */
	private static boolean saveBindShipInfo() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		String projectDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech";
		File dir = new File(projectDir);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String text;
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			int size = bindShipList.size();
			serializer.startTag(null, "datas");
			serializer.startTag(null, "result");
			serializer.text("success");
			serializer.endTag(null, "result");
			for (int i = 0; i < size; i++) {
				Map<String, Object> _BindShipData = bindShipList.get(i);
				serializer.startTag(null, "info");
				text = (String) _BindShipData.get("source");
				if (text != null) {
					serializer.startTag(null, "source");
					serializer.text(text);
					serializer.endTag(null, "source");
				}
				text = (String) _BindShipData.get("hc");
				if (text != null) {
					serializer.startTag(null, "hc");
					serializer.text(text);
					serializer.endTag(null, "hc");
				}
				text = (String) _BindShipData.get("cbzwm");
				if (text != null) {
					serializer.startTag(null, "cbzwm");
					serializer.text(text);
					serializer.endTag(null, "cbzwm");
				}
				text = (String) _BindShipData.get("cbywm");
				if (text != null) {
					serializer.startTag(null, "cbywm");
					serializer.text(text);
					serializer.endTag(null, "cbywm");
				}
				text = (String) _BindShipData.get("jcfl");
				if (text != null) {
					serializer.startTag(null, "jcfl");
					serializer.text(text);
					serializer.endTag(null, "jcfl");
				}
				text = (String) _BindShipData.get("gj");
				if (text != null) {
					serializer.startTag(null, "gj");
					serializer.text(text);
					serializer.endTag(null, "gj");
				}
				text = (String) _BindShipData.get("cbxz");
				if (text != null) {
					serializer.startTag(null, "cbxz");
					serializer.text(text);
					serializer.endTag(null, "cbxz");
				}
				text = (String) _BindShipData.get("tkwz");
				if (text != null) {
					serializer.startTag(null, "tkwz");
					serializer.text(text);
					serializer.endTag(null, "tkwz");
				}
				text = (String) _BindShipData.get("tkmt");
				if (text != null) {
					serializer.startTag(null, "tkmt");
					serializer.text(text);
					serializer.endTag(null, "tkmt");
				}
				text = (String) _BindShipData.get("tkbw");
				if (text != null) {
					serializer.startTag(null, "tkbw");
					serializer.text(text);
					serializer.endTag(null, "tkbw");
				}
				text = (String) _BindShipData.get("cdgs");
				if (text != null) {
					serializer.startTag(null, "cdgs");
					serializer.text(text);
					serializer.endTag(null, "cdgs");
				}
				text = (String) _BindShipData.get("cys");
				if (text != null) {
					serializer.startTag(null, "cys");
					serializer.text(text);
					serializer.endTag(null, "cys");
				}
				text = (String) _BindShipData.get("dlcys");
				if (text != null) {
					serializer.startTag(null, "dlcys");
					serializer.text(text);
					serializer.endTag(null, "dlcys");
				}
				text = (String) _BindShipData.get("dlrys");
				if (text != null) {
					serializer.startTag(null, "dlrys");
					serializer.text(text);
					serializer.endTag(null, "dlrys");
				}
				text = (String) _BindShipData.get("bdzt");
				if (text != null) {
					serializer.startTag(null, "bdzt");
					serializer.text(text);
					serializer.endTag(null, "bdzt");
				}
				text = (String) _BindShipData.get("lg_time");
				if (text != null) {
					serializer.startTag(null, "lg_time");
					serializer.text(text);
					serializer.endTag(null, "lg_time");
				}
				text = (String) _BindShipData.get("yb_time");
				if (text != null) {
					serializer.startTag(null, "yb_time");
					serializer.text(text);
					serializer.endTag(null, "yb_time");
				}
				text = (String) _BindShipData.get("person_balance");
				if (text != null) {
					serializer.startTag(null, "person_balance");
					serializer.text(text);
					serializer.endTag(null, "person_balance");
				}
				text = (String) _BindShipData.get("dg_time");
				if (text != null) {
					serializer.startTag(null, "dg_time");
					serializer.text(text);
					serializer.endTag(null, "dg_time");
				}
				text = (String) _BindShipData.get("kb_time");
				if (text != null) {
					serializer.startTag(null, "kb_time");
					serializer.text(text);
					serializer.endTag(null, "kb_time");
				}
				text = (String) _BindShipData.get("kkmc");
				if (text != null) {
					serializer.startTag(null, "kkmc");
					serializer.text(text);
					serializer.endTag(null, "kkmc");
				}
				text = (String) _BindShipData.get("kkfw");
				if (text != null) {
					serializer.startTag(null, "kkfw");
					serializer.text(text);
					serializer.endTag(null, "kkfw");
				}
				text = (String) _BindShipData.get("kkxx");
				if (text != null) {
					serializer.startTag(null, "kkxx");
					serializer.text(text);
					serializer.endTag(null, "kkxx");
				}
				text = (String) _BindShipData.get("id");
				if (text != null) {
					serializer.startTag(null, "id");
					serializer.text(text);
					serializer.endTag(null, "id");
				}
				text = (String) _BindShipData.get("kacbzt");
				if (text != null) {
					serializer.startTag(null, "kacbzt");
					serializer.text(text);
					serializer.endTag(null, "kacbzt");
				}
				text = (String) _BindShipData.get("kacbqkid");
				if (text != null) {
					serializer.startTag(null, "kacbqkid");
					serializer.text(text);
					serializer.endTag(null, "kacbqkid");
				}
				serializer.endTag(null, "info");
			}
			serializer.endTag(null, "datas");
			serializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		try {
			OutputStream os;
			os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(writer.toString());
			osw.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/** 从文件读取绑定的船舶信息 */
	public static boolean restoreBindShipInfo() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE);
			boolean flag = file.exists();
			if (!file.exists()) {
				return false;
			}
			saxParser.parse(file, new BindShipInfoXmlHandler());
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("restoreBindShipInfo", "file.delete();");
			File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE);
			if (file.exists()) {
				file.delete();
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** 从文件读取绑定的船舶信息 */
	public static boolean restoreBindShipInfoForCfzg() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + XMLFILE);
			if (!file.exists()) {
				return false;
			}
			saxParser.parse(file, new BindShipInfoXmlHandler());
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (Exception e) {
			/*
			 * File file = new
			 * File(Environment.getExternalStorageDirectory().getPath() +
			 * File.separator + "pingtech" + File.separator + XMLFILE); if
			 * (file.exists()) { file.delete(); } e.printStackTrace();
			 */
			return false;
		}
		return true;
	}

	public static ArrayList<HashMap<String, Object>> getShipOfKK() {
		return shipOfKK;
	}

	public static void setShipOfKK(ArrayList<HashMap<String, Object>> shipOfKK) {
		SystemSetting.shipOfKK = shipOfKK;
	}

	public static ArrayList<HashMap<String, Object>> getShipInfoList() {
		return shipInfoList;
	}

	public static void setShipInfoList(ArrayList<HashMap<String, Object>> shipInfoList) {
		SystemSetting.shipInfoList = shipInfoList;
	}

	private static class BindShipInfoXmlHandler extends DefaultHandler {
		private String value;

		HashMap<String, Object> _BindShipData = null;

		public BindShipInfoXmlHandler() {
			super();
		}

		public void startDocument() throws SAXException {
			if (bindShipList == null) {
				bindShipList = new ArrayList<HashMap<String, Object>>();
			} else {
				bindShipList.clear();
			}
		}

		public void endDocument() throws SAXException {

		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			value = "";
			if (localName.equals("info")) {
				_BindShipData = new HashMap<String, Object>();
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (localName.equals("info")) {
				bindShipList.add(_BindShipData);
				_BindShipData = null;
			} else {
				if (value != null && _BindShipData != null) {
					_BindShipData.put(localName, value);
				}
			}
		}

		public void characters(char ch[], int start, int length) throws SAXException {
			value += new String(ch, start, length);
		}
	}
}
