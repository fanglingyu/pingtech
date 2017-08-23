package com.pingtech.hgqw.module.offline.zjyf.entity;

import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;

/**
 * 
 *
 * 类描述：验放结果
 *
 * <p> Title: 江海港边检勤务综合管理系统-YfResult.java </p>
 * <p> Copyright: Copyright (c) 2012 </p>
 * <p> Company: 品恩科技 </p>
 * @author  赵琳 
 * @version 1.0
 * @date  2013-7-5 上午10:49:39
 */
public class YfResult {
	private boolean result;//验证结果
	private String tsxx;//提示信息
	private String zjlx;//证件类型-48:等论证,-50登陆证,-17海员证 ,--52搭靠外轮许可证
	private Hgzjxx zjxx;//证件 信息
	private TBCyxx cyxx;//船员信息
	
	private boolean isToast = false;//船员信息
	
	private Kacbqk ship ;//船舶
	private Qyxx qyxx ;//区域
	
	public boolean isToast() {
		return isToast;
	}
	public void setToast(boolean isToast) {
		this.isToast = isToast;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getTsxx() {
		return tsxx;
	}
	public void setTsxx(String tsxx) {
		this.tsxx = tsxx;
	}
	public String getZjlx() {
		return zjlx;
	}
	public void setZjlx(String zjlx) {
		this.zjlx = zjlx;
	}
		
	public Hgzjxx getZjxx() {
		return zjxx;
	}
	public void setZjxx(Hgzjxx zjxx) {
		this.zjxx = zjxx;
	}
	public Kacbqk getShip() {
		return ship;
	}
	public void setShip(Kacbqk ship) {
		this.ship = ship;
	}
	public Qyxx getQyxx() {
		return qyxx;
	}
	public void setQyxx(Qyxx qyxx) {
		this.qyxx = qyxx;
	}
	public TBCyxx getCyxx() {
		return cyxx;
	}
	public void setCyxx(TBCyxx cyxx) {
		this.cyxx = cyxx;
	}
	
	
}
