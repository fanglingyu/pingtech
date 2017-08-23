package com.pingtech.hgqw.web;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.utils.Log;
import com.pingtech.hgqw.utils.StringEncoder;

/** webservice类，应用第三方ksoap2接口 */
public class WebService {
	private static final String TAG = "WebService";

	private static final String METHOD_NAME = "execute";

	public static String request(String path, List<NameValuePair> params) {
		if (BaseApplication.instent.getSystemSettingInfo() == null) {
			BaseApplication.instent.settingInit();
		}
		String URL = BaseApplication.instent.getSystemSettingInfo().getWebServiceWSDLUrl();
		String NAMESPACE = BaseApplication.instent.getSystemSettingInfo().getWebServiceNamespace();
		String SOAP_ACTION = NAMESPACE + METHOD_NAME;
		String arg0 = BaseApplication.instent.getSystemSettingInfo().getWebServiceUserName();
		String arg1 = BaseApplication.instent.getSystemSettingInfo().getWebServicePassword();
		String arg2 = BaseApplication.instent.getSystemSettingInfo().getWebServiceCode();
		String kadm = BaseApplication.instent.getSystemSettingInfo().getServerKadm();
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		String str = "method=" + path;
		Log.i(TAG, "\n\n");
		Log.i(TAG, "method=" + str);
		int count = 0;
		if (params != null) {
			count = params.size();
		}

		// 统一增加分站口岸代码
		String userKadm = "";
		params.add(new BasicNameValuePair("userKadm", userKadm));
		// ","+"KEY="+"VALUE"
		for (int i = 0; i < count; i++) {
			if (str.length() > 0) {
				str += ",";
			}
			NameValuePair temp = params.get(i);
			String temp_str = temp.getName();
			try {
				str += StringEncoder.encode(temp_str, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			str += "=";
			temp_str = temp.getValue();
			try {
				temp_str = StringEncoder.encode(temp_str, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				temp_str = "";
			}
			str += temp_str;
		}
		// 每个接口增加USERID
		String useridParams = "userID=" + BaseApplication.instent.gainUserID();
		if (!str.contains(useridParams)) {
			str = str + "," + useridParams;
			Log.i(TAG, "!str.contains(useridParams), add useridParams");
			Log.i(TAG, "str=" + str);
		}
		if (BaseApplication.instent.getSystemSettingInfo().getWebServiceArg1() != null
				&& BaseApplication.instent.getSystemSettingInfo().getWebServiceArg1().length() != 0) {
			rpc.addProperty(BaseApplication.instent.getSystemSettingInfo().getWebServiceArg1(), arg0);
		}
		if (BaseApplication.instent.getSystemSettingInfo().getWebServiceArg2() != null
				&& BaseApplication.instent.getSystemSettingInfo().getWebServiceArg2().length() != 0) {
			rpc.addProperty(BaseApplication.instent.getSystemSettingInfo().getWebServiceArg2(), arg1);
		}
		if (BaseApplication.instent.getSystemSettingInfo().getWebServiceArg3() != null
				&& BaseApplication.instent.getSystemSettingInfo().getWebServiceArg3().length() != 0) {
			rpc.addProperty(BaseApplication.instent.getSystemSettingInfo().getWebServiceArg3(), arg2);
		}
		if (BaseApplication.instent.getSystemSettingInfo().getWebServiceArg4() != null
				&& BaseApplication.instent.getSystemSettingInfo().getWebServiceArg4().length() != 0) {
			rpc.addProperty(BaseApplication.instent.getSystemSettingInfo().getWebServiceArg4(), str);
		}
		// 1、增加口岸代码，如果口岸代码设置为-1，则调用原有webservice只传四个参数
		if (BaseApplication.instent.getSystemSettingInfo().getWebServiceArg5() != null
				&& BaseApplication.instent.getSystemSettingInfo().getWebServiceArg5().length() != 0 && !"test".equals(kadm)) {
			rpc.addProperty(BaseApplication.instent.getSystemSettingInfo().getWebServiceArg5(), kadm);
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
		envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);
		Log.i(TAG, "WebRequest() WSDL url: " + URL);
		HttpTransportSE ht = new HttpTransportSE(URL, 30000);
		try {
			ht.call(SOAP_ACTION, envelope);
			Object Response = envelope.getResponse();
			if (Response != null) {
				String result = Response.toString();
				return result;
			} else {
				Log.i(TAG, "~~~" + path + "~~~,WebRequest Response == null ");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}