package com.pingtech.hgqw.mqtt;

import java.util.ArrayList;
import java.util.List;

import com.pingtech.hgqw.mqtt.interfaces.MessageHandler;

/**
 * 
 *
 * 类描述：推送消息公共类
 *
 * <p> Title: 系统名称-MqttContent.java </p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: 品恩科技 </p>
 * @author  zhaotf 
 * @version 1.0
 * @date  2014-3-24 下午2:58:12
 */
public class MqttContent {
	/** 语音消息 */
	public static final String MQTT_MESSAGE_YYXX = "yyxx";

	/** 警务指令 */
	public static final String MQTT_MESSAGE_JWZL = "jwzl";

	/** 用于存放绑定语音Message显示的Activity **/
	public static List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();
	
	/** 用于存放绑定指令Message显示的Activity **/
	public static List<MessageHandler> orderMessageHandlers = new ArrayList<MessageHandler>();
}
