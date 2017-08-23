package com.pingtech.hgqw.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Environment;

/**
 * 保存及处理数据字典数据
 * */
public class DataDictionary {
	private static HashMap<String, String> chellxMap = null;

	/** 默认对象类别名称 */
	private static List<String> defaultObjectTypeNameList = null;

	/** 默认对象类别代码 */
	private static List<String> defaultObjectTypeCodeList = null;

	/** 默认事件类别名称 */
	private static List<String> defaultEventTypeNameList = null;

	/** 默认事件类别代码 */
	private static List<String> defaultEventTypeCodeList = null;

	/** 默认性别名称 */
	private static List<String> defaultSexTypeNameList = null;

	/** 默认性别代码 */
	private static List<String> defaultSexTypeCodeList = null;

	/** 默认证件类型名称 */
	private static List<String> defaultCertificatesTypeNameList = null;

	/** 默认证件类型代码 */
	private static List<String> defaultCertificatesTypeCodeList = null;

	/** 默认船舶类型名称 */
	private static List<String> defaultShipTypeNameList = null;

	/** 默认船舶类型代码 */
	private static List<String> defaultShipTypeCodeList = null;

	/** 默认船舶用途名称 */
	private static List<String> defaultShipPurposeNameList = null;

	/** 默认船舶用途代码 */
	private static List<String> defaultShipPurposeCodeList = null;

	/** 默认重点关注事件类别名称 */
	private static List<String> defaultZDGZ_EventTypeNameList = null;

	/** 默认重点关注事件类别代码 */
	private static List<String> defaultZDGZ_EventTypeCodeList = null;

	/** 默认其他事件类别名称 */
	private static List<String> defaultQT_EventTypeNameList = null;

	/** 默认其他事件类别代码 */
	private static List<String> defaultQT_EventTypeCodeList = null;

	/** 默认查岗查哨其他事件类别名称 */
	private static List<String> defaultCGCSQT_EventTypeNameList = null;

	/** 默认查岗查哨其他事件类别代码 */
	private static List<String> defaultCGCSQT_EventTypeCodeList = null;

	/** 默认货物类型名称 */
	private static List<String> defaultGoodsTypeNameList = null;

	/** 默认货物类型代码 */
	private static List<String> defaultGoodsTypeCodeList = null;

	/** 默认信息来源名称 */
	private static List<String> defaultInfoSourceNameList = null;

	/** 默认信息来源代码 */
	private static List<String> defaultInfoSourceCodeList = null;

	/** 默认登陆人员职务名称 */
	private static List<String> defaultDlryzwNameList = null;

	/** 默认登陆人员职务代码 */
	private static List<String> defaultDlryzwCodeList = null;

	/** 默认船舶员工职务名称 */
	private static List<String> defaultCbygzwNameList = null;

	/** 默认船舶员工职务英文名称 */
	private static List<String> defaultCbygzwNameListEn = null;

	/** 默认船舶员工职务代码 */
	private static List<String> defaultCbygzwCodeList = null;

	/** 默认船舶员工职务英文代码 */
	private static List<String> defaultCbygzwCodeListEn = null;

	/** 默认摄像头类型名称 */
	private static List<String> defaultCameraTypeNameList = null;

	/** 默认摄像头类型代码 */
	private static List<String> defaultCameraTypeCodeList = null;

	/** 默认摄像头类型名称 */
	private static List<String> defaultChellxTypeNameList = null;

	/** 默认摄像头类型代码 */
	private static List<String> defaultChellxTypeCodeList = null;

	/** 对象类别 */
	public static final int DATADICTIONARY_TYPE_OBJECT_TYPE = 0;

	/** 事件类别 */
	public static final int DATADICTIONARY_TYPE_EVENT_TYPE = 1;

	/** 性别 */
	public static final int DATADICTIONARY_TYPE_SEX_TYPE = 2;

	/** 证件类型 */
	public static final int DATADICTIONARY_TYPE_CERTIFICATES_TYPE = 3;

	/** 车辆类型 */
	public static final int DATADICTIONARY_TYPE_CHELLX_TYPE = 101265;

	/** 船舶类型 */
	public static final int DATADICTIONARY_TYPE_SHIP_TYPE = 4;

	/** 船舶用途 */
	public static final int DATADICTIONARY_TYPE_SHIP_PURPOSE = 6;

	/** 国家列表 */
	public static final int DATADICTIONARY_TYPE_COUNTRY = 7;

	/** 重点关注事件类别 */
	public static final int DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE = 8;

	/** 其他事件类别 */
	public static final int DATADICTIONARY_TYPE_QT_EVENT_TYPE = 9;

	/** 查岗查哨事件类别 */
	public static final int DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP = 10;

	/** 货物类型 */
	public static final int DATADICTIONARY_TYPE_GOODS_TYPE = 11;

	/** 信息来源 */
	public static final int DATADICTIONARY_TYPE_INFO_SOURCE = 12;

	/** 登轮人员职务 */
	public static final int DATADICTIONARY_TYPE_DLRYZW = 13;

	/** 船舶员工职务 */
	public static final int DATADICTIONARY_TYPE_CBYGZW = 14;

	/** 船舶员工职务英文 */
	public static final int DATADICTIONARY_TYPE_CBYGZW_EN = 142;

	/** 摄像头类型 */
	public static final int DATADICTIONARY_TYPE_CAMERA_TYPE = 15;

	/** 车辆类型 */
	public static final int DATADICTIONARY_TYPE_CLLX_TYPE = 101265;

