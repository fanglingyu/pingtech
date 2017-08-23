package com.pingtech.hgqw.readcard.utils;

import android.os.Message;

import com.pingtech.hgqw.entity.CardInfo;
import com.pingtech.hgqw.readcard.service.ReadService;

public class ReadCardTools {
	/**
	 * 
	 * @方法名：byteArrayToString
	 * @功能说明：读IC卡加密区数据翻译
	 * @author liums
	 * @date 2013-4-2 下午3:50:11
	 * @param buffer
	 * @return
	 */
	public static String byteArrayToString(byte[] buffer) {
		if (buffer == null || buffer.length < 1)
			return null;
		int i = 0;
		for (i = 0; i < buffer.length; i++) {
			if (buffer[i] == 0) {
				break;
			}
		}
		byte[] temp = new byte[i];
		for (int j = 0; j < temp.length; j++) {
			temp[j] = buffer[j];
		}
		return new String(temp);
	}

	/**
	 * 
	 * @方法名：byteArrayToLong
	 * @功能说明：默认卡号的处理，数据默认从低位到高位，需要翻转。此方法用户深圳安软警务通mime_PE43读卡程序返回结果的处理。
	 * @author liums
	 * @date 2013-3-31 下午2:08:06
	 * @param a
	 * @return
	 */
	public static String byteArrayToLongTemp(byte[] a) {
		StringBuilder sbTemp = new StringBuilder();
		
		StringBuilder sb = new StringBuilder();
		if (a == null || a.length < 1)
			return null;
		for (int i = a.length - 1; i >= 0; i--) {
			int v = a[i] & 0xFF;
			String temp = Integer.toHexString(v);
			sbTemp.append(temp);
			if (temp.length() < 2) {
				sb.append("0");
			}
			sb.append(temp);
		}
		String longStr = Long.valueOf(sb.toString(), 16).toString();
		if (longStr.length() == 9) {
			longStr = "0" + longStr;
		}
		return longStr;
	}
	/**
	 * 
	 * @方法名：byteArrayToLong
	 * @功能说明：默认卡号的处理,转成16进制字符串
	 * @author liums
	 * @date 2013-3-31 下午2:08:06
	 * @param a
	 * @return
	 */
	public static String byteArrayToHex(byte[] a) {
		StringBuilder sbTemp = new StringBuilder();
		if (a == null || a.length < 1)
			return null;
		for (int i =0;i<a.length;i++) {
			int v = a[i] & 0xFF;
			String temp = Integer.toHexString(v);
			if (temp.length() < 2) {
				sbTemp.append("0");
			}
			sbTemp.append(temp);
		}
		return sbTemp.toString().toUpperCase();
	}

	/**
	 * 
	 * @方法名：reverseStrToLong
	 * @功能说明：默认卡号的处理，数据默认从低位到高位，需要翻转。此方法用于南京厂商警务通M802读卡程序返回结果的处理
	 * @author liums
	 * @date 2013-6-19
	 * @param result
	 * @return
	 */
	public static String reverseStrToLong(String result) {
		result = result.replace("0x", "");
		result = result.replace("\n", "");
		result = result.replace("\r", "");
		String[] buf = result.split(",");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = buf.length - 1; i >= 0; i--) {
			stringBuilder.append(buf[i]);
		}
		String longStr = Long.valueOf(stringBuilder.toString(), 16).toString();
		if (longStr.length() == 9) {
			longStr = "0" + longStr;
		}
		return longStr;
	}

	/**
	 * 南京警务通M802读默认卡号转换方法，与发证程序统一：不转换高地位，字母转为大写
	 * @param result
	 * @return
	 */
	public static String reverseStrToHex_M802(String result) {
		result = result.replace("0x", "");
		result = result.replace("\n", "");
		result = result.replace("\r", "");
		return result.replace(",", "").toUpperCase();
	}

	/**
	 * 重构卡片对象
	 * @param msg
	 * @param cardInfo
	 * @return
	 */
	public static CardInfo rebuildCardInfo(Message msg , CardInfo cardInfo) {
		if(msg==null){
			return null;
		}
		cardInfo = (CardInfo) msg.obj;
		switch (msg.what) {
		case ReadService.READ_TYPE_DEFAULT_AND_ICKEY:
			cardInfo.setCardType(CardInfo.CARD_TYPE_IC);
			break;
		case ReadService.READ_TYPE_ID:
			cardInfo.setCardType(CardInfo.CARD_TYPE_ID);
			break;
		default:
			break;
		}
		return cardInfo;
	}

}
