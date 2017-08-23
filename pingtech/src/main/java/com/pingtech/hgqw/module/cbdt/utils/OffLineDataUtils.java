package com.pingtech.hgqw.module.cbdt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pingtech.hgqw.entity.LoginUser;
import com.pingtech.hgqw.entity.ManagerFlag;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.utils.DeviceUtils;
import com.pingtech.hgqw.utils.xml.XmlUtils;

public class OffLineDataUtils {

	/**
	 * 
	 * @方法名：getCbdgOffData
	 * @功能说明：船舶抵港业务数据封装
	 * @author liums
	 * @date 2013-10-9 下午8:06:21
	 * @param hc
	 * @return
	 */
	public static OffData getCbdgOffData(String hc) {
		OffData offData = new OffData();
		// 离线数据xml封装
		// <userid>0123456</userid><!--用户id-->
		// <pdacode>0123456</pdacode><!--警务通编号-->
		// <hc>0123456</hc><!--船舶航次-->
		// <dgsj>2013-09-25 11:05:20</dgsj><!--抵港时间-->
		Map<String, String> map = new HashMap<String, String>();
		map.put("userid", LoginUser.getCurrentLoginUser().getUserID());
		map.put("pdacode", DeviceUtils.getIMEI());
		map.put("hc", hc);
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		map.put("dgsj", nowTime);
		String xmlData = XmlUtils.buildXml(map);
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_CBDT + "");
		offData.setCzgn(ManagerFlag.PDA_CBDT_CBDG + "");
		return offData;
	}

	public static OffData getCblgOffData(String hc) {
		OffData offData = new OffData();
		// 离线数据xml封装
		// <userid>0123456</userid><!--用户id-->
		// <pdacode>0123456</pdacode><!--警务通编号-->
		// <hc>0123456</hc><!--船舶航次-->
		// <lgsj>2013-09-25 11:05:20</lgsj><!--离港时间-->
		Map<String, String> map = new HashMap<String, String>();
		map.put("userid", LoginUser.getCurrentLoginUser().getUserID());
		map.put("pdacode", DeviceUtils.getIMEI());
		map.put("hc", hc);
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		map.put("lgsj", nowTime);
		String xmlData = XmlUtils.buildXml(map);
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_CBDT + "");
		offData.setCzgn(ManagerFlag.PDA_CBDT_CBLG + "");
		return offData;
	}

	public static OffData getCbkbOffData(String hc) {
		OffData offData = new OffData();
		// 离线数据xml封装
		// <userid>0123456</userid><!--用户id-->
		// <pdacode>0123456</pdacode><!--警务通编号-->
		// <hc>0123456</hc><!--船舶航次-->
		// <kbsj>2013-09-25 11:05:20</kbsj><!--靠泊时间-->
		Map<String, String> map = new HashMap<String, String>();
		map.put("userid", LoginUser.getCurrentLoginUser().getUserID());
		map.put("pdacode", DeviceUtils.getIMEI());
		map.put("hc", hc);
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		map.put("kbsj", nowTime);
		String xmlData = XmlUtils.buildXml(map);
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_CBDT + "");
		offData.setCzgn(ManagerFlag.PDA_CBDT_CBKB + "");
		return offData;
	}

	public static OffData getCbybOffData(String hc) {
		OffData offData = new OffData();
		// 离线数据xml封装
		// <userid>0123456</userid><!--用户id-->
		// <pdacode>0123456</pdacode><!--警务通编号-->
		// <hc>0123456</hc><!--船舶航次-->
		// <ybsj>2013-09-25 11:05:20</ybsj><!--移泊时间-->
		Map<String, String> map = new HashMap<String, String>();
		map.put("userid", LoginUser.getCurrentLoginUser().getUserID());
		map.put("pdacode", DeviceUtils.getIMEI());
		map.put("hc", hc);
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		map.put("ybsj", nowTime);
		String xmlData = XmlUtils.buildXml(map);
		offData.setPdacode(DeviceUtils.getIMEI());
		offData.setUserid(LoginUser.getCurrentLoginUser().getUserID());
		offData.setXmldata(xmlData);
		offData.setGxsj(new Date());
		offData.setCjsj(new Date());
		offData.setCzmk(ManagerFlag.PDA_CBDT + "");
		offData.setCzgn(ManagerFlag.PDA_CBDT_CBYB + "");
		return offData;
	}

}