	/** 国家名称及代码列表，目前国家使用这个固定数组，未使用数据字典 */
	private final static String countryCodeString[][] = { { "阿鲁巴岛", "ABW" }, { "阿富汗", "AFG" }, { "安哥拉", "AGO" }, { "安圭拉", "AIA" },
			{ "阿尔巴尼亚", "ALB" }, { "安道尔", "AND" }, { "阿拉伯联合酋长国", "ARE" }, { "阿根廷", "ARG" }, { "安提瓜和巴布达", "ATG" }, { "澳大利亚", "AUS" }, { "奥地利", "AUT" },
			{ "阿塞拜疆", "AZE" }, { "阿尔及利亚", "DZA" }, { "埃及", "EGY" }, { "爱沙尼亚", "EST" }, { "埃塞俄比亚", "ETH" }, { "爱尔兰", "IRL" }, { "阿曼", "OMN" },
			{ "布隆迪", "BDI" }, { "比利时", "BEL" }, { "贝宁", "BEN" }, { "布基纳法索", "BFA" }, { "保加利亚", "BGR" }, { "巴林", "BHR" }, { "巴哈马", "BHS" },
			{ "波斯尼亚和黑塞哥维那", "BIH" }, { "白俄罗斯", "BLR" }, { "伯利兹", "BLZ" }, { "百慕大", "BMU" }, { "玻利维亚", "BOL" }, { "巴西", "BRA" }, { "巴巴多斯", "BRB" },
			{ "不丹", "BTN" }, { "布维岛", "BVT" }, { "博茨瓦纳", "BWA" }, { "冰岛", "ISL" }, { "北马里亚纳群岛", "MNP" }, { "巴基斯坦", "PAK" }, { "巴拿马", "PAN" },
			{ "巴布亚新几内亚", "PNG" }, { "波兰", "POL" }, { "波多黎各", "PRI" }, { "巴拉圭", "PRY" }, { "巴勒斯坦", "PST" }, { "赤道几内亚", "GNQ" }, { "朝鲜", "PRK" },
			{ "德国", "DEU" }, { "多米尼克国", "DMA" }, { "丹麦", "DNK" }, { "多米尼加共和国", "DOM" }, { "多哥", "TGO" }, { "东帝汶", "TMP" }, { "厄瓜多尔", "ECU" },
			{ "厄立特里亚", "ERI" }, { "俄罗斯", "RUS" }, { "法属南部领土", "ATF" }, { "佛得角", "CPV" }, { "芬兰", "FIN" }, { "斐济", "FJI" },
			{ "福克兰群岛（马尔维纳斯群岛）", "FLK" }, { "法国", "FRA" }, { "法罗群岛", "FRO" }, { "法属圭亚那", "GUF" }, { "菲律宾", "PHL" }, { "法属波利尼西亚", "PYF" },
			{ "梵蒂冈", "VAT" }, { "刚果（金）", "COD" }, { "刚果（布）", "COG" }, { "哥伦比亚", "COL" }, { "哥斯达黎加", "CRI" }, { "古巴", "CUB" }, { "格鲁吉亚", "GEO" },
			{ "瓜德罗普", "GLP" }, { "冈比亚", "GMB" }, { "格林纳达", "GRD" }, { "格陵兰", "GRL" }, { "关岛", "GUM" }, { "圭亚那", "GUY" }, { "公海", "MLB" },
			{ "荷属安的列斯", "ANT" }, { "豪兰和贝克群岛(大洋洲)", "HB" }, { "赫德岛和麦克唐纳岛", "HMD" }, { "洪都拉斯", "HND" }, { "海地", "HTI" }, { "哈萨克斯坦", "KAZ" },
			{ "韩国", "KOR" }, { "荷兰", "NLD" }, { "黑山共和国", "MNE" }, { "加拿大", "CAN" }, { "捷克", "CZE" }, { "吉布提", "DJI" }, { "加蓬", "GAB" },
			{ "加纳", "GHQ" }, { "几内亚", "GIN" }, { "几内亚比绍", "GNB" }, { "贾维斯岛(大洋洲)", "JI" }, { "吉尔吉斯斯坦", "KGZ" }, { "柬埔寨", "KHM" }, { "基里巴斯", "KIR" },
			{ "津巴布韦", "ZWE" }, { "科科斯（基林）群岛", "CCK" }, { "科特迪瓦", "CIV" }, { "喀麦隆", "CMR" }, { "库克群岛", "COK" }, { "科摩罗", "COM" }, { "开曼群岛", "CYM" },
			{ "克罗地亚", "HRV" }, { "肯尼亚", "KEN" }, { "科威特", "KWT" }, { "卡塔尔", "QAT" }, { "老挝", "LAO" }, { "黎巴嫩", "LBN" }, { "利比里亚", "LBR" },
			{ "利比亚", "LBY" }, { "列支敦士登", "LIE" }, { "莱索托", "LSO" }, { "立陶宛", "LTU" }, { "卢森堡", "LUX" }, { "拉脱维亚", "LVA" }, { "留尼汪", "REU" },
			{ "罗马尼亚", "ROM" }, { "卢旺达", "RWA" }, { "联合国", "AUN" }, { "美属萨摩亚", "ASM" }, { "孟加拉国", "BGD" }, { "密克罗尼西亚", "FSM" }, { "摩洛哥", "MAR" },
			{ "摩纳哥", "MCO" }, { "摩尔多瓦", "MDA" }, { "马达加斯加", "MDG" }, { "马尔代夫", "MDV" }, { "墨西哥", "MEX" }, { "马绍尔群岛", "MHL" }, { "马其顿", "MKD" },
			{ "马里", "MLI" }, { "马耳他", "MLT" }, { "缅甸", "MMR" }, { "蒙古", "MNG" }, { "莫桑比克", "MOZ" }, { "毛里塔尼亚", "MRT" }, { "蒙特塞拉特", "MSR" },
			{ "马提尼克", "MTQ" }, { "毛里求斯", "MUS" }, { "马拉维", "MWI" }, { "马来西亚", "MYS" }, { "马约特", "MYT" }, { "秘鲁", "PER" }, { "美属太平洋各群岛", "UMI" },
			{ "美国", "USA" }, { "美属维尔京群岛", "VIR" }, { "南极洲", "ATA" }, { "纳米比亚", "NAM" }, { "尼日尔", "NER" }, { "诺福克岛", "NFK" }, { "尼日利亚", "NGA" },
			{ "尼加拉瓜", "NIC" }, { "纽埃", "NIU" }, { "挪威", "NOR" }, { "尼泊尔", "NPL" }, { "瑙鲁", "NRU" }, { "南乔治亚岛和南桑德韦奇岛", "SGS" }, { "南斯拉夫", "YUG" },
			{ "南非", "ZAF" }, { "皮特凯恩群岛", "PCN" }, { "帕劳", "PLW" }, { "葡萄牙", "PRT" }, { "瑞士", "CHE" }, { "日本", "JPN" }, { "瑞典", "SWE" },
			{ "圣诞岛", "CXR" }, { "塞浦路斯", "CYP" }, { "圣基茨和尼维斯", "KNA" }, { "圣卢西亚", "LCA" }, { "斯里兰卡", "LKA" }, { "沙特阿拉伯", "SAU" }, { "苏丹", "SDN" },
			{ "塞内加尔", "SEN" }, { "圣赫勒拿", "SHN" }, { "斯瓦巴德群岛", "SJM" }, { "所罗门群岛", "SLB" }, { "塞拉利昂", "SLE" }, { "萨尔瓦多", "SLV" }, { "圣马力诺", "SMR" },
			{ "索马里", "SOM" }, { "圣皮埃尔和密克隆", "SPM" }, { "塞班", "SS" }, { "圣多美和普林西比", "STP" }, { "苏里南", "SUR" }, { "斯洛伐克", "SVK" }, { "斯洛文尼亚", "SVN" },
			{ "斯威士兰", "SWZ" }, { "塞舌尔", "SYC" }, { "圣文森特和格林纳丁斯", "VCT" }, { "萨摩亚", "WSM" }, { "塞尔维亚共和国", "SRB" }, { "特克斯和凯科斯群岛", "TCA" },
			{ "泰国", "THA" }, { "塔吉克斯坦", "TJK" }, { "托克劳", "TKL" }, { "土库曼斯坦", "TKM" }, { "汤加", "TON" }, { "特立尼达和多巴哥", "TTO" }, { "突尼斯", "TUN" },
			{ "土耳其", "TUR" }, { "图瓦卢", "TUV" }, { "塔希提", "TX" }, { "坦桑尼亚", "TZA" }, { "文莱", "BRN" }, { "危地马拉", "GTM" }, { "无国籍", "NN" },
			{ "乌干达", "UGA" }, { "乌克兰", "UKA" }, { "乌拉圭", "URY" }, { "乌兹别克斯坦", "UZB" }, { "委内瑞拉", "VEN" }, { "瓦努阿图", "VUT" }, { "威克岛", "WAK" },
			{ "瓦利斯和富图纳群岛", "WLF" }, { "西撒哈拉", "ESH" }, { "西班牙", "ESP" }, { "希腊", "GRC" }, { "匈牙利", "HUN" }, { "新喀里多尼亚", "NCL" }, { "新西兰", "NZL" },
			{ "新加坡", "SGP" }, { "叙利亚", "SYR" }, { "亚美尼亚", "ARM" }, { "英国", "GBR" }, { "印度尼西亚", "IDN" }, { "印度", "IND" }, { "英属印度洋领土", "IOT" },
			{ "伊朗", "IRN" }, { "伊拉克", "IRQ" }, { "以色列", "ISR" }, { "意大利", "ITA" }, { "牙买加", "JAM" }, { "约旦", "JOR" }, { "约翰斯顿岛", "JTN" },
			{ "英属维尔京群岛", "VGB" }, { "越南", "VNM" }, { "也门", "YEM" }, { "中非", "CAF" }, { "智利", "CHL" }, { "中国", "CHN" }, { "直布罗陀", "GIB" },
			{ "中国香港", "HKG" }, { "中国澳门", "MAC" }, { "中途岛", "MID" }, { "乍得", "TCD" }, { "中国台湾", "TWN" }, { "赞比亚", "ZMB" } };

	/** 对象类别名称 */
	private static ArrayList<String> objectTypeNameList;

	/** 对象类别代码 */
	private static ArrayList<String> objectTypeCodeList;

	/** 事件类别名称 */
	private static ArrayList<String> eventTypeNameList;

