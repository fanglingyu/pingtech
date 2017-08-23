package com.pingtech.hgqw.interf;

import android.util.Pair;

/**
 * 
 *
 * 类描述：返回后通过该接口传递到各个activity
 *
 * <p> Title: 江海港边检勤务-移动管理系统-OffLineResult.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  娄高伟 
 * @version 1.0
 * @date  2013-10-11 上午9:50:03
 */
public interface OffLineResult {
	void offLineResult(Pair<Boolean, Object> res, int offLineRequestType);
	
}
