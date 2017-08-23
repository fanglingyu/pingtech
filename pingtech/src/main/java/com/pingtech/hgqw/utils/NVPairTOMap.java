package com.pingtech.hgqw.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
/**
 * 
 *
 * 类描述：请求平台参数转化为请求本地数据参数
 *
 * <p> Title: 江海港边检勤务-移动管理系统-NVPairTOMap.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  娄高伟 
 * @version 1.0
 * @date  2013-10-13 下午3:47:02
 */
public class NVPairTOMap {
	public static Map<String, Object> nameValuePairTOMap(List<NameValuePair> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (params != null && params.size() > 0) {
			for (NameValuePair nameValuePair : params) {
				map.put(nameValuePair.getName(), nameValuePair.getValue());
			}
			return map;
		}
		return map;

	}
}