	/** 事件类别代码 */
	private static ArrayList<String> eventTypeCodeList;

	/** 国家名称 */
	private static ArrayList<String> countryNameList;

	/** 国家代码 */
	private static ArrayList<String> countryCodeList;

	/** 国家名称 */
	private static ArrayList<String> chellxNameList;

	/** 国家代码 */
	private static ArrayList<String> chellxCodeList;

	/** 证件类型名称 */
	private static ArrayList<String> certificatesTypeNameList;

	/** 证件类型代码 */
	private static ArrayList<String> certificatesTypeCodeList;

	/** 性别名称 */
	private static ArrayList<String> sexTypeNameList;

	/** 性别代码 */
	private static ArrayList<String> sexTypeCodeList;

	/** 处理类型名称 */
	private static ArrayList<String> dealTypeNameList;

	/** 处理类型代码 */
	private static ArrayList<String> dealTypeCodeList;

	/** 船舶类型名称 */
	private static ArrayList<String> shipTypeNameList;

	/** 船舶类型代码 */
	private static ArrayList<String> shipTypeCodeList;

	/** 船舶用途名称 */
	private static ArrayList<String> shipPurposeNameList;

	/** 船舶用途代码 */
	private static ArrayList<String> shipPurposeCodeList;

	/** 重点关注事件类别名称 */
	private static ArrayList<String> zdgz_EventTypeNameList;

	/** 重点关注事件类别代码 */
	private static ArrayList<String> zdgz_EventTypeCodeList;

	/** 其他事件类别名称 */
	private static ArrayList<String> qt_EventTypeNameList;

	/** 其他事件类别代码 */
	private static ArrayList<String> qt_EventTypeCodeList;

	/** 查岗查哨事件类别名称 */
	private static ArrayList<String> cgcsqt_EventTypeNameList;

	/** 查岗查哨事件类别代码 */
	private static ArrayList<String> cgcsqt_EventTypeCodeList;

	/** 货物类型名称 */
	private static ArrayList<String> goodsTypeNameList;

	/** 货物类型代码 */
	private static ArrayList<String> goodsTypeCodeList;

	/** 信息来源名称 */
	private static ArrayList<String> infoSourceNameList;

	/** 信息来源代码 */
	private static ArrayList<String> infoSourceCodeList;

	/** 登轮人员职务名称 */
	private static ArrayList<String> dlryzwNameList;

	/** 登轮人员职务代码 */
	private static ArrayList<String> dlryzwCodeList;

	/** 船舶员工职务名称 */
	private static ArrayList<String> cbygzwNameList;

	/** 船舶员工职务英文名称 */
	private static ArrayList<String> cbygzwNameListEn;

	/** 船舶员工职务代码 */
	private static ArrayList<String> cbygzwCodeList;

	/** 船舶员工职务英文代码 */
	private static ArrayList<String> cbygzwCodeListEn;

	/** 摄像头类型名称 */
	private static ArrayList<String> cameraTypeNameList;

	/** 摄像头类型代码 */
	private static ArrayList<String> cameraTypeCodeList;

	private static boolean isInit;

	/** 初始化数据 */
	public static void init() {
		if (isInit) {
			return;
		}
		chellxMap = new HashMap<String, String>();
		defaultObjectTypeNameList = new ArrayList<String>(Arrays.asList("人员", "车辆", "船舶", "设备", "区域"));
		defaultObjectTypeCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03", "04", "05"));
		defaultEventTypeNameList = new ArrayList<String>(Arrays.asList("无效证件", "证件过期", "设备损坏", "查岗查哨", "查获违禁物品", "违法违规偷渡人员"));
		defaultEventTypeCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03", "04", "05", "06"));
		defaultSexTypeNameList = new ArrayList<String>(Arrays.asList("男", "女", "未说明"));
		defaultSexTypeCodeList = new ArrayList<String>(Arrays.asList("1", "2", "9"));
		defaultCertificatesTypeNameList = new ArrayList<String>(Arrays.asList("一次有效台湾居民来往大陆通行证", "个人身份证", "外交护照", "公务护照", "公务普通或因公普通护照", "普通护照",
				"中华人民共和国旅行证", "台湾居民来往大陆通行证", "海员证", "机组人员证", "铁路员工证", "中华人民共和国出入境通行证", "往来港澳通行证（多次有效）", "往来港澳通行证（一次有效）", "前往港澳通行证", "港澳同胞回乡证或通行卡",
				"大陆居民往来台湾通行证", "往来香港特别行政区通行证", "往来澳门特别行政区通行证", "中华人民共和国外国人出入境证", "中华人民共和国外国人旅行证", "中华人民共和国外国人居留证", "中华人民共和国外国人临时居留证", "入籍证书", "出籍证书",
				"复籍证书", "中华人民共和国回国证明", "出海渔船民证", "临时出海渔船民证", "出海船舶户口簿", "出海船舶户口证", "粤港澳流动渔民证", "粤港澳临时流动渔民证", "粤港澳流动渔船户口簿", "搭靠台轮许可证", "登轮许可证", "登陆证",
				"住宿证", "搭靠外轮许可证", "随船工作证", "航行港澳小型船舶证明书", "航行港澳小型船舶查验簿", "香港特别行政区护照", "澳门特别行政区护照", "因公往来港澳特区通行证（高官）", "因公往来港澳特区通行证（普通）", "其他因公证件",
				"其他因私证件", "中方因公边境地区出入境通行证", "中方因私边境地区出入境通行证", "外方因公边境地区入出境通行证", "外方因私边境地区入出境通行证"));
		defaultCertificatesTypeCodeList = new ArrayList<String>(Arrays.asList("06", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
				"21", "22", "23", "24", "25", "26", "27", "30", "31", "32", "33", "35", "36", "37", "38", "40", "41", "42", "43", "44", "45", "46",
				"47", "48", "50", "51", "52", "53", "54", "55", "70", "71", "72", "73", "98", "99", "90", "91", "92", "93"));
		defaultShipTypeNameList = new ArrayList<String>(Arrays.asList("中国籍", "方便旗", "外国籍"));
		defaultShipTypeCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03"));
		defaultShipPurposeNameList = new ArrayList<String>(Arrays.asList("船舶供水", "船舶供油", "回收垃圾", "船舶交通", "回收废油", "船舶修理", "拖轮", "驳运货物"));
		defaultShipPurposeCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08"));

		defaultZDGZ_EventTypeNameList = new ArrayList<String>(Arrays.asList("无效证件", "证件过期", "设备损坏", "查岗查哨", "查获违禁物品", "违法违规偷渡人员"));
		defaultZDGZ_EventTypeCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03", "04", "05", "06"));
		defaultQT_EventTypeNameList = new ArrayList<String>(Arrays.asList("无效证件", "证件过期", "设备损坏", "查岗查哨", "查获违禁物品", "违法违规偷渡人员"));
		defaultQT_EventTypeCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03", "04", "05", "06"));
		defaultCGCSQT_EventTypeNameList = new ArrayList<String>(Arrays.asList("无效证件", "证件过期", "设备损坏", "查岗查哨", "查获违禁物品", "违法违规偷渡人员"));
		defaultCGCSQT_EventTypeCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03", "04", "05", "06"));
		defaultInfoSourceNameList = new ArrayList<String>(Arrays.asList("卡口验证", "梯口验证", "现场巡查", "视频巡视", "智能监控"));
		defaultInfoSourceCodeList = new ArrayList<String>(Arrays.asList("01", "02", "03", "04", "05"));
		defaultGoodsTypeNameList = new ArrayList<String>(Arrays.asList("供伙食"));
		defaultGoodsTypeCodeList = new ArrayList<String>(Arrays.asList("01"));
		defaultDlryzwNameList = new ArrayList<String>(Arrays.asList("工人"));
		defaultDlryzwCodeList = new ArrayList<String>(Arrays.asList("01"));
		defaultCbygzwNameList = new ArrayList<String>(Arrays.asList("船长"));
		defaultCbygzwNameListEn = new ArrayList<String>(Arrays.asList("Master"));
		defaultCbygzwCodeList = new ArrayList<String>(Arrays.asList("01"));
		defaultCbygzwCodeListEn = new ArrayList<String>(Arrays.asList("01"));
		defaultCameraTypeNameList = new ArrayList<String>(Arrays.asList("固定摄像机"));
		defaultCameraTypeCodeList = new ArrayList<String>(Arrays.asList("01"));
		objectTypeNameList = new ArrayList<String>();
		objectTypeCodeList = new ArrayList<String>();
		eventTypeNameList = new ArrayList<String>();
		eventTypeCodeList = new ArrayList<String>();
		countryNameList = new ArrayList<String>();
		countryCodeList = new ArrayList<String>();
		certificatesTypeNameList = new ArrayList<String>();
		certificatesTypeCodeList = new ArrayList<String>();
		sexTypeNameList = new ArrayList<String>();
		sexTypeCodeList = new ArrayList<String>();
		dealTypeNameList = new ArrayList<String>();
		dealTypeCodeList = new ArrayList<String>();
		shipTypeNameList = new ArrayList<String>();
		shipTypeCodeList = new ArrayList<String>();
		shipPurposeNameList = new ArrayList<String>();
		shipPurposeCodeList = new ArrayList<String>();
		zdgz_EventTypeNameList = new ArrayList<String>();
		zdgz_EventTypeCodeList = new ArrayList<String>();
		qt_EventTypeNameList = new ArrayList<String>();
		qt_EventTypeCodeList = new ArrayList<String>();
		cgcsqt_EventTypeNameList = new ArrayList<String>();
		cgcsqt_EventTypeCodeList = new ArrayList<String>();
		goodsTypeNameList = new ArrayList<String>();
		goodsTypeCodeList = new ArrayList<String>();
		infoSourceNameList = new ArrayList<String>();
		infoSourceCodeList = new ArrayList<String>();
		dlryzwNameList = new ArrayList<String>();
		dlryzwCodeList = new ArrayList<String>();
		cbygzwNameList = new ArrayList<String>();
		cbygzwNameListEn = new ArrayList<String>();
		cbygzwCodeList = new ArrayList<String>();
		cbygzwCodeListEn = new ArrayList<String>();
		cameraTypeNameList = new ArrayList<String>();
		cameraTypeCodeList = new ArrayList<String>();
		restoreDataDictionary();
		isInit = true;
	}

