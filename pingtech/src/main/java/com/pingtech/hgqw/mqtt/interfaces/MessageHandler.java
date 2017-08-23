package com.pingtech.hgqw.mqtt.interfaces;

/**
 * 
 *
 * 类描述：推送消息Handler
 *
 * <p> Title: 系统名称-MessageHandler.java </p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: 品恩科技 </p>
 * @author  zhaotf 
 * @version 1.0
 * @date  2014-3-24 下午2:57:44
 */
public interface MessageHandler {
	public void handleMessage(String topic, byte[] payload);
}