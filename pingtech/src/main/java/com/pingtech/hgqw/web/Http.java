package com.pingtech.hgqw.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.SystemSetting;

/** HTTP 协议处理流程 */
public class Http {
	private static final String TAG = "Http";

	private static HttpClient httpClient = null;
	private static HttpContext httpContext = null;
	private static CookieStore cookieStore = null;
	private static String HTTP_URL_HEAD = "hgqw/servlet/PDAServlet?method=";// 119.253.56.52:8080
	private static String HTTP_STR = "http://";

	/** 创建一个httpClient */
	private static synchronized HttpClient getHttpClient() {
		if (httpClient == null) {
			HttpParams httpParameters = new BasicHttpParams();
			ConnManagerParams.setTimeout(httpParameters, 50000);
			ConnManagerParams.setMaxConnectionsPerRoute(httpParameters, new ConnPerRouteBean(10));
			HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);
			httpParameters.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httpParameters.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(httpParameters, schReg);
			manager.closeIdleConnections(30, TimeUnit.SECONDS);
			httpClient = new DefaultHttpClient(manager, httpParameters);
		}

		return httpClient;
	}

	public static synchronized void destroyHttpClient() {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
			httpClient = null;
		}
	}

	/** 发起一个http post */
	public static String httpPost(String path, List<NameValuePair> params) {
		path = HTTP_STR + SystemSetting.getServerHost() + ":" + SystemSetting.getServerPort() + "/" + HTTP_URL_HEAD
				+ path;
		Log.i(TAG, "httpPost(): " + path);
		try {
			HttpPost httpPost = new HttpPost(path);

			httpPost.setHeader("Accept-Language", "zh-CN, en-US");

			HttpEntity entity;
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
			HttpResponse response = getHttpClient().execute(httpPost, getHttpContext());

			int statusCode = response.getStatusLine().getStatusCode();
			Log.i(TAG, "httpPost(): " + statusCode);
			HttpEntity entityResponse = response.getEntity();
			if (statusCode == 200 || statusCode == 302) {
				String res = EntityUtils.toString(entityResponse, "UTF-8");
				entityResponse.consumeContent();
				return res;
			} else {
				entityResponse.consumeContent();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private static HttpContext getHttpContext() {
		if (httpContext == null) {
			cookieStore = getCookieStore();
			httpContext = new BasicHttpContext();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		}

		return httpContext;
	}

	private static CookieStore getCookieStore() {
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		return cookieStore;
	}
}