	public static void destroy() {
		isInit = false;
		defaultObjectTypeNameList = null;
		defaultObjectTypeCodeList = null;
		defaultEventTypeNameList = null;
		defaultEventTypeCodeList = null;
		defaultSexTypeNameList = null;
		defaultSexTypeCodeList = null;
		defaultCertificatesTypeNameList = null;
		defaultCertificatesTypeCodeList = null;
		defaultZDGZ_EventTypeNameList = null;
		defaultZDGZ_EventTypeCodeList = null;
		defaultQT_EventTypeNameList = null;
		defaultQT_EventTypeCodeList = null;
		defaultCGCSQT_EventTypeNameList = null;
		defaultCGCSQT_EventTypeCodeList = null;
		defaultGoodsTypeNameList = null;
		defaultGoodsTypeCodeList = null;
		defaultInfoSourceNameList = null;
		defaultInfoSourceCodeList = null;
		defaultDlryzwNameList = null;
		defaultDlryzwCodeList = null;
		defaultCbygzwNameList = null;
		defaultCbygzwNameListEn = null;
		defaultCbygzwCodeList = null;
		defaultCbygzwCodeListEn = null;
		defaultCameraTypeNameList = null;
		defaultCameraTypeCodeList = null;
		objectTypeNameList = null;
		objectTypeCodeList = null;
		eventTypeNameList = null;
		eventTypeCodeList = null;
		countryNameList = null;
		countryCodeList = null;
		certificatesTypeNameList = null;
		certificatesTypeCodeList = null;
		sexTypeNameList = null;
		sexTypeCodeList = null;
		dealTypeNameList = null;
		dealTypeCodeList = null;
		shipTypeNameList = null;
		shipTypeCodeList = null;
		shipPurposeNameList = null;
		shipPurposeCodeList = null;
		zdgz_EventTypeNameList = null;
		zdgz_EventTypeCodeList = null;
		qt_EventTypeNameList = null;
		qt_EventTypeCodeList = null;
		cgcsqt_EventTypeNameList = null;
		cgcsqt_EventTypeCodeList = null;
		goodsTypeNameList = null;
		goodsTypeCodeList = null;
		infoSourceNameList = null;
		infoSourceCodeList = null;
		dlryzwNameList = null;
		dlryzwCodeList = null;
		cbygzwNameList = null;
		cbygzwNameListEn = null;
		cbygzwCodeList = null;
		cbygzwCodeListEn = null;
		cameraTypeNameList = null;
		cameraTypeCodeList = null;
	}

