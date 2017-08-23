package com.pingtech.hgqw.interf;

/**
 * 
 * 
 * http请求（包括webservice请求）返回后通过该接口传递到各个activity
 * 
 * @param str
 *            后台返回的内容，详细见接口文档
 * @param httpRequestType
 *            请求的类型，用于区分一个activity中的多个http请求
 */
public interface OnHttpResult {
	void onHttpResult(String str, int httpRequestType);
}