	/**
	 * 获取数据字典名称
	 * 
	 * @param code
	 *            数据字典代码
	 * @param type
	 *            数据字典类型
	 * @return 数据字典名称
	 * */
	public static String getDataDictionaryName(String code, int type) {
		if (code == null || "".equals(code)) {
			return "";
		}
		List<String> defaultname = null;
		List<String> defaultcode = null;
		List<String> datadictname;
		List<String> datadictcode;
		switch (type) {
		case DATADICTIONARY_TYPE_OBJECT_TYPE:
			defaultname = defaultObjectTypeNameList;
			defaultcode = defaultObjectTypeCodeList;
			datadictname = objectTypeNameList;
			datadictcode = objectTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_EVENT_TYPE:
			defaultname = defaultEventTypeNameList;
			defaultcode = defaultEventTypeCodeList;
			datadictname = eventTypeNameList;
			datadictcode = eventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SEX_TYPE:
			defaultname = defaultSexTypeNameList;
			defaultcode = defaultSexTypeCodeList;
			datadictname = sexTypeNameList;
			datadictcode = sexTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CERTIFICATES_TYPE:
			defaultname = defaultCertificatesTypeNameList;
			defaultcode = defaultCertificatesTypeCodeList;
			datadictname = certificatesTypeNameList;
			datadictcode = certificatesTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_TYPE:
			defaultname = defaultShipTypeNameList;
			defaultcode = defaultShipTypeCodeList;
			datadictname = shipTypeNameList;
			datadictcode = shipTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_PURPOSE:
			defaultname = defaultShipPurposeNameList;
			defaultcode = defaultShipPurposeCodeList;
			datadictname = shipPurposeNameList;
			datadictcode = shipPurposeCodeList;
			break;
		case DATADICTIONARY_TYPE_COUNTRY:
			// defaultname = DefaultCountryNameList;
			// defaultcode = DefaultCountryCodeList;
			datadictname = countryNameList;
			datadictcode = countryCodeList;
			return null;
		case DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE:
			defaultname = defaultZDGZ_EventTypeNameList;
			defaultcode = defaultZDGZ_EventTypeCodeList;
			datadictname = zdgz_EventTypeNameList;
			datadictcode = zdgz_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_QT_EVENT_TYPE:
			defaultname = defaultQT_EventTypeNameList;
			defaultcode = defaultQT_EventTypeCodeList;
			datadictname = qt_EventTypeNameList;
			datadictcode = qt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP:
			defaultname = defaultCGCSQT_EventTypeNameList;
			defaultcode = defaultCGCSQT_EventTypeCodeList;
			datadictname = cgcsqt_EventTypeNameList;
			datadictcode = cgcsqt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_GOODS_TYPE:
			defaultname = defaultGoodsTypeNameList;
			defaultcode = defaultGoodsTypeCodeList;
			datadictname = goodsTypeNameList;
			datadictcode = goodsTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_INFO_SOURCE:
			defaultname = defaultInfoSourceNameList;
			defaultcode = defaultInfoSourceCodeList;
			datadictname = infoSourceNameList;
			datadictcode = infoSourceCodeList;
			break;
		case DATADICTIONARY_TYPE_DLRYZW:
			defaultname = defaultDlryzwNameList;
			defaultcode = defaultDlryzwCodeList;
			datadictname = dlryzwNameList;
			datadictcode = dlryzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CBYGZW:
			defaultname = defaultCbygzwNameList;
			defaultcode = defaultCbygzwCodeList;
			datadictname = cbygzwNameList;
			datadictcode = cbygzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CBYGZW_EN:
			// defaultname = defaultCbygzwNameList;
			defaultname = defaultCbygzwNameListEn;
			defaultcode = defaultCbygzwCodeListEn;
			// datadictname = cbygzwNameList;
			datadictname = cbygzwNameListEn;
			datadictcode = cbygzwCodeListEn;
			break;
		case DATADICTIONARY_TYPE_CAMERA_TYPE:
			defaultname = defaultCameraTypeNameList;
			defaultcode = defaultCameraTypeCodeList;
			datadictname = cameraTypeNameList;
			datadictcode = cameraTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CHELLX_TYPE:
			initChellx();
			datadictname = chellxNameList;
			defaultname = chellxNameList;
			datadictcode = chellxCodeList;
			break;
		default:
			return null;
		}
		if (datadictcode != null && datadictcode.size() > 0) {
			int count = datadictcode.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(datadictcode.get(i))) {
					return (String) datadictname.get(i);
				}
			}
			return (String) datadictname.get(0);
		} else {
			int count = defaultcode.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(defaultcode.get(i))) {
					return defaultname.get(i);
				}
			}
			return defaultname.get(0);
		}
	}

	/**
	 * 获取数据字典职务名称，自定义的目的是职务有两个，需要合并查找
	 * 
	 * @param code
	 *            职务代码
	 * @param type
	 *            职务类型
	 * @return 职务名称
	 * 
	 * */
	public static String getDataDictionaryOfficeName(String code, int type) {
		if (code == null || code.length() == 0 || (type != DATADICTIONARY_TYPE_DLRYZW && type != DATADICTIONARY_TYPE_CBYGZW)) {
			return "";
		}

		if (dlryzwCodeList != null && dlryzwCodeList.size() > 0) {
			int count = dlryzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(dlryzwCodeList.get(i))) {
					return (String) dlryzwNameList.get(i);
				}
			}
		}
		if (cbygzwCodeList != null && cbygzwCodeList.size() > 0) {
			int count = cbygzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(cbygzwCodeList.get(i))) {
					return (String) cbygzwNameList.get(i);
				}
			}
		}
		if (dlryzwCodeList != null && dlryzwCodeList.size() > 0) {

		} else if (defaultDlryzwCodeList != null && defaultDlryzwCodeList.size() > 0) {
			int count = defaultDlryzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(defaultDlryzwCodeList.get(i))) {
					return defaultDlryzwNameList.get(i);
				}
			}
		}
		if (cbygzwCodeList != null && cbygzwCodeList.size() > 0) {

		} else if (defaultCbygzwCodeList != null && defaultCbygzwCodeList.size() > 0) {
			int count = defaultCbygzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(defaultCbygzwCodeList.get(i))) {
					return defaultCbygzwNameList.get(i);
				}
			}
		}
		if (type == DATADICTIONARY_TYPE_DLRYZW) {
			if (dlryzwNameList != null && dlryzwNameList.size() > 0) {
				return (String) dlryzwNameList.get(0);
			} else {
				return (String) defaultDlryzwNameList.get(0);
			}
		} else {
			if (cbygzwNameList != null && cbygzwNameList.size() > 0) {
				return (String) cbygzwNameList.get(0);
			} else {
				return (String) defaultCbygzwNameList.get(0);
			}
		}
	}

	/**
	 * 获取数据字典职务索引，包含两个职务
	 * 
	 * @param code
	 *            职务代码
	 * @return 职务索引
	 * 
	 * */
	public static int getDataDictionaryOfficeIndex(String code) {
		if (code == null) {
			return 0;
		}

		if (dlryzwCodeList != null && dlryzwCodeList.size() > 0) {
			int count = dlryzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(dlryzwCodeList.get(i))) {
					return i;
				}
			}
		}
		if (cbygzwCodeList != null && cbygzwCodeList.size() > 0) {
			int count = cbygzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(cbygzwCodeList.get(i))) {
					return (i + (dlryzwCodeList.size() == 0 ? defaultDlryzwCodeList.size() : dlryzwCodeList.size()));
				}
			}
		}
		if (dlryzwCodeList != null && dlryzwCodeList.size() > 0) {

		} else if (defaultDlryzwCodeList != null && defaultDlryzwCodeList.size() > 0) {
			int count = defaultDlryzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(defaultDlryzwCodeList.get(i))) {
					return i;
				}
			}
		}
		if (cbygzwCodeList != null && cbygzwCodeList.size() > 0) {

		} else if (defaultCbygzwCodeList != null && defaultCbygzwCodeList.size() > 0) {
			int count = defaultCbygzwCodeList.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(defaultCbygzwCodeList.get(i))) {
					return (i + (dlryzwCodeList.size() == 0 ? defaultDlryzwCodeList.size() : dlryzwCodeList.size()));
				}
			}
		}
		return 0;
	}

	/**
	 * 获取数据字典代码
	 * 
	 * @param name
	 *            数据字典名称
	 * @param type
	 *            数据字典类型
	 * @return 数据字典代码
	 * 
	 * */
	public static String getDataDictionaryCode(String name, int type) {
		if (name == null) {
			return null;
		}
		List<String> defaultname = null;
		List<String> defaultcode = null;
		List<String> datadictname = null;
		List<String> datadictcode;
		switch (type) {
		case DATADICTIONARY_TYPE_OBJECT_TYPE:
			defaultname = defaultObjectTypeNameList;
			defaultcode = defaultObjectTypeCodeList;
			datadictname = objectTypeNameList;
			datadictcode = objectTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_EVENT_TYPE:
			defaultname = defaultEventTypeNameList;
			defaultcode = defaultEventTypeCodeList;
			datadictname = eventTypeNameList;
			datadictcode = eventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SEX_TYPE:
			defaultname = defaultSexTypeNameList;
			defaultcode = defaultSexTypeCodeList;
			datadictname = sexTypeNameList;
			datadictcode = sexTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CERTIFICATES_TYPE:
			defaultname = defaultCertificatesTypeNameList;
			defaultcode = defaultCertificatesTypeCodeList;
			datadictname = certificatesTypeNameList;
			datadictcode = certificatesTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_TYPE:
			defaultname = defaultShipTypeNameList;
			defaultcode = defaultShipTypeCodeList;
			datadictname = shipTypeNameList;
			datadictcode = shipTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_PURPOSE:
			defaultname = defaultShipPurposeNameList;
			defaultcode = defaultShipPurposeCodeList;
			datadictname = shipPurposeNameList;
			datadictcode = shipPurposeCodeList;
			break;
		case DATADICTIONARY_TYPE_COUNTRY:
			// defaultname = DefaultCountryNameList;
			// defaultcode = DefaultCountryCodeList;
			datadictname = countryNameList;
			datadictcode = countryCodeList;
			return null;
		case DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE:
			defaultname = defaultZDGZ_EventTypeNameList;
			defaultcode = defaultZDGZ_EventTypeCodeList;
			datadictname = zdgz_EventTypeNameList;
			datadictcode = zdgz_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_QT_EVENT_TYPE:
			defaultname = defaultQT_EventTypeNameList;
			defaultcode = defaultQT_EventTypeCodeList;
			datadictname = qt_EventTypeNameList;
			datadictcode = qt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP:
			defaultname = defaultCGCSQT_EventTypeNameList;
			defaultcode = defaultCGCSQT_EventTypeCodeList;
			datadictname = cgcsqt_EventTypeNameList;
			datadictcode = cgcsqt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_GOODS_TYPE:
			defaultname = defaultGoodsTypeNameList;
			defaultcode = defaultGoodsTypeCodeList;
			datadictname = goodsTypeNameList;
			datadictcode = goodsTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_INFO_SOURCE:
			defaultname = defaultInfoSourceNameList;
			defaultcode = defaultInfoSourceCodeList;
			datadictname = infoSourceNameList;
			datadictcode = infoSourceCodeList;
			break;
		case DATADICTIONARY_TYPE_DLRYZW:
			defaultname = defaultDlryzwNameList;
			defaultcode = defaultDlryzwCodeList;
			datadictname = dlryzwNameList;
			datadictcode = dlryzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CBYGZW:
			defaultname = defaultCbygzwNameList;
			defaultcode = defaultCbygzwCodeList;
			datadictname = cbygzwNameList;
			datadictcode = cbygzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CAMERA_TYPE:
			defaultname = defaultCameraTypeNameList;
			defaultcode = defaultCameraTypeCodeList;
			datadictname = cameraTypeNameList;
			datadictcode = cameraTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CHELLX_TYPE:
			initChellx();
			datadictname = chellxNameList;
			defaultname = chellxNameList;
			datadictcode = chellxCodeList;
			break;
		default:
			return null;
		}
		if (datadictname != null && datadictname.size() > 0) {
			int count = datadictname.size();
			for (int i = 0; i < count; i++) {
				if (name.equals(datadictname.get(i))) {
					return (String) datadictcode.get(i);
				}
			}
			return (String) datadictcode.get(0);
		} else {
			int count = defaultname.size();
			for (int i = 0; i < count; i++) {
				if (name.equals(defaultname.get(i))) {
					return defaultcode.get(i);
				}
			}
			return defaultcode.get(0);
		}
	}

	/**
	 * 根据索引获取数据字典代码
	 * 
	 * @param index
	 *            索引
	 * @param type
	 *            数据字典类型
	 * @return 数据字典代码
	 * 
	 * */
	public static String getDataDictionaryCodeByIndex(int index, int type) {
		if (index < 0) {
			return null;
		}
		List<String> defaultcode = null;
		List<String> datadictcode;
		switch (type) {
		case DATADICTIONARY_TYPE_OBJECT_TYPE:
			defaultcode = defaultObjectTypeCodeList;
			datadictcode = objectTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_EVENT_TYPE:
			defaultcode = defaultEventTypeCodeList;
			datadictcode = eventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SEX_TYPE:
			defaultcode = defaultSexTypeCodeList;
			datadictcode = sexTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CERTIFICATES_TYPE:
			defaultcode = defaultCertificatesTypeCodeList;
			datadictcode = certificatesTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_TYPE:
			defaultcode = defaultShipTypeCodeList;
			datadictcode = shipTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_PURPOSE:
			defaultcode = defaultShipPurposeCodeList;
			datadictcode = shipPurposeCodeList;
			break;
		case DATADICTIONARY_TYPE_COUNTRY:
			// defaultcode = DefaultCountryCodeList;
			datadictcode = countryCodeList;
			return null;
		case DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE:
			defaultcode = defaultZDGZ_EventTypeCodeList;
			datadictcode = zdgz_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_QT_EVENT_TYPE:
			defaultcode = defaultQT_EventTypeCodeList;
			datadictcode = qt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP:
			defaultcode = defaultCGCSQT_EventTypeCodeList;
			datadictcode = cgcsqt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_GOODS_TYPE:
			defaultcode = defaultGoodsTypeCodeList;
			datadictcode = goodsTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_INFO_SOURCE:
			defaultcode = defaultInfoSourceCodeList;
			datadictcode = infoSourceCodeList;
			break;
		case DATADICTIONARY_TYPE_DLRYZW:
			defaultcode = defaultDlryzwCodeList;
			datadictcode = dlryzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CBYGZW:
			defaultcode = defaultCbygzwCodeList;
			datadictcode = cbygzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CAMERA_TYPE:
			defaultcode = defaultCameraTypeCodeList;
			datadictcode = cameraTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CHELLX_TYPE:
			initChellx();
			datadictcode = chellxCodeList;
			break;
		default:
			return null;
		}
		if (datadictcode != null && datadictcode.size() > 0) {
			int count = datadictcode.size();
			if (index < count) {
				return (String) datadictcode.get(index);
			} else {
				return (String) datadictcode.get(0);
			}
		} else {
			int count = defaultcode.size();
			if (index < count) {
				return (String) defaultcode.get(index);
			} else {
				return (String) defaultcode.get(0);
			}
		}
	}

	/**
	 * 根据数据字典代码获取数据字典索引
	 * 
	 * @param code
	 *            职务代码
	 * @param type
	 *            数据字典类型
	 * @return 数据字典索引
	 * 
	 * */
	public static int getDataDictionaryIndexByCode(String code, int type) {
		if (code == null) {
			return 0;
		}
		List<String> defaultcode;
		List<String> datadictcode;
		switch (type) {
		case DATADICTIONARY_TYPE_OBJECT_TYPE:
			defaultcode = defaultObjectTypeCodeList;
			datadictcode = objectTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_EVENT_TYPE:
			defaultcode = defaultEventTypeCodeList;
			datadictcode = eventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SEX_TYPE:
			defaultcode = defaultSexTypeCodeList;
			datadictcode = sexTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CERTIFICATES_TYPE:
			defaultcode = defaultCertificatesTypeCodeList;
			datadictcode = certificatesTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_TYPE:
			defaultcode = defaultShipTypeCodeList;
			datadictcode = shipTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_SHIP_PURPOSE:
			defaultcode = defaultShipPurposeCodeList;
			datadictcode = shipPurposeCodeList;
			break;
		case DATADICTIONARY_TYPE_COUNTRY:
			// defaultcode = DefaultCountryCodeList;
			datadictcode = countryCodeList;
			return 0;
		case DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE:
			defaultcode = defaultZDGZ_EventTypeCodeList;
			datadictcode = zdgz_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_QT_EVENT_TYPE:
			defaultcode = defaultQT_EventTypeCodeList;
			datadictcode = qt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP:
			defaultcode = defaultCGCSQT_EventTypeCodeList;
			datadictcode = cgcsqt_EventTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_GOODS_TYPE:
			defaultcode = defaultGoodsTypeCodeList;
			datadictcode = goodsTypeCodeList;
			break;
		case DATADICTIONARY_TYPE_INFO_SOURCE:
			defaultcode = defaultInfoSourceCodeList;
			datadictcode = infoSourceCodeList;
			break;
		case DATADICTIONARY_TYPE_DLRYZW:
			defaultcode = defaultDlryzwCodeList;
			datadictcode = dlryzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CBYGZW:
			defaultcode = defaultCbygzwCodeList;
			datadictcode = cbygzwCodeList;
			break;
		case DATADICTIONARY_TYPE_CAMERA_TYPE:
			defaultcode = defaultCameraTypeCodeList;
			datadictcode = cameraTypeCodeList;
			break;
		default:
			return 0;
		}
		if (datadictcode != null && datadictcode.size() > 0) {
			int count = datadictcode.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(datadictcode.get(i))) {
					return i;
				}
			}
			return 0;
		} else {
			int count = defaultcode.size();
			for (int i = 0; i < count; i++) {
				if (code.equals(defaultcode.get(i))) {
					return i;
				}
			}
			return 0;
		}
	}

	/**
	 * 根据数据字典名称获取数据字典索引
	 * 
	 * @param name
	 *            数据字典名称
	 * @param 数据字典类型
	 * @return 数据字典索引
	 * 
	 * */
	public static int getDataDictionaryIndexByName(String name, int type) {
		if (name == null) {
			return 0;
		}
		List<String> defaultname;
		List<String> datadictname;
		switch (type) {
		case DATADICTIONARY_TYPE_OBJECT_TYPE:
			defaultname = defaultObjectTypeNameList;
			datadictname = objectTypeNameList;
			break;
		case DATADICTIONARY_TYPE_EVENT_TYPE:
			defaultname = defaultEventTypeNameList;
			datadictname = eventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_SEX_TYPE:
			defaultname = defaultSexTypeNameList;
			datadictname = sexTypeNameList;
			break;
		case DATADICTIONARY_TYPE_CERTIFICATES_TYPE:
			defaultname = defaultCertificatesTypeNameList;
			datadictname = certificatesTypeNameList;
			break;
		case DATADICTIONARY_TYPE_SHIP_TYPE:
			defaultname = defaultShipTypeNameList;
			datadictname = shipTypeNameList;
			break;
		case DATADICTIONARY_TYPE_SHIP_PURPOSE:
			defaultname = defaultShipPurposeNameList;
			datadictname = shipPurposeNameList;
			break;
		case DATADICTIONARY_TYPE_COUNTRY:
			// defaultname = DefaultCountryNameList;
			datadictname = countryNameList;
			return 0;
		case DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE:
			defaultname = defaultZDGZ_EventTypeNameList;
			datadictname = zdgz_EventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_QT_EVENT_TYPE:
			defaultname = defaultQT_EventTypeNameList;
			datadictname = qt_EventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP:
			defaultname = defaultCGCSQT_EventTypeNameList;
			datadictname = cgcsqt_EventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_GOODS_TYPE:
			defaultname = defaultGoodsTypeNameList;
			datadictname = goodsTypeNameList;
			break;
		case DATADICTIONARY_TYPE_INFO_SOURCE:
			defaultname = defaultInfoSourceNameList;
			datadictname = infoSourceNameList;
			break;
		case DATADICTIONARY_TYPE_DLRYZW:
			defaultname = defaultDlryzwNameList;
			datadictname = dlryzwNameList;
			break;
		case DATADICTIONARY_TYPE_CBYGZW:
			defaultname = defaultCbygzwNameList;
			datadictname = cbygzwNameList;
			break;
		case DATADICTIONARY_TYPE_CAMERA_TYPE:
			defaultname = defaultCameraTypeNameList;
			datadictname = cameraTypeNameList;
			break;
		default:
			return 0;
		}
		if (datadictname != null && datadictname.size() > 0) {
			int count = datadictname.size();
			for (int i = 0; i < count; i++) {
				if (name.equals(datadictname.get(i))) {
					return i;
				}
			}
			return 0;
		} else {
			int count = defaultname.size();
			for (int i = 0; i < count; i++) {
				if (name.equals(defaultname.get(i))) {
					return i;
				}
			}
			return 0;
		}
	}

	/**
	 * 获取数据字典所有名称，用于显示
	 * 
	 * @param type
	 *            数据字典类型
	 * @return 名称列表
	 * 
	 * */
	public static List<String> getDataDictionaryNameList(int type) {
		List<String> defaultname = null;
		List<String> datadictname = null;
		switch (type) {
		case DATADICTIONARY_TYPE_OBJECT_TYPE:
			defaultname = defaultObjectTypeNameList;
			datadictname = objectTypeNameList;
			break;
		case DATADICTIONARY_TYPE_EVENT_TYPE:
			defaultname = defaultEventTypeNameList;
			datadictname = eventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_SEX_TYPE:
			defaultname = defaultSexTypeNameList;
			datadictname = sexTypeNameList;
			break;
		case DATADICTIONARY_TYPE_CERTIFICATES_TYPE:
			defaultname = defaultCertificatesTypeNameList;
			datadictname = certificatesTypeNameList;
			break;
		case DATADICTIONARY_TYPE_SHIP_TYPE:
			defaultname = defaultShipTypeNameList;
			datadictname = shipTypeNameList;
			break;
		case DATADICTIONARY_TYPE_SHIP_PURPOSE:
			defaultname = defaultShipPurposeNameList;
			datadictname = shipPurposeNameList;
			break;
		case DATADICTIONARY_TYPE_COUNTRY:
			// defaultname = DefaultCountryNameList;
			datadictname = countryNameList;
			return null;
		case DATADICTIONARY_TYPE_ZDGZ_EVENT_TYPE:
			defaultname = defaultZDGZ_EventTypeNameList;
			datadictname = zdgz_EventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_QT_EVENT_TYPE:
			defaultname = defaultQT_EventTypeNameList;
			datadictname = qt_EventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_CGCSQT_EVENT_TYP:
			defaultname = defaultCGCSQT_EventTypeNameList;
			datadictname = cgcsqt_EventTypeNameList;
			break;
		case DATADICTIONARY_TYPE_GOODS_TYPE:
			defaultname = defaultGoodsTypeNameList;
			datadictname = goodsTypeNameList;
			break;
		case DATADICTIONARY_TYPE_INFO_SOURCE:
			defaultname = defaultInfoSourceNameList;
			datadictname = infoSourceNameList;
			break;
		case DATADICTIONARY_TYPE_DLRYZW:
			defaultname = defaultDlryzwNameList;
			datadictname = dlryzwNameList;
			break;
		case DATADICTIONARY_TYPE_CBYGZW:
			defaultname = defaultCbygzwNameList;
			datadictname = cbygzwNameList;
			break;
		case DATADICTIONARY_TYPE_CAMERA_TYPE:
			defaultname = defaultCameraTypeNameList;
			datadictname = cameraTypeNameList;
			break;
		case DATADICTIONARY_TYPE_CLLX_TYPE:
			initChellx();
			datadictname = chellxNameList;
			break;
		default:
			return null;
		}
		if (datadictname != null && datadictname.size() > 0) {
			return datadictname;
		} else {
			return defaultname;
		}
	}

	private static void initChellx() {
		if (chellxNameList == null || chellxCodeList == null) {
			chellxNameList = new ArrayList<String>();
			chellxCodeList = new ArrayList<String>();
			for (Entry<String, String> entry : chellxMap.entrySet()) {
				chellxNameList.add(entry.getValue());
				chellxCodeList.add(entry.getKey());
			}
		}
	}

	/**
	 * 获取国家代码
	 * 
	 * @param name
	 *            国家名称
	 * @return 国家代码
	 * 
	 * */
	public static String getCountryCode(String name) {
		for (int i = 0; i < countryCodeString.length; i++) {
			if (countryCodeString[i][0].equals(name)) {
				return countryCodeString[i][1];
			}
		}
		return countryCodeString[0][1];
	}

	/**
	 * 获取国家名称
	 * 
	 * @param code
	 *            国家代码
	 * @return 国家名称
	 * 
	 * */
	public static String getCountryName(String code) {
		if (StringUtils.isEmpty(code)) {
			return "";
		}
		for (int i = 0; i < countryCodeString.length; i++) {
			if (countryCodeString[i][1].equals(code)) {
				return countryCodeString[i][0];
			}
		}
		return "";
	}

	/**
	 * 获取国家索引
	 * 
	 * @param name
	 *            国家名称
	 * @return 国家索引
	 * 
	 * */
	public static int getCountryIndexByName(String name) {
		for (int i = 0; i < countryCodeString.length; i++) {
			if (countryCodeString[i][0].equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取国家索引
	 * 
	 * @param code
	 *            国家代码
	 * @return 国家索引
	 * 
	 * */
	public static int getCountryIndexByCode(String code) {
		for (int i = 0; i < countryCodeString.length; i++) {
			if (countryCodeString[i][1].equals(code)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取国家代码
	 * 
	 * @param index
	 *            国家索引
	 * @return 国家代码
	 * 
	 * */
	public static String getCountryCode(int index) {
		if (index < 0 || index >= countryCodeString.length) {
			return null;
		}
		return countryCodeString[index][1];
	}

	/**
	 * 获取国家名称
	 * 
	 * @param index
	 *            国家索引
	 * @return 国家名称
	 * 
	 * */
	public static String getCountryName(int index) {
		if (index < 0 || index >= countryCodeString.length) {
			return null;
		}
		return countryCodeString[index][0];
	}

	/**
	 * 获取国家总数目
	 * 
	 * @return 国家总数目
	 * 
	 * */
	public static int getCountryCodeLen() {
		return countryCodeString.length;
	}

	/**
	 * 从文件解析数据字典并存放到数组列表中
	 * 
	 * @return true 成功 false 失败
	 * 
	 * */
	public static boolean restoreDataDictionary() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator
					+ "datadict.xml"), new DataDictionaryXmlHandler());
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (Exception e) {
			File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "pingtech" + File.separator + "datadict.xml");
			if (file.exists()) {
				file.delete();
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static class DataDictionaryXmlHandler extends DefaultHandler {
		private String value;

		private int mElementType;

		private static final int ELEMENT_TYPE_NONE = -1;

		private static final int ELEMENT_TYPE_INFO = 0;

		private static final int ELEMENT_TYPE_DXLB = 1;

		private static final int ELEMENT_TYPE_SJLB = 2;

		private static final int ELEMENT_TYPE_GJ = 3;

		private static final int ELEMENT_TYPE_ZJLX = 4;

		private static final int ELEMENT_TYPE_XB = 5;

		private static final int ELEMENT_TYPE_CLLX = 6;

		private static final int ELEMENT_TYPE_CBXZ = 7;

		private static final int ELEMENT_TYPE_CBYT = 8;

		private static final int ELEMENT_TYPE_ZDGZSJLB = 9;

		private static final int ELEMENT_TYPE_QTSJLB = 10;

		private static final int ELEMENT_TYPE_CGCSQTSJLB = 11;

		private static final int ELEMENT_TYPE_WPLX = 12;

		private static final int ELEMENT_TYPE_XXLY = 13;

		private static final int ELEMENT_TYPE_DLRYZW = 14;

		private static final int ELEMENT_TYPE_CBYGZW = 15;

		private static final int ELEMENT_TYPE_CBYGZW_EN = 152;

		private static final int ELEMENT_TYPE_CAMERA = 16;

		private static final int ELEMENT_TYPE_CHELLX = 17;

		public DataDictionaryXmlHandler() {
			super();
		}

		public void startDocument() throws SAXException {
			objectTypeNameList.clear();
			objectTypeCodeList.clear();
			eventTypeNameList.clear();
			eventTypeCodeList.clear();
			countryNameList.clear();
			countryCodeList.clear();
			certificatesTypeNameList.clear();
			certificatesTypeCodeList.clear();
			sexTypeNameList.clear();
			sexTypeCodeList.clear();
			dealTypeNameList.clear();
			dealTypeCodeList.clear();
			shipTypeNameList.clear();
			shipTypeCodeList.clear();
			shipPurposeNameList.clear();
			shipPurposeCodeList.clear();
			zdgz_EventTypeNameList.clear();
			zdgz_EventTypeCodeList.clear();
			qt_EventTypeNameList.clear();
			qt_EventTypeCodeList.clear();
			cgcsqt_EventTypeNameList.clear();
			cgcsqt_EventTypeCodeList.clear();
			goodsTypeNameList.clear();
			goodsTypeCodeList.clear();
			infoSourceNameList.clear();
			infoSourceCodeList.clear();
			dlryzwNameList.clear();
			dlryzwCodeList.clear();
			cbygzwNameList.clear();
			cbygzwNameListEn.clear();
			cbygzwCodeList.clear();
			cbygzwCodeListEn.clear();
			cameraTypeNameList.clear();
			cameraTypeCodeList.clear();
			chellxMap.clear();
		}

		public void endDocument() throws SAXException {

		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			value = "";
			if (localName.equals("info")) {
				mElementType = ELEMENT_TYPE_INFO;
				return;
			}
			if (mElementType == ELEMENT_TYPE_NONE) {
				if (localName.equals("dxlb")) {
					mElementType = ELEMENT_TYPE_DXLB;
				} else if (localName.equals("sjlb")) {
					mElementType = ELEMENT_TYPE_SJLB;
				} else if (localName.equals("gj")) {
					mElementType = ELEMENT_TYPE_GJ;
				} else if (localName.equals("zjlx")) {
					mElementType = ELEMENT_TYPE_ZJLX;
				} else if (localName.equals("xb")) {
					mElementType = ELEMENT_TYPE_XB;
				} else if (localName.equals("cllx")) {
					mElementType = ELEMENT_TYPE_CLLX;
				} else if (localName.equals("cbxz")) {
					mElementType = ELEMENT_TYPE_CBXZ;
				} else if (localName.equals("cbyt")) {
					mElementType = ELEMENT_TYPE_CBYT;
				} else if (localName.equals("zdgzsjlb")) {
					mElementType = ELEMENT_TYPE_ZDGZSJLB;
				} else if (localName.equals("qtsjlb")) {
					mElementType = ELEMENT_TYPE_QTSJLB;
				} else if (localName.equals("cgcsqtsjlb")) {
					mElementType = ELEMENT_TYPE_CGCSQTSJLB;
				} else if (localName.equals("wplx")) {
					mElementType = ELEMENT_TYPE_WPLX;
				} else if (localName.equals("xxly")) {
					mElementType = ELEMENT_TYPE_XXLY;
				} else if (localName.equals("dlryzw")) {
					mElementType = ELEMENT_TYPE_DLRYZW;
				} else if (localName.equals("cbygzw")) {
					mElementType = ELEMENT_TYPE_CBYGZW;
				} else if (localName.equals("cbygzwEn")) {
					mElementType = ELEMENT_TYPE_CBYGZW_EN;
				} else if (localName.equals("sxtlx")) {
					mElementType = ELEMENT_TYPE_CAMERA;
				} else if (localName.equals("chellx")) {
					mElementType = ELEMENT_TYPE_CHELLX;
				}
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (localName.equals("info")) {
				mElementType = ELEMENT_TYPE_NONE;
				return;
			}
			if (mElementType == ELEMENT_TYPE_DXLB) {
				if (localName.equals("dxlb")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						objectTypeNameList.add(value);
						objectTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_SJLB) {
				if (localName.equals("sjlb")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						eventTypeNameList.add(value);
						eventTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_GJ) {
				if (localName.equals("gj")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						countryNameList.add(value);
						countryCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_ZJLX) {
				if (localName.equals("zjlx")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						certificatesTypeNameList.add(value);
						certificatesTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_XB) {
				if (localName.equals("xb")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						sexTypeNameList.add(value);
						sexTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CLLX) {
				if (localName.equals("cllx")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						dealTypeNameList.add(value);
						dealTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CBXZ) {
				if (localName.equals("cbxz")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						shipTypeNameList.add(value);
						shipTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CBYT) {
				if (localName.equals("cbyt")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						shipPurposeNameList.add(value);
						shipPurposeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_ZDGZSJLB) {
				if (localName.equals("zdgzsjlb")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						zdgz_EventTypeNameList.add(value);
						zdgz_EventTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_QTSJLB) {
				if (localName.equals("qtsjlb")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						qt_EventTypeNameList.add(value);
						qt_EventTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CGCSQTSJLB) {
				if (localName.equals("cgcsqtsjlb")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						cgcsqt_EventTypeNameList.add(value);
						cgcsqt_EventTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_WPLX) {
				if (localName.equals("wplx")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						goodsTypeNameList.add(value);
						goodsTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_XXLY) {
				if (localName.equals("xxly")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						infoSourceNameList.add(value);
						infoSourceCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_DLRYZW) {
				if (localName.equals("dlryzw")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						dlryzwNameList.add(value);
						dlryzwCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CBYGZW) {
				if (localName.equals("cbygzw")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						cbygzwNameList.add(value);
						cbygzwCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CBYGZW_EN) {
				if (localName.equals("cbygzwEn")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						cbygzwNameListEn.add(value);
						cbygzwCodeListEn.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CAMERA) {
				if (localName.equals("sxtlx")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						cameraTypeNameList.add(value);
						cameraTypeCodeList.add(localName.substring(1));
					}
				}
			} else if (mElementType == ELEMENT_TYPE_CHELLX) {
				if (localName.equals("chellx")) {
					mElementType = ELEMENT_TYPE_NONE;
				} else {
					if (value != null) {
						chellxMap.put(localName.substring(1), value);
					}
				}
			}
		}

		public void characters(char ch[], int start, int length) throws SAXException {
			value += new String(ch, start, length);
		}
	}
}
